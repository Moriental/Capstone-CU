package com.example.capstonedesign.repository;

import com.example.capstonedesign.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepositroy  extends JpaRepository<Menu, Long> {
}
