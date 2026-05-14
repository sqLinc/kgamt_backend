package com.kgamt.menu.repository

import com.kgamt.menu.entity.MenuDay
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface MenuDayRepository : JpaRepository<MenuDay, Long> {
    fun findByDate(date: LocalDate): MenuDay?
}