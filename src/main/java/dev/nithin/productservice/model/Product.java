package dev.nithin.productservice.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Product {
    private long id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private Category category;
}
