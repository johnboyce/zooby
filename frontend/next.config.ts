import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  distDir: 'out',
  basePath: process.env.NODE_ENV === 'production' ? '/zooby' : '',
  assetPrefix: process.env.NODE_ENV === 'production' ? '/zooby' : '',
  images: {
    unoptimized: true,
  },
};

export default nextConfig;
