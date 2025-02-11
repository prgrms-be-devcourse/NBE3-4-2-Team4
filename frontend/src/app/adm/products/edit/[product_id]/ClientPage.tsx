"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
    Card,
    CardHeader,
    CardTitle,
    CardContent,
    CardFooter
} from "@/components/ui/card";
import client from "@/lib/backend/client";

const saleStates = [
    { value: "ONSALE", label: "판매 중" },
    { value: "SOLDOUT", label: "품절" },
    { value: "RESERVED", label: "예약 중" },
    { value: "COMINGSOON", label: "곧 출시 예정" },
];

export default function ClientPage({ product }: { product?: any }) {
    const router = useRouter();
    const [loading, setLoading] = useState(false);

    if (!product) {
        return <p className="text-center text-red-500">상품 정보를 불러올 수 없습니다.</p>;
    }

    const [formData, setFormData] = useState({
        product_name: product.product_name || "",
        product_price: product.product_price || 0,
        product_description: product.product_description || "",
        product_image: product.product_image_url || "",
        product_category: product.product_category || "",
        product_sale_state: product.product_sale_state || "ONSALE",
    });

    // 입력값 변경 처리
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    // 상품 수정 요청
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const response = await client.PATCH(`/api/products/${product.product_id}`, {
                body: {
                    product_name: formData.product_name,
                    product_price: formData.product_price,
                    product_description: formData.product_description,
                    product_image_url: formData.product_image,
                    product_category: formData.product_category,
                    product_sale_state: formData.product_sale_state,
                },
            });

            if (response.error) {
                throw new Error("상품 수정 실패");
            }

            alert("상품이 성공적으로 수정되었습니다!");
            router.push("/adm/products/list");
        } catch (error) {
            console.error("상품 수정 중 오류 발생:", error);
            alert("상품 수정 중 오류가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-6 max-w-lg mx-auto">
            <Card className="shadow-lg rounded-xl">
                <CardHeader>
                    <CardTitle className="text-xl font-semibold text-gray-800">
                        상품 수정
                    </CardTitle>
                </CardHeader>

                <CardContent>
                    <form onSubmit={handleSubmit} className="space-y-4">
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

                        <div className="space-y-2">
                            <Label htmlFor="product_price">가격</Label>
                            <Input
                                id="product_price"
                                name="product_price"
                                type="number"
                                placeholder="상품 가격을 입력하세요"
                                value={formData.product_price}
                                onChange={handleChange}
                                required
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
                            <Input
                                id="product_description"
                                name="product_description"
                                placeholder="상품 설명을 입력하세요"
                                value={formData.product_description}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="product_category">카테고리</Label>
                            <Input
                                id="product_category"
                                name="product_category"
                                placeholder="상품 카테고리를 입력하세요"
                                value={formData.product_category}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="product_sale_state">판매 상태</Label>
                            <select
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
                            </select>
                        </div>

                        <CardFooter className="flex justify-between">
                            <Button
                                variant="outline"
                                type="button"
                                onClick={() => router.back()}
                                className="w-1/3 text-gray-700 border-gray-400"
                            >
                                취소
                            </Button>
                            <Button
                                type="submit"
                                disabled={loading}
                                className="w-2/3 bg-blue-600 hover:bg-blue-700 text-white"
                            >
                                {loading ? "수정 중..." : "수정하기"}
                            </Button>
                        </CardFooter>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}
