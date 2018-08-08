package com.yhaguy.gestion.compras.gastos.subdiario;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;

@SuppressWarnings("serial")
public class ArticuloGastoDTO extends DTO{

	private String descripcion = "";
	private String creadoPor = "";
	private String verificadoPor = "";
	
	private MyArray cuentaContable = new MyArray();
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.toUpperCase();
	}
	
	public String getCreadoPor() {
		return creadoPor;
	}
	
	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}
	
	public String getVerificadoPor() {
		return verificadoPor;
	}
	
	public void setVerificadoPor(String verificadoPor) {
		this.verificadoPor = verificadoPor;
	}

	public MyArray getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(MyArray cuentaContable) {
		this.cuentaContable = cuentaContable;
	}
}
