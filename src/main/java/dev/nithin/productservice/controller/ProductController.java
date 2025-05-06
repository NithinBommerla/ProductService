package dev.nithin.productservice.controller;

import dev.nithin.productservice.dto.ProductResponseDto;
import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Product;
import dev.nithin.productservice.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {

    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{id}")
    public ProductResponseDto getProductById(@PathVariable("id") long id) throws ProductNotFoundException {
        // Logic to get product by ID is in ProductService
        Product product = productService.getProductById(id);
        // Convert Product to ProductResponseDto
        return ProductResponseDto.from(product);
    }

    @GetMapping("/products")
    public List<ProductResponseDto> getAllProducts() throws ProductNotFoundException {
        // Logic to get all products is in ProductService
        List<Product> allProducts = productService.getAllProducts();
        // Check if the response is null or empty
        if(allProducts == null || allProducts.isEmpty()) {
            throw new ProductNotFoundException("No products found");
        }

        // Convert List<Product> to List<ProductResponseDto>
        // return allProducts.stream().map(ProductResponseDto::from).toList(); // Lambda expression
        return convertToProductResponseDtoList(allProducts);
    }

    private List<ProductResponseDto> convertToProductResponseDtoList(List<Product> allProducts){
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for (Product product : allProducts) {
            productResponseDtos.add(ProductResponseDto.from(product));
        }
        return productResponseDtos;
    }
}
