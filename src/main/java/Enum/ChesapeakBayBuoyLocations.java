package Enum;

import java.util.HashMap;
import java.util.Map;

public enum ChesapeakBayBuoyLocations {

	/*
	 * all the current slot list 
	 */
	
//	annapolis - AN
//	susquehanna - S
//	patapsco - SN
//	upper potomac - IP
//	gooses reef - GR
//	potomac - PL
//	stingray point - SR
//	yorkspit - YS
//	jamestown - J
//	norfolk - N
//	first landing - FL



	AN("ANNAPOLIS"),
	S("SUSQUEHANNA"),
	SN("PATAPSCO"),
	IP("UPPER POTOMAC"),
	GR("GOOSES REEF"),
	PL("POTOMAC"),
	SR("STINGRAY POINT"),
	YS("YORKSPIT"),
	J("JAMESTOWN"),
	N("NORFOLK"),
	FL("FIRST LANDING");
	
	private final String url;
	
	// Reverse-lookup map for getting a day from an abbreviation
    private static final Map<String, ChesapeakBayBuoyLocations> lookup = new HashMap<String, ChesapeakBayBuoyLocations>();
	
    static {
        for (ChesapeakBayBuoyLocations location : ChesapeakBayBuoyLocations.values()) {
            lookup.put(location.getAbbreviation(), location);
        }
    }
    
	ChesapeakBayBuoyLocations(String url){
		this.url = url;
	}
	
	public String getAbbreviation() {
        return url;
    }
	
	
	public static ChesapeakBayBuoyLocations get(String abbreviation){
		return lookup.get(abbreviation);
	}
	
}
