"use client";

import Image from "next/image";
import Link from "next/link";
import { components } from "@/lib/backend/apiV1/schema";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import PaginationType1Responsive from "@/lib/business/components/PaginationType1Responsive";
import imageLoader from "@/utils/imageLoader";
import { Badge } from "@/components/ui/badge";
import {useSearchParams} from "next/navigation";

interface ProductListProps {
  itemPage: any;
}

export default function ProductList({itemPage}: ProductListProps) {

  const searchParams = useSearchParams();

  // 조회 파라미터에서 page 파라미터를 삭제하는 함수
  const filteredSearchParams = () => {
    const queryParams = new URLSearchParams(searchParams.toString());
    queryParams.delete("page");
    return queryParams.toString();
  };

  const normalizedItemPage: components["schemas"]["PageDtoGetItem"] = {
    currentPageNumber: itemPage.data.current_page_number,
    pageSize: itemPage.data.page_size,
    totalPages: itemPage.data.total_pages,
    totalItems: itemPage.data.total_items,
    hasMore: itemPage.data.has_more,
    items: itemPage.data.items,
  };

  const filteredItems =
    normalizedItemPage.items || [];

  return (
    <>
      {filteredItems.length === 0 ? (
        <div className="flex flex-col min-h-[calc(100dvh-280px)] items-center justify-center py-12 text-muted-foreground">
          <p>판매 중인 상품이 없습니다.</p>
        </div>
      ) : (
        <ul className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {filteredItems.map((item) => (
            <li key={item.product_id}>
              <Link href={`/shop/${item.product_id}`}>
                <Card className="hover:shadow-[0_0_10px_0_rgba(0,0,0,0.2)] hover:dark:shadow-[0_0_10px_0_rgba(255,255,255,0.3)] transition-colors">
                  {item.product_image_url && (
                    <Image
                      loader={imageLoader}
                      src={item.product_image_url}
                      alt={item.product_name}
                      width={200}
                      height={200}
                      className="object-cover rounded-t-xl w-full aspect-square"
                    />
                  )}
                  <CardHeader className="relative">
                    <div>
                      {/* product_category 추가 */}
                      {item.product_category && (
                        <Badge variant="default">{item.product_category}</Badge>
                      )}

                      <CardTitle className="my-2 text-md font-semibold overflow-hidden text-ellipsis whitespace-nowrap">
                        {item.product_name}
                      </CardTitle>

                      {/* product_price 추가 */}
                      <p className="text-2xl font-bold">
                        {item.product_price.toLocaleString()} 원
                      </p>

                    </div>
                  </CardHeader>
                </Card>
              </Link>
            </li>
          ))}
        </ul>
      )}
      <PaginationType1Responsive
        className="my-6"
        baseQueryString={filteredSearchParams()}
        totalPages={normalizedItemPage.totalPages}
        currentPageNumber={normalizedItemPage.currentPageNumber}
      />
    </>
  );
}
