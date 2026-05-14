package com.kgamt.menu.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    val passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role,

    @Column(unique = true, nullable = false, name = "user_group")
    val userGroup: String
)
