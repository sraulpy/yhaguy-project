package com.yhaguy.gestion.stock.ajustes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class AjusteStockDTO extends DTO {

	private Date fecha = new Date();
	private String numero = "...";
	private String descripcion = "";
	private String autorizadoPor = "...";

	private MyPair sucursal;
	private MyArray tipoMovimiento;
	private MyPair estadoComprobante;
	private MyPair deposito;
	private List<AjusteStockDetalleDTO> detalles = new ArrayList<AjusteStockDetalleDTO>();
	
	//para que el assembler sepa que debe actualizar el stock..
	private boolean actualizarStock = false;
	
	@DependsOn("detalles")
	public double getTotalCostoGs() {
		double out = 0;
		for (AjusteStockDetalleDTO item : this.detalles) {
			out += item.getTotalCostoGs();
		}
		return out;
	}
	
	/**
	 * @return la url de la imagen..
	 */
	public String getUrlImagen() {
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			return Configuracion.URL_IMAGES_PUBLIC_MRA + "ajustes/" + this.getId() + ".png";
		}
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRPS)) {
			return Configuracion.URL_IMAGES_PUBLIC_RPS + "ajustes/" + this.getId() + ".png";
		}
		return Configuracion.URL_IMAGES_PUBLIC_BAT + "ajustes/" + this.getId() + ".png";
	}
	
	public boolean isAjustePositivo() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
	}
	
	public boolean isAjusteNegativo() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
	}
	
	public boolean isConfirmado() {
		String sigla = (String) this.getEstadoComprobante().getSigla();
		return sigla.equals(Configuracion.SIGLA_ESTADO_COMPROBANTE_CERRADO);
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.toUpperCase();
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

	public MyPair getDeposito() {
		return deposito;
	}

	public void setDeposito(MyPair deposito) {
		this.deposito = deposito;
	}

	public List<AjusteStockDetalleDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<AjusteStockDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public boolean isActualizarStock() {
		return actualizarStock;
	}

	public void setActualizarStock(boolean actualizarStock) {
		this.actualizarStock = actualizarStock;
	}

	public String getAutorizadoPor() {
		return autorizadoPor;
	}

	public void setAutorizadoPor(String autorizadoPor) {
		this.autorizadoPor = autorizadoPor;
	}
}
