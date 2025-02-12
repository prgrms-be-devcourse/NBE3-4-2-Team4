"use client";

import { useState } from "react";
import Link from "next/link";
import { useRedirectIfNotAuthenticated } from "@/lib/hooks/useRedirect"; // 경로는 적절히 수정
import { useId } from "@/context/IdContext";
import { useNickname } from "@/context/NicknameContext";
import { useRole } from "@/context/RoleContext";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useToast } from "@/hooks/use-toast";
import { Badge } from "@/components/ui/badge";

export default function WithdrawalPage() {
  useRedirectIfNotAuthenticated();

  const [confirmationText, setConfirmationText] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const { setNickname } = useNickname();
  const { setId } = useId();
  const { setRole } = useRole();
  const { toast } = useToast();

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
        //alert("성공적으로 탈퇴 되었습니다.");
        toast({
          title: "탈퇴 성공",
          description: "성공적으로 탈퇴 되었습니다.",
        });
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
    <div className="container mx-auto p-4 flex flex-col justify-center items-center h-screen mt-[-104px]">
      <div className="mb-10 text-center relative">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          회원 탈퇴
        </h2>
      </div>

      {/* 경고문 */}
      <div className="mb-4 border border-destructive p-4 rounded-md text-destructive w-[400px] max-w-full">
        <p>
          <strong>탈퇴 시 주의사항</strong>
        </p>
        <ul>
          <li>회원 탈퇴 후에는 모든 데이터가 삭제됩니다.</li>
          <li>보유 중인 포인트는 환급되지 않습니다.</li>
          <li>탈퇴 후에는 복구가 불가능합니다.</li>
        </ul>
      </div>

      {/* 확인 입력란 */}
      <div className="mb-4 w-[400px] max-w-full">
        <Label htmlFor="confirmation" className="flex items-center gap-2 mb-2">
          <Badge variant="secondary">확인했습니다.</Badge> 를 입력해 주세요.
        </Label>
        <Input
          type="text"
          id="confirmation"
          value={confirmationText}
          onChange={handleInputChange}
        />
      </div>

      {/* 오류 메시지 */}
      {error && <div className="text-red-500 text-sm mb-4">{error}</div>}

      {/* 탈퇴 버튼 */}
      <div className="mt-4 flex items-center gap-2 w-[400px] max-w-full">
        <div className="grow">
          <Button variant="outline" className="w-full" asChild>
            <Link href="/mypage">마이페이지로 돌아가기</Link>
          </Button>
        </div>
        <div className="grow">
          <Button
            onClick={handleWithdraw}
            variant="destructive"
            disabled={loading}
            className={`w-full ${
              loading ? "opacity-50 cursor-not-allowed" : ""
            }`}
          >
            {loading ? "탈퇴 중..." : "탈퇴하기"}
          </Button>
        </div>
      </div>
    </div>
  );
}
