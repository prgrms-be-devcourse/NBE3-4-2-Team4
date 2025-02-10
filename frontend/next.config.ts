import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
    // TODO 임시 이미지 추가 (삭제 필요)
    images: {
        remotePatterns: [
            {
                protocol: 'https',
                hostname: 'images.unsplash.com',
            },
            {
                protocol: 'https',
                hostname: 'images.pexels.com',
            },
            {
                protocol: 'https',
                hostname: 'placeimg.com',
            },
            {
                protocol: 'https',
                hostname: 'picsum.photos',
            },
        ],
    },

};

export default nextConfig;
