package com.example.unravelling

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableTarget
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.http.HttpMethod
import io.ktor.utils.io.reader
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.ServerSocket
import kotlin.concurrent.thread
import kotlin.math.abs


data class Container<T>(var value: T)

inline fun<reified T> T.toContainer() : Container<T>
{
    return Container(this)
}

infix inline fun<reified T> Pair<List<T>, List<T>>.mergeBy(func: (T, T) -> T) : List<T>
{
    return Array(this.first.size){func(this.first[it], this.second[it])}.toList()
    //return List(this.first.size){func(this.first[it], this.second[it])}
}

inline infix fun<reified T> Pair<Pair<T, T>, Pair<T, T>>.mergeBy(func: (T, T) -> T ): Pair<T, T>
{
    return ((listOf(this.first.first, this.first.second) to listOf(this.second.first, this.second.second)) mergeBy func).run{this[0] to this[1]}
}

infix fun Float.between(diapason: Pair<Float, Float>) : Boolean
{
    return (this > diapason.first) && (this < diapason.second)
}

infix fun Float.distance(b: Float) : Float
{
    return kotlin.math.sqrt((this * this + b * b).toDouble()).toFloat()
}

fun Double.toRadians() : Double
{
    return this * Math.PI / 180
}

fun Float.toRadians() : Double
{
    return this * Math.PI / 180
}

data class User(var name: String, var password: String, var score: Int, var status: String)
{
    public constructor(Name: String, Password: String = "") : this(Name, Password, 0, "newbie")
}

class Data
{
    companion object Companion
    {
        var currentUser : MutableState<User?> = mutableStateOf(null)
        var currentGetActions: MutableList<(str: String) -> Unit> = mutableListOf()
        var ServerSocket: DefaultClientWebSocketSession? = runBlocking  {
            try
            {
                val client = HttpClient(CIO) { install(WebSockets) }
                client.webSocketSession(
                    method = HttpMethod.Get,
                    //host = "192.168.43.234",
                    host = "192.168.1.3",
                    port = 8080,
                    path = "/"
                )
            }
            catch(e: Exception)
            {
                null
            }
        }.also{
            thread{
                //pinging
                runBlocking {
                    while(true)
                    {
                        it?.send("ping")
                        delay(20000)
                    }
                }
            }
        }


        var rainbowcolorSource: MutableState<MutableList<Color>> = mutableStateOf(MutableList(30){ hue -> Color.hsl(hue.toFloat() * 12, 1F, 0.5F)})

        var currentPulseDelta = mutableStateOf(0f)
        var currentPulseAngle = mutableStateOf(0)

    }
}

fun makeColorSource(hue: Float, saturation: Float, lightness: Float): MutableList<Color>
{
    return mutableListOf(Color.hsl(hue, saturation + Data.currentPulseDelta.value / 350, lightness + Data.currentPulseDelta.value / 1000))
}

@Composable
fun MultiColoredText(label: String, source: MutableList<Color> = Data.rainbowcolorSource.value, alignment: Alignment.Horizontal = Alignment.Start, modifier: Modifier = Modifier.Companion)
{
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    )
    {
        label.split("\n").forEach{string ->
            Row()
            {
                string.forEachIndexed { ind, ch ->
                    Text(ch.toString(), color = source[ind % source.size])
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableTextBox(textSource: MutableState<String> = remember{ mutableStateOf("") }, label: @Composable () -> Unit, onValueChanged: (String) -> Unit, onEntered: (String) -> Unit, modifier: Modifier = Modifier.Companion)
{
    TextField(
        modifier = modifier,
        label = label,
        value = textSource.value,
        onValueChange = { newStr: String ->
            if (newStr.isEmpty()) textSource.value = ""
            else
            {
                newStr.firstOrNull{ it == '\n' }
                    ?.let { onEntered(textSource.value.slice(0 until textSource.value.length)) }
                    ?: Unit.let {textSource.value = newStr.also { onValueChanged(it) } }
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GradientalBox(
    modifier: Modifier? = null,
    paddingValue: Dp = 20.dp,
    enableShadow: Boolean = true,
    disableClick: Boolean = false,
    squareCorners: Boolean = false,
    centerVariable: Pair<Float, Float>? = null,
    colors: MutableList<Color> =
        mutableListOf(
            Blue,
            White,
            Blue,
            Black,
        ),
    colorStops: MutableList<Float> = mutableListOf(0f, 0.3f, 0.8f, 1f),
    click: (() -> Unit)? = null,
    longClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
)
{
    Box(
        modifier = Modifier
            .run modifier@{
                object : ShaderBrush()
                {
                    override fun createShader(size: Size): Shader
                    {
                        return RadialGradientShader(
                            centerVariable?.let {
                                Offset(
                                    centerVariable.first,
                                    centerVariable.second
                                )
                            } ?: Offset(0.0F, 0.0F),
                            size.width,
                            colors,
                            colorStops
                        )
                    }
                }.run brush@{
                    if (squareCorners) this@modifier.background(this)
                    else this@modifier.background(
                        this,
                        CircleShape
                    )
                }
            }
            .padding(paddingValue)
            .alpha(0.75f)
            /*.let {
                if (enableShadow) it.shadow(
                    elevation = 100.dp,
                    shape = RoundedCornerShape(8.dp),
                    spotColor = Black,
                    ambientColor = White
                )
                else it
            }*/
            .run {
                if (!disableClick)
                    this.combinedClickable(
                        onClick =
                        {
                            MainActivity.lightColor.value = colors
                            click?.let { it() }
                        },
                        onLongClick = longClick
                    )
                else
                    this
            }
            .then(modifier ?: Modifier.Companion),
        contentAlignment = Alignment.Center
    )
    {
        content()
    }
}

@Composable fun F(content: @Composable () -> Unit)
{
    content()
}

class MainActivity : ComponentActivity()
{
    companion object Companion
    {
        var bActionReady = true
        var bUserActionReady = true

        var bUserExists = mutableStateOf(false)

        var lightCenterPos = mutableStateOf(0f to 0f)
        var lightCenterSpeed = 1f to 1f
        var lightCenterAcceleration = 0f to 0f
        var lightSlowRate = 0.05f
        var bounciness = 1f

        var lightColor = mutableStateOf(mutableListOf(Yellow, White, Yellow, Black))
        var lightColorStops = mutableStateOf(mutableListOf(0f, 0.05f, 0.1f, 1f))

        lateinit var tiltManager: TiltManager
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            var context = LocalContext.current
            if (bUserActionReady)
            {

                //File(filesDir, "UserData").delete()
                bUserActionReady = false
                File(filesDir, "UserData").run {
                    if (this.exists())
                    {
                        thread {
                            runBlocking {
                                this@run.readLines().also{println(it.joinToString(""))}[0].split(" ").also{ userInfo ->
                                    Data.ServerSocket?.send("logInUser ${userInfo[0]} ${userInfo[1]}")
                                }
                                (Data.ServerSocket?.incoming?.receive() as Frame.Text?)?.readText()
                                    ?.also { servResponse ->
                                        if (servResponse != "0")
                                        {
                                            try {
                                                var userInfo = servResponse.split(" ")
                                                Data.currentUser.value = User(
                                                    userInfo[0],
                                                    userInfo[1],
                                                    userInfo[2].toInt(),
                                                    userInfo[3]
                                                )
                                                bUserExists.value = true
                                            }
                                            catch(e: Exception)
                                            {

                                            }
                                        }
                                    }
                            }
                        }
                    }
                }
            }
            if (bActionReady)
            {
                bActionReady = false
                thread{
                    tiltManager = TiltManager(this)
                    tiltManager.orientationUpdated += { pitch, roll, yaw ->
                        lightCenterAcceleration =
                            ((-kotlin.math.sin(roll.toRadians()) * 1).toFloat() to (kotlin.math.cos(roll.toRadians()) * 1).toFloat()).let { it.first * 0.15f to it.second * 0.15f }
                        //state.value = "${pitch}\n ${roll}\n $yaw"`
                        //OX: LEFT=90(2) RIGHT=-90(2) /// (1-sin(x)) * CENTER_WIDTH / 2
                        //OY: BOTTOM=0(2) TOP=180(2)  /// (1+cos(x)) * CENTER_HEIGHT / 2
                    }
                    tiltManager.startListening()
                }
                thread {
                    runBlocking {
                        listOf(Data.rainbowcolorSource).run {
                            while (true)
                            {
                                this.forEach { list ->
                                    list.value =
                                        list.value.run { this.slice(1 until this.size) + this[0] }
                                            .toMutableList()
                                }
                                delay(80)
                            }
                        }
                    }
                }

                thread {
                    runBlocking {
                        var angle = 0
                        while (true)
                        {
                            angle = when
                            {
                                angle < 360 -> angle + 1
                                else -> 0
                            }
                            Data.currentPulseDelta.value =
                                (kotlin.math.sin(angle * Math.PI / 180) * 100).toFloat()
                            Data.currentPulseAngle.value = angle
                            delay(20)
                        }
                    }
                }
                thread {
                    runBlocking {
                        while (true)
                        {
                            lightCenterSpeed = (lightCenterSpeed to lightCenterAcceleration) mergeBy { a, b -> a + b }
                            lightCenterSpeed = (lightCenterSpeed to (-lightSlowRate * lightCenterSpeed.first / (lightCenterSpeed.first distance lightCenterSpeed.second) to -lightSlowRate * lightCenterSpeed.second / (lightCenterSpeed.first distance lightCenterSpeed.second))) mergeBy { a, b ->
                                    a + b
                                }

                            lightCenterPos.value = (lightCenterPos.value to lightCenterSpeed) mergeBy { a, b -> a + b }
                            if (lightCenterPos.value.first - 100 < 0)
                            {
                                lightCenterPos.value = 100f to lightCenterPos.value.second
                                lightCenterSpeed =
                                    -lightCenterSpeed.first * bounciness to lightCenterSpeed.second
                            }
                            if (lightCenterPos.value.second - 100 < 0)
                            {
                                lightCenterPos.value = lightCenterPos.value.first to 100f
                                lightCenterSpeed =
                                    lightCenterSpeed.first to -lightCenterSpeed.second * bounciness
                            }
                            if (lightCenterPos.value.first + 100 > this@MainActivity.window.windowManager.defaultDisplay.width)
                            {
                                lightCenterPos.value =
                                    this@MainActivity.window.windowManager.defaultDisplay.width.toFloat() - 100 to lightCenterPos.value.second
                                lightCenterSpeed =
                                    -lightCenterSpeed.first * bounciness to lightCenterSpeed.second
                            }
                            if (lightCenterPos.value.second + 100 > this@MainActivity.window.windowManager.defaultDisplay.height)
                            {
                                lightCenterPos.value =
                                    lightCenterPos.value.first to this@MainActivity.window.windowManager.defaultDisplay.height.toFloat() - 100
                                lightCenterSpeed =
                                    lightCenterSpeed.first to -lightCenterSpeed.second * bounciness
                            }
                            delay(10)
                        }
                    }
                }
            }

            (LocalContext.current as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            GradientalBox(
                modifier = Modifier.fillMaxSize(),
                enableShadow = false,
                paddingValue = 0.dp,
                disableClick = true,
                squareCorners = true,
                centerVariable = lightCenterPos.value,
                colors = MainActivity.lightColor.value,
                colorStops = MainActivity.lightColorStops.value
            )
            {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                )
                {
                    Spacer(modifier = Modifier.weight(0.1f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    {
                        Spacer(modifier = Modifier.weight(1f))
                        GradientalBox(
                            click =
                            {
                            }
                        )
                        {
                            Text("Добро пожаловать в игру", color = Magenta)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        if (bUserExists.value) let{
                            GradientalBox(
                                colors = mutableListOf(Color.Black, Color.White, Color.Black, Color.Black),
                                click =
                                {
                                    bUserExists.value = false
                                    Data.currentUser.value = null
                                    File(filesDir, "UserData").delete()
                                }
                            )
                            {
                                Text("Выйти из аккаунта", color = Color.Blue)
                            }
                            Spacer(modifier = Modifier.width(40.dp))
                            GradientalBox(colors = mutableListOf(Color.Red, Color.White, Color.Magenta, Color.Black))
                            {
                                Text(Data.currentUser.value!!.name, color = Color.Blue)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            GradientalBox()
                            {
                                Text(Data.currentUser.value!!.score.toString(), color = Color.Yellow)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            GradientalBox()
                            {
                                Text(Data.currentUser.value!!.status, color = Color.White)
                            }
                        } else let {
                            GradientalBox(
                                click =
                                {
                                    RegisterUserActivity.bActionReady = true
                                    RegisterUserActivity.bActivityLiving = true
                                    this@MainActivity.startActivity(
                                        Intent(
                                            context,
                                            RegisterUserActivity::class.java
                                        )
                                    )
                                }
                            )
                            {
                                Text("Регистрация", color = Magenta)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            GradientalBox(
                                click =
                                {
                                    LogInUserActivity.bActivityLiving = true
                                    LogInUserActivity.bActionReady = true
                                    this@MainActivity.startActivity(
                                        Intent(
                                            context,
                                            LogInUserActivity::class.java
                                        )
                                    )
                                }
                            )
                            {
                                Text("Вход", color = Magenta)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(0.4f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Top
                    )
                    {
                        Spacer(modifier = Modifier.weight(0.3f))
                        GradientalBox(
                            colors = mutableListOf(Green, White, Green, Black),
                            click =
                            {
                                GameActivity.bActionReady = true
                                GameActivity.bActivityLiving = true
                                this@MainActivity.startActivity(Intent(context, GameActivity::class.java).also{intent -> intent.putExtra("difficulty", "easy"); intent.putExtra("height", windowManager.defaultDisplay.height); intent.putExtra("width", windowManager.defaultDisplay.width)})
                            }
                        )
                        {
                            Text("Низкая сложность", color = Magenta)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        GradientalBox(
                            colors = mutableListOf(Yellow, White, Yellow, Black),
                            click =
                            {
                                GameActivity.bActionReady = true
                                GameActivity.bActivityLiving = true
                                this@MainActivity.startActivity(Intent(context, GameActivity::class.java).also{intent -> intent.putExtra("difficulty", "medium"); intent.putExtra("height", windowManager.defaultDisplay.height); intent.putExtra("width", windowManager.defaultDisplay.width)})
                            }
                        )
                        {
                            Text("Средняя сложность", color = Magenta)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        GradientalBox(
                            colors = mutableListOf(Red, White, Red, Black),
                            click =
                            {
                                GameActivity.bActionReady = true
                                GameActivity.bActivityLiving = true
                                this@MainActivity.startActivity(Intent(context, GameActivity::class.java).also{intent -> intent.putExtra("difficulty", "hard"); intent.putExtra("height", windowManager.defaultDisplay.height); intent.putExtra("width", windowManager.defaultDisplay.width)})
                            }
                        )
                        {
                            Text("Высокая сложность", color = Green)
                        }
                        Spacer(modifier = Modifier.weight(0.3f))
                    }
                    Spacer(modifier = Modifier.weight(0.3f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Top
                    )
                    {
                        Spacer(modifier = Modifier.weight(0.3f))
                        F {
                            GradientalBox(
                                colors = mutableListOf(Black, Red, Black, Black),
                                click = {
                                    this@MainActivity.startActivity(Intent(context, RulesActivity::class.java))
                                }
                            )
                            {
                                MultiColoredText(
                                    "Правила игры",
                                    source = mutableListOf(Color.hsl(180f, 1f, 0.6f), Color.hsl(200f, 0.5f, 0.5f))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        F {
                            GradientalBox(
                                colors = mutableListOf(Black, Magenta, Black, Black),
                                click = {
                                    suspend fun f()
                                    {
                                        delay(1)
                                    }
                                    runBlocking { delay(1); f() }
                                    this@MainActivity.startActivity(Intent(context, TutorialActivity::class.java))
                                }
                            )
                            {
                                MultiColoredText(
                                    "Обучение",
                                    source = mutableListOf(Color.hsl(100f, 1f, 0.6f), Color.hsl(120f, 0.5f, 0.5f))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        F {
                            GradientalBox(
                                colors = mutableListOf(Black, Blue, Black, Black),
                            )
                            {
                                MultiColoredText(
                                    "Спец уровень",
                                    source = mutableListOf(Color.hsl(0f, 1f, 0.6f), Color.hsl(30f, 0.5f, 0.5f))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.3f))
                    }
                    Spacer(modifier = Modifier.weight(0.4f))
                }
            }
        }
    }
}
