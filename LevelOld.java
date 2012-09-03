import java.io.*;
import java.util.Properties;
import javax.xml.bind.DatatypeConverter;
import java.util.*;

public class LevelOld implements Serializable {
  int        _W;
  int        _H;
  int[]      _walls;
  Properties       theIni = new Properties();
  ArrayList<Actor> actor  = new ArrayList<Actor>();
  ArrayList<Tile>  tile   = new ArrayList<Tile>();

  public LevelOld(String fname) throws IOException {
    //_W=0;
    //_H=0;
    readLevel(fname);
  }

  public void readLevel(String fname) throws IOException {
    theIni.loadFromXML(new FileInputStream(fname));

    _W=Integer.parseInt(theIni.getProperty("width"));
    _H=Integer.parseInt(theIni.getProperty("height"));
    String[] commaDelimited = theIni.getProperty("walls").split(",");
    _walls = new int[commaDelimited.length];
    for(int n=0;n<_walls.length;n++) {
      _walls[n] = Integer.parseInt(commaDelimited[n]);
    }
    Arrays.sort(_walls);

    byte[] b = DatatypeConverter.parseBase64Binary(theIni.getProperty("data"));

    int myX, myY, myI;
    boolean myC = false;
    boolean myR = false;
    for(int n=0;n<b.length/4;n++) {
      myX = (int)n%_W;
      myY = (int)n/_W;
      myI = (int)b[n*4];
      myC = Arrays.binarySearch(_walls,myI)>=0?true:false;
      tile.add(new Tile(myX,myY,myI,myC,myR));
    }
    // -------------

    theIni.remove("data");
    theIni.remove("width");
    theIni.remove("height");
    theIni.remove("walls");
  }

  public void writeLevel(String fname) throws IOException {
    theIni.storeToXML(new FileOutputStream(fname),"this is the name of the level");
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public ArrayList<Actor> getActors() {
    Enumeration      e = theIni.propertyNames();
    Vector           v = new Vector();
    while(e.hasMoreElements()) { v.add(e.nextElement()); }
    Collections.sort(v);
    for(int i=0;i<v.size();i++) {
      String[] commaDelimited = theIni.getProperty(v.get(i).toString()).split(",");
      actor.add(new Actor(commaDelimited));
    }
    return(actor);
  }

  int[]  getWalls()     { return(_walls);      }
  int    getI(int n)    { return(tile.get(n).getIndex()); }
  int    getW()         { return(_W);          }
  int    getH()         { return(_H);          }
  Tile   getTile(int t) { return(tile.get(t)); }

  ArrayList<Tile> getTiles() { return(tile); }

}
