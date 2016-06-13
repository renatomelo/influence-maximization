package algoritmos;

import geradores.SocialNetworkGenerate;
import grafos.Actor;
import grafos.DirectedSocialNetwork;
import interfaces.SeedChooser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.PriorityQueue;

public class LazyGreedy implements SeedChooser<Actor> {
	private DirectedSocialNetwork grafo = null;
	private int cont;
	private double[] spreadData;
	private double[] callData;
	

	public LazyGreedy(DirectedSocialNetwork g) {
		this.grafo = g;
	}

	/**
	 * Cria uma fila de prioridade por ganho marginal de todos os vértices do grafo
	 * e cada vértice é inicializado com ganho de NaN e inválido
	 * 
	 * @return uma fila de prioridade com o ganho marginal de todos os vértices
	 */
	private PriorityQueue<MarginalGain> priorityQueueOfGains() {

		PriorityQueue<MarginalGain> fila = new PriorityQueue<MarginalGain>();

		for (Actor v : grafo.vertexSet()) {
			fila.add(new MarginalGain(v, Double.NaN, false));
		}

		return fila;
	}

	@Override
	public HashSet<Actor> escolher(int k) {
		HashSet<Actor> semente = new HashSet<>();
		cont = 0;
		spreadData = new double[k+1];
		callData = new double[k+1];

		// create priority queue of all nodes, with marginal gain delta +inf
		PriorityQueue<MarginalGain> fila = priorityQueueOfGains();

		double MaxSpread = 0; 

		while (semente.size() < k) {
			// set all gains invalid
			for (MarginalGain mg : fila) {
				mg.setValid(false);
			}

			while (true) {
				MarginalGain max = fila.poll();
				if (max.isValid() == true) {
					semente.add(max.getVertice());
					MaxSpread = MaxSpread + max.getGain();
//					System.out.println(semente.size()+"\t"+MaxSpread);
					spreadData[semente.size()] = MaxSpread;
					callData[semente.size()] = cont;
					break;
				} else {
					double sigma = cascata(max.getVertice(), semente);
					double delta = sigma - MaxSpread;
					max.setGain(delta);
					max.setValid(true);
					fila.add(max);
					cont++;
				}
			}
		}
		System.out.println("chamadas: " + cont);
		return semente;
	}
	
	public int callToSigma(){
		return this.cont;
	}
	public double[] getSpreadData(){
		return this.spreadData;
	}
	
	public double[] getCallData(){
		return this.callData;
	}
	
	public HashSet<Actor> toFile(int k, FileWriter writer) throws IOException {
		HashSet<Actor> semente = new HashSet<>();
		cont = 0;

		// create priority queue of all nodes, with marginal gain delta +inf
		PriorityQueue<MarginalGain> fila = priorityQueueOfGains();

		double MaxSpread = 0; 

		while (semente.size() < k) {
			// set all gains invalid
			for (MarginalGain mg : fila) {
				mg.setValid(false);
			}

			while (true) {
				MarginalGain max = fila.poll();
				if (max.isValid() == true) {
					semente.add(max.getVertice());
					MaxSpread = MaxSpread + max.getGain();
					writer.write(semente.size()+"\t"+MaxSpread);
					break;
				} else {
					double sigma = cascata(max.getVertice(), semente);
					double delta = sigma - MaxSpread;
					max.setGain(delta);
					max.setValid(true);
					fila.add(max);
					cont++;
				}
			}
		}
//		System.out.println("chamadas: " + cont);
		return semente;
	}

	private double cascata(Actor v, HashSet<Actor> semente) {
		HashSet<Actor> ativados = new HashSet<>(semente);
		ativados.add(v);

		return grafo.espectedSpread(ativados, true);
	}

	public static void main(String[] args) {
		DirectedSocialNetwork g;
		g = new SocialNetworkGenerate().gerarGrafo(2000, 2.5);
		long startTime = 0;

		startTime = System.nanoTime();
		HashSet<Actor> seed2 = new LazyGreedy(g).escolher(15);
		System.out.println("LazyGreedy = "
				+ g.espectedSpread(seed2, true) + ", tempo: "
				+ (System.nanoTime() - startTime) / 1000);

		startTime = System.nanoTime();
		HashSet<Actor> seed1 = new GreedyIC(g).escolher(10);
		System.out.println("GeneralGreedy = " + g.espectedSpread(seed1, true)
				+ ", tempo: " + (System.nanoTime() - startTime) / 1000);
	}

}
