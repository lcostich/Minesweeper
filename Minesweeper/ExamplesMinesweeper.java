import tester.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import javalib.worldimages.*;

class ExamplesMinesweeper {
  
  //example just for a preliminary game tester
  MSGame testGame = new MSGame(15, 10, 10);
  
  /*
   * test for main game
   * IMPORTANT NOTE PLEASE READ *
   * for the game to work right, the window size must be your screen resolution
   * for example, my 13" latop is 1440x900, so the input is 1440,900
   * to determine this, Google "what is my screen resolution" and 
   * click the first link
   */
  void testScene(Tester t) {
    this.testGame.bigBang(1440, 900);
  }
  
  //additional examples to be initialized:
  MSGame game1;
  MSGame game2;
  ArrayList<Cell> list1;
  ArrayList<Cell> list2;
  Cell cell1;
  Cell cell2;
  Cell cell3;
  
  //to initialize the declared values
  void initializeBoard() {
    game1 = new MSGame(5, 3, 4);
    game2 = new MSGame(2, 2, 2);
    
    cell1 = new Cell(4);
    cell2 = new Cell(4);
    cell3 = new Cell(4);
    
    cell2.toMine();
    cell3.toMine();
    
    cell1.open = true;
    cell3.open = true;
    
    list1 = new ArrayList<Cell>(Arrays.asList(cell1, cell3));
    list2 = new ArrayList<Cell>(Arrays.asList(cell3, cell2));
    
    cell1.makeSurroundings(list2);
    cell2.makeSurroundings(list1);
    cell3.makeSurroundings(new ArrayList<Cell>());
  }
  
  //tests have to be void so that values can be mutated and checked multiple times
  
  //to test constructor exceptions
  void testBuild(Tester t) {
    t.checkConstructorException(
        new IllegalArgumentException("Error: Too Many Mines For This Board Size"),
        "MSGame", 20, 4, 4);
    t.checkConstructorException(
        new IllegalArgumentException("Error: Invalid Row and/or Column Input"),
        "MSGame", 0, 0, 0);
  }
  
  //to test getMineP and toMine
  void testMineMethods(Tester t) {
    initializeBoard();
    t.checkExpect(cell1.getMineP(), false);
    t.checkExpect(cell2.getMineP(), true);
    t.checkExpect(cell3.getMineP(), true);
    
    cell1.toMine();
    t.checkExpect(cell1.getMineP(), true);
  }
  
  //to test flagging
  void testFlag(Tester t) {
    initializeBoard();
    t.checkExpect(cell1.flag, false);
    t.checkExpect(cell2.flag, false);
    t.checkExpect(cell3.flag, false);
    
    cell1.swapFlag();
    cell2.swapFlag();
    cell3.swapFlag();
    //cell 1 and 3 wont change, since they are open
    //cell 2 will swap
    t.checkExpect(cell1.flag, false);
    t.checkExpect(cell2.flag, true);
    t.checkExpect(cell3.flag, false);
    
    cell2.swapFlag();
    //since cell 2 is still not open, it can swap back
    t.checkExpect(cell1.flag, false);
  }
  
  //to test basic draw implementation
  void testDraw(Tester t) {
    initializeBoard();
    //drawing a mine:
    t.checkExpect(cell3.draw(), 
        new FrameImage(
          new OverlayImage(
            new CircleImage(4 / 4, OutlineMode.SOLID, Color.red),
            new RectangleImage(4, 4, OutlineMode.SOLID, Color.darkGray))));
    //drawing a blank open cell:
    t.checkExpect(cell1.draw(),
        new FrameImage(
            new RectangleImage(4, 4, OutlineMode.SOLID, Color.darkGray)));
    //drawing a numbered cell:
    cell1.numMines();
    t.checkExpect(cell1.draw(),
        new FrameImage(
            new OverlayImage(
                new TextImage("2", 4 / 2, 
                    Color.green),
                new RectangleImage(
                    4, 4, OutlineMode.SOLID, Color.gray))));
    //drawing a flagged cell:
    cell2.swapFlag();
    t.checkExpect(cell2.draw(),
        new FrameImage(
            new OverlayImage(
                new TriangleImage(new Posn(0, -4 / 2), 
                    new Posn(4 / 2, 4 / 2),
                    new Posn(-4 / 2, 4 / 2),
                    OutlineMode.SOLID, Color.orange),
                new RectangleImage(4, 4, OutlineMode.SOLID, Color.gray))));
  }
  
  
  
  
}
