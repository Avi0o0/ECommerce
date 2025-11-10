package com.ecom.cartservice.dto;

public class CartDto {

	public static class AddToCartRequest extends com.ecom.cartservice.dto.AddToCartRequest {
		public AddToCartRequest() {
			super();
		}

		public AddToCartRequest(Long productId, Integer quantity) {
			super(productId, quantity);
		}
	}

	public static class UpdateQuantityRequest extends com.ecom.cartservice.dto.UpdateQuantityRequest {
		public UpdateQuantityRequest() {
			super();
		}

		public UpdateQuantityRequest(Long productId, Integer quantity) {
			super(productId, quantity);
		}
	}

	public static class CartResponse extends com.ecom.cartservice.dto.CartResponse {
		public CartResponse() {
			super();
		}

		public CartResponse(Long id, String userId, java.util.List<CartItemResponse> items, Integer totalItems,
				java.math.BigDecimal totalPrice, java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt) {
			super(id, userId,
					items.stream().map(item -> (com.ecom.cartservice.dto.CartResponse.CartItemResponse) item).toList(),
					totalItems, totalPrice, createdAt, updatedAt);
		}

		public static class CartItemResponse extends com.ecom.cartservice.dto.CartResponse.CartItemResponse {
			public CartItemResponse() {
				super();
			}

			public CartItemResponse(Long id, Long productId, String productName, String productDescription,
					String productImageUrl, Integer quantity, java.math.BigDecimal priceAtAddition,
					java.math.BigDecimal currentPrice, java.math.BigDecimal totalPrice,
					java.time.LocalDateTime addedAt) {
				super(id, productId, productName, productDescription, productImageUrl, quantity, priceAtAddition,
						currentPrice, totalPrice, addedAt);
			}
		}
	}
}
