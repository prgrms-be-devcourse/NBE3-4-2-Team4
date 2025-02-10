'use client';

import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useRouter } from 'next/navigation';
import client from '@/lib/backend/client';

const saleStates = [
    { value: 'ONSALE', label: '판매 중' },
    { value: 'SOLDOUT', label: '품절' },
    { value: 'RESERVED', label: '예약 중' },
    { value: 'COMINGSOON', label: '곧 출시 예정' },
];

export default function ClientPage() {
    const router = useRouter();
    const [formData, setFormData] = useState({
        product_name: '',
        product_price: 0,
        product_description: '',
        product_image: '',
        product_category: '',
        product_sale_state: 'ONSALE',
    });
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
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
                alert(response.error.msg || "상품 등록 실패");
                return;
            }

            alert('상품이 성공적으로 등록되었습니다!');
            router.push('/adm/products/list');
        } catch (error) {
            console.error("상품 등록 중 오류 발생:", error);
            alert('상품 등록 중 오류가 발생했습니다.');
        } finally {
            setLoading(false);
        }
    };


    return (
        <div className="p-6 max-w-lg mx-auto">
            <h1 className="text-2xl font-bold mb-4">상품 등록</h1>
            <form onSubmit={handleSubmit} className="space-y-4">
                <Input name="product_name"
                       placeholder="상품명"
                       value={formData.product_name}
                       onChange={handleChange} required />
                <Input name="product_price"
                       type="number"
                       placeholder="가격"
                       value={formData.product_price}
                       onChange={handleChange} required />
                <Input name="product_image"
                       placeholder="이미지 URL"
                       value={formData.product_image}
                       onChange={handleChange} required />
                <Input name="product_description"
                       placeholder="설명"
                       value={formData.product_description}
                       onChange={handleChange} required />
                <Input name="product_category"
                       placeholder="카테고리"
                       value={formData.product_category}
                       onChange={handleChange} required />

                <div>
                    <label className="block text-sm font-medium mb-1">판매 상태</label>
                    <select
                        name="product_sale_state"
                        value={formData.product_sale_state}
                        onChange={handleChange}
                        className="w-full p-2 border rounded">
                        {saleStates.map((state) => (
                            <option key={state.value} value={state.value}>{state.label}</option>
                        ))}
                    </select>
                </div>

                <Button type="submit" disabled={loading}>{loading ? '등록 중...' : '등록'}</Button>
            </form>
        </div>
    );
}
