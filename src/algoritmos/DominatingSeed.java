package algoritmos;

import java.util.HashSet;

import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;

public class DominatingSeed implements SeedChooser<Actor> {
	private DirectedSocialNetwork grafo;
	public DominatingSeed(DirectedSocialNetwork g) {
		this.grafo = g;
	}

	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<Actor>();
		HashSet<Actor> minSet = new HashSet<Actor>();
		
		MinDominatingSet ds =  new MinDominatingSet();
		minSet = ds.fastGreedy(grafo);
		
		semente.addAll(grafo.verticesGrauMaior(minSet, k));
		
		return semente;
	}
	
	public static void main(String[] args) {
		DirectedSocialNetwork g = new SocialNetworkGenerate()
				.gerarGrafo(100, 2);
		HashSet<Actor> seed = new DominatingSeed(g).escolher(10);
		g.activate(seed);
		// HashSet<Actor> ativos = g.indepCascadeDiffusion(seed);
		// HashSet<Actor> ativos = g.linearThresholdDiffusion(seed);
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|S| = " + seed.size());
		// System.out.println("|A| = "+ativos.size());
		g.visualize();
	}

}
