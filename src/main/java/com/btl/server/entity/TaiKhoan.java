package com.btl.server.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.btl.server.enums.AuthProvider;
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

    @Column
    private String password;
    
    @Column(nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL;

    @OneToOne(mappedBy = "taiKhoan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private KhachHang khachHang;

    @Column(name = "is_locked", columnDefinition = "boolean default false")
    private Boolean locked = false; 

    /**
     * Lấy danh sách các quyền hạn được cấp cho tài khoản (Roles) của Spring Security UserDetails.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    /**
     * Lấy giá trị của locked.
     */
    public boolean isLocked() { 
        return this.locked != null ? this.locked : false; 
    }

    /**
     * Cập nhật giá trị cho locked.
     */
    public void setLocked(Boolean locked) { 
        this.locked = locked; 
    }

                @Override
    /**
     * Lấy giá trị của accountNonLocked.
     */
    public boolean isAccountNonLocked() { 
        return !isLocked(); 
    }

    @Override
    /**
     * Lấy giá trị của accountNonExpired.
     */
    public boolean isAccountNonExpired() { return true; }

    @Override
    /**
     * Lấy giá trị của credentialsNonExpired.
     */
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    /**
     * Lấy giá trị của enabled.
     */
    public boolean isEnabled() { return true; }

                /**
                 * Lấy giá trị của id.
                 */
                public Long getId() { return id; }
    /**
     * Cập nhật giá trị cho id.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Lấy giá trị của username.
     */
    public String getUsername() { return username; }
    /**
     * Cập nhật giá trị cho username.
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Lấy giá trị của password.
     */
    public String getPassword() { return password; }
    /**
     * Cập nhật giá trị cho password.
     */
    public void setPassword(String password) { this.password = password; }

        /**
         * Lấy giá trị của role.
         */
        public String getRole() { 
        if (this.role == null) return null;
        return this.role.startsWith("ROLE_") ? this.role : "ROLE_" + this.role; 
    }

        /**
         * Cập nhật giá trị cho role.
         */
        public void setRole(String role) { 
        if (role != null) {
            String upperRole = role.trim().toUpperCase();
            this.role = upperRole.startsWith("ROLE_") ? upperRole : "ROLE_" + upperRole;
        } else {
            this.role = null;
        }
    }

    /**
     * Lấy giá trị của khachHang.
     */
    public KhachHang getKhachHang() { return khachHang; }
    /**
     * Cập nhật giá trị cho khachHang.
     */
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    /**
     * Lấy giá trị của provider.
     */
    public AuthProvider getProvider() { return provider; }
    /**
     * Cập nhật giá trị cho provider.
     */
    public void setProvider(AuthProvider provider) { this.provider = provider; }
}