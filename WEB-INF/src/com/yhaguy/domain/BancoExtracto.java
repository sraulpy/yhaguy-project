package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class BancoExtracto extends Domain {
	
	private String numero;
	private Date desde;
	private Date hasta;
	private Set<BancoExtractoDetalle> detalles2 = new HashSet<BancoExtractoDetalle>();
	private boolean cerrado;	

	private BancoCta banco;
	private SucursalApp sucursal;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public BancoCta getBanco() {
		return banco;
	}

	public void setBanco(BancoCta banco) {
		this.banco = banco;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public boolean isCerrado() {
		return cerrado;
	}

	public void setCerrado(boolean cerrado) {
		this.cerrado = cerrado;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public Set<BancoExtractoDetalle> getDetalles2() {
		return detalles2;
	}

	public void setDetalles2(Set<BancoExtractoDetalle> detalles2) {
		this.detalles2 = detalles2;
	}
}
