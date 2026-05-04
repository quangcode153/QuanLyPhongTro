package com.btl.server.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tai_khoan")
public class TaiKhoan implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String role;

    @OneToOne(mappedBy = "taiKhoan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private KhachHang khachHang;

    @Column(name = "is_locked", columnDefinition = "boolean default false")
    private Boolean locked = false; 

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    public boolean isLocked() { 
        return this.locked != null ? this.locked : false; 
    }

    public void setLocked(Boolean locked) { 
        this.locked = locked; 
    }

    // 🔥 FIX BẢO MẬT: Nối mạch biến locked với Spring Security
    // Hàm này hỏi "Tài khoản CÓ KHÔNG BỊ KHÓA phải không?". 
    // Nếu isLocked() là true (bị khóa) -> Trả về false (Không được phép đăng nhập)
    @Override
    public boolean isAccountNonLocked() { 
        return !isLocked(); 
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { 
        if (this.role == null) return null;
        return this.role.startsWith("ROLE_") ? this.role : "ROLE_" + this.role; 
    }

    public void setRole(String role) { 
        if (role != null) {
            String upperRole = role.trim().toUpperCase();
            this.role = upperRole.startsWith("ROLE_") ? upperRole : "ROLE_" + upperRole;
        } else {
            this.role = null;
        }
    }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
}