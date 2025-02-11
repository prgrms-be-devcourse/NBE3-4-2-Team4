"use client";

import Image from "next/image";
import Link from "next/link";
import { components } from "@/lib/backend/apiV1/schema";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Lock } from "lucide-react";
import PaginationType1Responsive from "@/lib/business/components/PaginationType1Responsive";
import imageLoader from "@/utils/imageLoader";

interface ProductListProps {
    page: number;
    pageSize: number;
    itemPage: any;
}

export default function ProductList({ page, pageSize, itemPage }: ProductListProps) {
    const normalizedItemPage: components["schemas"]["PageDtoGetItem"] = {
        currentPageNumber: itemPage.data.current_page_number,
        pageSize: itemPage.data.page_size,
        totalPages: itemPage.data.total_pages,
        totalItems: itemPage.data.total_items,
        hasMore: itemPage.data.has_more,
        items: itemPage.data.items,
    };

    // "ONSALE" 상태인 상품만 필터링
    const filteredItems = normalizedItemPage.items?.filter(
        (item) => item.product_sale_state === "ONSALE"
    ) || [];

    return (
        <>
            {filteredItems.length === 0 ? (
                <div className="flex flex-col min-h-[calc(100dvh-280px)] items-center justify-center py-12 text-muted-foreground">
                    <p>판매 중인 상품이 없습니다.</p>
                </div>
            ) : (
                <ul className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                    {filteredItems.map((item) => (
                        <li key={item.product_id} className="flex flex-col space-y-4">
                            <Link href={`/shop/${item.product_id}`}>
                                <Card className="hover:bg-accent/50 transition-colors rounded-xl shadow-lg">
                                    <CardHeader className="relative">
                                        {item.product_image_url && (
                                            <Image
                                                loader={imageLoader}
                                                src={item.product_image_url}
                                                alt={item.product_name}
                                                width={200}
                                                height={200}
                                                className="object-cover rounded-xl"
                                            />
                                        )}
                                    </CardHeader>
                                    <CardContent className="p-4">
                                        <div className="flex flex-col space-y-2">
                                            {/* product_category 추가 */}
                                            {item.product_category && (
                                                <p className="text-sm font-medium text-muted-foreground">
                                                    {item.product_category}
                                                </p>
                                            )}

                                            <CardTitle className="text-lg font-semibold text-primary truncate">
                                                {item.product_name}
                                            </CardTitle>

                                            {/* product_price 추가 */}
                                            <p className="text-xl font-bold text-primary">
                                                {item.product_price.toLocaleString()} 원
                                            </p>

                                            {/* 비공개 상품에 대한 잠금 아이콘 */}
                                            {!item.product_category && (
                                                <Lock className="w-6 h-6 text-muted" />
                                            )}
                                        </div>
                                    </CardContent>
                                </Card>
                            </Link>
                        </li>
                    ))}
                </ul>
            )}

            <PaginationType1Responsive
                className="my-6"
                baseQueryString={`pageSize=${pageSize}`}
                totalPages={normalizedItemPage.totalPages}
                currentPageNumber={normalizedItemPage.currentPageNumber}
            />
        </>
    );
}
