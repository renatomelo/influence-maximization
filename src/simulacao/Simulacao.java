package simulacao;

import java.util.HashSet;

import plot.MeuPlot;
import readgraph.GraphReader;
import algoritmos.ArticulationSeed;
import algoritmos.BestNeighbors;
import algoritmos.DominatingSeed;
import algoritmos.HightDegree;
import algoritmos.LazyGreedy;
import algoritmos.OriginalGreedy;
import algoritmos.RandomSeed;
import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;

public class Simulacao {

	public void simularIC(DirectedSocialNetwork g, String outDir) {
		double[] tamSemente = { 10, 20, 30, 40, 50 };
		double[] sigma1 = new double[5], tempo1 = new double[5];
		double[] sigma2 = new double[5], tempo2 = new double[5];
		double[] sigma3 = new double[5], tempo3 = new double[5];
		double[] sigma4 = new double[5], tempo4 = new double[5];
		int k;

		long excutionTime = 0;
		for (int i = 0; i < tamSemente.length; i++) {
			k = (int) tamSemente[i];
			System.out.println("\nTestando para k = " + k);

			excutionTime = System.currentTimeMillis() * -1;
			HashSet<Actor> seed1 = new DominatingSeed(g).escolher(k);
			excutionTime += System.currentTimeMillis();
			tempo1[i] = (excutionTime / 1000.0f);
			sigma1[i] = g.espectedSpread(seed1, true);
			System.out.println("DominatingSeed: sigma  = " + sigma1[i]
					+ ", tempo = " + tempo1[i] + " segundos");

			excutionTime = System.currentTimeMillis() * -1;
			HashSet<Actor> seed2 = new LazyGreedy(g).escolher(k);
			excutionTime += System.currentTimeMillis();
			tempo2[i] = (excutionTime / 1000.0f);
			sigma2[i] = g.espectedSpread(seed2, true);
			System.out.println("LazyGreedy: sigma = " + sigma2[i]
					+ ", tempo = " + tempo2[i] + " segundos");

			excutionTime = System.currentTimeMillis() * -1;
			HashSet<Actor> seed3 = new HightDegree(g).escolher(k);
			excutionTime += System.currentTimeMillis();
			tempo3[i] = (excutionTime / 1000.0f);
			sigma3[i] = g.espectedSpread(seed3, true);
			System.out.println("HighDegree: sigma = " + sigma3[i]
					+ ", tempo = " + tempo3[i] + " segundos");

			excutionTime = System.currentTimeMillis() * -1;
			HashSet<Actor> seed4 = new RandomSeed(g).escolher(k);
			excutionTime += System.currentTimeMillis();
			tempo4[i] = (excutionTime / 1000.0f);
			sigma4[i] = g.espectedSpread(seed4, true);
			System.out.println("RandomSeed: sigma = " + sigma4[i]
					+ ", tempo = " + tempo4[i] + " segundos");

		}

		MeuPlot meuplot = new MeuPlot();
		meuplot.plotPropagacao(tamSemente, sigma1, sigma2, sigma3, sigma4,
				outDir);
		meuplot.plotTempoExecucao(tamSemente, tempo1, tempo2, tempo3, tempo4,
				outDir);
	}

	public static void main(String[] args) {
//		simularArtificial();
		 simularHep();
//		 simularPhy();
		// simularEpinions();
	}

	private static void simularArtificial() {
		DirectedSocialNetwork g;
		g = new SocialNetworkGenerate().gerarGrafo(1000, 2.5);
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

		String outDir = "'plots/tex/";

		new Simulacao().simularIC(g, outDir);
		
	}

	private static void simularPhy() {
		DirectedSocialNetwork g;
		g = new GraphReader().readPhy();
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());
		
		String outDir = "'plots/phy/";

		new Simulacao().simularIC(g, outDir);
	}

	private static void simularEpinions() {
		DirectedSocialNetwork g;
		g = new GraphReader().readEpinions();
		
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());
		
		String outDir = "'plots/epinions/";

		new Simulacao().simularIC(g, outDir);
	}

	private static void simularHep() {
		DirectedSocialNetwork g;
		g = new GraphReader().readHep();
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());
		
		String outDir = "'plots/hep/";

		new Simulacao().simularIC(g, outDir);
	}

}
