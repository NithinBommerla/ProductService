package dev.nithin.productservice.repository;

public class CustomQuery {

    public static final String GET_PRODUCTS_BY_CATEGORY_NAME = "SELECT * FROM product p WHERE p.category_id IN (SELECT id FROM category c WHERE c.name = :categoryName)";
}
