# 🔀 Git Flow y Estrategia de Versionamiento

## 1. Descripción General

Para garantizar el control de cambios, la integración continua y la entrega continua (CI/CD), el equipo de desarrollo utiliza la estrategia de ramificación **Git Flow**.

---

## 2. Estructura de Ramas

```mermaid
gitGraph
    commit id: "v0.1.0"
    branch develop
    checkout develop
    commit id: "Init develop"
    
    branch feature/clients-module
    checkout feature/clients-module
    commit id: "Add client domain"
    commit id: "Add client controller"
    
    checkout develop
    merge feature/clients-module id: "Merge feature/clients"
    
    branch feature/products-module
    checkout feature/products-module
    commit id: "Add account entity"
    
    checkout develop
    merge feature/products-module id: "Merge feature/products"
    
    checkout main
    merge develop id: "Release v1.0.0" tag: "v1.0.0"
```

| Rama | Propósito | Reglas |
|------|-----------|--------|
| `main` | Código de producción listo para despliegue | Solo recibe merges de `release` o `hotfix`. Etiquetado con versiones semánticas (v1.0.0). |
| `develop` | Rama principal de integración para desarrollo | Recibe merges de ramas `feature`. Base para nuevas características. |
| `feature/*` | Nuevas funcionalidades o módulos | Creada desde `develop`, se fusiona de nuevo a `develop` mediante Pull Request. |
| `hotfix/*` | Correcciones urgentes en producción | Creada desde `main`, se fusiona a `main` y a `develop`. |

---

## 3. Convención de Commits (Conventional Commits)

Formatos de mensaje estandarizados:

- `feat(clients): añadir validación de mayoría de edad`
- `fix(accounts): corregir error 500 en actualización de estado`
- `docs(readme): actualizar índice de documentación`
- `test(transactions): añadir pruebas unitarias de transferencias`
- `refactor(domain): simplificar lógica de fábrica de números de cuenta`
