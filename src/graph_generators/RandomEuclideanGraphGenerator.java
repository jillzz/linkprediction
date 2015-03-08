package graph_generators;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.random.UncorrelatedRandomVectorGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import linkpred_batch.FeatureField;
import linkpred_batch.Network;
import linkpred_batch.RandomWalkGraph;

public class RandomEuclideanGraphGenerator extends GraphGenerator {
	/**Random number generator*/
	private JDKRandomGenerator rand;
	private static int id = 0;
	private Generator generator;
	
	public RandomEuclideanGraphGenerator() {
		super();
		this.rand = new JDKRandomGenerator();
		this.rand.setSeed(new Date().getTime());
		this.generator = new RandomEuclideanGenerator();
	}

	@Override
	public RandomWalkGraph generate(int n, int f, int s) {
		// Generate GraphStream graph
		Graph graph;
		graph = new SingleGraph(String.format("random_graph_%d", id++));
		generator.addSink(graph);
		generator.begin();
		for (int i = 0; i < n-1; i++)  // n-1 since it starts with a single node i believe
			generator.nextEvents();
		generator.end();
		//graph.display(); 
		generator.clearSinks();
		
		// Convert to Network
		ArrayList<FeatureField> edges = new ArrayList<FeatureField> (); 
				
		RandomVectorGenerator randomVector = 
				new UncorrelatedRandomVectorGenerator(
				f, new GaussianRandomGenerator(rand));
		
		Iterator<Edge> iterator = graph.getEdgeIterator();
		FeatureField ff = null;
		Edge e = null;
		while (iterator.hasNext()) {
			e = iterator.next();
			//System.out.println(e.getNode0().getIndex() + "-" + e.getNode1().getIndex());
			ff = new FeatureField(e.getNode0().getIndex(), e.getNode1().getIndex(), randomVector.nextVector());
			edges.add(ff);
		}
				
		RandomWalkGraph g = new Network(n, f, s, edges, 
				new ArrayList<Integer>(), new ArrayList<Integer>());
				
		return g;
	}

}
