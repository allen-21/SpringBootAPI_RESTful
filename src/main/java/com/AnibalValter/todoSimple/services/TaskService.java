package com.AnibalValter.todoSimple.services;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.DataBindingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // Adicionado para indicar que esta classe é um serviço
import org.springframework.transaction.annotation.Transactional;

import com.AnibalValter.todoSimple.models.Task;
import com.AnibalValter.todoSimple.models.User;
import com.AnibalValter.todoSimple.repositories.TaskRepository;
import com.AnibalValter.todoSimple.services.exceptions.DataBindingViolationException;
import com.AnibalValter.todoSimple.services.exceptions.ObjectNotFoundException;

@Service // Adicionado para indicar que esta classe é um serviço
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new ObjectNotFoundException(
            "Tarefa não encontrada! Id " + id + " Tipo " + Task.class.getName()
        ));
    }

    public List<Task> findAllByUserId(Long userId){
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        return tasks;
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
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas!");
        }
    }
}
