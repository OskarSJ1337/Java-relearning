import java.math.BigDecimal;
import java.util.Objects;

class Product {
	private int id;
	private String name;
	private BigDecimal price;

	public Product(int id, String name, BigDecimal price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}
}