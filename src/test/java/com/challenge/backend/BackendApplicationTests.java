package com.challenge.backend;

import com.challenge.backend.model.order.*;
import com.challenge.backend.model.product.Product;
import com.challenge.backend.model.product.ProductIdInput;
import com.challenge.backend.model.product.ProductIdPriceInput;
import com.challenge.backend.model.product.Rating;
import com.challenge.backend.model.user.User;
import com.challenge.backend.repository.OrderRepository;
import com.challenge.backend.service.OrderService;
import com.challenge.backend.service.ProductService;
import com.challenge.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class BackendApplicationTests {

	@Mock
	private UserService userService;

	@Mock
	private ProductService productService;

	@Mock
	private OrderRepository pedidoRepository;

	@InjectMocks
	private OrderService orderService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void deveCriarUmPedidoECalcularOSeuValorTotal() {
		Long userId = 1L;
		List<ProductIdInput> productIds = Arrays.asList(new ProductIdInput(1L), new ProductIdInput(2L));

		User user = new User();
		user.setId(userId);
		when(userService.getUserById(userId)).thenReturn(user);

		Rating rating = new Rating(5.0, 3);
		List<Product> products = Arrays.asList(
				new Product(1L, "Product 1", new BigDecimal("109.95"), "Desc 1", "Drama", "image", rating),
				new Product(2L, "Product 2", new BigDecimal("22.30"), "Desc 2", "Drama", "image", rating));
		when(productService.getProductsByIds(productIds)).thenReturn(products);

		OrderInput orderInput = new OrderInput(userId, productIds);
		List<OrderItem> orderItems = Arrays.asList(
				new OrderItem(1L, new BigDecimal("109.95"), 1, new BigDecimal("109.95")),
				new OrderItem(2L, new BigDecimal("22.30"), 1, new BigDecimal("22.30")));

		Order newOrder = new Order(UUID.fromString("ffb2b140-1856-42f9-9740-90cebe59033c"), userId,
				Status.PENDING, BigDecimal.valueOf(132.25), orderItems);


		when(orderService.calcularTotalPedido(orderInput)).thenReturn(newOrder);

		assertNotNull(newOrder.getId());
		assertEquals(userId, newOrder.getUserId());
		assertEquals(Status.PENDING, newOrder.getStatus());
		assertEquals(new BigDecimal("132.25"), newOrder.getTotalPrice());
		assertEquals(2, newOrder.getItems().size());

	}

	@Test
	void deveAdicionarItensAUmPedidoExistente() {
		Order pedidoExistente = new Order();
		pedidoExistente.setId(UUID.fromString("b888cb1d-7b22-4d9b-b4f0-ce4e31d1f6aa"));
		pedidoExistente.setUserId(1L);
		pedidoExistente.setStatus(Status.PENDING);
		pedidoExistente.setTotalPrice(BigDecimal.valueOf(109.95));

		OrderItem itemPedidoExistente = new OrderItem();
		itemPedidoExistente.setId(1L);
		itemPedidoExistente.setPrice(BigDecimal.valueOf(109.95));
		itemPedidoExistente.setAmount(1);
		pedidoExistente.setItems(List.of(itemPedidoExistente));

		when(pedidoRepository.save(pedidoExistente)).thenReturn(pedidoExistente);

		ProductIdPriceInput itemPedidoInput1 = new ProductIdPriceInput();
		itemPedidoInput1.setId(4L);
		itemPedidoInput1.setPrice(BigDecimal.valueOf(15.99));
		ProductIdPriceInput itemPedidoInput2 = new ProductIdPriceInput();
		itemPedidoInput2.setId(5L);
		itemPedidoInput2.setPrice(BigDecimal.valueOf(695));

		List<ProductIdPriceInput> itemPedidoInputs = List.of(itemPedidoInput1, itemPedidoInput2);
		AddOrderInput addOrderInput = new AddOrderInput(pedidoExistente.getId(), pedidoExistente.getUserId(), itemPedidoInputs);

		Order pedidoAtualizado = new Order();
		when(orderService.adicionarItensPedido(pedidoExistente.getId(), addOrderInput)).thenReturn(pedidoAtualizado);

		assertEquals(3, pedidoAtualizado.getItems().size());
		assertEquals(2, pedidoAtualizado.getItems().get(0).getAmount());
		assertEquals(BigDecimal.valueOf(930.89), pedidoAtualizado.getTotalPrice());

	}


	@Test
	void deveAtualizarStatusDeUmPedidoExistente() {

		Order pedidoExistente = new Order();
		pedidoExistente.setId(UUID.fromString("b888cb1d-7b22-4d9b-b4f0-ce4e31d1f6aa"));
		pedidoExistente.setUserId(1L);
		pedidoExistente.setStatus(Status.PENDING);
		pedidoExistente.setTotalPrice(BigDecimal.valueOf(132.25));


		OrderInput orderInput = new OrderInput();
		ProductIdInput p1 = new ProductIdInput(1L);
		ProductIdInput p2 = new ProductIdInput(2L);

		List<ProductIdInput> products = List.of(p1,p2);
		orderInput.setUserId(pedidoExistente.getUserId());
		orderInput.setProducts(products);

		when(pedidoRepository.findById(UUID.fromString("b888cb1d-7b22-4d9b-b4f0-ce4e31d1f6aa"))).thenReturn(Optional.of(pedidoExistente));
		when(pedidoRepository.save(pedidoExistente)).thenReturn(pedidoExistente);

		StatusInput statusInput = new StatusInput(pedidoExistente.getId(), pedidoExistente.getUserId(), Status.CONCLUDED);

		Order pedidoAtualizado = orderService.atualizarStatusPedido(pedidoExistente.getId(), statusInput);

		assertEquals(Status.CONCLUDED, pedidoAtualizado.getStatus());

	}

	@Test
	void deveRetornarExceptionCasoNaoEncontrarUsuarioAoCriarPedido() {

	}

	@Test
	void deveRetornarExceptionCasoNaoEncontrarUsuarioAoAtualizarStatus() {

	}

	@Test
	void deveRetornarExceptionCasoNaoEncontrarUsuarioAoAdicionarItensAoPedido() {

	}
}
