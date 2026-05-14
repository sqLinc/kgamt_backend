package com.kgamt.menu.repository

import com.kgamt.menu.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>{
    fun findByUsername(username: String): User?
    fun findByUserGroup(userGroup: String): User?
}