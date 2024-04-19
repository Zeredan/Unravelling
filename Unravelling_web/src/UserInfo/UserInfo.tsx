import { useEffect, useRef, useState } from "react";
import classes from "./Styles.module.css"
import React from "react"
import { User } from "../App";

export default function UserInfo(props: {user: User, setUser, server})
{
    return(
        <div style={{display: "flex", flexFlow: "column", height: "100vh", width: "100%", alignItems: "center", overflowY: "scroll", background: "linear-gradient(15deg, blue 0%, cyan 50%, blue 100%)"}}>
            <div style={{display: "flex", flexFlow: "row-reverse", height: "200px", width: "100%", justifyContent: "end"}}>
                <div className={classes.infoBox} style={{color: "white"}}>
                    {props.user.name}
                </div>
                <div className={classes.infoBox} style={{color: "wheat"}}>
                    {props.user.money}
                </div>
                <div class={classes.infoBox} style={{mainColor: "blue", color: "wheat"}}>
                    {props.user.status}
                </div>
            </div>
            <div onClick = {() => {localStorage.removeItem("user"); props.server.send("logOut"); props.setUser(null)}} style={{marginTop: "20px"}}>
                <h2 style={{color: "wheat", userSelect: "none"}}>
                    Выйти из аккаунта
                </h2>
            </div>
        </div>
    )
}