public class Car  {

	private boolean motorRunning = false;
	private float x, y;
	
	private float speed = 0;
	private float speedMax = 0;
	private float tankCapacity = 0;
	private float tankCurrentFuel = 0;

	private double timeLastCoordCheck = 0;
	private double timeLastFuelCheck = 0;
	private double timeBuffer = 0;
	private double pauseTimeStarted = 0;
	private double pauseTimeLasted = 0;
	
	private float[][] fuelUsage = {{0,0}, {10,6}, {20,8}, {30,10}, {40,12}, {50,14}};
  

  
   	public Car(float speedMax, float tankCapacity) {
	
		this.speedMax = speedMax;
		this.tankCapacity = tankCapacity;
		this.tankCurrentFuel = tankCapacity;
		this.x = 0;
		this.y = 100;
		this.timeLastCoordCheck = System.currentTimeMillis();
		this.timeLastFuelCheck = System.currentTimeMillis();
		this.pauseTimeStarted = System.currentTimeMillis();
	}
	
	
	private void refreshCar() {
	
		if(speed<0) {
			motorStop();
			//System.out.println("refreshed Car, but speed < 0");
			
		}
		if(tankCurrentFuel<=0) {
			motorStop();
			//checkFuelNegative();
			//System.out.println("refreshed Car, but Fuel <= 0");
		}
		if(getMotorOn()) {
			recalculatePosition();
			if(tankCurrentFuel >= 0)
				recalculateFuel();
			else
				fuelOverflowSetPosition();
			pauseTimeLasted = 0;
		}
	}

	public void fuelOverflowSetPosition() {
		double usagePerHr = 0;
		for(int i = 0; i<fuelUsage.length; i++) {
			if(fuelUsage[i][0] >= speed) { //was ==
				usagePerHr = fuelUsage[i][1];
			}
		}
		double timeSpentWithoutFuel = tankCurrentFuel/((float)usagePerHr/3600);
		this.x = this.x-(float)((timeSpentWithoutFuel)*speed/100);
		System.out.println("FUEL OVERFLOW: X set back by "+ (timeSpentWithoutFuel)*speed/100);
	}
	
	public void recalculatePosition() {
		double passedTime = System.currentTimeMillis() - timeLastCoordCheck - pauseTimeLasted;	//how much time passed since last milestone?
		System.out.println("passedTime" + passedTime);
		timeLastCoordCheck = System.currentTimeMillis();	//mark new milestone
		this.x = this.x+(float)((passedTime)*speed/100);  // divided by 100 so animation runs slower
		System.out.println("Time since last COOR " + (passedTime+pauseTimeLasted) + " pause " + pauseTimeLasted);	
	}
	
	
	public void recalculateFuel() {
	
		double passedTime = System.currentTimeMillis() - timeLastFuelCheck - pauseTimeLasted;
		timeLastFuelCheck = System.currentTimeMillis();
		double usagePerHr = 0;
		for(int i = 0; i<fuelUsage.length; i++) {
			if(fuelUsage[i][0] >= speed) { //was ==
				usagePerHr = fuelUsage[i][1];
			}
		}
		if(fuelUsage[fuelUsage.length-1][0] <= speed)
			usagePerHr = fuelUsage[fuelUsage.length-1][0]; //if current speed is higher than max defined speed in table, set to max usage
		this.tankCurrentFuel = tankCurrentFuel - (float)((passedTime)*(usagePerHr/3600));
		System.out.println("Time since last FUEL " + (passedTime+pauseTimeLasted) + " pause " + pauseTimeLasted);
	}
	
	
	public void setUsage(float[][] fuelUsage) {
	
		this.fuelUsage = fuelUsage;
	}
	
	
	public void motorStop() {
		
		motorRunning = false;
		speed = 0;
		pauseTimeStarted = System.currentTimeMillis();
		System.out.print("MOTOR STOPPED at " + System.currentTimeMillis() + "s\n\n");
	}
		
	
	public void motorStart() {
		
		pauseTimeLasted = System.currentTimeMillis() - pauseTimeStarted;
		motorRunning = true;
		System.out.print("MOTOR STARTED, pause time lasted " +pauseTimeLasted+"\n\n");
		//pauseTimeLasted = 0;
		
	}
	
			
	public void setSpeed(float speed) {
		speed = speed;
		if(speed>speedMax)
			this.speed = speedMax;
		else if(speed<=0) {
			this.speed = 0;
		}	
		else {
			this.speed = speed;
		}
		refreshCar();
	}
	
	
	public float getX()  {
		//System.out.println(System.currentTimeMillis());
		if(getMotorOn())
			refreshCar();
		return x;
	}


	public float getY()  {
		if(getMotorOn())
			refreshCar();
		return y;
	}


	public boolean getMotorOn() {
		return motorRunning;
	}


	public float getFuelTankCapacity() {
		return tankCapacity;
	}


	public float getFuel() {
		if(getMotorOn())
			refreshCar();
		return tankCurrentFuel;
	}


	public float getMaxSpeed() {
		return speedMax;
	}


	public float getSpeed() {
		return speed;
	}
	
		
	
	public void tanken(float tankMenge) {	
		if(tankCurrentFuel + tankMenge > tankCapacity)
			tankCurrentFuel = tankCapacity;
		else
			tankCurrentFuel += tankMenge;
	}
    
}