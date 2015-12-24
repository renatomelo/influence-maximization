package algoritmos;

import java.util.HashSet;
import java.util.PriorityQueue;

import algoritmos.MarginalGain;
import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;

/*De acordo com os experimentos, utilizando o CELF como subrotina
 *  dentro do conjunto dominante obtem-se um resultado semelhante 
 *  ao CELF original, o que leva a crer sãio fortes indicativos de
 *  que os vértices "bons" estão dentro do conjunto dominante minimo
 *  num grafo direcionado
 */
public class DominatingSeed implements SeedChooser<Actor> {
	private DirectedSocialNetwork grafo;

	public DominatingSeed(DirectedSocialNetwork g) {
		this.grafo = g;
	}

	// @Override
	// public HashSet<Actor> escolher(int k) {
	// HashSet<Actor> semente = new HashSet<Actor>();
	// HashSet<Actor> minSet = new HashSet<Actor>();
	//
	// MinDominatingSet ds = new MinDominatingSet();
	// minSet = ds.fastGreedy(grafo);
	//
	// semente.addAll(grafo.verticesGrauMaior(minSet, k));
	//
	// return semente;
	// }

	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<Actor>();
		HashSet<Actor> minSet = new HashSet<Actor>();

		MinDominatingSet ds = new MinDominatingSet();
		minSet = ds.fastGreedy(grafo);

		if (minSet.size() < k) {
			System.out.println("Erro: o cojunto domintante não é menor que K");
			return null;
		}

		// create priority queue of all nodes, with marginal gain delta +inf
		PriorityQueue<MarginalGain> fila = priorityQueueOfGains(minSet);

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

		return semente;
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
			HashSet<Actor> minSet) {

		PriorityQueue<MarginalGain> fila = new PriorityQueue<MarginalGain>();

		for (Actor v : minSet) {
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
				.gerarGrafo(100, 2);
		HashSet<Actor> seed = new DominatingSeed(g).escolher(10);
		g.activate(seed);
		// HashSet<Actor> ativos = g.indepCascadeDiffusion(seed);
		// HashSet<Actor> ativos = g.linearThresholdDiffusion(seed);
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|S| = " + seed.size());
		// System.out.println("|A| = "+ativos.size());
		g.visualize();
	}

}
