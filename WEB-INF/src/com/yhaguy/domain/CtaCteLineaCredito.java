package com.yhaguy.domain;
import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class CtaCteLineaCredito extends Domain{

	private double linea;
	private String descripcion;
		
	public double getLinea() {
		return linea;
	}

	public void setLinea(double linea) {
		this.linea = linea;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public CtaCteLineaCredito() {
	}

	public CtaCteLineaCredito(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public int compareTo(Object o) {
		CtaCteLineaCredito cmp = (CtaCteLineaCredito) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.id)==0);
		   
		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
	}
	
	
}
