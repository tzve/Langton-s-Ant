package PracticaHormigasLangton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/* 19/10/2021 @author Tzvetan Petiov Hristov
 * 
 * Grabador. Cuando las Hormigas se han movido, el Director comunica al Grabador el estado del tablero,
 * y éste lo guarda en un archivo de texto. En lugar de colores, se utilizarán caracteres.
 * El color 0 es el espacio ‘ ‘.
 * El color 1 es la almohadilla ‘#’.
 * El color 2 es la barra  inclinada ‘/’.
 * El color 3 es el ampersand ‘&’.
 * Se deja a criterio del alumno incluir más colores para definir reglas más complejas, pero los cuatro anteriores son obligatorios.
 */

public class GrabadorPracticaLangton {
	private static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		//creamos el fichero, si existe lo borramos
	    File f = new File("tableroTzvetan.txt");
	    if (f.exists()) f.delete();
	    
		//aunque parezca que recibe un int, realmente recibe un string...
		String numMovimientos = scanner.nextLine();
		for (int i = 0; i<Integer.parseInt(numMovimientos);++i) {
			//protocolo = "10;10;4,7;4,7;0000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000;1";
			String protocolo = scanner.nextLine();
			iniciarGrabadora(protocolo, f);
		}//for
	}//main()
	
	private static void iniciarGrabadora(String protocolo, File f) {
		 //mensaje = bw.write(x+";"+xHormiga+";"+yHormiga+";"+tabla);
		 try{
		    FileWriter fw = new FileWriter(f, true);
		    
		    //protocolo = "xTabla;yTabla;xHormiga[];yHormiga[];tablaSinProcesar;numMovimientoEnCurso";
			String componentesprotocolo[] = protocolo.split(";");
		    int    xTabla	 			  = Integer.parseInt(componentesprotocolo[0]);
		    int    yTabla	 			  = Integer.parseInt(componentesprotocolo[1]);
		    String xHormiga[]		 	  = (componentesprotocolo[2].split(","));
		    String yHormiga[] 		 	  = (componentesprotocolo[3].split(","));
		    String tablaSinProcesar  	  = componentesprotocolo[4];
		    int    numMovimientoEnCurso   = Integer.parseInt(componentesprotocolo[5]);
		    
		    String[][] tablaProcesada = crearTablero(xTabla, yTabla, tablaSinProcesar);
		    //este for sirve por si hay más de una hormiga, me las localice todas y las marque con "¥"
		    for(int i = 0; i<xHormiga.length; ++i) {
		    	tablaProcesada[Integer.parseInt(xHormiga[i])][Integer.parseInt(yHormiga[i])] = "¥";
		    }
		    fw.write(protocolo+"\n");
		    grabarTablero(tablaProcesada, fw, numMovimientoEnCurso);
		    
		    fw.close();
		}catch(IOException e){ System.out.println(e.getMessage());}
		
	}//iniciarGrabadora()
	
	private static String[][] crearTablero(int xTabla, int yTabla, String tablaSinProcesar){
		//muestra el tablero en el entorno de desarrollo (Eclipse)
		//mostrar tablero, la primera dimensión del array representa la "y" y el segundo array la "x" -> tablero[y][x]=1;
		char charTabla;
		String[][] tablaProcesada = new String[yTabla][xTabla];
		int num = 0; 
        for(int i = 0; i < yTabla; ++i) {
        	for(int j =0; j<xTabla; ++j) {
        		charTabla = tablaSinProcesar.charAt(num);
        		//colores -> color0 = " "; color1 = "#"; color2 = "/"; color3 = "&";
        		if(charTabla == '0')
        			charTabla = ' ';
        		else if (charTabla == '1')
        			charTabla = '#';
        		else if (charTabla == '2')
        			charTabla = '/';
        		else if (charTabla == '3')
        			charTabla = '&';
        		tablaProcesada[i][j] = String.valueOf(charTabla);
        		++num;
        	}
        }
		return tablaProcesada;
	}//crearTablero()
	
	private static void grabarTablero(String[][] tablero, FileWriter fw, int numMovimientoEnCurso){
		try {
			fw.write("Movimiento "+numMovimientoEnCurso+"\n");
			for(int i = 0; i<tablero.length;++i) {
				for(int j = 0; j<tablero[0].length;++j) {
					fw.write(tablero[i][j]);
				}
				fw.write("\n");
			}
			fw.write("\n");
		} catch (Exception e) {System.out.println(e.getMessage());}
		
	}//grabarTablero()
	
}//GrabadorPracticaLangton


















