package com.akarsh.auraos

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Ink = Color(0xFF07111F)
private val Lilac = Color(0xFFC2B4FF)
private val AccentChoices = listOf(
    Color(0xFF8CEBFF), Color(0xFFC2B4FF), Color(0xFFFF9BCB), Color(0xFF9BFFCF), Color(0xFFFFD27D)
)

data class LaunchApp(val label: String, val packageName: String, val icon: android.graphics.drawable.Drawable)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(7, 17, 31)
        window.navigationBarColor = android.graphics.Color.rgb(7, 17, 31)
        setContent { AuraHome() }
    }
}

@Composable
fun AuraHome() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("aura_preferences", android.content.Context.MODE_PRIVATE) }
    var showApps by remember { mutableStateOf(false) }
    var showPersonalize by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var accentIndex by remember { mutableIntStateOf(prefs.getInt("accent_index", 0).coerceIn(0, AccentChoices.lastIndex)) }
    var glassOpacity by remember { mutableFloatStateOf(prefs.getFloat("glass_opacity", 0.72f).coerceIn(0.25f, 0.95f)) }
    var clock by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
    val accent = AccentChoices[accentIndex]
    val glass = Color(0xFF203045).copy(alpha = glassOpacity)
    val apps = remember {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .map { LaunchApp(it.loadLabel(pm).toString(), it.activityInfo.packageName, it.loadIcon(pm)) }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }
    LaunchedEffect(Unit) {
        while (true) {
            clock = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            kotlinx.coroutines.delay(30_000)
        }
    }
    val filtered = apps.filter { it.label.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true) }

    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Ink, Color(0xFF142840), Color(0xFF281E42))))) {
        Column(Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 18.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("A U R A", color = Color.White, fontSize = 13.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Bold)
                    Text(if (showPersonalize) "Make the glass yours." else "Your space. Your flow.", color = Color(0xFFB7C7DB), fontSize = 12.sp)
                }
                Surface(color = glass, shape = RoundedCornerShape(50), border = BorderStroke(1.dp, Color.White.copy(alpha = .18f))) {
                    Row(Modifier.clickable { showPersonalize = !showPersonalize; showApps = false }.padding(horizontal = 13.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, null, tint = accent, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(7.dp))
                        Text("AURA UI", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(22.dp))

            if (showPersonalize) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = accent, modifier = Modifier.clickable { showPersonalize = false }.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("PERSONALIZE", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Spacer(Modifier.height(18.dp))
                Surface(Modifier.fillMaxWidth(), color = glass, shape = RoundedCornerShape(26.dp), border = BorderStroke(1.dp, Color.White.copy(alpha = .2f))) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Accent colour", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        Text("Choose the glow used across Aura.", color = Color(0xFFC2CDDA), fontSize = 12.sp)
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AccentChoices.forEachIndexed { index, color ->
                                Box(
                                    Modifier.size(38.dp).clip(RoundedCornerShape(50)).background(color)
                                        .clickable { accentIndex = index; prefs.edit().putInt("accent_index", index).apply() }
                                        .then(if (index == accentIndex) Modifier.padding(4.dp).background(Color.White.copy(alpha = .25f), RoundedCornerShape(50)) else Modifier),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (index == accentIndex) Text("✓", color = Color(0xFF07111F), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Glass opacity", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        Text("Adjust how translucent the panels look.", color = Color(0xFFC2CDDA), fontSize = 12.sp)
                        Slider(
                            value = glassOpacity,
                            onValueChange = { glassOpacity = it; prefs.edit().putFloat("glass_opacity", it).apply() },
                            valueRange = 0.25f..0.95f,
                            colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent)
                        )
                        Text("${(glassOpacity * 100).toInt()}% opacity", color = accent, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(14.dp))
                GlassTile("Set Aura as default", "Choose Aura on the Home app screen", Modifier.fillMaxWidth(), accent, glass) {
                    context.startActivity(Intent(android.provider.Settings.ACTION_HOME_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
                Spacer(Modifier.height(10.dp))
                Text("Your colour and glass settings are saved on this device.", color = Color(0xFFB7C7DB), fontSize = 11.sp)
            } else {
                Surface(Modifier.fillMaxWidth(), color = glass, shape = RoundedCornerShape(30.dp), border = BorderStroke(1.dp, Color.White.copy(alpha = .22f))) {
                    Column(Modifier.padding(24.dp)) {
                        Text(SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()).uppercase(), color = accent, fontSize = 11.sp, letterSpacing = 2.sp)
                        Text(clock, color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Light, letterSpacing = (-2).sp)
                        Text("A calmer way to use your tablet.", color = Color(0xFFD3DEEA), fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    GlassTile("Explore apps", "Your apps, one place", Modifier.weight(1f), accent, glass) { showApps = true }
                    GlassTile("Personalize", "Colour and glass effects", Modifier.weight(1f), Lilac, glass) { showPersonalize = true }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (showApps) "APP LIBRARY" else "QUICK LAUNCH", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    TextButton(onClick = { showApps = !showApps; query = "" }) { Text(if (showApps) "Home" else "All apps", color = accent) }
                }
                if (showApps) {
                    OutlinedTextField(
                        value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search apps", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = accent) },
                        singleLine = true, shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = accent, unfocusedBorderColor = Color.White.copy(alpha = .25f), cursorColor = accent
                        )
                    )
                    LazyVerticalGrid(columns = GridCells.Adaptive(88.dp), modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filtered, key = { it.packageName }) { app -> AppShortcut(app) { launchApp(context, app) } }
                    }
                } else {
                    LazyVerticalGrid(columns = GridCells.Adaptive(78.dp), modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(apps.take(8), key = { it.packageName }) { app -> AppShortcut(app) { launchApp(context, app) } }
                    }
                }
                Surface(color = glass, shape = RoundedCornerShape(28.dp), border = BorderStroke(1.dp, Color.White.copy(alpha = .2f))) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Apps, "Apps", tint = accent, modifier = Modifier.size(24.dp).clickable { showApps = !showApps; query = "" })
                        Text("A U R A  •  LIQUID GLASS", color = Color.White.copy(alpha = .85f), fontSize = 10.sp, letterSpacing = 1.sp)
                        Icon(Icons.Default.Settings, "Personalize", tint = Lilac, modifier = Modifier.size(24.dp).clickable { showPersonalize = true })
                    }
                }
            }
        }
    }
}

private fun launchApp(context: android.content.Context, app: LaunchApp) {
    context.packageManager.getLaunchIntentForPackage(app.packageName)?.let { context.startActivity(it) }
}

@Composable
private fun AppShortcut(app: LaunchApp, onClick: () -> Unit) {
    Column(Modifier.clip(RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        AndroidAppIcon(app.icon)
        Spacer(Modifier.height(6.dp))
        Text(app.label, color = Color.White, fontSize = 11.sp, maxLines = 2)
    }
}

@Composable
private fun AndroidAppIcon(drawable: android.graphics.drawable.Drawable) {
    val bitmap = remember(drawable) {
        val bmp = android.graphics.Bitmap.createBitmap(96, 96, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bmp)
        drawable.setBounds(0, 0, 96, 96)
        drawable.draw(canvas)
        bmp.asImageBitmap()
    }
    Image(painter = androidx.compose.ui.graphics.painter.BitmapPainter(bitmap), contentDescription = null, modifier = Modifier.size(42.dp))
}

@Composable
private fun GlassTile(title: String, subtitle: String, modifier: Modifier, accent: Color, glass: Color, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(110.dp).clickable(onClick = onClick),
        color = glass, shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = .2f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(title, color = accent, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(5.dp))
            Text(subtitle, color = Color(0xFFC2CDDA), fontSize = 11.sp)
        }
    }
}
