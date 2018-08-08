package com.yhaguy.gestion.caja.principal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

@SuppressWarnings("serial")
public class CajaDTO extends DTO{
		
	private String numero = "";
	private String descripcion = "Sin desc..";
	private Date fecha = new Date();
	
	private MyPair clasificacion = new MyPair();
	private MyPair tipo = new MyPair();
	private MyPair estado = new MyPair();
	private MyArray responsable = new MyArray();
	private MyArray creador = new MyArray();
	private MyPair duracion = new MyPair();
	private MyPair sucursal = new MyPair();
	private List<MyArray> supervisores = new ArrayList<MyArray>();
	
	private double fondo = 0;
	
	// acciones permitidas..
	private boolean cobro = true;
	private boolean reposicion = true;
	private boolean facturacion = true;
	private boolean pago = true;
	private boolean gasto = true;
	private boolean egreso = true;
	private boolean notaCredito = true;
	
	private MyArray talonarioVentas;
	private MyArray talonarioNotasCredito;
	private MyArray talonarioAutoFacturas;
	private MyArray talonarioRecibos;
	private MyArray talonarioRetenciones;
	
	public String getNumero() {
		return numero;
	}
	
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.toUpperCase();
	}
	
	public Date getFecha() {
		return fecha;
	}
	
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public MyPair getClasificacion() {
		return clasificacion;
	}
	
	public void setClasificacion(MyPair clasificacion) {
		this.clasificacion = clasificacion;
	}
	
	public MyPair getTipo() {
		return tipo;
	}
	
	public void setTipo(MyPair tipo) {
		this.tipo = tipo;
	}
	
	public MyPair getEstado() {
		return estado;
	}
	
	public void setEstado(MyPair estado) {
		this.estado = estado;
	}
	
	public MyArray getResponsable() {
		return responsable;
	}
	
	public void setResponsable(MyArray responsable) {
		this.responsable = responsable;
	}
	
	public MyArray getCreador() {
		return creador;
	}
	
	public void setCreador(MyArray creador) {
		this.creador = creador;
	}
	
	public MyPair getDuracion() {
		return duracion;
	}
	
	public void setDuracion(MyPair duracion) {
		this.duracion = duracion;
	}
	
	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}

	public List<MyArray> getSupervisores() {
		return supervisores;
	}
	
	public void setSupervisores(List<MyArray> supervisores) {
		this.supervisores = supervisores;
	}	

	public double getFondo() {
		return fondo;
	}

	public void setFondo(double fondo) {
		this.fondo = fondo;
	}

	public MyArray getTalonarioVentas() {
		return talonarioVentas;
	}

	public void setTalonarioVentas(MyArray talonario) {
		this.talonarioVentas = talonario;
	}

	public MyArray getTalonarioNotasCredito() {
		return talonarioNotasCredito;
	}

	public void setTalonarioNotasCredito(MyArray talonarioNotasCredito) {
		this.talonarioNotasCredito = talonarioNotasCredito;
	}
	
	public String toString(){
		return this.getNumero() + " - " + this.getSucursal().getText() + " - " + this.getTipo().getText() + " - " + this.getFecha();
	}

	public MyArray getTalonarioAutoFacturas() {
		return talonarioAutoFacturas;
	}

	public void setTalonarioAutoFacturas(MyArray talonarioAutoFacturas) {
		this.talonarioAutoFacturas = talonarioAutoFacturas;
	}

	public MyArray getTalonarioRecibos() {
		return talonarioRecibos;
	}

	public void setTalonarioRecibos(MyArray talonarioRecibos) {
		this.talonarioRecibos = talonarioRecibos;
	}

	public MyArray getTalonarioRetenciones() {
		return talonarioRetenciones;
	}

	public void setTalonarioRetenciones(MyArray talonarioRetenciones) {
		this.talonarioRetenciones = talonarioRetenciones;
	}

	public boolean isCobro() {
		return cobro;
	}

	public void setCobro(boolean cobro) {
		this.cobro = cobro;
	}

	public boolean isReposicion() {
		return reposicion;
	}

	public void setReposicion(boolean reposicion) {
		this.reposicion = reposicion;
	}

	public boolean isFacturacion() {
		return facturacion;
	}

	public void setFacturacion(boolean facturacion) {
		this.facturacion = facturacion;
	}

	public boolean isPago() {
		return pago;
	}

	public void setPago(boolean pago) {
		this.pago = pago;
	}

	public boolean isGasto() {
		return gasto;
	}

	public void setGasto(boolean gasto) {
		this.gasto = gasto;
	}

	public boolean isEgreso() {
		return egreso;
	}

	public void setEgreso(boolean egreso) {
		this.egreso = egreso;
	}

	public boolean isNotaCredito() {
		return notaCredito;
	}

	public void setNotaCredito(boolean notaCredito) {
		this.notaCredito = notaCredito;
	}
}
