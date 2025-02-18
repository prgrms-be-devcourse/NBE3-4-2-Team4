import client from "@/lib/backend/client";

import ClientPage from "./ClientPage";

export default async function Page({
  params,
}: {
  params: { answerId: string; genFileId: string };
}) {
  const { answerId, genFileId } = await params;

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

  return <ClientPage id={answerId} genFile={genFileResponse.data} />;
}
