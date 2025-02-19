"use client";

export default function ClientPage() {
    return (
        <div className="flex flex-col gap-6">
            <h1 className="flex justify-center">카테고리 설정</h1>
            <form className="flex items-center space-x-4">
                <label className="text-gray-700 font-medium">카테고리 이름</label>
                <input
                    type="text"
                    placeholder="카테고리 입력"
                    className="flex-1 p-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <button
                    type="submit"
                    className="bg-gray-500 text-white font-semibold px-4 py-2 rounded-lg hover:bg-gray-600 transition"
                >
                    추가
                </button>
            </form>

            <h1>카테고리 목록</h1>
            <ul className="space-y-2">
                <li className="flex justify-between items-center p-2 border-b">
                    <span className="text-gray-700 font-medium">category01</span>
                    <button className="text-red-500 hover:text-red-600 transition">
                        삭제
                    </button>
                </li>
                <li className="flex justify-between items-center p-2 border-b">
                    <span className="text-gray-700 font-medium">category02</span>
                    <button className="text-red-500 hover:text-red-600 transition">
                        삭제
                    </button>
                </li>
            </ul>
        </div>
    );
}