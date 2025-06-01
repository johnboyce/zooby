---
name: Deployment Bug
about: Issues related to deployment configuration
title: "[Deploy] "
projects: ["Zooby Project"]
labels: bug, deployment
assignees: ''
---

**Current Configuration**
Affected files:
- `frontend/package.json`
- `.github/workflows/frontend.yml`
- `Makefile`
- `next.config.ts`

**Issue Description**
Current deployment configuration needs updates for:
- [ ] Package versions are incorrect/non-existent
- [ ] GitHub Actions workflow needs optimization
- [ ] Makefile deployment target needs improvement

**Current State**
```yaml
# Problematic package versions:
next: 15.3.2 (non-existent)
react: ^19.0.0 (non-existent)
