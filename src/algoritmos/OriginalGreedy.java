package algoritmos;

import java.util.HashSet;

import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;

public class OriginalGreedy implements SeedChooser<Actor> {
	private DirectedSocialNetwork grafo = null;

	public OriginalGreedy(DirectedSocialNetwork grafo) {
		super();
		this.grafo = grafo;
	}

	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<>();

		while (semente.size() < k) {
			Actor max = MaxIncreaseSpread(semente);
			semente.add(max);
		}
		clearActive();
		return semente;
	}

	private Actor MaxIncreaseSpread(HashSet<Actor> semente) {
		Actor max = null;
		int repeticoes = 100;
		double spread = 0;

		for (Actor v : grafo.vertexSet()) {
			if (!v.isActive()) {
				int soma = 0;

				for (int i = 0; i < repeticoes; i++) {

					clearTempActive();

					soma += InfluenceSpreadTrial(v, semente);
				}
				double media = soma / repeticoes;

				if (media > spread) {
					spread = media;
					max = v;					
				}
			}
		}
		System.out.println("spread: " + spread);
		return max;
	}

	private long InfluenceSpreadTrial(Actor v, HashSet<Actor> semente) {
		for (Actor w : semente) {
			SpreadTrial(w);
		}
		SpreadTrial(v);

		// Statistics
		int ativados = 0;
		for (Actor w : grafo.vertexSet()) {
			if (w.isTempActive() && !w.isActive()) {
				ativados++;
			}
		}
		return ativados;
	}

	private void SpreadTrial(Actor w) {
		for (Actor v : grafo.outNeighborsOf(w)) {
			double r = Math.random();
			
			if (!v.isTempActive() && !v.isActive()
					&& r <= grafo.propagationProbability(w, v)) {
				
				v.setTempActive(true);
				SpreadTrial(v);
			}
		}
	}

	public void clearActive() {
		for (Actor v : grafo.vertexSet()) {
			v.setActive(false);
		}
	}

	private void clearTempActive() {
		for (Actor v : grafo.vertexSet()) {
			v.setTempActive(false);
		}
	}
	
	public static void main(String[] args) {
		DirectedSocialNetwork g;
		g = new  SocialNetworkGenerate().gerarGrafo(500, 2.8);
		
		HashSet<Actor> seed = new OriginalGreedy(g).escolher(20);
		g.activate(seed);
		g.visualize();
	}

}
