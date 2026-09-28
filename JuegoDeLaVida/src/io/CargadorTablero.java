package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import modelo.Celda;
import modelo.Estado;
import modelo.EstadoEnfermo;
import modelo.EstadoLatente;
import modelo.EstadoMuerto;
import modelo.EstadoVivo;
import modelo.Tablero;


//INPUTS / OUTPUTS
public class CargadorTablero {
	
	//Leer archivo .txt y cargar tablero
	public static Tablero cargarDesdeArchivo(String ruta) throws FileNotFoundException, Exception {
		File archivo = new File(ruta);
		
		
		try(Scanner sc = new Scanner(archivo);){
			if (!sc.hasNextInt()) {//Busco si hay int para filas
				throw new Exception("Formato inválido: falta número de filas");
			}
			int filas = sc.nextInt();//tomo el int 
			if (filas <= 0) {//chequeo si ese int es igual o menor a 0(si o si tiene que haber filas y columnas declaradas)
				throw new Exception("Formato inválido: número de filas inválido");
			}

			//Ejerso el mismo chequeo y declaración que con filas
			if (!sc.hasNextInt()) {
			    throw new Exception("Formato inválido: falta número de columnas");
			}
	        int columnas = sc.nextInt();
			if (columnas <= 0) {
				throw new Exception("Formato inválido: número de columnas inválido");
			}

	        sc.nextLine(); //Limpiar el buffer
	
	        Tablero tablero = new Tablero(filas, columnas);
	
	        for (int i = 0; i < filas; i++) {
	            String linea = "";
				if (sc.hasNextLine()) linea = sc.nextLine();
	            for (int j = 0; j < columnas; j++) {
	                char c;
					if (j < linea.length()) c=linea.charAt(j);
					else c='.';
	                Celda nc = new Celda(crearEstadoSegunCaracter(c));
	                tablero.setCelda(i, j, nc);
	            }
	        }
	        sc.close();
	        return tablero;
		}
    }

    private static Estado crearEstadoSegunCaracter(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'O'	-> new EstadoVivo();   // O para vivo
            case 'E'    -> new EstadoEnfermo(); // Nuevo estado
            case 'X'    -> new EstadoLatente(); // Nuevo estado
            case '.'	-> new EstadoMuerto();
            default       -> new EstadoMuerto();  // cualquier otro
        };
    }
}
