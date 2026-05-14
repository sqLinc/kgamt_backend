package com.kgamt.menu.entity

import jakarta.persistence.*

@Entity
data class MenuItem(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "menu_day_id")
    val menuDay: MenuDay,

    @ManyToOne
    @JoinColumn(name = "dish_id")
    val dish: Dish,
)
