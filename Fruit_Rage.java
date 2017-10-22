package ai;

import java.io.*;
import java.util.*;

public class Fruit_Rage {
	static int xa;
	static int ya;
	static int wh;
	static int fruits;
	static int max_depth;
	
	static double time;
	static double start;
	
	static int[][] grid;
	static int[][] visited;
	
	static ArrayList<xy> al = new ArrayList<>();
	static ArrayList<xy> sl = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(new FileReader("input.txt"));
		
		wh = sc.nextInt();
		max_depth = (int)(Math.log(wh) / Math.log(2));
		fruits = sc.nextInt();
		time = sc.nextDouble()*1000;
				
		if(wh < 1 || fruits < 1 || time < 0) {
			System.out.println("Invalid input! Program terminated.");
			System.exit(0);
		}
		
		int[][] grid_copy = new int[wh][wh];

		grid = new int[wh][wh];
		visited = new int[wh][wh];
		
		for(int i = 0; i < wh; i++) {
			String line = sc.next();
			for(int j = 0; j < wh; j++) {
				grid[i][j] = line.charAt(j)-48;
				grid_copy[i][j] = grid[i][j]; 
				if(grid[i][j] >= fruits) {
					System.out.println("Invalid input! Program terminated.");
					System.exit(0);
				}
				visited[i][j] = -1;
			}
		}
		
		start = System.currentTimeMillis();
		
		int result = min_max(grid_copy, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0, true);
		
		display();
		
		sc.close();
	}

	private static void display() throws IOException {	
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		int num = 'A' + ya;
		char letter = (char) num;
		writer.write(letter+""+String.valueOf(xa+1));
		writer.newLine();
		
		int[][] grid_ans = move(al,grid);
		
		grid_ans = gravity(grid_ans);
		
		for(int i = 0; i < wh; i++) {
			for(int j = 0; j < wh; j++)
				if(grid_ans[i][j]==-6)
					writer.write('*');
				else
					writer.write(String.valueOf(grid_ans[i][j]));			
			writer.newLine();
		}
		writer.close();
	}
 
 	private static boolean check_clock() {
		double curr = System.currentTimeMillis();
		double elapsed = curr - start;

		if(time - elapsed > 0) {
	        return true;
		}
		return false;
	}

	private static int min_max(int[][] grid2, int alpha, int beta, int depth, int score, boolean max) {
		if(depth >= max_depth || !check_clock()) {
			return score;
		}
		
		int v = 0;
		HashMap<ArrayList<xy>,Integer> map = new HashMap<>();
		
		map = getScore(grid2,true,score);
		
		Object[] a = map.entrySet().toArray();
		sorter(a);
		
		map.clear();

		for (Object e : a) {
			map.put(((Map.Entry<ArrayList<xy>, Integer>) e).getKey(), ((Map.Entry<ArrayList<xy>, Integer>) e).getValue());
		}
		
		if(max == true) {
			v = Integer.MIN_VALUE;
			
			for (Object e : a) {
				ArrayList<xy> list = ((Map.Entry<ArrayList<xy>, Integer>) e).getKey();
				int new_score = score + (((Map.Entry<ArrayList<xy>, Integer>) e).getValue()*((Map.Entry<ArrayList<xy>, Integer>) e).getValue());
				
				int[][] gridn = new int[wh][wh];
				grid_copy(grid2,gridn);
				
				int[][] grid_copy = move(list,gridn);
				grid_copy = gravity(grid_copy);
				
				v = Math.max(v, min_max(grid_copy, alpha, beta, depth + 1, new_score, false));		
				
				if(v >= beta) {
					xa = list.get(0).x;
					ya = list.get(0).y;
					al.clear();
					al.addAll(list);
					return v;
				}

				else if(v > alpha) {
					xa = list.get(0).x;
					ya = list.get(0).y;
					al.clear();
					al.addAll(list);
		            alpha = v;
				}
			}
		}
		
		else {
			v = Integer.MAX_VALUE;
			
			for (Object e : a) {
				ArrayList<xy> list = ((Map.Entry<ArrayList<xy>, Integer>) e).getKey();
				int new_score = score - (((Map.Entry<ArrayList<xy>, Integer>) e).getValue()*((Map.Entry<ArrayList<xy>, Integer>) e).getValue());
				
				int[][] gridn = new int[wh][wh];
				grid_copy(grid2,gridn);
				
				int[][] grid_copy = move(list,gridn);
				grid_copy = gravity(grid_copy);
				
				v = Math.max(v, min_max(grid_copy, alpha, beta, depth + 1, new_score, true));		
				
				if(v <= alpha)
					return v;

				else if(beta > v)
		            beta = v;
			}
		}
		
		return v;
	}

	private static void grid_copy(int[][] grid2, int[][] gridn) {
		for(int i = 0; i < wh; i++) {
			for(int j = 0; j < wh; j++)
				gridn[i][j] = grid2[i][j]; 
		}
	}

	private static int[][] gravity(int[][] grid_copy) {
		for(int i = 0; i < wh; i++) {
			int k = wh-1;
			for(int j = wh-1; j >= 0; j--) {
				if(grid_copy[j][i]!=-6) {
					grid_copy[k][i] = grid_copy[j][i];
					k--;
				}				
			}
			if(k != wh-1) {
				while(k!=-1) {
					grid_copy[k][i] = -6;
					k--;
				}
			}
		}
		return grid_copy;
	}

	private static int[][] move(ArrayList<xy> list, int[][] grid2) {
		int x, y;
		for(int i = 0; i < list.size(); i++) {
			x = list.get(i).x;
			y = list.get(i).y;
			grid2[x][y] = -6;
		}
		return grid2;
	}

	private static void sorter(Object[] a) {
		Arrays.sort(a, new Comparator() {
		    public int compare(Object o1, Object o2) {
		        return ((Map.Entry<ArrayList<xy>, Integer>) o2).getValue()
		                   .compareTo(((Map.Entry<ArrayList<xy>, Integer>) o1).getValue());
		    }
		});
	}

	private static HashMap<ArrayList<xy>, Integer> getScore(int[][] grid2, boolean max, int score) {
		int size;
		HashMap<ArrayList<xy>,Integer> hm = new HashMap<>();
		
		if(max == true) {
			size = 0;
		}
		else {
			size = wh*wh;
		}
		
		for(int i = 0; i < wh; i++) {
			for(int j = 0; j < wh; j++) {
				if(grid2[i][j] != -6) {
					int k = clustersize(grid2,i,j,grid2[i][j]);
				
					if(max == true) {
						size = Math.max(size, k);
					}
					else {
						if(k != 0)
							size = Math.min(size, k);
					}
				
					if(sl.size() != 0) {
						ArrayList<xy> ss = new ArrayList<>(sl);
						hm.put(ss,k);
					}
				
					sl.clear();
				}
			}
		}
		for(int i = 0; i < wh; i++) {
			for(int j = 0; j < wh; j++) {
				visited[i][j] = -1;
			}
		}
		return hm;
	}

	private static int clustersize(int[][] grid3, int row, int col, int val) {
		if(row < 0 || col < 0 || row >= wh || col >= wh) {
			return 0;
		}
		if(grid3[row][col] != val) {
			return 0; 
		}
		if(visited[row][col] == -2)
			return 0;
		visited[row][col] = -2;
		int size = 1;
		sl.add(new xy(row,col));
		size += clustersize(grid3,row-1,col,val);
		size += clustersize(grid3,row+1,col,val);
		size += clustersize(grid3,row,col-1,val);
		size += clustersize(grid3,row,col+1,val);
		return size;
	}

}

class xy {
	int x;
	int y;
	xy(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

