package util;

import grafos.Actor;
import grafos.DirectedSocialNetwork;

import java.util.Comparator;

/*
 * Compara os vertices por grau de saída
*/
public class ComparaPorGrau implements Comparator<Actor> {
	DirectedSocialNetwork graph;
	private boolean ascendingOrder;

	public ComparaPorGrau(DirectedSocialNetwork g) {
		this.graph = g;
		this.ascendingOrder = true;
	}

	@Override
	public int compare(Actor x, Actor y) {
		
		int degree1 = graph.outDegreeOf(x);
		int degree2 = graph.outDegreeOf(y);

		if (((degree1 < degree2) && ascendingOrder)
				|| ((degree1 > degree2) && !ascendingOrder)) {
			return -1;
		} else if (((degree1 > degree2) && ascendingOrder)
				|| ((degree1 < degree2) && !ascendingOrder)) {
			return 1;
		} else {
			return 0;
		}
	}



}
