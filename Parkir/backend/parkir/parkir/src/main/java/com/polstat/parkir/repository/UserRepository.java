package com.polstat.parkir.repository;

import com.polstat.parkir.entity.User;
import com.polstat.parkir.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface UserRepository extends JpaRepository<User, Long>, CrudRepository<User, Long> {

    // Basic queries
    User findByEmail(String email);

    // Civitas Akademik queries
    @Query("SELECT u FROM User u WHERE u.role IN :roles")
    List<User> findAllCivitasAkademik(@Param("roles") List<Role> roles);

    // Custom summary query
    @Query("SELECT new map(" +
            "u.role as role, " +
            "COUNT(u) as count) " +
            "FROM User u GROUP BY u.role")
    List<Object> getUserCountByRole();

    // Search with multiple criteria
    @Query("SELECT u FROM User u WHERE " +
            "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    List<User> searchUsers(
            @Param("name") String name,
            @Param("role") Role role,
            @Param("email") String email
    );
}