package CafeSystem.Cafe.JWT;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTUtil {

    private String secret_key = "rishabh@sp98";


    //Extract UserName from token
    public String extractUserName(String token){
        return extractClaims(token,Claims::getSubject);
    }

    public Date extractExpirationTime(String token){
        return extractClaims(token,Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token){
        return extractExpirationTime(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(String username,String role){
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",role);
        return createToken(claims,username);
    }

    private String createToken(Map<String,Object> claims,String subject){
        return Jwts.builder().
                setClaims(claims).
                setSubject(subject).
                setIssuedAt(new Date(System.currentTimeMillis())).
                setExpiration(new Date(System.currentTimeMillis() + 1000 * 10 *10 *10)).
                signWith(SignatureAlgorithm.HS384,secret_key).compact();
    }

    public <T> T extractClaims(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(secret_key).parseClaimsJws(token).getBody();
    }
}
