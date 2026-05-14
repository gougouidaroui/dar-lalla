package com.darlalla.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.darlalla.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategorieIgnoreCase(String categorie);

    List<Product> findByActiveTrue();

}