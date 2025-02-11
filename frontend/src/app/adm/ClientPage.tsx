"use client";


import {useRedirectIfNotAuthenticated} from "@/lib/hooks/useRedirect";

export default function ClientPage() {
  useRedirectIfNotAuthenticated();
  return (
      <div className="flex flex-col justify-center items-center gap-[10px]">
    <div className="flex-1 flex justify-center items-center">관리자 홈</div>
    <ul>
        <li className="flex justify-center items-center">
            <a href="/point/admin" className="underline">
                포인트 관리 페이지로 이동
              </a>
        </li>
    </ul>
  </div>
  );
}
