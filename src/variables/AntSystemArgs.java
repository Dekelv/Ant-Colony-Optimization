package variables;

public class AntSystemArgs {
	public int size = 76;
	public double alpha = 1;
	public double beta = 2;
	public double tI = 100;
	public int Iter = 85000;
	public double p = 0.98;
	public int antNum = size;
	public int Q = 1;
	public double pb = 0.05;
	public int coloniesNumber = 5;
	
	public AntSystemArgs(){
		
	}
	
	public AntSystemArgs(int size, int antNum){
		this.size = size;
		this.antNum = antNum;
	}
	
	public AntSystemArgs(int size){
		this.size = size;
		this.antNum = size;
	}
	
	
	public int getSize(){return size;}
	
	public int getAntNum(){return antNum;}
}
