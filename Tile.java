import java.awt.Point;

class Tile extends Point {
  int      index     = -1;
  boolean  collide   = false;
  boolean  raised    = false;  // true = actors move behind object
  int      height    = 0;      // -1 = bottomless pit

  public Tile(int x, int y, int i, boolean c, boolean r, int h) {
    setLocation(x, y);
    index   = i;
    collide = c;
    raised  = r;
    height  = h;
  }

  public Tile(Point p, int i, boolean c, boolean r, int h) {
    setLocation(p);
    index   = i;
    collide = c;
    raised  = r;
    height  = h;
  }

  public Tile(Tile t) { 
    index   = t.getIndex();
    collide = t.collides();
    raised  = t.isRaised();
  }

  int     getIndex()   { return(index);   }
  boolean collides()   { return(collide); }
  boolean isRaised()   { return(raised);  }
  int     getHeight()  { return(height);  }

  void    setIndex(int i)       { index   = i; }
  void    setCollide(boolean c) { collide = c; }
  void    setRaised(boolean r)  { raised  = r; }
  void    setHeight(int h)      { height  = h; }

}

