package com.gabriela.charm

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class EventoReal(
    val titulo: String,
    val inicio: Long,
    val todoElDia: Boolean
)

private data class Tema(
    val fondo: Color,
    val tarjeta: Color,
    val acento: Color,
    val texto: Color,
    val suave: Color,
    val borde: Color,
    val acentoSecundario: Color
)

private fun leerEventosCalendario(context: Context): List<EventoReal> {
    val ahora = System.currentTimeMillis()
    val limite = ahora + 7L * 24 * 60 * 60 * 1000
    val uriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon()
    ContentUris.appendId(uriBuilder, ahora)
    ContentUris.appendId(uriBuilder, limite)
    val columnas = arrayOf(
        CalendarContract.Instances.TITLE,
        CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.ALL_DAY
    )
    val eventos = mutableListOf<EventoReal>()
    try {
        context.contentResolver.query(
            uriBuilder.build(), columnas, null, null,
            "${CalendarContract.Instances.BEGIN} ASC"
        )?.use { cursor ->
            val titulo = cursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
            val inicio = cursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
            val todoElDia = cursor.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)
            while (cursor.moveToNext()) {
                eventos.add(
                    EventoReal(
                        cursor.getString(titulo)?.takeIf { it.isNotBlank() } ?: "Evento sin título",
                        cursor.getLong(inicio),
                        cursor.getInt(todoElDia) != 0
                    )
                )
            }
        }
    } catch (_: Exception) {
        // La pantalla de agenda informa si no hay eventos disponibles.
    }
    return eventos
}

private fun formatoHora(evento: EventoReal, ingles: Boolean): String {
    val patron = if (evento.todoElDia) {
        if (ingles) "EEE, MMM d" else "EEE d MMM"
    } else {
        if (ingles) "h:mm a" else "HH:mm"
    }
    val locale = if (ingles) Locale.ENGLISH else Locale("es", "CO")
    return SimpleDateFormat(patron, locale).format(Date(evento.inicio))
}

private fun crearTema(oscuro: Boolean, acentoElegido: Int): Tema {
    val acentos = listOf(
        Color(0xFF36E7FF), Color(0xFF8B7CFF), Color(0xFFFF70C8)
    )
    val acento = acentos[acentoElegido.coerceIn(0, 2)]
    val secundario = when (acentoElegido) {
        1 -> Color(0xFF43D9FF)
        2 -> Color(0xFFFFB86B)
        else -> Color(0xFF8B7CFF)
    }
    return if (oscuro) {
        Tema(
            Color(0xFF070B16), Color(0xFF111B2B), acento,
            Color(0xFFF2F7FF), Color(0xFF9EADC4),
            Color(0xFF263B53), secundario
        )
    } else {
        Tema(
            Color(0xFFF3F8FC), Color(0xFFFFFFFF),
            when (acentoElegido) {
                1 -> Color(0xFF6655D9)
                2 -> Color(0xFFC0448B)
                else -> Color(0xFF007F9B)
            },
            Color(0xFF142337), Color(0xFF65768C),
            Color(0xFFD6E3EE), Color(0xFF5A72D8)
        )
    }
}

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
    val contexto = LocalContext.current
    var permisoCalendario by remember {
        mutableStateOf(
            contexto.checkSelfPermission(Manifest.permission.READ_CALENDAR) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var eventosReales by remember { mutableStateOf<List<EventoReal>>(emptyList()) }
    val solicitarPermisoCalendario = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> permisoCalendario = concedido }

    val preferencias = remember {
        contexto.getSharedPreferences("charm_preferencias", Context.MODE_PRIVATE)
    }
    var pantalla by remember { mutableStateOf("inicio") }
    var ingles by remember { mutableStateOf(preferencias.getBoolean("ingles", false)) }
    var acentoElegido by remember { mutableStateOf(preferencias.getInt("acentoElegido", 0).coerceIn(0, 2)) }
    var modoOscuro by remember { mutableStateOf(preferencias.getBoolean("modoOscuro", true)) }
    var consejoIndex by remember { mutableStateOf(0) }
    val t = crearTema(modoOscuro, acentoElegido)
    val nombreEvento = if (ingles) "Lights Festival" else "Festival de Luces"

    LaunchedEffect(pantalla, permisoCalendario) {
        if (pantalla == "agenda" && permisoCalendario) {
            eventosReales = withContext(Dispatchers.IO) { leerEventosCalendario(contexto) }
        }
    }

    Surface(Modifier.fillMaxSize(), color = t.fondo) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(t.fondo, t.fondo, t.tarjeta.copy(alpha = 0.72f))
                    )
                )
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(30.dp)
                                .background(t.acento.copy(alpha = 0.13f), CircleShape)
                                .border(1.dp, t.acento.copy(alpha = 0.65f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✦", color = t.acento, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("CHARM", color = t.texto, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                            Text("SMART MOMENTS", color = t.suave, fontSize = 7.sp, letterSpacing = 1.5.sp)
                        }
                    }
                    Text(if (ingles) "SYSTEM ONLINE" else "SISTEMA ACTIVO",
                        color = t.acento, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(18.dp))

                when (pantalla) {
                    "inicio" -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(listOf(t.tarjeta, t.tarjeta.copy(alpha = 0.62f)))
                                )
                                .border(1.dp, t.borde, RoundedCornerShape(24.dp))
                                .padding(vertical = 20.dp, horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (ingles) "YOUR WORLD, IN SYNC" else "TU MUNDO, EN SINTONÍA",
                                    color = t.suave, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                                Spacer(Modifier.height(9.dp))
                                Box(
                                    Modifier
                                        .size(92.dp)
                                        .border(1.dp, t.acento.copy(alpha = 0.3f), CircleShape)
                                        .padding(7.dp)
                                        .border(2.dp, t.acento, CircleShape)
                                        .padding(5.dp)
                                        .background(t.fondo, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("✦", color = t.acento, fontSize = 18.sp)
                                        Text("CHARM", color = t.texto, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                                        Text(if (ingles) "READY" else "LISTO", color = t.acento, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Text(nombreEvento, color = t.texto, fontSize = 17.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Text(if (ingles) "YOUR NEXT MOMENT" else "TU PRÓXIMO MOMENTO",
                                    color = t.acento, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Spacer(Modifier.height(5.dp))
                                Text(if (ingles) "Your events and plans, always close." else "Tus eventos y planes, siempre contigo.",
                                    color = t.suave, fontSize = 9.sp, textAlign = TextAlign.Center)
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        Text(if (ingles) "QUICK ACCESS" else "ACCESO RÁPIDO",
                            color = t.suave, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(8.dp))
                        Acceso("▦", if (ingles) "MY DIGITAL PASS" else "MI ENTRADA DIGITAL", if (ingles) "Your event pass" else "Tu pase para el evento", t) { pantalla = "entrada" }
                        Acceso("◷", if (ingles) "MY AGENDA" else "MI AGENDA", if (ingles) "Calendar · next 7 days" else "Calendario · próximos 7 días", t) { pantalla = "agenda" }
                        Acceso("✧", if (ingles) "CHARM AI" else "CHARM IA", if (ingles) "Tips for your experience" else "Consejos para tu experiencia", t) { pantalla = "asistente" }
                        Acceso("⚙", if (ingles) "PERSONALIZE" else "PERSONALIZAR", if (ingles) "Language and appearance" else "Idioma y apariencia", t) { pantalla = "ajustes" }
                    }

                    "entrada" -> {
                        Titulo(
                            if (ingles) "DIGITAL PASS" else "ENTRADA DIGITAL",
                            if (ingles) "YOUR EVENT AT A GLANCE" else "TU EVENTO A UN VISTAZO",
                            t
                        )
                        Spacer(Modifier.height(12.dp))
                        Tarjeta(t) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(t.acento.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
                                    .border(1.dp, t.acento.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                    .padding(vertical = 12.dp, horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "CHARM  /  PASS 001",
                                        color = t.acento,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    Text(
                                        if (ingles) "EVENT PREVIEW" else "VISTA PREVIA DEL EVENTO",
                                        color = t.suave,
                                        fontSize = 7.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(18.dp))
                            Box(
                                Modifier
                                    .size(96.dp)
                                    .background(Color.White, RoundedCornerShape(14.dp))
                                    .border(1.dp, t.acento.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "✦\nCHARM\n✦",
                                    color = Color(0xFF111827),
                                    fontSize = 17.sp,
                                    lineHeight = 21.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(Modifier.height(14.dp))
                            Text(
                                nombreEvento,
                                color = t.texto,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (ingles) "DEMO · NOT A VALID TICKET" else "DEMOSTRACIÓN · NO ES UNA ENTRADA VÁLIDA",
                                color = t.acento,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(14.dp))
                            Box(
                                Modifier.fillMaxWidth()
                                    .background(t.fondo, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        if (ingles) "DATE & TIME" else "FECHA Y HORA",
                                        color = t.suave,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        if (ingles) "Demo · 7:30 PM" else "Demostración · 7:30 p. m.",
                                        color = t.texto,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        if (ingles) "LOCATION" else "UBICACIÓN",
                                        color = t.suave,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        if (ingles) "Main stage · demo" else "Escenario principal · demo",
                                        color = t.texto,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
                                if (ingles) "This preview has no scannable QR code." else "Esta vista previa no contiene un código QR escaneable.",
                                color = t.suave,
                                fontSize = 8.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Acceso(
                            "←",
                            if (ingles) "BACK TO HOME" else "VOLVER AL INICIO",
                            "",
                            t
                        ) { pantalla = "inicio" }
                    }

                    "agenda" -> {
                        Titulo(if (ingles) "MY AGENDA" else "MI AGENDA", if (ingles) "THE NEXT SEVEN DAYS" else "LOS PRÓXIMOS SIETE DÍAS", t)
                        Spacer(Modifier.height(12.dp))
                        Tarjeta(t) {
                            if (!permisoCalendario) {
                                Text(if (ingles) "Allow calendar access to display your real events." else "Autoriza el calendario para mostrar tus eventos reales.",
                                    color = t.texto, fontSize = 11.sp, textAlign = TextAlign.Center)
                                Spacer(Modifier.height(10.dp))
                                Acceso("＋", if (ingles) "ALLOW CALENDAR" else "PERMITIR CALENDARIO", "", t) {
                                    solicitarPermisoCalendario.launch(Manifest.permission.READ_CALENDAR)
                                }
                            } else if (eventosReales.isEmpty()) {
                                Text(if (ingles) "No events found in the next 7 days." else "No hay eventos en los próximos 7 días.",
                                    color = t.texto, fontSize = 11.sp, textAlign = TextAlign.Center)
                            } else {
                                eventosReales.forEachIndexed { index, item ->
                                    Agenda(formatoHora(item, ingles), item.titulo, t)
                                    if (index != eventosReales.lastIndex) Spacer(Modifier.height(4.dp))
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Acceso("←", if (ingles) "BACK TO HOME" else "VOLVER AL INICIO", "", t) { pantalla = "inicio" }
                    }

                    "asistente" -> {
                        Titulo(if (ingles) "CHARM AI" else "CHARM IA", if (ingles) "A LITTLE SMARTER, EVERY MOMENT" else "UN TOQUE MÁS INTELIGENTE, CADA MOMENTO", t)
                        Spacer(Modifier.height(14.dp))
                        Box(
                            Modifier.size(74.dp).background(t.acento.copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, t.acento.copy(alpha = 0.7f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) { Text("✧", color = t.acento, fontSize = 38.sp, fontWeight = FontWeight.Light) }
                        Spacer(Modifier.height(14.dp))
                        Tarjeta(t) {
                            Text(if (ingles) "YOUR SMART TIP" else "TU CONSEJO INTELIGENTE",
                                color = t.acento, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                when (consejoIndex % 3) {
                                    0 -> if (ingles) "Arrive 15 minutes early and keep your pass ready." else "Llega 15 minutos antes y ten lista tu entrada."
                                    1 -> if (ingles) "Keep your watch charged and check the event schedule." else "Lleva el reloj cargado y revisa los horarios del evento."
                                    else -> if (ingles) "Choose a meeting point in case your group gets separated." else "Acuerda un punto de encuentro por si te separas de tu grupo."
                                },
                                color = t.texto, fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 18.sp
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Acceso("✧", if (ingles) "ANOTHER TIP" else "OTRO CONSEJO", if (ingles) "Discover another suggestion" else "Descubre otra sugerencia", t) { consejoIndex++ }
                        Acceso("▦", if (ingles) "MY DIGITAL PASS" else "MI ENTRADA DIGITAL", "", t) { pantalla = "entrada" }
                        Acceso("←", if (ingles) "BACK TO HOME" else "VOLVER AL INICIO", "", t) { pantalla = "inicio" }
                    }

                    "ajustes" -> {
                        Titulo(if (ingles) "PERSONALIZATION" else "PERSONALIZACIÓN", if (ingles) "MAKE CHARM YOURS" else "HAZ CHARM A TU MANERA", t)
                        Spacer(Modifier.height(12.dp))
                        Tarjeta(t) {
                            Text(if (ingles) "COLOR SIGNATURE" else "COLOR DE IDENTIDAD",
                                color = t.suave, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(Modifier.height(10.dp))
                            listOf(
                                Pair(if (ingles) "CYBER CYAN" else "CIAN CIBERNÉTICO", Color(0xFF36E7FF)),
                                Pair(if (ingles) "NEBULA VIOLET" else "VIOLETA NÉBULA", Color(0xFF8B7CFF)),
                                Pair(if (ingles) "LASER PINK" else "ROSA LÁSER", Color(0xFFFF70C8))
                            ).forEachIndexed { index, item ->
                                Button(
                                    onClick = {
                                        acentoElegido = index
                                        preferencias.edit().putInt("acentoElegido", index).apply()
                                    },
                                    modifier = Modifier.fillMaxWidth().height(42.dp).padding(vertical = 2.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (acentoElegido == index) item.second.copy(alpha = 0.18f) else t.tarjeta,
                                        contentColor = t.texto
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Box(Modifier.size(12.dp).background(item.second, CircleShape))
                                        Spacer(Modifier.width(10.dp))
                                        Text(item.first, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.weight(1f))
                                        Text(if (acentoElegido == index) "✓" else "○", color = item.second, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Acceso("文", if (ingles) "LANGUAGE · ENGLISH" else "IDIOMA · ESPAÑOL",
                            if (ingles) "Tap to switch to Spanish" else "Toca para cambiar a inglés", t) {
                            val nuevoIdioma = !ingles
                            ingles = nuevoIdioma
                            preferencias.edit().putBoolean("ingles", nuevoIdioma).apply()
                        }
                        Acceso(if (modoOscuro) "☾" else "☀", if (ingles) {
                            if (modoOscuro) "DARK MODE" else "LIGHT MODE"
                        } else {
                            if (modoOscuro) "MODO OSCURO" else "MODO CLARO"
                        }, if (ingles) "Tap to change appearance" else "Toca para cambiar la apariencia", t) {
                            val nuevoTema = !modoOscuro
                            modoOscuro = nuevoTema
                            preferencias.edit().putBoolean("modoOscuro", nuevoTema).apply()
                        }
                        Spacer(Modifier.height(8.dp))
                        Acceso("←", if (ingles) "BACK TO HOME" else "VOLVER AL INICIO", "", t) { pantalla = "inicio" }
                    }
                }

                Spacer(Modifier.height(18.dp))
                Text("CHARM  •  MADE FOR YOUR MOMENTS",
                    color = t.suave.copy(alpha = 0.75f), fontSize = 7.sp,
                    textAlign = TextAlign.Center, letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun Titulo(titulo: String, subtitulo: String, t: Tema) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(titulo, color = t.texto, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(5.dp))
        Text(subtitulo, color = t.acento, fontSize = 7.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun Tarjeta(t: Tema, contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .background(t.tarjeta, RoundedCornerShape(22.dp))
            .border(1.dp, t.borde, RoundedCornerShape(22.dp))
            .padding(horizontal = 14.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = contenido
    )
}

@Composable
private fun Acceso(
    icono: String,
    titulo: String,
    subtitulo: String,
    t: Tema,
    accion: () -> Unit
) {
    Button(
        onClick = accion,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).defaultMinSize(minHeight = 50.dp),
        shape = RoundedCornerShape(17.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = t.tarjeta,
            contentColor = t.texto
        ),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 2.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(32.dp).background(t.acento.copy(alpha = 0.12f), RoundedCornerShape(11.dp))
                    .border(1.dp, t.acento.copy(alpha = 0.35f), RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icono, color = t.acento, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                Text(titulo, color = t.texto, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.35.sp)
                if (subtitulo.isNotBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(subtitulo, color = t.suave, fontSize = 8.sp, lineHeight = 11.sp)
                }
            }
            Text("›", color = t.acento, fontSize = 22.sp, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
private fun Agenda(hora: String, actividad: String, t: Tema) {
    Row(
        Modifier.fillMaxWidth()
            .background(t.fondo.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(3.dp).height(30.dp).background(t.acento, RoundedCornerShape(2.dp)))
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            Text(hora, color = t.acento, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text(actividad, color = t.texto, fontSize = 10.sp, lineHeight = 13.sp)
        }
    }
}
