"use client";
import { useState } from "react";

export default function ClientPage() {
  const [activeTab, setActiveTab] = useState('received'); // 탭 상태 관리 (받은 쪽지/보낸 쪽지)

  // 샘플 데이터 (받은 쪽지 및 보낸 쪽지)
  const receivedMessages = [
    { id: 1, title: '안녕하세요!', sender: '홍길동', content: '오늘 날씨 좋네요!', date: '2025-02-28' },
    { id: 2, title: '회의 일정 확인', sender: '김영희', content: '다음 주 회의 일정 확인 부탁드립니다.', date: '2025-02-27' },
    { id: 3, title: '프로젝트 업데이트', sender: '이철수', content: '프로젝트 진행 상황에 대해 업데이트 드립니다.', date: '2025-02-26' },
  ];

  const sentMessages = [
    { id: 1, title: '프로젝트 진행', receiver: '이철수', content: '프로젝트 진행 상황에 대한 보고 드립니다.', date: '2025-02-28' },
    { id: 2, title: '회의 일정 조정', receiver: '김영희', content: '회의 일정을 조정하려고 합니다.', date: '2025-02-27' },
    { id: 3, title: '새로운 기능 제안', receiver: '홍길동', content: '새로운 기능에 대한 제안을 드립니다.', date: '2025-02-26' },
  ];

  return (
    <div className="container mx-auto px-4">
      <h1 className="text-xl font-semibold mb-4">쪽지 목록</h1>

      {/* 탭 영역 */}
      <div className="flex space-x-4 mb-4">
        <button
          className={`py-2 px-4 rounded-t-lg ${activeTab === 'received' ? 'bg-gray-700 text-white' : 'bg-gray-200'}`}
          onClick={() => setActiveTab('received')}
        >
          받은 쪽지
        </button>
        <button
          className={`py-2 px-4 rounded-t-lg ${activeTab === 'sent' ? 'bg-gray-700 text-white' : 'bg-gray-200'}`}
          onClick={() => setActiveTab('sent')}
        >
          보낸 쪽지
        </button>
      </div>

      {/* 탭 내용 영역 */}
      <div className="px-4 border border-t-0 rounded-b-lg">
        {activeTab === 'received' ? (
          <div>
            {/* 받은 쪽지 목록 */}
            {receivedMessages.map((message) => (
              <div key={message.id} className="border-b py-2">
                <h3 className="font-medium">{message.title}</h3>
                <p className="text-sm text-gray-500">보낸 사람: {message.sender}</p>
                <p className="text-sm">{message.content}</p>
                <p className="text-xs text-gray-400">{message.date}</p>
              </div>
            ))}
          </div>
        ) : (
          <div>
            {/* 보낸 쪽지 목록 */}
            {sentMessages.map((message) => (
              <div key={message.id} className="border-b py-2">
                <h3 className="font-medium">{message.title}</h3>
                <p className="text-sm text-gray-500">받는 사람: {message.receiver}</p>
                <p className="text-sm">{message.content}</p>
                <p className="text-xs text-gray-400">{message.date}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}