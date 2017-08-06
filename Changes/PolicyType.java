package src.com.intervest.hercules;

public enum PolicyType {
	
	SINGLE_TRIP("Single Trip","single_trip", "S", "Single Trip"),
	ANNUAL_MULTI_TRIP("Annual Multi Trip","annual_trip", "A", "Annual Trip"),
	LONGSTAY("Longstay","single_trip", "L", "Long Stay"),
	BACKPACKER("Backpacker","backpacker","B","Backpacker");
	
	private String displayName;
	private String legacyHealixPriceValuesCode;
	private String legacyOneLetterCode;
	private String legacyUpgradesFieldPolicyType;
	
	private PolicyType(String displayName, String legacyHealixPriceValuesCode, String legacyOneLetterCode, String legacyUpgradesFieldPolicyType){
		this.displayName = displayName;
		this.legacyHealixPriceValuesCode = legacyHealixPriceValuesCode;
		this.legacyOneLetterCode = legacyOneLetterCode;
		this.legacyUpgradesFieldPolicyType = legacyUpgradesFieldPolicyType;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	public String getLegacyOneLetterCode() {
		return legacyOneLetterCode;
	}
	
	/** Returns POLICY_TYPE code for HEALIX_PRICE_VALUES */
	public String getLegacyHealixPriceValuesCode(){
		return legacyHealixPriceValuesCode;
	};
	
	public String getLegacyUpgradesFieldPolicyType(){
		return legacyUpgradesFieldPolicyType;
	}
	
}
