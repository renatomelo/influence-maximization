package algoritmos;

import grafos.Actor;

/**
 * Data structure for the lazy greedy optimization. It stores validity
 * information and gain. Since java priority queue is a min heap and we need
 * a max heap, we implement the comareTo() method accordingly.
 * 
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author 	ksarkar1, Renato Melo
 */

public class MarginalGain implements Comparable<MarginalGain> {
	private Actor vertice;
	private double gain;
	private boolean isvalid;

	public MarginalGain(Actor vertice, double nodeGain, boolean isValid) {
		this.vertice = vertice;
		this.gain = nodeGain;
		this.isvalid = isValid;
	}

	public Actor getVertice() {
		return vertice;
	}

	public double getGain() {
		return gain;
	}

	public boolean isValid() {
		return isvalid;
	}

	public void setGain(double gain) {
		this.gain = gain;
	}

	public void setValid(boolean isValid) {
		this.isvalid = isValid;
	}

	/**
	 * Greater node gain value is smaller in the ordering
	 */

	@Override
	public int compareTo(MarginalGain o) {
		return -(Double.compare(this.getGain(), o.getGain()));
	}
}