package algoritmos;

import java.util.HashSet;

import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;

/**
 * GeneralGreedy: algoritmo guloso proposto inicialmente 
 * por Kempe et. al. 2003 (extraido  de outro artigo)
 * @author Renato Melo
*/
public class GreedyLT implements SeedChooser<Actor> {
	private DirectedSocialNetwork grafo = null;

	public GreedyLT(DirectedSocialNetwork grafo) {
		super();
		this.grafo = grafo;
	}

	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<>();

		while (semente.size() < k) {
			Actor max = MaxSpread(semente);
			semente.add(max);
		}
		clearActive();
		return semente;
	}

	private Actor MaxSpread(HashSet<Actor> semente) {
		Actor max = null;
		double spread = 0;

		for (Actor v : grafo.vertexSet()) {
			if (!v.isActive()) {
				
				double media = cascata(v, semente);

				if (media > spread) {
					spread = media;
					max = v;					
				}
			}
		}
		System.out.println("spread: " + spread);
		return max;
	}

	private double cascata(Actor v, HashSet<Actor> semente) {
		HashSet<Actor> ativados = grafo.activate(semente);
		grafo.activate(v);
		ativados.add(v);
		
		return grafo.overageDiffusion(ativados, false);
	}

	public void clearActive() {
		for (Actor v : grafo.vertexSet()) {
			v.setActive(false);
		}
	}
	
	public static void main(String[] args) {
		DirectedSocialNetwork g;
		g = new  SocialNetworkGenerate().gerarGrafo(500, 2.8);
		long startTime = 0;
		
		startTime = System.nanoTime();
		HashSet<Actor> seed2 = new OriginalGreedy(g).escolher(15);
		System.out.println("OriginalGreedy = "+g.overageDiffusion(seed2, true)+", tempo: "+(System.nanoTime() - startTime)/1000);
		
		startTime =System.nanoTime();
		HashSet<Actor> seed1 = new GreedyIC(g).escolher(20);
		System.out.println("GreedyIC = "+g.overageDiffusion(seed1, true)+", tempo: "+(System.nanoTime() - startTime)/1000);
		startTime =System.nanoTime();
		
		startTime =System.nanoTime();
		HashSet<Actor> seed = new GreedyIC(g).escolher(20);
		System.out.println("GreedyLT = "+g.overageDiffusion(seed, false)+", tempo: "+(System.nanoTime() - startTime)/1000);
		
//		g.activate(seed);
//		g.visualize();
	}

}
