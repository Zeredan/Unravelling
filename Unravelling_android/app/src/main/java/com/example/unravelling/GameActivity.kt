package com.example.unravelling

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder.VideoSource
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.rememberImagePainter
import com.example.unravelling.ui.theme.Pink40
import kotlinx.coroutines.delay
import kotlin.math.abs

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.engine.cio.*
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.toURI
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.runInterruptible
import java.lang.Exception
import kotlin.concurrent.thread

class GraphNode
{
    var x: Double = 0.0
    var y: Double = 0.0
    var isActivated: Boolean = false
    constructor(x: Double, y: Double)
    {
        this.x = x
        this.y = y
    }
}

infix fun GraphNode.distance(b: GraphNode): Double {
    return Math.sqrt(Math.pow((this.x - b.x).toDouble(), 2.0) + Math.pow((this.y - b.y).toDouble(), 2.0));
}

infix fun Pair<GraphNode, GraphNode>.intersect (b: Pair<GraphNode, GraphNode>) : Boolean
{
    if ((this.first.x == b.first.x && this.first.y == b.first.y) || (this.first.x == b.second.x && this.first.y == b.second.y) || (this.second.x == b.first.x && this.second.y == b.first.y) || (this.second.x == b.second.x && this.second.y == b.second.y)) {
        return false
    }
    val A = (this.second.x - this.first.x)
    val B = (this.second.y - this.first.y)
    val C = (b.second.x - b.first.x)
    val D = (b.second.y - b.first.y)
    val c1 = B * this.first.x - A * this.first.y;
    val c2 = D * b.first.x - C * b.first.y;

    var y = (c2 - c1 * D / B) / (A * D / B - C)
    var x = (c2 + C * y) / D
    return GraphNode(x, y).run ( fun GraphNode.() : Boolean{
        return (((this@intersect.first distance this) < (this@intersect.first distance this@intersect.second)) && ((this@intersect.second distance this) < (this@intersect.first distance this@intersect.second)) && ((b.first distance this) < (b.first distance b.second)) && (((b.second distance this) < (b.first distance b.second))))
    } )

}

class OrientedGraph
{
    var normalRadius: Double = 0.0
    var selectedRadius: Double = 0.0

    var nodes: MutableList<GraphNode> = mutableListOf()
    var links: MutableMap<GraphNode, MutableList<GraphNode>> = mutableMapOf()

    var intersectedLinks: MutableSet<Pair<GraphNode, GraphNode>> = mutableSetOf()

    var selectedNode: GraphNode? = null
    var scoreForWin : Int = 0

    public override fun toString(): String {
        return "".toContainer().apply {
            this.value += "graphData\n"
            this.value += normalRadius.toString() + "\n"
            this.value += selectedRadius.toString() + "\n"
            this.value += nodes.size.toString() + "\n"
            nodes.forEach { this.value += it.x.toString() + " " + it.y.toString() + " " + (if (it.isActivated) "1" else "0") + "\n" }
            0.toContainer().run countLinks@{ "".toContainer().apply linkStrs@{ links.forEach { t, u -> u.forEach{link -> this@countLinks.value++; this.value += nodes.indexOfFirst { it == t }!!.toString() + " " + nodes.indexOfFirst { it == link }!!.toString() + "\n"}}}.let{this@apply.value += this.value.toString() + "\n" + it.value}}
        }.value.also{println(it)}
    }

    public constructor(str: String)
    {
        str.split("\n").apply{
            this@OrientedGraph.normalRadius = this[1].toDouble()
            this@OrientedGraph.selectedRadius = this[2].toDouble()
            repeat(this[3].toInt())
            {
                this@OrientedGraph.nodes += this[4 + it].split(" ").map{it.toDouble()}.run{ GraphNode(this[0], this[1]).also{it1 -> if (this[2].toInt() == 1) it1.isActivated = true.apply{this@OrientedGraph.selectedNode = it1}} }
            }
            repeat(this[this[3].toInt() + 4].toInt())
            {
                this[it + this[3].toInt() + 5].split(" ").map{it1 -> it1.toInt()}.let{
                        linkInfo ->
                    this@OrientedGraph.links.putIfAbsent(this@OrientedGraph.nodes[linkInfo[0]], mutableListOf(this@OrientedGraph.nodes[linkInfo[1]]))?.let{oldVal -> oldVal += this@OrientedGraph.nodes[linkInfo[1]]}
                }
            }
            intersectedLinks = mutableSetOf()
            links.forEach{ (vertice, ls) -> ls.forEach{link -> links.forEach{ (vertice1, ls1) -> ls1.forEach{ link1 -> if ((vertice != vertice1 || link != link1) && (vertice != link1 || link != vertice1)) if ((vertice to link) intersect (vertice1 to link1)) intersectedLinks.addAll(listOf(vertice to link, vertice1 to link1)) }} }}
        }
    }
    public constructor(normalRaduis: Double, selectedRadius: Double, scoreForWin: Int)
    {
        this.normalRadius = normalRaduis
        this.selectedRadius = selectedRadius
        this.scoreForWin = scoreForWin
    }
}



class GameActivity : ComponentActivity()
{
    companion object Companion
    {
        var bActionReady = false
        var bActivityLiving = false
        lateinit var messagehandler: Thread

        var victoryDialogExpanded = mutableStateOf(false)
        lateinit var burningSphereNormal: ImageBitmap
        lateinit var burningSphereSelected: ImageBitmap
        var currentGraph = mutableStateOf(OrientedGraph(.0, .0, 0))
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            var context = LocalContext.current


            BackHandler(true)
            {
                thread{
                    runBlocking {
                        bActivityLiving = false
                        runBlocking{
                            Data.ServerSocket?.send("endGame")
                            Data.ServerSocket?.send("clearingMessageThread")
                            delay(20)
                        }
                        MainActivity.bUserActionReady = true
                        startActivity(Intent(context, MainActivity::class.java))
                    }
                }
            }
            if (bActionReady)
            {
                bActionReady = false
                burningSphereNormal = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.img_1), 100, 100, false).asImageBitmap()
                burningSphereSelected = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.img_1), 150, 150, false).asImageBitmap()
                runBlocking {
                    Data.ServerSocket?.send(
                        "createGame ${
                            this@GameActivity.intent.extras!!.getString(
                                "difficulty"
                            )
                        } ${this@GameActivity.intent.extras!!.getInt("height")!!} ${
                            this@GameActivity.intent.extras!!.getInt(
                                "width"
                            )!!
                        }"
                    )
                }

                Data.currentGetActions = mutableListOf({ str ->
                    if (str.split('\n')[0] == "graphData") currentGraph.value = OrientedGraph(str)
                    if (str == "Victory") victoryDialogExpanded.value = true
                })
                thread {
                    runBlocking {
                        while (bActivityLiving)
                        {
                            (Data.ServerSocket?.incoming?.receive() as Frame.Text?)?.readText()
                                ?.also {
                                    Data.currentGetActions.forEach { it1 -> it1(it) }
                                }
                        }
                    }
                }
            }

            (LocalContext.current as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            F {
                GradientalBox(
                    modifier = Modifier.fillMaxSize(),
                    enableShadow = false,
                    paddingValue = 0.dp,
                    disableClick = true,
                    squareCorners = true,
                    centerVariable = MainActivity.lightCenterPos.value,
                    colors = MainActivity.lightColor.value,
                    colorStops = MainActivity.lightColorStops.value
                )
                {
                    if (victoryDialogExpanded.value)
                    {
                        var dialogDrag = remember{ mutableStateOf(Offset(0f, 0f)) }
                        AlertDialog(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(object : ShaderBrush()
                                {
                                    override fun createShader(size: Size): Shader
                                    {
                                        return LinearGradientShader(
                                            Offset(0f, 0f),
                                            Offset(0f, size.height),
                                            listOf(Color.Green, Color.Blue)
                                        )
                                    }
                                })
                                .offset(dialogDrag.value.x.dp, dialogDrag.value.y.dp)
                                .pointerInput(1) {
                                    detectDragGestures { change, dragAmount -> dialogDrag.value += dragAmount }
                                },
                            onDismissRequest = { victoryDialogExpanded.value = false },
                            dismissButton = {
                                TextButton(onClick =
                                    {
                                        victoryDialogExpanded.value = false
                                    }
                                ) {
                                    Text("Вернуться к игре")
                                }
                            },
                            confirmButton = {
                                TextButton(onClick =
                                {
                                    try
                                    {
                                        victoryDialogExpanded.value = false
                                        bActivityLiving = false
                                        runBlocking{
                                            Data.ServerSocket?.send("endGame")
                                            Data.ServerSocket?.send("clearingMessageThread")
                                            delay(20)
                                        }
                                        MainActivity.bUserActionReady = true
                                        startActivity(Intent(context, MainActivity::class.java))
                                    }
                                    catch(e: Exception)
                                    {

                                    }
                                }
                                ) {
                                    Text("Завершить игру")
                                }
                            }
                        )
                    }
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit)
                            {
                                detectTapGestures { offset ->
                                    runBlocking {
                                        Data.ServerSocket?.send(
                                            "canvasClicked ${offset.x} ${offset.y}"
                                        )
                                    }
                                }
                            }
                    )
                    {
                        currentGraph.value.run graph@{
                            this.links.forEach{(fromV, links) -> links.forEach{toV -> drawLine(this.intersectedLinks.find { it.first == fromV && it.second == toV }?.let{Color.Red} ?: Color.White, Offset(fromV.x.toFloat(), fromV.y.toFloat()), Offset(toV.x.toFloat(), toV.y.toFloat()))}}
                            this.nodes.forEach { node -> clipPath(Path().also{it.addArc(Rect(Offset(node.x.toFloat(), node.y.toFloat()), if (node.isActivated) 75f else 50f), 0f, 360f) }, ClipOp.Intersect){
                                    if (node.isActivated) drawImage(burningSphereSelected, Offset(node.x.toFloat() - 76, node.y.toFloat() - 76))
                                    else drawImage(burningSphereNormal, Offset(node.x.toFloat() - 51, node.y.toFloat() - 51))
                                }/*drawCircle(Color.Red, when{node.isActivated -> currentGraph.selectedRadius; else -> currentGraph.normalRadius}.toFloat(), Offset(node.x.toFloat(), node.y.toFloat()))*/
                            }
                        }
                    }
                }
            }
        }
    }
}
