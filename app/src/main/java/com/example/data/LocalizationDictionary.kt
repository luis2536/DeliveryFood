// app/src/main/java/com/example/data/LocalizationDictionary.kt
package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppLanguage {
    ES, EN
}

object LocalizationDictionary {
    private val _currentLanguage = MutableStateFlow(AppLanguage.ES)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    private val translations = mapOf(
        AppLanguage.ES to mapOf(
            "app_title" to "Comidar pidaVen",
            "app_subtitle" to "Delivery Premium de Comida Rápida",
            "login_welcome" to "Acceso de Alta Seguridad",
            "login_subtitle" to "Valide sus credenciales biométricas para continuar",
            "field_user" to "Usuario / Correo",
            "field_password" to "Contraseña",
            "btn_login" to "Iniciar Sesión",
            "btn_biometric" to "Usar Biometría (Huella/Rostro)",
            "biometric_prompt_title" to "Autenticación Biométrica",
            "biometric_prompt_subtitle" to "Confirme su identidad para acceder a Comidar",
            "biometric_prompt_desc" to "Coloque su huella en el lector o escanee su rostro.",
            "biometric_not_available" to "Biometría física no disponible o no configurada. Usando simulador nativo.",
            "tfa_title" to "Segundo Factor de Autenticación (2FA)",
            "tfa_desc" to "Ingrese el token dinámico de 6 dígitos enviado a su dispositivo.",
            "tfa_placeholder" to "Código 2FA",
            "btn_verify" to "Verificar Token",
            "error_invalid_credentials" to "Usuario o contraseña incorrectos",
            "error_invalid_tfa" to "Código 2FA inválido",
            "success_auth" to "Sesión autorizada de forma segura",
            "root_warning_title" to "¡DISPOSITIVO EN RIESGO!",
            "root_warning_desc" to "Se detectó acceso ROOT o firmware modificado. Comidar pidaVen se ejecuta en modo restringido para proteger sus datos.",
            "ssl_pinning_active" to "SSL Pinning Activado (Canal cifrado)",
            "keystore_active" to "Android Keystore Criptográfico Activado",
            
            // Categorías
            "cat_all" to "Todo",
            "cat_burgers" to "Burgers",
            "cat_pizzas" to "Pizzas",
            "cat_snacks" to "Pasapalos",
            "cat_drinks" to "Bebidas",
            "search_placeholder" to "Buscar comida típica o rápida...",
            "btn_add_cart" to "Agregar",
            "delivery_time" to "30-45 min",
            "currency" to "$",
            "cart_title" to "Carrito Seguro",
            "cart_empty" to "Su carrito está vacío. ¡Pida algo delicioso!",
            "cart_total" to "Total a Pagar",
            "btn_checkout" to "Procesar Pedido",
            "checkout_success" to "¡Pedido procesado de manera segura en la blockchain!",
            
            // Rastreo
            "track_title" to "Rastreador de Envío",
            "track_status" to "Estado de Entrega",
            "track_on_way" to "En camino (Simulación de física)",
            "track_prep" to "Preparando comida con amor",
            "track_arrived" to "El motorizado ha llegado",
            "track_delivered" to "Pedido Entregado",
            "track_rider" to "Motorizado: Luis Silva",
            "track_speed" to "Velocidad: ",
            "track_distance" to "Distancia: ",
            
            // Configuración / Seguridad
            "settings_title" to "Configuración y Seguridad",
            "lbl_language" to "Idioma de la Aplicación",
            "lbl_security_audit" to "Auditoría de Seguridad del Dispositivo",
            "lbl_root_status" to "Dispositivo Root / Liberado",
            "lbl_keystore_status" to "Almacenamiento Seguro Keystore",
            "lbl_ssl_pinning" to "Verificación de Certificado SSL",
            "lbl_encryption" to "Cifrado de Base de Datos Local",
            "lbl_proguard" to "Ofuscación R8/ProGuard activa",
            "status_active" to "ACTIVO / SEGURO",
            "status_inactive" to "INACTIVO / RIESGO",
            "status_detected" to "DETECTADO / BLOQUEADO",
            "status_not_detected" to "NO DETECTADO / SEGURO",
            "lbl_developer" to "Syntropy Delta Labs - Central de Inteligencia",
            
            // Productos
            "p1_name" to "Arepa Pelúa Sifrina",
            "p1_desc" to "Harina de maíz blanco con abundante pollo desmechado, aguacate, queso amarillo fundido y mayonesa.",
            "p2_name" to "Pizza Criolla PidaVen",
            "p2_desc" to "Masa artesanal fina, salsa marinara premium, queso mozzarella, carne mechada, plátano frito y salsa de ajo.",
            "p3_name" to "Burger Delta 3D",
            "p3_desc" to "200g de carne angus de primera, doble queso cheddar derretido, tocino crujiente, vegetales frescos y salsa secreta Delta.",
            "p4_name" to "Tequeños Gigantes (Ración)",
            "p4_desc" to "Cinco dedos de harina crujiente rellenos de abundante queso blanco llanero derretido. Acompañados de salsa tártara.",
            "p5_name" to "Empanada de Cazón Oriental",
            "p5_desc" to "Masa dulce crujiente rellena de cazón sazonado a la perfección al estilo margariteño."
        ),
        AppLanguage.EN to mapOf(
            "app_title" to "Comidar pidaVen",
            "app_subtitle" to "Premium Fast Food Delivery",
            "login_welcome" to "High Security Access",
            "login_subtitle" to "Validate your biometric credentials to continue",
            "field_user" to "User / Email",
            "field_password" to "Password",
            "btn_login" to "Log In",
            "btn_biometric" to "Use Biometrics (Fingerprint/Face)",
            "biometric_prompt_title" to "Biometric Authentication",
            "biometric_prompt_subtitle" to "Confirm your identity to access Comidar",
            "biometric_prompt_desc" to "Place your fingerprint on the reader or scan your face.",
            "biometric_not_available" to "Physical biometrics not available or configured. Using native simulator.",
            "tfa_title" to "Two-Factor Authentication (2FA)",
            "tfa_desc" to "Enter the dynamic 6-digit token sent to your device.",
            "tfa_placeholder" to "2FA Code",
            "btn_verify" to "Verify Token",
            "error_invalid_credentials" to "Invalid user or password",
            "error_invalid_tfa" to "Invalid 2FA code",
            "success_auth" to "Session securely authorized",
            "root_warning_title" to "DEVICE COMPROMISED!",
            "root_warning_desc" to "ROOT access or custom firmware detected. Comidar pidaVen is running in restricted mode to protect your data.",
            "ssl_pinning_active" to "SSL Pinning Active (Encrypted channel)",
            "keystore_active" to "Android Cryptographic Keystore Active",
            
            // Categories
            "cat_all" to "All",
            "cat_burgers" to "Burgers",
            "cat_pizzas" to "Pizzas",
            "cat_snacks" to "Snacks",
            "cat_drinks" to "Drinks",
            "search_placeholder" to "Search fast food...",
            "btn_add_cart" to "Add",
            "delivery_time" to "30-45 min",
            "currency" to "$",
            "cart_title" to "Secure Cart",
            "cart_empty" to "Your cart is empty. Order something delicious!",
            "cart_total" to "Total to Pay",
            "btn_checkout" to "Checkout",
            "checkout_success" to "Order processed securely on blockchain!",
            
            // Tracking
            "track_title" to "Delivery Tracker",
            "track_status" to "Delivery Status",
            "track_on_way" to "On the way (Physics-simulated)",
            "track_prep" to "Preparing food with love",
            "track_arrived" to "Delivery rider has arrived",
            "track_delivered" to "Order Delivered",
            "track_rider" to "Rider: Luis Silva",
            "track_speed" to "Speed: ",
            "track_distance" to "Distance: ",
            
            // Settings / Security
            "settings_title" to "Settings & Security",
            "lbl_language" to "Application Language",
            "lbl_security_audit" to "Device Security Audit",
            "lbl_root_status" to "Rooted / Jailbroken Device",
            "lbl_keystore_status" to "Secure Almacenamiento Keystore",
            "lbl_ssl_pinning" to "SSL Certificate Verification",
            "lbl_encryption" to "Local Database Encryption",
            "lbl_proguard" to "R8/ProGuard obfuscation active",
            "status_active" to "ACTIVE / SECURE",
            "status_inactive" to "INACTIVE / RISK",
            "status_detected" to "DETECTED / BLOCKED",
            "status_not_detected" to "NOT DETECTED / SECURE",
            "lbl_developer" to "Syntropy Delta Labs - Intelligence Center",
            
            // Products
            "p1_name" to "Sifrina Pelua Arepa",
            "p1_desc" to "White corn dough filled with shredded chicken, avocado, melted cheddar cheese, and premium mayo.",
            "p2_name" to "Criolla Pizza PidaVen",
            "p2_desc" to "Artisanal thin crust, premium marinara, mozzarella, shredded beef, sweet plantains, and garlic cream sauce.",
            "p3_name" to "Burger Delta 3D",
            "p3_desc" to "200g of premium black angus beef, double melted cheddar, crispy bacon, fresh veggies, and secret Delta sauce.",
            "p4_name" to "Giant Tequeños (Portion)",
            "p4_desc" to "Five crispy flour fingers filled with rich melted llanero white cheese. Served with tartara dipping sauce.",
            "p5_name" to "Margariteña Fish Empanada",
            "p5_desc" to "Crispy sweet corn pastry stuffed with perfectly seasoned shark/baby shark meat, margariteño style."
        )
    )

    fun getString(key: String): String {
        return translations[_currentLanguage.value]?.get(key) ?: key
    }
}
