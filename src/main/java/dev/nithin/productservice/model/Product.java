package dev.nithin.productservice.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Setter
@Getter
@Entity
public class Product extends Base {
    private String description;
    private double price;
    private String imageUrl;
    @ManyToOne(fetch = FetchType.LAZY) // By default, it is EAGER, but we want to fetch it lazily
    @JsonManagedReference
    @Fetch(FetchMode.JOIN)
    private Category category;
}
