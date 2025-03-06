import {useEffect, useState} from "react";
import client from "@/lib/backend/client";
import {toast} from "@/hooks/use-toast";
import {useRouter} from "next/navigation";

export function UseImportPayment() {
    const [isLoaded, setIsLoaded] = useState(false);
    const router = useRouter();

    // 아임포트 스크립트 실행
    useEffect(() => {
        if (typeof window !== "undefined") {
            const script = document.createElement("script");
            script.src = "https://cdn.iamport.kr/v1/iamport.js";
            script.async = true;
            script.onload = () => {
                console.log("아임포트 스크립트 로드 완료");
                setIsLoaded(true);
            };
            document.body.appendChild(script);
        }
    }, []);

    const requestPay = (
        amount: number,
        paymentMethod: string,
        user: {
            username: string;
            emailAddress: string;
        }
    ) => {
        if (!window.IMP) {
            console.error("아임포트 모듈 로딩 실패");
            return;
        }

        // 아임포트 가맹점 코드 등록
        const IMP = window.IMP;
        IMP.init("imp44456407");

        // 결제 주문 번호 등록 (format : order_{currTime})
        const merchantUid = `order_${new Date().getTime()}`;

        // 결제 데이터 등록
        const paymentData = {
            pg: "tosspayments.iamporttest_3",
            pay_method: paymentMethod,
            amount: Math.max(1000, amount),
            merchant_uid: merchantUid,
            name: "캐시 충전",
            buyer_name: user.username,
            buyer_email: user.emailAddress,
            locale: "ko",
        };

        console.log("결제 요청 데이터:", paymentData);

        IMP.request_pay(paymentData, async (response: any) => {
            console.log("결제 응답 데이터:", response);

            try {
                // 결제 승인 검증
                const verifyResponse = await client.POST("/api/payments/verify", {
                   body: {
                       imp_uid: response.imp_uid,
                       amount: amount
                   }
                });

                console.log("검증 결과:", verifyResponse);

                if (verifyResponse.response.ok) {

                    const verifiedData = verifyResponse.data.data;

                    // 캐시 충전 처리
                    const depositCashResponse = await client.PATCH("/api/asset/deposit", {
                        body: {
                            amount,
                            assetType: "캐시",
                            assetCategory: "CASH_DEPOSIT"
                        },
                    });

                    console.log("캐시 충전 결과:", depositCashResponse);

                    if (depositCashResponse.response.ok) {

                        const assetHistoryId = Number(depositCashResponse?.data?.data) || 0;

                        // 결제 내역 저장
                        const writePaymentResponse = await client.POST("/api/payments", {
                            body: {
                                asset_history_id: assetHistoryId,
                                imp_uid: verifiedData.imp_uid,
                                merchant_uid: verifiedData.merchant_uid,
                                amount: verifiedData.amount,
                                status: verifiedData.status.toUpperCase()
                            },
                        });

                        console.log("결제 내역 저장 결과:", writePaymentResponse);

                        if (writePaymentResponse.response.ok) {
                            toast({
                                title: "결제가 정상적으로 처리되었습니다!",
                                description: `결제 금액: ${verifiedData.amount}원`
                            });

                            router.push("/point/list");
                            router.refresh();

                        } else {
                            toast({
                                title: "결제 내역 저장 실패!",
                                description: "결제 내역 저장에 실패했습니다. 관리자에게 문의하세요."
                            });
                        }

                    } else {
                        toast({
                            title: "캐시 적립 실패!",
                            description: "캐시 적립에 실패했습니다. 관리자에게 문의하세요."
                        });
                    }

                } else {
                    toast({
                        title: "결제 실패!",
                        description: "결제 검증에 실패했습니다. 관리자에게 문의하세요."
                    });
                }

            } catch (error) {
                console.error("검증 요청 실패:", error);
                toast({
                    title: "결제 실패!",
                    description: `결제가 실패했습니다. 관리자에게 문의하세요.`
                });
            }
        });
    };

    return {requestPay, isLoaded};
}