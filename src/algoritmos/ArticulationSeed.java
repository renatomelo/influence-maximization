package algoritmos;

import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BlockCutpointGraph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import geradores.GeradorGrafoGnp;
import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;

public class ArticulationSeed implements SeedChooser<Actor> {
	private DirectedSocialNetwork grafo = null;

	public ArticulationSeed(DirectedSocialNetwork g) {
		this.grafo = g;
	}

	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<>();

		HashSet<Actor> cutActors = (HashSet<Actor>) findArticulations();
		System.out.println("|cutActors| = " + cutActors.size());

		if (cutActors.size() >= k) {

			// create priority queue of all nodes, with marginal gain delta +inf
			PriorityQueue<MarginalGain> fila = priorityQueueOfGains(cutActors);

			double MaxSpread = 0;

			while (semente.size() < k) {
				// set all gains invalid
				for (MarginalGain mg : fila) {
					mg.setValid(false);
				}

				while (true) {
					MarginalGain max = fila.poll();
					if (max.isValid() == true) {
						semente.add(max.getVertice());
						MaxSpread = MaxSpread + max.getGain();
						break;
					} else {
						double sigma = cascata(max.getVertice(), semente);
						double delta = sigma - MaxSpread;
						max.setGain(delta);
						max.setValid(true);
						fila.add(max);
					}
				}
			}
		}else{
			System.out.println("Abortado: poucos candidatos");
			return null;
		}

		return semente;
	}

	/**
	 * Ignora as direções das arestas e encontra os vértices de corte 
	 * do grafo não direcionado resultante
	 * 
	 * @return Conjunto de vértices de corte
	 */
	private Set<Actor> findArticulations() {
		UndirectedGraph<Actor, DefaultWeightedEdge> g;
		g = new AsUndirectedGraph<>(grafo);

		BlockCutpointGraph<Actor, DefaultWeightedEdge> blocos;
		blocos = new BlockCutpointGraph<>(g);

		Set<Actor> articulacoes = blocos.getCutpoints();
		return articulacoes;
	}
	
	/**
	 * Cria uma fila de prioridade por ganho marginal de todos os vértices do
	 * grafo e cada vértice é inicializado com ganho de NaN e inválido
	 * 
	 * @param minSet
	 * 
	 * @return uma fila de prioridade com o ganho marginal de todos os vértices
	 */

	private PriorityQueue<MarginalGain> priorityQueueOfGains(
			HashSet<Actor> vSet) {

		PriorityQueue<MarginalGain> fila = new PriorityQueue<MarginalGain>();

		for (Actor v : vSet) {
			fila.add(new MarginalGain(v, Double.NaN, false));
		}

		return fila;
	}

	private double cascata(Actor v, HashSet<Actor> semente) {
		HashSet<Actor> ativados = new HashSet<>(semente);
		ativados.add(v);

		return grafo.espectedSpread(ativados, true);
	}

	public static void main(String[] args) {

		DirectedSocialNetwork g = new SocialNetworkGenerate()
				.gerarGrafo(5000, 2);

		HashSet<Actor> seed = new ArticulationSeed(g).escolher(20);
		g.activate(seed);
		 HashSet<Actor> ativos = g.indepCascadeDiffusion(seed);
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|S| = " + seed.size());
		 System.out.println("|A| = "+ativos.size());
//		g.visualize();
	}

}
