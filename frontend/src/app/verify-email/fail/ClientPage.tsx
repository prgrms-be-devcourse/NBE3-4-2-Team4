"use client";

import React from "react";
import { useRouter } from "next/navigation";
import {Button} from "@/components/ui/button";

export default function ClientPage(){
    const router = useRouter();

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md text-center">
                <h1 className="text-3xl font-bold text-green-500 mb-4">이메일 인증 실패</h1>
                <p className="text-lg text-gray-700 mb-6">
                    이메일 인증이 성공적으로 완료되지 못했습니다. <br/> 로그인 후 마이페이지에서 인증 메일을 재전송하세요.
                </p>
                <Button
                    onClick={() => router.push('/login')} // 로그인 페이지로 이동
                >
                    로그인 페이지로 가기
                </Button>
            </div>
        </div>
    );
};
