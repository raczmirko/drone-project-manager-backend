# Build stage
FROM eclipse-temurin:25 AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q
COPY src/ src/
RUN ./mvnw clean package -DskipTests -q

# Extract layered JAR for better Docker cache usage
RUN java -Djarmode=tools -jar target/*.jar extract --layers --launcher --destination extracted

# Runtime stage
FROM eclipse-temurin:25
WORKDIR /app
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring

COPY --from=builder /app/extracted/dependencies/ ./
COPY --from=builder /app/extracted/spring-boot-loader/ ./
COPY --from=builder /app/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/extracted/application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]