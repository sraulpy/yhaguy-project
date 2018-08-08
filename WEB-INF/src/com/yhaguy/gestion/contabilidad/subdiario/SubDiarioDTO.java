package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;

public class SubDiarioDTO extends DTO {

	private String numero = "";
	private Date fecha = new Date();
	private String descripcion = "";
	private boolean confirmado = false;
	private double totalGastos = 0;
	private MyPair sucursal = new MyPair();
	
	private List<SubDiarioDetalleDTO> detalles = new ArrayList<SubDiarioDetalleDTO>();
	private List<SubDiarioDetalleDTO> detallesTemporal = new ArrayList<SubDiarioDetalleDTO>();

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
		this.descripcion = descripcion.trim().toUpperCase();
	}

	public boolean isConfirmado() {
		return confirmado;
	}

	public void setConfirmado(boolean confirmado) {
		this.confirmado = confirmado;
	}

	public double getTotalGastos() {
		return totalGastos;
	}

	public void setTotalGastos(double totalGastos) {
		this.totalGastos = totalGastos;
	}

	public List<SubDiarioDetalleDTO> getDetalles() {
		Collections.sort(detalles, new SubDiarioDetalleDTO());
		return detalles;
	}

	public void setDetalles(List<SubDiarioDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public List<SubDiarioDetalleDTO> getDetallesTemporal() {
		return detallesTemporal;
	}

	public void setDetallesTemporal(List<SubDiarioDetalleDTO> detallesTemporal) {
		this.detallesTemporal = detallesTemporal;
	}

	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}	
	
	public String getSistema(){
		return (this.getUsuarioMod()+" : "+this.getAuxi());
	} 
	
	public boolean getEsManual(){
		if(this.getAuxi().equals(Configuracion.SUBDIARIO_CARGA_MANUAL)){
			return true;
		}
		return false;
	}
}
