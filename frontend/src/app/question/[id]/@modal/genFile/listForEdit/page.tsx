import { cookies } from "next/headers";

import client from "@/lib/backend/client";

import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

async function getQuestion(id: string) {
  const res = await client.GET("/api/questions/{id}", {
    params: {
      path: {
        id: parseInt(id),
      },
    },
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  return res;
}

export default async function Page({ params }: { params: { id: string } }) {
  const { id } = await params;
  const questionResponse = await getQuestion(id);

  if (questionResponse.error) {
    return (
      <div className="flex-1 flex items-center justify-center">
        {questionResponse.error.msg}
      </div>
    );
  }

  const question = convertSnakeToCamel(questionResponse.data);

  const genFilesResponse = await client.GET(
    "/api/questions/{parentId}/genFiles",
    {
      params: { path: { parentId: question.id } },
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

  return <ClientPage question={question} genFiles={genFiles} />;
}
