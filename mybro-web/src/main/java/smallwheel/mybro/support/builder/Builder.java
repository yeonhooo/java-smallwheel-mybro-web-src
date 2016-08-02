package smallwheel.mybro.support.builder;

import smallwheel.mybro.domain.ClassFileInfo;
import smallwheel.mybro.domain.TableInfo;

public interface Builder {

	void build(String userId, TableInfo tableInfo, ClassFileInfo classFileInfo);

}