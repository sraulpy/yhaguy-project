package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class NotaCredito extends Domain {
	
	public static final String NCR_CREDITO = "NCR-CRE";
	public static final String NCR_CONTADO = "NCR-CON";
	public static final String NCR_COMPRA_MERCADERIA = "NCR-COM";
	public static final String NCR_COMPRA_GASTOS = "NCR-GTO";
	
	private String numero;
	private String timbrado_;
	private String observacion;
	
	private Date fechaEmision;
	
	private double importeGs;
	private double importeDs;
	private double importeIva;
	
	private double tipoCambio;
	private boolean promocion;
	
	/** atributos que se usan como informacion adicional **/
	private String cajaNro = "";
	private String planillaCajaNro = "";
	private String cajero = "";
	
	/** El nro de Solicitud que genero la N.C. ó viceversa **/
	private String enlace;
	
	/** El nro de timbrado del documento **/
	private Timbrado timbrado;
	
	/** La moneda en que fue hecha la transacción **/
	private Tipo moneda;
	
	/** El tipo de movimiento (N.C. Venta - N.C. Compra) **/
	private TipoMovimiento tipoMovimiento;
	
	/** El estado del documento**/
	private Tipo estadoComprobante;
	
	/** El motivo de la NC. (Descuento - Devolucion - Error Facturacion) **/
	private Tipo motivo;
	
	/** El acreedor para las N.C. Venta **/
	private Cliente cliente;
	
	/** El deudor para las N.C. Compra **/
	private Proveedor proveedor;
	
	/** La sucursal en la que se confecciono la N.C. **/
	private SucursalApp sucursal;
	
	private Funcionario vendedor;
	
	/** El tecnico */
	private Funcionario tecnico;
	private Tecnico _tecnico;
	
	private Deposito deposito;
	
	/** Los detalles de las N.C pueden hacer referencia a facturas ó articulos segun el tipo **/
	private Set<NotaCreditoDetalle> detalles = new HashSet<NotaCreditoDetalle>();
	
	/** Las notas de credito reclamo pueden referenciar a uno o mas servicios tecnicos **/
	private Set<ServicioTecnico> serviciosTecnicos = new HashSet<ServicioTecnico>();
	
	private VehiculoTipo vehiculoTipo;
	private VehiculoMarca vehiculoMarca;
	private VehiculoModelo vehiculoModelo;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return el importe total de la venta segun el proveedor..
	 */
	public double getImporteByProveedor(long idProveedor) throws Exception {
		double out = 0;
		for (NotaCreditoDetalle item : this.getDetallesArticulos()) {
			if (item.isProveedor(idProveedor)) {
				out += item.getImporteGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el importe total de la venta segun el proveedor..
	 */
	public double getImporteByProveedorSinIva(long idProveedor) throws Exception {
		double out = 0;
		for (NotaCreditoDetalle item : this.getDetallesArticulos()) {
			if (item.isProveedor(idProveedor)) {
				out += item.getImporteGsSinIva();
			}
		}
		return out;
	}
	
	/**
	 * @return true si es moneda local..
	 */
	public boolean isMonedaLocal() {
		return this.moneda.getSigla().equals(Configuracion.SIGLA_MONEDA_GUARANI);
	}
	
	/**
	 * @return el importe segun familias..
	 */
	public double getImporteGs(List<ArticuloFamilia> familias) {
		double out = 0;
		if (familias.size() == 0) {
			return this.getImporteGs();
		}
		for (ArticuloFamilia familia : familias) {
			out += this.getImporteGsByFamilia(familia.getId());
		}
		return out;
	}
	
	/**
	 * @return importe segun familia..
	 */
	public double getImporteGsByFamilia(long idFamilia) {
		double out = 0;
		for (NotaCreditoDetalle det : this.getDetallesArticulos()) {
			if (det.isFamilia(idFamilia)) {
				out += det.getImporteGs();
			}
		}
		if (this.isMotivoDescuento()) {
			Venta vta = this.getVentaAplicada();
			for (VentaDetalle det : vta.getDetalles()) {
				if (det.isFamilia(idFamilia)) {
					double porc = Utiles.obtenerPorcentajeDelValor(det.getImporteGs(), vta.getTotalImporteGs());
					double apli = Utiles.obtenerValorDelPorcentaje(this.getImporteGs(), porc);
					out += apli;
				}
			}
		}
		return out;
	}
	
	/**
	 * @return importe segun familia..
	 */
	public double getImporteGsByFamiliaSinIva(long idFamilia) {
		double out = 0;
		for (NotaCreditoDetalle det : this.getDetallesArticulos()) {
			if (det.isFamilia(idFamilia)) {
				out += det.getImporteGsSinIva();
			}
		}
		return out;
	}
	
	/**
	 * @return el costo segun familias..
	 */
	public double getCostoGs(List<ArticuloFamilia> familias) {
		double out = 0;
		if (familias.size() == 0) {
			return this.getTotalCostoGsSinIva();
		}
		for (ArticuloFamilia familia : familias) {
			out += this.getCostoGsByFamilia(familia.getId());
		}
		return Math.rint(out * 1) / 1;
	}
	
	/**
	 * @return costo segun familia..
	 */
	public double getCostoGsByFamilia(long idFamilia) {
		double out = 0;
		for (NotaCreditoDetalle det : this.getDetallesArticulos()) {
			if (det.isFamilia(idFamilia)) {
				out += det.getCostoTotalGsSinIva();
			}
		}
		return Math.rint(out * 1) / 1;
	}
	
	/**
	 * @return cantidad segun familia..
	 */
	public long getCantidadByFamilia(long idFamilia) {
		long out = 0;
		for (NotaCreditoDetalle det : this.getDetallesArticulos()) {
			if (det.isFamilia(idFamilia)) {
				out += det.getCantidad();
			}
		}
		return out;
	}
	
	/**
	 * @return volumen segun familia..
	 */
	public double getVolumenByFamilia(long idFamilia) {
		double out = 0;
		for (NotaCreditoDetalle det : this.getDetallesArticulos()) {
			if (det.isFamilia(idFamilia)) {
				out += det.getCantidad() * det.getArticulo().getVolumen();
			}
		}
		return out;
	}
	
	/**
	 * @return true si es nc de compra mercaderia..
	 */
	public boolean isNotaCreditoCompraProveedor() {
		for (NotaCreditoDetalle item : this.getDetallesFacturas()) {
			if (item.getCompra() != null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return true si es nc de compra gastos..
	 */
	public boolean isNotaCreditoCompraAcreedor() {
		for (NotaCreditoDetalle item : this.getDetallesFacturas()) {
			if (item.getGasto() != null || item.getImportacion() != null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return el total iva 10..
	 */
	public double getTotalIva10() {
		if (this.isExenta()) return 0.0;
		Misc misc = new Misc();	
		return Math.rint(misc.calcularIVA(this.getImporteGs(), 10) * 1) / 1;
	}
	
	/**
	 * @return el total gravado 10..
	 */
	public double getTotalGravado10() {
		if (this.isExenta()) return 0.0;
		Misc misc = new Misc();	
		return Math.rint(misc.calcularGravado(this.getImporteGs(), 10) * 1) / 1;
	}
	
	/**
	 * @return el total exenta..
	 */
	public double getTotalExenta() {
		if (this.isExenta()) return this.getImporteGs();
		return 0.0;
	}
	
	/**
	 * @return el importe total sin iva..
	 */
	public double getTotalImporteGsSinIva() {
		if (this.isExenta()) return this.getImporteGs();
		return this.getImporteGs() - this.getTotalIva10();
	}
	
	/**
	 * @return el costo total en Gs..
	 */
	public double getTotalCostoGsSinIva() {
		double out = 0;
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.getArticulo() != null) {
				out += item.getCostoTotalGsSinIva();
			}
		}
		return Math.rint(out * 1) / 1;
	}
	
	/**
	 * @return true si es exenta..
	 */
	public boolean isExenta() {
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.getTipoIva().getSigla().equals(Configuracion.SIGLA_IVA_EXENTO)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return la rentabilidad de la nota de credito..
	 */
	public double getRentabilidad() {
		if (!this.isMotivoDevolucion()) {
			double vta = this.getVentaAplicada().getTotalImporteGsSinIva();
			double nc = this.getTotalImporteGsSinIva();
			double porc = Utiles.obtenerPorcentajeDelValor(nc, vta);
			double rentVta = this.getVentaAplicada().getRentabilidad();			
			return (rentVta * porc) / 100;
		}		
		double ganancia = this.getTotalImporteGsSinIva() - this.getTotalCostoGsSinIva();	
		double out = Utiles.obtenerPorcentajeDelValor(ganancia, this.getTotalCostoGsSinIva());
		return Utiles.redondeoDosDecimales(out);
	}
	
	/**
	 * @return la rentabilidad de la nota de credito..
	 */
	public double getRentabilidadVenta() {
		if (!this.isMotivoDevolucion()) {
			double vta = this.getVentaAplicada().getTotalImporteGsSinIva();
			double nc = this.getTotalImporteGsSinIva();
			double porc = Utiles.obtenerPorcentajeDelValor(nc, vta);
			double rentVta = this.getVentaAplicada().getRentabilidad();			
			return (rentVta * porc) / 100;
		}		
		double ganancia = this.getTotalImporteGsSinIva() - this.getTotalCostoGsSinIva();	
		double out = Utiles.obtenerPorcentajeDelValor(ganancia, this.getTotalImporteGsSinIva());
		return Utiles.redondeoDosDecimales(out);
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
	 * @return si se aplica a una venta contado..
	 */
	public boolean isNotaCreditoVentaContado() {
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.getVenta() != null && item.getVenta().isVentaContado())
				return true;
		}
		return false;
	}
	
	/**
	 * @return si se aplica a un gasto de caja chica..
	 */
	public boolean isNotaCreditoGastoCajaChica() {
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.getGasto() != null && item.getGasto().isFondoFijo())
				return true;
		}
		return false;
	}
	
	/**
	 * @return true si la fecha de la venta es menor o igual al parametro..
	 */
	public boolean isFechaVentaIN(Date fecha) {
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.getVenta() != null && item.getVenta().getFecha().compareTo(fecha) >= 0)
				return true;
		}
		return false;
	}
	
	/**
	 * @return el nro de factura al cual se aplico..
	 */
	public String getNumeroFacturaAplicada() {
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.getVenta() != null)
				return item.getVenta().getNumero();
		}
		return "";
	}
	
	/**
	 * @return la venta aplicada..
	 */
	public Venta getVentaAplicada() {
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.getVenta() != null) {
				return item.getVenta();
			}
		}
		return null;
	}
	
	/**
	 * @return la compra aplicada..
	 */
	public CompraLocalFactura getCompraAplicada() {
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.getCompra() != null) {
				return item.getCompra();
			}
		}
		return null;
	}
	
	/**
	 * @return el vendedor de la factura aplicada..
	 */
	public Funcionario getVendedor_() {
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.getVenta() != null)
				return item.getVenta().getVendedor();
		}
		return null;
	}
	
	/**
	 * @return el tecnico de la factura aplicada..
	 */
	public Funcionario getTecnico_() {
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.getVenta() != null)
				return item.getVenta().getTecnico();
		}
		return null;
	}
	
	/**
	 * @return true si el motivo es por descuento..
	 */
	public boolean isMotivoDescuento() {
		String sigla = (String) this.motivo.getSigla();
		return sigla.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_DESCUENTO);	
	}
	
	/**
	 * @return true si el motivo es por devolucion..
	 */
	public boolean isMotivoDevolucion() {
		String sigla = (String) this.motivo.getSigla();
		return sigla.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_DEVOLUCION);	
	}
	
	/**
	 * @return true si el motivo es por reclamo..
	 */
	public boolean isMotivoReclamo() {
		String sigla = (String) this.motivo.getSigla();
		return sigla.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_RECLAMO);	
	}
	
	/**
	 * @return true si es nota credito venta..
	 */
	public boolean isNotaCreditoVenta() {
		String sigla = (String) this.tipoMovimiento.getSigla();
		return sigla.equals(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA);	
	}
	
	/**
	 * @return true si es nota credito compra..
	 */
	public boolean isNotaCreditoCompra() {
		String sigla = (String) this.tipoMovimiento.getSigla();
		return sigla.equals(Configuracion.SIGLA_TM_NOTA_CREDITO_COMPRA);	
	}
	
	/**
	 * @return los detalles que son de tipo factura..
	 */
	public List<NotaCreditoDetalle> getDetallesFacturas() {
		List<NotaCreditoDetalle> out = new ArrayList<NotaCreditoDetalle>();
		
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.isDetalleFactura() == true) {
				out.add(item);
			}
		}		
		return out;
	}
	
	/**
	 * @return los detalles que son de tipo articulo..
	 */
	public List<NotaCreditoDetalle> getDetallesArticulos() {
		List<NotaCreditoDetalle> out = new ArrayList<NotaCreditoDetalle>();
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.isDetalleFactura() == false) {
				out.add(item);
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

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
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

	public double getImporteIva() {
		return importeIva;
	}

	public void setImporteIva(double importeIva) {
		this.importeIva = importeIva;
	}

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public Tipo getMotivo() {
		return motivo;
	}

	public void setMotivo(Tipo motivo) {
		this.motivo = motivo;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public Set<NotaCreditoDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<NotaCreditoDetalle> detalles) {
		this.detalles = detalles;
	}	

	public Tipo getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(Tipo estadoDocumento) {
		this.estadoComprobante = estadoDocumento;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
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

	public String getEnlace() {
		return enlace;
	}

	public void setEnlace(String enlace) {
		this.enlace = enlace;
	}	

	public String getCajaNro() {
		return cajaNro;
	}

	public void setCajaNro(String cajaNro) {
		this.cajaNro = cajaNro;
	}

	public String getPlanillaCajaNro() {
		return planillaCajaNro;
	}

	public void setPlanillaCajaNro(String planillaCajaNro) {
		this.planillaCajaNro = planillaCajaNro;
	}

	public String getCajero() {
		return cajero;
	}

	public void setCajero(String cajero) {
		this.cajero = cajero;
	}

	public Timbrado getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(Timbrado timbrado) {
		this.timbrado = timbrado;
	}

	public String getTimbrado_() {
		return timbrado_;
	}

	public void setTimbrado_(String timbrado_) {
		this.timbrado_ = timbrado_;
	}

	public Set<ServicioTecnico> getServiciosTecnicos() {
		return serviciosTecnicos;
	}

	public void setServiciosTecnicos(Set<ServicioTecnico> serviciosTecnicos) {
		this.serviciosTecnicos = serviciosTecnicos;
	}

	public Funcionario getVendedor() {
		return vendedor;
	}

	public void setVendedor(Funcionario vendedor) {
		this.vendedor = vendedor;
	}

	public Deposito getDeposito() {
		return deposito;
	}

	public void setDeposito(Deposito deposito) {
		this.deposito = deposito;
	}

	public VehiculoTipo getVehiculoTipo() {
		return vehiculoTipo;
	}

	public void setVehiculoTipo(VehiculoTipo vehiculoTipo) {
		this.vehiculoTipo = vehiculoTipo;
	}

	public VehiculoMarca getVehiculoMarca() {
		return vehiculoMarca;
	}

	public void setVehiculoMarca(VehiculoMarca vehiculoMarca) {
		this.vehiculoMarca = vehiculoMarca;
	}

	public VehiculoModelo getVehiculoModelo() {
		return vehiculoModelo;
	}

	public void setVehiculoModelo(VehiculoModelo vehiculoModelo) {
		this.vehiculoModelo = vehiculoModelo;
	}

	public Funcionario getTecnico() {
		return tecnico;
	}

	public void setTecnico(Funcionario tecnico) {
		this.tecnico = tecnico;
	}

	public Tecnico get_tecnico() {
		return _tecnico;
	}

	public void set_tecnico(Tecnico _tecnico) {
		this._tecnico = _tecnico;
	}

	public boolean isPromocion() {
		return promocion;
	}

	public void setPromocion(boolean promocion) {
		this.promocion = promocion;
	}
}
