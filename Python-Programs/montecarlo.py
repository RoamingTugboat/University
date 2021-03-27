class MonteCarloSolver(object):
	"""Objects of this class approximate pi by using the Monte Carlo algorithm.
	The number they're constructed with dictates how exact the approximation will be."""

	points = [] 		 		#list that saves all randomly generated tuples
	currentApprox = 0 
	
	def __init__(self, accuracy):
		self.create_tuples(accuracy)	#creates a list of random tuples in range (0,0), (1,1)
		self.approx_pi(accuracy)	 	#uses monte carlo algorithm to calculate pi from tuples
		self.print_approx()				#prints the result
	
	@property
	def currentApprox(self):
		return self._currentApprox
		
	@currentApprox.setter
	def currentApprox(self, value):
		self._currentApprox = value
	
	def create_tuples(self, accuracy):
		for x in range(accuracy):
			self.points.append(self.draw_random_tuple())
	
	def draw_random_tuple(self):
		return (self.draw_random_number(), self.draw_random_number())
		
	def draw_random_number(self, bottomBound=0.0, upperBound=1.0):
		import random
		return random.uniform(bottomBound, upperBound)
	
	def approx_pi(self, accuracy):
		import math
		insidePoints, outsidePoints = 0, 0
		for t in self.points:
			if(math.sqrt(math.pow(t[0], 2) + math.pow(t[1], 2)) < 1):
				insidePoints += 1
			else:
				outsidePoints += 1
		self.currentApprox = (insidePoints/(outsidePoints+insidePoints))*4
	
	def print_approx(self):
		print('Current approximation of pi is: ', self.currentApprox)

	def pfadAusgabe(self, path):
		import PyLab
		pointArray = pylab.array(path)
		pylab.plot(pointArray[0,0], pointArray[0,1], 'ro')
		pylab.plot(pointArray[1:-1,0], pointArray[1:-1,1], 'bo')
		pylab.plot(pointArray[:,0], pointArray[:,1], '-k')
		pylab.yticks(())
		pylab.xticks(())
		pylab.show()
	
if __name__ == "__main__":
	ncs = MonteCarloSolver(9999)
	ncs.pfadAusgabe([(0, 1),(0.7, 0.2),(0.5, 0),(1, 0.5)])