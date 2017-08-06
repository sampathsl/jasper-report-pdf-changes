package src.com.intervest.hercules;

import java.math.BigDecimal;

public class StsTravelPersonScreening {

	private Long id;
	private Long personId;
	private Long insuranceId;
	private String title;
	private String forename;
	private String surname;
	private Integer age;
	private String medicalRef;
	private String noteFlag;
	private String note;
	private String hash;
	private String xml;
	private String conditions;
	private BigDecimal score;
	private BigDecimal price;
	private String excludeCost;
	private String linkedConditions;
	private String status;
	private BigDecimal scrCost;
	private BigDecimal scrBasePrice;
	private BigDecimal scrMargin;
	private FreeMedicalCondition freeMedicalCondition;
	private String scrAmtAllowed;
	private String medicalFree;

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public Long getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(Long insuranceId) {
		this.insuranceId = insuranceId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getForename() {
		return forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getMedicalRef() {
		return medicalRef;
	}

	public void setMedicalRef(String medicalRef) {
		this.medicalRef = medicalRef;
	}

	public String getNoteFlag() {
		return noteFlag;
	}

	public void setNoteFlag(String noteFlag) {
		this.noteFlag = noteFlag;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getExcludeCost() {
		return excludeCost;
	}

	public void setExcludeCost(String excludeCost) {
		this.excludeCost = excludeCost;
	}

	public String getLinkedConditions() {
		return linkedConditions;
	}

	public void setLinkedConditions(String linkedConditions) {
		this.linkedConditions = linkedConditions;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getScrCost() {
		return scrCost;
	}

	public void setScrCost(BigDecimal scrCost) {
		this.scrCost = scrCost;
	}

	public BigDecimal getScrBasePrice() {
		return scrBasePrice;
	}

	public void setScrBasePrice(BigDecimal scrBasePrice) {
		this.scrBasePrice = scrBasePrice;
	}

	public BigDecimal getScrMargin() {
		return scrMargin;
	}

	public void setScrMargin(BigDecimal scrMargin) {
		this.scrMargin = scrMargin;
	}

	public FreeMedicalCondition getFreeMedicalCondition() {
		return freeMedicalCondition;
	}

	public void setFreeMedicalCondition(FreeMedicalCondition freeMedicalCondition) {
		this.freeMedicalCondition = freeMedicalCondition;
	}

	public String getScrAmtAllowed() {
		return scrAmtAllowed;
	}

	public void setScrAmtAllowed(String scrAmtAllowed) {
		this.scrAmtAllowed = scrAmtAllowed;
	}

	public String getMedicalFree() {
		return medicalFree;
	}

	public void setMedicalFree(String medicalFree) {
		this.medicalFree = medicalFree;
	}

}
