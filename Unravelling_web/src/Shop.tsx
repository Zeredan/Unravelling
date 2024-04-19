import { useEffect, useRef, useState } from "react";
import React from "react"

export default function Shop()
{
    var [inc, setInc] = useState(0)
    var dr = useRef<HTMLDialogElement>(null)
    return (
        <div>
            <dialog ref = {dr}>
                dialog({inc})
                <div onClick = {() => {dr.current!.close()}}style={{backgroundColor: "greenyellow"}}>
                    close dialog
                </div>    
            </dialog>
            <div onClick = {() => {setInc((prev) => {return prev+1}); dr.current!.showModal()}}style={{backgroundColor: "greenyellow"}}>
                call dialog
            </div>
        </div>
    )
}