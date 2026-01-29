# Strava Club Activity Collector (StravaCC)

A Spring Boot application that automatically collects and stores Strava club activities from the Strava API. The application periodically fetches club activities, stores them in a PostgreSQL database, and provides REST endpoints to view and export the collected data.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Strava API Setup](#strava-api-setup)
- [Troubleshooting](#troubleshooting)

## ✨ Features

- **Automated Activity Collection**: Scheduled polling of Strava club activities at configurable intervals
- **OAuth Token Management**: Automatic token refresh to maintain API access
- **Automated OAuth Flow**: Web-based OAuth token retrieval with automatic persistence
- **Data Persistence**: Stores activities in PostgreSQL with duplicate detection
- **REST API**: Endpoints to retrieve activities and export data
- **Excel Export**: Export collected activities to Excel format (.xlsx)
- **Health Monitoring**: Spring Boot Actuator endpoints for health checks and metrics

## 🛠 Technology Stack

- **Java**: 17
- **Spring Boot**: 3.5.9
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Key Libraries**:
  - Spring Web (REST APIs)
  - Spring WebFlux (Reactive WebClient for API calls)
  - Spring Data JPA (Database persistence)
  - Apache POI (Excel export)
  - Lombok (Reduced boilerplate)
  - Spring Boot Actuator (Monitoring)

## 📦 Prerequisites

Before running this application, ensure you have:

1. **Java 17** or higher installed
   ```bash
   java -version
   ```

2. **Maven 3.6+** installed
   ```bash
   mvn -version
   ```

3. **PostgreSQL** database running (version 12+ recommended)
   - Create a database (e.g., `postgres` or `strava_cc`)
   - Note the connection details (host, port, database name, username, password)

4. **Strava API Credentials**:
   - Strava Application with Client ID and Client Secret
   - OAuth access token and refresh token
   - Club ID you want to monitor

## 🚀 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/talkraghu/strava-cc.git
   cd strava-cc
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Configure the application** (see [Configuration](#configuration) section)

## ⚙️ Configuration

Edit `src/main/resources/application.properties` with your settings:

### Database Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### Strava API Configuration

```properties
# Strava Base Configuration
strava.base-url=https://www.strava.com/api/v3
strava.club-id=YOUR_CLUB_ID

# Strava OAuth Configuration
strava.oauth.client-id=YOUR_CLIENT_ID
strava.oauth.client-secret=YOUR_CLIENT_SECRET
strava.oauth.redirect-uri=http://localhost:8080/oauth/callback

# Strava OAuth Token State (initial tokens)
strava.auth.access-token=YOUR_ACCESS_TOKEN
strava.auth.refresh-token=YOUR_REFRESH_TOKEN
strava.auth.expires-at=0
```

### Fetch Behavior Configuration

```properties
# Number of activities per API page (max 200)
strava.fetch.per-page=200

# Maximum pages to fetch per poll
strava.fetch.max-pages=1

# Maximum total records per poll cycle
strava.fetch.max-records=5000

# Poll interval in milliseconds (600000 = 10 minutes)
strava.fetch.poll-interval-ms=600000
```

### Server Configuration

```properties
server.port=8080
```

## 🏃 Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using Maven Wrapper (Windows)

```bash
.\mvnw.cmd spring-boot:run
```

### Using Maven Wrapper (Linux/Mac)

```bash
./mvnw spring-boot:run
```

### Running the JAR

After building, run the generated JAR:

```bash
java -jar target/StravaCC-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080` (or your configured port).

## 📡 API Endpoints

### Get All Activities

Retrieve all collected club activities.

**Endpoint**: `GET /activities`

**Response**: JSON array of activity objects

**Example**:
```bash
curl http://localhost:8080/activities
```

### Export Activities to Excel

Download all activities as an Excel file.

**Endpoint**: `GET /export/club-activities.xlsx`

**Response**: Excel file download

**Example**:
```bash
curl -O http://localhost:8080/export/club-activities.xlsx
```

Or open in browser: `http://localhost:8080/export/club-activities.xlsx`

### Health Check

Check application health status.

**Endpoint**: `GET /actuator/health`

**Example**:
```bash
curl http://localhost:8080/actuator/health
```

### Metrics

View application metrics.

**Endpoint**: `GET /actuator/metrics`

### OAuth Token Retrieval

Automated OAuth flow for obtaining Strava API tokens.

**Endpoint**: `GET /oauth/authorize`

**Description**: Initiates the OAuth flow by redirecting to Strava authorization page. After authorization, redirects to `/oauth/callback` which exchanges the code for tokens and displays them in a user-friendly HTML page.

**Example**:
- Open in browser: `http://localhost:8080/oauth/authorize`
- Follow the authorization flow
- Tokens are automatically saved to `application.properties` (if permissions allow)

## 📁 Project Structure

```
StravaCC/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/closecircuit/strava/
│   │   │       ├── StravaCcApplication.java      # Main application class
│   │   │       ├── client/
│   │   │       │   └── StravaClient.java         # Strava API client
│   │   │       ├── config/
│   │   │       │   ├── StravaClientConfig.java   # WebClient configuration
│   │   │       │   ├── StravaProperties.java     # Configuration properties
│   │   │       │   └── SchedulingConfig.java     # Scheduling configuration
│   │   │       ├── controller/
│   │   │       │   ├── ActivityController.java   # REST endpoints
│   │   │       │   ├── ClubActivityExportController.java
│   │   │       │   └── StravaOAuthController.java # OAuth token retrieval
│   │   │       ├── dto/
│   │   │       │   └── StravaActivityDto.java    # Data transfer objects
│   │   │       ├── entity/
│   │   │       │   └── ClubActivityLog.java       # JPA entity
│   │   │       ├── repository/
│   │   │       │   └── ClubActivityRepository.java
│   │   │       ├── scheduler/
│   │   │       │   └── ClubActivityScheduler.java # Scheduled tasks
│   │   │       ├── service/
│   │   │       │   ├── ClubActivityService.java   # Business logic
│   │   │       │   ├── ClubActivityExcelService.java
│   │   │       │   └── StravaTokenService.java    # Token management
│   │   │       ├── util/
│   │   │       │   └── TokenPersistenceUtil.java  # Token persistence utility
│   │   │       └── utils/
│   │   │           └── ActivityIdGenerator.java  # ID generation utility
│   │   └── resources/
│   │       └── application.properties            # Configuration file
│   └── test/
│       └── java/
├── pom.xml                                        # Maven configuration
├── .gitignore
└── README.md
```

## 🗄️ Database Schema

The application creates a `club_activity_log` table with the following structure:

| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT | Primary key (auto-generated) |
| `activity_id` | BIGINT | Unique activity identifier (hash-based) |
| `name` | VARCHAR(255) | Activity name |
| `athlete_name` | VARCHAR(255) | Athlete's full name |
| `type` | VARCHAR(50) | Activity type |
| `sport_type` | VARCHAR(50) | Sport type |
| `distance` | DOUBLE | Distance in kilometers |
| `moving_time` | INTEGER | Moving time in minutes |
| `elapsed_time` | INTEGER | Elapsed time in minutes |
| `elevation_gain` | DOUBLE | Elevation gain in kilometers |
| `device_name` | VARCHAR(100) | Device name |
| `collected_at` | TIMESTAMP | When the activity was collected |

The table uses `activity_id` as a unique constraint to prevent duplicate entries.

## 🔐 Strava API Setup

### Step 1: Create a Strava Application

1. Go to [Strava Developers](https://www.strava.com/settings/api)
2. Click "Create App"
3. Fill in the application details:
   - **Application Name**: Your app name
   - **Category**: Choose appropriate category
   - **Website**: Your website (or `http://localhost`)
   - **Authorization Callback Domain**: `localhost:8080` (for automated OAuth flow)
4. Save and note your **Client ID** and **Client Secret**

### Step 2: Get OAuth Tokens

#### Automated Method (Recommended)

The application includes an automated OAuth flow that simplifies token retrieval:

1. **Update Strava App Settings**:
   - Go to [Strava API Settings](https://www.strava.com/settings/api)
   - Find your application (Client ID: YOUR_CLIENT_ID)
   - Set **Authorization Callback Domain** to: `localhost:8080`
   - Save the changes

2. **Start the Application**:
   ```bash
   mvn spring-boot:run
   ```

3. **Initiate OAuth Flow**:
   - Open your browser and navigate to: `http://localhost:8080/oauth/authorize`
   - You'll be redirected to Strava to authorize the application
   - After authorization, you'll be redirected back to a success page

4. **Token Retrieval**:
   - The success page displays your `access_token`, `refresh_token`, and `expires_at`
   - **Tokens are automatically saved** to `application.properties` (if file permissions allow)
   - If auto-save fails, manually copy the tokens from the success page

5. **Restart Application** (if tokens were auto-saved):
   - Restart the Spring Boot application to load the new tokens
   - The application will automatically refresh tokens when they expire

**Benefits of Automated Method**:
- ✅ No manual URL construction or code copying
- ✅ Automatic token persistence to `application.properties`
- ✅ User-friendly HTML interface with copy buttons
- ✅ Automatic token refresh on expiration

#### Manual Method (Alternative)

If you prefer to get tokens manually:

1. Construct the authorization URL:
   ```
   https://www.strava.com/oauth/authorize?client_id=YOUR_CLIENT_ID&response_type=code&redirect_uri=http://localhost:8080/oauth/callback&scope=activity:read_all
   ```

2. Open the URL in your browser and authorize the application
3. After authorization, you'll be redirected to `http://localhost:8080/oauth/callback?code=AUTHORIZATION_CODE`
4. Exchange the authorization code for tokens:
   ```bash
   curl -X POST https://www.strava.com/oauth/token \
     -d client_id=YOUR_CLIENT_ID \
     -d client_secret=YOUR_CLIENT_SECRET \
     -d code=AUTHORIZATION_CODE \
     -d grant_type=authorization_code
   ```

5. Save the `access_token`, `refresh_token`, and `expires_at` from the response to `application.properties`

### Step 3: Get Your Club ID

1. Navigate to your Strava club page
2. The club ID is in the URL: `https://www.strava.com/clubs/CLUB_ID`

### Step 4: Configure the Application

Update `application.properties` with your credentials (see [Configuration](#configuration) section).

**Note**: The application automatically refreshes tokens when they expire. The refresh token is long-lived and should be kept secure.

## 🔄 How It Works

1. **Scheduled Collection**: The `ClubActivityScheduler` runs at configured intervals (default: 10 minutes)
2. **API Fetching**: `StravaClient` fetches activities from Strava API using pagination
3. **Token Management**: `StravaTokenService` automatically refreshes expired access tokens
4. **Data Processing**: Activities are mapped from API DTOs to entities
5. **Duplicate Prevention**: Unique activity IDs (hash-based) prevent duplicate entries
6. **Persistence**: Activities are saved to PostgreSQL database
7. **Export**: Activities can be exported to Excel format via REST endpoint

## 🐛 Troubleshooting

### Database Connection Issues

- Verify PostgreSQL is running: `pg_isready`
- Check database credentials in `application.properties`
- Ensure the database exists: `CREATE DATABASE your_database_name;`

### Strava API Errors

- **401 Unauthorized**: Check if your access token is valid or needs refresh
- **403 Forbidden**: Verify your OAuth scope includes `read` and `activity:read_all`
- **429 Too Many Requests**: Reduce `poll-interval-ms` or `max-pages` to avoid rate limits

### Token Refresh Issues

- Ensure `refresh_token` is valid and not expired
- Check `client_id` and `client_secret` are correct
- Verify network connectivity to `https://www.strava.com`
- If tokens expire, use the automated OAuth flow: `http://localhost:8080/oauth/authorize`

### OAuth Flow Issues

- **Redirect URI Mismatch**: Ensure `Authorization Callback Domain` in Strava settings matches `localhost:8080`
- **Tokens Not Auto-Saved**: Check file permissions for `application.properties`. If auto-save fails, manually copy tokens from the success page
- **Authorization Denied**: Make sure you authorize the application with the required scopes (`activity:read_all`)

### Application Won't Start

- Check Java version: `java -version` (must be 17+)
- Verify Maven build succeeded: `mvn clean install`
- Check application logs for specific error messages

### No Activities Being Collected

- Verify `strava.club-id` is correct
- Check scheduler is enabled (should see logs every poll interval)
- Verify database connection is working
- Check application logs for API errors

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📧 Contact

For questions or issues, please open an issue on GitHub.

---

**Note**: Remember to keep your Strava API credentials secure. Never commit tokens or secrets to version control. Consider using environment variables or Spring profiles for sensitive configuration in production.

## rags's readme
cd C:\Users\ragk\Documents\workspace-spring-tools-for-eclipse-4.30.0.RELEASE\StravaCC
mvn clean compile
mvn spring-boot:run