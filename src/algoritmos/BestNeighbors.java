package algoritmos;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import util.ComparaPorGrau;
import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;

/*
 * Para grafos muito pequenos o número de vertices escolhidos pode ser < k
 * Como o grafo é direcionado o tem muitos "best neighbors" se o grafo é grande
 */
public class BestNeighbors implements SeedChooser<Actor> {
	public DirectedSocialNetwork grafo = null;

	public BestNeighbors(DirectedSocialNetwork g) {
		this.grafo = g;
	}

	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<>();

		DirectedSocialNetwork g = grafo.copy();

		// compare os vertices pelo grau
		ComparaPorGrau comp = new ComparaPorGrau(g);

		while (semente.size() < k) {
			Set<Actor> pretos = new HashSet<>();
			
			if (g.vertexSet().size() > 0) {						
				Set<Actor> sorvedouros = g.getSorvedouros();

				for (Actor v : sorvedouros) {
					pretos.addAll(g.inNeighborsOf(v));
				}

				// DS <-- DS U pretos
				if (pretos.size() > k) {
					Set<Actor> escolhidos = g.verticesGrauMaior(pretos, k);
					semente.addAll(escolhidos);
				}else
					semente.addAll(pretos);
				
				// Remove os pretos e seus vizinhos
				for (Actor v : pretos) {
					for (Actor w : g.outNeighborsOf(v)) {
						if (!pretos.contains(w))
							g.removeVertex(w);
					}
					g.removeVertex(v);
				}
				
				while (semente.size() < k) {
					if (g.vertexSet().size() > 0) {
						Actor v = Collections.max(g.vertexSet(), comp);

						for (Actor w : g.outNeighborsOf(v)) {
								g.removeVertex(w);
						}
						semente.add(v);
						g.removeVertex(v);
					} else
						break;
				}
			} else
				break;
		}
		return semente;
	}


	public static void main(String[] args) {
		DirectedSocialNetwork g = new SocialNetworkGenerate()
				.gerarGrafo(100, 2);
		HashSet<Actor> seed = new BestNeighbors(g).escolher(10);
		g.activate(seed);
		// HashSet<Actor> ativos = g.indepCascadeDiffusion(seed);
		// HashSet<Actor> ativos = g.linearThresholdDiffusion(seed);
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|S| = " + seed.size());
		// System.out.println("|A| = "+ativos.size());
		g.visualize();
	}
}
