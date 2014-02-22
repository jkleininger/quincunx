import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.awt.*;

class TileSheet {
  BufferedImage _B;
  int     _ROWS;
  int     _COLS;
  int     _TILEWD;
  int     _TILEHT;
  int     _W;
  int     _H;
  byte[]  _walls;
  byte[]  _ports;

  public TileSheet(String fname, int cols, int rows) throws IOException {
    _B      = ImageIO.read(new File(fname));
    _ROWS   = rows;
    _COLS   = cols;
    _W      = _B.getWidth();
    _H      = _B.getHeight();
    _TILEWD = _W / _COLS;
    _TILEHT = _H / _ROWS;
  }

  public BufferedImage getI() {
    return(_B);
  }

  BufferedImage getTile(int n) {
    int tx = (n % _COLS) * _TILEWD;
    int ty = (n / _COLS) * _TILEHT;
    return(_B.getSubimage(tx, ty, _TILEWD, _TILEHT));
  }

  void drawTile(Graphics g, int t, int x, int y, ImageObserver IO) {
    x = x*_TILEWD;
    y = y*_TILEHT;
    g.drawImage(this.getTile(t),x,y,IO);
  }

  void drawFog(Graphics g, int x, int y) {
    g.setColor(new Color(0,0,0,128));
    x = x*_TILEWD;
    y = y*_TILEHT;
    g.fillRect(x, y, _TILEWD, _TILEHT);
  }

  Dimension getTileSize() {
    return new Dimension(_TILEWD, _TILEHT);
  }

  byte[] getPorts() { return(_ports); }

  int getTileCount() { return(_ROWS*_COLS); }

}
