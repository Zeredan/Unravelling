package com.example.unravelling

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.unravelling.ui.theme.Pink40
import kotlinx.coroutines.delay
import kotlin.math.abs

class TutorialActivity : ComponentActivity()
{
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent()
        {
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
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    var userName = remember{ mutableStateOf("") }
                    var password = remember { mutableStateOf("") }

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
                        GradientalBox(
                        )
                        {
                            Text("Войти через Google", color = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        GradientalBox(
                        )
                        {
                            Text("Войти через тело Пуджа", color = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        GradientalBox(
                        )
                        {
                            Text("Войти через ногу Альпаки", color = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(3f))
                    }
                    Spacer(Modifier.weight(2f))
                }
            }
        }
    }
}