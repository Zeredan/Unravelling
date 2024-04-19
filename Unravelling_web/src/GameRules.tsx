import React from "react"
import  intersectedGraphFragment from "./intersectedGraphFragment.png"
import  normalGraphFragment from "./normalGraphFragment.png"

export default function GameRules()
{
    return (
        <div style={{display: "flex", flexDirection: "column", width: "100%", height: "100vh", justifyContent: "start", alignItems: "center", borderRadius: "20px", overflowY: "scroll"}}>
            <h1 style={{userSelect: "none"}}>ПРАВИЛА ИГРЫ</h1>
            <div style={{flex: 1}}></div>
            <div style={{display: "flex", flexDirection: "row", backgroundColor: "rgba(150, 150, 150, 0.3)", width: "100%", borderRadius: "20px"}}>
                <div style={{flex: 1}}></div>
                <div style={{background: "linearGradient(30deg, rgba(255, 255, 255, 0.5) 0%, rgba(240, 0, 15, 0.5) 50%, rgba(255, 255, 255, 0.5) 100%"}}>
                    <p style={{color: "aqua"}}>
                        Правила данной игры:<br/>
                        Вам дана запутанная сеть(граф) из вершин в виде огненных шаров<br/>
                        и ребер. Необходимо её распутать перестановками двух выбранных шаров<br/><br/>
                        1)Если нить подсвечивается красной, значит она пересекается с другой нитью<br/>
                        2)Если нить белая - все хорошо, пересечений нет<br/>
                        3)Для того чтобы поменять две вершины нажмите одну из вершин, она увеличится<br/>
                        и затем выберите другую вершину. Если необходимо отменить выбор вершины нажмите на неё еще раз
                    </p>
                </div>
                <div style={{flex: 2}}></div>
                <div style={{display: 'flex', flexFlow: "column", borderRadius: "10px"}}>
                    <div style={{display: "flex", flexFlow: "row", justifyContent: "end"}}>
                        <p style={{marginRight: "10px"}}>Нормальная часть</p>
                        <img src={intersectedGraphFragment}></img>
                    </div>
                    <div style={{height: "20px"}}></div>
                    <div style={{display: "flex", flexFlow: "row", justifyContent: "end"}}>
                        <p style={{marginRight: "10px"}}>Нормальная часть</p>
                        <img src={normalGraphFragment}></img>
                    </div>
                </div>
                <div style={{flex: 1}}></div>
            </div>
            <div style={{flex: 1}}></div>
        </div>
    )
}