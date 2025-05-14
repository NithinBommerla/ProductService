package dev.nithin.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import dev.nithin.productservice.dto.ProductProjection;
import dev.nithin.productservice.dto.ProductProjectionDto;
import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Category;
import dev.nithin.productservice.model.Product;
import dev.nithin.productservice.repository.CategoryRepository;
import dev.nithin.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("productStorageService")
public class ProductStorageService implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductStorageService.class);

    @Autowired
    public ProductStorageService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product getProductById(long id) throws ProductNotFoundException {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));
    }

    @Override
    public List<Product> getAllProducts() throws ProductNotFoundException {
        return productRepository.findAll();
    }

    @Override
    public Product createProduct(String name, double price, String description, String category, String imageUrl) {
        Product product = new Product();
        buildProduct(product, name, price, description, category, imageUrl);
        return productRepository.save(product);
    }

    @Override
    public Product replaceProductById(long id, String name, double price, String description, String category, String imageUrl) throws ProductNotFoundException {
        Product product = getProductById(id);
        buildProduct(product, name, price, description, category, imageUrl);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByName(String name) throws ProductNotFoundException {
        // Gets Product when the entire name is passed else it returns null
        List<Product> products = productRepository.findByName(name);
        // Check if the response is null or empty
        if(products == null || products.isEmpty()) {
            throw new ProductNotFoundException("No products found with name " + name);
        }
        return products;
        // return productRepository.findByName(name).orElseThrow(() -> new ProductNotFoundException("Product not found with name " + name));
    }

    @Override
    public List<Product> getProductsByCategoryName(String categoryName) throws ProductNotFoundException {
        // Gets Product when the correct entire category name is passed else it returns null

        /*
        // Method 1: Query By Method i.e. Using findByCategory after finding Category by name
        Optional<Category> categoryOptional = categoryRepository.findByName(categoryName);
        if(categoryOptional.isEmpty()) {
            throw new ProductNotFoundException("No products found under category " + categoryName);
        }
        List<Product> products = productRepository.findByCategory(categoryOptional.get());

        // Method 3: JPQL/HQL Query i.e. Using @Query annotation using Model/Entity
        // List<Product> products = productRepository.getProductsByCategoryName(categoryName);

        // Method 4: Native SQL Query i.e. Using @Query annotation using native SQL
        // List<Product> products = productRepository.getProductsByCategoryNameNative(categoryName);
        */

        // Method 2: Declarative Query i.e. Using findByCategory_Name
        List<Product> products = productRepository.findByCategory_Name(categoryName);

        // Check if the response is null or empty
        if(products == null || products.isEmpty()) {
            throw new ProductNotFoundException("No products found under category " + categoryName);
        }
        return products;
    }

    /*
    @Override
    public Product applyPatchToProductById(long id, JsonPatch jsonPatch) throws ProductNotFoundException, JsonPatchException, JsonProcessingException {
        // Get Existing Product
        Product existingProduct = getProductById(id);
        // Convert the existing product into JSON format
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Convert the existing product to a JsonNode
            JsonNode patchedProduct = objectMapper.valueToTree(existingProduct); // This gave Internal Server Error due to two-way mapping b/w Product and category
            // Apply the patch to the product
            JsonNode patchedJsonPatch = jsonPatch.apply(patchedProduct);
            // Convert the patched JSON back to a Product object
            Product patchedProductObject = objectMapper.treeToValue(patchedJsonPatch, Product.class);
            // Use the replace product method to update the product
            return replaceProductById(id,
                    patchedProductObject.getName(),
                    patchedProductObject.getPrice(),
                    patchedProductObject.getDescription(),
                    patchedProductObject.getCategory().getName(),
                    patchedProductObject.getImageUrl()
            );
        } catch (JsonPatchException | JsonProcessingException e) {
            // Handle JSON patching errors
            // log.error("Error applying JSON patch to product ID {}: {}", id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            // log.error("Unexpected error while patching product ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to apply patch", e);
        }
    }
    */

    @Override
    public Product applyPatchToProductById(long id, JsonPatch jsonPatch) throws ProductNotFoundException, JsonPatchException, JsonProcessingException {
        // Get Existing Product
        Product existingProduct = getProductById(id);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            // Convert the existing product to a map for easier manipulation
            Map<String, Object> productMap = getStringObjectMap(existingProduct);

            // Convert to JsonNode and apply patch
            JsonNode productNode = objectMapper.valueToTree(productMap);
            JsonNode patchedNode = jsonPatch.apply(productNode);

            // Extract values from patched node
            String name = patchedNode.has("name") ? patchedNode.get("name").asText() : existingProduct.getName();
            double price = patchedNode.has("price") ? patchedNode.get("price").asDouble() : existingProduct.getPrice();
            String description = patchedNode.has("description") ? patchedNode.get("description").asText() : existingProduct.getDescription();
            String imageUrl = patchedNode.has("imageUrl") ? patchedNode.get("imageUrl").asText() : existingProduct.getImageUrl();

            // Handle different category formats
            String categoryName;
            JsonNode categoryNode = patchedNode.get("category");

            if (categoryNode == null) {
                categoryName = existingProduct.getCategory() != null ? existingProduct.getCategory().getName() : null;
            } else if (categoryNode.isTextual()) {
                // Handle case where category is just a string
                categoryName = categoryNode.asText();
            } else if (categoryNode.isObject() && categoryNode.has("name")) {
                // Handle case where category is an object with name property
                categoryName = categoryNode.get("name").asText();
            } else {
                categoryName = existingProduct.getCategory() != null ? existingProduct.getCategory().getName() : null;
            }

            // Use the replace product method to update the product
            return replaceProductById(id, name, price, description, categoryName, imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Error applying patch to product: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> getStringObjectMap(Product existingProduct) {
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("id", existingProduct.getId());
        productMap.put("name", existingProduct.getName());
        productMap.put("price", existingProduct.getPrice());
        productMap.put("description", existingProduct.getDescription());
        productMap.put("imageUrl", existingProduct.getImageUrl());

        Map<String, Object> categoryMap = new HashMap<>();
        if (existingProduct.getCategory() != null) {
            categoryMap.put("id", existingProduct.getCategory().getId());
            categoryMap.put("name", existingProduct.getCategory().getName());
        }
        productMap.put("category", categoryMap);
        return productMap;
    }

    private void buildProduct(Product product, String name, double price, String description, String category, String imageUrl) {
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setImageUrl(imageUrl);

        Category categoryObj = getCategoryByName(category);
        product.setCategory(categoryObj);
    }

    private Category getCategoryByName(String name) {
       Optional<Category> categoryOptional = categoryRepository.findByName(name);
       if(categoryOptional.isPresent()) return categoryOptional.get();
       Category newCategory = new Category();
       newCategory.setName(name);
       return categoryRepository.save(newCategory);
    }

//    // Projections
//    // Projection using interface
//    private List<ProductProjection> getProductsProjectionsByCategoryName(String categoryName) {
//        return productRepository.getProductsProjectionsByCategoryName(categoryName);
//    }
//
//    // Projection using DTO class
//    public List<ProductProjectionDto> getProductsProjectionDtosByCategoryName(String categoryName) throws Exception {
//        return productRepository.getProductsProjectionDtosByCategoryName(categoryName);
//    }

}
