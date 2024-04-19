import { useEffect, useRef, useState } from "react";
import {User} from "./App"
import React from "react"

async function register(name: String, password: String, setUser, server: WebSocket, errorDialogRef: React.RefObject<HTMLDialogElement>, setErrorInfo)
{
    server.send("registerUser " + name + " " + password)
    server.onmessage = function(e: MessageEvent)
    {
        if (e.data == "0")
        {
                setErrorInfo("Такой пользователь уже существует")
                errorDialogRef.current!.showModal()
        }
        else
        {
            localStorage.setItem("user", name + " " + password + " " + "0" + " " + "newbie")
            setUser(new User(name, password, 0, "newbie"))
        }
    }
}

async function logIn(name: String, password: String, setUser, server: WebSocket, errorDialogRef: React.RefObject<HTMLDialogElement>, setErrorInfo)
{
    server.send("logInUser " + name + " " + password)
    server.onmessage = function(e: MessageEvent)
    {
        if (e.data == "0")
        {
                errorDialogRef.current!.showModal()
                setErrorInfo("Пользователь не найден или пароль неверен")
        }
        else
        {
            let data = e.data.split(" ")
            localStorage.setItem("user", e.data)
            setUser(new User(data[0], data[1], data[2], data[3]))
        }
    }
}

function ErrorDialog(props: {children, dialogRef: React.RefObject<HTMLDialogElement>})
{
    return (
        <dialog ref={props.dialogRef}>
            <div style={{minWidth: "50%", minHeight: "50%", display: "flex", flexFlow: "column", justifyContent: "start", alignItems: "center", background: "radial-gradient(ellipse at 25% 25%, white 0%, magenta 5%, white 10%, magenta 20%, blue 100%)"}}>
                <h1 style={{color: "green", marginTop: "20px"}}>Ошибка</h1>
                <h2 style={{color: "green", marginTop: "20px"}}>
                    {props.children}
                </h2>
                <div style={{flex: "1"}}></div>
                <div style={{display: "flex", flexDirection: "row"}}>
                    <div style={{flex: "1"}}></div>
                    <input type="button" value="Ок" onClick={() => {props.dialogRef.current?.close();}}/>
                    <div style={{flex: "1"}}></div>    
                </div>
                <div style={{flex: "1"}}></div>
            </div>
        </dialog>
    );
}

export default function Registry(props: {server: WebSocket, setUser})
{
    var [mode, setMode] = useState("Registry");
    var [name, setName] = useState("")
    var [password, setPassword] = useState("")
    var [errorInfo, setErrorInfo] = useState("")
    var dialogRef = useRef(null)

    return (
        <div style={{display: "flex", flexDirection: "column", height: "100vh", minWidth: "100%", alignItems: "center"}}>
            <ErrorDialog dialogRef={dialogRef}>
                {errorInfo}
            </ErrorDialog>
            <div style={{display: "flex", flexDirection: "row-reverse", height: "100px", width: "100%"}}>
                <div style={{flex: "1"}}></div>
                <div onClick = {() => {setMode("Registry")}} style={{flex: "1", display: "flex", padding: "20px", justifyContent: "center", alignItems: "center", background: "radial-gradient(circle at center, black 0%, " + ((mode == "Registry") ? "green" : "purple") + " 60%, darkgray 100%)"}}>
                    <h2 style={{color: "white"}}>
                        РЕГИСТРАЦИЯ
                    </h2>
                </div>
                <div style={{flex: "1"}}></div>
                <div onClick = {() => {setMode("LogIn")}} style={{flex: "1", display: "flex", padding: "20px", justifyContent: "center", alignItems: "center", background: "radial-gradient(circle at center, black 0%, " + ((mode == "LogIn") ? "green" : "purple") + " 60%, darkgray 100%)"}}>
                    <h2 style={{color: "white"}}>
                        ВХОД
                    </h2>
                </div>
                <div style={{flex: "1"}}></div>
            </div>
            <div style={{flex: "1"}}></div>
            <input onChange={(e) => {setName(e.target.value)}} type="text" placeholder="Введите имя пользователя" value={name} style={{backgroundColor: "lightgray", width: "300px"}}></input>
            <input onChange={(e) => {setPassword(e.target.value)}} type="password" placeholder="Введите пароль" value={password} style={{backgroundColor: "darkgray", width: "300px"}}></input>
            <div style={{flex: "1"}}></div>
            <div onClick = {() => { (mode == "Registry") ? register(name, password, props.setUser, props.server, dialogRef, setErrorInfo) : logIn(name, password, props.setUser, props.server, dialogRef, setErrorInfo) }} style={{flex: "1", display: "flex", padding: "20px", width: "600px", justifyContent: "center", alignItems: "center", background: "radial-gradient(circle at center, black 0%, red 60%, darkgray 100%)"}}>
                <h2 style={{color: "white"}}>
                    <h1 style={{color: "yellow"}}>{mode == "Registry" ? "ЗАРЕГИСТРИРОВАТЬСЯ" : "ВОЙТИ"}</h1> 
                </h2>
                </div>
            <div style={{flex: "2"}}></div>
        </div>
    )
}//                <div onClick = {() => {setMode("LogIn")}} style={{flex: "1", padding: "20px", justifyContent: "center", alignItems: "center", background: "radial-gradient(circle at center, black 0%, " + (mode == "LogIn") ? "green" : "purple" + " 60%, darkgray 100%)"}}>