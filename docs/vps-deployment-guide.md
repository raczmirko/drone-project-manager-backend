# VPS Deployment Guide — Drone Project Manager

A step-by-step reference for deploying the Drone Project Manager (Spring Boot + PostgreSQL + frontend) on a VPS using Docker Compose, with GitHub Actions CI/CD.

***

## 1. SSH Access Configuration

### Generate an SSH Key Pair

On your local machine, generate a dedicated deploy key:

```bash
ssh-keygen -t ed25519 -C "github-actions-deploy" -f ~/.ssh/deploy_key
```

This creates two files:
- `~/.ssh/deploy_key` — private key (goes to GitHub)
- `~/.ssh/deploy_key.pub` — public key (goes to VPS)

### Authorize the Key on the VPS

Add the public key to the VPS's authorized keys:

```bash
cat ~/.ssh/deploy_key.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

***

## 2. GitHub Secrets

In your GitHub repository, navigate to **Settings → Secrets and variables → Actions** and add the following secrets:

| Secret Name | Value |
|---|---|
| `VPS_HOST` | Your VPS IP address or hostname |
| `VPS_USER` | SSH user (e.g. `root`) |
| `VPS_SSH_KEY` | Contents of the private key file (`~/.ssh/deploy_key`) |
| `DB_NAME` | PostgreSQL database name |
| `DB_USER` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |

These secrets are referenced in the GitHub Actions workflow to deploy over SSH and configure the application environment.

***

## 3. GitHub Actions Workflow

The workflow builds and pushes Docker images to GitHub Container Registry (GHCR), then SSHes into the VPS to pull and restart the containers.

Key steps in the workflow:
1. Build backend and frontend Docker images
2. Push images to `ghcr.io/<your-username>/...`
3. SSH into VPS and run `docker compose pull && docker compose up -d`

The `.env` file on the VPS holds the database credentials and is referenced by `docker-compose.yml` via `${VARIABLE}` syntax.

***

## 4. VPS Directory Structure

All application files live in:

```
/opt/apps/drone-project-manager/
├── docker-compose.yml
└── .env
```

The `.env` file must be created manually on the VPS:

```bash
nano /opt/apps/drone-project-manager/.env
```

With contents:

```env
DB_NAME=your_database_name
DB_USER=your_database_user
DB_PASSWORD=your_database_password
```

***

## 5. Docker Compose Configuration

The final working `docker-compose.yml`:

```yaml
services:

  db:
    image: postgres:16-alpine
    ports:
      - "127.0.0.1:5432:5432"
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER} -d ${DB_NAME}"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  backend:
    image: ghcr.io/<your-username>/drone-project-manager-backend:latest
    restart: unless-stopped
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_JPA_HIBERNATE_DDL_AUTO: validate
    depends_on:
      db:
        condition: service_healthy
    healthcheck:
      test: ["CMD-SHELL", "bash -c 'cat /dev/null > /dev/tcp/localhost/8080' && echo ok || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  frontend:
    image: ghcr.io/<your-username>/drone-project-manager-frontend:latest
    restart: unless-stopped
    ports:
      - "80:80"
    depends_on:
      backend:
        condition: service_healthy

volumes:
  postgres_data:
```

***

## 6. Backend Healthcheck — Root Cause & Fix

### The Problem

When running `docker compose up`, the backend container was being marked **unhealthy**, preventing the frontend from starting. The backend itself started fine in isolation.

The root cause was a combination of two issues:

1. The healthcheck used `curl`, which is **not installed** in the Spring Boot Docker image (Temurin JRE-based).
2. A fallback using `/dev/tcp` failed because `CMD-SHELL` defaults to `sh`, and `/dev/tcp` is a **bash-only** feature.

### Diagnosing Available Tools

To check which tools are available inside a container without running the application, override the entrypoint:

```bash
docker compose run --rm --entrypoint sh backend -c "
  echo '=== bash ===' && bash --version 2>/dev/null || echo 'MISSING';
  echo '=== curl ===' && curl --version 2>/dev/null || echo 'MISSING';
  echo '=== wget ===' && wget --version 2>/dev/null || echo 'MISSING';
  echo '=== nc ===' && nc -h 2>/dev/null || echo 'MISSING';
  echo '=== java ===' && java --version 2>/dev/null || echo 'MISSING';
"
```

Result for this image:
- `bash` ✅ available
- `curl` ❌ missing
- `wget` ❌ missing
- `nc` ❌ missing
- `java` ✅ available

### The Fix

Since `bash` is available, the `/dev/tcp` trick works — but must be explicitly invoked with `bash -c`:

```yaml
healthcheck:
  test: ["CMD-SHELL", "bash -c 'cat /dev/null > /dev/tcp/localhost/8080' && echo ok || exit 1"]
```

This opens a TCP connection to port 8080 using bash's built-in network feature, with no external tools required.

***

## 7. Starting and Managing Containers

### Start all containers (detached):

```bash
cd /opt/apps/drone-project-manager
docker compose up -d
```

### Stop all containers:

```bash
docker compose down
```

### Check container status:

```bash
docker compose ps
```

### View backend logs:

```bash
docker compose logs -f backend
```

### Pull latest images and restart (after a new deployment):

```bash
docker compose pull
docker compose up -d
```

***

## 8. Useful Diagnostic Commands

| Task | Command |
|---|---|
| Check healthcheck output | `docker inspect <container-name> --format='{{json .State.Health}}' \| jq` |
| Test tools inside a container | `docker compose run --rm --entrypoint sh <service> -c "<command>"` |
| Check container logs | `docker compose logs -f <service>` |
| Check running containers | `docker compose ps` |