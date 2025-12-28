package puzzleSolver;
import java.util.Arrays;
import java.util.Stack;
import java.util.function.Function;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import puzzleSolver.HelperFunctions;
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
import puzzleSolver.HelperFunctions.IntPair;

public class Main {
	
	public static IntPair displayMenu(Scanner scan) {
		System.out.println("-------------------");
		System.out.println("Single Test Puzzle");
		System.out.println("-------------------");
		System.out.println("Select Input Method");
		System.out.println("[1] -------- Random");
		System.out.println("[2] -------- Manual");
		int inputOption = scan.nextInt(); //for random or manual option
		scan.nextLine();
		System.out.println("Select H Function");
		System.out.println("[1] -------- H1");
		System.out.println("[2] -------- H2");
		
		int hOption = scan.nextInt(); 
		scan.nextLine();
		
		return new IntPair(inputOption, hOption);
	}
	
	public static void displayPuzzle(String str) {
		//Function to display the Menu for Puzzle Application
		if(str.length() == 9) {
			for(int i = 0; i < str.length(); i++) {
				System.out.print(str.charAt(i) + " ");
				if((i + 1) % 3 == 0) System.out.println();
			}
		}else {//if input size is incorrrect
			System.out.println("Input size incorrect");
		}
	}
	
	public static String generatePuzzle() {
		StringBuilder puzzle = new StringBuilder("012345678");
		Random rand = new Random();
		//Fisher-Yates Shuffle to randomize puzzle
		
		for(int i = puzzle.length() - 1; i >= 0; i--) {
			int randomIndex = rand.nextInt(i + 1);
			
			HelperFunctions.swapIndex(puzzle, randomIndex, i);
			
		}

		return puzzle.toString();
	}
	
	public static int outputSteps(PQNodeEntry node) {
		PQNodeEntry currNode = node;
		Stack<String> steps = new Stack<>();
		boolean reachedRoot = false;
		
		int totalH = 0;
		
		//add all to stack to output in order
			while(!reachedRoot) {
				steps.add(currNode.getPuzzle());
				totalH += currNode.getH();
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
		
		System.out.println("Search Cost: " + totalH);
		return totalH;
		
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
			if(checkPiece == 0) continue;
			for(int j = i + 1; j < puzzle.length(); j++) {
				int currPiece = puzzle.charAt(j) + '0';
				if(currPiece == 0) continue;
				if(checkPiece > currPiece) totalInversions++;
			}
		}
		
		boolean solvable = (totalInversions % 2 == 0);

		return solvable;
	}
	
	public static void insertOptions(PQNodeEntry parentNode, PriorityQueue PQ, Function<String, Integer> getH) {
		int emptyTileIndex = parentNode.getPuzzle().indexOf('0');
		
		int leftIndex = emptyTileIndex - 1;
		int rightIndex = emptyTileIndex + 1;
		int upIndex = emptyTileIndex - 3;
		int downIndex = emptyTileIndex + 3;
		
		int currLevel = HelperFunctions.getPuzzlePieceIndex(emptyTileIndex).first();
		int leftLevel = HelperFunctions.getPuzzlePieceIndex(leftIndex).first();
		int rightLevel = HelperFunctions.getPuzzlePieceIndex(rightIndex).first();
		
		//check if indecies are available
		if(currLevel != leftLevel) leftIndex = -1;
		if(currLevel != rightLevel) rightIndex = -1;
		if(upIndex < 0) upIndex = -1;
		if(downIndex > 8) downIndex = -1;
		
		//push options to Priority Queue
		HelperFunctions.pushOptionToPQ(emptyTileIndex, leftIndex, parentNode, PQ, getH );
		HelperFunctions.pushOptionToPQ(emptyTileIndex, rightIndex, parentNode, PQ, getH );
		HelperFunctions.pushOptionToPQ(emptyTileIndex, upIndex, parentNode, PQ, getH );
		HelperFunctions.pushOptionToPQ(emptyTileIndex, downIndex, parentNode, PQ, getH );
	}
	
	public static int solvePuzzle(String puzzle, boolean h1, long startTime) {
		String currPuzzle = puzzle;
		
		
		List<String> visited = new ArrayList<>();
		PriorityQueue<PQNodeEntry> Frontier = new PriorityQueue<>();	
		
		int stepCount = 0;
		int g = 0;
		int hStart = h1 ? HelperFunctions.getH1(puzzle) : HelperFunctions.getH2(puzzle);
		int totalCost = g + hStart;
		
		PQNodeEntry parentNode = new PQNodeEntry(puzzle, totalCost, null, g, hStart);
		Frontier.add(parentNode);
		
		boolean solved = false;
		boolean couldNotComplete = false;
		long timeDuration = System.nanoTime();
		
		
		while(!solved && !couldNotComplete) {
			timeDuration = (System.nanoTime() - startTime) / 1_000_000_000;
			if(timeDuration > 10) {
				System.out.println("COULDN'T COMPLETE");
				couldNotComplete = true;
				break;
			}
			stepCount++;
			
			//pop lowest on queue & check if final state
			if(Frontier.peek().getPuzzle().equals("012345678")) {
				solved = true;
				break;
			}
			
			PQNodeEntry currNode = Frontier.poll();
			
			while(visited.contains(currNode.getPuzzle())) {
				currNode = Frontier.poll();
			}
			
			if(h1) insertOptions(currNode, Frontier, HelperFunctions::getH1);
			else  insertOptions(currNode, Frontier, HelperFunctions::getH2);

			//add to visited list
			visited.add(currNode.getPuzzle());	
		}
		PQNodeEntry currNode = Frontier.poll();
		return couldNotComplete ? -1 : outputSteps(currNode);
	}	
//	public static void solvePuzzleH2(String puzzle) {
//		String currPuzzle = puzzle;
//		
//		
//		List<String> visited = new ArrayList<>();
//		PriorityQueue<PQNodeEntry> Frontier_H2 = new PriorityQueue<>();	
//		
//		int stepCount = 0;
//		int g = 0;
//		int h2Start = HelperFunctions.getH2(puzzle);
//		int totalCost_h2 = g + h2Start;
//		
//		PQNodeEntry parentNode_h2 = new PQNodeEntry(puzzle, totalCost_h2, null, g, h1Start, h2Start);
//		Frontier_H2.add(parentNode_h2);
//		
//		boolean solved = false;
//		
//		while(!solved) {
//			stepCount++;
//			
//			//pop lowest on queue & check if final state
//			if(Frontier_H2.peek().getPuzzle().equals("012345678")) {
//				solved = true;
//				break;
//			}
//			
//			PQNodeEntry currNode = Frontier_H2.poll();
//			
//			while(visited.contains(currNode.getPuzzle())) {
//				currNode = Frontier_H2.poll();
//			}
//			
//			insertOptions(currNode, Frontier_H2);
//
//			//add to visited list
//			visited.add(currNode.getPuzzle());	
//		}
//		PQNodeEntry currNode = Frontier_H2.poll();
//		outputSteps(currNode);
//	}

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
		IntPair input = displayMenu(scanner);
		int inputOption = input.first();
		int hOption = input.second();
		boolean h1 = (hOption == 1);
		
		boolean solvable = true;
		
		//if want to repeat menu options use boolean and while
//		boolean endApp = false;
//		while(!endApp) {
			if(inputOption == 1) {
				//create random puzzle
				String puzzle = generatePuzzle();
				
				System.out.println("Puzzle: ");
				displayPuzzle(puzzle);
				long startTime = System.nanoTime();
				long endTime = System.nanoTime();
				
				solvable = checkSolvable(puzzle);
				if(solvable) {
					//solve puzzle here
					
					solvePuzzle(puzzle, h1, startTime);
				}else {
					System.out.println("Puzzle is Not Solvable");
				}
				
			}else if(inputOption == 2) {
				//read manual input of puzzle

				System.out.println("Input Puzzle: ");
				String puzzleInput = "";
				
				
				while(scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if(line == null || line.isEmpty()) break;
					puzzleInput = puzzleInput + line.replaceAll("\\s+", "");
				}


				System.out.println("Puzzle: ");
				System.out.println(puzzleInput);
				displayPuzzle(puzzleInput);
				long startTime = System.nanoTime();
				long endTime = System.nanoTime();
				
				solvable = checkSolvable(puzzleInput);
				if(solvable) {
					//solve puzzle here
					solvePuzzle(puzzleInput, h1, startTime);
				}else {
					System.out.println("Puzzle is Not Solvable");
				}
				
				
			}else if(inputOption == 3) {
				//secret input option used to read in file of puzzles for data purposes and testing

				System.out.println("Input File Path");
				String filePath = scanner.nextLine();
				Path path = Paths.get(filePath);
				Path fileName = path.getFileName();
				int puzzleCount = 0;
				int costTotal = 0;
				long totalTimeNano = 0;
				long totalTimeMili = 0;
				
				
				

				try {
				Scanner readFile = new Scanner(new File(filePath));
				
				while(readFile.hasNextLine()) {
					String line = readFile.nextLine();
					int currCostTotal = 0;
					
					if(line.contains("//")) continue;
					
					String puzzle = line.replaceAll("\\s+", "") + readFile.nextLine().replaceAll("\\s+", "") + readFile.nextLine().replaceAll("\\s+", "");
					System.out.println("Puzzle: ");
					displayPuzzle(puzzle);
					
					long startTime = System.nanoTime();
					long endTime = System.nanoTime();
					long durationTimeNano = 0;
					long durationTimeMili = 0;
					
					solvable = checkSolvable(puzzle);
					if(solvable) {
						//solve puzzle here
						puzzleCount++;
						System.out.println("Puzzle #" + puzzleCount);
						currCostTotal = solvePuzzle(puzzle, h1, startTime);
						if(currCostTotal == -1) {
							puzzleCount--;
						}else {
							endTime = System.nanoTime();
							costTotal += currCostTotal;
							durationTimeNano = endTime - startTime;
							durationTimeMili = durationTimeNano / 1_000_000;
							System.out.println("Time ns: " + durationTimeNano);
							System.out.println("Time ms: " + durationTimeMili);
							totalTimeNano = totalTimeNano +  durationTimeNano;
							totalTimeMili = totalTimeNano / 1_000_000;
						}
					}else {
						System.out.println("Puzzle is Not Solvable");
					}
					
				}}catch(FileNotFoundException e) {
					System.err.println("Error: File " + filePath +  " not found exception: ");
					e.printStackTrace();
				}
				
				System.out.println("-----------------------------------");
				System.out.println(fileName.toString());
				System.out.println("Average Cost: " + (costTotal / puzzleCount));
				System.out.println("Average Total Time ns: " + (totalTimeNano / puzzleCount));
				System.out.println("Averagge Total Time ms: " + (totalTimeMili / puzzleCount));
				
			}else {
				System.out.print("Input Invalid, try again.");
//				displayMenu();
//				inputOption = scanner.nextInt();
			}	
//		}
		
		
		
		
	}

}
