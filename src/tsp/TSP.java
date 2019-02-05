package tsp;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;




public class TSP {
	Charset charset = Charset.forName("US-ASCII");
	String name; 
	int vertexNum;
	int idx = 0;
	boolean readVertices = false;
	ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	distancesMatrix distances;
	
	public TSP(String fileName){
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName), charset)) {
		    String line = reader.readLine();
		    String[] tokens;
		    //read in the vertices
		    while ((line = reader.readLine()) != null && !line.equals("EOF") && !line.equals("TOUR_SECTION") ) {
		        tokens = line.split(" +");
		        if(tokens[0].equals("DIMENSION")){
			    	vertexNum = Integer.valueOf(tokens[2]);
			    }
		        if(readVertices){
		        	Vertex v = new Vertex(Double.valueOf(tokens[1]),Double.valueOf(tokens[2]),idx);
		        	vertices.add(v);
		        	idx++;
		        }
		        if(tokens[0].equals("NODE_COORD_SECTION")){
		        	readVertices = true;
		        }
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		distances = new distancesMatrix(vertices);
	}
	
	public TSP(String fileName, Boolean b){
		idx =0;
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName), charset)) {
		    String line = reader.readLine();
		    String[] tokens = line.split(" +");
		    int size = Integer.valueOf(tokens[0]);
		    //read in the vertices
		    for(int i=0;i<size;i++){
		    	line = reader.readLine();
		        tokens = line.split(" +");
		        Vertex v = new Vertex(Double.valueOf(tokens[0]),Double.valueOf(tokens[1]),idx);
		        	vertices.add(v);
		        	idx++;
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		distances = new distancesMatrix(vertices);
	}
	
	public distancesMatrix getMatrix(){
		return distances;
	}	
}
