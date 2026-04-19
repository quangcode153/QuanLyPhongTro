package com.btl.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.btl.server.entity.KhachHang;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    }