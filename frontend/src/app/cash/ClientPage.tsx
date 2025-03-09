"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {CircleDollarSign} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import client from "@/lib/backend/client";
import {CashTopupModal} from "@/app/cash/topup/CashTopupModal";
import {RefundModal} from "@/app/cash/refund/RefundModal";

export default function ClientPage({
                                       user: initialUser,
                                       cookieString
                                   }: {
    user: {
        username: string;
        emailAddress: string;
        cash: number;
    };
    cookieString: string;
}) {

    const [user, setUser] = useState(initialUser);
    const [isCashTopupModalOpen, setIsCashTopupModalOpen] = useState(false);
    const [isRefundModalOpen, setIsRefundModalOpen] = useState(false);

    // 유저 포인트 갱신 함수
    const refreshCash = async () => {

        // 유저 정보 조회
        try {
            const userResponse = await client.GET("/api/members/details", {
                headers: {
                    cookie: cookieString,
                },
            });

            if (userResponse.response.ok) {
                const userData = userResponse?.data?.data ?? [];
                setUser(userData);
            }

        } catch (error) {
            console.error("유저 포인트 정보 불러오는 중 오류 발생:", error);
        }
    }

    return (
        <div className="container mx-auto px-4">
            <div className="mt-20 mb-10 text-center">
                <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
                    캐시 관리
                </h2>
                <p className="text-md text-gray-400 mt-3">
                    효율적인 캐시 관리로 더욱 편리한 서비스를 이용해 보세요.
                </p>
            </div>

            <div className="flex flex-col gap-10 w-full">
                {/* 환급 가능한 포인트 카드 */}
                <Card className="w-full">
                    <CardHeader>
                        <CardTitle className="flex items-center justify-between">
                            <span className="flex items-center gap-2">
                                보유 캐시
                            </span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="flex items-center justify-between w-full">
                            <div className="flex items-center gap-2">
                                <CircleDollarSign size={35} className="text-yellow-500" />
                                <span className="text-green-600 font-semibold text-2xl">
                                    {user.cash.toLocaleString()}
                                </span>
                                <span className="font-semibold text-xl text-gray-700">CASH</span>
                            </div>

                            <div className="flex gap-3">
                                <Button
                                    onClick={() => setIsCashTopupModalOpen(true)}
                                    className="px-5 py-2"
                                >
                                    충전하기
                                </Button>
                                <Button
                                    onClick={() => setIsRefundModalOpen(true)}
                                    className="px-5 py-2"
                                >
                                    환불하기
                                </Button>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="w-full bg-gray-100">
                    <CardHeader>
                        <CardTitle className="text-red-500">
                            주의사항
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="text-sm text-gray-600 leading-relaxed">
                            - 캐시 충전은 결제 후 즉시 처리되며 취소가 불가능합니다. 결제 전 다시 한번 확인해 주세요. <br />
                            - 캐시 환불은 결제 수단으로만 가능합니다. 다른 결제 수단이나 타 계좌로 환불되지 않습니다. <br />
                            - 충전한 캐시는 계정에 귀속되며 타 계정으로 양도할 수 없습니다. <br />
                            - 불법적인 거래 및 부정 결제는 제한될 수 있습니다. 이용 약관을 위반하는 경우 서비스 이용이 제한될 수 있어요. <br />
                            - 캐시 환불 요청이 반복되거나 이상 거래가 발생할 경우, 계정 사용이 제한될 수 있습니다. <br />
                            - 은행 점검 시간(23:30~00:30)에는 일부 캐시 결제 및 환불 처리가 지연될 수 있습니다. <br />
                        </p>
                    </CardContent>
                </Card>

                {/* 캐시 충전 모달 */}
                <CashTopupModal
                    isOpen={isCashTopupModalOpen}
                    onClose={() => setIsCashTopupModalOpen(false)}
                    user={user}
                    refreshCash={refreshCash}
                />

                {/* 환불 모달 */}
                <RefundModal
                    isOpen={isRefundModalOpen}
                    onClose={() => setIsRefundModalOpen(false)}
                    user={user}
                    refreshCash={refreshCash}
                />
            </div>
        </div>
    );
}