"use Client";

export default function ClientPage() {
    return (
        <div className="container mx-auto px-4">
            <h2 className="text-2xl font-bold mb-2">글쓰기</h2>
            <hr className="mb-6" />

            {/* 제목 입력 */}
            <label className="block text-lg font-semibold mb-2">제목</label>
            <input
                type="text"
                className="w-full p-2 border rounded-md mb-4 dark:bg-gray-800 dark:border-gray-700 dark:text-white"
                placeholder="제목을 입력하세요"
                // value={title}
                // onChange={(e) => setTitle(e.target.value)}
            />

            {/* 내용 입력 */}
            <label className="block text-lg font-semibold mb-2">내용</label>
            <textarea
                className="w-full p-2 border rounded-md h-40 resize-none mb-4 dark:bg-gray-800 dark:border-gray-700 dark:text-white"
                placeholder="내용을 입력하세요"
                // value={content}
                // onChange={(e) => setContent(e.target.value)}
            />

            {/* 카테고리 설정 */}
            <label className="block text-lg font-semibold mb-2">카테고리</label>

            {/* 작성 버튼 */}
            <button
                // onClick={handleSubmit}
                className="p-3 bg-teal-500 text-white font-bold py-2 rounded-md hover:bg-teal-600"
            >
                작성하기
            </button>

        </div>
    );
}