import java.io.*;
import java.util.Properties;
import javax.xml.bind.DatatypeConverter;
import java.util.*;

public class Level implements Serializable {
  byte[]     _map;
  int        _W;
  int        _H;
  int[]     _walls;
  Properties theIni = new Properties();
  ArrayList<Actor> actor = new ArrayList<Actor>();

  public Level(String fname) throws IOException {
    _map = readLevel(fname);
  }

  public byte[] readLevel(String fname) throws IOException {
    theIni.loadFromXML(new FileInputStream(fname));
    
    writeLevel("filename.dat");
    byte[] b = DatatypeConverter.parseBase64Binary(theIni.getProperty("data"));
    System.out.println(b);

    // stupid kludge - tiled map decodes to ints rather than bytes
    // can probably remove with integrated map editor
    byte[] m = new byte[b.length/4];
    for(int n=0;n<b.length;n+=4) { m[n/4] = b[n]; }
    // -------------
    _W=Integer.parseInt(theIni.getProperty("width"));
    _H=Integer.parseInt(theIni.getProperty("height"));
    String[] commaDelimited = theIni.getProperty("walls").split(",");
    _walls = new int[commaDelimited.length];
    for(int n=0;n<_walls.length;n++) {
      _walls[n] = Integer.parseInt(commaDelimited[n]);
    }
    Arrays.sort(_walls);
    theIni.remove("data");
    theIni.remove("width");
    theIni.remove("height");
    theIni.remove("walls");
    return(m);
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

  int[]  getWalls()  { return(_walls);  }
  byte   getB(int n) { return(_map[n]); }
  int    getW()      { return(_W);      }
  int    getH()      { return(_H);      }
  byte[] getMap()    { return(_map);    }

}
