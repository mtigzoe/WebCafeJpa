package edu.tridenttech.cpt237.cafe.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MenuItem {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String name;
	private double baseCost;
	private String type;

	protected MenuItem() {}

	public MenuItem(String type, String name, double cost) {
		this.name = name;
		this.baseCost = cost;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public double getBaseCost() {
		return baseCost;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "MenuItem [ id=" + id + ", name=" + name + ", type=" + type + ", baseCost=" + baseCost + "]";
	};

	
}