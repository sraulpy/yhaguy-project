package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class CompraLocalFactura extends Domain {
	
	private String numero;
	private Date fechaCreacion;
	private Date fechaOriginal;
	private Date fechaVencimiento;
	private double tipoCambio;
	private String observacion;
	private double descuentoGs;
	private double descuentoDs;
	private double totalAsignadoGs;
	private double totalAsignadoDs;	
	private boolean recepcionConfirmada;
	private boolean ivaRetenido;
	private String cajaPagoNumero;
	
	private double importeGs;
	private double importeDs;
	private double importeIva10;
	private double importeIva5;
	
	private Proveedor proveedor;
	private CondicionPago condicionPago;
	private Tipo moneda;
	private SucursalApp sucursal;
	private TipoMovimiento tipoMovimiento;
	private Timbrado timbrado;	
	private Set<CompraLocalFacturaDetalle> detalles = new HashSet<CompraLocalFacturaDetalle>();

	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * recalcula los costos segun cotizacion..
	 */
	public void recalcularCotizacion() {
		for (CompraLocalFacturaDetalle item : this.detalles) {
			item.setCostoGs(item.getCostoDs() * this.tipoCambio);
			item.setDescuentoGs(item.getDescuentoDs() * this.tipoCambio);
		}
		this.importeGs = this.importeDs * this.tipoCambio;
	}
	
	/**
	 * @return true si es moneda local..
	 */
	public boolean isMonedaLocal() {
		return this.moneda.getSigla().equals(Configuracion.SIGLA_MONEDA_GUARANI);
	}
	
	/**
	 * @return gravada 10%
	 */
	public double getGravada10() {
		double out = 0;
		for (CompraLocalFacturaDetalle det : detalles) {
			if (det.isIva10()) {
				out += det.getImporteGs() - det.getIva10();
			}
		}
		return out;
	}
	
	/**
	 * @return gravada 5%
	 */
	public double getGravada5() {
		double out = 0;
		for (CompraLocalFacturaDetalle det : detalles) {
			if (det.isIva5()) {
				out += det.getImporteGs() - det.getIva5();
			}
		}
		return out;
	}
	
	/**
	 * @return iva 10%
	 */
	public double getIva10() {
		double out = 0;
		for (CompraLocalFacturaDetalle det : detalles) {
			if (det.isIva10()) {
				out += det.getIva10();
			}
		}
		return out;
	}
	
	/**
	 * @return iva 5%
	 */
	public double getIva5() {
		double out = 0;
		for (CompraLocalFacturaDetalle det : detalles) {
			if (det.isIva5()) {
				out += det.getIva5();
			}
		}
		return out;
	}
	
	/**
	 * @return exenta
	 */
	public double getExenta() {
		double out = 0;
		for (CompraLocalFacturaDetalle det : detalles) {
			if (det.isExenta()) {
				out += det.getImporteGs();
			}
		}
		return out;
	}
	
	/**
	 * @return true si es compra contado..
	 */
	public boolean isContado() {
		return this.tipoMovimiento.getSigla().equals(Configuracion.SIGLA_TM_FAC_COMPRA_CONTADO);
	}
	
	/**
	 * @return true si es compra credito..
	 */
	public boolean isCredito() {
		return this.tipoMovimiento.getSigla().equals(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO);
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

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaOriginal() {
		return fechaOriginal;
	}

	public void setFechaOriginal(Date fechaOriginal) {
		this.fechaOriginal = fechaOriginal;
	}
	
	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
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

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public Timbrado getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(Timbrado timbrado) {
		this.timbrado = timbrado;
	}

	public Set<CompraLocalFacturaDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<CompraLocalFacturaDetalle> detalles) {
		this.detalles = detalles;
	}

	public boolean isIvaRetenido() {
		return ivaRetenido;
	}

	public void setIvaRetenido(boolean ivaRetenido) {
		this.ivaRetenido = ivaRetenido;
	}

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public double getImporteDs() {
		return importeDs;
	}

	public void setImporteDs(double importeDs) {
		this.importeDs = importeDs;
	}

	public double getImporteIva10() {
		return importeIva10;
	}

	public void setImporteIva10(double importeIva10) {
		this.importeIva10 = importeIva10;
	}

	public double getImporteIva5() {
		return importeIva5;
	}

	public void setImporteIva5(double importeIva5) {
		this.importeIva5 = importeIva5;
	}

	public String getCajaPagoNumero() {
		return cajaPagoNumero;
	}

	public void setCajaPagoNumero(String cajaPagoNumero) {
		this.cajaPagoNumero = cajaPagoNumero;
	}	
}
