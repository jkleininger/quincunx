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
