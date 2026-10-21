import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
	private Product product;
	private int quantity;

	List<String> orderList = new ArrayList<>();

    public Order() {
        this.status = OrderStatus.CREATED;
    }

    public OrderStatus getStatus() {
        return status;
    }

	public void addItem(product, quantity){
		OrderItem item = new OrderItem(product, quantity);
		orderList.add(item);
	}
	public void removeItem(productId){
		System.out.print("product");
	}
	public getTotalPrice(){
		return total;
	}

}