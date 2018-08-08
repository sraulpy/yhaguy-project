package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class AjusteStock extends Domain {

	private Date fecha;
	private String numero;
	private String descripcion;
	private String autorizadoPor;
	
	private SucursalApp sucursal;
	private TipoMovimiento tipoMovimiento;
	private Tipo estadoComprobante;
	private Deposito deposito;
	private Set<AjusteStockDetalle> detalles = new HashSet<AjusteStockDetalle>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	public String getContador() {
		return this.getOrden();
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNumero() {
		return numero;
	}
	
	public String getNumero_() {
		return numero.replace("INVENTARIO-", "");
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public Set<AjusteStockDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<AjusteStockDetalle> detalles) {
		this.detalles = detalles;
	}

	public Deposito getDeposito() {
		return deposito;
	}

	public void setDeposito(Deposito deposito) {
		this.deposito = deposito;
	}

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public Tipo getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(Tipo estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public String getAutorizadoPor() {
		return autorizadoPor;
	}

	public void setAutorizadoPor(String autorizadoPor) {
		this.autorizadoPor = autorizadoPor;
	}
}
