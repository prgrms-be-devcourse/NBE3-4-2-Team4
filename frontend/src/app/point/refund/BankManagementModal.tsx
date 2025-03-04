"use client";

import {useState, useEffect} from "react";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Card, CardContent} from "@/components/ui/card";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {useToast} from "@/hooks/use-toast";
import client from "@/lib/backend/client";
import {components} from "@/lib/backend/apiV1/schema";

type GetBanks = components["schemas"]["GetBanks"];

export function BankManagementModal({isOpen, onClose, bankAccounts, refreshAccounts}: {
    isOpen: boolean;
    onClose: () => void;
    bankAccounts: {
        bankAccountId: number;
        bankName: string;
        maskedAccountNumber: string;
        accountHolder: string;
        nickname: string;
    }[];
    refreshAccounts: () => void;
}) {
    const [nickname, setNickname] = useState("");
    const [newBank, setNewBank] = useState("");
    const [newAccountNum, setNewAccountNum] = useState("");
    const [accountHolder, setAccountHolder] = useState("");
    const [bankOptions, setBankOptions] = useState<GetBanks[]>([]);
    const [loading, setLoading] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [seletedBankAccountId, setSeletedBankAccountId] = useState<number | null>(null);
    const { toast } = useToast();

    // 은행 목록 API 호출
    useEffect(() => {
        const fetchBanks = async () => {
            try {
                const response = await client.GET("/api/banks");

                if (!response || !response.data) {
                    throw new Error("은행 목록 조회 API의 응답이 유효하지 않습니다.");
                }

                setBankOptions(response.data.data);
            } catch (error) {
                console.error("은행 목록을 불러오는 중 오류 발생:", error);
            }
        };

        fetchBanks();
    }, []);

    // 계좌 추가 API 호출
    const handleAddAccount = async () => {
        if (!newBank || !newAccountNum || !accountHolder) {
            toast({
                title: "은행, 계좌번호, 예금주는 필수 입력 항목입니다."
            });
            return;
        }

        try {
            setLoading(true);
            const response = await client.POST("/api/banks/accounts", {
                body: {
                    bank_code: newBank,
                    account_number: newAccountNum,
                    account_holder: accountHolder,
                    nickname: nickname || null,
                }
            });

            const responseData = response?.data?.data ?? null;

            if (!responseData) {
                throw new Error("계좌를 등록하는데 실패했습니다.")
            }

            toast({
                title: "환급 계좌가 성공적으로 등록 되었습니다!",
            });

            // 입력 form 초기화
            setNewBank("");
            setNewAccountNum("");
            setAccountHolder("");
            setNickname("");

            refreshAccounts(); // 계좌 목록 새로고침
        } catch (error) {
            console.error("계좌 등록 중 오류 발생:", error);
            alert("계좌 등록 중 오류가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    };

    // 계좌 삭제 API 호출
    const handleDeleteAccount = async () => {
        try {
            if (!seletedBankAccountId) {
                return;
            }
            setIsDeleting(true);

            const response = await client.DELETE(`/api/banks/accounts/${seletedBankAccountId}`);
            if (response.data) {
                toast({title: "계좌가 성공적으로 삭제되었습니다."});
                refreshAccounts();

            } else {
                throw new Error("계좌 삭제 실패");
            }
        } catch (error) {
            console.error("계좌 삭제 중 오류 발생:", error);
            toast({title: "계좌 삭제 중 오류가 발생했습니다."});
        } finally {
            setIsDeleting(false);
            setSeletedBankAccountId(null);
        }
    };

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="overflow-hidden">
                <DialogHeader>
                    <DialogTitle>환급 계좌 관리</DialogTitle>
                    <DialogDescription>
                        환급 계좌를 관리해보세요.
                    </DialogDescription>
                </DialogHeader>

                {/* 계좌 목록 */}
                <Card className="w-full">
                    <CardContent className="mt-4 max-h-60 overflow-y-auto">
                        {bankAccounts.length > 0 ? (
                            <ul className="space-y-2 text-gray-700">
                                {bankAccounts.map((account) => (
                                    <li
                                        key={account.bank_account_id}
                                        className="p-2 bg-gray-100 rounded-md flex justify-between items-center"
                                    >
                                        <div>
                                            <span className="font-medium block">
                                              [ {account.nickname} ]
                                            </span>
                                            <p className="text-xs text-gray-500">
                                                ({account.bank_name}) {account.masked_account_number}
                                            </p>
                                        </div>
                                        <Dialog>
                                            <DialogTrigger asChild>
                                                <Button
                                                    onClick={() => setSeletedBankAccountId(account.bank_account_id)}
                                                    variant="destructive"
                                                    size="sm"
                                                    disabled={loading}
                                                >
                                                    삭제
                                                </Button>
                                            </DialogTrigger>
                                            <DialogContent>
                                                <DialogTitle>정말 삭제하시겠습니까?</DialogTitle>
                                                <DialogDescription>
                                                    해당 계좌를 삭제하면 되돌릴 수 없습니다. 정말 삭제하시겠습니까?
                                                </DialogDescription>
                                                <div className="flex justify-end gap-4 mt-4">
                                                    <Button variant="outline" onClick={() => setSeletedBankAccountId(null)}>
                                                        취소
                                                    </Button>
                                                    <Button
                                                        variant="destructive"
                                                        onClick={handleDeleteAccount}
                                                        size="sm"
                                                        disabled={isDeleting}
                                                    >
                                                        {isDeleting ? "삭제 중..." : "삭제"}
                                                    </Button>
                                                </div>
                                            </DialogContent>
                                        </Dialog>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p className="text-gray-400 text-center">등록된 계좌가 없습니다.</p>
                        )}
                    </CardContent>
                </Card>

                {/* 계좌 등록 */}
                <Card className="shadow-sm border border-gray-200">
                    <CardContent className="flex flex-col space-y-4 mt-4">
                        <div>
                            <Label>은행명 *</Label>
                            <Select onValueChange={(value) => setNewBank(value)} value={newBank} disabled={loading}>
                                <SelectTrigger className="w-full">
                                    <SelectValue placeholder="은행 선택"/>
                                </SelectTrigger>
                                <SelectContent>
                                    {bankOptions.map((bank) => (
                                        <SelectItem key={bank.bank_code} value={bank.bank_code}>
                                            {bank.bank_name}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                        <div>
                            <Label>계좌번호 *</Label>
                            <Input
                                value={newAccountNum}
                                onChange={(e) => setNewAccountNum(e.target.value)}
                                placeholder="계좌번호 입력"
                                disabled={loading}
                            />
                        </div>
                        <div>
                            <Label>예금주 *</Label>
                            <Input
                                value={accountHolder}
                                onChange={(e) => setAccountHolder(e.target.value)}
                                placeholder="예금주 입력"
                                disabled={loading}
                            />
                        </div>
                        <div>
                            <Label>별칭 (선택)</Label>
                            <Input
                                value={nickname}
                                onChange={(e) => setNickname(e.target.value)}
                                placeholder="계좌 별칭 입력"
                                disabled={loading}
                            />
                        </div>
                        <Button onClick={handleAddAccount} className="w-full mt-4" disabled={loading}>
                            {loading ? "등록 중..." : "계좌 등록"}
                        </Button>
                    </CardContent>
                </Card>
            </DialogContent>
        </Dialog>
    );
}