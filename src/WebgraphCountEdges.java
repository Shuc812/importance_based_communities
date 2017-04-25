public class WebgraphCountEdges {
		
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		long estimatedTime;

		//args = new String[] {"edges02"}; //graph_basename
		
		if(args.length != 1) {
			System.out.println("Usage: WebgraphCountEdges basename");
			System.exit(1);
		}
		
		String basename = args[0];
		
		it.unimi.dsi.webgraph.ImmutableGraph G = it.unimi.dsi.webgraph.ImmutableGraph.load(basename);
		
		long countEdges = 0;
		
		int n = G.numNodes();
		for(int v=0; v<n; v++) {
			if (v%1000000 == 0) 
				System.out.println(v);
			
			int v_deg = G.outdegree(v);
			countEdges += v_deg; 
		}
		
		System.out.println("countEdges=" + countEdges);
		
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time elapsed = " + estimatedTime / 1000.0);
		System.out.println();
	}
}

