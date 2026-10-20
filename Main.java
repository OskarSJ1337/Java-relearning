public class Main {
    public static void main(String[] args) {
        // Giltig produkt
        Product keyboard = new Product(
                "1",
                "Keyboard",
                "1200.00"
        );

        System.out.println("Id: " + keyboard.getId());
        System.out.println("Namn: " + keyboard.getName());
        System.out.println("Pris: " + keyboard.getPrice());
		OrderItem item = new OrderItem(keyboard, 3);
		System.out.println(item.getTotalPrice());

		Order order = new Order();
		System.out.println(order.getStatus());
    }
}