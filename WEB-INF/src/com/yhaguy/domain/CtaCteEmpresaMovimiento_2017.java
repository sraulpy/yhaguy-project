package com.yhaguy.domain;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class CtaCteEmpresaMovimiento_2017 extends Domain {

	private long idEmpresa = 0;
	private Date fechaEmision = new Date();
	private long idMovimientoOriginal = 0;
	private String nroComprobante = "comprobante";
	private Date fechaVencimiento = new Date();
	private double importeOriginal = 0;
	private double saldo = 0;
	private long idVendedor;
	private TipoMovimiento tipoMovimiento; /* FAC, REC, ETC. */
	private Tipo tipoCaracterMovimiento; /* Cliente/Proveedor */
	private Tipo moneda; /* Dolar, Guaranies, Pesos, Etc. */
	private Set<CtaCteImputacion> imputaciones = new HashSet<CtaCteImputacion>();
	private SucursalApp sucursal;/* Sucursal donde se realizo el movimiento */

	private boolean cerrado = false;
	private boolean anulado = false;
	private boolean tesaka;
	
	/**
	 * @return true si es movimiento de proveedor..
	 */
	public boolean isCaracterProveedor() {
		return this.tipoCaracterMovimiento.getSigla().equals(
				Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR);
	}
	
	/**
	 * @return true si es nota de credito..
	 */
	public boolean isNotaCreditoVenta() {
		return this.tipoMovimiento.getSigla().equals(
				Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA);
	}
	
	/**
	 * @return true si es vencido..
	 */
	public boolean isVencido() {
		if (this.fechaVencimiento == null
				|| (!this.isMovimientoCredito())
						|| this.saldo <= 1) {
			return false;
		}
		Date hoy = new Date();
		int cmp = hoy.compareTo(this.fechaVencimiento);
		return cmp >= 0;
	}
	
	/**
	 * @return los dias de vencimiento..
	 */
	public long getDiasVencidos() {
		if (!isVencido())
			return 0;
		Misc misc = new Misc();
		return misc.diasEntreFechas(this.fechaVencimiento, new Date());
	}
	
	/**
	 * @return los dias de vencimiento..
	 */
	public long getDiasVencidos(boolean avencer) {
		if (!avencer) {
			return this.getDiasVencidos();
		}
		Misc misc = new Misc();
		return misc.diasEntreFechas(this.fechaVencimiento, new Date());
	}
	
	/**
	 * @return true si el vto esta dentro del rango de dias..
	 */
	public boolean isDiasVencidosEntre(long desde, long hasta) {
		long vto = this.getDiasVencidos();
		return (vto >= desde) && (vto <= hasta);
	}
	
	/**
	 * @return true si el vto esta dentro del rango de dias..
	 */
	public boolean isDiasVencidosEntre(long desde, long hasta, boolean avencer) {
		if (!avencer) {
			return this.isDiasVencidosEntre(desde, hasta);
		}
		Misc misc = new Misc();
		long vto = misc.diasEntreFechas(this.fechaVencimiento, new Date());
		return (vto >= desde) && (vto <= hasta);
	}
	
	/**
	 * @return la empresa..
	 */
	public Empresa getEmpresa() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getEmpresaById(this.idEmpresa);
	}
	
	/**
	 * @return true si es movimiento credito..
	 */
	public boolean isMovimientoCredito() {
		return this.tipoMovimiento.getSigla().equals(
				Configuracion.SIGLA_TM_FAC_VENTA_CREDITO)
				|| this.tipoMovimiento.getSigla().equals(
						Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO)
				|| this.tipoMovimiento.getSigla().equals(
						Configuracion.SIGLA_TM_FAC_GASTO_CREDITO);
	}
	
	/**
	 * @return true si es venta credito..
	 */
	public boolean isVentaCredito() {
		return this.tipoMovimiento.getSigla().equals(
				Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
	}
	
	/**
	 * @return el importe..
	 */
	public double getImporteOriginalFinal() {
		if (this.isNotaCreditoVenta()) {
			return this.getImporteOriginal() * -1;
		}
		return this.importeOriginal;
	}
	
	/**
	 * @return el saldo..
	 */
	public double getSaldoFinal() {
		if (this.isNotaCreditoVenta()) {
			return this.getSaldo() * -1;
		}
		return this.saldo;
	}

	public boolean isCerrado() {
		return cerrado;
	}

	public void setCerrado(boolean cerrado) {
		this.cerrado = (this.saldo * this.saldo) < 0.001;
	}

	public long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public String getFechaEmision_() {
		Misc misc = new Misc();
		return misc.dateToString(fechaEmision, Misc.DD_MM_YYYY);
	}
	
	public Date getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public long getIdMovimientoOriginal() {
		return idMovimientoOriginal;
	}

	public void setIdMovimientoOriginal(long idMovimientoOriginal) {
		this.idMovimientoOriginal = idMovimientoOriginal;
	}

	public String getNroComprobante() {
		return nroComprobante;
	}

	public void setNroComprobante(String nroComprobante) {
		this.nroComprobante = nroComprobante;
	}
	
	public String getFechaVencimiento_() {
		Misc misc = new Misc();
		return misc.dateToString(fechaVencimiento, Misc.DD_MM_YYYY);
	}

	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public String getImporteOriginal_() {
		NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
		return FORMATTER.format(this.getImporteOriginalFinal());
	}
	
	public double getImporteOriginal() {
		return importeOriginal;
	}

	public void setImporteOriginal(double importeOriginal) {
		this.importeOriginal = importeOriginal;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public Tipo getTipoCaracterMovimiento() {
		return tipoCaracterMovimiento;
	}

	public void setTipoCaracterMovimiento(Tipo tipoCaracterMovimiento) {
		this.tipoCaracterMovimiento = tipoCaracterMovimiento;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public Set<CtaCteImputacion> getImputaciones() {
		return imputaciones;
	}

	public void setImputaciones(Set<CtaCteImputacion> imputaciones) {
		this.imputaciones = imputaciones;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public boolean isAnulado() {
		return anulado;
	}

	public void setAnulado(boolean anulado) {
		this.anulado = anulado;
	}

	@Override
	public int compareTo(Object o) {
		CtaCteEmpresaMovimiento_2017 cmp = (CtaCteEmpresaMovimiento_2017) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}

	public long getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(long idVendedor) {
		this.idVendedor = idVendedor;
	}

	public boolean isTesaka() {
		return tesaka;
	}

	public void setTesaka(boolean tesaka) {
		this.tesaka = tesaka;
	}
}
