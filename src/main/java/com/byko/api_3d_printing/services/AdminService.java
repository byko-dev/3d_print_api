package com.byko.api_3d_printing.services;

import com.byko.api_3d_printing.configuration.JwtUtils;
import com.byko.api_3d_printing.database.AdminData;
import com.byko.api_3d_printing.database.repository.AdminRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@AllArgsConstructor
public class AdminService {

    private AdminRepository adminRepository;
    private BCryptPasswordEncoder encoder;

    private JwtUtils jwtUtils;

    public void changePassword(AdminData adminData, String password){
        adminData.setPassword(encoder.encode(password));
        adminRepository.save(adminData);
    }

    public AdminData getAdminAccount(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");
        String username = jwtUtils.extractUsername(authorizationHeader);
        AdminData adminData = adminRepository.findByUsername(username);
        return adminData;
    }

    public void setAdminLastActivity(AdminData adminData){
        adminData.setLastTimeActivity(System.currentTimeMillis());
        adminRepository.save(adminData);
    }

    public AdminData getByUsername(String username){
        return adminRepository.findByUsername(username);
    }

    public AdminData getLastActiveAdminData(){
        return adminRepository.findFirstByOrderByLastTimeActivityDesc();
    }



}
