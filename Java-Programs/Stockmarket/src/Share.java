public class Share {

	private final String shareCompany;
	private long rate;

	
	public Share(String shareCompany) {
		this.shareCompany = shareCompany;
		this.rate = 0;
	}
	
	public Share(String shareCompany, long rate) {
		this.shareCompany = shareCompany;
		this.rate = rate;
	}

	public long getRate() {
		return rate;
	}
	public void setRate(long rate) {
		this.rate = rate;
	}
	
	
	public String getCompany() {
		return shareCompany;
	}
	
	
	public boolean equals(Share share) {
		return getRate() == share.getRate() && getCompany() == share.getCompany();
	}
	
	public String toString() {
		return getClass().getName() + " of company " + getCompany() + " is at " + getRate() + " Rate.";
	}
	
}
