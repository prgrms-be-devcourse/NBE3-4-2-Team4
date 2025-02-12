"use client";
import createClient from "openapi-fetch";
import { useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";
import { paths } from "@/lib/backend/apiV1/schema";
import { Label } from "@radix-ui/react-dropdown-menu";
import { Input } from "@/components/ui/input";
import { useToast } from "@/hooks/use-toast";

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
});

export default function DeductForm() {
  const [username, setUsername] = useState("");
  const [amount, setAmount] = useState(0);

  const router = useRouter();
  const pathname = usePathname();
  const { toast } = useToast();

  const handleDeduct = async (e) => {
    console.log(amount);
    if (!amount || amount < 0) {
      //alert("0보다 큰수를 입력해주세요");
      toast({
        title: "0보다 큰수를 입력해주세요",
        variant: "destructive",
      });
      e.preventDefault();
      return;
    }

    if (username.trim() === "") {
      //alert("유저네임을 입력하세요.");
      toast({
        title: "유저네임을 입력하세요.",
        variant: "destructive",
      });
      e.preventDefault();
      return;
    }

    const data = await client.PUT("/api/admin/points/deduct", {
      body: {
        username: username,
        amount: Number(amount),
      },
      credentials: "include",
    });

    if (!data.response.ok) {
      //alert("실패: " + data.error.msg);
      toast({
        title: "차감 실패",
        description: data.error?.msg,
        variant: "destructive",
      });
      return;
    }

    //alert("성공!!");
    toast({
      title: "차감 성공!!",
    });
    setUsername("");
    setAmount(0);
    router.replace(pathname);
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">포인트 차감</CardTitle>
      </CardHeader>

      <CardContent className="flex gap-2">
        <Input
          type="text"
          placeholder="포인트 차감 대상"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <Input
          type="number"
          placeholder="포인트 금액을 입력하세요"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />
        <Button onClick={handleDeduct}>차감</Button>
      </CardContent>
    </Card>
  );
}
