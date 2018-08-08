package com.yhaguy.gestion.contabilidad.retencion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class RetencionIvaDTO extends DTO {

	private Date fecha = new Date();
	private String numero = "";
	private double montoIvaIncluido = 0;
	private double montoIva = 0;
	private double montoRetencion = 0;
	private int porcentaje = Configuracion.PORCENTAJE_RETENCION;

	private MyArray timbrado;
	private MyPair estadoComprobante = new MyPair();
	private MyArray empresa;
	private MyArray proveedor = new MyArray();
	
	private List<RetencionIvaDetalleDTO> detalles = new ArrayList<RetencionIvaDetalleDTO>();
	
	@DependsOn("detalles")
	public double getTotalIva() {
		double out = 0;
		for (RetencionIvaDetalleDTO item : this.detalles) {
			out += item.getImporteIvaGs();
		}
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalRetencion() {
		double out = 0;
		for (RetencionIvaDetalleDTO item : this.detalles) {
			out += item.getImporteRetenido();
		}		
		return out;
	}
	
	/**
	 * @return el importe en letras..
	 */
	public String getImporteEnLetras() {
		return getMisc().numberToLetter(this.getTotalRetencion());
	}
	
	/**
	 * @return el importe total de facturas..
	 */
	public double getTotalImporteFacturas() {
		double out = 0;
		for (RetencionIvaDetalleDTO item : this.detalles) {
			out += item.getImporteFactura();
		}
		return out;
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

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public double getMontoIvaIncluido() {
		return montoIvaIncluido;
	}

	public void setMontoIvaIncluido(double montoIvaIncluido) {
		this.montoIvaIncluido = montoIvaIncluido;
	}

	public double getMontoIva() {
		return montoIva;
	}

	public void setMontoIva(double montoIva) {
		this.montoIva = montoIva;
	}

	public double getMontoRetencion() {
		return montoRetencion;
	}

	public void setMontoRetencion(double montoRetencion) {
		this.montoRetencion = montoRetencion;
	}

	public MyArray getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(MyArray timbrado) {
		this.timbrado = timbrado;
	}

	public MyPair getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(MyPair estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public MyArray getEmpresa() {
		return empresa;
	}

	public void setEmpresa(MyArray empresa) {
		this.empresa = empresa;
	}

	public List<RetencionIvaDetalleDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<RetencionIvaDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public MyArray getProveedor() {
		return proveedor;
	}

	public void setProveedor(MyArray proveedor) {
		if(proveedor.esNuevo() == false) {
			 long idEmp = (long) proveedor.getPos4();
			 MyArray emp = new MyArray();
			 emp.setId(idEmp);
			 this.setEmpresa(emp);
		}
		this.proveedor = proveedor;
	}

	public int getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(int porcentaje) {
		for (RetencionIvaDetalleDTO item : detalles) {
			item.setPorcentaje(porcentaje);
		}
		this.porcentaje = porcentaje;
	}
}
