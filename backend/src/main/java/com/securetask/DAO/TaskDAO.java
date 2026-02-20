package com.securetask.DAO;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.securetask.Entitity.Task;
import com.securetask.Repository.TaskRepository;

@Component
public class TaskDAO {
    
    @Autowired
    private TaskRepository taskRepository;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Optional<Task> findById(@NonNull Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> findAllByAssigneeId(@NonNull Long assigneeId) {
        return taskRepository.findAllByAssigneeId(assigneeId);
    }

    public List<Task> findAllByCreatedById(@NonNull Long createdById) {
        return taskRepository.findAllByCreatedById(createdById);
    }

    public Task save(@NonNull Task task) {
        return taskRepository.save(task);
    }

    public void deleteById(@NonNull Long id) {
        taskRepository.deleteById(id);
    }
}
