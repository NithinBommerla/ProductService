package dev.nithin.productservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchRequestDto {
    private String query;
    private int pageNumber;
    private int pageSize;
    private SortRequestDto sortRequestDto;
}
