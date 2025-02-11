"use client"

import { useState } from "react";
import Link from "next/link";
import { useRedirectIfNotAuthenticated } from "@/lib/hooks/useRedirect";  // 경로는 적절히 수정
import { useId } from "@/context/IdContext";
import { useNickname } from "@/context/NicknameContext";
import { useRole } from "@/context/RoleContext";

export default function WithdrawalPage() {
    useRedirectIfNotAuthenticated();

    const [confirmationText, setConfirmationText] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const { setNickname } = useNickname();
    const { setId } = useId();
    const { setRole } = useRole();

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setConfirmationText(e.target.value);
    };

    const handleWithdraw = async () => {
        if (confirmationText !== "확인했습니다.") {
            setError("탈퇴를 진행하려면 '확인했습니다.'를 입력해야 합니다.");
            return;
        }

        setLoading(true);
        try {
            const response = await fetch("http://localhost:8080/api/members", {
                method: "DELETE",
                credentials: "include", // 쿠키를 포함한 요청
            });

            if (response.status === 204) {
                alert("성공적으로 탈퇴하였습니다.");
                setRole(null);
                setNickname(null);
                setId(null);

                window.location.href = "/";
            } else {
                setError("탈퇴 실패. 서버에 문제가 발생했습니다.");
            }
        } catch (err) {
            setError("네트워크 오류가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container mx-auto p-4 flex flex-col justify-center items-center h-screen">
            <h1 className="text-xl font-bold mb-4">회원 탈퇴</h1>

            {/* 경고문 */}
            <div className="mb-4 text-red-600">
                <p><strong>경고:</strong></p>
                <ul>
                    <li>회원 탈퇴 후에는 모든 데이터가 삭제됩니다.</li>
                    <li>보유 중인 포인트는 환급되지 않습니다.</li>
                    <li>탈퇴 후에는 복구가 불가능합니다.</li>
                </ul>
            </div>

            {/* 확인 입력란 */}
            <div className="mb-4">
                <label htmlFor="confirmation" className="block text-sm font-medium text-gray-700">
                    '확인했습니다.'를 입력해 주세요.
                </label>
                <input
                    type="text"
                    id="confirmation"
                    value={confirmationText}
                    onChange={handleInputChange}
                    className="mt-2 p-2 w-full border border-gray-300 rounded-md"
                />
            </div>

            {/* 오류 메시지 */}
            {error && <div className="text-red-500 text-sm mb-4">{error}</div>}

            {/* 탈퇴 버튼 */}
            <button
                onClick={handleWithdraw}
                disabled={loading}
                className={`px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 ${loading ? "opacity-50 cursor-not-allowed" : ""}`}
            >
                {loading ? "탈퇴 중..." : "탈퇴하기"}
            </button>

            <div className="mt-4 text-center">
                <Link href="/mypage">
                    <button className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
                        마이페이지로 돌아가기
                    </button>
                </Link>
            </div>
        </div>
    );
}
