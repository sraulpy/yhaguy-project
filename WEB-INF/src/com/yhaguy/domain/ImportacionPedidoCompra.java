package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class ImportacionPedidoCompra extends Domain {
	
	public static final String VIA_MARITIMA = "MARITIMO";
	public static final String VIA_TERRESTRE = "TERRESTRE";
	
	public static final String TIPO_CIF = "CIF";
	public static final String TIPO_FOB = "FOB";
	public static final String TIPO_CPT = "CPT";
	public static final String TIPO_CIP = "CIP";
	public static final String TIPO_EXW = "EXWORKS";
	
	private String numeroPedidoCompra;
	private Date fechaCreacion;
	private Date fechaCierre;
	private Date fechaFactura;
	private String numeroFactura;
	private String observacion;
	private String via;
	private double cambio;
	private double totalImporteGs;
	private double totalImporteDs;
	
	private boolean confirmadoImportacion;
	private boolean confirmadoVentas;
	private boolean confirmadoAdministracion;
	private int propietarioActual;
	private boolean pedidoConfirmado;
	private boolean importacionConfirmada;
	private boolean subDiarioConfirmado;
	private boolean cifProrrateado;	
	private boolean recepcionHabilitada;
	
	private boolean conteo1;
	private boolean conteo2;
	private boolean conteo3;

	private CondicionPago proveedorCondicionPago;
	private Tipo estado;
	private Tipo moneda;
	private Tipo tipo;
	private TipoMovimiento tipoMovimiento;
	private Deposito deposito;
	
	private Set<ImportacionPedidoCompraDetalle> solicitudCotizaciones = new HashSet<ImportacionPedidoCompraDetalle>();
	private Set<ImportacionPedidoCompraDetalle> importacionPedidoCompraDetalle = new HashSet<ImportacionPedidoCompraDetalle>();
	private Set<ImportacionFactura> importacionFactura = new HashSet<ImportacionFactura>();
	private Set<ImportacionGastoImprevisto> gastosImprevistos = new HashSet<ImportacionGastoImprevisto>();
	private Set<ImportacionAplicacionAnticipo> aplicacionAnticipos = new HashSet<ImportacionAplicacionAnticipo>();
	private Set<ImportacionTrazabilidad> trazabilidad = new HashSet<ImportacionTrazabilidad>();
	private Proveedor proveedor;
	private ImportacionResumenGastosDespacho resumenGastosDespacho;
	
	private SubDiario subDiario;
	
	/**
	 * @return la cantidad total de items..
	 */
	public int getCantidadTotal() {
		int out = 0;
		for (ImportacionPedidoCompraDetalle det : this.importacionPedidoCompraDetalle) {
			out += det.getCantidad();
		}
		return out;
	}

	public SubDiario getSubDiario() {
		return subDiario;
	}

	public void setSubDiario(SubDiario subDiario) {
		this.subDiario = subDiario;
	}

	public double getCambio() {
		return cambio;
	}

	public void setCambio(double cambio) {
		this.cambio = cambio;
	}
	
	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public boolean isImportacionConfirmada() {
		return importacionConfirmada;
	}

	public void setImportacionConfirmada(boolean importacionConfirmada) {
		this.importacionConfirmada = importacionConfirmada;
	}	

	public boolean isSubDiarioConfirmado() {
		return subDiarioConfirmado;
	}

	public void setSubDiarioConfirmado(boolean subDiarioConfirmado) {
		this.subDiarioConfirmado = subDiarioConfirmado;
	}

	public ImportacionResumenGastosDespacho getResumenGastosDespacho() {
		return resumenGastosDespacho;
	}

	public void setResumenGastosDespacho(
			ImportacionResumenGastosDespacho resumenGastosDespacho) {
		this.resumenGastosDespacho = resumenGastosDespacho;
	}

	public Set<ImportacionFactura> getImportacionFactura() {
		return importacionFactura;
	}
	
	public List<ImportacionFactura> getImportacionFactura_(){
		List<ImportacionFactura> out = new ArrayList<ImportacionFactura>();
		out.addAll(this.importacionFactura);
		return out;
	}

	public void setImportacionFactura(Set<ImportacionFactura> importacionFactura) {
		this.importacionFactura = importacionFactura;
	}

	public Set<ImportacionGastoImprevisto> getGastosImprevistos() {
		return gastosImprevistos;
	}

	public void setGastosImprevistos(
			Set<ImportacionGastoImprevisto> gastosImprevistos) {
		this.gastosImprevistos = gastosImprevistos;
	}

	public int getPropietarioActual() {
		return propietarioActual;
	}

	public void setPropietarioActual(int propietarioActual) {
		this.propietarioActual = propietarioActual;
	}

	public boolean isPedidoConfirmado() {
		return pedidoConfirmado;
	}

	public void setPedidoConfirmado(boolean pedidoConfirmado) {
		this.pedidoConfirmado = pedidoConfirmado;
	}

	public boolean isConfirmadoImportacion() {
		return confirmadoImportacion;
	}

	public void setConfirmadoImportacion(boolean confirmadoImportacion) {
		this.confirmadoImportacion = confirmadoImportacion;
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
	
	public boolean isCifProrrateado() {
		return cifProrrateado;
	}

	public void setCifProrrateado(boolean cifProrrateado) {
		this.cifProrrateado = cifProrrateado;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
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

	public Set<ImportacionPedidoCompraDetalle> getImportacionPedidoCompraDetalle() {
		return importacionPedidoCompraDetalle;
	}

	public void setImportacionPedidoCompraDetalle(
			Set<ImportacionPedidoCompraDetalle> importacionPedidoCompraDetalle) {
		this.importacionPedidoCompraDetalle = importacionPedidoCompraDetalle;
	}

	public Tipo getEstado() {
		return estado;
	}

	public void setEstado(Tipo estado) {
		this.estado = estado;
	}

	public CondicionPago getProveedorCondicionPago() {
		return proveedorCondicionPago;
	}

	public void setProveedorCondicionPago(
			CondicionPago proveedorCondicionPago) {
		this.proveedorCondicionPago = proveedorCondicionPago;
	}

	public String getNumeroPedidoCompra() {
		return numeroPedidoCompra;
	}

	public void setNumeroPedidoCompra(String numeroPedidoCompra) {
		this.numeroPedidoCompra = numeroPedidoCompra;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaCierre() {
		return fechaCierre;
	}

	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}
	
	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
	
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

	public double getTotalImporteGs() {
		return totalImporteGs;
	}

	public void setTotalImporteGs(double totalImporteGs) {
		this.totalImporteGs = totalImporteGs;
	}

	public double getTotalImporteDs() {
		return totalImporteDs;
	}

	public void setTotalImporteDs(double totalImporteDs) {
		this.totalImporteDs = totalImporteDs;
	}

	public Date getFechaFactura() {
		return fechaFactura;
	}

	public void setFechaFactura(Date fechaFactura) {
		this.fechaFactura = fechaFactura;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public Set<ImportacionAplicacionAnticipo> getAplicacionAnticipos() {
		return aplicacionAnticipos;
	}

	public void setAplicacionAnticipos(Set<ImportacionAplicacionAnticipo> aplicacionAnticipos) {
		this.aplicacionAnticipos = aplicacionAnticipos;
	}

	public Set<ImportacionPedidoCompraDetalle> getSolicitudCotizaciones() {
		return solicitudCotizaciones;
	}

	public void setSolicitudCotizaciones(Set<ImportacionPedidoCompraDetalle> solicitudCotizaciones) {
		this.solicitudCotizaciones = solicitudCotizaciones;
	}

	public Set<ImportacionTrazabilidad> getTrazabilidad() {
		return trazabilidad;
	}

	public void setTrazabilidad(Set<ImportacionTrazabilidad> trazabilidad) {
		this.trazabilidad = trazabilidad;
	}

	public String getVia() {
		return via;
	}

	public void setVia(String via) {
		this.via = via;
	}

	public boolean isRecepcionHabilitada() {
		return recepcionHabilitada;
	}

	public void setRecepcionHabilitada(boolean recepcionHabilitada) {
		this.recepcionHabilitada = recepcionHabilitada;
	}

	public boolean isConteo1() {
		return conteo1;
	}

	public void setConteo1(boolean conteo1) {
		this.conteo1 = conteo1;
	}

	public boolean isConteo2() {
		return conteo2;
	}

	public void setConteo2(boolean conteo2) {
		this.conteo2 = conteo2;
	}

	public boolean isConteo3() {
		return conteo3;
	}

	public void setConteo3(boolean conteo3) {
		this.conteo3 = conteo3;
	}
}