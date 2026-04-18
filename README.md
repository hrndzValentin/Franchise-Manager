# 🚀 Franquicias API - Spring WebFlux

API reactiva desarrollada con **Spring Boot + WebFlux** para gestionar franquicias, sucursales y productos, permitiendo operaciones concurrentes y escalables.

---

## 🧠 Descripción del proyecto

Este microservicio permite:

* Crear y gestionar **franquicias**
* Administrar **sucursales** dentro de cada franquicia
* Gestionar **productos** y su stock
* Consultar el **producto con mayor stock por sucursal**

El sistema está diseñado siguiendo un modelo **NoSQL embebido en MongoDB**, optimizando la lectura y evitando joins innecesarios.

---

## ⚙️ Tecnologías utilizadas

* **Java 21**
* **Spring Boot 3.x**
* **Spring WebFlux (programación reactiva)**
* **Spring Data MongoDB Reactive**
* **MongoDB**
* **Project Reactor (Mono / Flux)**
* **JUnit 5 + StepVerifier**
* **WebTestClient (testing de endpoints reactivos)**
* **Docker & Docker Compose**
* **Terraform (Infraestructura como código)**

---

## ⚡ Programación reactiva con WebFlux

Se utilizó **Spring WebFlux** para construir una API no bloqueante basada en el modelo reactivo.

### Beneficios:

* Manejo eficiente de múltiples requests concurrentes
* Uso de **event loop (Netty)** en lugar de threads bloqueantes
* Mayor escalabilidad con menos recursos

### Ejemplo:

```java id="ex1"
public Flux<TopProductoDTO> obtenerTopProductosPorSucursal(String franquiciaId) {
    return franquiciaRepository.findById(franquiciaId)
        .flatMapMany(f -> Flux.fromIterable(f.getSucursales()))
        .map(sucursal -> {
            Producto top = sucursal.getProductos().stream()
                .max(Comparator.comparingInt(Producto::getStock))
                .orElse(null);

            return new TopProductoDTO(
                sucursal.getNombre(),
                top.getNombre(),
                top.getStock()
            );
        });
}
```

---

## 🧱 Modelo de datos

```text id="ex2"
Franquicia
 └── Sucursales
      └── Productos (stock)
```

📌 Se utilizó un modelo **embebido en MongoDB** para:

* Reducir la cantidad de queries
* Evitar joins
* Mejorar el rendimiento en lecturas

---

## 🐳 Ejecución con Docker

### 📋 Prerrequisitos

* Docker
* Docker Compose

---

### ▶️ Pasos

```bash id="ex3"
# 1. Compilar proyecto
mvn clean package -DskipTests

# 2. Levantar servicios
docker-compose up --build
```

---

### 🌐 Acceso

```text id="ex4"
http://localhost:8080
```

---

### 📦 Servicios

* `app` → API Spring Boot
* `mongo` → Base de datos MongoDB

---

## 📡 Endpoints principales

### 🏢 Franquicias

* `POST /api/franquicias`
* `PATCH /api/franquicias/{id}/nombre`

---

### 🏬 Sucursales

* `POST /api/franquicias/{id}/sucursales`
* `PATCH /api/franquicias/{id}/sucursales/{sucursalId}/nombre`

---

### 📦 Productos

* `POST /api/franquicias/{id}/sucursales/{sucursalId}/productos`
* `DELETE /api/franquicias/{id}/sucursales/{sucursalId}/productos/{productoId}`
* `PATCH /api/franquicias/{id}/productos/{productoId}/stock`
* `PATCH /api/franquicias/{id}/productos/{productoId}/nombre`

---

### 🔥 Endpoint clave

```http id="ex5"
GET /api/franquicias/{id}/top-productos
```

Retorna el producto con mayor stock por sucursal.

---

## 🧪 Testing

Se implementaron pruebas unitarias y de integración usando herramientas reactivas:

### ✔️ StepVerifier

Permite validar flujos reactivos (`Mono`, `Flux`):

```java id="ex6"
StepVerifier.create(service.obtenerTopProductosPorSucursal(id))
    .expectNextCount(1)
    .verifyComplete();
```

---

### ✔️ WebTestClient

Permite probar endpoints WebFlux sin levantar servidor real:

```java id="ex7"
webTestClient.post()
    .uri("/api/franquicias")
    .bodyValue(request)
    .exchange()
    .expectStatus().isOk();
```

---

## ⚠️ Manejo de errores

Se implementó un **GlobalExceptionHandler** con respuestas estructuradas:

```json id="ex8"
{
  "status": 404,
  "message": "Franquicia no encontrada"
}
```

---

## 🐳 Contenerización

La aplicación se empaqueta usando Docker:

* Imagen basada en `openjdk`
* MongoDB en contenedor separado
* Orquestación con Docker Compose

---

## ☁️ Infraestructura como código

Se incluye configuración con **Terraform** para aprovisionar:

* Base de datos (Mongo compatible en AWS DocumentDB)
* Seguridad básica

Esto permite:

* Versionar infraestructura
* Reproducir entornos
* Automatizar despliegues

---

## 🧠 Decisiones técnicas

* ✔️ Uso de **WebFlux** para alta concurrencia
* ✔️ Modelo embebido en MongoDB (optimización de consultas)
* ✔️ Arquitectura por capas (Controller, Service, Repository)
* ✔️ Manejo global de errores
* ✔️ Validaciones con `@Valid`
* ✔️ Docker para portabilidad

---

## 📌 Mejoras futuras

* Autenticación y autorización (JWT)
* Observabilidad (logs estructurados, métricas)
* CI/CD pipeline
* Despliegue en nube (AWS / GCP)
* Caché con Redis

---

## 👨‍💻 Autor

Valentín Sánchez
