package com.kgamt.menu.dto

import com.kgamt.menu.entity.FoodRequest

fun FoodRequest.toDto() = FoodRequestDto(
    id = id,
    date = date,
    group = group,
    withSoup = withSoup,
    withoutSoup = withoutSoup,
    totalCost = totalCost,
    isPaid = isPaid,
    isConfirmed = isConfirmed,
    items = items,
    costPerServing = costPerServing
)