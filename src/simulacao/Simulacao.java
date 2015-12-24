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
	

	public void simularCascataIndependente(DirectedSocialNetwork g) {
		double[] tamSemente = {10, 20, 30, 40, 50};
		double[] sigma1 = new double[5], tempo1 = new double[5];
		double[] sigma2 = new double[5], tempo2 = new double[5]; 
		double[] sigma3 = new double[5], tempo3 = new double[5];
		double[] sigma4 = new double[5], tempo4 = new double[5];
		double[] sigma5 = new double[5], tempo5 = new double[5];
		int k;
		
		long startTime = 0;		
		for (int i = 0; i < tamSemente.length; i++) {
			k = (int) tamSemente[i];
			System.out.println("\nTestando para k = "+k);
			
			startTime = System.nanoTime();
			HashSet<Actor> seed1 = new HightDegree(g).escolher(k);
			tempo1[i] = (System.nanoTime() - startTime)/1000;
//			System.out.println("hDegreeTempo[i] = "+hDegreeTempo[i]);
			sigma1[i] = g.espectedSpread(seed1, true);
			System.out.println("sigma HighDegree = "+sigma1[i]);
			
			startTime = System.nanoTime();
			HashSet<Actor> seed2 = new BestNeighbors(g).escolher(k);
			tempo2[i] = (System.nanoTime() - startTime)/1000;
//			System.out.println("bNeigTempo[i] = "+bNeigTempo[i]);
			sigma2[i] = g.espectedSpread(seed2, true);
			System.out.println("sigma BestNeighbors = "+sigma2[i]);
			
			startTime = System.nanoTime();
			HashSet<Actor> seed3 = new RandomSeed(g).escolher(k);
			tempo3[i] = (System.nanoTime() - startTime)/1000;
//			System.out.println("rSeedTempo[i] = "+rSeedTempo[i]);
			sigma3[i] = g.espectedSpread(seed3, true);
			System.out.println("sigma RandomSeed = "+sigma3[i]);
			
//			startTime = System.nanoTime();
//			HashSet<Actor> dSeed = new DominatingSeed(g).escolher(k); // Provisóriamente====================
//			dSeedTempo[i] = (System.nanoTime() - startTime)/1000;
////			System.out.println("dSeedTempo[i] = "+dSeedTempo[i]);
//			dSeedMedia[i] = g.espectedSpread(dSeed, true);
//			System.out.println("sigma DominatingSeed = "+dSeedMedia[i]);
			
			/*startTime = System.nanoTime();
			HashSet<Actor> oSeed = new OriginalGreedy(g).escolher(k);
			dSeedTempo[i] = (System.nanoTime() - startTime)/1000;
//			System.out.println("dSeedTempo[i] = "+dSeedTempo[i]);
			dSeedMedia[i] = g.overageDiffusion(oSeed, true);*/
			
			startTime = System.nanoTime();
			HashSet<Actor> seed4 = new DominatingSeed(g).escolher(k);
			tempo4[i] = (System.nanoTime() - startTime)/1000;
//			System.out.println("dSeedTempo[i] = "+dSeedTempo[i]);
			sigma4[i] = g.espectedSpread(seed4, true);
			System.out.println("sigma DominatingSeed = "+sigma4[i]);
			
			startTime = System.nanoTime();
			HashSet<Actor> celf = new LazyGreedy(g).escolher(k);
			tempo5[i] = (System.nanoTime() - startTime)/1000;
//			System.out.println("dSeedTempo[i] = "+dSeedTempo[i]);
			sigma5[i] = g.espectedSpread(celf, true);
			System.out.println("sigma LazyGreedy = "+sigma5[i]);
			
			
		}
		
		MeuPlot meuplot = new MeuPlot();
		meuplot.plotarPropagacaoEsperada(tamSemente, sigma1, sigma2, sigma3, sigma4, sigma5);
		meuplot.plotarTempoExecucao(tamSemente, tempo1, tempo2, tempo3, tempo4, tempo5);
	}

	public void simularLimitanteLinear(DirectedSocialNetwork g) {
		double[] tamSemente = { 10, 20, 30, 40, 50 };
		double[] rSeedMedia = new double[5], rSeedTempo = new double[5];
		double[] hDegreeMedia = new double[5], hDegreeTempo = new double[5]; 
		double[] bNeigMedia = new double[5], bNeigTempo = new double[5];
		double[] dSeedMedia = new double[5], dSeedTempo = new double[5];
		int k;
		
		long startTime = 0;		
		for (int i = 0; i < tamSemente.length; i++) {
			k = (int) tamSemente[i];
			System.out.println("\nTestando pra k = "+k);
			
			startTime = System.nanoTime();
			HashSet<Actor> hDegree = new HightDegree(g).escolher(k);
			hDegreeTempo[i] = (System.nanoTime() - startTime)/1000;
			hDegreeMedia[i] = g.espectedSpread(hDegree, false);
			
			startTime = System.nanoTime();
			HashSet<Actor> bNeig = new BestNeighbors(g).escolher(k);
			bNeigTempo[i] = (System.nanoTime() - startTime)/1000;
			bNeigMedia[i] = g.espectedSpread(bNeig, false);
			
			startTime = System.nanoTime();
			HashSet<Actor> rSeed = new RandomSeed(g).escolher(k);
			rSeedTempo[i] = (System.nanoTime() - startTime)/1000;
			rSeedMedia[i] = g.espectedSpread(rSeed, false);
			
			startTime = System.nanoTime();
			HashSet<Actor> dSeed = new DominatingSeed(g).escolher(k);
			dSeedTempo[i] = (System.nanoTime() - startTime)/1000;
			dSeedMedia[i] = g.espectedSpread(dSeed, false);
			
		}
		
		MeuPlot meuplot = new MeuPlot();
		meuplot.plotarPropagacaoEsperadaLT(tamSemente, hDegreeMedia, bNeigMedia, rSeedMedia, dSeedMedia);
		meuplot.plotarTempoExecucaoLT(tamSemente, hDegreeTempo, bNeigTempo, rSeedTempo, dSeedTempo);
	}

	public static void main(String[] args) {
		DirectedSocialNetwork g;
//		g = new SocialNetworkGenerate().gerarGrafo(2500, 2.8);
//		g = new GraphReader().readHep();
		g = new GraphReader().readPhy();
//		g = new GraphReader().readEpinions();
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());
		
		Simulacao simula = new Simulacao();
//		simula.simularLimitanteLinear(g);
		simula.simularCascataIndependente(g);
		
	}

}
