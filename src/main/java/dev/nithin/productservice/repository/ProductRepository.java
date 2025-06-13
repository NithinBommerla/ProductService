package dev.nithin.productservice.repository;

import dev.nithin.productservice.dto.ProductProjection;
import dev.nithin.productservice.dto.ProductProjectionDto;
import dev.nithin.productservice.dto.ProductResponseDto;
import dev.nithin.productservice.model.Category;
import dev.nithin.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods can be defined here if needed
    // For example, findByCategory, findByName, etc.

    List<Product> findByName(String name);

    List<Product> findByCategory(Category category);

    List<Product> findByCategory_Name(String categoryName);

    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName")
    List<Product> getProductsByCategoryName(@Param("categoryName") String categoryName);

    @Query(value = CustomQuery.GET_PRODUCTS_BY_CATEGORY_NAME, nativeQuery = true)
    List<Product> getProductsByCategoryNameNative(@Param("categoryName") String categoryName);

    @Query("SELECT p.name, p.description, p.imageUrl, p.price FROM Product p WHERE p.category.name = :categoryName")
    List<ProductProjection> getProductsProjectionsByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT new dev.nithin.productservice.dto.ProductProjectionDto(p.name, p.price, p.description, p.imageUrl) FROM Product p WHERE p.category.name = :categoryName")
    List<ProductProjectionDto> getProductsProjectionDtosByCategoryName(@Param("categoryName") String categoryName);

    Page<Product> findByNameContaining(String query, Pageable pageable);
}
