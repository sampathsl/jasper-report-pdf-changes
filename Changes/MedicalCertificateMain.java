package src.com.intervest.hercules;

import java.util.ArrayList;
import java.util.List;

public class MedicalCertificateMain {

	private String date;
	private String underwriter;
	private String policyNumber;
	private List<MedicalDeclarationDetails> medicalDeclarationDetailsList = new ArrayList<MedicalDeclarationDetails>();
	private List<MedicalCondition> medicalConditionsList = new ArrayList<MedicalCondition>();
	private String underwriterCompanyName;
	private String tripType;
	private String medicalConfirmationInitialText;

	private Boolean isPrintVersion;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUnderwriter() {
		return underwriter;
	}

	public void setUnderwriter(String underwriter) {
		this.underwriter = underwriter;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public List<MedicalDeclarationDetails> getMedicalDeclarationDetailsList() {
		return medicalDeclarationDetailsList;
	}

	public void setMedicalDeclarationDetailsList(List<MedicalDeclarationDetails> medicalDeclarationDetailsList) {
		this.medicalDeclarationDetailsList = medicalDeclarationDetailsList;
	}

	public List<MedicalCondition> getMedicalConditionsList() {
		return medicalConditionsList;
	}

	public void setMedicalConditionsList(List<MedicalCondition> medicalConditionsList) {
		this.medicalConditionsList = medicalConditionsList;
	}

	public String getMedicalConfirmationInitialText() {
		return "We are pleased to confirm that your " + underwriterCompanyName + " " + tripType
				+ " travel insurance policy has been "
				+ "extended to provide full cover for any claims arising as a result of the above declared medical conditions under the normal terms and conditions of your policy - including "
				+ "any medical emergency. Cover is only provided for cancellation or curtailment if you have purchased this option with your policy.";
	}

	public void setUnderwriterCompanyName(String underwriterCompanyName) {
		this.underwriterCompanyName = underwriterCompanyName;
	}

	public void setTripType(String tripType) {
		this.tripType = tripType;
	}

	public String getUnderwriterCompanyName() {
		return underwriterCompanyName;
	}

	public String getTripType() {
		return tripType;
	}

	public void setMedicalConfirmationInitialText(String medicalConfirmationInitialText) {
		this.medicalConfirmationInitialText = medicalConfirmationInitialText;
	}

	public Boolean getIsPrintVersion() {
		return isPrintVersion;
	}

	public void setIsPrintVersion(Boolean isPrintVersion) {
		this.isPrintVersion = isPrintVersion;
	}

}
