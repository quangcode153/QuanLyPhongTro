package com.btl.server.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.btl.server.entity.KhachHang;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.repository.TaiKhoanRepository.ChuTroProjection;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TaiKhoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Test
    public void testFindChuTroProjections_Success() {
        TaiKhoan chuTro = new TaiKhoan();
        chuTro.setUsername("chutro_real");
        chuTro.setPassword("pass");
        chuTro.setRole("ROLE_LANDLORD");
        chuTro.setLocked(false);
        
        KhachHang kh = new KhachHang();
        kh.setHoTen("Nguyễn Chủ Trọ");
        kh.setTaiKhoan(chuTro);
        chuTro.setKhachHang(kh);

        entityManager.persist(chuTro);
        entityManager.flush();

        List<ChuTroProjection> results = taiKhoanRepository.findChuTroProjections();

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getUsername()).isEqualTo("chutro_real");
        assertThat(results.get(0).getHoTen()).isEqualTo("Nguyễn Chủ Trọ");
        assertThat(results.get(0).getLocked()).isFalse();
    }
}