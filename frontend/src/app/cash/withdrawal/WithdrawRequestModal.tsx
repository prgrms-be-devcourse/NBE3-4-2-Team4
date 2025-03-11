"use client";

import { useState } from "react";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useToast } from "@/hooks/use-toast";
import client from "@/lib/backend/client";
import {TriangleAlert} from "lucide-react";

export function WithdrawRequestModal({
                                       isOpen,
                                       onClose,
                                       currentCash,
                                       bankAccounts,
                                       refreshAccounts,
                                       refreshPoint,
                                   }: {
    isOpen: boolean;
    onClose: () => void;
    currentCash: number;
    bankAccounts: {
        bankAccountId: number;
        bankName: string;
        maskedAccountNumber: string;
        nickname: string;
    }[];
    refreshAccounts: () => void;
    refreshPoint: () => void;
}) {
    const [withdrawalAmount, setWithdrawalAmount] = useState("");
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

    // 출금 처리 핸들러
    const handleWithdrawRequest = async () => {
        const amount = Number(withdrawalAmount);

        // 출금 계좌 선택 확인
        if (!selectedAccount) {
            toast({
                title: "출금 계좌 선택 필요",
                description: "출금할 계좌를 선택해주세요.",
                variant: "destructive",
            });
            return;
        }

        // 최소 출금 금액 이상 입력 확인
        if (isNaN(amount) || amount < 5000) {
            toast({
                title: "출금 신청 실패",
                description: "최소 5000C 이상 출금할 수 있습니다.",
                variant: "destructive",
            });
            return;
        }

        // 보유 캐시보다 많은 출금 금액 입력 확인
        if (amount > currentCash) {
            toast({
                title: "출금 신청 실패",
                description: "보유 캐시보다 많은 금액을 입력할 수 없습니다.",
                variant: "destructive",
            });
            return;
        }

        // 은행 점검 시간 확인
        if (isBankMaintenanceTime()) {
            toast({
                title: "출금 신청 실패",
                description: "은행 점검 시간 (23:30 ~ 00:30)에는 출금 신청이 불가능합니다.",
                variant: "destructive",
            });
            return;
        }

        // 출금 요청
        try {
            setLoading(true);

            const withdrawalResponse = await client.PATCH("/api/asset/refund", {
                body: {
                    amount: amount,
                    assetType: "CASH",
                    assetCategory: "CASH_WITHDRAWAL"
                },
            });

            console.log(withdrawalResponse);

            if (withdrawalResponse.response.ok) {

                toast({
                    title: "출금 신청이 정상적으로 처리되었습니다.",
                });

                refreshAccounts();
                refreshPoint();
                onClose();

            } else {
                throw new Error("출금 요청 실패");
            }

        } catch (error) {
            console.error("출금 신청 중 오류 발생:", error);
            toast({
                title: "출금 신청을 처리하는 도중 오류가 발생했습니다.",
                variant: "destructive",
            });
        } finally {
            setLoading(false);
        }
    };

    // 수수료 및 출금액 계산
    const amount = parseInt(withdrawalAmount, 10);
    const isValidAmount = !isNaN(amount) && amount >= 5000 && currentCash >= 5000;
    const fee = 500;
    const withdrawAfterFee = isValidAmount ? amount - fee : 0; // 최종 출금액

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="max-w-md">
                <DialogHeader>
                    <DialogTitle>출금 신청</DialogTitle>
                    <DialogDescription>출금 계좌를 선택하고 출금할 캐시를 입력하세요.</DialogDescription>
                </DialogHeader>

                <div className="space-y-4">
                    {/* 현재 캐시 표시 */}
                    <div>
                        <Label>현재 보유 캐시</Label>
                        <div className="p-2 border rounded-md text-gray-500">
                            {currentCash.toLocaleString()} C
                        </div>
                    </div>

                    {/* 출금 계좌 선택 */}
                    <div>
                        <Label>출금 계좌 선택</Label>
                        <Select onValueChange={setSelectedAccount} value={selectedAccount || ""} disabled={loading}>
                            <SelectTrigger className="w-full">
                                <SelectValue placeholder="출금 계좌를 선택하세요" />
                            </SelectTrigger>
                            <SelectContent>
                                {bankAccounts.length > 0 ? (
                                    bankAccounts.map((account) => (
                                        <SelectItem key={account.bank_account_id} value={account.bank_account_id.toString()}>
                                            {account.nickname} ({account.bank_name} {account.masked_account_number})
                                        </SelectItem>
                                    ))
                                ) : (
                                    <SelectItem value="NotExist" disabled>
                                        등록된 계좌가 없습니다.
                                    </SelectItem>
                                )}
                            </SelectContent>
                        </Select>
                    </div>

                    {/* 출금할 캐시 입력 */}
                    <div>
                        <Label>출금할 캐시</Label>
                        <Input
                            type="number"
                            value={withdrawalAmount}
                            onChange={(e) => setWithdrawalAmount(e.target.value)}
                            placeholder="출금할 캐시 입력"
                            disabled={loading}
                        />

                        {amount < 5000 && (
                            <div className="flex items-center gap-2 text-red-500 text-sm mt-1">
                                <TriangleAlert size={16} />
                                <p>최소 5000캐시 이상부터 출금할 수 있습니다.</p>
                            </div>
                        )}
                    </div>

                    {/* 수수료 및 최종 출금 금액 */}
                    {isValidAmount && (
                        <div className="text-sm text-gray-500">
                            <p>수수료: {fee.toLocaleString()}원</p>
                        </div>
                    )}

                    {/* 버튼 그룹 */}
                    <div className="flex justify-end gap-2">
                        <Button
                            className="w-full"
                            onClick={handleWithdrawRequest}
                            disabled={loading || currentCash < 5000 || !isValidAmount || bankAccounts.length === 0}
                        >
                            {loading
                                ? "출금 신청 중..."
                                : isValidAmount
                                    ? `${withdrawAfterFee.toLocaleString()}원 출금 신청`
                                    : "출금 신청 불가"}
                        </Button>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    );
}