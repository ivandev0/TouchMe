package source;

import javax.swing.*;
import java.awt.*;

public class GameObject extends JPanel {
    public float x,y;//координаты объекта
    public int width, height;//ширина и высота объекта
    public Image obImg;//отрисовываемое изображение
    public BoxCollider collider;//коллайдер объекта

    public GameObject(float x, float y, float colX, float colY, int colW, int colH){
        this.x = x;
        this.y = y;

        collider = new BoxCollider(colX, colY, colW, colH);
    }

    public GameObject(float x, float y, Image obImg){
        this.x = x;
        this.y = y;

        this.obImg = obImg;

        collider = new BoxCollider(0, 0, 0, 0);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.drawImage(obImg, (int)x,(int)y, width, height, null);
        //for debug
        /*g.setColor(Color.GREEN);
        g.drawRect((int)(collider.xCollider + x), (int)(collider.yCollider + y), collider.widthCollider, collider.heightCollider);*/
    }

    public class BoxCollider {

        float xCollider, yCollider;
        int widthCollider, heightCollider;

        public BoxCollider(float xCollider, float yCollider, int widthCollider, int heightCollider) {
            this.xCollider = xCollider;
            this.yCollider = yCollider;
            this.widthCollider = widthCollider;
            this.heightCollider = heightCollider;
        }

        boolean boxCompare(GameObject ob) {
            Rectangle rect = new Rectangle((int)(xCollider+x),(int)(yCollider+y), widthCollider, heightCollider);
            if(rect.contains(ob.x, ob.y))
                return true;
            /*if (x + xCollider < ob.x + ob.collider.xCollider + ob.collider.widthCollider &&
                    x + xCollider + widthCollider > ob.x + ob.collider.xCollider &&
                    y + yCollider < ob.y + ob.collider.yCollider + ob.collider.heightCollider &&
                    y + yCollider + heightCollider > ob.y + ob.collider.yCollider)
                return true;*/
            return false;
        }
    }
}