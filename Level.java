import java.io.*;
import java.util.*;
import java.awt.Point;

public class Level {

  int               mapWd;
  int               mapHt;
  ArrayList<Actor>  actor = new ArrayList<Actor>();
  ArrayList<Tile>   map   = new ArrayList<Tile>();
  int               maxElevation = 10;

  Point             actorOrigin = new Point(10,10);

  int               pIndex = 90;

  public Level() {
  }

  public Level(Boolean b) {
    if(b) {
      mapWd = 10;
      mapHt = 10;
      initialize(mapWd,mapHt);
      addPlayer((int)actorOrigin.getX(),(int)actorOrigin.getY());
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

    //flatten up from the bottom as welll to eliminate 1x1 holes?
    makeBlobs(5);
    //smoothMap(1);
    //compressMap(3);
    //chopMap(1);
    //updateCollides();

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
      int rmX = (int)(Math.random()*(0.9*mapWd));
      int rmY = (int)(Math.random()*(0.9*mapHt));
      createBlob(rmX,rmY);
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

  ArrayList<Actor> getActors()             { return actor;                     }
  Actor            getActor(int a)         { return actor.get(a);              }
  ArrayList<Tile>  getMap()                { return map;                       }
  int              getW()                  { return mapWd;                     }
  int              getH()                  { return mapHt;                     }
  Tile             getTile(int t)          { return map.get(t);                }
  Tile             getTile(int c, int r)   { return map.get(linearize(c,r));   }
  int              getActorCount()         { return actor.size();              }
  int              getElevation(int t)        { return map.get(t).getElevation();    }
  int              getElevation(int c, int r) { return getTile(c,r).getElevation();  }
  void             removeActor(Actor a)    { actor.remove(a);                  }
  void             addPlayer(int x, int y) { actor.add(new Actor(x,y,pIndex)); }
  int              linearize(int x, int y) { return((y*mapWd)+x);              }
  boolean          collides(int c, int r)  { return(getTile(c,r).collides());  }

  void setTile(int x, int y, int i, boolean c, boolean r) {
    int myIndex = linearize(x,y);
    map.get(myIndex).setElevation(i);
    map.get(myIndex).setCollide(c);
    map.get(myIndex).setRaised(r);
  }

  void createBlob(int x, int y) {
    int blobWd = (int)(Math.random()*5)+2;
    int blobHt = (int)(Math.random()*5)+2;
    x=(x+blobWd)>mapWd?mapWd-x-1:x;
    y=(y+blobHt)>mapHt?mapHt-y-1:y;

    for(int r=y;r<y+blobHt;r++) {
      for(int c=x;c<x+blobWd;c++) {
        int theI = (int)Math.floor(Math.random()*maxElevation);
        map.set(r*mapWd+c,new Tile(c,r,false,false,1));
      }
    }
  }

}
