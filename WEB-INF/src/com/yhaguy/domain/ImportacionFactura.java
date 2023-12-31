package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class ImportacionFactura extends Domain {

	private String numero;	
	private Date fechaOriginal;
	private Date fechaCreacion;	
	private String observacion;	
	private boolean confirmadoImportacion;
	private boolean confirmadoAuditoria;
	private boolean confirmadoVentas;
	private boolean confirmadoAdministracion;
	private int propietarioActual;	
	private double descuentoGs;
	private double descuentoDs;
	private double totalAsignadoGs;
	private double totalAsignadoDs;
	private double porcProrrateo;
	private double coeficiente;
	private double tipoCambio;
	private boolean facturaVerificada;
	private boolean recepcionConfirmada;
	private Date fechaDespacho;
	private Date fechaVolcado;
	private double totalGastosFlete;
	private double totalGastosDespacho;
	private double totalSeguro;
	
	private Proveedor proveedor;
	private TipoMovimiento tipoMovimiento;	
	private CondicionPago condicionPago;
	private Tipo moneda;
	private Set<ImportacionFacturaDetalle> detalles = new HashSet<ImportacionFacturaDetalle>();
	
	/**
	 * @return el total de importe en gs.
	 */
	public double getTotalImporteGs() {
		double out = 0;
		for (ImportacionFacturaDetalle item : this.detalles) {
			out += item.getImporteGs();
		}
		return out;
	}
	
	/**
	 * @return el total de importe en ds.
	 */
	public double getTotalImporteDs() {
		double out = 0;
		for (ImportacionFacturaDetalle item : this.detalles) {
			out += item.getImporteDs();
		}
		return out;
	}
	
	/**
	 * @return gravada 10%
	 */
	public double getGravada10() {
		return 0;
	}
	
	/**
	 * @return gravada 5%
	 */
	public double getGravada5() {
		return 0;
	}
	
	/**
	 * @return iva 10%
	 */
	public double getIva10() {
		return 0;
	}
	
	/**
	 * @return iva 5%
	 */
	public double getIva5() {
		return 0;
	}
	
	/**
	 * @return exenta
	 */
	public double getExenta() {
		double out = 0;
		for (ImportacionFacturaDetalle det : this.detalles) {
			out += det.getImporteGs();
		}
		return out;
	}
	
	public String getDescripcionTipoMovimiento(){
		return tipoMovimiento.getDescripcion();
	}
	
	public void setDescripcionTipoMovimiento(String descripcion){
		tipoMovimiento.setDescripcion(descripcion);
	}
		
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFechaOriginal() {
		return fechaOriginal;
	}

	public void setFechaOriginal(Date fechaOriginal) {
		this.fechaOriginal = fechaOriginal;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public boolean isConfirmadoImportacion() {
		return confirmadoImportacion;
	}

	public void setConfirmadoImportacion(boolean confirmadoImportacion) {
		this.confirmadoImportacion = confirmadoImportacion;
	}

	public boolean isConfirmadoAuditoria() {
		return confirmadoAuditoria;
	}

	public void setConfirmadoAuditoria(boolean confirmadoAuditoria) {
		this.confirmadoAuditoria = confirmadoAuditoria;
	}

	public boolean isConfirmadoVentas() {
		return confirmadoVentas;
	}

	public void setConfirmadoVentas(boolean confirmadoVentas) {
		this.confirmadoVentas = confirmadoVentas;
	}

	public boolean isConfirmadoAdministracion() {
		return confirmadoAdministracion;
	}

	public void setConfirmadoAdministracion(boolean confirmadoAdministracion) {
		this.confirmadoAdministracion = confirmadoAdministracion;
	}

	public int getPropietarioActual() {
		return propietarioActual;
	}

	public void setPropietarioActual(int propietarioActual) {
		this.propietarioActual = propietarioActual;
	}

	public double getDescuentoGs() {
		return descuentoGs;
	}

	public void setDescuentoGs(double descuentoGs) {
		this.descuentoGs = descuentoGs;
	}

	public double getDescuentoDs() {
		return descuentoDs;
	}

	public void setDescuentoDs(double descuentoDs) {
		this.descuentoDs = descuentoDs;
	}

	public double getTotalAsignadoGs() {
		return totalAsignadoGs;
	}

	public void setTotalAsignadoGs(double totalAsignadoGs) {
		this.totalAsignadoGs = totalAsignadoGs;
	}

	public double getTotalAsignadoDs() {
		return totalAsignadoDs;
	}

	public void setTotalAsignadoDs(double totalAsignadoDs) {
		this.totalAsignadoDs = totalAsignadoDs;
	}

	public boolean isFacturaVerificada() {
		return facturaVerificada;
	}

	public void setFacturaVerificada(boolean facturaVerificada) {
		this.facturaVerificada = facturaVerificada;
	}

	public boolean isRecepcionConfirmada() {
		return recepcionConfirmada;
	}

	public void setRecepcionConfirmada(boolean recepcionConfirmada) {
		this.recepcionConfirmada = recepcionConfirmada;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public CondicionPago getCondicionPago() {
		return condicionPago;
	}

	public void setCondicionPago(CondicionPago condicionPago) {
		this.condicionPago = condicionPago;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public Set<ImportacionFacturaDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<ImportacionFacturaDetalle> detalles) {
		this.detalles = detalles;
	}	

	public double getPorcProrrateo() {
		return porcProrrateo;
	}

	public void setPorcProrrateo(double porcProrrateo) {
		this.porcProrrateo = porcProrrateo;
	}
	
	@Override
	public int compareTo(Object o) { 
		return -1; 
	}

	public double getCoeficiente() {
		return coeficiente;
	}

	public void setCoeficiente(double coeficiente) {
		this.coeficiente = coeficiente;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public Date getFechaDespacho() {
		return fechaDespacho;
	}

	public void setFechaDespacho(Date fechaDespacho) {
		this.fechaDespacho = fechaDespacho;
	}

	public double getTotalGastosFlete() {
		return totalGastosFlete;
	}

	public void setTotalGastosFlete(double totalGastosFlete) {
		this.totalGastosFlete = totalGastosFlete;
	}

	public double getTotalGastosDespacho() {
		return totalGastosDespacho;
	}

	public void setTotalGastosDespacho(double totalGastosDespacho) {
		this.totalGastosDespacho = totalGastosDespacho;
	}

	public double getTotalSeguro() {
		return totalSeguro;
	}

	public void setTotalSeguro(double totalSeguro) {
		this.totalSeguro = totalSeguro;
	}

	public Date getFechaVolcado() {
		return fechaVolcado;
	}

	public void setFechaVolcado(Date fechaVolcado) {
		this.fechaVolcado = fechaVolcado;
	}
}
