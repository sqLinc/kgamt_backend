package com.kgamt.menu.config

import com.kgamt.menu.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class JwtFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")

        println("HEADER = $header")

        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)

            if (jwtService.isValid(token)) {
                val username = jwtService.extractUsername(token)

                val auth = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    emptyList()
                )

                SecurityContextHolder.getContext().authentication = auth

                println("AUTH SUCCESS")

            }
        }

        filterChain.doFilter(request, response)
    }
}