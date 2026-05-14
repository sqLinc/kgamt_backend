package com.kgamt.menu.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.Claims
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {

    private val key = Keys.hmacShaKeyFor(
        "super-secret-key-super-secret-key-123456".toByteArray()
    )

    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .signWith(key)
            .compact()
    }

    fun extractUsername(token: String): String {
        return getClaims(token).subject
    }

    fun isValid(token: String): Boolean {
        return try {
            !getClaims(token).expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}