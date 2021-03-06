package Algorithm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;

import log.Log;
import tsp.TSP;
import tsp.distancesMatrix;
import variables.AntSystemArgs;

public class MCMM {
	
	static Log l;
	static int epoch=1;
	MaxMin main;
	MaxMin[] colonies;
	static distancesMatrix dm;
	static AntSystemArgs ag;
	
	public MCMM(int colNum){
		main = new MaxMin(dm,new AntSystemArgs(ag.size,ag.antNum/colNum+ag.antNum%colNum));
		colonies = new MaxMin[colNum-1];
		for(int i=0;i<(colNum-1);i++){
			colonies[i]=new MaxMin(dm,new AntSystemArgs(ag.size,ag.antNum/colNum));
		}
	}
	
	private void NextIteration() {
		for(int i=0; i<(ag.coloniesNumber-1);i++){
			colonies[i].NextIteration();
			main.updateDelta(colonies[i].getISP(),colonies[i].getISL());
		}
		main.updateTrail();
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
		s=main.GSL+","+main.calcBranchingFactor(0.1);
		for(int i=0; i<ag.coloniesNumber-1;i++){
			s+="," + colonies[i].getGSL() + "," +colonies[i].calcBranchingFactor(0.1);
		}
		return s;
	}
	
	public static void main(String arg[]){
		TSP t = new TSP(arg[0]);
		dm = t.getMatrix();
		l = new Log("MCMMLog" + arg[2]);
		l.logger.setLevel(Level.SEVERE);
		ag = new AntSystemArgs(Integer.valueOf(arg[1]));
		
		MCMM mcmm = new MCMM(ag.coloniesNumber);
		
		long startTime = System.currentTimeMillis();
		
		while(epoch<=mcmm.ag.Iter){
			mcmm.NextIteration();
			System.out.print(epoch + ",");
			System.out.println(mcmm);
			epoch++;
		}	
		
		long runTime = System.currentTimeMillis() - startTime;
		
		l.logger.log(Level.SEVERE, "runtime: " + runTime);
	}
}
