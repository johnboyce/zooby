import type { Metadata } from "next";
import "./globals.css";
import { Inter } from 'next/font/google';
import SessionWrapper from './components/SessionWrapper';

const inter = Inter({
  subsets: ['latin'], // Or any other subsets you need
  variable: '--font-inter', // Define a CSS variable
});

export const metadata: Metadata = {
  title: "Zooby",
  description: "Zooby",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={`${inter.variable}`}>
      <body className="bg-slate-900 text-white">
        <SessionWrapper>{children}</SessionWrapper>
      </body>
    </html>
  );
}
