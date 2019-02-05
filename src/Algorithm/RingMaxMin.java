package Algorithm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;

import log.Log;
import tsp.TSP;
import tsp.distancesMatrix;
import variables.AntSystemArgs;

public class RingMaxMin {
	static int epoch=1;
	static Log l;
	static distancesMatrix dm;
	static AntSystemArgs ag;
	
	MaxMin[] colonies;
	
	public RingMaxMin(int colNum){
		colonies = new MaxMin[colNum];
		initColonies(colNum);
	}
	
	private void initColonies(int colNum){
		int rem = ag.antNum%colNum;
		int add=0;
		for(int i=0;i<colonies.length;i++){
			if(rem>0){
				add=1;
				rem--;
			}
			colonies[i] = new MaxMin(dm,new AntSystemArgs(ag.size,ag.antNum/colNum+add));
			add=0;
		}
	}
	
	public void printColumnHeadings(){
		System.out.print("Epoch, col0, BF0");
		for(int i=0; i<(ag.coloniesNumber-1);i++){
			System.out.print(", col" + (i+1) + ",BF " + (i+1));
		}
		System.out.println("");
	}
	
	private void NextIteration() {
		for(int i=0; i<ag.coloniesNumber;i++){
			colonies[i].NextIteration();
		}
	}
	
	private void shareInfoIteration(){
		colonies[0].NextIteration();
		for(int i=1; i<ag.coloniesNumber;i++){
			colonies[i].NextIteration();
			colonies[i].updateDelta(colonies[i-1].ISP, colonies[i-1].ISL);
		}
		colonies[0].updateDelta(colonies[ag.coloniesNumber-1].ISP,colonies[ag.coloniesNumber-1].ISL);
	}
	
	public String toString(){
		String s="";
		for(int i=0; i<ag.coloniesNumber;i++){
			s+="," + colonies[i].getGSL() + "," +colonies[i].calcBranchingFactor(0.1);
		}
		return s;
	}
	
	public static void main(String arg[]){
		TSP t = new TSP(arg[0]);
		dm = t.getMatrix();
		l = new Log("RingLog10" + arg[2]);
		ag = new AntSystemArgs(Integer.valueOf(arg[1]),Integer.valueOf(arg[1]));
		l.logger.setLevel(Level.SEVERE);
		
		try {
			System.setOut(new PrintStream(new FileOutputStream("ch130RingMM-bf.csv")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RingMaxMin ras = new RingMaxMin(ag.coloniesNumber);
		
		long startTime = System.currentTimeMillis();
		while(epoch<=ras.ag.Iter){
			if(epoch % 10==0){
				ras.shareInfoIteration();
			}else{
				ras.NextIteration();	
			}
			System.out.print(epoch);
			System.out.println(ras);
			epoch++;
		}	
		long runTime = System.currentTimeMillis() - startTime;
		
		l.logger.log(Level.SEVERE, "runtime: " + runTime);
	}			
}
