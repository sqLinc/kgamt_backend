package com.kgamt.menu.controller

import com.kgamt.menu.entity.Role
import com.kgamt.menu.entity.User
import com.kgamt.menu.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class UserWebController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @GetMapping("/create/user")
    fun createUserForm(): String {
        return "create-user"
    }

    @PostMapping("/create/user")
    fun createUser(
        @RequestParam username: String,
        @RequestParam password: String,
        @RequestParam group: String,
        model: Model
    ): String {
        val userName = userRepository.findByUsername(username)
        val userGroup = userRepository.findByUserGroup(group)
        if (userName != null){
            model.addAttribute("error", "Пользователь с таким именем уже существует")
            return "create-user"
        }
        if (userGroup != null){
            model.addAttribute("error", "Такая группа уже существует")
            return "create-user"
        }
        else{
            userRepository.save(
                User(
                    username = username,
                    passwordHash = passwordEncoder.encode(password),
                    userGroup = group,
                    role = Role.LEADER
                )
            )
        }
        return "redirect:/main-screen"
    }

    @GetMapping("/users")
    fun createUserList(
        model: Model
    ): String{
        val users = userRepository.findAll()
        model.addAttribute("users", users)
        return "users-list"
    }

    @GetMapping("/users/{id}")
    fun userPage(
        @PathVariable id: String,
        model: Model
    ): String {
        val user = userRepository.findByUserGroup(id)
        model.addAttribute("user", user)

        return "user-description"
    }

    @GetMapping("/edit/user/{id}")
    fun editUserPage(
        @PathVariable id: Long,
        model: Model
    ): String {
        val user = userRepository.findById(id).orElseThrow {
            RuntimeException("Пользователь не найден")
        }
        model.addAttribute("user", user)
        return "edit-user"
    }

    @PostMapping("/edit/user/{id}")
    fun editUser(
        @PathVariable id: Long,
        @RequestParam username: String,
        @RequestParam new_password: String,
        @RequestParam repeat_new_password: String,
        @RequestParam userGroup: String,
        model: Model
    ): String {
        val user = userRepository.findById(id).orElseThrow()
        if (new_password != repeat_new_password){
            model.addAttribute("error", "Пароли не совпадают")
            model.addAttribute("user", user)
            return "edit-user"
        }
        if (passwordEncoder.matches(new_password, user.passwordHash)){
            model.addAttribute("error", "Пароль не должен совпадать со старым паролем")
            model.addAttribute("user", user)
            return "edit-user"
        }
        if (userGroup == user.userGroup && id != user.id){
            model.addAttribute("error", "Такая группа уже существует!")
            model.addAttribute("user", user)
            return "edit-user"
        }
        else{
            userRepository.save(
                User(
                    id = id,
                    username = username,
                    passwordHash = passwordEncoder.encode(new_password),
                    role = Role.LEADER,
                    userGroup = userGroup
                )
            )
            return "redirect:/users"
        }
    }

    @DeleteMapping("delete/user/{id}")
    fun deleteUser(
        @PathVariable id: Long
    ): String {
        userRepository.deleteById(id)
        return "redirect:/users"
    }


}