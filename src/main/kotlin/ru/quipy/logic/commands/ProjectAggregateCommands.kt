package ru.quipy.logic.commands

import ru.quipy.api.*
import ru.quipy.exception.TaskProjectException
import ru.quipy.logic.state.ProjectAggregateState
import ru.quipy.logic.state.TaskStatusEntity
import ru.quipy.logic.state.TaskStatusEntity.Companion.DEFAULT_TASK_STATUS_COLOR
import ru.quipy.logic.state.TaskStatusEntity.Companion.DEFAULT_TASK_STATUS_NAME
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, projectName: String, creatorId: UUID): ProjectCreatedEvent {
    val defaultTaskStatusEntity = TaskStatusEntity(UUID.randomUUID(), DEFAULT_TASK_STATUS_NAME, DEFAULT_TASK_STATUS_COLOR)
    return ProjectCreatedEvent(
        projectId = id,
        projectName = projectName,
        creatorId = creatorId,
        defaultTaskStatusEntity = defaultTaskStatusEntity
    )
}

fun ProjectAggregateState.updateProjectName(newName: String): ProjectNameUpdatedEvent {
    if (this.projectName == newName) throw TaskProjectException("project already has name: $newName")
    return ProjectNameUpdatedEvent(projectId = this.getId(), newName = newName)
}

fun ProjectAggregateState.leaveProject(userId: UUID): UserLeftProjectEvent {
    if (this.creatorId == userId) throw TaskProjectException("Creator can't leave his project")
    return UserLeftProjectEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.joinUserToProject(userId: UUID): UserJoinedToProjectEvent {
    if (this.participants.contains(userId)) throw TaskProjectException("User $userId already has been joined to project ${this.getId()} ");
    return UserJoinedToProjectEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.createTaskStatus(taskStatusName: String, color: String): TaskStatusCreatedEvent {
    return TaskStatusCreatedEvent(projectId = this.getId(), taskStatusId = UUID.randomUUID(), taskStatusName = taskStatusName, color = color)
}

fun ProjectAggregateState.orderTasks(orderedStatuses: List<UUID>): TaskStatusesOrderedEvent {
    if (!this.taskStatuses.keys.containsAll(orderedStatuses)) throw TaskProjectException("invalid order statuses list")
    return TaskStatusesOrderedEvent(projectId = this.getId(), orderedStatuses)
}

fun ProjectAggregateState.deleteTaskStatus(taskStatusId: UUID): TaskStatusDeletedEvent {
    if (taskStatusId == getProjectDefaultStatusId(this)) throw TaskProjectException("Default status couldn't be deleted")
    return TaskStatusDeletedEvent(this.getId(), taskStatusId = taskStatusId)
}

private fun getProjectDefaultStatusId(projectState: ProjectAggregateState) =
    projectState.taskStatuses.values.find { it.taskStatusName == DEFAULT_TASK_STATUS_NAME }?.id
        ?: throw TaskProjectException("default status not found for project")