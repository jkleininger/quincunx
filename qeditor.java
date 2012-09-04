import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.*;

public class qeditor extends JFrame {

  TileSheet        theTiles;
  Level            theLevel;

  JPanel           mainPanel;
  JPanel           qeditPanel;
  JPanel           palettePanel;

  int              COLS = 10;
  int              ROWS = 10;

  int              cTile = 2;
  boolean          cCollide = false;

  public qeditor() throws IOException {
    super();
    this.setFocusable(true);

    theTiles    = new TileSheet("resources/dungeon.png",10,13);
    theLevel    = new Level();

    mainPanel     = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    palettePanel  = new JPanel(){
      @Override
      public void paintComponent(Graphics g) { paintPalette(g); }
    };

    qeditPanel    = new JPanel(){
      @Override
      public void paintComponent(Graphics g) { paintEditor(g); }
    };


    this.add(mainPanel);
    mainPanel.add(palettePanel);
    mainPanel.add(qeditPanel);

    palettePanel.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) { paletteClick(e.getX(),e.getY()); }
    });

    qeditPanel.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) { editorClick(e.getX(),e.getY()); }
    });

    JMenuBar theMenuBar = new JMenuBar();
    JMenu theMenuFile   = new JMenu("File");
    JMenu theMenuTiles  = new JMenu("Tiles");

    JMenuItem itemLoad  = new JMenuItem("Load");
    JMenuItem itemSave  = new JMenuItem("Save");

    JMenuItem itemWall  = new JMenuItem("Wall");
    JMenuItem itemFloor = new JMenuItem("Floor");
    JCheckBoxMenuItem itemCollide = new JCheckBoxMenuItem("Collide",false);

    itemLoad.addActionListener(new ActionListener()  { public void actionPerformed(ActionEvent evt) { readLevel("myLevel.dat",theLevel); } });
    itemSave.addActionListener(new ActionListener()  { public void actionPerformed(ActionEvent evt) { writeLevel("myLevel.dat",theLevel); } });

    itemWall.addActionListener(new ActionListener()    { public void actionPerformed(ActionEvent evt) { cTile=3; } });
    itemFloor.addActionListener(new ActionListener()   { public void actionPerformed(ActionEvent evt) { cTile=1; } });
    itemCollide.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) { cCollide= !cCollide; } });

    theMenuFile.add(itemLoad);
    theMenuFile.add(itemSave);

    theMenuTiles.add(itemWall);
    theMenuTiles.add(itemFloor);
    theMenuTiles.add(itemCollide);

    theMenuBar.add(theMenuFile);
    theMenuBar.add(theMenuTiles);

    this.setJMenuBar(theMenuBar);

  }

  private void paintPalette(Graphics g) {
  }

  private void paintEditor(Graphics g) {
    if(theLevel.getW()<1) { return; }
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
    qeditor theFrame = new qeditor();
    theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    theFrame.setVisible(true);
  }

  int linearize(Point p) { return( (int)p.getY()*COLS + (int)p.getX() ); }

  public void readLevel(String fName, Level myLevel) {
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(fName));
      theLevel = (Level)in.readObject();
      in.close();
    }
    catch (ClassNotFoundException cnfe) { }
    catch (IOException ioe) { }
    repaint();
  }

  public void writeLevel(String fName, Level theLevel) {
    try {
      ObjectOutput out = new ObjectOutputStream(new FileOutputStream(fName));
      out.writeObject(theLevel);
      out.close();
    } catch (IOException ioe) { }
  }

  public void editorClick(int x, int y) {
    System.out.println("Editor ("+x+","+y+")");
    int row = (int)Math.floor(y / (theTiles.getTileSize().getHeight()) );
    int col = (int)Math.floor(x / (theTiles.getTileSize().getWidth()) );
    theLevel.setTile(col,row,cTile,cCollide,false);
    repaint();
  }

  public void paletteClick(int x, int y) {
    System.out.println("Palette ("+x+","+y+")");
    int row = (int)Math.floor(y / (theTiles.getTileSize().getHeight()) );
    int col = (int)Math.floor(x / (theTiles.getTileSize().getWidth()) );
  }

}
