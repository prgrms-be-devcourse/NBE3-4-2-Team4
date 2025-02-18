import client from "@/lib/backend/client";

import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

export default async function Page({
  params,
}: {
  params: { id: string; answerId: string; genFileId: string };
}) {
  const { id, answerId, genFileId } = await params;

  const genFileResponse = await client.GET(
    "/api/answers/{answerId}/genFiles/{id}",
    {
      params: {
        path: {
          answerId: Number(answerId),
          id: Number(genFileId),
        },
      },
    }
  );

  if (genFileResponse.error) {
    return (
      <div className="flex-1 flex items-center justify-center">
        {genFileResponse.error.msg}
      </div>
    );
  }

  return (
    <ClientPage
      id={id}
      answerId={answerId}
      genFile={convertSnakeToCamel(genFileResponse.data)}
    />
  );
}
