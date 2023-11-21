package com.AnibalValter.todoSimple.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.AnibalValter.todoSimple.models.User;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {
     
    
}
