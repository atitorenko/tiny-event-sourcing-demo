package ru.quipy.logic.commands
import ru.quipy.api.*
import ru.quipy.logic.state.TaskAggregateState
import java.util.*

fun TaskAggregateState.create(projectId: UUID, taskId: UUID, taskStatusId: UUID, taskName: String, creatorId: UUID) : TaskCreatedEvent {
    return TaskCreatedEvent(projectId, taskId, taskStatusId, taskName, creatorId)
}

fun TaskAggregateState.assignTaskToUser(userId: UUID, taskId: UUID): TaskAssignedEvent {
    return TaskAssignedEvent(userId, taskId)
}

fun TaskAggregateState.unassignUserFromTask(userId: UUID, taskId: UUID): TaskUnassignedEvent {
    return TaskUnassignedEvent(userId, taskId)
}

fun TaskAggregateState.updateTaskName(taskId: UUID, newTaskName: String): TaskNameUpdatedEvent {
    return TaskNameUpdatedEvent(taskId, newTaskName)
}

fun TaskAggregateState.updateTaskStatus(taskId: UUID, newTaskStatusId: UUID): TaskStatusUpdatedEvent {
    return TaskStatusUpdatedEvent(taskId, newTaskStatusId)
}