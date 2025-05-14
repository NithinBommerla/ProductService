package dev.nithin.productservice.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Product extends Base {
    private String description;
    private double price;
    private String imageUrl;
    @ManyToOne
    @JsonManagedReference
    private Category category;
}
