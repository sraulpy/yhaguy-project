package com.yhaguy.gestion.notadebito;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;

@SuppressWarnings("serial")
public class NotaDebitoDetalleDTO extends DTO {

	private String descripcion = "";
	private double importeGs = 0;

	private MyPair tipoIva;

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.toUpperCase();
	}

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public MyPair getTipoIva() {
		return tipoIva;
	}

	public void setTipoIva(MyPair tipoIva) {
		this.tipoIva = tipoIva;
	}	
}
