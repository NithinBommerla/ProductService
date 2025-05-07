package dev.nithin.productservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Category {
    private long id;
    private String name;

    public Category( String name) {
        this.name = name;
    }

    public Category() {
        // Default constructor
    }

    @JsonCreator
    public static Category of(String name) {
        return new Category(name);
    }
}
