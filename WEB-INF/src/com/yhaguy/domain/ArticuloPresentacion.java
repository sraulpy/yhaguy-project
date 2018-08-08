package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class ArticuloPresentacion extends Domain {
	private String descripcion;
	private String observacion;
	private long unidad;
	private double peso;
	private Tipo unidadMedida;

	public Tipo getUnidadMedida() {
		return unidadMedida;
	}

	public void setUnidadMedida(Tipo unidadMedida) {
		this.unidadMedida = unidadMedida;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public long getUnidad() {
		return unidad;
	}

	public void setUnidad(long unidad) {
		this.unidad = unidad;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	@Override
	public int compareTo(Object o) {
		ArticuloPresentacion cmp = (ArticuloPresentacion) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
	}
}
