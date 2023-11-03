package CafeSystem.Cafe.Service;


import CafeSystem.Cafe.Wrapper.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;


public interface UserService {
    ResponseEntity<String> signUp(Map<String, String> requestMap);

    ResponseEntity<String> loginUser(Map<String, String> requestMap);

    ResponseEntity<List<UserDTO>> getAllUsers();

    ResponseEntity<String> updateStatus(Map<String, String> requestMap);
}
