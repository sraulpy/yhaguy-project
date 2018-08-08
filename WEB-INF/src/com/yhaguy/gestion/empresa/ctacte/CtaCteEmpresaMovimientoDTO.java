package com.yhaguy.gestion.empresa.ctacte;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

@SuppressWarnings("serial")
public class CtaCteEmpresaMovimientoDTO extends DTO {

	long idEmpresa = 0;
	Date fechaEmision = new Date();
	long idMovimientoOriginal = 0;
	String nroComprobante = "Comprobante no asignado";
	Date fechaVencimiento = new Date();
	double importeOriginal = 0;
	double saldo = 0;
	double tipoCambio = 0;
	private boolean cerrado;
	private boolean anulado = false;
	private boolean tesaka;
	
	private String ruc = "";
	private String telefono = "";
	private String direccion = "";
	
	private String numeroImportacion = "";

	private MyArray tipoMovimiento = new MyArray();
	private MyPair tipoCaracterMovimiento = new MyPair();
	private MyPair moneda = new MyPair();
	private List<CtaCteImputacionDTO> imputaciones = new ArrayList<CtaCteImputacionDTO>();
	private MyArray sucursal = new MyArray();
	
	private long idVendedor = 0;
	
	/**
	 * @return true si es vencido..
	 */
	public boolean isVencido() {
		if (this.fechaVencimiento == null) {
			return false;
		}
		Date hoy = new Date();
		int cmp = hoy.compareTo(this.fechaVencimiento);
		return cmp >= 0;
	}

	public long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(long idEmpresa) {
		this.idEmpresa = idEmpresa;
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

	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
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

	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public MyPair getTipoCaracterMovimiento() {
		return tipoCaracterMovimiento;
	}

	public void setTipoCaracterMovimiento(MyPair tipoCaracterMovimiento) {
		this.tipoCaracterMovimiento = tipoCaracterMovimiento;
	}

	public MyPair getMoneda() {
		return moneda;
	}

	public void setMoneda(MyPair moneda) {
		this.moneda = moneda;
	}

	/*
	 * public List<MyArray> getImputaciones() { return imputaciones; } public
	 * void setImputaciones(List<MyArray> imputaciones) { this.imputaciones =
	 * imputaciones; }
	 */
	public MyArray getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyArray sucursal) {
		this.sucursal = sucursal;
	}

	public List<CtaCteImputacionDTO> getImputaciones() {
		return imputaciones;
	}

	public void setImputaciones(List<CtaCteImputacionDTO> imputaciones) {
		this.imputaciones = imputaciones;
	}

	public boolean isCerrado() {
		return cerrado;
	}

	public void setCerrado(boolean cerrado) {
		this.cerrado = cerrado;
	}

	public boolean isAnulado() {
		return anulado;
	}

	public void setAnulado(boolean anulado) {
		this.anulado = anulado;
	}

	public boolean isTesaka() {
		return tesaka;
	}

	public void setTesaka(boolean tesaka) {
		this.tesaka = tesaka;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public long getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(long idVendedor) {
		this.idVendedor = idVendedor;
	}

	public String getNumeroImportacion() {
		return numeroImportacion;
	}

	public void setNumeroImportacion(String numeroImportacion) {
		this.numeroImportacion = numeroImportacion;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

}
