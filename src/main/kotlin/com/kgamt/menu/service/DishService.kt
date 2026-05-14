package com.kgamt.menu.service

import com.kgamt.menu.dto.MenuItemDto
import com.kgamt.menu.entity.Dish
import com.kgamt.menu.repository.DishRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class DishService(
    private val dishRepository: DishRepository
) {



    fun save(dish: Dish): Dish =
        dishRepository.save(dish)
    fun getDish(id: Long): MenuItemDto {
        val dish = dishRepository.findById(id).orElseThrow()
        val item = MenuItemDto(
            id = dish.id,
            name = dish.name,
            price = dish.price,
            quantity = dish.quantity,
            kcal = dish.kcal,
            protein = dish.protein,
            fat = dish.fat,
            carb = dish.carb,
            desc = dish.desc,
            category = dish.category.name,
            imageUrl = dish.imageUrl!!
        )
        return item
    }

    fun getAllPaginated(page: Int, size: Int): Page<Dish> {
        val pageable: Pageable = PageRequest.of(page - 1, size)
        return dishRepository.findAll(pageable)
    }
}