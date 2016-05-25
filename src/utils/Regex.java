package utils;

public class Regex {
	public String id;
	public String prefix;
	public String data;
	public String suffix;

	public Regex(String id, String data) {
		this(id, data, null, null);
	}

	public Regex(String id, String data, String suffix) {
		this(id, data, null, suffix);
	}
	
	public Regex(String id, String data, String prefix, String suffix) {
		this.id = id;
		this.data = data;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public String format() {
		StringBuilder str = new StringBuilder();
		Boolean hasPrefix = prefix != null;
		Boolean hasSuffix = suffix != null;
		// Boolean hasPrefixOrSuffix = hasPrefix || hasSuffix;

		if (hasPrefix) {
			str.append(prefix);
		}
		// if (hasPrefixOrSuffix) {
			str.append("(?:");
		// }

		str.append(data);

		// if (hasPrefixOrSuffix) {
			str.append(")");
		// }
		if (hasSuffix) {
			str.append(suffix);
		}

		return str.toString().replace("{ID}", this.id);
	}
	
	public String toString() {
		return id;
	}

}