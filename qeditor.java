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
  JScrollPane      qeditScroll;
  JPanel           palettePanel;
  JScrollPane      paletteScroll;

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
    mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));

    palettePanel  = new JPanel() {
      @Override
      public void paintComponent(Graphics g) { paintPalette(g); }
    };
    paletteScroll = new JScrollPane(palettePanel);
    palettePanel.setPreferredSize(new Dimension((int)theTiles.getTileSize().getWidth(),(int)(theTiles.getTileSize().getHeight()*theTiles.getTileCount())));
    paletteScroll.setMinimumSize(new Dimension((int)theTiles.getTileSize().getWidth()+50,300));
    paletteScroll.setMaximumSize(new Dimension((int)theTiles.getTileSize().getWidth()+50,1000));

    qeditPanel    = new JPanel(){
      @Override
      public void paintComponent(Graphics g) { paintEditor(g); }
    };
    qeditScroll = new JScrollPane(qeditPanel);
    qeditPanel.setPreferredSize(new Dimension(800,800));
    qeditScroll.setMinimumSize(new Dimension(400,400));
    qeditScroll.setMaximumSize(new Dimension(1000,1000));

    this.add(mainPanel);
    mainPanel.add(paletteScroll);
    mainPanel.add(qeditPanel);

    palettePanel.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) { paletteClick(e.getX(),e.getY()); }
    });

    qeditPanel.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) { editorClick(e.getX(),e.getY()); }
    });

    JMenuBar theMenuBar = new JMenuBar();
    JMenu theMenuFile   = new JMenu("File");
    JMenu theMenuTiles  = new JMenu("Tile Properties");

    JMenuItem itemLoad  = new JMenuItem("Load");
    JMenuItem itemSave  = new JMenuItem("Save");

    JCheckBoxMenuItem itemCollide = new JCheckBoxMenuItem("Collide",false);
    JCheckBoxMenuItem itemPlayer  = new JCheckBoxMenuItem("Player",false);

    itemLoad.addActionListener(new ActionListener()  { public void actionPerformed(ActionEvent evt) { readLevel("myLevel.dat",theLevel); } });
    itemSave.addActionListener(new ActionListener()  { public void actionPerformed(ActionEvent evt) { writeLevel("myLevel.dat",theLevel); } });

    itemCollide.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) { cCollide= !cCollide; } });
    itemCollide.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) { makeNewPlayer(); } });

    theMenuFile.add(itemLoad);
    theMenuFile.add(itemSave);

    theMenuTiles.add(itemCollide);
    theMenuTiles.add(itemPlayer);

    theMenuBar.add(theMenuFile);
    theMenuBar.add(theMenuTiles);

    this.setJMenuBar(theMenuBar);

  }

  private void makeNewPlayer() {
    for(Actor theActor : theLevel.getActors()) {
      if(theActor.getName().equals("player")) { theLevel.removeActor(theActor); }
    }
  }

  private void paintTools(Graphics g) {
    g.setColor(new Color(30,30,30));
    g.fillRect(0, 0, 100, 100);
  }

  private void paintPalette(Graphics g) {
    Dimension d = theTiles.getTileSize();
    int       t = theTiles.getTileCount();
    int       r = 0;
    for(int n=0;n<t;n++) {
      theTiles.drawTile(g,n,0,r++,this);
    }
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
    theFrame.setSize(437,358);
    theFrame.setVisible(true);
  }

  int linearize(Point p) { return( (int)p.getY()*COLS + (int)p.getX() ); }

  public void readLevel(String fName, Level myLevel) {
    System.out.println("trying to read the level");
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(fName));
      theLevel = (Level)in.readObject();
      in.close();
      System.out.println("read the level");
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
    int row = (int)Math.floor(y / (theTiles.getTileSize().getHeight()) );
    cTile = row;
  }

}
