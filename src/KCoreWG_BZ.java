import it.unimi.dsi.webgraph.ImmutableGraph;

import java.util.Iterator;

/**
 * K-core decomposition algorithm
 *
 * Outputs: array "int[] res" containing the core values for each vertex.  
 *
 * This is an implementation of the algorithm given in: 
 * V. Batagelj and M. Zaversnik. An o (m) algorithm for cores decomposition of networks. CoRR, 2003.
 *
 * @author Alex Thomo, thomo@uvic.ca, 2015
 */

public class KCoreWG_BZ {
	it.unimi.dsi.webgraph.ImmutableGraph G;
	int n;
	int maxDeg;
	boolean printprogress = false; 
	long E=0;
	
	public KCoreWG_BZ(String G_basename) throws Exception {
		G = it.unimi.dsi.webgraph.ImmutableGraph.load(G_basename); 
		n = G.numNodes();
		maxDeg = maxDegree(G);
	}
	
	// Computes the core decomposition, implements the Batagelz and Zaversnik algorithm
	public int[] KCoreCompute() {

		int n = G.numNodes();
		int md = maxDeg;
		int[] vert = new int[n];
		int[] pos = new int[n];
		int[] deg = new int[n];
		int[] bin = new int[md + 1]; // md+1 because we can have zero degree

		for (int d = 0; d <= md; d++)
			bin[d] = 0;
		for (int v = 0; v < n; v++) {

			if (printprogress && v % 1000000 == 0)
				System.out.println(v);

			deg[v] = G.outdegree(v);
			E += deg[v];
			bin[deg[v]]++;
		}

		int start = 0; // start=1 in original, but no problem
		for (int d = 0; d <= md; d++) {
			int num = bin[d];
			bin[d] = start;
			start += num;
		}

		// bin-sort vertices by degree
		for (int v = 0; v < n; v++) {
			pos[v] = bin[deg[v]];
			vert[pos[v]] = v;
			bin[deg[v]]++;
		}
		// recover bin[]
		for (int d = md; d >= 1; d--)
			bin[d] = bin[d - 1];
		bin[0] = 0; // 1 in original

		// main algorithm
		for (int i = 0; i < n; i++) {

			if (printprogress && i % 1000000 == 0)
				System.out.println(i);

			int v = vert[i]; // smallest degree vertex
			int[] N_v = G.successorArray(v);
			int v_deg = G.outdegree(v);
			for (int ii = 0; ii < v_deg; ii++) {
				int u = N_v[ii];
				//Alex: avoid self-loops
				if(u == v)
					continue;
				//

				if (deg[u] > deg[v]) {
					int du = deg[u];
					int pu = pos[u];
					int pw = bin[du];
					int w = vert[pw];
					if (u != w) {
						pos[u] = pw;
						vert[pu] = w;
						pos[w] = pu;
						vert[pw] = u;
					}
					bin[du]++;
					deg[u]--;
				}
			}
		}

		return deg;
	}    
    
	public int maxDegree(ImmutableGraph G) {
		Iterator<Integer> degIter = G.outdegrees();
		int maxDegree = -1;
		while (degIter.hasNext()) {
			Integer deg = degIter.next();
			if (deg > maxDegree)
				maxDegree = deg;
		}
		return maxDegree;
	}
    
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		
		//args = new String[] {"edges02"};
		//args = new String[] {"data1_astrocnet"};
		//args = new String[] {"data12_uk-2005-edgeList"};
		
		if(args.length != 1) {
			System.out.println("KCoreWG_BZ graph_basename");
			System.exit(1);
		}
		
		String basename = args[0];
		
		System.out.println("Starting " + basename);
		KCoreWG_BZ kc3 = new KCoreWG_BZ(basename);
		
		int[] deg = kc3.KCoreCompute();
		int kmax = -1;
		double sum = 0;
		int cnt = 0;
		for(int i=0; i<deg.length; i++) {
			//System.out.print(i+":" + res[i] + " ");
			if(deg[i] > kmax) 
				kmax = deg[i];
			sum += deg[i];
			if(deg[i] > 0) cnt++;
		}
		System.out.println("|V|	|E|	dmax	kmax	kavg");
		System.out.println(kc3.n + "\t" + (kc3.E/2) + "\t" + kc3.maxDeg + "\t" + kmax + "\t" + (sum/cnt) );
		
		System.out.println("Writing core numbers into a binary file...");
		Util.writeIntArrayToBinaryFile(deg, basename+".cores");
		
		long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(args[0] + ": Time elapsed = " + estimatedTime/1000.0);
	}
}
