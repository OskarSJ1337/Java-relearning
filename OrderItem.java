import java.math.BigDecimal;

public class OrderItem {
    private Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Produkt måste finnas");
        }

        if (quantity < 1) {
            throw new IllegalArgumentException("Antal måste vara minst 1");
        }

        this.product = product;
        this.quantity = quantity;
    }

	public BigDecimal getTotalPrice() {
		BigDecimal price = product.getPrice();
		BigDecimal amount = BigDecimal.valueOf(quantity);
		BigDecimal total = price.multiply(amount);

		return total;
	}
}