package com.btl.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.btl.server.entity.ChiSoDienNuoc;

@Repository
public interface ChiSoDienNuocRepository extends JpaRepository<ChiSoDienNuoc, Integer> {
}