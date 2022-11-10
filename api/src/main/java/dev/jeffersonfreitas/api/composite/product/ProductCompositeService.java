package dev.jeffersonfreitas.api.composite.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ProductCompositeService {
	
	@GetMapping(value = "/product-composite/{productId}")
	ProductAggregate getProduct(@PathVariable int productId);

}
