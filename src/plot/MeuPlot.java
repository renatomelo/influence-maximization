package plot;

import org.leores.util.data.DataTable;
import org.leores.util.data.DataTableSet;

public class MeuPlot extends JGnuplot {
	//windows terminal is only available to windows. You might get error output from gnuplot if you are not using windows.
//	String terminal = "windows enhanced dashed title 'id=100 hello there' size 600,600";
//	String terminal = "windows enhanced";
//	String output = null;
//	String beforeStyle="linewidth=4";
	
//	public void plotPropagacao(double[] x, double[] y1, double[] y2, double[] y3, double[] y4, String outDir){
//		double[] x2 = suavisalinhas(x);
//		double[] z1 = suavisalinhas(y1);
//		double[] z2 = suavisalinhas(y2);
//		double[] z3 = suavisalinhas(y3);
//		double[] z4 = suavisalinhas(y4);
//		
//		JGnuplot plot = new JGnuplot();
//		Plot plotPropagacao = new Plot("Propagação Esperada") {
//			{
//				xlabel = "'|S|'";
//				ylabel = "'sigma(S)'";
//				extra = "set key top left";
//			}
//		};
//		
//		DataTableSet dts = plotPropagacao.addNewDataTableSet("Propagação Esperada");
//		dts.addNewDataTable("PrevalentSeed", x2, z1);
//		dts.addNewDataTable("CELF", x2, z2);
//		dts.addNewDataTable("HighDegree", x2, z3);
//		dts.addNewDataTable("RandomSeed", x2, z4);
//		
//		plotPropagacao.add(dts);
//		String saida = outDir+"propagacao_esperada.plt";
//		plot.compile(plotPropagacao, plot.plot2d, saida);
//		
////		this.terminal = "epslatex color colortext dashed";
////		this.output = outDir+"propagacao.tex'";
//		this.terminal = "eps color dashed";
//		this.output = outDir+"propagacao.eps";
//		this.execute(plotPropagacao, this.plot2d);
//	}
	
	public void plotPropagacao(double[] x, double[] y1, double[] y2, String outDir){
		double[] x2 = suavisalinhas(x);
		double[] z1 = suavisalinhas(y1);
		double[] z2 = suavisalinhas(y2);
		
		JGnuplot plot = new JGnuplot();
		Plot plotPropagacao = new Plot("Propagação Esperada") {
			{
				xlabel = "'|S|'";
				ylabel = "'sigma(S)'";
				extra = "set key top left";
			}
		};
		
		DataTableSet dts = plotPropagacao.addNewDataTableSet("Propagação Esperada");
		dts.addNewDataTable("PrevalentSeed", x2, z1);
		dts.addNewDataTable("CELF", x2, z2);
		
		plotPropagacao.add(dts);
		String saida = outDir+"propagacao_esperada.plt";
		plot.compile(plotPropagacao, plot.plot2d, saida);
		
//		this.terminal = "epslatex color colortext dashed";
//		this.output = outDir+"propagacao.tex'";
		this.terminal = "eps color dashed";
		this.output = outDir+"propagacao.eps";
		this.execute(plotPropagacao, this.plot2d);
	}
	
	/**
	 * Reduz um vetor x para 1/5 do tamanho original
	 * @param x[]: vetor do tipo double
	 * @return x2[]: vetor do tipo double
	 */
	private double[] suavisalinhas(double[] x) {
		double[] x2 = new double[x.length/5 + 1];
		for (int i = 0; i < x2.length; i++) {
			x2[i] = x[i*5];
		}
		return x2;
	}

	public void plotChamadas(double[] y1, double[] y2, String outDir){
	        JGnuplot jg = new JGnuplot();
	        Plot plot = new Plot("") {
	            {
	                xlabel = "x";
	                ylabel = "y";
	                yrange = "[0:15]";
	                extra2 = "set key top left";
	            }
	        };
	        double[] x = { 1, 2, 3, 4, 5 };
	        DataTableSet dts = plot.addNewDataTableSet("2D Bar");
	        DataTable dt = dts.addNewDataTable("", x, y1, y2);
	        dt.insert(0, new String[] { "", "y1=2x", "y2=3x" });
	        jg.execute(plot, jg.plot2dBar);
		
		
//		JGnuplot plot = new JGnuplot();
//		Plot p = new Plot("Chamadas à sigma") {
//			String xlabel = "'|S|'", ylabel = "'n. chamadas'";
//			String extra = "set key top left";
//		};
//		
//		DataTableSet dts = p.addNewDataTableSet("Chamadas à sigma");
//		dts.addNewDataTable("PrevalentSeed", x, y1);
//		dts.addNewDataTable("CELF", x, y2);
//		
//		p.add(dts);
//		plot.compile(p, plot.plot2d, "chamadas.plt");
//		
////		this.terminal = "epslatex color colortext dashed";
//		this.terminal = "eps color dashed";
////		this.output = outDir+"chamadas.tex'";
//		this.output = outDir+"chamadas.eps'";
//		this.execute(p, this.plot2d);
	}
	
	public void plotTempoExecucao(double[] tamSemente, double[] y1, double[] y2, double[] y3, double[] y4, String outDir){
		JGnuplot plot = new JGnuplot();
		
		Plot plotTempo = new Plot("Tempo de execução"){
			{
				xlabel = "'|S|'";
				ylabel = "'Tempo de execução'";
				extra = "set key top left";
			}
		};		
		
		DataTableSet dts2 = plotTempo.addNewDataTableSet("Tempo de execução");
		dts2.addNewDataTable("PrevalentSeed", tamSemente, y1);
		dts2.addNewDataTable("CELF", tamSemente, y2);
		dts2.addNewDataTable("HighDegree", tamSemente, y3);
		dts2.addNewDataTable("RandomSeed", tamSemente, y4);
		plotTempo.add(dts2);		
		plot.compile(plotTempo, plot.plot2d, "tempo_execucao.plt");
		
		this.terminal = "epslatex color colortext dashed";
//		this.terminal = "eps color dashed";
		this.output = outDir+"tempo.tex'";
		this.execute(plotTempo, this.plot2d);
	}
}
