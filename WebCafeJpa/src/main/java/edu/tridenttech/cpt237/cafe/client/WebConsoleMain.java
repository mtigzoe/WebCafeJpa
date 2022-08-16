package edu.tridenttech.cpt237.cafe.client;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import edu.tridenttech.cpt237.cafe.model.OrderItem;
import edu.tridenttech.cpt237.cafe.model.CafeOrder;
import edu.tridenttech.cpt237.cafe.model.MenuItem;

public class WebConsoleMain {
	private final static String BASE_URL = "http://localhost:8080";

	public static void main(String[] args) throws FileNotFoundException {
		WebCafe cafe = new WebCafe(BASE_URL);
		Scanner console = new Scanner(System.in);
		List<String> menuTypes = cafe.getMenuItemTypes();
		//creates types menu
		String orderMenu = "Please select from the following types:\n";
		char menuChoice = 'A';
		String menuOptions = "";
		for (String type : menuTypes) {
			orderMenu += menuChoice + ") " + type + "\n";
			menuOptions += menuChoice;
			menuChoice++;
		}
		orderMenu +='Q' + ") Quit";
		menuOptions += 'Q';
			
		String mainMenu = "Would you like to place a new order? (Y/N)";
		char placeNewOrder;
		
		placeNewOrder = getValidatedSelection(console, mainMenu, "YN");
		while (placeNewOrder != 'N') {
			int currentOrderId = cafe.startOrder();
			char selection = getValidatedSelection(console, orderMenu, menuOptions);
			while (selection != 'Q') {
				String selectionType = menuTypes.get(selection - 'A');
				processPurchase(console, selectionType, cafe, currentOrderId);
				selection = getValidatedSelection(console, orderMenu, menuOptions);
			}
			cafe.placeOrder(currentOrderId);
			CafeOrder order = cafe.getOrderById(currentOrderId);
			displayOrder(order);
			placeNewOrder = getValidatedSelection(console, mainMenu, "YN");
		}
		
		displayAllOrders(cafe);

		console.close();
	}

	public static void displayAllOrders(WebCafe cafe) {
		for (CafeOrder order : cafe.getOrderList()) {
			System.out.printf("Order # %d%n", order.getOrderId());
			displayOrder(order);
		}
	}
	private static char getValidatedSelection(Scanner input, String prompt, String allowedChars) {
		char selected = getMenuSelection(input, prompt);
		while (allowedChars.indexOf(selected) < 0) {
			System.out.printf("%c is not a valid selection; please input a valid character.%n", selected);
			selected = getMenuSelection(input, prompt);
		}
		return selected;
	}
	
	private static char getMenuSelection(Scanner input, String prompt) {
		System.out.println(prompt);
		String response = input.nextLine();
		// Ensure that the user entered some value
		while (response.length() == 0 ) {
			System.out.println("Please input a value");
			System.out.println(prompt);
			response = input.nextLine();
		}
		return Character.toUpperCase(response.charAt(0));
	}
	
	private static void processPurchase(Scanner input, String category, WebCafe cafe, int currentOrder) {
		List<MenuItem> sales = cafe.getMenuItemsByType(category);

		String menu = createItemMenu(sales);
		String validChars = getAlphabetChars(sales.size());
		char bakerySelect = getValidatedSelection(input, menu, validChars);

		int ndx = bakerySelect - 'A';

		// should use validate method
		System.out.printf("How many would you like to purchase?");
		int numPurchased = input.nextInt();
		input.nextLine(); // clear the newline
		System.out.printf("You have selected %d %s(s).%n", numPurchased, sales.get(ndx).getName());
		cafe.addLineItem(currentOrder, sales.get(ndx).getName(), numPurchased);
	}
	
	private static String createItemMenu(List<MenuItem> itemList) {
		String itemMenu = "Select from the following list:\n";
		char itemType = 'A';
		for (MenuItem si : itemList) {
			double price = si.getBaseCost();
			itemMenu += String.format("%c) %-20s%8.2f%n", itemType, si.getName(), price);
			itemType++;
		}
		return itemMenu;
	}

	private static String getAlphabetChars(int size) {
		String validChars = "";
		char start = 'A';
		for (int i=0; i < size; i++) {
			validChars += (char)(start + i);
		}
		return validChars;
	}

	private static void displayOrder(CafeOrder order) {
		List<OrderItem> itemList = order.getOrderedItems();
		System.out.printf("%-20s%5s%8s%8s%n", "Item", "Cnt", "Price", "Cost");
		for (OrderItem item : itemList) {
			System.out.printf("%-20s%5d%8.2f%8.2f%n",
					item.getItemName(), item.getNumSold(), item.getPriceEach(), item.getCost());
		}
		System.out.printf("%-33s%8.2f%n", "Total", order.getTotalCost());
	}
}
