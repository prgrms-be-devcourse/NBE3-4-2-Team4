"use client";

import Image from "next/image";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import type {components} from "@/lib/backend/apiV1/schema";

interface ProductDetailProps {
    product: components["schemas"]["GetItem"];
    onPurchase: () => void;
}

export default function ProductDetail({product, onPurchase}: ProductDetailProps) {
    return (
        <div className="container mx-auto px-4 py-8">
            <div className="flex flex-col md:flex-row gap-6">
                {/* 상품 상세 정보 */}
                <div className="flex-1">
                    <Card className="mb-6">
                        <CardHeader>
                            {product.product_image_url ? (
                                <Image
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
                            <p className="text-base text-gray-700">
                                {product.product_description}
                            </p>
                        </div>
                    )}
                </div>

                {/* 데스크톱용 구매 정보 (오른쪽 고정) */}
                <div className="hidden md:block w-1/3">
                    <div className="sticky top-20 h-screen">
                        <Card className="h-full">
                            <CardContent className="pt-6 h-full flex flex-col justify-between">
                                {/* 상단 영역 */}
                                <div className="space-y-4">
                                    <h2 className="text-2xl font-bold mb-6">구매 정보</h2>
                                    <div className="flex justify-between items-center">
                                        <span className="text-lg">{product.product_name}</span>
                                        <span className="text-lg font-semibold text-primary">
                                            {product.product_price.toLocaleString()} 원
                                        </span>
                                    </div>
                                </div>

                                {/* 하단 영역: 항상 카드의 최하단에 위치 */}
                                <div>
                                    <hr/>
                                    <div className="flex justify-between items-center mt-4">
                                        <span className="text-xl font-bold">총 결제 금액</span>
                                        <span className="text-xl font-bold text-primary">
                                            {product.product_price.toLocaleString()} 원
                                        </span>
                                    </div>
                                    <Button onClick={onPurchase} className="w-full mt-4">
                                        포인트로 구매하기
                                    </Button>
                                </div>
                            </CardContent>
                        </Card>
                    </div>
                </div>
            </div>

            {/* 모바일용 구매 정보 (상품 상세 정보 아래) */}
            <div className="block md:hidden mt-6">
                <Card>
                    <CardContent className="pt-6">
                        <h2 className="text-2xl font-bold mb-6">구매 정보</h2>
                        <div className="flex justify-between items-center mt-4">
                            <span className="text-lg">{product.product_name}</span>
                            <span className="text-lg font-semibold text-primary">
                                {product.product_price.toLocaleString()} 원
                            </span>
                        </div>
                        <hr className="my-4"/>
                        <div className="flex justify-between items-center">
                            <span className="text-xl font-bold">총 결제 금액</span>
                            <span className="text-xl font-bold text-primary">
                                {product.product_price.toLocaleString()} 원
                            </span>
                        </div>
                        <Button onClick={onPurchase} className="w-full mt-4">
                            포인트로 구매하기
                        </Button>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
