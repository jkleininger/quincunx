import java.io.*;
import java.util.*;

public class Level implements Serializable {
  private static final long serialVersionUID = 1;

  int               mapWd;
  int               mapHt;
  ArrayList<Actor>  actor = new ArrayList<Actor>();
  ArrayList<Tile>   map   = new ArrayList<Tile>();

  public Level() throws IOException {
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
        map.add(new Tile(c,r,0,false,false));
      }
    }
  }

  ArrayList<Actor> getActors()     { return actor;                  }
  Actor            getActor(int a) { return actor.get(a);           }
  ArrayList<Tile>  getMap()        { return map;                    }
  int              getW()          { return(mapWd);                 }
  int              getH()          { return(mapHt);                 }
  Tile             getTile(int t)  { return map.get(t);             }
  int              getI(int t)     { return map.get(t).getIndex();  }
  int              getActorCount() { return actor.size();           }

  void setTile(int x, int y, int i, boolean c, boolean r) {
    int myIndex = linearize(x,y);
    map.get(myIndex).setIndex(i);
    map.get(myIndex).setCollide(c);
    map.get(myIndex).setRaised(r);
  }

  void addPlayer(int x, int y) {
    actor.add(new Actor(x,y));
  }

  int linearize(int x, int y) { return((y*mapWd)+x); }

}
