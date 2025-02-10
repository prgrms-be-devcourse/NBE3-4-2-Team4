 import createClient from "openapi-fetch";

    const client = createClient<paths>({
      baseUrl: "http://localhost:8080",
    });

export default function AttendanceButton() {

        const handleAttend = async(e) => {

                   const data = await client.PUT("/api/points/attendance", {});

                   if (!data.response.ok) {
                               console.log(data);
                               alert("출석 실패");
                               return;
                               }
                           alert("출석 성공!");
                   }


    return <button
              className="bg-blue-500 text-white px-4 py-2 w-[125px] rounded-md font-semibold hover:bg-blue-600 transition"
              onClick={handleAttend}
              >
                출석
              </button>;
}