# Contributing to Zooby

Thank you for your interest in contributing to Zooby! This document provides guidelines and instructions for contributing.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Making Changes](#making-changes)
- [Testing](#testing)
- [Submitting Changes](#submitting-changes)
- [Style Guidelines](#style-guidelines)

## Code of Conduct

Please be respectful and inclusive in all interactions. We are committed to providing a welcoming and inspiring community for all.

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/zooby.git
   cd zooby
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/johnboyce/zooby.git
   ```

## Development Setup

### Prerequisites

- Java 21 (GraalVM recommended)
- Node.js ≥18
- Docker
- Terraform ≥1.6.0
- AWS CLI v2

### Local Environment

1. **Start LocalStack** for AWS services:
   ```bash
   docker-compose up -d
   ```

2. **Initialize infrastructure**:
   ```bash
   make tf-init TERRAFORM_ENV=local
   make tf-apply TERRAFORM_ENV=local
   ```

3. **Seed test data**:
   ```bash
   make seed-localstack
   ```

4. **Start backend**:
   ```bash
   make dev
   ```

5. **Start frontend** (in a new terminal):
   ```bash
   cd frontend && npm ci && npm run dev
   ```

## Making Changes

### Branch Naming

Use descriptive branch names:
- `feature/add-device-search` - New features
- `fix/activation-timeout` - Bug fixes
- `docs/api-reference` - Documentation
- `refactor/cleanup-services` - Code refactoring

### Create a Feature Branch

```bash
git checkout main
git pull upstream main
git checkout -b feature/your-feature-name
```

## Testing

### Backend Tests

```bash
make test
```

### Frontend Linting

```bash
make lint
```

### Full Check

Run all checks before submitting:

```bash
make check-core
```

### Test Coverage

- Write tests for new features
- Ensure existing tests pass
- Aim for meaningful test coverage

## Submitting Changes

### Commit Messages

Follow conventional commit format:

```
type(scope): description

[optional body]

[optional footer]
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Test additions or modifications
- `chore`: Maintenance tasks

Examples:
```
feat(graphql): add inventory search query
fix(auth): resolve JWT token expiration issue
docs(readme): update setup instructions
```

### Pull Request Process

1. **Update your branch** with main:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Push your changes**:
   ```bash
   git push origin feature/your-feature-name
   ```

3. **Create a Pull Request** on GitHub

4. **Fill out the PR template** with:
   - Description of changes
   - Related issues
   - Testing performed
   - Screenshots (if UI changes)

5. **Request review** from maintainers

6. **Address feedback** and update as needed

## Style Guidelines

### Java (Backend)

- Follow standard Java conventions
- Use meaningful variable and method names
- Add JavaDoc for public APIs
- Use SLF4J for logging
- Prefer constructor injection for dependencies

### TypeScript (Frontend)

- Use TypeScript for all new code
- Define types/interfaces for props and state
- Use functional components with hooks
- Follow React best practices

### Terraform

- Use consistent naming: `resource-type-environment-name`
- Add descriptions to all variables
- Tag all resources with project and environment
- Document modules with README files

### General

- Keep functions/methods focused and small
- Write self-documenting code
- Add comments for complex logic
- Remove dead/commented code

## Questions?

If you have questions, feel free to:
- Open a GitHub Issue
- Check existing documentation
- Review related code and tests

Thank you for contributing! 🦓
