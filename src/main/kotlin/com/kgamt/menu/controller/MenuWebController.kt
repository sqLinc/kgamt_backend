package com.kgamt.menu.controller

import com.kgamt.menu.dto.FoodRequestDto
import com.kgamt.menu.dto.GroupDto
import com.kgamt.menu.dto.MonthDto

import com.kgamt.menu.entity.MenuDay
import com.kgamt.menu.entity.MenuItem
import com.kgamt.menu.entity.WeekDay
import com.kgamt.menu.repository.DishRepository
import com.kgamt.menu.repository.MenuDayRepository
import com.kgamt.menu.repository.MenuItemRepository
import com.kgamt.menu.repository.RequestRepository
import com.kgamt.menu.service.MenuService
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Controller
class MenuWebController(
    private val menuDayRepository: MenuDayRepository,
    private val dishRepository: DishRepository,
    private val menuItemRepository: MenuItemRepository,
    private val menuService: MenuService,
    private val requestRepository: RequestRepository
) {

    @GetMapping("/create/menu")
    fun createMenuForm(model: Model): String {
        val dishes = dishRepository.findAll()
        val grouped = dishes.groupBy { it.category }
        model.addAttribute("dishes", dishRepository.findAll())
        model.addAttribute("weekDay", WeekDay.entries.toTypedArray())
        model.addAttribute("groupedDishes", grouped)
        return "create-menu"
    }

    @PostMapping("/create/menu")
    fun createMenu(
        @RequestParam date: String,
        @RequestParam weekDay: WeekDay,
        @RequestParam(required = false) dishIds: List<Long>?
    ): String {
        var cost = 0
        if (menuDayRepository.findByDate(LocalDate.parse(date)) != null) {
            return "redirect:/main-screen?error=exists"
        }
        dishIds?.forEach { id ->
            val dish = dishRepository.findById(id).orElse(null)
            cost += dish.price
        }

        val menuDay = menuDayRepository.save(MenuDay(date = LocalDate.parse(date), weekDay = weekDay, cost = cost))
        dishIds?.forEach { id ->
            val dish = dishRepository.findById(id).orElse(null)
            if (dish != null) {
                menuItemRepository.save(MenuItem(menuDay = menuDay, dish = dish))
            }
        }

        return "redirect:/main-screen"

    }

    @DeleteMapping("/delete/week")
    fun deleteWeek(): String {
        menuItemRepository.deleteAll()
        menuDayRepository.deleteAll()

        return "redirect:/main-screen"
    }

    @DeleteMapping("/delete/day")
    fun deleteDay(
        @RequestParam date: String
    ): String {
        val menuDay = menuDayRepository.findByDate(LocalDate.parse(date))
        val items = menuItemRepository.findAllByMenuDay(menuDay!!)

        menuItemRepository.deleteAll(items)
        menuDayRepository.delete(menuDay)

        return "redirect:/main-screen"
    }

    @GetMapping("/edit/menu/{date}")
    fun editMenuPage(
        @PathVariable date: LocalDate,
        model: Model
    ): String {

        val menuDay = menuDayRepository.findByDate(date) ?: return "redirect:/admin"
        model.addAttribute("menu", menuDay)


        val allDishes = dishRepository.findAll()
        model.addAttribute("dishes", allDishes)
        val groupedDishes = allDishes.groupBy { it.category }
        model.addAttribute("groupedDishes", groupedDishes)

        val selectedDishIds = menuItemRepository.findAllByMenuDay(menuDay)
            .map { it.dish.id }
        model.addAttribute("selectedDishIds", selectedDishIds)

        return "edit-menu"
    }

    @PostMapping("/edit/menu/{date}")
    fun updateMenuDay(
        @PathVariable date: LocalDate,
        @RequestParam(required = false) dishIds: List<Long>?
    ): String {

        val menuDay = menuDayRepository.findByDate(date)
            ?: return "redirect:/admin"

        val items = menuItemRepository.findAllByMenuDay(menuDay)
        menuItemRepository.deleteAll(items)

        val dishes = dishIds?.mapNotNull {
            dishRepository.findById(it).orElse(null)
        } ?: emptyList()

        dishes.forEach {
            menuItemRepository.save(MenuItem(menuDay = menuDay, dish = it))
        }

        val totalCost = dishes.sumOf { it.price }

        menuDayRepository.save(
            menuDay.copy(cost = totalCost)
        )

        return "redirect:/main-screen"
    }

    @GetMapping("/requests/list")
    fun getRequests(model: Model): String {

        val all = requestRepository.findAll()
        val today = LocalDate.now()

        // 🔥 TODAY
        val todayRequests = all
            .filter { it.date == today }
            .map {

                val totalCost =
                    (it.withSoup * it.costPerServing) +
                            (it.withoutSoup * (it.costPerServing - 10))

                FoodRequestDto(
                    id = it.id,
                    date = it.date,
                    group = it.group,
                    withSoup = it.withSoup,
                    withoutSoup = it.withoutSoup,
                    totalCost = totalCost, // 👈 считаем тут
                    isPaid = it.isPaid,
                    isConfirmed = it.isConfirmed,
                    items = it.items,
                    costPerServing = it.costPerServing
                )
            }

        // 🔥 HISTORY
        val history = all
            .filter { it.isConfirmed }
            .groupBy { it.date.withDayOfMonth(1) }
            .map { (monthDate, monthRequests) ->

                val groups = monthRequests
                    .groupBy { it.group }
                    .map { (groupName, items) ->

                        val mappedRequests = items.map {

                            val totalCost =
                                (it.withSoup * it.costPerServing) +
                                        (it.withoutSoup * (it.costPerServing - 10))

                            FoodRequestDto(
                                id = it.id,
                                date = it.date,
                                group = it.group,
                                withSoup = it.withSoup,
                                withoutSoup = it.withoutSoup,
                                totalCost = totalCost, // 👈 считаем тут тоже
                                isPaid = it.isPaid,
                                isConfirmed = it.isConfirmed,
                                items = it.items,
                                costPerServing = it.costPerServing
                            )
                        }

                        GroupDto(
                            groupName = groupName,
                            totalCost = mappedRequests.sumOf { it.totalCost }, // 👈 сумма уже правильная
                            requests = mappedRequests
                        )
                    }

                MonthDto(
                    month = monthDate,
                    groups = groups
                )
            }

        model.addAttribute("todayRequests", todayRequests)
        model.addAttribute("history", history)

        return "requests-list"
    }

    @PostMapping("/requests/{id}/confirm")
    fun confirmRequest(@PathVariable id: Long): String {
        val request = requestRepository.findById(id).orElseThrow()
        val updated = request.copy(isConfirmed = true)
        requestRepository.save(updated)

        return "redirect:/requests/list"
    }

    @PostMapping("/requests/{id}/paid")
    fun markAsPaid(@PathVariable id: Long): String {
        val req = requestRepository.findById(id).orElseThrow()
        requestRepository.save(req.copy(isPaid = true))
        return "redirect:/requests/list"
    }

    @PostMapping("/requests/{id}/delete")
    fun deleteRequest(@PathVariable id: Long): String {
        requestRepository.deleteById(id)
        return "redirect:/requests/list"
    }

    @PostMapping("/requests/delete/group")
    fun deleteGroupHistory(
        @RequestParam group: String,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        monthStart: LocalDate,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        monthEnd: LocalDate
    ): String {

        val all = requestRepository.findAll()

        val toDelete = all.filter { req ->
            req.group == group &&
                    !req.date.isBefore(monthStart) &&
                    !req.date.isAfter(monthEnd)
        }

        requestRepository.deleteAll(toDelete)

        return "redirect:/requests/list"
    }

    @GetMapping("/requests/export")
    fun exportToExcel(
        @RequestParam group: String,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        monthStart: LocalDate,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        monthEnd: LocalDate,

        response: HttpServletResponse
    ) {

        val data = requestRepository.findAll().filter {
            it.group == group &&
                    !it.date.isBefore(monthStart) &&
                    !it.date.isAfter(monthEnd)
        }

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Отчёт")

        var rowIdx = 0

        // HEADER
        val header = sheet.createRow(rowIdx++)
        header.createCell(0).setCellValue("Дата")
        header.createCell(1).setCellValue("Группа")
        header.createCell(2).setCellValue("С супом")
        header.createCell(3).setCellValue("Без супа")
        header.createCell(4).setCellValue("Стоимость")
        header.createCell(5).setCellValue("Стоимость за порцию")
        header.createCell(6).setCellValue("Оплачено")

        var totalMonthCost = 0

        // DATA
        data.forEach {
            val row = sheet.createRow(rowIdx++)
            row.createCell(0).setCellValue(it.date.toString())
            row.createCell(1).setCellValue(it.group) // 👈 ДОБАВИЛИ ГРУППУ
            row.createCell(2).setCellValue(it.withSoup.toDouble())
            row.createCell(3).setCellValue(it.withoutSoup.toDouble())
            row.createCell(4).setCellValue(it.totalCost.toDouble())
            row.createCell(4).setCellValue(it.costPerServing.toDouble())
            row.createCell(6).setCellValue(if (it.isPaid) "Да" else "Нет")

            totalMonthCost += it.totalCost
        }

        // EMPTY ROW
        rowIdx++

        // TOTAL ROW
        val totalRow = sheet.createRow(rowIdx++)
        totalRow.createCell(3).setCellValue("ИТОГО:")
        totalRow.createCell(4).setCellValue(totalMonthCost.toDouble())

        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        response.setHeader("Content-Disposition", "attachment; filename=report.xlsx")

        workbook.write(response.outputStream)
        workbook.close()
    }







}