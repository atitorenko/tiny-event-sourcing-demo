package ru.quipy.logic.service

import org.springframework.stereotype.Service
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.exception.TaskProjectException
import ru.quipy.logic.commands.createUser
import ru.quipy.logic.state.UserAggregateState
import java.util.*

@Service
class UserService (
    private val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

){
    fun create(login: String, password: String) : UserCreatedEvent {
        return userEsService.create { it.createUser(UUID.randomUUID(), login, password) }
    }

    fun getUser(userId: UUID) : UserAggregateState {
        return userEsService.getState(userId) ?: throw TaskProjectException("User with id $userId not found")
    }
}