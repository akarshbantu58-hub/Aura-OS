package com.akarsh.auraos

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
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
private val Glass = Color(0xB8203045)
private val Cyan = Color(0xFF8CEBFF)
private val Lilac = Color(0xFFC2B4FF)

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
    var showApps by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var clock by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
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
    Box(
        Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Color(0xFF07111F), Color(0xFF142840), Color(0xFF281E42)))
        )
    ) {
        Column(Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 18.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("A U R A", color = Color.White, fontSize = 13.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Bold)
                    Text("Your space. Your flow.", color = Color(0xFFB7C7DB), fontSize = 12.sp)
                }
                Surface(color = Glass, shape = RoundedCornerShape(50), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .18f)) ) {
                    Row(Modifier.padding(horizontal = 13.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, null, tint = Cyan, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(7.dp))
                        Text("AURA UI", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Glass,
                shape = RoundedCornerShape(30.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .22f))
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()).uppercase(), color = Cyan, fontSize = 11.sp, letterSpacing = 2.sp)
                    Text(clock, color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Light, letterSpacing = (-2).sp)
                    Text("A calmer way to use your tablet.", color = Color(0xFFD3DEEA), fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                GlassTile("Explore apps", "Your apps, one place", Modifier.weight(1f), Cyan) { showApps = true }
                GlassTile("Personalize", "Make it yours", Modifier.weight(1f), Lilac) {
                    context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (showApps) "APP LIBRARY" else "QUICK LAUNCH", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                TextButton(onClick = { showApps = !showApps; query = "" }) { Text(if (showApps) "Home" else "All apps", color = Cyan) }
            }
            if (showApps) {
                OutlinedTextField(
                    value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search apps", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Cyan) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = Cyan, unfocusedBorderColor = Color.White.copy(alpha = .25f),
                        cursorColor = Cyan
                    )
                )
                LazyVerticalGrid(columns = GridCells.Adaptive(88.dp), contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered, key = { it.packageName }) { app ->
                        Column(
                            Modifier.clip(RoundedCornerShape(18.dp)).clickable {
                                val launch = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                if (launch != null) context.startActivity(launch)
                            }.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AndroidAppIcon(app.icon)
                            Spacer(Modifier.height(6.dp))
                            Text(app.label, color = Color.White, fontSize = 11.sp, maxLines = 2)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Adaptive(78.dp), modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(apps.take(8), key = { it.packageName }) { app ->
                        Column(Modifier.clip(RoundedCornerShape(18.dp)).clickable {
                            context.packageManager.getLaunchIntentForPackage(app.packageName)?.let { context.startActivity(it) }
                        }.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            AndroidAppIcon(app.icon)
                            Spacer(Modifier.height(5.dp))
                            Text(app.label, color = Color.White, fontSize = 10.sp, maxLines = 1)
                        }
                    }
                }
            }
            Surface(color = Color(0xA61B2A40), shape = RoundedCornerShape(28.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .2f))) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Apps, "Apps", tint = Cyan, modifier = Modifier.size(24.dp).clickable { showApps = !showApps })
                    Text("A U R A   •   LIQUID GLASS", color = Color.White.copy(alpha = .85f), fontSize = 10.sp, letterSpacing = 1.sp)
                    Icon(Icons.Default.Settings, "Settings", tint = Lilac, modifier = Modifier.size(24.dp).clickable {
                        context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    })
                }
            }
        }
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
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.graphics.painter.BitmapPainter(bitmap),
        contentDescription = null,
        modifier = Modifier.size(42.dp)
    )
}

@Composable
private fun GlassTile(title: String, subtitle: String, modifier: Modifier, accent: Color, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(110.dp).clickable(onClick = onClick),
        color = Glass, shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .2f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(title, color = accent, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(5.dp))
            Text(subtitle, color = Color(0xFFC2CDDA), fontSize = 11.sp)
        }
    }
}
