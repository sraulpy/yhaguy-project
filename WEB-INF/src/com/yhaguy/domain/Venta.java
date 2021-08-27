package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class Venta extends Domain {
	
	public static final double MARGEN_LINEA_CREDITO = 30;
	public static final double MAXIMO_DESCUENTO = 5;
	
	public static final String FORMA_ENTREGA_SERVICIO = "SERVICIO";
	public static final String FORMA_ENTREGA_EMPAQUE = "EMPAQUE";
	public static final String FORMA_ENTREGA_REPARTO = "REPARTO";
	public static final String FORMA_ENTREGA_TRANSPORTADORA = "TRANSPORTADORA";
	public static final String FORMA_ENTREGA_COLECTIVO = "COLECTIVO";
	public static final String FORMA_ENTREGA_DELIVERY = "DELIVERY";

	/** Presupuesto o Pedido */
	private TipoMovimiento tipoMovimiento;
	
	/** Sucursal del movimiento */
	private SucursalApp sucursal;

	/**
	 * Presupuesto: solo-presupuesto, pasado a pedido Pedido: reservado, vendido
	 */
	private Tipo estado;

	/**
	 * Si pasó de estado: Presupuesto: id del pedido que le corresponde Pedido:
	 * id de la venta que le corresponde
	 */
	private long idEnlaceSiguiente;

	/** El que hizo el pedido */
	private Funcionario atendido;
	
	/** El vendedor */
	private Funcionario vendedor;
	
	/** El tecnico */
	private Funcionario tecnico;
	private Tecnico tecnico_;

	private Cliente cliente;

	private CondicionPago condicionPago;

	/** Depósito de de la venta, con el depósito se puede saber la sucursal */
	private Deposito deposito;
	
	/** Si la venta es por Reparto **/
	private boolean reparto;
	
	/** Si la venta esta dentro de una Planilla de caja cerrada **/
	private boolean planillaCajaCerrada;
	
	/** Si la venta fue cobrada **/
	private boolean cobrado;
	private boolean debitoGroupauto;
	
	/** Datos que van en la impresion de la factura remision **/
	private String puntoPartida;
	private String fechaTraslado;
	private String fechaFinTraslado;
	private String repartidor;
	private String cedulaRepartidor;
	private String marcaVehiculo;	
	private String chapaVehiculo;
	
	/** para la impresion de la factura **/
	private String denominacion;

	private Date fecha;
	private Date vencimiento;
	private int cuotas;
	private int validez;
	
	private String numero = "-- nuevo --";
	private String timbrado;
	private String observacion;
	private String preparadoPor = "";
	private Tipo moneda;
	
	/** Los valores en términos monetarios de la Venta */
	private double totalImporteGs;
	private double totalImporteDs;
	private double tipoCambio; 
	private double costoPromedioGs;
	private double costoUltimoGs;
	
	/** Los numeros de los Documentos que integran la Venta */
	private String numeroPresupuesto;
	private String numeroPedido;
	private String numeroFactura;
	private String numeroNotaCredito;
	private String numeroReciboCobro;
	private String numeroPlanillaCaja;
	private String numeroReparto;
	
	/** Modos de venta: ej. 'venta mostrador', 'venta externa', otros */
	private Tipo modoVenta;
	
	/** El estado del comprobante: confeccionado - anulado - etc**/
	private Tipo estadoComprobante;
	
	/** Forma de Pago: cuando se factura al contado se asigna 1 o varias formas de Pago **/
	private Set<ReciboFormaPago> formasPago = new HashSet<ReciboFormaPago>();
	
	private Reserva reserva;	
	private String cartera;
	
	private String formaEntrega;
	private VehiculoTipo vehiculoTipo;
	private VehiculoMarca vehiculoMarca;
	private VehiculoModelo vehiculoModelo;

	private Set<VentaDetalle> detalles = new HashSet<VentaDetalle>();
	
	/**
	 * @return true si es moneda local..
	 */
	public boolean isMonedaLocal() {
		return this.moneda.getSigla().equals(Configuracion.SIGLA_MONEDA_GUARANI);
	}
	
	/**
	 * @return el nombre del tecnico..
	 */
	public String getNombreTecnico() {
		if (this.getTecnico() == null) return "";
		return this.getTecnico().getRazonSocial();
	}
	
	@DependsOn("detalles")
	public double getImporteGs() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			out += item.getImporteGs();
		}
		return out;
	}
	
	/**
	 * @return el importe segun familias..
	 */
	public double getTotalImporteGs(List<ArticuloFamilia> familias) {
		double out = 0;
		if (familias.size() == 0) {
			return this.getTotalImporteGs();
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
		for (VentaDetalle det : this.getDetalles()) {
			if (det.isFamilia(idFamilia)) {
				out += det.getImporteGs();
			}
		}
		return out;
	}
	
	/**
	 * @return importe segun familia..
	 */
	public double getImporteGsByFamiliaSinIva(long idFamilia) {
		double out = 0;
		for (VentaDetalle det : this.getDetalles()) {
			if (det.isFamilia(idFamilia)) {
				out += det.getImporteGsSinIva();
			}
		}
		return out;
	}
	
	/**
	 * @return el importe segun familias..
	 */
	public double getTotalCostoGs(List<ArticuloFamilia> familias) {
		double out = 0;
		if (familias.size() == 0) {
			return Math.rint(this.getTotalCostoGsSinIva() * 1) / 1;
		}
		for (ArticuloFamilia familia : familias) {
			out += this.getCostoGsByFamilia(familia.getId());
		}
		return Math.rint(out * 1) / 1;
	}
	
	/**
	 * @return importe segun familia..
	 */
	public double getCostoGsByFamilia(long idFamilia) {
		double out = 0;
		for (VentaDetalle det : this.getDetalles()) {
			if (det.isFamilia(idFamilia)) {
				out += det.getCostoTotalGsSinIva();
			}
		}
		return out;
	}
	
	/**
	 * @return cantidad segun familia..
	 */
	public long getCantidadByFamilia(long idFamilia) {
		long out = 0;
		for (VentaDetalle det : this.getDetalles()) {
			if (det.isFamilia(idFamilia)) {
				out += det.getCantidad();
			}
		}
		return out;
	}
	
	/**
	 * @return cantidad segun familia..
	 */
	public double getVolumenByFamilia(long idFamilia) {
		double out = 0;
		for (VentaDetalle det : this.getDetalles()) {
			if (det.isFamilia(idFamilia)) {
				out += det.getCantidad() * det.getArticulo().getVolumen();
			}
		}
		return out;
	}
	
	/**
	 * @return
	 * [0]:familia
	 * [1]:porcentaje
	 */
	public List<Object[]> getProrrateoFamilia() {
		List<Object[]> out = new ArrayList<Object[]>();
		Map<String, Double> acum = new HashMap<String, Double>();
		double importe = this.getTotalImporteGs();
		for (VentaDetalle det : this.getDetalles()) {
			String key = det.getArticulo().getFamilia().getDescripcion();
			Double porc = acum.get(key);
			Double porc_ = Utiles.obtenerPorcentajeDelValor(det.getImporteGs(), importe);
			if (porc != null) {
				porc += porc_;
			} else {
				porc = porc_;
			}
			acum.put(key, porc);
		}
		for (String key : acum.keySet()) {
			double tot = acum.get(key);
			out.add(new Object[] { key, tot });
		}
		return out;
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
	 * @return true si es venta contado..
	 */
	public boolean isVentaContado(){
		return (tipoMovimiento.getSigla().compareTo(
				Configuracion.SIGLA_TM_FAC_VENTA_CONTADO) == 0);
	}
	
	/**
	 * @return true si es venta una venta con nota de credito..
	 */
	public boolean isVentaConNotaDeCredito(){
		if(this.numeroNotaCredito == null) return false;
		return (!this.numeroNotaCredito.isEmpty());
	}
	
	/**
	 * @return true si es venta una venta con recibo de cobro..
	 */
	public boolean isVentaCobrada(){
		if(this.numeroReciboCobro == null) return false;
		return (!this.numeroReciboCobro.isEmpty());
	}
	
	/**
	 * @return el horario de la venta..
	 */
	public int getHora() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(this.fecha); 
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * @return el total importe gs..
	 */
	public double getTotalImporteGs_() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			out += item.getImporteGs();
		}
		return out;
	}
	
	/**
	 * @return el total iva 10..
	 */
	public double getTotalIva10() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			if (item.isIva10()) {
				out += Utiles.getIVA(item.getImporteGs(), 10);
			}
		}
		return out;
	}
	
	/**
	 * @return el total iva 5..
	 */
	public double getTotalIva5() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			if (item.isIva5()) {
				out += Utiles.getIVA(item.getImporteGs(), 5);
			}
		}
		return out;
	}
	
	/**
	 * @return el total gravado 10..
	 */
	public double getTotalGravado10() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			if (item.isIva10()) {
				out += item.getImporteGs();
			}
		}
		return out - Utiles.getIVA(out, 10);
	}
	
	/**
	 * @return el total gravado 5..
	 */
	public double getTotalGravado5() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			if (item.isIva5()) {
				out += item.getImporteGs();
			}
		}
		return out - Utiles.getIVA(out, 5);
	}
	
	/**
	 * @return el total exenta..
	 */
	public double getTotalExenta() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			if (item.isExenta()) {
				out += item.getImporteGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el costo total de la venta..
	 */
	public double getTotalCostoGs() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			out += item.getCostoTotalGs();
		}
		return out;
	}
	
	/**
	 * @return el costo total de la venta sin iva..
	 */
	public double getTotalCostoGsSinIva() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			out += item.getCostoTotalGsSinIva();
		}
		return Math.rint(out * 1) / 1;
	}
	
	/**
	 * @return el costo total de la venta sin iva..
	 */
	public double getTotalCostoPromedioGsSinIva() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			out += item.getCostoPromedioTotalGsSinIva();
		}
		return Math.rint(out * 1) / 1;
	}
	
	/**
	 * @return el importe total sin iva..
	 */
	public double getTotalImporteGsSinIva() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			out += item.getImporteGsSinIva();
		}
		return out;
	}
	
	/**
	 * @return el total efectivo..
	 */
	public double getTotalEfectivo() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isEfectivo()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	/**
	 * @return el total cheque al dia..
	 */
	public double getTotalChequeClienteAldia(Date fecha) {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isChequeTercero() && item.isChequeAlDia(fecha)) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total cheque adelantado..
	 */
	public double getTotalChequeClienteAdelantado(Date fecha) {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isChequeTercero() && item.isChequeAdelantado(fecha)) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total de retencion..
	 */
	public double getTotalRetencion() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isRetencion()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total deposito bancario..
	 */
	public double getTotalDepositoBancario() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isDepositoBancario()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total tarjeta credito..
	 */
	public double getTotalTarjetaCredito() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isTarjetaCredito()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total tarjeta debito..
	 */
	public double getTotalTarjetaDebito() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isTarjetaDebito()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return la rentabilidad del articulo..
	 */
	public double getRentabilidad() {
		double ganancia = this.getTotalImporteGsSinIva() - this.getTotalCostoGsSinIva();
		double out = Utiles.obtenerPorcentajeDelValor(ganancia, this.getTotalCostoGsSinIva());
		return Utiles.redondeoDosDecimales(out);
	}
	
	/**
	 * @return la rentabilidad del articulo..
	 */
	public double getRentabilidadPromedio() {
		double ganancia = this.getTotalImporteGsSinIva() - this.getTotalCostoGsSinIva();
		double out = Utiles.obtenerPorcentajeDelValor(ganancia, this.getTotalCostoGsSinIva());
		return Utiles.redondeoDosDecimales(out);
	}
	
	/**
	 * @return el importe total de la venta segun el proveedor..
	 */
	public double getImporteByProveedor(long idProveedor) throws Exception {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
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
		for (VentaDetalle item : this.detalles) {
			if (item.isProveedor(idProveedor)) {
				out += item.getImporteGsSinIva();
			}
		}
		return out;
	}
	
	/**
	 * @return el total de items segun el proveedor..
	 */
	public int getCantidadItemsByProveedor(long idProveedor) {
		int out = 0;
		for (VentaDetalle item : this.detalles) {
			if (item.isProveedor(idProveedor)) {
				out += item.getCantidad();
			}
		}
		return out;
	}
	
	/**
	 * @return el peso total de la venta..
	 */
	public double getPesoTotal() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			out += (item.getArticulo().getPeso() * item.getCantidad());
		}
		return out;
	}
	
	/**
	 * recalcula los costos segun cotizacion..
	 */
	public void recalcularCotizacion() {
		for (VentaDetalle item : this.detalles) {
			item.setDescuentoUnitarioGs(item.getDescuentoUnitarioDs() * this.tipoCambio);
			item.setPrecioGs(item.getPrecioVentaFinalDs() * this.tipoCambio);
		}
		this.totalImporteGs = this.totalImporteDs * this.tipoCambio;
	}
	
	/**
	 * @return la descripcion del tipo de movimiento..
	 */
	public String getDescripcionTipoMovimiento(){
		return this.tipoMovimiento.getDescripcion();
	}
	
	/**
	 * @return la sigla del tipo de movimiento..
	 */
	public String getSiglaTipoMovimiento(){
		return this.tipoMovimiento.getSigla();
	}
	
	/**
	 * @return la descripcion de la condicion..
	 */
	public String getDescripcionCondicion() {
		return this.condicionPago.getDescripcion();
	}
	
	/**
	 * @return la descripcion del cliente..
	 */
	public String getDescripcionCliente() {
		return this.cliente.getRazonSocial();
	}
	
	/**
	 * @return las formas de entrega de la venta..
	 */
	public static List<String> getFormasEntrega() {
		List<String> out = new ArrayList<String>();
		out.add(FORMA_ENTREGA_SERVICIO);
		out.add(FORMA_ENTREGA_EMPAQUE);
		out.add(FORMA_ENTREGA_REPARTO);
		out.add(FORMA_ENTREGA_TRANSPORTADORA);
		out.add(FORMA_ENTREGA_COLECTIVO);
		out.add(FORMA_ENTREGA_DELIVERY);
		return out;
	}
	
	public void setDescripcionTipoMovimiento(String descripcion) {
	}
	
	public void setSiglaTipoMovimiento(String sigla) {
	}
	
	public void setDescripcionCondicion(String condicion) {
	}
	
	public void setDescripcionCliente(String cliente) {
	}

	public TipoMovimiento getTipoMovimiento() {
		return this.tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public Tipo getEstado() {
		return estado;
	}

	public void setEstado(Tipo estado) {
		this.estado = estado;
	}

	public long getIdEnlaceSiguiente() {
		return idEnlaceSiguiente;
	}

	public void setIdEnlaceSiguiente(long idEnlaceSiguiente) {
		this.idEnlaceSiguiente = idEnlaceSiguiente;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Deposito getDeposito() {
		return deposito;
	}

	public void setDeposito(Deposito deposito) {
		this.deposito = deposito;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
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

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}
	
	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	public Set<VentaDetalle> getDetalles() {
		return detalles;
	}
	
	@DependsOn("detalles")
	public List<VentaDetalle> getDetallesOrdenado() {
		List<VentaDetalle> dets = new ArrayList<VentaDetalle>();
		dets.addAll(this.detalles);
		Collections.sort(dets, new Comparator<VentaDetalle>() {
			@Override
			public int compare(VentaDetalle o1, VentaDetalle o2) {
				long id1 = o1.getId();
				long id2 = o2.getId();
				return (int) (id1 - id2);
			}
		});
		return dets;
	}

	public void setDetalles(Set<VentaDetalle> detalles) {
		this.detalles = detalles;
	}

	public Funcionario getAtendido() {
		return atendido;
	}

	public void setAtendido(Funcionario atendido) {
		this.atendido = atendido;
	}

	public CondicionPago getCondicionPago() {
		return condicionPago;
	}

	public void setCondicionPago(CondicionPago condicionPago) {
		this.condicionPago = condicionPago;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	
	public Funcionario getVendedor() {
		return vendedor;
	}

	public void setVendedor(Funcionario vendedor) {
		this.vendedor = vendedor;
	}	

	public String getNumeroPresupuesto() {
		return numeroPresupuesto;
	}

	public void setNumeroPresupuesto(String numeroPresupuesto) {
		this.numeroPresupuesto = numeroPresupuesto;
	}

	public String getNumeroPedido() {
		return numeroPedido;
	}

	public void setNumeroPedido(String numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}
	
	public int getCuotas() {
		return cuotas;
	}

	public void setCuotas(int cuotas) {
		this.cuotas = cuotas;
	}
	
	public Tipo getModoVenta() {
		return modoVenta;
	}

	public void setModoVenta(Tipo modoVenta) {
		this.modoVenta = modoVenta;
	}	

	public double getTotalImporteGs() {
		return Math.rint(totalImporteGs * 1) / 1;
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

	public Set<ReciboFormaPago> getFormasPago() {
		return formasPago;
	}

	public void setFormasPago(Set<ReciboFormaPago> formasPago) {
		this.formasPago = formasPago;
	}
	
	@Override
	public int compareTo(Object o) {
		
		return -1;
	}

	public String getPuntoPartida() {
		return puntoPartida;
	}

	public void setPuntoPartida(String puntoPartida) {
		this.puntoPartida = puntoPartida;
	}

	public String getFechaTraslado() {
		return fechaTraslado;
	}

	public void setFechaTraslado(String fechaTraslado) {
		this.fechaTraslado = fechaTraslado;
	}

	public String getFechaFinTraslado() {
		return fechaFinTraslado;
	}

	public void setFechaFinTraslado(String fechaFinTraslado) {
		this.fechaFinTraslado = fechaFinTraslado;
	}

	public String getRepartidor() {
		return repartidor;
	}

	public void setRepartidor(String repartidor) {
		this.repartidor = repartidor;
	}

	public String getCedulaRepartidor() {
		return cedulaRepartidor;
	}

	public void setCedulaRepartidor(String cedulaRepartidor) {
		this.cedulaRepartidor = cedulaRepartidor;
	}

	public String getMarcaVehiculo() {
		return marcaVehiculo;
	}

	public void setMarcaVehiculo(String marcaVehiculo) {
		this.marcaVehiculo = marcaVehiculo;
	}

	public String getChapaVehiculo() {
		return chapaVehiculo;
	}

	public void setChapaVehiculo(String chapaVehiculo) {
		this.chapaVehiculo = chapaVehiculo;
	}

	public Tipo getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(Tipo estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public String getDenominacion() {
		return denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}

	public boolean isReparto() {
		return reparto;
	}

	public void setReparto(boolean reparto) {
		this.reparto = reparto;
	}

	public String getNumeroNotaCredito() {
		return numeroNotaCredito;
	}

	public void setNumeroNotaCredito(String numeroNotaCredito) {
		this.numeroNotaCredito = numeroNotaCredito;
	}

	public String getNumeroReciboCobro() {
		return numeroReciboCobro;
	}

	public void setNumeroReciboCobro(String numeroReciboCobro) {
		this.numeroReciboCobro = numeroReciboCobro;
	}

	public String getNumeroPlanillaCaja() {
		return numeroPlanillaCaja;
	}

	public void setNumeroPlanillaCaja(String numeroPlanillaCaja) {
		this.numeroPlanillaCaja = numeroPlanillaCaja;
	}

	public boolean isPlanillaCajaCerrada() {
		return planillaCajaCerrada;
	}

	public void setPlanillaCajaCerrada(boolean planillaCajaCerrada) {
		this.planillaCajaCerrada = planillaCajaCerrada;
	}

	public boolean isCobrado() {
		return cobrado;
	}

	public void setCobrado(boolean cobrado) {
		this.cobrado = cobrado;
	}

	public int getValidez() {
		return validez;
	}

	public void setValidez(int validez) {
		this.validez = validez;
	}

	public String getPreparadoPor() {
		return preparadoPor;
	}

	public void setPreparadoPor(String preparadoPor) {
		this.preparadoPor = preparadoPor;
	}

	public String getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(String timbrado) {
		this.timbrado = timbrado;
	}

	public String getFormaEntrega() {
		return formaEntrega;
	}

	public void setFormaEntrega(String formaEntrega) {
		this.formaEntrega = formaEntrega;
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

	public Tecnico getTecnico_() {
		return tecnico_;
	}

	public void setTecnico_(Tecnico tecnico_) {
		this.tecnico_ = tecnico_;
	}

	public String getCartera() {
		return cartera;
	}

	public void setCartera(String cartera) {
		this.cartera = cartera;
	}

	public String getNumeroReparto() {
		return numeroReparto;
	}

	public void setNumeroReparto(String numeroReparto) {
		this.numeroReparto = numeroReparto;
	}

	public double getCostoPromedioGs() {
		return costoPromedioGs;
	}

	public void setCostoPromedioGs(double totalCostoPromedioGs) {
		this.costoPromedioGs = totalCostoPromedioGs;
	}

	public double getCostoUltimoGs() {
		return costoUltimoGs;
	}

	public void setCostoUltimoGs(double costoUltimoGs) {
		this.costoUltimoGs = costoUltimoGs;
	}

	public boolean isDebitoGroupauto() {
		return debitoGroupauto;
	}

	public void setDebitoGroupauto(boolean debitoGroupauto) {
		this.debitoGroupauto = debitoGroupauto;
	}
}
