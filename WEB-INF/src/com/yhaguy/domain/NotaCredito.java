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
	
	private String numero;
	private String timbrado_;
	private String observacion;
	
	private Date fechaEmision;
	
	private double importeGs;
	private double importeDs;
	private double importeIva;
	
	private double tipoCambio;
	
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
	
	/** Los detalles de las N.C pueden hacer referencia a facturas ó articulos segun el tipo **/
	private Set<NotaCreditoDetalle> detalles = new HashSet<NotaCreditoDetalle>();
	
	/** Las notas de credito reclamo pueden referenciar a uno o mas servicios tecnicos **/
	private Set<ServicioTecnico> serviciosTecnicos = new HashSet<ServicioTecnico>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return el total iva 10..
	 */
	public double getTotalIva10() {
		Misc misc = new Misc();	
		return Math.rint(misc.calcularIVA(this.getImporteGs(), 10) * 1) / 1;
	}
	
	/**
	 * @return el total gravado 10..
	 */
	public double getTotalGravado10() {
		Misc misc = new Misc();	
		return misc.calcularGravado(this.getImporteGs(), 10);
	}
	
	/**
	 * @return el importe total sin iva..
	 */
	public double getTotalImporteGsSinIva() {
		return this.getImporteGs() - this.getTotalIva10();
	}
	
	/**
	 * @return el costo total en Gs..
	 */
	public double getTotalCostoGsSinIva() {
		double out = 0;
		if (!this.isMotivoDevolucion()) {
			return 0.0;
		}
		for (NotaCreditoDetalle item : this.detalles) {
			if (item.getArticulo() != null) {
				out += item.getCostoTotalGsSinIva();
			}
		}
		return Math.rint(out * 1) / 1;
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
}
