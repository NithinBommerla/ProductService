package dev.nithin.productservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FakeStoreRequestDto {
    // This dto is sent to third party API to create a product so has same fields as fake store product not our product
    private String title;
    private double price;
    private String description;
    private String image;
    private String category;
}
