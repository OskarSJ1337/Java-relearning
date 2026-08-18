# Java Backend Practice Project

Ett praktiskt övningsprojekt för att fräscha upp Java och successivt bygga erfarenhet inom sådant som ofta efterfrågas i backendroller:

- Java
- Spring Boot
- REST API:er
- PostgreSQL
- JPA / Hibernate
- Integrationer
- Testning
- Docker
- CI/CD
- Mikrotjänster

Projektet byggs stegvis. Varje uppgift bygger vidare på föregående uppgift.

> Målet är inte att bygga ett stort eller perfekt system, utan att öva på vanliga problem och tekniker från moderna Java-backends.

---

## Projektidé

Vi bygger ett enkelt ordersystem.

Systemet kommer så småningom kunna hantera:

- produkter
- orders
- orderrader
- orderstatus
- betalningar
- databaslagring
- externa API-anrop
- Docker
- automatiserade tester
- CI

---

# Uppgifter

## Status

- [ ] 1. Domänmodell i ren Java
- [ ] 2. Orderns affärslogik och livscykel
- [ ] 3. JUnit-tester
- [ ] 4. Spring Boot
- [ ] 5. REST API
- [ ] 6. Validation och felhantering
- [ ] 7. PostgreSQL och Spring Data JPA
- [ ] 8. Flyway och databasdesign
- [ ] 9. Sökning, filtrering och pagination
- [ ] 10. Extern API-integration
- [ ] 11. Integrationstester med Testcontainers
- [ ] 12. Docker och Docker Compose
- [ ] 13. CI med GitHub Actions
- [ ] 14. Separat Payment Service
- [ ] 15. Produktionslik finish

---

# 1. Domänmodell i ren Java

Bygg grunden till ordersystemet utan Spring, databas eller externa bibliotek.

Skapa:

```text
Product
OrderItem
Order
OrderStatus
```

## Product

Ska innehålla:

```text
id
name
price
```

Krav:

- `id` måste finnas
- `name` får inte vara `null`, tom eller bara whitespace
- `price` måste vara större än `0`
- använd inte `double` eller `float` för pengar

Fundera på vilken Java-typ som lämpar sig för priser och varför.

## OrderItem

Ska innehålla:

```text
product
quantity
```

Krav:

- produkt måste finnas
- `quantity` måste vara minst `1`
- en orderrad ska kunna beräkna sitt eget totalpris

Exempel:

```text
Produktpris: 1200
Antal: 3
Total: 3600
```

## OrderStatus

Skapa en enum:

```text
CREATED
PAID
SHIPPED
CANCELLED
```

## Order

Ska innehålla:

```text
id
items
status
```

En ny order ska alltid börja med status:

```text
CREATED
```

Implementera stöd för ungefär följande operationer:

```java
order.addItem(product, quantity);
order.removeItem(productId);
order.getTotalPrice();
order.getStatus();
```

Exakta metodsignaturer bestämmer du själv.

## Affärsregler

Om samma produkt läggs till flera gånger ska den inte skapa flera orderrader.

Exempel:

```java
order.addItem(keyboard, 2);
order.addItem(keyboard, 3);
```

ska motsvara:

```text
Keyboard x 5
```

`removeItem` ska ta bort hela orderraden.

En tom order ska ha totalpris `0`.

Ogiltiga operationer ska resultera i exceptions.

Exempel:

```java
new Product(..., -100);
order.addItem(product, 0);
```

### Bonus

Efter att ordern lämnat status `CREATED` får orderns innehåll inte längre ändras.

---

# 2. Orderns affärslogik och livscykel

Utöka ordern med tydliga statusövergångar.

Implementera operationer som:

```java
order.pay();
order.ship();
order.cancel();
```

## Tillåtna övergångar

```text
CREATED -> PAID
CREATED -> CANCELLED
PAID    -> SHIPPED
```

Andra övergångar ska inte vara tillåtna.

Exempel på ogiltiga operationer:

```text
SHIPPED -> CANCELLED
CANCELLED -> PAID
CREATED -> SHIPPED
```

## Ytterligare regler

- en tom order får inte betalas
- en betald order får inte ändras
- en skickad order får inte ändras
- en avbruten order får inte ändras
- samma statusoperation ska inte kunna köras flera gånger utan konsekvens

Använd exceptions när en operation bryter mot domänreglerna.

### Bonus

Skapa egna domänexceptions, exempelvis:

```text
InvalidOrderStateException
EmptyOrderException
```

Försök undvika att sprida statuskontroller överallt. Fundera på var reglerna hör hemma.

---

# 3. JUnit-tester

Skriv automatiserade tester för domänmodellen.

Använd JUnit 5.

Du ska minst testa:

## Product

- giltig produkt kan skapas
- tomt namn nekas
- negativt pris nekas
- pris `0` nekas

## OrderItem

- korrekt totalpris
- quantity `0` nekas
- negativ quantity nekas

## Order

- ny order har status `CREATED`
- produkt kan läggas till
- samma produkt läggs ihop
- produkt kan tas bort
- totalpris beräknas korrekt
- tom order har totalpris `0`
- tom order kan inte betalas
- order kan betalas
- betald order kan skickas
- ogiltiga statusövergångar nekas
- order kan inte ändras efter betalning

Försök hålla varje test fokuserat på ett beteende.

### Bonus

Titta på:

```java
@ParameterizedTest
@ValueSource
```

och använd parameteriserade tester där det känns naturligt.

---

# 4. Introducera Spring Boot

Gör projektet till en Spring Boot-applikation.

## Krav

Skapa en Spring Boot-app och behåll domänlogiken från tidigare uppgifter.

Strukturera projektet ungefär efter ansvar, exempelvis:

```text
controller
service
repository
domain
```

Det är inte ett krav att använda exakt dessa paket.

Skapa en enkel service för produkter och orders.

Exempel:

```java
ProductService
OrderService
```

Till en början får data fortfarande lagras i minnet.

Exempel:

```java
Map<Long, Product>
Map<Long, Order>
```

## Mål

Förstå rollerna för:

```java
@Component
@Service
@Repository
```

samt dependency injection via constructor injection.

Undvik field injection.

### Bonus

Skapa olika Spring-profiler för exempelvis:

```text
dev
test
```

---

# 5. REST API

Exponera funktionaliteten via HTTP.

Implementera minst följande endpoints.

## Products

```http
POST /products
GET  /products
GET  /products/{id}
```

## Orders

```http
POST /orders
GET  /orders/{id}
POST /orders/{id}/items
DELETE /orders/{id}/items/{productId}
POST /orders/{id}/pay
POST /orders/{id}/ship
POST /orders/{id}/cancel
```

Bestäm själv request- och response-format.

Exempel:

```json
{
  "name": "Mechanical Keyboard",
  "price": 1299.00
}
```

## HTTP-statuskoder

Använd lämpliga statuskoder, exempelvis:

```text
200 OK
201 Created
204 No Content
400 Bad Request
404 Not Found
409 Conflict
```

Fundera på vilken statuskod som passar för olika situationer.

## Viktigt

Exponera helst inte domänobjekten direkt som API-kontrakt.

Skapa DTO:er för requests och responses.

### Bonus

Returnera `Location`-header när en resurs skapas.

---

# 6. Validation och felhantering

Lägg till validering av inkommande HTTP-data.

Använd Bean Validation.

Exempel på relevanta annotations:

```java
@NotBlank
@NotNull
@Positive
@Min
@Valid
```

Ogiltig input ska ge tydliga `400 Bad Request`-svar.

Exempel:

```json
{
  "errors": [
    "name must not be blank",
    "price must be greater than zero"
  ]
}
```

## Global felhantering

Skapa central felhantering med exempelvis:

```java
@RestControllerAdvice
@ExceptionHandler
```

Hantera minst:

- validation errors
- resurs hittades inte
- ogiltig orderstatus
- generell oväntad exception

Fundera på skillnaden mellan:

```text
400 Bad Request
404 Not Found
409 Conflict
500 Internal Server Error
```

### Bonus

Skapa ett gemensamt error response-format med exempelvis:

```text
timestamp
status
error
message
path
```

---

# 7. PostgreSQL och Spring Data JPA

Byt ut in-memory-lagringen mot PostgreSQL.

Använd Spring Data JPA.

Skapa persistens för:

```text
Product
Order
OrderItem
```

## Relationer

Fundera över relationerna mellan:

```text
Order
  |
  +-- OrderItem
         |
         +-- Product
```

Använd relevanta JPA-mappningar.

Exempel:

```java
@OneToMany
@ManyToOne
```

## Repository

Skapa repositories med Spring Data.

Exempel:

```java
public interface ProductRepository
        extends JpaRepository<Product, Long> {
}
```

## Krav

Efter en omstart ska skapade produkter och orders finnas kvar.

### Bonus

Undersök:

```java
@Transactional
```

och använd transactions där flera databasoperationer behöver höra ihop.

---

# 8. Flyway och databasdesign

Lägg till Flyway för databasmigreringar.

Databasen ska inte längre skapas implicit av Hibernate.

Skapa migrationer, exempelvis:

```text
V1__create_products.sql
V2__create_orders.sql
V3__create_order_items.sql
```

## Krav

Databasschemat ska gå att skapa från en tom PostgreSQL-databas genom att starta applikationen.

Lägg till relevanta constraints i databasen.

Exempel:

```text
PRIMARY KEY
FOREIGN KEY
NOT NULL
UNIQUE
CHECK
```

Fundera på vilka regler som ska finnas:

- endast i Java
- endast i databasen
- i båda

### Bonus

Lägg till en ny kolumn genom en separat migration istället för att ändra den första migrationen.

Exempel:

```text
V4__add_created_at_to_orders.sql
```

---

# 9. Sökning, filtrering och pagination

Utöka produkt-API:t.

Implementera exempelvis:

```http
GET /products?name=keyboard
GET /products?minPrice=100&maxPrice=2000
GET /products?page=0&size=20
GET /products?sort=price,asc
```

## Krav

Stöd minst:

- sökning på namn
- minpris
- maxpris
- pagination
- sortering

Använd Spring Data där det är lämpligt.

Fundera på vad som händer om:

```text
page = -1
size = 1000000
minPrice > maxPrice
```

### Bonus

Lägg till filter på orderstatus:

```http
GET /orders?status=PAID
```

---

# 10. Extern API-integration

Låt applikationen prata med ett externt HTTP-API.

Ett exempel är valutakonvertering.

Implementera:

```http
GET /orders/{id}?currency=EUR
```

Svaret kan exempelvis innehålla:

```json
{
  "id": 42,
  "totalSek": 2500.00,
  "currency": "EUR",
  "convertedTotal": 225.31
}
```

## Krav

Skapa en separat klientklass för kommunikationen.

Exempel:

```text
CurrencyClient
```

Controllern ska inte själv göra externa HTTP-anrop.

Hantera:

- lyckat svar
- timeout
- felstatus från externa API:t
- ogiltigt svar
- nätverksfel

Fundera på vad ditt eget API ska göra om integrationen ligger nere.

### Bonus

Implementera enkel retry eller fallback.

---

# 11. Integrationstester med Testcontainers

Lägg till integrationstester som använder riktig PostgreSQL.

Använd Testcontainers.

## Testa minst ett komplett flöde

Exempel:

```text
POST /products
      |
      v
produkt sparas i PostgreSQL
      |
      v
GET /products/{id}
      |
      v
korrekt JSON returneras
```

Testa även ett orderflöde:

```text
Skapa produkt
-> skapa order
-> lägg till produkt
-> betala order
-> hämta order
```

## Mål

Integrationstesterna ska inte vara beroende av att du manuellt har startat en databas.

### Bonus

Mocka det externa API:t med exempelvis WireMock eller MockWebServer.

---

# 12. Docker och Docker Compose

Containerisera applikationen.

Skapa:

```text
Dockerfile
compose.yaml
```

eller:

```text
docker-compose.yml
```

## Krav

Det ska gå att starta hela systemet med:

```bash
docker compose up
```

Det ska starta minst:

```text
Spring Boot app
PostgreSQL
```

Applikationen ska ansluta till PostgreSQL-containern via Docker-nätverket.

Konfiguration såsom:

```text
database host
database port
username
password
```

ska kunna anges via environment variables.

### Bonus

Använd multi-stage Docker build.

Försök få slutimagen rimligt liten.

---

# 13. CI med GitHub Actions

Skapa en GitHub Actions-workflow.

Vid varje push eller pull request ska pipelinen minst:

```text
checkout
-> installera Java
-> bygga projektet
-> köra tester
```

Exempel på mål:

```text
Push
  |
  v
Compile
  |
  v
Unit tests
  |
  v
Integration tests
  |
  v
Build
```

En trasig test ska göra pipelinen röd.

## Krav

Workflow-filen ska ligga under:

```text
.github/workflows/
```

### Bonus

Bygg även Docker-imagen i CI.

Extra bonus:

Publicera Docker-imagen till GitHub Container Registry när kod mergas till `main`.

---

# 14. Separat Payment Service

Bryt ut betalningar till en egen Spring Boot-applikation.

Arkitekturen blir ungefär:

```text
Order Service
     |
     | HTTP
     v
Payment Service
```

Payment Service ska ha en endpoint ungefär som:

```http
POST /payments
```

Request:

```json
{
  "orderId": 123,
  "amount": 2500.00
}
```

Response:

```json
{
  "paymentId": "abc123",
  "status": "APPROVED"
}
```

Order Service ska anropa Payment Service när en order betalas.

## Fundera på

Vad händer om:

1. Payment Service godkänner betalningen
2. nätverket går ner innan Order Service får svaret?

Hur undviker du dubbelbetalning om Order Service försöker igen?

Du behöver inte lösa problemet perfekt, men dokumentera hur du skulle kunna hantera det.

### Bonus

Implementera idempotency key för betalningsanrop.

---

# 15. Produktionslik finish

Gör projektet mer likt en riktig backendtjänst.

Lägg till några av följande delar.

## Logging

Använd strukturerad och användbar logging.

Logga exempelvis:

```text
order created
order paid
payment failed
external integration failed
```

Undvik att logga känslig information.

## Health checks

Lägg till Spring Boot Actuator.

Exponera exempelvis:

```http
/actuator/health
```

## Configuration

Flytta miljöspecifik konfiguration till:

```text
application.yml
environment variables
Spring profiles
```

Lägg inga lösenord direkt i Git-repot.

## Dokumentation

Dokumentera API:t.

Du kan exempelvis använda OpenAPI / Swagger.

## Observability

Bonusområden att undersöka:

```text
metrics
request IDs
distributed tracing
structured logs
```

## Slutmål

Det ska vara möjligt för en annan utvecklare att:

```bash
git clone <repo>
docker compose up
```

och sedan förstå:

- vad projektet gör
- hur det startas
- hur tester körs
- vilka API-endpoints som finns
- hur arkitekturen ser ut

---

# Föreslagen arbetsmetod

Gör en uppgift i taget.

För varje uppgift:

1. Implementera kraven.
2. Kör applikationen/testerna lokalt.
3. Commit:a.
4. Gör gärna en egen kort code review.
5. Fortsätt först därefter till nästa uppgift.

Exempel på commits:

```text
feat: add initial order domain model
test: add order lifecycle tests
feat: expose product REST API
feat: persist orders with PostgreSQL
build: add Docker support
ci: add GitHub Actions workflow
```

---

# Principer att tänka på

Under projektets gång, fundera regelbundet på:

- Har varje klass ett tydligt ansvar?
- Finns affärslogiken på rätt plats?
- Är koden enkel att testa?
- Exponeras interna implementationdetaljer i API:t?
- Är felhanteringen tydlig?
- Kan systemet startas av någon annan?
- Kan en annan utvecklare förstå projektet utan muntlig förklaring?

Försök inte överdesigna lösningen i förväg.

Lös problemet du har nu och refaktorera när ett faktiskt behov uppstår.

---

# Tekniker som projektet berör

När alla uppgifter är genomförda har projektet berört bland annat:

```text
Java
Object-oriented design
BigDecimal
Collections
Exceptions
JUnit 5
Spring Boot
Dependency Injection
REST
HTTP
DTOs
Bean Validation
PostgreSQL
SQL
Spring Data JPA
Hibernate
Flyway
Transactions
External API integrations
Testcontainers
Docker
Docker Compose
GitHub Actions
CI/CD
Microservices
Spring Boot Actuator
OpenAPI
```

---

# Slutmål

Projektet är färdigt när du kan förklara inte bara **hur** koden fungerar, utan även varför du valde lösningarna du gjorde och vilka alternativ som hade varit möjliga.

Det är viktigare än att projektet innehåller så många tekniker som möjligt.
