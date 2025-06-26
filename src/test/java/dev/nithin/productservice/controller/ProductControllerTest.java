package dev.nithin.productservice.controller;
import dev.nithin.productservice.dto.ProductResponseDto;
import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Category;
import dev.nithin.productservice.model.Product;
import dev.nithin.productservice.service.ProductService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProductControllerTest {

    @MockitoBean
    @Qualifier("productStorageService")
    private ProductService productService;

    @Autowired
    ProductController productController;

    @Test
    public void testGetProductByIdReturnsProductResponseDto() throws ProductNotFoundException {
        // AAA
        // Arrange
        Product dummyProduct = new Product();
        dummyProduct.setId(1L);
        dummyProduct.setName("Product 1");
        dummyProduct.setDescription("Description of Product 1");
        dummyProduct.setPrice(12.5);
        dummyProduct.setImageUrl("image.url.dummy");

        Category dummyCategory = new Category();
        dummyCategory.setId(1L);
        dummyCategory.setName("Category 1");
        dummyProduct.setCategory(dummyCategory);
        // Train the mock to return the dummy product when getProductById is called
        when(productService.getProductById(1L)).thenReturn(dummyProduct);
        // Act
        ProductResponseDto productResponse = productController.getProductById(1L, "");
        // Assert
        assertEquals(1L, productResponse.getId());
        assertEquals("Product 1", productResponse.getName());
        assertEquals("Description of Product 1", productResponse.getDescription());
        assertEquals(12.5, productResponse.getPrice());
        assertEquals("image.url.dummy", productResponse.getImageUrl());
        assertEquals("Category 1", productResponse.getCategory());
    }

    @Test
    public void testGetProductByIdReturnsNull() throws ProductNotFoundException {
        // Arrange
        //  Train the mock to return the null when getProductById is called
        when(productService.getProductById(1L)).thenReturn(null);
        // Act
        ProductResponseDto productResponse = productController.getProductById(1L, "");
        // Assert
        // assertEquals(null, productResponse);
        assertNull(productResponse);
    }
}
