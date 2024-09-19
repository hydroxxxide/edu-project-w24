package kg.backend.tinder.repository;

import kg.backend.tinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getByUsername(String username);
    Optional<User> getByEmail(String email);
    Optional<User> getUserByResetToken(String token);

    @Query(value = """
    SELECT u
    FROM users u
    LEFT JOIN persons p ON u.person_id = p.id
    """, nativeQuery = true)
    Optional<User> getPersonUser(long userId);
}
