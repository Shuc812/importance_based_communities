//This is C2, implemented with flat arrays.
import java.util.ArrayList;
import java.util.BitSet;

public class C1 {
	it.unimi.dsi.webgraph.ImmutableGraph G;
	int n;
	int k; // given k
	
	int[] rank;
	int[] rank_idx;
	int[] deg; // degrees
	int[] deg2; // copy degrees needed for the second "while"

	int i; //number of iterations. Iteration numbers start from 0.
	
	BitSet gone;

	public C1(String basename) throws Exception {
		G = it.unimi.dsi.webgraph.ImmutableGraph.load(basename);
		
		n = G.numNodes();
		
		rank = new int[n];
		rank_idx = new int[n];
		deg = new int[n];
		deg2 = new int[n];
		
		gone = new BitSet(n);
		
		System.out.println("Reading ranks...");
		Util.readIntArrayFromBinaryFile(rank, basename+".rank");
		//Fill rank indexes
		for(int j=0; j<n; j++)
			rank_idx[rank[j]] = j;					
		System.out.println("Reading ranks finished!");
		
		System.out.println("Reading cores...");
		Util.readIntArrayFromBinaryFile(deg, basename+".cores");
		System.out.println("Reading cores finished!");		
	}

	void topCom(Integer r, Integer k) {
		this.k = k;
		System.out.println("r = " + r + "   k = " + k);
		
		BitSet gone2 = new BitSet(n);
		for(int u=0; u<n; u++)
			if (deg[u] < k) {
				gone.set(u);
				gone2.set(u);
			}
		
		System.out.println("Size of Ck (r="+ r +", k=" + k + ") = " + (n-gone.cardinality()));
		
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
			
			DFS(u);
			i++;
		}
		
		System.out.println("1st while is finished. i=" + i);
		
		//Extracting the top-r communities
		int j=0;
		ra = 0;
		gone = gone2;
		deg = deg2;
		ArrayList<Integer> com_sizes = new ArrayList<Integer>(); 
		int total_com_size = 0;
		while(true) {

			int u = minRankAliveVertex(ra);
			if (u == -1)
				break;
			ra = rank_idx[u];
			
			if(j>=i-r) {
				ArrayList<Integer> H = getConnectedComponentCk(u);
				com_sizes.add(H.size());
				total_com_size += H.size();
				System.out.println(j + ": " + H);
			}
			
			DFS(u);
			j++;
		}		
		System.out.println("2nd while is finished. j=" + j);
				
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time for core part (r="+ r +", k=" + k + ") = " + estimatedTime / 1000.0);
		System.out.println("Community sizes (r="+ r +", k=" + k + ") = " + com_sizes);
		System.out.println("Total size of communities (r="+ r +", k=" + k + ") = " + total_com_size);
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
		
	void DFS(int u) {
		gone.set(u);
		
		int[] u_neighbors = G.successorArray(u);
		int u_deg = G.outdegree(u);
		for (int ni = 0; ni < u_deg; ni++) {
			int v = u_neighbors[ni];
			deg[v]--;
			if (!gone.get(v) && deg[v] < k)
				DFS(v);
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
					deg2[v] = deg[v];
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

		//args = new String[] {"edges02", "6", "2"}; //graph_basename, r, k
		//args = new String[] {"data1_astrocnet", "10", "2"}; //graph_basename, r, k
		//args = new String[] {"data12_uk-2005-edgeList", "10", "2"}; //graph_basename, r, k
		
		String basename  = args[0];
		int k = Integer.valueOf(args[1]);
		int r = Integer.valueOf(args[2]);
		
		C1 kc = new C1(basename);		
		kc.topCom( k, r );
			
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time elapsed = " + estimatedTime / 1000.0);
		System.out.println();
	}
}
