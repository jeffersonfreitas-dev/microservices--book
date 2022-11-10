package dev.jeffersonfreitas.productcompositeservice.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import dev.jeffersonfreitas.api.composite.product.ProductAggregate;
import dev.jeffersonfreitas.api.composite.product.ProductCompositeService;
import dev.jeffersonfreitas.api.composite.product.RecommendationSummary;
import dev.jeffersonfreitas.api.composite.product.ReviewSummary;
import dev.jeffersonfreitas.api.composite.product.ServiceAddresses;
import dev.jeffersonfreitas.api.core.product.Product;
import dev.jeffersonfreitas.api.core.recommendation.Recommendation;
import dev.jeffersonfreitas.api.core.review.Review;
import dev.jeffersonfreitas.api.exceptions.NotFoundException;
import dev.jeffersonfreitas.util.http.ServiceUtil;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService{
	
	private final ServiceUtil serverUtil;
	private ProductCompositeIntegration integration;
	
	@Autowired
	public ProductCompositeServiceImpl(ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
		this.serverUtil = serviceUtil;
		this.integration = integration;
	}

	@Override
	public ProductAggregate getProduct(int productId) {
		Product product = integration.getProduct(productId);
		if(product == null) {
			throw new NotFoundException("No product found for productId: " + productId);
		}
		
		List<Recommendation> recommendations = integration.getRecommendations(productId);
		List<Review> reviews = integration.getReviews(productId);
		return createProductAggregate(product, recommendations, reviews, serverUtil.getServiceAddress());
	}
	

	private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations,
			List<Review> reviews, String serviceAddress) {

	    int productId = product.getProductId();
	    String name = product.getName();
	    int weight = product.getWeight();

	    List<RecommendationSummary> recommendationSummaries =
	      (recommendations == null) ? null : recommendations.stream()
	        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate()))
	        .collect(Collectors.toList());

	    List<ReviewSummary> reviewSummaries = 
	      (reviews == null) ? null : reviews.stream()
	        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject()))
	        .collect(Collectors.toList());
	    
	    String productAddress = product.getServiceAddress();
	    String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
	    String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).getServiceAddress() : "";
	    ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);
	    return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
	}
}
