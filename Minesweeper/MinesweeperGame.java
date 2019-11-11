import java.awt.Color;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Random;
import javalib.impworld.*;
import javalib.worldimages.*;


class MSGame extends World {
  //here, we need to set the size of the game board, number of mines,
  //and numbers for convenience so that our game is a nice size :)
  public int numRows;
  public int numColumns;
  public int numMines;
  public int cellW;
  public int openCells;
  //screen size values to determine mouse position, etc.
  public int sizeXVar;
  public int sizeYVar;
  public int sizeXSpace;
  public int sizeYSpace;
  //AND a random for generation purposes, conventionally named
  public Random rand;
  //Finally, the 2d array of cells that is going to be the game board:
  public ArrayList<ArrayList<Cell>> board;
  
  //constructor for MSGame:
  //*NOTE* chose to place parameters in this order due to ij convention
  public MSGame(int numMines, int numRows, int numColumns) {
    //first, need to check for invalid input:
    //neither rows NOR cols can't be 0
    if (numRows <= 0 || numColumns <= 0) {
      throw new IllegalArgumentException(
          "Error: Invalid Row and/or Column Input");
    }
    //mines can't take up the entire board or more
    if (numRows * numColumns <= numMines) {
      throw new IllegalArgumentException(
          "Error: Too Many Mines For This Board Size");
    }
    
    this.numMines = numMines;
    this.rand = new Random();
    this.numRows = numRows;
    this.numColumns = numColumns;
    this.openCells = 0;
  }
  
  @Override
  //new big bang that uses setCellW
  public void bigBang(int width, int height, double tr) {
    setCellW(width, height);
    genBoard();
    genMines(this.rand);
    super.bigBang(width, height, tr);
  }
  
  @Override
  //implementation of makeScene() for this MSGame
  public WorldScene makeScene() {
    WorldImage background = new EmptyImage();
    for (ArrayList<Cell> i : this.board) {
      WorldImage cellLine = new EmptyImage();
      for (Cell j : i) {
        cellLine = new BesideImage(cellLine, j.draw());
      }
      background = new AboveImage(background, cellLine);
    }
    WorldScene ws = this.getEmptyScene();
    ws.placeImageXY(background, sizeXVar, sizeYVar);
    return ws;
  }
  
  @Override
  //to handle clicker events, which include:
  //left click to reveal a cell
  //right/double click to flag/deflag a cell
  public void onMouseReleased(Posn pos, String side) {
    //first, convert posn to ij notation
    int xClick = (pos.x - this.sizeXSpace) / this.cellW;
    int yClick = (pos.y - this.sizeYSpace) / this.cellW;
    //to determine if the click is out of bounds, doing nothing:
    if (xClick >= this.numColumns || xClick < 0
        || yClick >= this.numRows || yClick < 0) {
      //*NOTE* blank return statement used to exit the method
      return;
    }
    //to initialize the selected cell, using our conversion
    Cell ij = board.get(yClick).get(xClick);
    //to handle each type of click:
    if (side.contentEquals("LeftButton")) {
      if (ij.floodFill(this)) {
        this.endOfWorld("Mine Detonated. You Lose!");
      }
      else if (this.openCells >= this.numColumns * this.numRows 
          - this.numMines) {
        this.endOfWorld("Victory! All Mines Flagged.");
      }
    }
    //if not left click, only other possibility is to flag/deflag
    //IFF it was in bounds
    else {
      ij.swapFlag();
    }
  }
  
  @Override
  //overridden implementation of lastScene() for this game
  //as described in assignment, takes in end message for player
  public WorldScene lastScene(String endMsg) {
    this.openBoard();
    WorldScene ws = this.makeScene();
    ws.placeImageXY(new TextImage(
        endMsg, 32, Color.white), this.sizeXVar, this.sizeYVar);
    return ws;
  }
  
  //increments the openCell value
  //helps to determines end game state
  public void incOpen() {
    this.openCells += 1;
  }
  
  //reveals the entire board of this MSGame
  public void openBoard() {
    for (ArrayList<Cell> i : this.board) {
      for (Cell j : i) {
        j.openUp(this);
      }
    }
  }
  
  //to generate mines at random and instance numMines for each cell:
  public void genMines(Random rand) {
    //initialize a place to store all board cells in a 1d array
    ArrayList<Cell> cellHolder = new ArrayList<Cell>();
    //store them:
    for (int i = 0; i < this.numRows; i++ ) {
      for (int j = 0; j < this.numColumns; j++) {
        cellHolder.add(this.board.get(i).get(j));
      }
    }
    //swaps mines at random to scramble the now single list
    //*NOTE* used the same technique in decoder
    int s = cellHolder.size();
    for (int i = 0; i < s; i++) {
      Cell toSwap = cellHolder.get(i);
      int swapIndex = rand.nextInt(s);
      cellHolder.set(i, cellHolder.get(swapIndex));
      cellHolder.set(swapIndex, toSwap);
    }
    //now that the list is fully randomized, we can just call
    //toMine() on the first (numMines) cells in the list
    for (int i = 0; i < this.numMines; i++) {
      cellHolder.get(i).toMine();
    }
    //and, now we make each cell store the # of mines around itself
    //using numMines() in class Cell
    for (int i = 0; i < s; i++) {
      cellHolder.get(i).numMines();
    }
  }
  
  
  //to generate the game board with cells:
  public void genBoard() {
    //instance an empty 2d array for the board
    board = new ArrayList<ArrayList<Cell>>();
    //loops with ij coords to create board:
    for (int i = 0; i < numRows; i++) {
      //instancing an empty row:
      ArrayList<Cell> row = new ArrayList<Cell>();
      for (int j = 0; j < numColumns; j++) {
        //loop to fill in the new row
        row.add(new Cell(cellW));
      }
      //add the new row to the board:
      board.add(row);
    }
    
    //connects proper cells as neighbors, similar to deque:
    int boardH = board.size();
    int boardW = board.get(0).size();
    
    for (int i = 0; i < numRows; i++) {
      
      for (int j = 0; j < numColumns; j++) {
        
        //initialize the neighbors:
        ArrayList<Cell> surroundingCells = new ArrayList<Cell>();
        
        //determines where this cell is and gives it the proper
        //surrounding cells:
        ArrayList<Cell> currentRow = board.get(i);
        //adds cells above this one:
        if (i != 0) {
          ArrayList<Cell> rowUp = board.get(i - 1);
          surroundingCells.add(rowUp.get(j));
          if (j != 0) {
            surroundingCells.add(rowUp.get(j - 1));
          }
          if (j < boardW - 1) {
            surroundingCells.add(rowUp.get(j + 1));
          }
        }
        //adds cells below this one:
        if (i < boardH - 1) {
          ArrayList<Cell> rowDown = board.get(i + 1);
          surroundingCells.add(rowDown.get(j));
          if (j != 0) {
            surroundingCells.add(rowDown.get(j - 1));
          }
          if (j < boardW - 1) {
            surroundingCells.add(rowDown.get(j + 1));
          }
        }
        //adds cell left of this one:
        if (j != 0) {
          surroundingCells.add(currentRow.get(j - 1));
        }
        //adds cell right of this one:
        if (j < boardW - 1) {
          surroundingCells.add(currentRow.get(j + 1));
        }
        //calls constructor helper to set the surrounding cells in place
        currentRow.get(j).makeSurroundings(surroundingCells);
      }
    }
  }
  
  //sets the size of all cells in this game to an appropriate
  //size for the input board / screen size
  //*NOTE* only for use in determining screen size, NOT
  //game mechanics
  public void setCellW(int width, int height) {
    int a = width / this.numColumns;
    int b = height / this.numRows;
    this.sizeXVar = width / 2;
    this.sizeYVar = height / 2;
    this.cellW = Math.min(a, b);
    this.sizeXSpace = (width - this.cellW * this.numColumns) / 2;
    this.sizeYSpace = (height - this.cellW * this.numRows) / 2;
  }
}











