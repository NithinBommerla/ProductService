package dev.nithin.productservice.repository;

import dev.nithin.productservice.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Custom query methods can be defined here if needed
    // For example, findByCategory, findByName, etc.
    Optional<Category> findByName(String name);

    // @EntityGraph(attributePaths = { "products" })
    List<Category> findAll();

    @Query("SELECT c FROM Category c JOIN FETCH c.products")
    List<Category> getCategoriesUsingJoinFetch();
}
