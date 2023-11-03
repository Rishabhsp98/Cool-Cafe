package CafeSystem.Cafe.Controller;

import CafeSystem.Cafe.Constants.CafeConstants;
import CafeSystem.Cafe.Service.UserService;
import CafeSystem.Cafe.Utils.CafeUtility;
import CafeSystem.Cafe.Wrapper.UserDTO;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(path = "/signUp")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap) {
            return userService.signUp(requestMap);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String,String> requestMap){
            return userService.loginUser(requestMap);
    }

    @GetMapping(path = "/getAllUsers")
    public ResponseEntity<List<UserDTO>>  getAllUsers(){
        return userService.getAllUsers();
    };

    @PostMapping(path = "/updateStatus")
    public ResponseEntity<String> updateStatus(@RequestBody(required = true) Map<String,String> requestMap){
        return userService.updateStatus(requestMap);
    }

}
