"use client";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import {CircleDollarSign, Coins} from "lucide-react";
import {useRouter} from "next/navigation";
import {CashTopupModal} from "@/app/cash/topup/CashTopupModal";
import {useState} from "react";
import {RefundModal} from "@/app/cash/refund/RefundModal";

export default function PayInfoForm({
                                        user
}: {
    user: {
        username: string;
        emailAddress: string;
        point: number;
        cash: number;
    };
}) {

    const router = useRouter();

    // 포인트 환불 관리 페이지 이동
    const withdrawPoint = () => {
        router.push("/point/withdrawal");
    };

    // 캐시 충전 모달 이동
    const [isCashTopupModalOpen, setIsCashTopupModalOpen] = useState(false);
    const [isRefundModalOpen, setIsRefundModalOpen] = useState(false);

    return (
        <div className="flex justify-center gap-5 w-full mx-auto">
            <Card className="w-1/2">
                <CardHeader>
                    <CardTitle className="flex items-center justify-between">
                        <span className="flex items-center gap-2">
                            <Coins size={35}/> 포인트
                        </span>
                    </CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col gap-3">
                    <div className="text-left pb-3">
                        <span className="font-semibold text-2xl">{user.point.toLocaleString()} POINT</span>
                    </div>

                    <div className="flex flex-row justify-normal gap-3">
                        <Button className="w-full" onClick={withdrawPoint}>계좌로 환급</Button>
                    </div>
                </CardContent>
            </Card>
            <Card className="w-1/2">
                <CardHeader>
                    <CardTitle className="flex items-center justify-between">
                        <span className="flex items-center gap-2">
                            <CircleDollarSign size={35}/> 캐시
                        </span>
                    </CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col gap-3">
                    <div className="text-left pb-3">
                        <span className="font-semibold text-2xl">{user.cash.toLocaleString()} CASH</span>
                    </div>

                    <div className="flex flex-row justify-normal gap-3">
                        <Button className="w-1/2" onClick={() => setIsCashTopupModalOpen(true)}>
                            충전하기
                        </Button>
                        <Button className="w-1/2" onClick={() => setIsRefundModalOpen(true)}>
                            환불하기
                        </Button>
                    </div>
                </CardContent>
            </Card>

            {/* 캐시 충전 모달 */}
            <CashTopupModal
                isOpen={isCashTopupModalOpen}
                onClose={() => setIsCashTopupModalOpen(false)}
                user={user}
            />

            {/* 환불 모달 */}
            <RefundModal
                isOpen={isRefundModalOpen}
                onClose={() => setIsRefundModalOpen(false)}
                user={user}
            />
        </div>
    );
}