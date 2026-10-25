// app/src/main/java/com/example/ui/ComidarScreens.kt
package com.example.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LocalizationDictionary
import kotlin.math.cos
import kotlin.math.sin

// --- Custom Theme Color Palette for Syntropy Delta Labs ---
object ComidarColors {
    val SlateDark = Color(0xFF0F172A)
    val GlassSurface = Color(0x331E293B)
    val GlassSurfaceHigh = Color(0x551E293B)
    val GlassBorder = Color(0x4D64748B)
    val PremiumAccent = Color(0xFFF97316) // Hot-dog-orange / gold accent
    val PrimaryPurple = Color(0xFF8B5CF6)
    val NeonCyan = Color(0xFF06B6D4)
    val SecureGreen = Color(0xFF10B981)
    val CriticalRed = Color(0xFFEF4444)
    val SoftWhite = Color(0xFFF8FAFC)
    val RadarGreen = Color(0xFF00FF66)
}

@Composable
fun GlassmorphicContainer(
    modifier: Modifier = Modifier,
    borderColor: Color = ComidarColors.GlassBorder,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ComidarColors.GlassSurface)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(16.dp),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationWrapper(viewModel: AppViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isRooted = viewModel.isDeviceRooted
    val language by viewModel.language.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }

    Scaffold(
        containerColor = ComidarColors.SlateDark,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(ComidarColors.PremiumAccent, ComidarColors.PrimaryPurple)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Fastfood,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = LocalizationDictionary.getString("app_title"),
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Black,
                            color = ComidarColors.SoftWhite,
                            fontSize = 19.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    // Language Switcher
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("btn_language_toggle")
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ComidarColors.GlassSurfaceHigh,
                            border = BorderStroke(1.dp, ComidarColors.GlassBorder)
                        ) {
                            Text(
                                text = if (language.name == "ES") "🇻🇪 ES" else "🇺🇸 EN",
                                color = ComidarColors.PremiumAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (currentScreen != "LOGIN" && currentScreen != "TFA") {
                BottomNavigationBar(
                    viewModel = viewModel,
                    currentScreen = currentScreen,
                    cartBadgeCount = cartCount
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ComidarColors.SlateDark,
                            Color(0xFF13132B),
                            Color(0xFF0A0F1D)
                        )
                    )
                )
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    "LOGIN" -> LoginScreen(viewModel = viewModel)
                    "TFA" -> TfaScreen(viewModel = viewModel)
                    "DASHBOARD" -> DashboardScreen(viewModel = viewModel)
                    "CHEF_AI" -> ChefAiScreen(viewModel = viewModel)
                    "CART" -> CartScreen(viewModel = viewModel)
                    "TRACKER" -> TrackerScreen(viewModel = viewModel)
                    "MARKETING" -> MarketingCampaignScreen(viewModel = viewModel)
                    "SECURITY" -> SecurityAuditScreen(viewModel = viewModel)
                }
            }

            // Root Warning Banner
            if (isRooted) {
                Surface(
                    color = ComidarColors.CriticalRed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                ) {
                    Text(
                        text = "⚠️ INTEGRIDAD EN RIESGO: DISPOSITIVO ROOT DETECTADO - MODO PROTEGIDO",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(viewModel: AppViewModel, currentScreen: String, cartBadgeCount: Int) {
    Surface(
        color = Color(0xCC0F172A),
        tonalElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Filled.Storefront,
                label = "Menú",
                isSelected = currentScreen == "DASHBOARD",
                testTag = "nav_dashboard",
                onClick = { viewModel.setScreen("DASHBOARD") }
            )
            BottomNavItem(
                icon = Icons.Filled.AutoAwesome,
                label = "Chef IA",
                isSelected = currentScreen == "CHEF_AI",
                testTag = "nav_chef_ai",
                onClick = { viewModel.setScreen("CHEF_AI") }
            )
            BottomNavItem(
                icon = Icons.Filled.ShoppingCart,
                label = "Carrito",
                isSelected = currentScreen == "CART",
                badgeCount = cartBadgeCount,
                testTag = "nav_cart",
                onClick = { viewModel.setScreen("CART") }
            )
            BottomNavItem(
                icon = Icons.Filled.Radar,
                label = "Radar 3D",
                isSelected = currentScreen == "TRACKER",
                testTag = "nav_tracker",
                onClick = { viewModel.setScreen("TRACKER") }
            )
            BottomNavItem(
                icon = Icons.Filled.Campaign,
                label = "Marketing",
                isSelected = currentScreen == "MARKETING",
                testTag = "nav_marketing",
                onClick = { viewModel.setScreen("MARKETING") }
            )
            BottomNavItem(
                icon = Icons.Filled.Shield,
                label = "Seguridad",
                isSelected = currentScreen == "SECURITY",
                testTag = "nav_security",
                onClick = { viewModel.setScreen("SECURITY") }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    testTag: String,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = ComidarColors.PremiumAccent,
                        contentColor = Color.White
                    ) {
                        Text(text = "$badgeCount", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) ComidarColors.PremiumAccent else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 9.5.sp,
            color = if (isSelected) ComidarColors.PremiumAccent else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// --- SCREEN: LOGIN ---
@Composable
fun LoginScreen(viewModel: AppViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(ComidarColors.PremiumAccent, ComidarColors.PrimaryPurple, Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Fastfood,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = LocalizationDictionary.getString("login_welcome"),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = ComidarColors.SoftWhite,
                textAlign = TextAlign.Center
            )
            Text(
                text = LocalizationDictionary.getString("login_subtitle"),
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(LocalizationDictionary.getString("field_user")) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = ComidarColors.PremiumAccent,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_user_input")
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(LocalizationDictionary.getString("field_password")) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = ComidarColors.PremiumAccent,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input")
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (viewModel.loginWithCredentials(username, password)) {
                            viewModel.setScreen("TFA")
                        } else {
                            Toast.makeText(context, LocalizationDictionary.getString("error_invalid_credentials"), Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComidarColors.PremiumAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_submit_btn")
                ) {
                    Text(
                        text = LocalizationDictionary.getString("btn_login"),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Biometrics Simulation / Hardware Keystore integration
                OutlinedButton(
                    onClick = {
                        viewModel.setBiometricVerified(true)
                        viewModel.setScreen("TFA")
                        Toast.makeText(context, "Autenticación Biométrica Aprobada por Keystore", Toast.LENGTH_SHORT).show()
                    },
                    border = BorderStroke(1.dp, ComidarColors.NeonCyan),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ComidarColors.NeonCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_biometric_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Filled.Fingerprint, contentDescription = null, tint = ComidarColors.NeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LocalizationDictionary.getString("btn_biometric"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

// --- SCREEN: TFA ---
@Composable
fun TfaScreen(viewModel: AppViewModel) {
    var token by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.VpnKey,
            contentDescription = null,
            tint = ComidarColors.NeonCyan,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = LocalizationDictionary.getString("tfa_title"),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = ComidarColors.SoftWhite,
            textAlign = TextAlign.Center
        )
        Text(
            text = LocalizationDictionary.getString("tfa_desc") + " (Ej: 123456)",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = token,
                onValueChange = { if (it.length <= 6) token = it },
                label = { Text(LocalizationDictionary.getString("tfa_placeholder")) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedLabelColor = ComidarColors.NeonCyan,
                    unfocusedLabelColor = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tfa_token_input")
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (viewModel.verifyTfaToken(token)) {
                        viewModel.setScreen("DASHBOARD")
                    } else {
                        Toast.makeText(context, LocalizationDictionary.getString("error_invalid_tfa"), Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ComidarColors.NeonCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("tfa_verify_btn")
            ) {
                Text(
                    text = LocalizationDictionary.getString("btn_verify"),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- SCREEN: DASHBOARD (SaaS + Customer view) ---
@Composable
fun DashboardScreen(viewModel: AppViewModel) {
    val isAdmin by viewModel.isAdminMode.collectAsState()
    val products by viewModel.products.collectAsState()
    var selectedCategory by remember { mutableStateOf("Todo") }
    val categories = listOf("Todo", "Perros", "Burgers", "Pizzas", "Pasapalos", "Criollo")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // SaaS Role Selector Mode
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ComidarColors.GlassSurface, RoundedCornerShape(14.dp))
                    .border(1.dp, ComidarColors.GlassBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isAdmin) Icons.Filled.AdminPanelSettings else Icons.Filled.Person,
                        contentDescription = null,
                        tint = if (isAdmin) ComidarColors.PremiumAccent else ComidarColors.NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAdmin) "Modo SaaS Restaurant Owner" else "Modo Cliente Comensal",
                        color = ComidarColors.SoftWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Switch(
                    checked = isAdmin,
                    onCheckedChange = { viewModel.toggleAdminMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ComidarColors.PremiumAccent,
                        checkedTrackColor = ComidarColors.PremiumAccent.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.testTag("saas_mode_switch")
                )
            }
        }

        // SaaS Stats Cards
        if (isAdmin) {
            item {
                Text(
                    text = "Métricas Ejecutivas de Venta (Venezuela / USD)",
                    color = ComidarColors.PremiumAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val grossSales by viewModel.grossSales.collectAsState()
                    val netProfit by viewModel.netProfit.collectAsState()
                    
                    FinancialCard(
                        title = "Ventas Brutas",
                        value = "$${String.format("%.2f", grossSales)}",
                        growth = "+18.4%",
                        icon = Icons.Filled.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                    FinancialCard(
                        title = "Ganancia Neta",
                        value = "$${String.format("%.2f", netProfit)}",
                        growth = "+14.2%",
                        icon = Icons.Filled.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val orders by viewModel.ordersCount.collectAsState()
                    val deliveries by viewModel.deliveriesCount.collectAsState()

                    FinancialCard(
                        title = "Órdenes Totales",
                        value = "$orders",
                        growth = "Hoy",
                        icon = Icons.Filled.ReceiptLong,
                        modifier = Modifier.weight(1f)
                    )
                    FinancialCard(
                        title = "Entregas Exitosas",
                        value = "$deliveries",
                        growth = "91.6%",
                        icon = Icons.Filled.Moped,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Real Canvas Graph (Ventas de la semana)
            item {
                GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Curva de Facturación Semanal",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Promedio: $390/día",
                            color = ComidarColors.NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    SalesChartCanvas(records = viewModel.salesChartData)
                }
            }
        }

        // Client Segment: Order Street Food
        item {
            Text(
                text = "Catálogo Gastronómico Urbano & Criollo",
                color = ComidarColors.SoftWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Categories Selection Menu
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(
                                        listOf(ComidarColors.PremiumAccent, ComidarColors.PrimaryPurple)
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        listOf(ComidarColors.GlassSurface, ComidarColors.GlassSurface)
                                    )
                                }
                            )
                            .border(1.dp, if (isSelected) Color.Transparent else ComidarColors.GlassBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.White else Color.LightGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Products Catalog mapping
        val filtered = if (selectedCategory == "Todo") products else products.filter { it.category == selectedCategory }
        items(filtered) { product ->
            ProductCard(product = product, onAddClick = { viewModel.addToCart(product) })
        }
    }
}

@Composable
fun FinancialCard(
    title: String,
    value: String,
    growth: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ComidarColors.GlassSurface)
            .border(1.dp, ComidarColors.GlassBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ComidarColors.PremiumAccent,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = growth,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ComidarColors.SecureGreen
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 11.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

// Canvas-based Sales Chart with glowing bezier curves
@Composable
fun SalesChartCanvas(records: List<SalesRecord>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {
        val width = size.width
        val height = size.height
        val maxVal = records.maxOfOrNull { it.value } ?: 1.0
        val spacing = width / (records.size + 1)

        // Draw horizontal guide lines
        for (i in 1..3) {
            val y = height * (i / 4f)
            drawLine(
                color = Color.White.copy(alpha = 0.06f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        // Draw smooth chart path
        val path = Path()
        records.forEachIndexed { index, record ->
            val x = spacing * (index + 1)
            val ratio = record.value / maxVal
            val y = height - (height * ratio.toFloat() * 0.78f)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }

            // Draw data point dots
            drawCircle(
                color = ComidarColors.PremiumAccent,
                radius = 4.5f,
                center = Offset(x, y)
            )
        }

        drawPath(
            path = path,
            color = ComidarColors.PrimaryPurple,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ProductCard(product: Product, onAddClick: () -> Unit) {
    val title = LocalizationDictionary.getString(product.nameKey)
    val desc = LocalizationDictionary.getString(product.descKey)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ComidarColors.GlassSurface)
            .border(1.dp, ComidarColors.GlassBorder, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ComidarColors.GlassSurfaceHigh
                ) {
                    Text(
                        text = product.category,
                        color = ComidarColors.NeonCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = Color.LightGray.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
            )
            Text(
                text = "$${String.format("%.2f", product.price)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = ComidarColors.PremiumAccent
            )
        }
        
        Spacer(modifier = Modifier.width(10.dp))

        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = ComidarColors.PremiumAccent),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.testTag("add_to_cart_${product.id}")
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = LocalizationDictionary.getString("btn_add_cart"),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- SCREEN: CHEF AI (Smart Food Sommelier & Recommender) ---
@Composable
fun ChefAiScreen(viewModel: AppViewModel) {
    var queryInput by remember { mutableStateOf("") }
    val isGenerating by viewModel.isGeneratingChefAi.collectAsState()
    val chefResponse by viewModel.chefAiResponse.collectAsState()
    val suggestedCombo by viewModel.suggestedCombo.collectAsState()
    val context = LocalContext.current

    val presetPrompts = listOf(
        "Para 3 panas con $12",
        "Con full queso de mano y tocineta",
        "Económico para matar antojo rápido",
        "Combo Perro Caliente + Tequeños"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ComidarColors.PrimaryPurple, ComidarColors.NeonCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = LocalizationDictionary.getString("ai_chef_title"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = ComidarColors.SoftWhite
                    )
                    Text(
                        text = LocalizationDictionary.getString("ai_chef_subtitle"),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        item {
            GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Sugerencias Rápidas:",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetPrompts) { prompt ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ComidarColors.GlassSurfaceHigh,
                            border = BorderStroke(1.dp, ComidarColors.GlassBorder),
                            modifier = Modifier.clickable {
                                queryInput = prompt
                                viewModel.askChefAi(prompt)
                            }
                        ) {
                            Text(
                                text = prompt,
                                color = ComidarColors.NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = queryInput,
                    onValueChange = { queryInput = it },
                    placeholder = { Text(LocalizationDictionary.getString("ai_chef_prompt_hint"), fontSize = 12.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = ComidarColors.NeonCyan,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chef_ai_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (queryInput.isNotBlank()) {
                            viewModel.askChefAi(queryInput)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComidarColors.PrimaryPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("chef_ai_ask_btn")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Psychology, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = LocalizationDictionary.getString("btn_ask_ai"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        if (chefResponse.isNotBlank()) {
            item {
                GlassmorphicContainer(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ComidarColors.NeonCyan.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = chefResponse,
                        color = ComidarColors.SoftWhite,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    suggestedCombo?.let { combo ->
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                viewModel.addComboToCart(combo)
                                Toast.makeText(context, "¡Combo agregado al carrito!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ComidarColors.PremiumAccent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_add_combo_to_cart")
                        ) {
                            Icon(imageVector = Icons.Filled.AddShoppingCart, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = LocalizationDictionary.getString("btn_add_combo"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN: CART ---
@Composable
fun CartScreen(viewModel: AppViewModel) {
    val items by viewModel.cartItems.collectAsState()
    var total = 0.0
    items.forEach { total += it.price * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = LocalizationDictionary.getString("cart_title"),
            fontSize = 19.sp,
            fontWeight = FontWeight.Black,
            color = ComidarColors.SoftWhite,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.RemoveShoppingCart,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = LocalizationDictionary.getString("cart_empty"),
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { viewModel.incrementCartItem(item) },
                        onDecrement = { viewModel.decrementCartItem(item) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LocalizationDictionary.getString("cart_total"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "$${String.format("%.2f", total)}",
                        color = ComidarColors.PremiumAccent,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.processCheckout() },
                    colors = ButtonDefaults.buttonColors(containerColor = ComidarColors.PremiumAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("cart_checkout_btn")
                ) {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LocalizationDictionary.getString("btn_checkout"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: com.example.data.CartItem, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ComidarColors.GlassSurface)
            .border(1.dp, ComidarColors.GlassBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                text = "$${String.format("%.2f", item.price)} c/u",
                color = ComidarColors.PremiumAccent,
                fontSize = 12.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Filled.RemoveCircleOutline, contentDescription = null, tint = Color.Gray)
            }
            Text(
                text = "${item.quantity}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
            IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Filled.AddCircleOutline, contentDescription = null, tint = ComidarColors.PremiumAccent)
            }
        }
    }
}

// --- SCREEN: TRACKER (3D Telemetry & Radar Canvas) ---
@Composable
fun TrackerScreen(viewModel: AppViewModel) {
    val distance by viewModel.riderDistance.collectAsState()
    val speed by viewModel.riderSpeed.collectAsState()
    val statusKey by viewModel.riderStatus.collectAsState()
    val progress by viewModel.riderProgress.collectAsState()
    val thermalTemp by viewModel.foodThermalTemp.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarAngle"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = LocalizationDictionary.getString("track_title"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = ComidarColors.SoftWhite
            )
        }

        // 3D Radar HUD Canvas
        item {
            GlassmorphicContainer(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ComidarColors.NeonCyan.copy(alpha = 0.4f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val maxRadius = (size.height / 2f) * 0.88f

                        // Draw concentric radar rings
                        for (r in listOf(0.33f, 0.66f, 1.0f)) {
                            drawCircle(
                                color = ComidarColors.NeonCyan.copy(alpha = 0.25f),
                                radius = maxRadius * r,
                                center = center,
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }

                        // Draw crosshair axes
                        drawLine(
                            color = ComidarColors.NeonCyan.copy(alpha = 0.2f),
                            start = Offset(center.x - maxRadius, center.y),
                            end = Offset(center.x + maxRadius, center.y),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = ComidarColors.NeonCyan.copy(alpha = 0.2f),
                            start = Offset(center.x, center.y - maxRadius),
                            end = Offset(center.x, center.y + maxRadius),
                            strokeWidth = 1f
                        )

                        // Draw rotating radar beam
                        val rad = Math.toRadians(radarAngle.toDouble())
                        val beamEnd = Offset(
                            (center.x + maxRadius * cos(rad)).toFloat(),
                            (center.y + maxRadius * sin(rad)).toFloat()
                        )
                        drawLine(
                            color = ComidarColors.RadarGreen,
                            start = center,
                            end = beamEnd,
                            strokeWidth = 2.dp.toPx()
                        )

                        // Draw motorcycle position marker moving along curve
                        val riderX = center.x - maxRadius * (1f - progress * 1.6f)
                        val riderY = center.y + sin(progress * 4f) * (maxRadius * 0.3f)
                        drawCircle(
                            color = ComidarColors.PremiumAccent,
                            radius = 6.dp.toPx(),
                            center = Offset(riderX.coerceIn(center.x - maxRadius, center.x + maxRadius), riderY)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = "GPS: 10.4806° N, 66.9036° W (Caracas)",
                            color = ComidarColors.RadarGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }

        // Telemetry details
        item {
            GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = LocalizationDictionary.getString("track_rider"),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = LocalizationDictionary.getString("track_status") + ":",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = LocalizationDictionary.getString(statusKey),
                            color = ComidarColors.PremiumAccent,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(ComidarColors.PremiumAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Moped,
                            contentDescription = null,
                            tint = ComidarColors.PremiumAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.Gray.copy(alpha = 0.25f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress.coerceIn(0.01f, 1.0f))
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(ComidarColors.PrimaryPurple, ComidarColors.PremiumAccent)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = LocalizationDictionary.getString("track_distance"), color = Color.Gray, fontSize = 11.sp)
                        Text(text = "${String.format("%.2f", distance)} km", color = ComidarColors.NeonCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Velocidad", color = Color.Gray, fontSize = 11.sp)
                        Text(text = "${String.format("%.1f", speed)} km/h", color = ComidarColors.SoftWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Temp. Térmica", color = Color.Gray, fontSize = 11.sp)
                        Text(text = "${String.format("%.1f", thermalTemp)} °C", color = ComidarColors.PremiumAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// --- SCREEN: MARKETING (WhatsApp Campaign Planner) ---
@Composable
fun MarketingCampaignScreen(viewModel: AppViewModel) {
    var discount by remember { mutableStateOf("25") }
    var target by remember { mutableStateOf("Estudiantes Universitarios & Oficinistas") }
    var product by remember { mutableStateOf("Combo Perro Caliente Especial + Tequeños") }
    
    val marketingText by viewModel.marketingOutput.collectAsState()
    val isGenerating by viewModel.isGeneratingMarketing.collectAsState()
    
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = LocalizationDictionary.getString("marketing_title"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = ComidarColors.SoftWhite
            )
            Text(
                text = LocalizationDictionary.getString("marketing_subtitle"),
                color = Color.Gray,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        item {
            GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = product,
                    onValueChange = { product = it },
                    label = { Text("Producto o Combo a promocionar") },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = discount,
                    onValueChange = { discount = it },
                    label = { Text("Descuento (%)") },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Público Objetivo") },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        viewModel.generateMarketingMessage(
                            discountPct = discount,
                            targetAudience = target,
                            promoProduct = product
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComidarColors.PrimaryPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("marketing_generate_btn")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generar Campaña con IA", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (marketingText.isNotBlank()) {
            item {
                GlassmorphicContainer(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ComidarColors.SecureGreen.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Texto Generado para WhatsApp:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = marketingText,
                            color = ComidarColors.SoftWhite,
                            fontSize = 12.5.sp,
                            lineHeight = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, marketingText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Compartir en WhatsApp"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ComidarColors.SecureGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("marketing_share_btn")
                    ) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compartir Directo a WhatsApp", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- SCREEN: SECURITY AUDIT ---
@Composable
fun SecurityAuditScreen(viewModel: AppViewModel) {
    val isRooted = viewModel.isDeviceRooted
    val auditLogs by viewModel.securityAuditLogs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = LocalizationDictionary.getString("settings_title"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = ComidarColors.SoftWhite
            )
            Text(
                text = "Consola de Seguridad Integral de Syntropy Delta Labs.",
                color = Color.Gray,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        item {
            GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = LocalizationDictionary.getString("lbl_security_audit"),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                SecurityFeatureRow(
                    label = LocalizationDictionary.getString("lbl_root_status"),
                    status = if (isRooted) LocalizationDictionary.getString("status_detected") else LocalizationDictionary.getString("status_not_detected"),
                    isSecure = !isRooted
                )
                
                Spacer(modifier = Modifier.height(10.dp))

                SecurityFeatureRow(
                    label = LocalizationDictionary.getString("lbl_keystore_status"),
                    status = LocalizationDictionary.getString("status_active"),
                    isSecure = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                SecurityFeatureRow(
                    label = LocalizationDictionary.getString("lbl_ssl_pinning"),
                    status = LocalizationDictionary.getString("status_active"),
                    isSecure = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                SecurityFeatureRow(
                    label = LocalizationDictionary.getString("lbl_encryption"),
                    status = LocalizationDictionary.getString("status_active"),
                    isSecure = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                SecurityFeatureRow(
                    label = LocalizationDictionary.getString("lbl_proguard"),
                    status = LocalizationDictionary.getString("status_active"),
                    isSecure = true
                )
            }
        }

        // Live Diagnostic Terminal
        item {
            GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Terminal,
                        contentDescription = null,
                        tint = ComidarColors.NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Terminal de Integridad Criptográfica:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        auditLogs.forEach { log ->
                            Text(
                                text = log,
                                color = ComidarColors.RadarGreen,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = LocalizationDictionary.getString("lbl_developer"),
                    color = Color.DarkGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SecurityFeatureRow(label: String, status: String, isSecure: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ComidarColors.GlassSurfaceHigh)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.LightGray, fontSize = 12.sp)
        Text(
            text = status,
            color = if (isSecure) ComidarColors.SecureGreen else ComidarColors.CriticalRed,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}
