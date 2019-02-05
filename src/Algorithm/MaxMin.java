package Algorithm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;

import log.Log;
import tsp.TSP;
import tsp.distancesMatrix;
import variables.AntSystemArgs;

public class MaxMin extends baseAntSystem {
	
	double max=100000,min=0;
	
	static Log l;
	
	public MaxMin(distancesMatrix dist) {
		super(dist);
	}
	
	public MaxMin(distancesMatrix dist,AntSystemArgs ag) {
		super(dist,ag);
	}
	
	private void updateDelta(){
		for(int i = 1 ; i<ag.size;i++){
			if(epoch %10==0){
				delta[GSP[i]][GSP[i-1]] +=  ag.Q/GSL;
				delta[GSP[i-1]][GSP[i]] = delta[GSP[i]][GSP[i-1]];
			}else{
				delta[ISP[i]][ISP[i-1]] +=  ag.Q/GSL;
				delta[ISP[i-1]][ISP[i]] = delta[ISP[i]][ISP[i-1]];
			}
		}
	}
	
	public void InitTrailDelta(){
		for(int i = 0; i <ag.size; i++ ){
			for(int j = i+1; j<ag.size;j++){
				trail[i][j] = 1000000.0;
				trail[j][i] = trail[i][j];
				delta[i][j] = 0;
				delta[j][i] = delta[i][j];
			}
		}
	}
	
	protected void updateMinMax(){
		this.max = (1/(1-ag.p)) * (1/GSL);
		this.min = (max*(1 - Math.pow(ag.pb, 1.0/ag.size)))/((ag.size/2-1) * Math.pow(ag.pb, 1.0/ag.size));
		if(min>max){
			min=max;
		}
	}
	
	public void updateMinMax(double GSL){
		this.max = (1/(1-ag.p)) * (1/GSL);
		this.min = (max*(1 - Math.pow(ag.pb, 1.0/ag.size)))/((ag.size/2-1) * Math.pow(ag.pb, 1.0/ag.size));
		if(min>max){
			min=max;
		}
	}
	
	public void updateTrail(){
		for(int i = 0; i<ag.size ; i++){
			for(int j = i+1; j<ag.size ; j++){
				trail[i][j] = ag.p * trail[i][j] + delta[i][j];
				if(trail[i][j]>max){
					trail[i][j]=max;
				}
				if(trail[i][j]<min){
					trail[i][j]=min;
				}
				trail[j][i] = trail[i][j];
				//l.logger.log(Level.SEVERE, "trail: " + i + ", " + j + "          "+ trail[i][j]);
				delta[i][j] = 0;
				delta[j][i] = delta[i][j];
			}
		}
	}
	
	
	public void NextIteration(){
		resetAllowed();
		pickFirstNode();
		calculateTransitionProbabilities();
		updateIterAndGlobLength();
		updateMinMax();
		updateDelta();
		updateTrail();
	}
	
	public static void main(String arg[]){
		TSP t = new TSP(arg[0]);
		distancesMatrix dm = t.getMatrix();
		//dm.printMatrix();
		//dm.printNearestMatrix();
		//System.exit(-1);
		MaxMin mm = new MaxMin(dm,new AntSystemArgs(Integer.valueOf(arg[1])));
		l = new Log("MMLog" + arg[2]);
		l.logger.setLevel(Level.SEVERE);
		
		try {
			System.setOut(new PrintStream(new FileOutputStream("eil76MM-bf.csv")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long startTime = System.currentTimeMillis();
	
		while(epoch<=mm.ag.Iter){
			mm.NextIteration();
			System.out.print(epoch);
			System.out.println(mm);
			
			epoch++;
		}	
		
		long runTime = System.currentTimeMillis() - startTime;
		
		l.logger.log(Level.SEVERE, "runtime: " + runTime);
	}
}
