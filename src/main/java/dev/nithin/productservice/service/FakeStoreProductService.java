package dev.nithin.productservice.service;

import dev.nithin.productservice.dto.FakeStoreResponseDto;
import dev.nithin.productservice.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FakeStoreProductService implements ProductService {

    RestTemplate restTemplate;

    public FakeStoreProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Product getProductById(long id) {
        FakeStoreResponseDto fakeStoreResponseDto = restTemplate.getForObject("https://fakestoreapi.com/products/" + id, FakeStoreResponseDto.class);

        return fakeStoreResponseDto.toProduct();
    }

    @Override
    public List<Product> getAllProducts() {
        return null;
    }

}
