package interfaces;

import grafos.Actor;

import java.util.HashSet;

public interface SeedChooser<V extends Activatable> {
	
	public HashSet<V> escolher(int k);

}
