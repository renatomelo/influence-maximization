package util;

import geradores.GeradorGrafoPowerLaw;
import grafos.Actor;
import grafos.DirectedSocialNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.event.TraversalListenerAdapter;
import org.jgrapht.event.VertexSetListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.graph.DirectedWeightedSubgraph;
import org.jgrapht.graph.UndirectedSubgraph;
import org.jgrapht.graph.UndirectedWeightedSubgraph;
import org.jgrapht.traverse.BreadthFirstIterator;

public class ComponentesConexos<V, E> implements GraphListener<V, E> {

	List<Set<V>> connectedSets;
	Map<V, Set<V>> vertexToConnectedSet;
	private Graph<V, E> graph;
	List<Set<V>> componentes;

	/**
	 * Creates a connectivity inspector for the specified undirected graph.
	 * 
	 * @param g
	 *            the graph for which a connectivity inspector to be created.
	 */
	public ComponentesConexos(Graph<V, E> g) {
		init();
		this.graph = g;
		componentes = this.connectedSets();
	}

	/**
	 * Test if the inspected graph is connected. An empty graph is <i>not</i>
	 * considered connected.
	 * 
	 * @return <code>true</code> if and only if inspected graph is connected.
	 */
	public boolean isGraphConnected() {
		return lazyFindConnectedSets().size() == 1;
	}

	/**
	 * Returns a set of all vertices that are in the maximally connected
	 * component together with the specified vertex. For more on maximally
	 * connected component, see <a
	 * href="http://www.nist.gov/dads/HTML/maximallyConnectedComponent.html">
	 * http://www.nist.gov/dads/HTML/maximallyConnectedComponent.html</a>.
	 * 
	 * @param vertex
	 *            the vertex for which the connected set to be returned.
	 * 
	 * @return a set of all vertices that are in the maximally connected
	 *         component together with the specified vertex.
	 */
	public Set<V> connectedSetOf(V vertex) {
		Set<V> connectedSet = vertexToConnectedSet.get(vertex);

		if (connectedSet == null) {
			connectedSet = new HashSet<V>();

			BreadthFirstIterator<V, E> i = new BreadthFirstIterator<V, E>(
					graph, vertex);

			while (i.hasNext()) {
				connectedSet.add(i.next());
			}

			vertexToConnectedSet.put(vertex, connectedSet);
		}

		return connectedSet;
	}

	/**
	 * Returns a list of <code>Set</code> s, where each set contains all
	 * vertices that are in the same maximally connected component. All graph
	 * vertices occur in exactly one set. For more on maximally connected
	 * component, see <a
	 * href="http://www.nist.gov/dads/HTML/maximallyConnectedComponent.html">
	 * http://www.nist.gov/dads/HTML/maximallyConnectedComponent.html</a>.
	 * 
	 * @return Returns a list of <code>Set</code> s, where each set contains all
	 *         vertices that are in the same maximally connected component.
	 */
	public List<Set<V>> connectedSets() {
		return lazyFindConnectedSets();
	}

	/**
	 * @see GraphListener#edgeAdded(GraphEdgeChangeEvent)
	 */
	@Override
	public void edgeAdded(GraphEdgeChangeEvent<V, E> e) {
		init(); // for now invalidate cached results, in the future need to
				// amend them.
	}

	/**
	 * @see GraphListener#edgeRemoved(GraphEdgeChangeEvent)
	 */
	@Override
	public void edgeRemoved(GraphEdgeChangeEvent<V, E> e) {
		init(); // for now invalidate cached results, in the future need to
				// amend them.
	}

	/**
	 * Tests if there is a path from the specified source vertex to the
	 * specified target vertices. For a directed graph, direction is ignored for
	 * this interpretation of path.
	 * 
	 * <p>
	 * Note: Future versions of this method might not ignore edge directions for
	 * directed graphs.
	 * </p>
	 * 
	 * @param sourceVertex
	 *            one end of the path.
	 * @param targetVertex
	 *            another end of the path.
	 * 
	 * @return <code>true</code> if and only if there is a path from the source
	 *         vertex to the target vertex.
	 */
	public boolean pathExists(V sourceVertex, V targetVertex) {
		/*
		 * Ignoring edge direction for directed graph may be confusing. For
		 * directed graphs, consider Dijkstra's algorithm.
		 */
		Set<V> sourceSet = connectedSetOf(sourceVertex);

		return sourceSet.contains(targetVertex);
	}

	/**
	 * @see VertexSetListener#vertexAdded(GraphVertexChangeEvent)
	 */
	@Override
	public void vertexAdded(GraphVertexChangeEvent<V> e) {
		init(); // for now invalidate cached results, in the future need to
				// amend them.
	}

	/**
	 * @see VertexSetListener#vertexRemoved(GraphVertexChangeEvent)
	 */
	@Override
	public void vertexRemoved(GraphVertexChangeEvent<V> e) {
		init(); // for now invalidate cached results, in the future need to
				// amend them.
	}

	private void init() {
		connectedSets = null;
		vertexToConnectedSet = new HashMap<V, Set<V>>();
	}

	private List<Set<V>> lazyFindConnectedSets() {
		if (connectedSets == null) {
			connectedSets = new ArrayList<Set<V>>();

			Set<V> vertexSet = graph.vertexSet();

			if (vertexSet.size() > 0) {
				BreadthFirstIterator<V, E> i = new BreadthFirstIterator<V, E>(
						graph, null);
				i.addTraversalListener(new MyTraversalListener());

				while (i.hasNext()) {
					i.next();
				}
			}
		}

		return connectedSets;
	}

	/**
	 * A traversal listener that groups all vertices according to to their
	 * containing connected set.
	 * 
	 * @author Barak Naveh
	 * @since Aug 6, 2003
	 */
	private class MyTraversalListener extends TraversalListenerAdapter<V, E> {
		private Set<V> currentConnectedSet;

		/**
		 * @see TraversalListenerAdapter#connectedComponentFinished(ConnectedComponentTraversalEvent)
		 */
		@Override
		public void connectedComponentFinished(
				ConnectedComponentTraversalEvent e) {
			connectedSets.add(currentConnectedSet);
		}

		/**
		 * @see TraversalListenerAdapter#connectedComponentStarted(ConnectedComponentTraversalEvent)
		 */
		@Override
		public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
			currentConnectedSet = new HashSet<V>();
		}

		/**
		 * @see TraversalListenerAdapter#vertexTraversed(VertexTraversalEvent)
		 */
		@Override
		public void vertexTraversed(VertexTraversalEvent<V> e) {
			V v = e.getVertex();
			currentConnectedSet.add(v);
			vertexToConnectedSet.put(v, currentConnectedSet);
		}
	}

	public int nComponentes() {
		return componentes.size();
	}

	public List<Set<V>> getComponentes() {
		return componentes;
	}

	public Graph<V, E> getComponenteGigante(boolean compeso, boolean direcionado) {
		Set<V> vertexSubset = Collections.max(componentes,
				new Comparator<Set<V>>() {
					public int compare(Set<V> x, Set<V> y) {
						if (x.size() > y.size())
							return 1;
						if (x.size() < y.size())
							return -1;
						return 0;
					}
				});

		int i = 0;
		for (V v : vertexSubset) {
			if (v instanceof Actor) {
				((Actor) v).setIndex(i++);
			}
		}

		if (compeso) {
			if (direcionado) {
				DirectedWeightedSubgraph<V, E> sub;
				sub = new DirectedWeightedSubgraph<>(
						(WeightedGraph<V, E>) graph, vertexSubset,
						graph.edgeSet());
				return sub;
			} else {
				UndirectedWeightedSubgraph<V, E> sub2;
				sub2 = new UndirectedWeightedSubgraph<>(
						(WeightedGraph<V, E>) graph, vertexSubset,
						graph.edgeSet());
				return sub2;
			}
		}
		if (direcionado) {
			DirectedSubgraph<V, E> sub2;
			sub2 = new DirectedSubgraph<>((DirectedGraph<V, E>) graph,
					vertexSubset, graph.edgeSet());
			return sub2;
		} else {
			UndirectedSubgraph<V, E> sub;
			sub = new UndirectedSubgraph<V, E>((UndirectedGraph<V, E>) graph,
					vertexSubset, graph.edgeSet());
			return sub;
		}
	}

	public static <V, E> void main(String[] args) {
		WeightedGraph<Actor, DefaultWeightedEdge> g = new GeradorGrafoPowerLaw()
				.gerarComPesos(100, 3, true);

		ComponentesConexos<Actor, DefaultWeightedEdge> gg;
		gg = new ComponentesConexos<>(g);

		Graph<Actor, DefaultWeightedEdge> result = gg.getComponenteGigante(
				true, true);
		DirectedSocialNetwork rede = new DirectedSocialNetwork(
				DefaultWeightedEdge.class);
		Set<Actor> vertices = result.vertexSet();
		for (Actor a : vertices) {
			rede.addVertex(a);
		}
		
		Set<DefaultWeightedEdge> arestas = result.edgeSet();
		for (DefaultWeightedEdge e : arestas) {
			rede.addEdge(result.getEdgeSource(e), result.getEdgeTarget(e), e);
		}
		
		rede.visualize();
	}
}
