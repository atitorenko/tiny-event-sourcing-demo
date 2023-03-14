package ru.quipy.logic.state

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    private var createdAt: Long = System.currentTimeMillis()
    private var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectName: String
    lateinit var creatorId: UUID
    var taskStatuses = mutableMapOf<UUID, TaskStatusEntity>()
    val participants = mutableListOf<UUID>()

    override fun getId() = projectId

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectName = event.projectName
        creatorId = event.creatorId
        updatedAt = createdAt
        participants.add(event.creatorId)
    }

    @StateTransitionFunc
    fun projectNameUpdatedApply(event: ProjectNameUpdatedEvent) {
        projectName = event.newName
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun userLeftProjectApply(event: UserLeftProjectEvent) {
        participants.remove(event.userId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun userJoinedToProjectApply(event: UserJoinedToProjectEvent) {
        participants.add(event.userId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusCreatedApply(event: TaskStatusCreatedEvent) {
        taskStatuses[event.taskStatusId] = TaskStatusEntity(event.taskStatusId, event.taskStatusName, event.color)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusesOrderedApply(event: TaskStatusesOrderedEvent) {
        val reorderedStatuses = mutableMapOf<UUID, TaskStatusEntity>()
        event.orderedStatuses.forEach {
            if (taskStatuses.contains(it)) {
                reorderedStatuses[it] = taskStatuses[it]!!
            }
        }
        taskStatuses = reorderedStatuses
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusDeletedApply(event: TaskStatusDeletedEvent) {
        taskStatuses.remove(event.taskStatusId)
        updatedAt = event.createdAt
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val tagsAssigned: MutableSet<UUID>
)

data class TaskStatusEntity(
    val id: UUID,
    val taskStatusName: String,
    val color: String? = null
) {
    companion object {
        const val DEFAULT_TASK_STATUS_NAME = "CREATED"
        const val DEFAULT_TASK_STATUS_COLOR = "GREEN"
    }
}