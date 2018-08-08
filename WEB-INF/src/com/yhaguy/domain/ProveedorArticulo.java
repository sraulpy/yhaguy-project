package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ProveedorArticulo extends Domain {
	private String descripcionArticuloProveedor;
	private String codigoFabrica;
	
	Proveedor proveedor;

	public String getDescripcionArticuloProveedor() {
		return descripcionArticuloProveedor;
	}

	public void setDescripcionArticuloProveedor(String descripcionArticuloProveedor) {
		this.descripcionArticuloProveedor = descripcionArticuloProveedor;
	}
	
	public String getCodigoFabrica() {
		return codigoFabrica;
	}

	public void setCodigoFabrica(String codigoFabrica) {
		this.codigoFabrica = codigoFabrica;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	@Override
	public int compareTo(Object o) {
		ProveedorArticulo cmp = (ProveedorArticulo) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.id)==0);
		   
		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
	}
}
