import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

public class quincunx extends JPanel implements KeyListener {

  static int       DRADIUS   = 5;
  int              DRADNEG   = (-1) * DRADIUS;
  Rectangle        VPORT     = new Rectangle(DRADIUS*2,DRADIUS*2);

  TileSheet        theTiles  = new TileSheet("resources/dungeon.png",10,13);
  Level            theLevel  = new Level("resources/level.xml");
  ArrayList<Actor> actors    = theLevel.getActors();
  LevelSer         tls       = new LevelSer(theLevel);

  int              COLS      = theLevel.getW();  // cols on board
  int              ROWS      = theLevel.getH();  // rows on board

  public quincunx() throws IOException {
    super();
    this.setFocusable(true);
    this.addKeyListener(this);
    //readLevel("theLevelSer.dat",tls);
    writeLevel("theLevelSer.dat",tls);
    tls.showInfo();
  }

  protected void paintComponent(Graphics g) {
    VPORT.setLocation(actors.get(0));
    VPORT.translate(DRADNEG,DRADNEG);
    int XX=(int)VPORT.getX(); int YY=(int)VPORT.getY();
    if(XX<0) { XX=0; } else if((XX + VPORT.getWidth())>COLS)  { XX=COLS-(int)VPORT.getWidth();  }
    if(YY<0) { YY=0; } else if((YY + VPORT.getHeight())>ROWS) { YY=ROWS-(int)VPORT.getHeight(); }
    VPORT.setLocation(XX,YY);
    for(int c=0;c<COLS;c++) {
      for(int r=0;r<ROWS;r++) {
        int i = r*COLS + c;
        if(VPORT.contains(c,r)) { theTiles.drawTile(g, (int)theLevel.getB(i), c-XX, r-YY, this); }
      }
    }
    for(int a=(actors.size()-1);a>=0;a--) {
      if(VPORT.contains(actors.get(a))) {
        theTiles.drawTile(g,actors.get(a).getI(),((int)actors.get(a).getX()-XX),((int)actors.get(a).getY()-YY),this);
      }
    }
    drawStatus(g, 321, 0);
  }

  public static void main(String arg[]) throws IOException {
    JFrame theFrame = new JFrame("Quincunx");
    theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    quincunx panel = new quincunx();
    //panel.setSize(theTiles.getTileSize());
    theFrame.setSize(437,358);
    theFrame.setContentPane(panel);
    theFrame.setVisible(true);
    panel.requestFocus();
  }

  public void keyPressed(KeyEvent e) {
    Point t = new Point(0,0);  // move destination
    Point p = new Point(0,0);  // push destination
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
        t.setLocation(-1,0);  p.setLocation(-2,0);
        break;
      case KeyEvent.VK_RIGHT:
        t.setLocation(1,0);  p.setLocation(2,0);
        break;
      case KeyEvent.VK_UP:
        t.setLocation(0,-1);  p.setLocation(0,-2);
        break;
      case KeyEvent.VK_DOWN:
        t.setLocation(0,1);  p.setLocation(0,2);
        break;
      default:  // some other unused key
        return;
    }
    t.translate((int)actors.get(0).getX(),(int)actors.get(0).getY());
    p.translate((int)actors.get(0).getX(),(int)actors.get(0).getY());

    int q = isOccupied(t);
    if(q>0) {
      switch(actors.get(q).getInteraction()) {
        case PUSH:
          if(canMoveTo(p)) {
            actors.get(0).setLocation(t);
            actors.get(q).setLocation(p);
          }
          break;
        case HIT:
          System.out.println("OUCH!");
          break;
        case PORT:
          actors.get(0).setLocation(actors.get(actors.get(q).getDest()));
          break;
        case TALK:
          System.out.println("blah blah blah");
          break;
        case NONE:
          actors.get(0).setLocation(t);
          break;
        case COLLIDE:
          break;
        default:
        break;
      }
    }
    else {
      if(canMoveTo(t)) {
        actors.get(0).setLocation(t);
      }
    }

    repaint();
  }

  void drawStatus(Graphics g, int x, int y) {
    g.setColor(new Color(30,30,30));
    g.fillRect(x, y, 100, 320);
  }

  boolean canMoveTo(Point t) {
    Rectangle r = new Rectangle(COLS,ROWS);
    if(!r.contains(t)) { return(false); }
    if(Arrays.binarySearch(theLevel.getWalls(),theLevel.getB(linearize(t)))>=0) { return(false); }
    int a = isOccupied(t);
    if(a>0) { return(!actors.get(a).hasInteraction()); }
    return(true);
  }

  int isOccupied(Point t) {
    for(int i=0;i<actors.size();i++) {
      if(actors.get(i).getLocation().equals(t)) { return(i); }
    }
    return(-1);
  }

  int linearize(Point p) { return( (int)p.getY()*COLS + (int)p.getX() ); }

  public void keyTyped(KeyEvent e)    { }
  public void keyReleased(KeyEvent e) { }

  public void readLevel(String fName, LevelSer theLevel) throws IOException {
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(fName));
      theLevel = (LevelSer)in.readObject();
      in.close();
    } catch (ClassNotFoundException cnfe) {
      System.out.println("doh.");
    }
  }

  public void writeLevel(String fName, LevelSer theLevel) throws IOException {
    ObjectOutput out = new ObjectOutputStream(new FileOutputStream(fName));
    out.writeObject(theLevel);
    out.close();
  }


  
}
