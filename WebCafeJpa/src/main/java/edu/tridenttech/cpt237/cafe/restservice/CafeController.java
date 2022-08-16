package edu.tridenttech.cpt237.cafe.restservice;

import java.util.List;

import edu.tridenttech.cpt237.cafe.model.CafeOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import edu.tridenttech.cpt237.cafe.model.MenuItem;


@RestController
public class CafeController {

	private static final Logger log = LoggerFactory.getLogger(CafeController.class);

	@Autowired
	private CafeService cafeService;
	
	@GetMapping("/menuItems")
	public List<MenuItem> getMenuItems(@RequestParam(value = "type", defaultValue = "") String type) {
		List<MenuItem> items;
		if (type.length() == 0) {
			items = cafeService.getAllMenuItems();
		} else {
			items = cafeService.getMenuItemsByType(type);
		}
		return items;
	}

	@GetMapping("/menuItem")
	public MenuItem menuItemByName(@RequestParam(value = "name") String name) {
		return cafeService.getMenuItemByName(name);
	}

	@GetMapping("/orders")
	public List<CafeOrder> getOrders() {
		return cafeService.getOrderList();
	}

	@GetMapping("/startOrder")
	public long startOrder() {
		return cafeService.startOrder();
	}

	@GetMapping("/order")
	public CafeOrder orderById(@RequestParam(value = "id") long id) {
		return cafeService.getOrderById(id);
	}

	@GetMapping("/menuItemTypes")
	public List<String> getItemTypes() {
		return cafeService.getMenuItemTypes();
	}

	@PostMapping("/placeOrder")
	public void placeOrder(@RequestParam(value = "id") long id) {
		cafeService.placeOrder(id);
	}

	@DeleteMapping("/cancelOrder")
	public void cancelOrder(@RequestParam(value = "id") long id) {
		cafeService.cancelOrder(id);
	}

	@PostMapping("/add")
	public void add(@RequestParam(value = "id") long id, @RequestParam(value = "name") String name,
					@RequestParam(value = "num") int num) {
		cafeService.addLineItem(id, name, num);
	}
}

