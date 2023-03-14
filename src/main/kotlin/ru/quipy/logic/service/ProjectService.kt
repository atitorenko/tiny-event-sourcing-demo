package ru.quipy.logic.service

import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.controller.OrderedStatusesDto
import ru.quipy.controller.TaskStatusDto
import ru.quipy.core.EventSourcingService
import ru.quipy.exception.TaskProjectException
import ru.quipy.logic.state.TaskStatusEntity.Companion.DEFAULT_TASK_STATUS_NAME
import ru.quipy.logic.commands.*
import ru.quipy.logic.state.ProjectAggregateState
import ru.quipy.logic.state.TaskAggregateState
import ru.quipy.logic.state.TaskStatusEntity.Companion.DEFAULT_TASK_STATUS_COLOR
import ru.quipy.logic.state.UserAggregateState
import java.util.*

@Service
class ProjectService(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    fun createProject(projectName: String, creatorId: UUID): ProjectCreatedEvent {
        checkUserExists(creatorId)
        val projectCreatedState = projectEsService.create { it.create(UUID.randomUUID(), projectName, creatorId) }
        createTaskStatus(
            projectCreatedState.projectId,
            TaskStatusDto(DEFAULT_TASK_STATUS_NAME, DEFAULT_TASK_STATUS_COLOR)
        )
        return projectCreatedState
    }

    fun getProject(projectId: UUID): ProjectAggregateState? {
        return getProjectInternal(projectId)
    }

    fun createTask(projectId: UUID, taskName: String, creatorId: UUID): TaskCreatedEvent {
        val projectState = getProjectInternal(projectId)
        val defaultStatusId = getProjectDefaultStatusId(projectState)

        return taskEsService.create { it.create(projectId, UUID.randomUUID(), defaultStatusId, taskName, creatorId) }
    }

    fun getTask(projectId: UUID, taskId: UUID): TaskAggregateState {
        val taskState = getTaskInternal(taskId)
        if (taskState.projectId != projectId) throw TaskProjectException("Task doesn't corresponding to project")
        return taskState
    }

    fun updateProjectName(projectId: UUID, newName: String): ProjectNameUpdatedEvent? {
        if (getProjectInternal(projectId).projectName == newName) return null
        return projectEsService.update(projectId) {
            it.updateProjectName(newName)
        }
    }

    fun leaveProject(projectId: UUID, userId: UUID): UserLeftProjectEvent {
        checkUserExists(userId)
        val projectState = getProjectInternal(projectId)
        if (projectState.creatorId == userId) throw TaskProjectException("Creator can't leave his project")
        return projectEsService.update(projectId) {
            it.leaveProject(userId)
        }
    }

    fun joinToProject(projectId: UUID, userId: UUID): UserJoinedToProjectEvent? {
        checkUserExists(userId)
        val projectState = getProjectInternal(projectId)
        if (projectState.participants.contains(userId)) return null;
        return projectEsService.update(projectId) {
            it.joinUserToProject(userId)
        }
    }

    fun createTaskStatus(projectId: UUID, dto: TaskStatusDto): TaskStatusCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTaskStatus(dto.name, dto.color)
        }
    }

    fun orderTasks(projectId: UUID, newOrderStatuses: OrderedStatusesDto): TaskStatusesOrderedEvent {
        val projectState = getProjectInternal(projectId)
        if (projectState.taskStatuses.keys.containsAll(newOrderStatuses.orderedStatuses)) {
            return projectEsService.update(projectId) {
                it.orderTasks(newOrderStatuses.orderedStatuses)
            }
        } else throw TaskProjectException("invalid order statuses list")
    }

    fun deleteTaskStatus(projectId: UUID, taskStatusId: UUID): TaskStatusDeletedEvent {
        val projectState = getProjectInternal(projectId)
        if (taskStatusId == getProjectDefaultStatusId(projectState)) throw TaskProjectException("Default status couldn't be deleted")
        return projectEsService.update(projectId) {
            it.deleteTaskStatus(taskStatusId)
        }
    }

    fun assignTask(projectId: UUID, taskId: UUID, userId: UUID): TaskAssignedEvent {
        val taskState = getTaskInternal(taskId)
        val projectState = getProjectInternal(projectId)
        if (taskState.projectId != projectId) throw TaskProjectException("Task doesn't corresponding to project")
        if (!projectState.participants.contains(userId)) throw TaskProjectException("User doesn't corresponding to project")
        return taskEsService.update(taskId) { it.assignTaskToUser(userId, taskId) }
    }

    fun unassignTask(projectId: UUID, taskId: UUID, userId: UUID): TaskUnassignedEvent {
        checkUserExists(userId)
        val taskState = getTaskInternal(taskId)
        val projectState = getProjectInternal(projectId)
        if (taskState.projectId != projectId) throw TaskProjectException("Task doesn't corresponding to project")
        if (!projectState.participants.contains(userId)) throw TaskProjectException("User doesn't corresponding to project")
        return taskEsService.update(taskId) { it.unassignUserFromTask(userId, taskId) }
    }

    fun updateTaskName(projectId: UUID, taskId: UUID, newTaskName: String): TaskNameUpdatedEvent {
        val taskState = getTaskInternal(taskId)
        if (taskState.projectId != projectId) throw TaskProjectException("Task doesn't corresponding to project")
        return taskEsService.update(taskId) { it.updateTaskName(taskId, newTaskName) }
    }

    fun updateTaskStatusId(projectId: UUID, taskId: UUID, newTaskStatusId: UUID): TaskStatusUpdatedEvent {
        val projectState = getProjectInternal(projectId)
        val taskState = getTaskInternal(taskId)
        if (taskState.projectId != projectId) throw TaskProjectException("Task doesn't corresponding to project")
        if (!projectState.taskStatuses.keys.contains(newTaskStatusId)) throw TaskProjectException("Task status doesn't corresponding to project")
        return taskEsService.update(taskId) { it.updateTaskStatus(taskId, newTaskStatusId) }
    }

    private fun getProjectInternal(projectId: UUID): ProjectAggregateState {
        return projectEsService.getState(projectId) ?: throw TaskProjectException(PROJECT_NOT_FOUND)
    }

    private fun getTaskInternal(taskId: UUID): TaskAggregateState {
        return taskEsService.getState(taskId) ?: throw TaskProjectException(TASK_NOT_FOUND)
    }

    private fun checkUserExists(userId: UUID) {
        userEsService.getState(userId) ?: throw TaskProjectException(USER_NOT_FOUND)
    }

    private fun getProjectDefaultStatusId(projectState: ProjectAggregateState) =
        projectState.taskStatuses.values.find { it.taskStatusName == DEFAULT_TASK_STATUS_NAME }?.id
            ?: throw TaskProjectException("default status not found for project")

    companion object {
        const val PROJECT_NOT_FOUND = "Project not found"
        const val USER_NOT_FOUND = "User not found"
        const val TASK_NOT_FOUND = "Task not found"
    }
}