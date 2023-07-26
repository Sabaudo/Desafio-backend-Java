package com.challenge.backend.model.order;

import com.challenge.backend.model.product.ProductIdPriceInput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddOrderInput {

    private UUID id;
    private Long userId;
    private List<ProductIdPriceInput> items;
}
