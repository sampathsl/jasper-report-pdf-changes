package src.com.intervest.hercules;

public enum FreeMedicalCondition {
	
	ARTHRITIS ("Arthritis (Juvenile, Osteoarthritis, Rheumatoid or Psoriatic Arthritis, Reiter's Syndrome, Rheumatism)" ),
	ASTHMA ("Asthma (Wheezing)"),
	DIABETES ("Diabetes Mellitus (Sugar Diabetes)"),
	DOWNS_SYNDROME ("Down's Syndrome" ),
	HYPERCHOLERTEROLAEMIA ("Hypercholesterolaemia (High/Raised Cholesterol)" ),
	HYPERTENSION ("Hypertension (High Blood Pressure, White Coat Syndrome)" ),
	HYPOTENSION ("Hypotension (Low Blood Pressure)" ),
	OSTEOPOROSIS ("Osteoporosis (Osteopaenia, Fragile Bones)" );
	
	private String displayName;
	
	private FreeMedicalCondition(String displayName) {
		this.displayName = displayName;
	}
	
	public String getName() {
		return name();
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public static FreeMedicalCondition fromString(String freeMedical) {
		for (FreeMedicalCondition b : FreeMedicalCondition.values()) {
			if (freeMedical != null && b.getName().equalsIgnoreCase(freeMedical)) {
				return b;
			}
		}
		throw new IllegalArgumentException("No constant with freeMedical " + freeMedical + " found");
	}
	
}
