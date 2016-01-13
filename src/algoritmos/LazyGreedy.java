package algoritmos;

import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;

import java.util.HashSet;
import java.util.PriorityQueue;

public class LazyGreedy implements SeedChooser<Actor> {
	private DirectedSocialNetwork grafo = null;

	public LazyGreedy(DirectedSocialNetwork g) {
		this.grafo = g;
	}

	/**
	 * Cria uma fila de prioridade por ganho marginal de todos os vértices do grafo
	 * e cada vértice é inicializado com ganho de NaN e inválido
	 * 
	 * @return uma fila de prioridade com o ganho marginal de todos os vértices
	 */
	private PriorityQueue<MarginalGain> priorityQueueOfGains() {

		PriorityQueue<MarginalGain> fila = new PriorityQueue<MarginalGain>();

		for (Actor v : grafo.vertexSet()) {
			fila.add(new MarginalGain(v, Double.NaN, false));
		}

		return fila;
	}

	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<>();

		// create priority queue of all nodes, with marginal gain delta +inf
		PriorityQueue<MarginalGain> fila = priorityQueueOfGains();

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
//			System.out.println("spread: " + MaxSpread);
		}

		return semente;
	}

	private double cascata(Actor v, HashSet<Actor> semente) {
		HashSet<Actor> ativados = new HashSet<>(semente);
		ativados.add(v);

		return grafo.espectedSpread(ativados, true);
	}

	public static void main(String[] args) {
		DirectedSocialNetwork g;
		g = new SocialNetworkGenerate().gerarGrafo(500, 2.8);
		long startTime = 0;

		startTime = System.nanoTime();
		HashSet<Actor> seed2 = new LazyGreedy(g).escolher(15);
		System.out.println("LazyGreedy = "
				+ g.espectedSpread(seed2, true) + ", tempo: "
				+ (System.nanoTime() - startTime) / 1000);

		startTime = System.nanoTime();
		HashSet<Actor> seed1 = new GreedyIC(g).escolher(10);
		System.out.println("GeneralGreedy = " + g.espectedSpread(seed1, true)
				+ ", tempo: " + (System.nanoTime() - startTime) / 1000);
	}

}
