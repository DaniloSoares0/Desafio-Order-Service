package br.com.codenation.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {

	private ProductRepository productRepository = new ProductRepositoryImpl();
	
	private final Double DESCONTO = 0.2;


	@Override
	public Double calculateOrderValue(List<OrderItem> items) {

		return	items.stream().mapToDouble(product -> {

			double totalProdutos = 0;

			Optional<Product> produtoOptional = this.productRepository.findById(product.getProductId());

			if(produtoOptional.isPresent()) {

				Product produto = produtoOptional.get();

				totalProdutos = produto.getIsSale() ? 
						(produto.getValue() * (1 - DESCONTO) * product.getQuantity()) : produto.getValue() * product.getQuantity();

			}
			return totalProdutos;
		}).sum();
	}

	@Override
	public Set<Product> findProductsById(List<Long> ids) {

		return ids.stream().map(id -> this.productRepository.findById(id))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());

	}

	@Override
	public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
		return orders.stream().mapToDouble(value -> this.calculateOrderValue(value)).sum();
	}

	@Override
	public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {
		return findProductsById(productIds)
				.stream()
				.collect(Collectors.groupingBy(Product::getIsSale));
	}

}