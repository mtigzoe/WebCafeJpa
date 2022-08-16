package edu.tridenttech.cpt237.cafe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.tridenttech.cpt237.cafe.model.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
  MenuItem findByName(String name);
  // List<MenuItem> findAll(); implemented by default
  List<MenuItem> findAllByType(String type);
}