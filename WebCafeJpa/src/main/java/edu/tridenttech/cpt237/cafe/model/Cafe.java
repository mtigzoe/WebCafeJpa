package edu.tridenttech.cpt237.cafe.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Cafe {

	private ArrayList<MenuItem> menuItems = new ArrayList<>();
	private String[] itemTypes = {"Bakery", "Beverage", "Sandwich"};
	private ArrayList<CafeOrder> placedOrderList = new ArrayList<>();
	private ArrayList<CafeOrder> pendingOrderList = new ArrayList<>();

	/**
	 * Initializes the Cafe object.  Loads the cafe from the provided
	 * configuration file.
	 */
	public Cafe() throws FileNotFoundException {
		String configPath = "C:/tmp/cafeMenu.txt";
		loadItems(configPath);
	}

	private void loadItems(String configPath) throws FileNotFoundException {
		Scanner input = new Scanner(new File(configPath));
		while (input.hasNext()) {
			String line = input.nextLine();
			String[] fields = line.split(",");
			char typeChar = fields[0].toUpperCase().charAt(0);
			String name = fields[1];
			String type;
			MenuItem item;
			double price = Double.parseDouble(fields[2]);
			switch (typeChar) {
				case 'D': {
					type = "Beverage";
				} break;
				case 'S': {
					type = "Sandwich";
				} break;
				case 'B': {
					type = "Bakery";
				} break;
				default: {
					System.err.printf("Unknown type: %c%n", typeChar);
					continue;
				}
			}
			item =  new MenuItem(type, name, price);
			menuItems.add(item);
		}
		input.close();
	}
	
	public List<CafeOrder> getOrderList() {
		return Collections.unmodifiableList(placedOrderList);
	}

	
	 //Adds a line to the specified order.
	public void addLineItem(long orderId, String itemName, int numOrdered) {
		CafeOrder order = findPendingOrder(orderId);
		MenuItem item = findItemByName(itemName);
		if (order != null && item != null) {
			order.addItem(item, numOrdered);
		}
	}
	
	private MenuItem findItemByName(String itemName) {
		for (MenuItem item : menuItems) {
			if (itemName.equalsIgnoreCase(item.getName())) {
				return item;
			}
		}
		return null;
	}

	 // Get the categories of items offered by the cafe.
	public List<String> getMenuItemTypes()  {
		return new ArrayList<String>(Arrays.asList(itemTypes));
	}
	
	 //Get all of the items on the menu.

	public List<MenuItem> getAllMenuItems() {
		return Collections.unmodifiableList(menuItems);
	}
	
	 // Get the items on the menu limited to a particular category.
	public List<MenuItem> getMenuItemsByType(String type) {
		List<MenuItem> items = new ArrayList<>();
		for (MenuItem item : menuItems) {
			if (type.equalsIgnoreCase(item.getType())) {
				items.add(item);
			}
		}
		return items;
	}

	public MenuItem getMenuItemByName(String itemName) {
		return findItemByName(itemName);
	}

	 //Find the order corresponding to the provided id and return this Order
	 //to the caller.
	public CafeOrder getOrderById(long id) {
		ArrayList<CafeOrder> all = new ArrayList<>();
		all.addAll(placedOrderList);
		all.addAll(pendingOrderList);
		return findOrderById(all, id);
	}
	
	 //Creates a new Order to hold items added by the user.  The returned order
	 //id is to be used when adding to the order, placing the order or/
	public long startOrder() {
		CafeOrder order = new CafeOrder();
		pendingOrderList.add(order);
		return order.getOrderId();
	}
	
	//Confirm that the order is being placed.
	public void placeOrder(long id) {
		CafeOrder order = findPendingOrder(id);
		if (order != null) {
			placedOrderList.add(order);
		}
	}

	 //Cancel the order.
	public void cancelOrder(long id) {
		CafeOrder order = findPendingOrder(id);
		if (order != null) {
			pendingOrderList.remove(order);
		}
	}
	
	private CafeOrder findOrderById(ArrayList<CafeOrder> list, long id) {
		return list.stream()
				   .filter(e -> e.getOrderId() == id)
				   .findAny()
				   .orElse(null);
	}

	public CafeOrder findPendingOrder(long id) {
		return findOrderById(pendingOrderList, id);
	}
	
	public CafeOrder findPlacedOrder(long id) {
		return findOrderById(placedOrderList, id);
	}
	public void displayOrders() {
		for (CafeOrder order : placedOrderList) {
			System.out.printf("Order # %d%n", order.getOrderId());
			for (OrderItem item : order.getOrderedItems()) {
				System.out.println(item);
			}
		}
	}
}
