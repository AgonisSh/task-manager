package com.securetask.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.securetask.Entitity.Task;


public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByAssigneeId(Long assigneeId);
    List<Task> findAllByCreatedById(Long createdById);

    
}
