"use client"

import { useState } from "react"
import {
  Button
} from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import client from "@/lib/backend/client";
import { useRouter } from "next/navigation";
import { useRedirectIfNotAdmin } from "@/lib/hooks/useRedirect";

export default function ClientPage({categories}) {
     useRedirectIfNotAdmin();
    const [edit, setEdit] = useState(0);
    const [val, setVal] = useState("");
    const [createField, setCreateField] = useState("");
    const [createVal, setCreateVal] = useState("");

    const router = useRouter();

       const handleEdit = (id, name) => {
            setEdit(id);
          }

      const handleCreate = async () => {
               try {
                            const response = await client.POST(`/api/admin/adminAssetCategory`, {
                              body: {
                                name: createVal
                              },
                              credentials: "include",
                            });
                            console.log(response)
                            setCreateField("")
                        router.refresh()

                    } catch (error) {
                      console.error(error);
                    }
         }

      const handleSubmit = async (id: number) => {

            console.log(val)

        try {
                const response = await client.PUT(`/api/admin/adminAssetCategory/${id}`, {
                  body: {
                    name: val
                  },
                  credentials: "include",
                });
                console.log(response)
                setEdit(0)
            router.refresh()

        } catch (error) {
          console.error(error);
        }
      };


      const handleDelete = async (id: number) => {
                try {
                        const response = await client.DELETE(`/api/admin/adminAssetCategory/${id}`, {
                          credentials: "include",
                        });
                        console.log(response)

                    router.refresh()

                } catch (error) {
                  console.error(error);
                }
      };


    return <div className="container mx-auto px-4 flex flex-col gap-6">
        <h1>Admin Asset Category</h1>
        <form className="flex gap-[10px]" onSubmit={handleCreate}>
            <Input className="border" value={createVal} onChange={(e)=>setCreateVal(e.target.value)}/>
            <Button>생성</Button>
         </form>
        <ul className="flex flex-col gap-[10px]">
            {categories.map((category) => (
                <li key={category.id}>
                <div className="flex justify-between items-center">
                    <div>{category.name}</div>
                    <div className="flex gap-[10px]">
                    {category.id === edit &&
                        <form className="flex gap-[5px]">
                        <Input value={val} onChange={(e) => setVal(e.target.value)}/>
                        <Button onClick={(e) => handleSubmit(category.id)}>제출</Button>
                        </form>
                     }
                    {category.id !== edit && <Button onClick={() => handleEdit(category.id, category.name)}>수정</Button>}
                    <Button variant="destructive" onClick={()=>handleDelete(category.id)}>삭제</Button>
                    </div>
                   </div>
                </li>
            ))}
        </ul>
    </div>
}