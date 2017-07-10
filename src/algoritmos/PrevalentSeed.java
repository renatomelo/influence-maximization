package algoritmos;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import util.ComparaPorGrau;

import com.google.common.collect.MinMaxPriorityQueue;

import algoritmos.MarginalGain;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;
import readgraph.GraphReader;

/*De acordo com os experimentos, utilizando o CELF como subrotina
 *  dentro do conjunto dominante obtem-se um resultado semelhante 
 *  ao CELF original, o que leva a crer são fortes indicativos de
 *  que os vértices "bons" estão dentro do conjunto dominante minimo
 *  num grafo direcionado
 */
public class PrevalentSeed implements SeedChooser<Actor> {
	private DirectedSocialNetwork grafo;
	private int cont; // número de chamadas a sigma
	private double[] spreadData; // histograma da propagação esperada
	private double[] callData;

	public PrevalentSeed(DirectedSocialNetwork g) {
		this.grafo = g;
	}

	public HashSet<Actor> preSelecao(DirectedSocialNetwork grafo) {
		int n = grafo.vertexSet().size();

		HashSet<Actor> candidatos = new HashSet<>();

		// comparador de vertices pelo grau
		ComparaPorGrau comp = new ComparaPorGrau(grafo);

		MinMaxPriorityQueue<Actor> heapMinMax = null;
		heapMinMax = MinMaxPriorityQueue.orderedBy(comp).maximumSize(n).create();
		for (Actor v : grafo.vertexSet()) {
			heapMinMax.add(v);
		}

		Set<Actor> cobertos = new HashSet<>();
		while (cobertos.size() < n) {

			Actor v = heapMinMax.removeLast();
			Set<Actor> vizinhos = grafo.outNeighborsOf(v);

			if (cobertos.addAll(vizinhos)) {
				candidatos.add(v);
			}
			cobertos.add(v);
		}
		return candidatos;
	}

	public HashSet<Actor> preSelecaoOriginal(DirectedSocialNetwork grafo) {
		int n = grafo.vertexSet().size();

		// DS <-- {}
		HashSet<Actor> candidatos = new HashSet<>();

		// compare os vertices pelo grau
		ComparaPorGrau comp = new ComparaPorGrau(grafo);

		MinMaxPriorityQueue<Actor> heapMinMax = null;
		heapMinMax = MinMaxPriorityQueue.orderedBy(comp).maximumSize(n).create();
		for (Actor v : grafo.vertexSet()) {
			heapMinMax.add(v);
		}

		Set<Actor> cobertos = new HashSet<>();
		while (!heapMinMax.isEmpty()) {

			Actor v = heapMinMax.removeLast();
			Set<Actor> vizinhos = grafo.outNeighborsOf(v);

			if (cobertos.addAll(vizinhos)) {
				candidatos.add(v);
				cobertos.add(v);
			}
		}
		return candidatos;
	}

	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<Actor>();
		HashSet<Actor> candidatos = new HashSet<Actor>();
		spreadData = new double[k + 1];
		callData = new double[k + 1];

		candidatos = preSelecaoOriginal(grafo);
		System.out.println("|C| = " + candidatos.size());

		if (candidatos.size() < k) {
			System.out.println("Erro: o cojunto de candidatos é menor que K");
			return null;
		}

		// create priority queue of all nodes, with marginal gain delta +inf
		PriorityQueue<MarginalGain> fila = priorityQueueOfGains(candidatos);

		double maxSpread = 0;
		spreadData[0] = 0;
		cont = 0;

		while (semente.size() < k) {
			// set all gains invalid
			for (MarginalGain mg : fila) {
				mg.setValid(false);
			}

			while (true) {
				MarginalGain max = fila.poll();
				if (max.isValid() == true) {
					semente.add(max.getVertice());
					maxSpread = maxSpread + max.getGain();
//					System.out.println(semente.size() + "\t" + maxSpread);
					spreadData[semente.size()] = maxSpread;
					break;
				} else {
					double sigma = cascata(max.getVertice(), semente);
					double delta = sigma - maxSpread;
					max.setGain(delta);
					max.setValid(true);
					fila.add(max);
					cont++; // Conta o numero de chamadas à sigma
				}
			}
		}
		System.out.println("Chamadas: " + cont);
		System.out.println("Chamadas depois da 1a iteracao: " + (cont - candidatos.size()));
		return semente;
	}

	public int callToSigma() {
		return this.cont;
	}

	public double[] getSpreadData() {
		return this.spreadData;
	}

	public double[] getCallData() {
		return this.callData;
	}

	public HashSet<Actor> escolher2(int k) {
		HashSet<Actor> semente = new HashSet<Actor>();
		HashSet<Actor> minSet = new HashSet<Actor>();
		cont = 0;

		MinDominatingSet ds = new MinDominatingSet();
		minSet = ds.fastGreedy2(grafo);

		System.out.println("|DS| = " + minSet.size());
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
					System.out.println("|S| = " + semente.size() + ", |A| = " + MaxSpread);
					break;
				} else {
					double sigma = cascata(max.getVertice(), semente);
					double delta = sigma - MaxSpread;
					max.setGain(delta);
					max.setValid(true);
					fila.add(max);
					cont++;
				}
			}
		}
		System.out.println("chamadas: " + cont);
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

	/**
	 * Cria uma fila de prioridade por ganho marginal de todos os vértices do
	 * grafo e cada vértice é inicializado com ganho de NaN e inválido
	 * 
	 * @param minSet
	 * 
	 * @return uma fila de prioridade com o ganho marginal de todos os vértices
	 */

	private PriorityQueue<MarginalGain> priorityQueueOfGains(HashSet<Actor> minSet) {

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
		/*
		 * DirectedSocialNetwork g = new
		 * SocialNetworkGenerate().gerarGrafo(2000, 2.5);
		 * System.out.println("|V(G)| = " + g.vertexSet().size()); long
		 * startTime = 0; startTime = System.nanoTime(); HashSet<Actor> seed =
		 * new PrevalentSeed(g).escolher2(15); System.out.println("Tempo: " +
		 * (System.nanoTime() - startTime) / 1000);
		 * 
		 * HashSet<Actor> ativos = g.indepCascadeDiffusion(seed);
		 * 
		 * // System.out.println("|S| = " + seed.size());
		 * System.out.println("|A| = " + ativos.size()); g.deactivate(ativos);
		 * // g.activate(seed); // g.visualize();
		 * 
		 * startTime = System.nanoTime(); HashSet<Actor> seed2 = new
		 * PrevalentSeed(g).escolher(15); System.out.println("Tempo: " +
		 * (System.nanoTime() - startTime) / 1000);
		 * 
		 * HashSet<Actor> ativos2 = g.indepCascadeDiffusion(seed2); //
		 * System.out.println("\n|S| = " + seed2.size());
		 * System.out.println("|A| = " + ativos2.size()); g.deactivate(ativos2);
		 * 
		 * // HashSet<Actor> seed3 = new DominatingSeed(g).escolherRandom(15);
		 * // // HashSet<Actor> ativos3 = g.indepCascadeDiffusion(seed3); ////
		 * System.out.println("\n|S| = " + seed3.size()); //
		 * System.out.println("|A| = "+ativos3.size());
		 */

		DirectedSocialNetwork g;
		PrevalentSeed ps = new PrevalentSeed(null);

		System.out.println("enron.txt");
		g = new GraphReader().enron();
		System.out.println("|C| = " + ps.preSelecaoOriginal(g).size() + "\n");

		System.out.println("epinions.txt");
		g = new GraphReader().readEpinions();
		System.out.println("|C| = " + ps.preSelecaoOriginal(g).size() + "\n");

		System.out.println("dblp.txt");
		g = new GraphReader().readDblp();
		System.out.println("|C| = " + ps.preSelecaoOriginal(g).size() + "\n");

		System.out.println("phy.txt");
		g = new GraphReader().readPhy();
		System.out.println("|C| = " + ps.preSelecaoOriginal(g).size() + "\n");

		System.out.println("hep.txt");
		g = new GraphReader().readHep();
		System.out.println("|C| = " + ps.preSelecaoOriginal(g).size() + "\n");

		System.out.println("amazon.txt");
		g = new GraphReader().amazon();
		System.out.println("|C| = " + ps.preSelecaoOriginal(g).size() + "\n");

	}

}
