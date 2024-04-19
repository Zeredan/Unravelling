package com.example.unravelling

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.unravelling.ui.theme.Pink40
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.concurrent.thread
import kotlin.math.abs

class RegisterUserActivity : ComponentActivity()
{
    companion object
    {
        var bActionReady = false
        var bActivityLiving = false
    }
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent()
        {
            var context = LocalContext.current
            BackHandler(true)
            {
                thread{
                    runBlocking {
                        bActivityLiving = false
                        runBlocking{
                            Data.ServerSocket?.send("clearingMessageThread")
                            delay(20)
                        }
                        MainActivity.bUserActionReady = true
                        startActivity(Intent(context, MainActivity::class.java))
                    }
                }
            }
            var userName = remember{ mutableStateOf("") }
            var password = remember { mutableStateOf("") }
            var bAlertDialogOpened = remember{ mutableStateOf(false) }
            if (bActionReady)
            {
                bActionReady = false
                thread {
                    runBlocking {
                        while(bActivityLiving)
                        {
                            (Data.ServerSocket?.incoming?.receive() as Frame.Text?)?.readText()?.let{ registerResponse ->
                                when (registerResponse)
                                {
                                    "0" -> { bAlertDialogOpened.value = true }
                                    "1" -> {
                                        File(filesDir, "UserData").also{it.createNewFile()}.writeText("${userName.value} ${password.value}")
                                        bActivityLiving = false
                                        /*runBlocking{
                                            Data.ServerSocket?.send("clearingMessageThread")
                                            delay(20)
                                        }*/// interrupting thread in it, no need, new loop iteration won't start
                                        MainActivity.bUserActionReady = true
                                        startActivity(Intent(context, MainActivity::class.java))
                                    }
                                }
                            }
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
                centerVariable = MainActivity.lightCenterPos.value,
                colors = MainActivity.lightColor.value,
                colorStops = MainActivity.lightColorStops.value
            )
            {
                if (bAlertDialogOpened.value)
                {
                    var dragSt = remember{ mutableStateOf(Offset(0f, 0f)) }
                    AlertDialog(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(dragSt.value.x.dp, dragSt.value.y.dp)
                            .pointerInput(2) {
                                detectDragGestures { change, dragAmount -> dragSt.value += dragAmount }
                            },
                        onDismissRequest = { bAlertDialogOpened.value = false },
                        dismissButton = {
                            OutlinedButton(onClick =
                            {
                                bAlertDialogOpened.value = false
                            })
                            {
                                Text("Ок")
                            }
                        },
                        confirmButton = {
                        },
                        text = {
                            Text("Такое имя пользователя занято", color = Color.Red)
                        }
                    )
                }
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {

                    Spacer(modifier = Modifier.weight(1f))
                    GradientalBox(
                        modifier = Modifier.fillMaxWidth(),
                    )
                    {
                        Text("РЕГИСТРАЦИЯ")
                    }
                    Spacer(modifier = Modifier.weight(2f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    )
                    {
                        EditableTextBox(
                            modifier = Modifier
                                .background(object : ShaderBrush()
                                {
                                    override fun createShader(size: Size): Shader
                                    {
                                        return LinearGradientShader(
                                            Offset(0f, size.height),
                                            Offset(size.width, size.height),
                                            listOf(Yellow, Red, Yellow)
                                        )
                                    }

                                })
                                .padding(20.dp),
                            textSource = userName,
                            onValueChanged =
                            { newValue ->

                            },
                            label = { Text("Введите Имя Пользователя") },
                            onEntered =
                            { result ->

                            }
                        )
                        if ((userName.value.isNotEmpty()) && (password.value.isNotEmpty()))
                        {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable {
                                        runBlocking {
                                            Data.ServerSocket?.send("registerUser ${userName.value} ${password.value}")
                                        }
                                    }
                            )
                            {
                                Image(
                                    modifier = Modifier.matchParentSize(),
                                    painter = painterResource(id = R.drawable.img),
                                    contentDescription = null
                                )
                                Text("Зарегистрироваться", color = Color.Magenta)
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    )
                    {
                        EditableTextBox(
                            modifier = Modifier
                                .background(object : ShaderBrush()
                                {
                                    override fun createShader(size: Size): Shader
                                    {
                                        return LinearGradientShader(
                                            Offset(0f, size.height),
                                            Offset(size.width, size.height),
                                            listOf(Yellow, Red, Yellow)
                                        )
                                    }

                                })
                                .padding(20.dp),
                            textSource = password,
                            onValueChanged =
                            { newValue ->

                            },
                            label = { Text("Введите Пароль") },
                            onEntered =
                            { result ->

                            }
                        )
                    }
                    Spacer(Modifier.weight(0.7f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    )
                    {
                        Spacer(modifier = Modifier.weight(3f))
                        Spacer(modifier = Modifier.weight(3f))
                    }
                    Spacer(Modifier.weight(2f))
                }
            }
        }
    }
}