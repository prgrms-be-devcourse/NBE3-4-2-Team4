"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardHeader,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import client from "@/lib/backend/client";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useToast } from "@/hooks/use-toast";

const saleStates = [
  { value: "ONSALE", label: "판매 중" },
  { value: "SOLDOUT", label: "품절" },
  { value: "RESERVED", label: "예약 중" },
  { value: "COMINGSOON", label: "곧 출시 예정" },
];

export default function ClientPage() {
  const router = useRouter();
  const [formData, setFormData] = useState({
    product_name: "",
    product_price: 0,
    product_description: "",
    product_image: "",
    product_category: "",
    product_sale_state: "ONSALE",
  });
  const [loading, setLoading] = useState(false);
  const { toast } = useToast();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSelectChange = (value: string) => {
    setFormData((prev) => ({ ...prev, product_sale_state: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await client.POST("/api/products", {
        body: {
          product_name: formData.product_name,
          product_price: formData.product_price,
          product_description: formData.product_description,
          product_image_url: formData.product_image,
          product_category: formData.product_category,
          product_sale_state: formData.product_sale_state,
        },
      });

      console.log("상품 등록 응답:", response);

      if (response.error) {
        console.error("상품 등록 실패:", response.error);
        //alert(response.error.msg || "상품 등록 실패");
        toast({
          title: "상품 등록 실패",
          description: response.error.msg || "상품 등록 실패",
          variant: "destructive",
        });
        return;
      }

      //alert("상품이 성공적으로 등록되었습니다!");
      toast({
        title: "상품이 성공적으로 등록되었습니다!",
      });
      router.push("/adm/products/list");
    } catch (error) {
      console.error("상품 등록 중 오류 발생:", error);
      //alert("상품 등록 중 오류가 발생했습니다.");
      toast({
        title: "상품 등록 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container max-w-[900px] mx-auto px-4">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          상품 등록
        </h2>
      </div>
      <form onSubmit={handleSubmit}>
        <Card className="shadow-lg rounded-xl w-full">
          <CardHeader></CardHeader>

          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="product_name">상품명</Label>
              <Input
                id="product_name"
                name="product_name"
                placeholder="상품명을 입력하세요"
                value={formData.product_name}
                onChange={handleChange}
                required
              />
            </div>

            <div className="space-y-2 flex items-center gap-3">
              <Label htmlFor="product_price" className="w-[80px]">
                가격
              </Label>
              <Input
                id="product_price"
                name="product_price"
                type="number"
                placeholder="상품 가격을 입력하세요"
                value={formData.product_price}
                onChange={handleChange}
                required
                className="md:w-[250px] w-full !my-0"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="product_image">이미지 URL</Label>
              <Input
                id="product_image"
                name="product_image"
                placeholder="상품 이미지 URL을 입력하세요"
                value={formData.product_image}
                onChange={handleChange}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="product_description">설명</Label>
              <Textarea
                id="product_description"
                name="product_description"
                placeholder="상품 설명을 입력하세요"
                value={formData.product_description}
                onChange={handleChange}
                rows={10}
                required
              />
            </div>

            <div className="space-y-2 flex items-center gap-3">
              <Label htmlFor="product_category" className="w-[80px]">
                카테고리
              </Label>
              <Input
                id="product_category"
                name="product_category"
                placeholder="상품 카테고리를 입력하세요"
                value={formData.product_category}
                onChange={handleChange}
                className="md:w-[250px] w-full !my-0"
                required
              />
            </div>

            <div className="space-y-2 flex items-center gap-3">
              <Label htmlFor="product_sale_state" className="w-[80px]">
                판매 상태
              </Label>
              <Select
                value={formData.product_sale_state}
                onValueChange={handleSelectChange}
              >
                <SelectTrigger
                  className="md:w-[250px] w-full !my-0"
                  id="product_sale_state"
                >
                  <SelectValue placeholder="판매 상태 선택" />
                </SelectTrigger>
                <SelectContent>
                  {saleStates.map((state) => (
                    <SelectItem key={state.value} value={state.value}>
                      {state.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {/* <select
                id="product_sale_state"
                name="product_sale_state"
                value={formData.product_sale_state}
                onChange={handleChange}
                className="w-full p-2 border rounded-md focus:ring focus:ring-blue-300"
              >
                {saleStates.map((state) => (
                  <option key={state.value} value={state.value}>
                    {state.label}
                  </option>
                ))}
              </select> */}
            </div>
          </CardContent>
          <CardFooter className="flex gap-4 justify-center">
            <Button
              variant="outline"
              type="button"
              onClick={() => router.back()}
              className="w-[130px]"
            >
              취소
            </Button>
            <Button
              type="submit"
              disabled={loading}
              variant="default"
              className="w-[130px]"
            >
              {loading ? "등록 중..." : "등록"}
            </Button>
          </CardFooter>
        </Card>
      </form>
    </div>
  );
}
