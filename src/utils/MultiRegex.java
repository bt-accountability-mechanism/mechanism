package utils;

public class MultiRegex {

	public String id;
	private Regex[] regexList;
	
	protected MultiRegex() { }
	
	public MultiRegex(String id, Regex[] regexList) {
		this.id = id;
		this.regexList = regexList;
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		if (regexList.length > 0) {
			str.append("(?<");
			str.append(id);
			str.append(">");
			for (int index = 0; index < regexList.length; index++) {
				str.append(regexList[index].format());
			}
			str.append(")");
		}
		return str.toString();
	}
	
}
