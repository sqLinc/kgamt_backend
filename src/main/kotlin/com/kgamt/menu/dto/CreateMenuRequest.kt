package com.kgamt.menu.dto

import com.kgamt.menu.entity.WeekDay
import java.time.LocalDate

data class CreateMenuRequest(
    val date: LocalDate,
    val weekDay: WeekDay,
    val dishIds: List<Long>,
    val cost: Int
)
