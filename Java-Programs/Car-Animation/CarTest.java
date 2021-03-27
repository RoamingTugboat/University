public class CarTest  {  


    public static void main(String[] args) { 
        Car car = new Car(120,80);  
        Traffic traffic = new Traffic(car);
        traffic.setSize(1000, 200);
        traffic.setVisible(true);  
     
        // start animation
        traffic.start();
        
        // put your code for car state manipulation here

		float[][] myUsage = {{0,0}, {10,20}, {20,30}, {30,50}, {40,60}, {50,80}};
		car.setUsage(myUsage);
		car.motorStart();
		car.setSpeed(10);
		
		traffic.waitAWhile(500);
		
		car.setSpeed(10);
		
		traffic.waitAWhile(1000);
		
		car.motorStop();
		System.out.println("tanken....");
		
		traffic.waitAWhile(1500);
		
		car.tanken(30);
		car.setSpeed(80);
		car.motorStart();
		
		traffic.waitAWhile(1500);
		
		//car.setSpeed(20);
		
		//traffic.waitAWhile(1000);
		
		//car.motorStop();
		//car.setSpeed(23);
    } 
	
}