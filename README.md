# 🏦 Sistema de Gestión Financiera y Bancaria

¡Bienvenido al repositorio de la **Prueba Técnica de Gestión Financiera y Bancaria**! 

Este proyecto es una solución **Full Stack** orientada a microservicios/arquitectura limpia para la administración de clientes, cuentas bancarias, transacciones financieras y estados de cuenta en tiempo real.

---

## 📐 Arquitectura del Sistema

El proyecto está diseñado bajo los principios de **Arquitectura Hexagonal (Ports & Adapters)** y **Clean Architecture**, garantizando desacoplamiento, testabilidad y escalabilidad.

```
                  +-----------------------------------+
                  |         Frontend (Angular 20)     |
                  +-----------------------------------+
                                    |
                                    v (HTTP REST API)
    +---------------------------------------------------------------+
    |                     Backend (Spring Boot 3)                   |
    |                                                               |
    |  +---------------------------------------------------------+  |
    |  | Infrastructure (Adapters: REST Controllers / JPA Repos) |  |
    |  +---------------------------------------------------------+  |
    |                               |                               |
    |                               v                               |
    |  +---------------------------------------------------------+  |
    |  | Application (Use Cases, Services & DTOs)                |  |
    |  +---------------------------------------------------------+  |
    |                               |                               |
    |                               v                               |
    |  +---------------------------------------------------------+  |
    |  | Domain (Entities, Enums & Service Ports)                |  |
    |  +---------------------------------------------------------+  |
    +---------------------------------------------------------------+
                                    |
                                    v
                  +-----------------------------------+
                  |      Database (PostgreSQL 16)     |
                  +-----------------------------------+
```

---

## 🚀 Tecnologías Utilizadas

### **Backend (`/prueba_backend`)**
- **Lenguaje:** Java 17
- **Framework:** Spring Boot 3.3.2
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:** PostgreSQL 16
- **Documentación API:** Springdoc OpenAPI (Swagger UI v2.6.0)
- **Arquitectura:** Arquitectura Hexagonal (Puertos y Adaptadores)

### **Frontend (`/prueba-frontend`)**
- **Framework:** Angular 20
- **Servidor Web:** Nginx (dentro de contenedor Docker)
- **Estilos & UI:** CSS / HTML5 responsivo
- **Manejo de Estado y Reactividad:** RxJS

### **DevOps & Contenerización**
- **Docker & Docker Compose:** Multi-contenedor orquestado para PostgreSQL, Backend y Frontend.

---

## 🗂️ Estructura del Repositorio

```
prueba-tecnica/
├── docker-compose.yml         # Archivo de orquestación de contenedores Docker
├── README.md                  # Documentación principal del proyecto
├── prueba_backend/            # Código fuente del Backend (Spring Boot)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/trinity/prueba/
│       │   ├── application/     # Casos de uso y DTOs
│       │   ├── domain/          # Modelos de dominio y Puertos
│       │   └── infraestructure/ # Adaptadores REST, JPA y Configuración
│       └── main/resources/
│           ├── schema.sql       # Script DDL de la Base de Datos
│           └── data.sql         # Datos de prueba de inicialización
└── prueba-frontend/           # Código fuente del Frontend (Angular)
    ├── Dockerfile
    ├── nginx.conf
    └── src/
```

---

## ⚡ Despliegue y Ejecución Rápida (Docker Compose)

El proyecto está listo para ejecutarse de forma integral utilizando **Docker Compose**.

### **Prerrequisitos**
- Tener instalado **Docker Desktop** y **Docker Compose**.

### **Pasos para iniciar todo el entorno:**

1. **Clonar el repositorio:**
   ```bash
   git clone <URL_DE_TU_REPOSITORIO>
   cd prueba-tecnica
   ```

2. **Levantar los contenedores:**
   ```bash
   docker-compose up --build -d
   ```

3. **Verificar el estado de los servicios:**
   ```bash
   docker-compose ps
   ```

4. **Detener los servicios:**
   ```bash
   docker-compose down
   ```

---

## 🌐 Servicios y Puertos de Acceso

Una vez ejecutado `docker-compose up`, los siguientes servicios estarán disponibles:

| Servicio | URL / Puerto | Descripción |
|---|---|---|
| **Frontend (Angular)** | `http://localhost` o `http://localhost:4200` | Aplicación Web de gestión |
| **Backend REST API** | `http://localhost:9000` | API REST Spring Boot |
| **Swagger UI (OpenAPI)** | `http://localhost:9000/swagger-ui/index.html` | Documentación interactiva de la API |
| **Base de Datos PostgreSQL** | `localhost:5433` | Host local (Puerto interno del contenedor: `5432`) |

---

## 🔑 Datos de Conexión a la Base de Datos

- **Host:** `localhost` (o `postgres` desde dentro de la red Docker)
- **Puerto Externo:** `5433`
- **Base de Datos:** `financiera_db`
- **Usuario:** `postgres`
- **Contraseña:** `123456`

> **Nota:** La base de datos ejecuta automáticamente los scripts `schema.sql` y `data.sql` al iniciar el contenedor para crear las tablas y sembrar datos de prueba iniciales.

---

## 📌 Principales Endpoints de la API REST

### 👤 **Clientes (`/api/v1/clients`)**
- `GET /api/v1/clients` - Listar todos los clientes.
- `GET /api/v1/clients/{id}` - Obtener cliente por ID.
- `POST /api/v1/clients` - Crear nuevo cliente.
- `PUT /api/v1/clients/{id}` - Actualizar cliente existente.
- `DELETE /api/v1/clients/{id}` - Eliminar cliente.

### 💳 **Cuentas Bancarias (`/api/v1/accounts`)**
- `GET /api/v1/accounts` - Listar todas las cuentas.
- `GET /api/v1/accounts/{id}` - Obtener detalles de una cuenta por ID.
- `GET /api/v1/accounts/client/{clientId}` - Listar cuentas asignadas a un cliente específico.
- `POST /api/v1/accounts` - Apertura de nueva cuenta (Ahorros o Corriente).
- `PUT /api/v1/accounts/{id}/status` - Cambiar el estado de una cuenta (Activa, Inactiva, Cancelada).
- `DELETE /api/v1/accounts/{id}` - Cancelación de cuenta.

### 💸 **Transacciones (`/api/v1/transactions`)**
- `POST /api/v1/transactions` - Registrar una transacción (Consignación, Retiro, Transferencia).
- `GET /api/v1/transactions/account/{accountId}` - Listar transacciones de una cuenta.
- `GET /api/v1/transactions/account/{accountId}/statement` - Obtener extracto / estado de cuenta.

---

## 🧪 Pruebas Unitarias e Integración

### **Ejecutar Pruebas Backend (Localmente sin Docker)**
```bash
cd prueba_backend
./mvnw clean test
```

### **Ejecutar Pruebas Frontend (Localmente sin Docker)**
```bash
cd prueba-frontend
npm install
npm run test
```
