package src.com.intervest.hercules;

public class TravellerScreeningsDTO {
	
	private String title;
	private String foreName;
	private String surName;
	private String conditions;
	private String linkedConditions;
	private String qandAnswers;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getForeName() {
		return foreName;
	}

	public void setForeName(String foreName) {
		this.foreName = foreName;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

	public String getConditions() {
		if(conditions != null){
			conditions = conditions.replace("|", " , ");
		}
		return conditions;
	}
	
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}
	
	public String getLinkedConditions() {
		if(linkedConditions != null){
			linkedConditions = linkedConditions.replace("|", " , ");
		}
		return linkedConditions;
	}
	
	public void setLinkedConditions(String linkedConditions) {
		this.linkedConditions = linkedConditions;
	}
	
	public String getQandAnswers() {
		return qandAnswers;
	}
	
	public void setQandAnswers(String qandAnswers) {
		this.qandAnswers = qandAnswers;
	}
	
}
