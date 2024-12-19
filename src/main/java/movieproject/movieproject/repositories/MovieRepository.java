package movieproject.movieproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import movieproject.movieproject.entity.Movie;


@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
   
}
