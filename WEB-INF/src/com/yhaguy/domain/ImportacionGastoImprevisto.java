package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ImportacionGastoImprevisto extends Domain{

	private double importeGs;
	private double importeDs;
	
	private Proveedor proveedor;
	
	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public double getImporteDs() {
		return importeDs;
	}

	public void setImporteDs(double importeDs) {
		this.importeDs = importeDs;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	@Override
	public int compareTo(Object o) {
		ImportacionGastoImprevisto cmp = (ImportacionGastoImprevisto) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.id) == 0);
		
		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}		
	}

}
