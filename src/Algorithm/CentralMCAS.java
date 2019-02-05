package Algorithm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;

import log.Log;
import tsp.TSP;
import tsp.distancesMatrix;
import variables.AntSystemArgs;

public class CentralMCAS {
	
	static Log l;
	static int epoch=1;
	baseAntSystem main;
	baseAntSystem[] colonies;
	static distancesMatrix dm;
	static AntSystemArgs ag;
	
	
	public CentralMCAS(int colNum){
		main = new baseAntSystem(dm,new AntSystemArgs(ag.size,ag.antNum));
		colonies = new baseAntSystem[colNum-1];
		int antPerCol = ag.antNum/colNum;
		int rem = ag.antNum%colNum;
		int add = 0;
		for(int i=0;i<(colNum-1);i++){
			add=0;
			if(rem>0){
				add = 1;
				rem--;
			}
			colonies[i]=new baseAntSystem(dm,new AntSystemArgs(ag.size,antPerCol+add));
		}
	}
	
	public static void main(String arg[]){
		TSP t = new TSP(arg[0]);
		dm = t.getMatrix();
		l = new Log("CMCASLog" + arg[2]);
		l.logger.setLevel(Level.SEVERE);
		ag = new AntSystemArgs(Integer.valueOf(arg[1]),Integer.valueOf(arg[1]));
		CentralMCAS mcas = new CentralMCAS(ag.coloniesNumber);
		
		
		long startTime = System.currentTimeMillis();
		
		try {
			System.setOut(new PrintStream(new FileOutputStream("ch130CMCAS-bf.csv")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(epoch<=mcas.ag.Iter){
			if((double)epoch/(double)mcas.ag.Iter<0.8){
			mcas.NextIteration();
			}else{
			mcas.FinalIterations();	
			}
			System.out.print(epoch + ",");
			System.out.println(mcas);
			epoch++;
		}
		
		long runTime = System.currentTimeMillis() - startTime;
		
		l.logger.log(Level.SEVERE, "runtime: " + runTime);
	}
	
	public void printColumnHeadings(){
		System.out.print("Epoch, MainCol, MainBF");
		for(int i=0; i<(ag.coloniesNumber-1);i++){
			System.out.print(", col" + (i+1) + ",BF " + (i+1));
		}
		System.out.println("");
	}

	private void NextIteration() {
		for(int i=0; i<(ag.coloniesNumber-1);i++){
			colonies[i].NextIteration();
			main.updateDelta(colonies[i].getISP(),colonies[i].getISL());
		}
		//main.NextIteration();
		main.updateTrail();
	}
	
	private void FinalIterations(){
		main.NextIteration();
	}
	
	public String toString(){
		String s;
		s=main.GSL+","+main.calcBranchingFactor(0.05);
		for(int i=0; i<ag.coloniesNumber-1;i++){
			s+="," + colonies[i].getGSL() + "," +colonies[i].calcBranchingFactor(0.05);
		}
		return s;
	}
}
