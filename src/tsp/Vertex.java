package tsp;

public class Vertex {
	public double x, y;
	public int index;
	
	public Vertex(double x, double y, int index){
		this.x = x;
		this.y = y;
		this.index = index;
	}
	
	public String toString(){
		return index + ": " + "{" + x + ", " + y + "}";
	}
	
	public void setValues(Vertex v){
		this.x = v.x;
		this.y = v.y;
		this.index = v.index;
	}
	
}
