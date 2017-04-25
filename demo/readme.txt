We can get webgraph files from http://law.di.unimi.it/datasets.php
Both the graph and it's transpose (inverse) are needed in order to 
create an undirected graph *efficiently*. 
For each graph, we need to download the .graph and .properties files. 
E.g. 
   wget http://data.law.di.unimi.it/webdata/dblp-2011/dblp-2011.graph
   wget http://data.law.di.unimi.it/webdata/dblp-2011/dblp-2011.properties

   wget http://data.law.di.unimi.it/webdata/dblp-2011/dblp-2011-t.graph
   wget http://data.law.di.unimi.it/webdata/dblp-2011/dblp-2011-t.properties


Next, we need to build the offsets for the webgraphs:
  java -cp "../lib/*" it.unimi.dsi.webgraph.BVGraph -o -O -L dblp-2011
  java -cp "../lib/*" it.unimi.dsi.webgraph.BVGraph -o -O -L dblp-2011-t

Now, we symmetrize the graph. 
That is, we make it undirected by adding for each edge (a,b) the edge (b,a). 
This amounts to taking the union of the graph with its transpose:
  java -cp "../lib/*" it.unimi.dsi.webgraph.Transform union dblp-2011 dblp-2011-t dblp-2011-sym
The resulting graph is dblp-2011-sym.

[This produces the same result as 
  java -cp "../lib/*" it.unimi.dsi.webgraph.Transform symmetrize dblp-2011 dblp-2011-sym
 but is more efficient. So, use union whenever the transpose exists.]

Finally, we remove selfloops (a,a):
  java -cp "../lib/*":"../bin" SelfLoopRemover dblp-2011-sym dblp-2011-sym-noself
The resulting graph is dblp-2011-sym-noself.
  
We compute the core numbers by running:
  java -cp "../lib/*":"../bin" KCoreWG_BZ dblp-2011-sym-noself
The result will be stored in a binary file: dblp-2011-sym-noself.cores
See Util.java for the format of this file. 

We also need a rank file to run the programs. 
The rank file contains for each node a rank number. E.g.
3, 2, 10, ...
The first number is the rank of node 0,
the second number is the rank of node 1, etc.
The rank captures the importance of a node. 
What's needed for all our algorithms is only the rank number in [0,n-1], 
rather than a raw importance number. 
We assume there are no ties. 
To generate ranks randomly, we do: 
  java -cp "../lib/*":"../bin" GenRank dblp-2011-sym-noself
This will generate a binary file: dblp-2011-sym-noself.rank
with rank numbers.
See Util.java for the format of this file.

-----------------------------
Run example:

  java -cp "../lib/*":"../bin" C1 dblp-2011-sym-noself 10 2

where r=10 and k=2.
The output looks like this: 
 
469229: [180363, 180250, 180371]
469230: [179500, 179504, 179514]
469231: [603813, 603809, 603821]
469232: [146342, 146348, 385739]
469233: [434401, 434400, 434403]
469234: [59279, 59277, 59281]
469235: [75389, 75386, 75387]
469236: [280765, 280766, 280767]
469237: [322674, 322677, 322682]
469238: [782254, 782154, 782272]

The first number here, 469229, is the rank of the lowest-rank node 
in the community that follows. 
The community is [180363, 180250, 180371]. 
Since, r=10, we find 10 communities. 

Replace C1 by C2, C3_BZ, etc, to run the other community finding 
algorithms (see paper for details).

For large scale experiments, use the following script:

  ./experiment.sh C1 dblp-2011-sym-noself

This will run C1 on dblp-2011-sym-noself with r=10,20,40,80,160,320 and k=2,4,8,16,32,64,128,512
The log file is dblp-2011-sym-noself.C1
To retrieve only the time for core from the rather large log file, do:

  grep "Time for core" dblp-2011-sym-noself.C1
This is the main time component to compute the communities. 

Again, replace C1 by C2, C3_BZ, etc, to run the other community finding
algorithms.
