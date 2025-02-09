"use client"
import createClient from "openapi-fetch";
import { useState } from "react";
import { useRouter, usePathname } from 'next/navigation';

    const client = createClient<paths>({
      baseUrl: "http://localhost:8080",
    });

export default function TransferForm() {

    const [username, setUsername] = useState("");
    const [amount, setAmount] = useState(0);

      const router = useRouter();
      const pathname = usePathname();

    const handleTransfer = async(e) => {
        console.log(amount);
        if (!amount || amount < 0) {
            alert("0보다 큰수를 입력해주세요");
            e.preventDefault();
            return;
        }

        if (username.trim() === "") {
            alert("유저네임을 입력하세요.");
            e.preventDefault();
            return;
        }


        const response = await client.PUT("/api/points/transfer", {
          body: {
              username: username,
              amount: Number(amount)
          }
        });

        if (!response.ok){
                    console.log(response);
                    alert("송금 실패");
                    return;
                    }


        alert("송금 성공!!");
        setUsername("");
        setAmount(0);
        router.replace(pathname);
    }

    return <div>
    <h1 className="mb-2">포인트 송금</h1>
    <div style={{ display: "flex", justifyContent: "flex", gap: "1rem" }}>

            <input type="text" placeholder="포인트 송금할 대상"
                   className="border-2 border-gray-300 px-2 rounded-md focus:outline-none focus:border-blue-500"
                   value={username}
                   onChange={(e) => setUsername(e.target.value)}/>
            <input type="number" placeholder="포인트 금액을 입력하세요"
                   className="border-2 border-gray-300 px-2 rounded-md focus:outline-none focus:border-blue-500"
                   value={amount}
                   onChange={(e) => setAmount(e.target.value)}/>
            <button
               className="border-2 border-blue-500 bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600"
               onClick={handleTransfer}>
                   송금</button>
    </div>
    </div>
}