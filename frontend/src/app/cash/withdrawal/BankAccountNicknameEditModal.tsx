"use client";

import { useState, useEffect } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";

export function BankAccountNicknameEditModal({ isOpen, onClose, account, refreshAccounts }: {
    isOpen: boolean;
    onClose: () => void;
    account: {
        bankAccountId: number;
        nickname: string;
    } | null;
    refreshAccounts: () => void;
}) {
    const [newNickname, setNewNickname] = useState(account?.nickname || "");
    const [loading, setLoading] = useState(false);
    const { toast } = useToast();

    // 계좌가 변경될 때마다 기존 별칭을 초기화
    useEffect(() => {
        setNewNickname(account?.nickname || "");
    }, [account]);

    // 별칭 업데이트 함수
    const handleUpdateNickname = async () => {
        if (!account) return;

        try {
            setLoading(true);
            const response = await client.PATCH(`/api/banks/accounts/${account.bankAccountId}`, {
                body: { nickname: newNickname },
            });

            if (!response) {
                throw new Error("계좌 별칭 업데이트 실패");
            }

            toast({ title: "계좌 별칭이 성공적으로 변경되었습니다!" });

            refreshAccounts();
            onClose();
        } catch (error) {
            console.error("계좌 별칭 수정 중 오류 발생:", error);
            toast({ title: "계좌 별칭 수정 중 오류가 발생했습니다.", variant: "destructive" });
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="max-w-md">
                <DialogHeader>
                    <DialogTitle>출금 계좌 별칭 수정</DialogTitle>
                    <DialogDescription>한눈에 보기 쉽게 출금 계좌의 별칭을 추가해보세요.</DialogDescription>
                </DialogHeader>
                <div className="space-y-4">
                    {/* 기존 별칭 표시 */}
                    <div>
                        <Label>현재 별칭</Label>
                        <div className="p-2 border rounded-md text-gray-500">
                            {account?.nickname || "등록된 별칭 없음"}
                        </div>
                    </div>

                    {/* 새로운 별칭 입력 */}
                    <div>
                        <Label>새 별칭</Label>
                        <Input
                            value={newNickname}
                            onChange={(e) => setNewNickname(e.target.value.slice(0, 20))} // 최대 20자 제한
                            maxLength={20}
                            placeholder="새로운 별칭 입력"
                            disabled={loading}
                        />
                        <p className="text-sm text-gray-500 mt-1 text-right">
                            {newNickname.length} / 20
                        </p>
                    </div>

                    {/* 버튼 그룹 */}
                    <div className="flex justify-end gap-2">
                        <Button variant="outline" onClick={onClose} disabled={loading}>
                            취소
                        </Button>
                        <Button onClick={handleUpdateNickname} disabled={loading || newNickname.length === 0}>
                            {loading ? "변경 중..." : "변경하기"}
                        </Button>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    );
}