package dev.nithin.productservice.service;

import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Product;

import java.util.List;

public interface ProductService {

    Product getProductById(long id) throws ProductNotFoundException;
    List<Product> getAllProducts() throws ProductNotFoundException;
}
