package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class CompraLocalOrden extends Domain {

	private String numero;
	private Date fechaCreacion;
	private double tipoCambio;
	private String observacion;
	private boolean autorizado;
	private boolean cerrado;
	private boolean subDiarioConfirmado;
	private boolean recepcionado;
	private String autorizadoPor;
	private String numeroFactura;

	private Proveedor proveedor;
	private CondicionPago condicionPago;
	private Tipo moneda;
	private SucursalApp sucursal;
	private Deposito deposito;
	private TipoMovimiento tipoMovimiento;
	private CompraLocalFactura factura;
	private Set<CompraLocalOrdenDetalle> detalles = new HashSet<CompraLocalOrdenDetalle>();
	private Set<CompraLocalFactura> facturas = new HashSet<CompraLocalFactura>();
	private Set<CompraLocalGasto> resumenGastos = new HashSet<CompraLocalGasto>();
	private SubDiario subDiario;
	private Set<Gasto> gastos = new HashSet<Gasto>();

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public Deposito getDeposito() {
		return deposito;
	}

	public void setDeposito(Deposito deposito) {
		this.deposito = deposito;
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

	public boolean isAutorizado() {
		return autorizado;
	}

	public void setAutorizado(boolean autorizado) {
		this.autorizado = autorizado;
	}

	public boolean isCerrado() {
		return cerrado;
	}

	public void setCerrado(boolean cerrado) {
		this.cerrado = cerrado;
	}

	public boolean isSubDiarioConfirmado() {
		return subDiarioConfirmado;
	}

	public void setSubDiarioConfirmado(boolean subDiarioConfirmado) {
		this.subDiarioConfirmado = subDiarioConfirmado;
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

	public Set<CompraLocalOrdenDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<CompraLocalOrdenDetalle> detalles) {
		this.detalles = detalles;
	}

	public Set<CompraLocalFactura> getFacturas() {
		return facturas;
	}
	
	public List<CompraLocalFactura> getFacturas_() {
		List<CompraLocalFactura> out = new ArrayList<CompraLocalFactura>();
		out.addAll(this.facturas);
		return out;
	}

	public void setFacturas(Set<CompraLocalFactura> facturas) {
		this.facturas = facturas;
	}

	public Set<CompraLocalGasto> getResumenGastos() {
		return resumenGastos;
	}

	public void setResumenGastos(Set<CompraLocalGasto> resumenGastos) {
		this.resumenGastos = resumenGastos;
	}

	public SubDiario getSubDiario() {
		return subDiario;
	}

	public void setSubDiario(SubDiario subDiario) {
		this.subDiario = subDiario;
	}

	public Set<Gasto> getGastos() {
		return gastos;
	}

	public void setGastos(Set<Gasto> gastos) {
		this.gastos = gastos;
	}

	public String getAutorizadoPor() {
		return autorizadoPor;
	}

	public void setAutorizadoPor(String autorizadoPor) {
		this.autorizadoPor = autorizadoPor;
	}

	public boolean isRecepcionado() {
		return recepcionado;
	}

	public void setRecepcionado(boolean recepcionado) {
		this.recepcionado = recepcionado;
	}

	public CompraLocalFactura getFactura() {
		return factura;
	}

	public void setFactura(CompraLocalFactura factura) {
		this.factura = factura;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}
}
