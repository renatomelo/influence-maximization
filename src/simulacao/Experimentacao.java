package simulacao;

import algoritmos.HightDegree;
import algoritmos.LazyGreedy;
import algoritmos.PrevalentSeed;
import algoritmos.RandomSeed;
import geradores.SocialNetworkGenerate;
import grafos.DirectedSocialNetwork;
import plot.MeuPlot;
import readgraph.GraphReader;

public class Experimentacao {

	public static void main(String[] args) {
		int k = 30;
		sinteticGraphSimulate(k);		
		//Simulações a serem feita com probabilidade 0.01
		//System.out.println("Simulaçoes realizadas para p = 0.025");
//		System.out.println("Grafos: HEP, PHY e DBLP");
		//System.out.println("Grafos: AMAZON");
//		simularHep(k);
		//simularPhy(k);
		//simularAmazon(k);
		
 		//Simulações a serem feita com probabilidade 0.0025
		//System.out.println("Simulaçoes realizadas para p = 0.0025");
		//System.out.println("Grafos: ENRON e EPINIONS");
		//System.out.println("Grafos: Epinions");
		//simularEnron(k);
		//simularEpinions(k);
		//simularDblp(k); 
	}
	
	private static void simularHep(int k) {
		System.out.println("Hep.txt");
		DirectedSocialNetwork g;
		g = new GraphReader().readHep();
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

		String out = "plots/hep/";

		monteCarloSimulation(g, out, k);
	}
	private static void simularPhy(int k) {
		DirectedSocialNetwork g;
		g = new GraphReader().readPhy();
		System.out.println("Phy.txt");
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

		String out = "plots/phy/";

		monteCarloSimulation(g, out, k);
	}
	private static void simularDblp(int k) {
		System.out.println("dblp.txt");
		DirectedSocialNetwork g;
		g = new GraphReader().readDblp();
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

		String out = "plots/dblp/";

		monteCarloSimulation(g, out, k);
	}
	
	private static void simularEpinions(int k) {
		System.out.println("Epinions.txt");
		DirectedSocialNetwork g;
		g = new GraphReader().readEpinions();

		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

		String out = "plots/epinions/";

		monteCarloSimulation(g, out, k);
	}
	
	private static void simularAmazon(int k) {
		System.out.println("Amazon.txt");
		DirectedSocialNetwork g;
		g = new GraphReader().amazon();

		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

		String out = "plots/amazon/";

		monteCarloSimulation(g, out, k);
	}
	
	private static void simularEnron(int k) {
		System.out.println("enron.txt");
		DirectedSocialNetwork g;
		g = new GraphReader().enron();

		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

		String out = "plots/enron/";

		monteCarloSimulation(g, out, k);
	}

	private static void monteCarloSimulation(DirectedSocialNetwork g, String out, int k) {
		double[] spreadPS,spreadCelf, spreadHD, spreadRS;
		double timePS, timeCelf, timeHD, timeRS;
		double[] x;
		long time = 0;

//		Testa o celf
		LazyGreedy celf = new LazyGreedy(g);
		System.out.println("#Celf");
		
		time = System.currentTimeMillis() * -1;
		celf.escolher(k);
		time += System.currentTimeMillis();
		
		timeCelf = (time / 1000.0f);
		System.out.println("Tempo: "+timeCelf);
		spreadCelf = celf.getSpreadData();

		
//		Testa o PrevalentSeed
		PrevalentSeed ps = new PrevalentSeed(g);
		System.out.println("#PrevalentSeed");
		
		time = System.currentTimeMillis() * -1;
		ps.escolher(k);
		time += System.currentTimeMillis();
		
		timePS = (time / 1000.0f);
		System.out.println("Tempo: "+timePS);
		spreadPS = ps.getSpreadData();

//		// Testa a heuristica de grau
//		HightDegree hd = new HightDegree(g);
//		System.out.println("#HightDegree");
//		hd.escolher(k);
//		spreadHD = hd.getSpreadData();
//
//		// Testa a heurística semente aleatória
//		RandomSeed rs = new RandomSeed(g);
//		System.out.println("#RandomSeed");
//		rs.escolher(k);
//		spreadRS = rs.getSpreadData();

		x = eixox(k + 1);
		MeuPlot meuplot = new MeuPlot();
		meuplot.plotPropagacao(x, spreadPS, spreadCelf, out);
	}

	private static void sinteticGraphSimulate(int k) {
		int n = 2;

		while (n <= 16) {
			String out = "plots/rand/" + n + "/";
			sinteticGraph(n, out, k);
			System.out.println("\t----------");
			n = n * 2;
		}
	}

	/**
	 * Faz um conjunto de simulações para grafos aleatórios de tamanho n e gera
	 * graficos com os resultados da simulação
	 * 
	 * @param n
	 *            multiplo de 1000 para o tamanho dos grafos gerados
	 * @param out
	 *            arquivo de saída para o grafico plotado
	 * @param k
	 *            tamanho do conjunto semente
	 */
	private static void sinteticGraph(int n, String out, int k) {
		double[] spreadPS = new double[k + 1];
		double[] spreadCelf = new double[k + 1];
		double[] spreadHD = new double[k + 1];
		double[] spreadRS = new double[k + 1];
		double[] mediaCelf = new double[k + 1], mediaPS = new double[k + 1], mediaHD = new double[k + 1],
				mediaRS = new double[k + 1];
		double timePS = 0, timeCelf = 0, timeHD = 0, timeRS = 0, time;
		double[] x;
		int numSimulacoes = 4;

		for (int i = 0; i < numSimulacoes; i++) {
			DirectedSocialNetwork g = new SocialNetworkGenerate().gerarGrafo(n * 1000, 2);
			System.out.println("\nGrafo n. " + (i + 1));
			mostrarTamanho(g);

			LazyGreedy celf = new LazyGreedy(g);
			System.out.println("#Celf");
			time = System.currentTimeMillis() * -1;
			celf.escolher(k);
			time += System.currentTimeMillis();
			spreadCelf = somaVetores(spreadCelf, celf.getSpreadData());
			timeCelf = timeCelf + (time / 1000.0f);

			PrevalentSeed ps = new PrevalentSeed(g);
			System.out.println("#PrevalentSeed");
			time = System.currentTimeMillis() * -1;
			ps.escolher(k);
			time += System.currentTimeMillis();
			spreadPS = somaVetores(spreadPS, ps.getSpreadData());
			timePS = timePS + (time / 1000.0f);

			// Testa a heuristica de grau
			HightDegree hd = new HightDegree(g);
			System.out.println("#HightDegree");
			time = System.currentTimeMillis() * -1;
			hd.escolher(k);
			time += System.currentTimeMillis();
			spreadHD = somaVetores(spreadHD, hd.getSpreadData());
			timeHD = timeHD + (time / 1000.0f);
			
			// Testa a heurística semente aleatória
			RandomSeed rs = new RandomSeed(g);
			System.out.println("#RandomSeed");
			time = System.currentTimeMillis() * -1;
			rs.escolher(k);
			time += System.currentTimeMillis();
			spreadRS = somaVetores(spreadRS, rs.getSpreadData());
			timeRS = timeRS + (time / 1000.0f);
		}
		
		System.out.println("Tempo Celf: "+timeCelf/numSimulacoes);
		System.out.println("Tempo PrevalentSeed: "+timePS/numSimulacoes);
		System.out.println("Tempo HighDegree: "+timeHD/numSimulacoes);
		System.out.println("Tempo RandomSeed: "+timeRS/numSimulacoes);

		// tirar a média das 20 simulações e plotar a media
		for (int i = 0; i < spreadPS.length; i++) {
			mediaPS[i] = spreadPS[i] / numSimulacoes;
			mediaCelf[i] = spreadCelf[i] / numSimulacoes;
			mediaHD[i] = spreadHD[i] / numSimulacoes;
			mediaRS[i] = spreadRS[i] / numSimulacoes;
		}

		x = eixox(k + 1); // preenche o vetor do eixo x
		MeuPlot meuplot = new MeuPlot();
		meuplot.plotPropagacao(x, mediaPS, mediaCelf, mediaHD, mediaRS, out);
//		meuplot.plotPropagacao(x, mediaPS, mediaCelf, out);
		// meuplot.plotChamadas(sigmaCalls, sigmaCalls2, "'");

	}

	private static double[] somaVetores(double[] a, double[] b) {
		double[] soma = new double[a.length];
		for (int i = 0; i < soma.length; i++) {
			soma[i] = a[i] + b[i];
		}
		return soma;
	}

	/**
	 * Preenche o vetor para o eixo x do grafico a ser plotado no intervalo
	 * [0..50]
	 * 
	 * @param l:
	 *            tamanho do vetor
	 * @return o vetor preenchido
	 */
	private static double[] eixox(int l) {
		double[] x = new double[l];
		for (int j = 0; j < x.length; j++) {
			x[j] = j;
		}
		return x;
	}

	private static void mostrarTamanho(DirectedSocialNetwork g) {
		System.out.println("Grafo direcionado: |V| = " + g.vertexSet().size() + " |E| = " + g.edgeSet().size());
	}

}
