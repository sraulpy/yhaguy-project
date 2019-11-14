package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class RepartoDetalle extends Domain {

	private Long idMovimiento;
	private String observacion;
	private double peso;
	private double importeGs;
	private boolean entregado;
	private TipoMovimiento tipoMovimiento;
	
	/**
	 * @return si es venta o no segun la sigla..
	 */
	public boolean isVenta() {
		String sigla = this.tipoMovimiento.getSigla();
		return sigla.equals(Configuracion.SIGLA_TM_PEDIDO_VENTA)
				|| sigla.equals(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO)
				|| sigla.equals(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
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

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
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

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

}
