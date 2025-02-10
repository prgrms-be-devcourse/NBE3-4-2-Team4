"use client";

import {useRedirectIfNotAuthenticated} from "@/lib/hooks/useRedirect";

export default function ClientPage() {
  useRedirectIfNotAuthenticated();
  return (
    <div className="flex-1 flex justify-center items-center">관리자 홈</div>
  );
}
