package com.kgamt.menu.dto

import com.kgamt.menu.entity.RequestItem
import java.time.LocalDate

data class FoodRequestDto(
    val id: Long,
    val date: LocalDate,
    val group: String,
    val withSoup: Int,
    val withoutSoup: Int,
    val totalCost: Int,
    val isPaid: Boolean,
    val isConfirmed: Boolean,
    val items: List<RequestItem>,
    val costPerServing: Int
)



data class GroupDto(
    val groupName: String,
    val requests: List<FoodRequestDto>,
    val totalCost: Int
)

data class MonthDto(
    val month: LocalDate,
    val groups: List<GroupDto>
)