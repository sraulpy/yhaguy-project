package com.yhaguy.gestion.venta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.dto.DTO;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.HistoricoLineaCredito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SaldoVale;
import com.yhaguy.domain.VehiculoMarca;
import com.yhaguy.domain.VehiculoModelo;
import com.yhaguy.domain.VehiculoTipo;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaVale;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;
import com.yhaguy.gestion.comun.ReservaDTO;
import com.yhaguy.gestion.empresa.ClienteDTO;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class VentaDTO extends DTO {
	
	private MyArray tipoMovimiento = new MyArray();	
	private MyPair 	sucursal = new MyPair();
	private MyArray estado = new MyArray();
	private long 	idEnlaceSiguiente = 0;
	private MyArray atendido = new MyArray();	
	private MyArray vendedor = new MyArray();
	private MyArray cliente = new MyArray();	
	private ClienteDTO clienteOcasional; 
	private MyArray condicionPago = new MyArray();
	private MyPair 	deposito = new MyPair();
	private boolean reparto = false;
	private Date 	fecha = new Date();
	private Date 	vencimiento = new Date();
	private int 	cuotas = 0;
	private String 	numero = "...";
	private String  timbrado = "";
	private String 	observacion = "sin obs..";
	private String 	preparadoPor = "";
	private String 	obsReparto = "";
	private String  cartera = "";
	private MyArray moneda = new MyArray();
	private MyPair 	modoVenta = new MyPair();
	private MyPair estadoComprobante;
	
	private MyArray tecnico = new MyArray();
	
	/** para la impresion de la factura **/
	private String denominacion = "";
	private boolean debitoGroupauto = false;
	
	private double tipoCambio;
	private int validez = 2;
	
	private String numeroPresupuesto = "";
	private String numeroPedido = "";
	private String numeroFactura = "";
	private String numeroPlanillaCaja = "";
	private String numeroReparto = "";
	
	private List<String> numerosFacturas = new ArrayList<String>();
	private List<SaldoVale> valesGenerados = new ArrayList<SaldoVale>();
	
	// Datos que van en la impresion de la factura remision..
	private String puntoPartida = "";
	private String fechaTraslado = "";
	private String fechaFinTraslado = "";
	private String repartidor = "";
	private String cedulaRepartidor = "";
	private String marcaVehiculo = "";
	private String chapaVehiculo = "";

	private ReservaDTO reserva;
	
	private VehiculoTipo vehiculoTipo;
	private VehiculoMarca vehiculoMarca;
	private VehiculoModelo vehiculoModelo;
	private String formaEntrega = "";

	private List<VentaDetalleDTO> detalles = new ArrayList<VentaDetalleDTO>();	
	private List<ReciboFormaPagoDTO> formasPago = new ArrayList<ReciboFormaPagoDTO>();	
	
	private List<BancoChequeTercero> chequesEliminar = new ArrayList<BancoChequeTercero>();
	
	/**
	 * @return el descuento mayorista..
	 */
	public double getDescuentoMayorista() throws Exception {
		if (this.cliente.getId() <= 0) {
			return 0;
		}
		double out = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		out = rr.getDescuentoMayoristaCliente(this.cliente.getId());
		return out;
	}
	
	/**
	 * valida la cuenta..
	 */
	private void validarCuenta() {
		if (this.cliente.esNuevo() || this.condicionPago.esNuevo())
			return;
		boolean ctaBloqueada = (boolean) this.cliente.getPos9();
		boolean vtaCredito = (boolean) this.cliente.getPos11();
		if ((ctaBloqueada || !vtaCredito) && !this.isCondicionContado()) {
			this.condicionPago = this.getCondicionContado();
			Clients.showNotification("CUENTA BLOQUEADA PARA VENTA CRÉDITO..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		try {
			if ((!this.isCondicionContado()) && (this.getCreditoDisponible() < this.getTotalImporteGs())) {
				this.condicionPago = this.getCondicionContado();
				Clients.showNotification(
						"SALDO INSUFICIENTE PARA VENTA CRÉDITO..",
						Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return la url de la imagen..
	 */
	public String getUrlImagen() {
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			return Configuracion.URL_IMAGES_PUBLIC_MRA + "ventas/" + this.getId() + ".png";
		}
		return Configuracion.URL_IMAGES_PUBLIC_BAT + "ventas/" + this.getId() + ".png";
	}
	
	@DependsOn("detalles")
	public double getTotalImporteGs() {
		double out = 0;		
		for (VentaDetalleDTO item : this.detalles) {
			if (!item.isImpresionDescuento()) {
				out += item.getImporteGs();
			}
		}		
		return Utiles.getRedondeo(out);
	}
	
	@DependsOn("detalles")
	public double getTotalImporteDs() {
		double out = 0;		
		for (VentaDetalleDTO item : this.detalles) {
			if (!item.isImpresionDescuento()) {
				out += item.getImporteDs();
			}
		}		
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalImporteGsIvaIncluido() {
		double out = 0;		
		for (VentaDetalleDTO item : this.detalles) {
			if (!item.isImpresionDescuento()) {
				out += item.isExenta() ? 0.0 : item.getImporteGs();
			}
		}		
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalImporteGsExenta() {
		double out = 0;		
		for (VentaDetalleDTO item : this.detalles) {
			if (!item.isImpresionDescuento()) {
				out += item.isExenta() ? item.getImporteGs() : 0.0;
			}
		}		
		return out;
	}
	
	/**
	 * @return el importe total sin iva..
	 */
	@DependsOn("detalles")
	public double getTotalImporteGsSinIva() {
		return this.getTotalImporteGs() - this.getTotalIva10();
	}
	
	@DependsOn("detalles")
	public double getTotalDescuentoGs() {
		double out = 0;
		for (VentaDetalleDTO item : this.detalles) {
			out += item.getDescuentoUnitarioGs();
		}
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalIva10() {
		double out = 0;		
		for (VentaDetalleDTO item : this.detalles) {
			if(item.isIva10())
				out += item.getTotalIva();
		}		
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalIva10Ds() {
		double out = 0;		
		for (VentaDetalleDTO item : this.detalles) {
			if(item.isIva10())
				out += item.getTotalIvaDs();
		}		
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalIva5() {
		double out = 0;		
		for (VentaDetalleDTO item : this.detalles) {
			if(item.isIva5())
				out += item.getTotalIva();
		}		
		return out;
	}
	
	@DependsOn("detalles")
	public long getTotalCantidad() {
		long out = 0;		
		for (VentaDetalleDTO item : this.detalles) {
			out += item.getCantidad();
		}		
		return out;
	}
	
	@DependsOn("detalles")
	public boolean isVentaConDescuento() {
		return this.getTotalDescuentoGs() > 0;
	}
	
	@DependsOn("cliente")
	public double getLimiteCredito() {
		if (!this.cliente.esNuevo()) {
			return (double) this.cliente.getPos12();
		}
		return 0;
	}
	
	@DependsOn("cliente")
	public double getLineaCreditoTemporal() throws Exception {
		double out = 0;
		if (!this.cliente.esNuevo()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<HistoricoLineaCredito> temp = rr.getHistoricoLineaCredito(this.cliente.getId(), true);
			for (HistoricoLineaCredito linea : temp) {
				out = linea.getImporteGs();
			}
		}
		return out;
	}
	
	@DependsOn("cliente")
	public double getCreditoDisponible() throws Exception {
		if (!this.cliente.esNuevo()) {
			double lineaTemporal = this.getLineaCreditoTemporal();
			double lineaCredito = lineaTemporal > 0 ? lineaTemporal : this.getLimiteCredito();
			System.out.println("--- linea: " + lineaCredito);
			System.out.println("--- saldoctacte: " + this.getSaldoCtaCte());
			System.out.println("--- cheques: " + this.getChequesPendientes());
			return (lineaCredito + Utiles.obtenerValorDelPorcentaje(lineaCredito, Venta.MARGEN_LINEA_CREDITO)) - this.getSaldoCtaCte();
		}
		return 0;
	}
	
	@DependsOn("cliente")
	public double getSaldoCtaCte() throws Exception {
		if (!this.cliente.esNuevo()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			return rr.getSaldoCtaCte((long) this.cliente.getPos4()) + this.getChequesPendientes();
		}
		return 0;
	}
	
	/**
	 * @return los cheques pendientes..
	 */
	private double getChequesPendientes() throws Exception {
		double out = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoChequeTercero> cheques = rr.getChequesTerceroCliente("",
				"", "", "", "", "",
				"", "", "", "", "",
				"", "FALSE", null, "FALSE", "FALSE", null, null, null, null, "", "", true, ((long) this.cliente.getPos4()));
		for (BancoChequeTercero cheque : cheques) {
			if (cheque.getFecha().compareTo(new Date()) > 0) {				
				out += cheque.getMonto();
			}				
		}
		return out;
	}
	
	@DependsOn("cliente")
	public Empresa getEmpresa() throws Exception {
		if (!this.cliente.esNuevo()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			return rr.getEmpresaById((long) this.cliente.getPos4());
		}
		return null;
	}
	
	/**
	 * @return el total del iva..
	 */
	public double getTotalIva() {
		return this.getTotalIva5() + this.getTotalIva10();
	}
	
	/**
	 * @return la cantidad de facturas a generar por pedido de venta..
	 */
	public int getCantidadFacturas_a_generar() {
		int size = this.getDetalles().size();
		int limite = Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA) ? 
				Configuracion.LIMITE_ITEMS_FACTURA_VENTA_BAT : Configuracion.LIMITE_ITEMS_FACTURA_VENTA;
		Double division = (double) ((double) size / limite);
		int entero = division.intValue();
		double decimal = division - entero;
		return entero + (decimal > 0 ? 1 : 0);
	}
	
	/**
	 * @return el total de vale generado por facturacion minima..
	 */
	public double getValeGeneradoFacturacionMinima() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		VentaVale vale = (VentaVale) rr.getObject(VentaVale.class.getName(), 1);
		if (!vale.isVigente()) return 0.0;
		double acumulado = 0;
		double facturacionVale = vale.getFacturacionGs();
		double out = 0;
		Map<String, String> arts = vale.getArticulosMap();
		for (VentaDetalleDTO item : this.detalles) {
			if (arts.get(item.getArticulo().getPos1()) != null) {
				acumulado += item.getImporteGs();
			}
		}		
		while ((acumulado - facturacionVale) >= 0) {
			out += vale.getValeGs();
			acumulado -= facturacionVale;
		}		
		return out;
	}
	
	/**
	 * @return el total de vale generado por facturacion porcentaje..
	 */
	public double getValeGeneradoFacturacionPorcentaje() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		VentaVale vale = (VentaVale) rr.getObject(VentaVale.class.getName(), 2);
		if (vale.getCondicion().getId().longValue() != this.getCondicionPago().getId().longValue())
			return 0.0;
		if (!vale.isVigente())
			return 0.0;
		double acumulado = 0;
		double porcentajeVale = vale.getValePorcentaje();
		double out = 0;
		if (vale.isTodosArticulos()) {
			acumulado = this.getTotalImporteGs();
		} else {
			Map<String, String> arts = vale.getArticulosMap();
			for (VentaDetalleDTO item : this.detalles) {
				if (arts.get(item.getArticulo().getPos1()) != null) {
					acumulado += item.getImporteGs();
				}
			}
		}
		out = Utiles.obtenerValorDelPorcentaje(acumulado, porcentajeVale);
		return out;
	}
	
	/**
	 * @return true si la condicion de venta es contado..
	 */
	public boolean isCondicionContado() {
		return (this.condicionPago.getId().longValue() == Configuracion.ID_CONDICION_PAGO_CONTADO);
	}
	
	/**
	 * @return true si es una factura contado..
	 */
	public boolean isFacturaContado() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return (sigla.compareTo(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO) == 0);
	}
	
	/**
	 * @return true si es una Factura Credito..
	 */
	public boolean isFacturaCredito() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return (sigla.compareTo(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO) == 0);
	}
	
	/**
	 * @return true si es un Presupuesto..
	 */
	public boolean isPresupuesto() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return (sigla.equals(Configuracion.SIGLA_TM_PRESUPUESTO_VENTA));
	}
	
	/**
	 * @return true si es un Presupuesto..
	 */
	public boolean isPedido() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return (sigla.equals(Configuracion.SIGLA_TM_PEDIDO_VENTA));
	}
	
	/**
	 * @return true si la venta es en moneda local..
	 */
	public boolean isMonedaLocal() {
		String sigla = (String) this.moneda.getPos2();
		return (sigla.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0);
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
	 * @return true si la planilla esta abierta..
	 */
	public boolean isCajaPlanillaAbierta() {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CajaPeriodo> cps;
		try {
			cps = rr.getCajaPlanillas(this.numeroPlanillaCaja);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (cps.size() > 0) {
			CajaPeriodo cp = cps.get(0);
			return cp.isAbierto();
		}
		return false;
	}
	
	/**
	 * @return el detalle para impresion del descuento..
	 */
	public List<VentaDetalleDTO> getDetallesConDescuento() {
		List<VentaDetalleDTO> out = new ArrayList<VentaDetalleDTO>();
		out.addAll(this.getDetalles());
		if (this.isVentaConDescuento()) {
			VentaDetalleDTO itemPrint = null;
			for (VentaDetalleDTO item : this.getDetalles()) {
				if (item.isImpresionDescuento()) {
					itemPrint = item;
				}
			}
			this.getDetalles().remove(itemPrint);
			out.remove(itemPrint);
			MyArray art = new MyArray();
			art.setPos1("DESCUENTO");
			art.setPos4("DESCUENTO");
			VentaDetalleDTO item = new VentaDetalleDTO();
			item.setArticulo(art);
			item.setCantidad(1);
			if (this.isMonedaLocal()) {
				item.setPrecioGs(this.getTotalDescuentoGs() * -1);
			} else {
				item.setPrecioVentaFinalDs(this.getTotalDescuentoGs() * -1);
			}			
			item.setTipoIVA(new MyPair());
			item.setImpresionDescuento(true);				
			out.add(item);
		}
		return out;
	}
	
	/**
	 * @return los items que son servicio..
	 */
	public List<VentaDetalleDTO> getItemsServicio() {
		List<VentaDetalleDTO> out = new ArrayList<VentaDetalleDTO>();
		for (VentaDetalleDTO item : this.detalles) {
			if (item.isServicio()) {
				out.add(item);
			}
		}
		return out;
	}
	
	/**
	 * @return la razon social..
	 */
	public String getRazonSocial() {
		return (String) this.cliente.getPos2();
	}
	
	/**
	 * @return el ruc..
	 */
	public String getRuc() {
		return (String) this.cliente.getPos3();
	}
	
	/**
	 * @return la razon social..
	 */
	public String getNombreFantasia() {
		return (String) this.cliente.getPos10();
	}
	
	/**
	 * @return la direccion..
	 */
	public String getDireccion() {
		return (String) this.cliente.getPos6();
	}
	
	/**
	 * @return la direccion..
	 */
	public String getTelefono() {
		return (String) this.cliente.getPos7();
	}
	
	/**
	 * @return la fecha formateada..
	 */
	public String getFechaEmision() {
		return Utiles.getDateToString(this.fecha, Misc.DD_MM_YYYY);
	}
	
	/**
	 * @return el importe en letras..
	 */
	public String getImporteEnLetras() {
		double importe = this.isMonedaLocal() ? this.getTotalImporteGs() : this.getTotalImporteDs();
		return getMisc().numberToLetter(importe).toLowerCase();
	}
	
	/**
	 * @return el nombre del vendedor..
	 */
	public String getVendedor_() {
		return (String) this.vendedor.getPos1();
	}
	
	/**
	 * @return la razon social y el nombre fantasia..
	 */
	public List<String> getDenominaciones() {
		String razonSocial = (String) this.cliente.getPos2();
		String fantasia = (String) this.cliente.getPos8();
		List<String> out = new ArrayList<String>();
		out.add(razonSocial);
		out.add(fantasia);
		return out;
	}
	
	/**
	 * @return la condicion de venta contado..
	 */
	private MyArray getCondicionContado() {
		MyArray out = new MyArray("Contado", 0, 0, 0);
		out.setId(Configuracion.ID_CONDICION_PAGO_CONTADO);
		return out;
	}
	
	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}

	public MyArray getEstado() {
		return estado;
	}

	public void setEstado(MyArray estado) {
		this.estado = estado;
	}
	
	public void setEstado(MyPair estado) {
		MyArray estado_ = new MyArray();
		estado_.setId(estado.getId());
		this.estado = estado_;
	}

	public long getIdEnlaceSiguiente() {
		return idEnlaceSiguiente;
	}

	public void setIdEnlaceSiguiente(long idEnlaceSiguiente) {
		this.idEnlaceSiguiente = idEnlaceSiguiente;
	}

	public MyArray getAtendido() {
		return atendido;
	}

	public void setAtendido(MyArray atendido) {
		this.atendido = atendido;
	}
	
	public MyArray getVendedor() {
		return vendedor;
	}

	public void setVendedor(MyArray vendedor) {
		this.vendedor = vendedor;
	}

	public MyArray getCondicionPago() {
		return condicionPago;
	}

	public void setCondicionPago(MyArray condicionPago) {
		this.condicionPago = condicionPago;
		this.validarCuenta();
	}

	public MyArray getCliente() {
		return cliente;
	}

	public void setCliente(MyArray cliente) {
		this.cliente = cliente;
		this.clienteOcasional = null;
		BindUtils.postGlobalCommand(null, null, "validarCuenta", null);
		this.validarCuenta();
	}

	public ClienteDTO getClienteOcasional() {
		return clienteOcasional;
	}

	public void setClienteOcasional(ClienteDTO clienteOcasional) {
		this.clienteOcasional = clienteOcasional;
	}
	
	public MyPair getDeposito() {
		return deposito;
	}

	public void setDeposito(MyPair deposito) {
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
		String obsRep = observacion + " -Reparto: " + obsReparto;
		return obsReparto.length() > 0 ? obsRep : observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public MyArray getMoneda() {
		return moneda;
	}

	public void setMoneda(MyArray moneda) {
		this.moneda = moneda;
	}

	public ReservaDTO getReserva() {
		return reserva;
	}

	public void setReserva(ReservaDTO reserva) {
		this.reserva = reserva;
	}	

	public List<VentaDetalleDTO> getDetalles_() {
		Collections.sort(this.detalles,
				new Comparator<VentaDetalleDTO>() {
					@Override
					public int compare(VentaDetalleDTO o1,
							VentaDetalleDTO o2) {
						long id1 = o1.getId().longValue();
						long id2 = o2.getId().longValue();
						if (id1 < 0) {
							return 1;
						}
						return (int) (id1 - id2);
					}
				});
		return detalles;
	}
	
	/**
	 * @return el sub-detalle segun el parametro de desglose..
	 */
	public List<VentaDetalleDTO> getDetallesDesglose(int desglose) {
		int limite = Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA) ? 
				Configuracion.LIMITE_ITEMS_FACTURA_VENTA_BAT : Configuracion.LIMITE_ITEMS_FACTURA_VENTA;
		int size = this.getDetalles().size();
		int desde = (desglose - 1) * (limite);
		int hasta = (((desglose - 1) * (limite))) + (limite);
		return this.getDetalles().subList(desde, (hasta > size ? size : hasta));
	}

	public void setDetalles(List<VentaDetalleDTO> detalles) {
		this.detalles = detalles;
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
	
	public MyPair getModoVenta() {
		return modoVenta;
	}

	public void setModoVenta(MyPair modoVenta) {
		this.modoVenta = modoVenta;
	}

	public void setTotalImporteGs(double totalImporteGs) {
	}

	public void setTotalImporteDs(double totalImporteDs) {
	}	

	public List<ReciboFormaPagoDTO> getFormasPago() {
		return formasPago;
	}

	public void setFormasPago(List<ReciboFormaPagoDTO> formasPago) {
		this.formasPago = formasPago;
	}	

	public String getObsReparto() {
		return obsReparto;
	}

	public void setObsReparto(String obsReparto) {
		this.obsReparto = obsReparto;
	}	
	
	public String toString() {
		String out = "";

		out += this.numero + " - "
				+ Utiles.getDateToString(this.fecha, Misc.D_MMMM_YYYY2) + " - "
				+ this.cliente.getPos1();
		return out;
	}

	public List<String> getNumerosFacturas() {
		return numerosFacturas;
	}

	public void setNumerosFacturas(List<String> numerosFactura) {
		this.numerosFacturas = numerosFactura;
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

	public String getDenominacion() {
		return denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}

	public MyPair getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(MyPair estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public boolean isReparto() {
		return reparto;
	}

	public void setReparto(boolean reparto) {
		this.reparto = reparto;
	}

	public String getNumeroPlanillaCaja() {
		return numeroPlanillaCaja;
	}

	public void setNumeroPlanillaCaja(String numeroPlanillaCaja) {
		this.numeroPlanillaCaja = numeroPlanillaCaja;
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
		this.preparadoPor = preparadoPor.toUpperCase();
	}

	public String getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(String timbrado) {
		this.timbrado = timbrado;
	}

	public List<VentaDetalleDTO> getDetalles() {
		return detalles;
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

	public String getFormaEntrega() {
		return formaEntrega;
	}

	public void setFormaEntrega(String formaEntrega) {
		this.formaEntrega = formaEntrega;
	}

	public MyArray getTecnico() {
		return tecnico;
	}

	public void setTecnico(MyArray tecnico) {
		this.tecnico = tecnico;
	}

	public List<BancoChequeTercero> getChequesEliminar() {
		return chequesEliminar;
	}

	public void setChequesEliminar(List<BancoChequeTercero> chequesEliminar) {
		this.chequesEliminar = chequesEliminar;
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

	public List<SaldoVale> getValesGenerados() {
		return valesGenerados;
	}

	public void setValesGenerados(List<SaldoVale> valesGenerados) {
		this.valesGenerados = valesGenerados;
	}

	public boolean isDebitoGroupauto() {
		return debitoGroupauto;
	}

	public void setDebitoGroupauto(boolean debitoGroupauto) {
		this.debitoGroupauto = debitoGroupauto;
	}
}
