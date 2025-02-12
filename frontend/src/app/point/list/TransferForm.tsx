"use client";
import createClient from "openapi-fetch";
import { useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Coins, HandCoins } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
});

export default function TransferForm({ point }: { point: number }) {
  const [username, setUsername] = useState("");
  const [amount, setAmount] = useState(0);

  const router = useRouter();
  const pathname = usePathname();

  const { toast } = useToast();

  const handleTransfer = async (e) => {
    console.log(amount);
    if (!amount || amount < 0) {
      //alert("0보다 큰수를 입력해주세요");
      toast({
        title: "송금 실패",
        description: "0보다 큰수를 입력해주세요",
        variant: "destructive",
      });
      e.preventDefault();
      return;
    }

    if (username.trim() === "") {
      //alert("유저네임을 입력하세요.");
      toast({
        title: "송금 실패",
        description: "유저네임을 입력하세요.",
        variant: "destructive",
      });
      e.preventDefault();
      return;
    }

    const data = await client.PUT("/api/points/transfer", {
      body: {
        username: username,
        amount: Number(amount),
      },
      //                 headers: {
      //                   cookie: cookies,
      //                 },
      credentials: "include",
    });

    if (!data.response.ok) {
      console.log(data.response);
      //alert("송금실패: " + data.error.msg);
      toast({
        title: "송금 실패",
        description: data.error.msg,
        variant: "destructive",
      });
      return;
    }

    //alert("송금 성공!!");
    toast({
      title: "송금 성공!!",
    });
    setUsername("");
    setAmount(0);
    router.replace(pathname);
  };

  return (
    <div>
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center justify-between">
            <span className="flex items-center gap-2">
              <HandCoins size={17} />
              포인트 송금
            </span>
            <Badge
              className="flex items-center gap-2 font-normal"
              variant="outline"
            >
              <Coins size={16} /> 포인트 잔액 :{" "}
              <span className="font-semibold text-base">{point}</span>
            </Badge>
          </CardTitle>
        </CardHeader>
        <CardContent className="flex sm:gap-5 gap-2 sm:flex-row flex-col">
          <Input
            type="text"
            placeholder="포인트 송금할 대상"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="max-w-[250px]"
          />
          <Input
            type="number"
            placeholder="포인트 금액을 입력하세요"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            className="max-w-[250px]"
          />
          <Button onClick={handleTransfer}>송금</Button>
        </CardContent>
      </Card>
    </div>
  );
}
