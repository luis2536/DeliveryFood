// app/src/main/java/com/example/ui/ComidarScreens.kt
package com.example.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LocalizationDictionary

// --- Custom Theme Color Palette for Syntropy Delta Labs ---
object ComidarColors {
    val SlateDark = Color(0xFF0F172A)
    val GlassSurface = Color(0x331E293B)
    val GlassBorder = Color(0x4D64748B)
    val PremiumAccent = Color(0xFFF97316) // Hot-dog-orange / gold accent
    val PrimaryPurple = Color(0xFF8B5CF6)
    val NeonCyan = Color(0xFF06B6D4)
    val SecureGreen = Color(0xFF10B981)
    val CriticalRed = Color(0xFFEF4444)
    val SoftWhite = Color(0xFFF8FAFC)
}

@Composable
fun GlassmorphicContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ComidarColors.GlassSurface)
            .border(1.dp, ComidarColors.GlassBorder, RoundedCornerShape(20.dp))
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
    val context = LocalContext.current

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
                        Icon(
                            imageVector = Icons.Filled.Fastfood,
                            contentDescription = null,
                            tint = ComidarColors.PremiumAccent,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LocalizationDictionary.getString("app_title"),
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = ComidarColors.SoftWhite,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    // Language Switcher
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("btn_language_toggle")
                    ) {
                        Text(
                            text = if (language.name == "ES") "🇺🇸 EN" else "🇻🇪 ES",
                            color = ComidarColors.PremiumAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (currentScreen != "LOGIN" && currentScreen != "TFA") {
                BottomNavigationBar(viewModel = viewModel, currentScreen = currentScreen)
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
                            Color(0xFF1E1E2F)
                        )
                    )
                )
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    "LOGIN" -> LoginScreen(viewModel = viewModel)
                    "TFA" -> TfaScreen(viewModel = viewModel)
                    "DASHBOARD" -> DashboardScreen(viewModel = viewModel)
                    "CART" -> CartScreen(viewModel = viewModel)
                    "TRACKER" -> TrackerScreen(viewModel = viewModel)
                    "MARKETING" -> MarketingCampaignScreen(viewModel = viewModel)
                    "SECURITY" -> SecurityAuditScreen(viewModel = viewModel)
                }
            }

            // Root warnings badge
            if (isRooted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(ComidarColors.CriticalRed)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "⚠️ ROOT DETECTADO: MODO DE ALTA SEGURIDAD ACTIVO",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(viewModel: AppViewModel, currentScreen: String) {
    Surface(
        color = Color(0x990F172A),
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Filled.Dashboard,
                label = "Dashboard",
                isSelected = currentScreen == "DASHBOARD",
                testTag = "nav_dashboard",
                onClick = { viewModel.setScreen("DASHBOARD") }
            )
            BottomNavItem(
                icon = Icons.Filled.ShoppingCart,
                label = "Cart",
                isSelected = currentScreen == "CART",
                testTag = "nav_cart",
                onClick = { viewModel.setScreen("CART") }
            )
            BottomNavItem(
                icon = Icons.Filled.LocalShipping,
                label = "Tracker",
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
                icon = Icons.Filled.Security,
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
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ComidarColors.PremiumAccent else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
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
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(ComidarColors.PremiumAccent, Color.Transparent)))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Fastfood,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = LocalizationDictionary.getString("login_welcome"),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ComidarColors.SoftWhite,
                textAlign = TextAlign.Center
            )
            Text(
                text = LocalizationDictionary.getString("login_subtitle"),
                fontSize = 14.sp,
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Biometrics Simulation / Integration Trigger
                OutlinedButton(
                    onClick = {
                        viewModel.setBiometricVerified(true)
                        viewModel.setScreen("TFA")
                        Toast.makeText(context, "Autenticación Biométrica Exitosa (Keystore Protegido)", Toast.LENGTH_LONG).show()
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
                        Icon(imageVector = Icons.Filled.Fingerprint, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LocalizationDictionary.getString("btn_biometric"),
                            fontWeight = FontWeight.Bold
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
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = LocalizationDictionary.getString("tfa_title"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ComidarColors.SoftWhite,
            textAlign = TextAlign.Center
        )
        Text(
            text = LocalizationDictionary.getString("tfa_desc") + " (Sugerido: 123456 o 999999)",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
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
            Spacer(modifier = Modifier.height(24.dp))

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
                    fontSize = 16.sp,
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
    val categories = listOf("Todo", "Burgers", "Pizzas", "Pasapalos")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SaaS Selector Mode
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ComidarColors.GlassSurface, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SaaS Manager Mode",
                    color = ComidarColors.SoftWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Switch(
                    checked = isAdmin,
                    onCheckedChange = { viewModel.toggleAdminMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ComidarColors.PremiumAccent,
                        checkedTrackColor = ComidarColors.PremiumAccent.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.testTag("saas_mode_switch")
                )
            }
        }

        // SaaS Stats Cards
        if (isAdmin) {
            item {
                Text(
                    text = "SaaS Ventas & Administración (Venezuela)",
                    color = ComidarColors.PremiumAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
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
                        icon = Icons.Filled.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                    FinancialCard(
                        title = "Ganancia Neta",
                        value = "$${String.format("%.2f", netProfit)}",
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
                        title = "Pedidos Totales",
                        value = "$orders",
                        icon = Icons.Filled.Assignment,
                        modifier = Modifier.weight(1f)
                    )
                    FinancialCard(
                        title = "Entregados",
                        value = "$deliveries",
                        icon = Icons.Filled.LocalShipping,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Real Canvas Graph (Ventas de Perros & Comida de la semana)
            item {
                GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Reporte de Ventas Semanal (USD)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    SalesChartCanvas(records = viewModel.salesChartData)
                }
            }
        }

        // Client Segment: Order Street Food
        item {
            Text(
                text = "Catálogo de Delivery Premium",
                color = ComidarColors.SoftWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
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
                            .background(if (isSelected) ComidarColors.PremiumAccent else ComidarColors.GlassSurface)
                            .border(1.dp, ComidarColors.GlassBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = LocalizationDictionary.getString("cat_${category.lowercase()}"),
                            color = if (isSelected) Color.White else Color.Gray,
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
fun FinancialCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ComidarColors.GlassSurface)
            .border(1.dp, ComidarColors.GlassBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ComidarColors.PremiumAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 11.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// Canvas-based Sales Chart
@Composable
fun SalesChartCanvas(records: List<SalesRecord>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {
        val width = size.width
        val height = size.height
        val maxVal = records.maxOf { it.value }
        val spacing = width / (records.size + 1)

        // Draw horizontal guide lines
        for (i in 1..3) {
            val y = height * (i / 4f)
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        // Draw chart path
        val path = Path()
        records.forEachIndexed { index, record ->
            val x = spacing * (index + 1)
            val ratio = record.value / maxVal
            val y = height - (height * ratio.toFloat() * 0.8f)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }

            // Draw data point circles
            drawCircle(
                color = ComidarColors.PremiumAccent,
                radius = 5f,
                center = Offset(x, y)
            )
        }

        drawPath(
            path = path,
            color = ComidarColors.PrimaryPurple,
            style = Stroke(width = 3.dp.toPx())
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
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
            Text(
                text = "$${product.price}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ComidarColors.PremiumAccent
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))

        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = ComidarColors.PremiumAccent),
            shape = RoundedCornerShape(12.dp),
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
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ComidarColors.SoftWhite,
            modifier = Modifier.padding(bottom = 16.dp)
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
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = LocalizationDictionary.getString("cart_empty"),
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { viewModel.incrementCartItem(item) },
                        onDecrement = { viewModel.decrementCartItem(item) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = LocalizationDictionary.getString("cart_total"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "$${String.format("%.2f", total)}",
                        color = ComidarColors.PremiumAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                        fontSize = 16.sp
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
            .clip(RoundedCornerShape(12.dp))
            .background(ComidarColors.GlassSurface)
            .border(1.dp, ComidarColors.GlassBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = item.name, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = "$${item.price}", color = ComidarColors.PremiumAccent, fontSize = 13.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement) {
                Icon(imageVector = Icons.Filled.RemoveCircleOutline, contentDescription = null, tint = Color.Gray)
            }
            Text(
                text = "${item.quantity}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(onClick = onIncrement) {
                Icon(imageVector = Icons.Filled.AddCircleOutline, contentDescription = null, tint = ComidarColors.PremiumAccent)
            }
        }
    }
}

// --- SCREEN: TRACKER (Delivery Physics engine) ---
@Composable
fun TrackerScreen(viewModel: AppViewModel) {
    val distance by viewModel.riderDistance.collectAsState()
    val speed by viewModel.riderSpeed.collectAsState()
    val statusKey by viewModel.riderStatus.collectAsState()
    val progress by viewModel.riderProgress.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = LocalizationDictionary.getString("track_title"),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ComidarColors.SoftWhite,
            modifier = Modifier.padding(bottom = 24.dp)
        )

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
                        fontSize = 16.sp
                    )
                    Text(
                        text = LocalizationDictionary.getString("track_status") + ":",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = LocalizationDictionary.getString(statusKey),
                        color = ComidarColors.PremiumAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(ComidarColors.PremiumAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalShipping,
                        contentDescription = null,
                        tint = ComidarColors.PremiumAccent,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Custom dynamic speedometer using circular linear math
            Text(
                text = LocalizationDictionary.getString("track_speed") + "${String.format("%.1f", speed)} km/h",
                color = ComidarColors.SoftWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Linear customized progress bar (Physics mapping)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Gray.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(ComidarColors.PrimaryPurple, ComidarColors.PremiumAccent)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = LocalizationDictionary.getString("track_distance"),
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Text(
                    text = "${String.format("%.2f", distance)} km",
                    color = ComidarColors.NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// --- SCREEN: MARKETING (WhatsApp Campaign Planner) ---
@Composable
fun MarketingCampaignScreen(viewModel: AppViewModel) {
    var discount by remember { mutableStateOf("20") }
    var target by remember { mutableStateOf("Estudiantes Universitarios") }
    var product by remember { mutableStateOf("Combo Perro Caliente Especial") }
    
    val marketingText by viewModel.marketingOutput.collectAsState()
    val isGenerating by viewModel.isGeneratingMarketing.collectAsState()
    
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Campañas de Marketing con IA",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ComidarColors.SoftWhite
            )
            Text(
                text = "Generación de textos persuasivos con modismos venezolanos para captar clientes masivamente en WhatsApp.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = product,
                    onValueChange = { product = it },
                    label = { Text("Producto a promocionar") },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

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
                Spacer(modifier = Modifier.height(12.dp))

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
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Triggers dynamic Gemini rest execution
                        viewModel.generateMarketingMessage(
                            discountPct = discount,
                            targetAudience = target,
                            promoProduct = product,
                            apiKey = "" // Automatically falls back to high quality premium Venezuelan preset
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComidarColors.PrimaryPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("marketing_generate_btn")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generar con IA Delta", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (marketingText.isNotBlank()) {
            item {
                GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Texto Generado para WhatsApp:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = marketingText,
                            color = ComidarColors.SoftWhite,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                            .height(48.dp)
                            .testTag("marketing_share_btn")
                    ) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compartir en WhatsApp", fontWeight = FontWeight.Bold)
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = LocalizationDictionary.getString("settings_title"),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ComidarColors.SoftWhite
            )
            Text(
                text = "Consola de Seguridad Integral de Syntropy Delta Labs.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            GlassmorphicContainer(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = LocalizationDictionary.getString("lbl_security_audit"),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SecurityFeatureRow(
                    label = LocalizationDictionary.getString("lbl_root_status"),
                    status = if (isRooted) LocalizationDictionary.getString("status_detected") else LocalizationDictionary.getString("status_not_detected"),
                    isSecure = !isRooted
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                SecurityFeatureRow(
                    label = LocalizationDictionary.getString("lbl_keystore_status"),
                    status = LocalizationDictionary.getString("status_active"),
                    isSecure = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                SecurityFeatureRow(
                    label = LocalizationDictionary.getString("lbl_ssl_pinning"),
                    status = LocalizationDictionary.getString("status_active"),
                    isSecure = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                SecurityFeatureRow(
                    label = LocalizationDictionary.getString("lbl_encryption"),
                    status = LocalizationDictionary.getString("status_active"),
                    isSecure = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                SecurityFeatureRow(
                    label = LocalizationDictionary.getString("lbl_proguard"),
                    status = LocalizationDictionary.getString("status_active"),
                    isSecure = true
                )
            }
        }

        item {
            GlassmorphicContainer(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Syntropy Delta Engine v4.9",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = LocalizationDictionary.getString("lbl_developer"),
                    color = ComidarColors.PremiumAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    textAlign = TextAlign.Center
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
            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = status,
            color = if (isSecure) ComidarColors.SecureGreen else ComidarColors.CriticalRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
