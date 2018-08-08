package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.yhaguy.Configuracion;


@SuppressWarnings("serial")
public class CondicionPago extends Domain {
	private String descripcion;
	private int plazo;			 // dias de vencimiento
	private int cuotas;			 // cuotas por defecto
	private int diasEntreCuotas; // dias entre cuotas;
	
	/**
	 * @return true si la condicion es contado..
	 */
	public boolean isCondicionContado() {
		return (this.getId().longValue() == Configuracion.ID_CONDICION_PAGO_CONTADO);
	}

	public String getDescripcion() {
		return descripcion.toUpperCase();
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getPlazo() {
		return plazo;
	}

	public void setPlazo(int plazo) {
		this.plazo = plazo;
	}
	
	public int getCuotas() {
		return cuotas;
	}

	public void setCuotas(int cuotas) {
		this.cuotas = cuotas;
	}

	public int getDiasEntreCuotas() {
		return diasEntreCuotas;
	}

	public void setDiasEntreCuotas(int diasEntreCuotas) {
		this.diasEntreCuotas = diasEntreCuotas;
	}

	@Override
	public int compareTo(Object o) {
		CondicionPago cmp = (CondicionPago) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}	
}
