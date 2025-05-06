package dev.nithin.productservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFakeStoreProductRequestDto {
    // Dto for creating a Fake Store Product so has same fields as Product
    private String name;
    private double price;
    private String description;
    private String imageUrl;
    private String category;
}
