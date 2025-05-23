# 🦓 Zooby

**Zooby** is an event-driven AWS-native application that tracks activation events using a modern stack:

- **Backend:** Quarkus (GraphQL API) with native builds via GraalVM
- **Frontend:** React + Vite + TailwindCSS
- **Infra:** Terraform (with LocalStack for local dev)
- **CI/CD:** GitHub Actions
- **Deploy:** GitHub Pages + AWS (DynamoDB, Lambda, SQS, SNS)

---

## 🚀 Live Demo

🌐 [View the UI on GitHub Pages](https://johnboyce.github.io/zooby)

---

## 📦 Architecture

```
React UI (GitHub Pages)
         ↓
GraphQL API (Quarkus on Fargate or native local)
         ↓
SQS/SNS → Lambda → DynamoDB
```

---

## 🧰 Tech Stack

| Layer        | Tech                            |
|--------------|----------------------------------|
| Frontend     | React, Vite, TailwindCSS         |
| Backend      | Quarkus (GraphQL, JWT, MDC, native) |
| Database     | AWS DynamoDB                     |
| Messaging    | AWS SNS + SQS                    |
| Dev Infra    | LocalStack, Docker               |
| Infra-as-Code| Terraform                        |
| CI/CD        | GitHub Actions                   |

---

## 🛠️ Local Development

### Prerequisites

- Java 21 (GraalVM)
- Node.js ≥18
- Docker (for LocalStack)
- Terraform ≥1.5
- `make`, `git`, `jq`, `curl`

### Setup

```bash
git clone https://github.com/johnboyce/zooby.git
cd zooby
make setup  # optional: setup scripts, install deps, etc.
```

### Start Dev Environment

```bash
make dev              # Starts Quarkus in dev mode
make frontend-dev     # Runs React frontend with Vite
make localstack-up    # Spins up LocalStack
make infra-apply      # Applies Terraform locally
```

---

## 🧪 Testing

```bash
make test
```

---

## 🚢 Deployment

```bash
make native          # Builds native image
make native-run      # Runs native binary
make deploy-ui       # Deploys frontend to GitHub Pages
make infra-deploy    # Applies infra to AWS
```

---

## 📂 Project Structure

```
zooby/
├── backend/           # Quarkus GraphQL backend
├── frontend/          # React frontend (Vite)
├── infra/             # Terraform definitions
├── .github/workflows/ # CI/CD workflows
└── Makefile           # Automation scripts
```

---

## 📸 Screenshots

_Add screenshots of the UI and logs here (optional)_

---

## ✅ GitHub Actions

![Backend CI](https://github.com/johnboyce/zooby/actions/workflows/backend.yml/badge.svg)
![Frontend CI](https://github.com/johnboyce/zooby/actions/workflows/frontend.yml/badge.svg)
![Deploy UI](https://github.com/johnboyce/zooby/actions/workflows/deploy-ui.yml/badge.svg)

---

## 📃 License

MIT © [johnboyce](https://github.com/johnboyce)
