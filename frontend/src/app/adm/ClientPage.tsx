"use client";


import {useRedirectIfNotAuthenticated} from "@/lib/hooks/useRedirect";

export default function ClientPage() {
  useRedirectIfNotAuthenticated();
  return (
      <>
    <div className="flex-1 flex justify-center items-center">관리자 홈</div>
  <a href="/point/admin" className="px-4 py-2 bg-blue-500 text-white rounded-lg">
    포인트 관리 페이지로 이동
  </a>
  </>
  );
}
