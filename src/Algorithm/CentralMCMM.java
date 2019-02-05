package Algorithm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;

import log.Log;
import tsp.TSP;
import tsp.distancesMatrix;
import variables.AntSystemArgs;

public class CentralMCMM {
	
	static Log l;
	static int epoch=1;
	MaxMin main;
	MaxMin[] colonies;
	static distancesMatrix dm;
	static AntSystemArgs ag;
	double GSL;
	
	public CentralMCMM(int colNum){
		GSL = Double.MAX_VALUE;
		main = new MaxMin(dm,new AntSystemArgs(ag.size,ag.antNum));
		colonies = new MaxMin[colNum-1];
		for(int i=0;i<(colNum-1);i++){
			colonies[i]=new MaxMin(dm,new AntSystemArgs(ag.size,ag.antNum/colNum));
		}
	}
	
	private void NextIteration() {
		for(int i=0; i<(ag.coloniesNumber-1);i++){
			colonies[i].NextIteration();
			main.updateDelta(colonies[i].getISP(),colonies[i].getISL());
			if(colonies[i].getISL()<GSL){
				GSL = colonies[i].getISL();
			}
		}
		main.updateMinMax(GSL);
		main.updateTrail();
	}
	
	private void FinalIterations(){
		main.NextIteration();
	}
	
	public void printColumnHeadings(){
		System.out.print("Epoch, MainCol, MainBF");
		for(int i=0; i<(ag.coloniesNumber-1);i++){
			System.out.print(", col" + (i+1) + ",BF " + (i+1));
		}
		System.out.println("");
	}
	
	public String toString(){
		String s;
		s=main.GSL+","+main.calcBranchingFactor(0.05);
		for(int i=0; i<ag.coloniesNumber-1;i++){
			s+="," + colonies[i].getGSL() + "," +colonies[i].calcBranchingFactor(0.05);
		}
		return s;
	}
	
	public static void main(String arg[]){
		TSP t = new TSP(arg[0]);
		dm = t.getMatrix();
		l = new Log("CMCMMLog" + arg[2]);
		l.logger.setLevel(Level.SEVERE);
		ag = new AntSystemArgs(Integer.valueOf(arg[1]));
		
		try {
			System.setOut(new PrintStream(new FileOutputStream("ch130CMCMM-bf.csv")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CentralMCMM mcmm = new CentralMCMM(ag.coloniesNumber);
		
		long startTime = System.currentTimeMillis();
		
		while(epoch<=mcmm.ag.Iter){
			if((double)epoch/(double)mcmm.ag.Iter<0.8){
			mcmm.NextIteration();
			}else{
			mcmm.FinalIterations();	
			}
			System.out.print(epoch + ",");
			System.out.println(mcmm);
			epoch++;
		}
		long runTime = System.currentTimeMillis() - startTime;
		
		l.logger.log(Level.SEVERE, "runtime: " + runTime);
	}
}

