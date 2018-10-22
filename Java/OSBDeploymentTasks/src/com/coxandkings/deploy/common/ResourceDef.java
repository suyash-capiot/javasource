package com.coxandkings.deploy.common;
import com.bea.wli.config.Ref;
import java.io.File;

public class ResourceDef {
	public Ref _ref;
	public File _file;
	public String _fullname;

	public ResourceDef(Ref ref, File file, String fullname) {
		this._ref = ref;
		this._file = file;
		this._fullname = fullname;
	}
	
	public String toString() {
		return "Ref:" + _ref.toString() + ";   File:" + _file.toString() +";   FullName:" + _fullname;
	}
}