package kg.backend.tinder.repository;

import kg.backend.tinder.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query(value = """
    SELECT role
    FROM UserRole role
    WHERE role.id = 3
    """)
    UserRole getRoleUser();
}
