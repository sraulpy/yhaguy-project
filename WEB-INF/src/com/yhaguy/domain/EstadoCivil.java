package com.yhaguy.domain;
import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class EstadoCivil extends Domain{

	private String descripcion;

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public EstadoCivil() {
	}

	public EstadoCivil(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public int compareTo(Object o) {
		EstadoCivil cmp = (EstadoCivil) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.id)==0);
		   
		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
	}
	
	
}
