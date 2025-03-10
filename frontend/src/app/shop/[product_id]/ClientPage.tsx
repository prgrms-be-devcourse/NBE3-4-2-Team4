"use client";

import Image from "next/image";
import { useRouter } from "next/navigation";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import imageLoader from "@/utils/imageLoader";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";

export default function ClientPage({ product, cookieString }) {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [paymentType, setPaymentType] = useState<"cash" | "point">("cash");

  const { toast } = useToast();

  const cashPrice = product.product_price;
  const pointPrice = (product.product_price * 5);

  // 결제 방식에 따른 표시 가격 선택
  const selectedPrice = paymentType === "cash"
      ? `${cashPrice.toLocaleString()} C`
      : `${pointPrice.toLocaleString()} P`

  // 구매 버튼 클릭 시 API 요청
  const onPurchase = async () => {
    if (loading) return;
    setLoading(true);

    try {
      // 유저 정보 조회
      const userResponse = await client.GET("/api/members/details", {
        headers: {
          cookie: cookieString,
        },
      });

      if (!userResponse.response.ok) {
        toast({
          title: "유저 정보를 가져오는데 실패했습니다.",
          variant: "destructive",
        });
        return;
      }

      const userData = userResponse?.data?.data;

      // 401 응답이면 로그인 페이지로 리디렉션
      if (userResponse.response.status === 401) {
        toast({
          title: "로그인이 필요합니다.",
          variant: "destructive",
        });
        router.push("/login");
        return;
      }

      const remainPrice = paymentType === "cash" ? userData.cash.amount : userData.point.amount;
      const requiredPrice = Number(paymentType === "cash" ? cashPrice : pointPrice);
      const username = userData.username;

      // 구매 부족 확인
      if (remainPrice < requiredPrice) {
        const deficit = (requiredPrice - remainPrice).toLocaleString();

        toast({
          title: `상품을 구매할 ${paymentType === "cash" ? "캐시" : "포인트"}가 부족합니다.`,
          description: `${deficit} ${paymentType === "cash" ? "C" : "P"} 부족합니다.`,
          variant: "destructive",
        });

        setLoading(false);
        return;
      }

      // 재화 차감 요청
      const deductResponse = await client.PUT("/api/order/{product_id}", {
        params: {
          path: {
            product_id: product.product_id,
          },
        },
        body: {
          username: username,
          amount: requiredPrice,
          asset_type: paymentType,
          asset_category: "PURCHASE"
        },
      });

      if (deductResponse.response.ok) {
        toast({
          title: "구매가 성공하였습니다.",
        });

        router.push(`/shop/list`);

      } else {
        toast({
          title: `${paymentType === "cash" ? "캐시" : "포인트"} 차감 실패`,
          description: deductResponse.error.msg,
          variant: "destructive",
        });
        setLoading(false);
        return;
      }

    } catch (error) {
      console.error("구매 요청 중 오류:", error);
      if (error.message.includes("401") || error.response?.status === 401) {
        router.push("/login");
      } else if (
        error.message.includes("404") ||
        error.response?.status === 404
      ) {
        toast({
          title: "구매 요청에 실패하였습니다.",
          variant: "destructive",
        });
      } else {
        toast({
          title: "알 수 없는 오류가 발생했습니다.",
          variant: "destructive",
        });
      }
    } finally {
      setLoading(false);
    }
  };

  // 판매 상태에 따른 버튼 활성화 여부 및 텍스트 결정
  const isPurchasable = product.product_sale_state === "AVAILABLE";

  let purchaseButtonText = "";
  if (!isPurchasable) {
    if (product.product_sale_state === "UNAVAILABLE") {
      purchaseButtonText = "판매가 중지되었습니다.";
    } else if (product.product_sale_state === "UPCOMING") {
      purchaseButtonText = "출시 예정 상품입니다.";
    }
  } else {
    purchaseButtonText = loading
        ? "구매 진행 중..."
        : "구매하기";
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mt-20 mb-10 text-center">
        <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
          쇼핑
        </h2>
        <p className="text-md text-gray-400 mt-3">
          보유한 자금으로 특별한 혜택을 누리세요
        </p>
      </div>
      <div className="flex flex-col md:flex-row gap-6">
        {/* 상품 상세 정보 */}
        <div className="flex-1">
          {product.product_image_url ? (
            <Image
              loader={imageLoader}
              src={product.product_image_url}
              alt={product.product_name}
              width={600}
              height={400}
              className="object-cover w-full border border-gray-100"
            />
          ) : (
            <div className="w-full h-64 bg-gray-200 rounded-xl flex items-center justify-center">
              이미지 없음
            </div>
          )}

          <div className="text-3xl font-bold mt-6 mb-3">
            {product.product_name}
          </div>

          <div className="flex items-center mt-4 gap-x-10">
            <div className="flex flex-col">
              <span className="text-lg text-gray-500">캐시 가격</span>
              <span className="text-xl font-semibold text-primary">{cashPrice.toLocaleString()} C</span>
            </div>
            <div className="flex flex-col">
              <span className="text-lg text-gray-500">포인트 가격</span>
              <span className="text-xl font-semibold text-primary">{pointPrice.toLocaleString()} P</span>
            </div>
          </div>

          {product.product_description && (
            <div className="mt-10 py-10 border-t border-dashed border-gray-200">
              <h3 className="text-2xl font-semibold mb-4">상품 설명</h3>
              <div
                className="text-base text-gray-700"
                dangerouslySetInnerHTML={{
                  __html: product.product_description,
                }}
              />
            </div>
          )}
        </div>

        <div className="w-full md:w-1/3 lg:w-2/5 space-y-6">
          <div className="sticky top-20 space-y-6">
            <Card>
              <CardContent className="pt-6 h-full flex flex-col justify-between space-y-6">
                <h2 className="text-2xl font-bold">구매 정보</h2>
                <div className="flex justify-between items-center">
                  <span className="text-lg">{product.product_name}</span>
                  <span className="text-4xl font-bold text-primary">
                    {selectedPrice}
                  </span>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6 h-full flex flex-col justify-between space-y-6">
                <h2 className="text-2xl font-bold">결제 선택</h2>
                <div className="flex flex-col gap-2">
                  <Select value={paymentType} onValueChange={(value) => setPaymentType(value as "cash" | "point")}>
                    <SelectTrigger className="w-full">
                      <SelectValue placeholder="결제 방법 선택" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="cash">캐시 구매</SelectItem>
                      <SelectItem value="point">포인트 구매</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <Button
                    onClick={onPurchase}
                    disabled={loading || !isPurchasable}
                    className="w-full"
                >
                  {purchaseButtonText}
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
}
