"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Coins } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import { BankManagementModal } from "./BankManagementModal";
import {BankAccountNicknameEditModal} from "./BankAccountNicknameEditModal";
import client from "@/lib/backend/client";

export default function ClientPage({ user, bankAccounts: initialBankAccounts }: {
    user: {
        username: string;
        nickname: string;
        point: { amount: number };
        cash: { amount: number };
        question_size: number;
        answer_size: number;
    };
    bankAccounts: {
        bankAccountId: number;
        bankName: string;
        maskedAccountNumber: string;
        accountHolder: string;
        nickname: string;
    }[];
}) {

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [bankAccounts, setBankAccounts] = useState(initialBankAccounts);
    const [selectedBankAccount, setSelectedBankAccount] = useState<{
        bankAccountId: number;
        nickname: string;
    } | null>(null);

    // 계좌 목록 갱신 함수
    const refreshAccounts = async () => {
        try {
            const response = await client.GET("/api/banks/accounts");
            if (response.data) {
                setBankAccounts(response.data.data);
            }
        } catch (error) {
            console.error("계좌 목록을 불러오는 중 오류 발생:", error);
        }
    };

    return (
        <div className="container mx-auto px-4">
            <div className="mt-20 mb-10 text-center">
                <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
                    계좌로 환급 신청
                </h2>
                <p className="text-md text-gray-400 mt-3">
                    활동으로 얻은 포인트를 현금화 해보세요.
                </p>
            </div>

            <div className="flex flex-col gap-10 w-full">
                {/* 환급 가능한 포인트 카드 */}
                <Card className="w-full">
                    <CardHeader>
                        <CardTitle className="flex items-center justify-between">
                            <span className="flex items-center gap-2">
                                환급 가능한 포인트
                            </span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="flex items-center justify-between w-full">
                            <div className="flex items-center gap-2">
                                <Coins size={35} className="text-yellow-500" />
                                <span className="text-green-600 font-semibold text-2xl">
                                    {user.point.amount.toLocaleString()}
                                </span>
                                <span className="font-semibold text-sm">P</span>
                            </div>

                            <Button className="flex flex-row justify-normal gap-3">
                                환급 신청
                            </Button>
                        </div>
                    </CardContent>
                </Card>

                {/* 환급 계좌 카드 */}
                <Card className="w-full">
                    <CardHeader>
                        <CardTitle className="flex items-center justify-between">
            <span className="flex items-center gap-2">
                환급 계좌
            </span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4 w-3/4">
                            {bankAccounts.length > 0 ? (
                                bankAccounts.map((account) => (
                                    <Card key={account.bank_account_id} className="w-full border border-gray-300 shadow-sm">
                                        <CardContent className="flex flex-row items-center justify-between mt-6">
                                            <span className="font-medium text-lg">{account.nickname}</span>
                                            <span className="text-sm text-gray-500">
                                                ({account.bank_name}) {account.masked_account_number}
                                            </span>
                                            <Button
                                                onClick={() => {
                                                    setSelectedBankAccount({
                                                        bankAccountId: account.bank_account_id,
                                                        nickname: account.nickname,
                                                    });
                                                    setIsEditModalOpen(true);
                                                }}
                                                className="flex flex-row justify-normal gap-3">
                                                별칭 수정
                                            </Button>
                                        </CardContent>
                                    </Card>
                                ))
                            ) : (
                                <p className="text-gray-400">등록된 계좌가 없습니다.</p>
                            )}
                        </div>
                        <div className="flex justify-end w-full mt-4">
                            <Button onClick={() => setIsModalOpen(true)} className="flex flex-row justify-normal gap-3">
                                환급 계좌 관리
                            </Button>
                        </div>
                    </CardContent>
                </Card>

                <Card className="w-full bg-gray-100">
                    <CardHeader>
                        <CardTitle className="text-red-500">
                            포인트 환급 시 유의사항
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="text-sm text-gray-600 leading-relaxed">
                            - 환급수수료는 수수료 500원이 발생할 수 있어요. <br />
                            - 출금신청 후 취소는 불가하니 신중히 진행해 주세요. <br />
                            - 출금은 환급 신청 후 즉시 처리돼요. <br />
                            - 은행 점검시간인 23:30~00:30 사이에는 환급 신청을 할 수 없어요. <br />
                            - 은행 장애 등의 사유로 출금이 실패할 경우 포인트로 다시 적립돼요. <br />
                            - 환급금은 등록된 계좌로 입금되므로, 등록된 계좌가 없을 경우 본인명의 계좌를 먼저 등록해 주세요. <br />
                            - 환급 수수료보다 보유한 충전포인트 금액이 큰 경우에만 환급 신청을 할 수 있어요. <br />
                        </p>
                    </CardContent>
                </Card>

                {/* 계좌 관리 모달 (계좌 추가/삭제) */}
                <BankManagementModal
                    isOpen={isModalOpen}
                    onClose={() => setIsModalOpen(false)}
                    bankAccounts={bankAccounts}
                    refreshAccounts={refreshAccounts}
                />

                {/* 계좌 별칭 수정 모달 */}
                <BankAccountNicknameEditModal
                    isOpen={isEditModalOpen}
                    onClose={() => setIsEditModalOpen(false)}
                    account={selectedBankAccount}
                    refreshAccounts={refreshAccounts}
                />
            </div>
        </div>
    );
}