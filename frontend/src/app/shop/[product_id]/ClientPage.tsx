"use client";

import { useRouter } from "next/navigation";
import ProductDetail from "@/lib/business/components/product/ProductDetail";
import type { components } from "@/lib/backend/apiV1/schema";

interface DetailPageProps {
    product: components["schemas"]["GetItem"];
}

export default function ClientPage({ product }: DetailPageProps) {
    const router = useRouter();

    const handlePurchase = () => {
        // 포인트 결제 페이지로 이동 (TODO 적절한 경로로 변경 필요)
        router.push(`/shop/list`);
    };

    return <ProductDetail product={product} onPurchase={handlePurchase} />;
}
