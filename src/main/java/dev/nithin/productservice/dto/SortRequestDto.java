package dev.nithin.productservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SortRequestDto {
    private String sortByParam;
    private SortDirection sortDirection;
}
