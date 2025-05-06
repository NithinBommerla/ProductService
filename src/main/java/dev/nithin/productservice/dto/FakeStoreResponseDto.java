package dev.nithin.productservice.dto;

import dev.nithin.productservice.model.Category;
import dev.nithin.productservice.model.Product;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FakeStoreResponseDto {
    private long id;
    private String title;
    private double price;
    private String description;
    private String image;
    private String category;


    // Mapping the response from the fake store API to our Product model
    public Product toProduct() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.title);
        product.setPrice(this.price);
        product.setDescription(this.description);
        product.setImageUrl(this.image);

        Category category = new Category();
        category.setName(this.category);

        product.setCategory(category);

        return product;
    }
}
