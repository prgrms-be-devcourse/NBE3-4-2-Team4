"use client";

import React from "react";
import { useRouter } from "next/navigation";

export default function ClientPage(){
    const router = useRouter();

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md text-center">
                <p className="text-lg text-gray-700 mb-6">
                    이미 인증된 이메일입니다.
                </p>
                <button
                    onClick={() => router.push('/login')} // 로그인 페이지로 이동
                    className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded w-full"
                >
                    로그인 페이지로 가기
                </button>
            </div>
        </div>
    );
};
