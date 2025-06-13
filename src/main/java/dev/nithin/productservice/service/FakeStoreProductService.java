package dev.nithin.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import dev.nithin.productservice.dto.FakeStoreRequestDto;
import dev.nithin.productservice.dto.FakeStoreResponseDto;
import dev.nithin.productservice.dto.ProductProjectionDto;
import dev.nithin.productservice.exception.ProductNotFoundException;
import dev.nithin.productservice.model.Product;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service("fakeStoreProductService")
public class FakeStoreProductService implements ProductService {

    RestTemplate restTemplate;
    RedisTemplate<String, Object> redisTemplate;

    public FakeStoreProductService(RestTemplate restTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Product getProductById(long id) throws ProductNotFoundException {

        Product productFromRedis = (Product) redisTemplate.opsForValue().get(String.valueOf(id));
        if(productFromRedis != null) return productFromRedis;

        FakeStoreResponseDto fakeStoreResponseDto = restTemplate.getForObject("https://fakestoreapi.com/products/" + id, FakeStoreResponseDto.class);
        if(fakeStoreResponseDto == null) {
            throw new ProductNotFoundException("Product not found with id " + id);
        }
        Product productFromFakeStore =  fakeStoreResponseDto.toProduct();
        redisTemplate.opsForValue().set(String.valueOf(id), productFromFakeStore);
        return productFromFakeStore;
    }

    @Override
    public List<Product> getProductsByName(String name) throws ProductNotFoundException {
        return Collections.emptyList();
    }

    @Override
    public List<Product> getProductsByCategoryName(String categoryName) throws ProductNotFoundException {
        return Collections.emptyList();
    }

//    @Override
//    public List<ProductProjectionDto> getProductsProjectionDtosByCategoryName(String categoryName) throws Exception{
//        return Collections.emptyList();
//    }

    @Override
    public List<Product> getAllProducts() throws ProductNotFoundException {

        List<Product> cachedProducts = (List<Product>) redisTemplate.opsForValue().get("ALL_PRODUCTS");
        if(cachedProducts != null && !cachedProducts.isEmpty()) return cachedProducts;
        FakeStoreResponseDto[] allProducts = restTemplate.getForObject("https://fakestoreapi.com/products/", FakeStoreResponseDto[].class);
        // FakeStoreResponseDto[] allProducts = new FakeStoreResponseDto[]{restTemplate.getForObject("https://fakestoreapi.com/products/", FakeStoreResponseDto.class)}; // new Array with .class

        // Check if the response is null or empty
        if(allProducts == null || allProducts.length == 0) {
            throw new ProductNotFoundException("No products found");
        }
        // Convert FakeStoreResponseDto[] to List<Product>
        // return Arrays.stream(allProducts).map(FakeStoreResponseDto::toProduct).toList(); // Lambda expression
        List<Product> productsList = convertToProductList(allProducts);
        redisTemplate.opsForValue().set("ALL_PRODUCTS", productsList);
        return productsList;
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

    @Override
    public Product applyPatchToProductById(long id, JsonPatch jsonPatch) throws ProductNotFoundException, JsonPatchException, JsonProcessingException {
        // Get the product from the fake store API
//        FakeStoreResponseDto fakeStoreResponseDto = restTemplate.getForObject("https://fakestoreapi.com/products/" + id, FakeStoreResponseDto.class);
//        if(fakeStoreResponseDto == null) throw new ProductNotFoundException("Product not found with id " + id);

        // Get Existing Product
        Product existingProduct = getProductById(id);
        // Convert the existing product into JSON format
        ObjectMapper objectMapper = new ObjectMapper();
        // Convert the existing product to a JsonNode
        JsonNode patchedProduct = objectMapper.valueToTree(existingProduct);
        // Apply the patch to the product
        JsonNode patchedJsonPatch = jsonPatch.apply(patchedProduct);
        // Convert the patched JSON back to a Product object
        Product patchedProductObject = objectMapper.treeToValue(patchedJsonPatch, Product.class);
        // Use the replace product method to update the product
        return replaceProductById(id,
                patchedProductObject.getName(),
                patchedProductObject.getPrice(),
                patchedProductObject.getDescription(),
                patchedProductObject.getCategory().getName(),
                patchedProductObject.getImageUrl()
        );
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
