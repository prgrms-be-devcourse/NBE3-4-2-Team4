"use client";

import { useRouter } from "next/navigation";

export default function ClientPage() {
    const router = useRouter();

    return (
        <div className="flex flex-col items-center justify-center h-screen">
            <h1 className="text-gray-700 text-2xl">
                회원가입이 완료되었습니다. <br /> 이메일 인증 전에는 일부 기능이 제한될 수 있습니다.
            </h1>
        </div>
    );
}
