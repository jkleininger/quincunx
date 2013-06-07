import java.awt.Point;

class Tile extends Point {
  boolean  collide   = false;
  boolean  raised    = false;  // true = actors move behind object
  int      height    = 0;      // -1 = bottomless pit

  public Tile(int x, int y, boolean c, boolean r, int h) {
    setLocation(x, y);
    collide = c;
    raised  = r;
    height  = h;
  }

  public Tile(Point p, boolean c, boolean r, int h) {
    setLocation(p);
    collide = c;
    raised  = r;
    height  = h;
  }

  public Tile(Tile t) { 
    collide = t.collides();
    raised  = t.isRaised();
    height  = t.getHeight();
  }

  boolean collides()   { return(collide); }
  boolean isRaised()   { return(raised);  }
  int     getHeight()  { return(height);  }

  void    setCollide(boolean c) { collide = c; }
  void    setRaised(boolean r)  { raised  = r; }
  void    setHeight(int h)      { height  = h; }

}

