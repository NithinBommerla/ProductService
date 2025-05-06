package dev.nithin.productservice.service;

import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    Product getProductById(long id) throws ProductNotFoundException;
    List<Product> getAllProducts() throws ProductNotFoundException;
    Product createProduct(String name, double price, String description, String category, String imageUrl);
    Product replaceProductById(long id, String name, double price, String description, String category, String imageUrl) throws ProductNotFoundException;
}
