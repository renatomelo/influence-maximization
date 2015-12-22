package algoritmos;

import java.util.HashSet;
import java.util.Set;

import util.ComparaPorGrau;
import com.google.common.collect.MinMaxPriorityQueue;

import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;

/*
 * TODO Testar comparação por grau de saida apenas
 */
public class HightDegree implements SeedChooser<Actor> {

	public DirectedSocialNetwork grafo = null;

	public HightDegree(DirectedSocialNetwork grafo) {
		super();
		this.grafo = grafo;
	}

	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<>();

		Set<Actor> vSet = grafo.vertexSet();

		MinMaxPriorityQueue<Actor> heapMinMax = null;

		ComparaPorGrau comparador = new ComparaPorGrau(grafo);

		heapMinMax = MinMaxPriorityQueue.orderedBy(comparador)
				.maximumSize(vSet.size()).create();

		for (Actor a : vSet) {
			heapMinMax.add(a);
		}

		while (semente.size() < k) {
			Actor maior = heapMinMax.removeLast();
			semente.add(maior);
		}
		return semente;
	}

	public static void main(String[] args) {
		DirectedSocialNetwork g = new SocialNetworkGenerate()
				.gerarGrafo(50, 2);
		HashSet<Actor> seed = new HightDegree(g).escolher(5);
		g.activate(seed);
		// HashSet<Actor> ativos = g.indepCascadeDiffusion(seed);
		 HashSet<Actor> ativos = g.linearThresholdDiffusion(seed);
		System.out.println("|V(G)| = " + g.vertexSet().size());
		 System.out.println("|A| = "+ativos.size());
//		g.visualize();
	}
}
