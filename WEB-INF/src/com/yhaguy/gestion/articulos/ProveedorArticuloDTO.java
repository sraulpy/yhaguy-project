package com.yhaguy.gestion.articulos;


import com.coreweb.dto.DTO;
import com.yhaguy.gestion.empresa.ProveedorDTO;

@SuppressWarnings("serial")
public class ProveedorArticuloDTO extends DTO {
	
	private String descripcionArticuloProveedor = "";
	private String codigoFabrica = "";

	ProveedorDTO proveedor;

	public String getDescripcionArticuloProveedor() {
		return descripcionArticuloProveedor;
	}

	public void setDescripcionArticuloProveedor(
			String descripcionArticuloProveedor) {
		this.descripcionArticuloProveedor = descripcionArticuloProveedor
				.toUpperCase();
	}

	public String getCodigoFabrica() {
		return codigoFabrica;
	}

	public void setCodigoFabrica(String codigoFabrica) {
		this.codigoFabrica = codigoFabrica.toUpperCase();
	}

	public ProveedorDTO getProveedor() {
		return proveedor;
	}

	public void setProveedor(ProveedorDTO proveedor) {
		this.proveedor = proveedor;
	}

}
