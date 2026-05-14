package com.kgamt.menu.controller

import com.kgamt.menu.dto.MenuResponse
import com.kgamt.menu.service.MenuService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/menu")
class MenuRestController(
    private val menuService: MenuService
) {
    @GetMapping("/full")
    fun getFullMenu(): List<MenuResponse> {
        return menuService.getFullMenu()
    }
    @GetMapping("/today")
    fun getTodayMenu(): MenuResponse {
        return menuService.getTodayMenu()

    }

    @GetMapping("/date")
    fun getByDate(@RequestParam date: LocalDate): MenuResponse {
        return menuService.getMenuByDate(date)
    }
}