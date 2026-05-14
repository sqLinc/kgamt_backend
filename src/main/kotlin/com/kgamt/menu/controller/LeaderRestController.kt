package com.kgamt.menu.controller

import com.kgamt.menu.dto.FoodRequestDto
import com.kgamt.menu.dto.RequestHistoryDto
import com.kgamt.menu.entity.FoodRequest
import com.kgamt.menu.entity.RequestItem
import com.kgamt.menu.service.AuthService
import com.kgamt.menu.service.RequestService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/leader")
class LeaderRestController(
    private val authService: AuthService,
    private val requestService: RequestService
) {

    data class LoginRequest(val username: String, val password: String)
    data class LoginResponse(val token: String, val group: String)


    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        val token = authService.login(request.username, request.password)
        val group = authService.getUser(request.username)

        return if (token != null && group != null) {
            LoginResponse(token = token, group = group)
        } else {
            throw RuntimeException("Неверный логин или пароль")
        }
    }

    @PostMapping("/send_request")
    fun onResult(@RequestBody request: FoodRequestDto): Boolean {

        val entity = FoodRequest(
            group = request.group,
            withSoup = request.withSoup,
            withoutSoup = request.withoutSoup,
            date = request.date,
            isConfirmed = request.isConfirmed,
            totalCost = request.totalCost,
            isPaid = request.isPaid,
            costPerServing = request.costPerServing,
            items = request.items.map { dto ->
                RequestItem(
                    name = dto.name,
                    price = dto.price,
                    quantity = dto.quantity,
                    kcal = dto.kcal,
                    protein = dto.protein,
                    fat = dto.fat,
                    carb = dto.carb,
                    desc = dto.desc,
                    category = dto.category,
                    imageUrl = dto.imageUrl
                )
            }
        )


        return requestService.saveRequest(entity)
    }

    @GetMapping("/get_today_request")
    fun getTodayRequest(@RequestParam group: String): FoodRequestDto {
        return requestService.getTodayRequest(group)
    }

    @PutMapping("/request/update")
    fun update(
        @RequestBody dto: FoodRequestDto
    ) : Boolean{
        return requestService.updateRequest(dto)
    }

    @GetMapping("/request/all")
    fun getAll(@RequestParam group: String): List<RequestHistoryDto> {
        return requestService.getAll(group)
    }


}