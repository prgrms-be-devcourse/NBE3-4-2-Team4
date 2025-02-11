import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

export default async function Page({
  params,
}: {
  params: Promise<{ id: string; answerId: string }>;
}) {
  const param = await params;
  const responseAnswer = await client.GET("/api/answers/{id}", {
    params: {
      path: {
        id: Number(param.answerId),
      },
    },
  });
  const answer = convertSnakeToCamel(responseAnswer.data) ?? {
    id: 0,
    createdAt: "",
    modifiedAt: "",
    questionId: 0,
    authorId: 0,
    authorName: "",
    content: "",
    selected: false,
  };

  return <ClientPage params={param} answer={answer} />;
}
