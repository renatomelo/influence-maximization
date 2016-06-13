package simulacao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import algoritmos.HightDegree;
import algoritmos.LazyGreedy;
import algoritmos.PrevalentSeed;
import algoritmos.RandomSeed;
import geradores.SocialNetworkGenerate;
import grafos.DirectedSocialNetwork;
import plot.MeuPlot;

public class Experimentacao {

	public static void main(String[] args) {
		int k = 50;
		grafosSinteticos(k);
	}

	private static void grafosSinteticos(int k) {
		double[] spreadData1 = new double[k+1], tempo1 = new double[5];
		double[] spreadData2 = new double[k+1], tempo2 = new double[5];
		double[] spreadData3 = new double[k+1], tempo3 = new double[5];
		double[] spreadData4 = new double[k+1], tempo4 = new double[5];
		double[] x = eixox(k + 1); // preenche o vetor do eixo x
		double[] sigmaCalls = new double [k+1];
		double[] sigmaCalls2 = new double [k+1];

		DirectedSocialNetwork g = new SocialNetworkGenerate().gerarGrafo(200, 2.5);
		mostrarTamanho(g);

		LazyGreedy celf = new LazyGreedy(g);
		System.out.println("#Celf");
		celf.escolher(k);
		spreadData2 = celf.getSpreadData();
		sigmaCalls2 = celf.getCallData();
		for (int i = 0; i < spreadData2.length; i++) {
			System.out.println(i + "\t" + spreadData2[i]);
		}

		PrevalentSeed ps = new PrevalentSeed(g);
		System.out.println("#PrevalentSeed");
		ps.escolher(k);
		spreadData1 = ps.getSpreadData();
		sigmaCalls = ps.getCallData();
		for (int i = 0; i < spreadData1.length; i++) {
			System.out.println(i + "\t" + spreadData1[i]);
		}

		// Testa a heuristica de grau
		HightDegree hd = new HightDegree(g);
		System.out.println("#HightDegree");
		hd.escolher(k);
		spreadData3 = hd.getSpreadData();
		for (int i = 0; i < spreadData3.length; i++) {
			System.out.println(i + "\t" + spreadData3[i]);
		}

		// Testa a heurística semente aleatória
		RandomSeed rs = new RandomSeed(g);
		System.out.println("#RandomSeed");
		rs.escolher(k);
		spreadData4 = rs.getSpreadData();
		for (int i = 0; i < spreadData4.length; i++) {
			System.out.println(i + "\t" + spreadData4[i]);
		}
		
		MeuPlot meuplot = new MeuPlot();
		meuplot.plotPropagacao(x, spreadData1, spreadData2, spreadData3, spreadData4,
				"'");
		meuplot.plotChamadas(x, sigmaCalls, sigmaCalls2, "'");
		
	}

	/**
	 * Preenche o vetor para o eixo x do grafico a ser plotado no intervalo
	 * [0..50]
	 * 
	 * @param l: tamanho do vetor
	 * @return o vetor preenchido
	 */
	private static double[] eixox(int l) {
		double[] x = new double[l];
		for (int j = 0; j < x.length; j++) {
			x[j] = j;
		}
		return x;
	}

	/**
	 * Salva os resultados em arquivos de log
	 * 
	 * @param g
	 *            uma rede social direcionada
	 * @param k
	 *            um inteiro
	 * @throws IOException
	 */
	private static void lazyGreedyToFile(DirectedSocialNetwork g, int k) throws IOException {
		File sigma1 = new File("sigma1.txt");
		sigma1.createNewFile();
		FileWriter writer = new FileWriter(sigma1);

		LazyGreedy celf = new LazyGreedy(g);
		writer.write("#Celf\n");
		celf.toFile(k, writer);

		writer.flush();
		writer.close();
	}

	private static void mostrarTamanho(DirectedSocialNetwork g) {
		System.out.println("Grafo direcionado: |V| = " + g.vertexSet().size() + " |E| = " + g.edgeSet().size());
	}

}
