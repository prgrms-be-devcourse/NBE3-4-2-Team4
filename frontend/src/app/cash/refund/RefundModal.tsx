"use client";
import { useState, useEffect } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { useToast } from "@/hooks/use-toast";
import client from "@/lib/backend/client";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import {useRouter} from "next/navigation";

export function RefundModal({ isOpen, onClose, user }: {
    isOpen: boolean;
    onClose: () => void;
    user: {
        username: string;
        emailAddress: string;
        cash: number;
    };
}) {
    const { toast } = useToast();
    const [refundReason, setRefundReason] = useState("");
    const [selectedPayment, setSelectedPayment] = useState<string | null>(null);
    const [paymentHistory, setPaymentHistory] = useState([]);
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);
    const [loading, setLoading] = useState(false);
    const router = useRouter();

    // 환불 가능한 결제 내역 불러오기 (페이징 처리)
    useEffect(() => {
        if (isOpen) {
            setPaymentHistory([]); // 모달이 열릴 때 기존 데이터 초기화
            setPage(1);
            setHasMore(true);
            fetchPayments(1);
        }
    }, [isOpen, user.username]);

    const fetchPayments = async (pageNumber: number) => {
        if (loading) return; // 로딩 중이면 중복 요청 방지

        try {
            setLoading(true);
            const response = await client.GET("/api/payments", {
                params: {
                    query: {
                        page: pageNumber,
                        page_size: 5,
                        paymentStatus: "PAID"
                    },
                },
            });

            const responseData = response?.data?.data;
            if (!responseData) {
                console.error("결제 내역 API 응답 오류:", response);
                setHasMore(false);
                return;
            }

            console.log("결제 내역:", responseData);

            if (responseData.items.length === 0) {
                setHasMore(false); // ✅ 더 이상 불러올 데이터가 없으면 false
            } else {
                setPaymentHistory((prev) => [...prev, ...responseData.items]); // ✅ 기존 데이터에 추가
                setPage(pageNumber + 1); // ✅ 다음 페이지 설정
            }
        } catch (error) {
            console.error("결제 내역 불러오기 실패:", error);
            toast({
                title: "결제 내역을 불러오는 중 오류 발생",
                description: "네트워크 상태를 확인해주세요.",
                variant: "destructive",
            });
        } finally {
            setLoading(false);
        }
    };

    const handleRefundRequest = async () => {
        if (!selectedPayment) {
            toast({
                title: "환불할 결제를 선택해주세요.",
                variant: "destructive"
            });
            return;
        }

        if (!refundReason.trim()) {
            toast({
                title: "환불 사유를 입력해주세요.",
                variant: "destructive"
            });
            return;
        }

        // payment_id를 기반으로 결제 정보 가져오기
        const selectedPaymentData = paymentHistory.find(
            (payment) => payment.payment_id === selectedPayment
        );

        if (!selectedPaymentData) {
            toast({
                title: "선택한 결제 정보를 찾을 수 없습니다.",
                variant: "destructive"
            });
            return;
        }

        try {
            // 환불 요청 승인
            const refundVerifyResponse = await client.POST("/api/payments/cancel", {
                body: {
                    imp_uid: selectedPaymentData.imp_uid,
                    merchant_uid: selectedPaymentData.merchant_uid,
                    amount: selectedPaymentData.amount,
                    reason: refundReason
                },
            });

            console.log("검증 결과:", refundVerifyResponse);

            if (refundVerifyResponse.response.ok) {

                // 결제 내역 내 결제 상태 변경
                const cancelResponse = await client.PATCH(`/api/payments/${selectedPaymentData.payment_id}`, {
                    body: {
                        status: "CANCELED"
                    },
                });
                
                console.log("결제 취소 상태 조회 결과: ", cancelResponse);

                if (cancelResponse.response.ok) {

                    const refundData = cancelResponse?.data?.data;

                    // 캐시 차감
                    const refundResponse = await client.PATCH(`/api/asset/refund`, {
                        body: {
                            amount: refundData.amount,
                            assetType: "캐시",
                            assetCategory: "CASH_REFUND"
                        },
                    });

                    console.log("캐시 차감 결과: ", refundResponse);

                    if (refundResponse.response.ok) {
                        toast({
                            title: "환불 처리 되었습니다!",
                            description: `환불 금액 : ${refundData.amount}원`
                        });

                        setRefundReason("");
                        setSelectedPayment(null);
                        setPaymentHistory([]);
                        setPage(1);
                        setHasMore(true);

                        onClose();
                        router.push("/point/list");
                        router.refresh();

                    } else {
                        toast({
                            title: "환불 처리 실패!",
                            description: "환불 처리에 실패했습니다. 관리자에게 문의하세요."
                        });
                    }

                } else {
                    toast({
                        title: "결제 취소 상태 조회 실패!",
                        description: "결제 취소 상태를 조회하는데 실패했습니다. 관리자에게 문의하세요."
                    });
                }

            } else {
                toast({
                    title: "환불 요청 실패!",
                    description: "환불 요청에 실패했습니다. 관리자에게 문의하세요.",
                    variant: "destructive"
                });
            }
        } catch (error) {
            console.error("환불 요청 중 오류 발생:", error);
            toast({
                title: "환불 요청 실패!",
                description: "환불 요청에 실패했습니다. 관리자에게 문의하세요.",
                variant: "destructive"
            });
        }
    };

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="max-w-lg p-6">
                <DialogHeader>
                    <DialogTitle className="text-center text-xl font-bold mb-3">캐시 환불 요청</DialogTitle>
                </DialogHeader>

                {/* ✅ 환불할 결제 선택 (페이징 적용) */}
                <div>
                    <p className="text-sm text-gray-500 mb-2">환불할 결제 선택</p>
                    <Select onValueChange={setSelectedPayment} value={selectedPayment || ""}>
                        <SelectTrigger className="w-full">
                            <SelectValue placeholder="결제를 선택하세요." />
                        </SelectTrigger>
                        <SelectContent>
                            {paymentHistory.length > 0 ? (
                                <>
                                    {paymentHistory.map((payment) => (
                                        payment.payment_id ? ( // ✅ payment_id 검증 추가
                                            <SelectItem key={payment.payment_id} value={payment.payment_id}>
                                                [ {payment.created_at} ] - {payment.amount.toLocaleString()}원 결제
                                            </SelectItem>
                                        ) : (
                                            console.error("❌ 잘못된 결제 데이터:", payment)
                                        )
                                    ))}
                                    {/* 더보기 버튼 추가 (데이터가 더 있을 때만 표시) */}
                                    {hasMore && !loading && (
                                        <Button variant="outline" className="w-full mt-2" onClick={() => fetchPayments(page)}>
                                            더보기
                                        </Button>
                                    )}
                                    {loading && <p className="text-gray-400 text-center mt-2">로딩 중...</p>}
                                </>
                            ) : (
                                <SelectItem value="none" disabled>
                                    환불 가능한 결제가 없습니다.
                                </SelectItem>
                            )}
                        </SelectContent>
                    </Select>
                </div>

                {/* 환불 사유 입력 */}
                <div>
                    <p className="text-sm text-gray-500 mb-2">환불 사유</p>
                    <Textarea
                        className="w-full min-h-[150px] resize-none overflow-hidden"
                        placeholder="환불 사유를 입력해주세요."
                        value={refundReason}
                        onChange={(e) => setRefundReason(e.target.value)}
                    />
                </div>

                {/* 환불 요청 & 취소 버튼 */}
                <div className="flex gap-2 mt-4">
                    <Button className="w-full" onClick={handleRefundRequest}>환불 요청</Button>
                    <Button className="w-full" variant="outline" onClick={onClose}>취소</Button>
                </div>
            </DialogContent>
        </Dialog>
    );
}