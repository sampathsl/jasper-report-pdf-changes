package src.com.intervest.hercules;

public enum Underwriter {

	ERV("ERV"), 
	ASTRENSKA("ASTRENSKA"), 
	CIGNA("CIGNA"), 
	UKG("UKG"), 
	URV("URV"), 
	AXA("AXA");

	public String getCode() {
		return name();
	}

	public static Underwriter getUnderwriterFromPolicyNumber(String policyNumber) {
		String policy_number = null;
		policy_number = policyNumber;
		if (policyNumber != null) {

			if (policyNumber != null && policyNumber.contains("/")) {
				String parts[] = policyNumber.split("/");
				policy_number = parts[0];
			} else {
				policy_number = policyNumber;
			}

			if (Long.valueOf(policy_number) < 25000000)
				return null;
			if (Long.valueOf(policy_number) < 35000000)
				return ASTRENSKA; // 3XXXXXX
			if (Long.valueOf(policy_number) < 45000000)
				return CIGNA; // 4XXXXXX
			if (Long.valueOf(policy_number) < 55000000)
				return UKG; // 5XXXXXX
			if (Long.valueOf(policy_number) < 65000000)
				return URV; // 6XXXXXX
			if (Long.valueOf(policy_number) < 75000000)
				return AXA; // 7XXXXXX: MSM policies
			if (Long.valueOf(policy_number) < 100000000)
				return ERV;
		}

		return null;
	}

	private String underwriter;

	Underwriter(String underwriter) {
		this.underwriter = underwriter;
	}

	public String getUnderwriter() {
		return this.underwriter;
	}

	public static Underwriter fromString(String underwriter) {
		for (Underwriter b : Underwriter.values()) {
			if (b.underwriter.equalsIgnoreCase(underwriter)) {
				return b;
			}
		}
		throw new IllegalArgumentException("No constant with underwriter " + underwriter + " found");
	}

}
