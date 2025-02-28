import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

export default async function Page() {
  try {
    const responseReceive = await client.GET("/api/messages/receive");
    const responseSend = await client.GET("/api/messages/send");

    // response 에러 반환 시 처리
    if (!responseReceive || !responseReceive.data || !responseSend || !responseSend.data) {
      throw new Error("API 응답이 유효하지 않습니다.");
    }

    const receiveData = convertSnakeToCamel(responseReceive.data);
    const sendData = convertSnakeToCamel(responseSend.data);

    return (<ClientPage receive={receiveData} send={sendData} />);
  } catch (error) {
    console.error("API 요청 실패:", error);

    return (
      <div className="flex justify-center items-center h-96">
        데이터를 불러오는 중 오류가 발생했습니다.
      </div>
    );
  }
}