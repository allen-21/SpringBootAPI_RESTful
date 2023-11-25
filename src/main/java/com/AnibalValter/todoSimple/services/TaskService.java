package com.AnibalValter.todoSimple.services;

import java.util.List;
import java.util.Optional;


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

    // Encontra uma tarefa por ID
    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new ObjectNotFoundException(
            "Tarefa não encontrada! Id " + id + " Tipo " + Task.class.getName()
        ));
    }

    // Encontra todas as tarefas associadas a um usuário pelo ID do usuário
    public List<Task> findAllByUserId(Long userId){
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        return tasks;
    }

    // Cria uma nova tarefa associada a um usuário
    @Transactional
    public Task create(Task obj){
        // Obtém o usuário associado à tarefa
        User user = this.userService.findById(obj.getUser().getId());
        
        // Define o ID como nulo para garantir que uma nova tarefa seja criada
        obj.setId(null);
        
        // Associa a tarefa ao usuário
        obj.setUser(user);
        
        // Salva a tarefa no repositório
        obj = this.taskRepository.save(obj);
        
        // Retorna a tarefa criada
        return obj;
    }

    // Atualiza uma tarefa existente
    @Transactional
    public Task update(Task obj){
        // Encontra a tarefa existente pelo ID
        Task newobj = findById(obj.getId());
        
        // Atualiza a descrição da tarefa
        newobj.setDescription(obj.getDescription());
        
        // Salva a tarefa atualizada no repositório
        return this.taskRepository.save(newobj);
    }

    // Exclui uma tarefa pelo ID, lançando uma exceção personalizada se houver entidades relacionadas
    public void delete(Long id){
        // Verifica se a tarefa existe pelo ID
        findById(id);
        
        try {
            // Tenta excluir a tarefa pelo ID
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            // Se a exclusão falhar, lança uma exceção personalizada indicando que há entidades relacionadas
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas!");
        }
    }
}

