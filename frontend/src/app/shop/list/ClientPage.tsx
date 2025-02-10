"use client";

import type { components } from "@/lib/backend/apiV1/schema";
import ProductList from "@/lib/business/components/product/ProductList";

export default function ClientPage({
                                     page,
                                     pageSize,
                                     itemPage,
                                   }: {
  page: number;
  pageSize: number;
  itemPage: components["schemas"]["PageDtoGetItem"];
}) {

  return (
      <>
        <div className="container mx-auto px-4">
          <h1 className="text-2xl font-bold text-center my-4 mb-10">상품 리스트</h1>

          <ProductList
              page={page}
              pageSize={pageSize}
              itemPage={itemPage}
          />
        </div>
      </>
  );
}