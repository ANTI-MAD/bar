package com.example.Bar.dto.menuItem;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MenuItemDTO {

    private Integer id;
    private String name;
    private String category;
    private String description;
    private Double price;
}