package CafeSystem.Cafe.ServiceImpl;


import CafeSystem.Cafe.Constants.CafeConstants;
import CafeSystem.Cafe.JWT.CustomerDetailsService;
import CafeSystem.Cafe.JWT.JWTUtil;
import CafeSystem.Cafe.Models.User;
import CafeSystem.Cafe.Repository.UserRepository;
import CafeSystem.Cafe.Service.UserService;
import CafeSystem.Cafe.Utils.CafeUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerDetailsService customerDetailsService;

    @Autowired
    JWTUtil jwtUtil;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Rest Request to SignUp User , {}", requestMap);

        try {
            if (!ValidateSignUpMap(requestMap)) {
                return CafeUtility.getResponseEntity(CafeConstants.Invalid_Request_Data, HttpStatus.BAD_REQUEST);
            } else {
                // means request body is correct
                User user = userRepository.findByEmailId(requestMap.get("email"));
                if (user != null)
                    return CafeUtility.getResponseEntity(CafeConstants.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                userRepository.save(getUserFromMap(requestMap));
                return CafeUtility.getResponseEntity("Successfully Registered", HttpStatus.OK);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        return CafeUtility.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean ValidateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();

        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> loginUser(Map<String, String> requestMap) {
        log.info("REST request to Login User");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );

            if(auth.isAuthenticated()){
                //Checking If the user is Authenticated by admin
                if(customerDetailsService.getUserDetails().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""+
                            jwtUtil.generateToken(customerDetailsService.getUserDetails().getEmail()
                        ,customerDetailsService.getUserDetails().getRole()) + "\"}",
                        HttpStatus.OK);
                }else{
                    return new ResponseEntity<String>(CafeConstants.WAIT_FOR_ADMIN_APPROVAL,HttpStatus.BAD_REQUEST);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ResponseEntity<String>(CafeConstants.BAD_CREDENTIALS,HttpStatus.BAD_REQUEST);

    }
}
