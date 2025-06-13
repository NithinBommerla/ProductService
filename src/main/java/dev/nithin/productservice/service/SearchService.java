package dev.nithin.productservice.service;

import dev.nithin.productservice.model.Product;
import org.springframework.data.domain.Page;

public interface SearchService {
    Page<Product> search(String query, int pageNumber, int pageSize, String sortDirection, String sortField);
}
