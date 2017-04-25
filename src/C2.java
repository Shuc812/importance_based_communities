//This is C2, implemented with flat arrays.
import java.util.ArrayList;
import java.util.BitSet;

public class C2 {
	it.unimi.dsi.webgraph.ImmutableGraph G;
	int n;
	int k; // given k
	
	int[] rank;
	int[] rank_idx;
	int[] deg; // degrees

	int i; //number of iterations. Iteration numbers start from 0.
	
	int[] I; //Flat iteration structure
	int[] Ioffsets; //Array of offsets. 

	//Vertices deleted in iteration 0 are in I[0], ..., I[Ioffsets[0]-1]
	//Vertices deleted in iteration 1 are in I[Ioffsets[0]], ..., I[Ioffsets[2]-1], 
	//and so on.
	
	BitSet gone;

	public C2(String basename) throws Exception {
		G = it.unimi.dsi.webgraph.ImmutableGraph.load(basename);
		
		n = G.numNodes();
		
		rank = new int[n];
		rank_idx = new int[n];
		deg = new int[n];
		
		gone = new BitSet(n);
		
		System.out.println("Reading ranks...");
		Util.readIntArrayFromBinaryFile(rank, basename+".rank");
		//Fill rank indexes
		for(int j=0; j<n; j++) {
			rank_idx[rank[j]] = j;
			//System.out.println("vertex " + rank[j] + " has rank " + j);
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
				gone.set(u);
		
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
		
		//Extracting the top-r communities
		for(int j=i-1; j>=i-r; j--) {
			int v = -1;
			int start_idx = Ioffsets[j]-1;
			int stop_idx = 0; 
			if (j-1>=0) 
				stop_idx = Ioffsets[j-1];
			
			for(int idx=start_idx; idx >= stop_idx; idx-- ) {
				v = I[ idx ];
				gone.clear(v);
			}
			
			System.out.println(getConnectedComponentCk(v)); 
		}
		
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time for core part (r="+ r +", k=" + k + ") = " + estimatedTime / 1000.0);
	}
	
	
	//Returns the min rank *alive* vertex starting from start_from_rank.
	int minRankAliveVertex(int start_from_rank) {
		int u = -1;
		
		for(int raa=start_from_rank; raa<n; raa++)
			if( !gone.get( rank[raa] ) ) {
				u = rank[raa];
				break;
			}
	
		return u; //if there is no more
	}
		
	void DFS(int u, int i) {
		gone.set(u);
		I[ Ioffsets[i] ] = u;
		Ioffsets[i]++;
		
		int[] u_neighbors = G.successorArray(u);
		int u_deg = G.outdegree(u);
		for (int ni = 0; ni < u_deg; ni++) {
			int v = u_neighbors[ni];
			deg[v]--;
			if (!gone.get(v) && deg[v] < k)
				DFS(v, i);
		}
	}	
	
	void fillDeg() {
		
		for(int v=0; v<n; v++) {
			
			deg[v] = 0;
			if(gone.get(v))
				continue;
			
			int[] v_neighbors = G.successorArray(v);
			int v_deg = G.outdegree(v);
			for (int i = 0; i < v_deg; i++) {
				int w = v_neighbors[i];
				
				if (!gone.get(w)) {
					deg[v]++;
				}
			}
		}
	}

	ArrayList<Integer> getConnectedComponentCk(int u) {
		ArrayList<Integer> cc = new ArrayList<Integer>();
		ConnectedComponentsCkDFS(u, cc);
		return cc;
	}

	void ConnectedComponentsCkDFS(Integer u, ArrayList<Integer> cc) {
		cc.add(u);
		
		int[] u_neighbors = G.successorArray(u);
		int u_deg = G.outdegree(u);
		for (int i = 0; i < u_deg; i++) {
			int v = u_neighbors[i];
			if (!gone.get(v) && !cc.contains(v))
				ConnectedComponentsCkDFS(v, cc);
		}		
	}
		
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		long estimatedTime;

		//args = new String[] {"edges02", "7", "2"}; //graph_basename, r, k
		//args = new String[] {"data1_astrocnet", "10", "2"}; //graph_basename, r, k
		//args = new String[] {"data12_uk-2005-edgeList", "10", "2"}; //graph_basename, r, k
		
		String basename  = args[0];
		int k = Integer.valueOf(args[1]);
		int r = Integer.valueOf(args[2]);
		
		C2 kc = new C2(basename);		
		kc.topCom( k, r );
			
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time elapsed = " + estimatedTime / 1000.0);
		System.out.println();
	}
}
