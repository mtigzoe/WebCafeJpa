package edu.tridenttech.cpt237.cafe.repository;

import org.springframework.data.repository.CrudRepository;
import edu.tridenttech.cpt237.cafe.model.OrderItem;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
}
