package ru.quipy.logic.commands

import ru.quipy.api.*
import ru.quipy.logic.state.ProjectAggregateState
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, projectName: String, creatorId: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        projectName = projectName,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.updateProjectName(newName: String): ProjectNameUpdatedEvent {
    return ProjectNameUpdatedEvent(projectId = this.getId(), newName = newName)
}

fun ProjectAggregateState.leaveProject(userId: UUID): UserLeftProjectEvent {
    return UserLeftProjectEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.joinUserToProject(userId: UUID): UserJoinedToProjectEvent {
    return UserJoinedToProjectEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.createTaskStatus(taskStatusName: String, color: String): TaskStatusCreatedEvent {
    return TaskStatusCreatedEvent(projectId = this.getId(), taskStatusId = UUID.randomUUID(), taskStatusName = taskStatusName, color = color)
}

fun ProjectAggregateState.orderTasks(orderedStatuses: List<UUID>): TaskStatusesOrderedEvent {
    return TaskStatusesOrderedEvent(projectId = this.getId(), orderedStatuses)
}

fun ProjectAggregateState.deleteTaskStatus(taskStatusId: UUID): TaskStatusDeletedEvent {
    return TaskStatusDeletedEvent(this.getId(), taskStatusId = taskStatusId)
}