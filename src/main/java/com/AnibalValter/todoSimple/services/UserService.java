package com.AnibalValter.todoSimple.services;

import java.util.Objects;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AnibalValter.todoSimple.models.User;
import com.AnibalValter.todoSimple.models.dto.UserCreateDTO;
import com.AnibalValter.todoSimple.models.dto.UserUpdateDTO;
import com.AnibalValter.todoSimple.models.enums.ProfileEnum;
import com.AnibalValter.todoSimple.repositories.UserRepository;
import com.AnibalValter.todoSimple.security.UserSpringSecurity;
import com.AnibalValter.todoSimple.services.exceptions.AuthorizationException;
import com.AnibalValter.todoSimple.services.exceptions.ObjectNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // Encontra um usuário pelo ID
    public User findById(Long id) {

        UserSpringSecurity userSpringSecurity = authenticated();
        if (!Objects.nonNull(userSpringSecurity)
                || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !id.equals(userSpringSecurity.getId()))
            throw new AuthorizationException("Acesso negado!");

        Optional<User> user = this.userRepository.findById(id);

        // Lança uma exceção se o usuário não for encontrado
        return user.orElseThrow(() -> new ObjectNotFoundException(
                "Usuário não encontrado! ID:" + id + ", Tipo: " + User.class.getName()));
    }

    // Cria um novo usuário
    @Transactional
    public User create(User obj) {
        // Garante que o ID seja nulo para criar um novo usuário
        obj.setId(null);

        // Codifica a senha antes de armazenar no banco de dados
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));

        // Define o perfil do usuário como "USER"
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));

        // Salva o usuário no repositório
        obj = this.userRepository.save(obj);

        // Retorna o usuário criado
        return obj;
    }

    // Atualiza as informações de um usuário
    @Transactional
    public User update(User obj) {
        // Encontra o usuário existente pelo ID
        User newobj = findById(obj.getId());

        // Atualiza a senha do usuário (se necessário)
        newobj.setPassword(obj.getPassword());
        newobj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));

        // Salva o usuário atualizado no repositório
        return this.userRepository.save(newobj);
    }

    // Exclui um usuário pelo ID, lançando uma exceção personalizada se houver
    // entidades relacionadas
    public void delete(Long id) {
        // Verifica se o usuário existe pelo ID
        findById(id);

        try {
            // Tenta excluir o usuário pelo ID
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            // Se a exclusão falhar, lança uma exceção personalizada indicando que há
            // entidades relacionadas
            throw new RuntimeException("Não é possível excluir pois há entidades relacionadas!");
        }
    }

    public static UserSpringSecurity authenticated() {

        try {
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

    public User fromDTO(@Valid UserCreateDTO obj) {
        User user = new User();
        user.setUsername(obj.getUsername());
        user.setPassword(obj.getPassword());
        return user;
    }

    public User fromDTO(@Valid UserUpdateDTO obj) {
        User user = new User();
        user.setId(obj.getId());
        user.setPassword(obj.getPassword());
        return user;
    }
}
