package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"

@DomainEvent(name = USER_CREATED_EVENT)
class UserCreatedEvent(
    val userId: UUID,
    val login: String,
    val password: String
) : Event<UserAggregate>(
    name = USER_CREATED_EVENT,
    createdAt = System.currentTimeMillis()
)