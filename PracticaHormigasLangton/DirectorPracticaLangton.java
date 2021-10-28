package PracticaHormigasLangton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/* 19/10/2021 @author Tzvetan Petiov Hristov
 * 
 * ENUNCIADO
 * - Terreno bidimensional, como una tabla de ajedrez, "x" "y"
 * - Pueden ir en cuatro direcciones
 * 
 * - Director. El Director debe arrancar al menos una instancia de los procesos Hormiga y Grabador (ver más abajo).
 *   El Director contiene la información del tablero bidimensional en el que se mueven las hormigas, y por tanto informa a
 *   éstas del color en el que se encuentran. El Director no mueve las hormigas, ni guarda constancia de sus posiciones a
 *   menos que sea imprescindible (ver el apartado “Criterios de calificación”).
 *   
 * - El director posee una configuración inicial que consta de los siguientes elementos:
 *		- Las rutas de los procesos Hormiga y Grabador.
 *		- Las dimensiones del tablero.
 * 		- La posición inicial de la hormiga.
 * 		- La orientación inicial de la hormiga.
 * 		- La regla que ha de seguir la hormiga en su comportamiento.
 * 		- El número máximo de movimientos que se deben registrar.
 * - El protocolo de comunicación entre Director y Hormiga debe ser el siguiente:
 * 		- El Director indica el ancho y el alto del tablero, número de hormigas, la posición inicial de la hormiga,
 * 		  su orientación y la regla que habrá de seguir. Lo hace una vez a modo de configuración inicial.
 * 		- A continuación, en cada movimiento el Director y la Hormiga se mandan los siguientes mensajes:
 * 			- El Director envía a la Hormiga un código indicando si la hormiga debe moverse o bien debe terminar
 *			  (cuando se ha llegado al número máximo de iteraciones).
 * 			- La Hormiga envía al Director su posición actual.
 * 			- El Director envía a la Hormiga el color actual de esa posición.
 * 		- Cuando la Hormiga se ha movido, el Director envía al Grabador los colores que componen el tablero.
 */

//ancho(x), alto(y), numHormigas, posicion incial hormiga, orientación, regla

public class DirectorPracticaLangton {
	private static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		System.out.println("Las Hormigas de Langton\n");
		
		System.out.println("Acceder a fichero(responder 'f') o configuración manual(responder 'm')");
		String respuestaScanner = scanner.nextLine().toLowerCase();
		if 		(respuestaScanner.equals("f")) iniciarJuegoFichero();
		else if (respuestaScanner.equals("m")) iniciarJuego();
		else System.out.println("respuesta incorrecta");
		
		System.out.println("\nPrograma finalizado.");
	}//main()
	
	private static void iniciarJuegoFichero() {
		try {
			String textoFichero = leerFichero("ficheroConfiguracionLangton.txt");
			
			String componenteTextoFr[] = textoFichero.split(":");
			int 			 x = Integer.parseInt(componenteTextoFr[1].replaceAll("\\s+",""));
			int 			 y = Integer.parseInt(componenteTextoFr[3].replaceAll("\\s+",""));
			int numMovimientos = Integer.parseInt(componenteTextoFr[5].replaceAll("\\s+",""));
			String 	  hormigas = componenteTextoFr[7].replaceFirst("\\s+","");
			String	 hormiga[] = hormigas.split(" ");
			
			int[][] tablero = new int[x][y];
			tablero = crearTablero(x, y);
			
			String xy = x+" "+y;
			//mirar el prorocolo de la hormiga para entender esto
			conectarHormigaGrabador(xy, hormigas, tablero, numMovimientos, hormiga.length);
			
		} catch (Exception e) {System.out.println(e.getMessage());}

	}//iniciarJuego()
	
	private static String leerFichero(String fichero) throws FileNotFoundException, IOException{
		File f = new File(fichero);
		FileReader fr = new FileReader(f);
		
		int intFr = fr.read();
		String textoFr = "";
		while (intFr != -1) {
			char charFr = (char) intFr;
			textoFr += charFr;
			intFr = fr.read();
		}
		fr.close();
		return textoFr;
	}
	
	private static void iniciarJuego() {
		//Dimensiones del tablero, me parece más cómodo definirlo desde aquí
		int x = 10;
		int y = 10;
		System.out.println("Dimensiones tablero: x: "+x+"  y: "+y);
		
		int[][] tablero = new int[x][y];
		tablero = crearTablero(x, y);
		
		System.out.print("Número de movimientos: ");
		int numMovimientos = scanner.nextInt();
		
		System.out.print("\nNúmero de hormigas: ");
		int numHormigas = scanner.nextInt();scanner.nextLine();
		String hormigas = crearHormigas(numHormigas);
		String xy = x+" "+y;
		//mirar el prorocolo de la hormiga para entender esto
		conectarHormigaGrabador(xy, hormigas, tablero, numMovimientos, numHormigas);
	}//iniciarJuego()
	
	private static void conectarHormigaGrabador(String xy, String hormigas, int[][] tablero, int numMovimientos, int numHormigas) {
		try {
			Process 	   pHormiga   = pHormiga();
			BufferedWriter bwHormiga  = new BufferedWriter(new OutputStreamWriter(pHormiga.getOutputStream()));
			BufferedReader brHormiga  = new BufferedReader(new InputStreamReader(pHormiga.getInputStream()));
			//pensaba enviarle todo en un bw.write y lo hice, aunque creo que asi es más legible y más útil,
			//en dos mensajes "x" e "y" por un lado y hormigas por otro
			bwHormiga.write(xy+" "+numMovimientos+"\n");bwHormiga.flush();
			bwHormiga.write(hormigas+"\n");bwHormiga.flush();
			
			Process 	   pGrabador  = pGrabador();
			BufferedWriter bwGrabador = new BufferedWriter(new OutputStreamWriter(pGrabador.getOutputStream()));
			//envio numMovimientos a grabdor para que se prepare para recibir
			bwGrabador.write(numMovimientos+"\n");bwGrabador.flush();

			for(int i = 0; i < numMovimientos; ++i) {
				//declaro las variables posteriores en este for porque las necesito en el método guardarTablero(), me serviran para identificar la hormiga como ¥
				String xPosicionTodasHormiga = "", yPosicionTodasHormiga = "";
				for (int j = 0; j < numHormigas; ++j) {
					//resultado = x;y;
					String resultado = brHormiga.readLine();
					//definimos "x" e "y", componentesResultado[] es el lo que nos envia la hormiga, la posición de cada una de ellas
					String[] componentesResultado = resultado.split(";");
					int x = Integer.parseInt(componentesResultado[1]);
					int y = Integer.parseInt(componentesResultado[0]);
					//enviamos el color en el que se encuentra la hormiga
					int colorTablero = tablero[x][y];
					bwHormiga.write(colorTablero+"\n");bwHormiga.flush();
					//la primera dimensión del array representa la "y" y el segundo array la "x" -> tablero[y][x]=1;
					tablero[x][y] = definirNumeroColor(colorTablero, hormigas, x, y);
					xPosicionTodasHormiga += x+",";
					yPosicionTodasHormiga += y+",";
				}//forHormigas
				int numMovimientoEnCurso = i+1;
				///System.out.println("Movimineto "+numMovimientoEnCurso);
				///mostrarTablero(tablero);
				///System.out.println();
				
				//tambien necesito el ancho del tablero x, para poder separar la linea de caracteres que le llegará a grabador, ya que no puedo enviar con "\n"
				guardarTablero(tablero, xy, xPosicionTodasHormiga, yPosicionTodasHormiga, numMovimientoEnCurso, bwGrabador);
			}//forMoviemtos
			
			bwGrabador.close();
			bwHormiga.close();brHormiga.close();

		} catch (Exception e) {System.out.println(e.getMessage());}
	}//conectarHormigaGrabador()
	
	private static Process pHormiga() throws IOException{
		ProcessBuilder pb = new ProcessBuilder("java","-cp",
				"C:\\Users\\tzvet\\eclipse-workspace\\Procesos\\bin",
				"PracticaHormigasLangton.HormigaPracticaLangton");
		Process p = pb.start();
		return p;
	}//pHormiga()

	private static Process pGrabador() throws IOException{
		ProcessBuilder pb = new ProcessBuilder("java","-cp",
				"C:\\Users\\tzvet\\eclipse-workspace\\Procesos\\bin",
				"PracticaHormigasLangton.GrabadorPracticaLangton");
		Process p = pb.start();
		return p;
	}//pGrabador()

	private static int definirNumeroColor(int colorTablero, String hormigas, int x, int y) {
		//extraemos la regla que debe seguir la hormiga
		String Hormigas[] = hormigas.split(" ");
		String componentesHormigas[] = Hormigas[0].split(";");
		String reglaHormigas[] = componentesHormigas[3].split(",");
		
		//según el color de la posición en la que se encuentre la hormiga y dependiendo del tamaño de la regla (que de eso dependerán el num de colores q hay)
		if (colorTablero<reglaHormigas.length-1)
			++colorTablero;
		else
			colorTablero = 0;
		
		return colorTablero;
	}//definirNumeroColor()
	
	private static void guardarTablero(int[][] tablero, String xyTablero, String xPosicionTodasHormiga, String yPosicionTodasHormiga, int numMovimientoEnCurso, BufferedWriter bw) throws IOException{
		//envia string al grabador.java que contiene toda la información del tablero,
		//se envia una vez por cada actualización del tablero
		//definimos ancho(x) del tablero
		String xy[] = xyTablero.split(" ");
		String x = xy[0];
		String y = xy[1];
		
		//introduces un array y devuleve un String con todo su contenido
		String tabla = "";
		for(int i = 0; i<tablero.length;++i) {
			for(int j = 0; j<tablero[0].length;++j) {
				tabla += tablero[i][j];
			}
		}
		
		bw.write(x+";"+y+";"+xPosicionTodasHormiga+";"+yPosicionTodasHormiga+";"+tabla+";"+numMovimientoEnCurso+"\n");bw.flush();
		
	}//guardarTablero()
	
	private static int[][] crearTablero(int x, int y) {
		//introduces int[][] y devuelve lleno de 0, no estoy seguro si java ya lo hace solo(creo que si), pero lo hago por si acaso, por compatibilidad con otros lenguajes
		int[][] tablero = new int[y][x];
		//el primer for crea el alto(y) del tablero y el segundo for el ancho(x) y inicializamos el tablero todo a 0, que 0 = " "
		for(int i = 0; i < tablero.length;++i)
			for(int j = 0; j < tablero[0].length;++j)
				tablero[i][j] = 0;
		return tablero;
	}//crearTablero()
	
	private static int[][] mostrarTablero(int[][] tablero){
		//muestra el tablero en el entorno de desarrollo (Eclipse)
		//mostrar tablero, la primera dimensión del array representa la "y" y el segundo array la "x" -> tablero[y][x]=1;
		for(int i = 0; i<tablero.length;++i) {
			for(int j = 0; j<tablero[0].length;++j) {
				System.out.print(tablero[i][j]);
			}
			System.out.println("");
		}
		return tablero;
	}//mostrarTablero()
	
	private static String crearHormigas(int numHormigas) {
		//este método devuelve x;y;orientacion(0,1,2,3);regla(r,l,l,r); por cada hormiga introducida
		//debemos establecer un control para no introducir otro tipo de datos
		Hormiga[] woHormiga = new Hormiga[numHormigas];
		for(int i = 0; i<numHormigas;++i) {
			System.out.println("\nHormiga "+(i+1)+":");
			System.out.println("Posición de la hormiga en el tablero:");
			System.out.print("x: ");
			int xHormiga = scanner.nextInt();
			System.out.print("y: ");
			int yHormiga = scanner.nextInt();
			System.out.println("Orientación: 0=arriba 1=derecha 2=abajo 3=izquierda");
			int orientacionHormiga = scanner.nextInt();scanner.nextLine();
			System.out.println("Regla: (pulsar enter = formato por defecto -> r,l,l,r)");
			String reglaHormiga = scanner.nextLine();
			if (reglaHormiga.equals(""))
				reglaHormiga = "r,l,l,r";
			woHormiga[i] = new Hormiga(xHormiga, yHormiga, orientacionHormiga, reglaHormiga); 
		}
		
		String resumenHormigas = "";
		for(int i = 0;i<woHormiga.length;++i) {
			resumenHormigas += woHormiga[i].toString();
		}
		return resumenHormigas;
	}//crearHormigas()
	
}//directorPracticaLangton

class Hormiga{
	//sinceramente podría hacerse el programa perfectamente sin la clase Hormiga, aunque me parece interesante tener la clase...
	//x,y son la posicion de la hormiga, orientacion 0=arriba 1=derecha 2=abajo 3=izquierda
	int x,y,orientacion;
	//ejemplo de la regla; r,l,l,r --> importante que esté separado por coma
	String regla;
	
	public Hormiga( int ix, int iy, int iorientacion, String iregla) {
		y           = iy;
		x           = ix;
		orientacion = iorientacion;
		regla       = iregla;
	}
	
	@Override
	public String toString() {
		//lo separamos con ";"
		return x+";"+y+";"+orientacion+";"+regla+"; ";
	}
}//Hormiga







