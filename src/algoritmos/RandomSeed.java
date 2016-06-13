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
	public DirectedSocialNetwork grafo = null;
	private double[] spreadData; // histograma da propagação esperada

	public RandomSeed(DirectedSocialNetwork grafo) {
		this.grafo = grafo;
	}

	@Override
	public HashSet<Actor> escolher(int k) {		
		HashSet<Actor> semente = new HashSet<>();		
		Set<Actor> vSet = grafo.vertexSet();
		spreadData = new double[k+1];

		Actor[] vertices = new Actor[vSet.size()];
		int i = 0;
		for (Actor a : vSet)
			vertices[i++] = a;

		Random rand = new Random();
		Actor sorteado = vertices[rand.nextInt(vertices.length)];
		while (semente.size() < k) {
			if (!semente.contains(sorteado)) {
				semente.add(sorteado);
				spreadData[semente.size()] = grafo.espectedSpread(semente, true);
			}
			sorteado = vertices[rand.nextInt(vertices.length)];
		}

		return semente;
	}
	
	public double[] getSpreadData(){
		return this.spreadData;
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
