package geradores;

import grafos.Actor;

import java.util.ArrayList;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

/*
 * Gera um grafo aleatório generalizado (generalized random graph - GRG)
 * de acordo com uma sequencia W de pesos dos vértices, vertices com pesos 
 * maiores tem mais chances de possuirem mais vizinhos do que vertices 
 * com peso baixo 
 */

public class GeradorModeloGRG {
	
	public Graph<Actor, DefaultEdge> gerarGrafo(int[] seq) {
		return new GeradorModeloGRG().gerarGrafo(seq, false);
	}

	public Graph<Actor, DefaultEdge> gerarGrafo(int[] seq, boolean direcionado) {
		Graph<Actor, DefaultEdge> g;
		
		ArrayList<Actor> vertices = new ArrayList<>();
		Double p = 0.0;
		int soma = 0;
		int cont = 0;

		if (direcionado)
			g = new SimpleDirectedGraph<>(DefaultEdge.class);
		else
			g = new SimpleGraph<>(DefaultEdge.class);

		// cria y = seq[i] vertices de peso i para todo i
		for (int i = 0; i < seq.length; i++) {
			for (int j = 0; j < seq[i]; j++) {
				Actor v = new Actor(cont++, i);
				vertices.add(v);
				g.addVertex(v); // Add o vértice no grafo
			}
		}

		// Obtem a soma dos pesos de todos os vértices
		Set<Actor> vSet = g.vertexSet();
		int n = vSet.size();
		for (Actor v : vSet) {
			soma = soma + v.peso;
		}

		// Sorteio das arestas/arcos
		// Add uma aresta com probabilidade p = (v_i * v_j / soma)
		if (!direcionado) {
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					p = (vertices.get(i).peso * vertices.get(j).peso)
							/ ((double) soma);
					if (Math.random() < p)
						g.addEdge(vertices.get(i), vertices.get(j));
				}
			}
		} else {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i != j) {
						p = (vertices.get(i).peso * vertices.get(j).peso)
								/ ((double) soma);
						if (Math.random() < p)
							g.addEdge(vertices.get(i), vertices.get(j));
					}
				}
			}
		}
		return g;
	}
	
	public WeightedGraph<Actor, DefaultWeightedEdge> gerarGrafoComPesos(int[] seq, boolean direcionado) {

		WeightedGraph<Actor, DefaultWeightedEdge> g;
		ArrayList<Actor> vertices = new ArrayList<>();
		Double p = 0.0;
		int soma = 0;
		int cont = 0;
		
		if (direcionado)
			g = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		else
			g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		// cria y = seq[i] vertices de peso i para todo i
		for (int i = 0; i < seq.length; i++) {
			for (int j = 0; j < seq[i]; j++) {
				Actor v = new Actor(cont++, i);
				vertices.add(v);
				g.addVertex(v); // Add o vértice no grafo
			}
		}

		// Obtem a soma dos pesos de todos os vértices
		Set<Actor> vSet = g.vertexSet();
		int n = vSet.size();
		for (Actor v : vSet) {
			soma = soma + v.peso;
		}

		// Sorteio das arestas/arcos
		// Add uma aresta com probabilidade p = (v_i * v_j / soma)
		// Define os pesos das arestas aleatoriamente de maneira uniforme
		if (!direcionado) {
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					p = (vertices.get(i).peso * vertices.get(j).peso)
							/ ((double) soma);
					if (Math.random() < p){
						DefaultWeightedEdge e;
						e = g.addEdge(vertices.get(i), vertices.get(j));
						g.setEdgeWeight(e, Math.random());
					}
				}
			}
		} else {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i != j) {
						p = (vertices.get(i).peso * vertices.get(j).peso)
								/ ((double) soma);
						if (Math.random() < p){
							DefaultWeightedEdge e;
							e = g.addEdge(vertices.get(i), vertices.get(j));
							g.setEdgeWeight(e, Math.random());
						}
					}
				}
			}
		}
		return g;
	}
}
