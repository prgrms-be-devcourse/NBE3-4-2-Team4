"use client";

import Link from "next/link";

export default function ClientPage() {
  return (
      <div className="flex-1 flex flex-col justify-center items-center space-y-4">
        <div>관리자 홈</div>
        <Link href="/adm/products/list">상품 관리 페이지</Link>
      </div>
  );
}
