"use client";

import Image from "next/image";
import { useRouter } from "next/navigation";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import imageLoader from "@/utils/imageLoader";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";

export default function ClientPage({ product, cookieString }) {
  const router = useRouter();
  const [loading, setLoading] = useState(false);

  const { toast } = useToast();

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

      // 401 응답이면 로그인 페이지로 리디렉션
      if (userResponse.response.status === 401) {
        toast({
          title: "로그인이 필요합니다.",
          variant: "destructive",
        });
        router.push("/login");
        return;
      }

      const point = userResponse.data.data!.point;
      const username = userResponse.data.data!.username;

      // 포인트 부족 확인
      if (point < product.product_price) {
        toast({
          title: "상품을 구매할 포인트가 부족합니다.",
          variant: "destructive",
        });
        setLoading(false);
        return;
      }

      // 포인트 차감 요청
      const pointDeductResponse = await client.PUT("/api/order/{product_id}", {
        params: {
          path: {
            product_id: product.product_id,
          },
        },
        body: {
          username: username,
          amount: product.product_price,
        },
      });

      if (pointDeductResponse.error || !pointDeductResponse.data) {
        toast({
          title: "포인트 차감 실패",
          description: pointDeductResponse.error.msg,
          variant: "destructive",
        });
        setLoading(false);
        return;
      }

      // 구매 성공 후 알림 및 상품 리스트로 이동
      toast({
        title: "구매가 성공하였습니다.",
      });
      router.push(`/shop/list`);
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
      purchaseButtonText =
          "판매가 중지되었습니다.";
    } else if (product.product_sale_state === "UPCOMING") {
      purchaseButtonText = "출시 예정 상품입니다.";
    }
  } else {
    purchaseButtonText = loading ? "구매 진행 중..." : "포인트로 구매하기";
  }

  return (
      <div className="container mx-auto px-4 py-8">
        <div className="mt-20 mb-10 text-center">
          <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
            포인트 쇼핑
          </h2>
          <p className="text-md text-gray-400 mt-3">
            적립한 포인트로 특별한 혜택을 누리세요
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
            <p className="text-xl font-semibold text-primary mb-4">
              {product.product_price.toLocaleString()} 원
            </p>

            {product.product_description && (
                <div className="mt-10 py-10 border-t border-dashed border-gray-200">
                  <h3 className="text-2xl font-semibold mb-4">상품 설명</h3>
                  <p className="text-base text-gray-700">
                    {product.product_description}
                  </p>
                </div>
            )}
          </div>

          {/* 데스크톱용 구매 정보 (오른쪽 고정) */}
          <div className="w-full md:w-1/3 lg:w-2/5">
            <div className="sticky top-20">
              <Card>
                <CardContent className="pt-6 h-full flex flex-col justify-between">
                  <div className="space-y-4">
                    <h2 className="text-2xl font-bold mb-6">구매 정보</h2>
                  </div>
                  <div>
                    <div className="flex justify-between items-center">
                      <span className="text-lg">{product.product_name}</span>
                      <span className="text-4xl font-bold text-primary">
                      {product.product_price.toLocaleString()} 원
                    </span>
                    </div>
                    <Button
                        onClick={onPurchase}
                        disabled={loading || !isPurchasable}
                        className="w-full mt-4"
                    >
                      {purchaseButtonText}
                    </Button>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </div>
  );
}