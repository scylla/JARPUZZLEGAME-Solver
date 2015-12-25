import java.util.*;
import java.io.*;

public class FillTheJarPuzzle {

	int[] jars;
	HashMap<Node, ArrayList<Node>> graph;
	HashMap<Node, Boolean> nodeState;
	HashMap<Node, ArrayList<Boolean>> boolMap;
	int jarCount;
	MyScanner mySc;

	public static class MyScanner {

		BufferedReader br;
		StringTokenizer st;

		public MyScanner() {
			br = new BufferedReader(new InputStreamReader(System.in));
		}

		String next() {

			while(st == null || !st.hasMoreElements()) {
				try {
					st = new StringTokenizer(br.readLine());
				} catch(IOException e) {
					e.printStackTrace();
				}
			}

			return st.nextToken();
		}

		public int nextInt() {
			return Integer.parseInt(next());
		}

	}

	public FillTheJarPuzzle() {
		
		mySc = new MyScanner();

		this.jarCount = mySc.nextInt();
		jars = new int[jarCount];

		for(int i=0;i<jarCount;i++) {
			jars[i] = mySc.nextInt();
		}

		Node start = new Node();

		for(int i=0;i<jarCount;i++) {
			start.jars[i] = mySc.nextInt();
		}

		graph = new HashMap<Node, ArrayList<Node>>();
		nodeState = new HashMap<Node, Boolean>();
		graph.put(start, null);
		nodeState.put(start, false);

		genGraph();
		genBFSGraph(start);
		performDFS(start, mySc.nextInt());
	}


	void genBFSGraph(Node start) {

		HashMap<Node, ArrayList<Node>> bfsGraph = new HashMap<Node, ArrayList<Node>>();
		HashMap<Node, Boolean> bfsState = new HashMap<Node, Boolean>();
		boolMap = new HashMap<Node, ArrayList<Boolean>>();
		
		for(Node key: nodeState.keySet()){
			bfsState.put(key, false);
		}

		LinkedList<Node> queue = new LinkedList<Node>();
		queue.add(start);

		while(!queue.isEmpty()) {
			Node n = queue.remove();
			ArrayList<Node> nl = new ArrayList<Node>();
			ArrayList<Node> ol = graph.get(n);
			ArrayList<Boolean> bl = new ArrayList<Boolean>();
			bfsState.put(n, true);
			for(Node key: ol) {
				if(!bfsState.get(key)){
					nl.add(key);
					queue.add(key);
					bl.add(false);
				}
			} 
			bfsGraph.put(n, nl);
			boolMap.put(n, bl);
		}

		graph = bfsGraph;

	}


	void performDFS(Node start, int sol) {

		Stack<Node> stk = new Stack<Node>();
		stk.push(start);
		start.pass = true;
		boolean add = false;
		int count = 1;
		
		while(!stk.empty()) {

			Node top = stk.peek();

			if(solutionNode(top, sol)) {
				System.out.println("sol " + count++);
				System.out.println(stk.toString());
				Node pop = stk.pop();
				System.out.println();
			}

			ArrayList<Node> adj = graph.get(top);
			
			for(int i=0;i<adj.size();i++) {

				if(!boolMap.get(top).get(i)){
					stk.push(adj.get(i));
					boolMap.get(top).set(i, true);
					add = true;
					break;
				}
			}

			if(!add) {
				stk.pop();
			}
			
			add = false;

		}

	}

	boolean solutionNode(Node n, int sol) {
		for(int i=0;i<jarCount;i++) {
			if(n.jars[i] == sol)
				return true;
		}
		return false;
	}


	void printGraph(){
		for(Node key : graph.keySet()) {
			System.out.println(key + " | " + graph.get(key).size());
			for(int i=0;i<graph.get(key).size();i++){
				System.out.println(graph.get(key).get(i));
			}
			System.out.println();
		}
	}

	void genGraph() {
		
		Node proNode = getUnprocessedNode();

		while(proNode != null) {

			ArrayList<Node> an = new ArrayList<Node>();

			for(int i=0; i< jars.length; i++){

				for(int j=0;j<jars.length;j++) {
					if(i != j) {
						int amt = transferAmt(proNode.jars[i], jars[j] - proNode.jars[j]);
						if(amt != -1){
							Node x = new Node();
							x.copy(proNode);
							x.update(i, -amt);
							x.update(j, amt);
							an.add(getLastSimilar(x));
						}
					}
				}
			}

			graph.put(proNode, an);
			nodeState.put(proNode, true);
			proNode = getUnprocessedNode();
		}

	}

	// get next node to process
	Node getUnprocessedNode() {

		for(Node key : nodeState.keySet()) {
			if(!nodeState.get(key))
				return key;
		}

		return null;
	}

	// return last added similar node if present
	Node getLastSimilar(Node n) {

		for(Node key : graph.keySet()) {
			if(n.equals(key))
				return key;
		}

		// new node update graph
		graph.put(n, null);
		nodeState.put(n, false);
		return n;
	}

	int transferAmt(int src, int sink) {
		if(src == 0 || sink == 0)
			return -1;
		if(src >= sink) 
			return sink;
		else 
			return src;
	}
	

	private class Node {

		int[] jars;
		boolean[] states;
		boolean pass;

		Node() {
			jars = new int[jarCount];
			states = new boolean[jarCount];
			Arrays.fill(states, false);
			pass = false;
		}

		public String toString() {
			String st = "|";
			for(int i = 0;i< jarCount; i++) {
				st = st + jars[i] + "|";
			}
			return st;
		}

		void copy(Node n1) {
			System.arraycopy(n1.jars, 0, jars, 0, jarCount);
		}

		void update(int index, int amt){
			jars[index] = jars[index] + amt;
		}

		public boolean equals(Object o) {
			Node n = (Node)o;
			for(int i=0;i<jarCount;i++) {
				if(jars[i] != n.jars[i])
					return false;
			}
			return true;
		}

		public int hashCode() {
			int res = 0;
			for(int i=0;i<jarCount;i++) {
				res = jars[i]*jars[i] + res;
			}
			return res;
		}
	}

	public static void main(String[] args) {
		FillTheJarPuzzle fu = new FillTheJarPuzzle();
	}

}

