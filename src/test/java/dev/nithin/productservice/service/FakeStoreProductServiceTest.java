package dev.nithin.productservice.service;

import dev.nithin.productservice.dto.FakeStoreRequestDto;
import dev.nithin.productservice.dto.FakeStoreResponseDto;
import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Product;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FakeStoreProductServiceTest {
    // This is testing for service layer
    // Here we will mock the RestTemplate to return a fake response
    RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    FakeStoreProductService fakeStoreProductService = new FakeStoreProductService(restTemplate);

    private FakeStoreResponseDto createDummyResponseForTest() {
        FakeStoreResponseDto dummyResponse = new FakeStoreResponseDto();
        dummyResponse.setId(1L);
        dummyResponse.setTitle("title");
        dummyResponse.setDescription("description");
        dummyResponse.setPrice(12.5);
        dummyResponse.setImage("image.url.dummy");
        dummyResponse.setCategory("category");
        return dummyResponse;
    }

    @Test
    public void testGetProductByIdReturnsProduct() throws ProductNotFoundException {
        // Arrange
        FakeStoreResponseDto dummyResponse = createDummyResponseForTest();

        // Train the mock to return the dummy response when getForObject is called
        when(restTemplate.getForObject("https://fakestoreapi.com/products/1", FakeStoreResponseDto.class))
                .thenReturn(dummyResponse);
        // Act
        Product product = fakeStoreProductService.getProductById(1L);

        // Assert
        assertEquals(1L, product.getId());
        assertEquals("title", product.getName());
        assertEquals("description", product.getDescription());
        assertEquals(12.5, product.getPrice(), 0);
        assertEquals("image.url.dummy", product.getImageUrl());
        assertEquals("category", product.getCategory().getName());
    }

    @Test
    public void testGetProductByIdThrowsProductNotFoundExceptionOnNullValue() throws ProductNotFoundException {

        // Train the mock to return null when getForObject is called
        when(restTemplate.getForObject("https://fakestoreapi.com/products/1", FakeStoreResponseDto.class))
                .thenReturn(null);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> fakeStoreProductService.getProductById(1L));

    }

    @Test
    public void testCreateProductReturnsProduct() {
        // Arrange
        FakeStoreResponseDto dummyResponse = createDummyResponseForTest();

        FakeStoreRequestDto dummyRequestDto = new FakeStoreRequestDto();
        dummyRequestDto.setTitle("title");
        dummyRequestDto.setDescription("description");
        dummyRequestDto.setPrice(12.5);
        dummyRequestDto.setCategory("category");
        dummyRequestDto.setImage("image.url.dummy");

        // Train the mock to return the dummy response when postForObject is called
        // Here instead of using the requestDto create above we are using any() because
        // Thew newly created product will have different memory address hence will not match the requestDto
        when(restTemplate.postForObject(eq("https://fakestoreapi.com/products"), any(), eq(FakeStoreResponseDto.class))).thenReturn(dummyResponse);

        //Act
        Product product = fakeStoreProductService.createProduct(
                dummyRequestDto.getTitle(),
                dummyRequestDto.getPrice(),
                dummyRequestDto.getDescription(),
                dummyRequestDto.getCategory(),
                dummyRequestDto.getImage()
        );

        // Assert
        assertEquals(1L, product.getId());
        assertEquals("title", product.getName());
        assertEquals("description", product.getDescription());
        assertEquals(12.5, product.getPrice(), 0);
        assertEquals("image.url.dummy", product.getImageUrl());
        assertEquals("category", product.getCategory().getName());
    }

    @Test
    public void testCreateProductReturnsProductVerifyAPICalls() {
        // Arrange
        FakeStoreResponseDto dummyResponse = createDummyResponseForTest();

        when(restTemplate.postForObject(eq("https://fakestoreapi.com/products"), any(), eq(FakeStoreResponseDto.class))).thenReturn(dummyResponse);

        //Act
        Product product = fakeStoreProductService.createProduct("title", 12.5, "description", "category", "image.url.dummy");

        // Assert or verify
        verify(restTemplate, times(1)).postForObject(
                eq("https://fakestoreapi.com/products"),
                any(FakeStoreRequestDto.class),
                eq(FakeStoreResponseDto.class)
        );

    }
}
