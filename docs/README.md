# 📚 Documentación del Proyecto - Entidad Financiera

## Descripción General

Aplicación para la administración de clientes, productos financieros (cuentas) y transacciones de una entidad financiera. Desarrollada con **arquitectura hexagonal**, utilizando **Spring Boot** (backend), **Angular** (frontend) y **PostgreSQL** (base de datos).

---

## 🗂️ Índice de Documentación

### 🏗️ Arquitectura y Diseño

| Documento | Descripción |
|-----------|-------------|
| [Arquitectura Hexagonal](./backend/01-ARQUITECTURA-HEXAGONAL.md) | Estructura de capas, puertos y adaptadores |
| [Patrones de Diseño](./backend/02-PATRONES-DISENO.md) | Patrones aplicados en el proyecto |
| [Principios SOLID](./backend/03-PRINCIPIOS-SOLID.md) | Aplicación de principios SOLID |
| [Principios ACID](./backend/04-PRINCIPIOS-ACID.md) | Garantías transaccionales en la base de datos |

### ⚙️ Backend (Spring Boot)

| Documento | Descripción |
|-----------|-------------|
| [Módulo Clientes](./backend/05-MODULO-CLIENTES.md) | CRUD de clientes con validaciones de negocio |
| [Módulo Productos](./backend/06-MODULO-PRODUCTOS.md) | Gestión de cuentas corrientes y de ahorros |
| [Módulo Transacciones](./backend/07-MODULO-TRANSACCIONES.md) | Consignaciones, retiros y transferencias |
| [Tests Unitarios](./backend/08-TESTS-UNITARIOS.md) | Estrategia de testing con JUnit y Mockito |

### 🗄️ Base de Datos (PostgreSQL)

| Documento | Descripción |
|-----------|-------------|
| [DDL - Definición de Datos](./database/09-DDL-DEFINICION-DATOS.md) | Scripts de creación de tablas, índices y constraints |
| [DML - Manipulación de Datos](./database/10-DML-MANIPULACION-DATOS.md) | Scripts de inserción, consultas y operaciones |

### 🎨 Frontend (Angular)

| Documento | Descripción |
|-----------|-------------|
| [Arquitectura Frontend](./frontend/11-ARQUITECTURA-FRONTEND.md) | Estructura del proyecto Angular |
| [Módulo Clientes (UI)](./frontend/12-MODULO-CLIENTES-UI.md) | Componentes y servicios de clientes |
| [Módulo Productos (UI)](./frontend/13-MODULO-PRODUCTOS-UI.md) | Componentes y servicios de productos |
| [Módulo Transacciones (UI)](./frontend/14-MODULO-TRANSACCIONES-UI.md) | Componentes y servicios de transacciones |

### 🐳 DevOps y Despliegue

| Documento | Descripción |
|-----------|-------------|
| [Docker y Contenedores](./devops/15-DOCKER.md) | Dockerización del proyecto completo |
| [Servicios en la Nube](./devops/16-SERVICIOS-NUBE.md) | Servicios cloud utilizados |
| [Git Flow y Versionamiento](./devops/17-GIT-FLOW.md) | Estrategia de ramas y versionamiento |

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología | Versión |
|------------|-----------|---------|
| **Backend** | Spring Boot | 4.1.0 |
| **Lenguaje Backend** | Java | 17 |
| **Frontend** | Angular | 18+ |
| **Base de Datos** | PostgreSQL | 16+ |
| **Build Tool** | Maven | 3.9+ |
| **Contenedores** | Docker & Docker Compose | 24+ |
| **Testing** | JUnit 5 + Mockito | 5.x |
| **Versionamiento** | Git (Git Flow) | 2.x |

---

## 🚀 Inicio Rápido

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/entidad-financiera.git

# Levantar con Docker Compose
docker-compose up -d

# Backend disponible en: http://localhost:8080
# Frontend disponible en: http://localhost:4200
# PostgreSQL disponible en: localhost:5432
```

---

## 📐 Estructura del Proyecto

```
proyecto/
├── docs/                          # 📚 Documentación
│   ├── backend/                   # Documentación del backend
│   ├── frontend/                  # Documentación del frontend
│   ├── database/                  # Documentación de base de datos
│   └── devops/                    # Documentación de DevOps
├── src/                           # 🔧 Código fuente del backend
│   ├── main/
│   │   ├── java/com/trinity/prueba/
│   │   │   ├── domain/            # 🔵 Capa de Dominio (núcleo)
│   │   │   ├── application/       # 🟢 Capa de Aplicación (casos de uso)
│   │   │   └── infraestructure/   # 🟠 Capa de Infraestructura (adaptadores)
│   │   └── resources/
│   └── test/                      # 🧪 Tests unitarios
├── frontend/                      # 🎨 Proyecto Angular
├── docker-compose.yml             # 🐳 Orquestación de contenedores
├── Dockerfile                     # 🐳 Imagen del backend
└── pom.xml                        # 📦 Configuración Maven
```
