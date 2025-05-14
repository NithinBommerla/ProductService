package dev.nithin.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import dev.nithin.productservice.dto.ProductProjectionDto;
import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Product;

import java.util.List;

public interface ProductService {

    Product getProductById(long id) throws ProductNotFoundException;
    List<Product> getAllProducts() throws ProductNotFoundException;
    Product createProduct(String name, double price, String description, String category, String imageUrl);
    Product replaceProductById(long id, String name, double price, String description, String category, String imageUrl) throws ProductNotFoundException;
    Product applyPatchToProductById(long id, JsonPatch jsonPatch) throws ProductNotFoundException, JsonPatchException, JsonProcessingException;
    List<Product> getProductsByName(String name) throws ProductNotFoundException;
    List<Product> getProductsByCategoryName(String categoryName) throws ProductNotFoundException;
//    List<ProductProjectionDto> getProductsProjectionDtosByCategoryName(String categoryName) throws Exception;
}
