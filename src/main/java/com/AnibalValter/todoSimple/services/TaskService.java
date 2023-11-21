package com.AnibalValter.todoSimple.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.AnibalValter.todoSimple.models.Task;
import com.AnibalValter.todoSimple.models.User;
import com.AnibalValter.todoSimple.repositories.TaskRepository;

import jakarta.transaction.Transactional;

public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id){

        Optional<Task> task = this.taskRepository.findById(id);

        return task.orElseThrow (() -> new RuntimeException(

        "Tarefa nao encontardo! Id" + id + "Tipo" + Task.class.getName()
        ));

    }

    @Transactional
    public Task create(Task obj){
        User user = this.userService.findById(obj.getUser().getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task update(Task obj){
        Task newobj = findById(obj.getId());
        newobj.setDescription(obj.getDescription());

        return this.taskRepository.save(newobj);
    }

    public void delete(Long id){
        findById(id);

        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Nao é possivel excluir pois ha entidades relacionados!");
        }
    }

}
