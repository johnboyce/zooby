# Zooby

[![Backend](https://github.com/johnboyce/zooby/actions/workflows/backend.yml/badge.svg)](https://github.com/johnboyce/zooby/actions/workflows/backend.yml)
[![CI](https://github.com/johnboyce/zooby/actions/workflows/ci.yml/badge.svg)](https://github.com/johnboyce/zooby/actions/workflows/ci.yml)
[![Frontend](https://github.com/johnboyce/zooby/actions/workflows/frontend.yml/badge.svg)](https://github.com/johnboyce/zooby/actions/workflows/frontend.yml)
[![Deploy UI](https://github.com/johnboyce/zooby/actions/workflows/deploy-ui.yml/badge.svg)](https://github.com/johnboyce/zooby/actions/workflows/deploy-ui.yml)
[![Infra](https://github.com/johnboyce/zooby/actions/workflows/infra.yml/badge.svg)](https://github.com/johnboyce/zooby/actions/workflows/infra.yml)
[![Terraform](https://github.com/johnboyce/zooby/actions/workflows/terraform.yml/badge.svg)](https://github.com/johnboyce/zooby/actions/workflows/terraform.yml)

An event-driven AWS application with a Quarkus GraphQL backend, LocalStack, Terraform infrastructure, and a React + Tailwind frontend.

## 🚀 Live Demo

👉 [Visit the App](https://johnboyce.github.io/zooby/)

# 🦾 Zooby

**Zooby** is a modern, event-driven device activation platform for tracking and managing network-connected devices using AWS-native services, GraphQL, and a sleek frontend UI. Development is LocalStack-powered and CI/CD-ready with full GitHub integration.

---

## ✅ Project Overview

Zooby enables:
- Device eligibility verification and activation
- Real-time status tracking of activation steps
- User-facing UI to track device status
- Event-driven backend with GraphQL + DynamoDB
- AWS Lambda integration (coming soon)

---

## 📋 Project Checklist

### ✅ Completed

- [x] GraphQL API with Quarkus
- [x] GraphQL endpoints: `eligibility`, `activate`, `activationStatus`
- [x] DynamoDB integration (LocalStack)
- [x] React + Vite + TailwindCSS frontend
- [x] Frontend deployed via GitHub Pages
- [x] GitHub Actions CI for backend, frontend, and Terraform
- [x] LocalStack setup (Docker Compose)
- [x] Terraform infra with validation CI
- [x] Project branding and assets
- [x] Makefile for unified dev workflow

### 🛠️ In Progress

- [ ] Frontend connection to GraphQL
- [ ] JWT authentication in frontend/backend
- [ ] Local end-to-end simulation (device → activation → SQS)
- [ ] AWS Lambda scaffolding
- [ ] Terraform-managed AWS deployment (beyond LocalStack)

### 🧭 To Do Next

- [ ] Implement Lambda for device online → trigger activation
- [ ] Implement Lambda for processing SQS → update status
- [ ] Add authentication via JWT
- [ ] Add WebSocket or polling for real-time frontend updates
- [ ] Polish Terraform modules for full AWS deploy

---

## 🧰 Tech Stack

| Layer        | Technology                         |
|--------------|-------------------------------------|
| Backend      | Quarkus, GraphQL, Java 17          |
| Frontend     | React, Vite, TailwindCSS           |
| Auth         | JWT (planned)                      |
| Infrastructure | Terraform                        |
| AWS Services | DynamoDB, SQS, Lambda, LocalStack  |
| CI/CD        | GitHub Actions                     |

---

## 🧪 Local Development

1. Start LocalStack & DynamoDB Admin:
    ```bash
    make up
    ```

2. Initialize AWS resources:
    ```bash
    ./init-localstack.sh
    ```

3. Build and run backend:
    ```bash
    make build
    ```

4. Develop UI:
    ```bash
    cd frontend && npm run dev
    ```

---

## 🔗 Links

- Frontend UI: [GitHub Pages Deployment](https://your-username.github.io/zooby)
- GitHub Repo: [https://github.com/your-username/zooby](https://github.com/your-username/zooby)

---

> Made with ☕, ☁️, and a little bit of 🔌 by you.
