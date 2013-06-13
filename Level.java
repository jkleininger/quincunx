import java.io.*;
import java.util.*;
import java.awt.Point;

public class Level {

  int               mapWd;
  int               mapHt;
  ArrayList<Actor>  actor = new ArrayList<Actor>();
  ArrayList<Tile>   map   = new ArrayList<Tile>();
  int               maxElevation = 9;

  int               pIndex = 90;

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
    int r,c;
    for(r=0;r<h;r++) {
      for(c=0;c<w;c++) {
        map.add(new Tile(c,r,true,false,0));
      }
    }

    makeConnectedBlobs(30);
    smoothMap(1);
    compressMap(4);
    //onebitMap();
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
    }
  }

  // need to check for existing actor
  Point findOccupyablePoint() {
    Point p = new Point();
    do {
      p.setLocation((int)(Math.random()*mapWd),(int)(Math.random()*mapHt));
      System.out.println(p.toString());
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
    int steps = 40;
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
      for(int cs=0;cs<steps;cs++) {
        double cx = x0 + (stepX * cs);
        double cy = y0 + (stepY * cs);
        createBlob((int)cx,(int)cy);
      }
    }
  }

  void makeConnectedRooms(int r) {
    Point[] thePoints = new Point[r];
    for(int p=0;p<r;p++) {
      int cX = (int)(Math.random()*mapWd);
      int cY = (int)(Math.random()*mapHt);
    }
  }

  void createBlob(int x, int y) {
    int blobWd = (int)(Math.random()*(mapWd/10))+3;
    int blobHt = (int)(Math.random()*(mapHt/10))+3;
    x=(x+blobWd)>mapWd?mapWd-x-1:x;
    y=(y+blobHt)>mapHt?mapHt-y-1:y;

    for(int r=y;r<y+blobHt;r++) {
      for(int c=x;c<x+blobWd;c++) {
        int theI = (int)Math.floor(Math.random()*maxElevation);
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
  boolean          collides(int c, int r)     { return(getTile(c,r).collides());            }





}
