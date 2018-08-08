package com.yhaguy.domain;

import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class InvArticulo extends Domain{
	
	private String codigoInterno;
	private String codigoProveedor;
	private String codigoOriginal;
	private String descripcion;
	private String marca;
	
	private String linea;
	private String familia;
	private String grupo;
	private String controlStock;
	private String estado;
	
	private long stock = -1;;
	private double costo = -1;;
	private long ajuste = -1;
	private long inventario = -1;
	private Set<InvUbicacion> invUbicacion = new HashSet<InvUbicacion>();
	
	
	public void addToInvUbicacion(InvUbicacion ubi){
		this.getInvUbicacion().add(ubi);
		ubi.getInvArticulo().add(this);
	}
	
	public String getMarca() {
		return marca;
	}


	public void setMarca(String marca) {
		this.marca = marca;
	}


	public String getCodigoInterno() {
		return codigoInterno;
	}


	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}


	public String getCodigoProveedor() {
		return codigoProveedor;
	}


	public void setCodigoProveedor(String codigoProveedor) {
		this.codigoProveedor = codigoProveedor;
	}


	public String getCodigoOriginal() {
		return codigoOriginal;
	}


	public void setCodigoOriginal(String codigoOriginal) {
		this.codigoOriginal = codigoOriginal;
	}


	public long getInventario() {
		return inventario;
	}


	public void setInventario(long inventario) {
		this.inventario = inventario;
	}


	public long getAjuste() {
		return ajuste;
	}


	public void setAjuste(long ajuste) {
		this.ajuste = ajuste;
	}


	private Set<InvUbicacion> getInvUbicacion() {
		return invUbicacion;
	}


	public void setInvUbicacion(Set<InvUbicacion> invUbicacion) {
		this.invUbicacion = invUbicacion;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public long getStock() {
		return stock;
	}


	public void setStock(long stock) {
		this.stock = stock;
	}


	public double getCosto() {
		return costo;
	}


	public void setCosto(double costo) {
		this.costo = costo;
	}


	public String getLinea() {
		return linea;
	}


	public void setLinea(String linea) {
		this.linea = linea;
	}


	public String getFamilia() {
		return familia;
	}


	public void setFamilia(String familia) {
		this.familia = familia;
	}


	public String getGrupo() {
		return grupo;
	}


	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}


	public String getControlStock() {
		return controlStock;
	}


	public void setControlStock(String controlStock) {
		this.controlStock = controlStock;
	}


	public String getEstado() {
		return estado;
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}


	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
