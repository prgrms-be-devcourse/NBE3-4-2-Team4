import client from "@/lib/backend/client";

import ClientPage from "./ClientPage";
import { convertSnakeToCamel } from "@/utils/convertCase";

export default async function Page({
  params,
}: {
  params: { id: string; genFileId: string };
}) {
  const { id, genFileId } = await params;

  const genFileResponse = await client.GET(
    "/api/questions/{parentId}/genFiles/{id}",
    {
      params: {
        path: {
          parentId: Number(id),
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
    <ClientPage id={id} genFile={convertSnakeToCamel(genFileResponse.data)} />
  );
}
