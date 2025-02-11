"use client";

import { format } from "date-fns";
import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import TransferForm from "./TransferForm";
import AttendanceButton from "./AttendanceButton";


type PageDtoPointHistoryRes = components["schemas"]["PageDtoPointHistoryRes"];
type PointHistoryRes = components["schemas"]["PointHistoryRes"];



function formatDate(date: string) {
  return format(new Date(date), "yyyy년 MM월 dd일 HH:mm:ss");
}

export default function ClientPage({ body }) {


      const router = useRouter();
      const searchParams = useSearchParams();
      const data : PointHistoryRes = body.data;

      const currentPage = Number(searchParams.get("page")) || 1;
      const selectedCategory = String(searchParams.get("pointCategory") || "");



        // 페이지 이동 함수
        const changePage = (newPage: number) => {
          const queryParams = new URLSearchParams(searchParams.toString());
          queryParams.set("page", newPage.toString());

          router.push(`?${queryParams.toString()}`);
        };


      const handleCategoryChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newCategory = event.target.value;

        const queryParams = new URLSearchParams();

        if (newCategory) {
          queryParams.set("pointCategory", newCategory);
        } else {
          queryParams.delete("pointCategory");
        }

        router.push(`?${queryParams.toString()}`);
      };


    //날짜 부분

    const today = new Date().toISOString().split("T")[0];

    const pastDate = new Date();
    pastDate.setDate(pastDate.getDate() - 30);
    const formattedPastDate = pastDate.toISOString().split("T")[0];


    const initialStartDate = searchParams.get("startDate") || formattedPastDate;
    const initialEndDate = searchParams.get("endDate") || today;



    const [startDate, setStartDate] = useState(initialStartDate);
    const [endDate, setEndDate] = useState(initialEndDate);


    const handleEndDateChange = (value: string) => {
        setEndDate(value);
    }

    const handleStartDateChange = (value: string) => {
        setStartDate(value);
      }



    const applyFilters = (e) => {
      const queryParams = new URLSearchParams(searchParams.toString());

        const start = new Date(startDate);
        const end = new Date(endDate);
          const today = new Date();


          if (end > today) {
              e.preventDefault();
              alert("끝날짜가 현재 이후일 수 없습니다");
              return;
          }

        const diffDays = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));

        if (diffDays < 30) {
            e.preventDefault();
            alert("시작과 끝날짜가 30일 이상이어야 합니다");
            return;
          }

        queryParams.set('startDate', startDate);
        queryParams.set('endDate', endDate);
        queryParams.set('page', 1);

      router.push(`?${queryParams.toString()}`);
    };


    console.log(currentPage, data.hasMore);
  return (<div  className="container mx-auto px-4 flex flex-col gap-7">
      <div className="flex justify-between mt-[10px]">
      <h1 className="my-3">포인트 페이지</h1>
      <AttendanceButton/>
    </div>
             <TransferForm/>
            <div className="flex flex-col gap-4">
              {/* 시작 날짜 선택 */}
              <div>
                <label htmlFor="startDate" className="block font-semibold mb-1">
                  시작 날짜
                </label>
                <input
                  id="startDate"
                  type="date"
                  className="border rounded-md px-3 py-2"
                  value={startDate}
                  onChange={(e) => handleStartDateChange(e.target.value)}
                  max={today} // 시작 날짜는 오늘까지 선택 가능
                />
              </div>

              {/* 끝 날짜 선택 */}
              <div>
                <label htmlFor="endDate" className="block font-semibold mb-1">
                  끝 날짜
                </label>
                <input
                  id="endDate"
                  type="date"
                  className="border rounded-md px-3 py-2"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                  min={startDate} // 끝 날짜는 시작 날짜 이후여야 함
                  max={today} // 끝 날짜는 오늘을 초과할 수 없음
                />
              </div>

              {/* 적용 버튼 */}
              <button
                className="bg-blue-500 text-white px-4 py-2 w-[125px] rounded-md font-semibold hover:bg-blue-600 transition"
                onClick={applyFilters}
              >
                날짜 적용
              </button>
            </div>
        <div className="flex flex-col gap-2">
          <label htmlFor="pointCategory" className="font-semibold">
            포인트 카테고리 선택
          </label>
          <select
            id="pointCategory"
            className="border rounded-md px-3 py-2"
            value={selectedCategory}
            onChange={handleCategoryChange}
          >
            <option value="">전체</option>
            <option value="ANSWER">답변채택</option>
            <option value="PURCHASE">구매</option>
            <option value="TRANSFER">송금</option>
            <option value="ATTENDANCE">출석</option>
            <option value="ADMIN">관리자</option>
          </select>
        </div>

    <ul className="flex flex-col gap-4 mx-3">
        {
            body.data.items?.map((item : PointHistoryRes, index:number) => (
                <li key={index}
                className="flex items-center justify-between border-2 border-gray-300 px-5 py-5 rounded-md mb-5">
                    <div>
                        <div className="text-3xl mb-3">{item.amount >= 0 ? "+" : ""}{item.amount}</div>
                        <div>{item.pointCategory}</div>
                        <div>{formatDate(item.createdAt)}</div>
                    </div>

                    <div>
                        <div>{item.counterAccountUsername ? "수신자" : ""} {item.counterAccountUsername}</div>
                    </div>
                </li>
            ))
        }
    </ul>

    {/* 페이지 이동 버튼 */}
          <div className="flex justify-center gap-2">
            <button onClick={() => changePage(currentPage - 1)} disabled={currentPage === 1}
              className={`px-4 py-2 rounded-md text-white font-semibold transition ${
                currentPage === 1
                  ? "bg-gray-300 cursor-not-allowed" // 이전, 다음 페이지 없을 시 비활성화
                  : "bg-blue-500 hover:bg-blue-600"
              }`}
              >
              이전
            </button>
            <button
                onClick={() => changePage(currentPage + 1)} disabled={currentPage === body.total_pages}
                disabled={!data.hasMore}
              className={`px-4 py-2 rounded-md text-white font-semibold transition ${
                !data.hasMore
                  ? "bg-gray-300 cursor-not-allowed disabled"
                  : "bg-blue-500 hover:bg-blue-600"
              }`}
              >
              다음
            </button>
          </div>

  </div>);
}