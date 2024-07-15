package com.aluracursos.literalura.literalura.principal;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.aluracursos.literalura.literalura.model.Autor;
import com.aluracursos.literalura.literalura.model.DatosAutor;
import com.aluracursos.literalura.literalura.model.DatosBusqueda;
import com.aluracursos.literalura.literalura.model.DatosLibro;
import com.aluracursos.literalura.literalura.model.Libro;
import com.aluracursos.literalura.literalura.repository.LibroRepository;
import com.aluracursos.literalura.literalura.service.ConsultaAPI;
import com.aluracursos.literalura.literalura.service.ConvierteDatos;

public class Principal {
    private Scanner entrada = new Scanner(System.in);
    private ConsultaAPI consultaAPI = new ConsultaAPI();
    private final String URL = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository repositorio;

    private String menu = """
            Elija una de las siguientes opciones:
            1.- Buscar libro por título
            2.- Listar libros registrados
            3.- Listar autores registrados
            4.- Listar autores vivos en un determinado año
            5.- Listar libros por idioma
            0.- Salir
            """;
    private int opcion = -1;

    public Principal(LibroRepository repository) {
        this.repositorio = repository;
    }

    public void mostrarMenu() {
        while (opcion != 0) {
            System.out.println(menu);
            opcion = entrada.nextInt();
            entrada.nextLine();
            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;

                case 2:
                    listarLibrosRegistrados();
                    break;

                case 3:
                    listarAutoresRegistrados();
                    break;

                case 4:
                    listarAutoresPorAño();
                    break;

                case 5:
                    listarLibrosPorIdioma();
                    break;

                case 0:
                    System.out.println("Terminando ejecución");
                    break;

                default:
                    System.out.println("Digite una opción válida");
                    break;
            }
        }
    }

    private void buscarLibro() {
        // Preguntamos al usuario qué libro desea
        System.out.println("Ingrese el nombre del libro que desea buscar");
        String tituloLibro = entrada.nextLine();
        // Hacemos la consulta al api
        String json = consultaAPI.obtenerDatosAPI(URL + "?search=" + tituloLibro.replace(" ", "+"));
        // System.out.println(json);
        // Convertimos los datos a la clase DatosBusqueda
        var datosBusqueda = conversor.obtenerDatos(json, DatosBusqueda.class);
        // System.out.println(datosBusqueda);
        // Consultamos dentro de la lista de resultados el libro
        Optional<DatosLibro> libroBuscado = datosBusqueda.resultado().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            // Si lo conseguimos convertimos al tipo DatosLibro
            var libroObtenido = libroBuscado.get();
            // Buscamos el libro en nuestro repositorio para validar si ya se ha guardado
            Optional<Libro> libroRegistrado = repositorio.findByTituloContainsIgnoreCase(libroObtenido.titulo());

            if (libroRegistrado.isPresent()) {
                // Si ya ha sido consultado el libro mandamos mensaje
                System.out.println("El libro " + libroRegistrado.get().getTitulo()
                        + " ya fue registrado. No se puede agregar un libro dos veces.");
            } else {
                // En caso contrario mostramos la información y guardamos el libro
                imprimirDatosLibro(libroObtenido);
                Libro libroAGuardar = new Libro(libroObtenido);
                Autor autor = new Autor(libroObtenido.autores().get(0));
                libroAGuardar.setAutor(autor);
                repositorio.save(libroAGuardar);
            }
        } else {
            System.out.println("Libro no encontrado. Intente con otro nombre\n");
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> listaLibrosRegistrados = repositorio.findAllLibros();
        listaLibrosRegistrados.stream().forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
        List<Autor> listaAutoresRegistrados = repositorio.findAllAutores();
        listaAutoresRegistrados.forEach(System.out::println);
    }

    private void listarAutoresPorAño() {
        System.out.println("Ingrese el año en el que deseé buscar los autores vivos");
        int anho = entrada.nextInt();
        entrada.nextLine();

        Optional<List<Autor>> listaAutoresPorAño = repositorio.listaAutoresPorAño(anho);
        if (listaAutoresPorAño.isPresent()) {
            List<Autor> autores = listaAutoresPorAño.get();
            if (!autores.isEmpty()) {
                System.out.println("Autores vivos en el año " + anho + ":");
                autores.forEach(System.out::println);
            } else {
                System.out.println("No se encontraron autores vivos en el año " + anho);
            }
        } else {
            System.out.println("No se encontraron autores vivos en el año " + anho);
        }

    }

    private void listarLibrosPorIdioma() {
        System.out.println("Escriba el idioma para buscar los libros");
        System.out.println("""
                            es-Español
                            en-Inglés
                            fr-Francés
                            pt-Portugés
                            """);
        String idioma = entrada.nextLine().toLowerCase();
        Optional<List<Libro>> listaLibrosPorIdioma = repositorio.listarLibrosPorIdioma(idioma);
        if (listaLibrosPorIdioma.isPresent()) {
            listaLibrosPorIdioma.get().forEach(System.out::println);
        } else {
            System.out.println("No se encontraron libros con ese idioma");
        }
    }

    public String imprimirDatosLibro(DatosLibro libro) {
        String datos = "_______________________"
                + "\n¡Libro encontrado!"
                + "\nTítulo: " + libro.titulo()
                + "\nAutor: " + libro.autores().get(0).nombre()
                + "\nLenguaje: " + libro.lenguajes().get(0)
                + "\nNúmero de descargas: " + libro.numeroDescargas()
                + "\n_______________________";
        return datos;
    }
}
