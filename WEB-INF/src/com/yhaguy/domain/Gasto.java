package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class Gasto extends Domain {
	
	private Date fecha;
	private Date vencimiento;
	private String numeroFactura;
	private String numeroTimbrado;	
	private double tipoCambio;
	private double totalAsignado;
	private double totalIvaAsignado;	
	private boolean existeComprobanteFisico;
	private String motivoComprobanteFisico;
	private String cajaPagoNumero;
	private String numeroOrdenPago;
	private String observacion;
	private String beneficiario;
	private boolean ivaRetenido;
	
	private double importeGs;
	private double importeDs;
	private double importeIva10;
	private double importeIva5;
	
	/** referencia a las importaciones **/
	private long idImportacion;
	private String numeroImportacion = "";
	private String despachante = "";
	
	/** referencia al debito bancario **/
	private boolean debitoBancario;
	
	/** gastos contado opcion no generar saldo **/
	private boolean no_generar_saldo;
	
	private Proveedor proveedor;
	private Tipo moneda;	
	private TipoMovimiento tipoMovimiento;
	private Timbrado timbrado;	
	private CondicionPago condicionPago;
	private Tipo estadoComprobante;
	private SucursalApp sucursal;
	private BancoCta banco;
	
	private Set<GastoDetalle> detalles = new HashSet<GastoDetalle>();
	private Set<ReciboFormaPago> formasPago = new HashSet<ReciboFormaPago>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return la url de la imagen..
	 */
	public String getUrlImagen() {
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS)) {
			return Configuracion.URL_IMAGES_PUBLIC_MRA + "gastos/" + this.id + ".png";
		}
		return Configuracion.URL_IMAGES_PUBLIC_BAT + "gastos/" + this.id + ".png";
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
	 * @return true si es contado..
	 */
	public boolean isContado() {
		String sigla = this.tipoMovimiento.getSigla();
		return sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO);
	}
	
	/**
	 * @return true si es credito..
	 */
	public boolean isCredito() {
		String sigla = this.tipoMovimiento.getSigla();
		return sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO);
	}
	
	/**
	 * @return true si es fondo fijo..
	 */
	public boolean isFondoFijo() {
		return (!this.getCajaPagoNumero().equals("- - -"))
				&& (this.getNumeroOrdenPago().equals("- - -"));
	}
	
	/**
	 * @return true si es pagado..
	 */
	public boolean isPagado() {
		return !this.getNumeroOrdenPago().equals("- - -");
	}
	
	/**
	 * @return true si es pendiente de pago..
	 */
	public boolean isPendientePago() {
		return this.isCredito() && this.getCajaPagoNumero().equals("- - -");
	}
	
	/**
	 * @return true si es gasto importacion..
	 */
	public boolean isGastoImportacion() {
		return !this.getNumeroImportacion().equals("- - -");
	}
	
	public String getDescripcionTipoMovimiento(){
		return tipoMovimiento.getDescripcion();
	}
	
	public String getDescripcionCuenta() {
		for (GastoDetalle det : this.detalles) {
			return det.getArticuloGasto().getDescripcion().toUpperCase();
		}
		return this.tipoMovimiento.getDescripcion().toUpperCase();
	}
	
	public void setDescripcionTipoMovimiento(String descripcion){
		tipoMovimiento.setDescripcion(descripcion);
	}

	public String getObservacion() {
		if (this.observacion == null || this.observacion.isEmpty()) {
			return "- - -";
		}
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public String getNumeroTimbrado() {
		return numeroTimbrado;
	}

	public void setNumeroTimbrado(String numeroTimbrado) {
		this.numeroTimbrado = numeroTimbrado;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public double getTotalAsignado() {
		return totalAsignado;
	}

	public void setTotalAsignado(double totalAsignado) {
		this.totalAsignado = totalAsignado;
	}

	public double getTotalIvaAsignado() {
		return totalIvaAsignado;
	}

	public void setTotalIvaAsignado(double totalIva) {
		this.totalIvaAsignado = totalIva;
	}	

	public boolean isExisteComprobanteFisico() {
		return existeComprobanteFisico;
	}

	public void setExisteComprobanteFisico(boolean existeComprobanteFisico) {
		this.existeComprobanteFisico = existeComprobanteFisico;
	}

	public String getMotivoComprobanteFisico() {
		return motivoComprobanteFisico;
	}

	public void setMotivoComprobanteFisico(String motivoComprobanteFisico) {
		this.motivoComprobanteFisico = motivoComprobanteFisico;
	}
	
	public String getCajaPagoNumero() {
		if (this.cajaPagoNumero == null || this.cajaPagoNumero.isEmpty()) {
			return "- - -";
		}
		return cajaPagoNumero;
	}

	public void setCajaPagoNumero(String cajaPagoNumero) {
		this.cajaPagoNumero = cajaPagoNumero;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	
	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
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

	public CondicionPago getCondicionPago() {
		return condicionPago;
	}

	public void setCondicionPago(CondicionPago condicionPago) {
		this.condicionPago = condicionPago;
	}

	public Set<GastoDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<GastoDetalle> detalles) {
		this.detalles = detalles;
	}	

	public long getIdImportacion() {
		return idImportacion;
	}

	public void setIdImportacion(long idImportacion) {
		this.idImportacion = idImportacion;
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

	public void setImporteIva10(double importeIva) {
		this.importeIva10 = importeIva;
	}	

	public double getImporteIva5() {
		return importeIva5;
	}

	public void setImporteIva5(double importeIva5) {
		this.importeIva5 = importeIva5;
	}

	public Tipo getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(Tipo estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public boolean isIvaRetenido() {
		return ivaRetenido;
	}

	public void setIvaRetenido(boolean ivaRetenido) {
		this.ivaRetenido = ivaRetenido;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}

	public Set<ReciboFormaPago> getFormasPago() {
		return formasPago;
	}

	public void setFormasPago(Set<ReciboFormaPago> formasPago) {
		this.formasPago = formasPago;
	}

	public String getNumeroImportacion() {
		if (this.numeroImportacion == null || this.numeroImportacion.isEmpty()) {
			return "- - -";
		}
		return numeroImportacion;
	}

	public void setNumeroImportacion(String numeroImportacion) {
		this.numeroImportacion = numeroImportacion;
	}

	public String getDespachante() {
		return despachante;
	}

	public void setDespachante(String despachante) {
		this.despachante = despachante;
	}

	public boolean isDebitoBancario() {
		return debitoBancario;
	}

	public void setDebitoBancario(boolean debitoBancario) {
		this.debitoBancario = debitoBancario;
	}

	public String getNumeroOrdenPago() {
		if (this.numeroOrdenPago == null || this.numeroOrdenPago.isEmpty()) {
			return "- - -";
		}
		return numeroOrdenPago;
	}

	public void setNumeroOrdenPago(String numeroOrdenPago) {
		this.numeroOrdenPago = numeroOrdenPago;
	}

	public boolean isNo_generar_saldo() {
		return no_generar_saldo;
	}

	public void setNo_generar_saldo(boolean no_generar_saldo) {
		this.no_generar_saldo = no_generar_saldo;
	}

	public BancoCta getBanco() {
		return banco;
	}

	public void setBanco(BancoCta banco) {
		this.banco = banco;
	}
}
