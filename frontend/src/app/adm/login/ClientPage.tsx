"use client";
import { useState } from "react";
import type { components } from "@/lib/backend/apiV1/schema";
import { useId } from "@/context/IdContext";
import { useNickname } from "@/context/NicknameContext";
import { useRedirectIfAuthenticated } from "@/lib/hooks/useRedirect";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { LockKeyhole, UserRound } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import client from "@/lib/backend/client";

type AdminLoginRequestDto = components["schemas"]["AdminLoginRequestDto"];

export default function ClientPage() {
  useRedirectIfAuthenticated();

  const [formData, setFormData] = useState<AdminLoginRequestDto>({
    adminUsername: "",
    password: "",
  });
  const { setNickname } = useNickname();
  const { setId } = useId();
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };
  const { toast } = useToast();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await client.POST("/api/admin/login", {
        headers: {
          "Content-Type": "application/json",
        },
        body: formData,
      });

      if (response.error) {
        //console.error("로그인 실패:", data);
        toast({
          title: "로그인 실패",
          description: response.error.msg,
          variant: "destructive",
        });

        return;
      } else {
        const data = await response.data;

        setId(data.data.id);
        setNickname(data.data.nickname);
      }

      toast({
        title: "로그인 성공",
        description: "환영합니다! " + response.data.data.nickname + "님",
      });

      window.location.href = "/adm";
    } catch (error) {
      //console.error(error);

      toast({
        title: "로그인 실패",
        description: "로그인 처리 중 오류가 발생했습니다.",
        variant: "destructive",
      });

      return;
    }
  };

  return (
    <div className="flex-1 flex flex-col justify-center items-center p-4">
      <div className="mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          관리자 로그인
        </h2>
      </div>
      <form onSubmit={handleSubmit} className="flex flex-col gap-4 w-80">
        <Input
          type="text"
          name="adminUsername"
          placeholder="관리자 아이디"
          value={formData.adminUsername}
          onChange={handleChange}
        />

        <Input
          type="password"
          name="password"
          placeholder="비밀번호"
          value={formData.password}
          onChange={handleChange}
        />

        <Button type="submit" variant="default" className="mt-4">
          로그인
        </Button>
      </form>
    </div>
  );
}
