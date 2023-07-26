package com.challenge.backend.model.order;

import com.challenge.backend.model.product.ProductIdInput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderInput {

    private Long userId;
    private List<ProductIdInput> products;

}
