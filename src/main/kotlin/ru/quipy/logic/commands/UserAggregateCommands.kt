package ru.quipy.logic.commands

import ru.quipy.api.UserCreatedEvent
import ru.quipy.logic.state.UserAggregateState
import java.util.*

fun UserAggregateState.createUser(
    id: UUID,
    login: String,
    password: String
): UserCreatedEvent {
    return UserCreatedEvent(id, login, password)
}