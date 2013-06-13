import java.awt.Point;

class Tile extends Point {
  boolean  collide    = false;
  boolean  raised     = false;  // true = actors move behind object
  int      elevation  = 0;
  int      brightness = 0;

  public Tile(int x, int y, boolean c, boolean r, int e) {
    setLocation(x, y);
    collide   = c;
    raised    = r;
    elevation = e;
  }

  public Tile(Point p, boolean c, boolean r, int e) {
    setLocation(p);
    collide    = c;
    raised     = r;
    elevation  = e;
  }

  public Tile(Tile t) { 
    collide    = t.collides();
    raised     = t.isRaised();
    elevation  = t.getElevation();
  }

  boolean collides()      { return(collide);   }
  boolean isRaised()      { return(raised);    }
  int     getElevation()  { return(elevation); }

  void    setCollide(boolean c) { collide = c;    }
  void    setRaised(boolean r)  { raised  = r;    }
  void    setElevation(int e)   { elevation  = e; }

}

