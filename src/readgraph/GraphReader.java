package readgraph;

import grafos.Actor;
import grafos.DirectedSocialNetwork;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jgrapht.graph.DefaultWeightedEdge;

import plot.Histograma;

public class GraphReader {

	public DirectedSocialNetwork run(int n, int m, String arquivo) {

		Actor[] vertices = new Actor[n];
		DirectedSocialNetwork g;
		g = new DirectedSocialNetwork(DefaultWeightedEdge.class);

		try {
			FileInputStream fstream = new FileInputStream(arquivo);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {

				String[] str = null;
				if (strLine.contains(" ")) {
					str = strLine.split(" ");
				} else if (strLine.contains("\t")) {
					str = strLine.split("\t");
				}

				int[] values = new int[str.length];
				for (int i = 0; i < str.length; i++) {
					values[i] = Integer.valueOf(str[i]);
				}

				// Cria n vertices e atribui limitante aleatórios
				if (values[0] == n && values[1] == m) {
					for (int i = 0; i < values[0]; i++) {
						Actor v = new Actor(i);
						//
						v.setThreshold(Math.random());
						g.addVertex(v);
						vertices[i] = v;
					}
					continue;
				}

				if (values[0] >= 0 && values[0] <= n && values[1] >= 0
						&& values[1] <= n) {
					// Adiciona uma aresta e, caso já exista e = null
					int v1 = values[0];
					int v2 = values[1];
					if (v1 != v2) { // Não permite laços
						DefaultWeightedEdge e;
						e = g.addEdge(vertices[v1], vertices[v2]);
						if (e != null) {
							g.setEdgeWeight(e, peso());
						}
					}
				}
			}

			in.close();
		} catch (Exception e) {
			System.err.println("Erro ao ler o arquivo: " + e.getMessage());
		}

		return g;
	}

	/**
	 * Sorteia um valor aleatório
	 */
	public double peso() {

		// int p = (int) (Math.random() * 3);
		// double[] choice = { 0.2, 0.04, 0.008 };
		// return choice[p];

		// uniform IC model
//		 return (double)10/100;
//		return 0.025;
		return Math.random()/4;
//		return 0.0025;
	}

	public DirectedSocialNetwork readEpinions() {
		int n = 75888;
		int m = 508837;
		String arquivo = "data/soc-Epinions1.txt";
		return run(n, m, arquivo);
	}

	/**
	 * Hep.txt – A collaboration graph crawled from arXiv.org, High Energy
	 * Physics – Theory section, from year 1991 to year 2003.
	 **/
	public DirectedSocialNetwork readHep() {
		int n = 15233;
		int m = 58891;
		String arquivo = "data/hep.txt";
		return run(n, m, arquivo);
	}

	/**
	 * Phy.txt – A collaboration graph crawled from arXiv.org, Physics section.
	 */
	public DirectedSocialNetwork readPhy() {
		int n = 37154;
		int m = 231584;
		String arquivo = "data/phy.txt";
		return run(n, m, arquivo);
	}

	public DirectedSocialNetwork readDblp() {
		int n = 654628;
		int m = 1990259;
		String arquivo = "data/dblp.txt";
		return run(n, m, arquivo);
	}

	public static void main(String args[]) {
		GraphReader reader = new GraphReader();
		DirectedSocialNetwork g;
		g = reader.readEpinions();
		// g = reader.readHep();

		Histograma histograma = new Histograma();

		int[] h = histograma.gerarHistograma(g);
		try {
			histograma.plotarGraficos(h);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("|V(G)| = " + g.vertexSet().size());
		System.out.println("|E(G)| = " + g.edgeSet().size());
		System.out.println("Gráficos do histograma gerados!");
	}
}
