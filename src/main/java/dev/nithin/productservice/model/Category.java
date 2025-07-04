package dev.nithin.productservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Entity
public class Category extends Base implements Serializable {
    // To let the ORM know that this field should be added as a foreign key in the table instead of product_category table
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER) // By default, it is LAZY, but we want to fetch it eagerly
    // @JsonIgnore // This Annotation is used to ignore this field when serializing the object to JSON in (Patch method)
    @Fetch(FetchMode.SUBSELECT)
    @JsonBackReference
    private List<Product> products;

    public Category( String name) {
        this.setName(name);
    }

    public Category() {
        // Default constructor
    }

    @JsonCreator
    public static Category of(String name) {
        return new Category(name);
    }
}
