"use client";
import createClient from "openapi-fetch";
import { useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Coins, HandCoins, Wallet } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
});

export default function TransferForm({ point, cash }: { point: number, cash : number }) {
  const [username, setUsername] = useState("");
  const [selectedType, setSelectedType] = useState("POINT");
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

    const data = await client.PUT("/api/asset/transfer", {
      body: {
        username: username,
        amount: Number(amount),
        assetType: selectedType
      },
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
            <div className="flex gap-2">
            <Badge
              className="flex items-center gap-2 font-normal"
              variant="outline"
            >
              <Coins size={16} /> 포인트 잔액 :{" "}
              <span className="font-semibold text-base">{point}</span>
            </Badge>

                        <Badge
                          className="flex items-center gap-2 font-normal"
                          variant="outline"
                        >
                          <Wallet size={16} /> 캐시 잔액 :{" "}
                          <span className="font-semibold text-base">{cash}</span>
                        </Badge>
            </div>
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
                          <Select
                                value={selectedType}
                                onValueChange={(value) => setSelectedType(value)}
                              >
                                <SelectTrigger className="md:w-[180px] w-[120px]" id="category">
                                  <SelectValue placeholder="재화 타입" />
                                </SelectTrigger>
                                <SelectContent>

                                  <SelectItem value="POINT">포인트</SelectItem>
                                  <SelectItem value="CASH">캐시</SelectItem>
                                </SelectContent>
                           </Select>
          <Button onClick={handleTransfer}>송금</Button>
        </CardContent>
      </Card>
    </div>
  );
}
