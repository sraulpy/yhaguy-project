package com.yhaguy.gestion.reparto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class RepartoDTO extends DTO {

	private String numero = "";
	private Date fechaCreacion;
	private Date fechaRecepcion;
	private String observaciones = "Sin obs..";
	private double costo = 0;
	private MyPair estadoReparto = new MyPair();
	private MyPair tipoReparto = new MyPair();
	private MyArray vehiculo = new MyArray();
	private MyPair repartidor = new MyPair();
	private MyArray creador = new MyArray();
	private MyArray receptor = new MyArray();
	private MyPair sucursal = new MyPair();

	private MyArray proveedor = new MyArray();

	private List<RepartoDetalleDTO> detalles = new ArrayList<RepartoDetalleDTO>();
	private List<RepartoDetalleDTO> itemsEliminados = new ArrayList<RepartoDetalleDTO>();
	private List<MyArray> serviciosTecnicos = new ArrayList<MyArray>();
	
	@DependsOn("serviciosTecnicos")
	public String getServiciosTecnicos_() {
		String out = "";
		for (MyArray servtec : this.serviciosTecnicos) {
			out += servtec.getPos1().toString().replace("SER-TEC-", "") + " - ";
		}
		return out;
	}
	
	@DependsOn("estadoReparto")
	public boolean isPreparacion() {
		String sigla = this.estadoReparto.getSigla();
		return sigla.equals(Configuracion.SIGLA_ESTADO_REP_PREPARACION);
	}
	
	@DependsOn("estadoReparto")
	public boolean isEnTransito() {
		String sigla = this.estadoReparto.getSigla();
		return sigla.equals(Configuracion.SIGLA_ESTADO_REP_TRANSITO);
	}
	
	@DependsOn("estadoReparto")
	public boolean isEntregado() {
		String sigla = this.estadoReparto.getSigla();
		return sigla.equals(Configuracion.SIGLA_ESTADO_REP_ENTREGADO);
	}
	
	/**
	 * @return las entregas de remisiones..
	 */
	public List<RepartoDetalleDTO> getEntregas(boolean entregado) {
		List<RepartoDetalleDTO> out = new ArrayList<RepartoDetalleDTO>();
		for (RepartoDetalleDTO item : this.detalles) {
			if(item.isEntregado() == entregado)
				out.add(item);
		}
		return out;
	}
	
	/**
	 * @return los items que son ventas..
	 */
	public List<RepartoDetalleDTO> getDetallesVentas() {
		List<RepartoDetalleDTO> out = new ArrayList<RepartoDetalleDTO>();
		for (RepartoDetalleDTO item : this.detalles) {
			if(item.isVenta())
				out.add(item);
		}
		return out;
	}
	
	@DependsOn("detalles")
	public double getPesoTotal() {
		double out = 0;
		for (RepartoDetalleDTO item : this.detalles) {
			out += item.getPeso();
		}
		return out;
	}
	
	@DependsOn({ "detalles", "vehiculo" })
	public boolean isCapacidadMenor() {
		if (this.vehiculo.esNuevo()) {
			return false;
		}
		return this.getPesoTotal() > (double) this.vehiculo.getPos5();
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	public MyArray getReceptor() {
		return receptor;
	}

	public void setReceptor(MyArray receptor) {
		this.receptor = receptor;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public double getCosto() {
		return costo;
	}

	public void setCosto(double costo) {
		this.costo = costo;
	}

	public MyPair getEstadoReparto() {
		return estadoReparto;
	}

	public void setEstadoReparto(MyPair estadoReparto) {
		this.estadoReparto = estadoReparto;
	}

	public MyPair getTipoReparto() {
		return tipoReparto;
	}

	public void setTipoReparto(MyPair tipoReparto) {
		this.tipoReparto = tipoReparto;
	}

	public MyArray getVehiculo() {
		return vehiculo;
	}

	public void setVehiculo(MyArray vehiculo) {
		this.vehiculo = vehiculo;
	}

	public MyArray getCreador() {
		return creador;
	}

	public MyPair getRepartidor() {
		return repartidor;
	}

	public void setRepartidor(MyPair repartidor) {
		this.repartidor = repartidor;
	}

	public void setCreador(MyArray creador) {
		this.creador = creador;
	}

	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}

	public MyArray getProveedor() {
		return proveedor;
	}

	public void setProveedor(MyArray proveedor) {
		this.proveedor = proveedor;
	}

	public List<RepartoDetalleDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<RepartoDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public List<RepartoDetalleDTO> getItemsEliminados() {
		return itemsEliminados;
	}

	public void setItemsEliminados(List<RepartoDetalleDTO> itemsEliminados) {
		this.itemsEliminados = itemsEliminados;
	}

	public List<MyArray> getServiciosTecnicos() {
		return serviciosTecnicos;
	}

	public void setServiciosTecnicos(List<MyArray> serviciosTecnicos) {
		this.serviciosTecnicos = serviciosTecnicos;
	}
}
