'use client';

import { Button } from '@/components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import Link from 'next/link';
import {components} from "@/lib/backend/apiV1/schema";
import PaginationType1Responsive from "@/lib/business/components/PaginationType1Responsive";

export default function ClientPage({
                                       page,
                                       pageSize,
                                       itemPage,
                                   }: {
    page: number;
    pageSize: number;
    itemPage: components["schemas"]["PageDtoGetItem"];
}) {

    const normalizedItemPage: components["schemas"]["PageDtoGetItem"] = {
        currentPageNumber: itemPage.data.current_page_number,
        pageSize: itemPage.data.page_size,
        totalPages: itemPage.data.total_pages,
        totalItems: itemPage.data.total_items,
        hasMore: itemPage.data.has_more,
        items: itemPage.data.items,
    };

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-4">
                <h1 className="text-2xl font-bold">상품 목록</h1>
                <Button asChild>
                    <Link href="/adm/products/new">상품 등록</Link>
                </Button>
            </div>
            <div className="p-4 bg-white shadow rounded-lg">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>ID</TableHead>
                            <TableHead>상품명</TableHead>
                            <TableHead>가격</TableHead>
                            <TableHead>설명</TableHead>
                            <TableHead>카테고리</TableHead>
                            <TableHead>판매 상태</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {normalizedItemPage.items?.map((item) => (
                            <TableRow key={item.product_id}>
                                <TableCell>{item.product_id}</TableCell>
                                <TableCell>{item.product_name}</TableCell>
                                <TableCell>{item.product_price.toLocaleString()}원</TableCell>
                                <TableCell>{item.product_description}</TableCell>
                                <TableCell>{item.product_category}</TableCell>
                                <TableCell>{item.product_sale_state}</TableCell>
                                <TableCell>
                                    <div className="flex gap-2">
                                        <Button variant="outline" asChild>
                                            <Link href={`/adm/products/edit/${item.product_id}`}>수정</Link>
                                        </Button>
                                        <Button variant="destructive">삭제</Button>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>

            <PaginationType1Responsive
                className="my-6"
                baseQueryString={`pageSize=${pageSize}`}
                totalPages={normalizedItemPage.totalPages}
                currentPageNumber={normalizedItemPage.currentPageNumber}
            />
        </div>
    );
}
