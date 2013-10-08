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
  int               maxElevation = 9;

  int               pIndex = 90;

  BufferedImage     bimg;
  Graphics2D        theG;

  int               cellIterations = 5;

  int cDead  = new Color(0,0,0).getRGB();
  int cAlive = new Color(128,128,128).getRGB();

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
    bimg = new BufferedImage(mapWd,mapHt,BufferedImage.TYPE_INT_RGB);
    theG = bimg.createGraphics();

    genPixelMap();

    map.clear();
    map.ensureCapacity(w*h);
    actor.clear();
    mapWd = w;
    mapHt = h;
    int r,c;
    for(r=0;r<h;r++) {
      for(c=0;c<w;c++) {
        map.add(new Tile(c,r,true,false,0));
      }
    }

    makeConnectedBlobs(40);
    smoothMap(1);
    compressMap(4);
    onebitMap();
    updateCollides();
    addPlayer(findOccupyablePoint());
    initMobs(10); 
    initCrates(20);
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
    Point p = new Point();
    do {
      p.setLocation((int)(Math.random()*mapWd),(int)(Math.random()*mapHt));
    } while(collides((int)p.getX(),(int)p.getY()));
    return p;
  }

  void onebitMap() {
    for(int r=0;r<mapHt;r++) {
      for(int c=0;c<mapWd;c++) {
        if(getElevation(c,r)>0) { getTile(c,r).setElevation(1); }
      }
    }
  }

  void chopMap(int chopElevation) {
    for(int r=0;r<mapHt;r++) {
      for(int c=0;c<mapWd;c++) {
        if(getElevation(c,r) > chopElevation) {
          map.get(linearize(c,r)).setElevation(chopElevation);
        }
      }
    }
  }

  void compressMap(int levels) {
    float factor = (maxElevation+1) / levels;
    for(int r=0;r<mapHt;r++) {
      for(int c=0;c<mapWd;c++) {
        int newElevation = (int)(Math.ceil( getElevation(c,r) / factor ));
        map.get(linearize(c,r)).setElevation(newElevation);
      }
    }
    chopMap(levels-1);
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
    int steps = 3;
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
        cx += stepX;
        cy += stepY;
        createBlob((int)cx,(int)cy);
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
    int blobWd = (int)(Math.random()*(mapWd/10))+3;
    int blobHt = (int)(Math.random()*(mapHt/10))+3;
    x=(x+blobWd)>mapWd?mapWd-x-1:x;
    y=(y+blobHt)>mapHt?mapHt-y-1:y;

    for(int r=y;r<y+blobHt;r++) {
      for(int c=x;c<x+blobWd;c++) {
        theI = (int)Math.floor(Math.random()*maxElevation);
        getTile(c,r).setElevation(theI);
      }
    }

  }

  void smoothMap(int iterations) {
    for(int i=0;i<iterations;i++) {
      for(int r=1;r<mapHt-1;r++) {
        for(int c=1;c<mapWd-1;c++) {
          map.get(linearize(c,r)).setElevation(smoothPoint(c,r));
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

  int smoothPoint(int x, int y) {
    float corners = ( getElevation(x-1,y-1) + getElevation(x+1,y-1) + getElevation(x-1,y+1) + getElevation(x+1,y+1) ) / 16;
    float sides   = ( getElevation(x-1,y)   + getElevation(x+1,y)   + getElevation(x,y-1)   + getElevation(x,y+1)   ) / 8;
    float center  = ( getElevation(x,y) ) / 4 ;

    return((int)(corners + sides + center));
  }

  void genPixelMap() {
    double prob = .3;
    
    for(int c=0 ; c<mapWd ; c++) {
      for(int r=0 ; r<mapHt ; r++) {
        if(Math.random() < prob) { 
          bimg.setRGB(c,r,cAlive);
        }
      }
    }

    bimg = iterateCells(bimg);
    bimg = iterateCells(bimg);
    bimg = iterateCells(bimg);
    bimg = iterateCells(bimg);
    bimg = iterateCells(bimg);
    bimg = iterateCells(bimg);
    bimg = iterateCells(bimg);
    bimg = iterateCells(bimg);

  }

  BufferedImage iterateCells(BufferedImage inImg) {
    BufferedImage outImg = new BufferedImage(mapWd,mapHt,BufferedImage.TYPE_INT_RGB);
    for(int c=1 ; c<(mapWd-1) ; c++) {
      for(int r=1 ; r<(mapHt-1) ; r++) {
        int neighbors = countNeighbors(bimg,c,r);
        if(inImg.getRGB(c,r)!=cDead) {
          outImg.setRGB(c,r,judge(neighbors));
        } else {
          if(neighbors==3) outImg.setRGB(c,r,cAlive);
        }
      }
    }
    return outImg;
  }



  int countNeighbors(BufferedImage b, int x, int y) {
    int[] n = new int[9];
    n[0] = b.getRGB(x-1,y-1) == cAlive ? 1 : 0;
    n[1] = b.getRGB(x,  y-1) == cAlive ? 1 : 0;
    n[2] = b.getRGB(x+1,y-1) == cAlive ? 1 : 0;
    n[3] = b.getRGB(x-1,y)   == cAlive ? 1 : 0;
    n[5] = b.getRGB(x+1,y)   == cAlive ? 1 : 0;
    n[6] = b.getRGB(x-1,y+1) == cAlive ? 1 : 0;
    n[7] = b.getRGB(x,  y+1) == cAlive ? 1 : 0;
    n[8] = b.getRGB(x+1,y+1) == cAlive ? 1 : 0;

    n[4] = n[0]+n[1]+n[2]+n[3]+n[5]+n[6]+n[7]+n[8];
    System.out.print(n[4] + " *** ");

    return n[4];
  }

  int judge(int neighbors) {
    switch(neighbors) {
      case 0:
        return cDead;
      case 1:
        return cDead;
      case 2:
        return cAlive;
      case 3:
        return cAlive;
      case 4:
        return cDead;
      case 5:
        return cDead;
      case 6:
        return cDead;
      case 7:
        return cDead;
      case 8:
        return cDead;
      default:
        break;
    }
    return(new Color(255,255,255).getRGB());
  }



  // this section on hold, was just a thought
  //Polygon makePolyRoom(int w, int h) {
  //  Polygon thePoly = new Polygon();
  //  thePoly.addPoint(0,0);
  //  thePoly.addPoint(w,0);
  //  thePoly.addPoint(w,h);
  //  thePoly.addPoint(0,h);
  //  return thePoly;
  //}
  //
  //void addShapeToMap(Polygon thePoly) {
  //  for(int c=0 ; c<mapWd ; c++) {
  //    for(int r=0 ; r<mapHt ; r++) {
  //      if(thePoly.contains(c,r) getTile(c,r).setElevation(1);
  //    }
  //  }
  //}
  // end just a thought

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

  BufferedImage    getRawImage()              { return bimg;                                }
}

