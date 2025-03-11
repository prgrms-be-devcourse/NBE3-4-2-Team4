import ClientWrapper from "./ClientWrapper";

export default function Page({ searchParams }: { searchParams: { page?: string } }) {
  const page = searchParams.page ? Number(searchParams.page) : 1;
  return <ClientWrapper page={page} />;
}
