package com.example.pandeyprojectandroid

data class OrderDataClass(
    val id: Int = 0,
    val name: String = "",
    val contact: String = "",
    val items: List<Int> = listOf(),
    val totalPrice: Float = 0.0f
)
