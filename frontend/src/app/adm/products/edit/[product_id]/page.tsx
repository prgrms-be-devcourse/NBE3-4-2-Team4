import { cookies } from "next/headers";
import client from "@/lib/backend/client";
import type { components } from "@/lib/backend/apiV1/schema";
import ClientPage from "@/app/adm/products/edit/[product_id]/ClientPage";

type Props = {
    params: Promise<{ product_id?: string }>;
};

export default async function Page({ params }: Props) {
    try {
        const resolvedParams = await params;
        const cookieHeader = await cookies();

        const productId = resolvedParams?.product_id ? Number(resolvedParams.product_id) : null;

        if (!productId) {
            return <p className="text-center text-red-500">잘못된 접근입니다.</p>;
        }

        const response = await client.GET("/api/products/{product_id}", {
            params: {
                path: {
                    product_id: productId
                },
            },
            headers: {
                cookie: cookieHeader.toString(),
            },
        });

        if (response.error || !response.data?.data) {
            return <p className="text-center text-red-500">존재하지 않는 상품입니다.</p>;
        }

        const product: components["schemas"]["GetItem"] = response.data.data;
        return <ClientPage product={product} />;

    } catch (error) {
        console.error("상품 정보를 불러오는 중 오류 발생:", error);
        return <p className="text-center text-red-500">상품 정보를 불러올 수 없습니다.</p>;
    }
}
