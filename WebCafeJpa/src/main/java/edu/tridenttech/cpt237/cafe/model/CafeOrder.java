package edu.tridenttech.cpt237.cafe.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class CafeOrder {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long orderId;

//	@OneToMany(fetch = FetchType.EAGER)
	@OneToMany(mappedBy = "orderId")
    //@JoinColumn(name = "cafeorder_menuitem")
	private Set<OrderItem> orderedItems = new HashSet<>();

	private boolean pending = true;

	public CafeOrder() {}
	
	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public void addItem(MenuItem item, int numPurchased) {
		orderedItems.add(new OrderItem(item, numPurchased, orderId));
	}
	
	@JsonIgnore
	public double getTotalCost() {
		double cost = getCostByList(orderedItems);
		return cost;
	}
	
	@JsonIgnore
	private double getCostByList(Set<OrderItem> list) {
		double cost = 0;
		for (OrderItem item : list) {
			cost += item.getCost();
		}
		return cost;
	}
	
	public Long getOrderId() {
		return orderId;
	}

	public List<OrderItem> getOrderedItems() {
		return (new ArrayList<OrderItem>(orderedItems));
	}
	
	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", pending=" + pending /*+ ", orderedItems=" + orderedItems*/ + "]";
	}

}
