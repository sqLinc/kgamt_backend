package com.kgamt.menu.controller

import com.kgamt.menu.entity.Dish
import com.kgamt.menu.entity.DishCategory
import com.kgamt.menu.repository.DishRepository
import com.kgamt.menu.repository.MenuDayRepository
import com.kgamt.menu.repository.MenuItemRepository
import com.kgamt.menu.service.SupabaseStorageService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
class DishWebController(
    private val dishRepository: DishRepository,
    private val supabaseStorageService: SupabaseStorageService,
    private val menuItemRepository: MenuItemRepository,
    private val menuDayRepository: MenuDayRepository
) {

    @GetMapping("/create/dish")
    fun createDishForm(model: Model): String {
        model.addAttribute("category", DishCategory.entries.toTypedArray())
        return "create-dish"
    }

    @PostMapping("/create/dish")
    fun createDish(
        @RequestParam name: String,
        @RequestParam price: Int,
        @RequestParam quantity: Int,
        @RequestParam kcal: Int,
        @RequestParam description: String,
        @RequestParam category: DishCategory,
        @RequestParam protein: Int,
        @RequestParam carb: Int,
        @RequestParam fat: Int,
        @RequestParam image: MultipartFile
    ): String {

        val imageUrl = supabaseStorageService.uploadFile(image)
        dishRepository.save(
            Dish(
                name = name,
                price = price,
                quantity = quantity,
                kcal = kcal,
                protein = protein,
                carb = carb,
                fat = fat,
                desc = description,
                category = category,
                imageUrl = imageUrl
            )
        )
        return "redirect:/main-screen"
    }

    @DeleteMapping("/delete/dish/{id}")
    fun deleteDish(
        @PathVariable id: Long
    ): String {

        val items = menuItemRepository.findByDishId(id)

        // Запоминаем дни меню, где использовалось блюдо
        val affectedMenuDays = items.map { it.menuDay }.distinct()

        val dish = dishRepository.findById(id).orElseThrow()

        dish.imageUrl?.let {
            supabaseStorageService.deleteFile(it)
        }

        menuItemRepository.deleteAll(items)
        dishRepository.deleteById(id)

        // Проверяем дни меню
        affectedMenuDays.forEach { menuDay ->

            val remainingItems = menuItemRepository.findAllByMenuDay(menuDay)

            if (remainingItems.isEmpty()) {
                menuDayRepository.delete(menuDay)
            } else {
                val newCost = remainingItems.sumOf { it.dish.price }
                menuDayRepository.save(
                    menuDay.copy(cost = newCost)
                )
            }
        }

        return "redirect:/main-screen"
    }

    @GetMapping("/edit/dish/{id}")
    fun editDishPage(
        @PathVariable id: Long,
        model: Model
    ): String {

        val dish = dishRepository.findById(id).orElseThrow { RuntimeException("Блюдо не найдено") }
        model.addAttribute("dish", dish)
        model.addAttribute("category", DishCategory.entries.toTypedArray())

        return "edit-dish"
    }

    @PostMapping("/edit/dish/{id}")
    fun editDish(
        @PathVariable id: Long,
        @RequestParam name: String,
        @RequestParam price: Int,
        @RequestParam quantity: Int,
        @RequestParam kcal: Int,
        @RequestParam description: String,
        @RequestParam protein: Int,
        @RequestParam fat: Int,
        @RequestParam carb: Int,
        @RequestParam category: DishCategory,
        @RequestParam image: MultipartFile
    ): String {
        val dish = dishRepository.findById(id).orElseThrow()
        if (dish.imageUrl != null){
            dish.let {
                supabaseStorageService.deleteFile(dish.imageUrl!!)
            }
        }

        val imageUrl = supabaseStorageService.uploadFile(image)
        dishRepository.save(
            Dish(
                id = id,
                name = name,
                price = price,
                quantity = quantity,
                kcal = kcal,
                carb = carb,
                fat = fat,
                protein = protein,
                desc = description,
                category = category,
                imageUrl = imageUrl
            )
        )

        recalculateMenuCosts()
        return "redirect:/main-screen"
    }

    private fun recalculateMenuCosts() {

        val menuDays = menuDayRepository.findAll()

        menuDays.forEach { menuDay ->

            val items = menuItemRepository.findAllByMenuDay(menuDay)

            val totalCost = items.sumOf { it.dish.price }

            menuDayRepository.save(
                menuDay.copy(cost = totalCost)
            )
        }
    }

    @GetMapping("/dish/{id}")
    fun descriptionPage(
        @PathVariable id: Long,
        model: Model
    ): String {
        val dish = dishRepository.findById(id).orElseThrow {
            RuntimeException("Блюдо не найдено")
        }
        model.addAttribute("dish", dish)

        return "dish-description"
    }







}