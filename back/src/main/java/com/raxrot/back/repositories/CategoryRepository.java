package com.raxrot.back.repositories;

import com.raxrot.back.entities.Category;
import com.raxrot.back.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);
    Optional<Category> findByIdAndUser(Long id, User user);
    List<Category> findByTypeAndUser(String type, User user);
    boolean existsByNameAndUser(String name, User user);
}
