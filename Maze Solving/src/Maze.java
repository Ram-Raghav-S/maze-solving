
import java.util.Scanner;

import becker.robots.*;

public class Maze {	
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		// building maze 
		int numberOfStreets;
		int numberOfAvenues;
		try {
			System.out.print("Length of maze: ");
			numberOfStreets = scanner.nextInt();
			System.out.print("Width of maze: ");
			numberOfAvenues = scanner.nextInt();
		} finally {
			scanner.close();;
		}	
		
		MazeCity maze = new MazeCity(numberOfStreets, numberOfAvenues);
		
		
		// taking exit as bottom right (can be changed)
		int endStreet = numberOfStreets - 1;
		int endAvenue = numberOfAvenues - 1;
		
		MazeRunner karel = new MazeRunner(maze, Direction.SOUTH); 
		karel.solveMaze(endStreet, endAvenue, true);
		
	}			
	
}
			
		
		

		
	
	
