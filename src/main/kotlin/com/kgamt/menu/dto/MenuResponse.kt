package com.kgamt.menu.dto

import java.time.LocalDate

data class MenuItemDto(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val kcal: Int,
    val protein: Int,
    val fat: Int,
    val carb: Int,
    val desc: String,
    val category: String,
    val imageUrl: String
)

data class MenuResponse(
    val date: LocalDate,
    val weekDay: String,
    val items: List<MenuItemDto>,
    val cost: Int
)


