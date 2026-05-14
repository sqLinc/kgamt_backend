package com.kgamt.menu

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication
@EntityScan("com.kgamt.menu.entity")
class MenuApplication

fun main(args: Array<String>) {
	runApplication<MenuApplication>(*args)
}
