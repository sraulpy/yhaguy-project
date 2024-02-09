package com.yhaguy.gestion.caja.periodo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import com.coreweb.Config;
import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.domain.AutoNumero;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.extras.browser.Browser;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.Caja;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.CajaReposicion;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.CompraLocalFacturaDetalle;
import com.yhaguy.domain.CompraLocalOrden;
import com.yhaguy.domain.CompraLocalOrdenDetalle;
import com.yhaguy.domain.CondicionPago;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.DebitoGroupauto;
import com.yhaguy.domain.DebitoGroupautoDetalle;
import com.yhaguy.domain.DebitoGroupautoFormaPago;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Remision;
import com.yhaguy.domain.RemisionDetalle;
import com.yhaguy.domain.SaldoVale;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.Talonario;
import com.yhaguy.domain.TareaProgramada;
import com.yhaguy.domain.Tecnico;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.gestion.bancos.libro.ControlBancoMovimiento;
import com.yhaguy.gestion.caja.principal.AssemblerCaja;
import com.yhaguy.gestion.caja.principal.CajaDTO;
import com.yhaguy.gestion.caja.recibos.AssemblerRecibo;
import com.yhaguy.gestion.caja.recibos.AssemblerReciboFormaPago;
import com.yhaguy.gestion.caja.recibos.BeanFormaPagoCheque;
import com.yhaguy.gestion.caja.recibos.BeanReciboDetalle;
import com.yhaguy.gestion.caja.recibos.ReciboDTO;
import com.yhaguy.gestion.caja.recibos.ReciboDetalleDTO;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;
import com.yhaguy.gestion.compras.gastos.subdiario.GastoDTO;
import com.yhaguy.gestion.comun.Control;
import com.yhaguy.gestion.comun.ControlAnulacionMovimientos;
import com.yhaguy.gestion.comun.ControlArticuloCostoPromedio;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.gestion.contabilidad.retencion.RetencionIvaDTO;
import com.yhaguy.gestion.contabilidad.retencion.RetencionIvaDetalleDTO;
import com.yhaguy.gestion.modulos.Permisos;
import com.yhaguy.gestion.notacredito.NotaCreditoAssembler;
import com.yhaguy.gestion.notacredito.NotaCreditoControlBody;
import com.yhaguy.gestion.notacredito.NotaCreditoDTO;
import com.yhaguy.gestion.notacredito.NotaCreditoDetalleDTO;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.gestion.venta.AssemblerVenta;
import com.yhaguy.gestion.venta.VentaControlBody;
import com.yhaguy.gestion.venta.VentaDTO;
import com.yhaguy.gestion.venta.VentaDetalleDTO;
import com.yhaguy.process.ProcesosHistoricos;
import com.yhaguy.util.Utiles;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class CajaPeriodoControlBody extends BodyApp {
	
	//test git..
	
	static final String ZUL_IMPRESION_FACTURA_BAT = "/yhaguy/gestion/caja/periodo/impresion_factura.zul";
	static final String ZUL_IMPRESION_FACTURA = "/yhaguy/gestion/caja/periodo/impresion_factura.zul";
	static final String ZUL_IMPRESION_FACTURA_DS = "/yhaguy/gestion/caja/periodo/impresion_factura_ds.zul";
	static final String ZUL_IMPRESION_FACTURA_RPS = "/yhaguy/gestion/caja/periodo/impresion_factura_rps.zul";
	static final String ZUL_IMPRESION_FACTURA_RPS_DS = "/yhaguy/gestion/caja/periodo/impresion_factura_rps_ds.zul";
	static final String ZUL_IMPRESION_NOTACREDITO = "/yhaguy/gestion/caja/periodo/impresion_notacredito.zul";
	static final String ZUL_IMPRESION_NOTACREDITO_RPS = "/yhaguy/gestion/caja/periodo/impresion_notacredito_rps.zul";
	static final String ZUL_IMPRESION_NOTACREDITO_DS = "/yhaguy/gestion/caja/periodo/impresion_notacredito_ds.zul";
	static final String ZUL_IMPRESION_NOTACREDITO_RPS_DS = "/yhaguy/gestion/caja/periodo/impresion_notacredito_rps_ds.zul";
	static final String ZUL_IMPRESION_RECIBO = "/yhaguy/gestion/caja/periodo/impresion_recibo.zul";

	private CajaPeriodoDTO dto = new CajaPeriodoDTO();
	private MyArray selectedChequera = new MyArray();
	private MyArray reposicion = new MyArray();
	private MyArray selectedItem;
	private String selectedDenominacion;
	private Date fechaDesde = new Date();
	private Date fechaHasta = new Date();
	private Object[] selectedCajaChica;
	private String nroPlanilla = "";

	private Window win;
	
	private int totalPedidosPendientes = 0;

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	static final NumberFormat FORMATTER_DS = new DecimalFormat("###,###,##0.00");
	
	@Wire
	private Window win_caja;

	@Init(superclass = true)
	public void init() {
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public Assembler getAss() {
		return new CajaPeriodoAssembler();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (CajaPeriodoDTO) dto;
		
		if (this.isEmpresaCentral() || this.isEmpresaGroupauto()) {
			if (this.dto.getTipo().contains(CajaPeriodo.TIPO_CHICA)) {
				if (this.dto.getDetalles().size() == 0) {
					try {
						this.abrirVentanaSaldoCajaChica();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}		
		
		if (!this.isEmpresaGTSA()) {
			if (!this.dto.esReadonly() && !this.dto.esNuevo()) {
				if (CajaUtil.CAJAS_ABIERTAS.get(this.dto.getNumero()) != null) {
					String msg = "CAJA BLOQUEADA POR USUARIO: " + CajaUtil.CAJAS_ABIERTAS.get(this.dto.getNumero());
					Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					this.dto = new CajaPeriodoDTO();
				} else {
					CajaUtil.CAJAS_ABIERTAS.put(this.dto.getNumero(), this.getUs().getNombre().toUpperCase());
					this.win_caja.setHeight("99%");
					this.win_caja.setWidth("99%");
					this.win_caja.setMode(Window.MODAL);
					this.win_caja.setClosable(true);
					this.win_caja.setBorder("normal");
					this.win_caja.setTitle("Caja / Planilla");
				}
			}
		}		
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		CajaPeriodoDTO out = new CajaPeriodoDTO();
		out.setEstado(this.getDtoUtil().getCajaPeriodoEstadoAbierta());
		return out;
	}

	@Override
	public String getEntidadPrincipal() {
		return CajaPeriodo.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(getEntidadPrincipal());
	}

	@Override
	public Browser getBrowser() {
		return new CajaPeriodoBrowser();
	}

	@Override
	public boolean getInformacionDeshabilitado() {
		return this.dto.esNuevo();
	}

	@Override
	public void showInformacion() throws Exception {
		this.mensajeInfo(this.getInformacionAdicional());
	}

	/************************* COMANDOS *************************/

	@Command
	public void imprimirItem() throws Exception {
		this.imprimirItem_();
	}

	@Command
	public void imprimirRetencion() throws Exception {
		this.imprimirRetencion_();
	}

	@Command
	public void reporteCajaPlanilla() throws Exception {
		this.imprimirResumen();
	}

	@Command
	public void selectDenominacion() {
		this.selectDenominacion_();
	}
	
	@Command
	public void facturarVenta() throws Exception {
		this.facturarVenta_();
	}
	
	@Command
	@NotifyChange("totalPedidosPendientes")
	public void verificarPedidosAprobados() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.getFecha(Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY + " 00:00:00"));
		List<Venta> vtas = new ArrayList<Venta>();
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			vtas = rr.getPedidosPendientesBaterias(this.getSucursal().getId(), desde, new Date());
		}
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRSA)) {
			vtas = rr.getPedidosPendientes(this.getSucursal().getId(), desde, new Date());
		}		
		this.totalPedidosPendientes = vtas.size();
	}
	
	@Command
	public void cancelarFacturacion() {
		this.selectedVenta = null;
		this.selectedDenominacion = null;
		this.win.detach();
	}
	
	@Command
	@NotifyChange("*")
	public void abrirCaja() throws Exception {
		this.reabrirCaja();
	}
	
	@Command
	@NotifyChange("*")
	public void confirmarRendicion() throws Exception {
		this.confirmarRendicionCaja();
	}
	
	@Command
	@NotifyChange("selectedVenta")
	public void guaranizarVenta() throws Exception {
		if (!this.selectedVenta.isMonedaLocal()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			double tc = rr.getTipoCambioVenta();
			this.selectedVenta.setTipoCambio(tc);
			for (VentaDetalleDTO item : this.selectedVenta.getDetalles()) {
				item.setPrecioVentaFinalDs(item.getPrecioGs() / this.selectedVenta.getTipoCambio());
			}
		}
	}
	
	@Command
	public void desbloquear() {
		this.getPagina().getTool().mainComponent.detach();
		CajaUtil.CAJAS_ABIERTAS.remove(this.dto.getNumero());
	}
	
	@Command
	@NotifyChange("*")
	public void actualizarDebitoGroupauto() throws Exception {
		
		RegisterDomain rr = RegisterDomain.getInstance();
		Funcionario func = rr.getFuncionario("ADMINISTRACION");
		Deposito dep = (Deposito) rr.getObject(Deposito.class.getName(), 2);
		CondicionPago cond = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CREDITO_30);
		Tipo moneda = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		Proveedor prov = rr.getProveedorById(18);
		Tipo iva = rr.getTipoPorSigla(Configuracion.SIGLA_IVA_10);
		SucursalApp suc = rr.getSucursalAppById(2);
		Date fecha = new Date();
		
		List<DebitoGroupauto> list = rr.getDebitosGroupauto();
		for (DebitoGroupauto dbg : list) {
			
			CompraLocalFactura fac = new CompraLocalFactura();
			fac.setCondicionPago(cond);
			fac.setFechaCreacion(fecha);
			fac.setFechaOriginal(fecha);
			fac.setFechaVencimiento(fecha);
			fac.setImporteGs(dbg.getTotalImporteGs());
			fac.setMoneda(moneda);
			fac.setNumero(dbg.getOrigen());
			fac.setProveedor(prov);
			fac.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO));
			fac.setSucursal(suc);
			fac.setDbEstado('R');
			Set<CompraLocalFacturaDetalle> facdets = new HashSet<CompraLocalFacturaDetalle>();
			for (DebitoGroupautoDetalle item : dbg.getDetalles()) {
				CompraLocalFacturaDetalle det = new CompraLocalFacturaDetalle();
				det.setArticulo(this.getArticulo(item.getCodigo(), item.getDescripcion(), item.getFamilia().getId()));
				det.setCantidad(Integer.parseInt(item.getCantidad() + ""));
				det.setCostoFinalGs(item.getCostoGs());
				det.setCostoPromedioGs(item.getCostoGs());
				det.setCostoGs(item.getCostoGs());
				det.setIva(iva);
				facdets.add(det);
			}
			fac.setDetalles(facdets);
			rr.saveObject(fac, this.getLoginNombre());
			
			CompraLocalOrden oc = new CompraLocalOrden();
			oc.setFechaCreacion(fecha);
			oc.setAutorizado(true);
			oc.setAutorizadoPor("ADMINISTRACION");
			oc.setCerrado(true);
			oc.setDeposito(dep);
			oc.setCondicionPago(cond);
			oc.setMoneda(moneda);
			oc.setNumero("OCL-" + AutoNumeroControl.getAutoNumero("OCL", 5));
			oc.setObservacion("DEBITOS GROUPAUTO");
			oc.setNumeroFactura(dbg.getOrigen());
			oc.setProveedor(prov);
			oc.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_ORDEN_COMPRA));
			oc.setFactura(fac);
			oc.setSucursal(suc);
			oc.setDbEstado('R');
			Set<CompraLocalOrdenDetalle> ocdets = new HashSet<CompraLocalOrdenDetalle>();
			for (DebitoGroupautoDetalle item : dbg.getDetalles()) {
				CompraLocalOrdenDetalle det = new CompraLocalOrdenDetalle();
				det.setArticulo(this.getArticulo(item.getCodigo(), item.getDescripcion(), item.getFamilia().getId()));
				det.setCantidad(Integer.parseInt(item.getCantidad() + ""));
				det.setCostoGs(item.getCostoGs());
				det.setIva(iva);
				det.setUltCostoGs(item.getCostoGs());
				ocdets.add(det);
			}
			oc.setDetalles(ocdets);
			rr.saveObject(oc, this.getLoginNombre());
			
			Empresa emp = rr.getEmpresaByCodigo("#DEBITO_GROUPAUTO");
			CtaCteEmpresaMovimiento cm = new CtaCteEmpresaMovimiento();
			cm.setCarteraCliente(rr.getCartera("OTROS"));
			cm.setFechaEmision(new Date());
			cm.setFechaVencimiento(Utiles.agregarDias(new Date(), 30));
			cm.setIdEmpresa(emp.getId());
			cm.setIdMovimientoOriginal(fac.getId());
			cm.setIdVendedor(0);
			cm.setImporteOriginal(fac.getImporteGs());
			cm.setMoneda(moneda);
			cm.setNroComprobante(fac.getNumero());
			cm.setSaldo(fac.getImporteGs());
			cm.setSucursal(fac.getSucursal());
			cm.setTipoCambio(fac.getTipoCambio());
			cm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
			cm.setTipoMovimiento(fac.getTipoMovimiento());
			rr.saveObject(cm, this.getLoginNombre());
			
			Venta vta = new Venta();
			vta.setAtendido(func);
			vta.setCartera("OTROS");
			vta.setCliente(this.getCliente(dbg.getRuc(), dbg.getRazonSocial()));
			vta.setCondicionPago(rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CONTADO));
			vta.setDenominacion(dbg.getRazonSocial());
			vta.setDeposito(dep);
			vta.setEstado(rr.getTipoPorSigla(Configuracion.SIGLA_VENTA_ESTADO_FACTURADO));
			vta.setFecha(new Date());
			vta.setVencimiento(new Date());
			vta.setFormaEntrega(Venta.FORMA_ENTREGA_EMPAQUE);
			vta.setFormasPago(null);
			vta.setTipoCambio(1);
			vta.setModoVenta(rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_MODO_VENTA_MOSTRADOR));
			vta.setMoneda(moneda);
			vta.setNumero(this.getNumeroVenta());
			vta.setNumeroPlanillaCaja(this.dto.getNumero());
			vta.setTecnico(func);
			vta.setTecnico_((Tecnico) rr.getObject(Tecnico.class.getName(), 1));
			vta.setTimbrado(this.dto.getCaja().getTalonarioVentas().getPos1() + "");
			vta.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO));
			vta.setTotalImporteGs(dbg.getTotalImporteGs());
			vta.setVendedor(func);
			vta.setSucursal(suc);
			Set<VentaDetalle> dets = new HashSet<VentaDetalle>();
			for (DebitoGroupautoDetalle item : dbg.getDetalles()) {
				VentaDetalle det = new VentaDetalle();
				det.setArticulo(this.getArticulo(item.getCodigo(), item.getDescripcion(), item.getFamilia().getId()));
				det.setCantidad(item.getCantidad());
				det.setCostoPromedioGs(item.getCostoGs());
				det.setCostoUnitarioGs(item.getCostoGs());
				det.setDescripcion(item.getDescripcion());
				det.setListaPrecio(rr.getListaDePrecio(1));
				det.setPrecioGs(item.getPrecioGs());
				det.setTipoIVA(iva);
				dets.add(det);
			}
			Set<ReciboFormaPago> fps = new HashSet<ReciboFormaPago>();
			for (DebitoGroupautoFormaPago item : dbg.getFormasPago()) {
				ReciboFormaPago fp = new ReciboFormaPago();
				fp.setDepositoBancoCta(item.getDepositoBancoCta());
				fp.setDepositoNroReferencia(item.getDepositoNroReferencia());
				fp.setDescripcion(item.getDescripcion());
				fp.setFechaOperacion(fecha);
				fp.setIdSucursal(suc.getId());
				fp.setImporteAcreditado(item.getMontoGs());
				fp.setMoneda(moneda);
				fp.setMontoChequeGs(item.getMontoChequeGs());
				fp.setMontoGs(item.getMontoGs());
				fp.setNroComprobanteAsociado(item.getNroComprobanteAsociado());
				fp.setRetencionNumero(item.getRetencionNumero());
				fp.setTarjetaNumero(item.getTarjetaNumero());
				fp.setTarjetaNumeroComprobante(item.getTarjetaNumeroComprobante());
				fp.setTarjetaProcesadora(item.getTarjetaProcesadora());
				fp.setTarjetaTipo(item.getTarjetaTipo());
				fp.setTipo(item.getTipo());
				fps.add(fp);
			}
			vta.setDetalles(dets);
			vta.setFormasPago(fps);
			rr.saveObject(vta, this.getLoginNombre());
			
			dbg.setAuxi("OK");
			rr.saveObject(dbg, this.getLoginNombre());
			
			CajaPeriodo planilla = (CajaPeriodo) rr.getObject(CajaPeriodo.class.getName(), this.dto.getId());
			planilla.getVentas().add(vta);
			rr.saveObject(planilla, this.getLoginNombre());
			
			this.dto = (CajaPeriodoDTO) this.getDTOById(CajaPeriodo.class.getName(), this.dto.getId());
		}
	}
	
	/**
	 * @return cliente by ruc..
	 */
	private Cliente getCliente(String ruc, String razonSocial) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Cliente cl = ruc.equals(Configuracion.RUC_EMPRESA_LOCAL)
				? rr.getClienteByRazonSocial("CONSUMIDOR FINAL")
				: rr.getClienteByRuc(ruc);
		if (cl != null) {
			return cl;
		}
		
		Empresa emp = new Empresa();
		emp.setRuc(ruc);
		emp.setRazonSocial(razonSocial);
		emp.setNombre(razonSocial);
		rr.saveObject(emp, this.getLoginNombre());
		
		Cliente out = new Cliente();
		out.setCartera("OTROS");
		out.setVentaCredito(false);
		out.setEmpresa(emp);
		rr.saveObject(out, this.getLoginNombre());
		return out;
	}
	
	/**
	 * @return articulo by codigo..
	 */
	private Articulo getArticulo(String codigo, String descripcion, long idFamilia) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo art = rr.getArticuloByCodigoInterno(codigo);
		if (art != null) {
			return art;
		}
		
		Articulo out = new Articulo();
		out.setFamilia((ArticuloFamilia) rr.getObject(ArticuloFamilia.class.getName(), idFamilia));
		out.setCodigoInterno(codigo);
		out.setDescripcion(descripcion);
		rr.saveObject(out, this.getLoginNombre());
		
		return out;
	}

	/************************************************************/

	
	/************************ CONSTANTES ************************/

	public static final int ES_PAGO = 1;
	public static final int ES_REPOSICION = 2;
	public static final int ES_EGRESO = 3;
	public static final int ES_COBRO = 4;
	public static final int ES_VENTA_CONTADO = 5;
	public static final int ES_VENTA_CREDITO = 6;
	public static final int ES_NOTA_CREDITO_VENTA = 7;
	public static final int ES_GASTO = 8;
	public static final int ES_CANC_CHQ_RECHAZADO = 9;
	public static final int ES_REEMBOLSO_PRESTAMO = 10;
	public static final int ES_NOTA_CREDITO_COMPRA = 11;
	public static final int ES_COMPRA = 12;
	public static final int ES_DEVOLUCION_ANTICIPO = 13;
	public static final int ES_CANC_CHQ_RECHAZADO_PROV = 14;

	public static String getStrTipo(int tipo) {
		String out = "	-- error tipo (" + tipo + ")--";
		if (tipo == ES_PAGO) {
			out = "PAGO";
		} else if (tipo == ES_REPOSICION) {
			out = "REPOSICION";
		} else if (tipo == ES_EGRESO) {
			out = "EGRESO";
		} else if (tipo == ES_COBRO) {
			out = "COBRO";
		} else if (tipo == ES_VENTA_CONTADO) {
			out = "VENTA CONTADO";
		} else if (tipo == ES_VENTA_CREDITO) {
			out = "VENTA CREDITO";
		} else if (tipo == ES_NOTA_CREDITO_VENTA) {
			out = "NOTA DE CREDIDO";
		} else if (tipo == ES_GASTO) {
			out = "GASTO";
		} else if (tipo == ES_COMPRA) {
			out = "COMPRA";
		} else if (tipo == ES_DEVOLUCION_ANTICIPO) {
			out = "DEVOLUCIÓN ANTICIPO";
		}
		return out;
	}

	/************************************************************/

	
	/************************ FUNCIONES *************************/
	
	/**
	 * reabrir caja..
	 */
	private void reabrirCaja() throws Exception {
		long dias = Utiles.diasEntreFechas(this.dto.getCierre(), new Date());
		
		if (dias > 1) {
			this.mensajeError("Solo puede reabrirse hasta 24hs luego del cierre..");
			return;
		}
		
		if (this.mensajeSiNo("Desea abrir la Planilla de Caja..?")) {
			this.dto.setCierre(null);
			this.dto.setEstado(this.getDtoUtil().getCajaPeriodoEstadoAbierta());
			this.dto.setDbEstado(' ');
			this.dto = (CajaPeriodoDTO) this.saveDTO(this.dto);
		}	
	}
	
	/**
	 * reabrir caja..
	 */
	private void confirmarRendicionCaja() throws Exception {
		
		if (this.mensajeSiNo("Confirma la rendicion de esta caja..?")) {
			this.dto.setControlRendicion(true);
			this.dto = (CajaPeriodoDTO) this.saveDTO(this.dto);
		}	
	}

	/**
	 * selecciona la denominacion para la impresion de la factura..
	 */
	private void selectDenominacion_() {
		this.selectedVenta.setDenominacion(this.selectedDenominacion);
		BindUtils.postNotifyChange(null, null, this.selectedVenta, "denominacion");
	}
	
	/**
	 * facturacion de una venta..
	 */
	private void facturarVenta_() throws Exception {
		this.win.detach();
		if (this.selectedVenta.isCondicionContado() == true) {
			this.asignarFormaPago(this.selectedVenta);
		} else {
			this.generarFacturaVenta(this.selectedVenta);
		}
		this.selectedVenta = null;
		this.selectedDenominacion = null;
	}

	/************************************************************/

	
	/************************ BUSCAR CAJA ***********************/

	private static String[] attCaja = { "numero", "tipo.descripcion",
			"responsable.empresa.razonSocial", "sucursal.nombre" };
	
	private static String[] columnas = { "Número", "Tipo", "Responsable",
			"Sucursal" };

	@Command
	@NotifyChange("*")
	public void buscarCaja() throws Exception {

		long idFuncionario = this.getAcceso().getFuncionario().getId().longValue();
		String where = "responsable.id = " + idFuncionario;

		BuscarElemento b = new BuscarElemento();
		b.setClase(Caja.class);
		b.setAtributos(attCaja);
		b.setNombresColumnas(columnas);
		b.setTitulo("Buscar Caja");
		b.setWidth("900px");
		b.setAssembler(new AssemblerCaja());
		b.addWhere(where);
		b.addOrden("numero");
		b.show(this.dto.getCaja().getNumero());

		if (b.isClickAceptar()) {
			CajaDTO caja = (CajaDTO) b.getSelectedItemDTO();
			this.dto.setResponsable(caja.getResponsable());
			this.dto.setCaja(caja);
		}
	}

	/************************************************************/
	

	/******************** ASIGNAR VERIFICADOR *******************/

	private static String[] attFuncionario = { "empresa.nombre" };
	private static String[] colsFuncionario = { "Apellido y Nombre" };

	@Command
	@NotifyChange("*")
	public void buscarVerificador() throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(Funcionario.class);
		b.setAtributos(attFuncionario);
		b.setNombresColumnas(colsFuncionario);
		b.setTitulo("Buscar Funcionario");
		b.setWidth("600px");
		b.addOrden("empresa.nombre");
		b.addWhere("id != " + this.dto.getResponsable().getId());
		b.show((String) this.dto.getVerificador().getPos1());
		if (b.isClickAceptar()) {
			this.dto.setVerificador(b.getSelectedItem());
		}
	}

	/************************************************************/
	

	/********************** AGREGAR RECIBOS *********************/

	private ReciboDTO reciboDTO;

	@Command
	@NotifyChange("*")
	public void abrirVentanaRecibo(@BindingParam("tipo") int tipo)
			throws Exception {
		this.abrirVentanaRecibo(tipo, WindowPopup.NUEVO, false);
	}
	
	@Command
	@NotifyChange("*")
	public void abrirVentanaRecaudacionMRA(@BindingParam("tipo") int tipo)
			throws Exception {
		this.abrirVentanaRecibo(tipo, WindowPopup.NUEVO, true);
	}

	/**
	 * Despliega la ventana del recibo..
	 */
	private void abrirVentanaRecibo(int tipo, String modo, boolean recaudacionMRA) throws Exception {
		
		if (tipo == ES_PAGO && (this.dto.getTipo().equals(CajaPeriodo.TIPO_PAGOS)
				|| this.dto.getTipo().equals(CajaPeriodo.TIPO_PAGOS_EXTERIOR))) {
			if (this.dto.getDetalles().size() > 29) {
				Clients.showNotification("PARA PAGOS, MAXIMO 30 REGISTROS");
				return;
			}
		}

		String titulo = null;
		String tituloDet = null;
		String labelEmp = null;
		MyArray tipoMovto = null;

		switch (tipo) {

		case ES_PAGO:
			titulo = "Órden de Pago";
			tituloDet = "Facturas a Pagar";
			labelEmp = "Proveedor";
			tipoMovto = this.tipoMvtoPago;
			break;

		case ES_COBRO:
			titulo = "Recibo de Cobro";
			tituloDet = "Facturas a Cobrar";
			labelEmp = "Cliente";
			tipoMovto = this.tipoMvtoCobro;
			break;
			
		case ES_CANC_CHQ_RECHAZADO:
			titulo = "Reembolso de cheques rechazados";
			tituloDet = "Cheques a cancelar";
			labelEmp = "Cliente";
			tipoMovto = this.tipoMvtoCancelacionCheque;
			break;
			
		case ES_REEMBOLSO_PRESTAMO:
			titulo = "Reembolso de préstamo";
			tituloDet = "Préstamos a reembolsar";
			labelEmp = "Acreedor";
			tipoMovto = this.tipoMvtoReembolsoPrestamo;
			break;
			
		case ES_CANC_CHQ_RECHAZADO_PROV:
			titulo = "Reembolso de cheques rechazados";
			tituloDet = "Cheques a cancelar";
			labelEmp = "Proveedor";
			tipoMovto = this.tipoMvtoCancelacionChequeProv;
			break;
		}

		if (modo.equals(WindowPopup.NUEVO)) {
			RegisterDomain rr = RegisterDomain.getInstance();
			double tc = rr.getTipoCambioVenta();

			this.reciboDTO = new ReciboDTO();
			this.reciboDTO.setIdUsuarioCarga(this.getIdUsuario());
			this.reciboDTO.setNombreUsuarioCarga(this.getNombreUsuario());
			this.reciboDTO.setSucursal(this.dto.getCaja().getSucursal());
			this.reciboDTO.setTipoMovimiento(tipoMovto);
			this.reciboDTO.setMoneda(this.monedaLocal);
			this.reciboDTO.setTipoCambio(tc);
			if (recaudacionMRA) {
				this.reciboDTO.setCobroExterno(true);
				this.reciboDTO.setRecaudacionMra(true);
				this.reciboDTO.setAuxi(Recibo.RECAUDACION_MRA);
				this.reciboDTO.setNumero(AutoNumeroControl.getAutoNumeroKey(this.isEmpresaMRA()? "REC-CEN":"REC-MRA", 7, true));
			}
			if (this.reciboDTO.esNuevo()) {
				this.reciboDTO.setImputar(true);
			} 
			this.reciboDTO.setEstadoComprobante(this.estadoComprobanteConfeccionado);
			this.reciboDTO.setEstadosComprobantes(this.utilDto.getEstadosComprobantes());
			this.reciboDTO.setNumeroPlanilla(this.dto.getNumero());
			if (this.reciboDTO.isCancelacionChequeRechazado()
					|| this.reciboDTO.isReembolsoPrestamo()) {
				this.reciboDTO.setNumero("001-001"+ "-" +
						AutoNumeroControl.getAutoNumero(Configuracion.NRO_CANCELACION_CHEQUE_RECHAZADO, 7, true));
			}
		}
		this.reciboDTO.setTituloDetalles(tituloDet);
		this.reciboDTO.setLabelEmpresa(labelEmp);

		// si el recibo es pendiente de cobro permite editar..
		String sigla = this.reciboDTO.getEstadoComprobante().getSigla();
		if (this.isMovimientoPendiente(sigla) == true) {
			modo = WindowPopup.NUEVO;
		}

		// serializa el objeto
		byte[] ori = this.m.serializar(this.reciboDTO);

		WindowPopup w = new WindowPopup();
		w.setModo(WindowPopup.NUEVO);
		w.setTitulo(titulo);
		w.setWidth("1100px");
		w.setHigth("650px");
		w.setCheckAC(new ValidadorAgregarRecibo(this));
		w.setDato(this);
		if (modo.equals(WindowPopup.SOLO_LECTURA) && !this.isOperacionHabilitada(Permisos.MODIFICAR_RECIBOS))
			w.setSoloBotonCerrar();
		w.show(Configuracion.RECIBO_ZUL);

		if (w.isClickAceptar()) {
			if (!this.reciboDTO.esNuevo()) {
				this.reciboDTO.setMovimientoBancoActualizado(false);
			}
			this.saveRecibo();
			if (this.reciboDTO.isCobro()) {
				this.imprimirCobro();
			} else {
				this.imprimirPago();
			}
		} else {
			// si cancela la edicion restaura el objeto original..
			ReciboDTO oriRec = (ReciboDTO) this.m.deSerializar(ori);
			int pos = -1;
			for (ReciboDTO recibo : this.dto.getRecibos()) {
				if (recibo.getId().longValue() == oriRec.getId().longValue()) {
					pos = this.dto.getRecibos().indexOf(recibo);
				}
			}
			if (pos != -1) {
				this.dto.getRecibos().set(pos, oriRec);
			}
		}
	}

	/**
	 * Graba el recibo..
	 */
	private void saveRecibo() throws Exception {
		this.updateImporteRecibos(this.reciboDTO);
		if (this.reciboDTO.getTotalImporteGs() == 0) {
			this.reciboDTO.setEstadoComprobante(this.estadoComprobanteAnulado);
		}

		if (this.reciboDTO.esNuevo() == true) {
			this.dto.getRecibos().add(this.reciboDTO);
		}
		
		if (this.reciboDTO.isOrdenPagoGastosContado_()) {
			this.reciboDTO.setNumeroRecibo("GASTO CONTADO");
			this.reciboDTO.setFechaRecibo(new Date());
			this.reciboDTO.setEntregado(true);
		}
		
		if (this.reciboDTO.isAnticipoPago()) {
			this.reciboDTO.setNumeroRecibo("ANTICIPO");
			this.reciboDTO.setFechaRecibo(new Date());
			this.reciboDTO.setEntregado(true);
		}

		if (this.validarFormulario() == true) {
			RegisterDomain rr = RegisterDomain.getInstance();
			if (!this.reciboDTO.esNuevo()) {				
				for (BancoChequeTercero cheque : this.reciboDTO.getChequesEliminar()) {
					rr.deleteObject(cheque);
				}
				for (ReciboFormaPagoDTO fp : this.reciboDTO.getFormasPago()) {
					ReciboFormaPago rfp = (ReciboFormaPago) rr.getObject(ReciboFormaPago.class.getName(), fp.getId());
					if (rfp != null) {
						rfp.setOrden("1");
						rr.saveObject(rfp, rfp.getUsuarioMod());
					}					
				}				
			}	
			for (ReciboFormaPagoDTO fp : this.reciboDTO.getFormasPago()) {
				if (fp.isPagare()) {
					if (fp.getSelectedPagare() != null) {
						fp.getSelectedPagare().setPagado(true);
						fp.getSelectedPagare().setNumeroPago(this.reciboDTO.getNumero());
						rr.saveObject(fp.getSelectedPagare(), this.getLoginNombre());
					}
				}
			}
			if (this.reciboDTO.isRecaudacionMra()) {
				this.reciboDTO.setNumero(AutoNumeroControl.getAutoNumeroKey(this.isEmpresaMRA()? "REC-CEN":"REC-MRA", 7));
			}
			if (this.reciboDTO.getTarea() != null) {
				TareaProgramada t = this.reciboDTO.getTarea();
				t.setRealizado(true);
				t.setRealizadoPor(this.reciboDTO.getCobrador() + " " + Utiles.getDateToString(new Date(), "dd-MM-yyyy hh:mm:ss"));
				rr.saveObject(t, this.getLoginNombre());
			}
			
			this.asignarNumeros();

			CajaPeriodoDTO cajaDto = (CajaPeriodoDTO) this.saveDTO(this.dto);
			this.dto = cajaDto;

			this.addMovimientoBanco();
			this.setEstadoABMConsulta();
		}
	}

	/**
	 * Invoca a la API de Banco para agregar los movimientos de banco..
	 */
	private void addMovimientoBanco() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		ControlBancoMovimiento ctr = new ControlBancoMovimiento(null);

		for (ReciboDTO recibo : this.dto.getRecibos()) {
			if (!recibo.isMovimientoBancoActualizado()) {
				for (ReciboFormaPagoDTO formaPago : recibo.getFormasPago()) {
					ReciboFormaPago fp = (ReciboFormaPago) rr.getObject(ReciboFormaPago.class.getName(), formaPago.getId());
					if (!formaPago.getOrden().equals("1") && !formaPago.getTarjetaNumeroComprobante().equals("CHEQUE TERCERO")) {
						ctr.registrarMovimientoBanco(formaPago,
								recibo.getFechaEmision(), recibo.getSucursal(),
								recibo.getCliente().getId(), this.dto.getNumero(),
								recibo.getNumero(), "", recibo.getVendedorRazonSocial());
					} else if (formaPago.getTarjetaNumeroComprobante().equals("CHEQUE TERCERO")) {
						BancoChequeTercero cheque = (BancoChequeTercero) rr.getObject(BancoChequeTercero.class.getName(), Long.parseLong(formaPago.getTarjetaNumero()));
						cheque.setReciboFormaPago(fp);
						cheque.setNumeroRecibo(recibo.getNumero());
						cheque.setNumeroPlanilla(this.dto.getNumero());
						rr.saveObject(cheque, this.getLoginNombre());
					}					
				}
				recibo.setMovimientoBancoActualizado(true);
				this.saveDTO(recibo, new AssemblerRecibo());
			}
		}
	}

	/************************************************************/
	

	/************************ FACTURACION ***********************/

	private ReciboFormaPagoDTO nvoFormaPago = new ReciboFormaPagoDTO();
	private VentaDTO selectedVenta;

	private static String[] attVentas = { "fecha", "numero",
			"cliente.empresa.razonSocial", "vendedor.empresa.razonSocial",
			"condicionPago.descripcion" };

	private static String[] colVentas = { "Fecha", "Número", "Cliente",
			"Vendedor", "Condición" };

	private static String[] tipos = { Config.TIPO_DATE, Config.TIPO_STRING,
			Config.TIPO_STRING, Config.TIPO_STRING, Config.TIPO_STRING,
			Config.TIPO_STRING };

	private static String[] anchosCol = { "150px", "11%", "", "11%", "11%" };
	private String hoy = m.dateToString(new Date(), Misc.YYYY_MM_DD);

	/**
	 * Factura un pedido de venta..
	 */
	@Command
	@NotifyChange("*")
	public void facturar() throws Exception {
		this.selectedVenta = null;

		String sigla = Configuracion.SIGLA_VENTA_ESTADO_CERRADO;
		long idSuc = this.dto.getCaja().getSucursal().getId();
		String where = "c.estado.sigla like '"
				+ sigla
				+ "' and c.sucursal.id = "
				+ idSuc
				+ " and ( (c.tipoReservaReparto != null and c.repartidor != '') or (c.tipoReservaReparto = null and c.repartidor = '') )";

		BuscarElemento b = new BuscarElemento();
		b.setTitulo("Pedidos Pendientes de Facturar - Sucursal: "
				+ this.dto.getCaja().getSucursal().getText());
		b.setWidth("900px");
		b.setHeight("450px");
		b.setClase(Venta.class);
		b.setAssembler(new AssemblerVenta());
		b.setAtributos(attVentas);
		b.setNombresColumnas(colVentas);
		b.setAnchoColumnas(anchosCol);
		b.setTipos(tipos);
		b.addOrden("numero");
		b.addWhere(where);
		b.setContinuaSiHayUnElemento(false);
		// b.show(hoy);

		Map<String, String> args = new HashMap<String, String>();
		args.put("validez", this.getValidezPedidos());
		System.out.println(this.getValidezPedidos());
		this.win = (Window) Executions.createComponents(
				"/yhaguy/gestion/caja/periodo/buscarFacturas.zul",
				this.mainComponent, args);
		this.win.doModal();

		if (b.isClickAceptar() == true) {
			VentaDTO venta = (VentaDTO) b.getSelectedItemDTO();
			this.selectedVenta = venta;
			
			if ((!venta.isCondicionContado())
					&& venta.isDebitoGroupauto()) {
				Clients.showNotification("SOLO VENTAS CONTADO PARA DEBITO GROUPAUTO..", Clients.NOTIFICATION_TYPE_ERROR, null, 0, 0, 0);
				return;
			}

			if (venta.isCondicionContado() == true) {
				this.asignarFormaPago(venta);
			} else {
				this.generarFacturaVenta(venta);
			}
		}
	}
	
	@Command
	public void mostrarObservacion(@BindingParam("ref") Component ref) {
		Clients.showNotification(this.selectedVenta.getObservacion().toUpperCase(), 
				Clients.NOTIFICATION_TYPE_INFO, ref, "overlap_after", 0);
	}

	/**
	 * Genera la factura de venta..
	 */
	private void generarFacturaVenta(VentaDTO pedido) throws Exception {
		
		if (!pedido.isCondicionContado()
				&& pedido.isDebitoGroupauto()) {
			Clients.showNotification("SOLO VENTAS CONTADO PARA DEBITOS GROUPAUTO..", 
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		
		if (!this.isStockDisponible(pedido)) {
			Clients.showNotification("STOCK INSUFICIENTE..FAVOR VERIFIQUE EL PEDIDO", 
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		
		if (pedido.getTotalImporteGs() <= 0 ||
				pedido.getDetalles().size() == 0) {
			Clients.showNotification(
					"NO SE PUEDE FACTURAR EL PEDIDO CON IMPORTE 0 GS.",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		
		if (pedido.isCondicionContado()) {			
			double totalFormaPago = pedido.getTotalFormaPago();
			double totalImporte = pedido.getTotalImporteGs();
			
			if (totalFormaPago != totalImporte) {
				Clients.showNotification(
						"NO COINCIDEN EL TOTAL DE LA FACTURA Y LAS FORMAS DE PAGO.",
						Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}			
		}
		
		// verifica el saldo disponible..
		double importeVenta = pedido.getTotalImporteGs();
		double disponible = pedido.getCreditoDisponible();
		if ((!pedido.isCondicionContado()) && (importeVenta > disponible)) {
			Clients.showNotification(
					"LINEA DE CREDITO INSUFICIENTE",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
		
		if (!this.mensajeSiNo("EL NRO. DE FACTURA A GENERAR ES EL: "
				+ this.getNumeroVentaProvisorio() + " \n ES EL CORRECTO..?")) {
			return;
		}

		// generacion de los nros de factura..
		for (int i = 1; i <= pedido.getCantidadFacturas_a_generar(); i++) {
			pedido.getNumerosFacturas().add(this.getNumeroVenta());
		}

		VentaControlBody ctr = new VentaControlBody();
		List<VentaDTO> ventas = null;
		String timbrado = this.getTalonarioVentas().getTimbrado().getNumero();

		if (pedido.isCondicionContado() == true) {
			ventas = ctr.crearFacturaContadoDesdePedido(pedido, true, this.dto.getNumero(), timbrado);
		} else {
			ventas = ctr.crearFacturaCreditoDesdePedido(pedido, true, this.dto.getNumero(), timbrado);
		}

		for (VentaDTO venta : ventas) {
			MyArray facVenta = new MyArray();
			facVenta.setId(venta.getId());
			this.dto.getVentas().add(facVenta);
			
			if (venta.getFormaEntrega().equals(Venta.FORMA_ENTREGA_REPARTO)) {
				this.generarRemision(venta);
			}
			
		}
		this.dto.setVentas_a_imputar(ventas);
		this.dto = (CajaPeriodoDTO) this.saveDTO(this.dto);

		for (VentaDTO venta : ventas) {
			String key = venta.getNumeroPresupuesto().length() > 0 ? venta
					.getNumeroPresupuesto() : venta.getNumeroPedido();

			this.getCtrAgenda().addDetalle(ControlAgendaEvento.NORMAL, key, 0,
					EVENTO_FACTURACION_PEDIDO + venta.getNumeroPedido(), null);
		}

		this.updateSaldoTalonario();
		
		double total_vtas = 0;
		boolean servicio = false;
		for (VentaDTO ventaDTO : ventas) {
			if (ventaDTO.getVendedor_().toUpperCase().equals("SERVICIO")) {
				servicio = true;
			}
		}
		if (pedido.isCondicionContado()) {
			ProcesosHistoricos.updateHistoricoCobranzaMeta(total_vtas);
			ProcesosHistoricos.updateHistoricoCobranzaDiaria(new Date(), total_vtas);
		}
		ProcesosHistoricos.updateHistoricoVentaMeta(total_vtas, 0, servicio);
		ProcesosHistoricos.updateHistoricoVentaDiaria(new Date(), total_vtas, 0);
		ControlCuentaCorriente.verificarBloqueoTemporal((long) pedido.getCliente().getPos4(), pedido.getCliente().getId(), this.getLoginNombre());
		Control.updateControlTalonario(this.getLoginNombre(), ventas.size());

		for (VentaDTO venta : ventas) {
			this.addMovimientoBanco(venta);
			this.imprimirVenta(venta);
			for (SaldoVale vale : venta.getValesGenerados()) {
				this.imprimirVale(vale);
			}
		}
	}

	/**
	 * Despliega la ventana para asignar las formas de pago..
	 */
	private void asignarFormaPago(VentaDTO venta) throws Exception {
		WindowPopup wp = new WindowPopup();
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Asignar Formas de Pago");
		wp.setHigth("400px");
		wp.setWidth("600px");
		wp.setDato(this);
		wp.setCheckAC(new ValidadorFormaPagoVenta(this, venta));
		wp.show(Configuracion.VENTA_LISTA_FORMA_PAGO_ZUL);

		if (wp.isClickAceptar() == true) {
			this.generarFacturaVenta(venta);
		}
	}
	
	/**
	 * genera la nota de remision..
	 */
	private void generarRemision(VentaDTO venta) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			Venta v = (Venta) rr.getObject(Venta.class.getName(), venta.getId());
			Remision r = new Remision();
			r.setFecha(new Date());
			r.setImporteGs(venta.getTotalImporteGs());
			r.setNumero(venta.getNumero());
			r.setObservacion("AUTO GENERADO");
			r.setVenta(v);
			
			Set<RemisionDetalle> dets = new HashSet<RemisionDetalle>();
			for (VentaDetalle item : v.getDetalles()) {
				RemisionDetalle d = new RemisionDetalle();
				d.setArticulo(item.getArticulo());
				d.setCantidad(Integer.parseInt(item.getCantidad() + ""));
				dets.add(d);
			}
			r.getDetalles().addAll(dets);
			rr.saveObject(r, this.getLoginNombre());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * verifica existencia de stock..
	 */
	private boolean isStockDisponible(VentaDTO pedido) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		long idDeposito = pedido.getDeposito().getId();
		for (VentaDetalleDTO item : pedido.getDetalles()) {
			Articulo art = rr.getArticuloById(item.getArticulo().getId());
			if (!art.getFamilia().getDescripcion().equals(ArticuloFamilia.CONTABILIDAD)
					&& !art.getFamilia().getDescripcion().equals(ArticuloFamilia.SERVICIOS)
					&& !art.getFamilia().getDescripcion().equals(ArticuloFamilia.MERCADERIAS_USADAS)) {
				long stock = rr.getStockDisponible(item.getArticulo().getId(), idDeposito);
				if (stock < item.getCantidad()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Despliega la ventana de consulta de la venta seleccionada..
	 */
	private void showFormVenta() throws Exception {
		String titulo = this.selectedVenta.getTipoMovimiento().getPos1() + "";

		WindowPopup wp = new WindowPopup();
		wp.setModo(WindowPopup.SOLO_LECTURA);
		wp.setDato(this);
		wp.setWidth("400px");
		wp.setHigth("400px");
		wp.setTitulo(titulo);
		wp.setSoloBotonCerrar();
		wp.show(Configuracion.CAJA_VENTA_ZUL);
	}

	/**
	 * actualiza el saldo del talonario..
	 */
	private void updateSaldoTalonario() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.updateSaldoTalonario(this.dto.getCaja().getTalonarioVentas().getId());
	}
	
	/**
	 * @return el talonario de ventas..
	 */
	private Talonario getTalonarioVentas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return (Talonario) rr.getObject(Talonario.class.getName(), 
				this.dto.getCaja().getTalonarioVentas().getId());
	}
	
	/**
	 * @return el talonario de notas de credito..
	 */
	private Talonario getTalonarioNotasCredito() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return (Talonario) rr.getObject(Talonario.class.getName(), 
				this.dto.getCaja().getTalonarioNotasCredito().getId());
	}

	/**
	 * Invoca a la API de Banco para agregar los movimientos de banco..
	 */
	private void addMovimientoBanco(VentaDTO venta) throws Exception {
		ControlBancoMovimiento ctr = new ControlBancoMovimiento(null);

		for (ReciboFormaPagoDTO formaPago : venta.getFormasPago()) {
			ctr.registrarMovimientoBanco(formaPago, venta.getFecha(), venta
					.getSucursal(), venta.getCliente().getId(), this.dto
					.getNumero(), "", venta.getNumero(), (String) venta.getVendedor().getPos1());
		}
	}

	/************************************************************/

	
	/************** DEVOLUCION CON NOTA DE CREDITO **************/

	static String[] attNC = { "fechaEmision", "numero",
			"cliente.empresa.razonSocial", "motivo.descripcion",
			"estadoComprobante.descripcion" };

	static String[] tiposNC = { Config.TIPO_DATE, Config.TIPO_STRING,
			Config.TIPO_STRING, Config.TIPO_STRING, Config.TIPO_STRING };

	static String[] colsNC = { "Fecha Emisión", "Número", "Cliente", "Motivo",
			"Estado" };

	static String[] anchosColNC = { "160px", "110px", "", "110px", "110px" };

	private NotaCreditoDTO selectedNotaCredito = new NotaCreditoDTO();

	@Command 
	@NotifyChange("*")
	public void devolucionConNotaDeCredito() throws Exception {

		String sigla = Configuracion.SIGLA_ESTADO_COMPROBANTE_APROBADO;
		long idSuc = this.dto.getCaja().getSucursal().getId();
		String where = "estadoComprobante.sigla = '" + sigla
				+ "' and sucursal.id = " + idSuc;

		BuscarElemento b = new BuscarElemento();
		b.setTitulo("Solicitudes de Nota de Crédito Aprobadas - Sucursal: "
				+ this.dto.getCaja().getSucursal().getText());
		b.setWidth("960px");
		b.setHeight("430px");
		b.setClase(NotaCredito.class);
		b.setAssembler(new NotaCreditoAssembler());
		b.addWhere(where);
		b.addOrden("numero");
		b.setAtributos(attNC);
		b.setTipos(tiposNC);
		b.setNombresColumnas(colsNC);
		b.setAnchoColumnas(anchosColNC);
		b.setContinuaSiHayUnElemento(false);
		b.show(hoy);

		if (b.isClickAceptar()) {

			NotaCreditoDTO nc = (NotaCreditoDTO) b.getSelectedItemDTO();
			
			if (!this.validarNotaCredito(nc)) {
				return;
			}
			
			this.selectedNotaCredito = nc;

			nc.setNumeroNotaCredito(this.getNumeroNotaCredito());
			nc.setTimbrado_(this.getTalonarioNotasCredito().getTimbrado().getNumero());
			nc.setCajaNro(this.dto.getCaja().getNumero());
			nc.setPlanillaCajaNro(this.dto.getNumero());
			nc.setCajero((String) this.dto.getResponsable().getPos1());
			this.selectedNotaCredito.setNumeroNotaCredito(nc.getNumeroNotaCredito());
			this.selectedNotaCredito.setNumero(nc.getNumeroNotaCredito());

			NotaCreditoControlBody ctr = new NotaCreditoControlBody();
			NotaCreditoDTO notaCred = ctr.crearNotaCreditoVentaDesde(nc, getDtoUtil().getEstadoComprobanteCerrado());

			MyArray notaCredito = new MyArray();
			notaCredito.setId(notaCred.getId());
			this.dto.getNotasCredito().add(notaCredito);
			this.dto.setNotaCredito_a_imputar(notaCred);
			
			if (notaCred.isMotivoDevolucion()) {
				ControlArticuloCostoPromedio ctprom = new ControlArticuloCostoPromedio();
				ctprom.addCostoPromedioNotaCredito(notaCred.getId());
			}			
			
			// actualiza el historico venta / meta..
			double tot_nc = notaCred.getTotalImporteGsSinIva();
			ProcesosHistoricos.updateHistoricoVentaMeta(0, tot_nc, false);
			ProcesosHistoricos.updateHistoricoVentaDiaria(new Date(), 0, tot_nc);
			if (notaCred.isNotaCreditoVentaContado()) {
				ProcesosHistoricos.updateHistoricoCobranzaMeta(tot_nc * -1);
				ProcesosHistoricos.updateHistoricoCobranzaDiaria(new Date(), tot_nc * -1);
			}

			this.dto = (CajaPeriodoDTO) this.saveDTO(this.dto);
			
			if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
				this.imprimirNotaCredito_(this.selectedNotaCredito);
			} else {
				this.imprimirNotaCredito_(this.selectedNotaCredito);
			}
		}
	}
	
	/**
	 * @return true si es una nota de credito valida..
	 */
	private boolean validarNotaCredito(NotaCreditoDTO nc) throws Exception {
		MyArray vta = nc.getVentaAplicada();
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta v = (Venta) rr.getObject(Venta.class.getName(), vta.getId());
		long idCliente = nc.getCliente().getId().longValue(); 
		long idClienteVta = v.getCliente().getId().longValue();
		if (idCliente != idClienteVta) {
			Clients.showNotification("La venta aplicada no corresponde al cliente..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return false;
		}
		return true;
	}

	/**
	 * Despliega la ventana de consulta de la nota de Cred. seleccionada..
	 */
	private void showFormNotaCredito() throws Exception {
		String titulo = this.selectedNotaCredito.getTipoMovimiento().getPos1() + "";

		WindowPopup wp = new WindowPopup();
		wp.setModo(WindowPopup.SOLO_LECTURA);
		wp.setDato(this);
		wp.setWidth("400px");
		wp.setHigth("400px");
		wp.setTitulo(titulo);
		wp.setSoloBotonCerrar();
		wp.show(Configuracion.CAJA_NOTA_CREDITO_ZUL);
	}

	/************************************************************/

	/********************** AGREGAR EGRESO **********************/

	@Command
	@NotifyChange("*")
	public void abrirVentanaReposicion(@BindingParam("tipo") int tipo)
			throws Exception {
		this.abrirVentanaReposicion(tipo, WindowPopup.NUEVO);
	};	

	/**
	 * Despliega el popup del item de reposicion..
	 */
	private void abrirVentanaReposicion(int tipo, String modo) throws Exception {

		if (modo.equals(WindowPopup.NUEVO)) {

			double tc = utilDto.getCambioCompraBCP(this.monedaLocal);
			this.selectedItem = null;
			this.nvoFormaPago = new ReciboFormaPagoDTO();
			this.nvoFormaPago.setTipo(this.getDtoUtil().getFormaPagoEfectivo());

			this.reposicion = new MyArray();
			this.reposicion.setPos1(tipo == ES_REPOSICION ? this.getLoginFuncionario().toMyPair().getText() : "");
			this.reposicion.setPos2(false);
			this.reposicion.setPos3(new Date());
			this.reposicion.setPos4(tc);
			this.reposicion.setPos5((double) 0);
			this.reposicion.setPos6((double) 0);
			this.reposicion.setPos8((tipo == ES_REPOSICION) ? this.cajaReposicionIngreso
							: this.cajaReposicionEgreso);
			this.reposicion.setPos9(this.monedaLocal_);
			this.reposicion.setPos10(new MyPair());
			this.reposicion.setPos11(this.estadoComprobanteConfeccionado);
			this.reposicion.setPos12("");
			this.reposicion.setPos13(tipo == ES_REPOSICION ? this
					.getLoginFuncionario().toMyPair() : new MyPair());
			this.reposicion.setPos14(this.nvoFormaPago);
		}
		this.reposicion.setPos16((tipo == ES_REPOSICION) ? true : false);
		this.reposicion.setPos17("");
		this.reposicion.setPos18(false);

		String titulo = (tipo == ES_REPOSICION) ? "Ingreso de Caja" : "Egreso de Caja";

		WindowPopup w = new WindowPopup();
		w.setModo(modo);
		w.setDato(this);
		w.setTitulo(titulo);
		w.setWidth("450px");
		w.setHigth((tipo == ES_REPOSICION) ? "400px" : "400px");
		w.setCheckAC(new ValidadorEgreso(this.reposicion));
		if (modo.equals(WindowPopup.SOLO_LECTURA))
			w.setSoloBotonCerrar();
		w.show(Configuracion.CAJA_REPOSICION_ZUL);
		if (w.isClickAceptar()) {
			MyPair tipoRepEgreso = (MyPair) this.reposicion.getPos10();
			long idRepEgreso = tipoRepEgreso.getId().longValue();
			long idRepSinComp = this.egresoSinComp.getId().longValue();
			if ((idRepEgreso == idRepSinComp)) {
				this.reposicion.setPos11(this.estadoComprobantePendiente);
			}
			this.addReposicion(tipo);
		}
	}
	
	/**
	 * Despliega el popup del item de reposicion..
	 */
	@Command
	@NotifyChange("*")
	public void abrirVentanaSaldoCajaChica() throws Exception {
		
		int tipo = ES_REPOSICION; String modo = WindowPopup.NUEVO;

		if (modo.equals(WindowPopup.NUEVO)) {

			double tc = utilDto.getCambioCompraBCP(this.monedaLocal);
			this.selectedItem = null;
			this.nvoFormaPago = new ReciboFormaPagoDTO();
			this.nvoFormaPago.setTipo(this.getDtoUtil().getFormaPagoEfectivo());

			this.reposicion = new MyArray();
			this.reposicion.setPos1(tipo == ES_REPOSICION ? this.getLoginFuncionario().toMyPair().getText() : "");
			this.reposicion.setPos2(false);
			this.reposicion.setPos3(new Date());
			this.reposicion.setPos4(tc);
			this.reposicion.setPos5((double) 0);
			this.reposicion.setPos6((double) 0);
			this.reposicion.setPos7("OBSERVACION SALDO CAJA ANTERIOR");
			this.reposicion.setPos8((tipo == ES_REPOSICION) ? this.cajaReposicionIngreso
							: this.cajaReposicionEgreso);
			this.reposicion.setPos9(this.monedaLocal_);
			this.reposicion.setPos10(new MyPair());
			this.reposicion.setPos11(this.estadoComprobanteConfeccionado);
			this.reposicion.setPos12("");
			this.reposicion.setPos13(tipo == ES_REPOSICION ? this
					.getLoginFuncionario().toMyPair() : new MyPair());
			this.reposicion.setPos14(this.nvoFormaPago);
		}
		this.reposicion.setPos16((tipo == ES_REPOSICION) ? true : false);
		this.reposicion.setPos17("");
		this.reposicion.setPos18(true);

		String titulo = "SALDO CAJA ANTERIOR";

		WindowPopup w = new WindowPopup();
		w.setModo(modo);
		w.setDato(this);
		w.setTitulo(titulo);
		w.setWidth("450px");
		w.setHigth((tipo == ES_REPOSICION) ? "430px" : "430px");
		//w.setCheckAC(new ValidadorEgreso(this.reposicion));
		if (modo.equals(WindowPopup.SOLO_LECTURA))
			w.setSoloBotonCerrar();
		w.show(Configuracion.CAJA_REPOSICION_ZUL);
		if (w.isClickAceptar()) {
			MyPair tipoRepEgreso = (MyPair) this.reposicion.getPos10();
			long idRepEgreso = tipoRepEgreso.getId().longValue();
			long idRepSinComp = this.egresoSinComp.getId().longValue();
			if ((idRepEgreso == idRepSinComp)) {
				this.reposicion.setPos11(this.estadoComprobantePendiente);
			}
			
			if (this.selectedCajaChica != null) {
				RegisterDomain rr = RegisterDomain.getInstance();
				CajaPeriodo cp = (CajaPeriodo) rr.getObject(CajaPeriodo.class.getName(), (long) this.selectedCajaChica[0]);
				cp.setSaldoCajaChicaAplicado(this.dto.getNumero());
				rr.saveObject(cp, this.getLoginNombre());
			}
			this.selectedCajaChica = null;			
			this.addReposicion(tipo);
		}
	}

	/**
	 * Agrega la reposicion dentro de la caja..
	 */
	private void addReposicion(int tipo) throws Exception {

		ReciboFormaPagoDTO formaPago = (ReciboFormaPagoDTO) this.reposicion.getPos14();

		formaPago.setMoneda((MyPair) this.reposicion.getPos9());
		formaPago.setMontoGs((double) this.reposicion.getPos5());
		formaPago.setMontoDs((double) this.reposicion.getPos6());

		formaPago = (ReciboFormaPagoDTO) this.saveDTO(formaPago, new AssemblerReciboFormaPago(""));

		String key = tipo == ES_REPOSICION ? Configuracion.NRO_CAJA_REPOSICION : Configuracion.NRO_CAJA_EGRESO;

		this.reposicion.setPos14(formaPago);
		this.reposicion.setPos15(key + "-" + AutoNumeroControl.getAutoNumero(key, 5));
		this.dto.getReposiciones().add(this.reposicion);
		this.dto = (CajaPeriodoDTO) this.saveDTO(dto);
		
		this.addMovimientoBanco(formaPago, 0);
		this.imprimirReposicion();
	}
	
	/**
	 * Invoca a la API de Banco para agregar los movimientos de banco..
	 */
	private void addMovimientoBanco(ReciboFormaPagoDTO formaPago, long idCliente)
			throws Exception {
		MyPair sucursal = this.dto.getCaja().getSucursal();
		Date fecha = new Date();
		ControlBancoMovimiento ctr = new ControlBancoMovimiento(null);
		ctr.registrarMovimientoBanco(formaPago, fecha, sucursal, idCliente, "", "", "", "");
	}

	/************************************************************/

	/*********************** AGREGAR GASTO **********************/

	private GastoDTO dtoGasto = new GastoDTO();

	@Command
	@NotifyChange("*")
	public void showFormGastos() throws Exception {
		this.showFormGastos(WindowPopup.NUEVO);
	}

	/**
	 * despliega la ventana de gastos..
	 */
	public void showFormGastos(String modo) throws Exception {

		MyArray moneda = this.getDtoUtil().getMonedaGuaraniConSimbolo();
		double tipoCambio = 0.0;

		if (modo.equals(WindowPopup.NUEVO)) {
			this.dtoGasto = new GastoDTO();
			this.dtoGasto.setCajaPagoNumero(this.dto.getNumero());
			this.dtoGasto.setTipoCambio(tipoCambio);
			this.dtoGasto.setMoneda(moneda);
			this.dtoGasto.setSucursal(this.dto.getCaja().getSucursal());
			this.dtoGasto.setCondicionPago(this.condicionContado);
			this.dtoGasto.setTipoMovimiento(this.tipoMvtoGastoContado);
			this.dtoGasto.setVencimiento(new Date());
			this.dtoGasto.setEstadoComprobante(this.estadoComprobanteConfeccionado);
			this.dtoGasto.setTalonarioAutoFactura(this.dto.getCaja().getTalonarioAutoFacturas());
		}
		this.dtoGasto.setFondoFijo(true);

		WindowPopup wp = new WindowPopup();
		wp.setModo(modo);
		wp.setDato(this.dtoGasto);
		wp.setWidth("990px");
		wp.setHigth("90%");
		wp.setTitulo(Configuracion.TITULO_FORMULARIO_GASTOS);
		wp.setCheckAC(new ValidadorGasto(this));
		if (modo.equals(WindowPopup.SOLO_LECTURA))
			wp.setSoloBotonCerrar();
		wp.show(Configuracion.GASTOS_FACTURA_ZUL);

		if (wp.isClickAceptar()) {
			this.addGasto();
		}
	}

	/**
	 * Agrega y guarda el gasto..
	 */
	private void addGasto() throws Exception {
		if (!this.dtoGasto.isMonedaLocal()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			double tc = rr.getTipoCambioVenta(this.dtoGasto.getFecha(), -1);
			this.dtoGasto.setTipoCambio(tc);
		}
		this.dto.getGastos().add(this.dtoGasto);
		if (this.dtoGasto.isAutoFactura())
			this.dtoGasto.setNumeroFactura(this.getNumeroAutoFactura());
		this.dto = (CajaPeriodoDTO) this.saveDTO(dto);
	}

	/************************************************************/

	public List<MyPair> getCajasPeriodoActivas() {
		MyPair caja1 = new MyPair(1, "Caja 1 - Mosrador - Contado");
		MyPair caja2 = new MyPair(2, "Caja 2 - Credito - Jose");
		MyPair caja3 = new MyPair(3, "Caja 3 - Mayoristas - Maria");
		ArrayList<MyPair> lista = new ArrayList<MyPair>();
		lista.add(caja1);
		lista.add(caja2);
		lista.add(caja3);
		return lista;
	}

	/************************ VER ITEMS *************************/

	public static final String ITEM_PARAM = "item";
	public static final String ITEM_REPOSICION = "reposicion";

	@Command
	@NotifyChange("*")
	public void verItem(@BindingParam(ITEM_PARAM) MyArray item)
			throws Exception {

		int tipoItem = (int) item.getPos1();
		String modo = WindowPopup.SOLO_LECTURA;

		switch (tipoItem) {

		case ES_REPOSICION:
			this.abrirVentanaReposicion(ES_REPOSICION, modo);
			break;

		case ES_EGRESO:
			this.abrirVentanaReposicion(ES_EGRESO, modo);
			break;

		case ES_COBRO:
			this.abrirVentanaRecibo(ES_COBRO, modo, false);
			break;

		case ES_PAGO:
			this.abrirVentanaRecibo(ES_PAGO, modo, false);
			break;

		case ES_GASTO:
			this.showFormGastos(modo);
			break;

		case ES_VENTA_CONTADO:
			this.showFormVenta();
			break;

		case ES_VENTA_CREDITO:
			this.showFormVenta();
			break;

		case ES_NOTA_CREDITO_VENTA:
			this.showFormNotaCredito();
			break;
		}
	}

	/************************************************************/
	

	/***************** ANULACION DE MOVIMIENTOS *****************/

	/**
	 * Anulacion de los movimientos de caja..
	 */
	@Command
	@NotifyChange("*")
	public void anularMovimiento() throws Exception {
		String motivo = this.getMotivoAnulacion();

		if (motivo.trim().length() <= 0)
			return;

		int tipo = (int) this.selectedItem.getPos1();

		switch (tipo) {

		case ES_REPOSICION:
			this.anularReposicion(motivo);
			break;

		case ES_EGRESO:
			this.anularEgreso(motivo);
			break;

		case ES_COBRO:
			this.anularCobro(motivo);
			break;

		case ES_PAGO:
			this.anularPago(motivo);
			break;

		case ES_GASTO:
			this.anularGasto(motivo);
			break;

		case ES_VENTA_CONTADO:
			this.anularVentaContado(motivo);
			break;

		case ES_VENTA_CREDITO:
			this.anularVentaCredito(motivo);
			break;

		case ES_NOTA_CREDITO_VENTA:
			this.anularNotaCredito(motivo);
			break;
		}

		String clase = CajaPeriodo.class.getName();
		this.dto = (CajaPeriodoDTO) this.getDTOById(clase, this.dto.getId());
		this.mensajePopupTemporal("Movimiento Anulado..");
	}

	/**
	 * Anulacion del movimiento de reposicion..
	 */
	private void anularReposicion(String motivo) throws Exception {
		ControlAnulacionMovimientos.anularReposicionCaja(
				this.reposicion.getId(), motivo, this.getLoginNombre());
	}

	/**
	 * Anulacion del movimiento de egreso..
	 */
	private void anularEgreso(String motivo) throws Exception {
		ControlAnulacionMovimientos.anularEgresoCaja(this.reposicion.getId(),
				motivo, this.getLoginNombre());
	}

	/**
	 * Anulacion del movimiento de cobro..
	 */
	private void anularCobro(String motivo) throws Exception {
		ControlAnulacionMovimientos.anularCobro(this.reciboDTO.getId(), motivo,
				this.getLoginNombre());
	}

	/**
	 * Anulacion del movimiento de pago..
	 */
	private void anularPago(String motivo) throws Exception {
		ControlAnulacionMovimientos.anularPago(this.reciboDTO.getId(), motivo,
				this.getLoginNombre());
	}

	/**
	 * Anulacion del movimiento de gasto..
	 */
	private void anularGasto(String motivo) throws Exception {
		ControlAnulacionMovimientos.anularGastoCajaChica(this.dtoGasto.getId(),
				motivo, this.getLoginNombre());
	}

	/**
	 * Anulacion del movimiento de venta..
	 */
	private void anularVentaContado(String motivo) throws Exception {
		ControlAnulacionMovimientos.anularVentaContado(
				this.selectedVenta.getId(), motivo, this.getLoginNombre());
	}

	/**
	 * Anulacion de la venta credito..
	 */
	private void anularVentaCredito(String motivo) throws Exception {
		ControlAnulacionMovimientos.anularVentaCredito(
				this.selectedVenta.getId(), motivo, this.getLoginNombre());
	}

	/**
	 * Anulacion del movimiento de venta..
	 */
	private void anularNotaCredito(String motivo) throws Exception {
		this.selectedNotaCredito
				.setEstadoComprobante(this.estadoComprobanteAnulado);
		this.selectedNotaCredito.setObservacion(motivo);
		Assembler assembler = new NotaCreditoAssembler();
		this.saveDTO(this.selectedNotaCredito, assembler);
		this.dto = (CajaPeriodoDTO) this.saveDTO(this.dto);
		this.mensajePopupTemporal("Movimiento Anulado..");
	}

	/************************************************************/

	/******************** ITEM SELECCIONADO *********************/

	/**
	 * Asocia el item generico de la caja a su correspondiente..
	 */
	@Command
	public void selectItem() {
		long idItem = this.selectedItem.getId();
		int tipo = (int) this.selectedItem.getPos1();

		switch (tipo) {

		case ES_REPOSICION:
			for (MyArray reposicion : this.dto.getReposiciones()) {
				if (reposicion.getId().longValue() == idItem) {
					this.reposicion = reposicion;
				}
			}
			break;

		case ES_EGRESO:
			for (MyArray egreso : this.dto.getReposiciones()) {
				if (egreso.getId().longValue() == idItem) {
					this.reposicion = egreso;
				}
			}
			break;

		case ES_COBRO:
			for (ReciboDTO cobro : this.dto.getRecibos()) {
				if (cobro.getId().longValue() == idItem) {
					this.reciboDTO = cobro;
				}
			}
			break;

		case ES_PAGO:
			for (ReciboDTO pago : this.dto.getRecibos()) {
				if (pago.getId().longValue() == idItem) {
					this.reciboDTO = pago;
				}
			}
			break;

		case ES_GASTO:
			for (GastoDTO gasto : this.dto.getGastos()) {
				if (gasto.getId().longValue() == idItem) {
					this.dtoGasto = gasto;
				}
			}
			break;

		case ES_VENTA_CONTADO:
			try {
				String className = Venta.class.getName();
				Assembler assembler = new AssemblerVenta();
				this.selectedVenta = (VentaDTO) this.getDTOById(className,
						idItem, assembler);

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case ES_VENTA_CREDITO:
			try {
				String className = Venta.class.getName();
				Assembler assembler = new AssemblerVenta();
				this.selectedVenta = (VentaDTO) this.getDTOById(className,
						idItem, assembler);

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case ES_NOTA_CREDITO_VENTA:
			try {
				String className = NotaCredito.class.getName();
				Assembler assembler = new NotaCreditoAssembler();
				this.selectedNotaCredito = (NotaCreditoDTO) this.getDTOById(
						className, idItem, assembler);

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	/************************************************************/
	

	/********************** CIERRE DE CAJA **********************/

	private MyArray arqueo;

	@Command
	@NotifyChange("*")
	public void cerrarCaja() throws Exception {
		this.arqueoDeCaja();
	}

	/**
	 * Despliega la ventana para el arqueo de caja..
	 */
	private void arqueoDeCaja() throws Exception {

		this.arqueo = new MyArray();
		this.arqueo.setPos1(0.0);
		this.arqueo.setPos2(0.0);
		this.arqueo.setPos3(0.0);

		WindowPopup wp = new WindowPopup();
		// wp.setCheckAC(new ValidadorArqueoCaja(this));
		wp.setDato(this);
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Arqueo de Caja");
		wp.setHigth("280px");
		wp.setWidth("400px");
		wp.show(Configuracion.ARQUEO_CAJA_ZUL);
		if (wp.isClickAceptar() == true) {
			this.cierreDeCaja();
		}
	}

	/**
	 * Cierra la caja..
	 */
	private void cierreDeCaja() throws Exception {

		this.dto.setArqueo(this.arqueo);
		this.dto.setCierre(new Date());
		this.dto.setReadonly();
		this.dto.setEstado(estadoCajaCerrada);
		
		if (this.dto.getTipo().contains(CajaPeriodo.TIPO_CHICA)) {
			this.setSaldoCajaChica();
			if (this.dto.getSaldoCajaChica() < 0) {
				Clients.showNotification("NO SE PERMITEN SALDOS NEGATIVOS", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
		}
		
		if (this.dto.getTipo().equals(CajaPeriodo.TIPO_VENTA) 
				|| this.dto.getTipo().equals(CajaPeriodo.TIPO_COBROS)
						|| this.dto.getTipo().equals(CajaPeriodo.TIPO_COBROS_MOBILE)
						|| this.dto.getTipo().equals(CajaPeriodo.TIPO_COBROS_MOBILE_INT)) {
			this.setSaldoCaja();
		}
		
		this.dto = (CajaPeriodoDTO) this.saveDTO(this.dto);
		this.mensajePopupTemporal("Caja correctamente cerrada..");
	}
	
	/**
	 * setea el saldo de caja chica..
	 */
	private void setSaldoCajaChica() throws Exception {
		double totalEgresos = 0;
		double totalNotaCreditoCompra = 0;
		double totalRepEgresos = 0;
		double totalRepEgresosDtoViatico = 0;
		double totalReposiciones = 0;
		double totalRetencionProveedor = 0;

		RegisterDomain rr = RegisterDomain.getInstance();
		CajaPeriodo planilla = (CajaPeriodo) rr.getObject(CajaPeriodo.class.getName(), this.dto.getId());

		for (CompraLocalFactura compra : planilla.getCompras()) {
			double importe = compra.getImporteGs();
			totalEgresos += importe;
		}

		for (Gasto gasto : planilla.getGastosOrdenado()) {
			double importe = gasto.isAnulado() ? 0.0 : gasto.getImporteGs();
			totalEgresos += importe;
		}

		for (Recibo pago : planilla.getRecibosOrdenado()) {
			if (pago.isPago()) {
				double importe = pago.isAnulado() ? 0.0 : pago.getTotalImporteGs();
				totalEgresos += importe;
			}
		}

		for (CajaReposicion rep : planilla.getReposiciones()) {
			if (!rep.isIngreso()) {
				totalRepEgresos += rep.getMontoGs();
				if (rep.getTipoEgreso().getSigla().equals(Configuracion.SIGLA_CAJA_REPOSICION_EGRESO_EXCEDENTE)) {
					totalRepEgresosDtoViatico += rep.getMontoGs();
				}
			}
		}

		for (NotaCredito nc : planilla.getNotasCredito()) {
			if (nc.isNotaCreditoCompra()) {
				double importe = nc.isAnulado() ? 0.0 : nc.getImporteGs();
				totalNotaCreditoCompra += importe;
			}
		}

		for (CajaReposicion rep : planilla.getReposiciones()) {
			if (rep.isIngreso()) {
				double importe = rep.isAnulado() ? 0.0 : rep.getMontoGs();
				totalReposiciones += importe;
			}
		}

		for (ReciboFormaPago fp : planilla.getRetencionesProveedor()) {
			totalRetencionProveedor += fp.getMontoGs();
		}

		double saldo = (totalReposiciones + totalRetencionProveedor)
				- ((totalEgresos - totalRepEgresosDtoViatico - totalNotaCreditoCompra)
						+ (totalRepEgresos - totalRepEgresosDtoViatico));
		this.dto.setSaldoCajaChica(saldo);
		this.dto.setSaldoCajaChicaAplicado(null);
	}
	
	/**
	 * setea el saldo de caja..
	 */
	private void setSaldoCaja() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CajaPeriodo planilla = (CajaPeriodo) rr.getObject(CajaPeriodo.class.getName(), this.dto.getId());
		this.dto.setSaldoCajaChica(planilla.getSaldoCaja());
		this.dto.setSaldoCajaChicaAplicado(null);		
	}

	/**
	 * Graba en la agenda el intento fallido de arqueo..
	 */
	public void intentoFallidoArqueo() throws Exception {
		this.getCtrAgenda().addDetalle(this.getCtrAgendaTipo(),
				this.getCtrAgendaKey(), 0, "Intento Fallido de Arqueo", null);
	}

	/************************************************************/

	/************************** UTILES **************************/

	private UtilDTO utilDto = this.getDtoUtil();
	private MyArray tipoMvtoPago = utilDto.getTmReciboPago();
	private MyArray tipoMvtoCobro = utilDto.getTmReciboCobro();
	private MyArray tipoMvtoCancelacionCheque = utilDto.getTmCancelacionChequeRechazado();
	private MyArray tipoMvtoCancelacionChequeProv = utilDto.getTmCancelacionChequeRechazadoProv();
	private MyArray tipoMvtoReembolsoPrestamo = utilDto.getTmReembolsoPrestamo();
	private MyArray tipoMvtoGastoContado = utilDto.getTmFacturaGastoContado();
	private MyArray monedaLocal = utilDto.getMonedaGuaraniConSimbolo();
	private MyPair monedaLocal_ = utilDto.getMonedaGuarani();
	private MyArray condicionContado = utilDto.getCondicionPagoContado();
	private MyPair cajaReposicionEgreso = utilDto.getCajaReposicion_Egreso();
	private MyPair cajaReposicionIngreso = utilDto.getCajaReposicion_Ingreso();
	private MyPair egresoSinComp = utilDto
			.getCajaReposicionEgreso_SinComprobante();
	private MyPair estadoComprobantePendiente = utilDto
			.getEstadoComprobantePendiente();
	private MyPair estadoComprobanteAnulado = utilDto
			.getEstadoComprobanteAnulado();
	private MyPair estadoComprobanteConfeccionado = utilDto
			.getEstadoComprobanteConfeccionado();
	private MyPair estadoCajaCerrada = utilDto.getCajaPeriodoEstadoCerrada();

	public double getSaldoCajaPagoPeriodoGs() {
		Double out = (double) 0;
		for (MyArray d : this.dto.getDetalles()) {
			int tipo = (int) d.getPos1();

			MyPair estadoComprobante = (MyPair) d.getPos13();
			long idEstadoComprobante = estadoComprobante.getId().longValue();
			long idEstadoComprobanteAnulado = this.estadoComprobanteAnulado
					.getId().longValue();

			if ((idEstadoComprobante != idEstadoComprobanteAnulado)
					&& (tipo != ES_VENTA_CREDITO)
					&& (tipo != ES_NOTA_CREDITO_VENTA)) {
				out += (Double) d.getPos5();
			}

			if (tipo == ES_NOTA_CREDITO_VENTA) {
				int signo = -1;
				out += (this.getImporteNotaCredito(d.getId()) * signo);
			}
		}
		return out;
	}

	/**
	 * @return datos de la lista movimientos a pagar [0]:total facturas en
	 *         moneda local [1]:total facturas en moneda extranjera [2]:total
	 *         formaPago en moneda local [3]:total formaPago en moneda
	 *         extranjera
	 */
	public Object[] getDatosMovimientosAplicados() {
		double totalGs = 0;
		double totalDs = 0;
		double totalFormaPagoGs = 0;
		double totalFormaPagoDs = 0;

		for (ReciboDetalleDTO opd : this.reciboDTO.getDetalles()) {
			totalGs += opd.getMontoGs();
			totalDs += opd.getMontoDs();
		}

		for (ReciboFormaPagoDTO opfp : this.reciboDTO.getFormasPago()) {
			totalFormaPagoGs += opfp.getMontoGs();
			totalFormaPagoDs += opfp.getMontoDs();		
		}

		return new Object[] { totalGs, totalDs, totalFormaPagoGs,
				totalFormaPagoDs };
	}

	/**
	 * Efectua una reposicion en la caja..
	 */
	@Command
	public void agregarReposicion() throws Exception {

		WindowPopup wp = new WindowPopup();
		wp.setTitulo("Reposición de Caja");
		wp.setDato(this);
		wp.setHigth("400px");
		wp.setWidth("400px");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(Configuracion.CAJA_REPOSICION_ZUL);

		if (wp.isClickAceptar()) {
		}
	}

	private long getIdUsuario() {
		return this.getUs().getId();
	}

	private String getNombreUsuario() {
		return this.getUs().getNombre();
	}

	public boolean isCobro() {
		return this.reciboDTO.isCobro();
	}
	
	public boolean isCancelacionCheque() {
		return this.reciboDTO.isCancelacionChequeRechazado();
	}
	
	public boolean isReembolsoPrestamo() {
		return this.reciboDTO.isReembolsoPrestamo();
	}
	
	public boolean isCancelacionChequeProv() {
		return this.reciboDTO.isCancelacionChequeRechazadoProv();
	}

	/**
	 * Despliega el Reporte de Reposicion..
	 */
	private void imprimirReposicion() throws Exception {
		if (!(this.reposicion.getPos10() instanceof MyPair))
			this.reposicion.setPos10(new MyPair());
		String source = ReportesViewModel.SOURCE_REPOSICION;
		MyPair tipoEgreso = (MyPair) this.reposicion.getPos10();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", tipoEgreso.esNuevo() ? "Reposición de Caja" : tipoEgreso.getText());
		if ((boolean) this.reposicion.getPos18()) {
			params.put("title", "APLICACIÓN DE SALDO ANTERIOR");
		}
		params.put("ImporteEnLetra", this.m.numberToLetter(this.reposicion.getPos5()));
		params.put("Usuario", this.getUs().getNombre());
		params.put("NroRecibo", this.reposicion.getPos5());
		JRDataSource dataSource = new CajaReposicionDataSource();
		this.imprimirComprobante(source, params, dataSource);
	}

	/**
	 * Despliega el Reporte de Cobro..
	 */
	private void imprimirCobro() throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("RazonSocial", this.reciboDTO.getRazonSocial());
		params.put("Ruc", this.reciboDTO.getRuc());
		params.put("Numero", this.reciboDTO.getNumero());
		params.put("ImporteEnLetras", this.reciboDTO.isMonedaLocal() ? this.reciboDTO.getImporteEnLetras() : this.reciboDTO.getImporteEnLetrasDs());
		params.put("ImporteTotal", this.reciboDTO.isMonedaLocal() ? FORMATTER.format(this.reciboDTO.getTotalImporteGs()) : FORMATTER_DS.format(this.reciboDTO.getTotalImporteDs()));
		params.put("Usuario", this.getUs().getNombre());
		this.win = (Window) Executions.createComponents(ZUL_IMPRESION_RECIBO, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * Despliega el Reporte de Pago..
	 */
	private void imprimirPago() throws Exception {
		String source = ReportesViewModel.SOURCE_RECIBO;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new ReciboDataSource();
		params.put("title", this.reciboDTO.getTipoMovimiento().getPos1());
		params.put("fieldRazonSocial", this.reciboDTO.isCobro() ? "Recibí de:" : "A la Orden de:");
		params.put("RazonSocial", this.reciboDTO.getRazonSocial());
		params.put("Ruc", this.reciboDTO.getRuc());
		params.put("NroRecibo", this.reciboDTO.getNumero());
		params.put("Moneda", this.reciboDTO.isMonedaLocal() ? "Guaraníes:" : "Dólares:");
		params.put("Moneda_", this.reciboDTO.isMonedaLocal() ? "Gs." : "U$D");
		params.put("ImporteEnLetra", this.reciboDTO.isMonedaLocal() ? this.reciboDTO.getImporteEnLetras() : this.reciboDTO.getImporteEnLetrasDs());
		params.put("TotalImporteGs",
				this.reciboDTO.isMonedaLocal() ? Utiles.getNumberFormat(this.reciboDTO.getTotalImporteGs())
						: Utiles.getNumberFormatDs(this.reciboDTO.getTotalImporteDs()));
		params.put("Usuario", this.getUs().getNombre());
		this.imprimirComprobante(source, params, dataSource);
	}

	/**
	 * Despliega el Reporte de la Retencion..
	 */
	private void imprimirRetencion_() throws Exception {
		RetencionIvaDTO ret = this.reciboDTO.getRetencion();

		String source = ReportesViewModel.SOURCE_RETENCION;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new RetencionDataSource();
		params.put("NroRetencion", ret.getNumero());
		params.put("RazonSocial", ret.getProveedor().getPos2());
		params.put("Ruc", ret.getProveedor().getPos3());
		params.put("MontoIvaIncluido",
				FORMATTER.format(ret.getTotalImporteFacturas()));
		params.put("MontoIva", FORMATTER.format(ret.getTotalIva()));
		params.put("Porcentaje", String.valueOf(ret.getPorcentaje()));
		params.put("MontoRetencion", FORMATTER.format(ret.getTotalRetencion()));
		params.put("ImporteEnLetras", ret.getImporteEnLetras());

		this.imprimirComprobante(source, params, dataSource);
	}
	
	/**
	 * impresion de la venta..
	 */
	private void imprimirVenta(VentaDTO venta) throws Exception {
		this.selectedVenta = venta;
		
		//String source = ReportesViewModel.SOURCE_VENTA_;
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			//source = ReportesViewModel.SOURCE_VENTA_BATERIAS;
		}
		
		//JRDataSource dataSource = new VentaDataSource(venta);
		String vencimiento = this.m.dateToString(venta.getVencimiento(), Misc.DD_MM_YYYY);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Numero", venta.getNumero());
		params.put("RazonSocial", venta.getDenominacion());
		params.put("Ruc", venta.getRuc());
		params.put("Direccion", venta.getDireccion());
		params.put("Telefono", venta.getTelefono());
		params.put("Vendedor", venta.getVendedor_().toUpperCase());
		params.put("FechaEmision", venta.getFechaEmision());
		params.put("Vencimiento", vencimiento);
		params.put("CR", venta.isCondicionContado() ? "" : "X");
		params.put("CT", venta.isCondicionContado() ? "X" : "");
		params.put("CondicionVenta", venta.getCondicionPago().getPos1().toString().toUpperCase());
		params.put("ImporteTotal", FORMATTER.format(venta.getTotalImporteGs()));
		params.put("ImporteEnLetras", venta.getImporteEnLetras());
		params.put("Iva5", FORMATTER.format(venta.getTotalIva5()));
		params.put("Iva10", FORMATTER.format(venta.getTotalIva10()));
		params.put("TotalIva", FORMATTER.format(venta.getTotalIva()));
		params.put("CantidadTotal", FORMATTER.format(venta.getTotalCantidad()));
		params.put("Items", venta.getDetalles());

		params.put("PuntoPartida", "");
		params.put("FechaTraslado", "");
		params.put("FechaFinTraslado", "");
		params.put("Repartidor", "");
		params.put("CedulaRepartidor", "");
		params.put("MarcaVehiculo", "");
		params.put("ChapaVehiculo", "");
		
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			this.win = (Window) Executions.createComponents(ZUL_IMPRESION_FACTURA_BAT, this.mainComponent, params);
			this.win.doModal();
		} else if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRPS)) {
			String src = venta.isMonedaLocal() ? ZUL_IMPRESION_FACTURA_RPS : ZUL_IMPRESION_FACTURA_RPS_DS;
			this.win = (Window) Executions.createComponents(src, this.mainComponent, params);
			this.win.doModal();
		} else {
			String src = venta.isMonedaLocal() ? ZUL_IMPRESION_FACTURA : ZUL_IMPRESION_FACTURA_DS;
			this.win = (Window) Executions.createComponents(src, this.mainComponent, params);
			this.win.doModal();
		}
	}
	
	/**
	 * impresion del vale..
	 */
	public void imprimirVale(SaldoVale vale) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Title", "Vale");

		String source = "/reportes/jasper/VentaVale.jasper";
		JRDataSource dataSource = new VentaValeDataSource(vale);
		this.imprimirComprobante(source, params, dataSource);		
	}
	
	/**
	 * impresion de la venta..
	 */
	private void imprimirVenta_(VentaDTO venta) throws Exception {
		this.selectedVenta = venta;
		String vencimiento = this.m.dateToString(venta.getVencimiento(), Misc.DD_MM_YYYY);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Numero", venta.getNumero());
		params.put("RazonSocial", venta.getDenominacion());
		params.put("Ruc", venta.getRuc());
		params.put("Direccion", venta.getDireccion());
		params.put("Telefono", venta.getTelefono());
		params.put("Vendedor", venta.getVendedor_().toUpperCase());
		params.put("FechaEmision", venta.getFechaEmision());
		params.put("Vencimiento", vencimiento);
		params.put("CR", venta.isCondicionContado() ? "" : "X");
		params.put("CT", venta.isCondicionContado() ? "X" : "");
		params.put("CondicionVenta", venta.getCondicionPago().getPos1().toString().toUpperCase());
		params.put("ImporteTotal", FORMATTER.format(venta.getTotalImporteGs()));
		params.put("ImporteEnLetras", venta.getImporteEnLetras());
		params.put("Iva5", FORMATTER.format(venta.getTotalIva5()));
		params.put("Iva10", FORMATTER.format(venta.getTotalIva10()));
		params.put("TotalIva", FORMATTER.format(venta.getTotalIva()));
		params.put("CantidadTotal", FORMATTER.format(venta.getTotalCantidad()));
		params.put("Items", venta.getDetalles());

		params.put("PuntoPartida", venta.getPuntoPartida().toLowerCase());
		params.put("FechaTraslado", venta.getFechaTraslado());
		params.put("FechaFinTraslado", venta.getFechaFinTraslado());
		params.put("Repartidor", venta.getRepartidor().toLowerCase());
		params.put("CedulaRepartidor", venta.getCedulaRepartidor());
		params.put("MarcaVehiculo", venta.getMarcaVehiculo().toLowerCase());
		params.put("ChapaVehiculo", venta.getChapaVehiculo().toLowerCase());
		
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			this.win = (Window) Executions.createComponents(ZUL_IMPRESION_FACTURA_BAT, this.mainComponent, params);
			this.win.doModal();
			//this.imprimirComprobante(source, params, dataSource);
		} else if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRPS)) {
			String src = venta.isMonedaLocal() ? ZUL_IMPRESION_FACTURA_RPS : ZUL_IMPRESION_FACTURA_RPS_DS;
			this.win = (Window) Executions.createComponents(src, this.mainComponent, params);
			this.win.doModal();
		} else {
			String src = venta.isMonedaLocal() ? ZUL_IMPRESION_FACTURA : ZUL_IMPRESION_FACTURA_DS;
			this.win = (Window) Executions.createComponents(src, this.mainComponent, params);
			this.win.doModal();
			//this.imprimirComprobante(source, params, dataSource, ReportesViewModel.FORMAT_HTML);
		}
	}

	/**
	 * Despliega el Reporte de la Nota de Credito..
	 */
	private void imprimirNotaCredito() throws Exception {
		NotaCreditoDTO nc = this.selectedNotaCredito;
		Date fechaFac = (Date) nc.getDetallesFacturas().get(0).getVenta().getPos3();
		String fechaFac_ = this.m.dateToString(fechaFac, Misc.DD_MM_YYYY);
		String condicion = (String) nc.getDetallesFacturas().get(0).getVenta().getPos6();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Numero", nc.getNumeroNotaCredito());
		params.put("FechaEmision", nc.getFechaEmision_());
		params.put("RazonSocial", nc.getRazonSocial());
		params.put("Ruc", nc.getRuc());
		params.put("Direccion", nc.getDireccion());
		params.put("Telefono", nc.getTelefono());
		params.put("NroFactura", nc.getDetallesFacturas().get(0).getVenta().getPos2());
		params.put("FechaFactura", fechaFac_);
		params.put("Condicion", condicion.toUpperCase());
		params.put("ImporteTotal", FORMATTER.format(nc.getImporteGs()));
		params.put("ImporteEnLetras", nc.getImporteEnLetras());
		params.put("Iva5", FORMATTER.format(nc.getTotalIva5()));
		params.put("Iva10", FORMATTER.format(nc.getTotalIva10()));
		params.put("TotalIva", FORMATTER.format(nc.getTotalIva()));
		params.put("Vendedor", nc.getVendedor().getPos2());
		params.put("Motivo", nc.getMotivo().getText().toUpperCase());
		
		if (nc.isMotivoDescuento()) {
			nc.getDetalles().add(this.getItemDescuento(nc));
		}

		String src = nc.isMonedaLocal() ? ZUL_IMPRESION_NOTACREDITO : ZUL_IMPRESION_NOTACREDITO_DS;
		this.win = (Window) Executions.createComponents(src, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * impresion nota credito en laser..
	 */
	private void imprimirNotaCredito_(NotaCreditoDTO nc) throws Exception {
		Date fechaFac = (Date) nc.getDetallesFacturas().get(0).getVenta().getPos3();
		String fechaFac_ = this.m.dateToString(fechaFac, Misc.DD_MM_YYYY);
		String condicion = (String) nc.getDetallesFacturas().get(0).getVenta().getPos6();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Numero", nc.getNumeroNotaCredito());
		params.put("FechaEmision", nc.getFechaEmision_());
		params.put("RazonSocial", nc.getRazonSocial());
		params.put("Ruc", nc.getRuc());
		params.put("Direccion", nc.getDireccion());
		params.put("Telefono", nc.getTelefono());
		params.put("NroFactura", nc.getDetallesFacturas().get(0).getVenta().getPos2());
		params.put("FechaFactura", fechaFac_);
		params.put("Condicion", condicion.toUpperCase());
		params.put("ImporteTotal", FORMATTER.format(nc.getImporteGs()));
		params.put("ImporteEnLetras", nc.getImporteEnLetras());
		params.put("Iva5", FORMATTER.format(nc.getTotalIva5()));
		params.put("Iva10", FORMATTER.format(nc.getTotalIva10()));
		params.put("TotalIva", FORMATTER.format(nc.getTotalIva()));
		params.put("Vendedor", nc.getVendedor().getPos2());
		params.put("Motivo", nc.getMotivo().getText().toUpperCase());
		
		if (nc.isMotivoDescuento()) {
			nc.getDetalles().add(this.getItemDescuento(nc));
		}
		
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRPS)) {
			String src = nc.isMonedaLocal() ? ZUL_IMPRESION_NOTACREDITO_RPS : ZUL_IMPRESION_NOTACREDITO_RPS_DS;
			this.win = (Window) Executions.createComponents(src, this.mainComponent, params);
			this.win.doModal();
		} else {
			String src = nc.isMonedaLocal() ? ZUL_IMPRESION_NOTACREDITO : ZUL_IMPRESION_NOTACREDITO_DS;
			this.win = (Window) Executions.createComponents(src, this.mainComponent, params);
			this.win.doModal();
		}
	}

	/**
	 * @return el item descuento concedido..
	 */
	private NotaCreditoDetalleDTO getItemDescuento(NotaCreditoDTO nc) {
		double importeGs = (double) nc.getImportesFacturas()[0];

		MyArray art = new MyArray("DESCUENTO CONCEDIDO", "@DESCUENTO");
		NotaCreditoDetalleDTO out = new NotaCreditoDetalleDTO(this.getIva10());
		out.setArticulo(art);
		out.setCantidad(1);
		out.setMontoGs(importeGs);
		out.setImporteGs(importeGs);
		out.setTipoDetalle(new MyPair(0, Configuracion.SIGLA_TIPO_NC_DETALLE_ARTICULO));
		return out;
	}
	
	/**
	 * Despliega el Reporte del Resumen de la Planilla de Caja..
	 */
	private void imprimirResumen() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CajaPeriodo planilla = (CajaPeriodo) rr.getObject(
				CajaPeriodo.class.getName(), this.dto.getId());

		String source = ReportesViewModel.SOURCE_RESUMEN;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new CajaPeriodoResumenDataSource(planilla);
		params.put(
				"Periodo",
				this.m.dateToString(this.dto.getApertura(), Misc.DD_MM_YYYY)
						+ " AL "
						+ this.m.dateToString(this.dto.getCierre(),
								Misc.DD_MM_YYYY));
		params.put("NroPlanilla", planilla.getNumero());
		params.put("Cajero", planilla.getResponsable().getDescripcion());
		params.put("Usuario", getUs().getNombre());

		this.imprimirComprobante(source, params, dataSource);
	}

	/**
	 * impresion del item seleccionado..
	 */
	private void imprimirItem_() throws Exception {

		int tipo = (int) this.selectedItem.getPos1();

		switch (tipo) {

		case ES_REPOSICION:
			this.imprimirReposicion();
			break;

		case ES_EGRESO:
			this.imprimirReposicion();
			break;

		case ES_COBRO:
			this.imprimirCobro();
			break;

		case ES_PAGO:
			this.imprimirPago();
			break;

		case ES_GASTO:
			// no se imprime..
			break;

		case ES_VENTA_CONTADO:
			this.imprimirVenta(this.selectedVenta);
			break;

		case ES_VENTA_CREDITO:
			this.imprimirVenta_(this.selectedVenta);
			break;

		case ES_NOTA_CREDITO_VENTA:
			if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
				this.imprimirNotaCredito();
			} else {
				this.imprimirNotaCredito_(this.selectedNotaCredito);
			}
			break;
		}
	}

	/**
	 * Despliega el comprobante en un pdf para su impresion..
	 */
	public void imprimirComprobante(String source,
			Map<String, Object> parametros, JRDataSource dataSource) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);

		this.win = (Window) Executions.createComponents(ReportesViewModel.ZUL_REPORTES, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * Despliega el comprobante en un pdf para su impresion..
	 */
	public void imprimirComprobante(String source,
			Map<String, Object> parametros, JRDataSource dataSource, Object[] format) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);
		params.put("format", format);

		this.win = (Window) Executions.createComponents(ReportesViewModel.ZUL_REPORTES_, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * Despliega el comprobante en un pdf para su impresion..
	 */
	public void imprimirComprobante_nc(String source,
			Map<String, Object> parametros, JRDataSource dataSource, Object[] format) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);
		params.put("format", format);

		this.win = (Window) Executions.createComponents(ReportesViewModel.ZUL_REPORTES_NC, this.mainComponent, params);
		this.win.doModal();
	}

	/************************************************************/

	/*************************** AGENDA *************************/

	/**
	 * Se invoca al control de la Agenda 'ctrAgenda' y se muestra mediante
	 * mostrarAgenda(). Eventos que se generan automaticamente: - Cuando se
	 * agrega un Gasto - Cuando se confirma el Sub-Diario
	 */
	private static String EVENTO_FACTURACION_PEDIDO = "Se facturó el Pedido Nro. ";

	@Override
	public int getCtrAgendaTipo() {
		return ControlAgendaEvento.NORMAL;
	}

	@Override
	public String getCtrAgendaKey() {
		return this.dto.getNumero();
	}

	@Override
	public String getCtrAgendaTitulo() {
		return "[Caja: " + this.getCtrAgendaKey() + "]";
	}

	@Override
	public boolean getAgendaDeshabilitado() {
		return this.dto.esNuevo() == true;
	}

	/************************************************************/
	

	/*********************** VALIDACIONES ***********************/

	private String mensajeError = Configuracion.TEXTO_ERROR_VALIDACION;

	@Override
	public boolean verificarAlGrabar() {
		try {

			if (this.validarFormulario() && this.dto.esNuevo()) {
				this.dto.setNumero(Configuracion.NRO_CAJA_PERIODO
						+ "-"
						+ AutoNumeroControl.getAutoNumero(
								Configuracion.NRO_CAJA_PERIODO, 5));
				this.addEventoAgenda(ControlAgendaEvento.NORMAL,
						this.dto.getNumero(), 0,
						"Se realizó la Apertura de Caja..", null);
			}

			if (this.validarFormulario() == true) {
				// this.actualizarCuentaCorriente();
				this.asignarNumeros();
				this.setEstadoABMConsulta();
			}

		} catch (Exception e) {
			e.printStackTrace();
			// daniel
			// si da algún error, entonces debería retornar falso y no dejar
			// continuar
			this.mensajeError = "Error sistema  [CajaPeriodoControlBody.verificarAlGrabar]: "
					+ e.getMessage();
			return false;
		}

		return this.validarFormulario();
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return mensajeError;
	}

	private boolean validarFormulario() {
		boolean out = true;
		this.mensajeError = "No se puede completar la operación debido a:";

		if (this.dto.getCaja().esNuevo() == true) {
			out = false;
			this.mensajeError += "\n - Debe asignar la Caja Principal..";
		}
		
		if (this.dto.getTipo().isEmpty()) {
			out = false;
			this.mensajeError += "\n - Debe asignar el Tipo de Caja..";
		}

		return out;
	}

	// Asigna numeros a los Recibos
	private void asignarNumeros() {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			for (ReciboDTO rec : this.dto.getRecibos()) {
				if (rec.esNuevo() && !rec.isCobro()) {
					if (rec.isCancelacionChequeRechazado() || rec.isReembolsoPrestamo() || rec.isCancelacionChequeRechazadoProv()) {
						String nro = Configuracion.NRO_CANCELACION_CHEQUE_RECHAZADO
								+ "-"
								+ AutoNumeroControl.getAutoNumero(Configuracion.NRO_CANCELACION_CHEQUE_RECHAZADO, 5);
						rec.setNumero(nro);
					} else {
						String pref = this.dto.getTipo().equals(CajaPeriodo.TIPO_PAGOS_DEPURACION) ? 
								Configuracion.NRO_RECIBO_PAGO_DEPURACION : Configuracion.NRO_RECIBO_PAGO;
						String nro = pref
								+ "-"
								+ AutoNumeroControl.getAutoNumero(pref, 5);
						rec.setNumero(nro);
						for (ReciboFormaPagoDTO fp : rec.getFormasPago()) {
							if (fp.isChequePropio()) {
								AutoNumero aut = rr.getAutoNumero(fp.getAuxi());
								if (aut != null) {
									aut.setNumero(Long.parseLong(fp.getChequeNumero()));
									rr.saveObject(aut, this.getLoginNombre());
								}
							}
						}
					}
					this.addEventoAgenda(ControlAgendaEvento.NORMAL,
							this.dto.getNumero(), 0,
							"Se agregó el Recibo Número: " + rec.getNumero(),
							null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateImporteRecibos(ReciboDTO recibo) {
		double totalGs = 0;
		double totalDs = 0;

		// si es recibo normal calcula el monto segun las facturas aplicadas
		if (recibo.isAnticipo() == false) {
			for (ReciboDetalleDTO det : recibo.getDetalles()) {
				totalGs += det.getMontoGs();
				totalDs += det.getMontoDs();
			}

			// si es anticipo calcula el monto segun las formas de Pago
		} else {
			for (ReciboFormaPagoDTO det : recibo.getFormasPago()) {
				totalGs += det.getMontoGs();
				totalDs += det.getMontoDs();
			}
		}

		recibo.setTotalImporteDs(totalDs);
		recibo.setTotalImporteGs(totalGs);
	}

	/**
	 * DataSource de la Reposicion de Caja..
	 */
	class CajaReposicionDataSource implements JRDataSource {

		List<MyArray> detalle = new ArrayList<MyArray>();
		CajaReposicion rep;

		public CajaReposicionDataSource() {
			try {
				RegisterDomain rr = RegisterDomain.getInstance();
				long idReposicion = reposicion.getId();
				this.rep = (CajaReposicion) rr.getObject(CajaReposicion.class.getName(), idReposicion);
				this.detalle.add(new MyArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {

			Object value = null;
			String fieldName = field.getName();
			CajaReposicion item = this.rep;

			if ("FechaFactura".equals(fieldName)) {
				value = Utiles.getDateToString(item.getFormaPago().getChequeFecha(), Utiles.DD_MM_YYYY);
			} else if ("DescFactura".equals(fieldName)) {
				value = item.getFormaPago().getDescripcion();
				if (item.getFormaPago().isChequePropio()) {					
					try {
						RegisterDomain rr = RegisterDomain.getInstance(); 
						BancoCheque cheq = rr.getChequePropio(item.getFormaPago().getId());
						if (cheq != null) {
							value += " - " + cheq.getBeneficiario().toUpperCase();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			} else if ("Importe".equals(fieldName)) {
				double importe = item.getFormaPago().getMontoGs();
				value = Utiles.getNumberFormat(importe);
			} else if ("TipoDetalle".equals(fieldName)) {
				value = "DETALLE";
			} else if ("observacion".equals(fieldName)) {
				value = this.rep.getObservacion();
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < this.detalle.size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}

	/**
	 * DataSource de Vale..
	 */
	class VentaValeDataSource implements JRDataSource {

		List<MyArray> detalle = new ArrayList<MyArray>();
		CajaReposicion rep;
		SaldoVale vale;
		Venta venta;

		public VentaValeDataSource(SaldoVale vale) {
			try {
				RegisterDomain rr = RegisterDomain.getInstance();	
				this.vale = vale;
				this.venta = (Venta) rr.getObject(Venta.class.getName(), vale.getIdVenta());
				this.detalle.add(new MyArray());
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {

			Object value = null;
			String fieldName = field.getName();
			SaldoVale item = this.vale;

			if ("Fecha".equals(fieldName)) {
				value = Utiles.getDateToString(this.venta.getFecha(), Utiles.DD_MM_YYYY);
			} else if ("Factura".equals(fieldName)) {
				value = this.venta.getNumero();
			} else if ("Importe".equals(fieldName)) {
				double importe = this.vale.getImporte();
				value = Utiles.getNumberFormat(importe);
			} else if ("Cajero".equals(fieldName)) {
				value = dto.getCaja().getResponsable().getPos1();
			} else if ("Beneficiario".equals(fieldName)) {
				value = this.venta.getCliente().getRazonSocial();
			} else if ("observacion".equals(fieldName)) {
				value = "VALIDO HASTA: " + Utiles.getDateToString(item.getValidoHasta(), Utiles.DD_MM_YYYY);
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < this.detalle.size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}
	
	/**
	 * DataSource del Recibo..
	 */
	class ReciboDataSource implements JRDataSource {

		List<MyArray> detalle = new ArrayList<MyArray>();

		public ReciboDataSource() {
			for (ReciboDetalleDTO item : reciboDTO.getDetalles()) {
				MyArray m = new MyArray();
				m.setPos1(item.getFecha());
				m.setPos2(item.getDescripcion());
				m.setPos3(reciboDTO.isMonedaLocal() ? item.getMontoGs() : item.getMontoDs());
				m.setPos4("Facturas");
				this.detalle.add(m);
			}
			for (ReciboFormaPagoDTO item : reciboDTO.getFormasPago()) {
				MyArray my = new MyArray();
				my.setPos1(m.dateToString(item.getModificado(), Misc.DD_MM_YYYY));
				my.setPos2(item.getDescripcion());
				my.setPos3(reciboDTO.isMonedaLocal() ? item.getMontoGs() : item.getMontoDs());
				my.setPos4("Formas de Pago");
				this.detalle.add(my);
			}
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			MyArray item = this.detalle.get(index);

			if ("FechaFactura".equals(fieldName)) {
				value = item.getPos1();
			} else if ("DescFactura".equals(fieldName)) {
				value = item.getPos2();
			} else if ("Importe".equals(fieldName)) {
				double importe = (double) item.getPos3();
				value = reciboDTO.isMonedaLocal() ? Utiles.getNumberFormat(importe) : Utiles.getNumberFormatDs(importe);
			} else if ("TotalImporte".equals(fieldName)) {
				double importe = reciboDTO.isMonedaLocal() ? reciboDTO.getTotalImporteGs() : reciboDTO.getTotalImporteDs();
				value = reciboDTO.isMonedaLocal() ? Utiles.getNumberFormat(importe) : Utiles.getNumberFormatDs(importe);
			} else if ("TipoDetalle".equals(fieldName)) {
				value = item.getPos4();
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < detalle.size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}
	
	/**
	 * DataSource de los cobros..
	 */
	class ReciboCobroDataSource implements JRDataSource {

		Map<String, Double> totales = new HashMap<String, Double>();
		Misc misc = new Misc();
		
		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			List<BeanReciboDetalle> dets = new ArrayList<BeanReciboDetalle>();
			List<BeanFormaPagoCheque> cheques = new ArrayList<BeanFormaPagoCheque>();
			double efectivo = 0;
			double retencion = 0;
			
			for (ReciboDetalleDTO det : reciboDTO.getDetalles()) {
				dets.add(new BeanReciboDetalle(misc.dateToString(det
						.getMovimiento().getFechaEmision(), "dd-MM-yy"),
						det.isCancelacion() ? "Canc." : "C/parcial", det.getMovimiento().getNroComprobante()
								.replace("(1/1)", ""), det.getMontoGs_()));
			}
			
			for (ReciboFormaPagoDTO fpago : reciboDTO.getFormasPago()) {
				if (fpago.isChequeTercero()) {
					cheques.add(new BeanFormaPagoCheque(
							fpago.getChequeNumero(), fpago.getChequeBanco()
									.getText(), m.dateToString(
									fpago.getChequeFecha(), "dd-MM-yy"), fpago
									.getChequeLibrador()));
				} else if (fpago.isEfectivo()) {
					efectivo += fpago.getMontoGs();
					
				} else if (fpago.isRetencionIVA()) {
					retencion += fpago.getMontoGs();
				}
			}
			
			if ("Facturas".equals(fieldName)) {
				value = dets;
			} else if ("Cheques".equals(fieldName)) {
				value = cheques;
			} else if ("Efectivo".equals(fieldName)) {
				value = FORMATTER.format(efectivo);
			} else if ("Retencion".equals(fieldName)) {
				value = FORMATTER.format(retencion);
			} else if ("Fecha".equals(fieldName)) {
				value = misc.dateToString(reciboDTO.getFechaEmision(), "dd-MM-yy");
			}
 			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < 0) {
				index++;
				return true;
			}
			return false;
		}
	}

	/**
	 * DataSource de la Retencion..
	 */
	class RetencionDataSource implements JRDataSource {

		RetencionIvaDTO retencion;

		public RetencionDataSource() {
			this.retencion = reciboDTO.getRetencion();
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			RetencionIvaDetalleDTO det = this.retencion.getDetalles()
					.get(index);

			if ("NroFactura".equals(fieldName)) {
				value = det.getNumeroFactura();
			} else if ("DescFactura".equals(fieldName)) {
				value = det.getTipoMovimiento().getText();
			} else if ("ImporteFactura".equals(fieldName)) {
				value = FORMATTER.format(det.getImporteFactura());
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < this.retencion.getDetalles().size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}

	/*********************** GETTER/SETTER **********************/
	
	/**
	 * @return si la operacion es habilitada..
	 */
	public boolean isOperacionHabilitada(String operacion) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.isOperacionHabilitada(this.getLoginNombre(), operacion);
	}
	
	@DependsOn({ "arqueo.pos1", "arqueo.pos2", "arqueo.pos3" })
	public double getTotalArqueo() {
		double totalEfectivo = (double) this.arqueo.getPos1();
		double totalCheque = (double) this.arqueo.getPos2();
		double totalTarjeta = (double) this.arqueo.getPos3();
		return totalEfectivo + totalCheque + totalTarjeta;
	}

	@DependsOn("dto")
	public boolean isBotoneraVisible() {
		return (this.dto.isReadonly() == false)
				&& (this.isUsuarioResponsableCaja());
	}
	 
	public List<String> getTiposCajas() {
		return CajaPeriodo.getTipos();
	}

	public boolean isAbrirCajaDisabled() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return !rr.isOperacionHabilitada(this.getLoginNombre(), "ReabrirCajaPlanillaABM");
	}

	@DependsOn("selectedItem")
	public boolean isRetencionDisabled() {
		return this.selectedItem == null
				|| ((int) this.selectedItem.getPos1()) != ES_PAGO
				|| this.reciboDTO.getRetencion() == null;
	}

	/**
	 * @return el prefijo del talonario de venta..
	 */
	private String getNumeroVenta() throws Exception {
		MyArray talonario = this.dto.getCaja().getTalonarioVentas();
		int boca = (int) talonario.getPos2();
		int punto = (int) talonario.getPos3();
		String nro = AutoNumeroControl.getAutoNumero(
				(String) talonario.getPos1(), 7);
		String pref = "00"; if(boca > 9) pref = "0";
		String prefPunto = "00"; if(punto > 9) prefPunto = "0";
		return pref + boca + "-" + prefPunto + punto + "-" + nro;
	}
	
	/**
	 * @return el prefijo del talonario de venta provisorio..
	 */
	private String getNumeroVentaProvisorio() throws Exception {
		MyArray talonario = this.dto.getCaja().getTalonarioVentas();
		int boca = (int) talonario.getPos2();
		int punto = (int) talonario.getPos3();
		String nro = AutoNumeroControl.getAutoNumero(
				(String) talonario.getPos1(), 7, true);
		String pref = "00"; if(boca > 9) pref = "0";
		String prefPunto = "00"; if(punto > 9) prefPunto = "0";
		return pref + boca + "-" + prefPunto + punto + "-" + nro;
	}
	

	/**
	 * @return el prefijo del talonario de venta..
	 */
	private String getNumeroNotaCredito() throws Exception {
		MyArray talonario = this.dto.getCaja().getTalonarioNotasCredito();
		int boca = (int) talonario.getPos2();
		int punto = (int) talonario.getPos3();
		String nro = AutoNumeroControl.getAutoNumero(
				(String) talonario.getPos1(), 7);
		String pref = "00"; if(boca > 9) pref = "0";
		String prefPunto = "00"; if(punto > 9) prefPunto = "0";
		return pref + boca + "-" + prefPunto + punto + "-" + nro;
	}

	/**
	 * @return el prefijo del talonario de venta..
	 */
	private String getNumeroAutoFactura() throws Exception {
		MyArray talonario = this.dto.getCaja().getTalonarioAutoFacturas();
		int boca = (int) talonario.getPos2();
		int punto = (int) talonario.getPos3();
		String nro = AutoNumeroControl.getAutoNumero(
				(String) talonario.getPos1(), 7);
		return "00" + boca + "-00" + punto + "-" + nro;
	}

	private boolean isUsuarioResponsableCaja() {
		long idCurrent = this.getAcceso().getFuncionario().getId().longValue();
		long idResponsable = this.dto.getResponsable().getId().longValue();
		return idCurrent == idResponsable;
	}

	/**
	 * @return el mensaje de la informacion adicional..
	 */
	private String getInformacionAdicional() {
		MyArray talVta = this.dto.getCaja().getTalonarioVentas();
		MyArray talNC = this.dto.getCaja().getTalonarioNotasCredito();
		MyArray talAut = this.dto.getCaja().getTalonarioAutoFacturas();
		MyArray talRec = this.dto.getCaja().getTalonarioRecibos();
		String out = "- Talonario de Ventas: " + talVta.getPos1()
				+ "\n - Boca: " + talVta.getPos2() + "\n - Punto: "
				+ talVta.getPos3() + "\n - Desde: " + talVta.getPos4()
				+ "\n - Hasta: " + talVta.getPos5()
				+ "\n ------------------------------------------------"
				+ "\n - Talonario de N.Cred.: " + talNC.getPos1()
				+ "\n - Boca: " + talNC.getPos2() + "\n - Punto: "
				+ talNC.getPos3() + "\n - Desde: " + talNC.getPos4()
				+ "\n - Hasta: " + talNC.getPos5()
				+ "\n ------------------------------------------------"
				+ "\n - Talonario de Auto-Facturas: " + talAut.getPos1()
				+ "\n - Boca: " + talAut.getPos2() + "\n - Punto: "
				+ talAut.getPos3() + "\n - Desde: " + talAut.getPos4()
				+ "\n - Hasta: " + talAut.getPos5()
				+ "\n ------------------------------------------------"
				+ "\n - Talonario de Recibos: " + talRec.getPos1()
				+ "\n - Boca: " + talRec.getPos2() + "\n - Punto: "
				+ talRec.getPos3() + "\n - Desde: " + talRec.getPos4()
				+ "\n - Hasta: " + talRec.getPos5();
		return out;
	}

	/**
	 * @return el importe de la nc solo de las ventas contado..
	 */
	private double getImporteNotaCredito(long idNotaCredito) {
		NotaCreditoDTO ncdto = null;
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			NotaCredito nc = (NotaCredito) rr.getObject(
					NotaCredito.class.getName(), idNotaCredito);
			ncdto = (NotaCreditoDTO) new NotaCreditoAssembler().domainToDto(nc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ncdto == null ? 0 : ncdto.getImporteVentaContadoGs();
	}

	/**
	 * @return
	 */
	@DependsOn({ "fechaDesde", "fechaHasta" })
	public List<VentaDTO> getPedidosPendientes() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		AssemblerVenta assVta = new AssemblerVenta();
		List<VentaDTO> out = new ArrayList<VentaDTO>();
		List<Venta> vtas;
		
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			vtas = rr.getPedidosPendientesBaterias(this.dto.getCaja()
					.getSucursal().getId(), this.getFechaDesde(), this.getFechaHasta());
		} else {
			vtas = rr.getPedidosPendientes(this.dto.getCaja()
					.getSucursal().getId(), this.getFechaDesde(), this.getFechaHasta());			
		}
		
		for (Venta venta : vtas) {
			VentaDTO vtaDto = (VentaDTO) assVta.domainToDto(venta);
			out.add(vtaDto);
		}
		return out;
	}
	
	@DependsOn("nroPlanilla")
	public List<Object[]> getSaldosCajaChica() throws Exception {
		if (this.nroPlanilla.trim().isEmpty()) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getSaldosCajaChica(this.nroPlanilla);
	}
	
	/**
	 * @return las monedas..
	 */
	public List<MyArray> getMonedas() {
		List<MyArray> out = new ArrayList<MyArray>();
		out.add(this.getDtoUtil().getMonedaGuaraniConSimbolo());
		out.add(this.getDtoUtil().getMonedaDolaresConSimbolo());
		return out;
	}
	
	/**
	 * @return las monedas..
	 */
	public List<MyPair> getMonedas_() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Tipo gs = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		Tipo ds = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR);
		List<MyPair> out = new ArrayList<MyPair>();
		out.add(new MyPair(gs.getId(), gs.getDescripcion(), gs.getSigla()));
		out.add(new MyPair(ds.getId(), ds.getDescripcion(), ds.getSigla()));
		return out;
	}
	
	/**
	 * @return validez..
	 */
	public String getValidezPedidos() {
		Date valido = Utiles.agregarDias(new Date(), -2);
		return "after " + Utiles.getDateToString(valido, "yyyyMMdd");
	}

	public CajaPeriodoDTO getDto() {
		return dto;
	}

	public void setDto(CajaPeriodoDTO dto) {
		this.dto = dto;
	}

	public MyArray getSelectedChequera() {
		return selectedChequera;
	}

	public void setSelectedChequera(MyArray selectedChequera) {
		this.selectedChequera = selectedChequera;
	}

	public MyArray getReposicion() {
		return reposicion;
	}

	public void setReposicion(MyArray egreso) {
		this.reposicion = egreso;
	}

	public ReciboDTO getReciboDTO() {
		return reciboDTO;
	}

	public void setReciboDTO(ReciboDTO ordenPagoDTO) {
		this.reciboDTO = ordenPagoDTO;
	}

	@DependsOn("reposicion.pos9")
	public MyPair getSelectedMoneda() {
		MyPair moneda = (MyPair) this.reposicion.getPos9();
		return moneda;
	}

	@DependsOn("selectedMoneda")
	public boolean isReposicionMonedaLocal() {
		MyPair moneda = (MyPair) this.reposicion.getPos9();
		String sigla = moneda.getSigla();
		return sigla.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0;
	}

	public boolean isReciboMonedaLocal() {
		String sigla = (String) this.reciboDTO.getMoneda().getPos2();
		return sigla.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0;
	}

	public ReciboFormaPagoDTO getNvoFormaPago() {
		return nvoFormaPago;
	}

	public void setNvoFormaPago(ReciboFormaPagoDTO nvoFormaPago) {
		this.nvoFormaPago = nvoFormaPago;
	}

	public VentaDTO getSelectedVenta() {
		return selectedVenta;
	}

	public void setSelectedVenta(VentaDTO selectedVenta) {
		this.selectedVenta = selectedVenta;
	}

	public GastoDTO getDtoGasto() {
		return dtoGasto;
	}

	public void setDtoGasto(GastoDTO dtoGasto) {
		this.dtoGasto = dtoGasto;
	}

	public static String getItemParam() {
		return ITEM_PARAM;
	}

	public static String getItemReposicion() {
		return ITEM_REPOSICION;
	}

	public MyArray getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MyArray selectedItem) {
		this.selectedItem = selectedItem;
	}

	public MyArray getArqueo() {
		return arqueo;
	}

	public void setArqueo(MyArray arqueo) {
		this.arqueo = arqueo;
	}

	private boolean isMovimientoPendiente(String sigla) {
		String siglaPendiente = this.estadoComprobantePendiente.getSigla();
		return sigla.compareTo(siglaPendiente) == 0;
	}

	public NotaCreditoDTO getSelectedNotaCredito() {
		return selectedNotaCredito;
	}

	public void setSelectedNotaCredito(NotaCreditoDTO selectedNotaCredito) {
		this.selectedNotaCredito = selectedNotaCredito;
	}

	public String getSelectedDenominacion() {
		return selectedDenominacion;
	}

	public void setSelectedDenominacion(String selectedDenominacion) {
		this.selectedDenominacion = selectedDenominacion;
	}

	@SuppressWarnings("deprecation")
	public Date getFechaDesde() {
		fechaDesde.setHours(0);
		fechaDesde.setMinutes(0);
		fechaDesde.setSeconds(0);
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	@SuppressWarnings("deprecation")
	public Date getFechaHasta() {
		fechaHasta.setHours(23);
		fechaHasta.setMinutes(59);
		fechaHasta.setSeconds(59);
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public int getTotalPedidosPendientes() {
		return totalPedidosPendientes;
	}

	public void setTotalPedidosPendientes(int totalPedidosPendientes) {
		this.totalPedidosPendientes = totalPedidosPendientes;
	}

	public Object[] getSelectedCajaChica() {
		return selectedCajaChica;
	}

	public void setSelectedCajaChica(Object[] selectedCajaChica) {
		this.selectedCajaChica = selectedCajaChica;
	}

	public String getNroPlanilla() {
		return nroPlanilla;
	}

	public void setNroPlanilla(String nroPlanilla) {
		this.nroPlanilla = nroPlanilla;
	}

}

/**
 * Validador Agregar/Modificar Pago..
 */
class ValidadorAgregarRecibo implements VerificaAceptarCancelar {

	private CajaPeriodoControlBody ctr;
	private ReciboDTO recibo;
	private Object[] datos;
	private String mensaje;

	public ValidadorAgregarRecibo(CajaPeriodoControlBody ctr) {
		this.ctr = ctr;
		this.recibo = ctr.getReciboDTO();
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;

		this.datos = ctr.getDatosMovimientosAplicados();
		double totalGs = (double) datos[0];
		double totalGs_ = (double) datos[2];
		boolean anticipo = ctr.getReciboDTO().isAnticipo();
		boolean aCobrar = ctr.getReciboDTO().isPendienteCobro();
		this.mensaje = "No se puede realizar la operación debido a: \n";

		if (anticipo == true) {

			// validaciones del recibo de tipo anticipo..

		} else if (aCobrar == true) {

			// validaciones del recibo pendiente de cobro..
			if (this.recibo.getDetalles().size() == 0) {
				out = false;
				mensaje += "\n - El recibo debe aplicarse a al menos una Factura..";
			}

		} else {

			// validaciones del recibo normal..
			if (this.recibo.getDetalles().size() == 0) {
				// out = false;
				mensaje += "\n - El recibo debe aplicarse a al menos una Factura..";
			} else if (this.recibo.getFormasPago().size() == 0) {
				out = false;
				mensaje += "\n - La lista de Formas de Pago no puede quedar vacía..";
			} else if ((totalGs - totalGs_) >= 100 || (totalGs_ - totalGs) >= 100) {
				out = false;
				mensaje += "\n - El total de Facturas Aplicadas debe coincidir con el total de Formas de Pago ";
			}
		}
		
		if (this.recibo.isCobro() && this.recibo.getNumero().isEmpty()) {
			out = false;
			mensaje += "\n - Debe ingresar el número de recibo..";
		}
		
		if (this.recibo.getObservacion() == null || this.recibo.getObservacion().trim().isEmpty()) {
			out = false;
			mensaje += "\n - Debe ingresar la observación del recibo..";
		}
		
		if ((!this.recibo.isRecaudacionMra()) && this.recibo.isCobro() && ((this.recibo.getNumero().length() != 15) || (this.recibo.getNumero().charAt(3) != '-') || (this.recibo.getNumero().charAt(7) != '-')
				|| !Utiles.validarNumeroFactura(this.recibo.getNumero()))) {
			out = false;
			mensaje += "\n - Formato incorrecto de número..";
		}
		
		if (this.recibo.isCobro() && this.recibo.isEntregado() && this.recibo.getNumeroRecibo().isEmpty()) {
			out = false;
			mensaje += "\n - Debe ingresar el número de recibo..";
		}
		
		if (this.recibo.isCobro() && this.recibo.isEntregado() && this.recibo.getFechaRecibo() == null) {
			out = false;
			mensaje += "\n - Debe ingresar la fecha del recibo..";
		}
		
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			if (this.recibo.isCobro() && this.recibo.esNuevo() && rr.getRecibo(this.recibo.getNumero()) != null) {
				out = false;
				mensaje += "\n - Ya existe un Recibo con el mismo número..";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensaje;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al Cancelar..";
	}

}

// Validador Agregar Egreso
class ValidadorEgreso implements VerificaAceptarCancelar {

	private MyArray egreso;
	private String mensaje;

	public ValidadorEgreso(MyArray egreso) {
		this.egreso = egreso;
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensaje = "No se puede completar la operación debido a \n";

		String responsable = (String) egreso.getPos1();
		double monto = (double) egreso.getPos5();
		String observacion = (String) egreso.getPos7();
		boolean reposicion = (boolean) egreso.getPos16();
		MyPair tipoEgreso = (MyPair) egreso.getPos10();

		if ((reposicion == false) && (tipoEgreso.esNuevo() == true)) {
			out = false;
			mensaje += "\n - Debe especificar el tipo de egreso..";
		}

		if (responsable.trim().length() == 0) {
			out = false;
			mensaje += "\n - Debe ingresar quién es el responsable..";
		}

		if (monto <= 0.001) {
			out = false;
			mensaje += "\n - Debe ingresar el monto..";
		}

		if (observacion.trim().length() == 0) {
			out = false;
			mensaje += "\n - Debe ingresar la observación..";
		}

		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensaje;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al Cancelar";
	}

}

/**
 * Validador de Gasto..
 */
class ValidadorGasto implements VerificaAceptarCancelar {

	private GastoDTO gasto;
	private CajaPeriodoControlBody ctr;
	private String mensaje = "";

	public ValidadorGasto(CajaPeriodoControlBody ctr) {
		this.ctr = ctr;
		this.gasto = ctr.getDtoGasto();
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		mensaje = "No se puede completar la operación debido a: \n";

		double limite = Configuracion.IMPORTE_LIMITE_GASTO_FONDO_FIJO;
		double totalFac = this.gasto.getImporteGs();
		double totalFP = this.gasto.getImporteFormaPagoGs();

		RegisterDomain rr = RegisterDomain.getInstance();

		try {

			if (this.ctr.m.esIgual(totalFac, totalFP) == false) {
				out = false;
				this.mensaje += "- El total de la factura debe coincidir con el total de formas de pago \n";
			}

			if (this.gasto.getImporteGs() > limite) {
				/*
				 * out = false; mensaje +=
				 * "- Para este tipo de Gasto no se permite importe mayor a: " +
				 * String.format("%1$,.0f", limite) + " Gs. \n";
				 */
			}

			// Valida que se haya asignado el Proveedor..
			if (this.gasto.getProveedor().esNuevo() == true) {
				out = false;
				mensaje += "- Debe seleccionar un Proveedor.. \n";
			}

			// Valida la fecha de la factura
			if (this.gasto.getFecha().compareTo(
					ctr.m.agregarDias(new Date(), 1)) != -1) {
				mensaje = mensaje
						+ "\n - La fecha no puede ser mayor a la del día..";
				out = false;
			}

			// Valida el nro de factura..
			if (((this.gasto.getNumeroFactura().split("-").length != 3) || !this.ctr.m
					.containsOnlyNumbers(this.gasto.getNumeroFactura()))) {
				out = false;
				mensaje += "- Mal formato del número de Factura.. \n";
			}

			// Valida si la factura ya existe..
			if (rr.getGastoByNumero(this.gasto.getNumeroFactura(), this.gasto.getTimbrado()) != null) {
				out = false;
				mensaje += "\n - Ya existe en la Base de Datos una Factura "
						+ "con el mismo número y el mismo timbrado..";
			}

			// Valida el campo existe cmpbte fisico..
			if ((this.gasto.isExisteComprobanteFisico() == false)
					&& (this.gasto.getMotivoComprobanteFisico().trim().length() == 0)) {
				out = false;
				mensaje += "- Debe especificar el motivo por el cual no "
						+ "se cuenta con el Comprobante físico.. \n";
			}
			
			if (this.gasto.getDetalles().size() == 0) {
				out = false;
				mensaje += "- Debe ingresar el Detalle.";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return mensaje;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al Cancelar";
	}

}

/**
 * Validador del arqueo de caja..
 */
class ValidadorArqueoCaja implements VerificaAceptarCancelar {

	private CajaPeriodoControlBody ctr;
	private String mensaje = "";

	public ValidadorArqueoCaja(CajaPeriodoControlBody ctr) {
		this.ctr = ctr;
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensaje = "No se puede completar la operación debido a:";

		double saldoCaja = this.ctr.getSaldoCajaPagoPeriodoGs();
		double totalArqueo = this.ctr.getTotalArqueo();
		boolean coinciden = this.ctr.m.esIgual(saldoCaja, totalArqueo);

		if (coinciden == false) {
			out = false;
			this.mensaje += "\n - El arqueo no coincide con el Saldo de Caja..";
		}

		if (out == false) {
			try {
				this.ctr.intentoFallidoArqueo();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensaje;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al cancelar..";
	}
}

/**
 * validador de la forma de pago de ventas..
 */
class ValidadorFormaPagoVenta implements VerificaAceptarCancelar {

	private CajaPeriodoControlBody ctr;
	private VentaDTO venta;
	private String mensaje;

	public ValidadorFormaPagoVenta(CajaPeriodoControlBody ctr, VentaDTO venta) {
		this.ctr = ctr;
		this.venta = venta;
	}

	@Override
	public boolean verificarAceptar() {
		boolean valido = true;
		this.mensaje = "No se puede continuar la operación debido a: \n";

		double totalVT = venta.isMonedaLocal() ? venta.getTotalImporteGs() : venta.getTotalImporteDs();
		double totalFP = 0;

		String totalVT_ = this.ctr.m.formatoGs(totalVT);

		for (ReciboFormaPagoDTO item : venta.getFormasPago()) {
			totalFP += venta.isMonedaLocal() ? item.getMontoGs() : item.getMontoDs();
		}

		if ((Utiles.getRedondeo(totalVT) != Utiles.getRedondeo(totalFP))) {
			valido = false;
			this.mensaje += "\n - El importe de las Formas de Pago debe ser: "
					+ totalVT_ + " " +venta.getMoneda().getPos1();
		}

		return valido;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensaje;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al cancelar";
	}

}
