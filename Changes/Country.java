package src.com.intervest.hercules;

public class Country {
	
	private String code;
	private String continentCode;
	private String standardizedName;
	private String displayName;
	private boolean isAlias;
	private String aliasCountryCode;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getContinentCode() {
		return continentCode;
	}

	public void setContinentCode(String continentCode) {
		this.continentCode = continentCode;
	}

	public String getStandardizedName() {
		return standardizedName;
	}

	public void setStandardizedName(String standardizedName) {
		this.standardizedName = standardizedName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isAlias() {
		return isAlias;
	}

	public void setAlias(boolean isAlias) {
		this.isAlias = isAlias;
	}

	public String getAliasCountryCode() {
		return aliasCountryCode;
	}

	public void setAliasCountryCode(String aliasCountryCode) {
		this.aliasCountryCode = aliasCountryCode;
	}

	@Override
	public Object clone() {
		Country c = new Country();
		c.setCode(code);
		c.setContinentCode(continentCode);
		c.setStandardizedName(standardizedName);
		c.setDisplayName(displayName);
		c.setAlias(isAlias);
		c.setAliasCountryCode(aliasCountryCode);
		return c;
	}
	
}
