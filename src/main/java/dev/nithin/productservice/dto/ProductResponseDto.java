package dev.nithin.productservice.dto;

import dev.nithin.productservice.model.Product;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductResponseDto {
    private long id;
    private String title;
    private double price;
    private String description;
    private String image;
    private String category;

    public static ProductResponseDto from(Product product) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setTitle(product.getName());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setDescription(product.getDescription());
        productResponseDto.setImage(product.getImageUrl());
        productResponseDto.setCategory(product.getCategory().getName());

        return productResponseDto;
    }

}
