package smallwheel.mybro.domain;

import java.util.List;

public class GeneratingInfo {
	private MappingOption mappingOption;
	private List<String> tables;

	public MappingOption getMappingOption() {
		return mappingOption;
	}

	public void setMappingOption(MappingOption mappingOption) {
		this.mappingOption = mappingOption;
	}

	public List<String> getTables() {
		return tables;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}
}
