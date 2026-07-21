// app/src/main/java/com/example/ui/AppViewModel.kt
package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppLanguage
import com.example.data.CartItem
import com.example.data.CartRepository
import com.example.data.LocalizationDictionary
import com.example.security.KeystoreEncryptor
import com.example.security.RootDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

// --- Products Data Model ---
data class Product(
    val id: String,
    val nameKey: String,
    val descKey: String,
    val price: Double,
    val category: String,
    val imageRes: String
)

// --- Sales Record for SaaS Report ---
data class SalesRecord(
    val label: String,
    val value: Double
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = CartRepository(db.cartDao())

    // Language State
    private val _language = MutableStateFlow(AppLanguage.ES)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // UI Navigation State
    // "LOGIN", "TFA", "DASHBOARD", "CART", "TRACKER", "REPORTS", "MARKETING", "SECURITY"
    private val _currentScreen = MutableStateFlow("LOGIN")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // SaaS Role Mode
    private val _isAdminMode = MutableStateFlow(true)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    // Security Verification Flags
    val isDeviceRooted = RootDetector.isDeviceRooted()
    private val _biometricVerified = MutableStateFlow(false)
    val biometricVerified: StateFlow<Boolean> = _biometricVerified.asStateFlow()

    private val _tfaVerified = MutableStateFlow(false)
    val tfaVerified: StateFlow<Boolean> = _tfaVerified.asStateFlow()

    // Products Preset (Perros y Comida Venezolana)
    private val _products = MutableStateFlow(
        listOf(
            Product("p1", "p1_name", "p1_desc", 4.5, "Burgers", ""),
            Product("p2", "p2_name", "p2_desc", 8.0, "Pizzas", ""),
            Product("p3", "p3_name", "p3_desc", 6.5, "Burgers", ""),
            Product("p4", "p4_name", "p4_desc", 3.5, "Pasapalos", ""),
            Product("p5", "p5_name", "p5_desc", 2.0, "Pasapalos", "")
        )
    )
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Cart integration
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // SaaS Financial Stats (Simulated Live Updates)
    private val _grossSales = MutableStateFlow(1280.50)
    val grossSales: StateFlow<Double> = _grossSales.asStateFlow()

    private val _netProfit = MutableStateFlow(512.20)
    val netProfit: StateFlow<Double> = _netProfit.asStateFlow()

    private val _ordersCount = MutableStateFlow(84)
    val ordersCount: StateFlow<Int> = _ordersCount.asStateFlow()

    private val _deliveriesCount = MutableStateFlow(72)
    val deliveriesCount: StateFlow<Int> = _deliveriesCount.asStateFlow()

    // Chart Data
    val salesChartData = listOf(
        SalesRecord("Lun", 150.0),
        SalesRecord("Mar", 220.0),
        SalesRecord("Mie", 180.0),
        SalesRecord("Jue", 290.0),
        SalesRecord("Vie", 410.0),
        SalesRecord("Sab", 520.0),
        SalesRecord("Dom", 480.0)
    )

    // AI Marketing State
    private val _marketingOutput = MutableStateFlow("")
    val marketingOutput: StateFlow<String> = _marketingOutput.asStateFlow()

    private val _isGeneratingMarketing = MutableStateFlow(false)
    val isGeneratingMarketing: StateFlow<Boolean> = _isGeneratingMarketing.asStateFlow()

    // Delivery tracking variables (Simulated Physics updates)
    private val _riderDistance = MutableStateFlow(2.4) // km
    val riderDistance: StateFlow<Double> = _riderDistance.asStateFlow()

    private val _riderSpeed = MutableStateFlow(42.0) // km/h
    val riderSpeed: StateFlow<Double> = _riderSpeed.asStateFlow()

    private val _riderStatus = MutableStateFlow("track_on_way") // key
    val riderStatus: StateFlow<String> = _riderStatus.asStateFlow()

    private val _riderProgress = MutableStateFlow(0.1f) // 0.0 to 1.0f
    val riderProgress: StateFlow<Float> = _riderProgress.asStateFlow()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    init {
        // Default to ES
        LocalizationDictionary.setLanguage(AppLanguage.ES)
    }

    fun toggleLanguage() {
        val newLang = if (_language.value == AppLanguage.ES) AppLanguage.EN else AppLanguage.ES
        _language.value = newLang
        LocalizationDictionary.setLanguage(newLang)
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    // Authentication Functions
    fun loginWithCredentials(user: String, pass: String): Boolean {
        if (user.isNotBlank() && pass.isNotBlank()) {
            try {
                val encrypted = KeystoreEncryptor.encrypt(pass)
            } catch (e: Exception) {
                // fallbacks gracefully
            }
            return true
        }
        return false
    }

    fun setBiometricVerified(verified: Boolean) {
        _biometricVerified.value = verified
    }

    fun verifyTfaToken(token: String): Boolean {
        if (token == "123456" || token == "999999") {
            _tfaVerified.value = true
            return true
        }
        return false
    }

    fun logout() {
        _biometricVerified.value = false
        _tfaVerified.value = false
        _currentScreen.value = "LOGIN"
    }

    // Cart Management
    fun addToCart(product: Product) {
        viewModelScope.launch {
            val existing = cartItems.value.find { it.productId == product.id }
            if (existing != null) {
                repository.update(existing.copy(quantity = existing.quantity + 1))
            } else {
                repository.insert(
                    CartItem(
                        productId = product.id,
                        name = LocalizationDictionary.getString(product.nameKey),
                        price = product.price,
                        quantity = 1,
                        imageUrl = ""
                    )
                )
            }
        }
    }

    fun incrementCartItem(item: CartItem) {
        viewModelScope.launch {
            repository.update(item.copy(quantity = item.quantity + 1))
        }
    }

    fun decrementCartItem(item: CartItem) {
        viewModelScope.launch {
            if (item.quantity > 1) {
                repository.update(item.copy(quantity = item.quantity - 1))
            } else {
                repository.delete(item)
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clear()
        }
    }

    fun processCheckout() {
        viewModelScope.launch {
            var total = 0.0
            cartItems.value.forEach { total += it.price * it.quantity }
            _grossSales.value += total
            _netProfit.value += total * 0.40
            _ordersCount.value += 1
            _deliveriesCount.value += 1

            repository.clear()
            _riderDistance.value = 3.5
            _riderSpeed.value = 45.0
            _riderStatus.value = "track_prep"
            _riderProgress.value = 0.0f
            
            _currentScreen.value = "TRACKER"
            simulateDeliveryPhysics()
        }
    }

    private fun simulateDeliveryPhysics() {
        viewModelScope.launch {
            for (step in 1..20) {
                kotlinx.coroutines.delay(1000)
                val progress = step / 20f
                _riderProgress.value = progress
                _riderDistance.value = kotlin.math.max(0.0, 3.5 * (1.0 - progress))
                _riderSpeed.value = if (progress >= 1.0f) 0.0 else (35.0 + (step % 4) * 5.0)
                
                _riderStatus.value = when {
                    progress < 0.15f -> "track_prep"
                    progress < 0.85f -> "track_on_way"
                    progress < 0.98f -> "track_arrived"
                    else -> "track_delivered"
                }
            }
        }
    }

    // AI-Powered WhatsApp Marketing generator using clean raw OkHttp + JSONObject
    fun generateMarketingMessage(discountPct: String, targetAudience: String, promoProduct: String, apiKey: String) {
        viewModelScope.launch {
            _isGeneratingMarketing.value = true
            val prompt = """
                Escribe un mensaje de WhatsApp Marketing persuasivo y atractivo para una audiencia de tipo $targetAudience en Venezuela.
                Promociona el producto "$promoProduct" con un descuento de $discountPct%.
                Usa modismos venezolanos de manera premium e inteligente (ej. "pana", "brutal", "con todo"), agrega emojis apetitosos de comida rápida.
                Incluye una llamada a la acción clara para pedir vía Comidar pidaVen con entrega inmediata y pago seguro móvil o blockchain.
            """.trimIndent()

            val fallbackText = """
                🇻🇪 ¡Epa, mi pana! ¿Activo para el mejor sabor? 🔥
                
                En *Comidar pidaVen* te traemos una promo brutal que no te puedes perder. Llévate hoy mismo nuestro *${promoProduct}* con un increíble *${discountPct}% de descuento* brutal. 🌭🍔
                
                Diseñado especialmente para los panas de *$targetAudience*. ¡Viene con todo!
                
                🔒 *Pago 100% Seguro* vía Pago Móvil, Binance Pay o Blockchain.
                🚀 *Delivery Ultra-Rápido* garantizado por Luis Silva en su moto.
                
                👉 Haz clic abajo para pedir de inmediato. ¡No dejes que te lo cuenten!
            """.trimIndent()

            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                kotlinx.coroutines.delay(1000)
                _marketingOutput.value = fallbackText
                _isGeneratingMarketing.value = false
                return@launch
            }

            try {
                val generated = withContext(Dispatchers.IO) {
                    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                    
                    // Construct standard JSON programmatically
                    val rootJson = JSONObject()
                    val contentsArray = JSONArray()
                    val contentObj = JSONObject()
                    val partsArray = JSONArray()
                    val partObj = JSONObject()
                    
                    partObj.put("text", prompt)
                    partsArray.put(partObj)
                    contentObj.put("parts", partsArray)
                    contentsArray.put(contentObj)
                    rootJson.put("contents", contentsArray)

                    val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()

                    okHttpClient.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            return@withContext "Error: ${response.code} ${response.message}"
                        }
                        val responseBody = response.body?.string() ?: return@withContext "Respuesta vacía"
                        val responseJson = JSONObject(responseBody)
                        val candidates = responseJson.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val candidate = candidates.getJSONObject(0)
                            val content = candidate.optJSONObject("content")
                            val parts = content?.optJSONArray("parts")
                            if (parts != null && parts.length() > 0) {
                                return@withContext parts.getJSONObject(0).optString("text", "No text found")
                            }
                        }
                        "No se pudo estructurar el contenido de respuesta."
                    }
                }
                _marketingOutput.value = generated
            } catch (e: Exception) {
                _marketingOutput.value = "Error conectando con la IA de Syntropy: ${e.localizedMessage}. Respaldo local:\n\n$fallbackText"
            } finally {
                _isGeneratingMarketing.value = false
            }
        }
    }
}
