"use client";
import createClient from "openapi-fetch";
import { useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { paths } from "@/lib/backend/apiV1/schema";
import { Input } from "@/components/ui/input";
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

export default function AccumulateForm({categories}) {
  const [username, setUsername] = useState("");
  const [amount, setAmount] = useState(0);
  const [selectedType, setSelectedType] = useState("POINT");
  const [selectedCategory, setSelectedCategory] = useState(categories.length > 0 ? categories[0].id : 0);

  const router = useRouter();
  const pathname = usePathname();
  const { toast } = useToast();

  const handleAccumulate = async (e) => {

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

    const data = await client.PUT("/api/admin/asset/accumulate", {
      body: {
        username: username,
        amount: Number(amount),
        assetType: selectedType,
        adminAssetCategoryId: Number(selectedCategory)
      },
      credentials: "include",
    });

    if (!data.response.ok) {
      //alert("실패: " + data.error.msg);
      toast({
        title: "적립 실패",
        description: data.error?.msg,
        variant: "destructive",
      });
      return;
    }

    //alert("성공!!");
    toast({
      title: "적립 성공!!",
    });
    setUsername("");
    setAmount(0);
    router.replace(pathname);
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">적립</CardTitle>
      </CardHeader>

      <CardContent className="flex gap-2">
        <Input
          type="text"
          placeholder="적립 대상"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <Input
          type="number"
          placeholder="금액을 입력하세요"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
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

<Select

  value={selectedCategory}
  onValueChange={(val) => setSelectedCategory(val)} // 변경
>
  <SelectTrigger className="md:w-[180px] w-[120px]">
    <SelectValue placeholder="카테고리 선택" />
  </SelectTrigger>
  <SelectContent>
    {categories.map((category) => (
      <SelectItem key={category.id} value={category.id}>
        {category.name}
      </SelectItem>
    ))}
  </SelectContent>
</Select>
        <Button onClick={handleAccumulate}>적립</Button>
      </CardContent>
    </Card>
  );
}
