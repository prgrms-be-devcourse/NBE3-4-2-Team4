const nextConfig = {
    images: {
        remotePatterns: [
            {
                protocol: "https",
                hostname: "**", // 모든 호스트 허용
            },
        ],
        loader: "custom", // 로더를 직접 지정
        unoptimized: true, // Next.js의 이미지 최적화 기능 비활성화
    },
};

module.exports = nextConfig;
