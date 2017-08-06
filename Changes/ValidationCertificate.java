package src.com.intervest.hercules;

import java.util.List;

public class ValidationCertificate {

	private String insurer;
	private String policyRefNumber;
	private String date;
	private List<TableOne> tableOneList;
	private List<TableTwo> tableTwoList;
	private List<TableThree> tableThreeList;
	private List<TableFour> tableFourList;

	private Boolean isPrintVersion;

	public String getInsurer() {
		return insurer;
	}

	public void setInsurer(String insurer) {
		this.insurer = insurer;
	}

	public String getPolicyRefNumber() {
		return policyRefNumber;
	}

	public void setPolicyRefNumber(String policyRefNumber) {
		this.policyRefNumber = policyRefNumber;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<TableOne> getTableOneList() {
		return tableOneList;
	}

	public void setTableOneList(List<TableOne> tableOneList) {
		this.tableOneList = tableOneList;
	}

	public List<TableTwo> getTableTwoList() {
		return tableTwoList;
	}

	public void setTableTwoList(List<TableTwo> tableTwoList) {
		this.tableTwoList = tableTwoList;
	}

	public List<TableThree> getTableThreeList() {
		return tableThreeList;
	}

	public void setTableThreeList(List<TableThree> tableThreeList) {
		this.tableThreeList = tableThreeList;
	}

	public List<TableFour> getTableFourList() {
		return tableFourList;
	}

	public void setTableFourList(List<TableFour> tableFourList) {
		this.tableFourList = tableFourList;
	}

	public Boolean getIsPrintVersion() {
		return isPrintVersion;
	}

	public void setIsPrintVersion(Boolean isPrintVersion) {
		this.isPrintVersion = isPrintVersion;
	}

}
