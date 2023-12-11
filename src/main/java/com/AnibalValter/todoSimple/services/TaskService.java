package com.AnibalValter.todoSimple.services;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // Adicionado para indicar que esta classe é um serviço
import org.springframework.transaction.annotation.Transactional;

import com.AnibalValter.todoSimple.models.Task;
import com.AnibalValter.todoSimple.models.User;
import com.AnibalValter.todoSimple.models.enums.ProfileEnum;
import com.AnibalValter.todoSimple.models.porjection.TaskProjection;
import com.AnibalValter.todoSimple.repositories.TaskRepository;
import com.AnibalValter.todoSimple.security.UserSpringSecurity;
import com.AnibalValter.todoSimple.services.exceptions.AuthorizationException;
import com.AnibalValter.todoSimple.services.exceptions.DataBindingViolationException;
import com.AnibalValter.todoSimple.services.exceptions.ObjectNotFoundException;

@Service // Adicionado para indicar que esta classe é um serviço
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    // Encontra uma tarefa por ID
    public Task findById(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
                "Tarefa não encontrada! Id " + id + " Tipo " + Task.class.getName()));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity)
                || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task))
            throw new AuthorizationException("Acesso negado!");

        return task;
    }

    // Encontra todas as tarefas associadas a um usuário pelo ID do usuário
    public List<TaskProjection> findAllByUser() {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");
        List<TaskProjection> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    // Cria uma nova tarefa associada a um usuário
    @Transactional
    public Task create(Task obj) {
         UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");
   
        User user = this.userService.findById(userSpringSecurity.getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    // Atualiza uma tarefa existente
    @Transactional
    public Task update(Task obj) {
        Task newobj = findById(obj.getId());
        newobj.setDescription(obj.getDescription());
        return this.taskRepository.save(newobj);
    }

    // Exclui uma tarefa pelo ID, lançando uma exceção personalizada se houver
    // entidades relacionadas
    public void delete(Long id) {
        findById(id);

        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas!");
        }
    }

    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task) {
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }
}
