package com.kgamt.menu.controller

import com.kgamt.menu.dto.DishPageResponse
import com.kgamt.menu.dto.MenuItemDto
import com.kgamt.menu.service.DishService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/dishes")
class DishRestController(
    private val dishService: DishService
) {

    @GetMapping
    fun getAllPaginated(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): DishPageResponse {
        val pageResult = dishService.getAllPaginated(page, size)
        val dtos = pageResult.content.map { dish ->
            MenuItemDto(
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
        }
        return DishPageResponse(
            dishes = dtos,
            currentPage = page,
            totalPages = pageResult.totalPages,
            totalItems = pageResult.totalElements,
            nextPage = if (page < pageResult.totalPages) page + 1 else null
        )
    }

    @GetMapping("/{id}")
    fun getDish(
        @PathVariable id: Long
    ): MenuItemDto{
        return dishService.getDish(id)
    }

}