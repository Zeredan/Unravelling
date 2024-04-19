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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.unravelling.ui.theme.Pink40
import kotlinx.coroutines.delay
import kotlin.math.abs

class RulesActivity : ComponentActivity()
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
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Spacer(modifier = Modifier.weight(1f))
                    GradientalBox(
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Text("ПРАВИЛА")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.Center
                    )
                    {
                        F {
                            MultiColoredText(label = "Правила данной игры:\nВам дана запутанная сеть(граф) из вершин в виде огненных шаров\nи ребер. Необходимо её распутать перестановками двух выбранных шаров\n1)Если нить подсвечивается красной, значит она пересекается с другой нитью\n2)Если нить белая - все хорошо, пересечений нет\n3)Для того чтобы поменять две вершины нажмите одну из вершин, она увеличится\nи затем выберите другую вершину.\nЕсли необходимо отменить выбор вершины нажмите на неё еще раз", source = mutableListOf(Color.Magenta))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Spacer(modifier = Modifier.weight(2f))
                        Text("Нормальная часть сети", color = White)
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            modifier = Modifier.size(200.dp, 200.dp),
                            painter = painterResource(id = R.drawable.normalgraph),
                            contentDescription = "нормальная часть"
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Spacer(modifier = Modifier.weight(2f))
                        Text("Запутанная часть сети", color = White)
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            modifier = Modifier.size(200.dp, 200.dp),
                            painter = painterResource(id = R.drawable.foldedgraph),
                            contentDescription = "запутанная часть"
                        )
                    }
                }
            }
        }
    }
}