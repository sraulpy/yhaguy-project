package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class NotaDebito extends Domain {

	private String numero;
	private String timbrado;
	private Date fecha;
	private String numeroFactura;	
	private double importeGs;
	private double importeDs;
	private double tipoCambio;

	private Tipo moneda;
	private SucursalApp sucursal;
	private TipoMovimiento tipoMovimiento;
	private Tipo estadoComprobante;
	private Cliente cliente;
	private NotaCredito notaCredito;
	
	private Set<NotaDebitoDetalle> detalles = new HashSet<NotaDebitoDetalle>();

	@Override
	public int compareTo(Object arg0) {
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
	 * @return el total importe gs..
	 */
	public double getTotalImporteGs() {
		double out = 0;
		for (NotaDebitoDetalle item : this.detalles) {
			out += item.getImporteGs();
		}
		return out;
	}
	
	/**
	 * @return el total iva 10..
	 */
	public double getTotalIva10() {
		double out = 0;
		for (NotaDebitoDetalle item : this.detalles) {
			if (item.isIva10()) {
				out += Utiles.getIVA(item.getImporteGs(), 10);
			}
		}
		return out;
	}
	
	/**
	 * @return el total gravado 10..
	 */
	public double getTotalGravado10() {
		double out = 0;
		for (NotaDebitoDetalle item : this.detalles) {
			if (item.isIva10()) {
				out += item.getImporteGs();
			}
		}
		return out - Utiles.getIVA(out, 10);
	}
	
	/**
	 * @return el total exenta..
	 */
	public double getTotalExenta() {
		double out = 0;
		for (NotaDebitoDetalle item : this.detalles) {
			if (item.isExenta()) {
				out += item.getImporteGs();
			}
		}
		return out;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
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

	public Tipo getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(Tipo estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public Set<NotaDebitoDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<NotaDebitoDetalle> detalles) {
		this.detalles = detalles;
	}

	public String getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(String timbrado) {
		this.timbrado = timbrado;
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

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public NotaCredito getNotaCredito() {
		return notaCredito;
	}

	public void setNotaCredito(NotaCredito notaCredito) {
		this.notaCredito = notaCredito;
	}
}
