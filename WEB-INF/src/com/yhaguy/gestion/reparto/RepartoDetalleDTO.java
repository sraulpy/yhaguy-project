package com.yhaguy.gestion.reparto;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class RepartoDetalleDTO extends DTO {

	private Long idMovimiento;
	private String observacion = "";
	private double peso = 0;
	private MyArray tipoMovimiento = new MyArray();
 
	private MyArray detalle = new MyArray();
	private boolean entregado = true;
	
	/**
	 * @return si es venta o no segun la sigla..
	 */
	public boolean isVenta() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_PEDIDO_VENTA)
				|| sigla.equals(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO)
				|| sigla.equals(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
	}
	
	@DependsOn("entregado")
	public String getStyle() {
		String style = "text-decoration: line-through; " + Misc.BACKGROUND_NARANJA;
		return this.isEntregado()? "" : style;
	}

	public Long getIdMovimiento() {
		return idMovimiento;
	}

	public void setIdMovimiento(Long idMovimiento) {
		this.idMovimiento = idMovimiento;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public MyArray getDetalle() {
		return detalle;
	}

	public void setDetalle(MyArray datosDetalle) {
		this.detalle = datosDetalle;
	}

	public boolean isEntregado() {
		return entregado;
	}

	public void setEntregado(boolean entregado) {
		this.entregado = entregado;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}
}
