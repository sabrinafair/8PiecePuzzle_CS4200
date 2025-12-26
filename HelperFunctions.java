package puzzleSolver;

import java.util.PriorityQueue;

public final class HelperFunctions {

	public record IntPair(int first, int second) {}

	public static void swapIndex(StringBuilder puzzle, int firstIndex, int secondIndex) {
		//swap char at random index with char at i
		char temp = puzzle.charAt(firstIndex);
		puzzle.setCharAt(firstIndex, puzzle.charAt(secondIndex));
		puzzle.setCharAt(secondIndex, temp);
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
	
	public static void pushOptionToPQ(int startIndex, int toIndex, PQNodeEntry parentNode, PriorityQueue PQ_H1, PriorityQueue PQ_H2) {
		if(toIndex != -1) {
			StringBuilder currPuzzle = new StringBuilder(parentNode.getPuzzle());
			swapIndex(currPuzzle, startIndex, toIndex);
			
			int h1 = getH1(currPuzzle.toString());
			int h2 = getH2(currPuzzle.toString());
			int totalCost_h1 = parentNode.getG() + 1 + h1;
			int totalCost_h2 = parentNode.getG() + 1 + h2;
			
			PQ_H1.add(new PQNodeEntry(currPuzzle.toString(), totalCost_h1, parentNode, parentNode.getG() + 1, h1, h2));
			PQ_H2.add(new PQNodeEntry(currPuzzle.toString(), totalCost_h2, parentNode, parentNode.getG() + 1, h1, h2));
		}
	}
	
	public static int getH1(String puzzle) {
		int countMisplaced = 0; //hold how many pieces are misplaced
		
		for(int i = 0; i < puzzle.length(); i++) {
			if(puzzle.charAt(i) != i) countMisplaced++;
		}

		return countMisplaced;
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

}
