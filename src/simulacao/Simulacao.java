package simulacao;

import java.util.HashSet;

import plot.MeuPlot;
import readgraph.GraphReader;
import algoritmos.BestNeighbors;
import algoritmos.DominatingSeed;
import algoritmos.HightDegree;
import algoritmos.OriginalGreedy;
import algoritmos.RandomSeed;
import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;

public class Simulacao {
	

	public void simularCascataIndependente(DirectedSocialNetwork g) {
		double[] tamSemente = {10, 20, 30, 40, 50};
		double[] rSeedMedia = new double[5], rSeedTempo = new double[5];
		double[] hDegreeMedia = new double[5], hDegreeTempo = new double[5]; 
		double[] bNeigMedia = new double[5], bNeigTempo = new double[5];
		double[] dSeedMedia = new double[5], dSeedTempo = new double[5];
		int k;
		
		long startTime = 0;		
		for (int i = 0; i < tamSemente.length; i++) {
			k = (int) tamSemente[i];
			System.out.println("Testando pra k = "+k);
			
			startTime = System.nanoTime();
			HashSet<Actor> hDegree = new HightDegree(g).escolher(k);
			hDegreeTempo[i] = (System.nanoTime() - startTime)/1000;
//			System.out.println("hDegreeTempo[i] = "+hDegreeTempo[i]);
			hDegreeMedia[i] = g.overageDiffusion(hDegree, true);
			
			startTime = System.nanoTime();
			HashSet<Actor> bNeig = new BestNeighbors(g).escolher(k);
			bNeigTempo[i] = (System.nanoTime() - startTime)/1000;
//			System.out.println("bNeigTempo[i] = "+bNeigTempo[i]);
			bNeigMedia[i] = g.overageDiffusion(bNeig, true);
			
			startTime = System.nanoTime();
			HashSet<Actor> rSeed = new RandomSeed(g).escolher(k);
			rSeedTempo[i] = (System.nanoTime() - startTime)/1000;
//			System.out.println("rSeedTempo[i] = "+rSeedTempo[i]);
			rSeedMedia[i] = g.overageDiffusion(rSeed, true);
			
			startTime = System.nanoTime();
			HashSet<Actor> dSeed = new DominatingSeed(g).escolher(k);
			dSeedTempo[i] = (System.nanoTime() - startTime)/1000;
//			System.out.println("dSeedTempo[i] = "+dSeedTempo[i]);
			dSeedMedia[i] = g.overageDiffusion(dSeed, true);
			
			/*startTime = System.nanoTime();
			HashSet<Actor> oSeed = new OriginalGreedy(g).escolher(k);
			dSeedTempo[i] = (System.nanoTime() - startTime)/1000;
//			System.out.println("dSeedTempo[i] = "+dSeedTempo[i]);
			dSeedMedia[i] = g.overageDiffusion(oSeed, true);*/
			
		}
		
		MeuPlot meuplot = new MeuPlot();
		meuplot.plotarPropagacaoEsperada(tamSemente, hDegreeMedia, bNeigMedia, rSeedMedia, dSeedMedia);
		meuplot.plotarTempoExecucao(tamSemente, hDegreeTempo, bNeigTempo, rSeedTempo, dSeedTempo);
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
			System.out.println("Testando pra k = "+k);
			
			startTime = System.nanoTime();
			HashSet<Actor> hDegree = new HightDegree(g).escolher(k);
			hDegreeTempo[i] = (System.nanoTime() - startTime)/1000;
			hDegreeMedia[i] = g.overageDiffusion(hDegree, false);
			
			startTime = System.nanoTime();
			HashSet<Actor> bNeig = new BestNeighbors(g).escolher(k);
			bNeigTempo[i] = (System.nanoTime() - startTime)/1000;
			bNeigMedia[i] = g.overageDiffusion(bNeig, false);
			
			startTime = System.nanoTime();
			HashSet<Actor> rSeed = new RandomSeed(g).escolher(k);
			rSeedTempo[i] = (System.nanoTime() - startTime)/1000;
			rSeedMedia[i] = g.overageDiffusion(rSeed, false);
			
			startTime = System.nanoTime();
			HashSet<Actor> dSeed = new DominatingSeed(g).escolher(k);
			dSeedTempo[i] = (System.nanoTime() - startTime)/1000;
			dSeedMedia[i] = g.overageDiffusion(dSeed, false);
			
		}
		
		MeuPlot meuplot = new MeuPlot();
		meuplot.plotarPropagacaoEsperadaLT(tamSemente, hDegreeMedia, bNeigMedia, rSeedMedia, dSeedMedia);
		meuplot.plotarTempoExecucaoLT(tamSemente, hDegreeTempo, bNeigTempo, rSeedTempo, dSeedTempo);
	}

	public static void main(String[] args) {
		DirectedSocialNetwork g;
//		g = new SocialNetworkGenerate().gerarGrafo(850, 2.8);
		g = new GraphReader().readEpinions();
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());
		
		Simulacao simula = new Simulacao();
//		simula.simularLimitanteLinear(g);
		simula.simularCascataIndependente(g);
		
	}

}
