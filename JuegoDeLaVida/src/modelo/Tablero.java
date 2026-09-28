package modelo;

public class Tablero { 
	/* Establece la matriz de Celda, itera sobre la matriz, 
	 * cuenta vecinos 
	 * y ejerce la evolución (el paso a la siguiente generación
	*/
	private int filas, columnas;
	private Celda[][] celdas;; //matriz de Celda
	
	
	//CONSTRUSTOR: crear tablero con parametro de filas y comlumnas
	public Tablero (int filas, int columnas) {
		this.filas=filas;
		this.columnas =columnas;
		//Crea la matriz/tablero
			this.celdas = new Celda[filas][columnas];
	}

	//GETTERS
	public int getFilas() {
		return filas;
	}
	public int getColumnas() {
		return columnas;
	}
	
	//Buscar celda específica
	public Celda getCelda(int fila, int columna) {
		return celdas[fila][columna];
	}
	
	//SETTER
	//Cambiar Celda específica por otra, 
	//para inicializar al cargar un acrchivo
	public void setCelda(int fila, int columna, Celda celda) {
		this.celdas[fila][columna] = celda;
	}
	
	
	/*FUNCIONAMINETO DEL JUEGO DE LA VIDA
	 * 1. boolean avanzarGeneracion()
	 * 		+ contar vecinos vivos --> int contarVecinosVivos()
	 * 		+ chequear si hubieron cambios
	 * 		+ devolver boolean para el Vista, para saber si continuar o no iterando las generaciones
	 * 2. int contarVecinosVivos()
	 * 		+ chequearque se recorre el tablero --> isPosValida(fila, col)
	 * 3. void mostrar()
	*/
	
	//Recorro ma matriz y muersto tablero
	public void mostrar () {
		for(int i=0; i<getFilas(); i++){
			for(int j=0; j<getColumnas(); j++) {
				System.out.print(this.getCelda(i,j).mostrar()+" ");
			}
			System.out.println();//Imprime por fila, salto de linea
		}
	}
	
	//Recorro matriz y cuentp vecinos vivos alrededor por celda
	private int contarVecinosVivos(int fila, int col) {
		int vivos=0;
		//Recorro la matriz en 3x3 alrededor de la Celda
		for(int i=-1; i<=1; i++) {
			for (int j = -1; j<= 1; j++) {
				if (!(i==0 && j==0)) { //no se cuenta a la Celda central
					int nf = fila+i;
					int nc = col+j;
					if(isPosValida(nf, nc)) {
						if(getCelda(nf,nc).isViva()) {
							vivos++;
						}
					}
				}
			}
		}
		return vivos;
	}

	private boolean isPosValida(int nf, int nc) {
		// TODO Auto-generated method stub
		return nf>=0 && nf<getFilas() && nc>=0 && nc<getColumnas();
	}
	
	public boolean avanzarGeneracion() {
		boolean huboCambios = false;
		//Guardo los cambios para ver si los hay
		Estado[][] nEstados = new Estado [getFilas()][getColumnas()];
		//Calcular el siguiente estado de las celdas, tomando en cuenta el actual.
		for(int i=0; i<getFilas(); i++){
			for(int j=0; j<getColumnas(); j++) {
				int vecinosVivos = contarVecinosVivos(i, j);
				//Establesco el estado siguiente en cada celda
				celdas[i][j].calcularSig(vecinosVivos);
				Estado nEstado= celdas[i][j].getEstadoSig();
				nEstados[i][j]=nEstado;
				if(!(celdas[i][j].getEstadoActual().equals(nEstados[i][j]))){
					huboCambios=true;
				}
			}
		}
		
		
		for(int i=0; i<getFilas(); i++){
			for(int j=0; j<getColumnas(); j++) {
				celdas[i][j].evolucionar();
			}
		}
		return huboCambios;
	}
	
	
	
	

	
}
