"use client";

import { useSearchParams, useRouter } from "next/navigation";

export default function ClientPage() {
    const searchParams = useSearchParams();
    const result = searchParams.get("result"); // 쿼리 파라미터 가져오기
    const router = useRouter();

    return (
        <div className="flex flex-col items-center justify-center h-screen">
            {result === "true" ? (
                <>
                    <h1 className="text-green-500 text-2xl mb-4">이메일 인증 성공!</h1>
                    <button
                        onClick={() => router.push("/login")}
                        className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition"
                    >
                        로그인하러 가기
                    </button>
                </>
            ) : (
                <h1 className="text-red-500 text-2xl">이메일 인증 실패!</h1>
            )}
        </div>
    );
}