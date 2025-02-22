"use client";

import AccumulateForm from "./AccumulateForm";
import DeductForm from "./DeductForm";
import { useRedirectIfNotAdmin } from "@/lib/hooks/useRedirect";

export default function Page() {
  useRedirectIfNotAdmin();

  return (
    <div className="container max-w-[600px] mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          포인트 관리
        </h2>
      </div>
      <div className="flex flex-col gap-3">
        <AccumulateForm />
        <div className="border-t border-gray-200 my-5 border-dashed"></div>
        <DeductForm />
      </div>
    </div>
  );
}
