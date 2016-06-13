package geradores;

/*
 * Gera artificialmente grafos aleatórios usando o modelo de grafo generalizado (GRG model)
 * em conjunto com o modelo de Chung-Lu, os grafos gerados seguem a mesma topologia
 * E para cada grafo gerado com os mesmo parametros é muito improvavel que gerem grafos iguais.
 */
import grafos.Actor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.graph.DirectedWeightedSubgraph;
import org.jgrapht.graph.UndirectedSubgraph;
import org.jgrapht.graph.UndirectedWeightedSubgraph;

public class GeradorGrafoPowerLaw {

	// Por padrão retorna grafo não direcionado
	public Graph<Actor, DefaultEdge> gerarGrafo(int n, double beta) {
		return new GeradorGrafoPowerLaw().gerarGrafo(n, beta, false);
	}

	// não direcionado com pesos
	public WeightedGraph<Actor, DefaultWeightedEdge> gerarGrafoComPesos(
			int n, double beta) {
		return new GeradorGrafoPowerLaw().gerarComPesos(n, beta, false);
	}

	public Graph<Actor, DefaultEdge> gerarGrafo(int n, double beta,
			boolean direcionado) {
		//Devido a futura remoção dos vertices isolados
		if (direcionado) {
			n = (int) (n + n * 0.3);
		}else
			n = (int) (n + n * 0.5);
		
		Graph<Actor, DefaultEdge> g;
		// Cria uma sequencia de graus que respeite uma curva Power Law, ou
		// seja, muitos vertices de grau muito baixo e poucos de grau muito auto
		int sequenciaGrafica[] = new SequenciaGrafica().gerarSequenciaPowerLaw(
				n, beta);

		// Gera o grafo usando o modelo GRG passando a sequencia de graus
		GeradorModeloGRG grg = new GeradorModeloGRG();

		g = grg.gerarGrafo(sequenciaGrafica, direcionado);

		return removerIsolados(g, direcionado);
	}

	private <V, E> Graph<V, E> removerIsolados(Graph<V, E> g,
			boolean direcionado) {
		Set<V> vertices = g.vertexSet();

		// remove os vetices isolados
		Set<V> subSet = new HashSet<>();
		for (V v : vertices) {
			if (g.edgesOf(v).size() == 0) {
				subSet.add(v);
			}
		}
		g.removeAllVertices(subSet);

		// atualiza os indices dos vertices restantes
		Set<V> novoSubSet = g.vertexSet();
		int i = 0;
		for (V v : novoSubSet) {
			if (v instanceof Actor) {
				((Actor) v).setIndex(i++);
			}
		}

		Set<E> arestas = g.edgeSet();

		if (direcionado) {
			DirectedSubgraph<V, E> sub2;
			sub2 = new DirectedSubgraph<>((DirectedGraph<V, E>) g, novoSubSet, arestas);
			return sub2;
		}else{
			UndirectedSubgraph<V, E> sub;
			sub = new UndirectedSubgraph<V, E>((UndirectedGraph<V, E>) g, novoSubSet, arestas);
			return sub;
		}
	}

	private <V, E> WeightedGraph<V, E> removerIsoladosComPeso(
			WeightedGraph<V, E> g, boolean direcionado) {
		Set<V> vertices = g.vertexSet();

		// remove os vetices isolados
		Set<V> subSet = new HashSet<>();
		for (V v : vertices) {
			if (g.edgesOf(v).size() == 0) {
				subSet.add(v);
			}
		}
		g.removeAllVertices(subSet);

		// atualiza os indices dos vertices restantes
		Set<V> novoSubSet = g.vertexSet();
		int i = 0;
		for (V v : novoSubSet) {
			if (v instanceof Actor) {
				((Actor) v).setIndex(i++);
			}
		}

		Set<E> arestas = g.edgeSet();

		if (direcionado) {
			DirectedWeightedSubgraph<V, E> sub;
			sub = new DirectedWeightedSubgraph<>(g, novoSubSet, arestas);
			return sub;
		} else {
			UndirectedWeightedSubgraph<V, E> sub2;
			sub2 = new UndirectedWeightedSubgraph<>(g, novoSubSet, arestas);
			return sub2;
		}
	}

	public WeightedGraph<Actor, DefaultWeightedEdge> gerarComPesos(
			int n, double beta, boolean direcionado) {
		
		if (direcionado) {
			n = (int) (n + n * 0.13);
		}else
			n = (int) (n + n * 0.5);

		WeightedGraph<Actor, DefaultWeightedEdge> g;

		// Cria uma sequencia de graus que respeite uma curva Power Law, ou
		// seja, muitos vertices de grau muito baixo e poucos de grau muito auto
		int sequenciaGrafica[] = new SequenciaGrafica().gerarSequenciaPowerLaw(
				n, beta);

		// Gera o grafo usando o modelo GRG passando a sequencia de graus como
		// entrada
		GeradorModeloGRG grg = new GeradorModeloGRG();

		g = grg.gerarGrafoComPesos(sequenciaGrafica, direcionado);

		// return g;
		return removerIsoladosComPeso(g, direcionado);
	}

	static public <E> void exportarGrafo(Graph<Actor, E> g) {
		IntegerNameProvider<Actor> id = new IntegerNameProvider<Actor>();
		
		DOTExporter<Actor, E> dot;
		dot = new DOTExporter<Actor, E>(id, null, null, null, null);
		try {
			// Exporta o grafo para o arquivo
			dot.export(new FileWriter("grafo.dot"), g);
			System.out.println("Arquivo grafo.dot gerado");
		} catch (IOException e) {
		}
	}
}
