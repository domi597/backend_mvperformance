# ⚙️ MVPerformance — Backend

> REST-API für die **MVPerformance KFZ-Werkstatt** — Spring Boot 4 · Java 25 · PostgreSQL · JWT-Auth · OpenAPI.

---

## 📋 Projektübersicht

Das Backend ist das Herzstück der MVPerformance-Plattform. Es stellt sämtliche Daten und Geschäftslogik der Werkstatt über eine REST-API bereit — von der Online-Terminbuchung über Kunden- und Fahrzeugverwaltung bis hin zu Angeboten, Leistungen, Bewertungen, Öffnungszeiten und Kontaktdaten. Authentifizierung erfolgt zustandslos via JWT, der Zugriff wird rollenbasiert (USER / ADMIN) geschützt.

**Auftraggeber:** Devrim Gül

---

## 👥 Team

| Name | Aufgabe |
|------|---------|
| **Jan** | Frontend — Design & Logik der Benutzeroberfläche |
| **Nicolas** | Frontend-Design, Page-Entwicklung & Verbindung zwischen Frontend und Backend |
| **Dominik** | Backend-Routen (Controller / Endpoints) & Frontend-Entwicklung |

Alle drei sind sowohl am Frontend als auch am Backend beteiligt.

---

## 🎯 Backend-Ziele

- REST-API für die öffentliche Website **und** den Admin-Bereich
- Stateless **JWT-Authentifizierung** mit Rollen (USER, ADMIN)
- Saubere Schichten-Trennung: **Controller → Service → Repository → Entity**, mit DTO/MapStruct an den Grenzen
- Validierung aller Eingaben (`jakarta.validation`)
- Zentrales Error-Handling via `GlobalExceptionHandler`
- API-Dokumentation per **Swagger / OpenAPI**
- CORS-Setup für das Vite-Frontend (`http://localhost:5173`)

---

## 🛠️ Tech Stack

| Technologie | Zweck |
|-------------|-------|
| [Spring Boot 4.0.4](https://spring.io/projects/spring-boot) | Application Framework |
| **Java 25** | Sprache |
| Spring Web (MVC) | REST-Controller |
| Spring Data JPA / Hibernate | ORM & Persistenz |
| [PostgreSQL](https://www.postgresql.org/) | Datenbank |
| Spring Security | Auth-Filter, Passwort-Hashing, URL-Schutz |
| [JJWT 0.12.6](https://github.com/jwtk/jjwt) | JWT erstellen & validieren |
| Spring Validation | DTO-Validation (`@NotBlank`, `@Email`, …) |
| [MapStruct 1.5.5](https://mapstruct.org/) | Entity ↔ DTO Mapping |
| [Lombok](https://projectlombok.org/) | Boilerplate-Reduktion |
| [springdoc-openapi 2.6](https://springdoc.org/) | Swagger-UI |
| Spring Boot Actuator | Health & Metrics |
| Maven Wrapper | Build (`./mvnw`) |

---

## 🗂️ Projektstruktur

```
src/main/java/at/htlkaindorf/backend_mwperformence/
├── config/
│   ├── SecurityConfig.java            # Filter-Chain, Rollen, Public/Protected Routes
│   ├── JwtAuthenticationFilter.java   # Liest Bearer-Token aus Header
│   ├── JwtService.java                # Signiert & validiert Tokens
│   ├── CorsConfig.java                # CORS für das Frontend
│   ├── OpenApiConfig.java             # Swagger / OpenAPI Setup
│   └── StaticResourceConfig.java      # Statische Auslieferung (z. B. Bilder)
├── controller/                        # REST-Endpoints (siehe Tabelle unten)
├── services/                          # Geschäftslogik (Auth, Termine, Kunden, …)
├── repositories/                      # Spring Data JPA Repositories
├── entites/                           # JPA-Entities + Enums (Role, AppointmentStatus)
├── dtos/                              # Request/Response-DTOs
├── mapper/                            # MapStruct-Mapper Entity ↔ DTO
└── exception/
    ├── ApiException.java              # Eigene Runtime-Exception
    └── GlobalExceptionHandler.java    # @RestControllerAdvice für einheitliche Fehler

src/main/resources/
├── application.properties             # Env-basierte Konfiguration
└── data.sql                           # Seed-Daten (Demo-User, Services, …)
```

---

## 🧱 Datenmodell (Entities)

| Entity | Zweck |
|--------|-------|
| `User` | Kunden- & Admin-Accounts, Passwort-Hash, `Role` |
| `Role` *(Enum)* | `USER`, `ADMIN` |
| `Vehicle` | Fahrzeug eines Kunden (gehört zu `User`) |
| `Appointment` | Termin: Kunde, Fahrzeug, Service(s), Datum, `AppointmentStatus` |
| `AppointmentStatus` *(Enum)* | z. B. `PENDING`, `CONFIRMED`, `DONE`, `CANCELLED` |
| `Service` | Leistung (Ölwechsel, Bremsservice, …) inkl. Preis |
| `Offer` | Aktion / Angebot |
| `Review` | Kundenbewertung |
| `ContactInfo` | Adresse, Telefon, E-Mail der Werkstatt |
| `OpeningHours` | Öffnungszeiten pro Wochentag |

---

## 🌐 REST-API

Alle Endpoints sind unter dem Präfix **`/api`** erreichbar.

### Auth — `/api/auth`
| Methode | Pfad | Beschreibung |
|---------|------|--------------|
| `POST` | `/login` | Login → liefert JWT (`AuthResponse`) |
| `POST` | `/register` | Registrierung eines neuen Kunden |

### Termine — `/api/appointments`
| Methode | Pfad | Beschreibung |
|---------|------|--------------|
| `GET` | `/` | Liste aller Termine (Admin) bzw. eigene Termine (User) |
| `GET` | `/{id}` | Termin-Details |
| `POST` | `/` | Termin anlegen |
| `PATCH` | `/{id}/status` | Status setzen (`StatusUpdateRequest`) |
| `DELETE` | `/{id}` | Termin löschen |

### Weitere Ressourcen
| Bereich | Pfad |
|---------|------|
| Kunden / User | `/api/users` |
| Fahrzeuge | `/api/vehicles` |
| Leistungen | `/api/services` |
| Angebote | `/api/offers` |
| Bewertungen | `/api/reviews` |
| Öffnungszeiten | `/api/opening-hours` |
| Kontaktdaten | `/api/contact-info` |

> **Swagger UI:** `http://localhost:8080/swagger-ui.html`
> **OpenAPI-JSON:** `http://localhost:8080/v3/api-docs`

---

## 🔐 Authentifizierung & Sicherheit

- **Stateless JWT** (HS256), signiert mit `JWT_SECRET`
- Login (`/api/auth/login`) liefert ein Token; geschützte Endpoints erwarten:
  ```
  Authorization: Bearer <token>
  ```
- `JwtAuthenticationFilter` parst den Header und setzt den `SecurityContext`
- Passwörter werden mit **BCrypt** gehasht (über Spring Security)
- Rollen-Steuerung über `Role`-Enum (`USER`, `ADMIN`)
- CORS in `CorsConfig` für das Frontend freigegeben
- Einheitliche Fehlerantworten via `GlobalExceptionHandler`

---

## 🚀 Setup & lokale Entwicklung

### Voraussetzungen

- **JDK 25**
- **PostgreSQL** (lokal oder via Docker)
- Maven Wrapper (mitgeliefert: `./mvnw`)

### Datenbank vorbereiten

```bash
# Beispiel: lokale Datenbank anlegen
createdb mvperformance
```

oder per Docker:

```bash
docker run --name mvperformance-pg \
  -e POSTGRES_DB=mvperformance \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 -d postgres:16
```

### Umgebungsvariablen

Werden in `application.properties` gelesen:

| Variable | Default | Beschreibung |
|----------|---------|--------------|
| `APP_NAME` | `backend_MWPerformence` | Spring-App-Name |
| `APP_PORT` | `8080` | Server-Port |
| `DB_URL` | *(erforderlich)* | z. B. `jdbc:postgresql://localhost:5432/mvperformance` |
| `DB_USERNAME` | *(erforderlich)* | DB-User |
| `DB_PASSWORD` | *(erforderlich)* | DB-Passwort |
| `JWT_SECRET` | `mwperformance-super-secret-key-min-32-chars!!` | min. 32 Zeichen |
| `JWT_EXPIRATION_MS` | `86400000` (24 h) | Token-Gültigkeit |

> Hibernate läuft mit `ddl-auto=update` — Schemaänderungen werden im Dev-Betrieb automatisch übernommen.

### Start

```bash
./mvnw spring-boot:run
```

API danach erreichbar unter `http://localhost:8080`.

### Weitere Maven-Befehle

```bash
./mvnw clean package    # Build → target/*.jar
./mvnw test             # Tests inkl. spring-security-test
./mvnw spring-boot:run  # Dev-Start mit Devtools-Hot-Reload
```

### Ausführbares JAR

```bash
./mvnw clean package
java -jar target/backend_MWPerformence-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Tests

- **JUnit 5** via `spring-boot-starter-test`
- **`spring-security-test`** für Auth-Szenarien
- Test-Sourcen unter `src/test/java/at/htlkaindorf/backend_mwperformence/`

---

## 📊 Monitoring

Spring Boot Actuator ist aktiv. Standard-Endpoints:

- `GET /actuator/health` — Health-Check
- `GET /actuator/info` — App-Infos

---

## 🔗 Verwandte Repositories

- **Frontend:** [`frontend_mvperformence`](../frontend_mvperformence) — React 19 + Vite + MUI

---

*HTL Kaindorf — Schulprojekt 2025/26*
