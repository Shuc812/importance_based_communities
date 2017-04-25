import java.util.ArrayList;

//It starts from highest weighted vertices. No need to have the cores pre-computed.
public class NC2_BZ {
	it.unimi.dsi.webgraph.ImmutableGraph G;
	int n_orig; //number of nodes in the original graph. 
	int n_alive; //number of alive nodes.
	int md_alive = 0; //max degree in the alive graph.
	int k; //given k
	
	int[] rank;
	int[] cores;
	int[] cores_orig;
	int[] degrees;
	int[] al;
	int[] al_idx;
	
	java.util.BitSet alive;
	java.util.BitSet inPrevCom; //inPrevCom[v] == true when v is already in some community discovered backwards. 
	

	public NC2_BZ(String basename) throws Exception {
		G = it.unimi.dsi.webgraph.ImmutableGraph.load(basename);
		
		n_orig = G.numNodes();

		rank = new int[n_orig];
		cores = new int[n_orig];
		cores_orig = new int[n_orig];
		degrees = new int[n_orig];
		al = new int[n_orig];
		al_idx = new int[n_orig];
		alive = new java.util.BitSet(n_orig);
		inPrevCom = new java.util.BitSet(n_orig);
		
		n_alive = 0;
		
		System.out.println("Reading ranks...");
		Util.readIntArrayFromBinaryFile(rank, basename+".rank");
		System.out.println("Reading ranks finished!");	
		
		System.out.println("Reading cores...");
		Util.readIntArrayFromBinaryFile(cores_orig, basename+".cores");
		System.out.println("Reading cores finished!");			
	}

	void topCom(Integer r, Integer k) {
		this.k = k;
		System.out.println("r = " + r + "   k = " + k);
		
		// Continue with the algorithm
		long startTime = System.currentTimeMillis();
		long estimatedTime;

		int i=1;
		for(int j = n_orig-1; j>=0; j--) {
			int u = rank[j];
		
			if(cores_orig[u] < k)
				continue;
			
			alive.set(u);
			al[n_alive] = u;
			al_idx[u] = n_alive;
			n_alive++;
			
			updateCores(u);
			if(cores[u] >= k) {
				cnt_good++;
				//System.out.println(i + "\t" + u + ": " + getConnectedComponentCk(u));
				boolean[] isNC = new boolean[] {true};
				ArrayList<Integer> H = getConnectedComponentCk(u,isNC);
				if(isNC[0]) {
					System.out.println(H);
					i++;
					if(i>r)
						break;
				}
			}
		}
		
		//System.out.println("cnt_all=" + cnt_all);
		//System.out.println("cnt_good=" + cnt_good);
		
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time for core part (r="+ r +", k=" + k + ") = " + estimatedTime / 1000.0);
	}	
	
	
	int cnt_all = 0, cnt_good = 0;
	
	void updateCores(int u) {
		updateDegrees(u);
		if(degrees[u]>=k && d_k(u)>=k) {
			cnt_all++;
			KCoreCompute_alive();
		}
	}
	
	int d_k(int v) {
		int d = 0;	
		int[] v_neighbors = G.successorArray(v);
		int v_deg = G.outdegree(v);
		for (int i = 0; i < v_deg; i++) {
			int w = v_neighbors[i];
			
			if ( degrees[w] >= k && cores_orig[w] >= k )
				d++;
		}
		return d;
	}
	
	
	void updateDegrees(int v) {
		int d = 0;	
		int[] v_neighbors = G.successorArray(v);
		int v_deg = G.outdegree(v);
		for (int i = 0; i < v_deg; i++) {
			int w = v_neighbors[i];
			
			if ( alive.get(w) ) {
				d++;
				degrees[w]++;
				if(md_alive<degrees[w])
					md_alive=degrees[w];
			}
		}
		degrees[v] = d;
		if(md_alive<degrees[v])
			md_alive=degrees[v];
	}
	
	// Computes the core decomposition, implements the Batagelz and Zaversnik algorithm
	public void KCoreCompute_alive() {
		//md_alive = maxDegree_alive();
		
		int[] vert = new int[n_alive];
		int[] pos = new int[n_alive];
		int[] bin = new int[md_alive + 1]; // md+1 because we can have zero degree

		for (int d = 0; d <= md_alive; d++)
			bin[d] = 0;
		for (int v = 0; v < n_alive; v++) {
			cores[al[v]] = degrees[al[v]];
			bin[cores[al[v]]]++;
		}

		int start = 0; // start=1 in original, but no problem
		for (int d = 0; d <= md_alive; d++) {
			int num = bin[d];
			bin[d] = start;
			start += num;
		}

		// bin-sort vertices by degree
		for (int v = 0; v < n_alive; v++) {
			pos[v] = bin[cores[al[v]]];
			vert[pos[v]] = v;
			bin[cores[al[v]]]++;
		}
		// recover bin[]
		for (int d = md_alive; d >= 1; d--)
			bin[d] = bin[d - 1];
		bin[0] = 0; // 1 in original

		// main algorithm
		for (int i = 0; i < n_alive; i++) {

			int v = al[vert[i]]; // smallest degree vertex
			if(!alive.get(v))
				continue;
			
			int[] N_v = G.successorArray(v);
			int v_deg = G.outdegree(v);
			for (int ii = 0; ii < v_deg; ii++) {
				int u = N_v[ii];
				
				if(!alive.get(u))
					continue;
				
				if (cores[u] > cores[v]) {
					int du = cores[u];
					int pu = pos[al_idx[u]];
					int pw = bin[du];
					int w = al[vert[pw]];
					if (u != w) {
						pos[al_idx[u]] = pw;
						vert[pu] = al_idx[w];
						pos[al_idx[w]] = pu;
						vert[pw] = al_idx[u];
					}
					bin[du]++;
					cores[u]--;
				}
			}
		}
	}    

	ArrayList<Integer> getConnectedComponentCk(int u, boolean[] isNC) {
		ArrayList<Integer> cc = new ArrayList<Integer>();
		ConnectedComponentsCkDFS(u, cc, isNC);
		return cc;
	}

	void ConnectedComponentsCkDFS(int u, ArrayList<Integer> cc, boolean[] isNC) {
		cc.add(u);
		if(inPrevCom.get(u))
			isNC[0] = false;
		inPrevCom.set(u);
		
		int[] u_neighbors = G.successorArray(u);
		int u_deg = G.outdegree(u);
		for (int i = 0; i < u_deg; i++) {
			int v = u_neighbors[i];
			if (cores[v]>=k && !cc.contains(v))
				ConnectedComponentsCkDFS(v, cc, isNC);
		}		
	}
		
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		long estimatedTime;

		//args = new String[] {"edges02", "1", "2"}; //graph_basename, r, k
		//args = new String[] {"data1_astrocnet", "10", "2"}; //graph_basename, r, k
		//args = new String[] {"data12_uk-2005-edgeList", "10", "16"}; //graph_basename, r, k
		
		String basename = args[0];
		int k = Integer.valueOf(args[1]);
		int r = Integer.valueOf(args[2]);
		
		NC2_BZ kc = new NC2_BZ(basename);		
		kc.topCom( k, r );
			
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time elapsed = " + estimatedTime / 1000.0);
		System.out.println();
	}
}