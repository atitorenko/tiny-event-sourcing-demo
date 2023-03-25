package ru.quipy.logic.state

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID


class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var login: String
    private lateinit var password: String

    override fun getId(): UUID = userId

    @StateTransitionFunc
    fun userAggregateCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        login = event.login
        password = event.password
        createdAt = event.createdAt
        updatedAt = event.createdAt
    }
}