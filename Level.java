import java.io.*;
import java.util.*;
import java.awt.Point;

public class Level {

  int               mapWd;
  int               mapHt;
  ArrayList<Actor>  actor = new ArrayList<Actor>();
  ArrayList<Tile>   map   = new ArrayList<Tile>();
  int               mapHeight = 10;

  Point             actorOrigin = new Point(10,10);

  int               pIndex = 90;

  public Level() {
  }

  public Level(Boolean b) {
    if(b) {
      mapWd = 100;
      mapHt = 100;
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
        map.add(new Tile(c,r,false,false,0));
      }
    }

    makeBlobs(40);
    smoothMap(1);
    compressMap(3);

  }

  void chopMap(int chopHeight) {
    for(int r=0;r<mapHt;r++) {
      for(int c=0;c<mapWd;c++) {
        if(getHeight(c,r) > chopHeight) {
          map.set(linearize(c,r), new Tile(c,r,false,false,chopHeight) );
        }
      }
    }
  }

  void compressMap(int levels) {
    float factor = (mapHeight+1) / levels;
    for(int r=0;r<mapHt;r++) {
      for(int c=0;c<mapWd;c++) {
        int newHeight = (int)(Math.ceil( getHeight(c,r) / factor ));
        map.set(linearize(c,r), new Tile(c,r,false,false,newHeight ));
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
          map.set(linearize(c,r),new Tile (c,r,false,false,smoothPoint(c,r)));
        }
      }
    } 
  }

  int smoothPoint(int x, int y) {
    float corners = ( getHeight(x-1,y-1) + getHeight(x+1,y-1) + getHeight(x-1,y+1) + getHeight(x+1,y+1) ) / 16;
    float sides   = ( getHeight(x-1,y)   + getHeight(x+1,y)   + getHeight(x,y-1)   + getHeight(x,y+1)   ) / 8;
    float center  = ( getHeight(x,y) ) / 4 ;

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
  int              getHeight(int t)        { return map.get(t).getHeight();    }
  int              getHeight(int c, int r) { return getTile(c,r).getHeight();  }
  void             removeActor(Actor a)    { actor.remove(a);                  }
  void             addPlayer(int x, int y) { actor.add(new Actor(x,y,pIndex)); }
  int              linearize(int x, int y) { return((y*mapWd)+x);              }

  void setTile(int x, int y, int i, boolean c, boolean r) {
    int myIndex = linearize(x,y);
    map.get(myIndex).setHeight(i);
    map.get(myIndex).setCollide(c);
    map.get(myIndex).setRaised(r);
  }

  void createBlob(int x, int y) {
    int blobWd = (int)(Math.random()*12)+8;
    int blobHt = (int)(Math.random()*12)+8;
    x=(x+blobWd)>mapWd?mapWd-x-1:x;
    y=(y+blobHt)>mapHt?mapHt-y-1:y;

    for(int r=y;r<y+blobHt;r++) {
      for(int c=x;c<x+blobWd;c++) {
        int theI = (int)Math.floor(Math.random()*mapHeight);
        map.set(r*mapWd+c,new Tile(c,r,false,false,theI));
      }
    }
  }

}
