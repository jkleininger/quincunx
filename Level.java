import java.io.*;
import java.util.*;
import java.awt.Point;

public class Level {

  int               mapWd;
  int               mapHt;
  ArrayList<Actor>  actor = new ArrayList<Actor>();
  ArrayList<Tile>   map   = new ArrayList<Tile>();

  Point             actorOrigin = new Point(-1,-1);

  public Level() {
  }

  public Level(Boolean b) {
    if(b) {
      mapWd = 100;
      mapHt = 100;
      initialize(mapWd,mapHt);
      addPlayer((int)actorOrigin.getX(),(int)actorOrigin.getY(),91);
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
        map.add(new Tile(c,r,false,false,0));
      }
    }
    for(int n=0;n<40;n++) {
      int rmX = (int)(Math.random()*(0.9*mapWd));
      int rmY = (int)(Math.random()*(0.9*mapHt));
      createRoom(rmX,rmY);
    }
    for(r=1;r<h-1;r++) {
      for(c=1;c<w-1;c++) {
        map.set(linearize(c,r),new Tile (c,r,false,false,smoothPoint(c,r)));
      }
    } 
 }

  int smoothPoint(int x, int y) {
    float corners = ( getTile(x-1,y-1).getHeight() + getTile(x+1,y-1).getHeight() + getTile(x-1,y+1).getHeight() + getTile(x+1,y+1).getHeight() ) / 16;
    float sides   = ( getTile(x-1,y).getHeight()   + getTile(x+1,y).getHeight()   + getTile(x,y-1).getHeight()   + getTile(x,y+1).getHeight()   ) / 8;
    float center  = ( getTile(x,y).getHeight() ) / 4 ;

    return((int)(corners + sides + center));
  }

  ArrayList<Actor> getActors()           { return actor;                   }
  Actor            getActor(int a)       { return actor.get(a);            }
  ArrayList<Tile>  getMap()              { return map;                     }
  int              getW()                { return mapWd;                   }
  int              getH()                { return mapHt;                   }
  Tile             getTile(int t)        { return map.get(t);              }
  Tile             getTile(int c, int r) { return map.get(linearize(c,r)); }
  int              getActorCount()       { return actor.size();            }
  int              getHeight(int t)      { return map.get(t).getHeight();  }

  void             removeActor(Actor a)    { System.out.println("Removing " + a.getName()); actor.remove(a); }

  void setTile(int x, int y, int i, boolean c, boolean r) {
    int myIndex = linearize(x,y);
    map.get(myIndex).setHeight(i);
    map.get(myIndex).setCollide(c);
    map.get(myIndex).setRaised(r);
  }

  void addPlayer(int x, int y, int i) {
    actor.add(new Actor(x,y,i));
  }

  int linearize(int x, int y) { return((y*mapWd)+x); }

  void createRoom(int x, int y) {
    int roomWd = (int)(Math.random()*12)+8;
    int roomHt = (int)(Math.random()*12)+8;
    x=(x+roomWd)>mapWd?mapWd-x-1:x;
    y=(y+roomHt)>mapHt?mapHt-y-1:y;

    actorOrigin.setLocation( (int)((x+roomWd)/2) , (int)((y+roomHt)/2) );

    System.out.println("creating (" + roomWd + "x" + roomHt + ") room at (" + x + "," + y + ")");
    for(int r=y;r<y+roomHt;r++) {
      for(int c=x;c<x+roomWd;c++) {
        int theI = (int)(Math.random()*10);
        map.set(r*mapWd+c,new Tile(c,r,false,false,theI));
      }
    }
  }

}
