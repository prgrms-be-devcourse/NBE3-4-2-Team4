"use client"
import { useRedirectIfNotAdmin } from "@/lib/hooks/useRedirect";
import AccumulateForm from "./AccumulateForm";
import DeductForm from "./DeductForm";
import { Button } from "@/components/ui/button";

export default function ClientPage({categories}) {
    useRedirectIfNotAdmin();

    return (
        <div className="container max-w-[600px] mx-auto px-4">
          <div className="mt-20 mb-10 text-center">
            <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
              재화 관리
            </h2>
            <div className="flex flex-left">
                <Button>
                    <a href="point/categories">Admin Categories</a>
                </Button>
            </div>
          </div>
          <div className="flex flex-col gap-3">
            <AccumulateForm categories={categories}/>
            <div className="border-t border-gray-200 my-5 border-dashed"></div>
            <DeductForm categories={categories}/>
          </div>
        </div>
      );
}