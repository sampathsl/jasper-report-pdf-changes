package src.com.intervest.hercules;

public class TotalPolicyLetter {

	private String policyNumber;
	private Long validationCertificate;
	private Long medicalCertificate;
	private Long policyLetter;
	private String postageDeliveryType;
	private String hasCruiseCover;
	private String hasGadgetCover;
	private String totalPolicyText;

	public TotalPolicyLetter(String policyNumber, Long validationCertificate, Long medicalCertificate,
			Long policyLetter, String postageDeliveryType, String hasCruiseCover, String hasGadgetCover) {
		this.policyNumber = policyNumber;
		this.validationCertificate = validationCertificate;
		this.medicalCertificate = medicalCertificate;
		this.policyLetter = policyLetter;
		this.postageDeliveryType = postageDeliveryType;
		this.hasCruiseCover = hasCruiseCover;
		this.hasGadgetCover = hasGadgetCover;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public Long getValidationCertificate() {
		return validationCertificate;
	}

	public void setValidationCertificate(Long validationCertificate) {
		this.validationCertificate = validationCertificate;
	}

	public Long getMedicalCertificate() {
		return medicalCertificate;
	}

	public void setMedicalCertificate(Long medicalCertificate) {
		this.medicalCertificate = medicalCertificate;
	}

	public Long getPolicyLetter() {
		return policyLetter;
	}

	public void setPolicyLetter(Long policyLetter) {
		this.policyLetter = policyLetter;
	}

	public String getTotalPolicyText() {

		if (this.getPostageDeliveryType() != null && !this.getPostageDeliveryType().equalsIgnoreCase("")) {
			if (this.getHasCruiseCover() != null) {
				return "Policy " + this.getPolicyNumber() + "\n" + "Policy Letter - " + this.getPolicyLetter() + "\n"
						+ "Validation Certificate - " + this.getValidationCertificate() + "\n"
						+ "Medical Endorsement - " + this.getMedicalCertificate() + "\n" + "Delivery Option - "
						+ this.getPostageDeliveryType() + "\n" + "Ocean Cruise Cover - " + this.getHasCruiseCover()
						+ "\n" + "Gadget Cover - " + this.getHasGadgetCover();
			} else {
				return "Policy " + this.getPolicyNumber() + "\n" + "Policy Letter - " + this.getPolicyLetter() + "\n"
						+ "Validation Certificate - " + this.getValidationCertificate() + "\n"
						+ "Medical Endorsement - " + this.getMedicalCertificate() + "\n" + "Gadget Cover - "
						+ this.getHasGadgetCover();
			}
		} else {
			if (this.getHasCruiseCover() != null) {
				return "Policy " + this.getPolicyNumber() + "\n" + "Policy Letter - " + this.getPolicyLetter() + "\n"
						+ "Validation Certificate - " + this.getValidationCertificate() + "\n"
						+ "Medical Endorsement - " + this.getMedicalCertificate() + "\n" + "Cruise Cover - "
						+ this.getHasCruiseCover() + "\n" + "Gadget Cover - " + this.getHasGadgetCover();
			} else {
				return "Policy " + this.getPolicyNumber() + "\n" + "Policy Letter - " + this.getPolicyLetter() + "\n"
						+ "Validation Certificate - " + this.getValidationCertificate() + "\n"
						+ "Medical Endorsement - " + this.getMedicalCertificate() + "\n" + "Gadget Cover - "
						+ this.getHasGadgetCover();
			}
		}

	}

	public void setTotalPolicyText(String totalPolicyText) {
		this.totalPolicyText = totalPolicyText;
	}

	public String getPostageDeliveryType() {
		return postageDeliveryType;
	}

	public void setPostageDeliveryType(String postageDeliveryType) {
		this.postageDeliveryType = postageDeliveryType;
	}

	public String getHasCruiseCover() {
		return hasCruiseCover;
	}

	public void setHasCruiseCover(String hasCruiseCover) {
		this.hasCruiseCover = hasCruiseCover;
	}

	public String getHasGadgetCover() {
		return hasGadgetCover;
	}

	public void setHasGadgetCover(String hasGadgetCover) {
		this.hasGadgetCover = hasGadgetCover;
	}

}
