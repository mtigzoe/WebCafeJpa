package edu.tridenttech.cpt237.cafe.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class OrderItem {
	@ManyToOne(fetch = FetchType.EAGER, optional=false)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	private MenuItem item;

	private int numSold;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private Long orderId;

	protected OrderItem(){}


	public OrderItem(MenuItem item, int numSold , long orderId) {
		this.item = item;
		this.numSold = numSold;
		this.orderId = orderId;
	}

	@JsonIgnore
	public String getItemName() {
		return item.getName();
	}

	public int getNumSold() {
		return numSold;
	}

	@JsonIgnore
	public double getPriceEach() {
		return item.getBaseCost();
	}

	@JsonIgnore
	public double getCost() {
		return getPriceEach() * numSold;
	}

	public Long getOrderId() {
		return orderId;
	}

	@Override
	public String toString() {
		return String.format("LineItem [item=%s, numSold=%d, priceEach=%.2f, total=%.2f]",
				              getItemName(), numSold, getPriceEach(), getCost());
	}

}
