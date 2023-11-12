package CafeSystem.Cafe.Repository;


import CafeSystem.Cafe.Models.User;
import CafeSystem.Cafe.Wrapper.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("Select u from User u where u.email = :email")
    User findByEmailId(String email);


    @Query("Select u from User u where u.role='user'")
    List<User> getAllUser();


    @Transactional
    @Modifying
    @Query("update User u set u.status= :status where u.id =:id")
    Integer updateStatus(String status, Integer id);


    @Query("Select u.email from User u where u.role='admin'")
    List<String> getAllAdmin();

//    @Query("Select u from User u where u.name= :userName")
//    User findByUserName(String userName);
}
