package com.kgamt.menu.repository

import com.kgamt.menu.dto.RequestHistoryDto
import com.kgamt.menu.entity.FoodRequest
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface RequestRepository : JpaRepository<FoodRequest, Long>{
    fun findAllByDate(date: LocalDate) : List<FoodRequest?>
    fun findAllByGroup(group: String) : List<FoodRequest?>
}