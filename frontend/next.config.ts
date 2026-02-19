import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: "http://175.45.195.25:8080/api/:path*",
      },
    ];
  },
};

export default nextConfig;
