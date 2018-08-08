package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class CtaCteEmpresa extends Domain{
	
	/*
	 * Los estados pueden ser:
	 * 	- Activo
	 * 	- Inactivo
	 * 	- Bloqueado
	 * 	- Sin Cta. Cte.
	 */
	private Tipo estadoComoCliente;
	private Tipo estadoComoProveedor;
	private CtaCteLineaCredito lineaCredito;
	private CondicionPago condicionPagoCliente;
	private Date fechaAperturaCuentaCliente;
	private Date fechaAperturaCuentaProveedor;
	
	public Tipo getEstadoComoCliente() {
		return estadoComoCliente;
	}
	public void setEstadoComoCliente(Tipo estadoComoCliente) {
		this.estadoComoCliente = estadoComoCliente;
	}
	public Tipo getEstadoComoProveedor() {
		return estadoComoProveedor;
	}
	public void setEstadoComoProveedor(Tipo estadoComoProveedor) {
		this.estadoComoProveedor = estadoComoProveedor;
	}
	public CtaCteLineaCredito getLineaCredito() {
		return lineaCredito;
	}
	public void setLineaCredito(CtaCteLineaCredito lineaCredito) {
		this.lineaCredito = lineaCredito;
	}
	
	public CondicionPago getCondicionPagoCliente() {
		return condicionPagoCliente;
	}
	public void setCondicionPagoCliente(CondicionPago condicionPagoCliente) {
		this.condicionPagoCliente = condicionPagoCliente;
	}

	public Date getFechaAperturaCuentaCliente() {
		return fechaAperturaCuentaCliente;
	}
	public void setFechaAperturaCuentaCliente(Date fechaAperturaCuentaCliente) {
		this.fechaAperturaCuentaCliente = fechaAperturaCuentaCliente;
	}

	public Date getFechaAperturaCuentaProveedor() {
		return fechaAperturaCuentaProveedor;
	}
	public void setFechaAperturaCuentaProveedor(Date fechaAperturaCuentaProveedor) {
		this.fechaAperturaCuentaProveedor = fechaAperturaCuentaProveedor;
	}
	@Override
	public int compareTo(Object o) {
		CtaCteEmpresa cmp = (CtaCteEmpresa) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.id) == 0);
		
		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}		
	}
	
}
