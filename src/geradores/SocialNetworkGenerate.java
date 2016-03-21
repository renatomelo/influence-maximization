package geradores;

/*
 * Cria grafos artificiais nos modelos Limitante Linear (LT) e Cascata Independente (IC)
 * Por padrão gera grafos Power Law
 */

import grafos.Actor;
import grafos.DirectedSocialNetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import plot.Histograma;
import util.ComponentesConexos;

public class SocialNetworkGenerate {
	public static DirectedSocialNetwork g;
	
	// Por padrão a rede resultante é direcionada e com pesos
	public DirectedSocialNetwork gerarGrafo(int n, double beta) {
		boolean direcionado = true;
		boolean comPeso = true;

		return gerarModeloLT(n, beta, direcionado, comPeso);
	}

	// Por padrão a rede resultante é direcionada e com pesos
	public DirectedSocialNetwork gerarModeloLT(int n, double beta) {
		boolean direcionado = true;
		boolean comPeso = true;

		return gerarModeloLT(n, beta, direcionado, comPeso);
	}

	// Constroi um grafo power law
	public DirectedSocialNetwork gerarModeloLT(int n, double beta,
			boolean direcionado, boolean comPeso) {

		WeightedGraph<Actor, DefaultWeightedEdge> grafo;
		grafo = new GeradorGrafoPowerLaw().gerarComPesos(n, beta,
				direcionado);

		// Utiliza só o componente gigante do grafo
		ComponentesConexos<Actor, DefaultWeightedEdge> cc;
		cc = new ComponentesConexos<>(grafo);

		Graph<Actor, DefaultWeightedEdge> cg;
		cg = cc.getComponenteGigante(comPeso, direcionado);

		g = asDirectedSocialNetwork(cg);

		// Sorteia os limitantes de vertice
		for (Actor v : g.vertexSet()) {
			v.setThreshold(Math.random());
		}

		return g;
	}
	
	public DirectedSocialNetwork gerarGrafoInteiro(int n, double beta,
			boolean direcionado, boolean comPeso) {

		WeightedGraph<Actor, DefaultWeightedEdge> grafo;
		grafo = new GeradorGrafoPowerLaw().gerarComPesos(n, beta,
				direcionado);

		g = asDirectedSocialNetwork(grafo);

		return g;
	}

	// Por padrão a rede resultante é direcionada e com pesos
	public DirectedSocialNetwork gerarModeloIC(int n, double beta) {
		boolean direcionado = true;
		boolean comPeso = true;

		return gerarModeloIC(n, beta, direcionado, comPeso);
	}

	public DirectedSocialNetwork gerarModeloIC(int n, double beta,
			boolean direcionado, boolean comPeso) {

		WeightedGraph<Actor, DefaultWeightedEdge> grafo;
		grafo = new GeradorGrafoPowerLaw().gerarComPesos(n, beta, direcionado);

		// Utiliza só o componente gigante do grafo
		ComponentesConexos<Actor, DefaultWeightedEdge> cc;
		cc = new ComponentesConexos<>(grafo);

		Graph<Actor, DefaultWeightedEdge> cg;
		cg = cc.getComponenteGigante(comPeso, direcionado);

		g = asDirectedSocialNetwork(cg);

		return g;
	}

	/*
	 * Copia um grafo do tipo Graph<V,E> para uma rede socal
	 * (DirectedSocialNetwork)
	 */
	private DirectedSocialNetwork asDirectedSocialNetwork(
			Graph<Actor, DefaultWeightedEdge> cg) {

		DirectedSocialNetwork sn = new DirectedSocialNetwork(
				DefaultWeightedEdge.class);

		Set<Actor> vertices = cg.vertexSet();
		for (Actor a : vertices) {
			sn.addVertex(a);
		}

		Set<DefaultWeightedEdge> arestas = cg.edgeSet();
		for (DefaultWeightedEdge e : arestas) {
			sn.addEdge(cg.getEdgeSource(e), cg.getEdgeTarget(e), e);
		}

		return sn;
	}
	
	public static void main(String[] args) {
//		DirectedSocialNetwork g = new SocialNetworkGenerate().gerarGrafo(50000, 2.5);
		DirectedSocialNetwork g = new SocialNetworkGenerate().gerarGrafoInteiro(30000, 2.5, true, true);
		System.out.println("|V(G)|"+g.vertexSet().size());
		System.out.println("|E(G)|"+g.edgeSet().size());
		Histograma histograma = new Histograma();
		try {
			histograma.plotarGraficos(histograma.gerarHistograma(g));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
