package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.domain.Usuario;

@SuppressWarnings("serial")
public class UsuarioPropiedades extends Domain {
	
	private Usuario usuario = new Usuario();
	private Deposito depositoParaFacturar = new Deposito();
	private Tipo modoVenta = new Tipo();
	private Tipo modoDesarrollador = new Tipo();

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Deposito getDepositoParaFacturar() {
		return depositoParaFacturar;
	}

	public void setDepositoParaFacturar(Deposito depositoParaFacturar) {
		this.depositoParaFacturar = depositoParaFacturar;
	}

	public Tipo getModoVenta() {
		return modoVenta;
	}

	public void setModoVenta(Tipo modoVenta) {
		this.modoVenta = modoVenta;
	}

	public Tipo getModoDesarrollador() {
		return modoDesarrollador;
	}

	public void setModoDesarrollador(Tipo modoDesarrollador) {
		this.modoDesarrollador = modoDesarrollador;
	}

}
