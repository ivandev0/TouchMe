package source;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Game extends Canvas implements Runnable{
    public static int WIDTH, HEIGHT;
    private BufferedImage sourceImage;

    private float FPS = 60;//количество кадров в секунду
    static String NAME = "TouchMe";//название игры
    private String path = "/asset/images/hero.png";//путь к папке с файлом
    private boolean run = false, begin = false;//флаги игры
    private int clicks = 0, //текущее количество кликов
            limit = 100;//лимит кликов

    private float screenCoef = 0.75f;//коэфициент уменьшения экрана
    private float lastY = 0, lastX = 0;//координаты последнего положения главного объекта

    private Hero hero;//главный игровой объект/герой
    private GameObject mouseGO; //объект класса GameObject для хранения некого объекта мыши

    //создание и инициализация главного окна
    private Game(){
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = WIDTH = (int)(dimension.width * screenCoef);
        int screenHeight = HEIGHT = (int) (dimension.height * screenCoef);
        int screenX = (int)((dimension.width - screenWidth) / 2.0f);
        int screenY = (int)((dimension.height - screenHeight) / 2.0f);
        setPreferredSize(new Dimension(screenWidth,screenHeight));

        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(this,BorderLayout.CENTER);
        frame.setBounds(screenX, screenY, screenWidth, screenHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle(NAME);
        frame.setVisible(true);
        addMouseListener(new MouseListener());

        start();
    }

    public static void main(String[] args) {
        new Game();
    }

    //запуск нового потока
    private void start(){
        //!!! начальное окно
        init();
        run = true;
        new Thread(this).start();
    }

    @Override
    //перегруженный метод нового потока, где реализован цикл игры
    public void run() {
        long lastTime = System.nanoTime();
        double ns = 1000000000;
        float delta = 0.0f;

        //до тех пор пока поток запущен
        while(run) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= (float)1/FPS)//1/FPS время между кадрами
            {
                update(delta); //обновление параметров сцены
                delta-=(float)1/FPS;
            }
            render(); //отрисовка сцены
        }
    }

    //инициализация всех компонентов
    private void init() {
        sourceImage = null;
        try {
            sourceImage = ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            System.out.println("Ошибка открытия файла " + getClass().getResource(path));
            System.exit(3);
        }
        hero = new Hero(0,0, sourceImage);
    }

    //метод вызываемый при рестарте игры
    private void onRestart(){
        clicks = 0;
        hero = new Hero(lastX,lastY, sourceImage);
        run = true;
        new Thread(this).start();
    }

    //обновление игровой сцены
    private void update(float delta){
        if(begin)
            hero.update(delta);
        else
            hero.updateOnBegin(delta);
    }

    //отрисовка игровой сцены
    private void render(){
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(2); //создаем BufferStrategy для нашего холста
            requestFocus();
            return;
        }

        Graphics g = bs.getDrawGraphics(); //получаем Graphics из созданной нами BufferStrategy
        g.setColor(Color.decode("#f7f7f7")); //выбрать цвет  #f7f7f7 - Gray
        g.fillRect(0, 0, getWidth(), getHeight()); //заполнить прямоугольник

        hero.paint(g);

        //отрисовка меню
        if(!begin) {
            String text = "Touch the screen to begin";
            Font font = new Font("TimesRoman", Font.BOLD, 50);
            FontMetrics metrics = g.getFontMetrics(font);
            // Determine the X coordinate for the text
            int x = (WIDTH - metrics.stringWidth(text)) / 2;
            // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
            int y = -100 + ((HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent();

            double time = System.nanoTime() / 1000000000.0f;
            g.setColor(new Color(0, 0, 1, (float) Math.abs(Math.sin(time))));
            g.setFont(font);
            g.drawString(text, x, y);
        } else {
            String text = "Clicks:" + clicks;
            Font font = new Font("TimesRoman", Font.BOLD, 50);
            FontMetrics metrics = g.getFontMetrics(font);
            // Determine the X coordinate for the text
            int x = (int) (WIDTH*0.02f);
            // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
            int y = metrics.getHeight();

            g.setColor(new Color(35*(limit - clicks)/limit, 0, 255*(limit - clicks)/limit));
            g.setFont(font);
            g.drawString(text, x, y);
        }

        if(!run){
            String text1;
            if(clicks!= 100)
                text1 = "You win!";
            else
                text1 = "You lose! Try again";
            String text2 = "Touch the ball to restart";
            Font font = new Font("TimesRoman", Font.BOLD, 50);
            FontMetrics metrics = g.getFontMetrics(font);
            // Determine the X coordinate for the text
            int x = (WIDTH - metrics.stringWidth(text1)) / 2;
            // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
            int y = -100 + ((HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent();

            g.setColor(new Color(43, 112, 206));
            g.setFont(font);
            g.drawString(text1, x, y);

            x = (WIDTH - metrics.stringWidth(text2)) / 2;
            y += 100;
            g.drawString(text2, x, y);
        }

        //for debug
        /*g.setColor(Color.GREEN);
        if(hero.spline != null){
            float x = hero.arX[0];
            while(x < hero.arX[hero.arX.length - 1]){
                g.drawString("*", (int)x, (int)hero.spline.getValue(x));
                x+=1;
            }
        }*/

        g.dispose();
        bs.show();
    }

    private class MouseListener extends MouseAdapter {
        @Override
        //отслеживание и обработка нажатия клавишы мыши
        public void mousePressed(MouseEvent e) {
            if(begin) {
                mouseGO = new GameObject(e.getX(), e.getY(), 0, 0, 1, 1);
                if (run) {
                    clicks++;
                    if (hero.collider.boxCompare(mouseGO) || clicks == 100) {
                        lastX = hero.x;
                        lastY = hero.y;
                        run = false;
                        render();
                    }
                } else {
                    if (hero.collider.boxCompare(mouseGO)) {
                        onRestart();
                    }
                }
            } else {
                lastX = hero.x;
                lastY = hero.y;
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Touch the ball in " + limit + " clicks");
                hero = new Hero(lastX,lastY, sourceImage);
                begin = true;
            }
        }
    }
}
