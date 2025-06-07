import { withAuth } from "next-auth/middleware";

export default withAuth({
  pages: {
    signIn: '/api/auth/signin/connellboyce',
  },
  callbacks: {
    authorized: ({ token }) => {
      return !!token;
    },
  },
});

// Only apply middleware to protected pages
export const config = {
  matcher: [
    '/dashboard/:path*',
    '/devices/:path*',
    '/settings/:path*',
  ],
};
