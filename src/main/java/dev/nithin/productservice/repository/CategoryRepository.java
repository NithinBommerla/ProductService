package dev.nithin.productservice.repository;

import dev.nithin.productservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Custom query methods can be defined here if needed
    // For example, findByCategory, findByName, etc.
    Category findByName(String name);
}
