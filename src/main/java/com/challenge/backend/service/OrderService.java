package com.challenge.backend.service;

import com.challenge.backend.model.order.*;
import com.challenge.backend.model.product.Product;
import com.challenge.backend.model.product.ProductIdPriceInput;
import com.challenge.backend.model.user.User;
import com.challenge.backend.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    EntityManager entityManager;

    @Transactional
    public Order calcularTotalPedido(OrderInput orderInput) {

        User user = this.userService.getUserById(orderInput.getUserId());

        if(user != null && !orderInput.getProducts().isEmpty()) {
            List<Product> products = productService.getProductsByIds(orderInput.getProducts());

            BigDecimal totalPrice = products.stream()
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Order order = new Order();
            order.setUserId(orderInput.getUserId());
            order.setStatus(Status.PENDING);
            order.setTotalPrice(totalPrice);

            List<OrderItem> items = new ArrayList<>();
            for (Product product : products) {
                OrderItem orderItem = new OrderItem();
                orderItem.setPrice(product.getPrice());
                orderItem.setAmount(1);
                orderItem.setPartialAmount(product.getPrice());
                items.add(orderItem);
            }
            order.setItems(items);
            return this.orderRepository.save(order);
        } else {
            throw new EntityNotFoundException("Pedido ou usuário não encontrado");
        }
    }

    public Order atualizarStatusPedido(UUID id, StatusInput statusInput) {
        Optional<Order> optionalPedido = orderRepository.findById(id);
        User user = this.userService.getUserById(statusInput.getUserId());

        if (user != null && optionalPedido.isPresent()) {
            Order pedido = optionalPedido.get();
            pedido.setStatus(statusInput.getStatus());
            return orderRepository.save(pedido);
        } else {
            throw new EntityNotFoundException("Pedido ou usuário não encontrado");
        }
    }

    public Order adicionarItensPedido(UUID pedidoId, AddOrderInput addOrderInput) {
        Optional<Order> orderOpt = orderRepository.findById(pedidoId);
        User user = this.userService.getUserById(addOrderInput.getUserId());

        if (user != null && orderOpt.isPresent()) {
            Order order = orderOpt.get();

            for (ProductIdPriceInput newItem : addOrderInput.getItems()) {
                Optional<OrderItem> optionalItemPedido = order.getItems().stream()
                        .filter(item -> item.getId().equals(newItem.getId()))
                        .findFirst();

                if (optionalItemPedido.isPresent()) {
                    OrderItem orderItem = optionalItemPedido.get();
                    orderItem.setAmount(orderItem.getAmount() + 1);
                } else {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setId(newItem.getId());
                    orderItem.setPrice(newItem.getPrice());
                    orderItem.setPartialAmount(newItem.getPrice());
                    orderItem.setAmount(1);
                    order.getItems().add(orderItem);
                }
            }

            BigDecimal totalPrice = order.getItems().stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getAmount())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            order.setTotalPrice(totalPrice);
            return orderRepository.save(order);
        } else {
            throw new EntityNotFoundException("Pedido ou usuário não encontrado");
        }
    }
}
