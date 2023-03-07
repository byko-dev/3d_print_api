package com.byko.api_3d_printing.services;

import com.byko.api_3d_printing.configuration.JwtUtils;
import com.byko.api_3d_printing.database.AdminDAO;
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

    public void changePassword(AdminDAO adminData, String password){
        adminData.setPassword(encoder.encode(password));
        adminRepository.save(adminData);
    }

    public AdminDAO getAdminAccount(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");
        String username = jwtUtils.extractUsername(authorizationHeader);
        AdminDAO adminData = adminRepository.findByUsername(username);
        return adminData;
    }

    public void setAdminLastActivity(AdminDAO adminData){
        adminData.setLastTimeActivity(System.currentTimeMillis());
        adminRepository.save(adminData);
    }

    public AdminDAO getByUsername(String username){
        return adminRepository.findByUsername(username);
    }

    public AdminDAO getLastActiveAdminData(){
        return adminRepository.findFirstByOrderByLastTimeActivityDesc();
    }



}
