# 🗼 Watchtower — Network Security Operations Center

A real-time network security monitoring dashboard that simulates an enterprise Security Operations Center (SOC). Built with a Java Spring Boot backend and a React frontend, Watchtower monitors a fleet of network devices, generates live security events, and calculates a dynamic security score — all updating in real time.

## 🎯 Features

- **Live Device Monitoring** — Tracks 10 network devices (routers, switches, servers, firewalls, workstations) with real-time traffic, CPU usage, and threat levels
- **Real-Time Threat Feed** — Generates and displays security events (port scans, intrusions, DDoS, malware, login attempts) with severity classification
- **Dynamic Security Score** — Calculates an overall network security score (0–100) based on device health and active threats
- **Live Data Visualization** — Real-time traffic charts, device status distribution, and a security score gauge
- **SOC-Style Dashboard** — Dark command-center interface that updates every 3 seconds

## 🛠️ Tech Stack

**Backend**
- Java 21
- Spring Boot 3.5
- Spring Data JPA
- PostgreSQL
- Scheduled tasks for live data simulation

**Frontend**
- React 19 (Vite)
- Recharts (data visualization)
- Lucide React (icons)
- Axios

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/devices` | Get all network devices |
| GET | `/api/devices/status/{status}` | Filter devices by status |
| GET | `/api/events` | Get recent security events |
| GET | `/api/events/severity/{severity}` | Filter events by severity |
| GET | `/api/security-score` | Get overall security score |
| GET | `/api/stats` | Get dashboard summary stats |

## 🚀 Running Locally

**Backend:**
1. Create a PostgreSQL database named `watchtower`
2. Update `application.properties` with your credentials
3. Run the Spring Boot app (runs on port 8081)

**Frontend:**
1. `cd frontend`
2. `npm install`
3. `npm run dev` (runs on port 5173)

## 👤 Author

Yash Chhabra — [LinkedIn](https://www.linkedin.com/in/yash-chhabra-796829333/) | [GitHub](https://github.com/yashchhabra57)
