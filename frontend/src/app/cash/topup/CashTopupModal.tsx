"use client";

import { useState } from "react";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select";
import { Checkbox } from "@/components/ui/checkbox";
import { useToast } from "@/hooks/use-toast";
import { TriangleAlert } from 'lucide-react';
import { UseImportPayment } from "../../cash/payment/UseImportPayment";


export function CashTopupModal({
                                   isOpen,
                                   onClose,
                                   user,
                                   refreshCash,
                               }: {
    isOpen: boolean;
    onClose: () => void;
    user: {
        username: string;
        emailAddress: string;
        cash: number;
    };
    refreshCash: () => void;
}) {
    const { toast } = useToast();
    const [amount, setAmount] = useState<number>(0);
    const [paymentMethod, setPaymentMethod] = useState<string | null>(null);
    const [agreement, setAgreement] = useState(false);
    const { requestPay } = UseImportPayment();

    const presetAmounts = [1000, 3000, 5000, 10000];

    // 금액 버튼 클릭 시 해당 금액을 누적 추가
    const handlePresetAmount = (value: number) => {
        setAmount((prevAmount) => prevAmount + value);
    };

    // 직접 입력 핸들러 (수정 가능, 감소 가능)
    const handleCustomAmount = (value: string) => {
        let numericValue = Number(value);

        if (isNaN(numericValue)) {
            toast({
                title: "잘못된 입력",
                description: "숫자만 입력 가능합니다.",
                variant: "destructive",
            });
            return;
        }

        if (numericValue < 0) {
            numericValue = 0; // 음수 입력 방지
        }

        setAmount(numericValue); // 기존 값 덮어쓰기 (감소 가능)
    };

    // 충전 요청 핸들러
    const handleTopup = () => {
        if (amount < 1000) {
            toast({
                title: "충전 실패",
                description: "최소 1,000원 이상 충전해야 합니다.",
                variant: "destructive",
            });
            return;
        }

        if (!paymentMethod) {
            toast({
                title: "결제 수단 선택 필요",
                description: "결제 수단을 선택해주세요.",
                variant: "destructive",
            });
            return;
        }

        if (!agreement) {
            toast({
                title: "결제 동의 필요",
                description: "결제 조건 및 주의사항을 확인 후 동의해주세요.",
                variant: "destructive",
            });
            return;
        }

        toast({
            title: "결제 페이지로 이동 중...",
            description: `${amount.toLocaleString()}원 충전을 진행합니다.`,
        });

        // 결제 연동 (아임포트 API)
        requestPay(amount, paymentMethod, user);

        // 입력 상태 초기화
        setAmount(0);
        setPaymentMethod(null);
        setAgreement(false);
        refreshCash();

        // 모달 닫기
        onClose();
    };

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="max-w-lg p-6">
                <DialogHeader>
                    <DialogTitle className="text-center text-xl font-bold">캐시 충전</DialogTitle>
                    <DialogDescription></DialogDescription>
                </DialogHeader>

                {/* 충전할 캐시 */}
                <div>
                    <p className="text-sm text-gray-500 mb-2">충전 할 캐시</p>
                    <Input
                        className="text-3xl font-bold w-full px-4 py-7"
                        type="number"
                        value={amount}
                        onChange={(e) => handleCustomAmount(e.target.value)}
                    />
                </div>

                {/* 충전할 금액 선택 */}
                <div className="mb-2">
                    <div className="flex items-center gap-2">
                        {presetAmounts.map((amt) => (
                            <Button
                                key={amt}
                                variant="outline"
                                onClick={() => handlePresetAmount(amt)}
                            >
                                +{amt.toLocaleString()}원
                            </Button>
                        ))}
                    </div>
                </div>

                {/* 결제 정보 표시 */}
                <div className="mb-3 text-sm text-gray-500 space-y-2">
                    {amount < 1000 && (
                        <>
                            <div className="flex items-center gap-1 text-red-600 font-bold">
                                <TriangleAlert color="red" size="16" />
                                <p>1000원 이상 입력해주세요</p>
                            </div>
                        </>
                    )}
                    {amount >= 1000 && (
                        <>
                            <div className="flex justify-between">
                                <p>결제하실 금액</p>
                                <span className="font-black text-red-600">{amount.toLocaleString()} 원</span>
                            </div>
                            <div className="flex justify-between">
                                <p>결제 후 내 캐시</p>
                                <span className="font-black">{(user.cash + amount).toLocaleString()} CASH</span>
                            </div>
                        </>
                    )}
                </div>

                {/* 결제 수단 선택 */}
                <div className="mb-4">
                    <p className="text-sm text-gray-500 mb-2">결제 수단 선택</p>
                    <Select onValueChange={setPaymentMethod} value={paymentMethod || ""}>
                        <SelectTrigger className="w-full">
                            <SelectValue placeholder="결제 수단을 선택하세요" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="card">신용/체크카드</SelectItem>
                            {/*<SelectItem value="trans">계좌이체</SelectItem>*/}
                            {/*<SelectItem value="vbank">가상계좌</SelectItem>*/}
                            {/*<SelectItem value="phone">휴대폰 소액결제</SelectItem>*/}
                        </SelectContent>
                    </Select>
                </div>

                {/* 결제 동의 체크박스 */}
                <div className="flex items-center gap-2 mb-4">
                    <Checkbox checked={agreement} onCheckedChange={() => setAgreement(!agreement)} />
                    <p className="text-sm text-gray-600">[필수] 결제 조건 및 주의사항을 확인했습니다.</p>
                </div>

                {/* 결제 버튼 */}
                <Button className="w-full" onClick={handleTopup} disabled={amount < 1000 || !agreement}>
                    결제하기
                </Button>
            </DialogContent>
        </Dialog>
    );
}
