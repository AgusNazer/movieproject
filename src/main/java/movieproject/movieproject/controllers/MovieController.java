package movieproject.movieproject.controllers;

import movieproject.movieproject.entity.Movie;
import movieproject.movieproject.repositories.MovieRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.OptimisticLockException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private MovieRepository movieRepository;

    @CrossOrigin // Permitir solicitudes de diferentes dominios
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        return movie.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        
    }
    @CrossOrigin
    @PostMapping("/create")
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie){
           Movie savedMovie = movieRepository.save(movie);
           return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
           
    }
    @CrossOrigin
@PostMapping("/createMany")
public ResponseEntity<List<Movie>> createMovies(@RequestBody List<Movie> movies) {
    List<Movie> savedMovies = movieRepository.saveAll(movies);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedMovies);
}
@CrossOrigin
@PutMapping("/updateMany")
public ResponseEntity<List<Movie>> updateMovies(@RequestBody List<Movie> movies) {
    List<Movie> updatedMovies = movieRepository.saveAll(movies);
    return ResponseEntity.ok(updatedMovies);
}
    
    @CrossOrigin
    @DeleteMapping("/{id}")
    public ResponseEntity<Movie> deleteMovieById(@PathVariable Long id) {
       return ResponseEntity.noContent().build();
        
    }
    @CrossOrigin
@DeleteMapping("/deleteDuplicates")
public ResponseEntity<String> deleteDuplicateMovies() {
    // Obtener todas las películas
    List<Movie> allMovies = movieRepository.findAll();
    
    // Map para almacenar el primer título que encontramos y las películas duplicadas
    Map<String, Movie> movieMap = new HashMap<>();
    List<Movie> moviesToDelete = new ArrayList<>();
    
    for (Movie movie : allMovies) {
        if (movieMap.containsKey(movie.getTitle())) {
            // Si ya existe una película con ese título, la marcamos para eliminar
            moviesToDelete.add(movie);
        } else {
            // Si no, la agregamos al map
            movieMap.put(movie.getTitle(), movie);
        }
    }
    
    // Eliminar las películas duplicadas
    if (!moviesToDelete.isEmpty()) {
        movieRepository.deleteAll(moviesToDelete);
        return ResponseEntity.ok("Películas duplicadas eliminadas exitosamente.");
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("No se encontraron duplicados para eliminar.");
    }
}


    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie updatedMovie) {
        Optional<Movie> existingMovieOptional = movieRepository.findById(id);
    
        if (existingMovieOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        Movie existingMovie = existingMovieOptional.get();
    
        // Actualizar los campos necesarios
        existingMovie.setTitle(updatedMovie.getTitle());
        existingMovie.setDescription(updatedMovie.getDescription());
        existingMovie.setYear(updatedMovie.getYear());
        existingMovie.setVotes(updatedMovie.getVotes());
        existingMovie.setRating(updatedMovie.getRating());
        existingMovie.setImageUrl(updatedMovie.getImageUrl());
    
        try {
            Movie savedMovie = movieRepository.save(existingMovie);
            return ResponseEntity.ok(savedMovie);
        } catch (OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    @CrossOrigin
@GetMapping("/vote/{id}/{rating}")
public ResponseEntity<Movie> voteMovie(@PathVariable Long id, @PathVariable double rating) {
    if (!movieRepository.existsById(id)) {
        return ResponseEntity.notFound().build();
    }

    if (rating < 1 || rating > 10) {
        return ResponseEntity.badRequest().body(null); // Calificación fuera de rango
    }

    Optional<Movie> optional = movieRepository.findById(id);
    if (optional.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    Movie movie = optional.get();

    // Calculamos la nueva calificación ponderada
    double newRating = ((movie.getVotes() * movie.getRating()) + rating) / (movie.getVotes() + 1);
    movie.setVotes(movie.getVotes() + 1);
    movie.setRating(newRating);

    try {
        Movie savedMovie = movieRepository.save(movie);
        return ResponseEntity.ok(savedMovie);  // Devolvemos la película con la nueva calificación
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Manejo de error genérico
    }
}

}
 