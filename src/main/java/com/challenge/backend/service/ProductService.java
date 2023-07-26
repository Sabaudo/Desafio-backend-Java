package com.challenge.backend.service;

import com.challenge.backend.model.product.Product;
import com.challenge.backend.model.product.ProductIdInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private WebClient productWebClient;

    public List<Product> getProducts() {
        Mono<List<Product>> monoProduct = this.productWebClient.method(HttpMethod.GET)
                .uri("/products")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Product>>() {});
        return monoProduct.block();
    }

    public Product getProductById(Long id) {
        Mono<Product> monoProduct = this.productWebClient.method(HttpMethod.GET)
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class);
        return monoProduct.block();
    }

    public List<Product> getProductsByIds(List<ProductIdInput> productIds) {
        if(Optional.of(productIds).isPresent()) {
            List<Product> products = new ArrayList<>();
            productIds.forEach(p -> {
                products.add(this.getProductById(p.getId()));
            });
            return  products;
        }
        return null;
    }
}
