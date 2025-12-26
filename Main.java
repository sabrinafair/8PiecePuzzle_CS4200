package puzzleSolver;
import java.util.Arrays;
import java.util.Stack;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
/*------------------------------------------------------
------------------CS 4200: Project 1--------------------
--------------------------------------------------------
Name:		 	Sabrina Ferras
Date:			12-24-2025
Program Desc:	This porgram takes a 8 piece puzzle and 
				solves it in steps comparing with 
				A* functions
--------------------------------------------------------
--------------------------------------------------------
--------------------------------------------------------*/

public class Main {
	public record IntPair(int first, int second) {}
	
	public static void displayMenu() {
		System.out.println("-------------------");
		System.out.println("Single Test Puzzle");
		System.out.println("-------------------");
		System.out.println("Select Input Method");
		System.out.println("[1] -------- Random");
		System.out.println("[2] -------- Manual");
	}
	
	public static void displayPuzzle(String str) {
		//Function to display the Menu for Puzzle Application
		if(str.length() == 9) {
			for(int i = 0; i < str.length(); i++) {
				System.out.print(str.charAt(i) + " ");
				if((i + 1) % 3 == 0) System.out.println();
			}
		}else {//if input size is incorrrect
			System.out.println("INPUT SIZE INCORRECT");
		}
	}
	
	public static String generatePuzzle() {
		StringBuilder puzzle = new StringBuilder("012345678");
		Random rand = new Random();
		//Fisher-Yates Shuffle to randomize puzzle
		
		for(int i = puzzle.length() - 1; i >= 0; i--) {
			int randomIndex = rand.nextInt(i + 1);
			
			//swap char at random index with char at i
//			char temp = puzzle.charAt(randomIndex);
//			puzzle.setCharAt(randomIndex, puzzle.charAt(i));
//			puzzle.setCharAt(i, temp);
			swapIndex(puzzle, randomIndex, i);
			
		}

		return puzzle.toString();
	}
	
	public static void swapIndex(StringBuilder puzzle, int firstIndex, int secondIndex) {
		//swap char at random index with char at i
		char temp = puzzle.charAt(firstIndex);
		puzzle.setCharAt(firstIndex, puzzle.charAt(secondIndex));
		puzzle.setCharAt(secondIndex, temp);
	}
	
	public static int getH1(String puzzle) {
		int countMisplaced = 0; //hold how many pieces are misplaced
		
		for(int i = 0; i < puzzle.length(); i++) {
			if(puzzle.charAt(i) != i) countMisplaced++;
		}

		return countMisplaced;
	}
	
	public static IntPair getPuzzlePieceIndex(int num) {
		int j = num;
		int i = 0;
		while(j >= 3) {
			j = j - 3;
			i++;
		}
		return new IntPair(i, j);
	}
	
	public static int getPuzzleOffset(IntPair currPlace, IntPair goalPlace) {		
		IntPair offset = new IntPair(Math.abs(currPlace.first - goalPlace.first), Math.abs(currPlace.second - goalPlace.second));	
		return offset.first + offset.second;
	}
	
	public static int getH2(String puzzle) {
		int totalOffset = 0;
		
		for(int i = 0; i < puzzle.length(); i++) {
			int currNum = puzzle.charAt(i) - '0'; //prevent error on char to int conversion
			IntPair currPlace = getPuzzlePieceIndex(i);
			IntPair goalPlace = getPuzzlePieceIndex(currNum);
			totalOffset += getPuzzleOffset(currPlace, goalPlace);
		}

		return totalOffset;
	}
	
	public static void outputSteps(PQNodeEntry node) {
		PQNodeEntry currNode = node;
		Stack<String> steps = new Stack<>();
		boolean reachedRoot = false;
		
		//add all to stack to output in order
			while(!reachedRoot) {
				steps.add(currNode.getPuzzle());
				currNode = currNode.getParentNode();
				if(currNode.getParentNode() == null) {
					reachedRoot = true;
					break;
				};
			}
		
		//loop through stack to display results
		int stepCount = 1;
		while(!steps.empty()) {
			System.out.println("Step: " + stepCount);
			displayPuzzle(steps.pop());
			stepCount++;
		}
		
	}
	

	/*
		Definition:  For any other configuration besides the goal, whenever a tile with 
					 a greater number on it precedes a tile with a smaller number, the 
					 two tiles are said to be inverted.
					 example: 7 1
					 
		Proposition: For a given puzzle configuration, let N denote the sum of the 
					 total number of inversions. Then (N mod 2) is invariant under any 
					 legal move. In other words, after a legal move an odd N remains 
					 odd whereas an even N remains even. Therefore the goal state 
					 described above, with no inversions, has N = 0, and can only be 
					 reached from starting states with even N, not from starting states 
					 with odd N.
	 */
	
	public static boolean checkSolvable(String puzzle) {
		
		//count Inversions
		int totalInversions = 0;
		for(int i = 0; i < puzzle.length() - 1; i++) {
			int checkPiece = puzzle.charAt(i) + '0';
			for(int j = i + 1; j < puzzle.length(); j++) {
				int currPiece = puzzle.charAt(j) + '0';
				if(checkPiece > currPiece) totalInversions++;
		}
		}
		
		boolean solvable = (totalInversions % 2 == 0);

		return solvable;
	}
	
	public static void pushOptionToPQ(int startIndex, int toIndex, PQNodeEntry parentNode, PriorityQueue PQ_H1, PriorityQueue PQ_H2) {
		if(toIndex != -1) {
			StringBuilder currPuzzle = new StringBuilder(parentNode.getPuzzle());
			swapIndex(currPuzzle, startIndex, toIndex);
			
			int h1 = getH1(currPuzzle.toString());
			int h2 = getH2(currPuzzle.toString());
			int totalCost_h1 = parentNode.getG() + 1 + h1;
			int totalCost_h2 = parentNode.getG() + 1 + h2;
			
			PQ_H1.add(new PQNodeEntry(currPuzzle.toString(), totalCost_h1, parentNode, parentNode.getG() + 1));
			PQ_H2.add(new PQNodeEntry(currPuzzle.toString(), totalCost_h2, parentNode, parentNode.getG() + 1));
		}
	}
	
	public static void insertOptions(PQNodeEntry parentNode, PriorityQueue PQ_H1, PriorityQueue PQ_H2) {
		int emptyTileIndex = parentNode.getPuzzle().indexOf('0');
		
		int leftIndex = emptyTileIndex - 1;
		int rightIndex = emptyTileIndex + 1;
		int upIndex = emptyTileIndex - 3;
		int downIndex = emptyTileIndex + 3;
		
		int currLevel = getPuzzlePieceIndex(emptyTileIndex).first;
		int leftLevel = getPuzzlePieceIndex(leftIndex).first;
		int rightLevel = getPuzzlePieceIndex(rightIndex).first;
		
		//check if indecies are available
		if(currLevel != leftLevel) leftIndex = -1;
		if(currLevel != rightLevel) rightIndex = -1;
		if(upIndex < 0) upIndex = -1;
		if(downIndex > 8) downIndex = -1;
		
		//push options to Priority Queue
		pushOptionToPQ(emptyTileIndex, leftIndex, parentNode, PQ_H1, PQ_H2);
		pushOptionToPQ(emptyTileIndex, rightIndex, parentNode, PQ_H1, PQ_H2);
		pushOptionToPQ(emptyTileIndex, upIndex, parentNode, PQ_H1, PQ_H2);
		pushOptionToPQ(emptyTileIndex, downIndex, parentNode, PQ_H1, PQ_H2);
		
		
	}
	
	public static void solvePuzzle(String puzzle) {
		String currPuzzle = puzzle;
		
		List<String> visited = new ArrayList<>();
		PriorityQueue<PQNodeEntry> Frontier_H1 = new PriorityQueue<>();
		PriorityQueue<PQNodeEntry> Frontier_H2 = new PriorityQueue<>();	
		
		int stepCount = 0;
		int g = 0;
		int h1Start = getH1(puzzle);
		int h2Start = getH2(puzzle);
		int totalCost_h1 = g + h1Start;
		int totalCost_h2 = g + h2Start;
		
		PQNodeEntry parentNode_h1 = new PQNodeEntry(puzzle, totalCost_h1, null, g);
		PQNodeEntry parentNode_h2 = new PQNodeEntry(puzzle, totalCost_h2, null, g);
		Frontier_H1.add(parentNode_h1);
		Frontier_H2.add(parentNode_h2);
//		visited.add(currPuzzle);
		boolean solved = false;
		
		while(!solved) {
			stepCount++;
			
			//pop lowest on queue & check if final state
			if(Frontier_H2.peek().getPuzzle().equals("012345678")) {
				solved = true;
				break;
			}
			
			PQNodeEntry currNode = Frontier_H2.poll();
			
			while(visited.contains(currNode.getPuzzle())) {
				currNode = Frontier_H2.poll();
			}
			
			insertOptions(currNode, Frontier_H1, Frontier_H2);

			//add to visited list
			visited.add(currNode.getPuzzle());	
		}
		PQNodeEntry currNode = Frontier_H2.poll();
		outputSteps(currNode);

	}

	public static void main(String[] args) {
		// PUZZLE OF FORM
		// #1 #2 #3
		// #4    #5
		// #6 #7 #8
		//INPUT OF FORM
		// "#1#2#3#4#5#6#7#8"
		//The goal state is:
		//	#0 #1 #2
		//	#3 #4 #5
		//	#6 #7 #8
		//0 represents the empty tile.
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("CS 4200 - Project 1");
		displayMenu();
		
		int inputOption = scanner.nextInt(); //for random or manual option
		scanner.nextLine();
		
		boolean solvable = true;
		
		//if want to repeat menu options use boolean and while
//		boolean endApp = false;
//		while(!endApp) {
			if(inputOption == 1) {
				//create random puzzle
				String puzzle = generatePuzzle();
				
				System.out.println("Puzzle: ");
				displayPuzzle(puzzle);
				
				solvable = checkSolvable(puzzle);
				if(solvable) {
					//solve puzzle here
					solvePuzzle(puzzle);
				}else {
					System.out.println("Puzzle is Not Solvable");
				}
				
			}else if(inputOption == 2) {
				//read manual input of puzzle

				System.out.println("Input Puzzle: ");
				String puzzleInput = scanner.nextLine();


				System.out.println("Puzzle: ");
				displayPuzzle(puzzleInput);
				
				solvable = checkSolvable(puzzleInput);
				if(solvable) {
					//solve puzzle here
					solvePuzzle(puzzleInput);
				}else {
					System.out.println("Puzzle is Not Solvable");
				}
				
				
			}else {
				System.out.print("Input Invalid, try again.");
//				displayMenu();
//				inputOption = scanner.nextInt();
			}	
//		}
		
		
		
		
	}

}
