//Only produces the iteration structure. Just experimentation. 
//Uses only flat arrays.
public class CIter {
	it.unimi.dsi.webgraph.ImmutableGraph G;
	int n;
	int k; // given k
	
	int[] rank;
	int[] rank_idx;
	int[] deg; // degrees
	
	int[] I; //Flat iteration structure
	int[] Ioffsets; //Array of offsets. 
	
	//Vertices deleted in iteration 0 are in I[0], ..., I[Ioffsets[0]-1]
	//Vertices deleted in iteration 1 are in I[Ioffsets[0]], ..., I[Ioffsets[2]-1], 
	//and so on.
	
	boolean[] gone;
	
	int i; //number of iterations. Iteration numbers start from 0.

	public CIter(String basename) throws Exception {
		G = it.unimi.dsi.webgraph.Transform.filterArcs(
				it.unimi.dsi.webgraph.ImmutableGraph.load(basename), 
				it.unimi.dsi.webgraph.Transform.NO_LOOPS);
		
		n = G.numNodes();
		
		rank = new int[n];
		rank_idx = new int[n];
		deg = new int[n];
		
		gone = new boolean[n];
		for(int v=0; v<n; v++)
			gone[v] = false;
		
		System.out.println("Reading ranks...");
		Util.readIntArrayFromBinaryFile(rank, basename+".rank");
		
		//Fill rank indexes
		for(int j=0; j<n; j++) {
			rank_idx[rank[j]] = j;
			//System.out.println("Vertex " + rank[j] + " has rank " + j);
		}
		System.out.println("Reading ranks finished!");
		
		System.out.println("Reading cores...");
		Util.readIntArrayFromBinaryFile(deg, basename+".cores");
		System.out.println("Reading cores finished!");
		
		I = new int[n];
		Ioffsets = new int[n];
		
		for(int iter=0; iter<n; iter++)
			Ioffsets[iter] = 0;
	}


	void topCom(Integer r, Integer k) {
		this.k = k;
		System.out.println("r = " + r + "   k = " + k);
		
		for(int u=0; u<n; u++)
			if (deg[u] < k)
				gone[u] = true;
		
		//Reuse the deg array to keep for each live vertex its number of live neighbors
		fillDeg();
		
		// Continue with the algorithm
		long startTime = System.currentTimeMillis();
		long estimatedTime;	
		
		i=0;
		int ra = 0;
		while(true) {

			int u = minRankAliveVertex(ra);
			if (u == -1)
				break;
			ra = rank_idx[u];
			
			DFS(u, i);
			
			i++;
			Ioffsets[i] = Ioffsets[i-1];
		}
		System.out.println("i="+i);
		
		//Printouts, just for info
		for(int iter=0; iter<i; iter++) {
			//System.out.print(Ioffsets[iter] + ": ");
			System.out.print(iter + ": ");
			
			int start_span = 0;
			if(iter>0)
				start_span = Ioffsets[iter-1];
			for(int j=start_span; j<Ioffsets[iter]; j++) {
				System.out.print(I[j] + " ");
			}
			System.out.println();
		}
		
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time for core part = " + estimatedTime / 1000.0);
	}
	
	//Returns the min rank *alive* vertex starting from start_from_rank.
	int minRankAliveVertex(int start_from_rank) {
		int u = -1;
		
		for(int raa=start_from_rank; raa<n; raa++)
			if( !gone[ rank[raa] ] ) {
				u = rank[raa];
				break;
			}
	
		return u; //if there is no more
	}
		
	void DFS(int u, int i) {
		gone[u] = true;
		I[ Ioffsets[i] ] = u;
		Ioffsets[i]++;
		
		int[] u_neighbors = G.successorArray(u);
		int u_deg = G.outdegree(u);
		for (int ni = 0; ni < u_deg; ni++) {
			int v = u_neighbors[ni];
			deg[v]--;
			if (!gone[v] && deg[v] < k)
				DFS(v, i);
		}
	}	
	
	void fillDeg() {
		
		for(int v=0; v<n; v++) {
			
			deg[v] = 0;
			
			if(gone[v])
				continue;
			
			int[] v_neighbors = G.successorArray(v);
			int v_deg = G.outdegree(v);
			for (int i = 0; i < v_deg; i++) {
				int w = v_neighbors[i];
				
				if (!gone[w]) {
					deg[v]++;
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		long estimatedTime;

		//args = new String[] {"edges02", "3", "2"}; //graph_basename, r, k
		//args = new String[] {"data1_astrocnet", "10", "2"}; //graph_basename, r, k
		
		CIter kc = new CIter(args[0]);		
		kc.topCom( Integer.valueOf(args[1]), Integer.valueOf(args[2]) );
			
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time elapsed = " + estimatedTime / 1000.0);
		System.out.println();
	}
}
