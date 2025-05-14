package dev.nithin.productservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import dev.nithin.productservice.dto.CreateFakeStoreProductRequestDto;
import dev.nithin.productservice.dto.ProductProjectionDto;
import dev.nithin.productservice.dto.ProductResponseDto;
import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Product;
import dev.nithin.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {

    ProductService productService;

    public ProductController(@Qualifier("productStorageService") ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/id/{id}")
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

    @GetMapping("/products/name/{productName}")
    public List<ProductResponseDto> getProductByName(@PathVariable("productName") String productName) throws ProductNotFoundException {
        // Logic to get product by name is in ProductService
        List<Product> products = productService.getProductsByName(productName);
        // Check if the response is null or empty
        if(products == null || products.isEmpty()) {
            throw new ProductNotFoundException("No products found with name " + productName);
        }
        return convertToProductResponseDtoList(products);
    }

    @GetMapping("/products/category/{categoryName}")
    public List<ProductResponseDto> getProductByCategoryName(@PathVariable("categoryName") String categoryName) throws ProductNotFoundException {
        // Logic to get product by name is in ProductService
        List<Product> products = productService.getProductsByCategoryName(categoryName);
        // Check if the response is null or empty
        if(products == null || products.isEmpty()) {
            throw new ProductNotFoundException("No products found under category " + categoryName);
        }
        return convertToProductResponseDtoList(products);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody CreateFakeStoreProductRequestDto createFakeStoreProductRequestDto)  {
        // Logic to create product is in ProductService
        Product product = productService.createProduct(
                createFakeStoreProductRequestDto.getName(),
                createFakeStoreProductRequestDto.getPrice(),
                createFakeStoreProductRequestDto.getDescription(),
                createFakeStoreProductRequestDto.getCategory(),
                createFakeStoreProductRequestDto.getImageUrl()
        );
        // Convert Product to ProductResponseDto
        return new ResponseEntity<>(ProductResponseDto.from(product), HttpStatus.CREATED);
    }

    @PutMapping("/products/{id}")
    public ProductResponseDto replaceProductById(@RequestBody CreateFakeStoreProductRequestDto createFakeStoreProductRequestDto, @PathVariable("id") long id) throws ProductNotFoundException {
        // Logic to replace product is in ProductService
        Product product = productService.replaceProductById(id,
                createFakeStoreProductRequestDto.getName(),
                createFakeStoreProductRequestDto.getPrice(),
                createFakeStoreProductRequestDto.getDescription(),
                createFakeStoreProductRequestDto.getCategory(),
                createFakeStoreProductRequestDto.getImageUrl()
        );
        // Convert Product to ProductResponseDto
        return ProductResponseDto.from(product);
    }

    @PatchMapping(path = "/products/{id}", consumes = "application/json-patch+json")
    public ProductResponseDto applyPatchToProductById(@PathVariable("id") long id, @RequestBody JsonPatch jsonPatch) throws ProductNotFoundException, JsonPatchException, JsonProcessingException {
        // Logic to apply patch to product is in ProductService
        Product product = productService.applyPatchToProductById(id, jsonPatch);
        // Convert Product to ProductResponseDto
        return ProductResponseDto.from(product);
    }

    private List<ProductResponseDto> convertToProductResponseDtoList(List<Product> allProducts){
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for (Product product : allProducts) {
            productResponseDtos.add(ProductResponseDto.from(product));
        }
        return productResponseDtos;
    }

    /*
    // Projection DTO
    @GetMapping("/products/category/{categoryName}")
    public List<ProductProjectionDto> getProductByCategoryName(@PathVariable("categoryName") String categoryName) throws Exception {
        // Logic to get product by name is in ProductService
        List<ProductProjectionDto> productProjectionDtos = productService.getProductsProjectionDtosByCategoryName(categoryName);
        // Check if the response is null or empty
        if(productProjectionDtos == null || productProjectionDtos.isEmpty()) {
            throw new ProductNotFoundException("No products found under category " + categoryName);
        }
        return productProjectionDtos;
    }
    */
}
