package com.kgamt.menu.entity

import com.kgamt.menu.dto.MenuItemDto
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "food_requests")
data class FoodRequest(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "group_name", nullable = false)
    val group: String,

    @Column(name = "with_soup")
    val withSoup: Int,

    @Column(name = "without_soup")
    val withoutSoup: Int,


    @Column
    val date: LocalDate,

    @Column
    val isConfirmed: Boolean,

    @Column
    val totalCost: Int,

    @Column
    val costPerServing: Int,

    @Column
    val isPaid: Boolean,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    val items: List<RequestItem>
)
