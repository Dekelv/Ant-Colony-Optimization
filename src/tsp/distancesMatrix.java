package tsp;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class distancesMatrix implements Serializable {
	double[][] distances;
	double dist;
	double maxDist;
	int[][] nearest;
	public int size;
	public distancesMatrix(ArrayList<Vertex> vertices){
		size = vertices.size();
		maxDist = 0;
		distances = new double[size][size];
		nearest = new int[size][size];
		
		for(int i=0; i < size ; i++){
			for(int j=0; j< size;j++){
				distances[i][i]=Double.MIN_NORMAL;
				nearest[i][j]=Integer.MAX_VALUE;
			}
		}
		
		double x1,x2,y1,y2;
		for(int i=0; i < size ; i++){
			for(int j=0; j< size;j++){
				x1 = vertices.get(i).x;
				x2 = vertices.get(j).x;
				y1 = vertices.get(i).y;
				y2 = vertices.get(j).y;
				//System.out.println(x1);
				//System.out.println(x2);
				//System.out.println(y1);
				//System.out.println(y2);
				dist = Math.sqrt((x1 - x2)*(x1-x2) + (y1-y2)*(y1-y2));
				//System.out.println(dist);
				//System.exit(-1);
				if(dist>maxDist){
					maxDist = dist;
				}
				distances[i][j] = dist;
				
				int New = j;
				int old;
				
				for(int x=0;x<=j;x++){
					if(nearest[i][x]>size || distances[i][nearest[i][x]]>distances[i][j]){
						old = nearest[i][x];
						nearest[i][x]=New;
						New = old;
					}
				}
			}
		}
	}
	
	public distancesMatrix(int size){
		distances = new double[size][size];
	}
	
	public void addEdge(int i, int j, double e){
		distances[i][j] = e;
	}
	
	public double[][] getMatrix(){
		return distances;
	}
	
	public double getDist(int i, int j){
		return distances[i][j];
	}
	
	public double getMaxDist(){
		return maxDist;
	}
	
	public int[][] getNearestMatrix(){
		return nearest;
	}
	
	public void printMatrix(){
		String s="";
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				s=s+"["+numberFormat.format(getDist(i,j))+"]";
			}
			s=s+"\n";
		}
		System.out.println(s);
	}
	
	public void printNearestMatrix(){
		String s="";
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				s=s+"["+this.nearest[i][j]+"]";
			}
			s=s+"\n";
		}
		System.out.println(s);
	}
}
