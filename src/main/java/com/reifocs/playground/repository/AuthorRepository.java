package com.reifocs.playground.repository;

import com.reifocs.playground.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}