import java.awt.Point;
import java.util.ArrayList;

class Actor extends Point {
  String   _name;
  int      _index;                // tile image index
  int      _vradius     = 4;
  int      _pdest       = 0;      // actor id of portal destination
  int      _hp          = 100;    // hit points
  int      _sp          = 100;    // special points
  int      _ai          = 0;      // ai type
  enum     interaction { NONE, COLLIDE, PUSH, PULL, TALK, HIT, PORT }
  enum     facing      {N,S,E,W}
  interaction _interaction = interaction.NONE;

  ArrayList<Integer> _inventory = new ArrayList<Integer>();

  // portal constructor
  public Actor(int x, int y, int t, int d) {  
    setLocation((double)x, (double)y);
    _index = t;
    _pdest = d;
    _interaction=interaction.PORT;
  }

  // normal constructor
  public Actor(int x, int y, int index, interaction i) {
    setLocation((double)x, (double)y);
    _index = index;
    _interaction=i;
  }

  public Actor(String[] args) {
    _name=args[0];
    setLocation(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
    _index=Integer.parseInt(args[3]);
    _interaction=interaction.valueOf(args[4]);
    _pdest=Integer.parseInt(args[5]);
  }

  interaction getInteraction() { return(this._interaction); }

  boolean hasInteraction() { return(this._interaction!=interaction.NONE); }
  boolean canSee(Point p)  { return(this.distance(p)<=_vradius); }
  int     getI()           { return(_index);      }
  boolean isPushable()     { return(this._interaction==interaction.PUSH); }
  boolean isPortal()       { return(_pdest>0);    }
  int     getDest()        { return(_pdest);      }
}

