package com.kgamt.menu.service

import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class SupabaseStorageService {
    private val url = System.getenv("SUPABASE_URL")
    private val key = System.getenv("SUPABASE_KEY")
    private val bucket = "dish-images"

    private val webClient = WebClient.builder()
        .baseUrl(url)
        .defaultHeader("Authorization", "Bearer $key")
        .defaultHeader("apikey", key)
        .build()

    fun uploadFile(file: MultipartFile): String {

        val fileName = UUID.randomUUID().toString() + ".jpg"

        webClient.put()
            .uri("/storage/v1/object/$bucket/$fileName")
            .contentType(MediaType.parseMediaType(file.contentType ?: "image/jpeg"))
            .bodyValue(file.resource)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        return "$url/storage/v1/object/public/$bucket/$fileName"
    }

    fun deleteFile(imageUrl: String) {
        val fileName = imageUrl.substringAfterLast("/")

        webClient.delete()
            .uri("/storage/v1/object/$bucket/$fileName")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

    }
}