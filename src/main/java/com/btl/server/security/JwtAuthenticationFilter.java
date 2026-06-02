package com.btl.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.btl.server.entity.TaiKhoan;
import com.btl.server.repository.TaiKhoanRepository; 

import java.io.IOException;
import java.util.Optional;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * Bộ lọc xác thực JWT (OncePerRequestFilter).
 * Chặn mọi HTTP Request gửi tới Backend để trích xuất Token từ Header "Authorization: Bearer <token>",
 * kiểm tra hạn dùng, chữ ký số, tình trạng khóa tài khoản và thiết lập Security Context cho hệ thống Spring Security.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository; 

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Bỏ qua không quét Token đối với các API Đăng nhập và Đăng ký công khai
        if (requestPath.contains("/tai-khoan/login") || requestPath.contains("/tai-khoan/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // Nếu header trống hoặc không có tiền tố Bearer thì bỏ qua bộ lọc chuyển tiếp request
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            final String username = jwtService.extractUsername(token);

            // Nếu trích xuất được username và request này chưa được xác thực trong Session
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                Optional<TaiKhoan> userOpt = taiKhoanRepository.findByUsername(username);
                
                // Cơ chế bảo mật thời gian thực: Nếu tài khoản bị Admin khóa (locked), lập tức thu hồi quyền đăng nhập
                if (userOpt.isPresent() && userOpt.get().isLocked()) {
                    logger.warn("⚠️ Chặn truy cập: Tài khoản '{}' đang bị khóa!", username);
                    
                    SecurityContextHolder.clearContext(); // Xóa sạch chứng nhận cũ
                    
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\": \"Tài khoản của bạn đã bị khóa, phiên đăng nhập bị hủy!\"}");
                    
                    response.getWriter().flush();
                    return; 
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Nếu Token hợp lệ, tạo Authentication Token và đưa vào context bảo mật
                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                             );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (ExpiredJwtException e) {
            logger.warn("⚠️ Token hết hạn khi truy cập: {}", requestPath);
            SecurityContextHolder.clearContext(); // Xóa sạch context nếu token hết hạn

        } catch (Exception e) {
            logger.error("⚠️ JWT lỗi định dạng hoặc chữ ký: ", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}