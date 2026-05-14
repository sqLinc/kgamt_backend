package com.kgamt.menu.repository

import com.kgamt.menu.entity.Dish
import org.springframework.data.jpa.repository.JpaRepository

interface DishRepository : JpaRepository<Dish, Long>