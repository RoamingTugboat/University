import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class Traffic extends JFrame {  
    private Displayer displayer;
  
    public Traffic(Car car){ 
        setTitle("Traffic simulation");
        JPanel canvas = new DisplayArea(car);
        getContentPane().add("Center", canvas);
        displayer = new Displayer(canvas);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);    
    }
   
    public void start()  {
        displayer.start();
    }
   
   
    // interrupts work for the specified period (in milliseconds)
    public static void waitAWhile(long period)  {
        try { 
            Thread.sleep(period); 
        } catch(InterruptedException e) {};   
    } 
}

class DisplayArea extends JPanel  {
    private Car car;
    
    public DisplayArea(Car car)  {
        this.car = car;
    }
    
    public void paintComponent(Graphics g)  {
//    	super.paintComponent(g);
    	g.setColor(getBackground());
    	g.fillRect(0,0,getWidth(),getHeight());
        g.setColor(Color.red);
        Dimension d = getSize();
        
        // get current location of the car and convert meters into pixels (factor 8)
        int x = (int) (car.getX()*8);
		x %= d.width;
        int y = (int) car.getY();
        
        // now paint the car
		g.fillRect(x-5,y-10,40,10);       
        g.drawRect(x+2,y-17,20,7);
		g.setColor(Color.black);
        g.fillOval(x, y-5, 10, 10);  
        g.fillOval(x+20, y-5, 10, 10); 
		
       
        
        // if motor on show waste gas
        if (car.getMotorOn()) {
			g.setColor(Color.black);
			g.fillOval(x-20, y-10, 10, 8);
			g.fillOval(x-32, y-20, 16, 12);
        }
		
        
        final int scaleFactor = 4;
		g.setColor(Color.black);
		g.drawString("Speedometer:",0,140); 
		float maxSpeed = car.getMaxSpeed();
		g.drawRect(90,130,(int)(maxSpeed*scaleFactor),10); 
		g.setColor(Color.green); 
		float speed = car.getSpeed();
		if (speed < 0) {
			g.drawString("negative value",90,140);
		} else if (speed > maxSpeed) {
		    g.drawString("larger than max speed!!!!",90,140);
	    } else {
			g.fillRect(90,130,(int)(speed*scaleFactor),10);
	    }
		     
		g.setColor(Color.black);
		g.drawString("Fuel Gauge:",0,160);
		float maxFuel = car.getFuelTankCapacity(); 
		g.drawRect(90,150,(int)(maxFuel*scaleFactor),10);  
		g.setColor(Color.red);
		float fuel = car.getFuel(); 
		if (fuel < 0) {
		    g.drawString("negative value",90,160);
		} else if (fuel > maxFuel) {
			g.drawString("you are wasting gas filling in more than fuel tank capacity",90,160);
		} else {
			g.fillRect(90,150,(int)(fuel*scaleFactor),10);
		}
    }
}


class Displayer extends Thread {  
    private JPanel panel;
  
    public Displayer(JPanel panel) { 
        this.panel = panel; 
    }
   
    public void run() {
        // the animation loop 
        while (true) {  
           panel.repaint(); // display a new animation frame
           Traffic.waitAWhile(20);
        }
    }
}
  

