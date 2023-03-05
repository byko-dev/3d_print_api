package com.byko.api_3d_printing.configuration;

import com.byko.api_3d_printing.database.AdminData;
import com.byko.api_3d_printing.database.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MongoUserDetails implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminData adminData = adminRepository.findByUsername(username);

        if(adminData != null){
            return new User(adminData.getUsername(), adminData.getPassword(), true, true, true, true,
                    AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        }else{
            throw new UsernameNotFoundException("could not find the user '"
                    + username + "'");
        }
    }
}
