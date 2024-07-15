package com.aluracursos.literalura.literalura.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aluracursos.literalura.literalura.model.Autor;
import com.aluracursos.literalura.literalura.model.Libro;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    Optional<Libro> findByTituloContainsIgnoreCase(String titulo);

    @Query("SELECT l FROM Libro l")
    List<Libro> findAllLibros();

    @Query("SELECT a FROM Libro l Join l.autor a")
    List<Autor> findAllAutores();

    @Query("""
            SELECT DISTINCT a FROM Autor a
            JOIN a.libro l
            WHERE a.fechaNacimiento <= :anho
            AND (a.fechaMuerte IS NULL OR a.fechaMuerte >= :anho)
            """)
    Optional<List<Autor>> listaAutoresPorAño(int anho);

    @Query(value = """
            SELECT * FROM libros l
            WHERE :lenguaje = ANY(l.lenguajes)
            """, nativeQuery = true)
    Optional<List<Libro>> listarLibrosPorIdioma(@Param("lenguaje") String lenguaje);

}
