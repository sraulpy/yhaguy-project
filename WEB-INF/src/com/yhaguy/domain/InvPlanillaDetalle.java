package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class InvPlanillaDetalle extends Domain{
	
	private String observacion;
	private long aptos;
	private long averiados;
	private long caja;
	private InvArticulo invArticulo;
	private boolean original = true;
	private boolean existe = false;
	private InvUbicacion invUbicacion;
	private long total;
	

	
	public long getTotal() {
		return total;
	}


	public void setTotal(long total) {
		this.total = total;
	}


	public InvUbicacion getInvUbicacion() {
		return invUbicacion;
	}


	public void setInvUbicacion(InvUbicacion invUbicacion) {
		this.invUbicacion = invUbicacion;
	}


	public boolean isExiste() {
		return existe;
	}


	public void setExiste(boolean existe) {
		this.existe = existe;
	}


	public boolean isOriginal() {
		return original;
	}


	public void setOriginal(boolean original) {
		this.original = original;
	}


	public String getObservacion() {
		return observacion;
	}


	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}


	public long getAptos() {
		return aptos;
	}


	public void setAptos(long aptos) {
		this.aptos = aptos;
	}


	public long getCaja() {
		return caja;
	}


	public void setCaja(long caja) {
		this.caja = caja;
	}


	public InvArticulo getInvArticulo() {
		return invArticulo;
	}


	public void setInvArticulo(InvArticulo invArticulo) {
		this.invArticulo = invArticulo;
	}


	public long getAveriados() {
		return averiados;
	}


	public void setAveriados(long averiados) {
		this.averiados = averiados;
	}


	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
