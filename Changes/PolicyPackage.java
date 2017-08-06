package src.com.intervest.hercules;


/** {BASIC COMPREHENSIVE} */
public enum PolicyPackage {
	
	BASIC("Basic", "2"),
	COMPREHENSIVE("Comprehensive", "3");
	
	private final String displayName;
	private final String legacyCode;
	
	private PolicyPackage(String displayName, String legacyCode) {
		this.displayName = displayName;
		this.legacyCode = legacyCode;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public String getLegacyCode() {
		return this.legacyCode;
	}
	
}
