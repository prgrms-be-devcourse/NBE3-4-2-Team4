import { cookies } from "next/headers";

import client from "@/lib/backend/client";

import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

async function getAnswer(answerId: string) {
  const res = await client.GET("/api/answers/{id}", {
    params: {
      path: {
        id: parseInt(answerId),
      },
    },
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  return res;
}

export default async function Page({
  params,
}: {
  params: { id: string; answerId: string };
}) {
  const { id, answerId } = await params;
  const answerResponse = await getAnswer(answerId);

  if (answerResponse.error) {
    return (
      <div className="flex-1 flex items-center justify-center">
        {answerResponse.error.msg}
      </div>
    );
  }

  const answer = convertSnakeToCamel(answerResponse.data);

  const genFilesResponse = await client.GET(
    "/api/answers/{answerId}/genFiles",
    {
      params: { path: { answerId: answer.id } },
      headers: {
        cookie: (await cookies()).toString(),
      },
    }
  );

  if (genFilesResponse.error) {
    return (
      <div className="flex-1 flex items-center justify-center">
        {genFilesResponse.error.msg}
      </div>
    );
  }

  const genFiles = convertSnakeToCamel(genFilesResponse.data);

  return <ClientPage answer={answer} genFiles={genFiles} />;
}
