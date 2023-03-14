package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.*
import ru.quipy.logic.*
import ru.quipy.logic.service.ProjectService
import ru.quipy.logic.state.ProjectAggregateState
import ru.quipy.logic.state.TaskAggregateState
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    private val projectService: ProjectService
) {

    @PostMapping("/{projectName}")
    fun createProject(@PathVariable projectName: String, @RequestParam creatorId: UUID): ProjectCreatedEvent {
        return projectService.createProject(projectName, creatorId)
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): ProjectAggregateState? {
        return projectService.getProject(projectId)
    }

    @PatchMapping("/{projectId}")
    fun updateProjectName(@PathVariable projectId: UUID, @RequestParam newName: String): ProjectNameUpdatedEvent? {
        return projectService.updateProjectName(projectId, newName)
    }

    @DeleteMapping("/{projectId}/participants")
    fun leaveProject(@PathVariable projectId: UUID, @RequestParam userId: UUID): UserLeftProjectEvent {
        return projectService.leaveProject(projectId, userId)
    }

    @PostMapping("/{projectId}/participants")
    fun joinToProject(@PathVariable projectId: UUID, @RequestParam userId: UUID): UserJoinedToProjectEvent? {
        return projectService.joinToProject(projectId, userId)
    }

    @PostMapping("/{projectId}/taskStatus")
    fun createTaskStatus(@PathVariable projectId: UUID, @RequestBody dto: TaskStatusDto): TaskStatusCreatedEvent {
        return projectService.createTaskStatus(projectId, dto)
    }

    @PutMapping("/{projectId}/taskStatus")
    fun orderTasks(@PathVariable projectId: UUID, @RequestBody dto: OrderedStatusesDto): TaskStatusesOrderedEvent {
        return projectService.orderTasks(projectId, dto)
    }

    @DeleteMapping("/{projectId}/taskStatus")
    fun deleteTaskStatus(@PathVariable projectId: UUID, @RequestParam taskStatusId: UUID): TaskStatusDeletedEvent {
        return projectService.deleteTaskStatus(projectId, taskStatusId)
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(
        @PathVariable projectId: UUID,
        @PathVariable taskName: String,
        @RequestParam creatorId: UUID
    ): TaskCreatedEvent {
        return projectService.createTask(projectId, taskName, creatorId)
    }

    @GetMapping("/{projectId}/tasks/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskAggregateState {
        return projectService.getTask(projectId, taskId)
    }

    @PostMapping("/{projectId}/tasks/{taskId}/assignee")
    fun assignTask(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam userId: UUID
    ): TaskAssignedEvent {
        return projectService.assignTask(projectId, taskId, userId)
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}/assignee")
    fun unassignTask(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam userId: UUID
    ): TaskUnassignedEvent {
        return projectService.unassignTask(projectId, taskId, userId)
    }

    @PutMapping("/{projectId}/tasks/{taskId}/name")
    fun updateTask(
        @PathVariable projectId: UUID, @PathVariable taskId: UUID,
        @RequestParam newTaskName: String,
    ): TaskNameUpdatedEvent {
        return projectService.updateTaskName(projectId, taskId, newTaskName)
    }

    @PutMapping("/{projectId}/tasks/{taskId}/taskstatus")
    fun updateTask(
        @PathVariable projectId: UUID, @PathVariable taskId: UUID,
        @RequestParam newTaskStatusId: UUID,
    ): TaskStatusUpdatedEvent {
        return projectService.updateTaskStatusId(projectId, taskId, newTaskStatusId)
    }
}

data class TaskStatusDto(
    val name: String,
    val color: String
)

data class OrderedStatusesDto(
    val orderedStatuses: List<UUID>
)