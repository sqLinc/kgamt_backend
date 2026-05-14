package com.kgamt.menu.service

import com.kgamt.menu.dto.FoodRequestDto
import com.kgamt.menu.dto.RequestHistoryDto
import com.kgamt.menu.dto.toDto

import com.kgamt.menu.entity.FoodRequest
import com.kgamt.menu.repository.RequestRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RequestService (
    private val requestRepository: RequestRepository
) {
    fun saveRequest(request: FoodRequest) : Boolean {
        requestRepository.save(request)
        return true
    }

    fun getTodayRequest(group: String) : FoodRequestDto {
        val requests = requestRepository.findAllByDate(LocalDate.now())
        val request = requests.find { it!!.group == group } ?: throw RuntimeException("Request not found")
        val dto = FoodRequestDto(
            id = request.id,
            date = request.date,
            group = request.group,
            withSoup = request.withSoup,
            withoutSoup = request.withoutSoup,
            totalCost = request.totalCost,
            isPaid = request.isPaid,
            isConfirmed = request.isConfirmed,
            items = request.items,
            costPerServing = request.costPerServing

        )
        return dto
    }

    fun updateRequest(dto: FoodRequestDto) : Boolean{
        val exist = requestRepository.findById(dto.id).orElseThrow()

        val updated = exist.copy(
            withoutSoup = dto.withoutSoup,
            withSoup = dto.withSoup
        )
       requestRepository.save(updated)
        return true
    }

    fun getAll(group: String): List<RequestHistoryDto> {
        val requests = requestRepository.findAllByGroup(group)
            .filterNotNull()

        return requests
            .groupBy { "${it.date.year}-${it.date.monthValue.toString().padStart(2, '0')}" }
            .map { (month, requestsInMonth) ->
                RequestHistoryDto(
                    month = month,
                    requests = requestsInMonth.map { it.toDto() }
                )
            }
            .sortedByDescending { it.month }
    }



}