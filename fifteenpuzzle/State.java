package fifteenpuzzle;

import java.util.*;

public class State implements Comparable<State>{

    private int[][] board;
    private int hValue;
    private int f;
    private int[] blank;
    private String move;
    private State parent;
    private int  hashCode = 0;
    private int dimension;

    public State() {

    }

    public State(int[][] board)  {
        this.board = board;
        this.parent = null;
        this.dimension = this.board.length;
        this.hValue = this.heuristic();
        this.move = "";
        this.blank = new int[2];
        this.blackPosition();

        this.hashCode = 3;

        // inline hashing function for the board 
        for (int[] row : this.board) {
            for (int val : row) {
                this.hashCode =31 * this.hashCode + val;
            }
        }
        this.f = hValue;
    }

    

    public int[][] getBoard() {
        return this.board;
    }

    public int  getHashCode() {
        return this.hashCode;
    }


    public String getMove() {
        return this.move;
    }

    public State getParent() {
        return this.parent;
    }
    public int getF() {
        return this.f; 
    }

    public void setMove(String move) {
        this.move = move;
    }


    public void setParent(State parent) {
        this.parent = parent;
    }

    public void setF(int f) {
        this.f = f;
    }

    public void sethValue(int h) {
        this.hValue = h;
    }

    
    //generate all possible state and store them in an array 
    public ArrayList<State> possibleState() {

        int blankRow = this.blank[0];
        int blankColumn = this.blank[1];
        int[][] moves = { { blankRow, blankColumn - 1 }, { blankRow, blankColumn + 1 }, { blankRow - 1, blankColumn }, { blankRow + 1, blankColumn } };


        String[] move = { "R", "L", "D", "U"};

        ArrayList<State> children = new ArrayList<>();

        int moveLength = moves.length;

        for (int i = 0; i < moveLength; i++) {
            int[][] child = this.generateBoard(blankRow, blankColumn, moves[i][0], moves[i][1]);
            if (child != null) {
                State childVertex = new State(child);
                String m = this.board[moves[i][0]][moves[i][1]] + " " + move[i];
                childVertex.setMove(m);

                children.add(childVertex);
            }

        }

        return children;
    }

    //generate the board by swapping blank space and the tile
    private int[][] generateBoard(int blankRow, int blankColumn, int targetRow, int targetColumn) {
        if (targetRow >= 0 && targetRow < this.dimension && targetColumn >= 0 && targetColumn < this.dimension) {
            int[][] temp1 = new int[this.dimension][this.dimension];
            int temp2;
            for (int i = 0; i < this.dimension; i++) {
                temp1[i] = Arrays.copyOf(this.board[i], this.dimension);
            }

            temp2 = temp1[targetRow][targetColumn];
            temp1[targetRow][targetColumn] = temp1[blankRow][blankColumn];
            temp1[blankRow][blankColumn] = temp2;
            return temp1;
        } else {
            return null;
        }
    }


    private int linearConflict(int[][] rowConflict, int[][] columnConflict) {
        int rowConflicts = 0;
        int columnConflicts = 0;

        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension - 1; j++) {
                if (rowConflict[i][j] == i) {
                    for (int k = j + 1; k < this.dimension; k++) {
                        if (this.board[i][j] > this.board[i][k] && rowConflict[i][k] == i) {
                            rowConflicts += 2;
                        }
                    }
                }
                if (columnConflict[i][j] == j) {
                    for (int k = i + 1; k < this.dimension; k++) {
                        if (this.board[i][j] > this.board[k][j] && columnConflict[k][j] == j) {
                            columnConflicts += 2;
                        }
                    }
                }
            }
        }
        return rowConflicts + columnConflicts;
    }


    
    private int numberOfMisplcaceTile() {

        int count = 0;
        int goalValue = 1;

        //calculate the number of miplsace tile in the board 
        for (int row = 0; row < this.dimension; row++) {
            for (int col = 0; col < this.dimension; col++) {

                if (this.board[row][col] != goalValue) {
                    count++;
                }
                goalValue++;
                if (goalValue == this.dimension * this.dimension) {
                    goalValue = 0;
                }
            }
        }

        
        return count - 1;
    }
    
    //manhattan + euclidean + # of misplace tile  +  linear conflict 
    private int heuristic() {
        

        int[][] rowConflict = new int[this.dimension][this.dimension];
        int[][] columnConflict = new int[this.dimension][this.dimension];
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {

                if (this.board[i][j] != 0) {
                    rowConflict[i][j] = (this.board[i][j] - 1) / this.dimension;
                    columnConflict[i][j] = (this.board[i][j] - 1) % this.dimension;
                } else {
                    rowConflict[i][j] = -1;
                    columnConflict[i][j] = -1;
                }
            }
        }
        return manhattan() + (2 * linearConflict(rowConflict, columnConflict)) + 2 * euclidean() + numberOfMisplcaceTile();
    }

    public int euclidean() {
        int distance = 0;

        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                int value = this.board[i][j];
                if (value != 0) {
                    int rowGoal = (value - 1) / this.dimension;
                    int columnGoal = (value - 1) % this.dimension;
                    distance += Math.sqrt(Math.pow(i - rowGoal, 2) + Math.pow(j - columnGoal, 2));
                }
            }
        }

        return distance;
    }


    public int manhattan() {
        int distance = 0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                int value = board[i][j];

                if (value != 0) {
                    int targetI = (value - 1) / board.length;
                    int targetJ = (value - 1) % board.length;
                    distance += Math.abs(i - targetI) + Math.abs(j - targetJ);
                }
            }
        }

        return distance;
    }

    //comparing hashCode
    @Override
    public boolean equals(Object other) {
        return other != null && ((State) other).getHashCode() == this.getHashCode();
    }

    
    public void blackPosition() {
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                if (this.board[i][j] == 0) {
                    this.blank[0] = i;
                    this.blank[1] = j;
                    break;
                }
            }
        }
    }

    @Override
    public int compareTo(State other) {
        if (other != null) {
            return Integer.compare(this.getF(), other.getF());
        }
        return 0;
    }

    
}