package com.yhaguy.gestion.transferencia;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;

@SuppressWarnings("serial")
public class TransferenciaDetalleDTO extends DTO {
	
	static final String ESTADO_PENDIENTE = "Pendiente";
	static final String ESTADO_RESERVADO = "Reservado";
	static final String ESTADO_CONFIRMADO = "Confirmado";
	static final String ESTADO_RECEPCIONADO = "Recibido";
	
	private int cantidad;

	private long cantidadPedida = 0;
	private long cantidadEnviada = 0;
	private long cantidadRecibida = 0;
	private double costo = 0;
	
	private String estado = ESTADO_PENDIENTE;
	private MyArray articulo = new MyArray();
	
	private boolean editado = false;
	
	@DependsOn({"cantidad", "costo"})
	public double getCostoTotal() {
		return this.cantidad * costo;
	}
	
	@DependsOn("estado")
	public String getStyle() {
		String color = "red";
		
		switch (this.estado) {
		
		case ESTADO_RESERVADO:
			color = "orange";
			break;

		case ESTADO_CONFIRMADO:
			color = "green";
			break;
			
		case ESTADO_RECEPCIONADO:
			color = "green";
			break;
		}
		
		if(this.isEditado())
			color = "red";
		
		return "color:" + color;
	}
	
	@DependsOn({"estado", "cantidad", "cantidadRecibida"})
	public String getStyleRecibida() {
		if(!this.estado.equals(ESTADO_RECEPCIONADO))
			return "";
		boolean menos = this.cantidad > this.cantidadRecibida;
		return menos? "color:red" : "";
	}
	
	@DependsOn("articulo.pos3")
	public long getStockDisponible() {
		return this.articulo.esNuevo() ? 0 : (long) this.articulo.getPos3();
	}
	
	@DependsOn("estado")
	public boolean isReservado() {
		return this.estado.equals(ESTADO_RESERVADO);
	}
	
	@DependsOn("estado")
	public String getEstado(boolean transferenciaInterna) {
		if(!transferenciaInterna)
			return this.getEstadoExt();
		return this.estado;
	}
	
	private String getEstadoExt() {
		return this.estado;
	}

	public long getCantidadPedida() {
		return cantidadPedida;
	}

	public void setCantidadPedida(long cantidadPedida) {
		this.cantidadPedida = cantidadPedida;
	}

	public long getCantidadEnviada() {
		return cantidadEnviada;
	}

	public void setCantidadEnviada(long cantidadEnviada) {
		this.cantidadEnviada = cantidadEnviada;
	}

	public long getCantidadRecibida() {
		return cantidadRecibida;
	}

	public void setCantidadRecibida(long cantidadRecibida) {
		this.cantidadRecibida = cantidadRecibida;
	}

	public MyArray getArticulo() {
		return articulo;
	}

	public void setArticulo(MyArray articulo) {
		this.articulo = articulo;
	}

	public double getCosto() {
		return costo;
	}

	public void setCosto(double costo) {
		this.costo = costo;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public boolean isEditado() {
		return editado;
	}

	public void setEditado(boolean editado) {
		this.editado = editado;
	}	
}
