"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Coins } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import { BankManagementModal } from "./BankManagementModal";
import { BankAccountNicknameEditModal } from "./BankAccountNicknameEditModal";
import { WithdrawRequestModal } from "./WithdrawRequestModal";
import client from "@/lib/backend/client";

export default function ClientPage({
                                       user: initialUser,
                                       bankAccounts: initialBankAccounts,
                                       cookieString
}: {
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
    cookieString: string;
}) {

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [isWithdrawModalOpen, setIsWithdrawModalOpen] = useState(false);
    const [bankAccounts, setBankAccounts] = useState(initialBankAccounts);
    const [user, setUser] = useState(initialUser);
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

    // 유저 캐시 갱신 함수
    const refreshPoint = async () => {

        // 유저 정보 조회
        try {
            const response = await client.GET("/api/members/details", {
                headers: {
                    cookie: cookieString,
                },
            });
            if (response.data) {
                setUser(response.data.data);
            }
        } catch (error) {
            console.error("유저 캐시 정보 불러오는 중 오류 발생:", error);
        }
    }

    return (
        <div className="container mx-auto px-4">
            <div className="mt-20 mb-10 text-center">
                <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
                    계좌로 출금 신청
                </h2>
                <p className="text-md text-gray-400 mt-3">
                    활동으로 얻은 캐시를 출금 해보세요.
                </p>
            </div>

            <div className="flex flex-col gap-10 w-full">
                {/* 출금 가능한 캐시 */}
                <Card className="w-full">
                    <CardHeader>
                        <CardTitle className="flex items-center justify-between">
                            <span className="flex items-center gap-2">
                                출금 가능한 캐시
                            </span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="flex items-center justify-between w-full">
                            <div className="flex items-center gap-2">
                                <Coins size={35} className="text-yellow-500" />
                                <span className="text-green-600 font-semibold text-2xl">
                                    {user.cash.amount.toLocaleString()}
                                </span>
                                <span className="font-semibold text-sm">C</span>
                            </div>

                            <Button onClick={() => setIsWithdrawModalOpen(true)} className="flex flex-row justify-normal gap-3">
                                출금 신청
                            </Button>
                        </div>
                    </CardContent>
                </Card>

                {/* 출금 계좌 관리 */}
                <Card className="w-full">
                    <CardHeader>
                        <CardTitle className="flex items-center justify-between">
                            <span className="flex items-center gap-2">
                                출금 계좌
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
                                출금 계좌 관리
                            </Button>
                        </div>
                    </CardContent>
                </Card>

                <Card className="w-full bg-gray-100">
                    <CardHeader>
                        <CardTitle className="text-red-500">
                            캐시 출금 시 유의사항
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="text-sm text-gray-600 leading-relaxed">
                            - 출금은 5000C 이상부터 가능해요. <br />
                            - 소량의 출금 수수료가 발생할 수 있어요. (500원) <br />
                            - 출금신청 후 취소는 불가하니 신중히 진행해 주세요. <br />
                            - 출금은 신청 후 즉시 처리돼요. <br />
                            - 은행 점검시간인 23:30~00:30 사이에는 출금 신청을 할 수 없어요. <br />
                            - 은행 장애 등의 사유로 출금이 실패할 경우 캐시로 다시 적립돼요. <br />
                            - 출금은 등록된 계좌로 입금되므로, 등록된 계좌가 없을 경우 본인명의 계좌를 먼저 등록해 주세요. <br />
                        </p>
                    </CardContent>
                </Card>

                {/* 출금 신청 모달 */}
                <WithdrawRequestModal
                    isOpen={isWithdrawModalOpen}
                    onClose={() => setIsWithdrawModalOpen(false)}
                    currentCash={user.cash.amount}
                    bankAccounts={bankAccounts}
                    refreshAccounts={refreshAccounts}
                    refreshPoint={refreshPoint}
                />

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