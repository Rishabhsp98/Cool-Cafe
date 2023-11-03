package CafeSystem.Cafe.ServiceImpl;


import CafeSystem.Cafe.Constants.CafeConstants;
import CafeSystem.Cafe.JWT.CustomerDetailsService;
import CafeSystem.Cafe.JWT.JWTUtil;
import CafeSystem.Cafe.JWT.JwtFilter;
import CafeSystem.Cafe.Models.User;
import CafeSystem.Cafe.Repository.UserRepository;
import CafeSystem.Cafe.Service.UserService;
import CafeSystem.Cafe.Utils.CafeUtility;
import CafeSystem.Cafe.Wrapper.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    JwtFilter jwtFilter;

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

    @Override
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("REST request to GET All Users");
        try{
            if(jwtFilter.isAdmin()){
                List<User> allUsers = userRepository.getAllUser();

                List<UserDTO> ans = new ArrayList<>();
                for(User user : allUsers){
                    ans.add(getUserDTO(user));
                }

                return new ResponseEntity<List<UserDTO>>(ans,HttpStatus.OK);
            }else{
                return new ResponseEntity<List<UserDTO>>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return new ResponseEntity<List<UserDTO>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        log.info("REST request to Update Status, {}",requestMap);
        try{
            if(jwtFilter.isAdmin()){
                Optional<User> user = userRepository.findById(Integer.parseInt(requestMap.get("id")));
                if(!user.isEmpty()){
                    userRepository.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                    return CafeUtility.getResponseEntity("User Status Updated SuccessFully",HttpStatus.OK);
                }else{
                    CafeUtility.getResponseEntity("User Id does not Exists",HttpStatus.BAD_REQUEST);
                }

            }else{
                return CafeUtility.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtility.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public UserDTO getUserDTO(User user){
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail(user.getEmail());
        userDTO.setContactNumber(user.getContactNumber());
        userDTO.setName(user.getName());
        userDTO.setStatus(user.getStatus());

        return userDTO;
    }
}
