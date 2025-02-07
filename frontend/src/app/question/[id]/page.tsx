import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

async function getQuestionDetail(id: string) {
    try {
        const response = await fetch(`http://localhost:8080/api/questions/${id}`);
        if (!response.ok) {
            throw new Error("API 응답이 올바르지 않습니다.");
        }
        return await response.json();
    } catch (error) {
        console.error("API 요청 실패:", error);
        return null;
    }
}

export default async function Page({ params }: { params: { id: string } }) {
    const question = await getQuestionDetail(params.id);
    const body = convertSnakeToCamel(question);
    if (!question) {
        return (
            <div className="flex justify-center items-center h-96">
                데이터를 불러오는 중 오류가 발생했습니다.
            </div>
        );
    }

    return <ClientPage question={body}/>;
}