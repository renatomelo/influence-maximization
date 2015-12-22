package algoritmos;

import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.VertexCovers;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Subgraph;

import util.ComparaPorGrau;

import com.google.common.collect.MinMaxPriorityQueue;

/*
 * Algoritmo guloso para o menor conjunto dominante
 * RegularGreedy (Eubank et al. 2004)
 */
public class MinDominatingSet {

	public Set<Actor> regularGreedy(DirectedSocialNetwork grafo) {
		// DS <-- {}
		Set<Actor> dominante = new HashSet<Actor>();

		// G' <-- G
		DirectedSocialNetwork g = grafo.copy();

		// compare vertices in descending order of degree
		ComparaPorGrau comp = new ComparaPorGrau(g);

		// while G' != {}
		while (g.vertexSet().size() > 0) {

			// v <-- vertex with maximum degree in G'
			Actor v = Collections.max(g.vertexSet(), comp);

			// G' = g - {v e N(v)}
			for (Actor w : g.outNeighborsOf(v)) {
				g.removeVertex(w);
			}

			// DS <-- DS U {v}
			dominante.add(v);

			// remove from G' every edge incident on v, and v itself
			g.removeVertex(v);
		}

		return dominante;
	}

	public Set<Actor> greedy(DirectedSocialNetwork grafo) {
		// DS <-- {}
		Set<Actor> dominante = new HashSet<Actor>();

		// G' <-- G
		DirectedSocialNetwork g = grafo.copy();

		ComparaPorGrau comp = new ComparaPorGrau(g);

		// while G' != {}
		while (g.vertexSet().size() > 0) {
			Set<Actor> pretos = new HashSet<Actor>();
			Set<Actor> sorvedouros = g.getSorvedouros();

			for (Actor v : sorvedouros) {
				pretos.addAll(g.inNeighborsOf(v));
			}

			// DS <-- DS U pretos
			dominante.addAll(pretos);

			// Remove os pretos e seus vizinhos (cinzas)
			for (Actor v : pretos) {
				for (Actor w : g.outNeighborsOf(v)) {
					if (!pretos.contains(w))
						g.removeVertex(w);
				}
				g.removeVertex(v);
			}
			// DS <-- DS U {v}
			Actor v = Collections.max(g.vertexSet(), comp);

			for (Actor w : g.outNeighborsOf(v)) {
				g.removeVertex(w);
			}
			dominante.add(v);
			g.removeVertex(v);
		}
		return dominante;
	}

	public HashSet<Actor> fastGreedy(DirectedSocialNetwork grafo) {
		int n = grafo.vertexSet().size();

		// DS <-- {}
		HashSet<Actor> dominante = new HashSet<>();

		// compare os vertices pelo grau
		ComparaPorGrau comp = new ComparaPorGrau(grafo);

		MinMaxPriorityQueue<Actor> heapMinMax = null;
		heapMinMax = MinMaxPriorityQueue.orderedBy(comp).maximumSize(n)
				.create();
		for (Actor v : grafo.vertexSet()) {
			heapMinMax.add(v);
		}

		Set<Actor> dominados = new HashSet<>();

		while (dominados.size() < n) {
			Actor v = heapMinMax.removeLast();

			Set<Actor> vizinhos = grafo.outNeighborsOf(v);

			if (dominados.addAll(vizinhos)) {
				dominante.add(v);
			}
			dominados.add(v);
		}
		return dominante;
	}

	public static void main(String[] args) {

		DirectedSocialNetwork teste = new SocialNetworkGenerate().gerarGrafo(
				500, 2);

		System.out.println("|V(G)| = " + teste.vertexSet().size());
		System.out.println("|E(G)| = " + teste.edgeSet().size());

		teste.exportarGrafo();

		System.out.println("\nConjunto Dominante");
		MinDominatingSet minDom = new MinDominatingSet();
		Set<Actor> ds = minDom.greedy(teste);
		System.out.println("|DS|- GreedyPlus: " + ds.size());

		Set<Actor> ds2 = minDom.regularGreedy(teste);
		System.out.println("|DS|- RegularGreedy: " + ds2.size());

		Set<Actor> ds3 = minDom.fastGreedy(teste);
		System.out.println("|DS|- FastGreedy: " + ds3.size());

		// Cobertura de vertices
		System.out.println();
		System.out.println("\nCobertura de vérices");
		Set<Actor> cv = VertexCovers.find2ApproximationCover(teste);
		// Set<Actor> cv2 = VertexCovers.findGreedyCover((UndirectedGraph<Actor,
		// DefaultWeightedEdge>) teste);

		// System.out.println("|CV|- Guloso: " + cv2.size());
		System.out.println("|CV|- 2-aproximado: " + cv.size());

	}
}
