package com.AnibalValter.todoSimple.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
 
import org.springframework.stereotype.Repository;

import com.AnibalValter.todoSimple.models.Task;
import com.AnibalValter.todoSimple.models.porjection.TaskProjection;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<TaskProjection> findByUser_Id(Long id);

    // @Query(value = "SELECT t FROM Task t WHERE t.user.id = :id")
    // List<Task> findById(@Param("userid") long id);

    // @Query(value = "SELECT * FROM tasl t WHERE t.user_id = :id",nativeQuery = true)
    // List<Task> findById(@Param("id") long id);

}
