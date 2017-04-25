import java.util.Random;

public class GenRank {

	public static void main(String[] args) throws Exception {
		//args = new String [] {"edges02"};
		//args = new String [] {"data1_astrocnet"};
		//args = new String [] {"data12_uk-2005-edgeList"};
		
		if(args.length != 1) {
			System.out.println("GenRank basename");
			System.exit(1);
		}
		
		String basename = args[0];
		it.unimi.dsi.webgraph.ImmutableGraph G = 
				it.unimi.dsi.webgraph.ImmutableGraph.loadOffline(basename);
		int n = G.numNodes();
		
		System.out.println("Initializing the rank array...");
		int[] rank = new int[n];
		for(int i=0; i<n; i++)
			rank[i] = i;
		System.out.println("Rank array initialized.");
		
		System.out.println("Shuffling the rank array...");
		Random rnd = new Random();
		// Shuffle array
        for (int i=n; i>1; i--) {
        	int j = rnd.nextInt(i);
        	//swap
        	int temp = rank[i-1];
        	rank[i-1] = rank[j];
        	rank[j] = temp;
        }
        System.out.println("Rank array shuffled.");
		
        System.out.println("Writing rank array to file...");
        Util.writeIntArrayToBinaryFile(rank, basename+".rank");
        System.out.println("Rank array written to file.");
	}
}
