package com.yhaguy.domain;

import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloMarcaAplicacion extends Domain {

	private String descripcion;
	private String sigla;
	Set<ArticuloModeloAplicacion> articuloModeloAplicaciones = new HashSet<ArticuloModeloAplicacion>();
	


	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Set<ArticuloModeloAplicacion> getArticuloModeloAplicaciones() {
		return articuloModeloAplicaciones;
	}

	public void setArticuloModeloAplicaciones(
			Set<ArticuloModeloAplicacion> articuloModeloAplicaciones) {
		this.articuloModeloAplicaciones = articuloModeloAplicaciones;
	}

	@Override
	public int compareTo(Object o) {
		ArticuloMarcaAplicacion cmp = (ArticuloMarcaAplicacion) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.id)==0);
		   
		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
	}
	
	public String toString(){
		String out = "("+this.getId()+") "+ this.descripcion;
		return out;
	}

}
