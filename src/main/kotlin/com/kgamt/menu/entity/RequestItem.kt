package com.kgamt.menu.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "menu_items")
data class RequestItem(

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
    val category: String,
    val imageUrl: String
)