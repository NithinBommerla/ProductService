package dev.nithin.productservice.controller;

import dev.nithin.productservice.dto.ProductResponseDto;
import dev.nithin.productservice.dto.SearchRequestDto;
import dev.nithin.productservice.model.Product;
import dev.nithin.productservice.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/search")
    public Page<ProductResponseDto> search(@RequestBody SearchRequestDto searchRequestDto) {
        Page<Product> searchResult = searchService.search(searchRequestDto.getQuery(), searchRequestDto.getPageNumber(),
            searchRequestDto.getPageSize(), searchRequestDto.getSortRequestDto().getSortDirection().toString(),
                searchRequestDto.getSortRequestDto().getSortByParam()
        );
        return productResponseDtosPageFromProductPage(searchResult);
    }

    @GetMapping("/search")
    public Page<ProductResponseDto> search(@RequestParam String query, @RequestParam int pageNumber, @RequestParam int pageSize,
                                           @RequestParam String sortDirection, @RequestParam String sortField) {
        Page<Product> searchResult = searchService.search(query, pageNumber, pageSize, sortDirection, sortField);
        return productResponseDtosPageFromProductPage(searchResult);
    }

    private Page<ProductResponseDto> productResponseDtosPageFromProductPage(Page<Product> searchResult){
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        List<Product> products = searchResult.getContent();
        for(Product product : products) {
            ProductResponseDto productResponseDto = ProductResponseDto.from(product);
            productResponseDtos.add(productResponseDto);
        }
        return new PageImpl<>(productResponseDtos, searchResult.getPageable(), searchResult.getTotalElements());
    }
}
