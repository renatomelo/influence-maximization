package grafos;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import interfaces.NetworkOfActivatables;

public class SimpleSocialNetwork extends SimpleWeightedGraph<Actor, DefaultWeightedEdge> implements
		NetworkOfActivatables<Actor> {

	private static final long serialVersionUID = 56231525188689857L;

	/**
	 * Flag which shows if there has been an active actor during the history of
	 * this network. In particular, if it is false, no actor is active.
	 */
	protected boolean active;

	public SimpleSocialNetwork(EdgeFactory<Actor, DefaultWeightedEdge> ef) {
		super(ef);
		this.active = false;
	}

	/**
	 * Creates a social network of the specified actors and their directed and
	 * weighted relations determined by the specified weight matrix.
	 */
	public SimpleSocialNetwork(Class<? extends DefaultWeightedEdge> edgeClass) {
		this(new ClassBasedEdgeFactory<Actor, DefaultWeightedEdge>(edgeClass));
		this.active = false;
	}

	/**
	 * Returns at least one of the nodes has been active during the history of
	 * this network.
	 * 
	 * @return true if and only if a node of this network has been active
	 */
	@Override
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets this network as active, i.e., it has had at least one of its nodes
	 * been active during the history of this network.
	 * 
	 * @param active
	 *            true if and only if a node of this network has been active
	 */
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Activates all actives specified by the input actors, i.e., marks each of
	 * them as active. This method is a convenience routine to simplify
	 * activation, for instance of innovators. Note that this method changes the
	 * state of the input actors (call by reference), no matter whether the
	 * returned set is stored by the invoking process or not.
	 * 
	 * @param actors
	 *            a set actors of this network to be activated
	 * @return the set actors each of whom is activated
	 */
	public HashSet<Actor> activate(Actor... actors) {
		for (Actor a : actors) {
			a.setActive(true);
		}

		if (actors.length > 0)
			this.active = true;

		HashSet<Actor> activated = new HashSet<>();
		java.util.Collections.addAll(activated, actors);
		return activated;
	}

	/**
	 * Activates all actives specified by the input set of actors, i.e., marks
	 * each of them as active. This method is a convenience routine to simplify
	 * activation, for instance of innovators. Note that this method changes the
	 * state of the input actors (call by reference), no matter whether the
	 * returned set is stored by the invoking process or not.
	 * 
	 * @param actors
	 *            a set actors of this network to be activated
	 * @return the set actors each of whom is activated
	 */
	public HashSet<Actor> activate(java.util.Collection<Actor> actors) {
		for (Actor a : actors) {
			a.setActive(true);
		}
		if (actors.size() > 0)
			this.active = true;
		return new HashSet<>(actors);
	}

	/**
	 * Deactivates all actors specified by the input actors, i.e., marks each of
	 * them as inactive. This method is a convenience routine to simplify
	 * deactivation. Note that this method changes the state of the input actors
	 * (call by reference), no matter whether the returned set is stored by the
	 * invoking process or not.
	 * 
	 * @param actors
	 *            a set actors of this network to be activated
	 * @return the set actors each of whom is activated
	 */
	public HashSet<Actor> deactivate(Actor... actors) {
		for (Actor a : actors) {
			a.setActive(false);
		}
		HashSet<Actor> deactivated = new HashSet<>();
		java.util.Collections.addAll(deactivated, actors);
		return deactivated;
	}

	/**
	 * Deactivates all actors specified by the input set of actors, i.e., marks
	 * each of them as inactive. This method is a convenience routine to
	 * simplify deactivation. Note that this method changes the state of the
	 * input actors (call by reference), no matter whether the returned set is
	 * stored by the invoking process or not.
	 * 
	 * @param actors
	 *            a set actors of this network to be activated
	 * @return the set actors each of whom is activated
	 */
	public HashSet<Actor> deactivate(java.util.Collection<Actor> actors) {
		for (Actor a : actors) {
			a.setActive(false);
		}
		return new HashSet<>(actors);
	}
	
	public HashSet<Actor> neighborsOf(Actor v) {
		HashSet<Actor> adj = new HashSet<>();
		Set<DefaultWeightedEdge> edges = edgesOf(v);
		for (DefaultWeightedEdge e : edges) {
			if (v.equals(getEdgeSource(e))) {
				adj.add(getEdgeTarget(e));
			}else{
				adj.add(getEdgeSource(e));
			}
		}
		return adj;
	}
	
	public static void main(String[] args) {
		SimpleSocialNetwork g = new SimpleSocialNetwork(DefaultWeightedEdge.class);

		Actor odp = new Actor(1);
		Actor cck = new Actor(2);
		Actor mfe = new Actor(3);

		g.addVertex(odp);
		g.addVertex(cck);
		g.addVertex(mfe);

		DefaultWeightedEdge e1 = g.addEdge(odp, cck);
		DefaultWeightedEdge e2 = g.addEdge(odp, mfe);

		g.setEdgeWeight(e1, 10);
		g.setEdgeWeight(e2, 4);
		
		for (Actor v : g.vertexSet()) {
			Set<Actor> adj = g.neighborsOf(v);
			System.out.print("\n"+v.toString()+": ");
			for (Actor a : adj) {
				System.out.print(" "+a.toString());
			}
		}
		System.out.println();
		System.out.println(g.toString());
	}

	@Override
	public HashSet<Actor> linearThresholdDiffusion(HashSet<Actor> initiators) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashSet<Actor> indepCascadeDiffusion(HashSet<Actor> initiators) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double espectedSpread(HashSet<Actor> seed, boolean ic) {
		// TODO Auto-generated method stub
		return 0;
	}
}