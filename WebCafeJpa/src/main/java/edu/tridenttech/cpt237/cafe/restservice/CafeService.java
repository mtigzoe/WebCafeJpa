package edu.tridenttech.cpt237.cafe.restservice;
import edu.tridenttech.cpt237.cafe.model.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tridenttech.cpt237.cafe.RestServiceApplication;
import edu.tridenttech.cpt237.cafe.model.CafeOrder;
import edu.tridenttech.cpt237.cafe.model.MenuItem;
import edu.tridenttech.cpt237.cafe.repository.CafeOrderRepository;
import edu.tridenttech.cpt237.cafe.repository.MenuItemRepository;
import edu.tridenttech.cpt237.cafe.repository.OrderItemRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;
@Service
@Transactional
public class CafeService {
	
	private static final Logger log = LoggerFactory.getLogger(CafeService.class);

	private String[] itemTypes = {"Bakery", "Beverage", "Sandwich"};

	//private static Cafe instance;
	@Autowired
	private MenuItemRepository menuItemRepository;
	@Autowired
	private CafeOrderRepository cafeOrderRepository;
	@Autowired
	private OrderItemRepository orderItemRepository;
	/**
	 * Initializes the Cafe object.  Loads the cafe from the provided
	 * configuration file.
	 */
	public CafeService() {
	}
	
	public List<CafeOrder> getOrderList() {
		return cafeOrderRepository.findAll();
	}

	 //Adds a line to the specified order.
	@Transactional
	public void addLineItem(long orderId, String itemName, int numOrdered) {
		CafeOrder order = findPendingOrder(orderId);
		MenuItem item = findItemByName(itemName);
		order.addItem(item, numOrdered);
		OrderItem orderItem = order.getOrderedItems().get(order.getOrderedItems().size() - 1);
		orderItemRepository.save(orderItem);
		cafeOrderRepository.save(order);
	}
	
	private MenuItem findItemByName(String itemName) {
		return menuItemRepository.findByName(itemName);
	}

	 //Get the categories of items offered by the cafe./
	public List<String> getMenuItemTypes()  {
		return new ArrayList<String>(Arrays.asList(itemTypes));
	}
	
	
	 //Get all of the items on the menu.
	public List<MenuItem> getAllMenuItems() {
		return menuItemRepository.findAll();
	}
	
	 //Get the items on the menu limited to a particular category.
	public List<MenuItem> getMenuItemsByType(String type) {
		return menuItemRepository.findAllByType(type);
	}

	public MenuItem getMenuItemByName(String itemName) {
		return findItemByName(itemName);
	}

	 //Find the order corresponding to the provided id and return this Order
	 //to the caller.
	public CafeOrder getOrderById(long id) {
		return findOrderById(id);
	}
	
	/**
	 * Creates a new Order to hold items added by the user.  The returned order
	 * id is to be used when adding to the order, placing the order or
	 * canceling the order.
	 */
	public long startOrder() {
		CafeOrder cafeOrder = new CafeOrder();
		cafeOrder = cafeOrderRepository.save(cafeOrder);
		return cafeOrder.getOrderId();
	}
	
	 //Confirm that the order is being placed.	 
	public void placeOrder(long id) {
		CafeOrder cafeOrder = getOrderById(id);
		if(cafeOrder != null) {
			if (cafeOrder.isPending()) {
				cafeOrder.setPending(false);
				cafeOrderRepository.save(cafeOrder);
			}
		}
	}

	 //Cancel the order.
	public void cancelOrder(long id) {
		CafeOrder cafeOrder = getOrderById(id);
		if(cafeOrder != null) {
			cafeOrderRepository.deleteById(cafeOrder.getOrderId());
		}
	}
	
	private CafeOrder findOrderById(long id) {
		return cafeOrderRepository.findById(id).orElse(null);
	}

	public CafeOrder findPendingOrder(long id) {
		CafeOrder cafeOrder = cafeOrderRepository.findById(id).orElse(null);
		if (cafeOrder != null) {
			return cafeOrder.isPending() ? cafeOrder : null;
		}
		return null;
	}
	
	public CafeOrder findPlacedOrder(long id) {
		CafeOrder cafeOrder = cafeOrderRepository.findById(id).orElse(null);
		if (cafeOrder != null) {
			return !cafeOrder.isPending() ? cafeOrder : null;
		}
		return null;
	}
}
