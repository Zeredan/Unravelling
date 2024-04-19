import { MutableRefObject, useEffect, useRef, useState } from "react";
import fireSphere from "./fireSphere.mp4";
import { createPortal } from "react-dom";
import React from "react"

class GraphNode
{
    get radius(): number
    {
        return ((this.isActivated) ? OrientedGraph.selectedRadius : OrientedGraph.normalRadius);
    }
    public x = 0;
    public y = 0;
    public isActivated = false;
    constructor(x: number, y: number)
    {
        this.x = x;
        this.y = y;
    }
}

class OrientedGraph
{
    static normalRadius: number;
    static selectedRadius: number;

    static scoreForWin: number;

    static selectedNode: GraphNode | null = null;
    static normalBurningVideo: HTMLVideoElement;
    static selectedBurningVideo: HTMLVideoElement;
    nodes: GraphNode[] = new Array(); // list<node*>
    links: Map<GraphNode, GraphNode[]> = new Map(); // unordered_map<node*, list<node*>>

    IntersectedLinks: Array<{ a: GraphNode, b: GraphNode }> = new Array();

    public CopyConstructor(graph: OrientedGraph)
    {
        this.nodes = new Array();
        this.links = new Map<GraphNode, GraphNode[]>();
        graph.nodes.forEach((node) => { this.nodes.push(node) });
        graph.links.forEach((links, vertice, m) => { links.forEach((link) => { this.links.get(vertice) ? this.links.get(vertice)?.push(link) : this.links.set(vertice, Array(link)); }) });
    }
    public constructor()
    {

    }
    public AddNodes(...nodes: GraphNode[])
    {
        nodes.forEach(function(node: GraphNode, v, m)  {this.nodes.push(node) });
    }
}

let distance = function (a: GraphNode, b: GraphNode)
{
    return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
}


let intersect = function (a1: GraphNode, b1: GraphNode, a2: GraphNode, b2: GraphNode): boolean
{
    if ((a1.x == a2.x && a1.y == a2.y) || (a1.x == b2.x && a1.y == b2.y) || (b1.x == a2.x && b1.y == a2.y) || (b1.x == b2.x && b1.y == b2.y)) { return false; }
    const A = (b1.x - a1.x);
    const B = (b1.y - a1.y);
    const C = (b2.x - a2.x);
    const D = (b2.y - a2.y);
    const c1 = B * a1.x - A * a1.y;
    const c2 = D * a2.x - C * a2.y;

    let y = (c2 - c1 * D / B) / (A * D / B - C);
    let x = (c2 + C * y) / D;
    var node = new GraphNode(x, y);
    return ((distance(a1, node) < distance(a1, b1)) && (distance(b1, node) < distance(a1, b1)) && (distance(a2, node) < distance(a2, b2)) && (distance(b2, node) < distance(a2, b2)));
}

let draw_graph = function (ctx: CanvasRenderingContext2D, graph: OrientedGraph)
{
    ctx.clearRect(0, 0, 2000, 2000);

    graph.links.forEach((links, vertice, m) => { links.forEach((link) => { ctx!.strokeStyle = (vertice.isActivated || link.isActivated) ? "yellow" : (graph.IntersectedLinks.find((obj) => { return (obj.a == vertice && obj.b == link); }) ? "red" : "white"); ctx?.beginPath(); ctx?.moveTo(vertice.x, vertice.y); ctx?.lineTo(link.x, link.y); ctx?.closePath(); ctx?.stroke(); }); });
    graph.nodes.forEach((node) => { ctx?.save(); ctx?.beginPath(); ctx?.arc(node.x, node.y, node.radius, 0, 2 * Math.PI); ctx?.closePath(); ctx?.clip(); ctx?.drawImage(OrientedGraph.normalBurningVideo, node.x - node.radius, node.y - node.radius, node.radius * 2, node.radius * 2);/*ctx?.fillRect(node.x - node.radius, node.y - node.radius, 2 * node.radius, 2 * node.radius);*/ ctx?.restore(); });
}

var context : CanvasRenderingContext2D | null = null

function VictoryDialog(props: {children, winDialogRef: React.RefObject<HTMLDialogElement>, setGameState})
{
    return (
        <dialog ref={props.winDialogRef}>
            <div style={{minWidth: "50%", minHeight: "50%", display: "flex", flexFlow: "column", justifyContent: "start", alignItems: "center", background: "radial-gradient(ellipse at 25% 25%, white 0%, magenta 5%, white 10%, magenta 20%, blue 100%)"}}>
                <h1 style={{color: "green", marginTop: "20px"}}>ПОБЕДА</h1>
                <div style={{flex: "1"}}></div>
                <div style={{display: "flex", flexDirection: "row"}}>
                    <div style={{flex: "1"}}></div>
                    <input type="button" value = "Продолжить играть" onClick={() => {props.winDialogRef.current?.close()}}/>
                    <div style={{flex: "2"}}></div>
                    <input type="button" value="Завершить" onClick={() => {props.winDialogRef.current?.close(); props.setGameState("")}}/>
                    <div style={{flex: "1"}}></div>    
                </div>
                <div style={{flex: "1"}}></div>
            </div>
        </dialog>
    );
}

var graph = new OrientedGraph()

export default function TheGame(props : {server: WebSocket, difficulty: String, setGameState})
{
    var canvasRef = useRef<HTMLCanvasElement>(null);
    var dialogRef = useRef<HTMLDialogElement>(null);
    var [_ignoreWin, setIgnoreWin] = useState(false);
    var ignoreWin = useRef<boolean>()
    ignoreWin.current = _ignoreWin
    useEffect(() => {
            canvasRef.current!.width = window.innerWidth;
            canvasRef.current!.height = window.innerHeight;
            context = canvasRef.current!.getContext("2d")
    }, [canvasRef])
    useEffect(() =>{
        var video = document.createElement("video");
            video.src = fireSphere;
            OrientedGraph.normalBurningVideo = video;
            video.addEventListener("loadeddata", function (e)
            {
                var playingFunc = () => { draw_graph(context!, graph); var res = video.requestVideoFrameCallback(playingFunc); };
                video.loop = true;
                video.play();
                var res = video.requestVideoFrameCallback(playingFunc);
            });
        props.server.onmessage = async function(e: MessageEvent<any>)
        {
            if (e.data == "Victory")
                {
                    if (!ignoreWin.current) {
                        setIgnoreWin(true)
                        dialogRef?.current?.showModal()
                    }
                }
            if ((e.data as string).split("\n")[0]! == "graphData")
                {
                    let info: Array<string> = e.data.split("\n");
                    let newgraph = new OrientedGraph();
                    OrientedGraph.normalRadius = +info[1];
                    OrientedGraph.selectedRadius = +info[2];
                    for (var i = 0; i < +info[3]; ++i)
                    {
                        var newNode = new GraphNode(+(info[i + 4].split(" ")[0]), +(info[i + 4].split(" ")[1]));
                        if (+info[i + 4].split(" ")[2] == 1) newNode.isActivated = true;
                        newgraph.nodes.push(newNode);
                    }
                    for (var i = 0; i < +info[+info[3] + 4]; ++i)
                    {
                        newgraph.links.get(newgraph.nodes[+(info[i + (+info[3] + 5)].split(" ")[0])])?.push(newgraph.nodes[+(info[i + (+info[3] + 5)].split(" ")[1])]) ?? newgraph.links.set(newgraph.nodes[+(info[i + (+info[3] + 5)].split(" ")[0])], Array(newgraph.nodes[+(info[i + (+info[3] + 5)].split(" ")[1])]));
                    }
                    newgraph.IntersectedLinks = new Array();
                    newgraph.links.forEach((linksA, verticeA) => { linksA.forEach((linkA) => { newgraph.links.forEach((linksB, verticeB) => { linksB.forEach((linkB) => { if (intersect(verticeA, linkA, verticeB, linkB)) {newgraph.IntersectedLinks.push({a: verticeA, b: linkA}); newgraph.IntersectedLinks.push({a: verticeB, b: linkB}); } });}); });});
                    graph = newgraph
                    if (canvasRef.current) draw_graph(context!!, graph);
                }
        }
        var map = new Map<String, String>();
        map.set("Обучение", "tutorial");
        map.set("Легкая сложность", "easy");
        map.set("Средняя сложность", "medium");
        map.set("Высокая сложность", "hard");
        map.set("Спец. уровень", "square");
        props.server.send("createGame " + map.get(props.difficulty) + " " + window.innerHeight + " " + window.innerWidth)
    }, [])
    return (
        <div>
            <VictoryDialog winDialogRef={dialogRef} setGameState={props.setGameState}>alpaca</VictoryDialog>

            <div style={{position: "absolute", zIndex: "100", left: "0px", top: "0px", padding: "20px", userSelect: "none", borderRadius: "20px", background: "linear-gradient(0deg, black, white 50%, black 100%)"}} onClick={() => {props.server.send("endGame"); props.setGameState("")} }>Назад</div>
            <canvas ref={canvasRef} style={{position: "absolute", left: "0px", top: "0px", zIndex: "99", minWidth: "100%", minHeight: "100vh", background: "linear-gradient(0deg, rgba(240, 80, 80, 0.5) 0%, rgba(150, 190, 30, 0.5) 100%)"}} onClick={(e) => {props.server.send("canvasClicked " + e.pageX + " " + e.pageY)}} />
        </div>
    )
}//background: "linear-gradient(0deg, rgba(240, 80, 80, 0.5) 0%, rgba(150, 190, 30, 0.5) 100%)
//<video ref={videoRef} src={fireSphere} />