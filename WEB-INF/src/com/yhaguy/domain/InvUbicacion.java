package com.yhaguy.domain;

import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class InvUbicacion extends Domain{
	
	private String codigo;
	private boolean estante = true;
	
	private Set<InvArticulo> invArticulo = new HashSet<InvArticulo>();
	
	
	public Set<InvArticulo> getInvArticulo() {
		return invArticulo;
	}



	public void setInvArticulo(Set<InvArticulo> invArticulo) {
		this.invArticulo = invArticulo;
	}



	public String getCodigo() {
		return codigo;
	}



	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}



	public boolean isEstante() {
		return estante;
	}



	public void setEstante(boolean estante) {
		this.estante = estante;
	}



	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
