package Algorithm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.logging.Level;

import log.Log;
import tsp.TSP;
import tsp.distancesMatrix;
import variables.AntSystemArgs;



public class baseAntSystem {
	distancesMatrix distances;
	double [][] trail;
	double [][] delta;
	int [][] tabu;
	boolean [][] Allowed;
	
	long time;
	long NC;

	double [][][] TKij;
	//Iteration shortestLength
	double ISL=Double.MAX_VALUE;
	//Global shortestLength
	double GSL=Double.MAX_VALUE;
	//Iteration shortest path
	int[] ISP;
	//Global shortest path
	int[] GSP;
	
	static AntSystemArgs ag;
	double maxDist;
	
	Random r;
	
	static int epoch=1;
	static Log l;
	
	int[][] nearest;
	
	public baseAntSystem(distancesMatrix dist){
		this(dist, new AntSystemArgs());
		
	}
	
	public baseAntSystem(distancesMatrix dist,AntSystemArgs ag){
		baseAntSystem.ag = ag;
		this.distances = dist;
		
		trail = new double[ag.size][ag.size];
    	delta = new double[ag.size][ag.size];
		
		GSL = Double.MAX_VALUE;
		ISP = new int[ag.size];
		
		TKij = new double [ag.antNum][ag.size][ag.size]; 
		
		maxDist = dist.getMaxDist();

		this.InitTrailDelta();
		
		tabu = new int[ag.antNum][ag.size];
		Allowed = new boolean[ag.antNum][ag.size];
		r = new Random();
		
		nearest = dist.getNearestMatrix();
	}
	
	//O(n^2)
	public void updateTrail(){
		for(int i = 0; i<ag.size ; i++){
			for(int j = i+1; j<ag.size ; j++){
				trail[i][j] = ag.p * trail[i][j] + delta[i][j];
				trail[j][i] = trail[i][j];
				delta[i][j] = 0;
				delta[j][i] = delta[i][j];
			}
		}
	}
	
	//O(n^2)
	public void updateDelta(int[] ISP,double length){
		for(int j = 1; j<ag.size;j++){
			delta[ISP[j-1]][ISP[j]] += ag.Q/length;
			delta[ISP[j]][ISP[j-1]] = delta[ISP[j-1]][ISP[j]]; 
		}
	}
	//O(n^2)
	private void updateDelta(){
		double length;
		for(int i = 0 ; i<ag.antNum;i++){
			length = calculateTourLength(i);
			for(int j = 1; j<ag.size;j++){
				delta[this.tabu[i][j-1]][this.tabu[i][j]] += ag.Q/length;
				delta[this.tabu[i][j]][this.tabu[i][j-1]] = delta[this.tabu[i][j-1]][this.tabu[i][j]]; 
			}
		}
	}
	
	public void NextIteration(){
		this.resetAllowed();
		this.pickFirstNode();
		calculateTransitionProbabilities();
		updateIterAndGlobLength();
		updateDelta();
		updateTrail();
	}
	
	public void updateIterAndGlobLength(){
		double length = 0;
		ISL = Double.MAX_VALUE;
		
		for(int k = 0; k<ag.antNum ; k++){
			
			//O(size)
			length = calculateTourLength(k);
			
			
			if(length < ISL){
				ISL = length;
				ISP = tabu[k].clone();
			}
			
			if(ISL<GSL){
				GSL = ISL;
				GSP = ISP.clone();
			}
		}
		//System.out.println("GSL=" +GSL);
	}
	
	public void pickFirstNode(){
		for(int i=0; i< ag.antNum; i++){
			int rand = r.nextInt(ag.size);
			tabu[i][0]= rand;
			Allowed[i][rand] = false;
		}
	}
	
	public void resetAllowed(){
		for(int i=0;i<ag.antNum;i++){
			for(int j=0;j<ag.size;j++){
				Allowed[i][j]=true;
			}
		}
	}
	
	public double calcBranchingFactor(double x){
		double BF=0;
		int cnt = 0;
		double meanBF=0;
		double maxTrail=0,minTrail = Double.MAX_VALUE;
			for(int i=0;i<ag.size;i++){
				for(int j=0;j<ag.size;j++){
					if(i!=j){
						//l.logger.log(Level.INFO, "trail i j: " +i+"   "+j+"   "+Double.toString(trail[i][j]));
						if(trail[i][j]>maxTrail){
							maxTrail = trail[i][j];
						}
						if(trail[i][j]<minTrail){
							minTrail = trail[i][j];
							
						}
					}
				}
				BF= x * (maxTrail - minTrail) + minTrail;
				for(int j=0; j<ag.size;j++){
					if(trail[i][j]>BF){
					cnt++;
					}
				}
				meanBF+=cnt;
				cnt=0;
			}
		return (meanBF/ag.size);
	}
	
	//O(antNum * size^2)
		public void calculateTransitionProbabilities(){
			int s=1;
			while(s<ag.size){
				int curr;
				double sum;
				double r = Math.random();
				double probSum;
				int cityJ;
				for(int k=0; k<ag.antNum;k++){
					r = Math.random();
		
					curr = tabu[k][s-1];
					sum = 0;
					probSum = calcSum(k,curr);
					if(probSum==0){
						double max = 0;
						int maxCity=0;
						double val;
						for(int j=21;j<ag.size;j++){
							cityJ = nearest[curr][j];
							if(Allowed[k][cityJ]==false){
								continue;
							}
							val = Math.pow(trail[curr][cityJ],ag.alpha) * Math.pow(1/(distances.getDist(curr, cityJ)), ag.beta);//removed normalization 1/(distances.getDist(curr, cityJ)/maxDist*100) 
							if(val>max){
								max=val;
								maxCity=cityJ;
							}
						}
						tabu[k][s]=maxCity;
						Allowed[k][maxCity]=false;
						continue;
					}
					for(int j=1; j<21;j++){
						cityJ = nearest[curr][j];
						if(Allowed[k][cityJ]==true){
							TKij[k][curr][cityJ] = Math.pow(trail[curr][cityJ],ag.alpha) * Math.pow(1/(distances.getDist(curr, cityJ)/maxDist*100), ag.beta)/probSum;
							sum = sum + TKij[k][curr][cityJ];	
							if(sum >= r){
								tabu[k][s] = cityJ;
								Allowed[k][cityJ] = false;
								break;
							}
						}
					}
				}
				s = s + 1;
			}
		}
		
	public void InitTrailDelta(){
		for(int i = 0; i <ag.size; i++ ){	
			for(int j = i+1; j<ag.size;j++){
				trail[i][j] = 100000.0;
				trail[j][i] = trail[i][j];
				delta[i][j] = 0;
				delta[j][i] = delta[i][j];
			}
		}
	}
	
	//O(size)
	public double calculateTourLength(int k){
		double length=0;
		for(int i = 1; i <ag.size; i++){
			//System.out.println("i: " + i);
			length += distances.getDist(tabu[k][i], tabu[k][i-1]);
		}
		length += distances.getDist(tabu[k][ag.size-1], tabu[k][0]);
		
		return length;
	}
	
	//calculates the sum of trails * visibilities
	//O(size)
	public double calcSum(int ant, int i){
		double sum =0;
		for(int j=1; j < 21;j++){
			if(Allowed[ant][nearest[i][j]]==true){
				sum = sum + Math.pow(trail[i][nearest[i][j]],ag.alpha) * Math.pow(1/(distances.getDist(i, nearest[i][j])/maxDist*100), ag.beta);
			}
		}
		return sum;
	}
	
	//GETTERS
	public double getGSL(){
		return GSL;
	}
	
	public double getISL(){
		return ISL;
	}
	
	public int[] getGSP(){
		return this.GSP;
	}
	
	public int[] getISP(){
		return this.ISP;
	}
	
	public void printPath(int[] path){
		String s="[" + path[0];
		for(int i=1;i<path.length;i++){
			s=s + ","+path[i];
		}
		System.out.println(s);
	}
	
	public String toString(){
		String s;
		s = "," +  this.GSL + ","+calcBranchingFactor(0.05);
		return s;
	}
	
	
	public static void main(String arg[]){
		TSP t = new TSP(arg[0]);
		distancesMatrix dm = t.getMatrix();
		
		l = new Log("ASLog" + arg[2]);
		baseAntSystem as = new baseAntSystem(dm,new AntSystemArgs(Integer.valueOf(arg[1])));
		
		l.logger.setLevel(Level.SEVERE);
		
		try {
			System.setOut(new PrintStream(new FileOutputStream("ch130-ASbf.csv")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long startTime = System.currentTimeMillis();
		while(epoch<=as.ag.Iter){
			as.NextIteration();
			System.out.print(epoch);
			System.out.println(as);
			epoch++;
			//as.printPath(as.GSP);
		}	
		
		long runTime = System.currentTimeMillis() - startTime;
		
		l.logger.log(Level.SEVERE, "runtime: " + runTime);
	}
}

