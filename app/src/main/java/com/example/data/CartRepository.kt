// app/src/main/java/com/example/data/CartRepository.kt
package com.example.data

import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {
    val cartItems: Flow<List<CartItem>> = cartDao.getCartItems()

    suspend fun insert(item: CartItem) {
        cartDao.insertCartItem(item)
    }

    suspend fun update(item: CartItem) {
        cartDao.updateCartItem(item)
    }

    suspend fun delete(item: CartItem) {
        cartDao.deleteCartItem(item)
    }

    suspend fun clear() {
        cartDao.clearCart()
    }
}
