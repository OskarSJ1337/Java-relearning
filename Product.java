import java.math.BigDecimal;


public class Product {
	private String id;
	private String name;
	private BigDecimal price;

	public Product(String id, String name, String price) {
		if(id == null || id.isBlank()){
			throw new IllegalArgumentException("Id måste finnas");
		}

		if(name == null){
			throw new IllegalArgumentException("Namn måste finnas");
		}

		BigDecimal parsedPrice = new BigDecimal(price);
		if(parsedPrice.compareTo(BigDecimal.ZERO) <= 0){
			throw new IllegalArgumentException("Pris måste vara större än 0");
		}

		this.id = id;
		this.name = name;
		this.price = parsedPrice;
	}

	public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}