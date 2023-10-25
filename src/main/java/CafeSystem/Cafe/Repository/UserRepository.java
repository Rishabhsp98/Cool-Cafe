package CafeSystem.Cafe.Repository;


import CafeSystem.Cafe.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    @Query("Select u from User u where u.email = :email")
    User findByEmailId(String email);
}
