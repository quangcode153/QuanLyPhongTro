package com.btl.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.btl.server.entity.HopDong;

@Repository
public interface HopDongRepository extends JpaRepository<HopDong, Integer> {
}