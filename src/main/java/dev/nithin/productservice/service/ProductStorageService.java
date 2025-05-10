package dev.nithin.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Category;
import dev.nithin.productservice.model.Product;
import dev.nithin.productservice.repository.CategoryRepository;
import dev.nithin.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            // Create a simplified representation for patching
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", existingProduct.getId());
            productMap.put("name", existingProduct.getName());
            productMap.put("price", existingProduct.getPrice());
            productMap.put("description", existingProduct.getDescription());
            productMap.put("imageUrl", existingProduct.getImageUrl());

            if (existingProduct.getCategory() != null) {
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("id", existingProduct.getCategory().getId());
                categoryMap.put("name", existingProduct.getCategory().getName());
                productMap.put("category", categoryMap);
            }

            // Convert to JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode productNode = objectMapper.valueToTree(productMap);

            // Apply patch
            JsonNode patchedNode = jsonPatch.apply(productNode);

            // Extract patched values
            String name = patchedNode.has("name") ? patchedNode.get("name").asText() : existingProduct.getName();
            double price = patchedNode.has("price") ? patchedNode.get("price").asDouble() : existingProduct.getPrice();
            String description = patchedNode.has("description") ? patchedNode.get("description").asText() : existingProduct.getDescription();
            String imageUrl = patchedNode.has("imageUrl") ? patchedNode.get("imageUrl").asText() : existingProduct.getImageUrl();

            String categoryName = existingProduct.getCategory().getName();
            if (patchedNode.has("category") && patchedNode.get("category").has("name")) {
                categoryName = patchedNode.get("category").get("name").asText();
            }

            // Use the replace product method to update the product
            return replaceProductById(id, name, price, description, categoryName, imageUrl);
        } catch (Exception e) {
            // log.error("Unexpected error while patching product ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error applying patch to product: " + e.getMessage(), e);
        }
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
}
