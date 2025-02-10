"use client";

import type { components } from "@/lib/backend/apiV1/schema";
import PostList from "@/lib/business/components/PostList";

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
          <h1 className="text-2xl font-bold text-center my-4">상품 리스트</h1>

          <PostList
              page={page}
              pageSize={pageSize}
              itemPage={itemPage}
          />
        </div>
      </>
  );
}