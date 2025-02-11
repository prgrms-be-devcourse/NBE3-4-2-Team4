"use client"

import AccumulateForm from "./AccumulateForm";
import DeductForm from "./DeductForm";
import {useRedirectIfNotAuthenticated} from "@/lib/hooks/useRedirect";

export default function Page() {

    //useRedirectIfNotAuthenticated();

    return <div className="p-[20px] flex flex-col gap-3">
        <h1>관리자 포인트 페이지</h1>
        <AccumulateForm/>
        <DeductForm/>
    </div>
}