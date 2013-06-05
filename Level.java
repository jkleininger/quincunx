import java.io.*;
import java.util.*;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;

public class Level implements Serializable {
  private static final long serialVersionUID = 1;

  int               mapWd;
  int               mapHt;
  ArrayList<Actor>  actor = new ArrayList<Actor>();
  ArrayList<Tile>   map   = new ArrayList<Tile>();

  public Level() {
  }

  public Level(Boolean b) {
    if(b) {
      mapWd = 100;
      mapHt = 100;
      initialize(100,100);
      addPlayer(10,10,89);
    }
  }

  public Level(Level theLevel) {
    mapWd  = theLevel.getW();
    mapHt  = theLevel.getH();
    actor  = theLevel.getActors();
    map    = theLevel.getMap();
  }

  void initialize(int w, int h) {
    int theI;
    map.clear();
    map.ensureCapacity(w*h);
    actor.clear();
    mapWd = w;
    mapHt = h;
    int r,c;
    for(r=0;r<h;r++) {
      for(c=0;c<w;c++) {
        theI = (int)(Math.random() * 3);
        map.add(new Tile(c,r,theI,false,false));
      }
    }
  }

  ArrayList<Actor> getActors()      { return actor;                  }
  Actor            getActor(int a)  { return actor.get(a);           }
  ArrayList<Tile>  getMap()         { return map;                    }
  int              getW()           { return mapWd;                  }
  int              getH()           { return mapHt;                  }
  Tile             getTile(int t)   { return map.get(t);             }
  int              getI(int t)      { return map.get(t).getIndex();  }
  int              getActorCount()  { return actor.size();           }

  void             removeActor(Actor a)    { System.out.println("Removing " + a.getName()); actor.remove(a); }

  void setTile(int x, int y, int i, boolean c, boolean r) {
    int myIndex = linearize(x,y);
    map.get(myIndex).setIndex(i);
    map.get(myIndex).setCollide(c);
    map.get(myIndex).setRaised(r);
  }

  void addPlayer(int x, int y, int i) {
    actor.add(new Actor(x,y,i));
  }

  int linearize(int x, int y) { return((y*mapWd)+x); }

}
