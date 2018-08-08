package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class CajaReposicion extends Domain{
	
	private String numero;
	private String responsable;
	private boolean funcionario;
	private Date fechaEmision;
	private double tipoCambio;
	private double montoGs;
	private double montoDs;
	private String observacion;
	private String motivoAnulacion;
	private Tipo moneda;
	
	private Funcionario funcionarioAsignado;
	
	private ReciboFormaPago formaPago;
	
	/** Ingreso - Egreso **/
	private Tipo tipo;
	
	/** Egreso sin comprobante - Egreso por vale **/
	private Tipo tipoEgreso;
	
	/** El estado del comprobante **/
	private Tipo estadoComprobante;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return true si es anulado..
	 */
	public boolean isAnulado() {
		if(this.estadoComprobante == null)
			return false;
		String sigla = this.estadoComprobante.getSigla();
		return sigla.equals(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO);
	}
	
	/**
	 * @return true si es un ingreso..
	 */
	public boolean isIngreso() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_CAJA_REPOSICION_INGRESO);
	}

	public String getResponsable() {
		return responsable.toUpperCase();
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public boolean isFuncionario() {
		return funcionario;
	}

	public void setFuncionario(boolean funcionario) {
		this.funcionario = funcionario;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public double getMontoGs() {
		return montoGs;
	}

	public void setMontoGs(double montoGs) {
		this.montoGs = montoGs;
	}

	public double getMontoDs() {
		return montoDs;
	}

	public void setMontoDs(double montoDs) {
		this.montoDs = montoDs;
	}

	public String getObservacion() {
		return observacion.toUpperCase();
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}	

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}	

	public Date getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(Date fecha) {
		this.fechaEmision = fecha;
	}
	
	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public Tipo getTipoEgreso() {
		return tipoEgreso;
	}

	public void setTipoEgreso(Tipo tipoEgreso) {
		this.tipoEgreso = tipoEgreso;
	}

	public Tipo getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(Tipo estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}	

	public String getMotivoAnulacion() {
		return motivoAnulacion;
	}

	public void setMotivoAnulacion(String motivoAnulacion) {
		this.motivoAnulacion = motivoAnulacion;
	}

	public Funcionario getFuncionarioAsignado() {
		return funcionarioAsignado;
	}

	public void setFuncionarioAsignado(Funcionario funcionario) {
		this.funcionarioAsignado = funcionario;
	}

	public ReciboFormaPago getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(ReciboFormaPago formaPago) {
		this.formaPago = formaPago;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
}
