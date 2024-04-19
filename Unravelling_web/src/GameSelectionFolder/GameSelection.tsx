import React, { useEffect, useState, useRef } from "react";
import classes from "./Styles.module.css";
import {TutorialDescription, EasyDescription, MediumDescription, HardDescription, SquareDescription, TutorialScore, EasyScore, MediumScore, HardScore, SquareScore} from "./GameDescription.ts"


var TselectedLvlIndex = 0

var flexChangeSpeed = 0.05
var flexAcceleration = 0.001

var lvls = [
    {
        text: "Обучение",
        cssClass: classes.TutorialButton,
        dopStyle: "",
        description: TutorialDescription,
        score: TutorialScore,
        color: "rgba(255, 255, 240, 1)",
        serverSendedValue: "tutorial",
        flex: 3.0,
        speed: 0,
        acceleration: 0
    },
    {
        text: "Легкая сложность",
        cssClass: classes.SimpleGameButton,
        dopStyle: "--gradColor: green",
        description: EasyDescription,
        score: EasyScore,
        color: "rgba(0, 255, 0, 1)",
        serverSendedValue: "easy",
        flex: 1.0,
        speed: 0,
        acceleration: 0
    },
    {
        text: "Средняя сложность",
        cssClass: classes.SimpleGameButton,
        dopStyle: "--gradColor: yellow",
        description: MediumDescription,
        score: MediumScore,
        color: "rgba(255, 255, 0, 1)",
        serverSendedValue: "medium",
        flex: 1.0,
        speed: 0,
        acceleration: 0
    },
    {
        text: "Высокая сложность",
        cssClass: classes.SimpleGameButton,
        dopStyle: "--gradColor: red",
        description: HardDescription,
        score: HardScore,
        color: "rgba(255, 0, 0, 1)",
        serverSendedValue: "hard",
        flex: 1.0,
        speed: 0,
        acceleration: 0
    },
    {
        text: "Спец. уровень",
        description: SquareDescription,
        cssClass: classes.SquareButton,
        dopStyle: "--gradColor: green",
        score: SquareScore,
        color: "rgba(255, 0, 255, 1)",
        serverSendedValue: "square",
        flex: 1.0,
        speed: 0,
        acceleration: 0
    }
]

export default function GameSelection(props: {setLightColor, setGameState})
{
    var [selectedLvlIndex, setselectedLvlIndex] = useState(0)
    var [forRendering, setforRendering] = useState(0)
    var t1selectedLvlIndex = useRef(selectedLvlIndex)
    useEffect(
        () =>
        {
            TselectedLvlIndex = selectedLvlIndex
        },
        [selectedLvlIndex]
    )

    useEffect(
        () =>
        {
            const interval = setInterval(() => {
                setforRendering((prev) => { return (prev == 0) ? 1 : 0 })
                lvls.forEach((lvl, ind) => {
                    lvl.flex = (ind == TselectedLvlIndex) ? Math.min(3.0, lvl.flex + flexChangeSpeed) : Math.max(1.0, lvl.flex - flexChangeSpeed)
                })
            }, 10)
            return () => {
                clearInterval(interval)
            }
        },
        []
    )

    return (
        <div style={{display: "flex", flexFlow: "row", justifyContent: "start", alignItems: "center", width: "100%", height: "100vh"}}>
            {
                lvls.map(
                    function (lvl, ind, arr)
                    {
                        var bgStr = lvl.color//"linear-gradient(0deg, " + lvl.color + " 0%" + ", gray 50%, " + lvl.color + " 100%)"
                        return (
                            <div onMouseEnter={(e) => { props.setLightColor(lvl.color); setselectedLvlIndex(ind) }} style={{display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "start", opacity: "1", minHeight: "100vh", flex: lvl.flex, background: bgStr}}>
                                <h1 style={{userSelect: "none", opacity: "1"}}>{lvl.text}</h1>
                                <div style={{flex: "1"}}></div>
                                {
                                    (lvl.flex == 3) ?
                                    <div style={{display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "start", minWidth: "100%"}}>
                                        <h1 style={{userSelect: "none", opacity: "1"}}>{lvl.description}</h1>
                                        <h1 style={{userSelect: "none", opacity: "1"}}>Очки: {lvl.score?.toString() ?? "?"}</h1>
                                        <div style={{minHeight: "20px"}}></div>
                                        <div className={lvl.cssClass} onClick={() => {props.setGameState(lvl.text)}}>
                                            ИГРАТЬ
                                        </div>
                                        <div style={{minHeight: "20px"}}></div>
                                    </div>
                                    : null
                                }
                                <h1 style={{minHeight: "30px"}}></h1>
                            </div>
                        )
                    }
                )
            }
        </div>
    )
}