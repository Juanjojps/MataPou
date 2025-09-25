package com.example.huevo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huevo.ui.theme.HuevoTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HuevoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val taps = rememberSaveable { mutableIntStateOf(0) }
    val crackGoal = 10
    val hatchGoal = 20

    // Estado para la animación de rebote
    val scaleTarget = remember { mutableFloatStateOf(1f) }

    // Animación de escala suave
    val scale = animateFloatAsState(
        targetValue = scaleTarget.value,
        animationSpec = tween(
            durationMillis = 200,
            easing = androidx.compose.animation.core.EaseOutElastic
        ),
        label = "bounceAnimation"
    )

    // Calcular progreso (0-100%)
    val progress = if (hatchGoal > 0) {
        (taps.value.toFloat() / hatchGoal).coerceIn(0f, 1f)
    } else {
        0f
    }
    val percentage = (progress * 100).toInt()

    // Determinar si el Pou ha muerto (desactivar clicks)
    val isDead = taps.value >= hatchGoal

    // Determinar la fase del huevo con imágenes
    val (imageRes, message) = when {
        isDead -> Pair(R.drawable.pou_muerto, "¡Señores, Pou ha muerto!")
        taps.value >= crackGoal -> Pair(R.drawable.pou_enfermo, "¡Ya casi!")
        else -> Pair(R.drawable.pou, "Pégale")
    }

    // Función para activar la animación de rebote
    fun triggerBounce() {
        scaleTarget.value = 1.1f
        GlobalScope.launch {
            delay(100)
            scaleTarget.value = 1f
        }
    }

    // Función para resetear
    fun resetTaps() {
        taps.value = 0
        triggerBounce()
    }

    // Función para sumar 5 taps (boost)
    fun boostTaps() {
        if (!isDead) {
            val newValue = (taps.value + 5).coerceAtMost(hatchGoal)
            taps.value = newValue
            triggerBounce()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = !isDead, // Desactivar click cuando esté muerto
                onClick = {
                    taps.value++
                    triggerBounce()
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = if (isDead) "Pou muerto" else "Pou en desarrollo",
            modifier = Modifier
                .size(220.dp)
                .scale(scale.value)
                .padding(16.dp)
        )

        Text(
            text = message,
            fontSize = 28.sp,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
        )

        // Barra de progreso y porcentaje
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = "$percentage%",
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .width(280.dp)
                    .padding(horizontal = 32.dp)
            )
        }

        Text(
            text = "Toques: ${taps.value}/$hatchGoal",
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Botones Reset y +5
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 20.dp)
        ) {
            // Botón Reset
            Button(
                onClick = { resetTaps() },
                modifier = Modifier.width(120.dp)
            ) {
                Text("Reset")
            }

            // Botón +5 (solo activo si no está muerto)
            Button(
                onClick = { boostTaps() },
                enabled = !isDead && taps.value < hatchGoal,
                modifier = Modifier.width(120.dp)
            ) {
                Text("+5")
            }
        }

        Text(
            text = "Meta: cambio a $crackGoal, final a $hatchGoal",
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Mensaje cuando está muerto
        if (isDead) {
            Text(
                text = "💀 El Pou ha alcanzado su destino final",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HuevoTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingScreenPreview() {
    HuevoTheme {
        GameScreen()
    }
}

@Composable
fun GameScreen() {
    TODO("Not yet implemented")
}