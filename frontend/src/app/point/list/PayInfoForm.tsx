"use client";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import {Coins, Wallet} from "lucide-react";
import {useRouter} from "next/navigation";

export default function PayInfoForm({point, cash}: { point: number, cash: number }) {

    const router = useRouter();

    const withdrawPoint = () => {
        router.push("/point/withdrawal");
    };

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
                        <span className="font-semibold text-2xl">{point.toLocaleString()} POINT</span>
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
                            <Wallet size={35}/> 캐시
                        </span>
                    </CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col gap-3">
                    <div className="text-left pb-3">
                        <span className="font-semibold text-2xl">{cash.toLocaleString()} CASH</span>
                    </div>

                    <div className="flex flex-row justify-normal gap-3">
                        <Button className="w-1/2" onClick={"#"}>충전하기</Button>
                        <Button className="w-1/2" onClick={"#"}>환불하기</Button>
                    </div>
                </CardContent>
            </Card>

        </div>
    );
}