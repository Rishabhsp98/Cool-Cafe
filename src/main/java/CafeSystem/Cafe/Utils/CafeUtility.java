package CafeSystem.Cafe.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CafeUtility {

    private CafeUtility(){

    }
    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus){
        return new ResponseEntity<String>("\"message\":"+responseMessage,httpStatus);
    }
}
