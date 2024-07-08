package com.aluracursos.literalura.literalura.principal;

import java.util.Scanner;

public class Principal {
    String menu = """
            Elija una de las siguientes opciones:
            1.- Buscar libro por título
            2.- Listar libros registrados
            3.- Listar autores registrados
            4.- Listar autores vivos en un determinado año
            5.- Listar listar libros por idioma
            0.- Salir
            """;
    int opcion = -1;
    Scanner entrada = new Scanner(System.in);

    public void mostrarMenu() {
        while (opcion != 0) {
            System.out.println(menu);
            opcion = entrada.nextInt();
            entrada.nextLine();
            switch (opcion) {
                case 1:

                    break;
                    
                case 2:

                    break;

                case 3:

                    break;

                case 4:

                    break;

                case 5:

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

}
