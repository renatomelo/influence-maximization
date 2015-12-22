package algoritmos;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;

public class RandomSeed implements SeedChooser<Actor>{
	public Graph<Actor, DefaultWeightedEdge> grafo;

	public RandomSeed(Graph<Actor, DefaultWeightedEdge> grafo) {
		this.grafo = grafo;
	}

	@Override
	public HashSet<Actor> escolher(int k) {		
		HashSet<Actor> semente = new HashSet<>();		
		Set<Actor> vSet = grafo.vertexSet();

		Actor[] vertices = new Actor[vSet.size()];
		int i = 0;
		for (Actor a : vSet)
			vertices[i++] = a;

		Random rand = new Random();
		Actor sorteado = vertices[rand.nextInt(vertices.length)];
		while (semente.size() < k) {
			if (!semente.contains(sorteado)) {
				semente.add(sorteado);
			}
			sorteado = vertices[rand.nextInt(vertices.length)];
		}

		return semente;
	}
	
	public static void main(String[] args) {
		DirectedSocialNetwork g = new SocialNetworkGenerate().gerarGrafo(50, 2);
		HashSet<Actor> seed = new RandomSeed(g).escolher(5);
		g.activate(seed);
//		HashSet<Actor> ativos =  g.indepCascadeDiffusion(seed);
		HashSet<Actor> ativos =  g.linearThresholdDiffusion(seed);
		System.out.println("|V(G)| = "+g.vertexSet().size());
		System.out.println("|A| = "+ativos.size());
//		g.visualize();
	}
}
