package com.kgamt.menu.entity

import jakarta.persistence.*

@Entity
data class Dish(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val price: Int,
    val quantity: Int,
    val kcal: Int,
    val protein: Int,
    val fat: Int,
    val carb: Int,
    val desc: String,
    @Enumerated(EnumType.STRING)
    val category: DishCategory,
    var imageUrl: String? = null

)
