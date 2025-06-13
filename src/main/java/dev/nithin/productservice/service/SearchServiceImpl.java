package dev.nithin.productservice.service;

import dev.nithin.productservice.model.Product;
import dev.nithin.productservice.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

    private ProductRepository productRepository;

    public SearchServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> search(String query, int pageNumber, int pageSize, String sortDirection, String sortField) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        //Sort sort = Sort.by(sortField);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return productRepository.findByNameContaining(query, pageable);
    }
}
