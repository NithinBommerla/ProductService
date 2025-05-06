package dev.nithin.productservice.service;

import dev.nithin.productservice.dto.FakeStoreRequestDto;
import dev.nithin.productservice.dto.FakeStoreResponseDto;
import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
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

    @Override
    public Product createProduct(String name, double price, String description, String category, String imageUrl) {
        FakeStoreRequestDto fakeStoreRequestDto = createDtoFromParams(name, price, description, category, imageUrl);
        FakeStoreResponseDto fakeStoreResponseDto = restTemplate.postForObject("https://fakestoreapi.com/products", fakeStoreRequestDto, FakeStoreResponseDto.class);
        if(fakeStoreResponseDto == null) {
            return null;
        }
        return fakeStoreResponseDto.toProduct();
    }

    @Override
    public Product replaceProductById(long id, String name, double price, String description, String category, String imageUrl) throws ProductNotFoundException {
        FakeStoreRequestDto updatedFakeStoreRequestDto = createDtoFromParams(name, price, description, category, imageUrl);
        // restTemplate.put("https://fakestoreapi.com/products/" + id, fakeStoreRequestDto, FakeStoreResponseDto.class);
        // The above method is fine, but it doesn't return the response body. So we are using the exchange() method
        // exchange() method since put doesn't implicitly create the object i.e. there is no putForObject() method
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        HttpEntity<FakeStoreRequestDto> requestEntity = new HttpEntity<>(updatedFakeStoreRequestDto, headers);
        ResponseEntity<FakeStoreResponseDto> responseEntity = restTemplate.exchange("https://fakestoreapi.com/products/" + id, HttpMethod.PUT, requestEntity, FakeStoreResponseDto.class);

        FakeStoreResponseDto fakeStoreResponseDto = responseEntity.getBody();
        if(fakeStoreResponseDto == null) throw new ProductNotFoundException("Product not found with id " + id);
        return fakeStoreResponseDto.toProduct();
    }

    private List<Product> convertToProductList(FakeStoreResponseDto[] allProducts){
        List<Product> products = new ArrayList<>();
        for (FakeStoreResponseDto fakeStoreResponseDto : allProducts) {
            products.add(fakeStoreResponseDto.toProduct());
        }
        return products;
    }

    private FakeStoreRequestDto createDtoFromParams(String name, double price, String description, String category, String imageUrl) {
        FakeStoreRequestDto fakeStoreRequestDto = new FakeStoreRequestDto();
        fakeStoreRequestDto.setTitle(name);
        fakeStoreRequestDto.setPrice(price);
        fakeStoreRequestDto.setDescription(description);
        fakeStoreRequestDto.setCategory(category);
        fakeStoreRequestDto.setImage(imageUrl);
        return fakeStoreRequestDto;
    }
}
