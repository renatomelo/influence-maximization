package algoritmos;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

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

	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<Actor>();
		HashSet<Actor> minSet = new HashSet<Actor>();

		MinDominatingSet ds = new MinDominatingSet();
		minSet = ds.fastGreedy(grafo);

		if (minSet.size() < k) {
			System.out.println("Erro: o cojunto domintante é menor que K");
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
	
	public HashSet<Actor> escolherGreedy(int k) {
		HashSet<Actor> semente = new HashSet<Actor>();
		HashSet<Actor> minSet = new HashSet<Actor>();

		MinDominatingSet ds = new MinDominatingSet();
		minSet = ds.greedy(grafo);

		if (minSet.size() < k) {
			System.out.println("Erro: o cojunto domintante é menor que K");
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

	public HashSet<Actor> escolherRandom(int k) {
		HashSet<Actor> semente = new HashSet<Actor>();
		Set<Actor> vSet = grafo.vertexSet();
		HashSet<Actor> rSet = new HashSet<Actor>();

		Actor[] vertices = new Actor[vSet.size()];
		int i = 0;
		for (Actor a : vSet)
			vertices[i++] = a;

		Random rand = new Random();
		while (rSet.size() < vSet.size()/4) {
			rSet.add(vertices[rand.nextInt(vertices.length)]);
		}

		// create priority queue of all nodes, with marginal gain delta +inf
		PriorityQueue<MarginalGain> fila = priorityQueueOfGains(rSet);

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
				.gerarGrafo(600, 2.5);
		HashSet<Actor> seed = new DominatingSeed(g).escolherGreedy(15);
		
		 HashSet<Actor> ativos = g.indepCascadeDiffusion(seed);
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|S| = " + seed.size());
		 System.out.println("|A| = "+ativos.size());
		 g.deactivate(ativos);
//		g.activate(seed);
//		g.visualize();
		 
		 HashSet<Actor> seed2 = new DominatingSeed(g).escolher(15);
			
		 HashSet<Actor> ativos2 = g.indepCascadeDiffusion(seed2);
		System.out.println("\n|S| = " + seed2.size());
		 System.out.println("|A| = "+ativos2.size());
		 g.deactivate(ativos2);
		 
		 HashSet<Actor> seed3 = new DominatingSeed(g).escolherRandom(15);
			
		 HashSet<Actor> ativos3 = g.indepCascadeDiffusion(seed3);
		System.out.println("\n|S| = " + seed3.size());
		 System.out.println("|A| = "+ativos3.size());
		 
	}

}
