package com.kgamt.menu.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class MenuDay(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val date: LocalDate,
    @Enumerated(EnumType.STRING)
    val weekDay: WeekDay,
    val cost: Int
)