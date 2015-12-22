package algoritmos;

import java.util.HashSet;

import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;

public class ArticulationSeed implements SeedChooser<Actor> {
	private DirectedSocialNetwork grafo = null;
	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<>();
		while (semente.size() < k) {
			
		}
		return null;
	}

}
