package CafeSystem.Cafe.JWT;

import CafeSystem.Cafe.Models.User;
import CafeSystem.Cafe.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Slf4j
@Service
public class CustomerDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    private User userEntity;


    //getting the data from database by username, for username validation in authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside the loadUserByUsername , {}",username);
        userEntity = userRepository.findByEmailId(username); // we are using same method for username
        if(userEntity != null){
            return new org.springframework.security.core.userdetails.User(
                    userEntity.getEmail(),userEntity.getPassword(),new ArrayList<>()
            );
        }
        else
            throw new UsernameNotFoundException("User not found!.");
    }

    public User getUserDetails(){
        return userEntity;
    }
}
