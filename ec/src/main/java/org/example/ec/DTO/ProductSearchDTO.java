package org.example.ec.DTO;

import lombok.Data;

@Data
public class ProductSearchDTO {
    private String searchProductName;
    private String searchProductPrice;
    private String sortOrder;
}
