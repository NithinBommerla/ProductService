package dev.nithin.productservice.dto;

import dev.nithin.productservice.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductProjectionDto {
    private String name;
    private double price;
    private String description;
    private String imageUrl;
}
