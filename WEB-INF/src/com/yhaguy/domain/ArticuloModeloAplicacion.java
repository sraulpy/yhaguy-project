package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloModeloAplicacion extends Domain {

	private String descripcion;
	private String tipo;
	private ArticuloMarcaAplicacion articuloMarcaAplicacion;


	public ArticuloMarcaAplicacion getArticuloMarcaAplicacion() {
		return articuloMarcaAplicacion;
	}

	public void setArticuloMarcaAplicacion(
			ArticuloMarcaAplicacion articuloMarcaAplicacion) {
		this.articuloMarcaAplicacion = articuloMarcaAplicacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Override
	public int compareTo(Object o) {
		ArticuloModeloAplicacion cmp = (ArticuloModeloAplicacion) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}
}
