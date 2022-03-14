
import java.util.*;
import becker.robots.*;

/**
 * Definitions: 
 * Routes are the possible ways a MazeRunner can move.
 * Fork is an intersection which has more than 1 viable path.
 * Path is the trajectory a MazeRunner has taken 
 * a cell is the smallest unit in a maze 
 */
public class MazeRunner extends RobotSE{ 
	private boolean isAtDeadEnd = false;
	private boolean isAtFork = false; 
	private HashMap<Intersection, Integer> visitedRoutes = new HashMap<Intersection, Integer>(); // maps routes to number of times they are visited 
	private ArrayList<ArrayList<String>> allPathList = new ArrayList<ArrayList<String>>();
	private HashMap<Intersection, Direction> directionsAtForks = new HashMap<Intersection, Direction>();
	public int turns;
	public int moves;
		
	MazeRunner(City city, Direction Direction){
		super(city, 0, 0, Direction, 100000); // always starts at top left 
		rememberFork();
	}
	
	
	
	public void turnLeft() {
		super.turnLeft();
		turns++;
	}
	
	private void turnLeftAndLog() {
		turnLeft();
		allPathList.get(allPathList.size()-1).add("turn left");
	}
	
	public void turnRight() {
		super.turnRight();
		turns++;
	}
	
	public void turnRightAndLog() {
		super.turnRight();
		allPathList.get(allPathList.size()-1).add("turn right");
	}
	
	public void move() {
		super.move();
		moves++;
	}
	
	public void moveAndLog() {
		move();
		allPathList.get(allPathList.size()-1).add("move");
	}
	
	
	/**
	 * remembers directions on entering this fork and also sets up to log further path
	 */
	public void rememberFork() {
		allPathList.add(new ArrayList<String>());
		if(!directionsAtForks.containsKey(getIntersection())) // if visiting intersection for first time 
			directionsAtForks.put(getIntersection(), getDirection());
	}
		
	public int getNumberOfOpenPaths() {
		int openPaths = 0;
		
		for (int i = 0; i < 4; i++) {
			if (frontIsClear()) 
				openPaths += 1;
			turnLeft();
		}
		return openPaths;
	}
	
	public boolean isAtStart() {
		return (getStreet() == 0 && getAvenue() == 0) ? true: false;
	}
	
	public boolean isAtFork() {
		if (isAtStart() && getNumberOfOpenPaths() > 1) 
				return true; 
		else if (getNumberOfOpenPaths() > 2) {
			return true;
		}
		else 
			return false; 
	}
	
	public void logCell() {
		if(!visitedRoutes.containsKey(getIntersection()))
			visitedRoutes.put(getIntersection(), 1);
		else
			visitedRoutes.put(getIntersection(), 2);
	}
	
	public int timesVisited(Intersection cell) {
		if(visitedRoutes.containsKey(cell))
			return visitedRoutes.get(cell);
		return 0; // if not visited
	}
	
	public Intersection getFrontCell() {
		return getIntersection().getNeighbor(getDirection());
	}
	
	
	public void decideDirectionOrGoBackToLastIntersection() { 
		int numberOfOpenRoutes = 0 ; 
		/*
		 * walled paths are set to 2 so that they are ignored
		 * if not at start: index 0 of numberOfVisits is right, 1 is straight, 2 is left
		 * if at start: index represent number of left turns from current direction
		 */
		int[] numberOfVisits = isAtStart() ? new int[] {2, 2, 2, 2} : new int[] {2, 2, 2};
		
		if (!isAtStart()) // needed if not at start to remain in right position. Not required at start because it will take 4 left turns. 
			turnRight();
		for (int i = 0; i < numberOfVisits.length; i++) { // finds number of things in each clear path; walled paths are ignored; also finds number of open paths
			if (frontIsClear()) {
				numberOfOpenRoutes++;
				numberOfVisits[i] = timesVisited(getFrontCell());
			}
			if(!(i==numberOfVisits.length-1) || isAtStart()) // last turn left must only be done if is at start 
				turnLeft();
		}
		
		if (!isAtStart())
			turnRight(); // now faces initial direction  
		

		if (Arrays.equals(numberOfVisits, new int[] {2, 2, 2})) { 
			isAtDeadEnd = true;
			return; // return because turns are not needed at dead ends and also to to not remember intersection in case of both a dead end and intersection
		}
		
		
		if (numberOfOpenRoutes > 1) { // if is at fork
			rememberFork(); 
			isAtFork = true; 
		}
		
		int pathWithLeastThings = 0;
		for (int i=1; i<numberOfVisits.length ; i++) {
			if (numberOfVisits[i] < numberOfVisits[pathWithLeastThings]) {  // therefore,  MazeRunner prefers right over straight over left over back. 
				pathWithLeastThings = i;
			}
		}
			
		if(isAtStart()) {
			if(pathWithLeastThings < 2) { // if straight or left 
				for(int i=0; i < pathWithLeastThings; i++) {
					turnLeft();
				}
			} else {
				for(int i = 0; i < (4-pathWithLeastThings); i++) {
					turnRight();
				}
			}
		} else { // if not at start 
			if(pathWithLeastThings == 0) {
				if(isAtFork) // no logging needed at forks 
					turnRight();
				else
					turnRightAndLog();
			} else if(pathWithLeastThings == 2) {
				if(isAtFork) // no logging needed at forks
					turnLeft();
				else
					turnLeftAndLog();
			}// doesn't turn if pathWithLeastThings is straight
		}
		
		
	}		
		
		
	public void goBackToLastIntersection() {
		ArrayList<String> pathFromLastIntersection = allPathList.get(allPathList.size()-1); 
		
		turn180L();

		for (int i = pathFromLastIntersection.size()-1; i >=0 ; i--) {
			String command = pathFromLastIntersection.get(i);
			if (i == 0)
				logCell();
			if (command.equals("move")) 
				move();
			else if(command.equals("turn left")) 
				turnRight();
			else if(command.equals("turn right")) 
				turnLeft();
		
		}
		Direction directionAtLastIntersection = directionsAtForks.get(getIntersection());

		
		//facing same direction
		while(getDirection() != directionAtLastIntersection) {
			if(sideToTurn(directionAtLastIntersection) == 1)
				turnRight();
			else
				turnLeft();
			
		}
			
		allPathList.remove(allPathList.size()-1);
	}

		
	public void moveLogAndPutThing() {
		moveAndLog();
		putThing();
	}

	public void turn180L() {
		turnLeft();
		turnLeft();
	}

	public void turn180R() {
		turnRight();
		turnRight();
	}

	public void moveBack() {
		turn180L();
		move();
		turn180L();
	}
		
	public void markSolution() {
		turn180L();
		String command;
		for(int i = allPathList.size()-1; i >=0 ; i--) {
			if(directionsAtForks.containsKey(getIntersection())) {
				Direction directionAtIntersection = directionsAtForks.get(getIntersection()).opposite();
				while(getDirection() != directionAtIntersection) {
					if(sideToTurn(directionAtIntersection) == 1)
						turnRight();
					else
						turnLeft();
				}
			}	
			for(int j = allPathList.get(i).size()-1; j >=0 ; j--) {

				command = allPathList.get(i).get(j);
				if (command.equals("move")) {
					putThing();
					move();
				}
				else if(command.equals("turn left")) 
					turnRight();
				else if(command.equals("turn right")) 
					turnLeft();	

			}
			putThing();
		}

	}
		
		
    /**
     * finds whether it would be best to turn left or turn right to face a particular direction
     * @param direction the direction to face
     * @return -1 if turn left would be optimal. +1 if turn right would be optimal or in the case where direction of turn is arbitrary
     */
	public int sideToTurn(Direction direction) { 
		if(getDirection().left() == direction) 
			return -1; 
		return 1; // case of right or no preference

	}
		
        /**
         * 
         * @param endStreet Street of exit
         * @param endAvenue Avenue of exit 
         * @param showSolution Whether to put a line of things to show solution  
         */
	public void solveMaze(int endStreet, int endAvenue, boolean showSolution) {
		moves = 0;
		turns = 0; 
		while(getStreet() != endStreet || getAvenue() != endAvenue) {   
			decideDirectionOrGoBackToLastIntersection();

			if (!isAtDeadEnd) {
				moveAndLog();
				if (isAtFork) { 
					logCell();
					isAtFork = false; 
				}
			} else { // is at dead end
				goBackToLastIntersection();
				isAtDeadEnd = false;
			}
		}

		System.out.printf("Solved the maze in %d moves and %d turns", moves, turns);

		if(showSolution) 
			markSolution();
	}	

}
