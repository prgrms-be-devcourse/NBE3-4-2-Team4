 import createClient from "openapi-fetch";
 import { useRouter, usePathname } from 'next/navigation';

    const client = createClient<paths>({
      baseUrl: "http://localhost:8080",
    });

export default function AttendanceButton() {

              const router = useRouter();
              const pathname = usePathname();

        const handleAttend = async(e) => {

                   const data = await client.PUT("/api/points/attendance", {credentials: "include"});

                   if (!data.response.ok) {
                               console.log(data);
                               alert(data.error.msg);
                               return;
                               }
                           alert("출석 성공!");
                           router.replace(pathname);
                   }


    return <button
              className="bg-blue-500 text-white px-4 py-2 w-[125px] rounded-md font-semibold hover:bg-blue-600 transition"
              onClick={handleAttend}
              >
                출석
              </button>;
}