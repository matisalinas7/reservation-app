# Reservation App

Sistema de reservas full stack desarrollado con Spring Boot 4 y Angular 21.
Permite crear, listar y cancelar reservas con validación de disponibilidad en tiempo real.

## Tecnologías

**Backend**
- Java + Spring Boot 4
- Spring Data JPA + Hibernate
- MySQL
- Swagger / OpenAPI

**Frontend**
- Angular 21
- TypeScript
- Reactive Forms

## Funcionalidades

- Listado de reservas con estado (activa, cancelada, completada)
- Creación de reserva con validación de solapamiento de horarios
- Cancelación de reserva con feedback visual
- Manejo global de excepciones con respuestas de error estructuradas
- Notificaciones toast para éxitos y errores
- Página 404

## Arquitectura del backend

Sigue una arquitectura en capas:
- **Controller** — recibe y responde peticiones HTTP
- **Service** — lógica de negocio (interface + implementación)
- **Repository** — acceso a datos con Spring Data JPA
- **DTOs** — separación entre entidades internas y respuestas de la API

## Levantar el proyecto localmente

### Requisitos
- Java 21+
- Node.js 20+
- MySQL 8+

### Backend
```bash
cd reservation-backend
# Configurar credenciales de MySQL en application.properties
./mvnw spring-boot:run
```
Swagger disponible en `http://localhost:8081/swagger-ui/index.html`

### Frontend
```bash
cd reservation-frontend
npm install
ng serve
```
App disponible en `http://localhost:4200`

## Próximas mejoras

- Autenticación con Spring Security + JWT
- Filtros y paginación en el listado
- Tests unitarios e integración
- Servicios dinámicos desde la API
