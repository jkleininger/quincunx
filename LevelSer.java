import java.io.*;
import java.util.*;

public class LevelSer implements Serializable {
  byte[]            _map;
  int               _W;
  int               _H;
  int[]             _walls;
  ArrayList<Actor>  actor = new ArrayList<Actor>();

  public LevelSer() throws IOException {
  }

  public LevelSer(Level theLevel) {
    _map   = theLevel.getMap();
    _W     = theLevel.getW();
    _H     = theLevel.getH();
    _walls = theLevel.getWalls();
    actor  = theLevel.getActors();
  }

  public LevelSer(LevelSer theLevel) {
    _map   = theLevel.getMap();
    _W     = theLevel.getW();
    _H     = theLevel.getH();
    _walls = theLevel.getWalls();
    actor  = theLevel.getActors();
  }

/*
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public ArrayList<Actor> getActors() {
    ArrayList<Actor> a = new ArrayList<Actor>();
    Enumeration      e = theIni.propertyNames();
    Vector           v = new Vector();
    while(e.hasMoreElements()) { v.add(e.nextElement()); }
    Collections.sort(v);
    for(int i=0;i<v.size();i++) {
      String[] commaDelimited = theIni.getProperty(v.get(i).toString()).split(",");
      a.add(new Actor(commaDelimited));
    }
    return(a);
  }
*/

  ArrayList<Actor> getActors() {
    return (ArrayList<Actor>)actor;
  }

  int[] getWalls()  { return(_walls);  }
  byte  getB(int n) { return(_map[n]); }
  int   getW()      { return(_W);      }
  int   getH()      { return(_H);      }
  byte[] getMap()   { return(_map);    }

  public void showInfo() {
    System.out.println("Actors: " + actor.size());
    System.out.println("Map Length: " + _map.length);
  }



}
