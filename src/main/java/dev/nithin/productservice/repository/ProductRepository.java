package dev.nithin.productservice.repository;

import dev.nithin.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods can be defined here if needed
    // For example, findByCategory, findByName, etc.
}
