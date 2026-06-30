# SSL Setup Notes for Drone Project Manager Backend/VPS

This document summarizes the final working SSL and reverse-proxy setup for `drone-project-manager.eu`, including the main problems encountered during the setup process and how they were resolved.

## Overview

The final setup uses Contabo DNS for the domain, Docker Compose for the application stack, and Nginx Proxy Manager (NPM) as the public reverse proxy and SSL termination point. NPM listens on ports 80 and 443, forwards traffic to the frontend container over a shared Docker network, and manages the Let's Encrypt certificate through its web UI.

The NPM admin UI was intentionally bound to `127.0.0.1:81` so it is not exposed publicly on the internet. Access to the UI is intended through an SSH tunnel instead of direct public access, which is safer than opening port 81 to the world.

## DNS and domain setup

The domain `drone-project-manager.eu` was delegated to Contabo nameservers at the registrar, and the DNS zone in Contabo was configured with A records pointing the root domain and `www` subdomain to the VPS public IP. Without correct DNS pointing to the VPS, Let's Encrypt HTTP validation cannot succeed because the ACME challenge must reach the NPM instance on port 80.

Important DNS records used in the final setup:

| Type | Name | Value |
|---|---|---|
| A | `drone-project-manager.eu` | VPS public IP |
| A | `www.drone-project-manager.eu` | VPS public IP |

## Nginx Proxy Manager deployment

NPM was deployed in its own Docker Compose project and attached to an external Docker network named `proxy`. The recommended port mapping is public `80:80` and `443:443`, while the admin interface is bound only to localhost as `127.0.0.1:81:81`.

Example NPM Compose configuration:

```yaml
services:
  npm:
    image: jc21/nginx-proxy-manager:2.15.1
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
      - "127.0.0.1:81:81"
    volumes:
      - ./data:/data
      - ./letsencrypt:/etc/letsencrypt
    networks:
      - proxy

networks:
  proxy:
    external: true
```

## Application Compose changes

The application stack originally exposed the frontend directly on host port 80. That conflicts with NPM because both services cannot bind the same public ports at the same time. The frontend therefore had to stop publishing port 80 on the host and instead join the shared external `proxy` network so NPM could reach it internally by container hostname.

A key lesson from the setup was that Docker Compose network configuration must use a consistent syntax. Service networks can be defined either with the short array syntax or the long mapping syntax, but aliases require the long syntax.

Final relevant application Compose structure:

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
    networks:
      default:
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER} -d ${DB_NAME}"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  backend:
    image: ghcr.io/raczmirko/drone-project-manager-backend:latest
    restart: unless-stopped
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_JPA_HIBERNATE_DDL_AUTO: validate
    depends_on:
      db:
        condition: service_healthy
    networks:
      default:
      proxy:
        aliases:
          - drone-backend
    healthcheck:
      test: ["CMD-SHELL", "bash -c 'cat /dev/null > /dev/tcp/localhost/8080' && echo ok || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  frontend:
    image: ghcr.io/raczmirko/drone-project-manager-frontend:latest
    restart: unless-stopped
    depends_on:
      backend:
        condition: service_healthy
    networks:
      default:
      proxy:
        aliases:
          - drone-frontend

volumes:
  postgres_data:

networks:
  default:
  proxy:
    external: true
```

## Proxy host configuration in NPM

The working proxy host in NPM uses the domain names `drone-project-manager.eu` and `www.drone-project-manager.eu`, scheme `http`, destination hostname `drone-frontend`, and port `80`. On a shared Docker network, using the container hostname or alias is the intended pattern for NPM proxying to backend containers.

The domain names in NPM must be entered as bare hostnames only. A failed attempt was caused by entering `http://drone-project-manager.eu` instead of just `drone-project-manager.eu`, which NPM rejected because it expects a hostname and not a full URL.[cite:59]

## SSL certificate issuance

The SSL certificate was set up through the web UI of NPM, in the *Edit Proxy Host* dialog with the *Request a new Certificate with Let's Encrypt* and force SSL options. The domain names to issue a certificate for are:

- `drone-project-manager.eu`
- `www.drone-project-manager.eu`

## Errors encountered and resolutions

### 1. Backend healthcheck failed in Docker Compose

The backend container started correctly when launched manually, but Docker Compose marked it as unhealthy when starting the full stack. The root cause was that the image did not contain `curl`, `wget`, or `nc`, and the initial `/dev/tcp` attempt also failed because `CMD-SHELL` uses `sh` by default while `/dev/tcp` is a Bash-specific feature. The fix was to explicitly run the healthcheck under Bash using `bash -c 'cat /dev/null > /dev/tcp/localhost/8080'`.[cite:36]

### 2. `docker compose run` appeared to hang while testing commands

When testing tools inside the backend image, the command seemed stuck because `docker compose run backend ...` still used the image entrypoint, which starts Spring Boot. The fix was to override the entrypoint with `--entrypoint sh` so commands could run directly without starting the application container first.[cite:96]

### 3. NPM could not reach the frontend container

NPM initially pointed to `frontend:80`, but NPM itself was on the `proxy` network while the frontend was only on the default project network. Because the containers were on different Docker networks, NPM could not resolve or reach the frontend target. The fix was to connect the frontend to the shared external `proxy` network and add a stable alias `drone-frontend`.

### 4. Docker Compose YAML errors around `networks`

The Compose file broke because the short array syntax and long mapping syntax for `networks` were mixed incorrectly. The corrected version uses the long mapping form for services where aliases are needed, which is required by Compose syntax.

### 5. NPM default fallback page appeared instead of the app

After the proxy host was created, visiting the site sometimes showed the default NPM “Congratulations” page. This page is served when a request does not match a configured host or when the browser hits the default host unexpectedly. Direct `curl` tests with the correct `Host` header confirmed that NPM was matching the configured domain and returning an HTTP 301 redirect to HTTPS, while random hostnames returned the default page as expected. In the end, the remaining visible issue turned out to be browser cache, not a broken proxy configuration.[cite:59]

## Validation commands

Useful commands used during troubleshooting:

```bash
# Check that the frontend is listening locally
curl -I http://localhost

# Check Docker network membership
docker ps --format "table {{.Names}}\t{{.Networks}}"

# Confirm NPM can resolve the frontend alias
docker exec -it nginx-proxy-manager-npm-1 getent hosts drone-frontend

# Confirm NPM HTTP host matching
curl -I http://127.0.0.1 -H "Host: drone-project-manager.eu"
curl -I http://127.0.0.1 -H "Host: www.drone-project-manager.eu"

# Check HTTPS host handling
curl -k -I https://127.0.0.1 -H "Host: drone-project-manager.eu"
```

The decisive host-matching test was the HTTP request with an explicit `Host` header. A 301 redirect for the real domains and the fallback page for a fake domain showed that NPM was routing correctly and that the later browser behavior was caused by cache rather than by reverse-proxy misconfiguration.[cite:59]

## Final result

The final working state is:

- DNS points the domain to the Contabo VPS.
- NPM is the only public entrypoint on ports 80 and 443.
- The NPM admin UI is not public and is reachable only through localhost/SSH tunneling.
- The frontend and backend are attached to the shared external `proxy` Docker network.
- The proxy host forwards `drone-project-manager.eu` and `www.drone-project-manager.eu` to `drone-frontend:80`.
- Let's Encrypt certificates are issued and HTTPS works successfully.
