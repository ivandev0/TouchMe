package source;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;


public class Hero extends GameObject{

    private int maxX, maxY, minX, minY;//границы для объекта
    private float screenCoef = 0.25f; //масштаб
    private float borderCoef = 4;     //масштаб гранцы

    public float[] arX; //координаты Х сплайна
    private float[] arY;//координаты У сплайна

    public Spline spline; //обуект сплайна
    private boolean direct = true;//направление движения

    Hero(float x, float y, Image obImg){
        super(x, y, obImg);

        width = (int) (obImg.getWidth(null) * screenCoef);
        height = (int) (obImg.getHeight(null)  * screenCoef);
        collider = new BoxCollider(0,0,width,height);

        maxX = Game.WIDTH - width;
        maxY = Game.HEIGHT - height - 35;
        minX = 0;
        minY = 0;

        this.x = x == 0 ? (Game.WIDTH - width)*0.5f : x;
        this.y = y == 0 ? (Game.HEIGHT - height)*0.5f : y;

        direct = (int)(Math.random()*2) == 0;
        generateNewPoints(direct, this.x, this.y);
        spline = new Spline(arX, arY);
        if(direct)
            this.x = arX[0];
        else
            this.x = arX[arX.length - 1];
    }

    public void update(float delta){
        float speed = 1500; //1500
        if((x <= arX[arX.length - 1] || x < maxX) && direct) {
            x += delta * (speed*0.5f + Math.random()*speed*0.5f);
            y = spline.getValue(x);
        }
        if((x > arX[arX.length - 1] || x > maxX) && direct){
            direct = !direct;
            generateNewPoints(direct/*false*/,x,y);
            spline = new Spline(arX, arY);
        }
        if((x >= arX[0] || x > minX) && !direct){
            x -= delta * (speed*0.5f + Math.random()*speed*0.5f);
            y = spline.getValue(x);
        }
        if((x < arX[0] || x < minX) && !direct){
            direct = !direct;
            generateNewPoints(direct/*true*/,x,y);
            spline = new Spline(arX, arY);
        }

        if(y > maxY || y < minY){
            int probability = (int)(Math.random() * 2);
            direct = probability == 0;
            y = y > maxY ? maxY : minY;
            generateNewPoints(direct,x,y);
            spline = new Spline(arX, arY);
        }
    }

    public void updateOnBegin(float delta){
        float speed = 200;
        y = maxY * 0.7f;
        if(direct){
            x+=delta*speed;
            if(x > maxX*0.7f)
                direct = !direct;
        }
        else{
            x-=delta*speed;
            if(x < maxX*0.3f)
                direct = !direct;
        }
    }

    private void generateNewPoints(boolean direct, float lastX, float lastY){
        //System.out.println("new points" + i++);
        LinkedList<Float> arX = new LinkedList<>();
        LinkedList<Float> arY = new LinkedList<>();
        int minX = (int) (width*borderCoef); //100
        int maxX = 200; //200
        int minY = this.minY;
        int maxY = this.maxY;
        if(y < maxY*0.1f)
            maxY = (int) (maxY * 0.2f);
        if(y > maxY*0.9f)
            minY = (int) (maxY * 0.8f);
        if(direct) {
            arX.add(lastX);
            arY.add(lastY);
            int rand;
            int border = (int) Math.round(arX.getLast() + width*borderCoef) + (int) (Math.random()*(this.maxX - arX.getLast() - width*borderCoef));
            do {
                int rad = (minX + (int) (Math.random() * (maxX - minX)));
                rand = Math.round(arX.getLast()) + /*(int) (Math.random() * rad)*/rad;
                arX.add((float) rand);
                rad = (minY + (int) (Math.random() * (maxY - minY)));
                arY.add((float) rad);
                minY = this.minY;
                maxY = this.maxY;
            } while (rand < /*this.maxX*/border);
        } else {
            arX.add(lastX);
            arY.add(lastY);
            int rand;
            int border = (int) (Math.random()*(arX.getLast() - width*borderCoef));
            do {
                int rad = (minX + (int) (Math.random() * (maxX - minX)));
                rand = Math.round(arX.getLast()) - /*(int) (Math.random() * rad)*/rad;
                arX.add((float) rand);
                rad = (minY + (int) (Math.random() * (maxY - minY)));
                arY.add((float) rad);
                minY = this.minY;
                maxY = this.maxY;
            } while (rand > /*this.minX*/border);
            Collections.reverse(arX);
            Collections.reverse(arY);
        }
        this.arX = new float[arX.size()];
        this.arY = new float[arY.size()];
        for (int i = 0; i < arX.size(); i++) {
            this.arX[i] = arX.get(i);
            this.arY[i] = arY.get(i);
        }
    }

}
