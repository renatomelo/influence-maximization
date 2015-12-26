package grafos;

import geradores.SocialNetworkGenerate;
import interfaces.NetworkOfActivatables;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BlockCutpointGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.google.common.collect.MinMaxPriorityQueue;

import util.ComparaPorGrau;
import view.Vizualizador;


public class DirectedSocialNetwork extends
		SimpleDirectedWeightedGraph<Actor, DefaultWeightedEdge> implements
		NetworkOfActivatables<Actor> {

	/**
	 * serialID Gerado automaticamente
	 */
	private static final long serialVersionUID = -1330052074580361253L;
	/**
	 * Flag which shows if there has been an active actor during the history of
	 * this network. In particular, if it is false, no actor is active.
	 */
	protected boolean active;

	private Vizualizador<Actor, DefaultWeightedEdge> vizualizador;

	public DirectedSocialNetwork(Class<? extends DefaultWeightedEdge> edgeClass) {
		super(edgeClass);
		this.active = false;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets this network as active, i.e., it has had at least one of its nodes
	 * been active during the history of this network.
	 * 
	 * @param active
	 *            true if and only if a node of this network has been active
	 */
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Activates all actives specified by the input actors, i.e., marks each of
	 * them as active. This method is a convenience routine to simplify
	 * activation, for instance of innovators. Note that this method changes the
	 * state of the input actors (call by reference), no matter whether the
	 * returned set is stored by the invoking process or not.
	 * 
	 * @param actors
	 *            a set actors of this network to be activated
	 * @return the set actors each of whom is activated
	 */
	public HashSet<Actor> activate(Actor... actors) {
		for (Actor a : actors) {
			a.setActive(true);
		}

		if (actors.length > 0)
			this.active = true;

		HashSet<Actor> activated = new HashSet<>();
		java.util.Collections.addAll(activated, actors);
		return activated;
	}

	public HashSet<Actor> activate(java.util.Collection<Actor> actors) {
		for (Actor a : actors) {
			a.setActive(true);
		}
		if (actors.size() > 0)
			this.active = true;
		return new HashSet<>(actors);
	}


	public HashSet<Actor> deactivate(Actor... actors) {
		for (Actor a : actors) {
			a.setActive(false);
		}
		HashSet<Actor> deactivated = new HashSet<>();
		java.util.Collections.addAll(deactivated, actors);
		return deactivated;
	}

	
	public HashSet<Actor> deactivate(java.util.Collection<Actor> actors) {
		for (Actor a : actors) {
			a.setActive(false);
		}
		return new HashSet<>(actors);
	}

	@Override
	public HashSet<Actor> linearThresholdDiffusion(HashSet<Actor> seed) {
		activate(seed);

		HashSet<Actor> activated = new HashSet<>();
		Stack<Actor> pilha = new Stack<Actor>();
		double peso, cont;

		for (Actor s : seed) {
			pilha.push(s);
			while (!pilha.isEmpty()) {
				Actor v = pilha.pop();

				v.setActive(true);
				activated.add(v);

				for (Actor w : outNeighborsOf(v)) {
					peso = 0; cont = 0;

					if (!w.isActive()) {
						peso += propagationProbability(v, w);
						cont++;
						
						for (Actor u : inNeighborsOf(w)) {
							if (u.isActive()) {
								peso += propagationProbability(u, w);
								cont++;
							}
						}
						if (peso/cont >= w.getThreshold()) {
							pilha.push(w);
						}
					}
				}
			}
		}
		return activated;
	}

	@Override
	public HashSet<Actor> indepCascadeDiffusion(HashSet<Actor> seed) {
		activate(seed);

		HashSet<Actor> activated = new HashSet<>();
		Stack<Actor> pilha = new Stack<Actor>();

		for (Actor s : seed) {
			pilha.push(s);
			while (!pilha.isEmpty()) {
				Actor v = pilha.pop();

				v.setActive(true);
				activated.add(v);

				for (Actor w : outNeighborsOf(v)) {
					double rand = Math.random();
					if (rand <= propagationProbability(v, w)) {
						if (!w.isActive()) {
							pilha.push(w);
						}
					}
				}
			}
		}
		return activated;
	}
	
	
	@Override
	public double espectedSpread(HashSet<Actor> seed, boolean ic) {
		double media = 0;
		int soma = 0;
		int repeticoes = 1000;

		if (!ic) {
			HashSet<Actor> ativados = linearThresholdDiffusion(seed);
			media = ativados.size();
			deactivate(ativados);
		} else {
			for (int i = 0; i < repeticoes; i++) {
				HashSet<Actor> ativados = indepCascadeDiffusion(seed);
				soma = soma + ativados.size();
				deactivate(ativados);

				media = soma / repeticoes;
			}
		}
		return media;
	}

	public HashSet<Actor> inNeighborsOf(Actor v) {
		HashSet<Actor> adj = new HashSet<>();
		Set<DefaultWeightedEdge> edges = incomingEdgesOf(v);
		for (DefaultWeightedEdge e : edges) {
			adj.add(getEdgeSource(e));
		}
		return adj;
	}

	public HashSet<Actor> outNeighborsOf(Actor v) {
		HashSet<Actor> adj = new HashSet<>();
		Set<DefaultWeightedEdge> edges = outgoingEdgesOf(v);
		for (DefaultWeightedEdge e : edges) {
			adj.add(getEdgeTarget(e));
		}
		return adj;
	}

	public double propagationProbability(Actor a, Actor b) {
		DefaultWeightedEdge e = getEdge(a, b);
		return this.getEdgeWeight(e);
	}
	
	public Set<Actor> verticesGrauMaior(Set<Actor> vertexSet, int k) {
		
		Set<Actor> escolhidos = new HashSet<>();
		MinMaxPriorityQueue<Actor> heapMinMax = null;

		ComparaPorGrau comparador = new ComparaPorGrau(this);

		heapMinMax = MinMaxPriorityQueue.orderedBy(comparador)
				.maximumSize(vertexSet.size()).create();

		for (Actor a : vertexSet) {
			heapMinMax.add(a);
		}

		while (escolhidos.size() < k) {
			Actor maior = heapMinMax.removeLast();
			escolhidos.add(maior);
		}

		return escolhidos;
	}

	public DirectedSocialNetwork copy() {
		DirectedSocialNetwork g = new DirectedSocialNetwork(
				DefaultWeightedEdge.class);

		for (Actor a : vertexSet()) {
			g.addVertex(a);
		}

		for (DefaultWeightedEdge e : edgeSet()) {
			g.addEdge(getEdgeSource(e), getEdgeTarget(e), e);
		}

		return g;
	}

	// Pega os sorvedouros
	public Set<Actor> getSorvedouros() {
		Set<Actor> set = new HashSet<>();
		for (Actor v : vertexSet()) {
			if (inDegreeOf(v) == 1 && outDegreeOf(v) == 0) {
				set.add(v);
			}
		}
		return set;
	}
	
	// Pega os vertices fontes
	public Set<Actor> getFontes() {
		Set<Actor> set = new HashSet<>();
		for (Actor v : vertexSet()) {
			if (outDegreeOf(v) == 1 && inDegreeOf(v) == 0) {
				set.add(v);
			}
		}
		return set;
	}
	
	/**
	 * Ignora as direções das arestas e encontra os vértices de corte 
	 * do grafo não direcionado resultante
	 * 
	 * @return Conjunto de vértices de corte
	 */
	public HashSet<Actor> findArticulations() {
		UndirectedGraph<Actor, DefaultWeightedEdge> g;
		g = new AsUndirectedGraph<>(this);

		BlockCutpointGraph<Actor, DefaultWeightedEdge> blocos;
		blocos = new BlockCutpointGraph<>(g);

		return (HashSet<Actor>) blocos.getCutpoints();
	}

	public void exportarGrafo() {
		IntegerNameProvider<Actor> p1 = new IntegerNameProvider<Actor>();
		DOTExporter<Actor, DefaultWeightedEdge> dot;
		dot = new DOTExporter<Actor, DefaultWeightedEdge>(p1, null, null, null, null);
		try {
			dot.export(new FileWriter("grafo.dot"), this);
			System.out.println("\nArquivo grafo.dot gerado");
		} catch (IOException e) {
		}
	}

	public void visualize() {
		vizualizador = new Vizualizador<Actor, DefaultWeightedEdge>(this);
	}

	public void shutDisplay() {
		if (vizualizador != null) {
			vizualizador.setVisible(false);
		}
	}

	public static void main(String[] args) {
		DirectedSocialNetwork g = new DirectedSocialNetwork(
				DefaultWeightedEdge.class);

		g = new SocialNetworkGenerate().gerarGrafo(40, 2);

		HashSet<Actor> seed = new HashSet<>();

		Set<Actor> vertices = g.vertexSet();
		for (Actor actor : vertices) {
			if (seed.size() < 5) {
				seed.add(actor);
			}

		}
		
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());
		System.out.println("Seed:");
		for (Actor a : seed) {
			System.out.println(a.toString());
		}

		System.out.println("Difusão média no modelo IC = "+g.espectedSpread(seed, true));
		System.out.println("Difusão média no modelo LT = "+g.espectedSpread(seed, false));
		
//		g.activate(seed);
//		g.visualize();
	}
}
