package com.example.repo;

import java.util.Optional;

import com.example.Product;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface ProductRepo extends CrudRepository<Product, String> {
    Optional<Product> findById(String id);
}