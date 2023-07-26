package com.challenge.backend.api;

import com.challenge.backend.model.order.AddOrderInput;
import com.challenge.backend.model.order.Order;
import com.challenge.backend.model.order.OrderInput;
import com.challenge.backend.model.order.StatusInput;
import com.challenge.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderApi {

    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Order> add(@RequestBody OrderInput orderInput) {
        Order novoPedido = orderService.calcularTotalPedido(orderInput);
        return new ResponseEntity<>(novoPedido, HttpStatus.CREATED);
    }

    @RequestMapping(value ="/{id}/status", method = RequestMethod.PATCH)
    public ResponseEntity updateStatus(@PathVariable UUID id, @RequestBody StatusInput statusInput) {

        Order pedidoAtualizado = orderService.atualizarStatusPedido(id, statusInput);
        return ResponseEntity.ok(pedidoAtualizado);
    }

    @RequestMapping(value ="/{id}/items", method = RequestMethod.PUT)
    public ResponseEntity updateOrder(@PathVariable UUID id, @RequestBody AddOrderInput addOrderInput) {

        Order pedidoAtualizado = orderService.adicionarItensPedido(id, addOrderInput);
        return ResponseEntity.ok(pedidoAtualizado);
    }

}
