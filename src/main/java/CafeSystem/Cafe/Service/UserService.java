package CafeSystem.Cafe.Service;


import org.springframework.http.ResponseEntity;

import java.util.Map;


public interface UserService {
    ResponseEntity<String> signUp(Map<String, String> requestMap);

    ResponseEntity<String> loginUser(Map<String, String> requestMap);
}
