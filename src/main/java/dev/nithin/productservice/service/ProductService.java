package dev.nithin.productservice.service;

import dev.nithin.productservice.model.Product;

import java.util.List;

public interface ProductService {

    Product getProductById(long id);
    List<Product> getAllProducts();
}
