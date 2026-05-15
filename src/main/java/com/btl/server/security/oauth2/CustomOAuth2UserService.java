package com.btl.server.security.oauth2;

import com.btl.server.entity.KhachHang;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.enums.AuthProvider;
import com.btl.server.repository.KhachHangRepository;
import com.btl.server.repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        
        return processOAuth2User(registrationId, oAuth2User);
    }

    private OAuth2User processOAuth2User(String registrationId, OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
        Optional<TaiKhoan> taiKhoanOptional = taiKhoanRepository.findByUsername(email);
        
        TaiKhoan taiKhoan;
        if (taiKhoanOptional.isPresent()) {
            taiKhoan = taiKhoanOptional.get();
            if (!taiKhoan.getProvider().equals(provider)) {
                throw new OAuth2AuthenticationException("Looks like you're signed up with " +
                        taiKhoan.getProvider() + " account. Please use your " + taiKhoan.getProvider() +
                        " account to login.");
            }
            updateExistingUser(taiKhoan, name);
        } else {
            taiKhoan = registerNewUser(email, name, provider);
        }

        return oAuth2User;
    }

    private TaiKhoan registerNewUser(String email, String name, AuthProvider provider) {
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setUsername(email);
        taiKhoan.setRole("TENANT");
        taiKhoan.setProvider(provider);
        taiKhoan = taiKhoanRepository.save(taiKhoan);

        KhachHang khachHang = new KhachHang();
        khachHang.setTaiKhoan(taiKhoan);
        khachHang.setHoTen(name);
        khachHang.setEmail(email);
        khachHangRepository.save(khachHang);

        return taiKhoan;
    }

    private void updateExistingUser(TaiKhoan taiKhoan, String name) {
        KhachHang khachHang = taiKhoan.getKhachHang();
        if (khachHang != null) {
            khachHang.setHoTen(name);
            khachHangRepository.save(khachHang);
        }
    }
}
