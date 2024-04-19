import { useEffect, useRef, useState } from "react";
import NavigationRail from "./NavigationRail.tsx";
import GameRules from "./GameRules.tsx";
import Shop from "./Shop.tsx";
import GameSelection from "./GameSelectionFolder/GameSelection.tsx";
import TheGame from "./TheGame.tsx";
import UserInfo from "./UserInfo/UserInfo.tsx";
import Registry from "./Registry.tsx";

  var [repulsorX, repulsorY] = [0, 0]
  var [lightAccelerationX, lightAccelerationY] = [0, 0]
  var [lightSpeedX, lightSpeedY] = [0, 0]
  var [tLightX, tLightY] = [100, 100]
  var TleftBorder = 200

  var DessepativeStrength = 0.01
  var MaxStrength = 0.09
  var MaxRadius = 50
  var attraction = false

  export class User
  {
    name = ""
    password = ""
    money = 0
    status = "newbie"

    constructor(name, password, money, status)
    {
      this.name = name
      this.password = password
      this.money = money
      this.status = status
    }
  }

  var server = new WebSocket("ws://localhost:8080/")

  function App() {
    //#region lightStates and screen var
    var [lightX, setlightX] = useState(100)
    var [lightY, setlightY] = useState(100)
    var [lightColor, setlightColor] = useState("red")
    var [leftBorder, setLeftBorder] = useState(200)
    var mainScreen = useRef()
    //#endregion

    //#region selectedContent
    var [selectedIndex, setselectedIndex] = useState(2)
    var [gameState, setGameState] = useState("")
    var [user, setUser] = useState(null)
    let onCloseFun = () => {
      var newServer = new WebSocket("ws://localhost:8080/")
      newServer.onmessage = server.onmessage
      server = newServer
      server.onclose = onCloseFun
    }
    //var [server1, setServer1] = useState(new WebSocket("ws://localhost:8080/"))
    //#endregion
  
    //#region Unrepeatable Actions
    useEffect(() => {
      server.onclose = onCloseFun
      if (localStorage.getItem("user"))
      {
        console.log(localStorage.getItem("user"))
        let data = localStorage.getItem("user").split(" ")
        server.onopen = () => {
          server.send("logInUser " + data[0] + " " + data[1])
          server.onmessage = (e) => {
            if (e.data != "0") setUser(new User(data[0], data[1], data[2], data[3]))
          }
        }
      }
    }, [])
    useEffect(() => {
      setInterval(() => {
        var pushVector = {x: (attraction) ? -tLightX + repulsorX : tLightX - repulsorX, y: (attraction) ? -tLightY + repulsorY : tLightY - repulsorY}
        var distance = Math.sqrt(Math.pow(pushVector.x, 2) + Math.pow(pushVector.y, 2))
        var normalisedPushVector = {x: pushVector.x / distance, y: pushVector.y / distance }
  
        lightAccelerationX = (pushVector.x > 0) ? Math.min(normalisedPushVector.x * 10000 / (distance * distance + 100), MaxStrength) : Math.max(normalisedPushVector.x * 10000 / (distance * distance + 100), -MaxStrength)
        lightAccelerationY = (pushVector.y > 0) ? Math.min(normalisedPushVector.y * 10000 / (distance * distance + 100), MaxStrength) : Math.max(normalisedPushVector.y * 10000 / (distance * distance + 100), -MaxStrength)
  
        lightSpeedX += lightAccelerationX
        if (lightSpeedX > 0) lightSpeedX = (lightSpeedX > DessepativeStrength) ? lightSpeedX - DessepativeStrength : 0
        if (lightSpeedX < 0) lightSpeedX = (lightSpeedX < -DessepativeStrength) ? lightSpeedX + DessepativeStrength : 0
  
        lightSpeedY += lightAccelerationY
        if (lightSpeedY > 0) lightSpeedY = (lightSpeedY > DessepativeStrength) ? lightSpeedY - DessepativeStrength : 0
        if (lightSpeedY < 0) lightSpeedY = (lightSpeedY < -DessepativeStrength) ? lightSpeedY + DessepativeStrength : 0
  
        setlightX((prev) => {
          var newX = prev + lightSpeedX
          if (newX < TleftBorder + MaxRadius)
          {
            newX = TleftBorder + MaxRadius
            lightSpeedX = -lightSpeedX
          }
          if (newX > window.innerWidth + window.scrollX -  MaxRadius)
          {
            newX = window.innerWidth + window.scrollX - MaxRadius
            lightSpeedX = -lightSpeedX
          }
          return newX
        })
        setlightY((prev) => {
          var newY = prev + lightSpeedY
          if (newY < 0 + MaxRadius)
          {
            newY = 0 + MaxRadius
            lightSpeedY = -lightSpeedY
          }
          if (newY > window.innerHeight + window.scrollY - MaxRadius)
          {
            newY = window.innerHeight + window.scrollY - MaxRadius
            lightSpeedY = -lightSpeedY
          }
          return newY
        })
      }, 10)
    }, [])
    //#endregion

    //#region EFFECTS
    useEffect(() => {
      //server.onclose = onCloseFun
    }, [server])
    useEffect(() => {
      tLightX = lightX
      tLightY = lightY
    }, [lightX, lightY])

    useEffect(() => {
      setLeftBorder(gameState == "" ? 200 : 0)
    }, [gameState])

    useEffect(() => {
      TleftBorder = leftBorder
    }, [leftBorder])

    useEffect(() => {
      mainScreen.current.addEventListener("mousemove", (event) => { repulsorX = event.pageX; repulsorY = event.pageY })
      mainScreen.current.addEventListener("mousedown", (event) => { attraction = true;})
      mainScreen.current.addEventListener("mouseup", (event) => { attraction = false;})
    })
    //#endregion
  
    var headRegistry = 
      {
          text: "Регистрация/Вход",
          content: <Registry server={server} setUser={setUser}/>
      }
    var headUser = 
      {
          text: user?.name ?? "",
          content: <UserInfo user={user} setUser={setUser} server={server}/>
      }
    var menuItems = [
      (user != null) ? headUser : headRegistry,
      {
          text: "Правила игры",
          content: <GameRules />
      },
      {
          text: "Внутрeигровой магазин",
          content: <Shop />
      },
      {
          text: "ВЫБОР ИГРЫ",
          content: <GameSelection setLightColor={setlightColor} setGameState={setGameState}/>
      },
    ]

    var bgStr = "radial-gradient(circle at " + (lightX - 200).toString() + "px " + lightY.toString() + "px, " + lightColor + " 0%, white 3%, white 3.3%, " + lightColor + " 6%, black 70%)"
    var bgStrGame = "radial-gradient(circle at " + (lightX).toString() + "px " + lightY.toString() + "px, " + lightColor + " 0%, white 3%, white 3.3%, " + lightColor + " 6%, black 70%)"
    return (
      (gameState == "")
      ?
        <div style={{position: "absolute", zIndex: "1", display: "flex", flexFlow: "row", justifyContent: "start", alignItems: "start", left: "0px", top: "0px", minWidth: "100%", height: "100vh"}}>
          <NavigationRail selectedIndex={selectedIndex} setselectedIndex={setselectedIndex} contentMenuItems={menuItems} />
          {
            <div ref={mainScreen} style={{display: "flex", flexFlow: "row", width: "100%", height: "100vh", justifyContent: "center", alignItems: "center", background: bgStr}}>
              {
                (selectedIndex >= 0) && menuItems[selectedIndex].content
              }
            </div>
          }
        </div>
      :
        <div ref={mainScreen} style={{display: "flex", flexFlow: "row", left: "0px", top: "0px", width: "100%", height: "100vh", justifyContent: "center", alignItems: "center", background: bgStrGame}}>
          <TheGame server = {server} difficulty = {gameState} setGameState={setGameState}/>
        </div>
    );
  }

export default App;
