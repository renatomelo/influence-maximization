package plot;

import org.leores.util.DataTableSet;

public class MeuPlot extends JGnuplot {
	//windows terminal is only available to windows. You might get error output from gnuplot if you are not using windows.
//	String terminal = "windows enhanced dashed title 'id=100 hello there' size 600,600";
	String terminal = "windows enhanced dashed title 'id=100 hello there'";
	String output = null;
	//String beforeStyle="linewidth=4";
	
	public void plotarPropagacaoEsperada(double[] tamSemente, double[] y1, double[] y2, double[] y3, double[] y4, double[] y5){
		JGnuplot plot = new JGnuplot();
		Plot plotPropagacao = new Plot("Propagação Esperada") {
			String xlabel = "'Tam. Semente'", ylabel = "'Propagacao Esperada'";
		};
		
		DataTableSet dts = plotPropagacao.addNewDataTableSet("Propagacao");
		dts.addNewDataTable("HighDegree", tamSemente, y1);
		dts.addNewDataTable("BestNeighbors", tamSemente, y2);
		dts.addNewDataTable("RandomSeed", tamSemente, y3);
		dts.addNewDataTable("DominatingSeed", tamSemente, y4);
		dts.addNewDataTable("CELF", tamSemente, y5);
		
		plotPropagacao.add(dts);
		plot.compile(plotPropagacao, plot.plot2d, "propagacao_esperada.plt");
		
		this.terminal = "pngcairo enhanced dashed";
		this.output = "'plots/IC/propagacao_esperada.png'";
		this.execute(plotPropagacao, this.plot2d);
	}
	
	public void plotarPropagacaoEsperadaLT(double[] tamSemente, double[] y1, double[] y2, double[] y3, double[] y4){
		JGnuplot plot = new JGnuplot();
		Plot plotPropagacao = new Plot("Propagação Esperada") {
			String xlabel = "'Tam. Semente'", ylabel = "'Propagacao Esperada'";
		};
		
		DataTableSet dts = plotPropagacao.addNewDataTableSet("Propagacao");
		dts.addNewDataTable("HighDegree", tamSemente, y1);
		dts.addNewDataTable("BestNeighbors", tamSemente, y2);
		dts.addNewDataTable("RandomSeed", tamSemente, y3);
//		dts.addNewDataTable("DominatingSeed", tamSemente, y4);
		dts.addNewDataTable("CELF", tamSemente, y4);
		
		plotPropagacao.add(dts);
		plot.compile(plotPropagacao, plot.plot2d, "propagacao_esperada.plt");
		
		this.terminal = "pngcairo enhanced dashed";
		this.output = "'plots/LT/propagacao_esperada.png'";
		this.execute(plotPropagacao, this.plot2d);
	}
	
	public void plotarTempoExecucao(double[] tamSemente, double[] y1, double[] y2, double[] y3, double[] y4, double[] y5){
		JGnuplot plot = new JGnuplot();
		Plot plotTempo = new Plot("Tempo de execução") {
			String xlabel = "'Tam. Semente'", ylabel = "'Tempo de execução'";
		};		
		
		DataTableSet dts2 = plotTempo.addNewDataTableSet("TempoExecucao");
		dts2.addNewDataTable("HighDegree", tamSemente, y1);
		dts2.addNewDataTable("BestNeighbors", tamSemente, y2);
		dts2.addNewDataTable("RandomSeed", tamSemente, y3);
		dts2.addNewDataTable("DominatingSeed", tamSemente, y4);
		dts2.addNewDataTable("CELF", tamSemente, y5);
		plotTempo.add(dts2);		
		plot.compile(plotTempo, plot.plot2d, "tempo_execucao.plt");
		
		this.terminal = "pngcairo enhanced dashed";
		this.output = "'plots/IC/tempo_execucao.png'";
		this.execute(plotTempo, this.plot2d);
	}
	
	public void plotarTempoExecucaoLT(double[] tamSemente, double[] y1, double[] y2, double[] y3, double[] y4){
		JGnuplot plot = new JGnuplot();
		Plot plotTempo = new Plot("Tempo de execução") {
			String xlabel = "'Tam. Semente'", ylabel = "'Tempo de execução'";
		};		
		
		DataTableSet dts2 = plotTempo.addNewDataTableSet("TempoExecucao");
		dts2.addNewDataTable("HighDegree", tamSemente, y1);
		dts2.addNewDataTable("BestNeighbors", tamSemente, y2);
		dts2.addNewDataTable("RandomSeed", tamSemente, y3);
//		dts2.addNewDataTable("DominatingSeed", tamSemente, y4);
		dts2.addNewDataTable("CELF", tamSemente, y4);
		plotTempo.add(dts2);		
		plot.compile(plotTempo, plot.plot2d, "tempo_execucao.plt");
		
//		MeuPlot jg2 = new MeuPlot();
		this.terminal = "pngcairo enhanced dashed";
		this.output = "'plots/LT/tempo_execucao.png'";
		this.execute(plotTempo, this.plot2d);
	}
}
