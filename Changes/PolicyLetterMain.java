package src.com.intervest.hercules;

public class PolicyLetterMain {

	private String customerAddress;
	private String policyNumber;
	private String customerName;
	private String date;
	private String trustPilotLink;

	public PolicyLetterMain(String customerAddress, String policyNumber, String customerName, String date,
			String trustPilotLink) {
		this.customerAddress = customerAddress;
		this.policyNumber = policyNumber;
		this.customerName = customerName;
		this.date = date;
		this.trustPilotLink = trustPilotLink;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTrustPilotLink() {
		return trustPilotLink;
	}

	public void setTrustPilotLink(String trustPilotLink) {
		this.trustPilotLink = trustPilotLink;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

}
