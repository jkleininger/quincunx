import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

public class qeditor extends JPanel {

  TileSheet        theTiles;
  Level            theLevel;

  int              COLS = 10;
  int              ROWS = 10;

  int              cTile = 2;

  public qeditor() throws IOException {
    super();
    this.setFocusable(true);

    theTiles    = new TileSheet("resources/dungeon.png",10,13);
    theLevel    = new Level();
    //theLevel.initialize(COLS,ROWS);
    readLevel("myLevel.dat",theLevel);
    theLevel.addPlayer(3,4);

    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) { processClick(e.getX(),e.getY()); }
    });

    JMenuBar theMenuBar = new JMenuBar();
    JMenu theMenu = new JMenu("Tiles");

    JMenuItem itemWall = new JMenuItem("Wall");
    itemWall.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cTile=3;
      }
    });
    theMenu.add(itemWall);

    JMenuItem itemFloor = new JMenuItem("Floor");
    itemFloor.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cTile=1;
      }
    });
    theMenu.add(itemFloor);

    theMenuBar.add(theMenu);
    add(theMenuBar);
  }

  protected void paintComponent(Graphics g) {
    int i = 0;
    for(int c=0;c<COLS;c++) {
      for(int r=0;r<ROWS;r++) {
        i = r*COLS + c;
        theTiles.drawTile(g, theLevel.getI(i), c, r, this);
      }
    }
    for(int a=(theLevel.getActorCount()-1);a>=0;a--) {
        theTiles.drawTile(g,theLevel.getActor(a).getI(),((int)theLevel.getActor(a).getX()),((int)theLevel.getActor(a).getY()),this);
    }
  }

  public static void main(String arg[]) throws IOException {
    JFrame theFrame = new JFrame("Quincunx");
    theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    qeditor panel = new qeditor();
    theFrame.setSize(800,700);
    theFrame.setContentPane(panel);
    theFrame.setVisible(true);
    panel.requestFocus();
  }

  int linearize(Point p) { return( (int)p.getY()*COLS + (int)p.getX() ); }

  public void readLevel(String fName, Level myLevel) {
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(fName));
      theLevel = (Level)in.readObject();
      in.close();
      System.out.println("i just read a file.");
      theLevel.dumpMap();
    } catch (ClassNotFoundException cnfe) {
      System.out.println("doh.");
    } catch (IOException ioe) {
      System.out.println("ioe");
    }
  }

  public void writeLevel(String fName, Level theLevel) {
    try {
      ObjectOutput out = new ObjectOutputStream(new FileOutputStream(fName));
      out.writeObject(theLevel);
      out.close();
      theLevel.dumpMap();
    } catch (IOException ioe) {
      System.out.println("ioe");
    }
  }

  public void processClick(int x, int y) {
    int row = (int)Math.floor(y / (theTiles.getTileSize().getHeight()) );
    int col = (int)Math.floor(x / (theTiles.getTileSize().getWidth()) );
    System.out.println("Clicked (" + col + "," + row + ")");
    if( (col==0) && (row==0) )   { writeLevel("myLevel.dat",theLevel); }
    if( (col==19) && (row==19) ) { readLevel("myLevel.dat",theLevel); }
    theLevel.setTile(col,row,cTile,false,false);
    repaint();
  }

}
