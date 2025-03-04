"use client";

import { useState } from "react";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useToast } from "@/hooks/use-toast";
import client from "@/lib/backend/client";

export function RefundRequestModal({
                                       isOpen,
                                       onClose,
                                       currentPoint,
                                       bankAccounts,
                                       refreshAccounts,
                                       refreshPoint,
                                   }: {
    isOpen: boolean;
    onClose: () => void;
    currentPoint: number;
    bankAccounts: {
        bankAccountId: number;
        bankName: string;
        maskedAccountNumber: string;
        nickname: string;
    }[];
    refreshAccounts: () => void;
    refreshPoint: () => void;
}) {
    const [refundAmount, setRefundAmount] = useState("");
    const [selectedAccount, setSelectedAccount] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const { toast } = useToast();

    // 은행 점검 시간 체크 함수
    const isBankMaintenanceTime = () => {
        const now = new Date();
        const hours = now.getHours();
        const minutes = now.getMinutes();

        // 은행 점검 시간: 23:30 ~ 00:30
        if ((hours === 23 && minutes >= 30) || (hours === 0 && minutes < 30)) {
            return true;
        }
        return false;
    };

    // 환불 처리 핸들러
    const handleRefundRequest = async () => {
        const amount = Number(refundAmount);

        // 환급 계좌 선택 확인
        if (!selectedAccount) {
            toast({
                title: "환급 계좌 선택 필요",
                description: "환급할 계좌를 선택해주세요.",
                variant: "destructive",
            });
            return;
        }

        // 최소 환급 금액 이상 입력 확인
        if (isNaN(amount) || amount < 500) {
            toast({
                title: "환급 신청 실패",
                description: "최소 500P 이상 환급할 수 있습니다.",
                variant: "destructive",
            });
            return;
        }

        // 보유 포인트보다 많은 환급 금액 입력 확인
        if (amount > currentPoint) {
            toast({
                title: "환급 신청 실패",
                description: "보유 포인트보다 많은 금액을 입력할 수 없습니다.",
                variant: "destructive",
            });
            return;
        }

        // 은행 점검 시간 확인
        if (isBankMaintenanceTime()) {
            toast({
                title: "환급 신청 실패",
                description: "은행 점검 시간 (23:30 ~ 00:30)에는 환급 신청이 불가능합니다.",
                variant: "destructive",
            });
            return;
        }

        // 환급 요청
        try {
            setLoading(true);

            const response = await client.PATCH("/api/points/refund", {
                body: { amount },
            });

            console.log(response);

            if (!response) {
                throw new Error("환급 요청 실패");
            }

            toast({
                title: "환급 신청이 정상적으로 처리되었습니다.",
            });

            refreshAccounts();
            refreshPoint();
            onClose();
        } catch (error) {
            console.error("환급 신청 중 오류 발생:", error);
            toast({
                title: "환급 신청을 처리하는 도중 오류가 발생했습니다.",
                variant: "destructive",
            });
        } finally {
            setLoading(false);
        }
    };

    // 수수료 및 환급액 계산
    const amount = parseInt(refundAmount, 10);
    const isValidAmount = !isNaN(amount) && amount >= 500 && currentPoint >= 500;
    const fee = 500;
    const refundAfterFee = isValidAmount ? amount - fee : 0; // 최종 환급액

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="max-w-md">
                <DialogHeader>
                    <DialogTitle>환급 신청</DialogTitle>
                    <DialogDescription>환급 계좌를 선택하고 환급할 포인트를 입력하세요.</DialogDescription>
                </DialogHeader>

                <div className="space-y-4">
                    {/* 현재 포인트 표시 */}
                    <div>
                        <Label>현재 보유 포인트</Label>
                        <div className="p-2 border rounded-md text-gray-500">
                            {currentPoint.toLocaleString()} P
                        </div>
                    </div>

                    {/* 환급 계좌 선택 */}
                    <div>
                        <Label>환급 계좌 선택</Label>
                        <Select onValueChange={setSelectedAccount} value={selectedAccount || ""} disabled={loading}>
                            <SelectTrigger className="w-full">
                                <SelectValue placeholder="환급 계좌를 선택하세요" />
                            </SelectTrigger>
                            <SelectContent>
                                {bankAccounts.length > 0 ? (
                                    bankAccounts.map((account) => (
                                        <SelectItem key={account.bank_account_id} value={account.bank_account_id.toString()}>
                                            {account.nickname} ({account.bank_name} {account.masked_account_number})
                                        </SelectItem>
                                    ))
                                ) : (
                                    <SelectItem value="" disabled>
                                        등록된 계좌가 없습니다.
                                    </SelectItem>
                                )}
                            </SelectContent>
                        </Select>
                    </div>

                    {/* 환급할 포인트 입력 */}
                    <div>
                        <Label>환급할 포인트</Label>
                        <Input
                            type="number"
                            value={refundAmount}
                            onChange={(e) => setRefundAmount(e.target.value)}
                            placeholder="환급할 포인트 입력"
                            disabled={loading}
                        />
                    </div>

                    {/* 수수료 및 최종 환급 금액 */}
                    {isValidAmount && (
                        <div className="text-sm text-gray-500">
                            <p>수수료: {fee.toLocaleString()}P</p>
                        </div>
                    )}

                    {/* 버튼 그룹 */}
                    <div className="flex justify-end gap-2">
                        <Button
                            className="w-full"
                            onClick={handleRefundRequest}
                            disabled={loading || currentPoint < 500 || !isValidAmount || bankAccounts.length === 0}
                        >
                            {loading
                                ? "환급 신청 중..."
                                : isValidAmount
                                    ? `${refundAfterFee.toLocaleString()}원 환급 신청`
                                    : "환급 신청 불가"}
                        </Button>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    );
}