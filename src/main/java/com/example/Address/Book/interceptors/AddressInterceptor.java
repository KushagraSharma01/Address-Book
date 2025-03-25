package com.example.Address.Book.interceptors;

import com.example.Address.Book.entities.AuthUser;
import com.example.Address.Book.repositories.UserRepository;
import com.example.Address.Book.utils.JwtTokenService;
import com.example.Address.Book.utils.RedisTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@CrossOrigin(origins = "http://localhost:4200")
public class AddressInterceptor implements HandlerInterceptor {

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
       try{
            // Get token from cookies
           System.out.println("inside interceptor");
            String token = null;

            String auth = request.getHeader("Authorization");
           System.out.println(auth);
            if(auth == null){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            token = auth.substring(7);
           System.out.println("token is "+token);

            // If no token, reject request
            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }



            // Decode token to get user ID
            Long userId = jwtTokenService.decodeToken(token);

            // Check for user in database with given id

            AuthUser foundUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            // Token is valid → Allow request
            return true;
       } catch (RuntimeException e) {
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           return false;
       }
    }
}