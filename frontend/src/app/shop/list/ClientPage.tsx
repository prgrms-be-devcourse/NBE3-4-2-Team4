"use client";

import type { components } from "@/lib/backend/apiV1/schema";
import ProductList from "@/app/shop/list/ProductList";

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
        <div className="mt-20 mb-10 text-center">
          <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
            포인트 쇼핑
          </h2>
          <p className="text-md text-gray-400 mt-3">
            적립한 포인트로 특별한 혜택을 누리세요
          </p>
        </div>

        <ProductList page={page} pageSize={pageSize} itemPage={itemPage} />
      </div>
    </>
  );
}
