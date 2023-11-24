package com.AnibalValter.todoSimple.services;

import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AnibalValter.todoSimple.models.User;

import com.AnibalValter.todoSimple.repositories.UserRepository;



@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public User findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new RuntimeException(
                "Usuario nao encontrado! ID:" + id + ", Tipo: " + User.class.getName()));

    }

    @Transactional
    public User create(User obj) {

        obj.setId(null);
        obj = this.userRepository.save(obj);
      
        return obj;
    }

    @Transactional
    public User update(User obj) {

        User newobj = findById(obj.getId());
        newobj.setPassword(obj.getPassword());
        return this.userRepository.save(newobj);
    }

    public void delete(Long id){
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Nao é possivel excluir pois ha entidades relacionados!");
        }
    }

}
