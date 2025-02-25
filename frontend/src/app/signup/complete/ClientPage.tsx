"use client";

import { Button } from "@/components/ui/button";
import Link from "next/link"; // Button 컴포넌트 임포트

export default function ClientPage() {
    return (
        <div className="flex items-center justify-center h-screen bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-lg max-w-lg w-full text-center">
                <h1 className="text-2xl font-semibold text-gray-800 mb-4">
                    회원가입이 완료되었습니다.
                </h1>
                <p className="text-gray-600 mb-6">
                    이메일 인증 전에는 일부 기능이 제한될 수 있습니다.
                </p>

                <p className="text-sm text-gray-500 mb-6">
                    ⚠ 인증 이메일을 확인하시고, 인증을 완료해 주세요.
                </p>

                <Button className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition duration-200">
                    <Link href="/">
                        메인 페이지로 이동
                    </Link>
                </Button>
            </div>
        </div>
    );
}
