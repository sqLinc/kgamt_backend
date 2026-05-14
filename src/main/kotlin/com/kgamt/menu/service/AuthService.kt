package com.kgamt.menu.service

import com.kgamt.menu.entity.User
import com.kgamt.menu.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    private  val passwordEncoder = BCryptPasswordEncoder()

    fun login(username: String, password: String): String? {
        val user = userRepository.findByUsername(username) ?: return null

        return if (passwordEncoder.matches(password, user.passwordHash)) {
            jwtService.generateToken(username)
        } else null
    }

    fun getUser(username: String) : String?{
        val user = userRepository.findByUsername(username) ?: return null
        return user.userGroup
    }



}