import ClientPage from "./ClientPage";

export default async function Page({
  params,
}: {
  params: Promise<{ id: string; answerId: string }>;
}) {
  const param = await params;

  return <ClientPage params={param} />;
}
