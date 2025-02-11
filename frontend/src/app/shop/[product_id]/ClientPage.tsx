"use client";

import Image from "next/image";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import type { paths } from "@/lib/backend/apiV1/schema";
import createClient from "openapi-fetch";
import { useState } from "react";
import imageLoader from "@/utils/imageLoader";

const client = createClient<paths>({
    baseUrl: "http://localhost:8080",
    credentials: "include",
});

export default function ClientPage({ product, cookieString }) {
    const router = useRouter();
    const [loading, setLoading] = useState(false); // 로딩 상태 추가

    // 구매 버튼 클릭 시 API 요청
    const onPurchase = async () => {
        if (loading) return; // 이미 요청 중이면 중복 실행 방지
        setLoading(true); // 로딩 시작

        try {
            // 유저 정보 조회
            const userResponse = await client.GET("/api/members/details", {
                headers: {
                    cookie: cookieString,
                },
            });

            // 401 응답이면 로그인 페이지로 리디렉션
            if (userResponse.response.status === 401) {
                alert("로그인이 필요합니다.");
                router.push("/login");
                return;
            }

            console.log(userResponse);

            const point = userResponse.data.data!.point;
            const username = userResponse.data.data!.username;

            console.log(`유저 이름: ${username}, 현재 포인트: ${point}`);

            // 포인트 확인: 부족하면 알림 후 중단
            if (point < product.product_price) {
                alert("상품을 구매할 포인트가 부족합니다.");
                setLoading(false);
                return;
            }

            // 포인트 차감 요청
            const pointDeductResponse = await client.PUT("/api/order/{product_id}", {
                params: {
                    path: {
                        product_id: product.product_id
                    },
                },

                body: {
                    username: username,
                    amount: product.product_price,
                },
            });

            if (pointDeductResponse.error || !pointDeductResponse.data) {
                throw new Error(`포인트 차감 실패: ${pointDeductResponse.status}`);
            }

            console.log("포인트 차감 성공:", pointDeductResponse);

            //  구매 성공 후 알림 및 상품 리스트로 이동
            alert("구매가 성공하였습니다.");
            router.push(`/shop/list`);
        } catch (error) {
            console.error("구매 요청 중 오류:", error);

            if (error.message.includes("401") || error.response?.status === 401) {
                router.push("/login");
            } else if (error.message.includes("404") || error.response?.status === 404) {
                alert("구매 요청에 실패하였습니다.");
            } else {
                alert("알 수 없는 오류가 발생했습니다.");
            }
        } finally {
            setLoading(false); // 로딩 종료
        }
    };

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="flex flex-col md:flex-row gap-6">
                {/* 상품 상세 정보 */}
                <div className="flex-1">
                    <Card className="mb-6">
                        <CardHeader>
                            {product.product_image_url ? (
                                <Image
                                    loader={imageLoader}
                                    src={product.product_image_url}
                                    alt={product.product_name}
                                    width={600}
                                    height={400}
                                    className="object-cover rounded-xl"
                                />
                            ) : (
                                <div className="w-full h-64 bg-gray-200 rounded-xl flex items-center justify-center">
                                    이미지 없음
                                </div>
                            )}
                        </CardHeader>
                        <CardContent>
                            <CardTitle className="text-3xl font-bold mb-4">
                                {product.product_name}
                            </CardTitle>
                            <p className="text-xl font-semibold text-primary mb-4">
                                {product.product_price.toLocaleString()} 원
                            </p>
                        </CardContent>
                    </Card>

                    {product.product_description && (
                        <div className="mb-6 p-6 bg-white border rounded-xl shadow">
                            <h3 className="text-2xl font-semibold mb-4">상품 설명</h3>
                            <p className="text-base text-gray-700">{product.product_description}</p>
                        </div>
                    )}
                </div>

                {/* 데스크톱용 구매 정보 (오른쪽 고정) */}
                <div className="hidden md:block w-1/3">
                    <div className="sticky top-20 h-screen">
                        <Card className="h-full">
                            <CardContent className="pt-6 h-full flex flex-col justify-between">
                                <div className="space-y-4">
                                    <h2 className="text-2xl font-bold mb-6">구매 정보</h2>
                                    <div className="flex justify-between items-center">
                                        <span className="text-lg">{product.product_name}</span>
                                        <span className="text-lg font-semibold text-primary">
                                            {product.product_price.toLocaleString()} 원
                                        </span>
                                    </div>
                                </div>
                                <div>
                                    <hr />
                                    <div className="flex justify-between items-center mt-4">
                                        <span className="text-xl font-bold">총 결제 금액</span>
                                        <span className="text-xl font-bold text-primary">
                                            {product.product_price.toLocaleString()} 원
                                        </span>
                                    </div>
                                    <Button onClick={onPurchase} disabled={loading} className="w-full mt-4">
                                        {loading ? "구매 진행 중..." : "포인트로 구매하기"}
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
