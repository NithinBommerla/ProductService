package dev.nithin.productservice.service;

import dev.nithin.productservice.dto.FakeStoreResponseDto;
import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FakeStoreProductService implements ProductService {

    RestTemplate restTemplate;

    public FakeStoreProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Product getProductById(long id) throws ProductNotFoundException {
        FakeStoreResponseDto fakeStoreResponseDto = restTemplate.getForObject("https://fakestoreapi.com/products/" + id, FakeStoreResponseDto.class);
        if(fakeStoreResponseDto == null) {
            throw new ProductNotFoundException("Product not found with id " + id);
        }
        return fakeStoreResponseDto.toProduct();
    }

    @Override
    public List<Product> getAllProducts() throws ProductNotFoundException {
        FakeStoreResponseDto[] allProducts = restTemplate.getForObject("https://fakestoreapi.com/products/", FakeStoreResponseDto[].class);
        // FakeStoreResponseDto[] allProducts = new FakeStoreResponseDto[]{restTemplate.getForObject("https://fakestoreapi.com/products/", FakeStoreResponseDto.class)}; // new Array with .class

        // Check if the response is null or empty
        if(allProducts == null || allProducts.length == 0) {
            throw new ProductNotFoundException("No products found");
        }
        // Convert FakeStoreResponseDto[] to List<Product>
        // return Arrays.stream(allProducts).map(FakeStoreResponseDto::toProduct).toList(); // Lambda expression
        return convertToProductList(allProducts);
    }

    private List<Product> convertToProductList(FakeStoreResponseDto[] allProducts){
        List<Product> products = new ArrayList<>();
        for (FakeStoreResponseDto fakeStoreResponseDto : allProducts) {
            products.add(fakeStoreResponseDto.toProduct());
        }
        return products;
    }

}
