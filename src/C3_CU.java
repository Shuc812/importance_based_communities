import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

//It starts from highest weighted vertices. No need to have the cores pre-computed.
public class C3_CU {
	it.unimi.dsi.webgraph.ImmutableGraph G;
	int n_orig; // number of nodes in the original graph.
	int n_alive; // number of alive nodes.
	int md_alive = 0; // max degree in the alive graph.
	int k; // given k

	int[] rank;
	int[] cores;
	int[] degrees;
	int[] al;
	int[] al_idx;
	boolean[] visited;
	boolean[] color;

	ArrayList<Integer> Vc;

	java.util.BitSet alive;

	public C3_CU(String basename) throws Exception {
		G = it.unimi.dsi.webgraph.ImmutableGraph.load(basename);
		
		n_orig = G.numNodes();
		visited = new boolean[G.numNodes()];
		color = new boolean[G.numNodes()];

		rank = new int[n_orig];
		cores = new int[n_orig];
		degrees = new int[n_orig];
		al = new int[n_orig];
		al_idx = new int[n_orig];
		alive = new java.util.BitSet(n_orig);

		n_alive = 0;
		System.out.println("Reading ranks...");
		Util.readIntArrayFromBinaryFile(rank, basename+".rank");
		System.out.println("Reading ranks finished!");

		// No need to read cores. It doesn't need pre-computed cores.
	}

	void topInfluentialCommunitiesAlg2(Integer r, Integer k) {
		this.k = k;
		System.out.println("r = " + r + "   k = " + k);

		// Continue with the algorithm
		long startTime = System.currentTimeMillis();
		long estimatedTime;

		int i = 1;
		for (int j = n_orig - 1; j >= 0; j--) {
			int u = rank[j];// weight high to low

			alive.set(u);// make node alive
			al[n_alive] = u;
			al_idx[u] = n_alive;
			n_alive++;

			updateCores(u);

			if (cores[u] >= k) {
				cnt_good++;
				// System.out.println(i + "\t" + u + ": " +
				// getConnectedComponentCk(u));
				System.out.println(getConnectedComponentCk(u));
				i++;
				if (i > r)
					break;
			}
		}

		// System.out.println("cnt_all=" + cnt_all);
		// System.out.println("cnt_good=" + cnt_good);

		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time for core part (r="+ r +", k=" + k + ") = " + estimatedTime / 1000.0);
	}

	int cnt_all = 0, cnt_good = 0;

	void updateCores(int u) {
		updateDegrees(u);
		/*
		 * if(degrees[u]>=k && d_k(u)>=k) { cnt_all++; KCoreCompute_alive(); }
		 */

		int[] u_neighbors = G.successorArray(u);
		int u_deg = G.outdegree(u);
		for (int i = 0; i < u_deg; i++) {
			int w = u_neighbors[i];
			if (alive.get(w))
				InsertEdge(u, w);
		}
	}
	
	void updateDegrees(int v) {
		int d = 0;
		int[] v_neighbors = G.successorArray(v);
		int v_deg = G.outdegree(v);
		for (int i = 0; i < v_deg; i++) {
			int w = v_neighbors[i];

			if (alive.get(w)) {
				d++;
				degrees[w]++;
				if (md_alive < degrees[w])
					md_alive = degrees[w];
			}
		}
		degrees[v] = d;
		if (md_alive < degrees[v])
			md_alive = degrees[v];
	}

	void InsertEdge(int u, int v) {
		for (int i = 0; i < G.numNodes(); i++) {
			visited[i] = false;
			color[i] = false;
		}
		Vc = new ArrayList<Integer>();
		for (int i = 0; i < G.numNodes(); i++) {
		//	System.out.println(i + " core is " + cores[i]);

		}
		//System.out.println("insert edge:("+u+" "+v+")");
		int c = 0;
		if (cores[u] > cores[v]) {
			c = cores[v];
			Color(v, c);
		//	System.out.println("Vc is "+Vc);
			//System.out.println("***************");
			RecolorInsert(v, c);
			UpdateInsert(c);
		} else {
			c = cores[u];
			Color(u, c);
		//	System.out.println("Vc is "+Vc);
			//System.out.println("***************");
			RecolorInsert(u, c);
			UpdateInsert(c);
		}
		for (int i = 0; i < G.numNodes(); i++) {
			//System.out.println(i + " core is " + cores[i]);
		}
	}

	void Color(int pick, int c) {
		Queue<Integer> Q = new LinkedBlockingQueue<Integer>();
		Q.offer(pick);
		visited[pick] = true;
		int v_deg = 0;
		int[] N_v;
		while (!Q.isEmpty()) {
			int v = Q.poll();
			N_v = G.successorArray(v);
			v_deg = G.outdegree(v);
			for (int ii = 0; ii < v_deg; ii++) {
				int w = N_v[ii];
				if (alive.get(w)) {
					if (visited[w] == false && cores[w] == c) {
						Q.offer(w);
						visited[w] = true;
					}
				}
			}
			if (color[v] == false) {
				Vc.add(v);
				color[v] = true;
			}
		}
	}

	void RecolorInsert(int pick, int c) {
		int flag = 0;
		int u_deg = 0;
		int[] N_u;
		for (int i = 0; i < Vc.size(); i++) {
			int u = (int) Vc.get(i);
			N_u = G.successorArray(u);
			u_deg = G.outdegree(u);

			if (color[u] == true) {
				int Xu = 0;
				for (int ii = 0; ii < u_deg; ii++) {
					int w = N_u[ii];
					if (alive.get(w)) {
					if (color[w] == true || cores[w] > c)
						Xu++;
				}
				}
				if (Xu <= c) {
					color[u] = false;
					flag = 1;
				}
			}
		}
		if (flag == 1)
			RecolorInsert(pick, c);
	}

	void UpdateInsert(int c) {
		for (int i = 0; i < Vc.size(); i++) {
			int w = (int) Vc.get(i);
			if (color[w] == true)
				cores[w] = c + 1;
		}
	}

	int d_k(int v) {
		int d = 0;
		int[] v_neighbors = G.successorArray(v);
		int v_deg = G.outdegree(v);
		for (int i = 0; i < v_deg; i++) {
			int w = v_neighbors[i];

			if (degrees[w] >= k)
				d++;
		}
		return d;
	}

	ArrayList<Integer> getConnectedComponentCk(int u) {
		ArrayList<Integer> cc = new ArrayList<Integer>();
		ConnectedComponentsCkDFS(u, cc);
		return cc;
	}

	void ConnectedComponentsCkDFS(int u, ArrayList<Integer> cc) {
		cc.add(u);
		int[] u_neighbors = G.successorArray(u);
		int u_deg = G.outdegree(u);
		for (int i = 0; i < u_deg; i++) {
			int v = u_neighbors[i];
			if (cores[v] >= k && !cc.contains(v))
				ConnectedComponentsCkDFS(v, cc);
		}
	}

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		long estimatedTime;

		//args = new String[] {"data1_astrocnet", "100", "2"}; //graph_basename, r, k
		//args = new String[] {"data1_astrocnet", "10", "2"}; //graph_basename, r, k
		//args = new String[] {"data12_uk-2005-edgeList", "100", "2"}; //graph_basename, r, k

		String basename = args[0];
		int k = Integer.valueOf(args[1]);
		int r = Integer.valueOf(args[2]);

		C3_CU kc = new C3_CU(basename);
		kc.topInfluentialCommunitiesAlg2(k, r);

		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time elapsed = " + estimatedTime / 1000.0);
		System.out.println();
	}
}
