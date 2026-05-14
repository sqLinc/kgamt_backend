package com.kgamt.menu.dto

import java.time.LocalDate

data class RequestHistoryDto(
    val month: String,
    val requests: List<FoodRequestDto>
)
