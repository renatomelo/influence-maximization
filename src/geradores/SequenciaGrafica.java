/*
 * Algumas questões/observações:
 * 1 - A sequencia gerada tem que ser aleatória?
 * 2 - Senão, existirá apenas uma sequencia gráfica para cada valor de n se o beta for sempre o mesmo
 * 3 - Por que o valor da posição zero é sempre infinito? R. Por que não se divide por zero (na função zeta).
 * 4 - Se o vetor da sequencia for de inteiros, todos os valores são zero, menos o primeiro
 * 5 - É preciso eviitar a posição zero(isso não vai fazer diferença no grafo?)
 * 6 - O número de vértices resultantes é menor que n, deve ser por causa do arredondamento para baixo na formula e_alpha/i^beta
 * 7 - comparado com outros softwares qual a diferença?
 * 8 - Quanto menor o beta mais arestas e menos vertices
 * 9 - tentar substituir o alpha pela serie harmonica(se for convergente)
 * 10 - Com um beta entre 1 e 2 o processamento é mais lento
 * 11 - Com um beta =< 1.6 o zeta é zero e a sequencia não é criada
 * 12 - quanto maior o valor de beta, menor é o grau maximo na e muitos vertices de grau 1 na sequencia gerada
 * 13 - com beta < 7 todos os vertices tem grau 1 na sequencia resultante.
 * 14 - aproximadamente 1/3 dos vértices são isolados
 */

package geradores;

public class SequenciaGrafica {
	
	public SequenciaGrafica(){	} // Construtor
	
	public int[] gerarSequenciaPowerLaw(int n, double beta){
		
		int sequencia[] = new int[n];
		double zeta;
		double e_alpha = 0.0;
		
		if(beta < 0){
			System.out.println("O beta não pode ser menor que zero.");
		}
		else if(beta > 1){
			zeta = calcularZeta(beta);
			e_alpha = n/zeta; // e^alpla do modelo de chung-lu
		}
		else if(0 < beta && beta < 1){
			e_alpha = Math.pow((n - n * beta), beta);
		}
		else if(beta == 1){
			//Não é considerado por enquanto
		}

		sequencia[0] = 0;
		for (int i = 1; i < n; i++) 
			sequencia[i] = (int)(e_alpha / Math.pow(i, beta)) ;
		
		return sequencia;
	}
	
	double calcularZeta(double beta){
		double soma = 0;
		int a = 50000; // Precisão
		for (int i = 1; i <= a; i++) // Soma direta 'a' vezes
			soma += (double) 1/(Math.pow(i, beta));
		
		soma += Math.pow(a, -beta + 1)/(beta-1); // Integração - VERIFICAR SEM
		soma += Math.pow(a, -beta)/2;
		return soma;
	}

	private void mostraSequencia(int s[]) {
		System.out.println("Sequencia de Pesos");
		for (int i = 0; i < s.length; i++) {
			System.out.print(i + "\t");
		}
		System.out.println();
		for (int i = 0; i < s.length; i++) {
			System.out.print(s[i] + "\t");
		}
		System.out.println("\n");
	}
/*	
	public static void main(String[] args) {
		Sequencia s = new Sequencia();
		int resultado[] = s.gerarSequencia(100, 2.5);
		for (int i = 1; i < resultado.length; i++) {
			System.out.print(" "+resultado[i]+",");
		}
		System.out.println();
	}
*/
}