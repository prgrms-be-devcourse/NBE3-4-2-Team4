import ClientPage from "./ClientPage"

export default function Page() {
    return (
        <div className="container max-w-[600px] mx-auto px-4">
              <div className="mt-20 mb-10 text-center">
                <h2 className="flex items-center text-4xl font-bold justify-center gap-2">
                  질문 관리
                </h2>
              </div>
            <ClientPage />
        </div>
    );
}