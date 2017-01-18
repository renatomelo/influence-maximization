package simulacao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

//import javax.media.j3d.PhysicalBody;

import org.jgrapht.graph.DefaultWeightedEdge;

import plot.MeuPlot;
import readgraph.GraphReader;
import algoritmos.ArticulationSeed;
import algoritmos.BestNeighbors;
import algoritmos.PrevalentSeed;
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
			HashSet<Actor> seed1 = new PrevalentSeed(g).escolher(k);
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
//		meuplot.plotPropagacao(tamSemente, sigma1, sigma2, sigma3, sigma4,outDir);
		meuplot.plotTempoExecucao(tamSemente, tempo1, tempo2, tempo3, tempo4,
				outDir);
	}

	public static void simularRandomGraphIC(int n, String outDir) {
		double[] tamSemente = { 10, 20, 30, 40, 50 };
		double[] sigma1 = new double[5], tempo1 = new double[5];
		double[] sigma2 = new double[5], tempo2 = new double[5];
		double[] sigma3 = new double[5], tempo3 = new double[5];
		double[] sigma4 = new double[5], tempo4 = new double[5];
		int k, repeticoes = 1;

		double[] ssigma1 = new double[5], stempo1 = new double[5];
		double[] ssigma2 = new double[5], stempo2 = new double[5];
		double[] ssigma3 = new double[5], stempo3 = new double[5];
		double[] ssigma4 = new double[5], stempo4 = new double[5];
		
		for (int j = 0; j < repeticoes; j++) {
			System.out.println("Grafo "+j);
			DirectedSocialNetwork g;
			g = new SocialNetworkGenerate().gerarGrafo(n * 1000, 2.5);
			System.out.println("|V(G)| = " + g.vertexSet().size());
			System.out.println("|E(G)| = " + g.edgeSet().size());
			System.out.println();

			long excutionTime = 0;
			for (int i = 0; i < tamSemente.length; i++) {
				k = (int) tamSemente[i];
				System.out.println("\nTestando para k = " + k);

				excutionTime = System.currentTimeMillis() * -1;
				HashSet<Actor> seed1 = new PrevalentSeed(g).escolher(k);
				excutionTime += System.currentTimeMillis();
				tempo1[i] = (excutionTime / 1000.0f);
				sigma1[i] = g.espectedSpread(seed1, true);
				System.out.println("PrevalentSeed: sigma  = " + sigma1[i]
						+ ", tempo = " + tempo1[i] + " segundos");
				ssigma1[i] += sigma1[i];
				stempo1[i] += tempo1[i];

				excutionTime = System.currentTimeMillis() * -1;
				HashSet<Actor> seed2 = new LazyGreedy(g).escolher(k);
				excutionTime += System.currentTimeMillis();
				tempo2[i] = (excutionTime / 1000.0f);
				sigma2[i] = g.espectedSpread(seed2, true);
				System.out.println("LazyGreedy: sigma = " + sigma2[i]
						+ ", tempo = " + tempo2[i] + " segundos");
				ssigma2[i] += sigma2[i];
				stempo2[i] += tempo2[i];
				
//				excutionTime = System.currentTimeMillis() * -1;
//				HashSet<Actor> seed2 = new PrevalentSeed(g).escolher2(k);
//				excutionTime += System.currentTimeMillis();
//				tempo2[i] = (excutionTime / 1000.0f);
//				sigma2[i] = g.espectedSpread(seed2, true);
//				System.out.println("preSelecaoOriginal: sigma = " + sigma2[i]
//						+ ", tempo = " + tempo2[i] + " segundos");
//				ssigma2[i] += sigma2[i];
//				stempo2[i] += tempo2[i];

				excutionTime = System.currentTimeMillis() * -1;
				HashSet<Actor> seed3 = new HightDegree(g).escolher(k);
				excutionTime += System.currentTimeMillis();
				tempo3[i] = (excutionTime / 1000.0f);
				sigma3[i] = g.espectedSpread(seed3, true);
				System.out.println("HighDegree: sigma = " + sigma3[i]
						+ ", tempo = " + tempo3[i] + " segundos");
				ssigma3[i] += sigma3[i];
				stempo3[i] += tempo3[i];

				excutionTime = System.currentTimeMillis() * -1;
				HashSet<Actor> seed4 = new RandomSeed(g).escolher(k);
				excutionTime += System.currentTimeMillis();
				tempo4[i] = (excutionTime / 1000.0f);
				sigma4[i] = g.espectedSpread(seed4, true);
				System.out.println("RandomSeed: sigma = " + sigma4[i]
						+ ", tempo = " + tempo4[i] + " segundos");
				ssigma4[i] += sigma4[i];
				stempo4[i] += tempo4[i];

			}
		}
		// TODO tirar a média das 10 repetiçoes e plotar a media
		for (int i = 0; i < tamSemente.length; i++) {
			sigma1[i] = ssigma1[i] / repeticoes;
			sigma2[i] = ssigma2[i] / repeticoes;
			sigma3[i] = ssigma3[i] / repeticoes;
			sigma4[i] = ssigma4[i] / repeticoes;

			tempo1[i] = stempo1[i] / repeticoes;
			tempo2[i] = stempo2[i] / repeticoes;
			tempo3[i] = stempo3[i] / repeticoes;
			tempo4[i] = stempo4[i] / repeticoes;
		}

		MeuPlot meuplot = new MeuPlot();
//		meuplot.plotPropagacao(tamSemente, sigma1, sigma2, sigma3, sigma4,outDir);
		meuplot.plotTempoExecucao(tamSemente, tempo1, tempo2, tempo3, tempo4,
				outDir);
	}

	private static void simularPhy() {
		DirectedSocialNetwork g;
		g = new GraphReader().readPhy();
		System.out.println("Phy.txt");
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

		String outDir = "'plots/phy/";

		new Simulacao().simularIC(g, outDir);
	}

	private static void simularEpinions() {
		System.out.println("Epinions.txt");
		DirectedSocialNetwork g;
		g = new GraphReader().readEpinions();

		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

		String outDir = "'plots/epinions/";

		new Simulacao().simularIC(g, outDir);
	}

	private static void simularHep() {
		System.out.println("Hep.txt");
		DirectedSocialNetwork g;
		g = new GraphReader().readHep();
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

		String outDir = "'plots/hep/";

		new Simulacao().simularIC(g, outDir);
	}

	private static void simularDblp() {
		System.out.println("dblp.txt");
		DirectedSocialNetwork g;
		g = new GraphReader().readDblp();
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

		String outDir = "'plots/dblp/";

		new Simulacao().simularIC(g, outDir);

	}

	private static void simularArtificial() {
		int i = 2;
		
		while (i <= 64) {
			String outDir = "'plots/rand/"+i+"/";
			simularRandomGraphIC(i, outDir);
			System.out.println("\t----------");
			i = i * 2;
		}
	}

	public static void main(String[] args) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date data = new Date();
		System.out.println("Data de início: " + dateFormat.format(data));
		simularArtificial();
//		 simularPhy();
//		 simularEpinions();
//		 simularHep();
//		 simularDblp();
	}

}
