"use client";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import {CircleDollarSign, Coins} from "lucide-react";
import {useRouter} from "next/navigation";

export default function PayInfoForm({
                                        user
}: {
    user: {
        username: string;
        emailAddress: string;
        point: number;
        cash: number;
    };
}) {

    const router = useRouter();

    // 캐시 관리 페이지 이동
    const moveCashPage = () => {
        router.push("/cash");
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
                        <span className="font-semibold text-2xl">{user.point.toLocaleString()} POINT</span>
                    </div>
                </CardContent>
            </Card>
            <Card className="w-1/2">
                <CardHeader>
                    <CardTitle className="flex items-center justify-between">
                        <span className="flex items-center gap-2">
                            <CircleDollarSign size={35}/> 캐시
                        </span>
                    </CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col gap-3">
                    <div className="text-left pb-3">
                        <span className="font-semibold text-2xl">{user.cash.toLocaleString()} CASH</span>
                    </div>

                    <div className="flex flex-row justify-normal gap-3">
                        <Button className="w-full" onClick={moveCashPage}>
                            캐시 충전하러 가기
                        </Button>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
}