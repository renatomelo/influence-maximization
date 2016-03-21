package algoritmos;

import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import util.ComparaPorGrau;
import com.google.common.collect.MinMaxPriorityQueue;

/*
 * Algoritmo guloso para o menor conjunto dominante
 * RegularGreedy (Eubank et al. 2004)
 */
public class MinDominatingSet {

	public HashSet<Actor> regularGreedy(DirectedSocialNetwork grafo) {
		// DS <-- {}
		HashSet<Actor> dominante = new HashSet<Actor>();

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

	public HashSet<Actor> greedy(DirectedSocialNetwork grafo) {
		// DS <-- {}
		HashSet<Actor> dominante = new HashSet<Actor>();

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

	/**
	 * Está errado, tem que olhar melhor a questão da direção das arestas
	 **/
	
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
	
	public HashSet<Actor> fastGreedy2(DirectedSocialNetwork grafo) {
		int n = grafo.vertexSet().size();

		// DS <-- {}
		HashSet<Actor> dominante = new HashSet<>();

		// compare os vertices pelo grau
		ComparaPorGrau comp = new ComparaPorGrau(grafo);

		MinMaxPriorityQueue<Actor> fila = null;
		fila = MinMaxPriorityQueue.orderedBy(comp).maximumSize(n)
				.create();
		for (Actor v : grafo.vertexSet()) {
			fila.add(v);
		}

		Set<Actor> dominados = new HashSet<>();
		while (!fila.isEmpty()) {
			
			Actor v = fila.removeLast();
			Set<Actor> vizinhos = grafo.outNeighborsOf(v);

			if (dominados.addAll(vizinhos)) {
				dominante.add(v);
			}else
				dominados.add(v);
		}
		return dominante;
	}

	public static void main(String[] args) {

		DirectedSocialNetwork g = new SocialNetworkGenerate().gerarGrafo(
				5000, 2.5);

		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());

//		teste.exportarGrafo();
		long startTime = 0;

		System.out.println("\nConjunto Dominante");
		MinDominatingSet minDom = new MinDominatingSet();
		
		startTime = System.nanoTime();
		HashSet<Actor> ds = (HashSet<Actor>) minDom.greedy(g);
		System.out.println("Tempo: "+(System.nanoTime() - startTime)/1000);
		System.out.println("|DS|- vRegularGreedy: " + ds.size());
		System.out.println("\nspread: "+g.espectedSpread(ds, true));
//		g.activate(ds);
//		g.visualize();
		
		startTime = System.nanoTime();
		HashSet<Actor> ds2 = (HashSet<Actor>) minDom.regularGreedy(g);
//		System.out.println("Tempo: "+(System.nanoTime() - startTime)/1000);
		System.out.println("\n\n|DS|- RegularGreedy: " + ds2.size());
//		System.out.println("Vertices dominantes:");
//		for (Actor v : ds2) {
//			System.out.print(v+" ");
//		}
		System.out.println("\nspread: "+g.espectedSpread(ds2, true));
		
		
		startTime = System.nanoTime();
		HashSet<Actor> ds3 = minDom.fastGreedy(g);
//		System.out.println("Tempo: "+(System.nanoTime() - startTime)/1000);
		System.out.println("\n\n|DS|- FastGreedy: " + ds3.size());	
//		for (Actor v : ds3) {
//			System.out.print(v.toString()+" ");
//		}
		System.out.println("\nspread: "+g.espectedSpread(ds3, true));
//		g.activate(ds3);
//		g.visualize();
		
//		HashSet<Actor> seed = new LazyGreedy(g).escolher(ds3.size());
//		System.out.println("\nVertices Influentes: "+seed.size());
//		for (Actor v : seed) {
//			System.out.print(v.toString()+" ");
//		}
//		System.out.println("\n spread: "+g.espectedSpread(seed, true));
////		g.activate(seed);
////		g.visualize();
////		g.deactivate(seed);
//		
//		System.out.println("\nVertices influentes do fastGreedy:");
//		HashSet<Actor> interseccao = new HashSet<>(); 
//		int i = 0;
//		for (Actor v : seed) {
//			g.deactivate(v);
//			for (Actor w : ds3) {
//				if(v.equals(w)){
//					System.out.print(v.toString()+" ");
//					interseccao.add(v);
//					i++;
//				}
//			}
//		}
//		System.out.println(": "+i);
//		System.out.println("\n spread: "+g.espectedSpread(interseccao, true));
////		g.activate(interseccao);
////		g.visualize();
////		g.deactivate(interseccao);
//		
//		HashSet<Actor> seed2 = new DominatingSeed(g).escolher(interseccao.size());
//		System.out.println("\nVertices DiminatingSeed: "+seed2.size());
//		for (Actor v : seed2) {
//			System.out.print(v.toString()+" ");
//		}
//		System.out.println("\n spread: "+g.espectedSpread(seed2, true));
//		g.activate(seed2);
//		g.visualize();
//		g.deactivate(seed2);
		
		HashSet<Actor> artics = g.findArticulations();
		System.out.println("\nVertices de corte: "+artics.size());
//		for (Actor v : artics) {
//			System.out.print(v.toString()+" ");
//		}
		System.out.println("\n spread: "+g.espectedSpread(artics, true));
//		g.activate(artics);
//		g.visualize();
		

		// Cobertura de vertices
//		System.out.println();
//		System.out.println("\nCobertura de vérices");
//		startTime = System.nanoTime();
//		Set<Actor> cv = VertexCovers.find2ApproximationCover(teste);
//		System.out.println("Tempo: "+(System.nanoTime() - startTime)/1000);
//		System.out.println("|CV|- 2-aproximado: " + cv.size());
		// Set<Actor> cv2 = VertexCovers.findGreedyCover((UndirectedGraph<Actor,
		// DefaultWeightedEdge>) teste);

		// System.out.println("|CV|- Guloso: " + cv2.size());
		

	}
}
