package com.yhaguy.gestion.reportes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Tipo;
import com.coreweb.domain.Usuario;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloGasto;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.CajaPlanillaResumen;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.util.Utiles;

@SuppressWarnings("unchecked")
public class ReportesFiltros {
	
	public static final String TODOS = "TODOS..";
	public static final String SALDO_MIG_POSITIVO = "SALDO POSITIVO";
	public static final String SALDO_MIG_NEGATIVO = "SALDO NEGATIVO";
	public static final String RENTABILIDAD_POS = "RENTABILIDAD POSITIVA..";
	public static final String RENTABILIDAD_NEG = "RENTABILIDAD NEGATIVA..";
	
	public static final String VTO_30_DIAS = "ATRASO ENTRE 01 A 30 DÍAS";
	public static final String VTO_60_DIAS = "ATRASO ENTRE 30 A 60 DÍAS";
	public static final String VTO_90_DIAS = "ATRASO ENTRE 60 A 90 DÍAS";
	public static final String VTO_91_DIAS = "ATRASO MAYOR A 90 DÍAS";
	
	public static final String RANKING_CANTIDAD = "POR CANTIDAD..";
	public static final String RANKING_IMPORTE = "POR IMPORTE..";
	
	public static final String RETENCION_RECIBIDAS = "RETENCIONES RECIBIDAS";
	public static final String RETENCION_EMITIDAS = "RETENCIONES EMITIDAS";
	
	public static final String CHEQUE_AL_DIA = "CHEQUE AL DÍA";
	public static final String CHEQUE_ADELANTADO = "CHEQUE ADELANTADO";
	public static final String EFECTIVO = "EFECTIVO";
	public static final String RETENCION = "RETENCIÓN IVA";
	public static final String DEPOSITO_BANCARIO = "DEPÓSITO BANCARIO";
	public static final String TARJETA_CREDITO = "TARJETA DE CRÉDITO";
	public static final String TARJETA_DEBITO = "TARJETA DE DÉBITO";
	public static final String DEBITO_COBRANZA_CENTRAL = "DÉBITO POR COBRANZA CENTRAL";
	public static final String RECAUDACION_CENTRAL = "RECAUDACIÓN CASA CENTRAL";
	public static final String TRANSFERENCIA_CENTRAL = "TRANSFERENCIA CASA CENTRAL";
	public static final String SALDO_FAVOR_CLIENTE = "SALDO A FAVOR GENERADO";
	public static final String SALDO_FAVOR_COBRADO = "SALDO A FAVOR COBRADO";
	
	public static final String CHEQUES_DESCONTADOS = "DESCONTADOS";
	public static final String CHEQUES_NO_DESCONTADOS = "NO DESCONTADOS";
	public static final String CHEQUES_DEPOSITADOS = "DEPOSITADOS";
	public static final String CHEQUES_NO_DEPOSITADOS = "NO DEPOSITADOS";
	
	public static final String ESTADO_CTA_CLIENTE_HABILITADO = "HABILITADO";
	public static final String ESTADO_CTA_CLIENTE_BLOQUEADO = "BLOQUEADO";
	
	public static final String REEMBOLSADO = "REEMBOLSADOS..";
	public static final String SIN_REEMBOLSO = "PENDIENTES REEMBOLSO..";
	
	public static final String COSTO_ULTIMO = "ÚLTIMO COSTO";
	public static final String COSTO_PROMEDIO = "COSTO PROMEDIO";
	
	private Date fechaDesde;
	private Date fechaHasta;
	private Date fechaDesde2;
	private Date fechaHasta2;
	private Date fechaHoy = new Date();
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	private String fecha = Utiles.getDateToString(new Date(), "dddd");
	
	private String anhoDesde;
	private String anhoHasta;
	
	private int cantidadDesde = 0;
	private int cantidadHasta = 0;
	
	private Object[] formato;
	private boolean formularioContinuo = false;
	private MyArray selectedMes;
	private String selectedAnho = Utiles.getAnhoActual();
	
	private TipoMovimiento tipoMovimiento;
	private Tipo tipo;
	private BancoChequeTercero chequeTercero;
	private Tipo bancoTercero;
	private BancoCta bancoCta;
	private Deposito deposito;
	private List<Deposito> depositos = new ArrayList<Deposito>();
	private ArticuloGasto articuloGasto;
	private String descripcionArticuloGasto = "";
	
	private CajaPeriodo cajaPlanilla;
	private CajaPlanillaResumen selectedResumen;
	private Object[] cajaPlanillaResumen; 
	private String nroPlanillaCaja = "";
	private String nroPlanillaCajaResumen = "";
	
	private Recibo recibo;
	private String numeroRecibo = "";
	
	private Articulo articulo;
	private String codigoArticulo = "";
	private String tipoSaldoMigracion = TODOS;
	private String tipoRentabilidad = TODOS;
	private boolean todos = false;
	private boolean excluirPyAutopartes = false;
	private boolean excluirIcaturbo = false;
	private boolean costoPromedio = false;
	private boolean ivaIncluido = true;
	
	private String morosidad = "";
	private String tipoRanking = "";
	private String tipoRetencion = "";
	private String formaPago_ = "";
	private String numeroChequeTercero = "";
	private String nombrebancoTercero = "";
	private String descuentoCheque = "";
	private String depositoCheque = "";
	private String reembolso = "";
	private String tipoCosto = COSTO_ULTIMO;

	// Filtros de Stock..	
	private Tipo familia = new Tipo();
	private Tipo linea = new Tipo();
	private Tipo marca = new Tipo();
	private Tipo parte = new Tipo();
	private Tipo rubro = new Tipo();
	
	private Tipo moneda = new Tipo();
	
	private SucursalApp sucursalOrigen;
	private SucursalApp sucursalDestino;
	private SucursalApp selectedSucursal;

	private boolean incluirNCR = true;
	private boolean incluirNCR_CRED = true;
	private boolean incluirVCR = true;
	private boolean incluirVCT = true;
	private boolean incluirREC = true;
	private boolean incluirCHQ_RECH = true;
	private boolean incluirPRE = true;
	
	private Funcionario vendedor;
	private String razonSocialVendedor = "";
	private Funcionario cobrador;
	private String razonSocialCobrador = "";
	private boolean todosLosVendedores = false;
	private boolean fraccionado = false;
	
	private Tipo motivoNotaCredito;
	private ArticuloListaPrecio listaPrecio;
	
	private Cliente cliente;
	private String razonSocialCliente = "";
	private boolean todosLosClientes = false;
	private List<Cliente> selectedClientes = new ArrayList<Cliente>();
	
	private Proveedor proveedor;
	private String razonSocialProveedor = "";
	
	private Proveedor proveedorExterior;
	private String razonSocialProveedorExterior = "";

	// Filtros de Tesoreria..
	private String libradoPor = "";
	private String nroComprobanteCheque = "";
	private boolean descontadoCheque = true;
	private String estadoCuentaCliente;
	
	// Filtros de Usuarios
	private boolean usuariosActivos = true;
	private boolean usuariosInactivos = true;
	private Usuario usuario;
	private String nombreUsuario = "";

	/**
	 * GET / SET
	 */	
	
	/**
	 * @return los formatos de reporte..
	 */
	public List<Object[]> getFormatos() {
		List<Object[]> out = new ArrayList<Object[]>();
		out.add(ReportesViewModel.FORMAT_PDF);
		out.add(ReportesViewModel.FORMAT_XLS);
		out.add(ReportesViewModel.FORMAT_CSV);
		return out;
	}
	
	/**
	 * @return los tipos de migracion..
	 */
	public List<String> getTiposSaldoMigracion() {
		List<String> out = new ArrayList<String>();
		out.add(SALDO_MIG_POSITIVO);
		out.add(SALDO_MIG_NEGATIVO);
		out.add(TODOS);
		return out;
	}
	
	/**
	 * @return los tipos de rentabilidad..
	 */
	public List<String> getTiposRentabilidad() {
		List<String> out = new ArrayList<String>();
		out.add(RENTABILIDAD_POS);
		out.add(RENTABILIDAD_NEG);
		out.add(TODOS);
		return out;
	}
	
	/**
	 * @return los tipos de reembolso..
	 */
	public List<String> getTiposReembolso() {
		List<String> out = new ArrayList<String>();
		out.add(REEMBOLSADO);
		out.add(SIN_REEMBOLSO);
		return out;
	}
	
	/**
	 * @return las morosidades..
	 */
	public List<String> getMorosidades() {
		List<String> out = new ArrayList<String>();
		out.add(VTO_30_DIAS);
		out.add(VTO_60_DIAS);
		out.add(VTO_90_DIAS);
		out.add(VTO_91_DIAS);
		return out;
	}
	
	/**
	 * @return los tipos de ranking de ventas..
	 */
	public List<String> getTiposRanking() {
		List<String> out = new ArrayList<String>();
		out.add(RANKING_CANTIDAD);
		out.add(RANKING_IMPORTE);
		return out;
	}
	
	/**
	 * @return los tipos de retencion..
	 */
	public List<String> getTiposRetenciones() {
		List<String> out = new ArrayList<String>();
		out.add(RETENCION_EMITIDAS);
		out.add(RETENCION_RECIBIDAS);
		return out;
	}
	
	/**
	 * @return los descuentos de cheques..
	 */
	public List<String> getDescuentoCheques() {
		List<String> out = new ArrayList<String>();
		out.add(CHEQUES_DESCONTADOS);
		out.add(CHEQUES_NO_DESCONTADOS);
		return out;
	}
	
	/**
	 * @return los depositos de cheques..
	 */
	public List<String> getDepositoCheques() {
		List<String> out = new ArrayList<String>();
		out.add(CHEQUES_DEPOSITADOS);
		out.add(CHEQUES_NO_DEPOSITADOS);
		return out;
	}
	
	/**
	 * @return los estados de las cuentas de clientes..
	 */
	public List<String> getEstadosCuentaCliente() {
		List<String> out = new ArrayList<String>();
		out.add(ESTADO_CTA_CLIENTE_HABILITADO);
		out.add(ESTADO_CTA_CLIENTE_BLOQUEADO);
		return out;
	}
	
	/**
	 * @return los tipos de costo..
	 */
	public List<String> getTiposDeCosto() {
		List<String> out = new ArrayList<String>();
		out.add(COSTO_ULTIMO);
		out.add(COSTO_PROMEDIO);
		return out;
	}
	
	/**
	 * @return los meses..
	 */
	public List<MyArray> getMeses() {
		return Utiles.getMeses();
	}
	
	/**
	 * @return las sucursales..
	 */
	public List<SucursalApp> getSucursales() {
		List<SucursalApp> out = new ArrayList<SucursalApp>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			out = rr.getObjects(SucursalApp.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * @return los motivos de notas de credito..
	 */
	public List<Tipo> getMotivosNotaCredito() {
		List<Tipo> out = new ArrayList<Tipo>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			out = rr.getTipos(Configuracion.ID_TIPO_NOTA_CREDITO_MOTIVOS);
			Tipo todos = new Tipo();
			todos.setDescripcion("TODOS");
			todos.setSigla("TODOS");
			out.add(todos);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;
	}
	
	/**
	 * @return las monedas..
	 */
	public List<Tipo> getMonedas() {
		List<Tipo> out = new ArrayList<Tipo>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			out = rr.getTipos(Configuracion.ID_TIPO_MONEDA);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;
	}
	
	/**
	 * @return las marcas..
	 */
	public List<Tipo> getMarcas() {
		List<Tipo> out = new ArrayList<Tipo>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			out = rr.getTipos(Configuracion.ID_TIPO_ARTICULO_MARCA);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;
	}
	
	/**
	 * @return las familias..
	 */
	public List<Tipo> getFamilias() {
		List<Tipo> out = new ArrayList<Tipo>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			out = rr.getTipos(Configuracion.ID_TIPO_ARTICULO_FAMILIA);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;
	}
	
	/**
	 * @return los bancos..
	 */
	public List<Tipo> getBancosTerceros() {
		List<Tipo> out = new ArrayList<Tipo>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			out = rr.getTipos(Configuracion.ID_TIPO_BANCOS_TERCEROS);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;
	}
	
	/**
	 * @return las cuentas de banco..
	 */
	public List<BancoCta> getBancos() {
		List<BancoCta> out = new ArrayList<BancoCta>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			out = rr.getBancosCta();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;
	}
	
	/**
	 * @return los rubros..
	 */
	public List<Tipo> getRubros() {
		List<Tipo> out = new ArrayList<Tipo>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			out = rr.getTipos(Configuracion.ID_TIPO_RUBRO_EMPRESAS);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;
	}
	
	/**
	 * @return las formas de pago..
	 */
	public List<String> getFormasPago_() {
		List<String> out = new ArrayList<String>();
		out.add(EFECTIVO);
		out.add(RETENCION);
		out.add(CHEQUE_AL_DIA);
		out.add(CHEQUE_ADELANTADO);
		out.add(DEPOSITO_BANCARIO);
		out.add(TARJETA_CREDITO);
		out.add(TARJETA_DEBITO);
		out.add(DEBITO_COBRANZA_CENTRAL);
		out.add(RECAUDACION_CENTRAL);
		out.add(TRANSFERENCIA_CENTRAL);
		out.add(SALDO_FAVOR_CLIENTE);
		out.add(SALDO_FAVOR_COBRADO);
		return out;
	}
	
	/**
	 * @return los anhos..
	 */
	public List<String> getAnhos() {
		List<String> out = new ArrayList<String>();
		out.add(Utiles.ANHO_2016);
		out.add(Utiles.ANHO_2017);
		out.add(Utiles.ANHO_2018);
		out.add(Utiles.ANHO_2019);
		out.add(Utiles.ANHO_2020);
		return out;
	}
	
	/**
	 * @return los tipos de ajuste de Stock..
	 */
	public List<TipoMovimiento> getTiposAjustes() {
		List<TipoMovimiento> out = new ArrayList<TipoMovimiento>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			TipoMovimiento positivo = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
			TipoMovimiento negativo = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
			out.add(positivo);
			out.add(negativo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * @return los depositos segun sucursal..
	 */
	public List<Deposito> getDepositos(long idSucursal) {
		List<Deposito> out = new ArrayList<Deposito>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			out = rr.getDepositosPorSucursal(idSucursal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * @return las listas de precio..
	 */
	public List<ArticuloListaPrecio> getListasDePrecio() {
		List<ArticuloListaPrecio> out = new ArrayList<ArticuloListaPrecio>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			out = rr.getListasDePrecio();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;
	}
	
	/**
	 * @return la fecha de inicio de operaciones..
	 */
	public Date getFechaInicioOperaciones() throws Exception {
		return Utiles.getFecha("01-01-2016 00:00:00");
	}
	
	@DependsOn("razonSocialVendedor")
	public List<Funcionario> getVendedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getFuncionarios(this.razonSocialVendedor);
	}
	
	/**
	 * @return los cobradores..
	 */
	public List<Funcionario> getTeleCobradores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTeleCobradores();
	}
	
	@DependsOn("razonSocialCliente")
	public List<Cliente> getClientes() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getClientes(this.razonSocialCliente);
	}
	
	@DependsOn("razonSocialProveedor")
	public List<Proveedor> getProveedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getProveedores(this.razonSocialProveedor);
	}
	
	@DependsOn("razonSocialProveedorExterior")
	public List<Proveedor> getProveedoresExterior() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getProveedoresExterior(this.razonSocialProveedorExterior);
	}
	
	@DependsOn("codigoArticulo")
	public List<Articulo> getArticulos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticulos(this.codigoArticulo, 30);
	}
	
	@DependsOn("descripcionArticuloGasto")
	public List<ArticuloGasto> getArticulosGastos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticulosGastos(this.descripcionArticuloGasto, 30);
	}
	
	@DependsOn("numeroRecibo")
	public List<Recibo> getRecibos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getRecibos(this.numeroRecibo);
	}
	
	@DependsOn("nroPlanillaCaja")
	public List<CajaPeriodo> getCajaPlanillas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getCajaPlanillas(this.nroPlanillaCaja);
	}
	
	@DependsOn({"nroPlanillaCajaResumen", "filterFechaDD", "filterFechaMM", "filterFechaAA", "nroPlanillaCaja" })
	public List<Object[]> getCajaPlanillaResumenes() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getCajaPlanillaResumenes_(this.getFilterFecha(), this.nroPlanillaCajaResumen, this.nroPlanillaCaja);
	}
	
	/**
	 * seleccion del resumen..
	 */
	public void selectResumen() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance(); 
		long id = (long) this.cajaPlanillaResumen[3];
		this.selectedResumen = (CajaPlanillaResumen) rr.getObject(CajaPlanillaResumen.class.getName(), id);
	}
	
	@DependsOn("numeroChequeTercero")
	public List<BancoChequeTercero> getChequesTercero() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getChequesTercero(this.numeroChequeTercero);
	}
	
	public List<BancoCta> getBancoCtas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(BancoCta.class.getName());
	}
	
	@DependsOn("nombreUsuario")
	public List<Usuario> getUsuarios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getUsuarios("", this.nombreUsuario);
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	@SuppressWarnings("deprecation")
	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
		if (fechaDesde != null) {
			this.fechaDesde.setHours(00);
			this.fechaDesde.setMinutes(00);
			this.fechaDesde.setSeconds(00);
		}
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	@SuppressWarnings("deprecation")
	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;

		if (fechaHasta != null) {
			this.fechaHasta.setHours(23);
			this.fechaHasta.setMinutes(59);
			this.fechaHasta.setSeconds(59);
		}
	}
	
	/**
	 * @return el filtro de fecha..
	 */
	private String getFilterFecha() {
		if (this.filterFechaAA.isEmpty() && this.filterFechaDD.isEmpty() && this.filterFechaMM.isEmpty())
			return "";
		if (this.filterFechaAA.isEmpty())
			return this.filterFechaMM + "-" + this.filterFechaDD;
		if (this.filterFechaMM.isEmpty())
			return this.filterFechaAA;
		if (this.filterFechaMM.isEmpty() && this.filterFechaDD.isEmpty())
			return this.filterFechaAA;
		return this.filterFechaAA + "-" + this.filterFechaMM + "-" + this.filterFechaDD;
	}
	
	public String getMonedaDs() {
		return Configuracion.ID_TIPO_MONEDA_DOLAR;
	}
	
	public String getMonedaGs() {
		return Configuracion.ID_TIPO_MONEDA_GUARANI;
	}

	public String getLibradoPor() {
		return libradoPor;
	}

	public void setLibradoPor(String libradoPor) {
		this.libradoPor = libradoPor;
	}

	public String getNroComprobanteCheque() {
		return nroComprobanteCheque;
	}

	public void setNroComprobanteCheque(String nroComprobanteCheque) {
		this.nroComprobanteCheque = nroComprobanteCheque;
	}

	public boolean isDescontadoCheque() {
		return descontadoCheque;
	}

	public void setDescontadoCheque(boolean descontadoCheque) {
		this.descontadoCheque = descontadoCheque;
	}

	public boolean isIncluirNCR() {
		return incluirNCR;
	}

	public void setIncluirNCR(boolean incluirNCR) {
		this.incluirNCR = incluirNCR;
	}

	public boolean isIncluirVCR() {
		return incluirVCR;
	}

	public void setIncluirVCR(boolean incluirVCR) {
		this.incluirVCR = incluirVCR;
	}

	public boolean isIncluirVCT() {
		return incluirVCT;
	}

	public void setIncluirVCT(boolean incluirVCT) {
		this.incluirVCT = incluirVCT;
	}

	public Funcionario getVendedor() {
		return vendedor;
	}

	public void setVendedor(Funcionario vendedor) {
		this.vendedor = vendedor;
	}

	public String getRazonSocialVendedor() {
		return razonSocialVendedor;
	}

	public void setRazonSocialVendedor(String razonSocialVendedor) {
		this.razonSocialVendedor = razonSocialVendedor;
	}

	public String getRazonSocialCliente() {
		return razonSocialCliente;
	}

	public void setRazonSocialCliente(String razonSocialCliente) {
		this.razonSocialCliente = razonSocialCliente;
	}

	public boolean isTodosLosClientes() {
		return todosLosClientes;
	}

	public void setTodosLosClientes(boolean todosLosClientes) {
		this.todosLosClientes = todosLosClientes;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Object[] getFormato() {
		return formato;
	}

	public void setFormato(Object[] formato) {
		this.formato = formato;
	}

	public boolean isFormularioContinuo() {
		return formularioContinuo;
	}

	public void setFormularioContinuo(boolean formularioContinuo) {
		this.formularioContinuo = formularioContinuo;
	}

	public boolean isIncluirNCR_CRED() {
		return incluirNCR_CRED;
	}

	public void setIncluirNCR_CRED(boolean incluirNCR_CRED) {
		this.incluirNCR_CRED = incluirNCR_CRED;
	}

	public Tipo getMotivoNotaCredito() {
		return motivoNotaCredito;
	}

	public void setMotivoNotaCredito(Tipo motivoNotaCredito) {
		this.motivoNotaCredito = motivoNotaCredito;
	}

	public SucursalApp getSucursalOrigen() {
		return sucursalOrigen;
	}

	public void setSucursalOrigen(SucursalApp sucursalOrigen) {
		this.sucursalOrigen = sucursalOrigen;
	}

	public SucursalApp getSucursalDestino() {
		return sucursalDestino;
	}

	public void setSucursalDestino(SucursalApp sucursalDestino) {
		this.sucursalDestino = sucursalDestino;
	}

	public boolean isTodosLosVendedores() {
		return todosLosVendedores;
	}

	public void setTodosLosVendedores(boolean todosLosVendedores) {
		this.todosLosVendedores = todosLosVendedores;
	}

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public Deposito getDeposito() {
		return deposito;
	}

	public void setDeposito(Deposito deposito) {
		this.deposito = deposito;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public String getTipoSaldoMigracion() {
		return tipoSaldoMigracion;
	}

	public void setTipoSaldoMigracion(String tipoSaldoMigracion) {
		this.tipoSaldoMigracion = tipoSaldoMigracion;
	}

	public boolean isTodos() {
		return todos;
	}

	public void setTodos(boolean todos) {
		this.todos = todos;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public String getCodigoArticulo() {
		return codigoArticulo;
	}

	public void setCodigoArticulo(String codigoArticulo) {
		this.codigoArticulo = codigoArticulo;
	}

	public Tipo getFamilia() {
		return familia;
	}

	public void setFamilia(Tipo familia) {
		this.familia = familia;
	}

	public Tipo getLinea() {
		return linea;
	}

	public void setLinea(Tipo linea) {
		this.linea = linea;
	}

	public Tipo getMarca() {
		return marca;
	}

	public void setMarca(Tipo marca) {
		this.marca = marca;
	}

	public Tipo getParte() {
		return parte;
	}

	public void setParte(Tipo parte) {
		this.parte = parte;
	}

	public String getNroPlanillaCaja() {
		return nroPlanillaCaja;
	}

	public void setNroPlanillaCaja(String nroPlanillaCaja) {
		this.nroPlanillaCaja = nroPlanillaCaja;
	}

	public CajaPeriodo getCajaPlanilla() {
		return cajaPlanilla;
	}

	public void setCajaPlanilla(CajaPeriodo cajaPlanilla) {
		this.cajaPlanilla = cajaPlanilla;
	}

	public String getMorosidad() {
		return morosidad;
	}

	public void setMorosidad(String morosidad) {
		this.morosidad = morosidad;
	}

	public String getTipoRanking() {
		return tipoRanking;
	}

	public void setTipoRanking(String tipoRanking) {
		this.tipoRanking = tipoRanking;
	}

	public String getTipoRentabilidad() {
		return tipoRentabilidad;
	}

	public void setTipoRentabilidad(String tipoRentabilidad) {
		this.tipoRentabilidad = tipoRentabilidad;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public String getRazonSocialProveedor() {
		return razonSocialProveedor;
	}

	public void setRazonSocialProveedor(String razonSocialProveedor) {
		this.razonSocialProveedor = razonSocialProveedor;
	}

	public String getTipoRetencion() {
		return tipoRetencion;
	}

	public void setTipoRetencion(String tipoRetencion) {
		this.tipoRetencion = tipoRetencion;
	}

	public SucursalApp getSelectedSucursal() {
		return selectedSucursal;
	}

	public void setSelectedSucursal(SucursalApp selectedSucursal) {
		this.selectedSucursal = selectedSucursal;
	}

	public String getFormaPago_() {
		return formaPago_;
	}

	public void setFormaPago_(String formaPago_) {
		this.formaPago_ = formaPago_;
	}

	public BancoChequeTercero getChequeTercero() {
		return chequeTercero;
	}

	public void setChequeTercero(BancoChequeTercero chequeTercero) {
		this.chequeTercero = chequeTercero;
	}

	public String getNumeroChequeTercero() {
		return numeroChequeTercero;
	}

	public void setNumeroChequeTercero(String numeroChequeTercero) {
		this.numeroChequeTercero = numeroChequeTercero;
	}

	public Tipo getBancoTercero() {
		return bancoTercero;
	}

	public void setBancoTercero(Tipo bancoTercero) {
		this.bancoTercero = bancoTercero;
	}

	public String getNombrebancoTercero() {
		return nombrebancoTercero;
	}

	public void setNombrebancoTercero(String nombrebancoTercero) {
		this.nombrebancoTercero = nombrebancoTercero;
	}

	public boolean isUsuariosActivos() {
		return usuariosActivos;
	}

	public void setUsuariosActivos(boolean usuariosActivos) {
		this.usuariosActivos = usuariosActivos;
	}

	public boolean isUsuariosInactivos() {
		return usuariosInactivos;
	}

	public void setUsuariosInactivos(boolean usuariosInactivos) {
		this.usuariosInactivos = usuariosInactivos;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getDescuentoCheque() {
		return descuentoCheque;
	}

	public void setDescuentoCheque(String descuentoCheque) {
		this.descuentoCheque = descuentoCheque;
	}

	public String getDepositoCheque() {
		return depositoCheque;
	}

	public void setDepositoCheque(String depositoCheque) {
		this.depositoCheque = depositoCheque;
	}

	public Proveedor getProveedorExterior() {
		return proveedorExterior;
	}

	public void setProveedorExterior(Proveedor proveedorExterior) {
		this.proveedorExterior = proveedorExterior;
	}

	public String getRazonSocialProveedorExterior() {
		return razonSocialProveedorExterior;
	}

	public void setRazonSocialProveedorExterior(String razonSocialProveedorExterior) {
		this.razonSocialProveedorExterior = razonSocialProveedorExterior;
	}

	public boolean isExcluirPyAutopartes() {
		return excluirPyAutopartes;
	}

	public void setExcluirPyAutopartes(boolean excluirPyAutopartes) {
		this.excluirPyAutopartes = excluirPyAutopartes;
	}

	public boolean isExcluirIcaturbo() {
		return excluirIcaturbo;
	}

	public void setExcluirIcaturbo(boolean excluirIcaturbo) {
		this.excluirIcaturbo = excluirIcaturbo;
	}

	public boolean isCostoPromedio() {
		return costoPromedio;
	}

	public void setCostoPromedio(boolean costoPromedio) {
		this.costoPromedio = costoPromedio;
	}

	public String getEstadoCuentaCliente() {
		return estadoCuentaCliente;
	}

	public void setEstadoCuentaCliente(String estadoCuentaCliente) {
		this.estadoCuentaCliente = estadoCuentaCliente;
	}

	public BancoCta getBancoCta() {
		return bancoCta;
	}

	public void setBancoCta(BancoCta bancoCta) {
		this.bancoCta = bancoCta;
	}

	public Funcionario getCobrador() {
		return cobrador;
	}

	public void setCobrador(Funcionario cobrador) {
		this.cobrador = cobrador;
	}

	public String getRazonSocialCobrador() {
		return razonSocialCobrador;
	}

	public void setRazonSocialCobrador(String razonSocialCobrador) {
		this.razonSocialCobrador = razonSocialCobrador;
	}

	public Recibo getRecibo() {
		return recibo;
	}

	public void setRecibo(Recibo recibo) {
		this.recibo = recibo;
	}

	public String getNumeroRecibo() {
		return numeroRecibo;
	}

	public void setNumeroRecibo(String numeroRecibo) {
		this.numeroRecibo = numeroRecibo;
	}

	public List<Deposito> getDepositos() {
		return depositos;
	}

	public void setDepositos(List<Deposito> depositos) {
		this.depositos = depositos;
	}

	public boolean isIvaIncluido() {
		return ivaIncluido;
	}

	public void setIvaIncluido(boolean ivaIncluido) {
		this.ivaIncluido = ivaIncluido;
	}

	public boolean isIncluirREC() {
		return incluirREC;
	}

	public void setIncluirREC(boolean incluirREC) {
		this.incluirREC = incluirREC;
	}

	public Tipo getRubro() {
		return rubro;
	}

	public void setRubro(Tipo rubro) {
		this.rubro = rubro;
	}

	public ArticuloListaPrecio getListaPrecio() {
		return listaPrecio;
	}

	public void setListaPrecio(ArticuloListaPrecio listaPrecio) {
		this.listaPrecio = listaPrecio;
	}

	public int getCantidadDesde() {
		return cantidadDesde;
	}

	public void setCantidadDesde(int cantidadDesde) {
		this.cantidadDesde = cantidadDesde;
	}

	public int getCantidadHasta() {
		return cantidadHasta;
	}

	public void setCantidadHasta(int cantidadHasta) {
		this.cantidadHasta = cantidadHasta;
	}

	public boolean isFraccionado() {
		return fraccionado;
	}

	public void setFraccionado(boolean fraccionado) {
		this.fraccionado = fraccionado;
	}

	public String getAnhoDesde() {
		return anhoDesde;
	}

	public void setAnhoDesde(String anhoDesde) {
		this.anhoDesde = anhoDesde;
	}

	public String getAnhoHasta() {
		return anhoHasta;
	}

	public void setAnhoHasta(String anhoHasta) {
		this.anhoHasta = anhoHasta;
	}

	public List<Cliente> getSelectedClientes() {
		return selectedClientes;
	}

	public void setSelectedClientes(List<Cliente> selectedClientes) {
		this.selectedClientes = selectedClientes;
	}

	public MyArray getSelectedMes() {
		return selectedMes;
	}

	public void setSelectedMes(MyArray selectedMes) {
		this.selectedMes = selectedMes;
	}

	public String getSelectedAnho() {
		return selectedAnho;
	}

	public void setSelectedAnho(String selectedAnho) {
		this.selectedAnho = selectedAnho;
	}

	public ArticuloGasto getArticuloGasto() {
		return articuloGasto;
	}

	public void setArticuloGasto(ArticuloGasto articuloGasto) {
		this.articuloGasto = articuloGasto;
	}

	public String getDescripcionArticuloGasto() {
		return descripcionArticuloGasto;
	}

	public void setDescripcionArticuloGasto(String descripcionArticuloGasto) {
		this.descripcionArticuloGasto = descripcionArticuloGasto;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public String getNroPlanillaCajaResumen() {
		return nroPlanillaCajaResumen;
	}

	public void setNroPlanillaCajaResumen(String nroPlanillaCajaResumen) {
		this.nroPlanillaCajaResumen = nroPlanillaCajaResumen;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getFilterFechaDD() {
		return filterFechaDD;
	}

	public void setFilterFechaDD(String filterFechaDD) {
		this.filterFechaDD = filterFechaDD;
	}

	public String getFilterFechaMM() {
		return filterFechaMM;
	}

	public void setFilterFechaMM(String filterFechaMM) {
		this.filterFechaMM = filterFechaMM;
	}

	public String getFilterFechaAA() {
		return filterFechaAA;
	}

	public void setFilterFechaAA(String filterFechaAA) {
		this.filterFechaAA = filterFechaAA;
	}

	public Object[] getCajaPlanillaResumen() {
		return cajaPlanillaResumen;
	}

	public void setCajaPlanillaResumen(Object[] cajaPlanillaResumen) {
		this.cajaPlanillaResumen = cajaPlanillaResumen;
	}

	public CajaPlanillaResumen getSelectedResumen() {
		return selectedResumen;
	}

	public void setSelectedResumen(CajaPlanillaResumen selectedResumen) {
		this.selectedResumen = selectedResumen;
	}

	public Date getFechaHoy() {
		return fechaHoy;
	}

	public void setFechaHoy(Date fechaHoy) {
		this.fechaHoy = fechaHoy;
	}

	public String getReembolso() {
		return reembolso;
	}

	public void setReembolso(String reembolso) {
		this.reembolso = reembolso;
	}

	public String getTipoCosto() {
		return tipoCosto;
	}

	public void setTipoCosto(String tipoCosto) {
		this.tipoCosto = tipoCosto;
	}

	public Date getFechaDesde2() {
		return fechaDesde2;
	}

	public void setFechaDesde2(Date fechaDesde2) {
		this.fechaDesde2 = fechaDesde2;
	}

	public Date getFechaHasta2() {
		return fechaHasta2;
	}

	public void setFechaHasta2(Date fechaHasta2) {
		this.fechaHasta2 = fechaHasta2;
	}

	public boolean isIncluirCHQ_RECH() {
		return incluirCHQ_RECH;
	}

	public void setIncluirCHQ_RECH(boolean incluirCHQ_RECH) {
		this.incluirCHQ_RECH = incluirCHQ_RECH;
	}

	public boolean isIncluirPRE() {
		return incluirPRE;
	}

	public void setIncluirPRE(boolean incluirPRE) {
		this.incluirPRE = incluirPRE;
	}
}
