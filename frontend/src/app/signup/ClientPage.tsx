"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import {Button} from "@/components/ui/button";

export default function ClientPage() {
    const [nickname, setNickname] = useState("");
    const [isAvailable, setIsAvailable] = useState<null | boolean>(null);
    const [error, setError] = useState("");
    const [signUpMessage, setSignUpMessage] = useState("");
    const router = useRouter();

    const [emailName, setEmailName] = useState(""); // 이메일 전체 입력 상태
    const [domain, setDomain] = useState(""); // 도메인 선택 상태
    const [customDomain, setCustomDomain] = useState(""); // 직접 입력 도메인 상태

    // 이메일 앞부분 (username)과 도메인 부분을 나누기 위한 처리
    const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setEmailName(e.target.value);
    };

    const handleDomainChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const selectedDomain = e.target.value;
        setDomain(selectedDomain);
        if (selectedDomain !== "(직접 입력)") {
            setCustomDomain(""); // 도메인 선택 시 직접 입력 값 초기화
        }
    };

    const handleCustomDomainChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCustomDomain(e.target.value);
    };

    const emailDomain = domain === "(직접 입력)" ? customDomain : domain;

    useEffect(() => {
        const checkTempToken = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/auth/temp-token", {
                    method: "GET",
                    credentials: "include",
                });

                if (response.ok) {
                    const result = await response.json();
                    if (result.data == false){
                        router.push("/");
                    }

                } else {
                    console.error("서버 오류:", response.body);
                }
            } catch (error) {
                console.error("서버 오류:", error);
                router.push("/");
            }
        };

        checkTempToken();
    }, []);

    // 닉네임 중복 확인
    const checkNicknameAvailability = async () => {
        if (!nickname.trim()) {
            setError("닉네임을 입력하세요.");
            return;
        }
        setError("");
        setIsAvailable(null); // 중복 확인 상태 초기화

        try {
            const response = await fetch(`http://localhost:8080/api/members?nickname=${nickname}`);
            if (response.ok) {
                const result = await response.json(); // ✅ RsData 전체를 받아옴
                setIsAvailable(result.data);
            } else {
                setError("닉네임 확인 중 오류 발생");
            }
        } catch (error) {
            setError("서버와의 연결이 원활하지 않습니다.");
        }
    };

    // 회원가입 요청
    const handleSignUp = async () => {
        if (!emailName.trim() || !domain.trim() || !nickname.trim()) {
            setSignUpMessage("이메일과 닉네임을 입력하세요.");
            return;
        }

        const email = `${emailName}@${emailDomain}`;

        try {
            const response = await fetch("http://localhost:8080/api/members", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials : "include",
                body: JSON.stringify({ email, nickname }),
            });

            if (response.ok) {
                router.push("/signup/complete");
            } else {
                setSignUpMessage("회원가입 실패. 다시 시도해주세요.");
                const status = response.status;
                alert(status);
            }
        } catch (error) {
            setSignUpMessage("서버 오류 발생.");
        }
    };

    return (
        <div className="flex flex-col items-center justify-center h-screen space-y-6">
            <h2 className="text-2xl font-bold">회원가입</h2>

            {/* 이메일 입력 */}
            <div className="flex flex-col w-full max-w-md space-y-2">
                <label className="mb-1 font-medium">이메일</label>
                <div className="flex items-center gap-2">
                    {/* 이메일 앞부분 (username) */}
                    <input
                        type="text"
                        value={emailName}
                        onChange={handleEmailChange}
                        className="border p-2 rounded-md w-3/4"
                        placeholder="이메일 입력"
                    />

                    {/* @ 기호 */}
                    <span className="text-gray-600">@</span>

                    {/* 도메인 입력 칸 */}
                    <input
                        type="text"
                        value={emailDomain}
                        onChange={handleCustomDomainChange}
                        className="border p-2 rounded-md w-2/4"
                        placeholder="직접 입력"
                        readOnly={domain !== "(직접 입력)"}
                    />

                    {/* 도메인 선택 */}
                    <select
                        value={domain}
                        onChange={handleDomainChange}
                        className="border p-2 rounded-md w-2/4"
                    >
                        <option value="(직접 입력)">(직접 입력)</option>
                        <option value="gmail.com">gmail.com</option>
                        <option value="naver.com">naver.com</option>
                        <option value="daum.net">daum.net</option>
                    </select>
                </div>
            </div>

            {/* 닉네임 입력 + 중복 확인 버튼 */}
            <div className="flex flex-col w-full max-w-md space-y-2">
                <label className="mb-1 font-medium">닉네임</label>
                <div className="flex items-center gap-2">
                    <input
                        type="text"
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
                        className="border p-2 rounded-md w-full"
                        placeholder="닉네임 입력"
                    />
                    <Button
                        onClick={checkNicknameAvailability}
                        className="bg-blue-500 text-white px-5 py-2 rounded-md whitespace-nowrap"
                    >
                        중복 확인
                    </Button>
                </div>

                {/* 중복 확인 결과 메시지 */}
                {isAvailable === null && <p className="text-gray-600 mt-1">닉네임 중복 확인을 진행하세요.</p>}
                {isAvailable === true && <p className="text-green-600 mt-1">사용 가능한 닉네임입니다!</p>}
                {isAvailable === false && <p className="text-red-600 mt-1">이미 사용 중인 닉네임입니다.</p>}
                {error && <p className="text-red-600 mt-1">{error}</p>}
            </div>

            {/* 가입하기 버튼 */}
            <button
                onClick={handleSignUp}
                disabled={isAvailable !== true} // 닉네임 중복 확인이 끝나지 않았거나 중복이면 비활성화
                className={`px-6 py-2 rounded-md text-white ${isAvailable === true ? "bg-green-500" : "bg-gray-400 cursor-not-allowed"}`}
            >
                가입하기
            </button>

            {/* 가입 결과 메시지 */}
            {signUpMessage && <p className="mt-2">{signUpMessage}</p>}
        </div>
    );
}
