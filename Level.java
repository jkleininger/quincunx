import java.io.*;
import java.util.*;
import java.awt.Point;
import java.awt.image.*;
import java.awt.Color;
import java.awt.Graphics2D;

public class Level {

  int               mapWd;
  int               mapHt;
  ArrayList<Actor>  actor = new ArrayList<Actor>();
  ArrayList<Tile>   map   = new ArrayList<Tile>();
  int               maxElevation = 1;

  int               pIndex = 90;

  BufferedImage     bimg;
  Graphics2D        theG;

  int               cellIterations = 5;

  int cWall  = 0;
  int cFloor = 1;

  public Level() {
  }

  public Level(Boolean b) {
    if(b) {
      mapWd = 100;
      mapHt = 100;
      initialize(mapWd,mapHt);
    }
  }

  public Level(Level theLevel) {
    mapWd  = theLevel.getW();
    mapHt  = theLevel.getH();
    actor  = theLevel.getActors();
    map    = theLevel.getMap();
  }

  void initialize(int w, int h) {

    map.clear();
    map.ensureCapacity(w*h);
    actor.clear();
    mapWd = w;
    mapHt = h;

    genMap(map, 1, maxElevation);
    makeConnectedBlobs(40);
    //chopMap(map,4);
    //smoothMap(map,1);

    //onebitMap(map);
    for(int iter=0 ; iter<cellIterations ; iter++) map = iterateCells(map,4,5);

    updateCollides();
    addPlayer(findOccupyablePoint());
    initMobs(10); 
    initCrates(20);

    drawMapBorder(map);
    bimg = getImgFromTiles(map);

    System.out.println("Made new map at " + mapWd + "x" + mapHt);
  }

  void drawMapBorder(ArrayList<Tile> m) {
    for(int r=0 ; r<mapHt ; r++) {
      m.set(linearize(0,r),new Tile(0,r,true,false,0));
      m.set(linearize(mapWd-1,r),new Tile(0,r,true,false,0));
    }
    for(int c=0 ; c<mapWd ; c++) {
      m.set(linearize(c,0),new Tile(c,0,true,false,0));
      m.set(linearize(c,mapHt-1),new Tile(c,mapHt,true,false,0));
    }
  }

  void initMobs(int n) {
    for(int m=0;m<n;m++) {
      actor.add(new Actor(findOccupyablePoint(),60,Actor.interaction.TALK));
    }
  }

  void initCrates(int n) {
    for(int c=0;c<n;c++) {
      actor.add(new Actor(findOccupyablePoint(),80,Actor.interaction.PUSH));
      actor.get(actor.size()-1).setAI(Actor.ai.NONE); 
   }
  }

  // need to check for existing actor
  Point findOccupyablePoint() {
    Point p = new Point(20,20);
    do {
      p.setLocation((int)(Math.random()*mapWd),(int)(Math.random()*mapHt));
    } while(collides((int)p.getX(),(int)p.getY()));
    return p;
  }

  void onebitMap(ArrayList<Tile> m) {
    for(int r=0;r<mapHt;r++) {
      for(int c=0;c<mapWd;c++) {
        if(m.get(linearize(c,r)).getElevation()>0) { m.get(linearize(c,r)).setElevation(1); }
      }
    }
  }

  void chopMap(ArrayList<Tile> m, int chopElevation) {
    for(int r=0;r<mapHt;r++) {
      for(int c=0;c<mapWd;c++) {
        if(m.get(linearize(c,r)).getElevation() > chopElevation) {
          m.get(linearize(c,r)).setElevation(chopElevation);
        }
      }
    }
  }

  void compressMap(ArrayList<Tile> m, int levels) {
    double factor = levels / (maxElevation + 1);
    for(int r=0;r<mapHt;r++) {
      for(int c=0;c<mapWd;c++) {
        int newElevation = (int)(Math.ceil( (double)m.get(linearize(c,r)).getElevation() * factor ));
        m.get(linearize(c,r)).setElevation(newElevation);
      }
    }
    //chopMap(levels-1);
  }

  void makeBlobs(int b) {
    for(int n=0;n<b;n++) {
      int rmX = (int)(Math.random()*(mapWd-1));
      int rmY = (int)(Math.random()*(mapHt-1));
      createBlob(rmX,rmY);
    }
  }

  void makeConnectedBlobs(int b) {
    Point[] thePoints = new Point[b];
    int steps = 15;
    for(int p=0;p<b;p++) {
      thePoints[p] = new Point((int)(Math.random()*mapWd),(int)(Math.random()*mapHt));
    }
    for(int p=1;p<b;p++) {
      int x0 = (int)thePoints[p-1].getX();
      int y0 = (int)thePoints[p-1].getY();
      int x1 = (int)thePoints[p].getX();
      int y1 = (int)thePoints[p].getY();
      double stepX = (x1 - x0) / steps;
      double stepY = (y1 - y0) / steps;
      double cx = x0;
      double cy = y0;
      for(int cs=0;cs<steps;cs++) {
        createBlob((int)(cx+(stepX*cs)),(int)(cy+(stepY*cs)));
      }
    }
  }

  void makeConnectedRooms(int r) {
    Point[] thePoints = new Point[r];
    int pX = (int)(Math.random()*mapWd);
    int pY = (int)(Math.random()*mapHt);
    for(int p=0;p<r;p++) {
      int cX = (int)(Math.random()*mapWd);
      int cY = (int)(Math.random()*mapHt);
    }
  }

  void createBlob(int x, int y) {
    int theI = 0;

    //double xr = mapWd / (2 * Math.abs(x-(mapWd/2)));
    //double yr = mapHt / (2 * Math.abs(x-(mapHt/2)));

    int blobWd = (int)(Math.random()*(mapWd/15))+2;
    int blobHt = (int)(Math.random()*(mapHt/15))+2;

    //int blobWd = (int)((xr * 10) + 3);
    //int blobHt = (int)((yr * 10) + 3);

    x=(x+blobWd)>mapWd?mapWd-x-1:x;
    y=(y+blobHt)>mapHt?mapHt-y-1:y;

    for(int r=y;r<y+blobHt;r++) {
      for(int c=x;c<x+blobWd;c++) {
        theI = (int)Math.floor(Math.random()*maxElevation)+1;
        getTile(c,r).setElevation(theI);
      }
    }

  }

  void smoothMap(ArrayList<Tile> m, int iterations) {
    for(int i=0;i<iterations;i++) {
      for(int r=1;r<mapHt-1;r++) {
        for(int c=1;c<mapWd-1;c++) {
          m.get(linearize(c,r)).setElevation(smoothPoint(m,c,r));
        }
      }
    } 
  }

  void updateCollides() {
    int i = 0; 
    for(int r=0;r<mapHt;r++) {
      for(int c=0;c<mapWd;c++) {
        i = linearize(c,r);
        if(map.get(i).getElevation() == 0) { map.get(i).setCollide(true); }
        else { map.get(i).setCollide(false); }
      }
    }
  }

  int smoothPoint(ArrayList<Tile> m, int x, int y) {
    float corners = ( getElevation(m, x-1,y-1) + getElevation(m, x+1,y-1) + getElevation(m, x-1,y+1) + getElevation(m, x+1,y+1) ) / 16;
    float sides   = ( getElevation(m, x-1,y)   + getElevation(m, x+1,y)   + getElevation(m, x,y-1)   + getElevation(m, x,y+1)   ) / 8;
    float center  = ( getElevation(m, x,y) ) / 4 ;

    if(center==0) { return 0; } else { return (int)(corners+sides+center); }

  }

  void genMap(ArrayList<Tile> m, double wProbability, int levels) {
    for(int c=0 ; c<mapWd ; c++) {
      for(int r=0 ; r<mapHt ; r++) {
        double thisTile = Math.random();
        m.add(new Tile(c,r,(thisTile<wProbability),false,thisTile<wProbability?0:((int)(thisTile*(levels+1)))));
      }
    }
  }

  ArrayList<Tile> iterateCells(ArrayList<Tile> m, int r1, int r2) {
    ArrayList<Tile> mOut = new ArrayList<Tile>();
    mOut.clear();
    mOut.ensureCapacity(mapWd*mapHt);
    int outTile = 1;
    int inTile = 0;

    for(int c=0 ; c<mapWd ; c++) {
      for(int r=0 ; r<mapHt ; r++) {
        inTile = m.get(linearize(c,r)).getElevation();
        outTile = 1;
        int neighbors = countNeighbors(m,c,r);
        if(neighbors<9) {
          if(inTile==0) {
            if(neighbors>=r1) { outTile=0; }
          } else if(inTile>0) {
            if(neighbors>=r2) { outTile=0; }
          }
        }
        //System.out.print(inTile + "," + neighbors + "," + outTile + " *** ");
        mOut.add(new Tile(c,r,(outTile==0),false,outTile));
      }
    }
    return mOut;
  }

  int countNeighbors(ArrayList<Tile> m, int x, int y) {
    if(x<=0 || x>=(mapWd-1) || y<=0 || y>=(mapHt-1)) {
      return 9;
    } else {
      int  num = 0;

      for(int r=-1 ; r<=1 ; r++) {
        for(int c=-1 ; c<=1 ; c++) {
          if(!(r==0 && c==0)) {
            if(m.get(linearize(x+c,y+r)).getElevation()==0) num++;
          }
        }
      }

      return num;
    }

  }

  BufferedImage getImgFromTiles(ArrayList<Tile> m) {
    BufferedImage b = new BufferedImage(mapWd,mapHt,BufferedImage.TYPE_INT_RGB);
    for(int r=0 ; r<mapHt ; r++) {
      for(int c=0 ; c<mapWd ; c++) {
        int e = m.get(linearize(c,r)).getElevation();
        //System.out.print(e);
        b.setRGB(c,r,new Color(e*24,e*24,e*24).getRGB());
      }
      //System.out.println();
    }
    return b;
  }

  ArrayList<Actor> getActors()                { return actor;                               }
  Actor            getActor(int a)            { return actor.get(a);                        }
  ArrayList<Tile>  getMap()                   { return map;                                 }
  int              getW()                     { return mapWd;                               }
  int              getH()                     { return mapHt;                               }
  Tile             getTile(int t)             { return map.get(t);                          }
  Tile             getTile(int c, int r)      { return map.get(linearize(c,r));             }
  Tile             getTile(Point p)           { return map.get(linearize(p));               }
  int              getActorCount()            { return actor.size();                        }
  int              getElevation(int t)        { return map.get(t).getElevation();           }
  int              getElevation(Point p)      { return getTile(p).getElevation();           }
  int              getElevation(int c, int r) { return getTile(c,r).getElevation();         }
  void             removeActor(Actor a)       { actor.remove(a);                            }
  void             addPlayer(Point p)         { actor.add(new Actor(p,pIndex));             }
  int              linearize(int x, int y)    { return((y*mapWd)+x);                        }
  int              linearize(Point p)         { return( (int)((p.getY()*mapWd)+p.getX()));  }
  boolean          collides(int c, int r)     { return getTile(c,r).collides();             }
  boolean          isLOS(int c, int r)        { return getTile(c,r).isLOS();                }
  void             setLOS(int c, int r)       { getTile(c,r).setLOS(true);                  }
  void             clrLOS(int c, int r)       { getTile(c,r).setLOS(false);                 }
  boolean          getSeen(int c, int r)      { return getTile(c,r).getSeen();              }
  void             setSeen(int c, int r)      { getTile(c,r).setSeen();                     }

  int getElevation(ArrayList<Tile> m, int c, int r) {
    return m.get(linearize(c,r)).getElevation();
  }


  BufferedImage    getRawImage()              { return bimg;                                }
}

