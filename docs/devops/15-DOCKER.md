# 🐳 Docker y Contenedores — DevOps

## 1. Descripción General

El proyecto cuenta con dockerización completa del backend (Spring Boot), frontend (Angular) y base de datos (PostgreSQL), facilitando la portabilidad y el despliegue mediante **Docker Compose**.

---

## 2. Dockerfile del Backend (`Dockerfile`)

```dockerfile
# Multi-stage build para optimizar el tamaño de la imagen
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 3. Orquestación con Docker Compose (`docker-compose.yml`)

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: entidad_financiera_db
    environment:
      POSTGRES_DB: entidad_financiera
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgrespassword
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./docs/database/09-DDL-DEFINICION-DATOS.md:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./prueba_backend
      dockerfile: Dockerfile
    container_name: entidad_financiera_backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/entidad_financiera
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgrespassword
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      postgres:
        condition: service_healthy

volumes:
  postgres_data:
```

---

## 4. Comandos de Operación

```bash
# Construir y levantar todos los servicios
docker-compose up --build -d

# Ver logs del backend
docker-compose logs -f backend

# Detener los contenedores
docker-compose down

# Limpiar volúmenes
docker-compose down -v
```
