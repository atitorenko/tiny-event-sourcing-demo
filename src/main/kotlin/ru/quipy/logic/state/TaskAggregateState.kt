package ru.quipy.logic.state

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    private var createdAt: Long = System.currentTimeMillis()
    private var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectId: UUID
    lateinit var taskName: String
    lateinit var taskStatusId: UUID
    lateinit var creatorId: UUID
    private val assignees = mutableSetOf<UUID>()

    override fun getId(): UUID  = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        createdAt = event.createdAt
        updatedAt = event.createdAt
        projectId = event.projectId
        taskName = event.taskName
        taskStatusId = event.taskStatusId
        creatorId = event.creatorId
    }

    @StateTransitionFunc
    fun taskAssignedApply(event: TaskAssignedEvent) {
        assignees.add(event.userId)
    }

    @StateTransitionFunc
    fun taskUnassignedApply(event: TaskUnassignedEvent) {
        assignees.remove(event.userId)
    }

    @StateTransitionFunc
    fun taskNameUpdatedApply(event: TaskNameUpdatedEvent) {
        taskName = event.newTaskName
    }

    @StateTransitionFunc
    fun taskStatusUpdatedApply(event: TaskStatusUpdatedEvent) {
        taskStatusId = event.newTaskStatusId
    }
}