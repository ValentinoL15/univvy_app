# 🚀 Univvy System Backend - Arquitectura de Microservicios

Este repositorio contiene el ecosistema de microservicios para el proyecto Univvy. El sistema utiliza una arquitectura distribuida basada en **Spring Cloud**.

## 📂 Estructura del Proyecto

El sistema se divide en tres componentes principales:

1.  **`eureka-server`**: Servidor de descubrimiento de servicios (Service Discovery). Es el núcleo que permite que los microservicios se encuentren entre sí.
2.  **`pagos`**: Microservicio encargado de la gestión de transacciones y pagos (integrado con Stripe).
3.  **`unnivy_app`**: Microservicio principal de lógica de negocio, usuarios y comunicación en tiempo real.

## 🛠️ Requisitos del Sistema

* **Java JDK 17**
* **Maven 3.8+**
* **MySQL Server** (asegurate de tener creadas las bases de datos para cada servicio)
* **Lector de variables de entorno** (o configuración manual en los archivos `.properties` / `.yml`)

---

## 🚦 Orden de Encendido

Para que el sistema funcione correctamente, es **obligatorio** seguir este orden:

### 1. Eureka Server (Prioridad Alta)
Sin este servidor, los demás servicios no podrán comunicarse entre sí mediante Feign Clients.
* **Ruta:** `./eureka-server`
* **Comando:** `mvn spring-boot:run`
* **Puerto por defecto:** `8761`
* **Verificación:** Acceder a `http://localhost:8761` para ver el panel de control.

### 2. Microservicio de Pagos
* **Ruta:** `./pagos`
* **Comando:** `mvn spring-boot:run`
* **Dependencia:** Requiere que Eureka esté arriba para registrarse.

### 3. Unnivy App (Servicio Principal)
* **Ruta:** `./unnivy_app`
* **Comando:** `mvn spring-boot:run`
* **Dependencia:** Requiere Eureka y, opcionalmente, que el servicio de Pagos esté activo para realizar consultas inter-service vía OpenFeign.

---

## 🔧 Configuración General

Cada microservicio tiene su propio archivo de configuración. Asegúrate de revisar:

* **Puertos:** Verifica que no haya colisiones (ej. Eureka: 8761, App: 8080, Pagos: 8081).
* **Credenciales de DB:** Configura usuario y contraseña de MySQL en cada `application.properties`.
* **Tokens:** Configura el secreto del JWT para la autenticación entre servicios.

## 🧪 Comandos Útiles

* **Limpiar e instalar todo:**
    ```bash
    mvn clean install
    ```
* **Levantar un servicio específico:**
    ```bash
    cd backend
    cd nombre-del-servicio && mvn spring-boot:run
    ```
