import java.awt.Point;

class Tile extends Point {
  int      index     = -1;
  boolean  collide   = false;
  boolean  raised    = false;  // true = actors move behind object

  public Tile(int x, int y, int i, boolean c, boolean r) {
    setLocation(x, y);
    index   = i;
    collide = c;
    raised  = r;
  }

  public Tile(Point p, int i, boolean c, boolean r) {
    setLocation(p);
    index   = i;
    collide = c;
    raised  = r;
  }

  public Tile(Tile t) { 
    index   = t.getIndex();
    collide = t.collides();
    raised  = t.isRaised();
  }

  int     getIndex()   { return(index);   }
  boolean collides()   { return(collide); }
  boolean isRaised()   { return(raised);  }

  void    setIndex(int i)       { index   = i; }
  void    setCollide(boolean c) { collide = c; }
  void    setRaised(boolean r)  { raised  = r; }

}

