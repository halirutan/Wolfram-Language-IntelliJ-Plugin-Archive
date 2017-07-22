(* Mathematica Package  *)
(* Created by Mathematica Plugin for IntelliJ IDEA, see http://wlplugin.halirutan.de/ *)

(* :Title: IGraph/M   *)
(* :Context: IGraphM` *)
(* :Author: szhorvat  *)
(* :Date: 2015-08-28  *)

(* :Package Version: %%version%% *)
(* :Mathematica Version: %%mathversion%% *)
(* :Copyright: (c) 2017 Szabolcs Horvát *)
(* :Keywords: igraph, graphs, networks, LibraryLink *)
(* :Discussion: igraph interface for Mathematica, see http://igraph.org/ *)


(* Mathematica version check *)
If[Not@OrderedQ[{10.0, 2}, {$VersionNumber, $ReleaseNumber}],
  Print["IGraph/M requires Mathematica 10.0.2 or later.  Aborting."];
  Abort[]
]


BeginPackage["IGraphM`"];

Needs["HierarchicalClustering`"]; (* do not place in BeginPackage -- we do not want this context exported *)

Unprotect["IGraphM`*", "IGraphM`Developer`*", "IGraphM`Information`*"];

(* Privately load and configure LTemplate *)
(* NOTE: replaced in build script *)
Get["LTemplate`LTemplatePrivate`"];
ConfigureLTemplate["MessageSymbol" -> IGraphM, "LazyLoading" -> False];

(***** Usage mesages *****)

IGraphM::usage = "IGraphM is a symbol to which igraph related messages are associated.";

`Information`$Version::usage = "IGraphM`Information`$Version is a string that gives the version of the currently loaded IGraph/M package.";
`Developer`Recompile::usage = "IGraphM`Developer`Recompile[] recompiles the IGraphM library and reloads the functions.";
`Developer`GetInfo::usage = "IGraphM`Developer`GetInfo[] returns useful information about IGraph/M and the system it is running on, for debugging and troubleshooting purposes.";
PrependTo[$ContextPath, $Context <> "Developer`"];

IGDocumentation::usage = "IGDocumentation[] opens the IGraph/M documentation.";

mytest::usage = "Test";

IGData::usage =
    "IGData[] returns a list of available items.\n" <>
    "IGData[item] returns the requested item.";

IGVersion::usage = "IGVersion[] returns the IGraph/M version along with the version of the igraph library in use.";
IGSeedRandom::usage = "IGSeedRandom[seed] seeds the random number generator used by igraph.";

IGLCF::usage =
    "IGLCF[shifts, repeats] creates a graph from LCF notation.\n" <>
    "IGLCF[shifts, repeats, vertexCount] creates a graph from LCF notation with the number of vertices specified.";
IGMakeLattice::usage = "IGMakeLattice[{d1, d2, \[Ellipsis]}] generates a lattice graph of the given dimensions.";
IGGraphAtlas::usage =
    "IGGraphAtlas[n] returns graph number n from An Atlas of Graphs by Ronald C. Read and Robin J. Wilson, Oxford University Press, 1998. " <>
    "This function is provided for convenience; if you are looking for a specific named graph, use the builtin GraphData function.";
IGKautzGraph::usage = "IGKautzGraph[m, n] returns a Kautz graph on m+1 characters with string length n+1.";
IGKaryTree::usage =
    "IGKaryTree[n] returns a binary tree with n vertices.\n" <>
    "IGKaryTree[n, k] returns a k-ary tree with n vertices.";
IGCompleteGraph::usage = "IGCompleteGraph[n] returns a complete graph on n vertices.";
IGCompleteAcyclicGraph::usage = "IGCompleteAcyclicGraph[n] returns a complete acyclic directed graph on n vertices.";
IGDeBruijnGraph::usage = "IGDeBruijnGraph[m, n] returns a De Bruijn graph on m characters and string length n.";
IGChordalRing::usage = "IGChordalRing[m, w] returns an extended chordal ring on n vertices, based on the matrix w.";

IGEmptyGraph::usage =
    "IGEmptyGraph[] returns a graph with no edges or vetices.\n" <>
    "IGEmptyGraph[n] returns a graph with no edges and n vertices.";

IGConnectNeighborhood::usage =
    "IGConnectNeighborhood[graph] connects each vertex in graph to its 2nd order neighbourhood.\n" <>
    "IGConnectNeighborhood[graph, k] connects each vertex in graph to its order k neighbourhood. Weights and other graph properties are discarded.";

IGBetweenness::usage = "IGBetweenness[graph] gives a list of betweenness centralities for the vertices of graph.";
IGEdgeBetweenness::usage = "IGEdgeBetweenness[graph] gives a list of betweenness centralities for the edges of graph.";
IGCloseness::usage = "IGCloseness[graph] gives a list of closeness centralities for the vertices of graph.";

IGBetweennessEstimate::usage = "IGBetweennessEstimate[graph, cutoff] estimates vertex betweenness by considering only paths of at most length cutoff.";
IGEdgeBetweennessEstimate::usage = "IGEdgeBetweennessEstimate[graph, cutoff] estimates edge betweenness by considering only paths of at most length cutoff.";
IGClosenessEstimate::usage = "IGClosenessEstimate[graph, cutoff] estimates closeness centrality by considering only paths of at most length cutoff.";

IGPageRank::usage =
    "IGPageRank[graph] gives a list of PageRank centralities for the vertices of the graph.\n" <>
    "IGPageRank[graph, damping] gives a list of PageRank centralities for the vertices of the graph using damping factor damping.";

IGPersonalizedPageRank::usage =
    "IGPersonalizedPageRank[graph, reset] gives a list of personalized PageRank centralities for the vertices of the graph.\n" <>
    "IGPersonalizedPageRank[graph, reset, damping] gives a list of personalized PageRank centralities for the vertices of the graph using damping factor damping.";

IGEigenvectorCentrality::usage = "IGEigenvectorCentrality[graph] return the eigenvector centrality of each vertex.";
IGHubScore::usage = "IGHubScore[graph] returns Kleinberg's hub score for each vertex.";
IGAuthorityScore::usage = "IGAuthorityScore[graph] returns Kleinberg's authority score for each vertex.";
IGConstraintScore::usage = "IGConstraintScore[graph] returns Burt's constraint score for each vertex.";

IGRewire::usage = "IGRewire[graph, n] attempts to rewire the edges of graph n times while preserving its degree sequence. Weights and other graph properties are discarded.";
IGRewireEdges::usage = "IGRewireEdges[graph, p] rewires each edge of the graph with probability p. Weights and other graph properties are discarded.";

IGDirectedAcyclicGraphQ::usage = "IGDirectedAcyclicGraphQ[graph] tests if graph is directed and acyclic.";
IGConnectedQ::usage = "IGConnectedQ[graph] tests if graph is strongly connected.";
IGWeaklyConnectedQ::usage = "IGWeaklyConnectedQ[graph] tests if graph is weakly connected.";
IGGraphicalQ::usage =
    "IGGraphicalQ[degrees] tests if degrees is the degree sequence of any simple undirected graph.\n" <>
    "IGGraphicalQ[indegrees, outdegrees] tests if indegrees with outdegrees is the degree sequence of any simple directed graph.";
IGBipartiteQ::usage = "IGBipartiteQ[graph] tests if graph is bipartite.";

IGIsomorphicQ::usage = "IGIsomorphicQ[graph1, graph2] tests if graph1 and graph2 are isomorphic.";
IGSubisomorphicQ::usage = "IGSubisomorphicQ[subgraph, graph] tests if subgraph is contained within graph.";
IGIsoclass::usage = "IGIsoclass[graph] returns the isomorphism class of the graph. Used as the index into the vector returned by motif finding functions. See IGData[] to get list of graphs ordered by isoclass.";

IGBlissCanonicalLabeling::usage =
    "IGBlissCanonicalLabeling[graph] computes a canonical integer labeling of the graph vertices. Using this labeling brings representations of isomorphic graphs to the same form.\n" <>
    "IGBlissCanonicalLabeling[{graph, colorSpec}] computes a canonical integer labeling for the vertices of a vertex coloured graph.";

IGBlissCanonicalPermutation::usage =
    "IGBlissCanonicalPermutation[graph] returns a permutation that, when applied to the adjacency matrices of isomorphic graphs, brings them to the same form.\n" <>
    "IGBlissCanonicalPermutation[{graph, colorSpec}] returns the canonical vertex permutation of a vertex coloured graph.";
IGBlissCanonicalGraph::usage =
    "IGBlissCanonicalGraph[graph] returns a canonical graph of graph, based on the canonical integer labeling.\n" <>
    "IGBlissCanonicalGraph[{graph, colorSpec}] returns a canonical graph of a vertex coloured graph, based on the canonical integer labeling.";
IGBlissIsomorphicQ::usage =
    "IGBlissIsomorphicQ[graph1, graph2] tests if graph1 and graph2 are isomorphic using the Bliss algorithm.\n" <>
    "IGBlissIsomorphicQ[{graph1, colorSpec}, {graph2, colorSpec}] tests if two vertex coloured graphs are isomorphic using the Bliss algorithm.";
IGBlissGetIsomorphism::usage =
    "IGBlissGetIsomorphism[graph1, graph2] returns one isomorphism between graph1 and graph2, if it exists.\n" <>
    "IGBlissGetIsomorphism[{graph1, colorSpec}, {graph2, colorSpec}] returns one isomorphism between two vertex colored graphs, if it exists.";
IGBlissAutomorphismCount::usage =
    "IGBlissAutomorphismCount[graph] returns the number of automorphisms of graph.\n" <>
    "IGBlissAutomorphismCount[{graph, colorSpec}] returns the number of automorphisms of a vertex coloured graph.";
IGBlissAutomorphismGroup::usage =
    "IGBlissAutomorphismGroup[graph] returns a set of generators for the automorphism group of graph. It is not guaranteed to be minimal.\n" <>
    "IGBlissAutomorphismGroup[{graph, colorSpec}] returns a set of generators for the automorphism group of a vertex coloured graph.";

IGVF2IsomorphicQ::usage =
    "IGVF2IsomorphicQ[graph1, graph2] tests if graph1 and graph2 are isomorphic using the VF2 algorithm.\n" <>
    "IGVF2IsomorphicQ[{graph1, colorSpec}, {graph2, colorSpec}] tests if vertex or edge coloured graphs graph1 and graph2 are isomorphic.";
IGVF2FindIsomorphisms::usage =
    "IGVF2FindIsomorphisms[graph1, graph2] finds all isomorphisms between graph1 and graph2 using the VF2 algorithm.\n" <>
    "IGVF2FindIsomorphisms[graph1, graph2, n] finds at most n isomorphisms between graph1 and graph2.\n" <>
    "IGVF2FindIsomorphisms[{graph1, colorSpec}, {graph2, colorSpec}] finds all isomorphisms between vertex or edge coloured graphs graph1 and graph2.\n" <>
    "IGVF2FindIsomorphisms[{graph1, colorSpec}, {graph2, colorSpec}, n] finds at most n isomorphisms between vertex or edge coloured graphs graph1 and graph2.";
IGVF2GetIsomorphism::usage = "IGVF2GetIsomorphism[graph1, graph2] returns one isomorphism between graph1 and graph2, if it exists.\n" <>
    "IGVF2GetIsomorphism[{graph1, colorSpec}, {graph2, colorSpec}] returns one isomorphism between two vertex or edge colored graphs, if it exists.";
IGVF2SubisomorphicQ::usage =
    "IGVF2SubisomorphicQ[subgraph, graph] tests if subgraph is contained in graph using the VF2 algorithm.\n" <>
    "IGVF2SubisomorphicQ[{subgraph, colorSpec}, {graph, colorSpec}] tests if vertex or edge coloured subgraph is contained in graph.";
IGVF2FindSubisomorphisms::usage =
    "IGVF2FindSubisomorphisms[subgraph, graph] finds all subisomorphisms from subgraph to graph using the VF2 algorithm.\n" <>
    "IGVF2FindSubisomorphisms[subgraph, graph, n] finds at most n subisomorphisms from subgraph to graph.\n" <>
    "IGVF2FindSubisomorphisms[{subgraph, colorSpec}, {graph, colorSpec}] finds all subisomorphisms from vertex or edge coloured subgraph to graph.\n" <>
    "IGVF2FindSubisomorphisms[{subgraph, colorSpec}, {graph, colorSpec}, n] finds at most n subisomorphisms from vertex or edge coloured subgraph to graph.";
IGVF2GetSubisomorphism::usage = "IGVF2GetSubisomorphism[subgraph, graph] returns one subisomorphism from subgraph to graph, if it exists.\n" <>
    "IGVF2GetSubisomorphism[{subgraph, colorSpec}, {graph, colorSpec}] returns one subisomorphism from a vertex or edge coloured subgraph to graph, if it exists.";
IGVF2IsomorphismCount::usage =
    "IGVF2IsomorphismCount[graph1, graph2] returns the number of isomorphisms between graph1 and graph2.\n" <>
    "IGVF2IsomorphismCount[{graph1, colorSpec}, {graph2, colorSpec}] returns the number of isomorphisms between vertex or edge coloured graphs graph1 and graph2. Note that this is not the same as simply counting the automorphisms of one graph if their colourings differ.";
IGVF2SubisomorphismCount::usage =
    "IGVF2SubisomorphismCount[subgraph, graph] returns the number of mappings from subgraph to graph.\n" <>
    "IGVF2SubisomorphismCount[{subgraph, colorSpec}, {graph, colorSpec}] returns the number of mappings from vertex or edge coloured subgraph to graph.";

IGLADSubisomorphicQ::usage =
    "IGLADSubisomorphicQ[subgraph, graph] tests if subgraph is contained in graph. Use the \"Induced\" -> True option to look for induced subgraphs.\n" <>
    "IGLADSubisomorphicQ[{subgraph, colorSpec}, {graph, colorSpec}] tests if a vertex coloured subgraph is contained in graph.";
IGLADGetSubisomorphism::usage =
    "IGLADGetSubisomorphism[subgraph, graph] returns one subisomorphism from subgraph to graph, if it exists.\n" <>
    "IGLADGetSubisomorphism[{subgraph, colorSpec}, {graph, colorSpec}] returns one subisomorphism from a vertex coloured subgraph to graph.";
IGLADFindSubisomorphisms::usage =
    "IGLADFindSubisomorphisms[subgraph, graph] finds all subisomorphisms from subgraph to graph.\n" <>
    "IGLADFindSubisomorphisms[{subgraph, colorSpec}, {graph, colorSpec}] finds all subisomorphisms from a vertex coloured subgraph to graph.";
IGLADSubisomorphismCount::usage =
    "IGLADSubisomorphismCount[subgraph, graph] counts subisomorphisms from subgraph to graph.\n" <>
    "IGLADSubisomorphismCount[{subgraph, colorSpec}, {graph, colorSpec}] counts subisomorphisms from a vertex coloured subgraph to graph.";

IGTopologicalOrdering::usage =
    "IGTopologicalOrdering[graph] returns a permutation that sorts the vertices in topological order. " <>
    "Note that the values returned are vertex indices, not vertex names.";
IGFeedbackArcSet::usage = "IGFeedbackArcSet[graph] computes a feedback edge set of graph. Removing these edges makes the graph acyclic.";

IGDyadCensus::usage = "IGDyadCensus[graph] classifies dyad in the graph into mutual, asymmetric or null states.";
IGTriadCensus::usage = "IGTriadCensus[graph] classifies triads in the graph into 16 possible states, labelled using MAN (mutual, asymmetric, null) notation.";
IGMotifs::usage = "IGMotifs[graph, motifSize] returns the motif distribution of graph. See IGIsoclass and IGData for motif ordering.";
IGMotifsTotalCount::usage = "IGMotifsTotalCount[graph, motifSize] returns the total count of motifs (connected subgraphs) of the given size in the graph.";
IGMotifsEstimateTotalCount::usage = "IGMotifsEstimateTotalCount[graph, motifSize, sampleSize] estimates the total count of motifs (connected subgraphs) of the given size in graph, based on a sample of the give size.";
IGTriangles::usage = "IGTriangles[graph] lists all triangles in the graph. Edge directions are ignored.";
IGAdjacentTriangleCount::usage =
    "IGAdjacentTriangleCount[graph] counts the triangles each vertex participates in. Edge directions are ignored.\n" <>
    "IGAdjacentTriangleCount[graph, vertex] counts the triangles vertex participates in.\n" <>
    "IGAdjacentTriangleCount[graph, {vertex1, vertex2, \[Ellipsis]}] counts the triangles the specified vertices participate in.";

IGDegreeSequenceGame::usage =
    "IGDegreeSequenceGame[degrees] generates an undirected random graph with the given degree sequence.\n" <>
    "IGDegreeSequenceGame[indegrees, outdegrees] generates a directed random graph with the given in- and out-degree sequences.";

IGKRegularGame::usage = "IGKRegularGame[n, k] generates a k-regular graph on n vertices, i.e. a graph in which all vertices have degree k.";
IGStochasticBlockModelGame::usage = "IGStochasticBlockModelGame[ratesMatrix, blockSizes] samples from a stochastic block model.";
IGForestFireGame::usage =
    "IGForestFireGame[n, fwprob]\n" <>
    "IGForestFireGame[n, fwprob, bwfactor]\n" <>
    "IGForestFireGame[n, fwprob, bwfactor, nambassadors]";

IGBipartiteGameGNM::usage = "IGBipartiteGameGNM[n1, n2, m] generates a bipartite random graph with n1 and n2 vertices in the two partitions and m edges.";
IGBipartiteGameGNP::usage = "IGBipartiteGameGNP[n1, n2, p] generates a bipartite Bernoulli random graph with n1 and n2 vertices in the two partitions and connection probability p.";

IGGeometricGame::usage = "IGGeometricGame[n, radius] generates an n-vertex geometric random graph on the unit square.";

IGBarabasiAlbertGame::usage =
    "IGBarabasiAlbertGame[n, k] generates an n-vertex Barabási–Albert random graph by adding a new vertex with k out-edges in each step.\n" <>
    "IGBarabasiAlbertGame[n, {k2, k3, \[Ellipsis]}] generates an n-vertex Barabási–Albert random graph by adding a new vertex with k2, k3, \[Ellipsis] out-edges in each step.\n" <>
    "IGBarabasiAlbertGame[n, k, {\[Beta], a}] generates a Barabási–Albert random graph with preferential attachment probabilities proportional to d^\[Beta] + a where d is the vertex (in-)degree.";

IGWattsStrogatzGame::usage =
    "IGWattsStrogatzGame[n, p] generates an n-vertex Watts–Strogatz random graph using rewiring probability\n" <>
    "IGWattsStrogatzGame[n, p, k] rewires a lattice where each node is connected to its k-neighbourhood.\n" <>
    "IGWattsStrogatzGame[n, p, {dim, k}] rewires a dim dimensional lattice of n^dim vertices, where each node is connected to its k-neighbourhood.";

IGStaticFitnessGame::usage =
    "IGStaticFitnessGame[m, {f1, f2, \[Ellipsis]}] generates a random undirected graph with m edges where edge i <-> j is inserted with probability proportional to f_i\[Times]f_j.\n" <>
    "IGStaticFitnessGame[m, {fout1, fout2, \[Ellipsis]}, {fin1, fin2, \[Ellipsis]}] generates a random directed graph with m edges where edge i -> j is inserted with probability proportional to fout_i\[Times]fin_j.";

IGStaticPowerLawGame::usage =
    "IGStaticPowerLawGame[n, m, exp] generates a random graph with n vertices and m edges, having a power-law degree distribution with the given exponent.\n" <>
    "IGStaticPowerLawGame[n, m, expOut, expIn] generates a random directed graph with n vertices and m edges, having power-law in- and out-degree distributions with the given exponents.";

IGGrowingGame::usage = "IGGrowingGame[n, k] generates a growing random graph with n vertices, adding a new vertex and k new edges in each step.";

IGCallawayTraitsGame::usage = "IGCallawayTraitsGame[n, k, typeWeights, preferenceMatrix]";
IGEstablishmentGame::usage = "IGEstablishmentGame[n, k, typeWeights, preferenceMatrix]";

IGDistanceMatrix::usage =
    "IGDistanceMatrix[graph] computes the shortest path length between each vertex pair in graph.\n" <>
    "IGDistanceMatrix[graph, fromVertices] computes the shortest path lengths between from the given vertices to each vertex in graph.\n" <>
    "IGDistanceMatrix[graph, fromVertices, toVertices] computes the shortest path lengths between the given vertices in graph.";
IGDistanceCounts::usage = "IGDistanceCounts[graph] computes a histogram of unweighted shortest path lengths between all vertex pairs. The kth element of the result is the count of shortest paths of length k.";
IGDistanceHistogram::usage =
    "IGDistanceHistogram[graph, binsize] computes a histogram of weighted all-pair shortest path lengths in graph with the given bin size. In case of undirected graphs, path lengths are double counted.\n" <>
    "IGDistanceHistogram[graph, binsize, from] computes a histogram of weighted shortest path lengths in graph for the given starting vertices and bin size.\n" <>
    "IGDistanceHistogram[graph, binsize, from, to] computes a histogram of weighted shortest path lengths in graph for the given starting and ending vertices and bin size.";
IGAveragePathLength::usage = "IGAveragePathLength[graph] returns the average of all-pair unweighted shortest path lengths of the graph. Vertex pairs between which there is no path are excluded.";
IGGirth::usage = "IGGirth[graph] returns the length of the shortest cycle of the graph. The graph is treated as undirected, self-loops and multi-edges are ignored.";
IGDiameter::usage = "IGDiameter[graph] computes the diameter of graph.";
IGFindDiameter::usage = "IGFindDiameter[graph] returns a longest shortest path in graph, i.e. a shortest path with length equal to the graph diameter.";
IGEccentricity::usage =
    "IGEccentricity[graph] returns the eccentricity of all vertices.\n" <>
    "IGEccentricity[graph, vertex] returns the eccentricity of the given vertex.\n" <>
    "IGEccentricity[graph, {vertex1, vertex2, \[Ellipsis]}] returns the eccentricity of the given vertices.";
IGRadius::usage = "IGRadius[graph] returns the unweighted graph radius.";

IGCliques::usage =
    "IGCliques[graph] returns all complete subgraphs (cliques) in graph. Note that this is different from the builtin FindCliques[], which finds maximal cliques.\n" <>
    "IGCliques[graph, {min, max}] returns all complete subgraphs between sizes min and max.\n" <>
    "IGCliques[graph, max] returns all complete subgraphs of size at most max.\n" <>
    "IGCliques[graph, {n}] returns all complete subgraphs of size n.";
IGCliqueSizeCounts::usage =
    "IGCliqueSizeCounts[graph] computes a histogram of clique sizes in graph. The kth element of the result is the number of k-cliques.\n" <>
    "IGCliqueSizeCounts[graph, {min, max}] computes a histogram of clique sizes between min and max in graph.\n" <>
    "IGCliqueSizeCounts[graph, max] computes a histogram of clique sizes no larger than max in graph.\n" <>
    "IGCliqueSizeCounts[graph, {n}] counts cliques of size n in graph.";
IGMaximalCliqueSizeCounts::usage =
    "IGMaximalCliqueSizeCounts[graph] computes a histogram of maximal clique sizes in graph. The kth element of the result is the number of maximal k-cliques.\n" <>
    "IGMaximalCliqueSizeCounts[graph, {min, max}] computes a histogram of maximal clique sizes between min and max in graph.\n" <>
    "IGMaximalCliqueSizeCounts[graph, max] computes a histogram of maximal clique sizes no larger than max in graph.\n" <>
    "IGMaximalCliqueSizeCounts[graph, {n}] counts maximal cliques of size n in graph.";
IGMaximalCliques::usage =
    "IGMaximalCliques[graph] returns all maximal cliques in graph.\n" <>
    "IGMaximalCliques[graph, {min, max}] returns all maximal cliques between sizes min and max.\n" <>
    "IGMaximalCliques[graph, max] returns all maximal cliques of size at most max.\n" <>
    "IGMaximalCliques[graph, {n}] returns all maximal cliques of size n.";
IGMaximalCliquesCount::usage =
    "IGMaximalCliquesCount[graph] counts all maximal cliques in graph.\n" <>
    "IGMaximalCliquesCount[graph, {min, max}] counts all maximal cliques between sizes min and max.\n" <>
    "IGMaximalCliquesCount[graph, max] counts all maximal cliques of size at most max.\n" <>
    "IGMaximalCliquesCount[graph, {n}] counts all maximal cliques of size n.";
IGLargestCliques::usage = "IGLargestCliques[graph] returns the largest cliques in graph.";
IGCliqueNumber::usage = "IGCliqueNumber[graph] returns the clique number of graph. The clique number is the size of the largest clique.";
IGWeightedCliques::usage = "IGWeightedCliques[graph, {min, max}] returns all complete subgraphs having total vertex weight between min and max.";
IGMaximalWeightedCliques::usage = "IGMaximalWeightedCliques[graph, {min, max}] returns all maximal cliques having total vertex weight between min and max.";
IGLargestWeightedCliques::usage = "IGLargestWeightedCliques[graph] returns the cliques having the largest total vertex weight in graph.";
IGWeightedCliqueNumber::usage = "IGWeightedCliqueNumber[graph] return the maximum total vertex weight of any clique in graph.";

IGIndependentVertexSets::usage =
    "IGIndependentVertexSets[graphs] returns all independent vertex sets of graph.\n" <>
    "IGIndependentVertexSets[graphs, {min, max}] returns all independent vertex sets of graph between sizes min and max.\n" <>
    "IGIndependentVertexSets[graphs, max] returns all independent vertex sets up to size max.\n" <>
    "IGIndependentVertexSets[graphs, {n}] returns all independent vertex sets of size n.";
IGLargestIndependentVertexSets::usage = "IGLargestIndependentVertexSets[graph] finds the largest independent vertex sets of graph.";
IGMaximalIndependentVertexSets::usage = "IGMaximalIndependentVertexSets[graph] finds the maximal independent vertex sets of graph.";
IGIndependenceNumber::usage = "IGIndependenceNumber[graph] returns the independence number of graph. The independence number is the size of the largest independent vertex set.";

IGLayoutRandom::usage = "IGLayoutRandom[graph] lays out vertices randomly in the unit square.";
IGLayoutCircle::usage = "IGLayoutCircle[graph] lays out vertices on a circle.";
IGLayoutSphere::usage = "IGLayoutSphere[graph] lays out vertices approximately uniformly distributed on a sphere.";
IGLayoutGraphOpt::usage = "IGLayoutGraphOpt[graph, options] lays out the graph using the GraphOpt algorithm.";
IGLayoutKamadaKawai::usage = "IGLayoutKamadaKawai[graph, options] lays out the graph using the Kamada–Kawai algorithm (similar to \"SpringEmbedding\").";
IGLayoutKamadaKawai3D::usage = "IGLayoutKamadaKawai3D[graph, options] lays out the graph in 3D using the Kamada–Kawai algorithm (similar to \"SpringEmbedding\").";
IGLayoutFruchtermanReingold::usage = "IGLayoutFruchtermanReingold[graph, options] lays out the graph using the Fruchterman–Reingold algorithm (similar to \"SpringElectricalEmbedding\").";
IGLayoutFruchtermanReingold3D::usage = "IGLayoutFruchtermanReingold3D[graph, options] lays out the graph using the Fruchterman–Reingold algorithm (similar to \"SpringElectricalEmbedding\").";
IGLayoutGEM::usage = "IGLayoutGEM[graph, options] lays out the graph using the GEM algorithm.";
IGLayoutDavidsonHarel::usage = "IGLayoutDavidsonHarel[graph, options] lays out the graph using the Davidson–Harel algorithm, based on simulated annealing.";
(* IGLayoutMDS::usage = "IGLayoutMDS[graph]"; *)
IGLayoutReingoldTilford::usage = "IGLayoutReingoldTilford[graph, options] lays out a tree using the Reingold–Tilford algorithm.";
IGLayoutReingoldTilfordCircular::usage = "IGLayoutReingoldTilfordCircular[graph, options] lays out a tree radially using the Reingold–Tilford algorithm.";
IGLayoutDrL::usage = "IGLayoutDrL[graph, options] lays out the graph using the DrL layout generator.";
IGLayoutDrL3D::usage = "IGLayoutDrL3D[graph, options] lays out the graph in 3D using the DrL layout generator.";
IGLayoutBipartite::usage = "IGLayoutBipartite[graph, options] lays out a bipartite graph, minimizing the number of edge crossings. Partitions can be specified manually using the \"BipartitePartitions\" option.";

IGGlobalClusteringCoefficient::usage = "IGGlobalClusteringCoefficient[graph] returns the global clustering coefficient of graph.";
IGLocalClusteringCoefficient::usage = "IGLocalClusteringCoefficient[graph] returns the local clustering coefficient of each vertex.";
IGAverageLocalClusteringCoefficient::usage = "IGAverageLocalClusteringCoefficient[graph] returns the average local clustering coefficient of graph.";
IGWeightedClusteringCoefficient::usage = "IGWeightedClusteringCoefficient[graph] computes the weighted local clustering coefficient, as defined by A. Barrat et al. (2004) http://dx.doi.org/10.1073/pnas.0400087101";

IGCocitationCoupling::usage =
    "IGCocitationCoupling[graph] returns the cocitation coupling between all vertex pairs in graph. The cocitation coupling of two vertices is the number of vertices connecting to both of them (with directed edges).\n" <>
    "IGCocitationCoupling[graph, vertex] returns the cocitation coupling of vertex with all other vertices in graph.\n" <>
    "IGCocitationCoupling[graph, {vertex1, vertex2, \[Ellipsis]}] returns the cocitation coupling of vertex1, vertex2, \[Ellipsis] with all other vertices in graph.";

IGBibliographicCoupling::usage =
    "IGBibliographicCoupling[graph] returns the bibliographic coupling between all vertex pairs in graph. The bibliographic coupling of two vertices is the number of vertices they both connect to (with directed edges).\n" <>
    "IGBibliographicCoupling[graph, vertex] returns the bibliographic coupling of vertex with all other vertices in graph.\n" <>
    "IGBibliographicCoupling[graph, {vertex1, vertex2, \[Ellipsis]}] returns the bibliographic coupling of vertex1, vertex2, \[Ellipsis] with all other vertices in graph.";

IGJaccardSimilarity::usage =
    "IGJaccardSimilarity[graph] returns the Jaccard similarity between all pairs of vertices.\n" <>
    "IGJaccardSimilarity[graph, {vertex1, vertex2, \[Ellipsis]}] returns the Jaccard similarity between the given vertices.";

IGDiceSimilarity::usage =
    "IGDiceSimilarity[graph] returns the Dice similarity between all pairs of vertices.\n" <>
    "IGDiceSimilarity[graph, {vertex1, vertex2, \[Ellipsis]}] returns the Dice similarity between the given vertices.";

IGInverseLogWeightedSimilarity::usage =
    "IGInverseLogWeightedSimilarity[graph] returns the inverse log-weighted similarity between all pairs of vertices.\n" <>
    "IGInverseLogWeightedSimilarity[graph, vertex] returns the inverse log-weighted similarity of vertex to all other vertices.\n" <>
    "IGInverseLogWeightedSimilarity[graph, {vertex1, vertex2, \[Ellipsis]}] returns the inverse log-weighted similarity between the given vertices.";

IGMaximumCardinalitySearch::usage = "IGMaximumCardinalitySearch[graph] assigns a rank to each vertex, from 1 to n, according to the maximum cardinality search algorithm. Visiting the vertices of the graph by decreasing rank is equivalent to always visiting the next vertex with the most already visited neighbours. Ties are broken randomly.";
IGChordalQ::usage = "IGChordalQ[graph] tests if graph is chordal.";
IGChordalCompletion::usage = "IGChordalCompletion[graph] returns a set of edges that, when added to graph, make it chordal. The edge-set this function returns is usually not minimal.";

IGMinSeparators::usage = "IGMinSeparators is deprecated. Use IGMinimumSeparators instead."; (* deprecated in favout of IGMinimumSeparators *)

IGMinimumSeparators::usage = "IGMinimumSeparators[graph] returns all separator vertex sets of minimum size. A vertex set is a separator if its removal disconnects the graph. Edge directions are ignored.";

IGMinimalSeparators::usage = "IGMinimalSeparators[graph] returns all minimal separator vertex sets. A vertex set is a separator if its removal disconnects the graph. Edge directions are ignored."

IGVertexSeparatorQ::usage = "IGVertexSeparatorQ[graph, {vertex1, vertex2, \[Ellipsis]}] tests if the given set of vertices disconnects the graph. Edge directions are ignored.";

IGMinimalVertexSeparatorQ::usage = "IGMinimalVertexSeparatorQ[graph, {vertex1, vertex2, \[Ellipsis]}] tests if the given vertex set is a minimal separator. Edge directions are ignored.";

IGArticulationPoints::usage = "IGArticulationPoints[graph] finds the articulation points of graph. A vertex is an articulation point if its removal increases the number of connected components in the graph.";

IGBiconnectedComponents::usage = "IGBiconnectedComponents[graph] returns the maximal biconnected subgraphs of graph. A graph is biconnected if the removal of any single vertex does not disconnect it. Size-one components are not returned.";

IGGraphlets::usage =
    "IGGraphlets[graph]\n" <>
    "IGGraphlets[graph, nIterations]";
IGGraphletBasis::usage = "IGGraphletBasis[graph]";
IGGraphletProject::usage =
    "IGGraphletProject[graph, cliques]\n" <>
    "IGGraphletProject[graph, cliques, nIterations]";

IGVertexConnectivity::usage =
    "IGVertexConnectivity[graph] returns the smallest number of vertices whose deletion disconnects graph.\n" <>
    "IGVertexConnectivity[graph, s, t] returns the smallest number of vertices whose deletion disconnects vertices s and t in graph.";

IGEdgeConnectivity::usage =
    "IGEdgeConnectivity[graph] returns the smallest number of edges whose deletion disconnects graph.\n" <>
    "IGEdgeConnectivity[graph, s, t] returns the smallest number of edges whose deletion disconnects vertices s and t in graph.";

IGClusterData::usage = "IGClusterData[association] represents the output of community detection functions. Properties can be queried using IGClusterData[\[Ellipsis]][\"property\"].";

IGCohesiveBlocks::usage = "IGCohesiveBlocks[graph] computes the cohesive block structure of a simple undirected graph.";

IGCompareCommunities::usage =
    "IGCompareCommunities[clusterdata1, clusterdata2] compares two community structures given as IGClusterData objects using all available methods.\n" <>
    "IGCompareCommunities[clusterdata1, clusterdata2, method] compares two community structures using method.\n" <>
    "IGCompareCommunities[clusterdata1, clusterdata2, {method1, \[Ellipsis]}] compares two community structures using each given method.\n" <>
    "IGCompareCommunities[graph, communities1, communities2] compares two partitionings of the graph vertices into communities using all available methods.\n" <>
    "IGCompareCommunities[graph, communities1, communities2, method] compares two community structures using method.\n" <>
    "IGCompareCommunities[graph, communities1, communities2, {method1, \[Ellipsis]}] compares two community structures using each given method.";

Test::usage =
    "Test[graph, {{v11, v12, \[Ellipsis]}, {v21, v22, \[Ellipsis]}, \[Ellipsis]}] computes the modularity of graph based on the given partitioning of the vertex list into communities.\n" <>
    "Test[graph, clusterdata] computes the modularity of graph based on the community structure represented as an IGClusterData object.";
IGCommunitiesEdgeBetweenness::usage = "IGCommunitiesEdgeBetweenness[graph] finds communities using the Girvan–Newman algorithm.";
IGCommunitiesGreedy::usage = "IGCommunitiesGreedy[graph] finds communities using greedy optimization of modularity.";
IGCommunitiesWalktrap::usage =
    "IGCommunitiesWalktrap[graph] finds communities via short random walks (of length 4 by default).\n" <>
    "IGCommunitiesWalktrap[graph, steps] finds communities via random walks of length steps.";
IGCommunitiesOptimalModularity::usage = "IGCommunitiesOptimalModularity[graph] finds communities by maximizing the modularity through integer programming.";
IGCommunitiesMultilevel::usage = "IGCommunitiesMultilevel[graph] finds communities using the Louvain method.";
IGCommunitiesLabelPropagation::usage = "IGCommunitiesLabelPropagation[graph] finds communities by assigning labels to each vertex and then updating them by majority voting in the neighbourhood of the vertex.";
IGCommunitiesInfoMAP::usage =
    "IGCommunitiesInfoMAP[graph] finds communities using the InfoMAP algorithm. The default number of trials is 10.\n" <>
    "IGCommunitiesInfoMAP[graph, trials]";
IGCommunitiesSpinGlass::usage = "IGCommunitiesSpinGlass[graph] finds communities using a spin glass model and simulated annealing.";
IGCommunitiesLeadingEigenvector::usage = "IGCommunitiesLeadingEigenvector[graph] finds communities based on the leading eigenvector of the modularity matrix.";

IGGomoryHuTree::usage = "IGGomoryHuTree[graph]";

IGUnfoldTree::usage = "IGUnfoldTree[graph]";

IGBipartitePartitions::usage = "IGBipartitePartitions[graph] partitions the vertices of a bipartite graph.";

IGVertexContract::usage = "IGVertexContract[g, {{v1, v2, \[Ellipsis]}, \[Ellipsis]}] returns a graph in which the specified vertex sets are contracted into single vertices.";

IGRandomWalk::usage = "IGRandomWalk[graph, start, steps] takes a random walk of length steps on graph, starting at vertex 'start'. The list of traversed vertices is returned.";

IGVertexTransitiveQ::usage = "IGVertexTransitiveQ[graph] tests if graph is vertex transitive.";
IGEdgeTransitiveQ::usage = "IGEdgeTransitiveQ[graph] tests if graph is edge transitive.";
IGSymmetricQ::usage = "IGSymmetricQ[graph] tests if graph is symmetric, i.e. it is both vertex transitive and edge transitive.";

IGSpanningTree::usage = "IGSpanningTree[graph] returns a minimum spanning tree of graph. Edge directions are ignored. Edge weights are taken into account and are preserved in the tree.";

Begin["`Private`"];

(* Function to abort loading and leave a clean $ContextPath behind *)
packageAbort[] := (End[]; EndPackage[]; Abort[])


(***** Package variables *****)


$packageVersion    = "%%version%% (%%date%%)";
$packageDirectory  = DirectoryName[$InputFileName];
$systemID = $SystemID;
(* On OS X libraries use libc++ ABI since M10.4 and libstdc++ ABI up to M10.3.  We need separate binaries. *)
If[$OperatingSystem === "MacOSX", $systemID = $systemID <> If[$VersionNumber <= 10.3, "-libstdc++", "-libc++"]];
$libraryDirectory  = FileNameJoin[{$packageDirectory, "LibraryResources", $systemID}];
$sourceDirectory   = FileNameJoin[{$packageDirectory, "LibraryResources", "Source"}];
$buildSettingsFile = FileNameJoin[{$packageDirectory, "BuildSettings.m"}];

IGraphM`Information`$Version = $packageVersion;


template = LTemplate["IGraphM",
  {
    LClass["IGlobal",
      {
        LFun["init", {}, "Void"],
        LFun["seedRandom", {Integer}, "Void"],
        LFun["version", {}, "UTF8String"],
        LFun["compilationDate", {}, "UTF8String"],

        LFun["infOrNanQ", {{Real, _, "Constant"}}, True|False],

        (* Graph related functions that do not use the graph data structure *)

        LFun["graphicalQ", {{Real, 1, "Constant"} (* outdeg *), {Real, 1, "Constant"} (* indeg *)}, True|False]
      }
    ],
    LClass["IG",
      {
        (* Create *)

        LFun["fromEdgeList", {{Real, _, "Constant"} (* edges *), Integer (* vertex count *), True|False (* directed *)}, "Void"],
        (* LFun["fromEdgeListML", LinkObject], *)
        LFun["fromLCF", {Integer, {Real, 1, "Constant"}, Integer}, "Void"],
        LFun["makeLattice", {{Real, 1, "Constant"}, Integer (* nei *), True|False (* directed *), True|False (* mutual *), True|False (* periodic *)}, "Void"],
        LFun["kautz", {Integer, Integer}, "Void"],
        LFun["tree", {Integer, Integer, True|False (* directed *)}, "Void"],
        LFun["completeGraph", {Integer, True|False (* directed *), True|False (* loops *)}, "Void"],
        LFun["completeCitationGraph", {Integer, True|False (* directed *)}, "Void"],
        LFun["deBruijn", {Integer, Integer}, "Void"],
        LFun["extendedChordalRing", {Integer, {Real, 2}}, "Void"],
        LFun["graphAtlas", {Integer}, "Void"],

        (* Directedness *)

        LFun["makeDirected", {}, "Void"],
        LFun["makeUndirected", {}, "Void"],

        (* Weights *)

        LFun["setWeights", {{Real, 1, "Constant"}}, "Void"],
        LFun["getWeights", {}, {Real, 1}],
        LFun["clearWeights", {}, "Void"],
        LFun["weightedQ", {}, True|False],

        (* Games *)

        LFun["degreeSequenceGame", {{Real, 1, "Constant"} (* outdeg *), {Real, 1, "Constant"} (* indeg *), Integer (* method *)}, "Void"],
        LFun["kRegularGame", {Integer, Integer, True|False (* directed *), True|False (* multiple *)}, "Void"],
        LFun["stochasticBlockModel", {{Real, 2, "Constant"}, {Integer, 1, "Constant"}, True|False (* directed *), True|False (* loops *)}, "Void"],
        LFun["forestFireGame", {Integer (* vertex count *), Real (* fwprob *), Real (* bwratio *), Integer (* nambs *), True|False (* directed *)}, "Void"],

        LFun["bipartiteGameGNM", {Integer (* n1 *), Integer (* n2 *), Integer (* m *), True|False (* directed *), True|False (* bidirectional *)}, "Void"],
        LFun["bipartiteGameGNP", {Integer (* n1 *), Integer (* n2 *), Real (* p *), True|False (* directed *), True|False (* bidirectional *)}, "Void"],

        LFun["geometricGame", {Integer (* n *), Real (* radius *), True|False (* periodic *)}, {Real, 2} (* coordinates *)],

        LFun["barabasiAlbertGame", {Integer (* n *), Real (* power *), Real (* A *), Integer (* m *), {Real, 1, "Constant"} (* mvec *), True|False (* directed *), True|False (* totalDegree *), Integer (* method *)}, "Void"],

        LFun["wattsStrogatzGame", {Integer (* dim *), Integer (* size *), Integer (* radius *), Real (* p *), True|False (* loops *), True|False (* multiple *)}, "Void"],

        LFun["staticFitnessGame", {Integer (* edges *), {Real, 1, "Constant"} (* out-fitness *), {Real, 1, "Constant"} (* in-fitness *), True|False (* loops *), True|False (* multiple *)}, "Void"],

        LFun["staticPowerLawGame", {Integer (* n *), Integer (* m *), Real (* expOut *), Real (* expIn *), True|False (* loops *), True|False (* multiple *), True|False (* finiteSizeCorrection *)}, "Void"],

        LFun["growingGame", {Integer (* n *), Integer (* m *), True|False (* directed *), True|False (* citation *)}, "Void"],

        LFun["callawayTraitsGame", {Integer (* n *), Integer (* types *), Integer (* k *), {Real, 1, "Constant"} (* type distribution *), {Real, 2, "Constant"} (* pref matrix *), True|False (* directed *)}, "Void"],

        LFun["establishmentGame", {Integer (* n *), Integer (* types *), Integer (* k *), {Real, 1, "Constant"} (* type distribution *), {Real, 2} (* pref matrix *), True|False (* directed *)}, "Void"],

        (* Modification *)

        LFun["connectNeighborhood", {Integer (* order *)}, "Void"],

        (* Structure *)

        LFun["edgeCount", {}, Integer],
        LFun["vertexCount", {}, Integer],

        LFun["edgeList", {}, {Real, 2}],

        (* Testing *)

        LFun["directedQ", {}, True|False],
        LFun["dagQ", {}, True|False],
        LFun["simpleQ", {}, True|False],
        LFun["connectedQ", {True|False (* strongly connected *)}, True|False],
        LFun["bipartiteQ", {}, True|False],

        (* Centrality *)

        LFun["betweenness", {True|False (* nobigint *)}, {Real, 1}],
        LFun["edgeBetweenness", {}, {Real, 1}],
        LFun["closeness", {True|False (* normalized *)}, {Real, 1}],

        LFun["betweennessEstimate", {Real (* cutoff *), True|False (* nobigint *)}, {Real, 1}],
        LFun["edgeBetweennessEstimate", {Real (* cutoff *)}, {Real, 1}],
        LFun["closenessEstimate", {Real (* cutoff *), True|False (* normalized *)}, {Real, 1}],

        LFun["pageRank", {Integer (* method *), Real (* damping *), True|False (* directed *), Integer (* powerNiter *), Real (* powerEpsilon *)}, {Real, 1}],
        LFun["personalizedPageRank", {Integer (* method *), {Real, 1, "Constant"}, Real (* damping *), True|False (* directed *), Integer (* powerNiter *), Real (* powerEpsilon *)}, {Real, 1}],
        LFun["eigenvectorCentrality", {True|False (* directed *), True|False (* nornalized *)}, {Real, 1}],
        LFun["hubScore", {True|False (* nornalized *)}, {Real, 1}],
        LFun["authorityScore", {True|False (* nornalized *)}, {Real, 1}],
        LFun["constraintScore", {}, {Real, 1}],

        (* Randomize *)

        LFun["rewire", {Integer (* n_trials *), True|False (* loops *)}, "Void"],
        LFun["rewireEdges", {Real (* probability *), True|False (* loops *), True|False (* multiple *)}, "Void"],

        (* Isomorphism *)

        LFun["isomorphic", {LExpressionID["IG"]}, True|False],
        LFun["subisomorphic", {LExpressionID["IG"]}, True|False],
        LFun["isoclass", {}, Integer],
        LFun["blissCanonicalPermutation", {Integer (* splitting heuristics *), {Integer, 1, "Constant"} (* colour *)}, {Real, 1}],
        LFun["blissIsomorphic", {LExpressionID["IG"], Integer (* splitting heuristics *), {Integer, 1, "Constant"} (* color1 *), {Integer, 1, "Constant"} (* color 2 *)}, True|False],
        LFun["blissFindIsomorphism", {LExpressionID["IG"], Integer (* splitting heuristics *), {Integer, 1, "Constant"} (* color1 *), {Integer, 1, "Constant"} (* color 2 *)}, {Real, 1}],
        LFun["blissAutomorphismCount", LinkObject],
        LFun["blissAutomorphismGroup", LinkObject],
        LFun["vf2Isomorphic", {LExpressionID["IG"], {Integer, 1, "Constant"}, {Integer, 1, "Constant"}, {Integer, 1, "Constant"}, {Integer, 1, "Constant"}}, True|False],
        LFun["vf2FindIsomorphisms", LinkObject],
        LFun["vf2Subisomorphic", {LExpressionID["IG"], {Integer, 1, "Constant"}, {Integer, 1, "Constant"}, {Integer, 1, "Constant"}, {Integer, 1, "Constant"}}, True|False],
        LFun["vf2FindSubisomorphisms", LinkObject],
        LFun["vf2IsomorphismCount", {LExpressionID["IG"], {Integer, 1, "Constant"}, {Integer, 1, "Constant"}, {Integer, 1, "Constant"}, {Integer, 1, "Constant"}}, Integer],
        LFun["vf2SubisomorphismCount", {LExpressionID["IG"], {Integer, 1, "Constant"}, {Integer, 1, "Constant"}, {Integer, 1, "Constant"}, {Integer, 1, "Constant"}}, Integer],
        LFun["ladSubisomorphic", {LExpressionID["IG"], True|False (* induced *)}, True|False],
        LFun["ladSubisomorphicColored", LinkObject],
        LFun["ladGetSubisomorphism", {LExpressionID["IG"], True|False (* induced *)}, {Real, 1}],
        LFun["ladGetSubisomorphismColored", LinkObject],
        LFun["ladFindSubisomorphisms", LinkObject],
        LFun["ladCountSubisomorphisms", {LExpressionID["IG"], True|False (* induced *)}, Integer],
        LFun["ladCountSubisomorphismsColored", LinkObject],

        (* Topological sorting and directed acylic graphs *)

        LFun["topologicalSorting", {}, {Real, 1}],
        LFun["feedbackArcSet", {True|False}, {Real, 1}],

        (* Motifs and subgraph counts *)

        LFun["dyadCensus", {}, {Integer, 1}],
        LFun["triadCensus", {}, {Real, 1}],
        LFun["motifs", {Integer (* size *), {Real, 1, "Constant"} (* cut_prob *)}, {Real, 1}],
        LFun["motifsNo", {Integer (* size *), {Real, 1, "Constant"} (* cut_prob *)}, Integer],
        LFun["motifsEstimate", {Integer (* size *), {Real, 1, "Constant"} (* cut_prob *), Integer (* sample_size *)}, Integer],

        LFun["triangles", {}, {Integer, 1}],
        LFun["countAdjacentTriangles", {{Real, 1, "Constant"}}, {Real, 1}],

        (* Shortest paths *)

        LFun["shortestPaths", {{Real, 1, "Constant"} (* from *), {Real, 1, "Constant"} (* to *)}, {Real, 2}],
        LFun["shortestPathCounts", {}, {Real, 1}],
        LFun["shortestPathWeightedHistogram", {Real (* bin size *), {Real, 1, "Constant"} (* from *), {Real, 1, "Constant"} (* to *), Integer (* method *)}, {Integer, 1}],
        LFun["averagePathLength", {}, Real],
        LFun["girth", {}, Real],
        LFun["radius", {}, Real],
        LFun["eccentricity", {{Real, 1, "Constant"}}, {Real, 1}],

        LFun["shortestPathsDijkstra", {{Real, 1, "Constant"} (* from *), {Real, 1, "Constant"} (* to *)}, {Real, 2}],
        LFun["shortestPathsBellmanFord", {{Real, 1, "Constant"} (* from *), {Real, 1, "Constant"} (* to *)}, {Real, 2}],
        LFun["shortestPathsJohnson", {{Real, 1, "Constant"} (* from *), {Real, 1, "Constant"} (* to *)}, {Real, 2}],

        LFun["diameter", {True|False (* by components *)}, Integer],
        LFun["findDiameter", {True|False (* by components *)}, {Real, 1}],
        LFun["diameterDijkstra", {True|False (* by components *)}, Real],
        LFun["findDiameterDijkstra", {True|False (* by components *)}, {Real, 1}],

        (* Cliques *)

        LFun["cliques", {Integer, Integer}, {Integer, 1}],
        LFun["cliqueDistribution", {Integer, Integer}, {Real, 1}],
        LFun["maximalCliques", {Integer, Integer}, {Integer, 1}],
        LFun["largestCliques", {}, {Integer, 1}],
        LFun["maximalCliquesCount", {Integer, Integer}, Integer],
        LFun["maximalCliqueDistribution", {Integer, Integer}, {Real, 1}],
        LFun["cliqueNumber", {}, Integer],
        LFun["cliquesWeighted", {Integer (* min_weight *), Integer (* max_weight *), {Real, 1, "Constant"} (* vertex_weights *), True|False (* maximal *)}, {Integer, 1}],
        LFun["largestCliquesWeighted", {{Real, 1, "Constant"} (* vertex_weights *)}, {Integer, 1}],
        LFun["cliqueNumberWeighted", {{Real, 1, "Constant"} (* vertex_weights *)}, Integer],

        (* Independent vertex sets *)

        LFun["independentVertexSets", {Integer, Integer}, {Integer, 1}],
        LFun["largestIndependentVertexSets", {}, {Integer, 1}],
        LFun["maximalIndependentVertexSets", {}, {Integer, 1}],
        LFun["independenceNumber", {}, Integer],

        (* Graph drawing (layouts) *)

        LFun["layoutRandom", {}, {Real, 2}],
        LFun["layoutCircle", {}, {Real, 2}],
        LFun["layoutSphere", {}, {Real, 2}],

        LFun["layoutGraphOpt",
          {{Real, 2, "Constant"} (* initial position *), True|False (* use initial *),
            Integer (* niter *),
            Real (* charge *), Real (* mass *), Real (* spring length *),
            Real (* spring constant *), Real (* max sa movement *)},
          {Real, 2}
        ],

        LFun["layoutKamadaKawai",
          {{Real, 2, "Constant"} (* initial position *), True|False (* use initial *),
            Integer (* maxiter *), Real (* epsilon *), Real (* kkconst *)},
          {Real, 2}
        ],

        LFun["layoutKamadaKawai3D",
          {{Real, 2, "Constant"} (* initial position *), True|False (* use initial *),
            Integer (* maxiter *), Real (* epsilon *), Real (* kkconst *)},
          {Real, 2}
        ],

        LFun["layoutFruchtermanReingold",
          {{Real, 2, "Constant"} (* initial position *), True|False (* use initial *),
            Integer (* niter *), Real (* start_temp *), Integer (* grid method *)},
          {Real, 2}
        ],

        LFun["layoutFruchtermanReingold3D",
          {{Real, 2, "Constant"} (* initial position *), True|False (* use initial *),
            Integer (* niter *), Real (* start_temp *)},
          {Real, 2}
        ],

        LFun["layoutGEM",
          {{Real, 2, "Constant"} (* initial position *), True|False (* use initial *),
            Integer (* maxiter *), Real (* temp_min *), Real (* temp_max *), Real (* temp_init *)},
          {Real, 2}
        ],

        LFun["layoutDavidsonHarel",
          {{Real, 2, "Constant"} (* initial position *), True|False (* use initial *),
            Integer (* maxiter *), Integer (* fineiter *), Real (* cool_fact *),
            Real (* weight_node_dist *), Real (* weight_border *),
            Real (* weight_edge_lengths *), Real (* weight_edge_crossings *),
            Real (* weight_node_edge_dist *)},
          {Real, 2}
        ],

        LFun["layoutMDS", {{Real, 2, "Constant"}, Integer}, {Real, 2}],

        LFun["layoutReingoldTilford", {{Real, 1, "Constant"} (* roots *), True|False (* directed *)}, {Real, 2}],
        LFun["layoutReingoldTilfordCircular", {{Real, 1, "Constant"} (* roots *), True|False (* directed *)}, {Real, 2}],

        LFun["layoutDrL", {{Real, 2, "Constant"} (* initial positions *), True|False (* use initial *), Integer (* settings template *)}, {Real, 2}],
        LFun["layoutDrL3D", {{Real, 2, "Constant"} (* initial positions *), True|False (* use initial *), Integer (* settings template *)}, {Real, 2}],

        LFun["layoutBipartite", {{Integer, 1, "Constant"} (* types *), Real (* hgap *), Real (* vgap *), Integer (* maxiter *)}, {Real, 2}],

        (* Clustering coefficient *)

        LFun["transitivityUndirected", {}, Real],
        LFun["transitivityLocalUndirected", {}, {Real, 1}],
        LFun["transitivityAverageLocalUndirected", {}, Real],
        LFun["transitivityBarrat", {}, {Real, 1}],

        (* Similarity *)

        LFun["similarityCocitation", {{Real, 1, "Constant"}}, {Real, 2}],
        LFun["similarityBibcoupling", {{Real, 1, "Constant"}}, {Real, 2}],
        LFun["similarityJaccard", {{Real, 1, "Constant"}, True|False (* self loops *)}, {Real, 2}],
        LFun["similarityDice", {{Real, 1, "Constant"}, True|False (* self loops *)}, {Real, 2}],
        LFun["similarityInverseLogWeighted", {{Real, 1, "Constant"}}, {Real, 2}],

        (* Chordal graphs *)

        LFun["maximumCardinalitySearch", {}, {Real, 1}],
        LFun["chordalQ", {}, True|False],
        LFun["chordalCompletion", {}, {Real, 1}],

        (* Vertex separators *)

        LFun["minimumSizeSeparators", {}, {Integer, 1}],
        LFun["minimalSeparators", {}, {Integer, 1}],
        LFun["separatorQ", {{Real, 1, "Constant"}}, True|False],
        LFun["minSeparatorQ", {{Real, 1, "Constant"}}, True|False],

        LFun["vertexConnectivity", {}, Integer],
        LFun["edgeConnectivity", {}, Integer],
        LFun["vertexConnectivityST", {Integer, Integer}, Integer],
        LFun["edgeConnectivityST", {Integer, Integer}, Integer],
        LFun["cohesiveBlocks", LinkObject],

        (* Articulation points *)

        LFun["articulationPoints", {}, {Real, 1}],
        LFun["biconnectedComponents", {}, {Integer, 1}],

        (* Graphlets *)

        LFun["graphlets", LinkObject],
        LFun["graphletBasis", LinkObject],
        LFun["graphletProject", LinkObject],

        (* Community detection *)

        LFun["modularity", {{Real, 1, "Constant"}}, Real],
        LFun["compareCommunities", {{Real, 1, "Constant"}, {Real, 1, "Constant"}, Integer (* method *)}, Real],
        LFun["communityEdgeBetweenness", LinkObject],
        LFun["communityWalktrap", LinkObject],
        LFun["communityFastGreedy", LinkObject],
        LFun["communityOptimalModularity", LinkObject],
        LFun["communityMultilevel", LinkObject],
        LFun["communityLabelPropagation", LinkObject],
        LFun["communityInfoMAP", LinkObject],
        LFun["communitySpinGlass", LinkObject],
        LFun["communityLeadingEigenvector", LinkObject],

        (* Maximum flow *)

        LFun["gomoryHuTree", {LExpressionID["IG"], {Real, 1, "Constant"}}, {Real, 1}],

        (* Unfold tree *)

        LFun["unfoldTree", {LExpressionID["IG"], {Real, 1, "Constant"} (* roots *), True|False (* directed *)}, {Real, 1}],

        (* Bipartite graphs *)

        LFun["bipartitePartitions", {}, {Integer, 1}],

        (* Vertex contraction *)
        LFun["contractVertices", {{Real, 1, "Constant"}}, "Void"],

        (* Random walk *)
        LFun["randomWalk", {Integer, Integer}, {Real, 1}],

        (* Spanning tree *)
        LFun["spanningTree", {}, {Real, 1}]
      }
    ]
  }
];


(***** Compilation, loading and initialization *****)

$buildSettings = None;
If[FileExistsQ[$buildSettingsFile], Get[$buildSettingsFile] ]


(* Add $libraryDirectory to $LibraryPath in case package is not installed in Applications. *)
If[Not@MemberQ[$LibraryPath, $libraryDirectory],
  PrependTo[$LibraryPath, $libraryDirectory]
]


Recompile::build = "No build settings found. Please check BuildSettings.m."

Recompile[] :=
    Module[{},
      If[$buildSettings === None,
        Message[Recompile::build];
        Return[$Failed]
      ];
      If[Not@DirectoryQ[$libraryDirectory],
        CreateDirectory[$libraryDirectory]
      ];
      SetDirectory[$sourceDirectory];
      Quiet@UnloadTemplate[template];
      CompileTemplate[template, {"IGlobal.cpp"},
        "ShellCommandFunction" -> Print, "ShellOutputFunction" -> Print,
        "TargetDirectory" -> $libraryDirectory,
        Sequence @@ $buildSettings
      ];
      ResetDirectory[];
      LoadIGraphM[]
    ]


igraphGlobal (* IGlobal object. There should only be a single object of this type; it's set in LoadIGraphM[] below. *)

LoadIGraphM[] :=
    Module[{deps},
      deps = FileNameJoin[{$libraryDirectory, "dependencies.m"}];
      Check[
        If[FileExistsQ[deps], Get[deps]],
        Return[$Failed]
      ];
      If[LoadTemplate[template] === $Failed,
        Return[$Failed]
      ];
      igraphGlobal = Make["IGlobal"];
      igraphGlobal@"init"[];
    ]


(* Load library, compile if necessary. *)
If[LoadIGraphM[] === $Failed,
  Print[Style["Loading failed, trying to recompile ...", Red]];
  If[Recompile[] === $Failed
    ,
    Print[Style["Cannot load or compile library. \[FreakedSmiley] Aborting.", Red]];
    packageAbort[]
    ,
    Print[Style["Successfully compiled and loaded the library. \[HappySmiley]", Red]];
  ]
  ,
  If[Not@MemberQ[Stack[], BeginPackage],
    Print["IGraph/M " <> $packageVersion];
    Print["Evaluate IGDocumentation[] to get started."]
  ]
]


(***** GetInfo for troubleshooting *****)

GetInfo[] :=
    Module[{res = "", igver, osver},
      res = StringJoin[res, "Mathematica version: \n", System`$Version, "; Release number: ", ToString[$ReleaseNumber], "\n\n"];
      res = StringJoin[res, "Package version: \n", $packageVersion, "\n\n"];
      res = StringJoin[res, "Package location: \n", ToString@FindFile["IGraphM`"], "\n\n"];
      res = StringJoin[res, "Library location: \n", ToString@FindLibrary["IGraphM"], "\n\n"];
      igver = Quiet@IGVersion[];
      res = StringJoin[res, "IGVersion[]: \n", If[StringQ[igver], igver, "Failed."], "\n\n"];
      res = StringJoin[res, "Build settings: \n", ToString[$buildSettings], "\n\n"];
      osver = Quiet@StringTrim@Switch[$OperatingSystem,
        "MacOSX", Import["!sw_vers", "String"],
        "Unix", Import["!uname -a", "String"] <> Import["!lsb_release -a 2>/dev/null", "String"],
        "Windows", Import["!cmd /C ver", "String"]
      ];
      res = StringJoin[res, "Operating system: \n", If[StringQ[osver], osver, "Failed."]]; (* no newline after last item *)
      res
    ]


(***** General messages *****)

IGraphM::lytcrd = "The graph doesn't already have existing vertex coordinates. The \"Continue\" -> True layout option will be ignored.";
IGraphM::lytdim = "The existing vertex coordinates do not have the appropriate dimension for this layout algorithm. The \"Continue\" -> True layout option will be ignored.";
IGraphM::lytcnt = "`` is not a valid value for the \"Continue\" layout option.";
IGraphM::lytaln = "`` is not a valid value for the \"Align\" layout option."


(***** Helper functions *****)

(* Common definitions *)
Get["IGraphM`Common`"]

(* For error handling: *)

igTag (* private tag for throw/catch *)
throw[val_] := Throw[val, igTag]

SetAttributes[catch, HoldFirst]
catch[expr_] := Catch[expr, igTag]

check[val_LibraryFunctionError] := throw[val] (* TODO: change to throw[$Failed] *)
check[$Failed] := throw[$Failed]
check[HoldPattern[LibraryFunction[___][___]]] := throw[$Failed]
check[val_] := val

sck[HoldPattern[LibraryFunction[___][___]]] := $Failed
sck[val_] := val


(* For argument checking: *)

nonNegIntVecQ = VectorQ[#, Internal`NonNegativeMachineIntegerQ]&;
intVecQ = VectorQ[#, Developer`MachineIntegerQ]&;
positiveNumericQ = NumericQ[#] && TrueQ@Positive[#]&;
nonnegativeNumericQ = NumericQ[#] && TrueQ@NonNegative[#]&;
positiveVecQ = VectorQ[#, Positive]&;

(* Replace Infinity by 0 *)
infToZero[arg_] := Replace[arg, Infinity -> 0]

(* Unpack array containing infinities or indeterminates *)
(* TODO: Test on all platforms that unpacking such arrays produces usable Infinity and Indeterminate *)
fixInfNaN[arr_?Developer`PackedArrayQ] := If[igraphGlobal@"infOrNanQ"[arr], Developer`FromPackedArray[arr], arr]
fixInfNaN[arr_] := arr

stream[x_]:= x^2;

(* Import compressed expressions. Used in IGData. *)
(* Avoid Import because it doesn't work during kernel initialization. *)
zimport[filename_] :=
    Module[{stream, str},
      stream = OpenRead[filename];
      str = Read[stream, Record, RecordSeparators -> {}];
      Close[stream];
      Uncompress[str]
    ]


(* Get an IG compatible edge list. *)
(* This implementation attempts to select the fastest method based on the internal representation
   of the graph. With the "Simple" representation, IndexGraph is very fast. With "Incidence" it's
   slower than the Lookup method. With "NullGraph", performance doesn't matter.

   While GraphComputation`GraphRepresentation is an internal undocumented function, hopefully this
   is robust against changes as both branches of the If are valid ways to retrieve
   the edge list for any graph. They only differ in performance.
*)
igEdgeList[graph_] :=
    Developer`ToPackedArray@If[GraphComputation`GraphRepresentation[graph] === "Simple",
      Flatten[EdgeList@IndexGraph[graph, 0], 1, If[DirectedGraphQ[graph], DirectedEdge, UndirectedEdge]]
      ,
      Lookup[
        AssociationThread[VertexList[graph], Range@VertexCount[graph] - 1],
        Flatten[EdgeList[graph], 1, If[DirectedGraphQ[graph], DirectedEdge, UndirectedEdge]]
      ]
    ]
(* igEdgeList[graph_] := List @@@ EdgeList@IndexGraph[graph, 0]; *)

(* Convert IG format vertex or edge index vector to Mathematica format. *)
igIndexVec[expr_LibraryFunctionError] := expr (* hack: allows LibraryFunctionError to fall through *)
igIndexVec[arr_] := 1 + Round[arr]

igDirectedQ[graph_] := DirectedGraphQ[graph] && Not@EmptyGraphQ[graph]

(* TODO: Find out how to implement this in a more robust way.
   We only want edge-weighted graphs, not vertex weighted ones. *)
(* Warning: PropertyValue[g, EdgeWeight] fails on the null graph. This is why we test with WeightedGraphQ first. *)
igWeightedGraphQ = WeightedGraphQ[#] && PropertyValue[#, EdgeWeight] =!= Automatic &;

IGraphM::invw = "Invalid edge weight vector. Edge weights will be ignored.";

(* Create IG object from Mathematica Graph. Must be used when edge ordering matters. *)
igMake[g_] :=
    With[{ig = Make["IG"]},
      ig@"fromEdgeList"[igEdgeList[g], VertexCount[g], igDirectedQ[g]];
      If[igWeightedGraphQ[g],
        Check[
          ig@"setWeights"[PropertyValue[g, EdgeWeight]],
          Message[IGraphM::invw]
        ]
      ];
      ig
    ]

(* Fast version. Use only for unweighted graphs and when edge ordering doesn't matter. *)
igMakeFast[g_?MultigraphQ] := igMake[g]
igMakeFast[g_?EmptyGraphQ] :=
    With[{ig = Make["IG"]},
      ig@"fromEdgeList"[{}, VertexCount[g], False];
      ig
    ]
igMakeFast[g_] :=
    With[{ig = Make["IG"]},
      If[DirectedGraphQ[g], (* empty graphs handled as undirected above *)
        ig@"fromEdgeList"[AdjacencyMatrix[g]["NonzeroPositions"] - 1, VertexCount[g], True],
        ig@"fromEdgeList"[UpperTriangularize[AdjacencyMatrix[g]]["NonzeroPositions"] - 1, VertexCount[g], False]
      ];
      ig
    ]

(* Fast version. Use for graphs that may be weighted when edge ordering doesn't matter. *)
igMakeFastWeighted[g_?MultigraphQ] := igMake[g]
igMakeFastWeighted[g_?EmptyGraphQ] :=
    With[{ig = Make["IG"]},
      ig@"fromEdgeList"[{}, VertexCount[g], False];
      ig
    ]
igMakeFastWeighted[g_] :=
    With[{ig = Make["IG"]},
      If[igWeightedGraphQ[g],
        If[DirectedGraphQ[g], (* empty graphs handled as undirected above *)
          With[{wam = WeightedAdjacencyMatrix[g]},
            ig@"fromEdgeList"[wam["NonzeroPositions"] - 1, VertexCount[g], True];
            Check[ig@"setWeights"[wam["NonzeroValues"]], Message[IGraphM::invw]]
          ]
          ,
          With[{wam = UpperTriangularize@WeightedAdjacencyMatrix[g]},
            ig@"fromEdgeList"[wam["NonzeroPositions"] - 1, VertexCount[g], False];
            Check[ig@"setWeights"[wam["NonzeroValues"]], Message[IGraphM::invw]]
          ]
        ]
        ,
        If[DirectedGraphQ[g],
          ig@"fromEdgeList"[AdjacencyMatrix[g]["NonzeroPositions"] - 1, VertexCount[g], True],
          ig@"fromEdgeList"[UpperTriangularize[AdjacencyMatrix[g]]["NonzeroPositions"] - 1, VertexCount[g], False]
        ];
      ];
      ig
    ]

(* Create Mathematica Graph from IG object. *)
igToGraph[ig_] :=
    Graph[
      Range[ig@"vertexCount"[]],
      igIndexVec[ig@"edgeList"[]],
      DirectedEdges -> ig@"directedQ"[]
    ]

(* Convert vertex indices to vertex names. *)
igVertexNames[graph_][indices_] := Part[VertexList[graph], indices]


partitionRagged[v_List, l_?VectorQ] := MapThread[Take[v, {#1, #2}] &, Module[{a = Accumulate[l]}, {a - l + 1, a}]]

(* Unpacks an index list representing vertex sets from an integer array,
   To be used in conunction with IG::packListIntoIntTensor() *)
igUnpackVertexSet[graph_][packed_] :=
    With[{len = First[packed]},
      partitionRagged[
        Part[VertexList[graph], 1 + packed[[len + 2 ;; All]]],
        packed[[2 ;; len + 1]]
      ]
    ]

(* convert vertex list to IG format *)
vss[graph_][All] := {}
vss[graph_][vs_List] := Check[VertexIndex[graph, #] - 1& /@ vs, throw[$Failed]]

vs[graph_][v_] := Check[VertexIndex[graph, v] - 1, throw[$Failed]]


vertexWeightedQ[graph_] := WeightedGraphQ[graph] && PropertyValue[graph, VertexWeight] =!= Automatic

(***** Public functions *****)

SyntaxInformation[IGDocumentation] = {"ArgumentsPattern" -> {}};
IGDocumentation[] :=
    If[$Notebooks,
      If[$Version == 11.1 && $OperatingSystem === "Unix", (* work around dyfunctional address bar in 11.1/Linux *)
        NotebookOpen@Documentation`ResolveLink["paclet:IGraphM/IGDocumentation"],
        Documentation`HelpLookupPacletURI["paclet:IGraphM/IGDocumentation"]
      ];
      ,
      Print["Built-in documentation is only available when running with a Front End.\nSee the online version at http://szhorvat.net/mathematica/IGraphM"]
    ]


(*  IGData  *)

$igData := $igData = zimport@FileNameJoin[{$packageDirectory, "IGData.mz"}];
$igDataCategories := $igDataCategories = GroupBy[Select[Keys[$igData], ListQ], First];
$igDataAll := $igDataAll = Join[$igData, $igDataCategories];
SyntaxInformation[IGData] = {"ArgumentsPattern" -> {_.}};
IGData[] := Keys[$igData]
IGData[item_] := Lookup[$igDataAll, Key[item], Missing["NotAvailable"]]

(* NOTE: replaced in build script *)
addCompletion[IGData, {Join[Keys[$igDataCategories], Select[Keys[$igData], StringQ]]}];

(* General (global) *)

SyntaxInformation[IGVersion] = {"ArgumentsPattern" -> {}};
IGVersion[] :=
    "IGraph/M " <> $packageVersion <>
    "\nigraph " <> igraphGlobal@"version"[] <> " (" <> igraphGlobal@"compilationDate"[] <> ")\n" <>
    $System;


SyntaxInformation[IGSeedRandom] = {"ArgumentsPattern" -> {_}};
IGSeedRandom[seed_?Internal`NonNegativeMachineIntegerQ] := sck@igraphGlobal@"seedRandom"[seed]


(* Create *)

Options[IGLCF] = { GraphLayout -> "CircularEmbedding" };
SyntaxInformation[IGLCF] = {"ArgumentsPattern" -> {_, _., _., OptionsPattern[]}, "OptionNames" -> optNames[IGLCF, Graph]};
IGLCF[shifts_?intVecQ, repeats : _?Internal`PositiveMachineIntegerQ : 1, n : (_?Internal`PositiveMachineIntegerQ | Automatic) : Automatic, opt : OptionsPattern[{IGLCF, Graph}]] :=
    catch@Block[{ig = Make["IG"], numVertices},
      numVertices = Replace[n, Automatic :> Length[shifts] repeats];
      If[numVertices < 2,
        IGEmptyGraph[numVertices]
        ,
        check@ig@"fromLCF"[numVertices, shifts, repeats];
        applyGraphOpt[GraphLayout -> OptionValue[GraphLayout], opt]@igToGraph[ig]
      ]
    ]


Options[IGMakeLattice] = {
  "Radius" -> 1, DirectedEdges -> False, "Mutual" -> False, "Periodic" -> False
};
SyntaxInformation[IGMakeLattice] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGMakeLattice, Graph]};
IGMakeLattice[dims_?nonNegIntVecQ, opt : OptionsPattern[{IGMakeLattice, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"makeLattice"[dims, OptionValue["Radius"], TrueQ@OptionValue[DirectedEdges], TrueQ@OptionValue["Mutual"], TrueQ@OptionValue["Periodic"]];
      If[Length[dims] === 2 && Not@TrueQ@OptionValue["Periodic"],
        applyGraphOpt[opt, GraphLayout -> {"GridEmbedding", "Dimension" -> dims}]@igToGraph[ig],
        applyGraphOpt[opt]@igToGraph[ig]
      ]
    ]

SyntaxInformation[IGKautzGraph] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}, "OptionNames" -> optNames[Graph]};
IGKautzGraph[m_?Internal`NonNegativeMachineIntegerQ, n_?Internal`NonNegativeMachineIntegerQ, opt : OptionsPattern[Graph]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"kautz"[m, n];
      applyGraphOpt[opt]@igToGraph[ig]
    ]

Options[IGKaryTree] = {
  DirectedEdges -> False
};
SyntaxInformation[IGKaryTree] = {"ArgumentsPattern" -> {_, _., OptionsPattern[]}, "OptionNames" -> optNames[IGKaryTree, Graph]};
IGKaryTree[m_?Internal`NonNegativeMachineIntegerQ, n : _?Internal`PositiveMachineIntegerQ : 2, opt : OptionsPattern[{IGKaryTree, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"tree"[m, n, OptionValue[DirectedEdges]];
      applyGraphOpt[opt]@igToGraph[ig]
    ]

Options[IGCompleteGraph] = {
  DirectedEdges -> False, SelfLoops -> False,
  GraphLayout -> "CircularEmbedding"
};
SyntaxInformation[IGCompleteGraph] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGCompleteGraph, Graph]};
IGCompleteGraph[m_?Internal`NonNegativeMachineIntegerQ, opt : OptionsPattern[{IGCompleteGraph, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"completeGraph"[m, OptionValue[DirectedEdges], OptionValue[SelfLoops]];
      applyGraphOpt[GraphLayout -> OptionValue[GraphLayout], opt]@igToGraph[ig]
    ]

SyntaxInformation[IGCompleteAcyclicGraph] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[Graph]};
IGCompleteAcyclicGraph[m_?Internal`NonNegativeMachineIntegerQ, opt : OptionsPattern[Graph]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"completeCitationGraph"[m, True];
      applyGraphOpt[opt]@igToGraph[ig]
    ]

SyntaxInformation[IGDeBruijnGraph] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}, "OptionNames" -> optNames[Graph]};
IGDeBruijnGraph[m_?Internal`NonNegativeMachineIntegerQ, n_?Internal`NonNegativeMachineIntegerQ, opt : OptionsPattern[Graph]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"deBruijn"[m, n];
      applyGraphOpt[opt]@igToGraph[ig]
    ]

SyntaxInformation[IGChordalRing] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}, "OptionNames" -> optNames[Graph]};
IGChordalRing[m_?Internal`NonNegativeMachineIntegerQ, w_?(MatrixQ[#, NonNegative]&), opt : OptionsPattern[Graph]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"extendedChordalRing"[m, w];
      applyGraphOpt[opt]@igToGraph[ig]
    ]

SyntaxInformation[IGEmptyGraph] = {"ArgumentsPattern" -> {_., OptionsPattern[]}, "OptionNames" -> optNames[Graph]};
IGEmptyGraph[n : _?Internal`NonNegativeIntegerQ : 0, opt : OptionsPattern[Graph]] := Graph[Range[n], {}, opt]

SyntaxInformation[IGGraphAtlas] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGGraphAtlas, Graph]};
IGGraphAtlas[n_?Internal`NonNegativeMachineIntegerQ, opt : OptionsPattern[Graph]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"graphAtlas"[n];
      applyGraphOpt[opt]@igToGraph[ig]
    ]

(* Create (games) *)

Options[IGDegreeSequenceGame] = { Method -> "SimpleNoMultiple" };
igDegreeSequenceGameMethods = <| "VigerLatapy" -> 2, "SimpleNoMultiple" -> 1, "Simple" -> 0 |>;
amendUsage[IGDegreeSequenceGame, "Available Method options: <*Keys[igDegreeSequenceGameMethods]*>."];
SyntaxInformation[IGDegreeSequenceGame] = {"ArgumentsPattern" -> {_, _., OptionsPattern[]}, "OptionNames" -> optNames[IGDegreeSequenceGame, Graph]};

IGDegreeSequenceGame[degrees_?nonNegIntVecQ, opt : OptionsPattern[{IGDegreeSequenceGame, Graph}]] :=
    catch@applyGraphOpt[opt]@igDegreeSequenceGame[{}, degrees, OptionValue[Method]]

IGDegreeSequenceGame[indegrees_?nonNegIntVecQ, outdegrees_?nonNegIntVecQ, opt : OptionsPattern[{IGDegreeSequenceGame, Graph}]] :=
    catch@applyGraphOpt[opt]@igDegreeSequenceGame[indegrees, outdegrees, OptionValue[Method]]

igDegreeSequenceGame[indegrees_, outdegrees_, method_] :=
    (* no catch *) Block[{ig = Make["IG"]},
      check@ig@"degreeSequenceGame"[outdegrees, indegrees, Lookup[igDegreeSequenceGameMethods, method, -1]];
      igToGraph[ig]
    ]


Options[IGKRegularGame] = { "MultipleEdges" -> False, DirectedEdges -> False };
SyntaxInformation[IGKRegularGame] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}, "OptionNames" -> optNames[IGKRegularGame, Graph]};
IGKRegularGame[n_?Internal`NonNegativeMachineIntegerQ, k_?Internal`NonNegativeMachineIntegerQ, opt : OptionsPattern[{IGKRegularGame, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"kRegularGame"[n, k, OptionValue[DirectedEdges], OptionValue["MultipleEdges"]];
      applyGraphOpt[opt]@igToGraph[ig]
    ]

Options[IGStochasticBlockModelGame] = { SelfLoops -> False, DirectedEdges -> False };
SyntaxInformation[IGStochasticBlockModelGame] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}, "OptionNames" -> optNames[IGStochasticBlockModelGame, Graph]};
IGStochasticBlockModelGame[ratesMatrix_?SquareMatrixQ, blockSizes_?nonNegIntVecQ, opt : OptionsPattern[{IGStochasticBlockModelGame, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"stochasticBlockModel"[ratesMatrix, blockSizes, OptionValue[DirectedEdges], OptionValue[SelfLoops]];
      applyGraphOpt[opt]@igToGraph[ig]
    ]

Options[IGForestFireGame] = { DirectedEdges -> False };
SyntaxInformation[IGForestFireGame] = {"ArgumentsPattern" -> {_, _, _., _., OptionsPattern[]}, "OptionNames" -> optNames[IGForestFireGame, Graph]};
IGForestFireGame[n_?Internal`PositiveMachineIntegerQ, fwprob_?NonNegative, bwratio : _?NonNegative : 1, nambs : _?Internal`NonNegativeIntegerQ : 1, opt : OptionsPattern[{IGForestFireGame, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"forestFireGame"[n, fwprob, bwratio, nambs, OptionValue[DirectedEdges]];
      applyGraphOpt[opt]@igToGraph[ig];
      fwprob = nambs;
    ]

(* TODO: Work around Mathematica bug where bidirectional directed bipartite graphs may not be laid out. *)
Options[IGBipartiteGameGNM] = { DirectedEdges -> False, "Bidirectional" -> True, GraphLayout -> "BipartiteEmbedding" };
SyntaxInformation[IGBipartiteGameGNM] = {"ArgumentsPattern" -> {_, _, _, OptionsPattern[]}, "OptionNames" -> optNames[IGBipartiteGameGNM, Graph]};
IGBipartiteGameGNM[n1_?Internal`NonNegativeMachineIntegerQ, n2_?Internal`NonNegativeMachineIntegerQ, m_?Internal`NonNegativeMachineIntegerQ, opt : OptionsPattern[{IGBipartiteGameGNM, Graph}]] :=
    catch@Block[{ig = Make[]},
      check@ig@"bipartiteGameGNM"[n1, n2, m, OptionValue[DirectedEdges], OptionValue["Bidirectional"]];
      applyGraphOpt[GraphLayout -> OptionValue[GraphLayout], opt]@igToGraph[ig]
    ]

Options[IGBipartiteGameGNP] = { DirectedEdges -> False, "Bidirectional" -> True, GraphLayout -> "BipartiteEmbedding" };
SyntaxInformation[IGBipartiteGameGNP] = {"ArgumentsPattern" -> {_, _, _, OptionsPattern[]}, "OptionNames" -> optNames[IGBipartiteGameGNP, Graph]};
IGBipartiteGameGNP[n1_?Internal`NonNegativeMachineIntegerQ, n2_?Internal`NonNegativeMachineIntegerQ, p_?NonNegative, opt : OptionsPattern[{IGBipartiteGameGNP, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"bipartiteGameGNP"[n1, n2, p, OptionValue[DirectedEdges], OptionValue["Bidirectional"]];
      applyGraphOpt[GraphLayout -> OptionValue[GraphLayout], opt]@igToGraph[ig]
    ]

Options[IGGeometricGame] = {"Periodic" -> False};
SyntaxInformation[IGGeometricGame] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}, "OptionNames" -> optNames[IGGeometricGame, Graph]};
IGGeometricGame[n_?Internal`NonNegativeMachineIntegerQ, radius_?nonnegativeNumericQ, opt : OptionsPattern[{IGGeometricGame, Graph}]] :=
    catch@Block[{ig = Make["IG"], coord},
      coord = check@ig@"geometricGame"[n, radius, OptionValue["Periodic"]];
      applyGraphOpt[VertexCoordinates -> coord, opt]@igToGraph[ig]
    ]

Options[IGBarabasiAlbertGame] = { DirectedEdges -> True, "TotalDegreeAttraction" -> False, Method -> "PSumTree" };
SyntaxInformation[IGBarabasiAlbertGame] = {"ArgumentsPattern" -> {_, _, _., OptionsPattern[]}, "OptionNames" -> optNames[IGBarabasiAlbertGame, Graph]};
igBarabasiAlbertGameMethods = <|"Bag" -> 0, "PSumTree" -> 1, "PSumTreeMultiple" -> 2|>;
amendUsage[IGBarabasiAlbertGame, "Available Method options: <*Keys[igBarabasiAlbertGameMethods]*>."];

IGBarabasiAlbertGame[
  n_?Internal`NonNegativeMachineIntegerQ, m : (_?Internal`PositiveMachineIntegerQ | _?nonNegIntVecQ),
  opt : OptionsPattern[{IGBarabasiAlbertGame, Graph}]] :=
    igBarabasiAlbertGame[n, m, {1,1}, OptionValue[DirectedEdges], OptionValue["TotalDegreeAttraction"], OptionValue[Method], opt]

IGBarabasiAlbertGame[
  n_?Internal`NonNegativeMachineIntegerQ, m : (_?Internal`PositiveMachineIntegerQ | _?nonNegIntVecQ),
  power_?nonnegativeNumericQ,
  opt : OptionsPattern[{IGBarabasiAlbertGame, Graph}]] :=
    igBarabasiAlbertGame[n, m, {power,1}, OptionValue[DirectedEdges], OptionValue["TotalDegreeAttraction"], OptionValue[Method], opt]

IGBarabasiAlbertGame[
  n_?Internal`NonNegativeMachineIntegerQ, m : (_?Internal`PositiveMachineIntegerQ | _?nonNegIntVecQ),
  {power_?nonnegativeNumericQ, a_?nonnegativeNumericQ},
  opt : OptionsPattern[{IGBarabasiAlbertGame, Graph}]] :=
    igBarabasiAlbertGame[n, m, {power, a}, OptionValue[DirectedEdges], OptionValue["TotalDegreeAttraction"], OptionValue[Method], opt]

igBarabasiAlbertGame[n_, m_, {power_, a_}, directed_, totalDegree_, method_, opt___] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"barabasiAlbertGame"[
        n, power, a,
        If[ListQ[m], 0, m], If[ListQ[m], Prepend[m,0], {}],
        directed, totalDegree, Lookup[igBarabasiAlbertGameMethods, method, -1]
      ];
      applyGraphOpt[opt]@igToGraph[ig]
    ]


Options[IGWattsStrogatzGame] = {
  SelfLoops -> False, "MultipleEdges" -> False
};
SyntaxInformation[IGWattsStrogatzGame] = {
  "ArgumentsPattern" -> {_, _, _., OptionsPattern[]}, "OptionNames" -> optNames[IGWattsStrogatzGame, Graph]
};
IGWattsStrogatzGame[
  n_?Internal`NonNegativeMachineIntegerQ, p_?NonNegative,
  {dim_?Internal`PositiveMachineIntegerQ, k_?Internal`PositiveMachineIntegerQ},
  opt : OptionsPattern[{IGWattsStrogatzGame, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"wattsStrogatzGame"[dim, n, k, p, OptionValue[SelfLoops], OptionValue["MultipleEdges"]];
      applyGraphOpt[opt]@igToGraph[ig]
    ]
IGWattsStrogatzGame[
  n_?Internal`NonNegativeMachineIntegerQ, p_?NonNegative,
  k_?Internal`PositiveMachineIntegerQ, opt : OptionsPattern[]] :=
      IGWattsStrogatzGame[n, p, {1, k}, opt]
IGWattsStrogatzGame[
  n_?Internal`NonNegativeMachineIntegerQ, p_?NonNegative,
  opt : OptionsPattern[]] :=
    IGWattsStrogatzGame[n, p, {1, 2}, opt]


Options[IGStaticFitnessGame] = { SelfLoops -> False, "MultipleEdges" -> False };
SyntaxInformation[IGStaticFitnessGame] = {
  "ArgumentsPattern" -> {_, _, _., OptionsPattern[]}, "OptionNames" -> optNames[IGStaticFitnessGame, Graph]
};
IGStaticFitnessGame[
  m_?Internal`NonNegativeMachineIntegerQ,
  inFitness_?nonNegIntVecQ, outFitness : _?nonNegIntVecQ : {}, opt : OptionsPattern[{IGStaticFitnessGame, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"staticFitnessGame"[m, inFitness, outFitness, OptionValue[SelfLoops], OptionValue["MultipleEdges"]];
      applyGraphOpt[opt]@igToGraph[ig]
    ]


Options[IGStaticPowerLawGame] = { SelfLoops -> False, "MultipleEdges" -> False, "FiniteSizeCorrection" -> True };
SyntaxInformation[IGStaticPowerLawGame] = {
  "ArgumentsPattern" -> {_, _, _, _., OptionsPattern[]}, "OptionNames" -> optNames[IGStaticPowerLawGame, Graph]
};
IGStaticPowerLawGame[n_?Internal`NonNegativeMachineIntegerQ, m_?Internal`NonNegativeMachineIntegerQ, exp_?nonnegativeNumericQ, opt : OptionsPattern[{IGStaticPowerLawGame, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"staticPowerLawGame"[n, m, exp, -1, OptionValue[SelfLoops], OptionValue["MultipleEdges"], OptionValue["FiniteSizeCorrection"]];
      applyGraphOpt[opt]@igToGraph[ig]
    ]
IGStaticPowerLawGame[n_?Internal`NonNegativeMachineIntegerQ, m_?Internal`NonNegativeMachineIntegerQ, expOut_?nonnegativeNumericQ, expIn_?nonnegativeNumericQ, opt : OptionsPattern[{IGStaticPowerLawGame, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"staticPowerLawGame"[n, m, expOut, expIn, OptionValue[SelfLoops], OptionValue["MultipleEdges"], OptionValue["FiniteSizeCorrection"]];
      applyGraphOpt[opt]@igToGraph[ig]
    ]


Options[IGGrowingGame] = { DirectedEdges -> False, "Citation" -> False };
SyntaxInformation[IGGrowingGame] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}, "OptionNames" -> optNames[IGGrowingGame, Graph]};
IGGrowingGame[n_?Internal`NonNegativeMachineIntegerQ, m_?Internal`NonNegativeMachineIntegerQ, opt : OptionsPattern[{IGGrowingGame, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      check@ig@"growingGame"[n, m, OptionValue[DirectedEdges], OptionValue["Citation"]];
      applyGraphOpt[opt]@igToGraph[ig]
    ]


IGEstablishmentGame::prefmsym  = IGCallawayTraitsGame::prefmsym = "The preference matrix must be symmetric when generating undirected graphs.";
IGEstablishmentGame::prefmdim = IGCallawayTraitsGame::prefmdim = "The preference matrix must be square and agree in size with the number of vertex types.";
IGEstablishmentGame::prefmel  = IGCallawayTraitsGame::prefmel  = "The elements of the preference matrix must be probabilities between 0 and 1.";
IGEstablishmentGame::weightnn = IGCallawayTraitsGame::weightnn = "The vertex type weights must be non-negative.";

Options[IGCallawayTraitsGame] = { DirectedEdges -> False };
SyntaxInformation[IGCallawayTraitsGame] = {
  "ArgumentsPattern" -> {_, _, _, _, OptionsPattern[]}, "OptionNames" -> optNames[IGCallawayTraitsGame, Graph]
};
IGCallawayTraitsGame[
  n_?Internal`NonNegativeMachineIntegerQ, k_?Internal`NonNegativeMachineIntegerQ,
  typeWeights_?VectorQ, prefMatrix_?MatrixQ,
  opt : OptionsPattern[{IGCallawayTraitsGame, Graph}]] :=
      catch@Block[{ig = Make["IG"]},
        If[Not@positiveVecQ[typeWeights],
          Message[IGCallawayTraitsGame::weightnn];
          Return[$Failed];
        ];
        If[Dimensions[prefMatrix] != {Length[typeWeights], Length[typeWeights]},
          Message[IGCallawayTraitsGame::prefmdim];
          Return[$Failed]
        ];
        If[Not@MatrixQ[prefMatrix, 0 <= # <= 1&],
          Message[IGCallawayTraitsGame::prefmel];
          Return[$Failed]
        ];
        If[Not@TrueQ@OptionValue[DirectedEdges] && Not@SymmetricMatrixQ[prefMatrix],
          Message[IGCallawayTraitsGame::prefmsym];
          Return[$Failed]
        ];
        check@ig@"callawayTraitsGame"[n, Length[typeWeights], k, typeWeights, prefMatrix, OptionValue[DirectedEdges]];
        applyGraphOpt[opt]@igToGraph[ig]
      ]

Options[IGEstablishmentGame] = { DirectedEdges -> False };
SyntaxInformation[IGEstablishmentGame] = {
  "ArgumentsPattern" -> {_, _, _, _, OptionsPattern[]}, "OptionNames" -> optNames[IGEstablishmentGame, Graph]
};
IGEstablishmentGame[
  n_?Internal`NonNegativeMachineIntegerQ, k_?Internal`NonNegativeMachineIntegerQ,
  typeWeights_?VectorQ, prefMatrix_?MatrixQ,
  opt : OptionsPattern[{IGEstablishmentGame, Graph}]] :=
    catch@Block[{ig = Make["IG"]},
      If[Not@positiveVecQ[typeWeights],
        Message[IGEstablishmentGame::weightnn];
        Return[$Failed];
      ];
      If[Dimensions[prefMatrix] != {Length[typeWeights], Length[typeWeights]},
        Message[IGEstablishmentGame::prefmdim];
        Return[$Failed]
      ];
      If[Not@MatrixQ[prefMatrix, 0 <= # <= 1&],
        Message[IGEstablishmentGame::prefmel];
        Return[$Failed]
      ];
      If[Not@TrueQ@OptionValue[DirectedEdges] && Not@SymmetricMatrixQ[prefMatrix],
        Message[IGEstablishmentGame::prefmsym];
        Return[$Failed]
      ];
      check@ig@"establishmentGame"[n, Length[typeWeights], k, typeWeights, prefMatrix, OptionValue[DirectedEdges]];
      applyGraphOpt[opt]@igToGraph[ig]
    ]


(* Modification *)

(* Warning: this function doesn't preserve the edge ordering or any graph properties *)
vertexRename[names_][graph_] :=
    If[VertexCount[graph] == 0,
      graph,
      AdjacencyGraph[names, AdjacencyMatrix[graph], DirectedEdges -> DirectedGraphQ[graph]]
    ]

SyntaxInformation[IGConnectNeighborhood] = {"ArgumentsPattern" -> {_, _., OptionsPattern[]}, "OptionNames" -> optNames[Graph]};
IGConnectNeighborhood[graph_?igGraphQ, k : _?Internal`NonNegativeMachineIntegerQ : 2, opt : OptionsPattern[Graph]] :=
    catch@Block[{ig = igMakeFast[graph]},
      check@ig@"connectNeighborhood"[k];
      Graph[vertexRename[VertexList[graph]]@igToGraph[ig], opt]
    ]

(* Testing *)

SyntaxInformation[IGDirectedAcyclicGraphQ] = {"ArgumentsPattern" -> {_}};
IGDirectedAcyclicGraphQ[g_?igGraphQ] := Block[{ig = igMakeFast[g]}, sck@ig@"dagQ"[]]

SyntaxInformation[IGConnectedQ] = {"ArgumentsPattern" -> {_}};
IGConnectedQ[g_?igGraphQ] := Block[{ig = igMakeFast[g]}, sck@ig@"connectedQ"[True]]

SyntaxInformation[IGWeaklyConnectedQ] = {"ArgumentsPattern" -> {_}};
IGWeaklyConnectedQ[g_?igGraphQ] := Block[{ig = igMakeFast[g]}, sck@ig@"connectedQ"[False]]

SyntaxInformation[IGGraphicalQ] = {"ArgumentsPattern" -> {_, _.}};
IGGraphicalQ[degrees_?nonNegIntVecQ] := IGGraphicalQ[{}, degrees]
IGGraphicalQ[indeg_?nonNegIntVecQ, outdeg_?nonNegIntVecQ] := sck@igraphGlobal@"graphicalQ"[outdeg, indeg]

SyntaxInformation[IGBipartiteQ] = {"ArgumentsPattern" -> {_}};
IGBipartiteQ[g_?igGraphQ] := Block[{ig = igMakeFast[g]}, sck@ig@"bipartiteQ"[]]

(* Centrality *)

igBetwennessMethods = <| "Precise" -> False, "Fast" -> True |>;

Options[IGBetweenness] = { Method -> "Precise" };

IGBetweenness::bdmtd =
    "Value of option Method -> `` is not one of " <>
        ToString[Keys[igBetwennessMethods], InputForm] <>
        ". Defaulting to " <> ToString[OptionValue[IGBetweenness, Method], InputForm] <> ".";

amendUsage[IGBetweenness, "Available Method options: <*Keys[igBetwennessMethods]*>."];

SyntaxInformation[IGBetweenness] = {"ArgumentsPattern" -> {_, OptionsPattern[]}};

IGBetweenness[g_?igGraphQ, opt : OptionsPattern[]] :=
    Block[{ig = igMakeFastWeighted[g]},
      sck@ig@"betweenness"[
        Lookup[igBetwennessMethods, OptionValue[Method], Message[IGBetweenness::bdmtd, OptionValue[Method]]; False]
      ]
    ]


(* Note: edge ordering is critical *)
SyntaxInformation[IGEdgeBetweenness] = {"ArgumentsPattern" -> {_}};
IGEdgeBetweenness[g_?igGraphQ] :=
    Block[{ig = igMake[g]}, sck@ig@"edgeBetweenness"[]]

Options[IGCloseness] = { "Normalized" -> False };
SyntaxInformation[IGCloseness] = {"ArgumentsPattern" -> {_, OptionsPattern[]}};
IGCloseness[g_?igGraphQ, opt : OptionsPattern[]] :=
    Block[{ig = igMakeFastWeighted[g]}, sck@ig@"closeness"[OptionValue["Normalized"]]]

(* Centrality estimates *)

Options[IGBetweennessEstimate] = { Method -> "Precise" };
IGBetweennessEstimate::bdmtd = IGBetweenness::bdmtd;
amendUsage[IGBetweennessEstimate, "Available Method options: <*Keys[igBetwennessMethods]*>."];
SyntaxInformation[IGBetweennessEstimate] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}};
IGBetweennessEstimate[g_?igGraphQ, cutoff_?positiveNumericQ, opt : OptionsPattern[]] :=
    Block[{ig = igMakeFastWeighted[g]},
      sck@ig@"betweennessEstimate"[
        infToZero[cutoff],
        Lookup[igBetwennessMethods, OptionValue[Method], Message[IGBetweennessEstimate::bdmtd, OptionValue[Method]]; False]
      ]
    ]

(* Note: edge ordering is critical *)
SyntaxInformation[IGEdgeBetweennessEstimate] = {"ArgumentsPattern" -> {_, _}};
IGEdgeBetweennessEstimate[g_?igGraphQ, cutoff_?positiveNumericQ] :=
    Block[{ig = igMake[g]}, sck@ig@"edgeBetweennessEstimate"@infToZero[cutoff]]

Options[IGClosenessEstimate] = { "Normalized" -> False };
SyntaxInformation[IGClosenessEstimate] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}};
IGClosenessEstimate[g_?igGraphQ, cutoff_?positiveNumericQ, opt : OptionsPattern[]] :=
    Block[{ig = igMakeFastWeighted[g]},
      sck@ig@"closenessEstimate"[infToZero[cutoff], OptionValue["Normalized"]]
    ]



igPageRankMethods = { "PowerIteration", "Arnoldi", "PRPACK" };
igPageRankMethodsAsc = AssociationThread[igPageRankMethods, Range@Length[igPageRankMethods] - 1];
igPageRankPowerOptions = <| "Epsilon" -> 0.001, "MaxIterations" -> 1000 |>;

Options[IGPageRank] = { Method -> "PRPACK", DirectedEdges -> True };
SyntaxInformation[IGPageRank] = {"ArgumentsPattern" -> {_, _., OptionsPattern[]}};
amendUsage[IGPageRank, "Available Method options: <*igPageRankMethods*>."];

IGPageRank[graph_?GraphQ, damping : _?positiveNumericQ : 0.85, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], method, methodOptions = {}, powerOpt},
      method = OptionValue[Method];
      If[ListQ[method],
        {method, methodOptions} = {First[method], Rest[method]};
      ];
      powerOpt = Join[igPageRankPowerOptions, Association[methodOptions]];
      check@ig@"pageRank"[
        Lookup[igPageRankMethodsAsc, method, -1], damping, OptionValue[DirectedEdges], powerOpt["MaxIterations"], powerOpt["Epsilon"]
      ]
    ]


Options[IGPersonalizedPageRank] = { Method -> "PRPACK", DirectedEdges -> True };
SyntaxInformation[IGPersonalizedPageRank] = {"ArgumentsPattern" -> {_, _, _., OptionsPattern[]}};

IGPersonalizedPageRank::invarg = "Second argument must be a vector of the same length as the vertex count of the graph.";

IGPersonalizedPageRank[graph_?GraphQ, reset_?VectorQ, damping : _?positiveNumericQ : 0.85, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], method, methodOptions = {}, powerOpt},
      method = OptionValue[Method];
      If[ListQ[method],
        {method, methodOptions} = {First[method], Rest[method]};
      ];
      powerOpt = Join[igPageRankPowerOptions, Association[methodOptions]];
      If[Length[reset] != VertexCount[graph],
        Message[IGPersonalizedPageRank::invarg];
        throw[$Failed]
      ];
      check@ig@"personalizedPageRank"[
        Lookup[igPageRankMethodsAsc, method, -1], reset, damping, OptionValue[DirectedEdges], powerOpt["MaxIterations"], powerOpt["Epsilon"]
      ]
    ]


Options[IGEigenvectorCentrality] = { DirectedEdges -> True, "Normalized" -> True };
SyntaxInformation[IGEigenvectorCentrality] = {"ArgumentsPattern" -> {_, OptionsPattern[]}};
IGEigenvectorCentrality[graph_?GraphQ, opt : OptionsPattern[]] :=
    Block[{ig = igMakeFastWeighted[graph]},
      sck@ig@"eigenvectorCentrality"[OptionValue[DirectedEdges], OptionValue["Normalized"]]
    ]

Options[IGHubScore] = { "Normalized" -> True };
SyntaxInformation[IGHubScore] = {"ArgumentsPattern" -> {_, OptionsPattern[]}};
IGHubScore[graph_?GraphQ, opt : OptionsPattern[]] :=
    Block[{ig = igMakeFastWeighted[graph]},
      sck@ig@"hubScore"[OptionValue["Normalized"]]
    ]

Options[IGAuthorityScore] = { "Normalized" -> True };
SyntaxInformation[IGAuthorityScore] = {"ArgumentsPattern" -> {_, OptionsPattern[]}};
IGAuthorityScore[graph_?GraphQ, opt : OptionsPattern[]] :=
    Block[{ig = igMakeFastWeighted[graph]},
      sck@ig@"authorityScore"[OptionValue["Normalized"]]
    ]

SyntaxInformation[IGConstraintScore] = {"ArgumentsPattern" -> {_}};
IGConstraintScore[graph_?igGraphQ] :=
    Block[{ig = igMakeFastWeighted[graph]},
      sck@ig@"constraintScore"[]
    ]

(* Randomization and rewiring *)

(* TODO: functions in this section should warn that edge weights will be lost *)

Options[IGRewire] = { SelfLoops -> False };
SyntaxInformation[IGRewire] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}};
IGRewire[g_?igGraphQ, n_?Internal`NonNegativeMachineIntegerQ, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFast[g]},
      check@ig@"rewire"[n, OptionValue[SelfLoops]];
      vertexRename[VertexList[g]]@igToGraph[ig]
    ]

Options[IGRewireEdges] = { SelfLoops -> False, "MultipleEdges" -> False };
SyntaxInformation[IGRewireEdges] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}};
IGRewireEdges[g_?igGraphQ, p_?Internal`RealValuedNumericQ, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFast[g]},
      check@ig@"rewireEdges"[p, OptionValue[SelfLoops], OptionValue["MultipleEdges"]];
      vertexRename[VertexList[g]]@igToGraph[ig]
    ]

(* Isomorphism *)

SyntaxInformation[IGIsomorphicQ] = {"ArgumentsPattern" -> {_, _}};
IGIsomorphicQ[g1_?igGraphQ, g2_?igGraphQ] :=
    catch@If[MultigraphQ[g1] || MultigraphQ[g2],
      igMultigraphIsomorphicQ[g1, g2]
      ,
      Block[{ig1 = igMakeFast[g1], ig2 = igMakeFast[g2]},
        check@ig1@"isomorphic"[ManagedLibraryExpressionID[ig2]]
      ]
    ]

(* Transform multigraph isomorphism to edge-coloured graph isomorphism. *)
igMultigraphIsomorphicQ[g1_, g2_] :=
    (* no catch *) Block[{ig1, ig2, ec1, ec2},
      VertexCount[g1] == VertexCount[g2] && EdgeCount[g1] == EdgeCount[g2] &&
      Internal`InheritedBlock[{UndirectedEdge},
        SetAttributes[UndirectedEdge, Orderless];
        ec1 = Counts@EdgeList[g1]; ec2 = Counts@EdgeList[g2];
        ig1 = igMake@Graph@Keys[ec1]; ig2 = igMake@Graph@Keys[ec2];
        check@ig1@"vf2Isomorphic"[ManagedLibraryExpressionID[ig2], {}, {}, Values[ec1], Values[ec2]]
      ]
    ]

SyntaxInformation[IGSubisomorphicQ] = {"ArgumentsPattern" -> {_, _}};
IGSubisomorphicQ[subgraph_?igGraphQ, graph_?igGraphQ] :=
    catch@If[MultigraphQ[subgraph] || MultigraphQ[graph],
      igMultigraphSubisomorphicQ[subgraph, graph]
      ,
      Block[{ig1 = igMakeFast[graph], ig2 = igMakeFast[subgraph]},
        check@ig1@"subisomorphic"[ManagedLibraryExpressionID[ig2]]
      ]
    ]

(* Transform multigraph subisomorphism to edge-coloured graph subisomorphism. *)
igMultigraphSubisomorphicQ[subgraph_, graph_] :=
(* no catch *) Block[{ig, igs, ec, ecs},
      Internal`InheritedBlock[{UndirectedEdge},
        SetAttributes[UndirectedEdge, Orderless];
        ec = Counts@EdgeList[graph]; ecs = Counts@EdgeList[subgraph];
        ig = igMake@Graph@Keys[ec]; igs = igMake@Graph@Keys[ecs];
        check@ig@"vf2Subisomorphic"[ManagedLibraryExpressionID[igs], {}, {}, Values[ec], Values[ecs]]
      ]
]


SyntaxInformation[IGIsoclass] = {"ArgumentsPattern" -> {_}};
IGIsoclass[graph_?igGraphQ] := Block[{ig = igMakeFast[graph]}, sck@ig@"isoclass"[]]


(* Vertex and edge colouring helper functions *)

IGraphM::vcol = "The \"VertexColors\" option must be a list of integers, an association assigning integers to vertices, or None.";
IGraphM::ecol = "The \"EdgeColors\" option must be a list of integers, an association assigning integers to edges, or None.";
IGraphM::bdecol = "Edge colors: the following edges are not in the graph: ``.";
IGraphM::bdvcol = "Vertex colors: the following vertices are not in the graph: ``.";
IGraphM::vcolcnt = "When vertex colours are specified as a list, the list length must be the same as the vertex count of the graph.";
IGraphM::ecolcnt = "When edge colours are specified as a list, the list length must be the same as the edge count of the graph.";
IGraphM::vcmm = "Only one graph is vertex coloured. Colours will be ignored.";

defaultVF2Colors = {"EdgeColors" -> None, "VertexColors" -> None};

colorCheckVertices[g_, c_] := With[{cm = Complement[Keys[c], VertexList[g]]}, If[cm =!= {}, Message[IGraphM::bdvcol, cm]]];

parseVertexColors[_][None] := {}
parseVertexColors[g_][col_?intVecQ] := (If[VertexCount[g] != Length[col], Message[IGraphM::vcolcnt]; throw[$Failed]]; col)
parseVertexColors[g_][col_?AssociationQ] := (colorCheckVertices[g, col]; Lookup[col, VertexList[g], 0])
parseVertexColors[_][_] := (Message[IGraphM::vcol]; {})

colorCheckEdges[g_, c_] := With[{cm = Complement[Keys[c], EdgeList[g]]}, If[cm =!= {}, Message[IGraphM::bdecol, cm]]];

parseEdgeColors[_][None] := {}
parseEdgeColors[g_][col_?intVecQ] := (If[EdgeCount[g] != Length[col], Message[IGraphM::ecolcnt]; throw[$Failed]]; col)
parseEdgeColors[g_][col_?AssociationQ] :=
    Internal`InheritedBlock[{UndirectedEdge},
      SetAttributes[UndirectedEdge, Orderless];
      colorCheckEdges[g,col];
      Lookup[KeyMap[Identity, col] (* allow Orderless to do its job *), EdgeList[g], 0]
    ]
parseEdgeColors[_][_] := (Message[IGraphM::ecol]; {})

(** Bliss **)

IGraphM::blissnmg = "Bliss does not support multigraphs.";
blissCheckMulti[graph_] := If[MultigraphQ[graph], Message[IGraphM::blissnmg]; throw[$Failed]]

defaultBlissColors = {"VertexColors" -> None};

blissSplittingHeuristicsNames = {
  "First", "FirstSmallest", "FirstLargest",
  "FirstMaximallyConnected", "FirstSmallestMaximallyConnected", "FirstLargestMaximallyConnected"
};

blissSplittingHeuristics = AssociationThread[blissSplittingHeuristicsNames, Range@Length[blissSplittingHeuristicsNames] - 1];

amendUsage[IGBlissCanonicalLabeling,
  " Available values for the \"SplittingHeuristics\" option: ``. The labeling depends on the splitting heuristics used.",
  blissSplittingHeuristicsNames
];


Options[IGBlissCanonicalLabeling] = { "SplittingHeuristics" -> "First" };
SyntaxInformation[IGBlissCanonicalLabeling] = {"ArgumentsPattern" -> {{__}, OptionsPattern[]}};
IGBlissCanonicalLabeling[graph_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFast[graph]},
      blissCheckMulti[graph];
      AssociationThread[
        VertexList[graph],
        igIndexVec@check@ig@"blissCanonicalPermutation"[Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], {}]
      ]
    ]
IGBlissCanonicalLabeling[{graph_?igGraphQ, col : OptionsPattern[]}, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFast[graph], vcol},
      blissCheckMulti[graph];
      vcol = parseVertexColors[graph]@OptionValue[defaultBlissColors, {col}, "VertexColors"];
      AssociationThread[
        VertexList[graph],
        igIndexVec@check@ig@"blissCanonicalPermutation"[Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], vcol]
      ]
    ]


Options[IGBlissCanonicalPermutation] = { "SplittingHeuristics" -> "First" };
SyntaxInformation[IGBlissCanonicalPermutation] = {"ArgumentsPattern" -> {{__}, OptionsPattern[]}};
IGBlissCanonicalPermutation[graph_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFast[graph]},
      blissCheckMulti[graph];
      InversePermutation@igIndexVec@check@ig@"blissCanonicalPermutation"[Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], {}]
    ]
IGBlissCanonicalPermutation[{graph_?igGraphQ, col : OptionsPattern[]}, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFast[graph], vcol},
      blissCheckMulti[graph];
      vcol = parseVertexColors[graph]@OptionValue[defaultBlissColors, {col}, "VertexColors"];
      InversePermutation@igIndexVec@check@ig@"blissCanonicalPermutation"[Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], vcol]
    ]


Options[IGBlissCanonicalGraph] = { "SplittingHeuristics" -> "First" };
SyntaxInformation[IGBlissCanonicalGraph] = {"ArgumentsPattern" -> {{__}, OptionsPattern[]}};
IGBlissCanonicalGraph[spec: (graph_?igGraphQ) | {graph_?igGraphQ, col : OptionsPattern[]}, opt : OptionsPattern[]] :=
      catch@With[{perm = check@IGBlissCanonicalPermutation[spec], am = AdjacencyMatrixx[graph]},
        AdjacencyGraph[ am[[perm, perm]] ]
      ]


Options[IGBlissIsomorphicQ] = { "SplittingHeuristics" -> "First" };
SyntaxInformation[IGBlissIsomorphicQ] = {"ArgumentsPattern" -> {{__}, {__}, OptionsPattern[]}};
IGBlissIsomorphicQ[graph1_?igGraphQ, graph2_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Block[{ig1 = igMakeFast[graph1], ig2 = igMakeFast[graph2]},
      blissCheckMulti /@ {graph1, graph2};
      check@ig1@"blissIsomorphic"[ManagedLibraryExpressionID[ig2], Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], {}, {}]
    ]
IGBlissIsomorphicQ[{graph1_?igGraphQ, col1 : OptionsPattern[]}, {graph2_?igGraphQ, col2 : OptionsPattern[]}, opt : OptionsPattern[]] :=
    catch@Block[{ig1 = igMakeFast[graph1], ig2 = igMakeFast[graph2], vcol1, vcol1},
      blissCheckMulti /@ {graph1, graph2};
      vcol1 = parseVertexColors[graph1]@OptionValue[defaultBlissColors, {col1}, "VertexColors"];
      vcol2 = parseVertexColors[graph2]@OptionValue[defaultBlissColors, {col2}, "VertexColors"];
      check@ig1@"blissIsomorphic"[ManagedLibraryExpressionID[ig2], Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], vcol1, vcol2]
    ]


Options[IGBlissGetIsomorphism] = { "SplittingHeuristics" -> "First" };
SyntaxInformation[IGBlissGetIsomorphism] = {"ArgumentsPattern" -> {{__}, {__}, OptionsPattern[]}};
IGBlissGetIsomorphism[graph1_?igGraphQ, graph2_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Block[{ig1 = igMakeFast[graph1], ig2 = igMakeFast[graph2], result},
      blissCheckMulti /@ {graph1, graph2};
      result = igIndexVec@check@ig1@"blissFindIsomorphism"[ManagedLibraryExpressionID[ig2], Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], {}, {}];
      If[result === {}, Return[{}]];
      List@AssociationThread[
        VertexList[graph1],
        igVertexNames[graph2]@result
      ]
    ]
IGBlissGetIsomorphism[{graph1_?igGraphQ, col1 : OptionsPattern[]}, {graph2_?igGraphQ, col2 : OptionsPattern[]}, opt : OptionsPattern[]] :=
    catch@Block[{ig1 = igMakeFast[graph1], ig2 = igMakeFast[graph2], result, vcol1, vcol2},
      blissCheckMulti /@ {graph1, graph2};
      vcol1 = parseVertexColors[graph1]@OptionValue[defaultBlissColors, {col1}, "VertexColors"];
      vcol2 = parseVertexColors[graph2]@OptionValue[defaultBlissColors, {col2}, "VertexColors"];
      result = igIndexVec@check@ig1@"blissFindIsomorphism"[ManagedLibraryExpressionID[ig2], Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], vcol1, vcol2];
      If[result === {}, Return[{}]];
      List@AssociationThread[
        VertexList[graph1],
        igVertexNames[graph2]@result
      ]
    ]


Options[IGBlissAutomorphismCount] = { "SplittingHeuristics" -> "First" };
SyntaxInformation[IGBlissAutomorphismCount] = {"ArgumentsPattern" -> {{__}, OptionsPattern[]}};
IGBlissAutomorphismCount[graph_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFast[graph]},
      blissCheckMulti[graph];
      ToExpression@check@ig@"blissAutomorphismCount"[Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], {}]
    ]

IGBlissAutomorphismCount[{graph_?igGraphQ, col : OptionsPattern[]}, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFast[graph], vcol},
      blissCheckMulti[graph];
      vcol = parseVertexColors[graph]@OptionValue[defaultBlissColors, {col}, "VertexColors"];
      ToExpression@check@ig@"blissAutomorphismCount"[Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], vcol]
    ]


Options[IGBlissAutomorphismGroup] = { "SplittingHeuristics" -> "First" };
SyntaxInformation[IGBlissAutomorphismGroup] = {"ArgumentsPattern" -> {{__}, OptionsPattern[]}};
IGBlissAutomorphismGroup[graph_?GraphQ, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFast[graph]},
      blissCheckMulti[graph];
      igIndexVec@check@ig@"blissAutomorphismGroup"[Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], {}]
    ]

IGBlissAutomorphismGroup[{graph_?GraphQ, col : OptionsPattern[]}, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFast[graph], vcol},
      blissCheckMulti[graph];
      vcol = parseVertexColors[graph]@OptionValue[defaultBlissColors, {col}, "VertexColors"];
      igIndexVec@check@ig@"blissAutomorphismGroup"[Lookup[blissSplittingHeuristics, OptionValue["SplittingHeuristics"], -1], vcol]
    ]

(** VF2 **)

IGraphM::vf2nmg = "VF2 does not support multigraphs.";
vf2CheckMulti[graph_] := If[MultigraphQ[graph], Message[IGraphM::vf2nmg]; throw[$Failed]]

SyntaxInformation[IGVF2IsomorphicQ] = {"ArgumentsPattern" -> {{__}, {__}}};

IGVF2IsomorphicQ[{graph1_?igGraphQ, opt1 : OptionsPattern[]}, {graph2_?igGraphQ, opt2 : OptionsPattern[]}] :=
    catch@Block[{ig1 = igMake[graph1], ig2 = igMake[graph2], vcol1, vcol2, ecol1, ecol2},
      vf2CheckMulti /@ {graph1, graph2};
      vcol1 = parseVertexColors[graph1]@OptionValue[defaultVF2Colors, {opt1}, "VertexColors"];
      vcol2 = parseVertexColors[graph2]@OptionValue[defaultVF2Colors, {opt2}, "VertexColors"];
      ecol1 = parseEdgeColors[graph1]@OptionValue[defaultVF2Colors, {opt1}, "EdgeColors"];
      ecol2 = parseEdgeColors[graph2]@OptionValue[defaultVF2Colors, {opt2}, "EdgeColors"];
      check@ig1@"vf2Isomorphic"[ManagedLibraryExpressionID[ig2], vcol1, vcol2, ecol1, ecol2]
    ]

IGVF2IsomorphicQ[graph1_?igGraphQ, graph2_?igGraphQ] :=
    catch@Block[{ig1 = igMake[graph1], ig2 = igMake[graph2]},
      vf2CheckMulti /@ {graph1, graph2};
      check@ig1@"vf2Isomorphic"[ManagedLibraryExpressionID[ig2], {}, {}, {}, {}]
    ]


SyntaxInformation[IGVF2FindIsomorphisms] = {"ArgumentsPattern" -> {{__}, {__}, _.}};

IGVF2FindIsomorphisms[{graph1_?igGraphQ, opt1 : OptionsPattern[]}, {graph2_?igGraphQ, opt2 : OptionsPattern[]}, max : (_?Internal`PositiveMachineIntegerQ | All | Infinity) : All] :=
    catch@Block[{ig1 = igMake[graph1], ig2 = igMake[graph2], vcol1, vcol2, ecol1, ecol2, n, result},
      vf2CheckMulti /@ {graph1, graph2};
      n = Replace[max, All|Infinity -> -1];
      vcol1 = parseVertexColors[graph1]@OptionValue[defaultVF2Colors, {opt1}, "VertexColors"];
      vcol2 = parseVertexColors[graph2]@OptionValue[defaultVF2Colors, {opt2}, "VertexColors"];
      ecol1 = parseEdgeColors[graph1]@OptionValue[defaultVF2Colors, {opt1}, "EdgeColors"];
      ecol2 = parseEdgeColors[graph2]@OptionValue[defaultVF2Colors, {opt2}, "EdgeColors"];
      result = igIndexVec@check@ig1@"vf2FindIsomorphisms"[ManagedLibraryExpressionID[ig2], n, vcol1, vcol2, ecol1, ecol2];
      AssociationThread[
        VertexList[graph1],
        igVertexNames[graph2][#]
      ]& /@ result
    ]

IGVF2FindIsomorphisms[graph1_?igGraphQ, graph2_?igGraphQ, max : (_?Internal`PositiveMachineIntegerQ | All | Infinity) : All] :=
    catch@Block[{ig1 = igMake[graph1], ig2 = igMake[graph2], n, result},
      vf2CheckMulti /@ {graph1, graph2};
      n = Replace[max, All|Infinity -> -1];
      result = igIndexVec@check@ig1@"vf2FindIsomorphisms"[ManagedLibraryExpressionID[ig2], n, {}, {}, {}, {}];
      AssociationThread[
        VertexList[graph1],
        igVertexNames[graph2][#]
      ]& /@ result
    ]


SyntaxInformation[IGVF2GetIsomorphism] = {"ArgumentsPattern" -> {{__}, {__}}};

IGVF2GetIsomorphism[graph1_?igGraphQ, graph2_?igGraphQ] :=
    IGVF2FindIsomorphisms[graph1, graph2, 1]
IGVF2GetIsomorphism[cg1: {graph1_?igGraphQ, opt1 : OptionsPattern[]}, cg2: {graph2_?igGraphQ, opt2 : OptionsPattern[]}] :=
    IGVF2FindIsomorphisms[cg1, cg2, 1]


SyntaxInformation[IGVF2SubisomorphicQ] = {"ArgumentsPattern" -> {{__}, {__}}};

IGVF2SubisomorphicQ[{subgraph_?igGraphQ, optsub : OptionsPattern[]}, {graph_?igGraphQ, opt : OptionsPattern[]}] :=
    catch@Block[{ig1 = igMake[graph], ig2 = igMake[subgraph], vcol, vcolsub, ecol, ecolsub},
      vf2CheckMulti /@ {subgraph, graph};
      vcol    = parseVertexColors[graph]@OptionValue[defaultVF2Colors, {opt}, "VertexColors"];
      vcolsub = parseVertexColors[subgraph]@OptionValue[defaultVF2Colors, {optsub}, "VertexColors"];
      ecol    = parseEdgeColors[graph]@OptionValue[defaultVF2Colors, {opt}, "EdgeColors"];
      ecolsub = parseEdgeColors[subgraph]@OptionValue[defaultVF2Colors, {optsub}, "EdgeColors"];
      check@ig1@"vf2Subisomorphic"[ManagedLibraryExpressionID[ig2], vcol, vcolsub, ecol, ecolsub]
    ]

IGVF2SubisomorphicQ[subgraph_?igGraphQ, graph_?igGraphQ] :=
    catch@Block[{ig1 = igMake[graph], ig2 = igMake[subgraph]},
      vf2CheckMulti /@ {subgraph, graph};
      check@ig1@"vf2Subisomorphic"[ManagedLibraryExpressionID[ig2], {}, {}, {}, {}]
    ]


SyntaxInformation[IGVF2FindSubisomorphisms] = {"ArgumentsPattern" -> {{__}, {__}, _.}};

IGVF2FindSubisomorphisms[{subgraph_?igGraphQ, optsub : OptionsPattern[]}, {graph_?igGraphQ, opt : OptionsPattern[]}, max : (_?Internal`PositiveMachineIntegerQ | All | Infinity) : All] :=
    catch@Block[{ig1 = igMake[graph], ig2 = igMake[subgraph], vcol, vcolsub, ecol, ecolsub, n, result},
      vf2CheckMulti /@ {subgraph, graph};
      n = Replace[max, All|Infinity -> -1];
      vcol    = parseVertexColors[graph]@OptionValue[defaultVF2Colors, {opt}, "VertexColors"];
      vcolsub = parseVertexColors[subgraph]@OptionValue[defaultVF2Colors, {optsub}, "VertexColors"];
      ecol    = parseEdgeColors[graph]@OptionValue[defaultVF2Colors, {opt}, "EdgeColors"];
      ecolsub = parseEdgeColors[subgraph]@OptionValue[defaultVF2Colors, {optsub}, "EdgeColors"];
      result = igIndexVec@check@ig1@"vf2FindSubisomorphisms"[ManagedLibraryExpressionID[ig2], n, vcol, vcolsub, ecol, ecolsub];
      AssociationThread[
        VertexList[subgraph],
        igVertexNames[graph][#]
      ]& /@ result
    ]


IGVF2FindSubisomorphisms[subgraph_?igGraphQ, graph_?igGraphQ, max : (_?Internal`PositiveMachineIntegerQ | All | Infinity) : All] :=
    catch@Block[{ig1 = igMake[graph], ig2 = igMake[subgraph], n, result},
      vf2CheckMulti /@ {subgraph, graph};
      n = Replace[max, All|Infinity -> -1];
      result = igIndexVec@check@ig1@"vf2FindSubisomorphisms"[ManagedLibraryExpressionID[ig2], n, {}, {}, {}, {}];
      igToGraphQ = False;
      subgraph + graph + max;
      AssociationThread[
        VertexList[subgraph],
        igVertexNames[graph][#]
      ]& /@ result
    ]


SyntaxInformation[IGVF2GetSubisomorphism] = {"ArgumentsPattern" -> {{__}, {__}}};

IGVF2GetSubisomorphism[subgraph2_?igGraphQ, graph_?igGraphQ] :=
    IGVF2FindSubisomorphisms[subgraph2, graph, 1]
IGVF2GetSubisomorphism[cg1: {subgraph_?igGraphQ, opt1 : OptionsPattern[]}, cg2: {graph_?igGraphQ, opt2 : OptionsPattern[]}] :=
    IGVF2FindSubisomorphisms[cg1, cg2, 1]


SyntaxInformation[IGVF2IsomorphismCount] = {"ArgumentsPattern" -> {{__}, {__}}};

IGVF2IsomorphismCount[{graph1_?igGraphQ, opt1 : OptionsPattern[]}, {graph2_?igGraphQ, opt2 : OptionsPattern[]}] :=
    catch@Block[{ig1 = igMake[graph1], ig2 = igMake[graph2], vcol1, vcol2, ecol1, ecol2},
      vf2CheckMulti /@ {graph1, graph2};
      vcol1 = parseVertexColors[graph1]@OptionValue[defaultVF2Colors, {opt1}, "VertexColors"];
      vcol2 = parseVertexColors[graph2]@OptionValue[defaultVF2Colors, {opt2}, "VertexColors"];
      ecol1 = parseEdgeColors[graph1]@OptionValue[defaultVF2Colors, {opt1}, "EdgeColors"];
      ecol2 = parseEdgeColors[graph2]@OptionValue[defaultVF2Colors, {opt2}, "EdgeColors"];
      check@ig1@"vf2IsomorphismCount"[ManagedLibraryExpressionID[ig2], vcol1, vcol2, ecol1, ecol2]
    ]

IGVF2IsomorphismCount[graph1_?igGraphQ, graph2_?igGraphQ] :=
    catch@Block[{ig1 = igMake[graph1], ig2 = igMake[graph2]},
      vf2CheckMulti /@ {graph1, graph2};
      check@ig1@"vf2IsomorphismCount"[ManagedLibraryExpressionID[ig2], {}, {} ,{}, {}]
    ]


SyntaxInformation[IGVF2SubisomorphismCount] = {"ArgumentsPattern" -> {{__}, {__}}};

IGVF2SubisomorphismCount[{subgraph_?igGraphQ, optsub : OptionsPattern[]}, {graph_?igGraphQ, opt : OptionsPattern[]}] :=
    catch@Block[{ig1 = igMake[graph], ig2 = igMake[subgraph], vcol, vcolsub, ecol, ecolsub},
      vf2CheckMulti /@ {subgraph, graph};
      vcol    = parseVertexColors[graph]@OptionValue[defaultVF2Colors, {opt}, "VertexColors"];
      vcolsub = parseVertexColors[subgraph]@OptionValue[defaultVF2Colors, {optsub}, "VertexColors"];
      ecol    = parseEdgeColors[graph]@OptionValue[defaultVF2Colors, {opt}, "EdgeColors"];
      ecolsub = parseEdgeColors[subgraph]@OptionValue[defaultVF2Colors, {optsub}, "EdgeColors"];
      check@ig1@"vf2SubisomorphismCount"[ManagedLibraryExpressionID[ig2], vcol, vcolsub, ecol, ecolsub]
    ]

IGVF2SubisomorphismCount[subgraph_?igGraphQ, graph_?igGraphQ] :=
    catch@Block[{ig1 = igMake[graph], ig2 = igMake[subgraph]},
      vf2CheckMulti /@ {subgraph, graph};
      check@ig1@"vf2SubisomorphismCount"[ManagedLibraryExpressionID[ig2], {}, {}, {}, {}]
    ]


(** LAD **)

defaultLADColors = {"VertexColors" -> None};

Options[IGLADSubisomorphicQ] = { "Induced" -> False };
SyntaxInformation[IGLADSubisomorphicQ] = {"ArgumentsPattern" -> {{__}, {__}, OptionsPattern[]}};

IGLADSubisomorphicQ[subgraph_?igGraphQ, graph_?igGraphQ, opt : OptionsPattern[]] :=
    Block[{ig1 = igMakeFast[graph], ig2 = igMakeFast[subgraph]},
      sck@ig1@"ladSubisomorphic"[ManagedLibraryExpressionID[ig2], OptionValue["Induced"]]
    ]

IGLADSubisomorphicQ[{subgraph_?igGraphQ, colsub : OptionsPattern[]}, {graph_?igGraphQ, col : OptionsPattern[]}, opt : OptionsPattern[]] :=
    catch@Block[{vcol, vcolsub},
      vcol    = parseVertexColors[graph]@OptionValue[defaultLADColors, {col}, "VertexColors"];
      vcolsub = parseVertexColors[subgraph]@OptionValue[defaultLADColors, {colsub}, "VertexColors"];
      If[vcol === {} || vcolsub === {},
        If[vcol =!= vcolsub, Message[IGraphM::vcmm]];
        IGLADSubisomorphicQ[subgraph, graph, opt]
        ,
        Block[{ig1 = igMakeFast[graph], ig2 = igMakeFast[subgraph]},
          check@ig1@"ladSubisomorphicColored"[
            ManagedLibraryExpressionID[ig2], Boole@TrueQ@OptionValue["Induced"],
            Flatten@Position[vcol, #, {1}] - 1& /@ vcolsub
          ]
        ]
      ]
    ]


Options[IGLADGetSubisomorphism] = { "Induced" -> False };
SyntaxInformation[IGLADGetSubisomorphism] = {"ArgumentsPattern" -> {{__}, {__}, OptionsPattern[]}};

IGLADGetSubisomorphism[subgraph_?igGraphQ, graph_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Block[{ig1 = igMakeFast[graph], ig2 = igMakeFast[subgraph], result},
      result = igIndexVec@check@ig1@"ladGetSubisomorphism"[ManagedLibraryExpressionID[ig2], OptionValue["Induced"]];
      If[result === {}, Return[{}]];
      List@AssociationThread[
        VertexList[subgraph],
        igVertexNames[graph][result]
      ]
    ]

IGLADGetSubisomorphism[{subgraph_?igGraphQ, colsub : OptionsPattern[]}, {graph_?igGraphQ, col : OptionsPattern[]}, opt : OptionsPattern[]] :=
    catch@Block[{vcol, vcolsub},
      vcol    = parseVertexColors[graph]@OptionValue[defaultLADColors, {col}, "VertexColors"];
      vcolsub = parseVertexColors[subgraph]@OptionValue[defaultLADColors, {colsub}, "VertexColors"];
      If[vcol === {} || vcolsub === {},
        If[vcol =!= vcolsub, Message[IGraphM::vcmm]];
        IGLADGetSubisomorphism[subgraph, graph, opt]
        ,
        Block[{ig1 = igMakeFast[graph], ig2 = igMakeFast[subgraph], result},
          result = igIndexVec@check@ig1@"ladGetSubisomorphismColored"[
            ManagedLibraryExpressionID[ig2], Boole@TrueQ@OptionValue["Induced"],
            Flatten@Position[vcol, #, {1}] - 1& /@ vcolsub
          ];
          If[result === {}, Return[{}]];
          List@AssociationThread[
            VertexList[subgraph],
            igVertexNames[graph][result]
          ]
        ]
      ]
    ]


Options[IGLADFindSubisomorphisms] = { "Induced" -> False };
SyntaxInformation[IGLADFindSubisomorphisms] = {"ArgumentsPattern" -> {{__}, {__}, OptionsPattern[]}};

IGLADFindSubisomorphisms[subgraph_?igGraphQ, graph_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Block[{ig1 = igMakeFast[graph], ig2 = igMakeFast[subgraph], result},
      result = igIndexVec@check@ig1@"ladFindSubisomorphisms"[ManagedLibraryExpressionID[ig2], Boole@TrueQ@OptionValue["Induced"], {}];
      AssociationThread[
        VertexList[subgraph],
        igVertexNames[graph][#]
      ]& /@ result
    ]

IGLADFindSubisomorphisms[{subgraph_?igGraphQ, colsub : OptionsPattern[]}, {graph_?igGraphQ, col : OptionsPattern[]}, opt : OptionsPattern[]] :=
    catch@Block[{ig1 = igMakeFast[graph], ig2 = igMakeFast[subgraph], result, vcol, vcolsub, domain},
      vcol    = parseVertexColors[graph]@OptionValue[defaultLADColors, {col}, "VertexColors"];
      vcolsub = parseVertexColors[subgraph]@OptionValue[defaultLADColors, {colsub}, "VertexColors"];
      If[vcol === {} || vcolsub === {},
        If[vcol =!= vcolsub, Message[IGraphM::vcmm]];
        domain = {}
        ,
        domain = Flatten@Position[vcol, #, {1}] - 1& /@ vcolsub;
      ];
      result = igIndexVec@check@ig1@"ladFindSubisomorphisms"[ManagedLibraryExpressionID[ig2], Boole@TrueQ@OptionValue["Induced"], domain];
      AssociationThread[
        VertexList[subgraph],
        igVertexNames[graph][#]
      ]& /@ result
    ]


Options[IGLADSubisomorphismCount] = { "Induced" -> False };
SyntaxInformation[IGLADSubisomorphismCount] = {"ArgumentsPattern" -> {{__}, {__}, OptionsPattern[]}};

IGLADSubisomorphismCount[subgraph_?igGraphQ, graph_?igGraphQ, opt : OptionsPattern[]] :=
    Block[{ig1 = igMakeFast[graph], ig2 = igMakeFast[subgraph], result},
      sck@ig1@"ladCountSubisomorphisms"[ManagedLibraryExpressionID[ig2], OptionValue["Induced"]]
    ]

IGLADSubisomorphismCount[{subgraph_?igGraphQ, colsub : OptionsPattern[]}, {graph_?igGraphQ, col : OptionsPattern[]}, opt : OptionsPattern[]] :=
    catch@Block[{ig1 = igMakeFast[graph], ig2 = igMakeFast[subgraph], result, vcol, vcolsub, domain},
      vcol    = parseVertexColors[graph]@OptionValue[defaultLADColors, {col}, "VertexColors"];
      vcolsub = parseVertexColors[subgraph]@OptionValue[defaultLADColors, {colsub}, "VertexColors"];
      If[vcol === {} || vcolsub === {},
        If[vcol =!= vcolsub, Message[IGraphM::vcmm]];
        domain = {}
        ,
        domain = Flatten@Position[vcol, #, {1}] - 1& /@ vcolsub;
      ];
      check@ig1@"ladCountSubisomorphismsColored"[ManagedLibraryExpressionID[ig2], Boole@TrueQ@OptionValue["Induced"], domain]
    ]


(* Directed acylic graphs and topological ordering *)

SyntaxInformation[IGTopologicalOrdering] = {"ArgumentsPattern" -> {_}};
IGTopologicalOrdering[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      igIndexVec@check@ig@"topologicalSorting"[]
    ]


igFeedbackArcSetMethods = <| "IntegerProgramming" -> True, "EadesLinSmyth" -> False |>;

Options[IGFeedbackArcSet] = { Method -> "IntegerProgramming" };
SyntaxInformation[IGFeedbackArcSet] = {"ArgumentsPattern" -> {_, OptionsPattern[]} };

IGFeedbackArcSet::bdmtd =
    "Value of option Method -> `` is not one of " <>
    ToString[Keys[igFeedbackArcSetMethods], InputForm] <> ".";

amendUsage[IGFeedbackArcSet, "Available Method options: <*Keys[igFeedbackArcSetMethods]*>. \"IntegerProgramming\" is guaranteed to find a minimum feedback arc set."];

IGFeedbackArcSet[graph_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMake[graph]}, (* use igMake because edge ordering matters *)
      Part[
        EdgeList[graph],
        igIndexVec@check@ig@"feedbackArcSet"[Lookup[igFeedbackArcSetMethods, OptionValue[Method], Message[IGFeedbackArcSet::bdmtd, OptionValue[Method]]; throw[$Failed]]]
      ]
    ]

(* Motifs and subgraph counts *)

SyntaxInformation[IGDyadCensus] = {"ArgumentsPattern" -> {_}};
IGDyadCensus[graph_?igGraphQ] := Block[{ig = igMakeFast[graph]}, AssociationThread[{"Mutual", "Asymmetric", "Null"}, Round@ig@"dyadCensus"[]]]

SyntaxInformation[IGTriadCensus] = {"ArgumentsPattern" -> {_}};
IGTriadCensus[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      AssociationThread[
        {"003", "012", "102", "021D", "021U", "021C", "111D", "111U", "030T", "030C", "201", "120D", "120U", "120C", "210", "300"},
        Round@check@ig@"triadCensus"[]
      ]
    ]

SyntaxInformation[IGMotifs] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}};
Options[IGMotifs] = { DirectedEdges -> Automatic };
IGMotifs[graph_?igGraphQ, size_?Internal`PositiveIntegerQ, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFast[graph]},
      Switch[OptionValue[DirectedEdges],
        True, ig@"makeDirected"[],
        False, ig@"makeUndirected"[]
      ];
      Round@Developer`FromPackedArray@check@ig@"motifs"[size, ConstantArray[0, size]
      ]
    ]

SyntaxInformation[IGMotifsTotalCount] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}};
Options[IGMotifsTotalCount] = { DirectedEdges -> Automatic };
IGMotifsTotalCount[graph_?igGraphQ, size_?Internal`PositiveIntegerQ, opt : OptionsPattern[]] :=
    Block[{ig = igMakeFast[graph]},
      Switch[OptionValue[DirectedEdges],
        True, ig@"makeDirected"[],
        False, ig@"makeUndirected"[]
      ];
      sck@ig@"motifsNo"[size, ConstantArray[0, size]]
    ]

SyntaxInformation[IGMotifsEstimateTotalCount] = {"ArgumentsPattern" -> {_, _, _, OptionsPattern[]}};
Options[IGMotifsEstimateTotalCount] = { DirectedEdges -> Automatic };
IGMotifsEstimateTotalCount[graph_?igGraphQ, size_?Internal`PositiveIntegerQ, sampleSize_?Internal`PositiveIntegerQ, opt : OptionsPattern[]] :=
    Block[{ig = igMakeFast[graph]},
      Switch[OptionValue[DirectedEdges],
        True, ig@"makeDirected"[],
        False, ig@"makeUndirected"[]
      ];
      sck@ig@"motifsEstimate"[size, ConstantArray[0, size], sampleSize]
    ]

SyntaxInformation[IGTriangles] = {"ArgumentsPattern" -> {_}};
IGTriangles[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      Partition[igVertexNames[graph]@igIndexVec@check@ig@"triangles"[], 3]
    ]

igAdjacentTriangleCount[graph_, vs_] :=
    (* no catch *) Block[{ig = igMakeFast[graph]},
      Round@check@ig@"countAdjacentTriangles"[vss[graph][vs]]
    ]

SyntaxInformation[IGAdjacentTriangleCount] = {"ArgumentsPattern" -> {_, _.}};
IGAdjacentTriangleCount[graph_?igGraphQ, {}] := {}
IGAdjacentTriangleCount[graph_?igGraphQ, vs : (_List | All) : All] := catch@igAdjacentTriangleCount[graph, vs]
IGAdjacentTriangleCount[graph_?igGraphQ, v_] := catch@First@igAdjacentTriangleCount[graph, {v}]

(* Shortest paths *)

Options[IGDistanceMatrix] = {Method -> Automatic};
igDistanceMatrixMethods = <|
  "Unweighted" -> igDistanceMatrixUnweighted,
  "Dijkstra" -> igDistanceMatrixDijkstra,
  "BellmanFord" -> igDistanceMatrixBellmanFord,
  "Johnson" -> igDistanceMatrixJohnson
|>;

IGDistanceMatrix::bdmtd = "Value of option Method -> `` is not one of " <> ToString[Keys[igDistanceMatrixMethods], InputForm] <> ".";

SyntaxInformation[IGDistanceMatrix] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGDistanceMatrix, Graph]};

amendUsage[IGDistanceMatrix, "Available Method options: <*Keys[igDistanceMatrixMethods]*>."];

IGDistanceMatrix[graph_?igGraphQ, from : (_?ListQ | All) : All, to : (_?ListQ | All) : All, opt : OptionsPattern[]] :=
    Module[{method},
      method = OptionValue[Method];
      If[from === {}, Return[{}]];
      If[to === {}, Return[ConstantArray[{}, Length[from]]]];
      If[Not@MemberQ[Keys[igDistanceMatrixMethods] ~Join~ {Automatic}, method],
        Message[IGDistanceMatrix::bdmtd, method];
        Return[$Failed]
      ];
      If[method === Automatic,
        method = Which[
          Not@igWeightedGraphQ[graph], "Unweighted",
          TrueQ[Min@PropertyValue[graph, EdgeWeight] >= 0], "Dijkstra",
          True, "Johnson"
        ]
      ];
      igDistanceMatrixMethods[method][graph, vss[graph][from], vss[graph][to]]
    ]

igDistanceMatrixUnweighted[graph_, from_, to_] :=
    catch@Block[{ig = igMakeFast[graph]},
      Round@fixInfNaN@check@ig@"shortestPaths"[from, to]
    ]

igDistanceMatrixDijkstra[graph_, from_, to_] :=
    catch@Block[{ig = igMakeFastWeighted[graph]},
      fixInfNaN@check@ig@"shortestPathsDijkstra"[from, to]
    ]

igDistanceMatrixBellmanFord[graph_, from_, to_] :=
    catch@Block[{ig = igMakeFastWeighted[graph]},
      fixInfNaN@check@ig@"shortestPathsBellmanFord"[from, to]
    ]

igDistanceMatrixJohnson[graph_, from_, to_] :=
    catch@Block[{ig = igMakeFastWeighted[graph]},
      fixInfNaN@check@ig@"shortestPathsJohnson"[from, to]
    ]


Options[IGDiameter] = { Method -> Automatic, "ByComponents" -> False };

igDiameterMethods = <|
  "Unweighted" -> igDiameterUnweighted,
  "Dijkstra" -> igDiameterDijkstra
|>;

SyntaxInformation[IGDiameter] = {"ArgumentsPattern" -> {_, OptionsPattern[]}};

amendUsage[IGDiameter, "Available Method options: <*Keys[igDiameterMethods]*>."];

IGDiameter::bdmtd = "Value of option Method -> `` is not one of " <> ToString[Keys[igDiameterMethods], InputForm] <> ".";

IGDiameter[graph_?igGraphQ, opt : OptionsPattern[]] :=
    Module[{method},
      method = OptionValue[Method];
      If[Not@MemberQ[Keys[igDiameterMethods] ~Join~ {Automatic}, method],
        Message[IGDiameter::bdmtd, method];
        Return[$Failed]
      ];
      If[method === Automatic,
        method = Which[
          igWeightedGraphQ[graph], "Dijkstra",
          True, "Unweighted"
        ]
      ];
      igDiameterMethods[method][graph, OptionValue["ByComponents"]]
    ]

igDiameterUnweighted[graph_, bycomp_] :=
    catch@Block[{ig = igMakeFast[graph], diam},
      diam = check@ig@"diameter"[bycomp];
      If[diam == VertexCount[graph], Infinity, diam]
    ]

igDiameterDijkstra[graph_, bycomp_] :=
    Block[{ig = igMakeFastWeighted[graph]},
      sck@ig@"diameterDijkstra"[bycomp]
    ]


Options[IGFindDiameter] = { Method -> Automatic, "ByComponents" -> False };

igFindDiameterMethods = <|
  "Unweighted" -> igFindDiameterUnweighted,
  "Dijkstra" -> igFindDiameterDijkstra
|>;

SyntaxInformation[IGFindDiameter] = {"ArgumentsPattern" -> {_, OptionsPattern[]}};

amendUsage[IGFindDiameter, "Available Method options: <*Keys[igDiameterMethods]*>."];

IGFindDiameter::bdmtd = "Value of option Method -> `` is not one of " <> ToString[Keys[igFindDiameterMethods], InputForm] <> ".";

IGFindDiameter[graph_?igGraphQ, opt : OptionsPattern[]] :=
    Module[{method},
      method = OptionValue[Method];
      If[Not@MemberQ[Keys[igFindDiameterMethods] ~Join~ {Automatic}, method],
        Message[IGFindDiameter::bdmtd, method];
        Return[$Failed]
      ];
      If[method === Automatic,
        method = Which[
          igWeightedGraphQ[graph], "Dijkstra",
          True, "Unweighted"
        ];
        method = 11;
      ];
      igFindDiameterMethods[method][graph, OptionValue["ByComponents"]]
    ]

igFindDiameterUnweighted[graph_, bycomp_] :=
    catch@Block[{ig = igMakeFastWeighted[graph], test},
      test =
      igVertexNames[graph]@igIndexVec@check@ig@"findDiameter"[bycomp]
    ]

igFindDiameterDijkstra[graph_, bycomp_] :=
    catch@Block[{ig = igMakeFastWeighted[graph]},
      igVertexNames[graph]@igIndexVec@check@ig@"findDiameterDijkstra"[bycomp]
    ]


SyntaxInformation[IGDistanceCounts] = {"ArgumentsPattern" -> {_}};
IGDistanceCounts[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      Round@check@ig@"shortestPathCounts"[]
    ]


Options[IGDistanceHistogram] = { Method -> "Dijkstra" };
SyntaxInformation[IGDistanceHistogram] = {"ArgumentsPattern" -> {_, _, _., _., OptionsPattern[]}};
igDistanceHistogramMethods = <| "Dijkstra" -> 0, "BellmanFord" -> 1 |>;
amendUsage[IGDistanceHistogram, "Available Method options: <*Keys[igDistanceHistogramMethods]*>."];
IGDistanceHistogram[graph_?igGraphQ, binsize_?positiveNumericQ, from : (_?ListQ | All) : All, to : (_?ListQ | All) : All, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], fromidx, toidx},
      If[from === {} || to === {},
        Return[{}]
      ];
      If[from === All,
        fromidx = Range[0, VertexCount[graph]-1], (* Must not use {} for All. See C++ code. *)
        fromidx = vss[graph][from]
      ];
      toidx = vss[graph][to];

      check@ig@"shortestPathWeightedHistogram"[binsize, fromidx, toidx, Lookup[igDistanceHistogramMethods, OptionValue[Method], -1] ]
    ]

SyntaxInformation[IGAveragePathLength] = {"ArgumentsPattern" -> {_}};
IGAveragePathLength[graph_?igGraphQ] :=
    Block[{ig = igMakeFast[graph]},
      sck@ig@"averagePathLength"[]
    ]

SyntaxInformation[IGGirth] = {"ArgumentsPattern" -> {_}};
IGGirth[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      Round@check@ig@"girth"[]
    ]


igEccentricity[graph_, vs_] :=
    Block[{ig = igMakeFast[graph]},
      Round@check@ig@"eccentricity"[vss[graph][vs]]
    ]

SyntaxInformation[IGEccentricity] = {"ArgumentsPattern" -> {_, _.}};
IGEccentricity[graph_?igGraphQ, {}] := {}
IGEccentricity[graph_?igGraphQ, vs : (_List | All) : All] := catch@igEccentricity[graph, vs]
IGEccentricity[graph_?igGraphQ, v_] := catch@First@igEccentricity[graph, {v}]


SyntaxInformation[IGRadius] = {"ArgumentsPattern" -> {_}};
IGRadius[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      Round@check@ig@"radius"[]
    ]



(* Cliques *)

SyntaxInformation[IGCliques] = {"ArgumentsPattern" -> {_, _.}};
IGCliques[graph_] := IGCliques[graph, Infinity]
IGCliques[graph_, max : (_Integer | Infinity)] := IGCliques[graph, {1, max}]
IGCliques[graph_, {size_}] := IGCliques[graph, {size, size}]
IGCliques[graph_?igGraphQ, {min_?Internal`PositiveMachineIntegerQ, max : (_?Internal`PositiveMachineIntegerQ | Infinity)}] /; max >= min :=
    igCliques[graph, {min, infToZero[max]}]
igCliques[graph_, {min_, max_}] :=
    catch@Block[{ig = igMakeFast[graph]},
      igUnpackVertexSet[graph]@check@ig@"cliques"[min, max]
    ]

SyntaxInformation[IGCliqueSizeCounts] = {"ArgumentsPattern" -> {_, _.}};
IGCliqueSizeCounts[graph_] := IGCliqueSizeCounts[graph, Infinity]
IGCliqueSizeCounts[graph_, max : (_Integer | Infinity)] := IGCliqueSizeCounts[graph, {1, max}]
IGCliqueSizeCounts[graph_, {size_}] := IGCliqueSizeCounts[graph, {size, size}]
IGCliqueSizeCounts[graph_?igGraphQ, {min_?Internal`PositiveMachineIntegerQ, max : (_?Internal`PositiveMachineIntegerQ | Infinity)}] /; max >= min :=
    igCliqueSizeCounts[graph, {min, infToZero[max]}]
igCliqueSizeCounts[graph_, {min_, max_}] :=
    catch@Block[{ig = igMakeFast[graph]},
      Round@check@ig@"cliqueDistribution"[min, max]
    ]

SyntaxInformation[IGMaximalCliqueSizeCounts] = {"ArgumentsPattern" -> {_, _.}};
IGMaximalCliqueSizeCounts[graph_] := IGMaximalCliqueSizeCounts[graph, Infinity]
IGMaximalCliqueSizeCounts[graph_, max : (_Integer | Infinity)] := IGMaximalCliqueSizeCounts[graph, {1, max}]
IGMaximalCliqueSizeCounts[graph_, {size_}] := IGMaximalCliqueSizeCounts[graph, {size, size}]
IGMaximalCliqueSizeCounts[graph_?igGraphQ, {min_?Internal`PositiveMachineIntegerQ, max : (_?Internal`PositiveMachineIntegerQ | Infinity)}] /; max >= min :=
    igMaximalCliqueSizeCounts[graph, {min, infToZero[max]}]
igMaximalCliqueSizeCounts[graph_, {min_, max_}] :=
    catch@Block[{ig = igMakeFast[graph]},
      Round@check@ig@"maximalCliqueDistribution"[min, max]
    ]

SyntaxInformation[IGMaximalCliques] = {"ArgumentsPattern" -> {_, _.}};
IGMaximalCliques[graph_] := IGMaximalCliques[graph, Infinity]
IGMaximalCliques[graph_, max : (_Integer | Infinity)] := IGMaximalCliques[graph, {1, max}]
IGMaximalCliques[graph_, {size_}] := IGMaximalCliques[graph, {size, size}]
IGMaximalCliques[graph_?igGraphQ, {min_?Internal`PositiveMachineIntegerQ, max : (_?Internal`PositiveMachineIntegerQ | Infinity)}] /; max >= min :=
    igMaximalCliques[graph, {min, infToZero[max]}]
igMaximalCliques[graph_, {min_, max_}] :=
    catch@Block[{ig = igMakeFast[graph]},
      igUnpackVertexSet[graph]@check@ig@"maximalCliques"[min, max]
    ]

SyntaxInformation[IGMaximalCliquesCount] = {"ArgumentsPattern" -> {_, _.}};
IGMaximalCliquesCount[graph_] := IGMaximalCliquesCount[graph, Infinity]
IGMaximalCliquesCount[graph_, max : (_Integer | Infinity)] := IGMaximalCliquesCount[graph, {1, max}]
IGMaximalCliquesCount[graph_, {size_}] := IGMaximalCliquesCount[graph, {size, size}]
IGMaximalCliquesCount[graph_?igGraphQ, {min_?Internal`PositiveMachineIntegerQ, max : (_?Internal`PositiveMachineIntegerQ | Infinity)}] /; max >= min :=
    igMaximalCliquesCount[graph, {min, infToZero[max]}]
igMaximalCliquesCount[graph_, {min_, max_}] :=
    catch@Block[{ig = igMakeFast[graph]},
      Round@check@ig@"maximalCliquesCount"[min, max]
    ]

SyntaxInformation[IGLargestCliques] = {"ArgumentsPattern" -> {_}};
IGLargestCliques[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      igUnpackVertexSet[graph]@check@ig@"largestCliques"[]
    ]

SyntaxInformation[IGCliqueNumber] = {"ArgumentsPattern" -> {_}};
IGCliqueNumber[graph_?igGraphQ] := Block[{ig = igMakeFast[graph]}, sck@ig@"cliqueNumber"[]]

SyntaxInformation[IGWeightedCliques] = {"ArgumentsPattern" -> {_, {_, _}}};
IGWeightedCliques[graph_?igGraphQ, {min_?Internal`NonNegativeMachineIntegerQ, max : (_?Internal`PositiveMachineIntegerQ | Infinity)}] /; max >= min :=
    If[vertexWeightedQ[graph], igCliquesWeighted, igCliques][graph, {min, infToZero[max]}]
igCliquesWeighted[graph_, {min_, max_}] :=
    catch@Block[{ig = igMakeFast[graph]},
      igUnpackVertexSet[graph]@check@ig@"cliquesWeighted"[min, max, PropertyValue[graph, VertexWeight], False]
    ]

SyntaxInformation[IGMaximalWeightedCliques] = {"ArgumentsPattern" -> {_, {_, _}}};
IGMaximalWeightedCliques[graph_?igGraphQ, {min_?Internal`NonNegativeMachineIntegerQ, max : (_?Internal`PositiveMachineIntegerQ | Infinity)}] /; max >= min :=
    If[vertexWeightedQ[graph], igMaximalCliquesWeighted, igMaximalCliques][graph, {min, infToZero[max]}]
igMaximalCliquesWeighted[graph_, {min_, max_}] :=
    catch@Block[{ig = igMakeFast[graph]},
      igUnpackVertexSet[graph]@check@ig@"cliquesWeighted"[min, max, PropertyValue[graph, VertexWeight], True (* maximal *)]
    ]

SyntaxInformation[IGLargestWeightedCliques] = {"ArgumentsPattern" -> {_}};
IGLargestWeightedCliques[graph_?igGraphQ] :=
    If[vertexWeightedQ[graph], igLargestCliquesWeighted, IGLargestCliques][graph]
igLargestCliquesWeighted[graph_] :=
    catch@Block[{ig = igMakeFast[graph]},
      igUnpackVertexSet[graph]@check@ig@"largestCliquesWeighted"[PropertyValue[graph, VertexWeight]]
    ]

SyntaxInformation[IGWeightedCliqueNumber] = {"ArgumentsPattern" -> {_}};
IGWeightedCliqueNumber[graph_?igGraphQ] :=
    If[vertexWeightedQ[graph], igMaximumCliqueWeight, IGCliqueNumber][graph]
igMaximumCliqueWeight[graph_] :=
    Block[{ig = igMakeFast[graph]},
      sck@ig@"cliqueNumberWeighted"[PropertyValue[graph, VertexWeight]]
    ]



(* Independent vertex sets *)

SyntaxInformation[IGIndependentVertexSets] = {"ArgumentsPattern" -> {_, _.}};
IGIndependentVertexSets[graph_] := IGIndependentVertexSets[graph, Infinity]
IGIndependentVertexSets[graph_, max : (_Integer | Infinity)] := IGIndependentVertexSets[graph, {1, max}]
IGIndependentVertexSets[graph_, {size_}] := IGIndependentVertexSets[graph, {size, size}]
IGIndependentVertexSets[graph_?igGraphQ, {min_?Internal`PositiveMachineIntegerQ, max : (_?Internal`PositiveMachineIntegerQ | Infinity)}] /; max >= min :=
    igIndependentVertexSets[graph, {min, infToZero[max]}]
igIndependentVertexSets[graph_, {min_, max_}] :=
    catch@Block[{ig = igMakeFast[graph]},
      igUnpackVertexSet[graph]@check@ig@"independentVertexSets"[min, max]
    ]

SyntaxInformation[IGLargestIndependentVertexSets] = {"ArgumentsPattern" -> {_}};
IGLargestIndependentVertexSets[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      igUnpackVertexSet[graph]@check@ig@"largestIndependentVertexSets"[]
    ]

SyntaxInformation[IGMaximalIndependentVertexSets] = {"ArgumentsPattern" -> {_}};
IGMaximalIndependentVertexSets[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      igUnpackVertexSet[graph]@check@ig@"maximalIndependentVertexSets"[]
    ]

SyntaxInformation[IGIndependenceNumber] = {"ArgumentsPattern" -> {_}};
IGIndependenceNumber[graph_?igGraphQ] := Block[{ig = igMakeFast[graph]}, sck@ig@"independenceNumber"[]]

(* Graph drawing (layouts *)

getVertexCoords[graph_] :=
    With[{coords = GraphEmbedding[graph]},
      If[MatrixQ[coords],
        coords,
        Message[IGraphM::lytcrd]; {{}}
      ]
    ]

continueLayout[graph_, False, ___] := Sequence[{{}}, False]
continueLayout[graph_, True, scale_ : 1, dim_ : 2] :=
    Sequence@@Module[{coords},
      coords = getVertexCoords[graph];
      If[coords =!= {{}} && Not@MatchQ[Dimensions[coords], {_, dim}],
        Message[IGraphM::lytdim];
        coords = {{}}
      ];
      {coords / scale, coords =!= {{}}}
    ]
continueLayout[graph_, cont_, ___] := ( Message[IGraphM::lytcnt, cont]; continueLayout[graph, False] )
continueLayout3D[graph_, cont_, scale_ : 1] := continueLayout[graph, cont, scale, 3]

(* These should simply use Graph and Graph3D but due to a Mathematica bug
   that won't work on some graphs, such as those returns by KaryTree.
   Thus we use SetProperty with GraphLayout instead.
*)
setVertexCoords[g_, coords_] := SetProperty[g, {VertexCoordinates -> Thread[ VertexList[g] -> coords ], PlotRange -> All}]
setVertexCoords3D[g_, coords_] := SetProperty[g, {GraphLayout -> {"Dimension" -> 3}, VertexCoordinates -> Thread[ VertexList[g] -> coords ], PlotRange -> All}]


igAlign[{}] := {}
igAlign[pts_] := PrincipalComponents[pts]

align[True] = igAlign;
align[False] = Identity;
align[val_] := (Message[IGraphM::lytaln, val]; Identity)

SyntaxInformation[IGLayoutRandom] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[Graph]};
IGLayoutRandom[graph_?igGraphQ, opt : OptionsPattern[Graph]] :=
    catch@Block[{ig = igMakeFast[graph]},
      applyGraphOpt[opt]@setVertexCoords[graph, check@ig@"layoutRandom"[]]
    ]

SyntaxInformation[IGLayoutCircle] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[Graph]};
IGLayoutCircle[graph_?igGraphQ, opt : OptionsPattern[Graph]] :=
    catch@Block[{ig = igMakeFast[graph]},
      applyGraphOpt[opt]@setVertexCoords[graph, check@ig@"layoutCircle"[]]
    ]

SyntaxInformation[IGLayoutSphere] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[Graph3D]};
IGLayoutSphere[graph_?igGraphQ, opt : OptionsPattern[Graph3D]] :=
    catch@Block[{ig = igMakeFast[graph]},
      applyGraphOpt3D[opt]@setVertexCoords3D[graph, check@ig@"layoutSphere"[]]
    ]


Options[IGLayoutGraphOpt] = {
  "MaxIterations" -> 500, "NodeCharge" -> 0.001, "NodeMass" -> 30, "SpringLength" -> 0,
  "SpringConstant" -> 1, "MaxStepMovement" -> 5,
  "Continue" -> False, "Align" -> True
};

SyntaxInformation[IGLayoutGraphOpt] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutGraphOpt, Graph]};

IGLayoutGraphOpt[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutGraphOpt,Graph}]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], scale = 0.01},
      applyGraphOpt[opt]@setVertexCoords[graph,
          scale align[OptionValue["Align"]]@check@ig@"layoutGraphOpt"[continueLayout[graph, OptionValue["Continue"], scale],
            OptionValue["MaxIterations"], OptionValue["NodeCharge"], OptionValue["NodeMass"],
            OptionValue["SpringLength"], OptionValue["SpringConstant"], OptionValue["MaxStepMovement"]
          ]
      ]
    ]


Options[IGLayoutKamadaKawai] = {
  "MaxIterations" -> Automatic, "Epsilon" -> 0, "KamadaKawaiConstant" -> Automatic,
  "Continue" -> False, "Align" -> True
};

SyntaxInformation[IGLayoutKamadaKawai] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutKamadaKawai, Graph]};

IGLayoutKamadaKawai[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutKamadaKawai,Graph}]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], maxiter, kkconst, scale = 0.5},
      maxiter = Replace[OptionValue["MaxIterations"], Automatic :> 10 VertexCount[graph]];
      kkconst = Replace[OptionValue["KamadaKawaiConstant"], Automatic :> Max[1, VertexCount[graph]]];
      applyGraphOpt[opt]@setVertexCoords[graph,
        scale align[OptionValue["Align"]]@check@ig@"layoutKamadaKawai"[continueLayout[graph, OptionValue["Continue"], scale],
          maxiter, OptionValue["Epsilon"], kkconst]
      ]
    ]


Options[IGLayoutKamadaKawai3D] = {
  "MaxIterations" -> Automatic, "Epsilon" -> 0, "KamadaKawaiConstant" -> Automatic,
  "Continue" -> False, "Align" -> True
};

SyntaxInformation[IGLayoutKamadaKawai3D] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutKamadaKawai3D, Graph3D]};

IGLayoutKamadaKawai3D[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutKamadaKawai3D,Graph3D}]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], maxiter, kkconst, scale = 0.5},
      maxiter = Replace[OptionValue["MaxIterations"], Automatic :> 10 VertexCount[graph]];
      kkconst = Replace[OptionValue["KamadaKawaiConstant"], Automatic :> Max[1, VertexCount[graph]]];
      applyGraphOpt3D[opt]@setVertexCoords3D[graph,
        scale align[OptionValue["Align"]]@check@ig@"layoutKamadaKawai3D"[continueLayout3D[graph, OptionValue["Continue"], scale],
          maxiter, OptionValue["Epsilon"], kkconst]
      ]
    ]


igFruchtermanReingoldMethods = <| Automatic -> 2, False -> 1, True -> 0 |>;

Options[IGLayoutFruchtermanReingold] = {
  "MaxIterations" -> 500, "MaxMovement" -> 5, "UseGrid" -> Automatic,
  "Continue" -> False, "Align" -> True
};

SyntaxInformation[IGLayoutFruchtermanReingold] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutFruchtermanReingold, Graph]};

IGLayoutFruchtermanReingold[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutFruchtermanReingold,Graph}]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], scale = 0.25},
      applyGraphOpt[opt]@setVertexCoords[graph,
        scale align[OptionValue["Align"]]@check@ig@"layoutFruchtermanReingold"[continueLayout[graph, OptionValue["Continue"], scale],
          OptionValue["MaxIterations"], OptionValue["MaxMovement"], Lookup[igFruchtermanReingoldMethods, OptionValue["UseGrid"], -1]
        ]
      ]
    ]


Options[IGLayoutFruchtermanReingold3D] = {
  "MaxIterations" -> 500, "MaxMovement" -> 5,
  "Continue" -> False, "Align" -> True
};

SyntaxInformation[IGLayoutFruchtermanReingold3D] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutFruchtermanReingold3D, Graph3D]};

IGLayoutFruchtermanReingold3D[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutFruchtermanReingold3D,Graph3D}]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], scale = 0.25},
      applyGraphOpt3D[opt]@setVertexCoords3D[graph,
        scale align[OptionValue["Align"]]@check@ig@"layoutFruchtermanReingold3D"[continueLayout3D[graph, OptionValue["Continue"], scale],
          OptionValue["MaxIterations"], OptionValue["MaxMovement"]
        ]
      ]
    ]


Options[IGLayoutGEM] = {
  "MaxIterations" -> Automatic, "Continue" -> False, "Align" -> True,
  "MaxTemperature" -> Automatic, "MinTemperature" -> 1/10, "InitTemperature" -> Automatic
};

SyntaxInformation[IGLayoutGEM] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutGEM, Graph]};

IGLayoutGEM[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutGEM,Graph}]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], maxiter, maxtemp, inittemp, scale = 3*^-3},
      maxiter = Replace[OptionValue["MaxIterations"], Automatic :> 40 VertexCount[graph]^2];
      maxtemp = Replace[OptionValue["MaxTemperature"], Automatic :> Max[1, VertexCount[graph]]];
      inittemp = Replace[OptionValue["InitTemperature"], Automatic :> Max[1, Sqrt@VertexCount[graph]]];
      applyGraphOpt[opt]@setVertexCoords[graph,
        scale align[OptionValue["Align"]]@check@ig@"layoutGEM"[continueLayout[graph, OptionValue["Continue"], scale],
          maxiter, maxtemp, OptionValue["MinTemperature"], inittemp
        ]
      ]
    ]


Options[IGLayoutDavidsonHarel] = {
  "MaxIterations" -> 10, "Continue" -> False, "Align" -> True,
  "FineTuningIterations" -> Automatic, "CoolingFactor" -> 0.75,
  "NodeDistanceWeight" -> 1.0, "BorderDistanceWeight" -> 0.0, "EdgeLengthWeight" -> Automatic,
  "EdgeCrossingWeight" -> Automatic, "EdgeDistanceWeight" -> Automatic
};

SyntaxInformation[IGLayoutDavidsonHarel] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutDavidsonHarel, Graph]};

IGLayoutDavidsonHarel[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutDavidsonHarel,Graph}]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], tuneiter, edgelenw, edgecrossw, edgedistw, dens, scale = 0.1},
      dens = If[VertexCount[graph] == 0, 0, GraphDensity[graph]];
      tuneiter = Replace[OptionValue["FineTuningIterations"], Automatic :> Max[10, Round@Log[2, VertexCount[graph]]]];
      edgelenw = Replace[OptionValue["EdgeLengthWeight"], Automatic :> dens/10];
      edgecrossw = Replace[OptionValue["EdgeCrossingWeight"], Automatic :> 1 - dens];
      edgedistw = Replace[OptionValue["EdgeDistanceWeight"], Automatic :> 1 - dens / 5];
      applyGraphOpt[opt]@setVertexCoords[graph,
        scale align[OptionValue["Align"]]@check@ig@"layoutDavidsonHarel"[continueLayout[graph, OptionValue["Continue"], scale],
          OptionValue["MaxIterations"],
          tuneiter, OptionValue["CoolingFactor"], OptionValue["NodeDistanceWeight"],
          OptionValue["BorderDistanceWeight"], edgelenw, edgecrossw, edgedistw
        ]
      ]
    ]

(*
IGLayoutMDS[graph_?igGraphQ, dim : (2|3) : 2, Optional[distMatrix_?SquareMatrixQ, Automatic]] :=
    catch@Block[{ig = igMake[graph]},
      setVertexCoords[graph,
        check@ig@"layoutMDS"[
          Replace[distMatrix, Automatic -> {{}}],
          dim
        ]
      ]
    ]
*)

Options[IGLayoutReingoldTilford] = {
  "RootVertices" -> Automatic, DirectedEdges -> False, "Rotation" -> 0,
  "LayerHeight" -> 1, "LeafDistance" -> 1
};

SyntaxInformation[IGLayoutReingoldTilford] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutReingoldTilford, Graph]};

IGLayoutReingoldTilford[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutReingoldTilford,Graph}]] :=
    catch@Block[{ig = igMakeFast[graph], roots},
      roots = vss[graph]@Replace[OptionValue["RootVertices"], Automatic -> {}];
      applyGraphOpt[opt]@setVertexCoords[graph,
        Composition[
          RotationTransform[Pi + OptionValue["Rotation"]],
          ScalingTransform[{OptionValue["LeafDistance"], OptionValue["LayerHeight"]}]
        ] /@ check@ig@"layoutReingoldTilford"[roots, OptionValue[DirectedEdges]]
      ]
    ]


Options[IGLayoutReingoldTilfordCircular] = { "RootVertices" -> Automatic, DirectedEdges -> False, "Rotation" -> 0 };

SyntaxInformation[IGLayoutReingoldTilfordCircular] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutReingoldTilfordCircular, Graph]};

IGLayoutReingoldTilfordCircular[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutReingoldTilfordCircular,Graph}]] :=
    catch@Block[{ig = igMakeFast[graph], roots},
      roots = vss[graph]@Replace[OptionValue["RootVertices"], Automatic -> {}];
      applyGraphOpt[opt]@setVertexCoords[graph,
        RotationTransform@OptionValue["Rotation"] /@ check@ig@"layoutReingoldTilfordCircular"[roots, OptionValue[DirectedEdges]]
      ]
    ]


Options[IGLayoutDrL] = { "Settings" -> "Default", "Continue" -> False, "Align" -> True };
Options[IGLayoutDrL3D] = { "Settings" -> "Default", "Continue" -> False, "Align" -> True };

SyntaxInformation[IGLayoutDrL] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutDrL, Graph]};
SyntaxInformation[IGLayoutDrL3D] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutDrL3D, Graph3D]};

igLayoutDrLSettings = {"Default", "Coarsen", "Coarsest", "Refine", "Final"};
igLayoutDrLSettingsAsc = AssociationThread[igLayoutDrLSettings, Range@Length[igLayoutDrLSettings]];


amendUsage[#, "Possible values for the \"Settings\" option are <*igLayoutDrLSettings*>."]& /@ {IGLayoutDrL, IGLayoutDrL3D};

IGLayoutDrL::conn = "IGLayoutDrL may fail on disconnected graphs. Use on connected graphs only.";

IGLayoutDrL[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutDrL,Graph}]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], scale = 0.25},
      If[Not@WeaklyConnectedGraphQ[graph], Message[IGLayoutDrL::conn]];
      applyGraphOpt[opt]@setVertexCoords[graph,
        scale align[OptionValue["Align"]]@check@ig@"layoutDrL"[continueLayout[graph, OptionValue["Continue"], scale],
          Lookup[igLayoutDrLSettingsAsc, OptionValue["Settings"], -1]
        ]
      ]
    ]

IGLayoutDrL3D::conn = IGLayoutDrL::conn;

IGLayoutDrL3D[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutDrL3D,Graph3D}]] :=
    catch@Block[{ig = igMakeFastWeighted[graph], scale = 0.25},
      If[Not@WeaklyConnectedGraphQ[graph], Message[IGLayoutDrL3D::conn]];
      applyGraphOpt[opt]@setVertexCoords3D[graph,
        scale align[OptionValue["Align"]]@check@ig@"layoutDrL3D"[continueLayout3D[graph, OptionValue["Continue"], scale],
          Lookup[igLayoutDrLSettingsAsc, OptionValue["Settings"], -1]
        ]
      ]
    ]


blockLayout[n_, gap_, height_, mult_, offset_] :=
    Module[{nrow = Ceiling[1.01 height/gap]},
      Table[gap QuotientRemainder[i, nrow] mult + offset, {i, 0, n - 1}]
    ]

Options[IGLayoutBipartite] = {
  "VertexGap" -> 0.1,
  "PartitionGap" -> 1,
  "Orientation" -> Vertical,
  MaxIterations -> 100,
  "BipartitePartitions" -> Automatic
};
SyntaxInformation[IGLayoutBipartite] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[IGLayoutBipartite, Graph]};
IGLayoutBipartite::invopt = "The option value `` is not valid.";
IGLayoutBipartite::notbp = "Graph is not bipartite.";
IGLayoutBipartite::bdprt = "`` is not a valid partitioning vertices.";
IGLayoutBipartite[graph_ /; VertexCount[graph] == 0, opt : OptionsPattern[{IGLayoutBipartite, Graph}]] :=
    applyGraphOpt[opt][graph]
IGLayoutBipartite[graph_?igGraphQ, opt : OptionsPattern[{IGLayoutBipartite, Graph}]] :=
    catch@Module[{sg, ig, parts, isolated, connected, vertical, types, coord, coordAsc, min, max},
      parts = OptionValue["BipartitePartitions"];
      If[parts === Automatic,
        isolated = Pick[VertexList[graph], VertexDegree[graph], 0];
        connected = Complement[VertexList[graph], isolated];
        sg = Subgraph[graph, connected];
        ig = igMakeFast[sg];
        If[Not@ig@"bipartiteQ"[],
          Message[IGLayoutBipartite::notbp];
          Return[$Failed]
        ];
        types = ig@"bipartitePartitions"[]
        ,
        If[Not[ MatchQ[parts, {_?ListQ, _?ListQ}] && SubsetQ[VertexList[graph], Join@@parts] && Intersection@@parts === {} ],
          Message[IGLayoutBipartite::bdprt, "BipartitePartitions" -> parts];
          Return[$Failed]
        ];
        connected = Join @@ parts;
        isolated = Complement[VertexList[graph], connected];
        sg = Subgraph[graph, connected];
        ig = igMakeFast[sg];
        types = communitiesToMembership[VertexList[sg], parts];
      ];

      Switch[OptionValue["Orientation"],
        Horizontal|"Horizontal", vertical = False,
        Vertical|"Vertical", vertical = True,
        _, Message[IGLayoutBipartite::invopt, "Orientation" -> OptionValue["Orientation"]]; Return[$Failed]
      ];

      If[Not@positiveNumericQ@OptionValue["VertexGap"],
        Message[IGLayoutBipartite::invopt, "VertexGap" -> OptionValue["VertexGap"]]; Return[$Failed]
      ];

      If[Not@positiveNumericQ@OptionValue["PartitionGap"],
        Message[IGLayoutBipartite::invopt, "PartitionGap" -> OptionValue["PartitionGap"]]; Return[$Failed]
      ];

      (* use below instead of Min/Max to avoid obtaining Infinity for empty lists *)
      min[{}] = 0; min[x_] := Min[x];
      max[{}] = 0; max[x_] := Max[x];

      coord = check@ig@"layoutBipartite"[types, OptionValue["VertexGap"], OptionValue["PartitionGap"], OptionValue[MaxIterations]];
      coordAsc = Join[
        AssociationThread[connected -> coord],
        AssociationThread[
          isolated -> blockLayout[
            Length[isolated],
            OptionValue["VertexGap"], OptionValue["PartitionGap"],
            If[vertical, {-1, -1}, {1, -1}],
            If[vertical,
              {min[coord[[All, 1]]] - 1.5 OptionValue["VertexGap"], OptionValue["PartitionGap"]}
              ,
              {max[coord[[All, 1]]] + 1.5 OptionValue["VertexGap"], OptionValue["PartitionGap"]}
            ]
          ]
        ]
      ];

      applyGraphOpt[opt]@setVertexCoords[graph,
        If[vertical, RotationTransform[Pi/2], Identity]@Lookup[coordAsc, VertexList[graph]]
      ]
    ]

(* Clustering coefficient *)

SyntaxInformation[IGGlobalClusteringCoefficient] = {"ArgumentsPattern" -> {_}};
IGGlobalClusteringCoefficient[graph_?igGraphQ] :=
    Block[{ig = igMakeFast[graph]}, ig@"transitivityUndirected"[]]

SyntaxInformation[IGLocalClusteringCoefficient] = {"ArgumentsPattern" -> {_}};
IGLocalClusteringCoefficient[graph_?igGraphQ] :=
    Block[{ig = igMakeFast[SimpleGraph@UndirectedGraph[graph]]}, ig@"transitivityLocalUndirected"[]]

SyntaxInformation[IGAverageLocalClusteringCoefficient] = {"ArgumentsPattern" -> {_}};
IGAverageLocalClusteringCoefficient[graph_?igGraphQ] :=
    Block[{ig = igMakeFast[graph]}, ig@"transitivityAverageLocalUndirected"[]]

SyntaxInformation[IGWeightedClusteringCoefficient] = {"ArgumentsPattern" -> {_}};
IGWeightedClusteringCoefficient[graph_?igGraphQ] :=
    Block[{ig = igMakeFastWeighted[graph]}, ig@"transitivityBarrat"[]]


(* Similarity *)

(* for those that return a list of vectors *)
similarityFunction1[name_, post_ : Identity][graph_, All] :=
    catch@Block[{ig = igMakeFast[graph]}, post@check@ig@name[{}] ]
similarityFunction1[name_, post_ : Identity][graph_, {}] := {}
similarityFunction1[name_, post_ : Identity][graph_, vs_?ListQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      post@check@ig@name[ Check[VertexIndex[graph, #] - 1& /@ vs, Return[$Failed, Block]] ]
    ]
similarityFunction1[name_, post_ : Identity][graph_, v_] := similarityFunction1[name, First @* post][graph, {v}]

SyntaxInformation[IGCocitationCoupling] = {"ArgumentsPattern" -> {_, _.}};
IGCocitationCoupling[graph_?igGraphQ, vs_ : All] := similarityFunction1["similarityCocitation", Round][graph, vs]

SyntaxInformation[IGBibliographicCoupling] = {"ArgumentsPattern" -> {_, _.}};
IGBibliographicCoupling[graph_?igGraphQ, vs_ : All] := similarityFunction1["similarityBibcoupling", Round][graph, vs]

SyntaxInformation[IGInverseLogWeightedSimilarity] = {"ArgumentsPattern" -> {_, _.}};
IGInverseLogWeightedSimilarity[graph_?igGraphQ, vs_ : All] := similarityFunction1["similarityInverseLogWeighted"][graph, vs]

(* for those that return a matrix *)
similarityFunction2[name_][graph_, All, loops_] :=
    catch@Block[{ig = igMakeFast[graph]}, check@ig@name[{}, loops] ]
similarityFunction2[name_][graph_, {}, loops_] := {}
similarityFunction2[name_][graph_, vs_?ListQ, loops_] :=
    catch@Block[{ig = igMakeFast[graph]},
      check@ig@name[ Check[VertexIndex[graph, #] - 1& /@ vs, Return[$Failed, Block]], loops ]
    ]

Options[IGJaccardSimilarity] = { SelfLoops -> False };
SyntaxInformation[IGJaccardSimilarity] = {"ArgumentsPattern" -> {_, _., OptionsPattern[]}};
IGJaccardSimilarity[graph_?igGraphQ, vs : (_?ListQ | All) : All, opt : OptionsPattern[]] := similarityFunction2["similarityJaccard"][graph, vs, OptionValue[SelfLoops]]

Options[IGDiceSimilarity] = { SelfLoops -> False };
SyntaxInformation[IGDiceSimilarity] = {"ArgumentsPattern" -> {_, _., OptionsPattern[]}};
IGDiceSimilarity[graph_?igGraphQ, vs : (_?ListQ | All) : All, opt : OptionsPattern[]] := similarityFunction2["similarityDice"][graph, vs, OptionValue[SelfLoops]]


(* Chordal graphs *)

SyntaxInformation[IGChordalQ] = {"ArgumentsPattern" -> {_}};
IGChordalQ[graph_?igGraphQ] :=
    Block[{ig = igMakeFast[graph]}, ig@"chordalQ"[]]

SyntaxInformation[IGMaximumCardinalitySearch] = {"ArgumentsPattern" -> {_}};
IGMaximumCardinalitySearch[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      igVertexNames[graph]@igIndexVec@check@ig@"maximumCardinalitySearch"[]
    ]

SyntaxInformation[IGChordalCompletion] = {"ArgumentsPattern" -> {_}};
IGChordalCompletion[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph], result},
      result = check@ig@"chordalCompletion"[];
      If[DirectedGraphQ[graph], DirectedEdge, UndirectedEdge] @@@ Partition[igVertexNames[graph]@igIndexVec[result], 2]
    ]

(* Vertex cuts *)

IGMinSeparators::deprec = "IGMinSeparators is deprecated and will be removed from future versions of IGraph/M. Use IGMinimumSeparators instead.";
IGMinSeparators[graph_] := (Message[IGMinSeparators::deprec]; IGMinimumSeparators[graph])

SyntaxInformation[IGMinimumSeparators] = {"ArgumentsPattern" -> {_}};
IGMinimumSeparators[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast@UndirectedGraph[graph]},
      igUnpackVertexSet[graph]@check@ig@"minimumSizeSeparators"[]
    ]

SyntaxInformation[IGMinimalSeparators] = {"ArgumentsPattern" -> {_}};
IGMinimalSeparators[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      igUnpackVertexSet[graph]@check@ig@"minimalSeparators"[]
    ]

SyntaxInformation[IGVertexSeparatorQ] = {"ArgumentsPattern" -> {_, _}};
IGVertexSeparatorQ[graph_?igGraphQ, vs_?ListQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      check@ig@"separatorQ"[vss[graph][vs]]
    ]

SyntaxInformation[IGMinimalVertexSeparatorQ] = {"ArgumentsPattern" -> {_, _}};
IGMinimalVertexSeparatorQ[graph_?igGraphQ, vs_?ListQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      check@ig@"minSeparatorQ"[vss[graph][vs]]
    ]

(* Connected components *)

SyntaxInformation[IGArticulationPoints] = {"ArgumentsPattern" -> {_}};
IGArticulationPoints[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      igVertexNames[graph]@igIndexVec@check@ig@"articulationPoints"[]
    ]

SyntaxInformation[IGBiconnectedComponents] = {"ArgumentsPattern" -> {_}};
IGBiconnectedComponents[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      igUnpackVertexSet[graph]@check@ig@"biconnectedComponents"[]
    ]

(* Connectivity *)

SyntaxInformation[IGVertexConnectivity] = {"ArgumentsPattern" -> {_, _., _.}};

IGVertexConnectivity[graph_?igGraphQ] :=
    Block[{ig = igMakeFast[graph]},
      sck@ig@"vertexConnectivity"[]
    ]

IGVertexConnectivity[graph_?igGraphQ, s_, t_] :=
    catch@Block[{ig = igMakeFast[graph]},
      check@ig@"vertexConnectivityST"[vs[graph][s], vs[graph][t]]
    ]


SyntaxInformation[IGEdgeConnectivity] = {"ArgumentsPattern" -> {_, _., _.}};

IGEdgeConnectivity[graph_?igGraphQ] :=
    Block[{ig = igMakeFast[graph]},
      sck@ig@"edgeConnectivity"[]
    ]

IGEdgeConnectivity[graph_?igGraphQ, s_, t_] :=
    catch@Block[{ig = igMakeFast[graph]},
      check@ig@"edgeConnectivityST"[vs[graph][s], vs[graph][t]]
    ]


IGCohesiveBlocks::badarg = "The input must be a simple undirected graph.";
SyntaxInformation[IGCohesiveBlocks] = {"ArgumentsPattern" -> {_}};
IGCohesiveBlocks[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph], blocks, cohesion, parents},
      If[Not@SimpleGraphQ[graph], Message[IGCohesiveBlocks::badarg]; Return[$Failed]];
      {blocks, cohesion, parents} = check@ig@"cohesiveBlocks"[];
      {igVertexNames[graph] /@ igIndexVec[blocks], Round[cohesion](*, igIndexVec[parents]*)}
    ]

(* Graphlets *)

IGGraphlets[graph_?igGraphQ, niter : _?Internal`PositiveMachineIntegerQ : 1000] :=
    catch@Block[{ig = igMake[graph], basis, mu},
      {basis, mu} = check@ig@"graphlets"[niter];
      {igVertexNames[graph] /@ igIndexVec[basis], mu}
    ]

IGGraphletBasis[graph_?igGraphQ] :=
    catch@Block[{ig = igMake[graph], basis, thresholds},
      {basis, thresholds} = check@ig@"graphletBasis"[];
      {igVertexNames[graph] /@ igIndexVec[basis], thresholds}
    ]

IGGraphletProject[graph_?igGraphQ, cliques : {__List}, niter : _?Internal`PositiveMachineIntegerQ : 1000] :=
    catch@Block[{ig = igMake[graph], clq},
      check@ig@"graphletProject"[Map[vss[graph], cliques, {2}], niter]
    ]


(* Community detection *)

clusterAscQ[asc_?AssociationQ] := AllTrue[{"Elements", "Communities", "Algorithm"}, KeyExistsQ[asc, #]&]
clusterAscQ[_] := False

igClusterDataQ[IGClusterData[asc_]] := clusterAscQ[asc]
igClusterDataQ[_] := Fals + 3;

hierarchicalQ[asc_] := KeyExistsQ[asc, "Merges"]

IGClusterData::hier   = "The provided clustering is not hierarchical.";
IGClusterData::noprop = "There is no property `` for IGClusterData objects.";

IGClusterData[asc_?AssociationQ]["Properties"] :=
    Sort@Join[
      Keys[asc],
      {
        "Properties", "ElementCount",
        If[hierarchicalQ[asc],
          Unevaluated@Sequence["HierarchicalClusters", "Tree"],
          Unevaluated@Sequence[]
        ]
      }
    ]

mergesToHierarchy[asc_] :=
    Module[{cl, merge, cnt, n, s},
      n = Length@asc@"Elements";
      Switch[asc@"Algorithm",
        "EdgeBetweenness", s = 1 + Length@asc@"RemovedEdges" - asc@"Bridges",
        _, s = Range[n]
      ];
      MapThread[Set[cl[#2], #1]&, {asc@"Elements", Range[n]}];
      cnt[Cluster[_, _, _, n1_, n2_]] := n1 + n2;
      cnt[_] := 1;
      merge[{j_, k_}, {i_}] := cl[i + n] = Cluster[cl[j], cl[k], s[[i]], cnt@cl[j], cnt@cl[k]];
      Last@MapIndexed[merge, asc["Merges"]]
    ]

IGClusterData[asc_?AssociationQ]["HierarchicalClusters"] :=
    If[hierarchicalQ[asc],
      mergesToHierarchy[asc],
      Message[IGClusterData::hier]; $Failed
    ]

mergesToTree[asc_] :=
    Module[{mc, root, ec, merges = asc["Merges"], leafIndices, g, s},
      mc = Length[merges];
      ec = Length[asc["Elements"]];
      root = ec + mc;
      leafIndices = Intersection[Range[ec], Flatten[merges]];
      Switch[asc@"Algorithm",
        "EdgeBetweenness", s = 1 + Length@asc@"RemovedEdges" - asc@"Bridges",
        _, s = Range[mc]
      ];
      TreeGraph[
        Append[Flatten[merges], root],
        Append[ec + Range[mc] /. n_Integer :> Sequence[n, n], root],
        GraphLayout -> {"LayeredEmbedding", "RootVertex" -> root},
        DirectedEdges -> True,
        EdgeShapeFunction -> "Line",
        EdgeStyle -> Gray,
        VertexShapeFunction -> ({}&), (* Using VertexShapeFunction -> None does not work in M10.0-10.3 due to a bug. *)
        VertexLabels -> Thread[ leafIndices -> (Placed[Short[#], Below]&) /@ asc["Elements"][[ leafIndices ]] ],
        VertexWeight -> Join[Thread[ec + Range[mc] -> s], Thread[leafIndices -> 0]],
        Properties -> {"GraphProperties" -> {"LeafLabels" -> AssociationThread[leafIndices, asc["Elements"][[ leafIndices ]] ]}}
      ]
    ]

IGClusterData[asc_?AssociationQ]["Tree"] :=
    If[hierarchicalQ[asc],
      mergesToTree[asc],
      Message[IGClusterData::hier]; $Failed
    ]

IGClusterData[asc_?AssociationQ]["ElementCount"] := Length[asc["Elements"]]

IGClusterData[asc_?AssociationQ][key_String] := Lookup[asc, key, Message[IGClusterData::noprop, key]; $Failed]


grid[g_] := Column[Row /@ MapAt[Style[#, Gray]&, g, Table[{i, 1}, {i, Length[g]}]]]

IGClusterData /: MakeBoxes[c : IGClusterData[asc_?clusterAscQ], form : (StandardForm|TraditionalForm)] :=
    With[{boxes = RowBox[{"IGClusterData", "[",
      ToBoxes[
        Panel[OpenerView[{
          grid[
            {
              {"Elements: ", Length@asc@"Elements"},
              {"Communities: ", Length@asc@"Communities"}
            }
          ],
          grid[{
            {"Modularity: ", If[KeyExistsQ[asc, "Modularity"], Max@asc@"Modularity", "unknown"]},
            {"Hierarchical: ", hierarchicalQ[asc]},
            {"Algorithm: ", asc@"Algorithm"}
          }
          ]
        }], BaselinePosition -> Baseline],
        form],
      "]"}]},
      InterpretationBox[
        boxes,
        c
      ]
    ]

Format[c : IGClusterData[asc_?clusterAscQ], OutputForm] := StringTemplate["IGClusterData[\"Elements\" -> <``>, \"Communities\" -> <``>]"][c["ElementCount"], Length@c["Communities"]]


igClusterData[graph_][asc_] := IGClusterData@Join[<|"Elements" -> VertexList[graph]|>, asc]

communitiesFromMembership[graph_, membership_] := Values@GroupBy[Transpose[{VertexList[graph], Round[membership]}], Last -> First]

communitiesToMembership[elems_, communities_] :=
    Module[{copy = communities},
      copy[[All, All]] = Range@Length[communities] - 1;
      Lookup[AssociationThread[Flatten[communities, 1], Flatten[copy, 1]], elems]
    ]

IGraphM::invcomm = "Invalid community specification for the given vertices.";

communitiesToMembershipChecked[elems_, communities_] :=
    If[Sort[elems] === Union@@communities && Length[elems] === Length@Flatten[communities, 1],
      communitiesToMembership[elems, communities],
      Message[IGraphM::invcomm]; throw[$Failed]
    ]


SyntaxInformation[Test] = {"ArgumentsPattern" -> {_, _}};
Test[graph_?igGraphQ, clusters_?igClusterDataQ] := Test[graph, clusters["Communities"]]
Test[graph_?igGraphQ, communities : {__List}] :=
    catch@Block[{ig = igMakeFastWeighted[graph]},
      check@ig@"modularity"[communitiesToMembershipChecked[VertexList[graph], communities]]
    ]

igCompareCommunitiesMethods = {
  "VariationOfInformation",
  "NormalizedMutualInformation",
  "SplitJoinDistance",
  "UnadjustedRandIndex",
  "AdjustedRandIndex"
};

igCompareCommunitiesMethodsAsc = AssociationThread[igCompareCommunitiesMethods, Range@Length[igCompareCommunitiesMethods] - 1]

igCompareCommunities[elems_, c1_, c2_, method_] :=
    Block[{ig = Make["IG"]},
      res = check@ig@"compareCommunities"[communitiesToMembershipChecked[elems, c1], communitiesToMembershipChecked[elems, c2],
        Lookup[igCompareCommunitiesMethodsAsc, method, -1]
      ];
      If[method === "SplitJoinDistance", res = Round[res]];
      <| method -> res |>
    ]

amendUsage[IGCompareCommunities, "Available methods: <*igCompareCommunitiesMethods*>."];

SyntaxInformation[IGCompareCommunities] = {"ArgumentsPattern" -> {_, _, _., _.}};

IGCompareCommunities[graph_?igGraphQ, comm1 : {__List}, comm2 : {__List}, methods : {__String} : igCompareCommunitiesMethods] :=
    catch[
      Join @@ (igCompareCommunities[VertexList[graph], comm1, comm2, #]& /@ methods)
    ]

IGCompareCommunities[graph_?igGraphQ, comm1 : {__List}, comm2 : {__List}, method_String] :=
    catch@igCompareCommunities[VertexList[graph], comm1, comm2, method]

IGCompareCommunities::diff = "The compared cluster objects must contain exactly the same elements"

IGCompareCommunities[c1_?igClusterDataQ, c2_?igClusterDataQ, methods : {__String} : igCompareCommunitiesMethods] :=
    catch[
      If[c1@"Elements" =!= c2@"Elements", Message[IGCompareCommunities::diff]; throw[$Failed]];
      Join @@ (igCompareCommunities[c1@"Elements", c1@"Communities", c2@"Communities", #]& /@ methods)
    ]

IGCompareCommunities[c1_?igClusterDataQ, c2_?igClusterDataQ, method_String] := IGCompareCommunities[c1, c2, {method}]


(* Note: edge ordering matters, use igMake instead of igMakeFastWeighted. *)
SyntaxInformation[IGCommunitiesEdgeBetweenness] = {"ArgumentsPattern" -> {_}};
IGCommunitiesEdgeBetweenness[graph_?igGraphQ] :=
    catch@Module[{ig = igMake[graph], result, merges, betweenness, bridges, modularity, membership, removed},
      {result, merges, betweenness, bridges, modularity, membership} = check@ig@"communityEdgeBetweenness"[];
      removed = Part[EdgeList[graph], igIndexVec[result]];
      igClusterData[graph]@<|
        "Communities" -> communitiesFromMembership[graph, membership],
        "Modularity" -> modularity,
        "Merges" -> igIndexVec[merges],
        "RemovedEdges" -> removed,
        "EdgeBetweenness" -> betweenness,
        "Bridges" -> Round[bridges],
        "Algorithm" -> "EdgeBetweenness"
      |>
    ]


SyntaxInformation[IGCommunitiesWalktrap] = {"ArgumentsPattern" -> {_, _.}};
IGCommunitiesWalktrap[graph_?igGraphQ, steps : _?Internal`PositiveMachineIntegerQ : 4] :=
    catch@Module[{ig = igMakeFastWeighted[graph], merges, modularity, membership},
      {merges, modularity, membership} = check@ig@"communityWalktrap"[steps];
      igClusterData[graph]@<|
        "Communities" -> communitiesFromMembership[graph, membership],
        "Modularity" -> modularity,
        "Merges" -> igIndexVec[merges],
        "Algorithm" -> "Walktrap"
      |>
    ]


SyntaxInformation[IGCommunitiesGreedy] = {"ArgumentsPattern" -> {_}};
IGCommunitiesGreedy[graph_?igGraphQ] :=
    catch@Module[{ig = igMakeFastWeighted[graph], merges, modularity, membership},
      {merges, modularity, membership} = check@ig@"communityFastGreedy"[];
      igClusterData[graph]@<|
        "Communities" -> communitiesFromMembership[graph, membership],
        "Modularity" -> modularity,
        "Merges" -> igIndexVec[merges],
        "Algorithm" -> "Greedy"
      |>
    ]


SyntaxInformation[IGCommunitiesOptimalModularity] = {"ArgumentsPattern" -> {_}};
IGCommunitiesOptimalModularity[graph_?igGraphQ] :=
    catch@Module[{ig = igMakeFastWeighted[graph], modularity, membership},
      {modularity, membership} = check@ig@"communityOptimalModularity"[];
      igClusterData[graph]@<|
        "Communities" -> communitiesFromMembership[graph, membership],
        "Modularity" -> {modularity},
        "Algorithm" -> "OptimalModularity"
      |>
    ]


SyntaxInformation[IGCommunitiesMultilevel] = {"ArgumentsPattern" -> {_}};
IGCommunitiesMultilevel[graph_?igGraphQ] :=
    catch@Module[{ig = igMakeFastWeighted[graph], modularity, membership, memberships},
      {modularity, membership, memberships} = check@ig@"communityMultilevel"[];
      igClusterData[graph]@<|
        "Communities" -> communitiesFromMembership[graph, membership],
        "Modularity" -> modularity,
        "MultilevelCommunities" -> (communitiesFromMembership[graph, #]&) /@ memberships,
        "Algorithm" -> "Multilevel"
      |>
    ]


Options[IGCommunitiesLabelPropagation] = { "Initial" -> None, "Fixed" -> None };
SyntaxInformation[IGCommunitiesLabelPropagation] = {"ArgumentsPattern" -> {_, OptionsPattern[]}};

IGCommunitiesLabelPropagation::invopt = "The option value `` is not valid.";
IGBarabasiAlbertGame + IGBipartiteQ
IGCommunitiesLabelPropagation[graph_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Module[{ig = igMakeFastWeighted[graph], membership, modularity, initial, fixed, vl},
      initial = OptionValue["Initial"];
      fixed = OptionValue["Fixed"];
      vl = VertexList[graph];
      If[initial =!= None,
        If[ Not@MatchQ[initial, {__List}] || Not@SubsetQ[vl, Flatten[initial, 1]],
          Message[IGCommunitiesLabelPropagation::invopt, "\"Initial\""];
          throw[$Failed];
        ];
        initial = Prepend[initial, Complement[vl, Flatten[initial, 1]]];
        initial = communitiesToMembership[vl, initial] - 1;
        ,
        initial = {};
      ];
      If[fixed =!= None,
        If[Not@SubsetQ[vl, fixed],
          Message[IGCommunitiesLabelPropagation::invopt, "\"Fixed\""];
          throw[$Failed];
        ];
        fixed = {Complement[VertexList[graph], fixed], fixed};
        fixed = communitiesToMembership[vl, fixed];
        ,
        fixed = {};
      ];
      {membership, modularity} = check@ig@"communityLabelPropagation"[initial, fixed];
      igClusterData[graph]@<|
        "Communities" -> communitiesFromMembership[graph, membership],
        "Modularity" -> {modularity},
        "Algorithm" -> "LabelPropagation"
      |>
    ]


SyntaxInformation[IGCommunitiesInfoMAP] = {"ArgumentsPattern" -> {_, _.}};
IGCommunitiesInfoMAP[graph_?igGraphQ, trials_ : 10] :=
    catch@Module[{ig = igMakeFastWeighted[graph], membership, codelen, vertexWeights},
      vertexWeights = If[vertexWeightedQ[graph], PropertyValue[graph, VertexWeight], {}];
      {membership, codelen} = check@ig@"communityInfoMAP"[trials, vertexWeights];
      igClusterData[graph]@<|
        "Communities" -> communitiesFromMembership[graph, membership],
        "CodeLength" -> codelen,
        "Algorithm" -> "InfoMAP"
      |>
    ]


Options[IGCommunitiesSpinGlass] = {
  "UpdateRule" -> "Configuration",
  "ParallelUpdating" -> False,
  "SpinCount" -> 25,
  "StartingTemperature" -> 1.,
  "StoppingTemperature" -> 0.01,
  "CoolingFactor" -> 0.99,
  "Gamma" -> 1,
  "GammaMinus" -> 1,
  Method -> Automatic
};

SyntaxInformation[IGCommunitiesSpinGlass] = {"ArgumentsPattern" -> {_, OptionsPattern[]}};

igSpinGlassUpdateRules = {"Simple", "Configuration"};
igSpinGlassUpdateRulesAsc = AssociationThread[igSpinGlassUpdateRules, Range@Length[igSpinGlassUpdateRules] - 1];
igSpinGlassMethods = {"Original", "Negative"};
igSpinGlassMethodsAsc = AssociationThread[igSpinGlassMethods, Range@Length[igSpinGlassMethods] - 1];

amendUsage[IGCommunitiesSpinGlass,
  "Available \"UpdateRule\" option values: <*igSpinGlassUpdateRules*>. Available Method options: <*igSpinGlassMethods*>."
];

IGCommunitiesSpinGlass[graph_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Module[{ig = igMakeFastWeighted[graph], modularity, membership, temp, method},
      method = OptionValue[Method];
      If[method === Automatic,
        method = If[
          igWeightedGraphQ[graph] && TrueQ@NonPositive@Min@PropertyValue[graph, EdgeWeight],
          "Negative",
          "Original"
        ]
      ];
      {membership, modularity, temp} = check@ig@"communitySpinGlass"[
        OptionValue["SpinCount"], Boole@OptionValue["ParallelUpdating"],
        OptionValue["StartingTemperature"], OptionValue["StoppingTemperature"], OptionValue["CoolingFactor"],
        Lookup[igSpinGlassUpdateRulesAsc, OptionValue["UpdateRule"], -1],
        OptionValue["Gamma"],
        Lookup[igSpinGlassMethodsAsc, method, -1],
        OptionValue["GammaMinus"]
      ];
      igClusterData[graph]@<|
        "Communities" -> communitiesFromMembership[graph, membership],
        "Modularity" -> {modularity},
        "Temperature" -> temp,
        "Algorithm" -> "SpinGlass"
      |>
    ]


Options[IGCommunitiesLeadingEigenvector] = { "Steps" -> Automatic };
SyntaxInformation[IGCommunitiesLeadingEigenvector] = {"ArgumentsPattern" -> {_, OptionsPattern[]}};

IGCommunitiesLeadingEigenvector[graph_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Module[{ig = igMakeFastWeighted[graph], modularity, membership, merges, eval, evec},
      {membership, merges, modularity, eval, evec} = check@ig@"communityLeadingEigenvector"[
        Replace[OptionValue["Steps"], Automatic :> VertexCount[graph]]
      ];
      igClusterData[graph]@<|
        "Communities" -> communitiesFromMembership[graph, membership],
        "Modularity" -> {modularity},
        "Merges" -> igIndexVec[merges], (* TODO *)
        "Eigenvalues" -> eval,
        "Eigenvectors" -> evec,
        "Algorithm" -> "LeadingEigenvector"
      |>
    ]

(* Maximum flow *)

(* TODO: Speed up VertexReplace *)
(* Note: edge ordering is critical *)
SyntaxInformation[IGGomoryHuTree] = {"ArgumentsPattern" -> {_}};
IGGomoryHuTree[graph_?GraphQ] :=
    catch@Block[{new = Make["IG"], ig = igMake[graph], flow, capacity},
      capacity = PropertyValue[graph, EdgeCapacity];
      If[Not@VectorQ[capacity], capacity = {}];
      flow = check@new@"gomoryHuTree"[ManagedLibraryExpressionID[ig], capacity];
      <|
        "Tree" -> VertexReplace[igToGraph[new], Thread[Range@VertexCount[graph] -> VertexList[graph]]],
        "Flow" -> flow
      |>
    ]

(* Unfold tree *)

Options[IGUnfoldTree] = { DirectedEdges -> True };
SyntaxInformation[IGUnfoldTree] = {"ArgumentsPattern" -> {_, OptionsPattern[]}};
IGUnfoldTree[graph_?GraphQ, roots_?ListQ, opt : OptionsPattern[]] :=
    catch@Block[{new = Make["IG"], ig = igMakeFast[graph], mapping, t},
      mapping = check@new@"unfoldTree"[ManagedLibraryExpressionID[ig], vss[graph][roots], OptionValue[DirectedEdges]];
      t = igToGraph[new];
      <|
        "Tree" -> t,
        "Mapping" -> AssociationThread[VertexList[t], igVertexNames[graph]@igIndexVec[mapping]]
      |>
    ]

(* Bipartite partitions *)

SyntaxInformation[IGBipartitePartitions] = {"ArgumentsPattern" -> {_}};
IGBipartitePartitions::nbipart = "The graph is not bipartite.";
IGBipartitePartitions[graph_?igGraphQ] :=
    catch@Block[{ig = igMakeFast[graph], parts},
      parts = ig@"bipartitePartitions"[];
      If[MatchQ[parts, _LibraryFunctionError],
        Message[IGBipartitePartitions::nbipart];
        Return[$Failed]
      ];
      {Pick[VertexList[graph], parts, 0], Pick[VertexList[graph], parts, 1]}
    ]


(* Vertex contraction *)

IGVertexContract::inv = "The vertices `` are not present in the graph.";
IGVertexContract::vset = "`` must be a list of vertex sets.";

Options[IGVertexContract] = { SelfLoops -> False, "MultipleEdges" -> False };
SyntaxInformation[IGVertexContract] = {"ArgumentsPattern" -> {_, _, OptionsPattern[]}, "OptionNames" -> optNames[IGVertexContract, Graph]};
IGVertexContract[graph_?igGraphQ, sets : {___List}, opt : OptionsPattern[{IGVertexContract, Graph}]] :=
    catch@Module[{ig = igMakeFast[graph + 1], allElements = Join @@ sets, g, self, multi},
      If[Not@SubsetQ[VertexList[graph], allElements],
        Message[IGVertexContract::inv, Complement[allElements, VertexList[graph]]];
        Return[$Failed]
      ];
      check@ig@"contractVertices"@communitiesToMembership[
        VertexList[graph],
        Join[sets, List /@ Complement[VertexList[graph], allElements]]
      ];
      self = Not@TrueQ@OptionValue[SelfLoops];
      multi = Not@TrueQ@OptionValue["MultipleEdges"];
      g = igToGraph[ig];
      applyGraphOpt[opt]@Which[
        self && multi, SimpleGraph[g],
        self, removeSelfLoops[g],
        multi, removeMultiEdges[g],
        True, g
      ]
    ]

IGVertexContract[graph_?igGraphQ, arg_, opt : OptionsPattern[]] := Null /; Message[IGVertexContract::vset, arg]


(* Random walk *)

IGRandomWalk[graph_?igGraphQ, start_, steps_?Internal`NonNegativeMachineIntegerQ] :=
    catch@Block[{ig = igMakeFast[graph]},
      Part[
        VertexList[graph],
        1 + Round@check@ig@"randomWalk"[vs[graph][start], steps]
      ]
    ]

(* Transitivity *)

IGVertexTransitiveQ::nmg = "Multigraphs are not supported.";
SyntaxInformation[IGVertexTransitiveQ] = {"ArgumentsPattern" -> {_}};
IGVertexTransitiveQ[graph_?EmptyGraphQ] = True;
IGVertexTransitiveQ[graph_?igGraphQ] :=
    If[MultigraphQ[graph],
      Message[IGVertexTransitiveQ::nmg];
      $Failed
      ,
      With[{elems = Range@VertexCount[graph]},
        GroupOrbits[PermutationGroup@IGBlissAutomorphismGroup[graph], elems] === {elems}
      ]
    ]
IGVertexTransitiveQ[_] = False;

IGEdgeTransitiveQ::nmg = IGVertexTransitiveQ::nmg;
SyntaxInformation[IGEdgeTransitiveQ] = {"ArgumentsPattern" -> {_}};
IGEdgeTransitiveQ[graph_?igGraphQ] :=
    If[MultigraphQ[graph],
      Message[IGEdgeTransitiveQ::nmg];
      $Failed
      ,
      IGVertexTransitiveQ@LineGraph[graph]
    ]
IGEdgeTransitiveQ[_] = False;

IGSymmetricQ::nmg = IGVertexTransitiveQ::nmg;
IGSymmetricQ[graph_?igGraphQ] :=
    If[MultigraphQ[graph],
      Message[IGSymmetricQ::nmg];
      $Failed
      ,
      IGVertexTransitiveQ[graph] && IGEdgeTransitiveQ[graph]
    ]
IGSymmetricQ[_] = False;


(* Spanning tree *)

SyntaxInformation[IGSpanningTree] = {"ArgumentsPattern" -> {_, OptionsPattern[]}, "OptionNames" -> optNames[Graph]};
IGSpanningTree[graph_?igGraphQ, opt : OptionsPattern[]] :=
    catch@Block[{ig = igMake[graph]},
      With[{indices = 1 + Round@check@ig@"spanningTree"[]},
        Graph[
          VertexList[graph],
          EdgeList[graph][[indices]],
          If[igWeightedGraphQ[graph], EdgeWeight -> PropertyValue[graph, EdgeWeight][[indices]], Unevaluated@Sequence[]],
          opt
        ]
      ]
    ]

(***** Finalize *****)

(* Protect all package symbols *)
With[{syms = Join @@ Names /@ {"IGraphM`*", "IGraphM`Information`*", "IGraphM`Developer`*"}},
  SetAttributes[syms, {Protected, ReadProtected}]
];

End[]; (* `Private` *)

EndPackage[];