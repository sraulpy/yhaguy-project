package com.yhaguy.gestion.notadebito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.empresa.ClienteDTO;

@SuppressWarnings("serial")
public class NotaDebitoDTO extends DTO {

	private String numero = "";
	private String timbrado = "";
	private Date fecha = new Date();
	private String numeroFactura = "";

	private MyPair sucursal = new MyPair();
	private MyArray tipoMovimiento = new MyArray();
	private MyPair estadoComprobante = new MyPair();
	private ClienteDTO cliente = new ClienteDTO();
	private List<NotaDebitoDetalleDTO> detalles = new ArrayList<NotaDebitoDetalleDTO>();
	
	/**
	 * @return el importe total
	 */
	public double getTotalImporteGs() {
		double out = 0;
		for (NotaDebitoDetalleDTO item : this.detalles) {
			out += item.getImporteGs();
		}
		return out;
	}

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

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}

	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public MyPair getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(MyPair estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public ClienteDTO getCliente() {
		return cliente;
	}

	public void setCliente(ClienteDTO cliente) {
		this.cliente = cliente;
	}

	public List<NotaDebitoDetalleDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<NotaDebitoDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public String getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(String timbrado) {
		this.timbrado = timbrado;
	}
}
