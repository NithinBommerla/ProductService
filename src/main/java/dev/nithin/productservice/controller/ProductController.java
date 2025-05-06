package dev.nithin.productservice.controller;

import dev.nithin.productservice.dto.ProductResponseDto;
import dev.nithin.productservice.model.Product;
import dev.nithin.productservice.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{id}")
    public ProductResponseDto getProductById(@PathVariable("id") long id) {
        // Logic to get product by ID is in ProductService
        // Convert Product to ProductResponseDto
        Product product = productService.getProductById(id);
        return ProductResponseDto.from(product);
    }

    @GetMapping("/products/")
    public List<ProductResponseDto> getAllProducts() {
        // Logic to get all products is in ProductService
         productService.getAllProducts();
         return null;
    }
}
