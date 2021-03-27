/**
 * Spielerentitaet; Interface zwischen Accountmanager und Aktienkauf.
 *
 */

public class Player {

	
	private String playerName;
	private CashAccount cashAccount;
	private ShareDeposit shareDeposit;
	
	
	public Player(String playerName) {
		this.playerName = playerName;
		this.cashAccount = new CashAccount(0);
		this.shareDeposit = new ShareDeposit();
	}
	
	
	
	public String getName() {
		return playerName;
	}
	public void setName(String playerName) {
		this.playerName = playerName;
	}
	
	
	
	public CashAccount getCashAccount() {
		return cashAccount;
	}
	public void setCashAccount(CashAccount cashAccount) {
		this.cashAccount = cashAccount;
	}
	
	
	
	public ShareDeposit getShareDeposit() {
		return shareDeposit;
	}
	public void setShareDeposit(ShareDeposit shareDeposit) {
		this.shareDeposit = shareDeposit;
	}
	
	

	
	public long getBalance() {
		return cashAccount.getValue();
	}
	public void setBalance(long balance) {
		cashAccount.setValue(balance);
	}
	public void addBalance(long balance) {
		cashAccount.setValue(cashAccount.getValue() + balance);
	}
	
	
	
	
	public void buyShares(Share share, int buyAmount) throws OutOfFundsException {
		if(buyAmount < 1) {
			System.err.println("You can't buy negative amounts of shares.");
		} else {
			if(getBalance() >= getShareRate(share) * buyAmount) {
				shareDeposit.buyShares(share, buyAmount);
				addBalance(-(getShareRate(share) * buyAmount));
			} else {
				throw new OutOfFundsException(this);
			}
		}
	}
	public void sellShares(Share share, int sellAmount) { //TODO: CHECK IF AMOUNT AND BALANCE ARE OK IN AMI
		if(sellAmount < 1) {
			System.out.println("You can't sell negative amounts of shares.");
		} else {
			if(getShareAmount(share) >= sellAmount) {
				shareDeposit.sellShares(share, sellAmount);
				addBalance(getShareRate(share) * sellAmount);
			} else {
				System.err.println(getName() + " doesn't have enough shares to sell.");
			}
		}
	}
	
	
	public int getShareAmount(Share share) {
		return shareDeposit.findShareItemByCompany(share).getShareCount();
	}
	public void setShareAmount(Share share, int shareCount) {
		shareDeposit.findShareItemByCompany(share).setShareCount(shareCount);
	}
	
	
	public long getShareRate(Share share) throws NullPointerException{
		if(share != null) {
			return share.getRate();
		} else {
			throw new NullPointerException();
		}
	}
	
	
	public void printStatus() {
		System.out.println("");
		System.out.println(getName() + "'s account status:\nBalance: " + cashAccount.getValue() + " Cents\nStock owned:");
		shareDeposit.printSharesArray();
		System.out.println("\nStock value total: " + shareDeposit.getValue() + " Cents.\n\n");
	}
	
}
