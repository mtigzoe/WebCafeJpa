package edu.tridenttech.cpt237.cafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.tridenttech.cpt237.cafe.model.CafeOrder;

public interface CafeOrderRepository extends JpaRepository<CafeOrder, Long> {
}
