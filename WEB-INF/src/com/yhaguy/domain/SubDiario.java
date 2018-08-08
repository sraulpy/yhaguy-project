package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class SubDiario extends Domain{

	private String numero;
	private Date fecha;
	private String descripcion;
	private boolean confirmado;
	
	private SucursalApp sucursal;
	
	private Set<SubDiarioDetalle> detalles = new HashSet<SubDiarioDetalle>();
	
	
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}	

	public boolean isConfirmado() {
		return confirmado;
	}

	public void setConfirmado(boolean confirmado) {
		this.confirmado = confirmado;
	}

	public Set<SubDiarioDetalle> getDetalles() {
		return detalles;
	}
	
	public void setDetalles(Set<SubDiarioDetalle> detalles) {
		this.detalles = detalles;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}
	
	@Override
	public int compareTo(Object o) {
		SubDiario cmp = (SubDiario) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.getId()) == 0);
		
		if (isOk) {
			return 0;
		} else {
			return -1;
		}
	}
}
