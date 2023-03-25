package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.UserCreatedEvent
import ru.quipy.logic.service.UserService
import ru.quipy.logic.state.UserAggregateState
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun create(@RequestParam login: String, @RequestParam password: String): UserCreatedEvent = userService.create(login, password)


    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): UserAggregateState = userService.getUser(userId)
}