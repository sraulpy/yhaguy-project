package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class Identificaciones extends Domain {

	private String per_nombres;
	private String per_apellidos;
	private String per_nrodocumento;
	private String per_fecha_nac;
	private String per_nacionalidad;
	private String per_profesion;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getPer_nombres() {
		return per_nombres;
	}

	public void setPer_nombres(String per_nombres) {
		this.per_nombres = per_nombres;
	}

	public String getPer_apellidos() {
		return per_apellidos;
	}

	public void setPer_apellidos(String per_apellidos) {
		this.per_apellidos = per_apellidos;
	}

	public String getPer_nrodocumento() {
		return per_nrodocumento;
	}

	public void setPer_nrodocumento(String per_nrodocumento) {
		this.per_nrodocumento = per_nrodocumento;
	}

	public String getPer_fecha_nac() {
		return per_fecha_nac;
	}

	public void setPer_fecha_nac(String per_fecha_nac) {
		this.per_fecha_nac = per_fecha_nac;
	}

	public String getPer_nacionalidad() {
		return per_nacionalidad;
	}

	public void setPer_nacionalidad(String per_nacionalidad) {
		this.per_nacionalidad = per_nacionalidad;
	}

	public String getPer_profesion() {
		return per_profesion;
	}

	public void setPer_profesion(String per_profesion) {
		this.per_profesion = per_profesion;
	}
}
