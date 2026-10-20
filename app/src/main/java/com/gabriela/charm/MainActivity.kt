
package com.gabriela.charm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Tema(
    val fondo: Color,
    val tarjeta: Color,
    val acento: Color,
    val texto: Color,
    val suave: Color,
    val borde: Color
)

private val temas = listOf(
    Tema(
        Color(0xFF10101C), Color(0xFF242238),
        Color(0xFFC4B0FF), Color(0xFFFFFAFF),
        Color(0xFFB9B3CE), Color(0xFF51466F)
    ),
    Tema(
        Color(0xFF101A20), Color(0xFF203039),
        Color(0xFF9BE6D0), Color(0xFFF5FFFC),
        Color(0xFFB7D0CA), Color(0xFF36564F)
    ),
    Tema(
        Color(0xFF20151D), Color(0xFF382431),
        Color(0xFFFFBFD7), Color(0xFFFFF8FC),
        Color(0xFFD8B8C8), Color(0xFF694256)
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PantallaCHARM()
            }
        }
    }
}

@Composable
fun PantallaCHARM() {
    var pantalla by remember { mutableStateOf("inicio") }
    var ingles by remember { mutableStateOf(false) }
    var temaElegido by remember { mutableStateOf(0) }
    var modoOscuro by remember { mutableStateOf(true) }
    var consejoIndex by remember { mutableStateOf(0) }

    val base = temas[temaElegido]
    val t = if (modoOscuro) base else Tema(
        Color(0xFFF7F5FC),
        Color(0xFFFFFFFF),
        when (temaElegido) {
            1 -> Color(0xFF287A66)
            2 -> Color(0xFFB34476)
            else -> Color(0xFF7050B5)
        },
        Color(0xFF29243A),
        Color(0xFF625B70),
        Color(0xFFE2DCEF)
    )
    val evento = if (ingles) "Lights Festival" else "Festival de Luces"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = t.fondo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(t.fondo)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "CHARM",
                    color = t.acento,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "✦  01",
                    color = t.suave,
                    fontSize = 9.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            when (pantalla) {
                "inicio" -> {
                    Acceso(
                        if (ingles) "▣  MY TICKET"
                        else "▣  MI ENTRADA",
                        t
                    ) { pantalla = "entrada" }

                    Spacer(Modifier.height(6.dp))

                    Acceso(
                        if (ingles) "◷  MY AGENDA"
                        else "◷  MI AGENDA",
                        t
                    ) { pantalla = "agenda" }

                    Spacer(Modifier.height(6.dp))

                    Acceso(
                        if (ingles) "✦  CHARM AI"
                        else "✦  CHARM IA",
                        t
                    ) { pantalla = "asistente" }

                    Spacer(Modifier.height(6.dp))

                    Acceso(
                        if (ingles) "⚙  SETTINGS"
                        else "⚙  AJUSTES",
                        t
                    ) { pantalla = "ajustes" }

                    Text(
                        if (ingles) "YOUR NEXT MOMENT"
                        else "TU PRÓXIMO PLAN",
                        color = t.suave,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(t.tarjeta, CircleShape)
                            .border(1.dp, t.borde, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "✧",
                            color = t.acento,
                            fontSize = 27.sp
                        )
                    }

                    Spacer(Modifier.height(9.dp))

                    Text(
                        evento,
                        color = t.texto,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        if (ingles) "TODAY · 7:30 PM"
                        else "HOY · 7:30 PM",
                        color = t.acento,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(12.dp))

                    Tarjeta(t) {
                        Text(
                            if (ingles) "YOUR EVENT"
                            else "TU EVENTO",
                            color = t.suave,
                            fontSize = 9.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (ingles) "Main stage"
                            else "Escenario principal",
                            color = t.texto,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(10.dp))


                }

                "entrada" -> {
                    Titulo(
                        if (ingles) "MY DIGITAL PASS"
                        else "MI ENTRADA",
                        t
                    )

                    Spacer(Modifier.height(12.dp))

                    Tarjeta(t) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .background(
                                    Color.White,
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "▦ ▦ ▦\n▦   ▦\n▦ ▦ ▦",
                                color = t.fondo,
                                fontSize = 17.sp,
                                lineHeight = 19.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            evento,
                            color = t.texto,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            if (ingles) "DEMO · NOT SCANNABLE"
                            else "PASE DE DEMOSTRACIÓN",
                            color = t.acento,
                            fontSize = 8.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            if (ingles) "Today · 7:30 PM"
                            else "Hoy · 7:30 PM",
                            color = t.suave,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    Acceso(
                        if (ingles) "← HOME" else "← INICIO",
                        t
                    ) { pantalla = "inicio" }
                }

                "agenda" -> {
                    Titulo(
                        if (ingles) "MY AGENDA" else "MI AGENDA",
                        t
                    )

                    Spacer(Modifier.height(8.dp))

                    Tarjeta(t) {
                        Agenda("07:30", if (ingles)
                            "Festival opens" else "Apertura", t)
                        Agenda("08:00", if (ingles)
                            "Light show" else "Show de luces", t)
                        Agenda("09:00", if (ingles)
                            "Main performance" else "Presentación", t)
                    }

                    Spacer(Modifier.height(12.dp))
                    Acceso(
                        if (ingles) "← HOME" else "← INICIO",
                        t
                    ) { pantalla = "inicio" }
                }

                "asistente" -> {
                    Titulo(
                        if (ingles) "CHARM AI" else "CHARM IA",
                        t
                    )

                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(t.tarjeta, CircleShape)
                            .border(1.dp, t.borde, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✦", color = t.acento, fontSize = 24.sp)
                    }

                    Spacer(Modifier.height(10.dp))

                    Tarjeta(t) {
                        Text(
                            if (ingles) "YOUR TIP"
                            else "TU CONSEJO",
                            color = t.acento,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(7.dp))

                        Text(
                            when (consejoIndex % 3) {
                                0 -> if (ingles)
                                    "Arrive 15 minutes early and keep your pass ready."
                                else
                                    "Llega 15 minutos antes y ten lista tu entrada."
                                1 -> if (ingles)
                                    "Keep your watch charged and check the event schedule."
                                else
                                    "Lleva el reloj cargado y revisa los horarios del evento."
                                else -> if (ingles)
                                    "Choose a meeting point in case your group gets separated."
                                else
                                    "Acuerda un punto de encuentro por si te separas de tu grupo."
                            },
                            color = t.texto,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Acceso(
                        if (ingles) "ANOTHER TIP" else "OTRO CONSEJO",
                        t
                    ) { consejoIndex++ }

                    Spacer(Modifier.height(6.dp))
                    Acceso(
                        if (ingles) "MY TICKET" else "MI ENTRADA",
                        t
                    ) { pantalla = "entrada" }

                    Spacer(Modifier.height(6.dp))
                    Acceso(
                        if (ingles) "← HOME" else "← INICIO",
                        t
                    ) { pantalla = "inicio" }
                }

                "ajustes" -> {
                    Titulo(
                        if (ingles) "APPEARANCE"
                        else "APARIENCIA",
                        t
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        if (ingles) "CHOOSE A COLOR"
                        else "ELIGE TU COLOR",
                        color = t.suave,
                        fontSize = 9.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    temas.forEachIndexed { indice, tema ->
                        Button(
                            onClick = { temaElegido = indice },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = tema.tarjeta,
                                contentColor = tema.acento
                            ),
                            contentPadding = PaddingValues(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                        ) {
                            Text(
                                when (indice) {
                                    0 -> if (ingles) "LAVENDER" else "LAVANDA"
                                    1 -> if (ingles) "MINT" else "MENTA"
                                    else -> if (ingles) "ROSE" else "ROSA"
                                } + if (temaElegido == indice) "  ✓" else "",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(5.dp))
                    }

                    Spacer(Modifier.height(8.dp))

                    Acceso(
                        if (ingles) "LANGUAGE: EN"
                        else "IDIOMA: ES",
                        t
                    ) { ingles = !ingles }

                    Spacer(Modifier.height(6.dp))

                    Acceso(
                        if (ingles) {
                            if (modoOscuro) "THEME: DARK" else "THEME: LIGHT"
                        } else {
                            if (modoOscuro) "TEMA: OSCURO" else "TEMA: CLARO"
                        },
                        t
                    ) { modoOscuro = !modoOscuro }

                    Spacer(Modifier.height(6.dp))

                    Acceso(
                        if (ingles) "← HOME" else "← INICIO",
                        t
                    ) { pantalla = "inicio" }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "MADE FOR YOUR MOMENTS",
                color = t.suave,
                fontSize = 7.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun Titulo(texto: String, t: Tema) {
    Text(
        texto,
        color = t.acento,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Tarjeta(
    t: Tema,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(t.tarjeta, RoundedCornerShape(15.dp))
            .border(1.dp, t.borde, RoundedCornerShape(15.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = contenido
    )
}

@Composable
private fun Acceso(
    texto: String,
    t: Tema,
    accion: () -> Unit
) {
    Button(
        onClick = accion,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = t.tarjeta,
            contentColor = t.acento
        ),
        contentPadding = PaddingValues(
            horizontal = 5.dp,
            vertical = 3.dp
        )
    ) {
        Text(
            texto,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun Agenda(hora: String, actividad: String, t: Tema) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            hora,
            color = t.acento,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(8.dp))
        Text(
            actividad,
            color = t.texto,
            fontSize = 10.sp
        )
    }
}
