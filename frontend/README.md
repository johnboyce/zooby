# Zooby Frontend

A Next.js 15 frontend application for the Zooby device activation tracking dashboard.

## Overview

The frontend provides:
- Real-time device activation status dashboard
- Device model browsing
- User authentication via OAuth/OIDC
- Responsive design with TailwindCSS

## Tech Stack

- **Framework:** Next.js 15 with App Router
- **UI Library:** React 19
- **Styling:** TailwindCSS 4
- **Authentication:** NextAuth.js 4
- **Build Tool:** Turbopack (dev)

## Prerequisites

- Node.js ≥18
- npm or yarn
- Backend API running (for full functionality)

## Project Structure

```
frontend/
├── app/                        # Next.js App Router
│   ├── api/                    # API routes
│   │   └── auth/               # NextAuth.js routes
│   │       └── [...nextauth]/  # OAuth callback handlers
│   ├── components/             # React components
│   │   ├── ActivationStatus.tsx    # Device activation status card
│   │   ├── DeploymentInfo.tsx      # Deployment metadata display
│   │   ├── ModelsPopup.tsx         # Device models modal
│   │   └── SessionWrapper.tsx      # NextAuth session provider
│   ├── globals.css             # Global styles
│   ├── layout.tsx              # Root layout
│   ├── page.tsx                # Home page (dashboard)
│   └── viewport.ts             # Viewport configuration
├── public/                     # Static assets
│   ├── images/                 # Image assets
│   └── deploy-meta.json        # Deployment metadata
├── middleware.ts               # Next.js middleware
├── next.config.ts              # Next.js configuration
├── tailwind.config.ts          # TailwindCSS configuration
├── tsconfig.json               # TypeScript configuration
└── package.json                # Dependencies and scripts
```

## Getting Started

### Install Dependencies

```bash
npm ci
# or
npm install
```

### Environment Variables

Create a `.env.local` file:

```env
# NextAuth Configuration
NEXTAUTH_URL=http://localhost:3000
NEXTAUTH_SECRET=your-secret-key-here

# OAuth Provider
OAUTH_CLIENT_ID=zooby
OAUTH_CLIENT_SECRET=your-client-secret

# Backend API (if different from default)
NEXT_PUBLIC_API_URL=http://localhost:8080
```

### Development Server

```bash
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

### From Repository Root

```bash
make frontend-dev
```

## Available Scripts

| Script | Description |
|--------|-------------|
| `npm run dev` | Start development server with Turbopack |
| `npm run build` | Build for production |
| `npm run start` | Start production server |
| `npm run lint` | Run ESLint |

## Components

### ActivationStatus

Displays the current device activation status with real-time updates.

```tsx
import ActivationStatus from './components/ActivationStatus';

<ActivationStatus />
```

### ModelsPopup

Modal component for browsing available device models.

```tsx
import ModelsPopup from './components/ModelsPopup';

<ModelsPopup 
  show={showPopup} 
  onHide={() => setShowPopup(false)} 
/>
```

### DeploymentInfo

Shows deployment metadata (git SHA, deployment time).

```tsx
import DeploymentInfo from './components/DeploymentInfo';

<DeploymentInfo />
```

### SessionWrapper

Wraps the application with NextAuth session provider.

```tsx
import SessionWrapper from './components/SessionWrapper';

<SessionWrapper>
  {children}
</SessionWrapper>
```

## Authentication

The application uses NextAuth.js with a custom OAuth provider.

### Configuration

The OAuth provider is configured in `app/api/auth/[...nextauth]/route.ts`.

### Usage

```tsx
import { signIn, signOut, useSession } from 'next-auth/react';

function MyComponent() {
  const { data: session, status } = useSession();

  if (status === 'loading') {
    return <div>Loading...</div>;
  }

  if (session) {
    return (
      <div>
        <p>Welcome, {session.user?.name}</p>
        <button onClick={() => signOut()}>Sign out</button>
      </div>
    );
  }

  return (
    <button onClick={() => signIn('connellboyce')}>
      Sign in
    </button>
  );
}
```

## Styling

The application uses TailwindCSS 4 with a custom dark theme featuring cyan accents.

### Theme Colors

- Primary: Cyan (`cyan-400`, `cyan-500`)
- Background: Slate gradients (`slate-900`, `slate-950`)
- Accent: Blue (`blue-500`)

### Custom Classes

The application uses utility-first CSS with custom gradients and animations:

```tsx
<div className="bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-slate-900 via-cyan-950/20 to-slate-950">
  {/* Content */}
</div>
```

## Building for Production

### Build

```bash
npm run build
```

### Start Production Server

```bash
npm run start
```

### Docker Build

```bash
docker build -t zooby-frontend .
```

## Deployment

### AWS App Runner

The frontend is deployed to AWS App Runner via the CI/CD pipeline:

```bash
# From repository root
make deploy-qa-ui
```

### GitHub Pages (Static Export)

```bash
# From repository root
make deploy-ui
```

## Configuration

### next.config.ts

Key configuration options:

```typescript
const nextConfig: NextConfig = {
  output: 'standalone',  // For Docker deployment
  images: {
    remotePatterns: [
      // Configure allowed image domains
    ],
  },
};
```

### Environment-specific Settings

| Environment | NEXTAUTH_URL | API URL |
|-------------|--------------|---------|
| Development | `http://localhost:3000` | `http://localhost:8080` |
| QA | `https://7qdnizqzpi.us-east-1.awsapprunner.com` | ECS endpoint |
| Production | Production URL | Production endpoint |

## Troubleshooting

### Common Issues

**Authentication not working:**
- Verify `NEXTAUTH_URL` matches your deployment URL
- Ensure `NEXTAUTH_SECRET` is set
- Check OAuth provider configuration

**API calls failing:**
- Verify backend is running
- Check CORS configuration on backend
- Verify API URL is correct

**Build errors:**
- Clear `.next` directory: `rm -rf .next`
- Reinstall dependencies: `rm -rf node_modules && npm ci`

### Debug Mode

Enable debug logging for NextAuth:

```env
NEXTAUTH_DEBUG=true
```

## Learn More

- [Next.js Documentation](https://nextjs.org/docs)
- [NextAuth.js Documentation](https://next-auth.js.org/)
- [TailwindCSS Documentation](https://tailwindcss.com/docs)
- [React Documentation](https://react.dev/)
