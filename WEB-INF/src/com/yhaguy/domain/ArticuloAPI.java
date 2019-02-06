package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloAPI extends Domain {

	/**
	 * Certificación API en Lubricantes. El sello de Certificación y el Símbolo de
	 * Servicio de API identifican la calidad de los aceites de motor para vehículos
	 * de gasolina y diésel. ... Por ejemplo, si el manual de propietario del
	 * automóvil indica un aceite API SL o SM, un aceite API SN le brindará una
	 * protección completa.
	 */

	private String descripcion;

	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
