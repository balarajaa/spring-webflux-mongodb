package com.learning.springwebfluxmongodb;

import com.learning.springwebfluxmongodb.controller.ProductController;
import com.learning.springwebfluxmongodb.dto.ProductDto;
import com.learning.springwebfluxmongodb.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(ProductController.class)
class SpringWebfluxMongodbApplicationTests {

	@Autowired
	private WebTestClient client;

	@MockBean
	private ProductService service;

	@Test
	public void addProductTest() {
		Mono<ProductDto> monoProduct = Mono.just(new ProductDto("101","Mobile",1,10000));
				when(service.saveProduct(monoProduct)).thenReturn(monoProduct);

				client.post().uri("/products").body(Mono.just(monoProduct),ProductDto.class)
						.exchange()
						.expectStatus().isOk();

	}

	@Test
	public void getProductsTest() {
		Flux<ProductDto> products = Flux.just(new ProductDto("101","Mobile",1,10000),
				new ProductDto("102","Book",1,1000));
		when(service.getProducts()).thenReturn(products);

		Flux<ProductDto> productDtoFlux = client.get().uri("/products")
				.exchange()
				.expectStatus().isOk()
				.returnResult(ProductDto.class)
				.getResponseBody();
		StepVerifier.create(productDtoFlux)
				.expectSubscription()
				.expectNext(new ProductDto("101","Mobile",1,10000))
				.expectNext(new ProductDto("102","Book",1,1000))
				.verifyComplete();
	}


}
