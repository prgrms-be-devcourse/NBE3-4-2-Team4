import { cookies } from "next/headers";
import client from "@/lib/backend/client";
import type { components } from "@/lib/backend/apiV1/schema";
import ClientPage from "@/app/shop/[product_id]/ClientPage";

export default async function Page({
                                                    params,
                                                }: {
    params: { product_id: string };
}) {
    const cookieStore = await cookies();
    const resolvedParams = await params;
    const productId = Number(resolvedParams.product_id);

    const response = await client.GET("/api/products/{product_id}", {
        params: {
            path: { product_id: productId },
        },
        headers: {
            cookie: cookieStore.toString(),
        },
    });

    const product: components["schemas"]["GetItem"] = response.data!.data;

    return <ClientPage product={product} />;
}
