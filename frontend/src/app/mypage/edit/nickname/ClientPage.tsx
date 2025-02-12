"use client";

import { useState } from "react";
import { components } from "@/lib/backend/apiV1/schema";
import { useNickname } from "@/context/NicknameContext";
import { useRouter } from "next/navigation";
import { useRedirectIfNotAuthenticated } from "@/lib/hooks/useRedirect"; // 경로는 적절히 수정
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useToast } from "@/hooks/use-toast";

type NicknameUpdateRequestDto =
  components["schemas"]["NicknameUpdateRequestDto"];

export default function ClientPage() {
  useRedirectIfNotAuthenticated();

  const [formData, setFormData] = useState<NicknameUpdateRequestDto>({
    newNickname: "",
  });
  const { setNickname } = useNickname();
  const router = useRouter();
  const { toast } = useToast();
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await fetch(
        "http://localhost:8080/api/members/nickname",
        {
          method: "PATCH",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(formData),
        }
      );

      if (!response.ok) {
        const errorData = await response.json();
        //console.error("닉네임 수정 실패:", errorData);
        toast({
          title: "닉네임 수정 실패",
          description: errorData,
          variant: "destructive",
        });

        return;
      } else {
        setNickname(formData.newNickname);
      }

      toast({
        title: "닉네임 수정 성공",
        description: "닉네임이 수정되었습니다.",
      });

      router.push("/mypage");
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="container mx-auto p-4 flex flex-col justify-center items-center h-screen mt-[-104px]">
      <div className="mb-10 text-center relative">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          닉네임 수정
        </h2>
      </div>

      <form onSubmit={handleSubmit} className="w-full max-w-lg">
        <div className="mb-4">
          <Label htmlFor="newNickname">새로운 닉네임</Label>
          <Input
            type="text"
            name="newNickname"
            placeholder="변경할 닉네임을 입력해주세요"
            value={formData.newNickname} // value 속성 추가
            onChange={handleChange}
            className="mt-2 p-2 w-full border border-gray-300 rounded-md"
            required
          />
        </div>
        <Button type="submit" className="mt-4 w-full">
          {"닉네임 수정"}
        </Button>
      </form>
    </div>
  );
}
