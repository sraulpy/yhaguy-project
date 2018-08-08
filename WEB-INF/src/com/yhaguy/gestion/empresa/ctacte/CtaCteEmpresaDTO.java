package com.yhaguy.gestion.empresa.ctacte;

import java.util.Date;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

public class CtaCteEmpresaDTO extends DTO{
	
	private Date fechaAperturaCuentaCliente;
	private Date fechaAperturaCuentaProveedor;
	private MyPair estadoComoCliente;
	private MyPair estadoComoProveedor;
	private MyArray lineaCredito = new MyArray();
	private MyArray condicionPagoCliente = new MyArray();

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
	public MyPair getEstadoComoCliente() {
		return estadoComoCliente;
	}
	public void setEstadoComoCliente(MyPair estadoComoCliente) {
		this.estadoComoCliente = estadoComoCliente;
	}
	public MyPair getEstadoComoProveedor() {
		return estadoComoProveedor;
	}
	public void setEstadoComoProveedor(MyPair estadoComoProveedor) {
		this.estadoComoProveedor = estadoComoProveedor;
	}
	public MyArray getLineaCredito() {
		return lineaCredito;
	}
	public void setLineaCredito(MyArray lineaCredito) {
		this.lineaCredito = lineaCredito;
	}
	public MyArray getCondicionPagoCliente() {
		return condicionPagoCliente;
	}
	public void setCondicionPagoCliente(MyArray condicionPagoCliente) {
		this.condicionPagoCliente = condicionPagoCliente;
	}
	

}
