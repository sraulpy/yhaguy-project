package com.yhaguy.gestion.stock.inventarios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class InventarioDTO extends DTO {

	private Date fecha = new Date();
	private String numero = "...";
	private String descripcion = "";
	private String autorizadoPor = "...";

	private MyPair sucursal;
	private MyArray tipoMovimiento;
	private MyPair estadoComprobante;
	private MyPair deposito;
	private List<InventarioDetalleDTO> detalles = new ArrayList<InventarioDetalleDTO>();
	
	@DependsOn("detalles")
	public double getTotalCostoGs() {
		double out = 0;
		for (InventarioDetalleDTO item : this.detalles) {
			out += item.getTotalCostoGs();
		}
		return out;
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

	public List<InventarioDetalleDTO> getDetalles() {
		Collections.sort(this.detalles, new Comparator<InventarioDetalleDTO>() {
			@Override
			public int compare(InventarioDetalleDTO o1,
					InventarioDetalleDTO o2) {
				return (int) (o1.getId().longValue() - o2.getId().longValue());
			}
		});
		return detalles;
	}

	public void setDetalles(List<InventarioDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public String getAutorizadoPor() {
		return autorizadoPor;
	}

	public void setAutorizadoPor(String autorizadoPor) {
		this.autorizadoPor = autorizadoPor;
	}
}
