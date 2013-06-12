import java.awt.Point;
import java.util.ArrayList;

class Actor extends Point {
  String      _name;
  int         _index;                // tile image index
  int         _vradius     = 4;
  int         _pdest       = 0;      // actor id of portal destination
  int         _hp          = 100;    // hit points
  int         _sp          = 100;    // special points
  int         _ai          = 0;      // ai type
  interaction _interaction = interaction.NONE;
  enum        interaction   { NONE, COLLIDE, PUSH, PULL, TALK, HIT, PORT }
  enum        facing        { N, S, E, W }
  int         xDest        = 0;
  int         yDest        = 0;
  int         actionDelay  = 3;
  int         actionTicker = 3;

  /* ***** AI Types *********
    0  do nothing
    1  move toward player
    2  move toward (xDest,yDest)
    3  move random
   ************************ */

  ArrayList<Integer> _inventory = new ArrayList<Integer>();

  // portal constructor
  public Actor(int x, int y, int t, int d) {
    setLocation(x,y);
    _index = t;
    _pdest = d;
    _interaction=interaction.PORT;
  }

  // normal xy constructor
  public Actor(int x, int y, int index, interaction i) {
    setLocation(x,y);
    _index = index;
    _interaction=i;
    _ai = 1;
  }

  // normal Point constructor
  public Actor(Point p, int index, interaction i) {
    setLocation(p);
    _index = index;
    _interaction=i;
    _ai = 1;
  }

  // add player xy
  public Actor(int x, int y, int i) {
    _name = "player";
    setLocation(x,y);
    _index = i;
  }

  // add player Point
  public Actor(Point p, int i) {
    _name = "player";
    setLocation(p);
    _index = i;
  }

  interaction getInteraction() { return(this._interaction); }

  boolean hasInteraction() { return(this._interaction!=interaction.NONE); }
  boolean canSee(Point p)  { return(this.distance(p)<=_vradius); }
  int     getI()           { return(_index);      }
  boolean isPushable()     { return(this._interaction==interaction.PUSH); }
  boolean isPortal()       { return(_pdest>0);    }
  int     getDest()        { return(_pdest);      }
  String  getName()        { return(_name);       }
  int     getAI()          { return(_ai);         }
  void    setAI(int ai)    { _ai = ai;            }

  boolean canAct()         {
    System.out.println("actionTicker: " + actionTicker);
    if(actionTicker-- <= 0) {
      actionTicker = actionDelay;
      return(true);
    } else {
      return(false);
    }
  }

}

