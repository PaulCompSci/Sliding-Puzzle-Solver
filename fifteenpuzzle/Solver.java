package fifteenpuzzle;

import java.io.* ;
import java.util.*;
import java.util.PriorityQueue;


public class Solver {

	public static int dimension;  // dimension of the baord e.g. : 3x3
	public static int[][] goalBoard;

	public static void main(String[] args) throws  IOException {


		//read file and store the board as 2D array 
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		dimension = (int) br.read() -'0';
		br.readLine();

		int[][] board = new int[dimension][dimension];
		int c1, c2, s;

		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				c1 = br.read();
				c2 = br.read();
				s = br.read(); // skip the space
				
				if (c1 == ' ')
					c1 = '0';
				if (c2 == ' ')
					c2 = '0';
				board[i][j] = 10 * (c1 - '0') + (c2 - '0');
			}
		}
		br.close();

		
		//create  2D array as a goal board 
		goalBoard = new int[dimension][dimension];
		int index = 1;
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (i == dimension - 1 && j == dimension - 1) {
					goalBoard[i][j] = 0;
					break;
				}
				goalBoard[i][j] = index;
				index++;
			}
		}

		//add the solution to the stack
		State solution = PHS(board);
		Stack<String> resultList = new Stack<>();
		while (solution.getParent() != null) {
			resultList.add(solution.getMove());
			solution = solution.getParent();
		}

		//output solution from the Stack into .txt file
		File fout = new File(args[1]);
		PrintWriter outputFile = new PrintWriter(fout);
		while (!resultList.isEmpty()) {
			
			String result = resultList.pop();
			
			outputFile.println(result);
		}
		outputFile.close();


		
	}
	

	//using Pure Heuristic search to solve the board
	public static State PHS(int[][] start) {

		State initialState = new State(start);

		HashMap<Integer,State> visited = new HashMap<>();
		HashSet<Integer> inQueue = new HashSet<>();
		PriorityQueue<State> queue = new PriorityQueue<>();

		queue.add(initialState);
		State goal = new State(goalBoard);
		inQueue.add(initialState.getHashCode());
		while (!queue.isEmpty()) {
			State node = queue.remove();
			inQueue.remove(node.getHashCode());

			for (State child : node.possibleState()) {
				if (child.getHashCode() == goal.getHashCode()) {
					child.setParent(node);
					return child;
				}

				State closedChild = null;
				State openChild = null;

				if (inQueue.contains(child.getHashCode())) {
					openChild = queue.stream().filter(n -> n.equals(child)).findFirst().get();
					if (openChild.compareTo(child) > 0) {
						queue.remove(openChild);
						child.setParent(node);
						queue.add(child);
					}
				}
				else {
					int childHash = child.getHashCode();
					if (visited.containsKey(childHash)) {

						closedChild = visited.get(child.getHashCode());
						if(closedChild.compareTo(child) > 0) {
							visited.put(childHash, child);
						}
					}

				}
				if (openChild == null && closedChild == null) {
					queue.add(child);
					child.setParent(node);
					inQueue.add(child.getHashCode());
				}

			}

			visited.put(node.getHashCode(), node); 
		}
		return null;
	}
}