import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import it.unimi.dsi.webgraph.ImmutableGraph;

public class Webgraph2ASCII {
		
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		long estimatedTime;

		//args = new String[] {"edges02"}; //graph_basename
		
		if(args.length != 1) {
			System.out.println("Usage: Webgraph2ASCII basename");
			System.exit(1);
		}
		
		String basename = args[0];
		
		ImmutableGraph G = it.unimi.dsi.webgraph.ImmutableGraph.load(basename);
		PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( basename + ".txt" ) ) );
		
		int n = G.numNodes();
		for(int v=0; v<n; v++) {
			if (v%1000000 == 0) 
				System.out.println(v);
			
			int[] v_neighbors = G.successorArray(v);
			int v_deg = G.outdegree(v);
			for (int i = 0; i < v_deg; i++) {
				int w = v_neighbors[i];
				out.println(v + "\t" + w);
			}
		}
		
		out.close();
		
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time elapsed = " + estimatedTime / 1000.0);
		System.out.println();
	}
}
