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
  int               maxElevation = 3;

  int               pIndex = 90;

  BufferedImage     bimg;
  Graphics2D        theG;

  int               cellIterations = 6;

  int cWall  = new Color(0,0,0).getRGB();
  int cFloor = new Color(128,128,128).getRGB();
  
  int[] cRules = { cWall,
                   cWall,
                   cWall,
                   cFloor,
                   cFloor,
                   cFloor,
                   cFloor,
                   cFloor,
                   cFloor
                 };

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

    map.clear();
    map.ensureCapacity(w*h);
    actor.clear();
    mapWd = w;
    mapHt = h;

    genPixelMap();
    //bimg = smoothImg(bimg,1);

    getTilesFromImg(bimg);


    //makeConnectedBlobs(40);
    //compressMap(4);
    //onebitMap();
    //updateCollides();
    addPlayer(findOccupyablePoint());
    //initMobs(10); 
    //initCrates(20);
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

  BufferedImage smoothImg(BufferedImage inImg, int iterations) {
    BufferedImage outImg = new BufferedImage(mapWd,mapHt,BufferedImage.TYPE_INT_RGB);
    for(int i=0;i<iterations;i++) {
      for(int r=1;r<mapHt-1;r++) {
        for(int c=1;c<mapWd-1;c++) {
          int smoothed = smoothPoint(inImg,c,r);
          outImg.setRGB(c,r,smoothed);
          //map.get(linearize(c,r)).setElevation(smoothPoint(c,r));
        }
      }
    } 
    return outImg;
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

  int smoothPoint(BufferedImage b, int x, int y) {
    float corners = ( b.getRGB(x-1,y-1) + b.getRGB(x-1,y+1) + b.getRGB(x+1,y-1) + b.getRGB(x+1,y+1) ) / 16;
    float sides   = ( b.getRGB(x-1,y)   + b.getRGB(x+1,y)   + b.getRGB(x,y-1)   + b.getRGB(x,y+1)   ) / 8;
    float center  = ( b.getRGB(x,y) ) / 4;
    //float corners = ( getElevation(x-1,y-1) + getElevation(x+1,y-1) + getElevation(x-1,y+1) + getElevation(x+1,y+1) ) / 16;
    //float sides   = ( getElevation(x-1,y)   + getElevation(x+1,y)   + getElevation(x,y-1)   + getElevation(x,y+1)   ) / 8;
    //float center  = ( getElevation(x,y) ) / 4 ;

    return((int)(corners + sides + center));
  }

  void genPixelMap() {
    double prob = .5;
    
    for(int c=0 ; c<mapWd ; c++) {
      for(int r=0 ; r<mapHt ; r++) {
        if(Math.random() < prob) { 
          bimg.setRGB(c,r,cFloor);
        }
      }
    }

    for(int i=0 ; i<cellIterations ; i++) { 
      bimg = iterateCells(bimg);
    }
  }

  BufferedImage iterateCells(BufferedImage inImg) {
    BufferedImage outImg = new BufferedImage(mapWd,mapHt,BufferedImage.TYPE_INT_RGB);
    int outTile = 0;
    for(int c=1 ; c<(mapWd-1) ; c++) {
      for(int r=1 ; r<(mapHt-1) ; r++) {
        int neighbors = countNeighbors(bimg,c,r);
        int tiletype = inImg.getRGB(c,r);

        if(tiletype==cWall) {
          if(neighbors>=4) { outTile = cWall; } else { outTile = cFloor; };
        } else if(tiletype==cFloor) {
          if(neighbors>=5) { outTile = cWall; } else { outTile = cFloor; };
        }
        outImg.setRGB(c,r,outTile);

      }
    }
    return outImg;
  }

  int countNeighbors(BufferedImage b, int x, int y) {
    int   num = 0;
    int[] n   = new int[9];

    n[0] = b.getRGB(x-1,y-1) == cWall ? 1 : 0;
    n[1] = b.getRGB(x,  y-1) == cWall ? 1 : 0;
    n[2] = b.getRGB(x+1,y-1) == cWall ? 1 : 0;
    n[3] = b.getRGB(x-1,y)   == cWall ? 1 : 0;
    n[4] = b.getRGB(x,  y);
    n[5] = b.getRGB(x+1,y)   == cWall ? 1 : 0;
    n[6] = b.getRGB(x-1,y+1) == cWall ? 1 : 0;
    n[7] = b.getRGB(x,  y+1) == cWall ? 1 : 0;
    n[8] = b.getRGB(x+1,y+1) == cWall ? 1 : 0;

    num = n[0]+n[1]+n[2]+n[3]+n[5]+n[6]+n[7]+n[8];

    return num;
  }

  int judge(int neighbors, int rule) {
    if(neighbors<=rule) { return cWall; } else { return cFloor; }
  }

  void getTilesFromImg(BufferedImage b) {
    int thisElevation = 0;
    for(int r=0 ; r<(mapHt) ; r++) {
      for(int c=0 ; c<(mapWd) ; c++) {
        int i = b.getRGB(c,r);
        if(i==cWall) {
          map.add(new Tile(c,r,true,false,0));
        } else if(i==cFloor) {
          map.add(new Tile(c,r,false,false,1));
        }
      }
    }
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

  BufferedImage    getRawImage()              { return bimg;                                }
}

