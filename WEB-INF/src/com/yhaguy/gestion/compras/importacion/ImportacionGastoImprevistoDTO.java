package com.yhaguy.gestion.compras.importacion;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;

@SuppressWarnings("serial")
public class ImportacionGastoImprevistoDTO extends DTO{

	private double importeGs = 0;
	private double importeDs = 0;
	
	private MyArray proveedor = new MyArray();

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = this.getMisc().redondeoCuatroDecimales(importeGs);
	}

	public double getImporteDs() {
		return importeDs;
	}

	public void setImporteDs(double importeDs) {
		this.importeDs = this.getMisc().redondeoCuatroDecimales(importeDs);
	}

	public MyArray getProveedor() {
		return proveedor;
	}

	public void setProveedor(MyArray proveedor) {
		this.proveedor = proveedor;
	}
	
}
