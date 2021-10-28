package PracticaHormigasLangton;

import java.util.Scanner;

/* 19/10/2021 @author Tzvetan Petiov Hristov
 * 
 * Hormiga. La hormiga es consciente de su posición y tiene programado su comportamiento de acuerdo a determinadas reglas.*/

public class HormigaPracticaLangton {
	private static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		iniciarHormigas();
	}//main()
	
	
	
	private static void iniciarHormigas() {
		//Protocolo (primero enviamos xyNumMovimientos y luego las hormigas): ejemplo -> "xAnchoTablero yAltoTablero 10" "3;3;0;r,l,l,r; 4;4;1;r,l,l,r;"
		String xyTableroNumMovimientos = scanner.nextLine();
		String hormigas = scanner.nextLine();
		
		String componentesProtocolo[] = xyTableroNumMovimientos.split(" ");
		//componentes 0 y 1 del protocolo son el ancho y el alto del tablero, componente 2 son el numMovimientos que hay
		int xTablero 		= Integer.parseInt(componentesProtocolo[0]);
		int yTablero 		= Integer.parseInt(componentesProtocolo[1]);
		int numMovimientos 	= Integer.parseInt(componentesProtocolo[2]);
		
		//creamos un array hormiga[], en el que se almacenan las hormigas por separado
		String hormiga[] = hormigas.split(" ");
		//fos se ejecuta hasta que no haya más movimientos, es decir termina a la par con el for de director, ocurre lo mismo con grabador
		for(int j = 0;j<numMovimientos;++j) {
			//este for se ejecutará según el numHormigas que haya
			for(int i = 0; i < hormiga.length; ++i) {
				String componentesHormiga[] = hormiga[i].split(";");
				int x 			= Integer.parseInt(componentesHormiga[0]);
				int y 			= Integer.parseInt(componentesHormiga[1]);
				int orientacion = Integer.parseInt(componentesHormiga[2]);
				//regla[] contiene los movimientos de la hormiga por separado, según el color en el que se encuentre la hormiga será una cosa u otra
				String regla[]  = componentesHormiga[3].split(",");
				
				//comprobarTablaToro() comprueba y controla que la hormiga no se salga del tablero, llevandola al otro lado de este, mirar método
				int[] xyComprobado = comprobarTablaToro(x, y, xTablero, yTablero);
				//nos devuelve "x" e "y" comprobado
				x = xyComprobado[0];
				y = xyComprobado[1];
				
				//este mensaje lo lee el director, no le envio la orientación ni la regla ya que no las necesita
				System.out.println(x+";"+y+";");
				
				//numColorHormiga es el color en el que se encuentra la hormiga, lo recibe desde director
				int numColorHormiga = scanner.nextInt();
				//rightLeft es la variable que determina si gira a la izq "l->left" o a la dcha "r -> right", mira definición regla[] para entender mejor
				String rightLeft = regla[numColorHormiga];
				hormiga[i] = moverHormiga(x, y, orientacion, componentesHormiga[3], rightLeft);
			}//forHormigas
		}//forMoviemtos
		
	}//iniciarHormigas()
	
	private static int[] comprobarTablaToro(int x, int y, int xTablero, int yTablero) {
		//este if comprueba las posiciones metidas por parámetros y en el caso de ser necesario modifica la posición
		if 		(x>xTablero-1) x=0;
		else if (x<0) x=xTablero-1;
		else if (y>yTablero-1) y=0;
		else if (y<0) y=yTablero-1;
		
		//devuelvo un array en el que 0 es "x" e "y" es 1
		int[] xyComprobado = new int[2];
		xyComprobado[0]=x;
		xyComprobado[1]=y;
		
		return xyComprobado;
	}//comprobarTablaToro()
	
	private static String moverHormiga(int x, int y, int orientacion, String regla, String rightLeft) {
		//cambiaremos la posición de la hormiga según la orientación y la regla que tenga 
		//orientación -> 0=arriba 1=derecha 2=abajo 3=izquierda
		boolean mensajeCorrecto = true;
		if 		 (orientacion == 0 && rightLeft.equals("r") || orientacion == 2 && rightLeft.equals("l")) {
			++x;
			orientacion = 1;
		}else if (orientacion == 0 && rightLeft.equals("l") || orientacion == 2 && rightLeft.equals("r")) {
			--x;
			orientacion = 3;
		}else if (orientacion == 1 && rightLeft.equals("r") || orientacion == 3 && rightLeft.equals("l")) {
			++y;
			orientacion = 2;
		}else if (orientacion == 1 && rightLeft.equals("l") || orientacion == 3 && rightLeft.equals("r")) {
			--y;
			orientacion = 0;
		}else mensajeCorrecto = false;
		
		if(mensajeCorrecto) return x+";"+y+";"+orientacion+";"+regla+";";
		else 				return "error moverHormiga()";
	}//moverHormiga()
	
}//HormigaPracticaLangton


















