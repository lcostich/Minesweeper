import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import javalib.worldimages.*;

//the game board will be a 2d array of cells, this is the class
//that determines cell identity

class Cell {
  int w;
  ArrayList<Cell> surroundingCells;
  int nearbyMines;
  boolean mineP;
  boolean open;
  boolean flag;
  
  //initialize an ArrayList of colors to retrieve from based
  //on how many mines surround this cell
  //*NOTE* Using Standard Colors:
  //1 = blue, 2 = green, 3 = red, 4 = dark blue
  //5 = maroon, 6 = cyan, 7 = black, 8 = grey
  public static final ArrayList<Color> COLORLIST = new ArrayList<Color>(
      Arrays.asList(
          Color.blue,
          Color.green,
          Color.red,
          new Color(25, 25, 112),
          new Color(176, 48, 96),
          new Color(72, 209, 204),
          Color.black,
          Color.gray));
  
  //constructor for generic Cell w/width
  public Cell(int w) {
    this.w = w;
  }
  
  //helper for constructor testings, allows to set neighbors
  //for a specific cell
  public void makeSurroundings(ArrayList<Cell> s) {
    this.surroundingCells = s;
  }
  
  //switches the value of flag for use in flagging and deflagging
  //only works if this Cell is not open
  public void swapFlag() {
    if (!this.open) {
      this.flag = !this.flag;
    }
  }
  
  //determines whether or not this cell is a mine
  public boolean getMineP() {
    return this.mineP;
  }
  
  //turns this cell into a mine
  public void toMine() {
    this.mineP = true;
  }
  
  //determines how many mines surround This Cell, permutes nearbyMines
  public void numMines() {
    int nmAcc = 0;
    for (int i = 0; i < surroundingCells.size(); i++) {
      if (surroundingCells.get(i).getMineP()) {
        nmAcc++;
      }
    }
    this.nearbyMines = nmAcc;
  }
  
  //opens a Cell, removing flag for draw() purposes if needed
  public void openUp(MSGame msg) {
    this.open = true;
    this.flag = false;
    msg.incOpen();
  }
  
  //Flood Fill operator; stops if the cell has nearbyMines>0
  //returns value of mineP
  public boolean floodFill(MSGame msg) {
    if (!this.open) {
      this.openUp(msg);
      if (this.nearbyMines == 0) {
        for (int i = 0; i < this.surroundingCells.size(); i++) {
          this.surroundingCells.get(i).floodFill(msg);
        }
      }
      return this.getMineP();
    }
    return false;
  }
  
  //to draw a single Cell, using all possible determinants
  //in the created parameters (nearbyMines, flag)
  public WorldImage draw() {
    //a flag should appear as an orange triangle
    if (this.flag) {
      return new FrameImage(
          new OverlayImage(
              new TriangleImage(new Posn(0, -w / 2), 
                  new Posn(w / 2, w / 2),
                  new Posn(-w / 2, w / 2),
                  OutlineMode.SOLID, Color.orange),
              new RectangleImage(w, w, OutlineMode.SOLID, Color.gray)));
    }
    //a non-open square should just be a gray square
    else if (!this.open) {
      return new FrameImage(
          new RectangleImage(w, w, OutlineMode.SOLID, Color.gray));
    }
    //a mine should appear as a red circle over a blank square
    else if (this.getMineP()) {
      return new FrameImage(
          new OverlayImage(
            new CircleImage(w / 4, OutlineMode.SOLID, Color.red),
            new RectangleImage(w, w, OutlineMode.SOLID, Color.darkGray)));
    }
    //a square with no surrounding mines should be blank
    else if (this.nearbyMines == 0) {
      return new FrameImage(
          new RectangleImage(w, w, OutlineMode.SOLID, Color.darkGray));
    }
    //otherwise, determines how many mines and draws the correct number/color
    else {
      return new FrameImage(
          new OverlayImage(
              new TextImage(this.nearbyMines + "", w / 2, 
                  COLORLIST.get(nearbyMines - 1)),
              new RectangleImage(
                  w, w, OutlineMode.SOLID, Color.gray)));
    }
  }
}
