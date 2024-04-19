import React, { useState} from "react";

export default function NavigationRail(props: { selectedIndex, setselectedIndex, contentMenuItems: {text: String, content: React.JSX.Element}[]})
{
    return (
        <div style={{display: "flex", flexFlow: "column", alignItems: "center", height: "100vh", minWidth: "200px"}}>
            <div style={{display: "flex", justifyContent: "center", alignItems: "center", width: "200px", height: "80px", background: "linear-gradient(90deg, green 0%, lightgreen 100%)"}} onClick={(event) => { props.setselectedIndex(0) }}>
                <p style={{color: "magenta", userSelect: "none"}}>{props.contentMenuItems[0].text}</p>
            </div>
            <div style={{flex: "1", display: "flex", flexFlow: "column", alignItems: "center", justifyContent: "end", minWidth: "200px", background: "linear-gradient(60deg, black 0%, blue 50%, darkgray 100%)", overflowY: "scroll"}}>
                <div style={{minHeight: "20px"}}></div>
                    {
                        props.contentMenuItems.map(
                            (menuItem, ind) => {
                                if (ind > 0)
                                {
                                    var selectedColor = (ind == props.selectedIndex) ? "green" : "yellow"
                                    var bg = "radial-gradient(circle at 0px 0px, " + selectedColor + " 0%, white 3%, white 4%, " + selectedColor + " 5%, black 100%)"
                                    return (
                                        <div>
                                            <div style={{width: "140px", height: "40px", borderRadius: "30px", padding: "20px", justifyContent: "center", alignItems: "center", background: bg}} onClick={(event) => { props.setselectedIndex(ind) }}>
                                                <p style={{color: "wheat", userSelect: "none"}}>{menuItem.text}</p>
                                            </div>
                                            <div style={{minHeight: "60px"}}></div>
                                        </div>
                                    );
                                }
                            }
                        )
                    }
                <div style={{minHeight: "20px"}}></div>
            </div>
        </div>
    );
}