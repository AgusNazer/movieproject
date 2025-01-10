package movieproject.movieproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import movieproject.movieproject.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Método para encontrar un usuario por email
    User findByEmail(String email);
    
    // Puedes agregar más métodos personalizados si los necesitas, por ejemplo:
    // List<User> findByRole(String role);
}
