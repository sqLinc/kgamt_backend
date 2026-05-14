package com.kgamt.menu.controller

import com.kgamt.menu.dto.MenuItemDto
import com.kgamt.menu.dto.MenuResponse
import com.kgamt.menu.entity.*
import com.kgamt.menu.repository.DishRepository
import com.kgamt.menu.repository.MenuDayRepository
import com.kgamt.menu.repository.MenuItemRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class AdminWebController(
    private val menuDayRepository: MenuDayRepository,
    private val dishRepository: DishRepository,
    private val menuItemRepository: MenuItemRepository,
) {

    @GetMapping("/main-screen")
    fun adminPage(model: Model): String {
        val menuList: MutableList<MenuResponse> = mutableListOf()
        val menuDays = menuDayRepository.findAll()

        menuDays
            .forEach { menuDay ->
                val items = menuItemRepository.findAllByMenuDay(menuDay)
                    .map {
                        MenuItemDto(
                            id = it.dish.id,
                            name = it.dish.name,
                            price = it.dish.price,
                            quantity = it.dish.quantity,
                            kcal = it.dish.kcal,
                            protein = it.dish.protein,
                            fat = it.dish.fat,
                            carb = it.dish.carb,
                            desc = it.dish.desc,
                            category = it.dish.category.displayName,
                            imageUrl = it.dish.imageUrl!!
                        )
                    }

                menuList.add(MenuResponse(menuDay.date, menuDay.weekDay.displayName, items, menuDay.cost))

            }
        model.addAttribute("menuItems", menuList)

        val dishes = dishRepository.findAll()
        model.addAttribute("dishes", dishes)

        val dishesByCategory: Map<DishCategory, List<Dish>> = dishRepository.findAll()
            .groupBy { it.category }
        model.addAttribute("dishCat", dishesByCategory)


        return "main-screen"
    }


    @GetMapping("/login")
    fun loginPage(): String {
        return "login"
    }







}