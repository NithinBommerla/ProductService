package dev.nithin.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nithin.productservice.dto.CreateFakeStoreProductRequestDto;
import dev.nithin.productservice.dto.ProductResponseDto;
import dev.nithin.productservice.model.Category;
import dev.nithin.productservice.model.Product;
import dev.nithin.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ProductController.class)
public class ProductControllerWebMVCTest {

    @MockitoBean
    @Qualifier("productStorageService")
    ProductService productService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private Product createDummyProductForTest(Long id) {
        Product dummyProduct = new Product();
        dummyProduct.setId(id);
        dummyProduct.setName("Product "+id);
        dummyProduct.setDescription("Description of Product "+id);
        dummyProduct.setPrice(12.5*id);
        dummyProduct.setImageUrl("image.url.dummy"+id);

        Category category = new Category();
        category.setId(id);
        category.setName("Category "+id);

        dummyProduct.setCategory(category);
        return dummyProduct;
    }

    @Test
    public void testGetAllProductsRunsSuccessfully() throws Exception {
        // Arrange
        Product dummyProduct1 = createDummyProductForTest(1L);
        Product dummyProduct2 = createDummyProductForTest(2L);

        List<Product> dummyProducts = Arrays.asList(dummyProduct1, dummyProduct2);

        List<ProductResponseDto> dummyProductResponseDtos = dummyProducts.stream()
                .map(ProductResponseDto::from)
                .toList();

        // Train the mock to return the dummy products when getAllProducts is called
        when(productService.getAllProducts()).thenReturn(dummyProducts);

        // Act & Assert

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(dummyProductResponseDtos)));
    }

    @Test
    public void testCreateProductRunsSuccessfully() throws Exception {

        // Arrange
        CreateFakeStoreProductRequestDto dummyRequestDto = new CreateFakeStoreProductRequestDto();
        dummyRequestDto.setName("Product 1");
        dummyRequestDto.setPrice(12.5);
        dummyRequestDto.setDescription("Description of Product 1");
        dummyRequestDto.setCategory("Category 1");
        dummyRequestDto.setImageUrl("image.url.dummy1");

        Product dummyProductAfterSave = createDummyProductForTest(1L);
        ProductResponseDto dummyProductResponseDto = ProductResponseDto.from(dummyProductAfterSave);

        // Train the mock to return the dummy product when createProduct is called
        when(productService.createProduct(dummyRequestDto.getName(), dummyRequestDto.getPrice(), dummyRequestDto.getDescription(),
                dummyRequestDto.getCategory(), dummyRequestDto.getImageUrl())).thenReturn(dummyProductAfterSave);

        // Act & Assert
        mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dummyRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(dummyProductResponseDto)));
    }
}
