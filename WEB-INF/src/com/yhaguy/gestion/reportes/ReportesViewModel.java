package com.yhaguy.gestion.reportes;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.GroupComparator;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Window;

import com.coreweb.Config;
import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Perfil;
import com.coreweb.domain.Tipo;
import com.coreweb.domain.Usuario;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.extras.reporte.DatosReporte;
import com.coreweb.login.LoginUsuario;
import com.coreweb.login.LoginUsuarioDTO;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.AjusteCtaCte;
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.AjusteStockDetalle;
import com.yhaguy.domain.AjusteValorizado;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.ArticuloGasto;
import com.yhaguy.domain.ArticuloHistorialMigracion;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.ArticuloListaPrecioDetalle;
import com.yhaguy.domain.ArticuloMarca;
import com.yhaguy.domain.ArticuloPrecioMinimo;
import com.yhaguy.domain.ArticuloPresentacion;
import com.yhaguy.domain.BancoBoletaDeposito;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.BancoDescuentoCheque;
import com.yhaguy.domain.BancoExtracto;
import com.yhaguy.domain.BancoExtractoDetalle;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.CajaPlanillaResumen;
import com.yhaguy.domain.CajaReposicion;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.CompraLocalFacturaDetalle;
import com.yhaguy.domain.CondicionPago;
import com.yhaguy.domain.ContableAsiento;
import com.yhaguy.domain.ContableAsientoDetalle;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.CtaCteEmpresaMovimiento_2016;
import com.yhaguy.domain.CtaCteLineaCredito;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.EmpresaCartera;
import com.yhaguy.domain.EmpresaRubro;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.GastoDetalle;
import com.yhaguy.domain.HistoricoBloqueoClientes;
import com.yhaguy.domain.HistoricoComisiones;
import com.yhaguy.domain.HistoricoMovimientoArticulo;
import com.yhaguy.domain.HistoricoMovimientos;
import com.yhaguy.domain.ImportacionFactura;
import com.yhaguy.domain.ImportacionFacturaDetalle;
import com.yhaguy.domain.ImportacionPedidoCompra;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.NotaDebito;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RRHHMarcaciones;
import com.yhaguy.domain.RecaudacionCentral;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Reparto;
import com.yhaguy.domain.RepartoDetalle;
import com.yhaguy.domain.RepartoEntrega;
import com.yhaguy.domain.Reporte;
import com.yhaguy.domain.ReporteFavoritos;
import com.yhaguy.domain.ServicioTecnico;
import com.yhaguy.domain.ServicioTecnicoDetalle;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.domain.VentaPerdida;
import com.yhaguy.domain.VentaPromo1;
import com.yhaguy.gestion.articulos.buscador.BuscadorArticulosViewModel;
import com.yhaguy.gestion.bancos.conciliacion.BeanConciliacion;
import com.yhaguy.gestion.caja.periodo.CajaPeriodoResumenDataSource;
import com.yhaguy.gestion.caja.recibos.BeanFormaPago;
import com.yhaguy.gestion.caja.recibos.BeanRecibo;
import com.yhaguy.gestion.caja.resumen.ResumenDataSource;
import com.yhaguy.gestion.compras.importacion.BeanImportacion;
import com.yhaguy.gestion.comun.ControlArticuloCosto;
import com.yhaguy.gestion.comun.ControlArticuloStock;
import com.yhaguy.gestion.contabilidad.BeanLibroVenta;
import com.yhaguy.gestion.empresa.ClienteBean;
import com.yhaguy.gestion.empresa.ctacte.BeanCtaCteEmpresa;
import com.yhaguy.gestion.empresa.ctacte.ControlCtaCteEmpresa;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaMovimientoDTO;
import com.yhaguy.gestion.transferencia.BeanTransferencia;
import com.yhaguy.gestion.venta.ReporteVentasGenerico;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.inicio.AssemblerAcceso;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ReportesViewModel extends SimpleViewModel {

	static final String FOLDER = "/yhaguy/gestion/reportes/";
	static final String FOLDER_MOBILE_PARAMS = "/yhaguy/mobile/reportes/parametros";
	static final String ZUL_PARAMETROS = FOLDER + "reportesParametros.zul";

	static final int ID_TESORERIA = 1;
	static final int ID_COMPRAS = 2;
	static final int ID_VENTAS = 3;
	static final int ID_STOCK = 4;
	static final int ID_LOGISTICA = 5;
	static final int ID_CONTABILIDAD = 6;
	static final int ID_RRHH = 7;
	static final int ID_SISTEMA = 8;
	static final int ID_FAVORITOS = 9;
	static final int ID_SERVICIO_TECNICO = 10;

	static final MyArray TESORERIA = new MyArray("Tesorería", ID_TESORERIA, "z-icon-briefcase");
	static final MyArray COMPRAS = new MyArray("Abastecimiento", ID_COMPRAS, "z-icon-shopping-cart");
	static final MyArray VENTAS = new MyArray("Ventas", ID_VENTAS, "z-icon-tags");
	static final MyArray STOCK = new MyArray("Stock", ID_STOCK, "z-icon-archive");
	static final MyArray LOGISTICA = new MyArray("Logística", ID_LOGISTICA, "z-icon-truck");
	static final MyArray SERVICIOS_TECNICOS = new MyArray("Servicio Técnico", ID_SERVICIO_TECNICO, "z-icon-wrench");
	static final MyArray CONTABILIDAD = new MyArray("Contabilidad", ID_CONTABILIDAD, "z-icon-bar-chart-o");
	static final MyArray RRHH = new MyArray("Recursos Humanos", ID_RRHH, "z-icon-user");
	static final MyArray SISTEMA = new MyArray("Sistema", ID_SISTEMA, "z-icon-cog");
	static final MyArray FAVORITOS = new MyArray("Favoritos", ID_FAVORITOS, "z-icon-star");
	
	static final String GRUPO_TESORERIA_CAJAS = " CAJAS";
	static final String GRUPO_TESORERIA_BANCOS = " BANCOS";
	static final String GRUPO_TESORERIA_CLIENTES = " CLIENTES";
	static final String GRUPO_TESORERIA_PROVEEDORES = " PROVEEDORES";
	static final String GRUPO_TESORERIA_PAGOS = " PAGOS";
	static final String GRUPO_TESORERIA_RECIBOS = " RECIBOS";
	
	static final String GRUPO_COMPRAS_COMPRAS = " ABASTECIMIENTO";
	
	static final String GRUPO_VENTAS_UTILIDAD = " UTILIDAD";
	static final String GRUPO_VENTAS_VENDEDOR = " VENDEDORES";
	static final String GRUPO_VENTAS_PROVEEDOR = " PROVEEDORES";
	static final String GRUPO_VENTAS_CLIENTES = " CLIENTES";
	static final String GRUPO_VENTAS_ARTICULOS = " ARTICULOS";
	static final String GRUPO_VENTAS_AUDITORIA = " AUDITORIA";
	static final String GRUPO_VENTAS_NOTACREDITO = " NOTAS CREDITO";
	
	static final String GRUPO_STOCK_ARTICULOS = " ARTICULOS";	
	static final String GRUPO_LOGISTICA_LOGISTICA = " LOGISTICA";
	static final String GRUPO_SERVICIO_TECNICO = " SERVICIO-TECNICO";
	static final String GRUPO_CONTABILIDAD_CONTABILIDAD = " CONTABILIDAD";
	static final String GRUPO_CONTABILIDAD_CIERRE_PERIODO = " CIERRE-PERIODO";
	static final String GRUPO_RRHH_RRHH = " RR.HH";
	static final String GRUPO_SISTEMA_SISTEMA = " SISTEMA";
	static final String GRUPO_FAVORITOS_FAVORITOS = " FAVORITOS";

	private MyArray selectedItem = TESORERIA;
	private MyArray selectedReporte;
	private String selectedGrupo = GRUPO_TESORERIA_CAJAS;
	
	private String filterDescripcion = "";
	private String filterCodigo = "";
	
	private ReportesFiltros filtro = new ReportesFiltros();
	private List<MyArray> reportes = new ArrayList<MyArray>();
	private Map<String, String> favoritos = new HashMap<String, String>();

	private Window win;
	
	private String user = "";
	private String pass = "";

	@Init(superclass = true)
	public void init() {
		try {							
			this.filtro.setFilterFechaMM("" + Utiles.getNumeroMesCorriente());
			this.filtro.setFilterFechaAA(Utiles.getAnhoActual());
			if (this.filtro.getFilterFechaMM().length() == 1) {
				this.filtro.setFilterFechaMM("0" + this.filtro.getFilterFechaMM());
			}
			this.filtro.setFecha(Utiles.getDateToString(Utiles.agregarDias(new Date(), -1), "yyyy-MM-dd"));
			
			this.cargarFavoritos();
			this.cargarReportes();
			
			if (this.getAtributoSession("COD_REPORTE") != null) {
				this.filterCodigo = (String) this.getAtributoSession("COD_REPORTE");
				this.selectedReporte = this.getReportesSistema_().get(0);
				this.selectedItem = CONTABILIDAD;
				this.seleccionarModulo((int) this.selectedReporte.getPos3());
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	public UtilDTO getUtilDto() {
		return (UtilDTO) this.getDtoUtil();
	}

	/**
	 * COMANDOS..
	 */

	@Command
	@NotifyChange("*")
	public void selectModulo() {
		this.selectedReporte = null;
		int modulo = (int) this.selectedItem.getPos2();
		this.seleccionarModulo(modulo);
	}
	
	@Command
	@NotifyChange("*")
	public void imprimir() throws Exception {
		this.imprimir_();
	}
	
	@Command
	@NotifyChange("*")
	public void imprimirMobile() throws Exception {
		this.imprimirMobile_();
	}
	
	@GlobalCommand
	@NotifyChange("*")
	public void imprimirReporte() throws Exception {
		this.imprimirReporte_();
	}
	
	@Command
	@NotifyChange("*")
	public void excluirCliente(@BindingParam("comp") Bandbox comp) {
		this.filtro.getSelectedClientes().add(this.filtro.getCliente());
		this.filtro.setCliente(null);
		this.filtro.setRazonSocialCliente("");
		comp.close();
	}

	@Command
	@NotifyChange("selectedGrupo")
	public void selectGrupo(@BindingParam("grupo") String grupo) {
		this.selectedGrupo = grupo;
	}

	@Command
	public void loguearse(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2) throws Exception {
		this.loguearse_(comp1, comp2);
	}
	
	@Command
	public void setFavoritos(@BindingParam("item") MyArray item) 
		throws Exception {
		this.setFavoritos_(item);
		BindUtils.postNotifyChange(null, null, item, "*");
	}
	
	/***********************************************/

	/****************** FUNCIONES ******************/

	/**
	 * levanta los reportes de la bd..
	 */
	private void cargarReportes() {
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			List<Reporte> reps = rr.getReportes();
			for (Reporte rep : reps) {
				MyArray m = new MyArray();
				m.setId(rep.getId());
				m.setPos1(rep.getCodigo());
				m.setPos2(rep.getDescripcion().toUpperCase());
				m.setPos3(rep.getModulo());
				m.setPos4(rep.getAuxi());
				m.setPos5(this.favoritos.get(rep.getCodigo()) != null);
				this.reportes.add(m);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * levanta los favoritos de la bd..
	 */
	private void cargarFavoritos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ReporteFavoritos> favs = rr.getReporteFavoritos(this.getLoginNombre());
		for (ReporteFavoritos fav : favs) {
			this.favoritos.put(fav.getCodigoReporte(), fav.getCodigoReporte());
		}
	}
	
	/**
	 * autentica el usuario..app mobile
	 */
	private void loguearse_(Component comp1, Component comp2) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String clave = m.encriptar(this.pass, true);
		if (rr.getUsuario(this.user, clave) != null) {
			LoginUsuario lu = new LoginUsuario();
			LoginUsuarioDTO uDto = lu.log(this.user, this.pass);
			this.setAtributoSession(Config.LOGEADO, uDto.isLogeado());
			this.setAtributoSession(Config.USUARIO, uDto);
			this.setUs(uDto);
			comp1.setVisible(false);
			comp2.setVisible(true);
		} else {
			Clients.showNotification("USUARIO Y/O CLAVE INCORRECTA..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	/**
	 * seleccion de modulo..
	 */
	private void seleccionarModulo(int modulo) {
		switch (modulo) {

		case ID_TESORERIA:
			this.selectedGrupo = GRUPO_TESORERIA_CAJAS;
			break;

		case ID_COMPRAS:
			this.selectedGrupo = GRUPO_COMPRAS_COMPRAS;
			break;

		case ID_VENTAS:
			this.selectedGrupo = GRUPO_VENTAS_UTILIDAD;
			break;

		case ID_STOCK:
			this.selectedGrupo = GRUPO_STOCK_ARTICULOS;
			break;

		case ID_LOGISTICA:
			this.selectedGrupo = GRUPO_LOGISTICA_LOGISTICA;
			break;
			
		case ID_SERVICIO_TECNICO:
			this.selectedGrupo = GRUPO_SERVICIO_TECNICO;
			break;

		case ID_CONTABILIDAD:
			this.selectedGrupo = GRUPO_CONTABILIDAD_CONTABILIDAD;
			break;

		case ID_RRHH:
			this.selectedGrupo = GRUPO_RRHH_RRHH;
			break;

		case ID_SISTEMA:
			this.selectedGrupo = GRUPO_SISTEMA_SISTEMA;
			break;

		case ID_FAVORITOS:
			this.selectedGrupo = GRUPO_FAVORITOS_FAVORITOS;
			break;
		}
	}

	/**
	 * Despliega el modal para filtro..
	 */
	private void imprimir_() throws Exception {

		this.inicializarFiltros();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Config.DATO_SOLO_VIEW_MODEL, this);
		this.win = (Window) Executions.createComponents(ZUL_PARAMETROS, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * despliega el reporte..
	 */
	private void imprimirReporte_() throws Exception {
		System.out.println("-- " + getLoginNombre() + " - " + this.getCodigoReporte());
		
		switch (this.getIdModulo()) {

		case ID_TESORERIA:
			new ReportesTesoreria(this.getCodigoReporte(), this, false);
			break;

		case ID_VENTAS:
			new ReportesVentas(this.getCodigoReporte(), false);
			break;

		case ID_STOCK:
			new ReportesDeStock(this.getCodigoReporte());
			break;

		case ID_COMPRAS:
			new ReportesDeCompras(this.getCodigoReporte());
			break;

		case ID_LOGISTICA:
			new ReportesDeLogistica(this.getCodigoReporte(), false);
			break;
			
		case ID_SERVICIO_TECNICO:
			new ReportesServicioTecnico(this.getCodigoReporte(), false);
			break;

		case ID_CONTABILIDAD:
			new ReportesDeContabilidad(this.getCodigoReporte(), false);
			break;
			
		case ID_RRHH:
			new ReportesRRHH(this.getCodigoReporte());
			break;
			
		case ID_SISTEMA:
			new ReportesDeSistema(this.getCodigoReporte());
			break;
			
		case ID_FAVORITOS:
			String cod = this.getCodigoReporte();
			if (cod.startsWith(Reporte.KEY_TESORERIA)) {
				new ReportesTesoreria(cod, this, false);
			} else if(cod.startsWith(Reporte.KEY_VENTAS)) {
				new ReportesVentas(cod, false);
			} else if(cod.startsWith(Reporte.KEY_STOCK)) {
				new ReportesDeStock(cod);
			} else if(cod.startsWith(Reporte.KEY_COMPRAS)) {
				new ReportesDeCompras(cod);
			} else if(cod.startsWith(Reporte.KEY_LOGISTICA)) {
				new ReportesDeLogistica(cod, false);
			} else if(cod.startsWith(Reporte.KEY_CONTABILIDAD)) {
				new ReportesDeContabilidad(cod, false);
			} else if(cod.startsWith(Reporte.KEY_SISTEMA)) {
				new ReportesDeSistema(cod);
			}
			break;
		}
	}
	
	/**
	 * vista del reporte version mobile..
	 */
	private void imprimirMobile_() throws Exception {

		switch (this.getIdModulo()) {

		case ID_TESORERIA:
			new ReportesTesoreria(this.getCodigoReporte(), this, true);
			break;

		case ID_VENTAS:
			new ReportesVentas(this.getCodigoReporte(), true);
			break;

		case ID_STOCK:
			new ReportesDeStock(this.getCodigoReporte());
			break;

		case ID_COMPRAS:
			//new ReportesDeCompras(this.getCodigoReporte());
			break;

		case ID_LOGISTICA:
			//new ReportesDeLogistica(this.getCodigoReporte());
			break;

		case ID_CONTABILIDAD:
			new ReportesDeContabilidad(this.getCodigoReporte(), true);
			break;
			
		case ID_SISTEMA:
			//new ReportesDeSistema(this.getCodigoReporte());
			break;
		}
	}

	/**
	 * Despliega el reporte en un pdf para su impresion..
	 */
	private void imprimirJasper(String source, Map<String, Object> parametros,
			JRDataSource dataSource, Object[] format) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);
		params.put("format", format);

		this.win = (Window) Executions
				.createComponents(
						com.yhaguy.gestion.reportes.formularios.ReportesViewModel.ZUL_REPORTES,
						this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * setea los favoritos..
	 */
	private void setFavoritos_(MyArray item) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String cod = (String) item.getPos1();
		if (this.favoritos.get(cod) != null) {
			item.setPos5(false);
			this.favoritos.remove(cod);
			ReporteFavoritos fav = rr.getReporteFavorito(this.getLoginNombre(), cod);
			if(fav != null) rr.deleteObject(fav);
		} else {
			item.setPos5(true);
			this.favoritos.put(cod, cod);
			ReporteFavoritos fav = new ReporteFavoritos();
			fav.setUsuario(this.getLoginNombre());
			fav.setCodigoReporte(cod);
			rr.saveObject(fav, this.getLoginNombre());
		}
	}
	
	/**
	 * agrupacion de los reportes..
	 */
	public class ReporteGroupsModel extends GroupsModelArray<MyArray, String, String, Object> {
		private static final long serialVersionUID = 1L;

		public ReporteGroupsModel(List<MyArray> data, Comparator<MyArray> cmpr) {
			super(data.toArray(new MyArray[0]), cmpr);
		}

		protected String createGroupHead(MyArray[] groupdata, int index, int col) {
			String ret = "";
			if (groupdata.length > 0) {
				ret = groupdata[0].getPos4().toString(); //agrupacion por auxi..
			}
			return ret;
		}
	}
	
	/**
	 * comparador para agrupar..
	 */
	class ReporteComparator implements Comparator<MyArray>, GroupComparator<MyArray>, Serializable {
		private static final long serialVersionUID = 1L;
		 
	    public int compare(MyArray o1, MyArray o2) {
	        return o1.getPos4().toString().compareTo(o2.getPos4().toString());
	    }
	 
	    public int compareGroup(MyArray o1, MyArray o2) {
	        if(o1.getPos4().toString().equals(o2.getPos4().toString()))
	            return 0;
	        else
	            return 1;
	    }
	}

	/***********************************************/

	/****************** GET/SET ********************/

	/**
	 * @return los modulos..
	 */
	public List<MyArray> getModulos() {
		List<MyArray> out = new ArrayList<MyArray>();
		out.add(TESORERIA);
		out.add(COMPRAS);
		out.add(VENTAS);
		out.add(STOCK);
		out.add(LOGISTICA);
		out.add(SERVICIOS_TECNICOS);
		out.add(CONTABILIDAD);
		out.add(RRHH);
		out.add(SISTEMA);
		out.add(FAVORITOS);
		return out;
	}
	
	@DependsOn("selectedItem")
	public List<String> getGrupo() {
		List<String> out = new ArrayList<String>();
		int modulo = (int) this.selectedItem.getPos2();
		switch (modulo) {
		
		case ID_TESORERIA:
			out.add(GRUPO_TESORERIA_CAJAS);
			out.add(GRUPO_TESORERIA_BANCOS);
			out.add(GRUPO_TESORERIA_CLIENTES);
			out.add(GRUPO_TESORERIA_PROVEEDORES);
			out.add(GRUPO_TESORERIA_PAGOS);
			out.add(GRUPO_TESORERIA_RECIBOS);
			break;
			
		case ID_COMPRAS:
			out.add(GRUPO_COMPRAS_COMPRAS);
			break;
			
		case ID_VENTAS:
			out.add(GRUPO_VENTAS_UTILIDAD);
			out.add(GRUPO_VENTAS_ARTICULOS);
			out.add(GRUPO_VENTAS_VENDEDOR);
			out.add(GRUPO_VENTAS_CLIENTES);
			out.add(GRUPO_VENTAS_PROVEEDOR);
			out.add(GRUPO_VENTAS_AUDITORIA);
			out.add(GRUPO_VENTAS_NOTACREDITO);
			break;
			
		case ID_STOCK:
			out.add(GRUPO_STOCK_ARTICULOS);
			break;
			
		case ID_LOGISTICA:
			out.add(GRUPO_LOGISTICA_LOGISTICA);
			break;
			
		case ID_SERVICIO_TECNICO:
			out.add(GRUPO_SERVICIO_TECNICO);
			break;
			
		case ID_CONTABILIDAD:
			out.add(GRUPO_CONTABILIDAD_CONTABILIDAD);
			out.add(GRUPO_CONTABILIDAD_CIERRE_PERIODO);
			break;
			
		case ID_RRHH:
			out.add(GRUPO_RRHH_RRHH);
			break;
			
		case ID_SISTEMA:
			out.add(GRUPO_SISTEMA_SISTEMA);
			break;
			
		case ID_FAVORITOS:
			out.add(GRUPO_FAVORITOS_FAVORITOS);
			break;
		}
		return out;
	}
	
	@DependsOn("selectedReporte")
	public boolean isImprimirDisabled() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String reporte = (String) (this.selectedReporte == null ? "" : this.selectedReporte.getPos1());
		return (!rr.isOperacionHabilitada(this.getLoginNombre(), reporte));
	}

	@DependsOn("selectedReporte")
	public boolean isImprimirMobileDisabled() throws Exception {
		if (this.user.isEmpty()) {
			return true;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		String reporte = (String) (this.selectedReporte == null ? "" : this.selectedReporte.getPos1());
		return (!rr.isOperacionHabilitada(this.user, reporte));
	}
	
	@DependsOn({ "selectedItem", "selectedGrupo" })
	public List<MyArray> getReportesSistema() {
		List<MyArray> out = new ArrayList<MyArray>();
		int modulo = (int) this.selectedItem.getPos2();
		if (modulo == ID_FAVORITOS) {
			for (MyArray reporte : this.reportes) {
				String cod = (String) reporte.getPos1();
				if (this.favoritos.get(cod) != null)
					out.add(reporte);
			}
		} else {
			for (MyArray reporte : this.reportes) {
				int mod = (int) reporte.getPos3();
				String grupo = (String) reporte.getPos4();
				if (mod == modulo && (grupo.equals(this.selectedGrupo.trim())))
					out.add(reporte);
			}
		}
		return out;
	}
	
	@DependsOn({ "filterCodigo", "filterDescripcion" })
	public List<MyArray> getReportesSistema_() {
		List<MyArray> out = new ArrayList<MyArray>();
		for (MyArray reporte : this.reportes) {
			if (reporte.getPos2().toString().toUpperCase().contains(this.filterDescripcion.toUpperCase())
					&& reporte.getPos1().toString().toUpperCase().contains(this.filterCodigo.toUpperCase())) {
				out.add(reporte);
			}
		}
		return out;
	}
	
	@DependsOn("selectedItem")
	public List<MyArray> getReportesSistemaMobile() {
		List<MyArray> out = new ArrayList<MyArray>();
		int modulo = (int) this.selectedItem.getPos2();
		for (MyArray reporte : this.reportes) {
			int mod = (int) reporte.getPos3();
			if (mod == modulo)
				out.add(reporte);
		}
		return out;
	}
	
	@DependsOn("selectedReporte")
	public String getSourceParametros() {	
		if(this.selectedReporte != null) {
			return FOLDER_MOBILE_PARAMS + "/" + this.getCodigoReporte() + ".zul";
		}
		return "";
	}
	
	@DependsOn("selectedReporte")
	public String getVistaPrevia() throws Exception {
		String port = (Executions.getCurrent().getServerPort() == 80) ? ""
				: (":" + Executions.getCurrent().getServerPort());
		String url = Executions.getCurrent().getScheme() + "://" + Executions.getCurrent().getServerName() + port
				+ Executions.getCurrent().getContextPath();
		if (!this.isImprimirDisabled()) {			
			return url + "/yhaguy/archivos/reportes/" + this.getCodigoReporte() + ".pdf";
		}
		return url + "/yhaguy/archivos/reportes/default.pdf";
	}

	/**
	 * @return el codigo del reporte..
	 */
	private String getCodigoReporte() {
		return (String) this.selectedReporte.getPos1();
	}

	/**
	 * @return el id del modulo..
	 */
	private int getIdModulo() {
		return (int) this.selectedItem.getPos2();
	}
	
	/**
	 * @return la sucursal operativa..
	 */
	private String getSucursal() {
		return getAcceso().getSucursalOperativa().getText();
	}
	
	/**
	 * @return la sucursal operativa..
	 */
	private long getIdSucursal() {
		return getAcceso().getSucursalOperativa().getId();
	}

	public MyArray getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MyArray selectedItem) {
		this.selectedItem = selectedItem;
		this.selectedReporte = null;
	}

	public MyArray getSelectedReporte() {
		return selectedReporte;
	}

	public void setSelectedReporte(MyArray selectedReporte) {
		this.selectedReporte = selectedReporte;
	}

	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		AccesoDTO out = (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
		if (out == null) {
			try {
				AssemblerAcceso as = new AssemblerAcceso();
				out = (AccesoDTO) as.obtenerAccesoDTO(Configuracion.USER_MOBILE);
				s.setAttribute(Configuracion.ACCESO, out);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}			
		return out;
	}
	
	/**
	 * @return true si es baterias..
	 */
	public boolean isEmpresaBaterias() {
		return Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA);
	}

	/**
	 * Reportes Stock..
	 */
	class ReportesDeStock {

		static final String ARTICULOS_GENERICO = "STK-00001";
		static final String LISTADO_TRANSF_EXT = "STK-00002";
		static final String LISTADO_STOCK_VALORIZADO = "STK-00003";
		static final String LISTADO_AJUSTES = "STK-00004";
		static final String LISTADO_MIGRACION = "STK-00005";
		static final String SALDO_STOCK = "STK-00006";
		static final String HISTORIAL_MOVIMIENTOS_ARTICULO = "STK-00007";
		static final String ARTICULOS_SIN_MOVIMIENTO = "STK-00008";
		static final String EXISTENCIA_ARTICULOS = "STK-00009";
		static final String STOCK_VALORIZADO = "STK-00010";
		static final String REMISION_CLIENTES = "STK-00011";
		static final String CONTROL_CONSUMO_CARGA = "STK-00012";

		/**
		 * procesamiento del reporte..
		 */
		public ReportesDeStock(String codigoReporte) throws Exception {
			switch (codigoReporte) {

			case ARTICULOS_GENERICO:
				this.articulosGenerico();
				break;

			case LISTADO_TRANSF_EXT:
				this.listadoTransferencias();
				break;

			case LISTADO_STOCK_VALORIZADO:
				this.listadoStockValorizado(LISTADO_STOCK_VALORIZADO);
				break;

			case LISTADO_AJUSTES:
				this.listadoAjustesStock();
				break;

			case LISTADO_MIGRACION:
				this.listadoMigracion();
				break;

			case SALDO_STOCK:
				this.listadoMigracion();
				break;

			case HISTORIAL_MOVIMIENTOS_ARTICULO:
				this.historialMovimientosArticulo();
				break;
				
			case ARTICULOS_SIN_MOVIMIENTO:
				this.articulosSinMovimiento();
				break;
				
			case EXISTENCIA_ARTICULOS:
				this.existenciaArticulos();
				break;
				
			case STOCK_VALORIZADO:
				this.stockValorizadoResumido();
				break;
				
			case REMISION_CLIENTES:
				this.listadoRemisiones();
				break;
				
			case CONTROL_CONSUMO_CARGA:
				this.consumoCarga();
				break;
			}
		}

		/**
		 * reporte STK-00001
		 */
		private void articulosGenerico() throws Exception {

			RegisterDomain rr = RegisterDomain.getInstance();
			Tipo familia = filtro.getFamilia();
			Deposito deposito = filtro.getDeposito();

			List<Articulo> articulos = rr.getArticulos(familia.getId(), 0, 0);

			List<Object[]> data = new ArrayList<Object[]>();
			for (Articulo art : articulos) {
				ArticuloDeposito ad = rr.getArticuloDeposito(art.getId(), deposito.getId());
				if (ad != null) {
					Object[] obj1 = new Object[] { art.getCodigoInterno(),
							art.getDescripcion(), ad.getStock() };
					data.add(obj1);
				}				
			}

			ReporteArticulosGenerico rep = new ReporteArticulosGenerico(
					getAcceso().getSucursalOperativa().getText(),
					deposito.getDescripcion(), familia.getDescripcion());
			rep.setDatosReporte(data);
			ViewPdf vp = new ViewPdf();
			vp.setBotonImprimir(false);
			vp.setBotonCancelar(false);
			vp.showReporte(rep, ReportesViewModel.this);
		}

		/**
		 * reporte STK-00002
		 */
		private void listadoTransferencias() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				SucursalApp origen = filtro.getSucursalOrigen();
				SucursalApp destino = filtro.getSucursalDestino();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				
				long idDestino = destino != null ? destino.getId() : 0;
				List<Transferencia> transferencias = rr.getTransferenciasExternas(desde, hasta, origen.getId(), idDestino);

				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LISTADO_TRANSFERENCIAS;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new ListadoTransferenciasDataSource(
						transferencias, desde, hasta);
				params.put("Usuario", getUs().getNombre());
				imprimirJasper(source, params, dataSource, formato);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte STK-00003
		 */
		private void listadoStockValorizado(String codReporte) {
			try {
				Proveedor proveedor = filtro.getProveedorExterior() != null ? filtro.getProveedorExterior() : filtro.getProveedorLocal();
				Date hasta = filtro.getFechaHasta();
				Articulo articulo = filtro.getArticulo();
				String tipoCosto = filtro.getTipoCosto();
				ArticuloFamilia familia = filtro.getFamilia_();
				SucursalApp sucursal = filtro.getSelectedSucursal();
				Deposito deposito = filtro.getDeposito();
				long idProveedor = proveedor != null ? proveedor.getId() : (long) 0;
				long idSucursal = sucursal != null ? sucursal.getId() : (long) 0;
				long idArticulo = articulo != null ? articulo.getId() : (long) 0;
				long idDeposito = deposito != null ? deposito.getId() : (long) 0;
				long idFamilia = familia != null ? familia.getId() : (long) 0;
				
				if (hasta == null) {
					Clients.showNotification("DEBE INDICAR FECHA HASTA..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> arts = new ArrayList<Object[]>();
				
				Map<Long, Double> ajs = new HashMap<Long, Double>();
				List<AjusteValorizado> ajs_ = rr.getAjustesValorizados(Utiles.getFecha("01-01-2016 00:00:00"), hasta);
				for (AjusteValorizado aj : ajs_) {
					ajs.put(aj.getArticulo().getId(), aj.getCostoGs());
				}
				
				arts = rr.getArticulos(idArticulo, idProveedor, idFamilia, true);
				
				for (Object[] art : arts) {
					
					List<Object[]> historial = ControlArticuloStock.getHistorialMovimientos((long) art[0], idDeposito, idSucursal, false, hasta, true);
					Object[] historial_ = historial.size() > 0 ? historial.get(historial.size() - 1) : null;
					
					String codigoInterno = (String) art[1];
					String descripcion = (String) art[2];
					
					String saldo = historial_ != null ? (String) historial_[7] : "0";
					long stock = historial_ != null ? Long.parseLong(saldo) : (long) 0;
					double costo  = (double) art[3];
					long idArticulo_ = (long) art[0];
					
					if (tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO)) {
						double costoPromedio = rr.getCostoPromedio_(idArticulo_, hasta);
						if (costoPromedio > 0) {
							costo = costoPromedio;
						}
					}
					
					if (stock > 0) {
						Double aj = ajs.get(idArticulo_);
						if (aj != null) {
							costo = costo + aj;
						}
						data.add(new Object[] { codigoInterno, descripcion, stock, Utiles.getRedondeo(costo), Utiles.getRedondeo(stock * costo) });
					}										
				}
								
				String desc = articulo != null ? articulo.getCodigoInterno() : "TODOS..";
				String familia_ = familia != null ? familia.getDescripcion() : "TODOS..";
				String proveedor_ = proveedor != null ? proveedor.getRazonSocial() : "TODOS..";
				String deposito_ = deposito != null ? deposito.getDescripcion() : "TODOS..";
				ReporteStockValorizadoAunaFecha rep = new ReporteStockValorizadoAunaFecha(hasta, desc, tipoCosto, familia_, proveedor_, deposito_);
				rep.setApaisada();
				rep.setDatosReporte(data);				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
				
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}

		/**
		 * reporte STK-00004
		 */
		private void listadoAjustesStock() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				TipoMovimiento tipo = filtro.getTipoMovimiento();
				Deposito dep = filtro.getDeposito();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				if (tipo == null)
					tipo = new TipoMovimiento();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<AjusteStock> ajustes = rr.getAjustesStock(desde, hasta,
						tipo.getId(), dep.getId());

				for (AjusteStock ajuste : ajustes) {
					for (AjusteStockDetalle item : ajuste.getDetalles()) {
						Object[] ajt = new Object[] {
								m.dateToString(ajuste.getFecha(),
										Misc.DD_MM_YYYY), ajuste.getNumero(),
								ajuste.getDescripcion(),
								item.getArticulo().getCodigoInterno(),
								item.getArticulo().getDescripcion(),
								item.getCantidad() };
						data.add(ajt);
					}
				}

				ReporteAjusteStock rep = new ReporteAjusteStock(getAcceso()
						.getSucursalOperativa().getText(),
						dep.getDescripcion(), tipo.getDescripcion(), desde,
						hasta);
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * Reporte STK-00005
		 */
		private void listadoMigracion() {
			try {
				Articulo articulo = filtro.getArticulo();
				String tipoSaldoMigracion = filtro.getTipoSaldoMigracion();
				boolean todos = filtro.isTodos();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<ArticuloHistorialMigracion> migracion = rr.getMigracion();

				for (ArticuloHistorialMigracion migr : migracion) {
					if (todos
							|| migr.getCodigoInterno().equals(
									articulo.getCodigoInterno())) {
						if (tipoSaldoMigracion
								.equals(ReportesFiltros.SALDO_MIG_NEGATIVO)
								&& (migr.isSaldoNegativo())) {
							data.add(new Object[] { migr.getCodigoInterno(),
									migr.getDescripcion(), migr.getStock() });

						} else if (tipoSaldoMigracion
								.equals(ReportesFiltros.SALDO_MIG_POSITIVO)
								&& (!migr.isSaldoNegativo())) {
							data.add(new Object[] { migr.getCodigoInterno(),
									migr.getDescripcion(), migr.getStock() });

						} else if (tipoSaldoMigracion
								.equals(ReportesFiltros.TODOS)) {
							data.add(new Object[] { migr.getCodigoInterno(),
									migr.getDescripcion(), migr.getStock() });
						}
					}
				}

				String art = todos ? "TODOS.." : articulo.getDescripcion();

				ReporteMigracionArticulo rep = new ReporteMigracionArticulo(
						getAcceso().getSucursalOperativa().getText(), art,
						tipoSaldoMigracion);
				rep.setDatosReporte(data);
				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte STK-00007
		 */
		private void historialMovimientosArticulo() {
			try {
				Date desde = filtro.getFechaInicioOperaciones();
				Date hasta = filtro.getFechaHasta();
				Articulo articulo = filtro.getArticulo();
				Deposito deposito = filtro.getDeposito();
				long idDeposito = deposito == null? 0 : deposito.getId();
				String desc_deposito = deposito == null? "TODOS.." : deposito.getDescripcion();
				String campoFecha = "fechaCreacion";

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> historico;
				List<Object[]> historicoEntrada;
				List<Object[]> historicoSalida;

				long idArticulo = articulo.getId();
				long idSucursal = getAcceso().getSucursalOperativa().getId();

				List<Object[]> ventas = rr.getVentasPorArticulo(idArticulo, idDeposito, desde, hasta, true);
				List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(idArticulo, idDeposito, desde, hasta, true);
				List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(idArticulo, idDeposito, desde, hasta, true);
				List<Object[]> compras = rr.getComprasLocalesPorArticulo_(idArticulo, idDeposito, desde, hasta, true);
				List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(idArticulo, idDeposito, desde, hasta, true, campoFecha);
				List<Object[]> transfs = rr.getTransferenciasPorArticulo(idArticulo, idDeposito, desde, hasta, true, true);
				List<Object[]> transfs_ = rr.getTransferenciasPorArticulo(idArticulo, idDeposito, desde, hasta, false, true);
				List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(idArticulo, idDeposito, desde, hasta, idSucursal, Configuracion.SIGLA_TM_AJUSTE_POSITIVO, true);
				List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(idArticulo, idDeposito, desde, hasta, idSucursal, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO, true);
				List<Object[]> migracion = rr.getMigracionPorArticulo(articulo.getCodigoInterno(), desde, hasta, idSucursal);

				historicoEntrada = new ArrayList<Object[]>();
				historicoSalida = new ArrayList<Object[]>();
				
				historicoEntrada.addAll(migracion);
				historicoEntrada.addAll(ajustStockPost);
				historicoEntrada.addAll(ntcsv);
				historicoEntrada.addAll(compras);
				historicoEntrada.addAll(importaciones);
				
				for (Object[] movim : ajustStockNeg) {
					movim[3] = (int) movim[3] * -1;
				}
				
				historicoSalida.addAll(ajustStockNeg);
				historicoSalida.addAll(ventas);
				historicoSalida.addAll(ntcsc);
				historicoSalida.addAll(transfs_);

				for (Object[] movim : historicoEntrada) {
					movim[0] = "(+)" + movim[0];
				}
				
				for (Object[] movim : transfs) {
					movim[0] = "(+)" + movim[0];
				}

				historico = new ArrayList<Object[]>();
				historico.addAll(historicoEntrada);
				historico.addAll(historicoSalida);
				historico.addAll(transfs);

				// ordena la lista segun fecha..
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						Date fecha1 = (Date) o1[1];
						Date fecha2 = (Date) o2[1];
						return fecha1.compareTo(fecha2);
					}
				});
				
				long saldo = 0;
				for (Object[] hist : historico) {
					boolean ent = ((String) hist[0]).startsWith("(+)");
					String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
					String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
					String numero = (String) hist[2];
					String concepto = ((String) hist[0]).replace("(+)", "");
					String entrada = ent ? hist[3] + "" : "";
					String salida = ent ? "" : hist[3] + "";
					String importe = Utiles.getNumberFormat((double) hist[4]);;
					String dep = (String) hist[6];
					saldo += ent ? Long.parseLong(hist[3] + "") :  Long.parseLong(hist[3] + "") * -1;
					data.add(new Object[] { fecha, hora, numero, concepto, dep, entrada, salida, saldo + "", importe });
				}
				
				ReporteHistorialMovimientosArticulo rep = new ReporteHistorialMovimientosArticulo(desde, hasta, articulo.getCodigoInterno(), desc_deposito);
				rep.setApaisada();
				rep.setDatosReporte(data);				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte STK-00008
		 */
		private void articulosSinMovimiento() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				
				List<Object[]> arts = rr.getArticulosSinMovimiento(desde, hasta);
				
				for (Object[] item : arts) {
					String cod = (String) item[1];
					if (!cod.equals("INTERESES") && !cod.startsWith("@")) {
						Object[] adp = rr.getStockDisponible_((long) item[0], BuscadorArticulosViewModel.ID_DEP_1);
						long stock = adp == null ? 0 : (long) adp[1];
						if (stock > 0) {
							data.add(new Object[]{ cod, item[2], stock });
						}
					}
				}

				String suc = getAcceso().getSucursalOperativa().getText();				
				ReporteArticulosSinMovimiento rep = new ReporteArticulosSinMovimiento(desde, hasta, suc);
				rep.setDatosReporte(data);
				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte STK-00009
		 */
		private void existenciaArticulos() {
			try {
				ArticuloFamilia familia = filtro.getFamilia_();
				ArticuloMarca marca = filtro.getMarca_();
				Articulo articulo = filtro.getArticulo();
				List<Proveedor> proveedores = filtro.getSelectedProveedores();
				List<Deposito> depositos = filtro.getSelectedDepositos();
				
				long idFamilia = familia == null? 0 : familia.getId();
				long idMarca = marca == null? 0 : marca.getId();
				long idArticulo = articulo == null? 0 : articulo.getId();
				String idProveedores = "0";
				String proveedores_ = proveedores.size() > 0 ? "" : "TODOS..";
				for (Proveedor prov : proveedores) {
					idProveedores += ", " + prov.getId();
					proveedores_ += prov.getRazonSocial() + " / ";
				}
				
				RegisterDomain rr = RegisterDomain.getInstance();
				
				Map<String, Object[]> data_ = new HashMap<String, Object[]>();
				List<Object[]> data = new ArrayList<Object[]>();				
				List<Object[]> articulos = rr.getArticulos_(idFamilia, idMarca, idArticulo, idProveedores);
				
				for (int i = 0; i < depositos.size(); i++) {
					for (Object[] art : articulos) {
						long idArt = (long) art[0];
						String cod = (String) art[1];					
						Object[] stock = rr.getStockArticulo(idArt, depositos.get(i).getId());
						long stock_ = (long) stock[1];
						Object[] obj = data_.get(cod);
						if (obj != null) {
							obj[i+1] = stock_;							
						} else {
							obj = new Object[depositos.size() + 1];
							obj[0] = cod;
							obj[i+1] = stock_;
						}
						data_.put(cod, obj);
					}
				}
				
				for (String key : data_.keySet()) {
					Object[] obj = data_.get(key);
					data.add(obj);
				}
				
				// ordena la lista segun codigo..
				Collections.sort(data, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String id1 = (String) o1[0];
						String id2 = (String) o2[0];
						return id1.compareTo(id2);
					}
				});

				String familia_ = familia == null ? "TODOS.." : familia.getDescripcion();
				String marca_ = marca == null ? "TODOS.." : marca.getDescripcion();
				String articulo_ = articulo == null ? "TODOS.." : articulo.getDescripcion();
				
				ReporteExistenciaArticulosDeposito rep = new ReporteExistenciaArticulosDeposito(familia_, articulo_, marca_, proveedores_, depositos);
				rep.setDatosReporte(data);
				rep.setApaisada();				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte STK-00010
		 */
		private void stockValorizadoResumido() {
			try {
				Proveedor proveedor = filtro.getProveedorExterior() != null ? filtro.getProveedorExterior() : filtro.getProveedorLocal();
				Date hasta = filtro.getFechaHasta();
				Articulo articulo = filtro.getArticulo();
				String tipoCosto = filtro.getTipoCosto();
				ArticuloFamilia familia = filtro.getFamilia_();
				SucursalApp sucursal = filtro.getSelectedSucursal();
				long idProveedor = proveedor != null ? proveedor.getId() : (long) 0;
				long idSucursal = sucursal != null ? sucursal.getId() : (long) 0;
				long idArticulo = articulo != null ? articulo.getId() : (long) 0;
				long idFamilia = familia != null ? familia.getId() : (long) 0;
				
				if (hasta == null) hasta = new Date();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> arts = new ArrayList<Object[]>();
				
				Map<String, Object[]> acum = new HashMap<String, Object[]>();
				
				arts = rr.getArticulos(idArticulo, idProveedor, idFamilia, true);

				for (Object[] art : arts) {
					List<Object[]> historial = ControlArticuloStock.getHistorialMovimientos((long) art[0], (long) 0, idSucursal, false, hasta, true);
					Object[] historial_ = historial.size() > 0 ? historial.get(historial.size() - 1) : null;
					String familia_ = (String) art[4];
					
					String saldo = historial_ != null ? (String) historial_[7] : "0";
					long stock = historial_ != null ? Long.parseLong(saldo) : (long) 0;
					double costo  = (double) art[3];
					
					if (stock != 0 && costo > 0) {
						Object[] obj = acum.get(familia_);
						if (obj == null) {
							acum.put(familia_, new Object[] { familia_, stock, costo, (stock * costo) });
						} else {
							long stock_ = (long) obj[1];
							double costo_ = (double) obj[2];
							stock_ += stock;
							costo_ += costo;
							obj[1] = stock_;
							obj[2] = costo_;
							obj[3] = stock_ * costo_;
							acum.put(familia_, obj);
						}
					}				
				}
				
				for (String key : acum.keySet()) {
					Object[] item = acum.get(key);
					data.add(item);
				}
				
				String desc = articulo != null ? articulo.getCodigoInterno() : "TODOS..";
				String familia_ = familia != null ? familia.getDescripcion() : "TODOS..";
				String proveedor_ = proveedor != null ? proveedor.getRazonSocial() : "TODOS..";
				String sucursal_ = sucursal != null ? sucursal.getDescripcion() : "TODOS..";
				ReporteStockValorizadoAunaFechaResumido rep = new ReporteStockValorizadoAunaFechaResumido(hasta, desc, tipoCosto, familia_, proveedor_, sucursal_);
				rep.setApaisada();
				rep.setDatosReporte(data);
				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
				
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		/**
		 * Reporte STK-00011
		 */
		private void listadoRemisiones() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				
				List<Object[]> remisiones = rr.getRemisiones(desde, hasta);
				
				for (Object[] rem : remisiones) {
					data.add(new Object[]{ Utiles.getDateToString((Date) rem[4], Utiles.DD_MM_YY), rem[1], rem[2], rem[3] });
				}
				
				ReporteRemisiones rep = new ReporteRemisiones(desde, hasta);
				rep.setDatosReporte(data);
				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte STK-00012
		 */
		private void consumoCarga() throws Exception {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Articulo art = filtro.getArticulo();
				long idArticulo = art != null? art.getId() : 0;

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				
				List<Object[]> cargas = rr.getArticuloControlCargas(desde, hasta, idArticulo);
				
				for (Object[] carga : cargas) {
					data.add(new Object[]{ Utiles.getDateToString((Date) carga[4], Utiles.DD_MM_YY), carga[1], carga[2], carga[3] });
				}
				
				ReporteArticuloControlCarga rep = new ReporteArticuloControlCarga(desde, hasta);
				rep.setDatosReporte(data);				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reportes de Ventas..
	 */
	class ReportesVentas {

		static final String VENTAS_UTILIDAD_FACTURA = "VEN-00001";
		static final String VENTAS_UTILIDAD = "VEN-00002";
		static final String VENTAS_HORA = "VEN-00003";
		static final String VENTAS_VENDEDOR = "VEN-00004";
		static final String VENTAS_COBRANZAS = "VEN-00005";
		static final String VENTAS_PROVEEDOR = "VEN-00006";
		static final String VENTAS_NOTAS_DE_CREDITO = "VEN-00007";
		static final String VENTAS_COSTO_DE_VTA = "VEN-00008";
		static final String VENTAS_POR_MARCA = "VEN-00009";
		static final String VENTAS_POR_CLIENTE_DETALLADO = "VEN-00010";
		static final String VENTAS_ANULADAS = "VEN-00011";
		static final String VENTAS_AUTORIZACIONES_PRECIO = "VEN-00012";
		static final String VENTAS_RANKING_POR_FLIA_DET = "VEN-00013";
		static final String VENTAS_RANKING_DE_CLIENTES = "VEN-00014";
		static final String VENTAS_RANKING_DE_CLIENTES_ARTICULOS = "VEN-00015";
		static final String VENTAS_COMISION_COBROS = "VEN-00016";
		static final String VENTAS_CANTIDAD_VENDIDA = "VEN-00017";
		static final String VENTAS_TOTAL_COBRANZAS = "VEN-00018";
		static final String VENTAS_CLIENTES_MES = "VEN-00019";
		static final String VENTAS_PEDIDOS_PENDIENTES = "VEN-00020";
		static final String VENTAS_RECIBOS = "VEN-00021";
		static final String VENTAS_PRESUPUESTOS_PENDIENTES = "VEN-00022";
		static final String VENTAS_VENDEDOR_AGRUPADO = "VEN-000..";
		static final String VENTAS_UTILIDAD_DETALLADO = "VEN-00023";
		static final String VENTAS_PREPARADOR_REPARTIDOR = "VEN-00024";
		static final String VENTAS_PERDIDAS = "VEN-00025";
		static final String VENTAS_VENDEDOR_GENERICO = "VEN-00026";
		static final String VENTAS_CLIENTES_VENDEDOR = "VEN-00027";
		static final String VENTAS_GENERICO = "VEN-00028";
		static final String VENTAS_RANKING_POR_FLIA_RES = "VEN-00029";
		static final String VENTAS_HISTORIAL_MES = "VEN-00030";
		static final String VENTAS_CLIENTES_ULTIMA_VTA = "VEN-00031";
		static final String VENTAS_PROMO_1 = "VEN-00032";
		static final String VENTAS_POR_FAMILIA = "VEN-00033";
		static final String VENTAS_UTILIDAD_RESUMIDO = "VEN-00034";
		static final String VENTAS_COBRANZAS_VENDEDOR = "VEN-00035";
		static final String VENTAS_COBRANZAS_VENDEDOR_DET = "VEN-00036";
		static final String VENTAS_CONTADO_CREDITO_VENDEDOR = "VEN-00037";
		static final String VENTAS_CONTADO_CREDITO_VENDEDOR_DET = "VEN-00038";
		static final String VENTAS_COBRANZAS_VENDEDOR_PROVEEDOR_DET = "VEN-00039";
		static final String VENTAS_VENDEDOR_CLIENTE_ARTICULO_VOLUMEN = "VEN-00040";
		static final String VENTAS_PROMO_COMPRA_VALVOLINE = "VEN-00041";
		static final String VENTAS_LITRAJE = "VEN-00042";
		static final String VENTAS_LISTA_PRECIO_DEPOSITO = "VEN-00043";
		static final String VENTAS_LISTA_PRECIO_FAMILIA = "VEN-00044";
		static final String VENTAS_COBRANZAS_VENDEDOR_CLIENTE = "VEN-00045";
		static final String VENTAS_LISTA_PRECIO_DEPOSITO_ = "VEN-00046";
		static final String VENTAS_COBRANZAS_VENDEDOR_PROVEEDOR_CLIENTE_DET = "VEN-00047";
		static final String VENTAS_CLIENTE_ARTICULO_MES = "VEN-00048";
		static final String VENTAS_PROVEEDOR_CLIENTE_MES = "VEN-00049";
		static final String VENTAS_PROVEEDOR_MES = "VEN-00050";
		static final String VENTAS_PROVEEDOR_CLIENTE_MES_CANT = "VEN-00051";
		static final String VENTAS_DETALLE = "VEN-00052";
		static final String VENTAS_COSTO_DETALLADO = "VEN-00053";
		
		/**
		 * procesamiento del reporte..
		 */
		public ReportesVentas(String codigoReporte, boolean mobile) {
			switch (codigoReporte) {

			case VENTAS_UTILIDAD_FACTURA:
				this.ventasUtilidadPorFactura(mobile);
				break;

			case VENTAS_UTILIDAD:
				this.ventasUtilidad(mobile);
				break;

			case VENTAS_HORA:
				this.ventasPorHora(mobile);
				break;

			case VENTAS_VENDEDOR:
				this.ventasPorVendedor(mobile);
				break;

			case VENTAS_COBRANZAS:
				this.cobranzasPorVendedor(mobile, VENTAS_COBRANZAS);
				break;

			case VENTAS_PROVEEDOR:
				this.ventasPorProveedor(mobile);
				break;

			case VENTAS_NOTAS_DE_CREDITO:
				this.notasDeCredito(mobile);
				break;

			case VENTAS_COSTO_DE_VTA:
				this.costoDeVentas(mobile);
				break;

			case VENTAS_POR_MARCA:
				this.ventasPorMarca(mobile);
				break;

			case VENTAS_POR_CLIENTE_DETALLADO:
				this.ventasPorClienteDetallado(mobile);
				break;

			case VENTAS_ANULADAS:
				this.ventasAnuladas(mobile);
				break;

			case VENTAS_AUTORIZACIONES_PRECIO:
				this.autorizacionesPrecios(mobile);
				break;

			case VENTAS_RANKING_POR_FLIA_DET:
				this.rankingPorFamiliaDetallado(mobile, false);
				break;

			case VENTAS_RANKING_DE_CLIENTES:
				this.rankingDeClientes(mobile);
				break;

			case VENTAS_RANKING_DE_CLIENTES_ARTICULOS:
				this.rankingClientesArticulos(mobile);
				break;
				
			case VENTAS_COMISION_COBROS: 
				this.comisionPorCobros(mobile);
				break;
				
			case VENTAS_CANTIDAD_VENDIDA: 
				this.cantidadVendidaPorArticulo(mobile);
				break;
				
			case VENTAS_TOTAL_COBRANZAS:
				this.totalCobranzasPorVendedor(mobile, VENTAS_TOTAL_COBRANZAS);
				break;
				
			case VENTAS_CLIENTES_MES:
				this.ventasClientesPorMes(mobile, VENTAS_CLIENTES_MES);
				break;
				
			case VENTAS_PEDIDOS_PENDIENTES: 
				this.pedidosPendientes(mobile);
				break;
				
			case VENTAS_RECIBOS: 
				this.ventasRecibos(mobile);
				break;
				
			case VENTAS_PRESUPUESTOS_PENDIENTES:
				this.presupuestosPendientes(mobile);
				break;
				
			case VENTAS_VENDEDOR_AGRUPADO: 
				this.ventasPorVendedorAgrupado(mobile);
				break;
				
			case VENTAS_UTILIDAD_DETALLADO: 
				this.ventasUtilidadDetallado(mobile, false, VENTAS_UTILIDAD_DETALLADO);
				break;
				
			case VENTAS_PREPARADOR_REPARTIDOR:
				this.ventasPreparadorRepartidor(mobile);
				break;
				
			case VENTAS_PERDIDAS:
				this.ventasPerdidas(mobile);
				break;
				
			case VENTAS_VENDEDOR_GENERICO:
				this.ventasPorVendedorGenerico(mobile);
				break;
				
			case VENTAS_CLIENTES_VENDEDOR:
				this.clientesPorVendedor(mobile, VENTAS_CLIENTES_VENDEDOR);
				break;
				
			case VENTAS_GENERICO:
				this.ventasGenerico(mobile);
				break;
				
			case VENTAS_RANKING_POR_FLIA_RES:
				this.rankingPorFamiliaDetallado(mobile, true);
				break;
				
			case VENTAS_HISTORIAL_MES:
				this.ventasPorMes(mobile);
				break;
				
			case VENTAS_CLIENTES_ULTIMA_VTA:
				this.ventasClientesPorMes_ultimaVenta(mobile);
				break;
				
			case VENTAS_PROMO_1:
				this.ventasPromo1(mobile);
				break;
				
			case VENTAS_POR_FAMILIA:
				this.ventasPorFamilia(mobile);
				break;
			
			case VENTAS_UTILIDAD_RESUMIDO:
				this.ventasUtilidadDetallado(mobile, true, VENTAS_UTILIDAD_RESUMIDO);
				break;
				
			case VENTAS_COBRANZAS_VENDEDOR:
				this.cobranzasVentasVendedor(mobile, VENTAS_COBRANZAS_VENDEDOR);
				break;
				
			case VENTAS_COBRANZAS_VENDEDOR_DET:
				this.cobranzasVentasVendedorDetallado(mobile, VENTAS_COBRANZAS_VENDEDOR_DET);
				break;
				
			case VENTAS_CONTADO_CREDITO_VENDEDOR:
				this.ventasContadoCreditoVendedor(mobile);
				break;
				
			case VENTAS_CONTADO_CREDITO_VENDEDOR_DET:
				this.ventasContadoCreditoVendedorDetallado(mobile);
				break;
				
			case VENTAS_COBRANZAS_VENDEDOR_PROVEEDOR_DET:
				this.cobranzasVentasVendedorProveedorDetallado(mobile, VENTAS_COBRANZAS_VENDEDOR_PROVEEDOR_DET);
				break;
				
			case VENTAS_VENDEDOR_CLIENTE_ARTICULO_VOLUMEN:
				this.ventasPorVendedorClienteArticuloPorMes(mobile);
				break;
				
			case VENTAS_PROMO_COMPRA_VALVOLINE:
				this.promoCompraValvoline(mobile);
				break;
				
			case VENTAS_LITRAJE:
				this.ventasLitraje(mobile, VENTAS_LITRAJE);
				break;
				
			case VENTAS_LISTA_PRECIO_DEPOSITO:
				this.listaPrecioPorDeposito(mobile);
				break;
				
			case VENTAS_LISTA_PRECIO_FAMILIA:
				this.ventasPorListaPrecioPorFamilia(mobile);
				break;
				
			case VENTAS_COBRANZAS_VENDEDOR_CLIENTE:
				this.cobranzasVentasVendedorCliente(mobile, VENTAS_COBRANZAS_VENDEDOR_CLIENTE);
				break;
				
			case VENTAS_LISTA_PRECIO_DEPOSITO_:
				this.listaPrecioPorDeposito_(mobile, VENTAS_LISTA_PRECIO_DEPOSITO_);
				break;
				
			case VENTAS_COBRANZAS_VENDEDOR_PROVEEDOR_CLIENTE_DET:
				this.cobranzasVendedorProveedorCliente(mobile, VENTAS_COBRANZAS_VENDEDOR_PROVEEDOR_CLIENTE_DET);
				break;
				
			case VENTAS_CLIENTE_ARTICULO_MES:
				this.ventasClienteArticuloMes(mobile, VENTAS_CLIENTE_ARTICULO_MES);
				break;
				
			case VENTAS_PROVEEDOR_CLIENTE_MES:
				this.ventasProveedorClientePorMes(mobile, VENTAS_PROVEEDOR_CLIENTE_MES);
				break;
				
			case VENTAS_PROVEEDOR_MES:
				this.ventasProveedorPorMes(mobile, VENTAS_PROVEEDOR_MES);
				break;
				
			case VENTAS_PROVEEDOR_CLIENTE_MES_CANT:
				this.ventasProveedorClientePorMes_(mobile, VENTAS_PROVEEDOR_CLIENTE_MES_CANT);
				break;
				
			case VENTAS_DETALLE:
				this.ventasDetalle(mobile, VENTAS_DETALLE);
				break;
				
			case VENTAS_COSTO_DETALLADO:
				this.costoDeVentasDetallado(mobile);
				break;
			}
		}

		/**
		 * reporte VEN-00001
		 */
		public void ventasUtilidadPorFactura(boolean mobile) {
			try {
				Map<String, String> excluir = new HashMap<String, String>();
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				double totalImporte = 0;
				double totalCosto = 0;
				
				for (Cliente cli : filtro.getSelectedClientes()) {
					excluir.put(cli.getRazonSocial(), cli.getRazonSocial());
				}
				
				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
				for (NotaCredito notacred : ncs) {
					if (excluir.get(notacred.getCliente().getRazonSocial()) == null) {
						String motivo = notacred.getMotivo().getDescripcion()
								.substring(0, 3).toUpperCase()
								+ ".";
						Object[] nc = new Object[] {
								Utiles.getDateToString(notacred.getFechaEmision(), "dd-MM-yy"),
								notacred.getNumero(),
								notacred.isNotaCreditoVentaContado() ? "NC-CO "
										+ motivo : "NC-CR " + motivo,
								notacred.isAnulado() || !notacred.isMotivoDevolucion() ? 0.0 : notacred.getTotalCostoGsSinIva() * -1,
								notacred.isAnulado() ? 0.0 : notacred.getTotalImporteGsSinIva() * -1,
								notacred.isAnulado() ? 0.0 : notacred.getRentabilidad() * -1};					
						if (!notacred.isAnulado()) {
							totalImporte += notacred.getTotalImporteGsSinIva() * -1;
							totalCosto += notacred.getTotalCostoGsSinIva() * -1;
						}
						data.add(nc);
					}					
				}

				List<Venta> ventas = rr.getVentas(desde, hasta, 0);
				for (Venta venta : ventas) {
					if (excluir.get(venta.getCliente().getRazonSocial()) == null) {
						Object[] vta = new Object[] {
								Utiles.getDateToString(venta.getFecha(), "dd-MM-yy"),
								venta.getNumero(),
								"FAC. " + venta.getCondicionPago().getDescripcion().substring(0, 3).toUpperCase(),
								venta.isAnulado() ? 0.0 : venta.getTotalCostoGsSinIva(),
								venta.isAnulado() ? 0.0 : (venta.getTotalImporteGsSinIva()),
								venta.isAnulado() ? 0.0 : venta.getRentabilidad() };
						if (!venta.isAnulado()) {
							totalImporte += venta.getTotalImporteGsSinIva();
							totalCosto += venta.getTotalCostoGsSinIva();
						}
						data.add(vta);
					}					
				}
				double promedio = Utiles.obtenerPorcentajeDelValor((totalImporte - totalCosto), totalCosto);
				String sucursal = getAcceso().getSucursalOperativa().getText();
				String excluidos = excluir.size() == 0 ? "- - -" : "";
				for (String excluido : excluir.keySet()) {
					excluidos += " - " + excluido;
				}

				ReporteVentasUtilidadPorFactura rep = new ReporteVentasUtilidadPorFactura(desde, hasta, sucursal, promedio, excluidos);
				rep.setDatosReporte(data);
				rep.setApaisada();

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00002
		 */
		public void ventasUtilidad(boolean mobile) {

			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Articulo art = filtro.getArticulo();
				Cliente cliente = filtro.getCliente();
				
				double totalImporte = 0;
				double totalCosto = 0;
				
				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				long idCliente = cliente == null ? 0 : cliente.getId();

				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, idCliente);
				for (NotaCredito notacred : ncs) {
					String motivo = notacred.getMotivo().getDescripcion().substring(0, 3).toUpperCase() + ".";
					for (NotaCreditoDetalle item : notacred.getDetallesArticulos()) {
						Object[] nc = new Object[] {
								Utiles.getDateToString(notacred.getFechaEmision(), "dd-MM-yy"),
								notacred.getNumero(),
								notacred.isNotaCreditoVentaContado() ? "NC-CO " + motivo : "NC-CR " + motivo,
								item.getArticulo().getCodigoInterno(),
								notacred.isAnulado() || !notacred.isMotivoDevolucion() ? 0.0 : item.getCostoGs() * -1,
								notacred.isAnulado() || !notacred.isMotivoDevolucion() ? (long) 0 : Long.parseLong(item.getCantidad() + ""),
								notacred.isAnulado() || !notacred.isMotivoDevolucion() ? 0.0 : item.getCostoTotalGsSinIva() * -1,
								notacred.isAnulado() ? 0.0 : item.getImporteGsSinIva() * -1,
								notacred.isAnulado() ? 0.0 : item.getRentabilidad() * -1 };
						if (art == null || art.getId().longValue() == item.getArticulo().getId().longValue()) {
							data.add(nc);
							if (!notacred.isAnulado() && art != null) {
								totalImporte += item.getImporteGsSinIva() * -1;
								totalCosto += item.getCostoTotalGsSinIva() * -1;
							}
						}
					}
					if (art == null && notacred.isMotivoDescuento()) {
						Object[] nc = new Object[] {
								Utiles.getDateToString(notacred.getFechaEmision(), "dd-MM-yy"),
								notacred.getNumero(),
								notacred.isNotaCreditoVentaContado() ? "NC-CO " + motivo : "NC-CR " + motivo,
								"DESCUENTO CONCEDIDO",
								0.0,
								(long) 0,
								0.0,
								notacred.isAnulado() ? 0.0 : notacred.getTotalImporteGsSinIva() * -1,
								notacred.isAnulado() ? 0.0 : notacred.getRentabilidad() * -1 };
						data.add(nc);
					}
					if (!notacred.isAnulado() && art == null) {
						totalImporte += notacred.getTotalImporteGsSinIva() * -1;
						totalCosto += notacred.getTotalCostoGsSinIva() * -1;
					}
				}

				List<Venta> ventas = rr.getVentas(desde, hasta, idCliente);
				for (Venta venta : ventas) {
					for (VentaDetalle item : venta.getDetalles()) {
						Object[] vta = new Object[] {
								Utiles.getDateToString(venta.getFecha(), "dd-MM-yy"),
								venta.getNumero(),
								"FAC. " + venta.getCondicionPago().getDescripcion().substring(0, 3).toUpperCase(),
								item.getArticulo().getCodigoInterno(),
								venta.isAnulado() ? 0.0 : item.getCostoUnitarioGs(),
								venta.isAnulado() ? (long) 0 : item.getCantidad(),
								venta.isAnulado() ? 0.0 : item.getCostoTotalGsSinIva(),
								venta.isAnulado() ? 0.0 : item.getImporteGsSinIva() - item.getDescuentoUnitarioGsSinIva(),
								venta.isAnulado() ? 0.0 : item.getRentabilidad() };
						if (art == null || art.getId().longValue() == item.getArticulo().getId().longValue()) {
							data.add(vta);
							if (!venta.isAnulado() && art != null) {
								totalImporte += (item.getImporteGsSinIva() - item.getDescuentoUnitarioGsSinIva());
								totalCosto += item.getCostoTotalGsSinIva();
							}
						}
					}
					if (!venta.isAnulado() && art == null) {
						totalImporte += venta.getTotalImporteGsSinIva();
						totalCosto += venta.getTotalCostoGsSinIva();
					}
				}
				double promedio = Utiles.obtenerPorcentajeDelValor((totalImporte - totalCosto), totalCosto);
				String sucursal = getAcceso().getSucursalOperativa().getText();
				String articulo = art == null ? "TODOS.." : art.getDescripcion();
				String cliente_ = cliente == null ? "TODOS.." : cliente.getRazonSocial();

				ReporteVentasUtilidadPorArticulo rep = new ReporteVentasUtilidadPorArticulo(desde, hasta, sucursal, promedio, articulo, cliente_);
				rep.setDatosReporte(data);
				rep.setApaisada();

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00003
		 */
		public void ventasPorHora(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas = rr.getVentas(desde, hasta, 0);

				double totalVtaSiete = 0;
				double totalVtaOcho = 0;
				double totalVtaNueve = 0;
				double totalVtaDiez = 0;
				double totalVtaOnce = 0;
				double totalVtaDoce = 0;
				double totalVtaTrece = 0;
				double totalVtaCatorce = 0;
				double totalVtaQuince = 0;
				double totalVtaDieciseis = 0;
				double totalVtaDiecisiete = 0;

				int cantVtaSiete = 0;
				int cantVtaOcho = 0;
				int cantVtaNueve = 0;
				int cantVtaDiez = 0;
				int cantVtaOnce = 0;
				int cantVtaDoce = 0;
				int cantVtaTrece = 0;
				int cantVtaCatorce = 0;
				int cantVtaQuince = 0;
				int cantVtaDieciseis = 0;
				int cantVtaDiecisiete = 0;

				for (Venta venta : ventas) {
					int hora = venta.getHora();
					switch (hora) {
					case 7:
						totalVtaSiete += venta.getTotalImporteGs();
						cantVtaSiete++;
						break;
					case 8:
						totalVtaOcho += venta.getTotalImporteGs();
						cantVtaOcho++;
						break;
					case 9:
						totalVtaNueve += venta.getTotalImporteGs();
						cantVtaNueve++;
						break;
					case 10:
						totalVtaDiez += venta.getTotalImporteGs();
						cantVtaDiez++;
						break;
					case 11:
						totalVtaOnce += venta.getTotalImporteGs();
						cantVtaOnce++;
						break;
					case 12:
						totalVtaDoce += venta.getTotalImporteGs();
						cantVtaDoce++;
						break;
					case 13:
						totalVtaTrece += venta.getTotalImporteGs();
						cantVtaTrece++;
						break;
					case 14:
						totalVtaCatorce += venta.getTotalImporteGs();
						cantVtaCatorce++;
						break;
					case 15:
						totalVtaQuince += venta.getTotalImporteGs();
						cantVtaQuince++;
						break;
					case 16:
						totalVtaDieciseis += venta.getTotalImporteGs();
						cantVtaDieciseis++;
						break;
					case 17:
						totalVtaDiecisiete += venta.getTotalImporteGs();
						cantVtaDiecisiete++;
						break;
					}
				}

				Object[] vtaSiete = new Object[] { "DE 7 A 8 HS.",
						cantVtaSiete, totalVtaSiete };
				Object[] vtaOcho = new Object[] { "DE 8 a 9 HS", cantVtaOcho,
						totalVtaOcho };
				Object[] vtaNueve = new Object[] { "DE 9 a 10 HS",
						cantVtaNueve, totalVtaNueve };
				Object[] vtaDiez = new Object[] { "DE 10 a 11 HS", cantVtaDiez,
						totalVtaDiez };
				Object[] vtaOnce = new Object[] { "DE 11 a 12 HS", cantVtaOnce,
						totalVtaOnce };
				Object[] vtaDoce = new Object[] { "DE 12 a 13 HS", cantVtaDoce,
						totalVtaDoce };
				Object[] vtaTrece = new Object[] { "DE 13 a 14 HS",
						cantVtaTrece, totalVtaTrece };
				Object[] vtaCatorce = new Object[] { "DE 14 a 15 HS",
						cantVtaCatorce, totalVtaCatorce };
				Object[] vtaQuince = new Object[] { "DE 15 a 16 HS",
						cantVtaQuince, totalVtaQuince };
				Object[] vtaDieciseis = new Object[] { "DE 16 a 17 HS",
						cantVtaDieciseis, totalVtaDieciseis };
				Object[] vtaDiecisiete = new Object[] { "DE 17 a 18 HS",
						cantVtaDiecisiete, totalVtaDiecisiete };
				data.add(vtaSiete);
				data.add(vtaOcho);
				data.add(vtaNueve);
				data.add(vtaDiez);
				data.add(vtaOnce);
				data.add(vtaDoce);
				data.add(vtaTrece);
				data.add(vtaCatorce);
				data.add(vtaQuince);
				data.add(vtaDieciseis);
				data.add(vtaDiecisiete);

				double promedio = (totalVtaSiete + totalVtaOcho + totalVtaNueve
						+ totalVtaDiez + totalVtaOnce + totalVtaDoce
						+ totalVtaTrece + totalVtaCatorce + totalVtaQuince
						+ totalVtaDieciseis + totalVtaDiecisiete) / 11;
				
				String sucursal = getAcceso().getSucursalOperativa().getText();

				ReporteVentasPorHora rep = new ReporteVentasPorHora(desde,
						hasta, "Todos", promedio, sucursal);
				rep.setDatosReporte(data);
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00004
		 */
		public void ventasPorVendedor(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				Proveedor proveedor = filtro.getProveedorExterior();
				Object[] formato = filtro.getFormato();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				double totalImporte = 0;
				
				Map<String, Object[]> values = new HashMap<String, Object[]>();
				Map<String, Object[]> values_ = new HashMap<String, Object[]>();

				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
				Map<String, List<NotaCredito>> ncs_vendedor = new HashMap<String, List<NotaCredito>>();
				
				// Notas de Credito
				if (filtro.isIncluirNCR() || filtro.isIncluirNCR_CRED()) {
					if (vendedor == null) {
						for (Funcionario vend : filtro.getVendedores_()) {
							for (NotaCredito nc : ncs) {
								long idVen = vend.getId().longValue();
								long idVenNc = nc.getVendedor().getId().longValue();
								if (idVen == idVenNc) {
									List<NotaCredito> ncs_ = ncs_vendedor.get(vend.getRazonSocial());
									if (ncs_ == null) ncs_ = new ArrayList<NotaCredito>();
									if (filtro.isIncluirNCR() && nc.isNotaCreditoVentaContado()) ncs_.add(nc); 
									if (filtro.isIncluirNCR_CRED() && !nc.isNotaCreditoVentaContado()) ncs_.add(nc);							
									ncs_vendedor.put(vend.getRazonSocial(), ncs_);
								}
							}
						}
					} else {
					for (NotaCredito nc : ncs) {
						long idVen = vendedor.getId().longValue();
						long idVenNc = nc.getVendedor().getId().longValue();
						if (idVen == idVenNc) {
							List<NotaCredito> ncs_ = ncs_vendedor.get(vendedor.getRazonSocial());
							if (ncs_ == null) ncs_ = new ArrayList<NotaCredito>();
							if (filtro.isIncluirNCR() && nc.isNotaCreditoVentaContado()) ncs_.add(nc);
							if (filtro.isIncluirNCR_CRED() && !nc.isNotaCreditoVentaContado()) ncs_.add(nc);
							ncs_vendedor.put(vendedor.getRazonSocial(), ncs_);
						}
					}
				}				
					for (String key : ncs_vendedor.keySet()) {
						List<NotaCredito> ncs_ = ncs_vendedor.get(key);
						for (NotaCredito ncr : ncs_) {
							int mes = Utiles.getNumeroMes(ncr.getFechaEmision()) - 1;
							if (!ncr.isAnulado()) {
								double importe = filtro.isIvaIncluido() ? ncr.getImporteGs() * -1 : ncr.getTotalImporteGsSinIva() * -1;
								if (proveedor != null) importe = filtro.isIvaIncluido() ? 
										ncr.getImporteByProveedor(proveedor.getId()) * -1 : ncr.getImporteByProveedorSinIva(proveedor.getId()) * -1;
								String vend = ncr.getVendedor().getRazonSocial();
								String key_ = ncr.getVendedor().getId() + "-" + mes;
								Object[] acum = values.get(key_);
								if (acum != null) {
									double importe_ = (double) acum[0];
									importe_ += importe;
									values.put(key_, new Object[] { importe_, mes, vend });
								} else {
									values.put(key_, new Object[] { importe, mes, vend });
								}
							}
						}
					}
				}											
				
				// Ventas Contado y Credito
				if (filtro.isIncluirVCR() && filtro.isIncluirVCT()) {
					List<Venta> ventas = new ArrayList<Venta>();
					if (vendedor == null) {
						for (Funcionario vend : filtro.getVendedores_()) {
							ventas.addAll(rr.getVentasPorVendedor(vend.getId(), desde, hasta));
						}
					} else {
						ventas = rr.getVentasPorVendedor(vendedor.getId(), desde, hasta);
					}
					for (Venta venta : ventas) {
						int mes = Utiles.getNumeroMes(venta.getFecha()) - 1;
						if (!venta.isAnulado()) {
							double importe = filtro.isIvaIncluido() ? 
									venta.getTotalImporteGs() : venta.getTotalImporteGsSinIva();	
							String vend = venta.getVendedor().getRazonSocial();
							if (proveedor != null) importe = filtro.isIvaIncluido() ? 
									venta.getImporteByProveedor(proveedor.getId()) : venta.getImporteByProveedorSinIva(proveedor.getId());
							String key = venta.getVendedor().getId() + "-" + mes;
							Object[] acum = values.get(key);							
							if (acum != null) {
								double importe_ = (double) acum[0];
								importe_ += importe;
								values.put(key, new Object[] { importe_, mes, vend });
							} else {
								values.put(key, new Object[] { importe, mes, vend });
							}
						}
					}
					
				} else if (filtro.isIncluirVCR()) {
					List<Venta> ventas = new ArrayList<Venta>();
					if (vendedor == null) {
						for (Funcionario vend : filtro.getVendedores_()) {
							ventas.addAll(rr.getVentasCreditoPorVendedor(desde, hasta, vend.getId()));
						}
					} else {
						ventas = rr.getVentasCreditoPorVendedor(desde, hasta, vendedor.getId());
					}
					for (Venta venta : ventas) {
						if (!venta.isAnulado()) {
							int mes = Utiles.getNumeroMes(venta.getFecha()) - 1;
							double importe = filtro.isIvaIncluido() ? 
									venta.getTotalImporteGs() : venta.getTotalImporteGsSinIva();
							String vend = venta.getVendedor().getRazonSocial();
							if (proveedor != null) importe = filtro.isIvaIncluido() ? 
									venta.getImporteByProveedor(proveedor.getId()) : venta.getImporteByProveedorSinIva(proveedor.getId());
							String key = venta.getVendedor().getId() + "-" + mes;
							Object[] acum = values.get(key);							
							if (acum != null) {
								double importe_ = (double) acum[0];
								importe_ += importe;
								values.put(key, new Object[] { importe_, mes, vend });
							} else {
								values.put(key, new Object[] { importe, mes, vend });
							}
						}						
					}
					
				} else if (filtro.isIncluirVCT()) {
					List<Venta> ventas = new ArrayList<Venta>();
					if (vendedor == null) {
						for (Funcionario vend : filtro.getVendedores_()) {
							ventas.addAll(rr.getVentasContadoPorVendedor(desde, hasta, vend.getId()));
						}
					} else {
						ventas = rr.getVentasContadoPorVendedor(desde, hasta, vendedor.getId());
					}
					for (Venta venta : ventas) {
						if (!venta.isAnulado()) {
							int mes = Utiles.getNumeroMes(venta.getFecha()) - 1;
							double importe = filtro.isIvaIncluido() ? 
									venta.getTotalImporteGs() : venta.getTotalImporteGsSinIva();
							String vend = venta.getVendedor().getRazonSocial();
							if (proveedor != null) {
								importe = filtro.isIvaIncluido() ? 
										venta.getImporteByProveedor(proveedor.getId()) : venta.getImporteByProveedorSinIva(proveedor.getId());
							}
							String key = venta.getVendedor().getId() + "-" + mes;
							Object[] acum = values.get(key);							
							if (acum != null) {
								double importe_ = (double) acum[0];
								importe_ += importe;
								values.put(key, new Object[] { importe_, mes, vend });
							} else {
								values.put(key, new Object[] { importe, mes, vend });
							}
						}						
					}
					
				}		
				
				for (String key : values.keySet()) {
					Object[] value = values.get(key);
					String vend = (String) value[2];
					int mes = (int) value[1];
					double importe = (double) value[0];
					Object[] value_ = values_.get(vend);
					if (value_ != null) {
						value_[mes] = importe;
						values_.put(vend, value_);
					} else {
						Object[] datos = new Object[] { (double) 0, (double) 0,
								(double) 0, (double) 0, (double) 0, (double) 0,
								(double) 0, (double) 0, (double) 0, (double) 0,
								(double) 0, (double) 0 };
						datos[mes] = importe;
						values_.put(vend, datos);
					}
				}
				
				for (String key : values_.keySet()) {
					Object[] value_ = values_.get(key);
					double total = (double) value_[0] + (double) value_[1]
							+ (double) value_[2] + (double) value_[3]
							+ (double) value_[4] + (double) value_[5]
							+ (double) value_[6] + (double) value_[7]
							+ (double) value_[8] + (double) value_[9]
							+ (double) value_[10] + (double) value_[11];
					data.add(new Object[] { key, value_[0],
							value_[1], value_[2], value_[3], value_[4],
							value_[5], value_[6], value_[7], value_[8],
							value_[9], value_[10], value_[11], total });
				}

				double totalSinIva = totalImporte - m.calcularIVA(totalImporte, 10);
				String proveedor_ = proveedor == null ? "TODOS.." : proveedor.getRazonSocial();
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_VENDEDOR;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new VentasPorVendedorDataSource(data, totalImporte, totalSinIva);
				params.put("Titulo", "VEN-00004 - Ventas por Vendedor / Proveedor");
				params.put("Usuario", getUs().getNombre());
				params.put("Vendedor", vendedor == null ? "TODOS.." : vendedor.getRazonSocial().toUpperCase());
				params.put("Proveedor", proveedor_);
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("NCR_INC", filtro.isIncluirNCR() ? "SI" : "NO");
				params.put("NCR_CRE", filtro.isIncluirNCR_CRED() ? "SI" : "NO");
				params.put("VCR_INC", filtro.isIncluirVCR() ? "SI" : "NO");
				params.put("VCT_INC", filtro.isIncluirVCT() ? "SI" : "NO");
				params.put("IVA_INC", filtro.isIvaIncluido() ? "SI" : "NO");
				imprimirJasper(source, params, dataSource, formato);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00005
		 */
		public void cobranzasPorVendedor(boolean mobile, String codReporte) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				long idVendedor = vendedor != null ? vendedor.getId() : 0;

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<Object[]> cobranzas = rr.getCobranzasPorVendedor(desde, hasta, idVendedor, getIdSucursal());

				for (Object[] cobro : cobranzas) {
					Recibo rec = (Recibo) cobro[0];
					String fac = (String) cobro[1];
					double montoGs = (double) cobro[2];
					montoGs = Utiles.getRedondeo(montoGs);
					double montoSinIva = montoGs - (Utiles.getIVA(montoGs, 10));
					montoSinIva = Utiles.getRedondeo(montoSinIva);
					Object[] cob = new Object[] { m.dateToString(rec.getFechaEmision(), Misc.DD_MM_YYYY),
							rec.getNumero(), fac, montoGs, montoSinIva };
					data.add(cob);
				}
				
				List<AjusteCtaCte> apls = rr.getAplicacionesAnticipos(desde, hasta, vendedor.getId().longValue());
				for (AjusteCtaCte apl : apls) {
					if (apl.getCredito().isVentaCredito()) {
						double importe = apl.getImporte();
						importe = Utiles.getRedondeo(importe);
						double importeSinIva = importe - (Utiles.getIVA(importe, 10));
						importeSinIva = Utiles.getRedondeo(importeSinIva);
						Object[] cob = new Object[] { m.dateToString(apl.getFecha(), Misc.DD_MM_YYYY),
								apl.getOrden(), apl.getCredito().getNroComprobante(), importe, 
								importeSinIva };
						data.add(cob);
					}					
				}
				
				String vendedor_ = vendedor == null ? "TODOS.." : vendedor.getRazonSocial();
				
				ReporteCobranzasPorVendedor rep = new ReporteCobranzasPorVendedor(desde, hasta, vendedor_);
				rep.setDatosReporte(data);
				rep.setTitulo(codReporte + " - Listado de Cobranzas por Vendedor");

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00006
		 */
		public void ventasPorProveedor(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Proveedor proveedor = filtro.getProveedorExterior();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();
				
				if (proveedor == null) {
					Clients.showNotification("Debe seleccionar un proveedor..", 
							Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				if (Utiles.diasEntreFechas(desde, hasta) > 32) {
					Clients.showNotification("Rango máximo 30 días..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Venta> ventas = rr.getVentas(desde, hasta, 0);
				List<Object[]> data = new ArrayList<Object[]>();
				Map<String, Object[]> values = new HashMap<String, Object[]>();
				Set<String> keys = new HashSet<String>();
				List<String> keys_ = new ArrayList<String>();
				
				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						for (VentaDetalle item : venta.getDetalles()) {
							if (item.getArticulo().isProveedor(proveedor.getId())) {
								String cod = item.getArticulo().getCodigoInterno();
								keys.add(cod);
								Object[] acum = values.get(cod);
								if (acum != null) {
									long cant = (long) acum[0];
									double importe = (double) acum[1];
									cant += item.getCantidad();
									importe += item.getImporteGs();
									values.put(cod, new Object[] { cant, importe, item.getArticulo().getDescripcion() });
								} else {
									values.put(cod,
											new Object[] { item.getCantidad(), item.getImporteGs(), item.getArticulo().getDescripcion() });
								}
							}														
						}
					}
				}
				
				keys_.addAll(keys);
				Collections.sort(keys_, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						int compare = o1.compareTo(o2);				
						return compare;
					}
				});
				
				for (String key : keys_) {
					Object[] value = values.get(key);
					long cantidad = (long) value[0];
					double importe = (double) value[1];
					String descripcion = (String) value[2];
					data.add(new Object[] { key, descripcion, cantidad, importe });
				}
				
				String proveedor_ = proveedor.getRazonSocial();
				ReporteVentasPorProveedor rep = new ReporteVentasPorProveedor(desde,
						hasta, proveedor_, getSucursal());
				rep.setDatosReporte(data);
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00007
		 */
		public void notasDeCredito(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Tipo motivo = filtro.getMotivoNotaCredito();
				Boolean promocion = filtro.isFraccionado() ? true : null;

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<NotaCredito> notasCredito = rr.getNotasCreditoVenta(desde, hasta, 0, promocion);

				for (NotaCredito nc : notasCredito) {
					double importe = nc.isAnulado() ? 0.0 : nc.getImporteGs();
					Object[] obj = new Object[] {
							m.dateToString(nc.getFechaEmision(), "dd-MM-yy"),
							nc.getNumero(),
							nc.isNotaCreditoVentaContado() ? "CON." : "CRÉ.",
							nc.getCliente().getRazonSocial(),
							nc.getCliente().getRuc(),
							nc.getNumeroFacturaAplicada(),
							nc.getVendedor().getRazonSocial().toUpperCase()
									.substring(0, 6)
									+ "..",
							nc.getMotivo().getDescripcion().toUpperCase()
									.substring(0, 6)
									+ "..", importe };
					if (motivo.getSigla().equals("TODOS")) {
						data.add(obj);
					} else if (nc.getMotivo().getSigla()
							.equals(motivo.getSigla())) {
						data.add(obj);
					}
				}

				ReporteNotasDeCredito rep = new ReporteNotasDeCredito(desde,
						hasta, motivo.getDescripcion());
				rep.setDatosReporte(data);
				rep.setA4();
				rep.setApaisada();

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00008
		 */
		private void costoDeVentas(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				String tipoCosto = filtro.getTipoCosto();
				
				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<Object[]> ncs = rr.getNotasCreditoVenta_(desde, hasta, 0);
				for (Object[] notacred : ncs) {
					String numero = (String) notacred[1];
					Date fecha = (Date) notacred[2];
					String descrMotivo = (String) notacred[3];
					String siglaMotivo = (String) notacred[4];
					double costoPromedio = (double) notacred[5];
					double costoUltimo = (double) notacred[6];
					if (siglaMotivo.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_DEVOLUCION)) {
						String motivo = descrMotivo.substring(0, 3).toUpperCase() + ".";
						double costo = 0;
						if (tipoCosto.equals(ReportesFiltros.COSTO_ULTIMO)) {
							costo = costoUltimo;
						}
						if (tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO)) {
							costo = costoPromedio;
						}
						Object[] nc = new Object[] {
								Utiles.getDateToString(fecha, "dd-MM-yy"),
								numero,
								"NCR " + motivo,
								Utiles.getRedondeo(costo * -1) };					
						data.add(nc);
					}
				}

				List<Object[]> ventas = rr.getVentas_(desde, hasta, 0);
				for (Object[] venta : ventas) {
					String numero = (String) venta[1];
					Date fecha = (Date) venta[2];
					String sigla = (String) venta[4];
					double costoPromedio = (double) venta[5];
					double costoUltimo = (double) venta[6];
					double costo = 0;					
					if (tipoCosto.equals(ReportesFiltros.COSTO_ULTIMO)) {
						costo = costoUltimo;
					}
					if (tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO)) {
						costo = costoPromedio;
					}
					Object[] vta = new Object[] { 
							Utiles.getDateToString(fecha, "dd-MM-yy"), 
							numero, 
							"FAC. " + TipoMovimiento.getAbreviatura(sigla),
							Utiles.getRedondeo(costo) };
					data.add(vta);
				}
				String sucursal = getAcceso().getSucursalOperativa().getText();

				ReporteCostoVentas rep = new ReporteCostoVentas(desde, hasta, sucursal, tipoCosto);
				rep.setDatosReporte(data);
				rep.setApaisada();

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00009
		 */
		public void ventasPorMarca(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Tipo marca = filtro.getTipo();
				Deposito deposito = filtro.getDeposito();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				if (marca == null)
					marca = new Tipo();

				if (deposito == null)
					deposito = new Deposito();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas = rr.getVentas(desde, hasta, 0);
				List<NotaCredito> ncreds = rr.getNotasCreditoVenta(desde,
						hasta, 0);

				for (NotaCredito ncred : ncreds) {
					if (!ncred.isAnulado()) {
						for (NotaCreditoDetalle det : ncred.getDetalles()) {
							if (det.getArticulo() != null) {
								if (det.getArticulo().getArticuloMarca()
										.getId().longValue() == marca.getId()
										.longValue()) {
									ArticuloDeposito ad = rr
											.getArticuloDeposito(det
													.getArticulo().getId(),
													deposito.getId());

									data.add(new Object[] {
											m.dateToString(
													ncred.getFechaEmision(),
													Misc.DD_MM_YYYY),
											ncred.getNumero(),
											getMaxLength(ncred.getCliente()
													.getRazonSocial(), 25),
											getMaxLength(det.getArticulo()
													.getCodigoInterno(), 15),
											getMaxLength(det.getArticulo()
													.getDescripcion(), 25),
											((Integer) det.getCantidad())
													.longValue() * -1,
											det.getMontoGs(), ad.getStock() });
								}
							}
						}
					}
				}

				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						for (VentaDetalle item : venta.getDetalles()) {
							if (item.getArticulo().getArticuloMarca().getId()
									.longValue() == marca.getId().longValue()) {
								ArticuloDeposito ad = rr.getArticuloDeposito(
										item.getArticulo().getId(),
										deposito.getId());
								data.add(new Object[] {
										m.dateToString(venta.getFecha(),
												Misc.DD_MM_YYYY),
										venta.getNumero(),
										getMaxLength(venta.getCliente()
												.getRazonSocial(), 25),
										getMaxLength(item.getArticulo()
												.getCodigoInterno(), 15),
										getMaxLength(item.getArticulo()
												.getDescripcion(), 25),
										item.getCantidad(), item.getPrecioGs(),
										ad.getStock() });
							}
						}
					}
				}

				ReporteVentasPorMarca rep = new ReporteVentasPorMarca(desde,
						hasta, getAcceso().getSucursalOperativa().getText(),
						deposito.getDescripcion(), marca.getDescripcion());
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00010
		 */
		private void ventasPorClienteDetallado(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Cliente cliente = filtro.getCliente();
				Articulo articulo = filtro.getArticulo();
				Tipo marca = filtro.getMarca();
				long idCliente = cliente == null ? 0 : cliente.getId();
				long idMarca = marca == null ? 0 : marca.getId();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas = rr.getVentasPorCliente(idCliente, desde, hasta);

				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						for (VentaDetalle item : venta.getDetalles()) {
							if ((articulo != null && item.getArticulo().getCodigoInterno().equals(articulo.getCodigoInterno()))
									|| articulo == null) {
								if (marca != null && item.getArticulo().getArticuloMarca().getId().longValue() == idMarca
										|| marca == null) {
									data.add(new Object[] {
											m.dateToString(venta.getFecha(),
													Misc.DD_MM_YYYY),
											venta.getNumero(),
											venta.getCliente().getRazonSocial(),
											item.getArticulo().getCodigoInterno(),
											"TODOS..",
											item.getCantidad(),
											(item.getCantidad() * item.getPrecioGs()) });
								}
							}							
						}
					}
				}
				
				String cliente_ = cliente == null ? "TODOS.." : cliente.getRuc() + " / "
						+ cliente.getRazonSocial();
				
				String marca_ = marca == null ? "TODOS.." : marca.getDescripcion().toUpperCase();

				ReporteVentasPorClienteDetallado rep = new ReporteVentasPorClienteDetallado(
						desde, hasta, getAcceso().getSucursalOperativa()
								.getText(), cliente_, marca_);
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00011
		 */
		private void ventasAnuladas(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas = rr.getVentas(desde, hasta, 0);

				for (Venta venta : ventas) {
					if (venta.isAnulado()) {
						data.add(new Object[] {
								m.dateToString(venta.getFecha(),
										Misc.DD_MM_YYYY),
								venta.getNumero(),
								getMaxLength(
										venta.getDenominacion() == null ? venta
												.getCliente().getRazonSocial()
												: venta.getDenominacion(), 25),
								venta.getCliente().getRuc(),
								venta.getObservacion().toUpperCase(),
								venta.getTotalImporteGs() });
					}
				}

				ReporteVentasAnuladas rep = new ReporteVentasAnuladas(desde,
						hasta, getAcceso().getSucursalOperativa().getText());
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00012
		 */
		private void autorizacionesPrecios(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<ArticuloPrecioMinimo> auts = rr.getAutorizacionesPrecios(
						desde, hasta);

				for (ArticuloPrecioMinimo aut : auts) {
					Articulo art = rr.getArticuloById(aut.getIdArticulo());
					data.add(new Object[] {
							m.dateToString(aut.getFecha(), Misc.DD_MM_YYYY),
							art.getCodigoInterno(), art.getDescripcion(),
							aut.getPrecioMinimo() });
				}

				ReporteAutorizacionesPrecios rep = new ReporteAutorizacionesPrecios(
						desde, hasta, getAcceso().getSucursalOperativa()
								.getText());
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * VEN-00013
		 */
		private void rankingPorFamiliaDetallado(boolean mobile, boolean resumido) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE PARA VERSION MOVIL..");
				return;
			}
			
			try {
				ArticuloFamilia familia = filtro.getFamilia_();
				Object[] formato = filtro.getFormato();
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				String tipoRanking = filtro.getTipoRanking();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();
				
				long idFamilia = familia == null? 0 : familia.getId();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> vtas = rr.getVentasPorFamilia(idFamilia, desde, hasta);
				List<Object[]> ntcs = rr.getNotasDeCreditoPorFamilia(idFamilia, desde, hasta);
				for (Object[] nc : ntcs) {
					nc[3] = (int) nc[3] * -1;
				}
				vtas.addAll(ntcs);

				// ordena la lista segun el tipo de ranking..
				Collections.sort(vtas, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						long id1 = (long) o1[7];
						long id2 = (long) o2[7];
						return (int) (id1 - id2);
					}
				});

				String familia_ = familia == null? "TODOS.." : familia.getDescripcion();				
				String source_det = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_RANKING_VTAS_FLIA_DET;
				String source_res = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_RANKING_VTAS_FLIA_RES;
				String source = resumido ? source_res : source_det;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new RankingVentasPorFamiliaDetalladoDataSource(
						vtas, familia_, getSucursal(), desde, hasta, tipoRanking);
				params.put("Usuario", getUs().getNombre());
				params.put("titulo", resumido ? "RANKING DE VENTAS POR ARTICULO / FAMILIA DETALLADO" : "RANKING DE VENTAS POR ARTICULO / FAMILIA RESUMIDO");
				imprimirJasper(source, params, dataSource, formato);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte VEN-00014
		 */
		private void rankingDeClientes(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				Map<Long, Object[]> acumulador = new HashMap<Long, Object[]>();
				Map<Long, Cliente> clientes_ = new HashMap<Long, Cliente>();
				Set<Cliente> clientes = new HashSet<Cliente>();
				List<Object[]> data = new ArrayList<Object[]>();

				List<Venta> vtas = rr.getVentas(desde, hasta, 0);
				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);

				for (Venta venta : vtas) {
					if (!venta.isAnulado()) {
						Object[] acum = acumulador.get(venta.getCliente().getId().longValue());
						double importeGs = venta.getTotalImporteGsSinIva();
						double costoGs = venta.getTotalCostoGsSinIva();
						if (acum != null) {
							importeGs += (double) acum[0];
							costoGs += (double) acum[1];
						}							
						acumulador.put(venta.getCliente().getId().longValue(), new Object[] { importeGs, costoGs });
						clientes_.put(venta.getCliente().getId().longValue(), venta.getCliente());
					}
				}

				for (NotaCredito nc : ncs) {
					if (!nc.isAnulado()) {
						Object[] acum = acumulador.get(nc.getCliente().getId().longValue());
						double importeGs = nc.getTotalImporteGsSinIva() * -1;
						double costoGs = nc.getTotalCostoGsSinIva() > 0 ? nc.getTotalCostoGsSinIva() * -1 : 0.0;
						if (acum != null) {
							importeGs += (double) acum[0];
							costoGs += (double) acum[1];
						}							
						acumulador.put(nc.getCliente().getId().longValue(), new Object[]{ importeGs, costoGs });
						clientes_.put(nc.getCliente().getId().longValue(), nc.getCliente());
					}
				}

				for (Long key : clientes_.keySet()) {
					clientes.add(clientes_.get(key));
				}
				
				for (Cliente cliente : clientes) {
					Object[] acum = acumulador.get(cliente.getId().longValue());
					data.add(new Object[] { (int) 0, cliente.getRuc(), cliente.getRazonSocial(),
							Utiles.getRedondeo((double) acum[0]), Utiles.getRedondeo((double) acum[1]) });
				}

				// ordena la lista segun el tipo de ranking..
				Collections.sort(data, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						double importe1 = (double) o1[3];
						double importe2 = (double) o2[3];
						return (int) (importe2 - importe1);
					}
				});

				int index = 1;
				for (Object[] obj : data) {
					obj[0] = index;
					index++;
				}

				ReporteRankingDeClientes rep = new ReporteRankingDeClientes(desde, hasta, getAcceso().getSucursalOperativa().getText());
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * VEN-00015
		 */
		private void rankingClientesArticulos(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Cliente cliente = filtro.getCliente();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();
				if (cliente == null) cliente = new Cliente();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas = rr.getVentasPorCliente(cliente.getId(), desde, hasta);
				Map<String, Object[]> result = new HashMap<String, Object[]>();
				Map<String, Articulo> articulo = new HashMap<String, Articulo>();

				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						for (VentaDetalle item : venta.getDetalles()) {
							Object[] acum = result.get(item.getArticulo().getCodigoInterno());
							if (acum != null) {
								acum[0] = (long) acum[0] + item.getCantidad();
								acum[1] = (double) acum[1] + item.getPrecioGs();
							} else {
								result.put(
										item.getArticulo().getCodigoInterno(),
										new Object[] { item.getCantidad(),
												item.getPrecioGs() });
								articulo.put(item.getArticulo()
										.getCodigoInterno(), item.getArticulo());
							}
						}
					}
				}

				for (String key : result.keySet()) {
					Articulo art = articulo.get(key);
					data.add(new Object[] { (int) 0, art.getCodigoInterno(),
							art.getDescripcion(), result.get(key)[0],
							result.get(key)[1] });
				}

				// ordena la lista segun el tipo de ranking..
				Collections.sort(data, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						long cant1 = (long) o1[3];
						long cant2 = (long) o2[3];
						return (int) (cant2 - cant1);
					}
				});

				int index = 1;
				for (Object[] obj : data) {
					obj[0] = index;
					index++;
				}

				ReporteRankingVentasClienteArticulo rep = new ReporteRankingVentasClienteArticulo(
						desde, hasta, getAcceso().getSucursalOperativa()
								.getText(), cliente.getRuc() + " / "
								+ cliente.getRazonSocial());
				rep.setDatosReporte(data);
				rep.setApaisada();				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00016 comision por cobros de ventas..
		 */
		private void comisionPorCobros(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE PARA VERSION MOVIL..");
				return;
			}
			try {
				Object[] formato = filtro.getFormato();
				MyArray mes = filtro.getSelectedMes(); 
				String anho = filtro.getSelectedAnho();
				
				if (mes == null) {
					Clients.showNotification("Debe indicar el mes..", 
							Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}

				int mes_ = (int) mes.getPos1();
				RegisterDomain rr = RegisterDomain.getInstance();	
				List<HistoricoComisiones> comisiones = rr.getHistoricoComisiones(mes_, anho);
				List<MyArray> values = new ArrayList<MyArray>();

				for (HistoricoComisiones com : comisiones) {
					values.add(new MyArray(
							com.getVendedor(),
							com.getProveedor(),
							Utiles.getRedondeo(com.getImporteVenta()),
							Utiles.getRedondeo(com.getImporteCobro()),
							Utiles.getRedondeo(com.getImporteNotaCredito()),
							Utiles.getRedondeo((com.getImporteVenta() + com.getImporteCobro()))));
				}
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_COMISION_COBRO;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new ComisionCobroDataSource(values);
				params.put("Titulo", "Comisión por Ventas Cobradas (S/iva)");
				params.put("Usuario", getUs().getNombre());
				params.put("Vendedor", "TODOS..");
				params.put("mes", mes.getPos2());
				params.put("anho", anho);
				imprimirJasper(source, params, dataSource, formato);				

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		class BeanComision {
			double importeVenta;
			double importeCobro;
			double importeNcred;

			public double getImporteVenta() {
				return importeVenta;
			}

			public void setImporteVenta(double importeVenta) {
				this.importeVenta = importeVenta;
			}

			public double getImporteCobro() {
				return importeCobro;
			}

			public void setImporteCobro(double importeCobro) {
				this.importeCobro = importeCobro;
			}

			public double getImporteNcred() {
				return importeNcred;
			}

			public void setImporteNcred(double importeNcred) {
				this.importeNcred = importeNcred;
			}
		}
		
		/**
		 * VEN-00017 cantidad vendida por articulo..
		 */
		private void cantidadVendidaPorArticulo(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Articulo articulo = filtro.getArticulo();
				Proveedor fabrica = filtro.getProveedorExterior();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				Map<String, MyArray> acum = new HashMap<String, MyArray>();
				List<Object[]> data = new ArrayList<Object[]>();		

				List<Venta> ventas = rr.getVentas(desde, hasta, 0);
				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);				
				
				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						for (VentaDetalle item : venta.getDetalles()) {
							if ((articulo == null && fabrica == null)
									|| (articulo != null && articulo.getId().longValue() == item.getArticulo().getId().longValue())
									|| (fabrica != null && item.getArticulo().isProveedor(fabrica.getId().longValue()))) {
								MyArray art = acum.get(item.getArticulo().getCodigoInterno());
								if (art != null) {
									long cantv = (long) art.getPos1();
									art.setPos1(cantv + item.getCantidad());
								} else {
									art = new MyArray();
									art.setPos1(item.getCantidad());
									art.setPos2((long) 0);
									art.setPos3(item.getArticulo());
								}
								acum.put(item.getArticulo().getCodigoInterno(), art);
							}
						}
					}					
				}
				
				for (NotaCredito notaCred : ncs) {
					if ((!notaCred.isAnulado()) && notaCred.isMotivoDevolucion()) {
						for (NotaCreditoDetalle item : notaCred.getDetallesArticulos()) {
							if ((articulo == null && fabrica == null)
									|| (articulo != null && articulo.getId().longValue() == item.getArticulo().getId().longValue())
									|| (fabrica != null && item.getArticulo().isProveedor(fabrica.getId().longValue()))) {
								MyArray art = acum.get(item.getArticulo().getCodigoInterno());
								if (art != null) {
									if (art.getPos2() instanceof Integer) {
										int cantn = (int) art.getPos2();
										art.setPos2(cantn + item.getCantidad());
									} else {
										long cantn = (long) art.getPos2();
										art.setPos2(cantn + item.getCantidad());
									}									
								} else {
									art = new MyArray();
									art.setPos1((long) 0);
									art.setPos2(item.getCantidad());
									art.setPos3(item.getArticulo());
								}
								acum.put(item.getArticulo().getCodigoInterno(), art);
							}
						}
					}					
				}
				
				for (String key : acum.keySet()) {
					MyArray value = acum.get(key);
					Articulo art = (Articulo) value.getPos3();
					long cantv = (long) value.getPos1();
					long cantn = 0;
					if (value.getPos2() instanceof Integer) {
						cantn = ((Integer) value.getPos2()).longValue();
					} else if (value.getPos2() instanceof Long) {
						cantn = (long) value.getPos2();
					}
					long stock = rr.getStock(art.getId().longValue());
					double costo = rr.getCostoGs(art.getId().longValue());
					data.add(new Object[] { key, art.getDescripcion(), cantv, cantn, (cantv - cantn), stock, costo });
				}
				
				String articulo_ = articulo == null ? "TODOS.." : articulo.getDescripcion();
				String fabrica_ = fabrica == null ? "TODOS.." : fabrica.getRazonSocial();
				ReporteCantidadVendida rep = new ReporteCantidadVendida(desde,
						hasta, articulo_, fabrica_, getSucursal());
				rep.setApaisada();
				rep.setDatosReporte(data);
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00018
		 */
		private void totalCobranzasPorVendedor(boolean mobile, String codReporte) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> cobros = rr.getCobranzasPorVendedor(desde, hasta, 0, getIdSucursal());
				Map<Long, Double> values = new HashMap<Long, Double>();

				for (Object[] cobro : cobros) {
					long idVend = (long) cobro[4];
					Double total = values.get(idVend);
					if (total != null) {
						total += (double) cobro[2];
					} else {
						total = (double) cobro[2];
					}
					values.put(idVend, total);
				}
				
				for (Funcionario vendedor : filtro.getVendedores()) {
					long idVend = vendedor.getId();
					List<AjusteCtaCte> apls = rr.getAplicacionesAnticipos(desde, hasta, vendedor.getId().longValue());
					for (AjusteCtaCte apl : apls) {
						if (apl.getCredito().isVentaCredito()) {						
							Double total = values.get(idVend);
							if (total != null) {
								total += apl.getImporte();
							} else {
								total = apl.getImporte();
							}
							values.put(idVend, total);
						}					
					}
				}
				
				for (Funcionario vendedor : filtro.getVendedores()) {
					Double cobrado = values.get(vendedor.getId());					
					if (cobrado != null) {
						cobrado = Utiles.getRedondeo(cobrado);
						double cobradoSinIva = (cobrado - Utiles.getIVA(cobrado, 10));
						cobradoSinIva = Utiles.getRedondeo(cobradoSinIva);
						data.add(new Object[]{ vendedor.getRazonSocial().toUpperCase(), cobrado, cobradoSinIva });
					}
				}
				
				Collections.sort(data, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String val1 = (String) o1[0];
						String val2 = (String) o2[0];
						int compare = val1.compareTo(val2);				
						return compare;
					}
				});

				ReporteTotalCobranzas rep = new ReporteTotalCobranzas(desde, hasta, getAcceso().getSucursalOperativa().getText());
				rep.setDatosReporte(data);
				rep.setTitulo(codReporte + " - Total Cobranzas por Vendedor");
				rep.setApaisada();			

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00019 ventas por clientes por mes..
		 */
		private void ventasClientesPorMes(boolean mobile, String codReporte) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE PARA VERSION MOVIL..");
				return;
			}
			
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				Cliente cliente_ = filtro.getCliente();
				Tipo rubro = filtro.getRubro();
				boolean ivaInc = filtro.isIvaIncluido();
				boolean ncrInc = filtro.isIncluirNCR();
				Object[] formato = filtro.getFormato();
				long idCliente = cliente_ != null ? cliente_.getId().longValue() : 0;

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Venta> ventas = rr.getVentas(desde, hasta, idCliente);
				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, idCliente);
				List<Object[]> data = new ArrayList<Object[]>();
				Map<String, Object[]> values = new HashMap<String, Object[]>();
				Map<String, Object[]> values_ = new HashMap<String, Object[]>();
				
				double vtas = 0;
				for (Venta venta : ventas) {
					int mes = Utiles.getNumeroMes(venta.getFecha()) - 1;
					if (vendedor == null || vendedor.getId().longValue() == venta.getVendedor().getId().longValue()) {
						if (rubro == null || rubro.getDescripcion().equals(venta.getCliente().getRubro())) {
							if (!venta.isAnulado()) {
								vtas += venta.getTotalImporteGsSinIva();
								String id = venta.getCliente().getId() + "";
								String key = id + "-" + mes;
								String cliente = venta.getCliente().getRazonSocial();
								String vendedor_ = venta.getVendedor().getRazonSocial();
								String rubro_ = venta.getCliente().getRubro();
								Long idEmpresa = venta.getCliente().getEmpresa().getId();
								Object[] acum = values.get(key);
								if (acum != null) {
									double importe = (double) acum[0];
									double importe_ = ivaInc ? venta.getTotalImporteGs_() : venta.getTotalImporteGsSinIva();
									importe += importe_;
									values.put(key, new Object[] { importe, mes, cliente, vendedor_, rubro_, idEmpresa });
								} else {
									double importe_ = ivaInc ? venta.getTotalImporteGs_() : venta.getTotalImporteGsSinIva();
									values.put(key,
											new Object[] { importe_, mes, cliente, vendedor_, rubro_, idEmpresa });
								}
							}
						}						
					}					
				}
				
				double ncrs = 0;
				if (ncrInc) {					
					for (NotaCredito ncred : ncs) {
						int mes = Utiles.getNumeroMes(ncred.getFechaEmision()) - 1;
						if (vendedor == null || vendedor.getId().longValue() == ncred.getVendedor().getId().longValue()) {
							if (rubro == null || rubro.getDescripcion().equals(ncred.getCliente().getRubro())) {
								if (!ncred.isAnulado()) {
									ncrs += ncred.getTotalImporteGsSinIva();
									String id = ncred.getCliente().getId() + "";
									String key = id + "-" + mes;
									String cliente = ncred.getCliente().getRazonSocial();
									String vendedor_ = ncred.getVendedor().getRazonSocial();
									String rubro_ = ncred.getCliente().getRubro();
									Long idEmpresa = ncred.getCliente().getEmpresa().getId();
									Object[] acum = values.get(key);
									if (acum != null) {
										double importe = (double) acum[0];
										double importe_ = ivaInc ? ncred.getImporteGs() : ncred.getTotalImporteGsSinIva();
										importe -= importe_;
										values.put(key, new Object[] { importe, mes, cliente, vendedor_, rubro_, idEmpresa });
									} else {
										double importe_ = ivaInc ? ncred.getImporteGs() : ncred.getTotalImporteGsSinIva();
										values.put(key,
												new Object[] { importe_ * -1, mes, cliente, vendedor_, rubro_, idEmpresa });
									}
								}
							}							
						}						
					}
				}		
				
				System.out.println("--TOTAL VTAS: " + Utiles.getNumberFormat(vtas));
				System.out.println("--TOTAL NCRS: " + Utiles.getNumberFormat(ncrs));
				System.out.println("--TOTAL VTNC: " + Utiles.getNumberFormat(vtas - ncrs));
				
				for (String key : values.keySet()) {
					Object[] value = values.get(key);
					double importe = (double) value[0];
					int mes = (int) value[1];
					String id = (String) value[2];
					String vend = (String) value[3];
					String rubro_ = (String) value[4];
					Long idEmpresa = (Long) value[5];
					
					Object[] value_ = values_.get(id);
					if (value_ != null) {
						value_[mes] = importe;
						values_.put(id, value_);
					} else {
						Object[] datos = new Object[] { (double) 0, (double) 0,
								(double) 0, (double) 0, (double) 0, (double) 0,
								(double) 0, (double) 0, (double) 0, (double) 0,
								(double) 0, (double) 0, vend, rubro_, idEmpresa };
						datos[mes] = importe;
						values_.put(id, datos);
					}
				}
				
				for (String key : values_.keySet()) {
					Object[] value_ = values_.get(key);
					long idEmpresa = (long) value_[14];
					double saldo = (double) rr.getTotalSaldoCtaCte(idEmpresa, Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE, 31)[1];
					
					double total = (double) value_[0] + (double) value_[1]
							+ (double) value_[2] + (double) value_[3]
							+ (double) value_[4] + (double) value_[5]
							+ (double) value_[6] + (double) value_[7]
							+ (double) value_[8] + (double) value_[9]
							+ (double) value_[10] + (double) value_[11];
					data.add(new Object[] { key.toUpperCase(), value_[0],
							value_[1], value_[2], value_[3], value_[4],
							value_[5], value_[6], value_[7], value_[8],
							value_[9], value_[10], value_[11], total, value_[12], value_[13], saldo });
				}
				String format = (String) formato[0];
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_POR_CLIENTES;
				String csv = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_CSV[0];
				String xls = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS[0];
				if (format.equals(csv) || format.equals(xls)) {
					source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_POR_CLIENTES_;
				}
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new VentasPorClienteDataSource(data);
				params.put("Titulo", codReporte + " - Ventas por Clientes por Mes");
				params.put("Usuario", getUs().getNombre());
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("Vendedor", vendedor == null ? "TODOS.." : vendedor.getRazonSocial());
				params.put("Rubro", rubro == null ? "TODOS.." : rubro.getDescripcion());
				params.put("NCR_INC", filtro.isIncluirNCR() ? "SI" : "NO");
				params.put("IVA_INC", filtro.isIvaIncluido() ? "SI" : "NO");
				imprimirJasper(source, params, dataSource, formato);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00020
		 */
		private void pedidosPendientes(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				long idSuc = getAcceso().getSucursalOperativa().getId();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas = rr.getPedidosSinFacturar(idSuc, desde, hasta);

				for (Venta venta : ventas) {
					data.add(new Object[] {
							m.dateToString(venta.getFecha(), Misc.DD_MM_YYYY),
							venta.getNumero(),
							getMaxLength(venta.getDenominacion() == null ? 
											venta.getCliente().getRazonSocial()
											: venta.getDenominacion(), 25),
							venta.getCliente().getRuc(),
							venta.getObservacion().toUpperCase(),
							venta.getTotalImporteGs() });
				}
				String titulo = "Pedidos Pendientes de Facturación";
				ReportePedidosPendientes rep = new ReportePedidosPendientes(desde,
						hasta, getAcceso().getSucursalOperativa().getText(), titulo);
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00021
		 */
		private void ventasRecibos(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Cliente cliente = filtro.getCliente();
				long idCliente = 0;

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();
				
				if (cliente != null) {
					idCliente = cliente.getId();
				}

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas = rr.getVentasCredito(desde, hasta, idCliente);

				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						Recibo rec = rr.getReciboByVenta(venta.getId(), venta.getTipoMovimiento().getId());
						NotaCredito nc = rr.getNotaCreditoByVenta(venta.getId());
						List<CtaCteEmpresaMovimiento> ctactes = rr.getCtaCteMovimientosByIdMovimiento(venta.getId(), venta.getTipoMovimiento().getSigla());
						String fechaVta = Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YYYY);
						String nroVta = venta.getNumero();
						String razonSocial = venta.getCliente().getRazonSocial();
						String fechaRec = rec == null ? "- - -" : Utiles.getDateToString(rec.getFechaEmision(), Utiles.DD_MM_YYYY);
						String nroRec = rec == null ? "- - -" : rec.getNumero();
						String nroNcr = nc == null ? "- - -" : nc.getNumero();
						String motivo = nc == null ? "- - -" : nc.getMotivo().getSigla();
						double saldo = 0;
						for (CtaCteEmpresaMovimiento movim : ctactes) {
							saldo += movim.getSaldo();
						}
						data.add(new Object[]{ fechaVta, nroVta, razonSocial, fechaRec, nroRec, nroNcr, motivo, saldo });
					}
				}
				String cliente_ = cliente == null? "TODOS.." : cliente.getRazonSocial();
				ReporteVentasRecibos rep = new ReporteVentasRecibos(desde,
						hasta, cliente_ , getAcceso().getSucursalOperativa().getText());
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00022
		 */
		private void presupuestosPendientes(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				long idSuc = getAcceso().getSucursalOperativa().getId();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas = rr.getPresupuestosSinAprobar(idSuc, desde, hasta);

				for (Venta venta : ventas) {
					data.add(new Object[] {
							m.dateToString(venta.getFecha(), Misc.DD_MM_YYYY),
							venta.getNumero(),
							getMaxLength(venta.getDenominacion() == null ? 
											venta.getCliente().getRazonSocial()
											: venta.getDenominacion(), 25),
							venta.getCliente().getRuc(),
							venta.getObservacion().toUpperCase(),
							venta.getTotalImporteGs() });
				}

				String titulo = "Presupuestos Pendientes de Facturación";
				ReportePedidosPendientes rep = new ReportePedidosPendientes(desde,
						hasta, getSucursal(), titulo);
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-000.. ventas por vendedor agrupado..
		 */
		private void ventasPorVendedorAgrupado(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				List<Funcionario> vendedores = filtro.getVendedores();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();	
				List<Object[]> values = new ArrayList<Object[]>();

				List<Venta> ventas = null;
				List<NotaCredito> notasCredito = null;
				
				if (vendedor != null) {					
					ventas = rr.getVentasPorVendedor(vendedor.getId(), desde, hasta);
					notasCredito = rr.getNotasCreditoVenta(desde, hasta, 0);
				} else {
					ventas = rr.getVentas(desde, hasta, 0);
					notasCredito = rr.getNotasCreditoVenta(desde, hasta, 0);
				}
				
				for (Funcionario vend : vendedores) {
					double importeVenta = 0.0;
					double importeNcred = 0.0;
					
					for (Venta venta : ventas) {
						if ((!venta.isAnulado()) && venta.getVendedor().getId().longValue() == vend.getId().longValue()) {
							double importe = venta.getTotalImporteGs();
							importeVenta += (importe - Utiles.getIVA(importe, 10));
						}
					}
					
					for (NotaCredito nc : notasCredito) {
						if ((!nc.isAnulado()) && nc.getVendedor().getId().longValue() == vend.getId().longValue()) {
							double importe = nc.getImporteGs();
							importeNcred += (importe - Utiles.getIVA(importe, 10));
						}
					}
					
					if (importeVenta > 0 || importeNcred > 0) {
						values.add(new Object[]{
								vend.getRazonSocial(),
								Utiles.getRedondeo(importeVenta),
								Utiles.getRedondeo(importeNcred),
								Utiles.getRedondeo(importeVenta - importeNcred)});
					}									
				}			
				
				String vendedor_ = vendedor == null ? "TODOS.." : vendedor.getRazonSocial();
				ReporteVentasVendedorAgrupado rep = new ReporteVentasVendedorAgrupado(desde,
						hasta, getSucursal(), vendedor_.toUpperCase());
				rep.setApaisada();
				rep.setDatosReporte(values);

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00023..
		 */
		private void ventasUtilidadDetallado(boolean mobile, boolean resumido, String codReporte) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}			
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Articulo art = filtro.getArticulo();
				ArticuloPresentacion pres = filtro.getPresentacion();
				Cliente cliente = filtro.getCliente();
				Object[] formato = filtro.getFormato();
				Funcionario vendedor = filtro.getVendedor();
				SucursalApp suc = filtro.getSelectedSucursal();
				ArticuloFamilia familia = filtro.getFamilia_();
				EmpresaRubro rubro = filtro.getRubro_();
				ArticuloMarca marca = filtro.getMarca_();
				Proveedor proveedor = filtro.getProveedor();
				
				long totalCantidad = 0;
				double totalImporte = 0;
				double totalCosto = 0;
				
				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				long idCliente = cliente == null ? 0 : cliente.getId().longValue();
				long idRubro = rubro == null ? 0 : rubro.getId().longValue();
				long idSucursal = suc == null ? 0 : suc.getId().longValue();
				long idVendedor = vendedor == null ? 0 : vendedor.getId().longValue();
				long idFamilia = familia == null ? 0 : familia.getId().longValue();
				long idMarca = marca == null ? 0 : marca.getId().longValue();
				long idProveedor = proveedor == null ? 0 : proveedor.getId().longValue();
				long idPresentacion = pres == null ? 0 : pres.getId().longValue();

				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, idCliente, idRubro, idSucursal, idVendedor, "", null);
				for (NotaCredito notacred : ncs) {
					if (!notacred.isAnulado()) {
						String motivo = notacred.getMotivo().getDescripcion().substring(0, 3).toUpperCase() + ".";
						for (NotaCreditoDetalle item : notacred.getDetallesArticulos()) {
							Object[] nc = new Object[] {
									Utiles.getDateToString(notacred.getFechaEmision(), "dd-MM-yyyy"),
									notacred.getNumero(),
									notacred.isNotaCreditoVentaContado() ? "NC-CO " + motivo : "NC-CR " + motivo,
									notacred.getCliente().getRazonSocial().toUpperCase(),
									notacred.getCliente().getRubro().toUpperCase(),
									notacred.getVendedor().getRazonSocial().toUpperCase(),
									item.getArticulo().getCodigoInterno(),
									item.getArticulo().getMarca().getDescripcion().toUpperCase(),
									item.getArticulo().getFamilia().getDescripcion().toUpperCase(),
									notacred.isAnulado() || !notacred.isMotivoDevolucion() ? 0.0 : item.getCostoGs() * -1,
									notacred.isAnulado() || !notacred.isMotivoDevolucion() ? (long) 0 : Long.parseLong((item.getCantidad() * -1) + ""),
									notacred.isAnulado() || !notacred.isMotivoDevolucion() ? 0.0 : item.getCostoTotalGsSinIva() * -1,
									notacred.isAnulado() ? 0.0 : item.getImporteGsSinIva() * -1,
									notacred.isAnulado() ? 0.0 : item.getRentabilidad() * -1,
									item.getArticulo().getDescripcion(),
									notacred.isAnulado() ? 0.0 : notacred.getRentabilidadVenta() * -1,
									notacred.isAnulado() ? 0.0 : (item.getImporteGsSinIva() - item.getCostoTotalGsSinIva()) * -1,
									notacred.getVentaAplicada().getNombreTecnico() };
							if (art == null || art.getId().longValue() == item.getArticulo().getId().longValue()) {
								if (familia == null || idFamilia == item.getArticulo().getFamilia().getId().longValue()) {
									if (marca == null || idMarca == item.getArticulo().getMarca().getId().longValue()) {
										if (proveedor == null || (item.getArticulo().getProveedor() != null && idProveedor == item.getArticulo().getProveedor().getId().longValue())) {
											if (pres == null || (item.getArticulo().getPresentacion() != null && idPresentacion == item.getArticulo().getPresentacion().getId().longValue())) {
												data.add(nc);
											}											
										}										
									}															
								}
							}
						}
						if (art == null && familia == null && marca == null && proveedor == null && pres == null && notacred.isMotivoDescuento()) {
							Object[] nc = new Object[] {
									Utiles.getDateToString(notacred.getFechaEmision(), "dd-MM-yyyy"),
									notacred.getNumero(),
									notacred.isNotaCreditoVentaContado() ? "NC-CO " + motivo : "NC-CR " + motivo,
									notacred.getCliente().getRazonSocial().toUpperCase(),
									notacred.getCliente().getRubro().toUpperCase(),
									notacred.getVendedor().getRazonSocial().toUpperCase(),
									"DESCUENTO CONCEDIDO",
									" - - - ", " - - - ",
									0.0,
									(long) 0,
									0.0,
									notacred.isAnulado() ? 0.0 : notacred.getTotalImporteGsSinIva() * -1,
									notacred.isAnulado() ? 0.0 : notacred.getRentabilidad() * -1,
									"DESCUENTO CONCEDIDO",
									notacred.isAnulado() ? 0.0 : notacred.getRentabilidadVenta() * -1,
									notacred.isAnulado() ? 0.0 : 0.0,
									notacred.getVentaAplicada().getNombreTecnico() };
							data.add(nc);
						}
					}					
				}

				List<Venta> ventas = rr.getVentas(desde, hasta, idCliente, idRubro, idSucursal, idVendedor);
				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						for (VentaDetalle item : venta.getDetalles()) {
							Object[] vta = new Object[] {
									Utiles.getDateToString(venta.getFecha(), "dd-MM-yyyy"),
									venta.getNumero(),
									"FAC. " + venta.getCondicionPago().getDescripcion().substring(0, 3).toUpperCase(),
									venta.getCliente().getRazonSocial().toUpperCase(),
									venta.getCliente().getRubro().toUpperCase(),
									venta.getVendedor().getRazonSocial().toUpperCase(),
									item.getArticulo().getCodigoInterno(),
									item.getArticulo().getMarca().getDescripcion().toUpperCase(),
									item.getArticulo().getFamilia().getDescripcion().toUpperCase(),
									venta.isAnulado() ? 0.0 : item.getCostoUnitarioGs(),
									venta.isAnulado() ? (long) 0 : item.getCantidad(),
									venta.isAnulado() ? 0.0 : item.getCostoTotalGsSinIva(),
									venta.isAnulado() ? 0.0 : item.getImporteGsSinIva(),
									venta.isAnulado() ? 0.0 : item.getRentabilidad(),
									item.getArticulo().getDescripcion(),
									venta.isAnulado() ? 0.0 : item.getRentabilidadVenta(),
									venta.isAnulado() ? 0.0 : (item.getImporteGsSinIva() - item.getCostoTotalGsSinIva()),
									venta.getNombreTecnico() };
							if (art == null || art.getId().longValue() == item.getArticulo().getId().longValue()) {
								if (familia == null || idFamilia == item.getArticulo().getFamilia().getId().longValue()) {
									if (marca == null || idMarca == item.getArticulo().getMarca().getId().longValue()) {
										if (proveedor == null || (item.getArticulo().getProveedor() != null && idProveedor == item.getArticulo().getProveedor().getId().longValue())) {
											if (pres == null || (item.getArticulo().getPresentacion() != null && idPresentacion == item.getArticulo().getPresentacion().getId().longValue())) {
												data.add(vta);
											}											
										}									
									}
								}
							}
						}
					}					
				}
				for (Object[] obj : data) {
					long cant = (long) obj[10];
					double costo = (double) obj[11];
					double importe = (double) obj[12];
					totalCantidad += cant;
					totalCosto += costo;
					totalImporte += importe;
				}
				double utilidad = totalImporte - totalCosto;
				double promedioSobreCosto = Utiles.obtenerPorcentajeDelValor(utilidad, totalCosto);
				double promedioSobreVenta = Utiles.obtenerPorcentajeDelValor(utilidad, totalImporte);
				promedioSobreCosto = Utiles.getRedondeo(promedioSobreCosto);
				promedioSobreVenta = Utiles.getRedondeo(promedioSobreVenta);
				
				if (!resumido) {
					String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_UTILIDAD_DETALLADO;
					if (!formato[0].equals("PDF")) {
						source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_UTILIDAD_DETALLADO_SIN_CAB;
					}
					Map<String, Object> params = new HashMap<String, Object>();
					JRDataSource dataSource = new VentasUtilidadDetallado(data);
					params.put("Titulo", codReporte + " - Ventas y Utilidad por Articulo Detallado");
					params.put("Usuario", getUs().getNombre());
					params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
					params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
					params.put("Promedio", Utiles.getNumberFormat(promedioSobreCosto));
					params.put("TOT_VTA_NETA", Utiles.getNumberFormat(totalImporte));
					params.put("TOT_COSTO", Utiles.getNumberFormat(totalCosto));
					params.put("TOT_UTILIDAD", Utiles.getNumberFormat((totalImporte - totalCosto)));
					params.put("TOT_MARGEN_VTA", Utiles.getNumberFormat(promedioSobreVenta));
					params.put("TOT_MARGEN_COSTO", Utiles.getNumberFormat(promedioSobreCosto));
					params.put("TOT_CANTIDAD", Utiles.getNumberFormat(totalCantidad));
					params.put("TIPO_COSTO", "ÚLTIMO COSTO");
					imprimirJasper(source, params, dataSource, formato);
					
				} else {
					List<Object[]> values = new ArrayList<Object[]>();
					values.add(new Object[] { totalImporte, totalCosto, (totalImporte - totalCosto), promedioSobreVenta, promedioSobreCosto });
					ReporteVentasUtilidadResumido rep = new ReporteVentasUtilidadResumido(desde, hasta, getSucursal());
					rep.setApaisada();
					rep.setDatosReporte(values);

					if (!mobile) {
						ViewPdf vp = new ViewPdf();
						vp.setBotonImprimir(false);
						vp.setBotonCancelar(false);
						vp.showReporte(rep, ReportesViewModel.this);
					} else {
						rep.ejecutar();
						Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
					}
				}
				

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * VEN-00024
		 */
		private void ventasPreparadorRepartidor(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas = rr.getVentas(desde, hasta, 0);

				for (Venta venta : ventas) {
					data.add(new Object[] {
							Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YYYY),
							venta.getNumero(),
							venta.getCliente().getRazonSocial().toUpperCase(),
							venta.getPreparadoPor(),
							venta.getRepartidor() });
				}

				ReportePreparadorRepartidor rep = new ReportePreparadorRepartidor(desde, hasta);
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		
		/**
		 * VEN-00025
		 */
		private void ventasPerdidas(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<VentaPerdida> vps = rr.getVentasPerdidas(desde, hasta);

				for (VentaPerdida vp : vps) {
					data.add(new Object[] {
							Utiles.getDateToString(vp.getFecha(), Utiles.DD_MM_YYYY),
							vp.getArticulo().toUpperCase(),
							vp.getMotivo().toUpperCase(),
							vp.getCliente().toUpperCase(),
							vp.getUsuarioMod().toUpperCase()});
				}

				ReporteVentasPerdidas rep = new ReporteVentasPerdidas(desde, hasta);
				rep.setDatosReporte(data);
				rep.setApaisada();				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00026
		 */
		private void ventasPorVendedorGenerico(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				String vendedor_ = vendedor == null ? "TODOS.." : vendedor.getRazonSocial();
				long idVendedor = vendedor == null ? 0 : vendedor.getId();
				
				if (vendedor == null) {
					Clients.showNotification("Debe seleccionar un vendedor..", 
							Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				double totalImporte = 0;

				// Notas de Credito..
				List<NotaCredito> ncs = rr.getNotasCreditoVentaVendedor(desde, hasta, idVendedor);
				for (NotaCredito notacred : ncs) {
					int length = notacred.getCliente().getRazonSocial().length();
					int maxlength = length > 25 ? 25 : length;
					String motivo = notacred.getMotivo().getDescripcion().substring(0, 3).toUpperCase() + ".";
					Object[] nc = new Object[] {
							Utiles.getDateToString(notacred.getFechaEmision(), Utiles.DD_MM_YY),
							notacred.getNumero(),
							notacred.getCliente().getRazonSocial().substring(0, maxlength) + "..",
							notacred.getCliente().getRuc(),
							notacred.isNotaCreditoVentaContado() ? "NC-CO " + motivo : "NC-CR " + motivo,
							notacred.isAnulado() ? 0.0 : notacred.getImporteGs() * -1 };
					data.add(nc);
					totalImporte += (notacred.isAnulado() ? 0.0 : notacred.getImporteGs() * -1);
				}

				// Ventas..
				List<Venta> ventas = rr.getVentasPorVendedor(idVendedor, desde, hasta);
				for (Venta venta : ventas) {
					Object[] vta = new Object[] {
							Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YY),
							venta.getNumero(),
							getMaxLength(venta.getDenominacion() == null ? 
									venta.getCliente().getRazonSocial() : venta.getDenominacion(), 25),
							venta.getCliente().getRuc(),
							"FAC. " + venta.getCondicionPago().getDescripcion().substring(0, 3).toUpperCase(),
							venta.isAnulado() ? 0.0 : venta.getTotalImporteGs() };
					data.add(vta);
					totalImporte += (venta.isAnulado() ? 0.0 : venta.getTotalImporteGs());
				}		

				double totalSinIva = totalImporte - m.calcularIVA(totalImporte, 10);
				String sucursal = getAcceso().getSucursalOperativa().getText();
				String familias_ = "";

				ReporteVentasGenerico rep = new ReporteVentasGenerico(
						totalSinIva, desde, hasta, vendedor_, "TODOS..", sucursal, "TODOS..", familias_);
				rep.setDatosReporte(data);
				
				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00027
		 */
		private void clientesPorVendedor(boolean mobile, String codReporte) {
			try {
				Funcionario vendedor = filtro.getVendedor();
				Object[] formato = filtro.getFormato();
				
				String vendedor_ = vendedor == null ? "TODOS.." : vendedor.getRazonSocial();
				long idVendedor = vendedor == null ? 0 : vendedor.getId();
				
				if (vendedor == null) {
					Clients.showNotification("Debe seleccionar un vendedor..", 
							Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}

				RegisterDomain rr = RegisterDomain.getInstance();				
				List<Object[]> emps = rr.getClientesPorVendedor(idVendedor);
				
				String format = (String) formato[0];
				String csv = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_CSV[0];
				String xls = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS[0];
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_CLIENTES_VENDEDOR;
				if (format.equals(csv) || format.equals(xls)) {
					source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_CLIENTES_VENDEDOR_;
				}
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new ClientesVendedorDataSource(emps);
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", codReporte + " - CLIENTES POR VENDEDOR");
				params.put("Vendedor", vendedor_);
				imprimirJasper(source, params, dataSource, formato);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00028
		 */
		private void ventasGenerico(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Cliente cliente = filtro.getCliente();
				String cliente_ = cliente == null ? "TODOS.." : cliente
						.getRazonSocial();
				long idCliente = cliente == null ? 0 : cliente.getId();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				double totalImporte = 0;

				if (filtro.isIncluirNCR() || filtro.isIncluirNCR_CRED()) {
					List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde,
							hasta, idCliente);
					for (NotaCredito notacred : ncs) {
						int length = notacred.getCliente().getRazonSocial()
								.length();
						int maxlength = length > 25 ? 25 : length;
						String motivo = notacred.getMotivo().getDescripcion()
								.substring(0, 3).toUpperCase()
								+ ".";
						Object[] nc = new Object[] {
								m.dateToString(notacred.getFechaEmision(),
										"dd-MM-yy"),
								notacred.getNumero(),
								notacred.getCliente().getRazonSocial()
										.substring(0, maxlength)
										+ "..",
								notacred.getCliente().getRuc(),
								notacred.isNotaCreditoVentaContado() ? "NC-CO "
										+ motivo : "NC-CR " + motivo,
								notacred.isAnulado() ? 0.0 : notacred
										.getImporteGs() * -1 };

						if (filtro.isIncluirNCR()
								&& notacred.isNotaCreditoVentaContado()) {
							data.add(nc);
							totalImporte += (notacred.isAnulado() ? 0.0
									: notacred.getImporteGs() * -1);
						} else if (filtro.isIncluirNCR_CRED()
								&& !notacred.isNotaCreditoVentaContado()) {
							data.add(nc);
							totalImporte += (notacred.isAnulado() ? 0.0
									: notacred.getImporteGs() * -1);
						}
					}
				}

				if (filtro.isIncluirVCR() && filtro.isIncluirVCT()) {
					List<Venta> ventas = rr.getVentas(desde, hasta, idCliente);
					for (Venta venta : ventas) {
						Object[] vta = new Object[] {
								m.dateToString(venta.getFecha(), "dd-MM-yy"),
								venta.getNumero(),
								getMaxLength(
										venta.getDenominacion() == null ? venta
												.getCliente().getRazonSocial()
												: venta.getDenominacion(), 25),
								venta.getCliente().getRuc(),
								"FAC. "
										+ venta.getCondicionPago()
												.getDescripcion()
												.substring(0, 3).toUpperCase(),
								venta.isAnulado() ? 0.0 : venta
										.getTotalImporteGs() };
						data.add(vta);
						totalImporte += (venta.isAnulado() ? 0.0 : venta
								.getTotalImporteGs());
					}

				} else if (filtro.isIncluirVCR()) {
					List<Venta> ventas = rr.getVentasCredito(desde, hasta,
							idCliente);
					for (Venta venta : ventas) {
						Object[] vta = new Object[] {
								m.dateToString(venta.getFecha(), "dd-MM-yy"),
								venta.getNumero(),
								getMaxLength(
										venta.getDenominacion() == null ? venta
												.getCliente().getRazonSocial()
												: venta.getDenominacion(), 25),
								venta.getCliente().getRuc(),
								"FAC. "
										+ venta.getCondicionPago()
												.getDescripcion()
												.substring(0, 3).toUpperCase(),
								venta.isAnulado() ? 0.0 : venta
										.getTotalImporteGs() };
						data.add(vta);
						totalImporte += (venta.isAnulado() ? 0.0 : venta
								.getTotalImporteGs());
					}

				} else if (filtro.isIncluirVCT()) {
					List<Venta> ventas = rr.getVentasContado(desde, hasta,
							idCliente, 0);
					for (Venta venta : ventas) {
						Object[] vta = new Object[] {
								m.dateToString(venta.getFecha(), "dd-MM-yy"),
								venta.getNumero(),
								getMaxLength(
										venta.getDenominacion() == null ? venta
												.getCliente().getRazonSocial()
												: venta.getDenominacion(), 25),
								venta.getCliente().getRuc(),
								"FAC. "
										+ venta.getCondicionPago()
												.getDescripcion()
												.substring(0, 3).toUpperCase(),
								venta.isAnulado() ? 0.0 : venta
										.getTotalImporteGs() };
						data.add(vta);
						totalImporte += (venta.isAnulado() ? 0.0 : venta
								.getTotalImporteGs());
					}
				}

				double totalSinIva = totalImporte
						- m.calcularIVA(totalImporte, 10);
				String sucursal = getAcceso().getSucursalOperativa().getText();
				String familias_ = "";

				ReporteVentasGenerico rep = new ReporteVentasGenerico(
						totalSinIva, desde, hasta, "TODOS..", cliente_, sucursal, "TODOS..", familias_);
				rep.setDatosReporte(data);
				
				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00030
		 */
		private void ventasPorMes(boolean mobile) {			
			try {
				if (mobile) {
					Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
					return;
				}				
				if (filtro.getAnhoDesde() == null) {
					Clients.showNotification("DEBE SELECCIONAR UN PERIODO..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				String periodo = filtro.getAnhoDesde();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				Date desde = Utiles.getFecha("01-01-" + periodo + " 00:00:00");		
				Date hasta = Utiles.getFecha("31-12-" + periodo + " 23:00:00");
				List<Object[]> ventas = new ArrayList<Object[]>();
				
				Object[] formato = filtro.getFormato();
				
				List<Object[]> arts = rr.get_articulos();
				for (Object[] articulo : arts) {
					ventas.addAll(rr.getVentasPorArticulo((long) articulo[0], desde, hasta));
				}
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_DESGLOSADO;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new VentasDesglosado(ventas, periodo);
				params.put("Titulo", "VENTAS DESGLOSADO POR MES (STOCK A LA FECHA ACTUAL)");
				params.put("Usuario", getUs().getNombre());
				params.put("Periodo", filtro.getAnhoDesde());
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		/**
		 * VEN-00031 ventas por clientes por mes vs ultima venta..
		 */
		private void ventasClientesPorMes_ultimaVenta(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE PARA VERSION MOVIL..");
				return;
			}
			
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Date tope = filtro.getFechaHasta2();
				boolean ncrInc = filtro.isIncluirNCR();
				Object[] formato = filtro.getFormato();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> ventas = rr.get_Ventas(desde, hasta, 0);
				List<Object[]> ncs = rr.getNotasCredito_Venta(desde, hasta, 0);
				List<Object[]> data = new ArrayList<Object[]>();
				Map<String, Object[]> values = new HashMap<String, Object[]>();
				Map<String, Object[]> values_ = new HashMap<String, Object[]>();
				
				//[0]:id - [1]:fecha - [2]:idCliente - [3]:razonSocial - [4]:vendedor - [5]:rubro - [6]:totalImporteGs
				for (Object[] venta : ventas) {
					Date fecha = (Date) venta[1];
					long idCli_ = (long) venta[2];
					double totalImporte = (double) venta[6];
					int mes = Utiles.getNumeroMes(fecha) - 1;
					String idCli = idCli_ + "";
					String key = idCli + "-" + mes;
					String cliente = (String) venta[3];
					String vendedor_ = (String) venta[4];
					String rubro_ = (String) venta[5];
					Object[] acum = values.get(key);
					Date ultimaVenta = rr.getUltimaVenta(idCli_).get(0);
					if (tope.compareTo(ultimaVenta) >= 0) {
						if (acum != null) {
							double importe = (double) acum[0];
							double importe_ = totalImporte;
							importe += importe_;
							values.put(key, new Object[] { importe, mes, cliente, vendedor_, rubro_,
									Utiles.getDateToString(ultimaVenta, Utiles.DD_MM_YYYY) });
						} else {
							double importe_ = totalImporte;
							values.put(key, new Object[] { importe_, mes, cliente, vendedor_, rubro_,
									Utiles.getDateToString(ultimaVenta, Utiles.DD_MM_YYYY) });
						}
					}								
				}
				
				//[0]:id - [1]:fecha - [2]:idCliente - [3]:razonSocial - [4]:vendedor - [5]:rubro - [6]:totalImporteGs
				if (ncrInc) {
					for (Object[] ncred : ncs) {
						Date fecha = (Date) ncred[1];
						long idCli_ = (long) ncred[2];
						double totalImporte = (double) ncred[6];
						int mes = Utiles.getNumeroMes(fecha) - 1;
						String id = idCli_ + "";
						String key = id + "-" + mes;
						String cliente = (String) ncred[3];
						String vendedor_ = (String) ncred[4];
						String rubro_ = (String) ncred[5];
						Object[] acum = values.get(key);
						Date ultimaVenta = rr.getUltimaVenta(idCli_).get(0);
						if (tope.compareTo(ultimaVenta) >= 0) {
							if (acum != null) {
								double importe = (double) acum[0];
								double importe_ = totalImporte;
								importe -= importe_;
								values.put(key, new Object[] { importe, mes, cliente, vendedor_, rubro_,
										Utiles.getDateToString(ultimaVenta, Utiles.DD_MM_YYYY) });
							} else {
								double importe_ = totalImporte;
								values.put(key, new Object[] { importe_ * -1, mes, cliente, vendedor_, rubro_,
										Utiles.getDateToString(ultimaVenta, Utiles.DD_MM_YYYY) });
							}
						}
					}
				}				
				
				for (String key : values.keySet()) {
					Object[] value = values.get(key);
					double importe = (double) value[0];
					int mes = (int) value[1];
					String id = (String) value[2];
					String vend = (String) value[3];
					String rubro_ = (String) value[4];
					String ultVta = (String) value[5];
					
					Object[] value_ = values_.get(id);
					if (value_ != null) {
						value_[mes] = importe;
						values_.put(id, value_);
					} else {
						Object[] datos = new Object[] { (double) 0, (double) 0,
								(double) 0, (double) 0, (double) 0, (double) 0,
								(double) 0, (double) 0, (double) 0, (double) 0,
								(double) 0, (double) 0, vend, rubro_, ultVta };
						datos[mes] = importe;
						values_.put(id, datos);
					}
				}
				
				for (String key : values_.keySet()) {
					Object[] value_ = values_.get(key);
					double total = (double) value_[0] + (double) value_[1]
							+ (double) value_[2] + (double) value_[3]
							+ (double) value_[4] + (double) value_[5]
							+ (double) value_[6] + (double) value_[7]
							+ (double) value_[8] + (double) value_[9]
							+ (double) value_[10] + (double) value_[11];
					data.add(new Object[] { key.toUpperCase(), value_[0],
							value_[1], value_[2], value_[3], value_[4],
							value_[5], value_[6], value_[7], value_[8],
							value_[9], value_[10], value_[11], total, value_[12], value_[13], value_[14] });
				}
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_POR_CLIENTES_ULT_VTA;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new VentasPorClienteDataSource(data);
				params.put("Titulo", "Ventas por Clientes por Mes / Registro ultima venta");
				params.put("Usuario", getUs().getNombre());
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("Tope", Utiles.getDateToString(tope, Utiles.DD_MM_YYYY));
				params.put("Vendedor", "TODOS..");
				params.put("Rubro", "TODOS..");
				params.put("NCR_INC", filtro.isIncluirNCR() ? "SI" : "NO");
				params.put("IVA_INC", filtro.isIvaIncluido() ? "SI" : "NO");
				imprimirJasper(source, params, dataSource, formato);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00032
		 */
		private void ventasPromo1(boolean mobile) {
			try {

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				
				List<VentaPromo1> registros = rr.getVentasPromo1();
				for (VentaPromo1 item : registros) {
					data.add(new Object[] { 
							item.getNombreApellido().toUpperCase(), 
							item.getDireccion().toLowerCase(),
							item.getCorreo().toLowerCase(),
							item.getTelefono() + "(" + item.getAuxi() + ")",
							Utiles.getDateToString(item.getNacimiento(), Utiles.DD_MM_YYYY)});
				}

				ReporteVentasPromo1 rep = new ReporteVentasPromo1();
				rep.setApaisada();
				rep.setDatosReporte(data);
				
				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00033
		 */
		@SuppressWarnings("unchecked")
		private void ventasPorFamilia(boolean mobile) {
			try {				
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				SucursalApp suc = filtro.getSelectedSucursal();
				Cliente cliente = filtro.getCliente();
				long idSucursal = suc != null? suc.getId() : 0;
				long idCliente = cliente != null? cliente.getId() : 0;
				
				Map<String, Double> acum = new HashMap<String, Double>();
				Map<String, Double> acum_costo = new HashMap<String, Double>();
				Map<String, Long> acum_cant = new HashMap<String, Long>();
				Map<String, Double> acum_vol = new HashMap<String, Double>();
				List<Object[]> data = new ArrayList<Object[]>();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<ArticuloFamilia> flias = rr.getObjects(ArticuloFamilia.class.getName());
				
				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();
				
				List<Venta> ventas = rr.getVentas_(desde, hasta, idCliente, idSucursal, "", 0);
				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						for (ArticuloFamilia flia : flias) {
							String desc = flia.getDescripcion();
							Double total = acum.get(desc);
							Double total_costo = acum_costo.get(desc);
							Long total_cant = acum_cant.get(desc);
							Double total_vol = acum_vol.get(desc);
							if (total != null) {
								total += Utiles.getRedondeo(venta.getImporteGsByFamiliaSinIva(flia.getId()));
								total_costo += Utiles.getRedondeo(venta.getCostoGsByFamilia(flia.getId()));
								total_cant += venta.getCantidadByFamilia(flia.getId());
								total_vol += venta.getVolumenByFamilia(flia.getId());
								acum.put(desc, total);
								acum_costo.put(desc, total_costo);
								acum_cant.put(desc, total_cant);
								acum_vol.put(desc, total_vol);
								
							} else {
								acum.put(desc, Utiles.getRedondeo(venta.getImporteGsByFamilia(flia.getId())));
								acum_costo.put(desc, Utiles.getRedondeo(venta.getCostoGsByFamilia(flia.getId())));
								acum_cant.put(desc, venta.getCantidadByFamilia(flia.getId()));
								acum_vol.put(desc, venta.getVolumenByFamilia(flia.getId()));
							}
						}
					}
				}
				
				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, idCliente, idSucursal, "");
				for (NotaCredito nc : ncs) {
					if (!nc.isAnulado()) {
						for (ArticuloFamilia flia : flias) {
							String desc = flia.getDescripcion();
							Double total = acum.get(desc);
							Double total_costo = acum_costo.get(desc);
							Long total_cant = acum_cant.get(desc);
							Double total_vol = acum_vol.get(desc);
							if (total != null) {
								total -= Utiles.getRedondeo(nc.getImporteGsByFamiliaSinIva(flia.getId()));
								total_costo -= Utiles.getRedondeo(nc.getCostoGsByFamilia(flia.getId()));
								total_cant -= nc.getCantidadByFamilia(flia.getId());
								total_vol -= nc.getVolumenByFamilia(flia.getId());
								acum.put(desc, total);
								acum_costo.put(desc, total_costo);
								acum_cant.put(desc, total_cant);
								acum_vol.put(desc, total_vol);
							} else {
								acum.put(desc, Utiles.getRedondeo(nc.getImporteGsByFamiliaSinIva(flia.getId())) * -1);
								acum_costo.put(desc, Utiles.getRedondeo(nc.getCostoGsByFamilia(flia.getId())) * -1);
								acum_cant.put(desc, nc.getCantidadByFamilia(flia.getId()) * -1);
								acum_vol.put(desc, nc.getVolumenByFamilia(flia.getId()) * -1);
							}
						}
					}				
				}
				
				for (String key : acum.keySet()) {
					double total = acum.get(key);
					double total_costo = acum_costo.get(key);
					long total_cant = acum_cant.get(key);
					double total_vol = acum_vol.get(key);
					data.add(new Object[] { key, total, total_costo, (total - total_costo),
							Utiles.obtenerPorcentajeDelValor((total - total_costo), total),
							Utiles.obtenerPorcentajeDelValor((total - total_costo), total_costo), total_cant, total_vol });
				}

				String sucursal = suc != null ? suc.getDescripcion() : "TODOS..";
				String cliente_ = cliente != null ? cliente.getRazonSocial() : "TODOS..";
				ReporteVentasPorFamilia rep = new ReporteVentasPorFamilia(desde, hasta, sucursal, cliente_);
				rep.setApaisada();
				rep.setDatosReporte(data);
				
				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00035
		 */
		private void cobranzasVentasVendedor(boolean mobile, String codReporte) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> cobros = rr.getCobranzasPorVendedor(desde, hasta, 0, 0);
				List<Venta> ventas = rr.getVentasContado(desde, hasta, 0, 0);
				Map<Long, Double> values = new HashMap<Long, Double>();
				Map<Long, Double> values_ = new HashMap<Long, Double>();

				for (Object[] cobro : cobros) {
					long idVend = (long) cobro[4];
					Double total = values.get(idVend);
					if(total != null) {
						total += (double) cobro[2];
					} else {
						total = (double) cobro[2];
					}
					values.put(idVend, total);
				}
				
				for (Venta venta : ventas) {
					long idVend = venta.getVendedor().getId();
					Double total = values_.get(idVend);
					if(total != null) {
						total += (double) venta.getTotalImporteGsSinIva();
					} else {
						total = (double) venta.getTotalImporteGsSinIva();
					}
					values_.put(idVend, total);
				}
				
				for (Funcionario vendedor : filtro.getVendedores()) {
					long idVend = vendedor.getId();
					List<AjusteCtaCte> apls = rr.getAplicacionesAnticipos(desde, hasta, vendedor.getId().longValue());
					for (AjusteCtaCte apl : apls) {
						if (apl.getCredito().isVentaCredito()) {						
							Double total = values.get(idVend);
							if (total != null) {
								total += apl.getImporte();
							} else {
								total = apl.getImporte();
							}
							values.put(idVend, total);
						}					
					}
				}
				
				for (Funcionario vendedor : filtro.getVendedores()) {
					Double cobrado = values.get(vendedor.getId());
					Double contado = values_.get(vendedor.getId());
					
					double cobrado_ = cobrado != null? cobrado : 0;
					double contado_ = contado != null? contado : 0;
					
					contado_ = Utiles.getRedondeo(contado_);
					cobrado_ = Utiles.getRedondeo(cobrado_);
					double cobradoSinIva = cobrado_ - Utiles.getIVA(cobrado_, 10);
					cobradoSinIva = Utiles.getRedondeo(cobradoSinIva);
					
					if (cobrado != null || contado != null) {
						data.add(new Object[]{ vendedor.getRazonSocial().toUpperCase(),
								cobradoSinIva, contado_ });
					}						
				}
				
				Collections.sort(data, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String val1 = (String) o1[0];
						String val2 = (String) o2[0];
						int compare = val1.compareTo(val2);				
						return compare;
					}
				});

				ReporteTotalCobranzasVentas rep = new ReporteTotalCobranzasVentas(desde, hasta, "TODOS..");
				rep.setDatosReporte(data);
				rep.setTitulo(codReporte + " - Total cobranzas / ventas contado por vendedor");
				rep.setApaisada();				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00036
		 */
		private void cobranzasVentasVendedorDetallado(boolean mobile, String codReporte) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				
				if (vendedor == null) {
					Clients.showNotification("DEBE SELECCIONAR UN VENDEDOR..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> cobros = rr.getCobranzasPorVendedor(desde, hasta, vendedor.getId(), 0);
				List<Venta> ventas = rr.getVentasContadoPorVendedor(desde, hasta, vendedor.getId());
				Map<Long, Double> values = new HashMap<Long, Double>();
				Map<Long, Double> values_ = new HashMap<Long, Double>();
				Map<Long, String> clientes = new HashMap<Long, String>();

				for (Object[] cobro : cobros) {
					Recibo rec = (Recibo) cobro[0];
					long idCliente = rec.getCliente().getId();
					Double total = values.get(idCliente);
					if(total != null) {
						total += (double) cobro[2];
					} else {
						total = (double) cobro[2];
					}
					values.put(idCliente, total);
					clientes.put(idCliente, rec.getCliente().getRazonSocial());
				}
				
				List<AjusteCtaCte> apls = rr.getAplicacionesAnticipos(desde, hasta, vendedor.getId().longValue());
				for (AjusteCtaCte apl : apls) {
					if (apl.getCredito().isVentaCredito()) {							
						long idEmpresa = apl.getCredito().getIdEmpresa();
						Cliente cli = rr.getClienteByEmpresa(idEmpresa);
						if (cli != null) {
							long idCliente = cli.getId();
							Double total = values.get(idCliente);
							if(total != null) {
								total += apl.getImporte();
							} else {
								total = apl.getImporte();
							}
							values.put(idCliente, total);
							clientes.put(idCliente, cli.getRazonSocial());
						}						
					}					
				}
				
				for (Venta venta : ventas) {
					long idCliente = venta.getCliente().getId();
					Double total = values_.get(idCliente);
					if(total != null) {
						total += (double) venta.getTotalImporteGsSinIva();
					} else {
						total = (double) venta.getTotalImporteGsSinIva();
					}
					values_.put(idCliente, total);
					clientes.put(idCliente, venta.getCliente().getRazonSocial());
				}				

				for (Long idCliente : clientes.keySet()) {
					Double cobrado = values.get(idCliente);
					Double contado = values_.get(idCliente);
					
					double cobrado_ = cobrado != null? cobrado : 0;
					double contado_ = contado != null? contado : 0;
					
					double cobradoSinIva = (cobrado_ - Utiles.getIVA(cobrado_, 10));
					cobradoSinIva = Utiles.getRedondeo(cobradoSinIva);
					contado_ = Utiles.getRedondeo(contado_);
					
					if (cobrado != null || contado != null) {
						data.add(new Object[]{ clientes.get(idCliente), cobradoSinIva, contado_ });
					}
				}				

				ReporteTotalCobranzasVentas rep = new ReporteTotalCobranzasVentas(desde, hasta, vendedor.getRazonSocial());
				rep.setDatosReporte(data);
				rep.setTitulo(codReporte + " - Cobranzas por vendedor detallado");
				rep.setApaisada();			

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00037
		 */
		private void ventasContadoCreditoVendedor(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas_contado = rr.getVentasContado_(desde, hasta, 0, 0, "");
				List<Venta> ventas_credito = rr.getVentasCredito_(desde, hasta, 0, 0, "");
				List<NotaCredito> notas_credito = rr.getNotasCreditoVenta(desde, hasta, 0);
				Map<Long, Double> values_cont = new HashMap<Long, Double>();
				Map<Long, Double> values_cred = new HashMap<Long, Double>();
				Map<Long, Double> values_ncre = new HashMap<Long, Double>();
				Map<Long, Double> values_anul = new HashMap<Long, Double>();
				
				for (Venta venta : ventas_contado) {
					long idVend = venta.getVendedor().getId();
					if (!venta.isAnulado()) {
						Double total = values_cont.get(idVend);
						if(total != null) {
							total += (double) venta.getTotalImporteGs();
						} else {
							total = (double) venta.getTotalImporteGs();
						}
						values_cont.put(idVend, total);
					} else {
						Double total = values_anul.get(idVend);
						if(total != null) {
							total += (double) venta.getTotalImporteGs();
						} else {
							total = (double) venta.getTotalImporteGs();
						}
						values_anul.put(idVend, total);					
					}
				}
				
				for (Venta venta : ventas_credito) {
					long idVend = venta.getVendedor().getId();
					if (!venta.isAnulado()) {
						Double total = values_cred.get(idVend);
						if(total != null) {
							total += (double) venta.getTotalImporteGs();
						} else {
							total = (double) venta.getTotalImporteGs();
						}
						values_cred.put(idVend, total);
					} else {
						Double total = values_anul.get(idVend);
						if(total != null) {
							total += (double) venta.getTotalImporteGs();
						} else {
							total = (double) venta.getTotalImporteGs();
						}
						values_anul.put(idVend, total);					
					}
				}
				
				for (NotaCredito ncred : notas_credito) {
					if (!ncred.isAnulado()) {
						long idVend = ncred.getVendedor().getId();
						Double total = values_ncre.get(idVend);
						if(total != null) {
							total += (double) ncred.getImporteGs();
						} else {
							total = (double) ncred.getImporteGs();
						}
						values_ncre.put(idVend, total);
					}
				}
				
				for (Funcionario vendedor : filtro.getVendedores_()) {
					Double contado = values_cont.get(vendedor.getId());
					Double credito = values_cred.get(vendedor.getId());
					Double ncredit = values_ncre.get(vendedor.getId());
					Double anulado = values_anul.get(vendedor.getId());
					
					double contado_ = contado != null? contado : 0;
					double credito_ = credito != null? credito : 0;
					double ncredit_ = ncredit != null? ncredit : 0;
					double anulado_ = anulado != null? anulado : 0;
					
					double contadoSiva = contado_ - m.calcularIVA(contado_, 10);
					double creditoSiva = credito_ - m.calcularIVA(credito_, 10);
					double notacreSiva = ncredit_ - m.calcularIVA(ncredit_, 10);
					double anuladoSiva = anulado_ - m.calcularIVA(anulado_, 10);
					
					if (contado != null || credito != null) {
						data.add(new Object[]{ vendedor.getRazonSocial().toUpperCase(), contadoSiva, creditoSiva, anuladoSiva, notacreSiva, (contadoSiva + creditoSiva) - notacreSiva });	
					}					
				}
				
				for (Object[] item : data) {
					item[1] = Utiles.getRedondeo((double) item[1]);
					item[2] = Utiles.getRedondeo((double) item[2]);
					item[3] = Utiles.getRedondeo((double) item[3]);
					item[4] = Utiles.getRedondeo((double) item[4]);
					item[5] = Utiles.getRedondeo((double) item[5]);
				}

				ReporteTotalVentas rep = new ReporteTotalVentas(desde, hasta, "TODOS..");
				rep.setDatosReporte(data);
				rep.setApaisada();				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00038
		 */
		private void ventasContadoCreditoVendedorDetallado(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				
				if (vendedor == null) {
					Clients.showNotification("DEBE SELECCIONAR UN VENDEDOR..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas_contado = rr.getVentasContadoPorVendedor(desde, hasta, vendedor.getId());
				List<Venta> ventas_credito = rr.getVentasCreditoPorVendedor(desde, hasta, vendedor.getId());
				List<NotaCredito> notas_credito = rr.getNotasCreditoPorVendedor(desde, hasta, vendedor.getId());
				Map<Long, Double> values_cont = new HashMap<Long, Double>();
				Map<Long, Double> values_cred = new HashMap<Long, Double>();
				Map<Long, Double> values_ncre = new HashMap<Long, Double>();
				Map<Long, String> clientes = new HashMap<Long, String>();
				
				for (Venta venta : ventas_contado) {
					long idCliente = venta.getCliente().getId();
					Double total = values_cont.get(idCliente);
					if(total != null) {
						total += (double) venta.getTotalImporteGsSinIva();
					} else {
						total = (double) venta.getTotalImporteGsSinIva();
					}
					values_cont.put(idCliente, total);
					clientes.put(venta.getCliente().getId(), venta.getCliente().getRazonSocial());
				}
				
				for (Venta venta : ventas_credito) {
					long idCliente = venta.getCliente().getId();
					Double total = values_cred.get(idCliente);
					if(total != null) {
						total += (double) venta.getTotalImporteGsSinIva();
					} else {
						total = (double) venta.getTotalImporteGsSinIva();
					}
					values_cred.put(idCliente, total);
					clientes.put(venta.getCliente().getId(), venta.getCliente().getRazonSocial());
				}
				
				for (NotaCredito ncred : notas_credito) {
					if (!ncred.isAnulado()) {
						long idCliente = ncred.getCliente().getId();
						Double total = values_ncre.get(idCliente);
						if(total != null) {
							total += (double) ncred.getTotalImporteGsSinIva();
						} else {
							total = (double) ncred.getTotalImporteGsSinIva();
						}
						values_ncre.put(idCliente, total);
						clientes.put(ncred.getCliente().getId(), ncred.getCliente().getRazonSocial());
					}
				}
				
				for (Long idCliente : clientes.keySet()) {
					Double contado = values_cont.get(idCliente);
					Double credito = values_cred.get(idCliente);
					Double ncredit = values_ncre.get(idCliente);
					
					double contado_ = contado != null? contado : 0;
					double credito_ = credito != null? credito : 0;
					double ncredit_ = ncredit != null? ncredit : 0;
					
					if (contado != null || credito != null) {
						data.add(new Object[]{ clientes.get(idCliente), contado_, credito_, ncredit_, (contado_ + credito_) - ncredit_ });	
					}
				}

				ReporteTotalVentas rep = new ReporteTotalVentas(desde, hasta, vendedor.getRazonSocial());
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00039
		 */
		private void cobranzasVentasVendedorProveedorDetallado(boolean mobile, String codReporte) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Date desde_ = filtro.getFechaDesde2();
				Date hasta_ = filtro.getFechaHasta2();
				Funcionario vendedor = filtro.getVendedor();
				
				if (vendedor == null) {
					Clients.showNotification("DEBE SELECCIONAR UN VENDEDOR..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();
				if (desde_ == null) desde_ = new Date();
				if (hasta_ == null) hasta_ = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> cobros = rr.getCobranzasPorVendedor(desde, hasta, vendedor.getId().longValue(), 0);
				List<Venta> ventas = rr.getVentasContadoPorVendedor(desde, hasta, vendedor.getId().longValue());
				List<NotaCredito> notasCredito = rr.getNotasCreditoPorVendedor(desde_, hasta_, vendedor.getId().longValue(), true);
				double totalContado = 0;
				double totalContadoItems = 0;
				double totalNCRContado = 0;
				double totalNCRCredito = 0;
				Map<Long, Integer> prov_acum = new HashMap<Long, Integer>();
				Map<Long, Integer> prov_acum_ = new HashMap<Long, Integer>();
				Map<Long, Double> values = new HashMap<Long, Double>();
				Map<Long, Double> values_ = new HashMap<Long, Double>();
				Map<Long, String> proveedores = new HashMap<Long, String>();
				
				for (Object[] cobro : cobros) {
					ReciboDetalle item = (ReciboDetalle) cobro[3];
					Venta vta = item.getVenta();
					if (vta != null) {
						for (VentaDetalle det : vta.getDetalles()) {
							Proveedor prov = det.getArticulo().getProveedor();
							long idProveedor = prov != null ? prov.getId() : 0;
							Integer total = prov_acum.get(idProveedor);
							if (total != null) {
								total ++;
							} else {
								total = 1;
							}
							double porcentaje = Utiles.obtenerPorcentajeDelValor(item.getMontoGs(), vta.getTotalImporteGs());
							double importeSinIva = Utiles.obtenerValorDelPorcentaje(det.getImporteGsSinIva(), porcentaje);
							prov_acum.put(idProveedor, total);
							Double acum = values.get(idProveedor);
							if (acum != null) {
								acum += importeSinIva;
							} else {
								acum = importeSinIva;
							}
							values.put(idProveedor, acum);
							proveedores.put(idProveedor, prov != null ? prov.getRazonSocial() : "SIN PROVEEDOR");
						}
					} else {
						List<Object[]> dets = item.getDetalleVentaMigracion();
						for (Object[] det : dets) {
							Articulo art = rr.getArticulo((String) det[0]);
							if (art != null) {
								Proveedor prov = art.getProveedor();
								long idProveedor = prov != null ? prov.getId() : 0;
								Integer total = prov_acum.get(idProveedor);
								if (total != null) {
									total ++;
								} else {
									total = 1;
								}
								prov_acum.put(idProveedor, total);
								proveedores.put(idProveedor, prov != null ? prov.getRazonSocial() : "SIN PROVEEDOR");
							}								
						}
					} 
				}
				
				List<AjusteCtaCte> apls = rr.getAplicacionesAnticipos(desde, hasta, vendedor.getId().longValue());
				for (AjusteCtaCte apl : apls) {
					if (apl.getCredito().isVentaCredito()) {
						Venta vta = (Venta) rr.getObject(Venta.class.getName(), apl.getCredito().getIdMovimientoOriginal());
						if (vta != null) {
							for (VentaDetalle det : vta.getDetalles()) {
								Proveedor prov = det.getArticulo().getProveedor();
								long idProveedor = prov != null ? prov.getId() : 0;
								Integer total = prov_acum.get(idProveedor);
								if (total != null) {
									total ++;
								} else {
									total = 1;
								}
								double porcentaje = Utiles.obtenerPorcentajeDelValor(apl.getImporte(), vta.getTotalImporteGs());
								double importeSinIva = Utiles.obtenerValorDelPorcentaje(det.getImporteGsSinIva(), porcentaje);
								prov_acum.put(idProveedor, total);
								Double acum = values.get(idProveedor);
								if (acum != null) {
									acum += importeSinIva;
								} else {
									acum = importeSinIva;
								}
								values.put(idProveedor, acum);
								proveedores.put(idProveedor, prov != null ? prov.getRazonSocial() : "SIN PROVEEDOR");
							}
						}
					}					
				}
				
				for (Venta venta : ventas) {
					totalContado += venta.getTotalImporteGsSinIva();
					for (VentaDetalle det : venta.getDetalles()) {
						Proveedor prov = det.getArticulo().getProveedor();
						long idProveedor = prov != null ? prov.getId() : 0;
						Integer total = prov_acum_.get(idProveedor);
						if (total != null) {
							total ++;
						} else {
							total = 1;
						}
						totalContadoItems ++;
						prov_acum_.put(idProveedor, total);
						proveedores.put(idProveedor, prov != null ? prov.getRazonSocial() : "SIN PROVEEDOR");
					}
				}
				
				for (NotaCredito ncr : notasCredito) {
					System.out.println("----- ncr: " + ncr.getNumero());
					if (ncr.isNotaCreditoVentaContado()) {
						totalNCRContado += ncr.getTotalImporteGsSinIva(); 
						System.out.println("totalNCRContado" + totalNCRContado);
					} else {
						totalNCRCredito += ncr.getTotalImporteGsSinIva(); 
						System.out.println("totalNCRCredito" + totalNCRCredito);
					}
				}
				
				for (Long idProveedor : proveedores.keySet()) {					
					Integer total_ = prov_acum_.get(idProveedor);
					if (total_ != null) {
						double porcentaje_ = Utiles.obtenerPorcentajeDelValor(total_, totalContadoItems);
						double importe_ = Utiles.obtenerValorDelPorcentaje(totalContado, porcentaje_);
						values_.put(idProveedor, importe_);
					}
				}

				for (Long idProveedor : proveedores.keySet()) {
					Double cobrado = values.get(idProveedor);
					Double contado = values_.get(idProveedor);
					
					double cobrado_ = cobrado != null? cobrado : 0;
					double contado_ = contado != null? contado : 0;
					cobrado_ = Utiles.getRedondeo(cobrado_);
					contado_ = Utiles.getRedondeo(contado_);
					
					if (cobrado != null || contado != null) {
						data.add(new Object[] { proveedores.get(idProveedor), cobrado_, contado_ });
					}
				}
				totalNCRCredito = Utiles.getRedondeo(totalNCRCredito);
				totalNCRContado = Utiles.getRedondeo(totalNCRContado);
				data.add(new Object[] { "NOTAS DE CRÉDITO POR PROMOCIÓN", totalNCRCredito, totalNCRContado });

				ReporteTotalCobranzasVentasProveedor rep = new ReporteTotalCobranzasVentasProveedor(desde, hasta, vendedor.getRazonSocial());
				rep.setDatosReporte(data);
				rep.setTitulo(codReporte + " - Total cobranzas / ventas contado por vendedor / proveedor");
				rep.setApaisada();				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * ventas por vendedor / clientes / articulos por mes..
		 */
		private void ventasPorVendedorClienteArticuloPorMes(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE PARA VERSION MOVIL..");
				return;
			}
			
			try {
				// PENDIENTE..
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00041
		 */
		private void promoCompraValvoline(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();
				
				Map<String, Object[]> codigos = new HashMap<String, Object[]>();
				codigos.put("VAL 774038", new Object[] { 2200, 4400 });
				codigos.put("VAL VV388", new Object[] { 600, 900 });
				codigos.put("VAL 773634", new Object[] { 1100, 2200 });
				codigos.put("VAL 874241", new Object[] { 600, 900 });
				codigos.put("VAL 859865", new Object[] { 1100, 2200 });
				codigos.put("VAL 818474", new Object[] { 55000, 110000 });
				codigos.put("VAL 710994", new Object[] { 600, 900 });
				codigos.put("VAL 847651", new Object[] { 600, 900 });
				codigos.put("VAL 847654", new Object[] { 55000, 110000 });
				codigos.put("VAL VV955", new Object[] { 900, 1700 });
				codigos.put("VAL VV966", new Object[] { 900, 1700 });
				codigos.put("VAL VV296", new Object[] { 900, 1700 });
				codigos.put("VAL VV150", new Object[] { 900, 1700 });
				codigos.put("VAL VV301", new Object[] { 900, 1700 });
				codigos.put("VAL 880953", new Object[] { 900, 1700 });
				codigos.put("VAL VV150", new Object[] { 900, 1700 });
				codigos.put("VAL VV301", new Object[] { 900, 1700 });
				codigos.put("VAL 773779", new Object[] { 2200, 4400 });
				codigos.put("VAL 877311", new Object[] { 600, 900 });
				codigos.put("VAL VV161", new Object[] { 900, 1700 });
				codigos.put("VAL 875391", new Object[] { 600, 900 });
				codigos.put("VAL 875290", new Object[] { 1100, 2200 });
				codigos.put("VAL 875392", new Object[] { 55000, 110000 });
				codigos.put("VAL 884577", new Object[] { 900, 1700 });
				codigos.put("VAL 884569", new Object[] { 900, 1700 });
				codigos.put("VAL 787301", new Object[] { 900, 1700 });

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Venta> ventas = rr.getVentas(desde, hasta, 0);
				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
				Map<String, Long> acum = new HashMap<String, Long>();
				Map<String, Double> acum_ = new HashMap<String, Double>();

				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						for (VentaDetalle item : venta.getDetalles()) {
							String cod = item.getArticulo().getCodigoInterno();
							if (codigos.get(cod) != null) {
								String cliente = venta.getCliente().getRazonSocial();
								String vendedor = venta.getVendedor().getRazonSocial();
								String key = cliente + ";" + vendedor + ";" + cod;
								Long cant = acum.get(key);
								Double importe = acum_.get(key);
								if (cant != null) {
									cant += item.getCantidad();
									importe += item.getImporteGs();
								} else {
									cant = item.getCantidad();
									importe = item.getImporteGs();
								}
								acum.put(key, cant);
								acum_.put(key, importe);
							}
						}
					}
				}
				
				for (NotaCredito nc : ncs) {
					if (!nc.isAnulado()) {
						for (NotaCreditoDetalle item : nc.getDetallesArticulos()) {
							String cod = item.getArticulo().getCodigoInterno();
							if (codigos.get(cod) != null) {
								String cliente = nc.getCliente().getRazonSocial();
								String vendedor = nc.getVentaAplicada().getVendedor().getRazonSocial();
								String key = cliente + ";" + vendedor + ";" + cod;
								Long cant = acum.get(key);
								Double importe = acum_.get(key);
								if (cant != null) {
									cant -= item.getCantidad();
									importe -= item.getImporteGs();
								}
								acum.put(key, cant);
								acum_.put(key, importe);
							}
						}
					}
				}
				
				Set<String> keys = acum.keySet();
				List<String> keys_ = new ArrayList<String>();
				keys_.addAll(keys);
				
				Collections.sort(keys_, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						String val1 = o1;
						String val2 = o2;
						int compare = val1.compareTo(val2);				
						return compare;
					}
				});
				
				for (String key : keys_) {
					Long cant = acum.get(key);
					Double importe = acum_.get(key);
					if (cant != null) {
						String[] dato = key.split(";");
						String cod = dato[2];
						Object[] coef = codigos.get(cod);
						int duenho = (int) coef[0];
						int empleado = (int) coef[1];
						data.add(new Object[] { dato[0], dato[1], cod, cant, importe, (cant * duenho), (cant * empleado) });
					}					
				}
				
				ReportePromoValvoline rep = new ReportePromoValvoline(desde, hasta);
				rep.setDatosReporte(data);
				rep.setApaisada();				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00042
		 */
		private void ventasLitraje(boolean mobile, String codReporte) {
			try {					
				Object[] formato = filtro.getFormato();
				ArticuloMarca marca = filtro.getMarca_();
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				
				if (marca == null) {
					Clients.showNotification("Debe seleccionar una marca..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}				
				long idMarca = marca.getId();
				
				RegisterDomain rr = RegisterDomain.getInstance();				
				List<Object[]> ventas = rr.getVentasDetalladoLitraje(desde, hasta, idMarca);
				List<Object[]> ncs = rr.getNotasCreditoDetalladoLitraje(desde, hasta, idMarca);
				
				List<HistoricoMovimientoArticulo> list = new ArrayList<HistoricoMovimientoArticulo>();
				Map<String, Double> cants = new HashMap<String, Double>();
				Map<String, Double> importes = new HashMap<String, Double>();
				
				for (Object[] venta : ventas) {
					int mes = Utiles.getNumeroMes((Date) venta[4]);
					String cli = (String) venta[5];
					String vnd = (String) venta[7];
					String key = cli + ";" + vnd + ";" + mes;
					Double acum = cants.get(key);
					if (acum != null) {
						acum += ((Double) venta[3]);
						cants.put(key, acum);
					} else {
						cants.put(key, ((Double) (venta[3])));
					}
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ += ((Double) venta[6]);
						importes.put(key, acum_);
					} else {
						importes.put(key, ((Double) (venta[6])));
					}
				}
				
				for (Object[] nc : ncs) {
					int mes = Utiles.getNumeroMes((Date) nc[4]);
					String cli = (String) nc[5];
					String vnd = (String) nc[7];
					String key = cli + ";" + vnd + ";" + mes;
					Double acum = cants.get(key);
					if (acum != null) {
						acum -= ((Double) nc[3]);
						cants.put(key, acum);
					} else {
						cants.put(key, ((Double) (nc[3]) * -1));
					}
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ -= ((Double) nc[6]);
						importes.put(key, acum_);
					} else {
						importes.put(key, ((Double) (nc[6]) * -1));
					}
				}
				
				Map<String, String> keys = new HashMap<String, String>();
				
				for (String key : cants.keySet()) {					
					String cliente = key.split(";")[0];
					String vendedor = key.split(";")[1];
					String key_ = cliente + ";" + vendedor;
					
					if (keys.get(key_) == null) {
						Double cantidad = cants.get(key);
						
						Double cantEnero = cants.get(key_ + ";1");
						if (cantEnero == null) cantEnero = 0.0; 
						
						Double cantFebrero = cants.get(key_ + ";2");
						if (cantFebrero == null) cantFebrero = 0.0;
						
						Double cantMarzo = cants.get(key_ + ";3");
						if (cantMarzo == null) cantMarzo = 0.0;
						
						Double cantAbril = cants.get(key_ + ";4");
						if (cantAbril == null) cantAbril = 0.0;
						
						Double cantMayo = cants.get(key_ + ";5");
						if (cantMayo == null) cantMayo = 0.0;
						
						Double cantJunio = cants.get(key_ + ";6");
						if (cantJunio == null) cantJunio = 0.0;
						
						Double cantJulio = cants.get(key_ + ";7");
						if (cantJulio == null) cantJulio = 0.0;
						
						Double cantAgosto = cants.get(key_ + ";8");
						if (cantAgosto == null) cantAgosto = 0.0;
						
						Double cantSetiembre = cants.get(key_ + ";9");
						if (cantSetiembre == null) cantSetiembre = 0.0;
						
						Double cantOctubre = cants.get(key_ + ";10");
						if (cantOctubre == null) cantOctubre = 0.0;
						
						Double cantNoviembre = cants.get(key_ + ";11");
						if (cantNoviembre == null) cantNoviembre = 0.0;
						
						Double cantDiciembre = cants.get(key_ + ";12");
						if (cantDiciembre == null) cantDiciembre = 0.0;
						
						Double impEnero = importes.get(key_ + ";1");
						if (impEnero == null) impEnero = 0.0;
						
						Double impFebrero = importes.get(key_ + ";2");
						if (impFebrero == null) impFebrero = 0.0;
						
						Double impMarzo = importes.get(key_ + ";3");
						if (impMarzo == null) impMarzo = 0.0;
						
						Double impAbril = importes.get(key_ + ";4");
						if (impAbril == null) impAbril = 0.0;
						
						Double impMayo = importes.get(key_ + ";5");
						if (impMayo == null) impMayo = 0.0;
						
						Double impJunio = importes.get(key_ + ";6");
						if (impJunio == null) impJunio = 0.0;
						
						Double impJulio = importes.get(key_ + ";7");
						if (impJulio == null) impJulio = 0.0;
						
						Double impAgosto = importes.get(key_ + ";8");
						if (impAgosto == null) impAgosto = 0.0;
						
						Double impSetiembre = importes.get(key_ + ";9");
						if (impSetiembre == null) impSetiembre = 0.0;
						
						Double impOctubre = importes.get(key_ + ";10");
						if (impOctubre == null) impOctubre = 0.0;
						
						Double impNoviembre = importes.get(key_ + ";11");
						if (impNoviembre == null) impNoviembre = 0.0;
						
						Double impDiciembre = importes.get(key_ + ";12");
						if (impDiciembre == null) impDiciembre = 0.0;
						
						HistoricoMovimientoArticulo hist = new HistoricoMovimientoArticulo();
						hist.setDescripcion(cliente);
						hist.setReferencia(vendedor);
						hist.setLitraje(cantidad);
						hist.setEnero_(cantEnero);
						hist.setFebrero_(cantFebrero);
						hist.setMarzo_(cantMarzo);
						hist.setAbril_(cantAbril);
						hist.setMayo_(cantMayo);
						hist.setJunio_(cantJunio);
						hist.setJulio_(cantJulio);
						hist.setAgosto_(cantAgosto);
						hist.setSetiembre_(cantSetiembre);
						hist.setOctubre_(cantOctubre);
						hist.setNoviembre_(cantNoviembre);
						hist.setDiciembre_(cantDiciembre);
						hist.set_enero(impEnero);
						hist.set_febrero(impFebrero);
						hist.set_marzo(impMarzo);
						hist.set_abril(impAbril);
						hist.set_mayo(impMayo);
						hist.set_junio(impJunio);
						hist.set_julio(impJulio);
						hist.set_agosto(impAgosto);
						hist.set_setiembre(impSetiembre);
						hist.set_octubre(impOctubre);
						hist.set_noviembre(impNoviembre);
						hist.set_diciembre(impDiciembre);
						hist.setTotal_(hist.getEnero() + hist.getFebrero() + hist.getMarzo() + hist.getAbril() + hist.getMayo() + hist.getJunio()
								+ hist.getJulio() + hist.getAgosto() + hist.getSetiembre() + hist.getOctubre() + hist.getNoviembre() + hist.getDiciembre());
						
						list.add(hist);			
						keys.put(key_, key_);
					}					
				}				
				String format = (String) formato[0];
				String csv = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_CSV[0];
				String xls = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS[0];
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_LITRAJE;
				if (format.equals(csv) || format.equals(xls)) {
					source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_LITRAJE_;
				}
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("Usuario", getUs().getNombre());
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("Titulo", codReporte + " - LITRAJE DE VENTAS EN LUBRICANTES");
				JRDataSource dataSource = new LitrajeArticulos(list);
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00043
		 */
		private void listaPrecioPorDeposito(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Proveedor proveedor = filtro.getProveedorExterior();
				ArticuloFamilia flia = filtro.getFamilia_();
				ArticuloMarca marca = filtro.getMarca_();
				boolean stock = filtro.isFraccionado();
				
				long idProveedor = proveedor != null ? proveedor.getId() : 0;
				long idFamilia = flia != null ? flia.getId() : 0;
				long idMarca = marca != null ? marca.getId() : 0;

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<Object[]> arts = rr.getArticulos(idProveedor, idMarca, idFamilia, "");
				for (Object[] art : arts) {					
					if (stock) {	
						long min = art[6] != null ? (long) art[6] : (long) 0;
						long may = art[7] != null ? (long) art[7] : (long) 0;
						long mcl = art[8] != null ? (long) art[8] : (long) 0;
						if (min > 0 || may > 0 || mcl > 0) {
							data.add(new Object[] { art[1], art[2], art[6], art[7], art[8], art[3], art[4], art[5] });
						}
					} else {
						data.add(new Object[] { art[1], art[2], art[6], art[7], art[8], art[3], art[4], art[5] });
					}					
				}
				
				String proveedor_ = proveedor != null ? proveedor.getRazonSocial() : "TODOS..";
				
				ReporteListaPrecioPorDeposito rep = new ReporteListaPrecioPorDeposito(proveedor_);
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00044
		 */
		private void ventasPorListaPrecioPorFamilia(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}			
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				SucursalApp suc = filtro.getSelectedSucursal();
				Articulo art = filtro.getArticulo();
				
				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				Map<String, Object[]> totales = new HashMap<String, Object[]>();
				Map<String, Object[]> totales_ = new HashMap<String, Object[]>();
				
				long idSucursal = suc == null ? 0 : suc.getId().longValue();
				String suc_ = suc == null ? "TODOS.." : suc.getDescripcion().toUpperCase();
				
				List<Venta> ventas = rr.getVentas(desde, hasta, 0, 0, idSucursal, 0);
				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						for (VentaDetalle item : venta.getDetalles()) {
							if (item.getListaPrecio() != null) {
								if ((art != null && art.getCodigoInterno().equals(item.getArticulo().getCodigoInterno()))
										|| art == null) {
									String lis = item.getListaPrecio().getDescripcion();
									String key = lis + "-" + item.getArticulo().getFamilia().getDescripcion();
									Object[] acum = totales.get(key);
									if (acum != null) {
										double imp = (double) acum[0];
										double cos = (double) acum[1];
										imp += item.getImporteGsSinIva();
										cos += item.getCostoTotalGsSinIva();
										acum[0] = imp;
										acum[1] = cos;
										totales.put(key, acum);
									} else {
										acum = new Object[] { item.getImporteGsSinIva(), item.getCostoTotalGsSinIva() };
										totales.put(key, acum);
									}
								}
							}							
						}
					}					
				}	
				
				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0, idSucursal, true);
				for (NotaCredito ncr : ncs) {
					if (!ncr.isAnulado()) {
						if (!ncr.isMotivoDescuento()) {
							for (NotaCreditoDetalle det : ncr.getDetallesArticulos()) {
								ArticuloListaPrecio lp = det.getListaPrecio();
								if (lp != null) {
									if ((art != null && art.getCodigoInterno().equals(det.getArticulo().getCodigoInterno()))
											|| art == null) {
										String lis = lp.getDescripcion();
										String key = lis + "-" + det.getArticulo().getFamilia().getDescripcion();
										Object[] acum = totales.get(key);
										if (acum != null) {
											double imp = (double) acum[0];
											double cos = (double) acum[1];
											imp -= det.getImporteGsSinIva();
											cos -= det.getCostoTotalGsSinIva();
											acum[0] = imp;
											acum[1] = cos;
											totales.put(key, acum);
										} else {
											acum = new Object[] { det.getImporteGsSinIva() * -1, det.getCostoTotalGsSinIva() * -1 };
											totales.put(key, acum);
										}
									}
								}
							}
						}
						if (ncr.isMotivoDescuento()) {
							double importe = ncr.getTotalImporteGsSinIva();
							Venta vta = ncr.getVentaAplicada();
							int size = vta.getDetalles().size();
							double prorrateo = importe / size;
							for (VentaDetalle item : vta.getDetalles()) {
								if (item.getListaPrecio() != null) {
									if ((art != null && art.getCodigoInterno().equals(item.getArticulo().getCodigoInterno()))
											|| art == null) {
										String lis = item.getListaPrecio().getDescripcion();
										String key = lis + "-" + item.getArticulo().getFamilia().getDescripcion();
										Object[] acum = totales.get(key);
										if (acum != null) {
											double imp = (double) acum[0];
											double cos = (double) acum[1];
											imp += prorrateo;
											cos += prorrateo;
											acum[0] = imp;
											acum[1] = cos;
											totales.put(key, acum);
										}
									}
								}							
							}
						}
					}
				}
				
				for (String key : totales.keySet()) {
					Object[] total = totales.get(key);
					String[] desc = key.split("-");
					double totalVta = (double) total[0];
					double totalCto = (double) total[1];
					double utilidad = totalVta - totalCto;
					double promedioSobreCosto = Utiles.obtenerPorcentajeDelValor(utilidad, totalCto);
					double promedioSobreVenta = Utiles.obtenerPorcentajeDelValor(utilidad, totalVta);
					data.add(new Object[] { desc[0], desc[1], totalVta, totalCto, utilidad, promedioSobreCosto, promedioSobreVenta });
					
					Object[] acum = totales_.get(desc[0]);
					if (acum != null) {
						double totalVta_ = (double) acum[0];
						double totalCto_ = (double) acum[1];
						double totalUti_ = (double) acum[2];
						totalVta_ += totalVta;
						totalCto_ += totalCto;
						totalUti_ += utilidad;
						totales_.put(desc[0], new Object[]{ totalVta_, totalCto_, totalUti_ });
					} else {
						totales_.put(desc[0], new Object[]{ totalVta, totalCto, utilidad });
					}
				}
				
				Collections.sort(data, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String val1 = (String) o1[0];
						String val2 = (String) o2[0];
						int compare = val1.compareTo(val2);				
						return compare;
					}
				});
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_LISTA_FAMILIA;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new VentasListaPrecioFamilia(data, totales_);
				params.put("Titulo", "Ventas y Utilidad por Lista de Precio Detallado");
				params.put("Usuario", getUs().getNombre());
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("Sucursal", suc_);
				imprimirJasper(source, params, dataSource, filtro.getFormato());				

			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		
		/**
		 * reporte VEN-00045
		 */
		private void cobranzasVentasVendedorCliente(boolean mobile, String codReporte) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				
				if (vendedor == null) {
					Clients.showNotification("DEBE SELECCIONAR UN VENDEDOR..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> cobros = rr.getCobranzasPorVendedor(desde, hasta, vendedor.getId().longValue(), 0);
				List<Venta> ventas = rr.getVentasContadoPorVendedor(desde, hasta, vendedor.getId().longValue());
				List<NotaCredito> notasCredito = rr.getNotasCreditoPorVendedor(desde, hasta, vendedor.getId().longValue(), true);
				Map<Long, Double> values = new HashMap<Long, Double>();
				Map<Long, Double> values_ = new HashMap<Long, Double>();
				Map<Long, Double> values_nc = new HashMap<Long, Double>();
				Map<Long, String> clientes = new HashMap<Long, String>();

				for (Object[] cobro : cobros) {
					Recibo rec = (Recibo) cobro[0];
					Cliente cli = rec.getCliente();
					long idCliente = cli.getId().longValue();
					Double total = values.get(idCliente);
					if (total != null) {
						total += ((double) cobro[2] - Utiles.getIVA((double) cobro[2], 10));
					} else {
						total = ((double) cobro[2] - Utiles.getIVA((double) cobro[2], 10));
					}
					values.put(idCliente, total);
					clientes.put(idCliente, cli.getRazonSocial());				
				}	
				
				List<AjusteCtaCte> apls = rr.getAplicacionesAnticipos(desde, hasta, vendedor.getId().longValue());
				for (AjusteCtaCte apl : apls) {
					if (apl.getCredito().isVentaCredito()) {							
						long idEmpresa = apl.getCredito().getIdEmpresa();
						Cliente cli = rr.getClienteByEmpresa(idEmpresa);
						if (cli != null) {
							long idCliente = cli.getId();
							Double total = values.get(idCliente);
							if(total != null) {
								total += (apl.getImporte() - Utiles.getIVA(apl.getImporte(), 10));
							} else {
								total = (apl.getImporte() - Utiles.getIVA(apl.getImporte(), 10));
							}
							values.put(idCliente, total);
							clientes.put(idCliente, cli.getRazonSocial());
						}						
					}					
				}
				
				for (Venta venta : ventas) {
					Cliente cli = venta.getCliente();
					long idCliente = cli.getId().longValue();
					Double total = values_.get(idCliente);
					if (total != null) {
						total += venta.getTotalImporteGsSinIva();
					} else {
						total = venta.getTotalImporteGsSinIva();
					}
					values_.put(idCliente, total);
					clientes.put(idCliente, cli.getRazonSocial());
				}
				
				for (NotaCredito ncr : notasCredito) {
					Cliente cli = ncr.getCliente();
					long idCliente = cli.getId().longValue();
					Double total = values_nc.get(idCliente);
					if (total != null) {
						total += ncr.getTotalImporteGsSinIva();
					} else {
						total = ncr.getTotalImporteGsSinIva();
					}
					values_nc.put(idCliente, total);
					clientes.put(idCliente, cli.getRazonSocial());
				}

				for (Long idCliente : clientes.keySet()) {
					Double cobrado = values.get(idCliente);
					Double contado = values_.get(idCliente);
					Double notacre = values_nc.get(idCliente);
					
					double cobrado_ = cobrado != null? cobrado : 0;
					double contado_ = contado != null? contado : 0;
					double notacre_ = notacre != null? notacre : 0;
					cobrado_ = Utiles.getRedondeo(cobrado_);
					contado_ = Utiles.getRedondeo(contado_);
					notacre_ = Utiles.getRedondeo(notacre_);
					
					if (cobrado != null || contado != null || notacre != null) {
						data.add(new Object[] { clientes.get(idCliente), cobrado_, contado_, notacre_ });
					}
				}				

				ReporteTotalCobranzasVentasCliente rep = new ReporteTotalCobranzasVentasCliente(desde, hasta, vendedor.getRazonSocial());
				rep.setDatosReporte(data);
				rep.setTitulo(codReporte + " - Total cobranzas / ventas contado por vendedor / cliente");
				rep.setApaisada();				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00046
		 */
		private void listaPrecioPorDeposito_(boolean mobile, String codReporte) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Proveedor proveedor = filtro.getProveedorExterior();
				ArticuloFamilia flia = filtro.getFamilia_();
				ArticuloMarca marca = filtro.getMarca_();
				boolean stock = filtro.isFraccionado();
				
				long idProveedor = proveedor != null ? proveedor.getId() : 0;
				long idFamilia = flia != null ? flia.getId() : 0;
				long idMarca = marca != null ? marca.getId() : 0;

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<Object[]> arts = rr.getArticulos(idProveedor, idMarca, idFamilia, "");
				for (Object[] art : arts) {	
					long min = art[6] != null ? (long) art[6] : (long) 0;
					long may = art[7] != null ? (long) art[7] : (long) 0;
					long mac = art[11] != null ? (long) art[11] : (long) 0;
					if (stock) {						
						if (min > 0 || may > 0 || mac > 0) {
							data.add(new Object[] { art[1], art[2], min, may, mac, art[3] });
						}
					} else {
						data.add(new Object[] { art[1], art[2], min, may, mac, art[3] });
					}					
				}
				
				String proveedor_ = proveedor != null ? proveedor.getRazonSocial() : "TODOS..";
				
				ReporteListaPrecioPorDeposito_ rep = new ReporteListaPrecioPorDeposito_(proveedor_);
				rep.setDatosReporte(data);
				rep.setApaisada();	
				rep.setTitulo(codReporte + " - Lista de precios por depósito");

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00047
		 */
		private void cobranzasVendedorProveedorCliente(boolean mobile, String codReporte) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				
				if (vendedor == null) {
					Clients.showNotification("DEBE SELECCIONAR UN VENDEDOR..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> cobros = rr.getCobranzasPorVendedor(desde, hasta, vendedor.getId().longValue(), 0);
				double totalCobrado = 0;
				double totalCobradoSinIva = 0;
				Map<Long, Integer> prov_acum = new HashMap<Long, Integer>();
				Map<Long, List<Object[]>> _prov_acum = new HashMap<Long, List<Object[]>>();
				Map<Long, Double> values = new HashMap<Long, Double>();
				Map<Long, Double> values_ = new HashMap<Long, Double>();
				Map<Long, String> proveedores = new HashMap<Long, String>();

				for (Object[] cobro : cobros) {
					Recibo rec = (Recibo) cobro[0];
					totalCobrado += (double) cobro[2];
					ReciboDetalle item = (ReciboDetalle) cobro[3];
					Venta vta = item.getVenta();
					if (vta != null) {
						for (VentaDetalle det : vta.getDetalles()) {
							Proveedor prov = det.getArticulo().getProveedor();
							long idProveedor = prov != null ? prov.getId() : 0;
							Integer total = prov_acum.get(idProveedor);
							List<Object[]> list = null;
							if (total != null) {
								total ++;
								list = _prov_acum.get(idProveedor);
							} else {
								total = 1;
								list = new ArrayList<Object[]>();
							}
							double porcentaje = Utiles.obtenerPorcentajeDelValor(item.getMontoGs(), vta.getTotalImporteGs());
							double importe = Utiles.obtenerValorDelPorcentaje(det.getImporteGs(), porcentaje);
							double importeSinIva = Utiles.obtenerValorDelPorcentaje(det.getImporteGsSinIva(), porcentaje);
							list.add(new Object[] { rec.getNumero(), vta.getNumero(),
									det.getArticulo().getCodigoInterno(), importe, importeSinIva,
									vta.getCliente().getRazonSocial() });
							prov_acum.put(idProveedor, total);
							_prov_acum.put(idProveedor, list);
							Double acum = values.get(idProveedor);
							Double acum_ = values_.get(idProveedor);
							if (acum != null) {
								acum += importe;
								acum_ += importeSinIva;
							} else {
								acum = importe;
								acum_ = importeSinIva;
							}
							values.put(idProveedor, acum);
							values_.put(idProveedor, acum_);
							proveedores.put(idProveedor, prov != null ? prov.getRazonSocial() : "SIN PROVEEDOR");
						}
					} else {
						List<Object[]> dets = item.getDetalleVentaMigracion();
						for (Object[] det : dets) {
							Articulo art = rr.getArticulo((String) det[0]);
							if (art != null) {
								Proveedor prov = art.getProveedor();
								long idProveedor = prov != null ? prov.getId() : 0;
								Integer total = prov_acum.get(idProveedor);
								List<Object[]> list = null;
								if (total != null) {
									total ++;
									list = _prov_acum.get(idProveedor);
								} else {
									total = 1;
									list = new ArrayList<Object[]>();
								}
								double importe = (double) det[1];
								list.add(new Object[] { rec.getNumero(), "migracion", (String) det[0], importe,
										(importe - Utiles.getIVA(importe, 10)), "" });
								prov_acum.put(idProveedor, total);
								_prov_acum.put(idProveedor, list);
								proveedores.put(idProveedor, prov != null ? prov.getRazonSocial() : "SIN PROVEEDOR");
							}								
						}
					} 
				}
				
				List<AjusteCtaCte> apls = rr.getAplicacionesAnticipos(desde, hasta, vendedor.getId().longValue());
				for (AjusteCtaCte apl : apls) {
					if (apl.getCredito().isVentaCredito()) {
						Venta vta = (Venta) rr.getObject(Venta.class.getName(), apl.getCredito().getIdMovimientoOriginal());
						if (vta != null) {
							for (VentaDetalle det : vta.getDetalles()) {
								Proveedor prov = det.getArticulo().getProveedor();
								long idProveedor = prov != null ? prov.getId() : 0;
								Integer total = prov_acum.get(idProveedor);
								List<Object[]> list = null;
								if (total != null) {
									total ++;
									list = _prov_acum.get(idProveedor);
								} else {
									total = 1;
									list = new ArrayList<Object[]>();
								}
								double porcentaje = Utiles.obtenerPorcentajeDelValor(apl.getImporte(), vta.getTotalImporteGs());
								double importe = Utiles.obtenerValorDelPorcentaje(det.getImporteGs(), porcentaje);
								double importeSinIva = Utiles.obtenerValorDelPorcentaje(det.getImporteGsSinIva(), porcentaje);
								list.add(new Object[] { apl.getOrden(), vta.getNumero(),
										det.getArticulo().getCodigoInterno(), importe, importeSinIva,
										vta.getCliente().getRazonSocial() });
								prov_acum.put(idProveedor, total);
								_prov_acum.put(idProveedor, list);
								Double acum = values.get(idProveedor);
								Double acum_ = values_.get(idProveedor);
								if (acum != null) {
									acum += importe;
									acum_ += importeSinIva;
								} else {
									acum = importe;
									acum_ = importeSinIva;
								}
								values.put(idProveedor, acum);
								values_.put(idProveedor, acum_);
								proveedores.put(idProveedor, prov != null ? prov.getRazonSocial() : "SIN PROVEEDOR");
							}
						}
					}					
				}

				for (Long idProveedor : proveedores.keySet()) {
					Double cobrado = values.get(idProveedor);
					Double cobradoSinIva = values_.get(idProveedor);
					
					double cobrado_ = cobrado != null? cobrado : 0;
					List<Object[]> items = _prov_acum.get(idProveedor);
					
					if (cobrado != null) {
						totalCobrado += cobrado_;
						totalCobradoSinIva += cobradoSinIva;
						for (Object[] item : items) {
							data.add(new Object[] { proveedores.get(idProveedor), cobrado_, item, cobradoSinIva });
						}
					}
				}				

				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_COBRANZAS_VENDEDOR_DETALLADO;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CobranzasVendedorDetallado(data, totalCobrado, totalCobradoSinIva);
				params.put("Titulo", codReporte + " - Cobranzas por vendedor / proveedor detallado");
				params.put("Usuario", getUs().getNombre());
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("Vendedor", vendedor.getRazonSocial());
				imprimirJasper(source, params, dataSource, filtro.getFormato());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00048
		 */
		private void ventasClienteArticuloMes(boolean mobile, String codReporte) {
			try {					
				Object[] formato = filtro.getFormato();
				Cliente cli = filtro.getCliente();
				ArticuloFamilia familia = filtro.getFamilia_();
				Proveedor proveedor = filtro.getProveedor();
				Funcionario vendedor = filtro.getVendedor();
				List<Deposito> depositos = filtro.getSelectedDepositos();
				SucursalApp sucursal = filtro.getSelectedSucursal();
				String medida = filtro.getExpedicion();
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				boolean todos = filtro.isFraccionado();
				long idCliente = cli != null ? cli.getId() : 0;
				long idFamilia = familia != null ? familia.getId().longValue() : 0;
				long idProveedor = proveedor != null ? proveedor.getId().longValue() : 0;
				long idSucursal = sucursal != null ? sucursal.getId().longValue() : 0;
				long idVendedor = vendedor != null ? vendedor.getId().longValue() : 0;
				int mes1 = Utiles.getNumeroMes(desde) - 1;
				int mes2 = Utiles.getNumeroMes(hasta);
				int rango = mes2 - mes1;
				
				if (depositos.size() > 3) {
					Clients.showNotification("MÁXIMO HASTA 3 DEPÓSITOS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				Deposito dep1 = depositos.size() > 0 ? depositos.get(0) : null;
				Deposito dep2 = depositos.size() > 1 ? depositos.get(1) : null;
				Deposito dep3 = depositos.size() > 2 ? depositos.get(2) : null;
				
				List<Deposito> deps1 = new ArrayList<Deposito>();
				List<Deposito> deps2 = new ArrayList<Deposito>();
				List<Deposito> deps3 = new ArrayList<Deposito>();
				
				if (dep1 != null) deps1.add(dep1);
				if (dep2 != null) deps2.add(dep2);
				if (dep3 != null) deps3.add(dep3);
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> articulos = new ArrayList<>();
				if (todos) {
					articulos = rr.getArticulos__(idFamilia, idProveedor, medida);
				}
				List<Object[]> ventas = rr.getVentasDetallado_(desde, hasta, idCliente, idFamilia, idProveedor, idVendedor, idSucursal, medida);
				List<Object[]> ncs = rr.getNotasCreditoDetallado_(desde, hasta, idCliente, idFamilia, idProveedor, idVendedor, idSucursal, medida, null);
				
				List<HistoricoMovimientoArticulo> list = new ArrayList<HistoricoMovimientoArticulo>();
				Map<String, Double> cants = new HashMap<String, Double>();
				Map<String, Double> importes = new HashMap<String, Double>();
				Map<String, Double> volumens = new HashMap<String, Double>();
				Map<String, Object[]> datos = new HashMap<String, Object[]>();
				
				for (Object[] art : articulos) {
					int mes = Utiles.getNumeroMes(desde);
					String cod = (String) art[1];
					String des = (String) art[8];
					String prove = (String) art[15];
					String marca = (String) art[16];
					String medid = (String) art[17];
					String key = cod + ";" + des + ";" + prove + ";" + marca + ";" + medid + ";" + mes;
					cants.put(key, 0.0);
				}
				
				for (Object[] venta : ventas) {
					int mes = Utiles.getNumeroMes((Date) venta[4]);
					String cod = (String) venta[1];
					String des = (String) venta[8];
					String prove = (String) venta[15];
					String marca = (String) venta[16];
					String medid = (String) venta[17];
					String key = cod + ";" + des + ";" + prove + ";" + marca + ";" + medid + ";" + mes;
					Double acum = cants.get(key);
					if (acum != null) {
						acum += ((Double) venta[3]);
						cants.put(key, acum);
						volumens.put(cod, (Double) venta[2]);
						datos.put(cod, new Object[] { (Double) venta[9], (Double) venta[10] });
					} else {
						cants.put(key, ((Double) (venta[3])));
						volumens.put(cod, (Double) venta[2]);
					}
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ += ((Double) venta[6]);
						importes.put(key, acum_);
					} else {
						importes.put(key, ((Double) (venta[6])));
					}
				}
				
				for (Object[] nc : ncs) {
					int mes = Utiles.getNumeroMes((Date) nc[4]);
					String cod = (String) nc[1];
					String des = (String) nc[8];
					String prove = (String) nc[15];
					String marca = (String) nc[16];
					String medid = (String) nc[17];
					String key = cod + ";" + des + ";" + prove + ";" + marca + ";" + medid + ";" + mes;
					Double acum = cants.get(key);
					if (acum != null) {
						acum -= ((Double) nc[3]);
						cants.put(key, acum);
						volumens.put(cod, (Double) nc[2]);
					} else {
						cants.put(key, ((Double) (nc[3]) * -1));
						volumens.put(cod, (Double) nc[2]);
					}
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ -= ((Double) nc[6]);
					} else {
						importes.put(key, ((Double) (nc[6]) * -1));
					}
				}
				
				Map<String, String> keys = new HashMap<String, String>();
				
				for (String key : cants.keySet()) {					
					String codigo = key.split(";")[0];
					String descripcion = key.split(";")[1];
					String proveedor_ = key.split(";")[2];
					String marca = key.split(";")[3];
					String medida_ = key.split(";")[4];
					String key_ = codigo + ";" + descripcion + ";" + proveedor_ + ";" + marca + ";" + medida_;
					
					if (keys.get(key_) == null) {
						Double cantidad = cants.get(key);
						
						Double cantEnero = cants.get(key_ + ";1");
						if (cantEnero == null) cantEnero = 0.0; 
						
						Double cantFebrero = cants.get(key_ + ";2");
						if (cantFebrero == null) cantFebrero = 0.0;
						
						Double cantMarzo = cants.get(key_ + ";3");
						if (cantMarzo == null) cantMarzo = 0.0;
						
						Double cantAbril = cants.get(key_ + ";4");
						if (cantAbril == null) cantAbril = 0.0;
						
						Double cantMayo = cants.get(key_ + ";5");
						if (cantMayo == null) cantMayo = 0.0;
						
						Double cantJunio = cants.get(key_ + ";6");
						if (cantJunio == null) cantJunio = 0.0;
						
						Double cantJulio = cants.get(key_ + ";7");
						if (cantJulio == null) cantJulio = 0.0;
						
						Double cantAgosto = cants.get(key_ + ";8");
						if (cantAgosto == null) cantAgosto = 0.0;
						
						Double cantSetiembre = cants.get(key_ + ";9");
						if (cantSetiembre == null) cantSetiembre = 0.0;
						
						Double cantOctubre = cants.get(key_ + ";10");
						if (cantOctubre == null) cantOctubre = 0.0;
						
						Double cantNoviembre = cants.get(key_ + ";11");
						if (cantNoviembre == null) cantNoviembre = 0.0;
						
						Double cantDiciembre = cants.get(key_ + ";12");
						if (cantDiciembre == null) cantDiciembre = 0.0;
						
						Double impEnero = importes.get(key_ + ";1");
						if (impEnero == null) impEnero = 0.0;
						
						Double impFebrero = importes.get(key_ + ";2");
						if (impFebrero == null) impFebrero = 0.0;
						
						Double impMarzo = importes.get(key_ + ";3");
						if (impMarzo == null) impMarzo = 0.0;
						
						Double impAbril = importes.get(key_ + ";4");
						if (impAbril == null) impAbril = 0.0;
						
						Double impMayo = importes.get(key_ + ";5");
						if (impMayo == null) impMayo = 0.0;
						
						Double impJunio = importes.get(key_ + ";6");
						if (impJunio == null) impJunio = 0.0;
						
						Double impJulio = importes.get(key_ + ";7");
						if (impJulio == null) impJulio = 0.0;
						
						Double impAgosto = importes.get(key_ + ";8");
						if (impAgosto == null) impAgosto = 0.0;
						
						Double impSetiembre = importes.get(key_ + ";9");
						if (impSetiembre == null) impSetiembre = 0.0;
						
						Double impOctubre = importes.get(key_ + ";10");
						if (impOctubre == null) impOctubre = 0.0;
						
						Double impNoviembre = importes.get(key_ + ";11");
						if (impNoviembre == null) impNoviembre = 0.0;
						
						Double impDiciembre = importes.get(key_ + ";12");
						if (impDiciembre == null) impDiciembre = 0.0;
						
						Object[] costoPrecio = rr.getCostoPrecio(codigo);
						Long stock1 = rr.getStock(codigo, deps1);
						Long stock2 = rr.getStock(codigo, deps2);
						Long stock3 = rr.getStock(codigo, deps3);
						long stock1_ = stock1 != null? stock1 : 0;
						long stock2_ = stock2 != null? stock2 : 0;
						long stock3_ = stock3 != null? stock3 : 0;
						Double volumen = volumens.get(codigo);
						HistoricoMovimientoArticulo hist = new HistoricoMovimientoArticulo();
						hist.setDescripcion(codigo);
						hist.setReferencia(descripcion);
						hist.setProveedor(proveedor_);
						hist.setMarca(marca);
						hist.setCodigoOriginal(medida_);
						hist.setLitraje(cantidad);
						hist.setCoeficiente(volumen != null ? (volumen * (stock1_ + stock2_ + stock3_)) : 0.0);
						hist.setEnero_(cantEnero);
						hist.setFebrero_(cantFebrero);
						hist.setMarzo_(cantMarzo);
						hist.setAbril_(cantAbril);
						hist.setMayo_(cantMayo);
						hist.setJunio_(cantJunio);
						hist.setJulio_(cantJulio);
						hist.setAgosto_(cantAgosto);
						hist.setSetiembre_(cantSetiembre);
						hist.setOctubre_(cantOctubre);
						hist.setNoviembre_(cantNoviembre);
						hist.setDiciembre_(cantDiciembre);
						hist.set_enero(impEnero);
						hist.set_febrero(impFebrero);
						hist.set_marzo(impMarzo);
						hist.set_abril(impAbril);
						hist.set_mayo(impMayo);
						hist.set_junio(impJunio);
						hist.set_julio(impJulio);
						hist.set_agosto(impAgosto);
						hist.set_setiembre(impSetiembre);
						hist.set_octubre(impOctubre);
						hist.set_noviembre(impNoviembre);
						hist.set_diciembre(impDiciembre);
						hist.setCostoGs((double) costoPrecio[1]);
						hist.setCostoFobGs((double) costoPrecio[2]);
						hist.setPrecioMinorista((double) costoPrecio[3]);
						hist.setPrecioLista((double) costoPrecio[4]);
						hist.setStock1(stock1);
						hist.setStock2(stock2);
						hist.setStock3(stock3);
						hist.setTotal_(hist.getEnero_() + hist.getFebrero_() + hist.getMarzo_() + hist.getAbril_() + hist.getMayo_() + hist.getJunio_()
								+ hist.getJulio_() + hist.getAgosto_() + hist.getSetiembre_() + hist.getOctubre_() + hist.getNoviembre_() + hist.getDiciembre_());
						list.add(hist);			
						keys.put(key_, key_);
					}					
				}				
				String format = (String) formato[0];
				String csv = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_CSV[0];
				String xls = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS[0];
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_CLIENTE_ARTICULO;
				if (format.equals(csv) || format.equals(xls)) {
					source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_CLIENTE_ARTICULO_;
				}
				
				String flia = familia != null ? familia.getDescripcion() : "TODOS..";
				String suc = sucursal != null ? sucursal.getDescripcion() : "TODOS..";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("Usuario", getUs().getNombre());
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("Cliente", cli != null ? cli.getRazonSocial() : "TODOS..");
				params.put("Familia", flia);
				params.put("stock1", dep1 != null ? dep1.getDescripcion() : "- - -");
				params.put("stock2", dep2 != null ? dep2.getDescripcion() : "- - -");
				params.put("stock3", dep3 != null ? dep3.getDescripcion() : "- - -");
				params.put("Titulo", codReporte + " - VENTAS POR CLIENTE POR ARTICULO POR MES");
				params.put("Proveedor", proveedor != null ? proveedor.getRazonSocial() : "TODOS..");
				params.put("Sucursal", suc);
				JRDataSource dataSource = new VentasClienteArticulo(list, rango);
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00049
		 */
		private void ventasProveedorClientePorMes(boolean mobile, String codReporte) {
			try {					
				Object[] formato = filtro.getFormato();
				Cliente cli = filtro.getCliente();
				ArticuloFamilia familia = filtro.getFamilia_();
				Proveedor proveedor = filtro.getProveedor();
				Funcionario vendedor = filtro.getVendedor();
				SucursalApp sucursal = filtro.getSelectedSucursal();
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				long idCliente = cli != null ? cli.getId() : 0;
				long idFamilia = familia != null ? familia.getId().longValue() : 0;
				long idProveedor = proveedor != null ? proveedor.getId().longValue() : 0;
				long idVendedor = vendedor != null ? vendedor.getId().longValue() : 0;
				long idSucursal = sucursal != null ? sucursal.getId().longValue() : 0;
				int mes1 = Utiles.getNumeroMes(desde) - 1;
				int mes2 = Utiles.getNumeroMes(hasta);
				int rango = mes2 - mes1;
				
				RegisterDomain rr = RegisterDomain.getInstance();				
				List<Object[]> ventas = rr.getVentasDetallado_(desde, hasta, idCliente, idFamilia, idProveedor, idVendedor, idSucursal, "");
				List<Object[]> ncs = rr.getNotasCreditoDetallado_(desde, hasta, idCliente, idFamilia, idProveedor, idVendedor, idSucursal, "", null);
				List<NotaCredito> ncs_ = rr.getNotasCreditoVentaByMotivo(desde, hasta, Configuracion.SIGLA_TIPO_NC_MOTIVO_DESCUENTO);
				
				List<HistoricoMovimientoArticulo> list = new ArrayList<HistoricoMovimientoArticulo>();
				Map<String, Double> cants = new HashMap<String, Double>();
				Map<String, Double> importes = new HashMap<String, Double>();
				Map<String, Double> volumens = new HashMap<String, Double>();
				Map<String, Object[]> datos = new HashMap<String, Object[]>();
				Map<String, Object[]> clientes = new HashMap<String, Object[]>();
				
				for (Object[] venta : ventas) {
					int mes = Utiles.getNumeroMes((Date) venta[4]);
					String clt = (String) venta[5];
					String key = clt + ";" + mes;
					Double acum = cants.get(key);
					if (acum != null) {
						acum += ((Double) venta[3]);
						cants.put(key, acum);
						volumens.put(clt, (Double) venta[2]);
						datos.put(clt, new Object[] { (Double) venta[9], (Double) venta[10] });
					} else {
						cants.put(key, ((Double) (venta[3])));
						volumens.put(clt, (Double) venta[2]);
					}
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ += ((Double) venta[6]);
						importes.put(key, acum_);
					} else {
						importes.put(key, ((Double) (venta[6])));
					}
					Object[] cld = clientes.get(clt);
					if (cld == null) {
						cld = new Object[] { venta[11], venta[12], venta[13], venta[14] };
						clientes.put(clt, cld);
					}
				}
				
				for (Object[] nc : ncs) {
					int mes = Utiles.getNumeroMes((Date) nc[4]);
					String clt = (String) nc[5];
					String key = clt + ";" + mes;
					Double acum = cants.get(key);
					if (acum != null) {
						acum -= ((Double) nc[3]);
						cants.put(key, acum);
						volumens.put(clt, (Double) nc[2]);
					} else {
						cants.put(key, ((Double) (nc[3]) * -1));
						volumens.put(clt, (Double) nc[2]);
					}
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ -= ((Double) nc[6]);
						importes.put(key, acum_);
					} else {
						importes.put(key, ((Double) (nc[6]) * -1));
					}
					Object[] cld = clientes.get(clt);
					if (cld == null) {
						cld = new Object[] { nc[11], nc[12], nc[13], nc[14] };
						clientes.put(clt, cld);
					}
				}
				
				for (NotaCredito nc : ncs_) {
					int mes = Utiles.getNumeroMes(nc.getFechaEmision());
					String clt = nc.getCliente().getRazonSocial();
					String key = clt + ";" + mes;				
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ -= (idProveedor > 0 ? nc.getImporteByProveedor(idProveedor) : nc.getImporteGs());
						importes.put(key, acum_);
					} else {
						importes.put(key, ((idProveedor > 0 ? nc.getImporteByProveedor(idProveedor) : nc.getImporteGs()) * -1));
					}
					Object[] cld = clientes.get(clt);
					if (cld == null) {
						cld = new Object[] { nc.getCliente().getRuc(), nc.getVendedor().getNombreEmpresa(),
								nc.getCliente().getRubro(), nc.getCliente().getEmpresa().getZona() };
						clientes.put(clt, cld);
					}
				}
				
				Map<String, String> keys = new HashMap<String, String>();
				
				for (String key : cants.keySet()) {					
					String codigo = key.split(";")[0];
					String key_ = codigo;
					String ruc = "";
					String vendedor_ = "";
					String rubro = "";
					String zona = "";
					Object[] dcli = clientes.get(key_);
					
					if (dcli != null) {
						ruc = (String) dcli[0];
						vendedor_ = (String) dcli[1];
						rubro = (String) dcli[2];
						zona = (String) dcli[3];
					}
					
					if (keys.get(key_) == null) {
						Double cantidad = cants.get(key);
						
						Double cantEnero = cants.get(key_ + ";1");
						if (cantEnero == null) cantEnero = 0.0; 
						
						Double cantFebrero = cants.get(key_ + ";2");
						if (cantFebrero == null) cantFebrero = 0.0;
						
						Double cantMarzo = cants.get(key_ + ";3");
						if (cantMarzo == null) cantMarzo = 0.0;
						
						Double cantAbril = cants.get(key_ + ";4");
						if (cantAbril == null) cantAbril = 0.0;
						
						Double cantMayo = cants.get(key_ + ";5");
						if (cantMayo == null) cantMayo = 0.0;
						
						Double cantJunio = cants.get(key_ + ";6");
						if (cantJunio == null) cantJunio = 0.0;
						
						Double cantJulio = cants.get(key_ + ";7");
						if (cantJulio == null) cantJulio = 0.0;
						
						Double cantAgosto = cants.get(key_ + ";8");
						if (cantAgosto == null) cantAgosto = 0.0;
						
						Double cantSetiembre = cants.get(key_ + ";9");
						if (cantSetiembre == null) cantSetiembre = 0.0;
						
						Double cantOctubre = cants.get(key_ + ";10");
						if (cantOctubre == null) cantOctubre = 0.0;
						
						Double cantNoviembre = cants.get(key_ + ";11");
						if (cantNoviembre == null) cantNoviembre = 0.0;
						
						Double cantDiciembre = cants.get(key_ + ";12");
						if (cantDiciembre == null) cantDiciembre = 0.0;
						
						Double impEnero = importes.get(key_ + ";1");
						if (impEnero == null) impEnero = 0.0;
						
						Double impFebrero = importes.get(key_ + ";2");
						if (impFebrero == null) impFebrero = 0.0;
						
						Double impMarzo = importes.get(key_ + ";3");
						if (impMarzo == null) impMarzo = 0.0;
						
						Double impAbril = importes.get(key_ + ";4");
						if (impAbril == null) impAbril = 0.0;
						
						Double impMayo = importes.get(key_ + ";5");
						if (impMayo == null) impMayo = 0.0;
						
						Double impJunio = importes.get(key_ + ";6");
						if (impJunio == null) impJunio = 0.0;
						
						Double impJulio = importes.get(key_ + ";7");
						if (impJulio == null) impJulio = 0.0;
						
						Double impAgosto = importes.get(key_ + ";8");
						if (impAgosto == null) impAgosto = 0.0;
						
						Double impSetiembre = importes.get(key_ + ";9");
						if (impSetiembre == null) impSetiembre = 0.0;
						
						Double impOctubre = importes.get(key_ + ";10");
						if (impOctubre == null) impOctubre = 0.0;
						
						Double impNoviembre = importes.get(key_ + ";11");
						if (impNoviembre == null) impNoviembre = 0.0;
						
						Double impDiciembre = importes.get(key_ + ";12");
						if (impDiciembre == null) impDiciembre = 0.0;
						
						HistoricoMovimientoArticulo hist = new HistoricoMovimientoArticulo();
						hist.setDescripcion(codigo);
						hist.setRuc(ruc);
						hist.setVendedor(vendedor_);
						hist.setRubro(rubro);
						hist.setZona(zona);
						hist.setLitraje(cantidad);
						hist.setCoeficiente(0.0);
						hist.setEnero_(cantEnero);
						hist.setFebrero_(cantFebrero);
						hist.setMarzo_(cantMarzo);
						hist.setAbril_(cantAbril);
						hist.setMayo_(cantMayo);
						hist.setJunio_(cantJunio);
						hist.setJulio_(cantJulio);
						hist.setAgosto_(cantAgosto);
						hist.setSetiembre_(cantSetiembre);
						hist.setOctubre_(cantOctubre);
						hist.setNoviembre_(cantNoviembre);
						hist.setDiciembre_(cantDiciembre);
						hist.set_enero(impEnero);
						hist.set_febrero(impFebrero);
						hist.set_marzo(impMarzo);
						hist.set_abril(impAbril);
						hist.set_mayo(impMayo);
						hist.set_junio(impJunio);
						hist.set_julio(impJulio);
						hist.set_agosto(impAgosto);
						hist.set_setiembre(impSetiembre);
						hist.set_octubre(impOctubre);
						hist.set_noviembre(impNoviembre);
						hist.set_diciembre(impDiciembre);
						hist.setCostoGs(0.0);
						hist.setCostoFobGs(0.0);
						hist.setTotal_(hist.get_enero() + hist.get_febrero() + hist.get_marzo() + hist.get_abril() + hist.get_mayo() + hist.get_junio()
								+ hist.get_julio() + hist.get_agosto() + hist.get_setiembre() + hist.get_octubre() + hist.get_noviembre() + hist.get_diciembre());
						list.add(hist);			
						keys.put(key_, key_);
					}					
				}				
				String format = (String) formato[0];
				String csv = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_CSV[0];
				String xls = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS[0];
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_PROVEEDOR_CLIENTE;
				if (format.equals(csv) || format.equals(xls)) {
					source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_PROVEEDOR_CLIENTE_;
				}
				
				String flia = familia != null ? familia.getDescripcion() : "TODOS..";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", codReporte + " - VENTAS POR PROVEEDOR POR CLIENTE");
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("Cliente", cli != null ? cli.getRazonSocial() : "TODOS..");
				params.put("Familia", flia);
				params.put("Proveedor", proveedor != null ? proveedor.getRazonSocial() : "TODOS..");
				JRDataSource dataSource = new VentasProveedorCliente(list, rango);
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00050
		 */
		private void ventasProveedorPorMes(boolean mobile, String codReporte) {
			try {					
				Object[] formato = filtro.getFormato();
				Cliente cli = filtro.getCliente();
				ArticuloFamilia familia = filtro.getFamilia_();
				Proveedor proveedor = filtro.getProveedor();
				Funcionario vendedor = filtro.getVendedor();
				SucursalApp sucursal = filtro.getSelectedSucursal();
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				long idCliente = cli != null ? cli.getId() : 0;
				long idFamilia = familia != null ? familia.getId().longValue() : 0;
				long idProveedor = proveedor != null ? proveedor.getId().longValue() : 0;
				long idVendedor = vendedor != null ? vendedor.getId().longValue() : 0;
				long idSucursal = sucursal != null ? sucursal.getId().longValue() : 0;
				int mes1 = Utiles.getNumeroMes(desde) - 1;
				int mes2 = Utiles.getNumeroMes(hasta);
				int rango = mes2 - mes1;
				
				RegisterDomain rr = RegisterDomain.getInstance();				
				List<Object[]> ventas = rr.getVentasDetallado_(desde, hasta, idCliente, idFamilia, idProveedor, idVendedor, idSucursal, "");
				List<Object[]> ncs = rr.getNotasCreditoDetallado_(desde, hasta, idCliente, idFamilia, idProveedor, idVendedor, idSucursal, "", null);
				
				List<HistoricoMovimientoArticulo> list = new ArrayList<HistoricoMovimientoArticulo>();
				Map<String, Double> cants = new HashMap<String, Double>();
				Map<String, Double> importes = new HashMap<String, Double>();
				Map<String, Double> volumens = new HashMap<String, Double>();
				Map<String, Object[]> datos = new HashMap<String, Object[]>();
				Map<String, Object[]> proveedores = new HashMap<String, Object[]>();
				
				for (Object[] venta : ventas) {
					int mes = Utiles.getNumeroMes((Date) venta[4]);
					String prv = (String) venta[15];
					String key = prv + ";" + mes;
					Double acum = cants.get(key);
					if (acum != null) {
						acum += ((Double) venta[3]);
						cants.put(key, acum);
						volumens.put(prv, (Double) venta[2]);
						datos.put(prv, new Object[] { (Double) venta[9], (Double) venta[10] });
					} else {
						cants.put(key, ((Double) (venta[3])));
						volumens.put(prv, (Double) venta[2]);
					}
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ += ((Double) venta[6]);
						importes.put(key, acum_);
					} else {
						importes.put(key, ((Double) (venta[6])));
					}
					Object[] pro = proveedores.get(prv);
					if (pro == null) {
						pro = new Object[] { venta[11], venta[12], venta[13], venta[14] };
						proveedores.put(prv, pro);
					}
				}
				
				for (Object[] nc : ncs) {
					int mes = Utiles.getNumeroMes((Date) nc[4]);
					String prv = (String) nc[15];
					String key = prv + ";" + mes;
					Double acum = cants.get(key);
					if (acum != null) {
						acum -= ((Double) nc[3]);
						cants.put(key, acum);
						volumens.put(prv, (Double) nc[2]);
					} else {
						cants.put(key, ((Double) (nc[3]) * -1));
						volumens.put(prv, (Double) nc[2]);
					}
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ -= ((Double) nc[6]);
					} else {
						importes.put(key, ((Double) (nc[6]) * -1));
					}
					Object[] pro = proveedores.get(prv);
					if (pro == null) {
						pro = new Object[] { nc[11], nc[12], nc[13], nc[14] };
						proveedores.put(prv, pro);
					}
				}
				
				Map<String, String> keys = new HashMap<String, String>();
				
				for (String key : cants.keySet()) {					
					String codigo = key.split(";")[0];
					String key_ = codigo;
					String ruc = "";
					String vendedor_ = "";
					String rubro = "";
					String zona = "";
					Object[] dcli = proveedores.get(key_);
					
					if (dcli != null) {
						ruc = (String) dcli[0];
						vendedor_ = (String) dcli[1];
						rubro = (String) dcli[2];
						zona = (String) dcli[3];
					}
					
					if (keys.get(key_) == null) {
						Double cantidad = cants.get(key);
						
						Double cantEnero = cants.get(key_ + ";1");
						if (cantEnero == null) cantEnero = 0.0; 
						
						Double cantFebrero = cants.get(key_ + ";2");
						if (cantFebrero == null) cantFebrero = 0.0;
						
						Double cantMarzo = cants.get(key_ + ";3");
						if (cantMarzo == null) cantMarzo = 0.0;
						
						Double cantAbril = cants.get(key_ + ";4");
						if (cantAbril == null) cantAbril = 0.0;
						
						Double cantMayo = cants.get(key_ + ";5");
						if (cantMayo == null) cantMayo = 0.0;
						
						Double cantJunio = cants.get(key_ + ";6");
						if (cantJunio == null) cantJunio = 0.0;
						
						Double cantJulio = cants.get(key_ + ";7");
						if (cantJulio == null) cantJulio = 0.0;
						
						Double cantAgosto = cants.get(key_ + ";8");
						if (cantAgosto == null) cantAgosto = 0.0;
						
						Double cantSetiembre = cants.get(key_ + ";9");
						if (cantSetiembre == null) cantSetiembre = 0.0;
						
						Double cantOctubre = cants.get(key_ + ";10");
						if (cantOctubre == null) cantOctubre = 0.0;
						
						Double cantNoviembre = cants.get(key_ + ";11");
						if (cantNoviembre == null) cantNoviembre = 0.0;
						
						Double cantDiciembre = cants.get(key_ + ";12");
						if (cantDiciembre == null) cantDiciembre = 0.0;
						
						Double impEnero = importes.get(key_ + ";1");
						if (impEnero == null) impEnero = 0.0;
						
						Double impFebrero = importes.get(key_ + ";2");
						if (impFebrero == null) impFebrero = 0.0;
						
						Double impMarzo = importes.get(key_ + ";3");
						if (impMarzo == null) impMarzo = 0.0;
						
						Double impAbril = importes.get(key_ + ";4");
						if (impAbril == null) impAbril = 0.0;
						
						Double impMayo = importes.get(key_ + ";5");
						if (impMayo == null) impMayo = 0.0;
						
						Double impJunio = importes.get(key_ + ";6");
						if (impJunio == null) impJunio = 0.0;
						
						Double impJulio = importes.get(key_ + ";7");
						if (impJulio == null) impJulio = 0.0;
						
						Double impAgosto = importes.get(key_ + ";8");
						if (impAgosto == null) impAgosto = 0.0;
						
						Double impSetiembre = importes.get(key_ + ";9");
						if (impSetiembre == null) impSetiembre = 0.0;
						
						Double impOctubre = importes.get(key_ + ";10");
						if (impOctubre == null) impOctubre = 0.0;
						
						Double impNoviembre = importes.get(key_ + ";11");
						if (impNoviembre == null) impNoviembre = 0.0;
						
						Double impDiciembre = importes.get(key_ + ";12");
						if (impDiciembre == null) impDiciembre = 0.0;
						
						HistoricoMovimientoArticulo hist = new HistoricoMovimientoArticulo();
						hist.setDescripcion(codigo);
						hist.setRuc(ruc);
						hist.setVendedor(vendedor_);
						hist.setRubro(rubro);
						hist.setZona(zona);
						hist.setLitraje(cantidad);
						hist.setCoeficiente(0.0);
						hist.setEnero_(cantEnero);
						hist.setFebrero_(cantFebrero);
						hist.setMarzo_(cantMarzo);
						hist.setAbril_(cantAbril);
						hist.setMayo_(cantMayo);
						hist.setJunio_(cantJunio);
						hist.setJulio_(cantJulio);
						hist.setAgosto_(cantAgosto);
						hist.setSetiembre_(cantSetiembre);
						hist.setOctubre_(cantOctubre);
						hist.setNoviembre_(cantNoviembre);
						hist.setDiciembre_(cantDiciembre);
						hist.set_enero(impEnero);
						hist.set_febrero(impFebrero);
						hist.set_marzo(impMarzo);
						hist.set_abril(impAbril);
						hist.set_mayo(impMayo);
						hist.set_junio(impJunio);
						hist.set_julio(impJulio);
						hist.set_agosto(impAgosto);
						hist.set_setiembre(impSetiembre);
						hist.set_octubre(impOctubre);
						hist.set_noviembre(impNoviembre);
						hist.set_diciembre(impDiciembre);
						hist.setCostoGs(0.0);
						hist.setCostoFobGs(0.0);
						hist.setTotal_(hist.get_enero() + hist.get_febrero() + hist.get_marzo() + hist.get_abril() + hist.get_mayo() + hist.get_junio()
								+ hist.get_julio() + hist.get_agosto() + hist.get_setiembre() + hist.get_octubre() + hist.get_noviembre() + hist.get_diciembre());
						list.add(hist);			
						keys.put(key_, key_);
					}					
				}				
				String format = (String) formato[0];
				String csv = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_CSV[0];
				String xls = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS[0];
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_PROVEEDOR;
				if (format.equals(csv) || format.equals(xls)) {
					source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_PROVEEDOR_;
				}
				
				String flia = familia != null ? familia.getDescripcion() : "TODOS..";
				String suc = sucursal != null ? sucursal.getDescripcion() : "TODOS..";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", codReporte + " - VENTAS POR PROVEEDOR");
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("Cliente", cli != null ? cli.getRazonSocial() : "TODOS..");
				params.put("Familia", flia);
				params.put("Sucursal", suc);
				params.put("Proveedor", proveedor != null ? proveedor.getRazonSocial() : "TODOS..");
				JRDataSource dataSource = new VentasProveedorCliente(list, rango);
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00051
		 */
		private void ventasProveedorClientePorMes_(boolean mobile, String codReporte) {
			try {					
				Object[] formato = filtro.getFormato();
				Cliente cli = filtro.getCliente();
				ArticuloFamilia familia = filtro.getFamilia_();
				Proveedor proveedor = filtro.getProveedor();
				Funcionario vendedor = filtro.getVendedor();
				SucursalApp sucursal = filtro.getSelectedSucursal();
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				long idCliente = cli != null ? cli.getId() : 0;
				long idFamilia = familia != null ? familia.getId().longValue() : 0;
				long idProveedor = proveedor != null ? proveedor.getId().longValue() : 0;
				long idVendedor = vendedor != null ? vendedor.getId().longValue() : 0;
				long idSucursal = sucursal != null ? sucursal.getId().longValue() : 0;
				int mes1 = Utiles.getNumeroMes(desde) - 1;
				int mes2 = Utiles.getNumeroMes(hasta);
				int rango = mes2 - mes1;
				
				RegisterDomain rr = RegisterDomain.getInstance();				
				List<Object[]> ventas = rr.getVentasDetallado_(desde, hasta, idCliente, idFamilia, idProveedor, idVendedor, idSucursal, "");
				List<Object[]> ncs = rr.getNotasCreditoDetallado_(desde, hasta, idCliente, idFamilia, idProveedor, idVendedor, idSucursal, "", null);
				
				List<HistoricoMovimientoArticulo> list = new ArrayList<HistoricoMovimientoArticulo>();
				Map<String, Double> cants = new HashMap<String, Double>();
				Map<String, Double> importes = new HashMap<String, Double>();
				Map<String, Double> volumens = new HashMap<String, Double>();
				Map<String, Object[]> datos = new HashMap<String, Object[]>();
				Map<String, Object[]> clientes = new HashMap<String, Object[]>();
				
				for (Object[] venta : ventas) {
					int mes = Utiles.getNumeroMes((Date) venta[4]);
					String clt = (String) venta[5];
					String key = clt + ";" + mes;
					Double acum = cants.get(key);
					if (acum != null) {
						acum += ((Double) venta[3]);
						cants.put(key, acum);
						volumens.put(clt, (Double) venta[2]);
						datos.put(clt, new Object[] { (Double) venta[9], (Double) venta[10] });
					} else {
						cants.put(key, ((Double) (venta[3])));
						volumens.put(clt, (Double) venta[2]);
					}
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ += ((Double) venta[6]);
						importes.put(key, acum_);
					} else {
						importes.put(key, ((Double) (venta[6])));
					}
					Object[] cld = clientes.get(clt);
					if (cld == null) {
						cld = new Object[] { venta[11], venta[12], venta[13], venta[14] };
						clientes.put(clt, cld);
					}
				}
				
				for (Object[] nc : ncs) {
					int mes = Utiles.getNumeroMes((Date) nc[4]);
					String clt = (String) nc[5];
					String key = clt + ";" + mes;
					Double acum = cants.get(key);
					if (acum != null) {
						acum -= ((Double) nc[3]);
						cants.put(key, acum);
						volumens.put(clt, (Double) nc[2]);
					} else {
						cants.put(key, ((Double) (nc[3]) * -1));
						volumens.put(clt, (Double) nc[2]);
					}
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ -= ((Double) nc[6]);
					} else {
						importes.put(key, ((Double) (nc[6]) * -1));
					}
					Object[] cld = clientes.get(clt);
					if (cld == null) {
						cld = new Object[] { nc[11], nc[12], nc[13], nc[14] };
						clientes.put(clt, cld);
					}
				}
				
				Map<String, String> keys = new HashMap<String, String>();
				
				for (String key : cants.keySet()) {					
					String codigo = key.split(";")[0];
					String key_ = codigo;
					String ruc = "";
					String vendedor_ = "";
					String rubro = "";
					String zona = "";
					Object[] dcli = clientes.get(key_);
					
					if (dcli != null) {
						ruc = (String) dcli[0];
						vendedor_ = (String) dcli[1];
						rubro = (String) dcli[2];
						zona = (String) dcli[3];
					}
					
					if (keys.get(key_) == null) {
						Double cantidad = cants.get(key);
						
						Double cantEnero = cants.get(key_ + ";1");
						if (cantEnero == null) cantEnero = 0.0; 
						
						Double cantFebrero = cants.get(key_ + ";2");
						if (cantFebrero == null) cantFebrero = 0.0;
						
						Double cantMarzo = cants.get(key_ + ";3");
						if (cantMarzo == null) cantMarzo = 0.0;
						
						Double cantAbril = cants.get(key_ + ";4");
						if (cantAbril == null) cantAbril = 0.0;
						
						Double cantMayo = cants.get(key_ + ";5");
						if (cantMayo == null) cantMayo = 0.0;
						
						Double cantJunio = cants.get(key_ + ";6");
						if (cantJunio == null) cantJunio = 0.0;
						
						Double cantJulio = cants.get(key_ + ";7");
						if (cantJulio == null) cantJulio = 0.0;
						
						Double cantAgosto = cants.get(key_ + ";8");
						if (cantAgosto == null) cantAgosto = 0.0;
						
						Double cantSetiembre = cants.get(key_ + ";9");
						if (cantSetiembre == null) cantSetiembre = 0.0;
						
						Double cantOctubre = cants.get(key_ + ";10");
						if (cantOctubre == null) cantOctubre = 0.0;
						
						Double cantNoviembre = cants.get(key_ + ";11");
						if (cantNoviembre == null) cantNoviembre = 0.0;
						
						Double cantDiciembre = cants.get(key_ + ";12");
						if (cantDiciembre == null) cantDiciembre = 0.0;
						
						Double impEnero = importes.get(key_ + ";1");
						if (impEnero == null) impEnero = 0.0;
						
						Double impFebrero = importes.get(key_ + ";2");
						if (impFebrero == null) impFebrero = 0.0;
						
						Double impMarzo = importes.get(key_ + ";3");
						if (impMarzo == null) impMarzo = 0.0;
						
						Double impAbril = importes.get(key_ + ";4");
						if (impAbril == null) impAbril = 0.0;
						
						Double impMayo = importes.get(key_ + ";5");
						if (impMayo == null) impMayo = 0.0;
						
						Double impJunio = importes.get(key_ + ";6");
						if (impJunio == null) impJunio = 0.0;
						
						Double impJulio = importes.get(key_ + ";7");
						if (impJulio == null) impJulio = 0.0;
						
						Double impAgosto = importes.get(key_ + ";8");
						if (impAgosto == null) impAgosto = 0.0;
						
						Double impSetiembre = importes.get(key_ + ";9");
						if (impSetiembre == null) impSetiembre = 0.0;
						
						Double impOctubre = importes.get(key_ + ";10");
						if (impOctubre == null) impOctubre = 0.0;
						
						Double impNoviembre = importes.get(key_ + ";11");
						if (impNoviembre == null) impNoviembre = 0.0;
						
						Double impDiciembre = importes.get(key_ + ";12");
						if (impDiciembre == null) impDiciembre = 0.0;
						
						HistoricoMovimientoArticulo hist = new HistoricoMovimientoArticulo();
						hist.setDescripcion(codigo);
						hist.setRuc(ruc);
						hist.setVendedor(vendedor_);
						hist.setRubro(rubro);
						hist.setZona(zona);
						hist.setLitraje(cantidad);
						hist.setCoeficiente(0.0);
						hist.setEnero_(cantEnero);
						hist.setFebrero_(cantFebrero);
						hist.setMarzo_(cantMarzo);
						hist.setAbril_(cantAbril);
						hist.setMayo_(cantMayo);
						hist.setJunio_(cantJunio);
						hist.setJulio_(cantJulio);
						hist.setAgosto_(cantAgosto);
						hist.setSetiembre_(cantSetiembre);
						hist.setOctubre_(cantOctubre);
						hist.setNoviembre_(cantNoviembre);
						hist.setDiciembre_(cantDiciembre);
						hist.set_enero(impEnero);
						hist.set_febrero(impFebrero);
						hist.set_marzo(impMarzo);
						hist.set_abril(impAbril);
						hist.set_mayo(impMayo);
						hist.set_junio(impJunio);
						hist.set_julio(impJulio);
						hist.set_agosto(impAgosto);
						hist.set_setiembre(impSetiembre);
						hist.set_octubre(impOctubre);
						hist.set_noviembre(impNoviembre);
						hist.set_diciembre(impDiciembre);
						hist.setCostoGs(0.0);
						hist.setCostoFobGs(0.0);
						hist.setTotal_(hist.get_enero() + hist.get_febrero() + hist.get_marzo() + hist.get_abril() + hist.get_mayo() + hist.get_junio()
								+ hist.get_julio() + hist.get_agosto() + hist.get_setiembre() + hist.get_octubre() + hist.get_noviembre() + hist.get_diciembre());
						list.add(hist);			
						keys.put(key_, key_);
					}					
				}				
				String format = (String) formato[0];
				String csv = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_CSV[0];
				String xls = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS[0];
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_PROVEEDOR_CLIENTE;
				if (format.equals(csv) || format.equals(xls)) {
					source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_PROVEEDOR_CLIENTE_;
				}
				
				String flia = familia != null ? familia.getDescripcion() : "TODOS..";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", codReporte + " - VENTAS POR PROVEEDOR POR CLIENTE");
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("Cliente", cli != null ? cli.getRazonSocial() : "TODOS..");
				params.put("Familia", flia);
				params.put("Proveedor", proveedor != null ? proveedor.getRazonSocial() : "TODOS..");
				JRDataSource dataSource = new VentasProveedorCliente(list, rango);
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-00052..
		 */
		private void ventasDetalle(boolean mobile, String codReporte) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}			
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Articulo art = filtro.getArticulo();
				ArticuloPresentacion pres = filtro.getPresentacion();
				Cliente cliente = filtro.getCliente();
				Object[] formato = filtro.getFormato();
				Funcionario vendedor = filtro.getVendedor();
				SucursalApp suc = filtro.getSelectedSucursal();
				ArticuloFamilia familia = filtro.getFamilia_();
				EmpresaRubro rubro = filtro.getRubro_();
				ArticuloMarca marca = filtro.getMarca_();
				Proveedor proveedor = filtro.getProveedor();
				boolean soloDescuentos = filtro.isFraccionado();
				
				double totalSubtotal = 0;
				double totalDescuento = 0;
				double totalImporte = 0;
				
				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				long idCliente = cliente == null ? 0 : cliente.getId().longValue();
				long idRubro = rubro == null ? 0 : rubro.getId().longValue();
				long idSucursal = suc == null ? 0 : suc.getId().longValue();
				long idVendedor = vendedor == null ? 0 : vendedor.getId().longValue();
				long idFamilia = familia == null ? 0 : familia.getId().longValue();
				long idMarca = marca == null ? 0 : marca.getId().longValue();
				long idProveedor = proveedor == null ? 0 : proveedor.getId().longValue();
				long idPresentacion = pres == null ? 0 : pres.getId().longValue();

				List<Venta> ventas = rr.getVentas(desde, hasta, idCliente, idRubro, idSucursal, idVendedor);
				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						for (VentaDetalle item : venta.getDetalles()) {
							Object[] vta = new Object[] {
									Utiles.getDateToString(venta.getFecha(), "dd-MM-yyyy"),
									venta.getNumero(),
									"FAC. " + venta.getCondicionPago().getDescripcion().substring(0, 3).toUpperCase(),
									venta.getCliente().getRazonSocial().toUpperCase(),
									venta.getCliente().getRubro().toUpperCase(),
									venta.getVendedor().getRazonSocial().toUpperCase(),
									item.getArticulo().getCodigoInterno(),
									item.getArticulo().getMarca().getDescripcion().toUpperCase(),
									item.getArticulo().getFamilia().getDescripcion().toUpperCase(),
									venta.isAnulado() ? 0.0 : item.getPrecioGs(),
									venta.isAnulado() ? (long) 0 : item.getCantidad(),
									venta.isAnulado() ? 0.0 : item.getDescuentoUnitarioGs() * -1,
									venta.isAnulado() ? 0.0 : item.getImporteGs(),
									venta.isAnulado() ? 0.0 : (item.getPrecioGs() * item.getCantidad()),
									item.getArticulo().getDescripcion(),
									venta.isAnulado() ? 0.0 : item.getPorcentajeDescuento(),
									venta.isAnulado() ? 0.0 : (item.getImporteGsSinIva() - item.getCostoTotalGsSinIva()),
									venta.getAtendido().getNombreEmpresa() };
							if (art == null || art.getId().longValue() == item.getArticulo().getId().longValue()) {
								if (familia == null || idFamilia == item.getArticulo().getFamilia().getId().longValue()) {
									if (marca == null || idMarca == item.getArticulo().getMarca().getId().longValue()) {
										if (proveedor == null || (item.getArticulo().getProveedor() != null && idProveedor == item.getArticulo().getProveedor().getId().longValue())) {
											if (pres == null || (item.getArticulo().getPresentacion() != null && idPresentacion == item.getArticulo().getPresentacion().getId().longValue())) {
												if (soloDescuentos == false || (soloDescuentos && item.getDescuentoUnitarioGs() > 0)) {
													data.add(vta);
												}
											}											
										}									
									}
								}
							}
						}
					}					
				}
				for (Object[] obj : data) {
					double subtotal = (double) obj[13];
					double descuento = (double) obj[11];
					double importe = (double) obj[12];
					totalSubtotal += subtotal;
					totalDescuento += descuento;
					totalImporte += importe;
				}
				double utilidad = totalImporte - totalDescuento;
				double promedioSobreCosto = Utiles.obtenerPorcentajeDelValor(utilidad, totalDescuento);
				double promedioSobreVenta = Utiles.obtenerPorcentajeDelValor(utilidad, totalImporte);
				promedioSobreCosto = Utiles.getRedondeo(promedioSobreCosto);
				promedioSobreVenta = Utiles.getRedondeo(promedioSobreVenta);				

				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_DETALLADO;
				if (!formato[0].equals("PDF")) {
					source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_DETALLADO_SIN_CAB;
				}
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new VentasDetalle(data);
				params.put("Titulo", codReporte + " - Detalle de Ventas y Descuentos");
				params.put("Usuario", getUs().getNombre());
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("TOT_SUBTOTAL", Utiles.getNumberFormat(totalSubtotal));
				params.put("TOT_DESCUENTO", Utiles.getNumberFormat(totalDescuento));
				params.put("TOT_IMPORTE", Utiles.getNumberFormat(totalImporte));
				imprimirJasper(source, params, dataSource, formato);							

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte VEN-
		 */
		private void costoDeVentasDetallado(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				String tipoCosto = filtro.getTipoCosto();
				Articulo art = filtro.getArticulo();
				long idArticulo = art != null ? art.getId().longValue() : 0;
				
				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<Object[]> ncs = rr.getNotasCreditoVentaDetalles(desde, hasta, 0, idArticulo);
				for (Object[] notacred : ncs) {
					String numero = (String) notacred[1];
					Date fecha = (Date) notacred[2];
					String descrMotivo = (String) notacred[3];
					String siglaMotivo = (String) notacred[4];
					if (siglaMotivo.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_DEVOLUCION)) {
						String motivo = descrMotivo.substring(0, 3).toUpperCase() + ".";
						if (tipoCosto.equals(ReportesFiltros.COSTO_ULTIMO)) {
							double cost = (double) notacred[6];
							String cant_ = notacred[7] + "";
							int cant = Integer.parseInt(cant_);
							double costo = Utiles.getRedondeo(cost * -1);
							String codigo = (String) notacred[10];
							Object[] nc = new Object[] {
									Utiles.getDateToString(fecha, "dd-MM-yy HH:mm:ss"),
									numero,
									"NCR " + motivo,
									codigo,
									costo,
									(costo * cant)};					
							data.add(nc);
						}
						if (tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO)) {
							double cost = (double) notacred[9];
							String cant_ = notacred[7] + "";
							int cant = Integer.parseInt(cant_);
							double costo = Utiles.getRedondeo(cost * -1);
							String codigo = (String) notacred[10];
							Object[] nc = new Object[] {
									Utiles.getDateToString(fecha, "dd-MM-yy HH:mm:ss"),
									numero,
									"NCR " + motivo,
									codigo,
									costo,
									(costo * cant)};					
							data.add(nc);						
						}
					}
				}

				List<Object[]> ventas = rr.getVentasDetalles(desde, hasta, 0, idArticulo);
				for (Object[] venta : ventas) {
					String numero = (String) venta[1];
					Date fecha = (Date) venta[2];
					String sigla = (String) venta[4];
					
					if (tipoCosto.equals(ReportesFiltros.COSTO_ULTIMO)) {
						double cost = (double) venta[6];
						String cant_ = venta[7] + "";
						int cant = Integer.parseInt(cant_);
						double costo = Utiles.getRedondeo(cost);
						String codigo = (String) venta[10];
						Object[] vta = new Object[] { 
								Utiles.getDateToString(fecha, "dd-MM-yy HH:mm:ss"), 
								numero, 
								"FAC. " + TipoMovimiento.getAbreviatura(sigla),
								codigo,
								costo,
								(costo * cant)};
						data.add(vta);					
					}
					if (tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO)) {
						double cost = (double) venta[9];
						String cant_ = venta[7] + "";
						int cant = Integer.parseInt(cant_);
						double costo = Utiles.getRedondeo(cost);
						String codigo = (String) venta[10];
						Object[] vta = new Object[] { 
								Utiles.getDateToString(fecha, "dd-MM-yy HH:mm:ss"), 
								numero, 
								"FAC. " + TipoMovimiento.getAbreviatura(sigla),
								codigo,
								costo,
								(costo * cant)};
						data.add(vta);
					
					}
				}
				String sucursal = getAcceso().getSucursalOperativa().getText();

				ReporteCostoVentasDetallado rep = new ReporteCostoVentasDetallado(desde, hasta, sucursal, tipoCosto);
				rep.setDatosReporte(data);
				rep.setApaisada();

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Limpia los atributos e inicializa los valores por defecto..
	 */
	public void inicializarFiltros() throws Exception {
		this.filtro.setFechaDesde(null);
		this.filtro.setFechaHasta(null);
		this.filtro.setFechaDesde2(null);
		this.filtro.setFechaHasta2(null);
		this.filtro.setLibradoPor("");
		this.filtro.setNroComprobanteCheque("");
		this.filtro.setDescontadoCheque(true);
		this.filtro.setVendedor(null);
		this.filtro.setIncluirNCR(true);
		this.filtro.setIncluirVCR(true);
		this.filtro.setIncluirVCT(true);
		this.filtro.setIncluirCHQ_RECH(true);
		this.filtro.setIncluirPRE(true);
		this.filtro.setRazonSocialVendedor("");
		this.filtro.setCliente(null);
		this.filtro.setRazonSocialCliente("");
		this.filtro.setProveedor(null);
		this.filtro.setProveedorExterior(null);
		this.filtro.setRazonSocialProveedor("");
		this.filtro.setRazonSocialProveedorExterior("");
		this.filtro.setTodosLosClientes(false);
		this.filtro.setTodosLosVendedores(false);
		this.filtro.setTodos(true);
		this.filtro.setArticulo(null);
		this.filtro.setCodigoArticulo("");
		this.filtro.setTipoSaldoMigracion(ReportesFiltros.TODOS);
		this.filtro.setNroPlanillaCaja("");
		this.filtro.setMorosidad("");
		this.filtro.setTipoRetencion("");
		this.filtro.setSucursalDestino(null);
		this.filtro.setSucursalOrigen(null);
		this.filtro.setSelectedSucursal(null);
		this.filtro.setFormaPago_("");
		this.filtro.setCajaPlanilla(null);
		this.filtro.setChequeTercero(null);
		this.filtro.setNumeroChequeTercero("");
		this.filtro.setBancoTercero(null);
		this.filtro.setNombrebancoTercero("");
		this.filtro.setUsuariosActivos(true);
		this.filtro.setUsuariosInactivos(true);
		this.filtro.setUsuario(null);
		this.filtro.setNombreUsuario("");
		this.filtro.setDescuentoCheque("");
		this.filtro.setMarca(null);
		this.filtro.setRubro(null);
		this.filtro.setFamilia(null);
		this.filtro.setExcluirPyAutopartes(false);
		this.filtro.setExcluirIcaturbo(false);
		this.filtro.setCostoPromedio(false);
		this.filtro.setEstadoCuentaCliente(null);
		this.filtro.setRecibo(null);
		this.filtro.setNumeroRecibo("");
		this.filtro.setCobrador(null);
		this.filtro.setRazonSocialCobrador("");
		this.filtro.setSelectedDepositos(new ArrayList<Deposito>());
		this.filtro.setIvaIncluido(true);
		this.filtro.setListaPrecio(null);
		this.filtro.setCantidadDesde(0);
		this.filtro.setCantidadHasta(0);
		this.filtro.setFraccionado(false);
		this.filtro.setSelectedClientes(new ArrayList<Cliente>());
		this.filtro.setSelectedMes(null);
		this.filtro.setArticuloGasto(null);
		this.filtro.setDescripcionArticuloGasto("");
		this.filtro.setReembolso("");
		this.filtro.setSelectedFamilias(new HashSet<ArticuloFamilia>());
		this.filtro.setSelectedDepositos(new ArrayList<Deposito>());
		this.filtro.setSelectedProveedores(new ArrayList<Proveedor>());
		this.filtro.setMarca_(null);
		this.filtro.setFamilia_(null);
		this.filtro.setStockMayorIgual(1);
		this.filtro.setRubro_(null);
		this.filtro.setVendedor(null);
		this.filtro.setSelectedMes(Utiles.getMeses().get(Utiles.getNumeroMesCorriente() - 1));
		this.filtro.setCartera(null);
	}

	/**
	 * Reportes de Tesoreria..
	 */
	class ReportesTesoreria {

		static final String PAGOS_FECHA = "TES-00001";
		static final String RETENCIONES_FECHA = "TES-00002";
		static final String SALDOS_CLIENTES = "TES-00003";
		static final String SALDOS_CLIENTES_DET = "TES-00004";
		static final String CLIENTES_SIN_MOVIMIENTO = "TES-00005";
		static final String VERIF_PAGOS_CHEQUES = "TES-00006";
		static final String PAGOS_PERIODO_DET = "TES-00007";
		static final String PAGOS_POR_RECIBO = "TES-00008";
		static final String SALDOS_PROVEEDORES = "TES-00009";
		static final String SALDOS_PROVEEDORES_DET = "TES-00010";
		static final String PAGOS_POR_PROVEEDOR = "TES-00011";
		static final String CHEQUES_A_VENCER = "TES-00012";
		static final String CHEQUES_POR_CLIENTE = "TES-00013";
		static final String CHEQUES_POR_NRO_COMPBT = "TES-00014";
		static final String CHEQUES_DESCONTADOS = "TES-00015";
		static final String SALDOS_CON_PROVEEDORES = "TES-000XX";
		static final String SALDOS_CON_PROVEEDORES_DETALLADO = "TES-00XXX";
		static final String COBRANZAS = "TES-00016";
		static final String PLANILLAS_DE_CAJA = "TES-00017";
		static final String MOROSIDAD_CLIENTES_DETALLADO = "TES-00018";
		static final String COBRANZAS_FORMA_PAGO = "TES-00020";
		static final String CHEQUES_PENDIENTES_DESCUENTO = "TES-00021";
		static final String CHEQUES_DEPOSITADOS = "TES-00022";
		static final String CLIENTES_CREDITO = "TES-00023";
		static final String BOLETAS_DEPOSITO = "TES-00024";
		static final String CHEQUES_PROPIOS = "TES-00025";
		static final String CLIENTES_COBRADOR = "TES-00026";
		static final String RECAUDACION_CASA_CENTRAL = "TES-00027";
		static final String CHEQUES_PROPIOS_A_VENCER = "TES-00028";
		static final String LISTADO_ANTICIPOS_UTILIDAD = "TES-00029";
		static final String CLIENTES_POR_RUBRO = "TES-00030";
		static final String CORRELATIVIDAD_RECIBOS = "TES-00031";
		static final String RECIBOS_ANULADOS = "TES-00032";
		static final String PRESTAMOS_CASA_CENTRAL = "TES-00033";
		static final String REEMBOLSOS_PRESTAMOS_CC = "TES-00034";
		static final String CLIENTES_CON_MOVIMIENTOS = "TES-00035";
		static final String HISTORIAL_BLOQUEO_CLIENTES = "TES-00036";
		static final String CHEQUES_PROPIOS_A_VENCER_ = "TES-00037";
		static final String INGRESOS_EGRESOS_DIFERENCIA_CAMBIO = "TES-00038";
		static final String SALDOS_CLIENTES_SEGUN_FECHA_VTO = "TES-00039";
		static final String RECAUDACIONES_NO_DEPOSITADAS = "TES-00040";
		static final String RESUMEN_PLANILLAS_CAJA = "TES-00041";
		static final String SALDOS_CLIENTES_CONSOLIDADO = "TES-00042";
		static final String HISTORIAL_SALDOS_CLIENTES = "TES-00043";
		static final String SALDOS_CLIENTES_MES = "TES-00044";
		static final String HISTORIAL_ATRASOS = "TES-00045";
		static final String HISTORIAL_SALDOS_PROVEEDOR_DETALLADO = "TES-00046";
		static final String HISTORIAL_SALDOS_PROVEEDOR_RESUMIDO = "TES-00047";
		static final String HISTORIAL_SALDOS_PROVEEDOR_EXT_DETALLADO = "TES-00048";
		static final String HISTORIAL_SALDOS_PROVEEDOR_EXT_RESUMIDO = "TES-00049";
		static final String HISTORIAL_SALDOS_CLIENTES_DETALLADO = "TES-00050";
		static final String HISTORIAL_SALDOS_CLIENTES_RESUMIDO = "TES-00051";
		static final String COBRANZAS_COBRADOR = "TES-00052";
		static final String COBRANZAS_COBRADOR_DET = "TES-00053";
		static final String SALDOS_CLIENTES_RESUMIDO = "TES-00054";
		static final String SALDOS_CLIENTES_POR_PROVEEDOR = "TES-00055";
		static final String ANTICIPOS_CLIENTES = "TES-00056";
		static final String COBRANZAS_DETALLADO = "TES-00057";
		static final String REEMBOLSOS_DETALLADO = "TES-00058";
		static final String CHEQUES_CLIENTES_EMISION = "TES-00059";
		static final String SALDOS_POR_FAMILIA = "TES-00060";
		static final String LISTADO_PAGOS = "TES-00061";
		static final String CHEQUES_GARANTIAS_RECIBIDAS = "TES-00062";

		/**
		 * procesamiento del reporte..
		 */
		public ReportesTesoreria(String codigoReporte, ReportesViewModel vm, boolean mobile)
				throws Exception {
			switch (codigoReporte) {
			
			case PAGOS_FECHA:
				this.pagosPorFecha(mobile);
				break;

			case RETENCIONES_FECHA:
				this.retencionesPorFecha(mobile);
				break;

			case SALDOS_CLIENTES:
				this.saldosDeClientes(mobile);
				break;

			case SALDOS_CLIENTES_DET:
				this.saldosCtaCteDetallado(true, mobile, SALDOS_CLIENTES_DET);
				break;

			case CLIENTES_SIN_MOVIMIENTO:
				this.clientesSinMovimiento();
				break;

			case VERIF_PAGOS_CHEQUES:
				this.verificacionDePagos();
				break;

			case PAGOS_PERIODO_DET:
				this.detallesDePagoDelPeriodo();
				break;

			case PAGOS_POR_RECIBO:
				this.pagosPorNumeroRecibo(mobile);
				break;
				
			case SALDOS_PROVEEDORES:
				this.saldosDeProveedores(mobile);
				break;

			case SALDOS_PROVEEDORES_DET:
				this.saldosCtaCteDetallado(false, mobile, SALDOS_PROVEEDORES_DET);
				break;

			case PAGOS_POR_PROVEEDOR:
				this.pagosPorProveedor();
				break;

			case CHEQUES_A_VENCER:
				this.cheques_a_vencer(vm, mobile);
				break;

			case CHEQUES_POR_CLIENTE:
				this.chequesPorCliente(mobile, CHEQUES_POR_CLIENTE);
				break;

			case CHEQUES_POR_NRO_COMPBT:
				this.chequesPorNroComprobante();
				break;

			case CHEQUES_DESCONTADOS:
				this.chequesDescontados(mobile);
				break;

			case SALDOS_CON_PROVEEDORES:
				this.saldosConProveedores(mobile);
				break;

			case SALDOS_CON_PROVEEDORES_DETALLADO:
				this.saldosConProveedoresDetallado(vm, mobile);
				break;

			case COBRANZAS:
				this.cobranzas(mobile, COBRANZAS);
				break;

			case PLANILLAS_DE_CAJA:
				this.resumenPlanillasCaja(mobile);
				break;

			case MOROSIDAD_CLIENTES_DETALLADO:
				this.morosidadDeClientesDetallado(mobile);
				break;

			case COBRANZAS_FORMA_PAGO:
				this.cobranzasFormaPago(mobile);
				break;
				
			case CHEQUES_PENDIENTES_DESCUENTO:
				this.chequesPendientesDescuento(mobile);
				break;
				
			case CHEQUES_DEPOSITADOS:
				this.chequesDepositados(mobile);
				break;
				
			case CLIENTES_CREDITO:
				this.clientesCredito(mobile);
				break;
				
			case BOLETAS_DEPOSITO:
				this.boletasDeposito(mobile);
				break;
			
			case CHEQUES_PROPIOS:
				this.chequesPropios(mobile);
				break;
				
			case CLIENTES_COBRADOR:
				this.clientesPorCobrador(mobile);
				break;
			
			case RECAUDACION_CASA_CENTRAL:
				this.recaudacionCasaCentral(mobile);
				break;
				
			case CHEQUES_PROPIOS_A_VENCER:
				this.chequesPropiosAvencer(mobile);
				break;
				
			case LISTADO_ANTICIPOS_UTILIDAD:
				this.anticiposUtilidad(mobile);
				break;
				
			case CLIENTES_POR_RUBRO:
				this.clientesPorRubro(mobile, CLIENTES_POR_RUBRO);
				break;
				
			case CORRELATIVIDAD_RECIBOS:
				this.correlatividadRecibos(mobile);
				break;
				
			case RECIBOS_ANULADOS:
				this.recibosAnulados(mobile);
				break;
				
			case PRESTAMOS_CASA_CENTRAL:
				this.prestamosCasaCentral(mobile);
				break;
				
			case REEMBOLSOS_PRESTAMOS_CC:
				this.reembolsosPrestamosCasaCentral(mobile);
				break;
				
			case CLIENTES_CON_MOVIMIENTOS:
				this.clientesConMovimientos(mobile);
				break;
				
			case HISTORIAL_BLOQUEO_CLIENTES:
				this.historialBloqueos(mobile);
				break;
				
			case CHEQUES_PROPIOS_A_VENCER_:
				this.chequesPropiosAvencer_(mobile);
				break;
				
			case INGRESOS_EGRESOS_DIFERENCIA_CAMBIO :
				this.diferenciasTipoCambio(mobile);
				break;
				
			case SALDOS_CLIENTES_SEGUN_FECHA_VTO :
				this.saldosDeClientesSegunVencimiento(mobile);
				break;
				
			case RECAUDACIONES_NO_DEPOSITADAS :
				this.recaudacionesNoDepositadas(mobile);
				break;
				
			case RESUMEN_PLANILLAS_CAJA:
				this.resumenPlanillasCaja();
				break;
				
			case SALDOS_CLIENTES_CONSOLIDADO:
				this.saldosCtaCteConsolidado(true, mobile);
				break;
				
			case HISTORIAL_SALDOS_CLIENTES:
				this.historialMovimientosCliente();
				break;
				
			case SALDOS_CLIENTES_MES:
				this.saldosClientesPorMes(mobile);
				break;
				
			case HISTORIAL_ATRASOS:
				this.historialAtrasosClientes(mobile);
				break;
				
			case HISTORIAL_SALDOS_PROVEEDOR_DETALLADO:
				this.historialMovimientosProveedores(mobile, false);
				break;
				
			case HISTORIAL_SALDOS_PROVEEDOR_RESUMIDO:
				this.historialMovimientosProveedores(mobile, true);
				break;
				
			case HISTORIAL_SALDOS_PROVEEDOR_EXT_DETALLADO:
				this.historialMovimientosProveedoresExterior(mobile, false);
				break;
				
			case HISTORIAL_SALDOS_PROVEEDOR_EXT_RESUMIDO:
				this.historialMovimientosProveedoresExterior(mobile, true);
				break;
				
			case HISTORIAL_SALDOS_CLIENTES_DETALLADO:
				this.historialMovimientosClientes(mobile, false, HISTORIAL_SALDOS_CLIENTES_DETALLADO);
				break;
				
			case HISTORIAL_SALDOS_CLIENTES_RESUMIDO:
				this.historialMovimientosClientes(mobile, true, HISTORIAL_SALDOS_CLIENTES_RESUMIDO);
				break;
				
			case COBRANZAS_COBRADOR:
				this.cobranzasVentasCobrador(mobile);
				break;
				
			case COBRANZAS_COBRADOR_DET:
				this.cobranzasVentasCobradorDetallado(mobile);
				break;
				
			case SALDOS_CLIENTES_RESUMIDO:
				this.saldosClientesResumido(mobile, SALDOS_CLIENTES_RESUMIDO);
				break;
				
			case SALDOS_CLIENTES_POR_PROVEEDOR:
				this.saldosClientesProveedor(mobile, SALDOS_CLIENTES_POR_PROVEEDOR);
				break;
				
			case ANTICIPOS_CLIENTES:
				this.anticipos(mobile, ANTICIPOS_CLIENTES);
				break;
				
			case COBRANZAS_DETALLADO:
				this.cobranzasDetallado(mobile, COBRANZAS_DETALLADO);
				break;
				
			case REEMBOLSOS_DETALLADO:
				this.reembolsosDetallado(mobile, REEMBOLSOS_DETALLADO);
				break;
				
			case CHEQUES_CLIENTES_EMISION:
				this.chequesPorClienteSegunEmision(mobile, CHEQUES_CLIENTES_EMISION);
				break;
				
			case SALDOS_POR_FAMILIA:
				this.saldosPorFamilia(mobile, SALDOS_POR_FAMILIA);
				break;
				
			case LISTADO_PAGOS:
				this.listadoPagos(LISTADO_PAGOS);
				break;
				
			case CHEQUES_GARANTIAS_RECIBIDAS:
				this.chequesGarantiasRecibidas(CHEQUES_GARANTIAS_RECIBIDAS);
				break;
			}
		}

		/**
		 * reporte TES-00001
		 */
		private void pagosPorFecha(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				long idCliente = 0;
				boolean todos = filtro.isTodosLosClientes();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				if (filtro.getCliente() != null)
					idCliente = filtro.getCliente().getId();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Recibo> cobranzas = rr.getCobranzasDetallado(desde, hasta,
						idCliente, todos);

				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_COBROS_DET;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CobrosDetalladoDataSource(
						cobranzas);
				params.put("Usuario", getUs().getNombre());
				imprimirJasper(source, params, dataSource, new Object[] {
						"PDF", "pdf" });

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte TES-00034
		 */
		private void retencionesPorFecha(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			String tipoRetencion = filtro.getTipoRetencion();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<Recibo> recibos = new ArrayList<Recibo>();
			List<Venta> ventas = new ArrayList<Venta>();
			
			if (desde == null)
				desde = new Date();

			if (hasta == null)
				hasta = new Date();

			if (tipoRetencion.equals(ReportesFiltros.RETENCION_RECIBIDAS)) {
				recibos = rr.getCobranzas(desde, hasta, 0, 0, true, true);
				ventas = rr.getVentasContado(desde, hasta, 0, 0);
			} else if (tipoRetencion.equals(ReportesFiltros.RETENCION_EMITIDAS)) {
				recibos = rr.getPagos(desde, hasta);
			}

			for (Recibo cobro : recibos) {
				for (ReciboFormaPago formaPago : cobro.getFormasPago()) {
					if (formaPago.isRetencion()) {
						data.add(new Object[] {
								m.dateToString(cobro.getFechaEmision(),
										"dd-MM-yy"), cobro.getNumero(),
								formaPago.getRetencionNumero(),
								formaPago.getMontoGs() });
					}
				}
			}

			for (Venta venta : ventas) {
				if (!venta.isAnulado()) {
					for (ReciboFormaPago formaPago : venta.getFormasPago()) {
						if (formaPago.isRetencion()) {
							data.add(new Object[] {
									m.dateToString(venta.getFecha(), "dd-MM-yy"),
									venta.getNumero(),
									formaPago.getRetencionNumero(),
									formaPago.getMontoGs() });
						}
					}
				}
			}

			ReporteRetenciones rep = new ReporteRetenciones(desde, hasta,
					tipoRetencion, getAcceso().getSucursalOperativa().getText());
			rep.setDatosReporte(data);
			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}

		/**
		 * reporte TES-00003
		 */
		private void saldosDeClientes(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Funcionario vendedor = filtro.getVendedor();
			long idVendedor = vendedor == null ? 0 : vendedor.getId();
			String vendedor_ = vendedor == null ? "TODOS.." : vendedor.getRazonSocial();
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();
			Map<String, BeanCtaCteEmpresa> map = new HashMap<String, BeanCtaCteEmpresa>();
			String caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE;
			List<CtaCteEmpresaMovimiento> saldos = rr.getMovimientosConSaldo(desde, hasta, caracter, idVendedor);
			List<Object[]> data = new ArrayList<Object[]>();

			for (CtaCteEmpresaMovimiento item : saldos) {
				BeanCtaCteEmpresa cta = map.get(item.getIdEmpresa() + "");
				if (cta != null) {
					if (item.isVencido()) {
						cta.setVencido(cta.getVencido() + item.getSaldoFinal());
					} else {
						cta.setAvencer(cta.getAvencer() + item.getSaldoFinal());
					}
					cta.setSaldo(cta.getSaldo() + item.getSaldoFinal());
				} else {
					Empresa emp = rr.getEmpresaById(item.getIdEmpresa());
					cta = new BeanCtaCteEmpresa();
					cta.setRazonSocial(emp.getRazonSocial());
					cta.setRuc(emp.getRuc());
					if (item.isVencido()) {
						cta.setVencido(item.getSaldoFinal());
					} else {
						cta.setAvencer(item.getSaldoFinal());
					}
					cta.setSaldo(item.getSaldoFinal());
				}
				map.put(item.getIdEmpresa() + "", cta);
			}

			for (String key : map.keySet()) {
				BeanCtaCteEmpresa cta = map.get(key);
				Object[] obj = new Object[] { cta.getRazonSocial(),
						cta.getRuc(), cta.getAvencer(), cta.getVencido(),
						cta.getSaldo() };
				data.add(obj);
			}
			
			Collections.sort(data, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					String val1 = (String) o1[0];
					String val2 = (String) o2[0];
					int compare = val1.compareTo(val2);				
					return compare;
				}
			});

			ReporteSaldosClientes rep = new ReporteSaldosClientes(desde, hasta, "Saldos de Clientes (Segun fecha Emision)", vendedor_);
			rep.setDatosReporte(data);
			
			rep.setA4();
			rep.setApaisada();

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}

		/**
		 * reporte TES-00016
		 */
		private void saldosConProveedores(boolean mobile) throws Exception {

			RegisterDomain rr = RegisterDomain.getInstance();
			ControlCtaCteEmpresa controlCtaCte = new ControlCtaCteEmpresa(null);
			List<MyArray> saldoClientes = controlCtaCte
					.getListadoCuentasProveedores();

			List<Object[]> data = new ArrayList<Object[]>();

			for (MyArray marray : saldoClientes) {
				Empresa e = rr.getEmpresaById((long) marray.getPos1());
				Object[] o = { marray.getPos1(), e.getRuc(), marray.getPos2(),
						marray.getPos3(),
						((Tipo) marray.getPos4()).getDescripcion(),
						marray.getPos5() };
				data.add(o);
			}

			ReporteSaldosConProveedores rep = new ReporteSaldosConProveedores();
			rep.setDatosReporte(data);
			
			rep.setApaisada();

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * reporte TES-00004 / TES-00010
		 */
		private void saldosCtaCteDetallado(boolean cliente, boolean mobile, String codReporte) throws Exception {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde_ = Utiles.getFecha("01-01-2015 00:00:00");			
			Date hasta_ = new Date();
			boolean incluirChequesRechazados = filtro.isIncluirCHQ_RECH();
			boolean incluirPrestamos = filtro.isIncluirPRE();
			Funcionario vendedor = filtro.getVendedor();
			Cliente cliente_ = filtro.getCliente();
			EmpresaRubro rubro = filtro.getRubro_();
			Tipo moneda = filtro.getMoneda();
			
			if (moneda.esNuevo()) {
				Clients.showNotification("DEBE SELECCIONAR UNA MONEDA..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
			Object[] formato = filtro.getFormato();
			long idVendedor = vendedor == null ? 0 : vendedor.getId();
			long idEmpresa = cliente_ == null ? 0 : cliente_.getEmpresa().getId();
			long idRubro = rubro == null ? 0 : rubro.getId();
			String caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE;
			if (!cliente) {
				caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR;
			}	
			List<Object[]> movims = new ArrayList<Object[]>();
			List<Object[]> movimientos = new ArrayList<Object[]>();
			List<Object[]> aux = new ArrayList<Object[]>();
			Map<String, Object[]> data = new HashMap<String, Object[]>();			

			movims = rr.getSaldos(desde_, hasta_, caracter, idVendedor, idEmpresa, moneda.getId(), incluirChequesRechazados, incluirPrestamos, idRubro);	
			for (Object[] movim : movims) {
				long id_mov = (long) movim[0];
				long id_tmv = (long) movim[1];
				data.put(id_mov + "-" + id_tmv, movim);
				movimientos.add(movim);					
			}
			for (String key : data.keySet()) {
				aux.add(data.get(key));
			}
		
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new CtaCteSaldosDataSource_(movimientos, aux);
			params.put("Titulo", cliente ? codReporte + " - SALDOS DE CLIENTES DETALLADO" : codReporte + " - SALDOS DE PROVEEDORES DETALLADO");
			params.put("Usuario", getUs().getNombre());
			params.put("Vendedor", vendedor == null ? "TODOS.." : vendedor.getRazonSocial().toUpperCase());
			params.put("Rubro", rubro == null ? "TODOS.." : rubro.getDescripcion().toUpperCase());
			params.put("Moneda", moneda.getDescripcion().toUpperCase());
			imprimirJasper(source, params, dataSource, formato);
		}
		
		/**
		 * @return true si es del rubro segun sigla..
		 */
		private boolean isRubro(long idRubro, long idEmpresa) throws Exception {
			RegisterDomain rr = RegisterDomain.getInstance();
			Object[] emp = rr.getEmpresa(idEmpresa);
			if (emp != null && emp[5] != null) {
				long idrubro = (long) emp[5];
				if (idRubro == idrubro) {
					return true;
				}
			}		
			return false;
		}

		/**
		 * reporte TES-00017
		 */
		private void saldosConProveedoresDetallado(ReportesViewModel vm, boolean mobile)
				throws Exception {
			ControlCtaCteEmpresa controlCtaCte = new ControlCtaCteEmpresa(null);
			List<MyArray> saldosProveedores = controlCtaCte
					.getListadoCuentasProveedores();
			List<MyArray> data = new ArrayList<MyArray>();
			for (MyArray marray : saldosProveedores) {

				List<CtaCteEmpresaMovimientoDTO> movimientos = new ArrayList<CtaCteEmpresaMovimientoDTO>();
				movimientos = controlCtaCte
						.getCtaCteEmpresaMovimientosPendientes(marray, vm
								.getUtilDto()
								.getCtaCteEmpresaCaracterMovCliente(),
								(long) 1);

				MyArray saldoDetallado = new MyArray();
				saldoDetallado.setPos1(marray);
				saldoDetallado.setPos2(movimientos);
				data.add(saldoDetallado);

			}

			ReporteSaldosProveedoresDetallado rep = new ReporteSaldosProveedoresDetallado(
					data);
			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}

		/**
		 * reporte TES-00037
		 */
		private void clientesSinMovimiento() {
			Clients.showNotification(CLIENTES_SIN_MOVIMIENTO);
		}

		/**
		 * reporte TES-00038
		 */
		private void verificacionDePagos() {
			Clients.showNotification(VERIF_PAGOS_CHEQUES);
		}

		/**
		 * reporte TES-00039
		 */
		private void detallesDePagoDelPeriodo() {
			Clients.showNotification(PAGOS_PERIODO_DET);
		}

		/**
		 * reporte TES-00008
		 */
		private void pagosPorNumeroRecibo(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			try {
				Recibo recibo = filtro.getRecibo();
				List<Recibo> list = new ArrayList<Recibo>();
				if (recibo != null) {
					list.add(recibo);
				}

				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_COBROS_DET;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CobrosDetalladoDataSource(list);
				params.put("Usuario", getUs().getNombre());
				imprimirJasper(source, params, dataSource, new Object[] {
						"PDF", "pdf" });

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte TES-00043
		 */
		private void pagosPorProveedor() {
			Clients.showNotification(PAGOS_POR_PROVEEDOR);
		}

		/**
		 * reporte TES-00016
		 */
		private void cobranzas(boolean mobile, String codReporte) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				SucursalApp sucursal = filtro.getSelectedSucursal();
				Cliente cli = filtro.getCliente();
				long idSucursal = sucursal == null ? 0 : sucursal.getId();
				long idCliente = cli != null ? cli.getId() : 0;
				String sucursal_ = sucursal == null ? "TODOS.." : sucursal.getDescripcion();
				boolean incluirAnticipos = false;

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Recibo> cobranzas = rr.getCobranzas(desde, hasta, 0, idCliente, incluirAnticipos, false);
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LISTADO_COBRANZAS;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new ListadoCobranzasDataSource(cobranzas, desde, hasta, sucursal_, false, idSucursal);
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", codReporte + " - LISTADO DE COBRANZAS");
				imprimirJasper(source, params, dataSource, formato);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte TES-00017
		 */
		private void resumenPlanillasCaja(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			try {
				CajaPeriodo planilla = filtro.getCajaPlanilla();

				if (planilla == null) {
					planilla = new CajaPeriodo();
				}

				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_RESUMEN;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CajaPeriodoResumenDataSource(planilla);
				params.put("Usuario", getUs().getNombre());
				params.put(
						"Periodo",
						m.dateToString(planilla.getApertura(), Misc.DD_MM_YYYY)
								+ " AL "
								+ m.dateToString(planilla.getCierre(),
										Misc.DD_MM_YYYY));
				params.put("NroPlanilla", planilla.getNumero());
				params.put("Cajero", planilla.getResponsable().getDescripcion());
				imprimirJasper(source, params, dataSource, new Object[] {
						"PDF", "pdf" });

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * TES-00012
		 */
		public void cheques_a_vencer(ReportesViewModel vm, boolean mobile) throws Exception {

			RegisterDomain rr = RegisterDomain.getInstance();
			Misc m = new Misc();
			List<Object[]> data = new ArrayList<Object[]>();			
			List<BancoChequeTercero> cheques = rr.getChequesPendientesClientes(0);

			for (BancoChequeTercero bct : cheques) {
				Object[] o = { m.dateToString(bct.getFecha(), "dd-MM-yyyy"),
						bct.getBanco().getDescripcion().toUpperCase(), bct.getNumero(),
						bct.getCliente().getRazonSocial().toUpperCase(), bct.getMonto() };
				data.add(o);
			}

			ReporteChequesAvencer rep = new ReporteChequesAvencer(vm);
			rep.setDatosReporte(data);
			rep.setApaisada();
			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}

		/**
		 * TES-00013 Genera el reporte correspondiente a todos los cheques de
		 * terceros ya sea que se hayan efectivizados o depositados o no de
		 * acuerdo a los parametros optenidos desde el filtro que podrian ser:
		 * FechaDesde, FechaHasta, LibradoPor
		 */
		private void chequesPorCliente(boolean mobile, String codReporte) throws Exception {
			try {
				Date emisionDesde = filtro.getFechaDesde2();
				Date emisionHasta = filtro.getFechaHasta2();
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Tipo banco = filtro.getBancoTercero();
				BancoChequeTercero selectedCheque = filtro.getChequeTercero();
				Cliente cliente = filtro.getCliente();
				long idCliente = cliente == null ? 0 : cliente.getId();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<BancoChequeTercero> cheques = rr.getChequesTercero(desde, hasta, banco, idCliente, emisionDesde, emisionHasta);

				if (selectedCheque != null) {
					int length = selectedCheque.getLibrado() == null ? 0 : selectedCheque.getLibrado().length();
					int maxlength = length > 40 ? 40 : length;
					String librador = selectedCheque.getLibrado() == null ? "---" : selectedCheque.getLibrado().substring(0, maxlength);
					Object[] obj = new Object[] {
							m.dateToString(selectedCheque.getEmision(), Utiles.DD_MM_YY),
							m.dateToString(selectedCheque.getFecha(), Utiles.DD_MM_YY),
							selectedCheque.getNumero(),
							selectedCheque.getBanco().getDescripcion().toUpperCase(), librador.toUpperCase(),
							selectedCheque.isDepositado() ? "SI" : "NO",
							selectedCheque.isDescontado() ? "SI" : "NO",
							Utiles.getRedondeo(selectedCheque.getMonto()) };
					data.add(obj);
				} else {
					for (BancoChequeTercero cheque : cheques) {
						int length = cheque.getLibrado().length();
						int maxlength = length > 40 ? 40 : length;
						String librador = cheque.getLibrado() == null ? "---" : cheque.getLibrado().substring(0, maxlength);
						Object[] obj = new Object[] {
								m.dateToString(cheque.getEmision(), Utiles.DD_MM_YY),
								m.dateToString(cheque.getFecha(), Utiles.DD_MM_YY),
								cheque.getNumero(),
								cheque.getBanco().getDescripcion().toUpperCase(), librador.toUpperCase(),
								cheque.isDepositado() ? "SI" : "NO",
								cheque.isDescontado() ? "SI" : "NO",
								Utiles.getRedondeo(cheque.getMonto()) };
						data.add(obj);
					}
				}

				String sucursal = getAcceso().getSucursalOperativa().getText();
				String nroCheque = selectedCheque == null ? "TODOS.." : selectedCheque.getNumero();
				String banco_ = banco == null || selectedCheque != null ? "TODOS.." : banco.getDescripcion();
				String cliente_ = cliente == null ? "TODOS.." : cliente.getRazonSocial();
				ReporteChequesDeTerceros rep = new ReporteChequesDeTerceros(desde, hasta, sucursal, nroCheque, banco_, cliente_);
				rep.setDatosReporte(data);				
				rep.setApaisada();
				rep.setTitulo(codReporte + " - Listado de Cheques de Clientes");

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * TES-00014
		 */
		private void chequesPorNroComprobante()
				throws Exception {
			Clients.showNotification("LISTADO CHEQUES..");
		}

		/**
		 * TES-00015
		 */
		private void chequesDescontados(boolean mobile) throws Exception {
			
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			
			Date desde_ = filtro.getFechaDesde2();
			Date hasta_ = filtro.getFechaHasta2();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();

			List<BancoChequeTercero> cheques = rr.getChequesDescontados(desde_, hasta_, desde, hasta);

			for (BancoChequeTercero cheque : cheques) {
				Object[] desc = rr.getBancoDescuento(cheque.getId());
				data.add(new Object[] {
						Utiles.getDateToString(cheque.getFechaDescuento(), Utiles.DD_MM_YYYY),
						Utiles.getDateToString(cheque.getFecha(), Utiles.DD_MM_YYYY),
						desc[2],
						cheque.getNumero(),
						cheque.getBanco().getDescripcion().toUpperCase(),
						desc[3],
						cheque.getMonto()});			
			}	
			
			// ordena la lista segun fecha..
			Collections.sort(data, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1,
						Object[] o2) {
					String f1 = (String) o1[0];
					String f2 = (String) o2[0];
					Date fecha1 = null;
					Date fecha2 = null;
					try {
						fecha1 = Utiles.getFecha(f1 + " 00:00:00");
						fecha2 = Utiles.getFecha(f2 + " 00:00:00");
					} catch (Exception e) {
						e.printStackTrace();
					}
					return fecha1.compareTo(fecha2);
				}
			});

			ReporteChequesDescontados rep = new ReporteChequesDescontados(desde, hasta, desde_, hasta_);
			rep.setDatosReporte(data);
			rep.setApaisada();
			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}

		/**
		 * TES-00018
		 */
		private void morosidadDeClientesDetallado(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			try {
				int desde = filtro.getCantidadDesde();
				int hasta = filtro.getCantidadHasta();
				Object[] formato = filtro.getFormato();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<CtaCteEmpresaMovimiento> movims = rr.getMovimientosConSaldo(
						Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE, 
						Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
				List<CtaCteEmpresaMovimiento> values = new ArrayList<CtaCteEmpresaMovimiento>();

				for (CtaCteEmpresaMovimiento movim : movims) {
					if (movim.isDiasVencidosEntre_(desde, hasta)) {
						values.add(movim);
					} 
				}
				
				String morosidad = "De " + desde + " a "+ hasta + " días..";
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_MOROSIDAD_CLI_DET;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new MorosidadClientesDataSource(
						values, morosidad, getAcceso().getSucursalOperativa()
								.getText(), (desde < 0));
				params.put("Usuario", getUs().getNombre());
				imprimirJasper(source, params, dataSource, formato);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * reporte TES-00009
		 */
		private void saldosDeProveedores(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();

			RegisterDomain rr = RegisterDomain.getInstance();
			Map<String, BeanCtaCteEmpresa> map = new HashMap<String, BeanCtaCteEmpresa>();
			String caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR;
			List<CtaCteEmpresaMovimiento> saldos = rr.getMovimientosConSaldo(
					desde, hasta, caracter, 0);
			List<Object[]> data = new ArrayList<Object[]>();

			for (CtaCteEmpresaMovimiento item : saldos) {
				BeanCtaCteEmpresa cta = map.get(item.getIdEmpresa() + "");
				if (cta != null) {
					if (item.isVencido()) {
						cta.setVencido(cta.getVencido() + item.getSaldoFinal());
					} else {
						cta.setAvencer(cta.getAvencer() + item.getSaldoFinal());
					}
					cta.setSaldo(cta.getSaldo() + item.getSaldoFinal());
				} else {
					Empresa emp = rr.getEmpresaById(item.getIdEmpresa());
					cta = new BeanCtaCteEmpresa();
					cta.setRazonSocial(emp.getRazonSocial());
					cta.setRuc(emp.getRuc());
					if (item.isVencido()) {
						cta.setVencido(item.getSaldoFinal());
					} else {
						cta.setAvencer(item.getSaldoFinal());
					}
					cta.setSaldo(item.getSaldoFinal());
				}
				map.put(item.getIdEmpresa() + "", cta);
			}

			for (String key : map.keySet()) {
				BeanCtaCteEmpresa cta = map.get(key);
				Object[] obj = new Object[] { cta.getRazonSocial(),
						cta.getRuc(), cta.getAvencer(), cta.getVencido(),
						cta.getSaldo() };
				data.add(obj);
			}

			ReporteSaldosClientes rep = new ReporteSaldosClientes(desde, hasta,
					"Saldos de Proveedores", "TODOS..");
			rep.setDatosReporte(data);
			
			rep.setA4();
			rep.setApaisada();

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}

		/**
		 * TES-00020 cobranzas segun forma de pago..
		 */
		private void cobranzasFormaPago(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Cliente cliente = filtro.getCliente();
			String formaPago = filtro.getFormaPago_();
			boolean ivaInc = filtro.isIvaIncluido();
			boolean recInc = filtro.isIncluirREC();
			boolean vtaInc = filtro.isIncluirVCT();
			boolean reeInc = filtro.isIncluirCHQ_RECH();
			boolean antInc = filtro.isIncluirAnticipos();
			boolean extInc = filtro.isIncluirCobroExterno();
			boolean ncrInc = filtro.isIncluirNCR();
			long idCliente = cliente != null? cliente.getId().longValue() : 0;
			long idSucursal = 0;
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<Recibo> cobranzas = new ArrayList<Recibo>();
			List<Venta> ventas = new ArrayList<Venta>();
			List<Recibo> reembolsos = new ArrayList<Recibo>();
			List<NotaCredito> notasCredito = new ArrayList<NotaCredito>();

			if (recInc) {
				cobranzas = rr.getCobranzas(desde, hasta, idSucursal, idCliente, antInc, false);
				if (formaPago.isEmpty()) {
					for (Recibo recibo : cobranzas) {
						System.out.println("--- " + recibo.getNumero());
						data.add(new Object[] {
								m.dateToString(recibo.getFechaEmision(), "dd-MM-yy"),
								recibo.getNumero(), 
								TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
								recibo.getCliente().getRazonSocial(), "TODOS..",
								ivaInc ? recibo.getTotalImporteGs() : recibo.getTotalImporteGsSinIva() });
					}
				} else if (formaPago.equals(ReportesFiltros.EFECTIVO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalEfectivo();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), 
									recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.RETENCION)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalRetencion();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.CHEQUE_ADELANTADO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo
								.getTotalChequeClienteAdelantado(recibo
										.getFechaEmision());
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.CHEQUE_AL_DIA)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalChequeClienteAldia(recibo.getFechaEmision());
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),			
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.DEPOSITO_BANCARIO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalDepositoBancario();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.TARJETA_CREDITO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalTarjetaCredito();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.TARJETA_DEBITO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalTarjetaDebito();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.DEBITO_COBRANZA_CENTRAL)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalDebitoCobranzaCentral();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.RECAUDACION_CENTRAL)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalRecaudacionCentral();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.TRANSFERENCIA_CENTRAL)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalTransferenciaCentral();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.SALDO_FAVOR_CLIENTE)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalSaldoFavorCliente();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				}  else if (formaPago.equals(ReportesFiltros.SALDO_FAVOR_COBRADO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalSaldoFavorCobrado();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.CANJE_DOCUMENTOS)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalCanjeDocumentos();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				}  else if (formaPago.equals(ReportesFiltros.VALORES_REPRESENTACIONES)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalValoresRepresentaciones();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				}
			}
			
			if (reeInc) {
				reembolsos = rr.getReembolsosCheques(desde, hasta, idSucursal, idCliente);
				if (formaPago.isEmpty()) {
					for (Recibo recibo : reembolsos) {
						data.add(new Object[] {
								m.dateToString(recibo.getFechaEmision(), "dd-MM-yy"),
								recibo.getNumero(), 
								TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
								recibo.getCliente().getRazonSocial(), "TODOS..",
								ivaInc ? recibo.getTotalImporteGs() : recibo.getTotalImporteGsSinIva() });
					}
				} else if (formaPago.equals(ReportesFiltros.EFECTIVO)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalEfectivo();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), 
									recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.RETENCION)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalRetencion();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.CHEQUE_ADELANTADO)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo
								.getTotalChequeClienteAdelantado(recibo
										.getFechaEmision());
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.CHEQUE_AL_DIA)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalChequeClienteAldia(recibo.getFechaEmision());
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),			
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.DEPOSITO_BANCARIO)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalDepositoBancario();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.TARJETA_CREDITO)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalTarjetaCredito();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.TARJETA_DEBITO)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalTarjetaDebito();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.DEBITO_COBRANZA_CENTRAL)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalDebitoCobranzaCentral();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.RECAUDACION_CENTRAL)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalRecaudacionCentral();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.TRANSFERENCIA_CENTRAL)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalTransferenciaCentral();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.SALDO_FAVOR_CLIENTE)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalSaldoFavorCliente();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				}  else if (formaPago.equals(ReportesFiltros.SALDO_FAVOR_COBRADO)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalSaldoFavorCobrado();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.CANJE_DOCUMENTOS)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalCanjeDocumentos();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				}  else if (formaPago.equals(ReportesFiltros.VALORES_REPRESENTACIONES)) {
					for (Recibo recibo : reembolsos) {
						double total = recibo.getTotalValoresRepresentaciones();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				}
			}
			
			if (extInc) {
				cobranzas = rr.getCobranzas(desde, hasta, idSucursal, idCliente, false, extInc);
				if (formaPago.isEmpty()) {
					for (Recibo recibo : cobranzas) {
						data.add(new Object[] {
								m.dateToString(recibo.getFechaEmision(), "dd-MM-yy"),
								recibo.getNumero(), 
								TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
								recibo.getCliente().getRazonSocial(), "TODOS..",
								ivaInc ? recibo.getTotalImporteGs() : recibo.getTotalImporteGsSinIva() });
					}
				} else if (formaPago.equals(ReportesFiltros.EFECTIVO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalEfectivo();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), 
									recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.RETENCION)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalRetencion();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.CHEQUE_ADELANTADO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo
								.getTotalChequeClienteAdelantado(recibo
										.getFechaEmision());
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.CHEQUE_AL_DIA)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalChequeClienteAldia(recibo.getFechaEmision());
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),			
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.DEPOSITO_BANCARIO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalDepositoBancario();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.TARJETA_CREDITO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalTarjetaCredito();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.TARJETA_DEBITO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalTarjetaDebito();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.DEBITO_COBRANZA_CENTRAL)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalDebitoCobranzaCentral();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.RECAUDACION_CENTRAL)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalRecaudacionCentral();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.TRANSFERENCIA_CENTRAL)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalTransferenciaCentral();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.SALDO_FAVOR_CLIENTE)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalSaldoFavorCliente();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				}  else if (formaPago.equals(ReportesFiltros.SALDO_FAVOR_COBRADO)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalSaldoFavorCobrado();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.CANJE_DOCUMENTOS)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalCanjeDocumentos();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				}  else if (formaPago.equals(ReportesFiltros.VALORES_REPRESENTACIONES)) {
					for (Recibo recibo : cobranzas) {
						double total = recibo.getTotalValoresRepresentaciones();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total != 0)
							data.add(new Object[] {
									m.dateToString(recibo.getFechaEmision(),
											"dd-MM-yy"), recibo.getNumero(),
									TipoMovimiento.getAbreviatura(recibo.getTipoMovimiento().getSigla()),
									recibo.getCliente().getRazonSocial(),
									formaPago, total });
					}
				}
			}
			
			// Ventas Contado..
			if (vtaInc) {
				List<Venta> ventas_ = rr.getVentasContado(desde, hasta, idCliente, 0);
				for (Venta venta : ventas_) {
					if (!venta.isAnulado()) {
						ventas.add(venta);
					}
				}
				if (formaPago.isEmpty()) {
					for (Venta venta : ventas) {
						data.add(new Object[] {
								Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YY),
								venta.getNumero(), 
								TipoMovimiento.getAbreviatura(venta.getTipoMovimiento().getSigla()),
								venta.getCliente().getRazonSocial(), "TODOS..",
								ivaInc ? venta.getTotalImporteGs() : venta.getTotalImporteGsSinIva() });
					}
				} else if (formaPago.equals(ReportesFiltros.EFECTIVO)) {
					for (Venta venta : ventas) {
						double total = venta.getTotalEfectivo();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YY),
									venta.getNumero(),
									TipoMovimiento.getAbreviatura(venta.getTipoMovimiento().getSigla()),
									venta.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.RETENCION)) {
					for (Venta venta : ventas) {
						double total = venta.getTotalRetencion();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YY),
									venta.getNumero(),
									TipoMovimiento.getAbreviatura(venta.getTipoMovimiento().getSigla()),
									venta.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.CHEQUE_ADELANTADO)) {
					for (Venta venta : ventas) {
						double total = venta.getTotalChequeClienteAdelantado(venta.getFecha());
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YY),
									venta.getNumero(),
									TipoMovimiento.getAbreviatura(venta.getTipoMovimiento().getSigla()),
									venta.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.CHEQUE_AL_DIA)) {
					for (Venta venta : ventas) {
						double total = venta.getTotalChequeClienteAldia(venta.getFecha());
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YY),
									venta.getNumero(),
									TipoMovimiento.getAbreviatura(venta.getTipoMovimiento().getSigla()),
									venta.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.DEPOSITO_BANCARIO)) {
					for (Venta venta : ventas) {
						double total = venta.getTotalDepositoBancario();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YY),
									venta.getNumero(),
									TipoMovimiento.getAbreviatura(venta.getTipoMovimiento().getSigla()),
									venta.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.TARJETA_CREDITO)) {
					for (Venta venta : ventas) {
						double total = venta.getTotalTarjetaCredito();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YY),
									venta.getNumero(),
									TipoMovimiento.getAbreviatura(venta.getTipoMovimiento().getSigla()),
									venta.getCliente().getRazonSocial(),
									formaPago, total });
					}
				} else if (formaPago.equals(ReportesFiltros.TARJETA_DEBITO)) {
					for (Venta venta : ventas) {
						double total = venta.getTotalTarjetaDebito();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] {
									Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YY),
									venta.getNumero(),
									TipoMovimiento.getAbreviatura(venta.getTipoMovimiento().getSigla()),
									venta.getCliente().getRazonSocial(),
									formaPago, total });
					}
				}
			}		
			
			// Notas de Credito Contado..
			if (ncrInc && (formaPago.isEmpty() || formaPago.equals(ReportesFiltros.EFECTIVO))) {
				List<NotaCredito> notasCredito_ = rr.getNotasCreditoVenta(desde, hasta, idCliente, idSucursal, "");
				for (NotaCredito ncr : notasCredito_) {
					if (!ncr.isAnulado() && ncr.getAuxi().equals(NotaCredito.NCR_CONTADO)) {
						notasCredito.add(ncr);
					}
				}
				if (formaPago.isEmpty()) {
					for (NotaCredito ncr : notasCredito) {
						double total = ivaInc ? ncr.getImporteGs() : ncr.getTotalImporteGsSinIva();
						data.add(new Object[] { Utiles.getDateToString(ncr.getFechaEmision(), Utiles.DD_MM_YY),
								ncr.getNumero(), TipoMovimiento.getAbreviatura(ncr.getTipoMovimiento().getSigla()),
								ncr.getCliente().getRazonSocial(), "TODOS..",
								total });
					}
				} else if (formaPago.equals(ReportesFiltros.EFECTIVO)) {
					for (NotaCredito ncr : notasCredito) {
						double total = ncr.getImporteGs();
						if (!ivaInc)
							total = total - Utiles.getIVA(total, Configuracion.VALOR_IVA_10);
						if (total > 0)
							data.add(new Object[] { Utiles.getDateToString(ncr.getFechaEmision(), Utiles.DD_MM_YY),
									ncr.getNumero(),
									TipoMovimiento.getAbreviatura(ncr.getTipoMovimiento().getSigla()),
									ncr.getCliente().getRazonSocial(), formaPago, (total * -1) });
					}
				}
			}

			ReporteCobranzasFormaPago rep = new ReporteCobranzasFormaPago(desde, hasta,
					formaPago.isEmpty() ? "TODOS.." : formaPago, recInc ? "SI" : "NO", vtaInc ? "SI" : "NO",
					antInc ? "SI" : "NO", reeInc ? "SI" : "NO", extInc ? "SI" : "NO", ncrInc ? "SI" : "NO",
					ivaInc ? "SI" : "NO", getAcceso().getSucursalOperativa().getText());
			rep.setApaisada();
			rep.setDatosReporte(data);
			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00021 cheques descontados y no descontados..
		 */
		private void chequesPendientesDescuento(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Date vtoDesde = filtro.getFechaDesde2();
			Date vtoHasta = filtro.getFechaHasta2();
			
			BancoCta banco = filtro.getBancoCta();
			long idBanco = banco != null ? banco.getId() : 0;
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();
			if (vtoDesde == null) vtoDesde = new Date();
			if (vtoHasta == null) vtoHasta = new Date();
			
			Date vtoDesde_ = Utiles.getFecha(Utiles.getDateToString(vtoDesde, Utiles.DD_MM_YYYY + " 00:00:00"));
			Date vtoHasta_ = Utiles.getFecha(Utiles.getDateToString(vtoHasta, Utiles.DD_MM_YYYY + " 23:59:00"));

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			
			List<BancoDescuentoCheque> descuentos = rr.getBancoDescuentos(desde, hasta, idBanco);
			
			for (BancoDescuentoCheque dto : descuentos) {
				for (BancoChequeTercero cheque : dto.getCheques()) {
					if (cheque.getFecha().compareTo(vtoDesde_) >= 0
							&& cheque.getFecha().compareTo(vtoHasta_) <= 0) {
						data.add(new Object[] { 
								Utiles.getDateToString(cheque.getFecha(), Utiles.DD_MM_YYYY),
								cheque.getNumero(),
								cheque.getBanco().getDescripcion().toUpperCase(),
								Utiles.getDateToString(dto.getFecha(), Utiles.DD_MM_YYYY),
								dto.getId() + "",
								dto.getBanco().getBanco().getDescripcion().toUpperCase(),
								Utiles.getMaxLength(cheque.getCliente().getRazonSocial(), 30),
								Utiles.getRedondeo(cheque.getMonto())});
					}					
				}
			}

			String sucursal = getAcceso().getSucursalOperativa().getText();
			String banco_ = banco == null ? "TODOS.." : banco.getBanco().getDescripcion().toUpperCase();
			ReporteChequesPendientesDescuento rep = new ReporteChequesPendientesDescuento(
					desde, hasta, sucursal, banco_, vtoDesde, vtoHasta);
			rep.setApaisada();
			rep.setDatosReporte(data);
			
			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00022 cheques depositados y no depositados..
		 */
		private void chequesDepositados(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Tipo banco = filtro.getBancoTercero();
			String deposito = filtro.getDepositoCheque();
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<BancoChequeTercero> cheques = rr.getChequesTercero(desde, hasta, banco, 0, null, null);

			for (BancoChequeTercero cheque : cheques) {
				String cliente = cheque.getCliente() == null ? "" : cheque.getCliente().getRazonSocial();
				if (deposito.isEmpty()) {
					data.add(new Object[] {
							cheque.getFecha(),
							Long.parseLong(cheque.getNumero()),
							cheque.getBanco().getDescripcion().toUpperCase(),
							cliente,
							cheque.isDepositado() ? "DEPOSITADO"
									: "NO DEPOSITADO", cheque.getMonto() });

				} else if (deposito.equals(ReportesFiltros.CHEQUES_DEPOSITADOS)
						&& cheque.isDepositado()) {
					data.add(new Object[] {
							cheque.getFecha(),
							Long.parseLong(cheque.getNumero()),
							cheque.getBanco().getDescripcion().toUpperCase(),
							cliente,
							cheque.isDepositado() ? "DEPOSITADO"
									: "NO DEPOSITADO", cheque.getMonto() });
					
				} else if (deposito.equals(ReportesFiltros.CHEQUES_NO_DEPOSITADOS)
						&& !cheque.isDepositado()) {
					data.add(new Object[] {
							cheque.getFecha(),
							Long.parseLong(cheque.getNumero()),
							cheque.getBanco().getDescripcion().toUpperCase(),
							cliente,
							cheque.isDepositado() ? "DEPOSITADO"
									: "NO DEPOSITADO", cheque.getMonto() });
				}
			}

			String sucursal = getAcceso().getSucursalOperativa().getText();
			String banco_ = banco == null ? "TODOS.." : banco.getDescripcion().toUpperCase();
			String deposito_ = deposito.isEmpty() ? "TODOS.." : deposito;
			ReporteChequesDepositados rep = new ReporteChequesDepositados(
					desde, hasta, sucursal, banco_, deposito_);
			rep.setApaisada();
			rep.setDatosReporte(data);
			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00023
		 */
		private void clientesCredito(boolean mobile) throws Exception {
			String estado = filtro.getEstadoCuentaCliente();
			EmpresaCartera cartera = filtro.getCartera();
			long idCartera = cartera != null ? cartera.getId().longValue() : 0; 

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> clientes = rr.getClientesCredito(idCartera);
			List<Object[]> data = new ArrayList<Object[]>();

			for (Object[] cliente : clientes) {
				Object[] cli = new Object[] {
						cliente[1],
						cliente[2],
						(boolean) cliente[3] ? "BLOQUEADO" : "HABILITADO", cliente[4] };
				if (estado == null) {
					data.add(cli);
				} else if (estado
						.equals(ReportesFiltros.ESTADO_CTA_CLIENTE_BLOQUEADO)
						&& (boolean) cliente[3]) {
					data.add(cli);
				} else if (estado
						.equals(ReportesFiltros.ESTADO_CTA_CLIENTE_HABILITADO)
						&& !(boolean) cliente[3]) {
					data.add(cli);
				}
			}
			String estado_ = estado == null? "TODOS.." : estado;
			String cartera_ = cartera == null? "TODOS.." : cartera.getDescripcion();
			ReporteClientesCredito rep = new ReporteClientesCredito(estado_, getSucursal(), cartera_);
			rep.setApaisada();
			rep.setDatosReporte(data);		

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00024
		 */
		private void boletasDeposito(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			BancoCta banco = filtro.getBancoCta();
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<BancoBoletaDeposito> depositos = rr.getBancoDepositos(desde, hasta, banco == null? 0:banco.getId());
			
			for (BancoBoletaDeposito dep : depositos) {
				if (dep.isCerrado()) {
					data.add(new Object[]{ 
							Utiles.getDateToString(dep.getFecha(), Utiles.DD_MM_YYYY),
							dep.getNumeroBoleta(), dep.getNroCuenta().getBanco().getDescripcion().toUpperCase(),
							dep.getPlanillaCaja().replace(";", " - "),
							dep.getMonto()});
				}
			}

			String sucursal = getAcceso().getSucursalOperativa().getText();
			String banco_ = banco == null? "TODOS.." : banco.getBanco().getDescripcion();
			ReporteBoletasDeposito rep = new ReporteBoletasDeposito(desde, hasta, banco_, sucursal);
			rep.setDatosReporte(data);

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00025
		 */
		private void chequesPropios(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			BancoCta banco = filtro.getBancoCta();
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<BancoCheque> cheques = rr.getCheques(desde, hasta, banco == null? 0 : banco.getId());
			
			for (BancoCheque cheque : cheques) {
				if (!cheque.isAnulado()) {
					data.add(new Object[]{ 
							Utiles.getDateToString(cheque.getFechaVencimiento(), Utiles.DD_MM_YYYY),
							Utiles.getDateToString(cheque.getFechaEmision(), Utiles.DD_MM_YYYY),
							cheque.getNumero() + "", cheque.getBanco().getBanco().getDescripcion().toUpperCase(),
							cheque.getBeneficiario(),
							cheque.isMonedaLocal() ? 0.0 : cheque.getMonto(),
							cheque.isMonedaLocal() ? cheque.getMonto() : 0.0});
				}
			}

			String sucursal = getAcceso().getSucursalOperativa().getText();
			String banco_ = banco == null? "TODOS.." : banco.getBanco().getDescripcion();
			ReporteChequesPropios rep = new ReporteChequesPropios(desde, hasta, banco_, sucursal);
			rep.setDatosReporte(data);
			rep.setApaisada();
			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00026
		 */
		private void clientesPorCobrador(boolean mobile) throws Exception {
			Funcionario cobrador = filtro.getCobrador();
			
			if (cobrador == null) {
				Clients.showNotification("Debe seleccionar un cobrador..", 
						Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
			
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Cliente> clientes = rr.getClientesByCobrador(cobrador.getId());
			List<Object[]> data = new ArrayList<Object[]>();

			for (Cliente cliente : clientes) {
				Object[] cli = new Object[] {
						cliente.getRazonSocial(),
						cliente.getRuc(),
						cliente.getDireccion(),
						cliente.getTelefono()};
						data.add(cli);
			}
			ReporteClientesPorCobrador rep = new ReporteClientesPorCobrador(cobrador.getRazonSocial(), getSucursal());
			rep.setApaisada();
			rep.setDatosReporte(data);
			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00027
		 */
		private void recaudacionCasaCentral(boolean mobile) throws Exception {

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<RecaudacionCentral> rccs = rr.getRecaudacionesCentral();
			
			boolean pendientes = filtro.isFraccionado();
			
			for (RecaudacionCentral rcc : rccs) {
				Object[] rcc_ = new Object[] { 
						rcc.getRazonSocial().toUpperCase(), rcc.getNumeroRecibo(),
						rcc.getNumeroCheque().isEmpty() ? "- - -" : rcc.getNumeroCheque(), 
						rcc.getNumeroDeposito().isEmpty() ? "- - -" : rcc.getNumeroDeposito(),
						rcc.getImporteGs(), rcc.getSaldoGs() };
				if (!pendientes) {
					data.add(rcc_);
				} else {
					if (rcc.getSaldoGs() > 0) {
						data.add(rcc_);
					}
				}				
			}

			String sucursal = getAcceso().getSucursalOperativa().getText();
			ReporteRecaudacionCentral rep = new ReporteRecaudacionCentral(sucursal);
			rep.setApaisada();
			rep.setDatosReporte(data);
			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00028
		 */
		private void chequesPropiosAvencer(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();
			
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<BancoCheque> cheques = rr.getCheques("", "", "", "", "", "", true, false, desde, hasta, "", "");			
			for (BancoCheque cheque : cheques) {
				if (!cheque.isAnulado()) {
					data.add(new Object[]{ 
							Utiles.getDateToString(cheque.getFechaEmision(), Utiles.DD_MM_YYYY),
							Utiles.getDateToString(cheque.getFechaVencimiento(), Utiles.DD_MM_YYYY),
							cheque.getNumero() + "", cheque.getBanco().getBanco().getDescripcion().toUpperCase(),
							cheque.getBeneficiario(),
							cheque.isMonedaLocal() ? 0.0 : cheque.getMonto(),
							cheque.isMonedaLocal() ? cheque.getMonto() : 0.0});
				}
			}
			String sucursal = getAcceso().getSucursalOperativa().getText();
			ReporteChequesPropiosAvencer rep = new ReporteChequesPropiosAvencer(sucursal);
			rep.setDatosReporte(data);
			rep.setApaisada();
			
			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00029
		 */
		private void anticiposUtilidad(boolean mobile) throws Exception {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<BancoDescuentoCheque> dtos = rr.getAnticiposUtilidad();
			
			for (BancoDescuentoCheque dto : dtos) {
				data.add(new Object[] {
						Utiles.getDateToString(dto.getFecha(),
						Utiles.DD_MM_YYYY), "ANTICIPO " + dto.getId(),
						dto.getObservacion(), dto.getTotalImporteGs() });
			}

			String sucursal = getAcceso().getSucursalOperativa().getText();
			ReporteAnticipoUtilidad rep = new ReporteAnticipoUtilidad(sucursal);
			rep.setDatosReporte(data);

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}	
		}
		
		/**
		 * TES-00030
		 */
		private void clientesPorRubro(boolean mobile, String codReporte) throws Exception {
			
			RegisterDomain rr = RegisterDomain.getInstance();
			EmpresaRubro rubro = filtro.getRubro_();
			long id_rubro = rubro == null? 0 : rubro.getId();
			
			List<Object[]> clientes = rr.getClientesByRubro(id_rubro);
			List<Object[]> data = new ArrayList<Object[]>();

			for (Object[] cliente : clientes) {
				Object[] cli = new Object[] {
						cliente[0],
						cliente[1],
						cliente[2],
						cliente[3]};
						data.add(cli);
			}
			String desc = rubro == null ? "TODOS.." : rubro.getDescripcion().toUpperCase();
			ReporteClientesPorRubro rep = new ReporteClientesPorRubro(desc, getSucursal());
			rep.setApaisada();
			rep.setDatosReporte(data);
			rep.setTitulo(codReporte + " - Clientes por Rubro");			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		
		}
		
		/**
		 * TES-00031
		 */
		private void correlatividadRecibos(boolean mobile) {
			try {
				int desde = filtro.getCantidadDesde();
				int hasta = filtro.getCantidadHasta();
				
				String desde_ = desde + "";
				String hasta_ = hasta + "";
				
				long desd = Long.parseLong(desde_);
				long hast = Long.parseLong(hasta_);

				SucursalApp sucursal = filtro.getSelectedSucursal();
				long idSucursal = sucursal == null ? 0 : sucursal.getId();
				String sucursal_ = sucursal == null ? "TODOS.." : sucursal.getDescripcion();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Recibo> cobranzas = rr.getCobranzas(desd, hast, idSucursal);
				List<Object[]> data = new ArrayList<Object[]>();

				for (Recibo cobro : cobranzas) {
					Object[] cob = new Object[] {
							Utiles.getDateToString(cobro.getFechaEmision(), Utiles.DD_MM_YYYY),
							cobro.getNumero(),
							cobro.isAnulado() ? "ANULADO.." : cobro.getCliente().getRazonSocial().toUpperCase(),
							cobro.getCliente().getRuc(),
							cobro.isCobroExterno() ? 0.0 : cobro.getTotalImporteGs()
					};
							data.add(cob);
				}
				
				ReporteCobranzas rep = new ReporteCobranzas(desde_, hasta_, sucursal_);
				rep.setApaisada();
				rep.setDatosReporte(data);

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * TES-00032
		 */
		private void recibosAnulados(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				
				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				SucursalApp sucursal = filtro.getSelectedSucursal();
				long idSucursal = sucursal == null ? 0 : sucursal.getId();
				long idCliente = 0;
				String sucursal_ = sucursal == null ? "TODOS.." : sucursal.getDescripcion();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Recibo> cobranzas = rr.getCobranzas(desde, hasta, idSucursal, idCliente, true, false);
				List<Object[]> data = new ArrayList<Object[]>();

				for (Recibo cobro : cobranzas) {
					if (cobro.isAnulado()) {
						Object[] cob = new Object[] {
								Utiles.getDateToString(cobro.getFechaEmision(), Utiles.DD_MM_YYYY),
								cobro.getNumero(),
								"ANULADO..",
								"",
								cobro.getTotalImporteGs()
						};
								data.add(cob);
					}					
				}
				
				String desde_ = Utiles.getDateToString(desde, Utiles.DD_MM_YY);	
				String hasta_ = Utiles.getDateToString(hasta, Utiles.DD_MM_YY);
				ReporteCobranzas rep = new ReporteCobranzas(desde_, hasta_, sucursal_);
				rep.setApaisada();
				rep.setDatosReporte(data);

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * TES-00033
		 */
		private void prestamosCasaCentral(boolean mobile) throws Exception {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<BancoDescuentoCheque> dtos = rr.getPrestamosCasaCentral();
			
			for (BancoDescuentoCheque dto : dtos) {
				data.add(new Object[] {
						Utiles.getDateToString(dto.getFecha(),
						Utiles.DD_MM_YYYY), "PRESTAMO " + dto.getId(),
						dto.getObservacion(), dto.getTotalImporteGs() });
			}

			String sucursal = getAcceso().getSucursalOperativa().getText();
			ReportePrestamoCasaCentral rep = new ReportePrestamoCasaCentral(sucursal);
			rep.setDatosReporte(data);

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}	
		}
		
		/**
		 * TES-00034
		 */
		private void reembolsosPrestamosCasaCentral(boolean mobile) throws Exception {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<Recibo> dtos = rr.getReembolsosPrestamosCasaCentral();
			
			for (Recibo dto : dtos) {
				data.add(new Object[] {
						Utiles.getDateToString(dto.getFechaEmision(), Utiles.DD_MM_YYYY),
						"REEMBOLSO NRO. " + dto.getNumero().replace("CAN-CHQ-", ""),
						dto.getTotalImporteGs() });
			}

			String sucursal = getAcceso().getSucursalOperativa().getText();
			ReporteReembolsosPrestamoCasaCentral rep = new ReporteReembolsosPrestamoCasaCentral(sucursal);
			rep.setDatosReporte(data);

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}	
		}
		
		/**
		 * TES-00035
		 */
		private void clientesConMovimientos(boolean mobile) throws Exception {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<HistoricoMovimientos> movims = rr.getHistoricoMovimientos();
				
			for (HistoricoMovimientos movim : movims) {
				Object[] cli = rr.getCliente(movim.getIdCliente());
				String razonSocial = (String) cli[1];
				double limiteCredito = (double) cli[2];
				boolean ventaCredito = (boolean) cli[3];
				data.add(new Object[] { razonSocial,
						movim.getTotalVentasContado(),
						movim.getTotalVentasCredito(),
						movim.getTotalSaldoGs(),
						movim.getTotalChequePendientesGs(),
						(movim.getTotalSaldoGs() + movim.getTotalChequePendientesGs()),
						limiteCredito,						
						ventaCredito ? "SI" : "NO"});
			}

			String sucursal = getAcceso().getSucursalOperativa().getText();
			ReporteClientesConMovimiento rep = new ReporteClientesConMovimiento(sucursal);
			rep.setApaisada();
			rep.setDatosReporte(data);

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}	
		}
		
		/**
		 * TES-00036
		 */
		private void historialBloqueos(boolean mobile) throws Exception {			
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();
			
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<HistoricoBloqueoClientes> bloqueos = rr.getHistoricoBloqueoClientes(desde, hasta);
			for (HistoricoBloqueoClientes bloqueo : bloqueos) {
				data.add(new Object[] {
						bloqueo.getNumeroFactura(),
						Utiles.getDateToString(bloqueo.getVencimiento(), Utiles.DD_MM_YYYY),
						bloqueo.getCliente(), bloqueo.getMotivo(), 
						bloqueo.getUsuarioMod().toUpperCase(),
						Utiles.getDateToString(bloqueo.getFecha(), Utiles.DD_MM_YYYY)});
			}
			
			ReporteHistorialBloqueos rep = new ReporteHistorialBloqueos(desde, hasta);
			rep.setApaisada();
			rep.setDatosReporte(data);

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}	
		}
		
		/**
		 * TES-00037
		 */
		private void chequesPropiosAvencer_(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Tipo moneda = filtro.getMoneda();
			
			if (moneda.esNuevo()) {
				Clients.showNotification("DEBE SELECCIONAR UNA MONEDA..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();
			
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<BancoCheque> cheques = rr.getChequesVencimiento(desde, hasta, moneda.getSigla());			
			for (BancoCheque cheque : cheques) {
				if (!cheque.isAnulado()) {
					data.add(new Object[]{ 
							Utiles.getDateToString(cheque.getFechaEmision(), Utiles.DD_MM_YYYY),
							Utiles.getDateToString(cheque.getFechaVencimiento(), Utiles.DD_MM_YYYY),
							cheque.getNumero() + "", cheque.getBanco().getBanco().getDescripcion().toUpperCase(),
							cheque.getBeneficiario(),
							cheque.isMonedaLocal() ? 0.0 : cheque.getMonto(),
							cheque.isMonedaLocal() ? cheque.getMonto() : 0.0 });
				}
			}
			String sucursal = getAcceso().getSucursalOperativa().getText();
			ReporteChequesPropiosAvencer rep = new ReporteChequesPropiosAvencer(sucursal);
			rep.setDatosReporte(data);
			rep.setApaisada();
			
			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00038 Ingresos / Egresos por diferencia cambio..
		 */
		private void diferenciasTipoCambio(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<Recibo> pagos = rr.getPagos(desde, hasta);

			for (Recibo pago : pagos) {
				for (ReciboDetalle det : pago.getDetalles()) {
					if (det.getAuxi().equals(ReciboDetalle.TIPO_DIF_CAMBIO)) {
						data.add(new Object[]{ 
								Utiles.getDateToString(pago.getFechaEmision(), Utiles.DD_MM_YYYY),
								pago.getNumero(), pago.getTipoMovimiento().getDescripcion(),
								det.getConcepto(), det.getMontoGs() });
					}
				}
			}		

			ReporteDiferenciaTipoCambio rep = new ReporteDiferenciaTipoCambio(desde, hasta, getSucursal());
			rep.setApaisada();
			rep.setDatosReporte(data);			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * reporte TES-00039
		 */
		private void saldosDeClientesSegunVencimiento(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Funcionario vendedor = filtro.getVendedor();
			long idVendedor = vendedor == null ? 0 : vendedor.getId();
			String vendedor_ = vendedor == null ? "TODOS.." : vendedor.getRazonSocial();
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();
			Map<String, BeanCtaCteEmpresa> map = new HashMap<String, BeanCtaCteEmpresa>();
			String caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE;
			List<CtaCteEmpresaMovimiento> saldos = rr.getMovimientosConSaldoSegunVencimiento(desde, hasta, caracter, idVendedor);
			List<Object[]> data = new ArrayList<Object[]>();

			for (CtaCteEmpresaMovimiento item : saldos) {
				BeanCtaCteEmpresa cta = map.get(item.getIdEmpresa() + "");
				if (cta != null) {
					if (item.isVencido()) {
						cta.setVencido(cta.getVencido() + item.getSaldoFinal());
					} else {
						cta.setAvencer(cta.getAvencer() + item.getSaldoFinal());
					}
					cta.setSaldo(cta.getSaldo() + item.getSaldoFinal());
				} else {
					Empresa emp = rr.getEmpresaById(item.getIdEmpresa());
					cta = new BeanCtaCteEmpresa();
					cta.setRazonSocial(emp.getRazonSocial());
					cta.setRuc(emp.getRuc());
					if (item.isVencido()) {
						cta.setVencido(item.getSaldoFinal());
					} else {
						cta.setAvencer(item.getSaldoFinal());
					}
					cta.setSaldo(item.getSaldoFinal());
				}
				map.put(item.getIdEmpresa() + "", cta);
			}

			for (String key : map.keySet()) {
				BeanCtaCteEmpresa cta = map.get(key);
				Object[] obj = new Object[] { cta.getRazonSocial(),
						cta.getRuc(), cta.getAvencer(), cta.getVencido(),
						cta.getSaldo() };
				data.add(obj);
			}
			
			Collections.sort(data, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					String val1 = (String) o1[0];
					String val2 = (String) o2[0];
					int compare = val1.compareTo(val2);				
					return compare;
				}
			});

			ReporteSaldosClientes rep = new ReporteSaldosClientes(desde, hasta, "Saldos de Clientes (Segun fecha Vencimiento)", vendedor_);
			rep.setDatosReporte(data);
			
			rep.setA4();
			rep.setApaisada();

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00040 recaudaciones no depositadas..
		 */
		private void recaudacionesNoDepositadas(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> resumenes = rr.getCajaPlanillaResumenes(desde, hasta);
			List<Object[]> datos = new ArrayList<Object[]>();
			for (Object[] res : resumenes) {
				Date fecha = (Date) res[0];
				datos.add(new Object[]{ Utiles.getDateToString(fecha, "dd-MM-yyyy"), res[1], res[2], res[3], res[4], res[5] });
			}

			ReporteRecaudacionesNoDepositadas rep = new ReporteRecaudacionesNoDepositadas(desde, hasta, getSucursal());
			rep.setApaisada();
			rep.setDatosReporte(datos);			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * TES-00041 Resumen planillas caja..
		 */
		private void resumenPlanillasCaja() {
			CajaPlanillaResumen resumen = filtro.getSelectedResumen();
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_RESUMEN_;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new ResumenDataSource(resumen);
			params.put("Fecha", Utiles.getDateToString(resumen.getFecha(), Utiles.DD_MM_YYYY));
			params.put("NroResumen", resumen.getNumero());
			params.put("Usuario", getUs().getNombre());
			imprimirJasper(source, params, dataSource, com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_PDF);
		}
		
		/**
		 * reporte TES-00042
		 */
		private void saldosCtaCteConsolidado(boolean cliente, boolean mobile) throws Exception {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde_ = Utiles.getFecha("01-01-2015 00:00:00");			
			Date hasta_ = new Date();
			Funcionario vendedor = filtro.getVendedor();
			Cliente cliente_ = filtro.getCliente();
			EmpresaRubro rubro = filtro.getRubro_();
			Tipo moneda = filtro.getMoneda();
			
			if (moneda.esNuevo()) {
				Clients.showNotification("DEBE SELECCIONAR UN TIPO DE CUENTA..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
			
			Object[] formato = filtro.getFormato();
			long idVendedor = vendedor == null ? 0 : vendedor.getId();
			long idEmpresa = cliente_ == null ? 0 : cliente_.getEmpresa().getId();
			String caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE;
			if (!cliente) {
				caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR;
			}
			List<Object[]> movims = new ArrayList<Object[]>();
			List<Object[]> movimientos = new ArrayList<Object[]>();
			List<Object[]> aux = new ArrayList<Object[]>();
			Map<String, Object[]> data = new HashMap<String, Object[]>();			

			movims = rr.getSaldos(desde_, hasta_, caracter, idVendedor, idEmpresa, moneda.getId(), 0);	
			for (Object[] movim : movims) {
				if ((rubro != null && this.isRubro(rubro.getId(), idEmpresa)) || rubro == null) {
					long id_mov = (long) movim[0];
					long id_tmv = (long) movim[1];
					data.put(id_mov + "-" + id_tmv, movim);
					movimientos.add(movim);
				}					
			}
			for (String key : data.keySet()) {
				aux.add(data.get(key));
			}
		
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_CONSOLIDADO;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new CtaCteSaldosDataSource_(movimientos, aux);
			params.put("Titulo", cliente ? "SALDOS DE CLIENTES CONSOLIDADO" : "SALDOS DE PROVEEDORES CONSOLIDADO");
			params.put("Usuario", getUs().getNombre());
			params.put("Vendedor", vendedor == null ? "TODOS.." : vendedor.getRazonSocial().toUpperCase());
			params.put("Rubro", rubro == null ? "TODOS.." : rubro.getDescripcion().toUpperCase());
			params.put("Moneda", moneda.getDescripcion().toUpperCase());
			imprimirJasper(source, params, dataSource, formato);
		}
		
		
		/**
		 * reporte TES-00043
		 */
		private void historialMovimientosCliente() {
			try {
				Date desde = filtro.getFechaInicioOperaciones();
				Date hasta = filtro.getFechaHasta();
				Cliente cliente = filtro.getCliente();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> historico;
				List<Object[]> historicoEntrada;
				List<Object[]> historicoSalida;
				
				long idCliente = cliente != null? cliente.getId() : 0;

				List<Object[]> ventas = rr.getVentasPorArticulo(idCliente, desde, hasta);

				historicoEntrada = new ArrayList<Object[]>();
				historicoSalida = new ArrayList<Object[]>();
				
				historicoEntrada.addAll(ventas);

				for (Object[] movim : historicoEntrada) {
					movim[0] = "(+)" + movim[0];
				}
				
				historico = new ArrayList<Object[]>();
				historico.addAll(historicoEntrada);
				historico.addAll(historicoSalida);

				// ordena la lista segun fecha..
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						Date fecha1 = (Date) o1[1];
						Date fecha2 = (Date) o2[1];
						return fecha1.compareTo(fecha2);
					}
				});
				
				long saldo = 0;
				for (Object[] hist : historico) {
					boolean ent = ((String) hist[0]).startsWith("(+)");
					String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
					String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
					String numero = (String) hist[2];
					String concepto = ((String) hist[0]).replace("(+)", "");
					String entrada = ent ? hist[3] + "" : "";
					String salida = ent ? "" : hist[3] + "";
					String importe = Utiles.getNumberFormat((double) hist[4]);;
					String dep = (String) hist[6];
					saldo += ent ? Long.parseLong(hist[3] + "") :  Long.parseLong(hist[3] + "") * -1;
					data.add(new Object[] { fecha, hora, numero, concepto, dep, entrada, salida, saldo + "", importe });
				}
				
				/*ReporteHistorialMovimientosArticulo rep = new ReporteHistorialMovimientosArticulo(desde, hasta, articulo.getCodigoInterno(), desc_deposito);
				rep.setApaisada();
				rep.setDatosReporte(data);				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);*/
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte TES-00044
		 */
		private void saldosClientesPorMes(boolean mobile) {			
			try {
				if (mobile) {
					Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
					return;
				}
				
				if (filtro.getAnhoDesde() == null) {
					Clients.showNotification("DEBE SELECCIONAR UN PERIODO..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				String periodo = filtro.getAnhoDesde();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				Date desde = Utiles.getFecha("01-01-2010 00:00:00");		
				Date hasta = new Date();
				Tipo moneda = filtro.getMoneda();
				EmpresaCartera cartera = filtro.getCartera();
				long idCartera = cartera != null ? cartera.getId().longValue() : 0;
				
				if (moneda.esNuevo()) {
					Clients.showNotification("DEBE SELECCIONAR UNA MONEDA..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				Object[] formato = filtro.getFormato();
				String caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE;					
				List<Object[]> movims = new ArrayList<Object[]>();	

				if (cartera != null) {
					movims = rr.getSaldos(desde, hasta, caracter, 0, 0, moneda.getId(), idCartera);
				} else {
					for (EmpresaCartera cart : filtro.getCarteras()) {
						movims.addAll(rr.getSaldos(desde, hasta, caracter, 0, 0, moneda.getId(), cart.getId()));
					}
				}
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_CTA_CTE_SALDOS_DESGLOSADO;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new SaldosCtaCteDesglosado(movims, periodo);
				params.put("Titulo", "Saldos de Clientes desglosado por mes (saldo a la fecha actual)");
				params.put("Usuario", getUs().getNombre());
				params.put("Periodo", periodo);
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		/**
		 * reporte TES-00045
		 */
		private void historialAtrasosClientes(boolean mobile) {
			try {
				RegisterDomain rr = RegisterDomain.getInstance();
				Date desde = filtro.getFechaInicioOperaciones();		
				Date hasta = new Date();
				Cliente cliente = filtro.getCliente();
				Tipo gs = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
				List<Object[]> data = new ArrayList<Object[]>();
				double saldo = 0; double totalCHE = 0;
				long max = 0; long min = 0; long prom = 0; long sum = 0; long min_ = 1000;
				
				if (cliente == null) {
					Clients.showNotification("DEBE SELECCIONAR UN CLIENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				List<Object[]> cheques = rr.getChequesPendientesClientes_(cliente.getId());
				for (Object[] cheque : cheques) {
					double monto = (double) cheque[4];
					totalCHE += monto;
				}
				
				List<Object[]> movims = rr.getSaldos(desde, hasta, Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE, 0, cliente.getIdEmpresa(), gs.getId(), 0);
				for (Object[] movim : movims) {
					Date vencimiento = (Date) movim[7];
					String sigla_tm = (String) movim[13];
					double saldo_ = (double) movim[12];
					if (hasta.compareTo(vencimiento) > 0 && sigla_tm.equals(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO)) {
						String nro = (String) movim[2];
						String recibo = "- - -";						
						long dias = Utiles.diasEntreFechas(vencimiento, new Date());
						String vto = Utiles.getDateToString(vencimiento, Utiles.DD_MM_YYYY);
						data.add(new Object[] { nro, vto, recibo, "- - -", dias });
						if (dias > max) max = dias;
						if (dias < min_) { min = dias; min_ = dias;}
						sum += dias;
					}
					if (sigla_tm.equals(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA)) saldo_ = saldo_ * -1;
					saldo += saldo_;
				}
				
				List<Object[]> recibos = rr.getRecibosPorCliente_(cliente.getId(), desde, hasta);
				for (Object[] rec : recibos) {
					Date vencimiento = (Date) rec[7];
					Date fechaRec = (Date) rec[1];
					if (fechaRec.compareTo(vencimiento) > 0) {
						String nro = (String) rec[5];
						String recibo = (String) rec[2];						
						long dias = Utiles.diasEntreFechas(vencimiento, fechaRec);
						String fecha_rec = Utiles.getDateToString(fechaRec, Utiles.DD_MM_YYYY);
						String vto = Utiles.getDateToString(vencimiento, Utiles.DD_MM_YYYY);
						data.add(new Object[] { nro, vto, recibo, fecha_rec, dias });
						if (dias > max) max = dias;
						if (dias < min_) { min = dias; min_ = dias; }
						sum += dias;
					}
				}
				
				if(data.size() > 0) prom = sum / data.size();
				
				String limiteCred = Utiles.getNumberFormat(cliente.getLimiteCredito());
				String chequesPendientes = Utiles.getNumberFormat(totalCHE);
				String saldoActual = Utiles.getNumberFormat(saldo);
				
				ReporteHistorialAtrasos rep = new ReporteHistorialAtrasos(desde, hasta, cliente.getRazonSocial(), limiteCred, chequesPendientes, saldoActual, max, min, prom);
				rep.setApaisada();
				rep.setDatosReporte(data);				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * TES-00047
		 */
		private void historialMovimientosProveedores(boolean mobile, boolean consolidado) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Date desde = filtro.getFechaInicioOperaciones();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				Proveedor proveedor = filtro.getProveedor();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> historico;
				List<Object[]> historicoDEBE;
				List<Object[]> historicoHABER;
				Map<String, String> totalSaldo = new HashMap<String, String>();

				long idProveedor = proveedor != null ? proveedor.getId() : 0;

				List<Object[]> compras = rr.getComprasPorProveedor_(idProveedor, desde, hasta);
				List<Object[]> gastos = rr.getGastosPorProveedor_(idProveedor, desde, hasta);
				List<Object[]> pagos = rr.getPagosPorProveedor(idProveedor, desde, hasta, true);
				List<Object[]> notascredito = rr.getNotasCreditoPorProveedor(idProveedor, desde, hasta, true);

				historicoDEBE = new ArrayList<Object[]>();
				historicoHABER = new ArrayList<Object[]>();
				
				historicoDEBE.addAll(pagos);
				historicoDEBE.addAll(notascredito);
				
				historicoHABER.addAll(compras);
				historicoHABER.addAll(gastos);

				for (Object[] movim : historicoDEBE) {
					movim[0] = "(+)" + movim[0];
				}

				historico = new ArrayList<Object[]>();
				historico.addAll(historicoHABER);
				historico.addAll(historicoDEBE);

				// ordena la lista segun fecha..
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						Date fecha1 = (Date) o1[1];
						Date fecha2 = (Date) o2[1];
						return fecha1.compareTo(fecha2);
					}
				});
				
				double saldo = 0;
				
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String val1 = (String) o1[4];
						String val2 = (String) o2[4];			
						int compare = val1.compareTo(val2);
						if (compare == 0) {
							Date emision1 = (Date) o1[1];
							Date emision2 = (Date) o2[1];
				            return emision1.compareTo(emision2);
				        }
				        else {
				            return compare;
				        }
					}
				});
				
				String key = "";
				for (Object[] hist : historico) {
					String razonsocial = (String) hist[4];
					if(!key.equals(razonsocial)) saldo = 0;					
					boolean ent = ((String) hist[0]).startsWith("(+)");
					String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
					String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
					String numero = (String) hist[2];
					String concepto = ((String) hist[0]).replace("(+)", "");
					String entrada = ent ? Utiles.getNumberFormat(Double.parseDouble(hist[3] + "")) : "";
					String salida = ent ? "" : Utiles.getNumberFormat(Double.parseDouble(hist[3] + ""));
					saldo += ent ? Double.parseDouble(hist[3] + "") * -1 : Double.parseDouble(hist[3] + "");
					String saldo_ = Utiles.getNumberFormat(saldo);
					data.add(new Object[] { fecha, hora, numero, concepto, entrada, salida, saldo_, razonsocial, (Date) hist[1] });
					totalSaldo.put(razonsocial, saldo_);
					key = razonsocial;
				}
				
				String cli = proveedor != null ? proveedor.getRazonSocial() : "TODOS..";
				String sourceDetallado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET_DHS;
				String sourceConsolidado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_CONSOLIDADO_DHS;
				String source = consolidado? sourceConsolidado : sourceDetallado;
				String titulo = consolidado ? "SALDOS DE PROVEEDORES RESUMIDO (A UNA FECHA)" : "SALDOS DE PROVEEDORES DETALLADO (A UNA FECHA)";
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
				params.put("Titulo", titulo);
				params.put("Usuario", getUs().getNombre());
				params.put("Moneda", filtro.getMonedaGs());
				params.put("desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * TES-00048
		 */
		private void historialMovimientosProveedoresExterior(boolean mobile, boolean consolidado) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Date desde = filtro.getFechaInicioOperaciones();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				Proveedor proveedor = filtro.getProveedor();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> historico;
				List<Object[]> historicoDEBE;
				List<Object[]> historicoHABER;
				Map<String, String> totalSaldo = new HashMap<String, String>();

				long idProveedor = proveedor != null ? proveedor.getId() : 0;

				List<Object[]> importaciones = rr.getImportacionesPorProveedor_(idProveedor, desde, hasta);
				List<Object[]> pagos = rr.getPagosPorProveedorExterior(idProveedor, desde, hasta, false);
				List<Object[]> anticipos = rr.getPagosAnticipadosPorProveedorExterior(idProveedor, desde, hasta, false);
				List<Object[]> notascredito = rr.getNotasCreditoPorProveedor(idProveedor, desde, hasta, false);
				
				historicoDEBE = new ArrayList<Object[]>();
				historicoHABER = new ArrayList<Object[]>();
				
				historicoDEBE.addAll(pagos);
				historicoDEBE.addAll(anticipos);
				historicoDEBE.addAll(notascredito);
				historicoHABER.addAll(importaciones);

				for (Object[] movim : historicoDEBE) {
					movim[0] = "(+)" + movim[0];
				}

				historico = new ArrayList<Object[]>();
				historico.addAll(historicoHABER);
				historico.addAll(historicoDEBE);

				// ordena la lista segun fecha..
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						Date fecha1 = (Date) o1[1];
						Date fecha2 = (Date) o2[1];
						return fecha1.compareTo(fecha2);
					}
				});
				
				double saldo = 0;
				
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String val1 = (String) o1[4];
						String val2 = (String) o2[4];			
						int compare = val1.compareTo(val2);
						if (compare == 0) {
							Date emision1 = (Date) o1[1];
							Date emision2 = (Date) o2[1];
				            return emision1.compareTo(emision2);
				        }
				        else {
				            return compare;
				        }
					}
				});
				
				String key = "";
				for (Object[] hist : historico) {
					String razonsocial = (String) hist[4];
					if(!key.equals(razonsocial)) saldo = 0;					
					boolean ent = ((String) hist[0]).startsWith("(+)");
					String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
					String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
					String numero = (String) hist[2];
					String concepto = ((String) hist[0]).replace("(+)", "");
					String entrada = ent ? Utiles.getNumberFormat(Double.parseDouble(hist[3] + "")) : "";
					String salida = ent ? "" : Utiles.getNumberFormat(Double.parseDouble(hist[3] + ""));
					saldo += ent ? Double.parseDouble(hist[3] + "") * -1 : Double.parseDouble(hist[3] + "");
					String saldo_ = Utiles.getNumberFormat(saldo);
					data.add(new Object[] { fecha, hora, numero, concepto, entrada, salida, saldo_, razonsocial, (Date) hist[1] });
					totalSaldo.put(razonsocial, saldo_);
					key = razonsocial;
				}
				
				String cli = proveedor != null ? proveedor.getRazonSocial() : "TODOS..";
				String sourceDetallado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET_DHS;
				String sourceConsolidado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_CONSOLIDADO_DHS;
				String source = consolidado? sourceConsolidado : sourceDetallado;
				String titulo = consolidado ? "SALDOS DE PROVEEDORES EXTERIOR RESUMIDO (A UNA FECHA)" : "SALDOS DE PROVEEDORES EXTERIOR DETALLADO (A UNA FECHA)";
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
				params.put("Titulo", titulo);
				params.put("Usuario", getUs().getNombre());
				params.put("Moneda", filtro.getMonedaDs());
				params.put("desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * TES-00050
		 */
		private void historialMovimientosClientes(boolean mobile, boolean consolidado, String codReporte) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Date desde = Utiles.getFechaInicioOperaciones();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				Cliente cliente = filtro.getCliente();
				boolean incluirChequesRechazados = filtro.isIncluirCHQ_RECH();
				boolean incluirPrestamos = filtro.isIncluirPRE();

				if (desde == null) desde = filtro.getFechaInicioOperaciones();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> historico;
				List<Object[]> historicoDEBE;
				List<Object[]> historicoHABER;
				Map<String, String> totalSaldo = new HashMap<String, String>();
				double totalVentas = 0;
				double totalChequesRechazados = 0;
				double totalNotasCredito = 0;
				double totalRecibos = 0;
				double totalNotasDebito = 0;
				double totalReembolsosCheques = 0;

				long idCliente = cliente != null ? cliente.getId() : 0;

				List<Object[]> ventas = rr.getVentasPorCliente_(idCliente, desde, hasta);
				List<Object[]> cheques_rechazados = rr.getChequesRechazadosPorCliente(idCliente, desde, hasta);
				List<Object[]> prestamos_cc = rr.getPrestamosCC(idCliente, desde, hasta);
				List<Object[]> ntcsv = rr.getNotasCreditoPorCliente(idCliente, desde, hasta);
				List<Object[]> recibos = rr.getRecibosPorCliente(idCliente, desde, hasta);
				List<Object[]> reembolsos_cheques_rechazados = rr.getReembolsosChequesRechazadosPorCliente(idCliente, desde, hasta);
				List<Object[]> reembolsos_prestamos_cc = rr.getReembolsosPrestamosCC(idCliente, desde, hasta);
				
				for (Object[] venta : ventas) {
					totalVentas += ((double) venta[3]);
				}
				
				for (Object[] chequeRech : cheques_rechazados) {
					totalChequesRechazados += ((double) chequeRech[3]);
				}
				
				for (Object[] ncred : ntcsv) {
					totalNotasCredito -= ((double) ncred[3]);
				}
				
				for (Object[] rec : recibos) {
					totalRecibos -= ((double) rec[3]);
				}
				
				for (Object[] reemb : reembolsos_cheques_rechazados) {
					totalReembolsosCheques -= ((double) reemb[3]);
				}

				historicoDEBE = new ArrayList<Object[]>();
				historicoHABER = new ArrayList<Object[]>();
				
				historicoDEBE.addAll(ventas);				
				if (incluirChequesRechazados) historicoDEBE.addAll(cheques_rechazados);
				if (incluirPrestamos) historicoDEBE.addAll(prestamos_cc);				
				
				historicoHABER.addAll(ntcsv);
				historicoHABER.addAll(recibos);
				if (incluirChequesRechazados) historicoHABER.addAll(reembolsos_cheques_rechazados);
				if (incluirPrestamos) historicoHABER.addAll(reembolsos_prestamos_cc);				

				for (Object[] movim : historicoDEBE) {
					movim[0] = "(+)" + movim[0];
				}

				historico = new ArrayList<Object[]>();
				historico.addAll(historicoDEBE);
				historico.addAll(historicoHABER);

				// ordena la lista segun fecha..
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						Date fecha1 = (Date) o1[1];
						Date fecha2 = (Date) o2[1];
						return fecha1.compareTo(fecha2);
					}
				});
				
				double saldo = 0;
				
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String val1 = (String) o1[4];
						String val2 = (String) o2[4];			
						int compare = val1.compareTo(val2);
						if (compare == 0) {
							Date emision1 = (Date) o1[1];
							Date emision2 = (Date) o2[1];
				            return emision1.compareTo(emision2);
				        }
				        else {
				            return compare;
				        }
					}
				});
				
				String key = "";
				for (Object[] hist : historico) {
					String razonsocial = (String) hist[4];
					if(!key.equals(razonsocial)) saldo = 0;					
					boolean ent = ((String) hist[0]).startsWith("(+)");
					String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
					String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
					String numero = (String) hist[2];
					String concepto = ((String) hist[0]).replace("(+)", "");
					String entrada = ent ? Utiles.getNumberFormat(Double.parseDouble(hist[3] + "")) : "";
					String salida = ent ? "" : Utiles.getNumberFormat(Double.parseDouble(hist[3] + ""));
					saldo += ent ? Double.parseDouble(hist[3] + "") :  Double.parseDouble(hist[3] + "") * -1;
					String saldo_ = Utiles.getNumberFormat(saldo);
					data.add(new Object[] { fecha, hora, numero, concepto, entrada, salida, saldo_, razonsocial, (Date) hist[1] });
					totalSaldo.put(razonsocial, saldo_);
					key = razonsocial;
				}
				
				String cli = cliente != null ? cliente.getRazonSocial() : "TODOS..";
				String sourceDetallado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET_DHS;
				String sourceConsolidado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_CONSOLIDADO_DHS;
				String source = consolidado? sourceConsolidado : sourceDetallado;
				String titulo = codReporte + " - " + (consolidado ? "SALDOS DE CLIENTES RESUMIDO (A UNA FECHA)" 
						: "SALDOS DE CLIENTES DETALLADO (HISTORIAL A UNA FECHA)");
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo, totalVentas,
						totalChequesRechazados, totalNotasCredito, totalRecibos, totalNotasDebito,
						totalReembolsosCheques);
				params.put("Titulo", titulo);
				params.put("Usuario", getUs().getNombre());
				params.put("Moneda", filtro.getMonedaGs());
				params.put("desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte TES-00052
		 */
		private void cobranzasVentasCobrador(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Funcionario> cobradores = filtro.getTeleCobradores();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Recibo> cobros = rr.getCobranzas(desde, hasta, 0, 0, true, true);
				List<Venta> ventas = rr.getVentasContado(desde, hasta, 0, 0);
				Map<Long, Double> values = new HashMap<Long, Double>();
				Map<Long, Double> values_ = new HashMap<Long, Double>();

				for (Recibo cobro : cobros) {
					Funcionario cobrador = cobro.getCliente().getCobrador();
					long idCobrador = cobrador != null ? cobrador.getId() : 0;
					Double total = values.get(idCobrador);
					if(total != null) {
						total += cobro.getTotalImporteGsSinIva();
					} else {
						total = cobro.getTotalImporteGsSinIva();
					}
					values.put(idCobrador, total);
				}
				
				for (Venta venta : ventas) {
					Funcionario cobrador = venta.getCliente().getCobrador();
					long idCobrador = cobrador != null ? cobrador.getId() : 0;
					Double total = values_.get(idCobrador);
					if(total != null) {
						total += (double) venta.getTotalImporteGsSinIva();
					} else {
						total = (double) venta.getTotalImporteGsSinIva();
					}
					values_.put(idCobrador, total);
				}
				
				Funcionario sin = new Funcionario();
				sin.setId((long) 0);
				cobradores.add(sin);
				for (Funcionario cobrador : cobradores) {
					Double cobrado = values.get(cobrador.getId());
					Double contado = values_.get(cobrador.getId());
					
					double cobrado_ = cobrado != null? cobrado : 0;
					double contado_ = contado != null? contado : 0;
					
					String razonsocial = cobrador.esNuevo() ? "SIN COBRADOR" : cobrador.getRazonSocial().toUpperCase();
					if (cobrado != null || contado != null) {
						data.add(new Object[] { razonsocial, (cobrado_ - Utiles.getIVA(cobrado_, 10)), contado_ });
					}						
				}

				ReporteTotalCobranzasVentasCobrador rep = new ReporteTotalCobranzasVentasCobrador(desde, hasta, "TODOS..");
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte TES-00053
		 */
		private void cobranzasVentasCobradorDetallado(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario cobrador = filtro.getCobrador();
				
				if (cobrador == null) {
					Clients.showNotification("DEBE SELECCIONAR UN COBRADOR..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Recibo> cobros = rr.getCobranzas(desde, hasta, 0, 0, true, true);
				List<Venta> ventas = rr.getVentasContado(desde, hasta, 0, 0);
				Map<Long, Double> values = new HashMap<Long, Double>();
				Map<Long, Double> values_ = new HashMap<Long, Double>();
				Map<Long, String> clientes = new HashMap<Long, String>();
				long idCobrador = cobrador.getId().longValue();

				for (Recibo cobro : cobros) {
					Funcionario _cobrador = cobro.getCliente().getCobrador();
					long _idCobrador = _cobrador != null ? _cobrador.getId().longValue() : 0;
					if (_idCobrador == idCobrador) {
						long idCliente = cobro.getCliente().getId();
						Double total = values.get(idCliente);
						if(total != null) {
							total += cobro.getTotalImporteGsSinIva();
						} else {
							total = cobro.getTotalImporteGsSinIva();
						}
						values.put(idCliente, total);
						clientes.put(idCliente, cobro.getCliente().getRazonSocial());
					}
				}
				
				for (Venta venta : ventas) {
					Funcionario _cobrador = venta.getCliente().getCobrador();
					long _idCobrador = _cobrador != null ? _cobrador.getId().longValue() : 0;
					
					if (_idCobrador == idCobrador) {
						long idCliente = venta.getCliente().getId();
						Double total = values_.get(idCliente);
						if(total != null) {
							total += (double) venta.getTotalImporteGsSinIva();
						} else {
							total = (double) venta.getTotalImporteGsSinIva();
						}
						values_.put(idCliente, total);
						clientes.put(idCliente, venta.getCliente().getRazonSocial());
					}
				}				

				for (Long idCliente : clientes.keySet()) {
					Double cobrado = values.get(idCliente);
					Double contado = values_.get(idCliente);
					
					double cobrado_ = cobrado != null? cobrado : 0;
					double contado_ = contado != null? contado : 0;
					
					if (cobrado != null || contado != null) {
						data.add(new Object[]{ clientes.get(idCliente),
								(cobrado_ - Utiles.getIVA(cobrado_, 10)), contado_ });
					}
				}				

				ReporteTotalCobranzasVentasCobrador rep = new ReporteTotalCobranzasVentasCobrador(desde, hasta, cobrador.getRazonSocial());
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * TES-00054
		 */
		private void saldosClientesResumido(boolean mobile, String codReporte) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				Cliente cliente = filtro.getCliente();
				// Tipo moneda = filtro.getMoneda();

				if (desde == null) desde = new Date();
				
				Map<String, Object[]> acum = new HashMap<String, Object[]>();
				List<Object[]> data = new ArrayList<Object[]>();
				
				double totalVentas = 0.0;
				double totalChequesRechazados = 0.0;
				double totalNotasCredito = 0.0;
				double totalRecibos = 0.0;
				double totalNotasDebito = 0.0;
				double totalChequesReembolsos = 0.0;
				double totalMigracion = 0.0;
				double totalMigracionChequesRechazados = 0.0;
				double totalAnticipos = 0.0;

				RegisterDomain rr = RegisterDomain.getInstance();

				long idCliente = cliente != null ? cliente.getId() : 0;
				// long idMoneda = moneda != null ? moneda.getId() : 0;

				List<Object[]> ventas = rr.getVentasCreditoPorCliente(desde, hasta, idCliente);
				List<Object[]> chequesRechazados = rr.getChequesRechazadosPorCliente(desde, hasta, idCliente);
				List<Object[]> ncreditos = rr.getNotasCreditoPorCliente(desde, hasta, idCliente);
				List<Object[]> recibos = rr.getRecibosPorCliente(desde, hasta, idCliente);
				List<Object[]> anticipos = rr.getAnticiposPorCliente(desde, hasta, idCliente);
				List<Object[]> ndebitos = rr.getNotasDebitoPorCliente(desde, hasta, idCliente);
				List<Object[]> reembolsos = rr.getReembolsosPorCliente(desde, hasta, idCliente);
				List<Object[]> migracion = rr.getCtaCteMigracionPorClienteVentasGs(desde, hasta, idCliente);
				List<Object[]> migracionChequesRechazados = rr.getCtaCteMigracionPorClienteChequesRechazados(desde, hasta, idCliente);
				
				for (Object[] venta : ventas) {
					String key = (String) venta[1];
					venta = Arrays.copyOf(venta, venta.length + 10);
					venta[3] = 0.0;
					venta[4] = 0.0;
					venta[5] = 0.0;
					venta[6] = 0.0;
					venta[7] = 0.0;
					venta[8] = 0.0;
					venta[9] = 0.0;
					venta[10] = 0.0;
					venta[11] = "";
					venta[12] = "";
					acum.put(key, venta);
				}
				
				for (Object[] cheque : chequesRechazados) {
					String key = (String) cheque[1];
					double importe = (double) cheque[2];
					Object[] obj = acum.get(key);
					if (obj != null) {
						obj[3] = importe;
					} else {
						obj = new Object[] { cheque[0], cheque[1], 0.0, cheque[2], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "", "" };
					}
					acum.put(key, obj);
				}
				
				for (Object[] ncred : ncreditos) {
					String key = (String) ncred[1];
					double importe = (double) ncred[2];
					Object[] obj = acum.get(key);
					if (obj != null) {
						obj[4] = importe * -1;
					} else {
						obj = new Object[] { ncred[0], ncred[1], 0.0, 0.0, importe * -1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "", "" };
					}
					acum.put(key, obj);
				}
				
				for (Object[] rec : recibos) {
					String key = (String) rec[1];
					double importe = (double) rec[2];
					Object[] obj = acum.get(key);
					if (obj != null) {
						obj[5] = importe * -1;
					} else {
						obj = new Object[] { rec[0], rec[1], 0.0, 0.0, 0.0, importe * -1, 0.0, 0.0, 0.0, 0.0, 0.0, "", "" };
					}
					acum.put(key, obj);
				}
				
				for (Object[] ndeb : ndebitos) {
					String key = (String) ndeb[1];
					double importe = (double) ndeb[2];
					Object[] obj = acum.get(key);
					if (obj != null) {
						obj[6] = importe;
					} else {
						obj = new Object[] { ndeb[0], ndeb[1], 0.0, 0.0, 0.0, 0.0, importe, 0.0, 0.0, 0.0, 0.0, "", "" };
					}
					acum.put(key, obj);
				}
				
				for (Object[] reemb : reembolsos) {
					String key = (String) reemb[1];
					double importe = (double) reemb[2];
					Object[] obj = acum.get(key);
					if (obj != null) {
						obj[7] = importe * -1;
					} else {
						obj = new Object[] { reemb[0], reemb[1], 0.0, 0.0, 0.0, 0.0, 0.0, importe * -1, 0.0, 0.0, 0.0, "", "" };
					}
					acum.put(key, obj);
				}
				
				for (Object[] mig : migracion) {
					String key = (String) mig[1];
					double importe = (double) mig[2];
					Object[] obj = acum.get(key);
					if (obj != null) {
						obj[8] = importe;
					} else {
						obj = new Object[] { mig[0], mig[1], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, importe, 0.0, 0.0, "", "" };
					}
					acum.put(key, obj);
				}
				
				for (Object[] mig : migracionChequesRechazados) {
					String key = (String) mig[1];
					double importe = (double) mig[2];
					Object[] obj = acum.get(key);
					if (obj != null) {
						obj[9] = importe;
					} else {
						obj = new Object[] { mig[0], mig[1], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, importe, 0.0, "", "" };
					}
					acum.put(key, obj);
				}
				
				for (Object[] rec : anticipos) {
					String key = (String) rec[1];
					double importe = (double) rec[2];
					Object[] obj = acum.get(key);
					if (obj != null) {
						obj[10] = importe * -1;
					} else {
						obj = new Object[] { rec[0], rec[1], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, importe * -1, "", "" };
					}
					acum.put(key, obj);
				}
				
				for (String key : acum.keySet()) {
					Object[] data_ = acum.get(key);
					double vtas = (double) data_[2];
					double rech = (double) data_[3];
					double ncrs = (double) data_[4];
					double recs = (double) data_[5];
					double ndeb = (double) data_[6];
					double reem = (double) data_[7];
					double migr = (double) data_[8];
					double mgcr = (double) data_[9];
					double anti = (double) data_[10];
					totalVentas += vtas;
					totalChequesRechazados += rech;
					totalNotasCredito += ncrs;
					totalRecibos += recs;
					totalNotasDebito += ndeb;
					totalChequesReembolsos += reem;
					totalMigracion += migr;
					totalMigracionChequesRechazados += mgcr;
					totalAnticipos += anti;
					data.add(data_);
				}
				
				Collections.sort(data, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String val1 = (String) o1[1];
						String val2 = (String) o2[1];
						int compare = val1.compareTo(val2);				
						return compare;
					}
				});	
				
				String cli = cliente != null ? cliente.getRazonSocial() : "TODOS..";
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_CLIENTES_RESUMIDO;
				if (!formato.equals(com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_PDF)) {
					source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_CLIENTES_RESUMIDO_;
				}
				String titulo = codReporte + " - SALDOS DE CLIENTES RESUMIDO (A UNA FECHA)";
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CtaCteSaldosResumidoDataSource(data, cli, totalVentas,
						totalChequesRechazados, totalNotasCredito, totalRecibos, totalNotasDebito,
						totalChequesReembolsos, totalMigracion, totalMigracionChequesRechazados, 0.0, totalAnticipos);
				params.put("Titulo", titulo);
				params.put("Usuario", getUs().getNombre());
				params.put("Moneda", filtro.getMonedaGs());
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * TES-00055
		 */
		private void saldosClientesProveedor(boolean mobile, String codReporte) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				
				if (vendedor == null) {
					Clients.showNotification("DEBE SELECCIONAR UN VENDEDOR..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> cobros = rr.getCobranzasPorVendedor(desde, hasta, vendedor.getId().longValue(), 0);
				double totalCobrado = 0;
				double totalCobradoSinIva = 0;
				Map<Long, Integer> prov_acum = new HashMap<Long, Integer>();
				Map<Long, List<Object[]>> _prov_acum = new HashMap<Long, List<Object[]>>();
				Map<Long, Double> values = new HashMap<Long, Double>();
				Map<Long, Double> values_ = new HashMap<Long, Double>();
				Map<Long, String> proveedores = new HashMap<Long, String>();

				for (Object[] cobro : cobros) {
					Recibo rec = (Recibo) cobro[0];
					totalCobrado += (double) cobro[2];
					ReciboDetalle item = (ReciboDetalle) cobro[3];
					Venta vta = item.getVenta();
					if (vta != null) {
						for (VentaDetalle det : vta.getDetalles()) {
							Proveedor prov = det.getArticulo().getProveedor();
							long idProveedor = prov != null ? prov.getId() : 0;
							Integer total = prov_acum.get(idProveedor);
							List<Object[]> list = null;
							if (total != null) {
								total ++;
								list = _prov_acum.get(idProveedor);
							} else {
								total = 1;
								list = new ArrayList<Object[]>();
							}
							double porcentaje = Utiles.obtenerPorcentajeDelValor(item.getMontoGs(), vta.getTotalImporteGs());
							double importe = Utiles.obtenerValorDelPorcentaje(det.getImporteGs(), porcentaje);
							double importeSinIva = Utiles.obtenerValorDelPorcentaje(det.getImporteGsSinIva(), porcentaje);
							list.add(new Object[] { rec.getNumero(), vta.getNumero(),
									det.getArticulo().getCodigoInterno(), importe, importeSinIva,
									vta.getCliente().getRazonSocial() });
							prov_acum.put(idProveedor, total);
							_prov_acum.put(idProveedor, list);
							Double acum = values.get(idProveedor);
							Double acum_ = values_.get(idProveedor);
							if (acum != null) {
								acum += importe;
								acum_ += importeSinIva;
							} else {
								acum = importe;
								acum_ = importeSinIva;
							}
							values.put(idProveedor, acum);
							values_.put(idProveedor, acum_);
							proveedores.put(idProveedor, prov != null ? prov.getRazonSocial() : "SIN PROVEEDOR");
						}
					} else {
						List<Object[]> dets = item.getDetalleVentaMigracion();
						for (Object[] det : dets) {
							Articulo art = rr.getArticulo((String) det[0]);
							if (art != null) {
								Proveedor prov = art.getProveedor();
								long idProveedor = prov != null ? prov.getId() : 0;
								Integer total = prov_acum.get(idProveedor);
								List<Object[]> list = null;
								if (total != null) {
									total ++;
									list = _prov_acum.get(idProveedor);
								} else {
									total = 1;
									list = new ArrayList<Object[]>();
								}
								double importe = (double) det[1];
								list.add(new Object[] { rec.getNumero(), "migracion", (String) det[0], importe,
										(importe - Utiles.getIVA(importe, 10)), "" });
								prov_acum.put(idProveedor, total);
								_prov_acum.put(idProveedor, list);
								proveedores.put(idProveedor, prov != null ? prov.getRazonSocial() : "SIN PROVEEDOR");
							}								
						}
					} 
				}	

				for (Long idProveedor : proveedores.keySet()) {
					Double cobrado = values.get(idProveedor);
					Double cobradoSinIva = values_.get(idProveedor);
					
					double cobrado_ = cobrado != null? cobrado : 0;
					List<Object[]> items = _prov_acum.get(idProveedor);
					
					if (cobrado != null) {
						totalCobrado += cobrado_;
						totalCobradoSinIva += cobradoSinIva;
						for (Object[] item : items) {
							data.add(new Object[] { proveedores.get(idProveedor), cobrado_, item, cobradoSinIva });
						}
					}
				}				

				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_COBRANZAS_VENDEDOR_DETALLADO;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CobranzasVendedorDetallado(data, totalCobrado, totalCobradoSinIva);
				params.put("Titulo", codReporte + " - Cobranzas por vendedor / proveedor detallado");
				params.put("Usuario", getUs().getNombre());
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("Vendedor", vendedor.getRazonSocial());
				imprimirJasper(source, params, dataSource, filtro.getFormato());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte TES-00056
		 */
		private void anticipos(boolean mobile, String codReporte) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				SucursalApp sucursal = filtro.getSelectedSucursal();
				Cliente cli = filtro.getCliente();
				long idSucursal = sucursal == null ? 0 : sucursal.getId();
				long idCliente = cli != null ? cli.getId() : 0;
				String sucursal_ = sucursal == null ? "TODOS.." : sucursal.getDescripcion();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Recibo> cobranzas = rr.getAnticipos(desde, hasta, idSucursal, idCliente);
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LISTADO_ANTICIPOS;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new ListadoCobranzasDataSource(cobranzas, desde, hasta, sucursal_, true, idSucursal);
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", codReporte + " - LISTADO DE ANTICIPOS");
				imprimirJasper(source, params, dataSource, formato);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte TES-00057
		 */
		private void cobranzasDetallado(boolean mobile, String codReporte) throws Exception {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = filtro.getFechaDesde();			
			Date hasta = filtro.getFechaHasta();	
			SucursalApp suc = filtro.getSelectedSucursal();
			Object[] formato = filtro.getFormato();	
			
			long idSucursal = suc != null ? suc.getId() : 0;			
			List<Recibo> recibos = rr.getCobranzas(desde, hasta, idSucursal, 0, false, false);
		
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_COBRANZAS_DETALLADO;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new CobranzasDetalladoDataSource(recibos, desde, hasta, true);
			params.put("Titulo", codReporte + " - COBRANZAS DETALLADO POR CARTERA");
			params.put("Usuario", getUs().getNombre());
			params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
			params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
			imprimirJasper(source, params, dataSource, formato);
		}
		
		/**
		 * reporte TES-00058
		 */
		private void reembolsosDetallado(boolean mobile, String codReporte) throws Exception {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = filtro.getFechaDesde();			
			Date hasta = filtro.getFechaHasta();	
			SucursalApp suc = filtro.getSelectedSucursal();
			Object[] formato = filtro.getFormato();	
			
			long idSucursal = suc != null ? suc.getId() : 0;
			
			List<Recibo> recibos = rr.getReembolsosCheques(desde, hasta, idSucursal, 0);
		
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_REEMBOLSOS_DETALLADO;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new CobranzasDetalladoDataSource(recibos, desde, hasta, false);
			params.put("Titulo", codReporte + " - REEMBOLSOS DE CHEQUES RECHAZADOS POR CARTERA");
			params.put("Usuario", getUs().getNombre());
			params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
			params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
			imprimirJasper(source, params, dataSource, formato);
		}
		
		/**
		 * TES-00059 Genera el reporte correspondiente a todos los cheques de
		 * terceros ya sea que se hayan efectivizados o depositados o no de
		 * acuerdo a los parametros optenidos desde el filtro que podrian ser:
		 * FechaDesde, FechaHasta, LibradoPor
		 */
		private void chequesPorClienteSegunEmision(boolean mobile, String codReporte) throws Exception {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Tipo banco = filtro.getBancoTercero();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<BancoChequeTercero> cheques = rr.getChequesTerceroEmision(desde, hasta, banco, 0);

				for (BancoChequeTercero cheque : cheques) {
					int length = cheque.getLibrado().length();
					int maxlength = length > 40 ? 40 : length;
					String librador = cheque.getLibrado() == null ? "---" : cheque.getLibrado().substring(0, maxlength);
					Object[] obj = new Object[] {
							m.dateToString(cheque.getEmision(), Utiles.DD_MM_YY),
							m.dateToString(cheque.getFecha(), Utiles.DD_MM_YY),
							cheque.getNumero(),
							cheque.getBanco().getDescripcion().toUpperCase(), librador.toUpperCase(),
							cheque.isDepositado() ? "SI" : "NO",
							cheque.isDescontado() ? "SI" : "NO",
							cheque.getMonto() };
					data.add(obj);
				}

				String sucursal = getAcceso().getSucursalOperativa().getText();
				String nroCheque = "TODOS..";
				String banco_ = banco == null ? "TODOS.." : banco.getDescripcion();
				String cliente_ = "TODOS..";
				ReporteChequesDeTerceros rep = new ReporteChequesDeTerceros(desde, hasta, sucursal, nroCheque, banco_, cliente_);
				rep.setDatosReporte(data);	
				rep.setTitulo(codReporte + " - Listado de Cheques de Clientes");
				rep.setApaisada();

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte TES-00060
		 */
		private void saldosPorFamilia(boolean mobile, String codReporte) throws Exception {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde_ = Utiles.getFecha("01-01-2015 00:00:00");			
			Date hasta_ = new Date();
			boolean incluirChequesRechazados = false;
			boolean incluirPrestamos = false;
			Funcionario vendedor = filtro.getVendedor();
			Cliente cliente_ = filtro.getCliente();
			EmpresaRubro rubro = filtro.getRubro_();
			Tipo moneda = filtro.getMoneda();
			
			if (moneda.esNuevo()) {
				Clients.showNotification("DEBE SELECCIONAR UNA MONEDA..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
			Object[] formato = filtro.getFormato();
			long idVendedor = vendedor == null ? 0 : vendedor.getId();
			long idEmpresa = cliente_ == null ? 0 : cliente_.getEmpresa().getId();
			long idRubro = rubro == null ? 0 : rubro.getId();
			String caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE;	
			List<Object[]> movims = new ArrayList<Object[]>();
			List<Object[]> movims_ = new ArrayList<Object[]>();		

			movims = rr.getSaldos(desde_, hasta_, caracter, idVendedor, idEmpresa, moneda.getId(),
					incluirChequesRechazados, incluirPrestamos, idRubro);	
			for (Object[] movim : movims) {
				Venta vta = rr.getMovimientoVenta((long)movim[1], (long)movim[0]);
				if (vta != null) {
					for (Object[] prorrateo : vta.getProrrateoFamilia()) {
						Object[] copy = Arrays.copyOf(movim, 17);
						copy[15] = prorrateo[0];
						copy[16] = prorrateo[1];
						copy[9] = Utiles.obtenerValorDelPorcentaje((double)copy[9], (double)copy[16]);
						movims_.add(copy);
					}
				}
			}
		
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_FAMILIA;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new CtaCteSaldosFamiliaDataSource(movims_);
			params.put("Titulo", codReporte + " - SALDOS DE CLIENTES DETALLADO POR FAMILIA");
			params.put("Usuario", getUs().getNombre());
			params.put("Vendedor", vendedor == null ? "TODOS.." : vendedor.getRazonSocial().toUpperCase());
			params.put("Moneda", moneda.getDescripcion().toUpperCase());
			imprimirJasper(source, params, dataSource, formato);
		}
		
		/**
		 * listado de pagos a proveedores..
		 */
		private void listadoPagos(String codReporte) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Proveedor prov = filtro.getProveedor();
				long idPrv = prov != null ? prov.getId() : 0; 

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Recibo> pagos = rr.getPagos(desde, hasta, idPrv);
				
				for (Recibo pago : pagos) {
					Object[] cmp = new Object[] {
							Utiles.getDateToString(pago.getFechaEmision(), Utiles.DD_MM_YY), 
							pago.getNumero(),
							pago.getNumeroRecibo(),
							Utiles.getDateToString(pago.getFechaRecibo(), Utiles.DD_MM_YY),
							pago.getProveedor().getRazonSocial(),
							Utiles.getRedondeo(pago.getTotalImporteGs()) };
					data.add(cmp);
				}						
				
				ReportePagos rep = new ReportePagos(desde, hasta, "");
				rep.setTitulo(codReporte + " - Listado de Pagos a proveedores");
				rep.setDatosReporte(data);
				rep.setApaisada();

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * listado de cheques por garantias recibidas..
		 */
		private void chequesGarantiasRecibidas(String codReporte) {
			try {				
				/**		
				String cuenta = "";
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Recibo> pagos = rr.getPagos(desde, hasta, idPrv);
				
				for (Recibo pago : pagos) {
					Object[] cmp = new Object[] {
							Utiles.getDateToString(pago.getFechaEmision(), Utiles.DD_MM_YY), 
							pago.getNumero(),
							pago.getNumeroRecibo(),
							Utiles.getDateToString(pago.getFechaRecibo(), Utiles.DD_MM_YY),
							pago.getProveedor().getRazonSocial(),
							Utiles.getRedondeo(pago.getTotalImporteGs()) };
					data.add(cmp);
				}						
				
				ReportePagos rep = new ReportePagos(desde, hasta, "");
				rep.setTitulo(codReporte + " - Listado de Pagos a proveedores");
				rep.setDatosReporte(data);
				rep.setApaisada();

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this); **/

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Reportes de rrhh..
	 */
	class ReportesRRHH {
		
		static final String MARCACIONES = "RRHH-00001";
		
		public ReportesRRHH(String codigoReporte) {
			switch (codigoReporte) {
			
			case MARCACIONES:
				this.listadoMarcaciones(MARCACIONES);
				break;
			}
		}
		
		/**
		 * marcaciones..
		 */
		private void listadoMarcaciones(String codigoReporte) {

			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				String funcionario = filtro.getFuncionarioMarcacion();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> marcaciones = rr.getMarcaciones(desde, hasta, funcionario);
				for (Object[] item : marcaciones) {
					Date fecha_ = Utiles.getFecha(((String) item[0]).replaceAll("entrada_", "")
							.replaceAll("salida_", "").replaceAll("interna_", "").replaceAll("interna", ""), "dd-MM-yyyy HH:mm:ss");
					String fecha = Utiles.getDateToString(fecha_, Utiles.DD_MM_YYYY);
					String tipo = this.getTipo((String) item[0]);
					String marcacion = Utiles.getDateToString(fecha_, "HH:mm:ss");
					String dia = Utiles.getDia(fecha_);
					
					if (tipo.equals("ENTRADA")) { 
						Date horario = Utiles.getFecha(fecha + " " + RRHHMarcaciones.ENTRADA, "dd-MM-yyyy HH:mm:ss");
						Object[] retraso_ = Utiles.diferenciaTiempo(fecha_, horario);
						String retraso = retraso_[0] + ":" + retraso_[1] + ":" + retraso_[2];
						if(((long)retraso_[0]) < 0 || ((long)retraso_[1]) < 0 || ((long)retraso_[2]) < 0) retraso = "";
						data.add(new Object[] { fecha, dia, tipo, marcacion, retraso, "", item[1] });

					} else if (tipo.equals("SALIDA")) {
						String key = dia.equals("SABADO") ? RRHHMarcaciones.SALIDA_SABADO : RRHHMarcaciones.SALIDA;
						Date horario = Utiles.getFecha(fecha + " " + key, "dd-MM-yyyy HH:mm:ss");
						Object[] adelanto_ = Utiles.diferenciaTiempo(horario, fecha_);
						String adelanto = adelanto_[0] + ":" + adelanto_[1] + ":" + adelanto_[2];
						if(((long)adelanto_[0]) < 0 || ((long)adelanto_[1]) < 0 || ((long)adelanto_[2]) < 0) adelanto = "";
						data.add(new Object[] { fecha, dia, tipo, marcacion, "", adelanto, item[1] });

					} else {
						data.add(new Object[] { fecha, dia, tipo, marcacion, "", "", item[1] });
					}
				}

				ReporteMarcaciones rep = new ReporteMarcaciones(desde, hasta, codigoReporte);
				rep.setApaisada();
				rep.setDatosReporte(data);			

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * @return el tipo
		 */
		private String getTipo(String descripcion) {
			String out = "";
			if (descripcion.startsWith("entrada")) {
				out = "ENTRADA";
			}
			if (descripcion.startsWith("entrada_interna")) {
				out = "ENTRADA INTERNA";
			}
			if (descripcion.startsWith("salida")) {
				out = "SALIDA";
			}
			if (descripcion.startsWith("salida_interna")) {
				out = "SALIDA INTERNA";
			}
			return out;
		}
	}
	

	/**
	 * Reportes de compras..
	 */
	class ReportesDeCompras {

		static final String LISTADO_COMPRAS = "COM-00001";
		static final String LISTADO_GASTOS = "COM-00002";
		static final String MATRIZ_REPOSICION_MERCADERIAS = "COM-00003";
		static final String ULTIMO_COSTO_ARTICULOS = "COM-00004";
		static final String MATRIZ_IMPORTACIONES = "COM-00005";
		static final String LISTADO_IMPORTACIONES = "COM-00006";
		static final String MOVIMIENTOS_ARTICULOS = "COM-00007";
		static final String MATRIZ_COMPRAS_LOCALES = "COM-00008";
		static final String COMPRAS_LOCALES_ARTICULO = "COM-00009";
		static final String DETALLE_COMPRAS_LOCALES = "COM-00010";

		/**
		 * procesamiento del reporte..
		 */
		public ReportesDeCompras(String codigoReporte) throws Exception {
			switch (codigoReporte) {

			case LISTADO_COMPRAS:
				this.listadoCompras();
				break;

			case LISTADO_GASTOS:
				this.listadoGastos();
				break;
			
			case MATRIZ_REPOSICION_MERCADERIAS:
				this.matrizReposicion(MATRIZ_REPOSICION_MERCADERIAS);
				break;
				
			case ULTIMO_COSTO_ARTICULOS:
				this.ultimoCostoArticulos();
				break;	
			
			case MATRIZ_IMPORTACIONES:
				this.matrizImportaciones();
				break;
				
			case LISTADO_IMPORTACIONES:
				this.listadoImportaciones(LISTADO_IMPORTACIONES);
				break;
				
			case MOVIMIENTOS_ARTICULOS:
				this.movimientosArticulos();
				break;
			
			case MATRIZ_COMPRAS_LOCALES:
				this.comprasArticuloMes(false, MATRIZ_COMPRAS_LOCALES);
				break;
				
			case COMPRAS_LOCALES_ARTICULO:
				this.comprasLocalesPorArticulo(COMPRAS_LOCALES_ARTICULO);
				break;
				
			case DETALLE_COMPRAS_LOCALES:
				this.detalleComprasLocales(DETALLE_COMPRAS_LOCALES);
				break;
			}
		}

		/**
		 * listado de facturas de compras..
		 */
		private void listadoCompras() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				SucursalApp suc = filtro.getSelectedSucursal();
				Proveedor prov = filtro.getProveedor();
				CondicionPago cond = filtro.getCondicion();
				boolean incluirNC = filtro.isIncluirNCR();
				long idSuc = suc != null ? suc.getId() : 0;
				long idPrv = prov != null ? prov.getId() : 0; 
				long idCon = cond != null ? cond.getId() : 0; 

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<CompraLocalFactura> compras = rr.getComprasLocales(desde, hasta, idSuc, idPrv, idCon);
				List<NotaCredito> ncreditos = rr.getNotasCreditoCompra(desde, hasta, idSuc);

				for (CompraLocalFactura compra : compras) {
					Object[] cmp = new Object[] {
							m.dateToString(compra.getFechaOriginal(),
									Utiles.DD_MM_YY), compra.getNumero(),
							compra.getCondicionPago().getDescripcion().toUpperCase().substring(0, 3),
							compra.getSucursal().getDescripcion(),
							compra.getProveedor().getRazonSocial(),
							Utiles.getRedondeo(compra.getImporteGs()) };
					data.add(cmp);
				}
				
				if (incluirNC) {
					for (NotaCredito nc : ncreditos) {
						if (nc.getCompraAplicada() != null) {
							Object[] cmp = new Object[] {
									m.dateToString(nc.getFechaEmision(),
											Utiles.DD_MM_YY), nc.getNumero(),
									"NC-" + nc.getCompraAplicada().getCondicionPago().getDescripcion().toUpperCase().substring(0, 3),
									nc.getSucursal().getDescripcion(),
									nc.getProveedor().getRazonSocial(),
									Utiles.getRedondeo(nc.getImporteGs() * -1) };
							data.add(cmp);
						}					
					}
				}

				ReporteComprasGenerico rep = new ReporteComprasGenerico(desde, hasta);
				rep.setDatosReporte(data);			

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * listado de gastos..
		 */
		private void listadoGastos() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Gasto> gastos = rr.getGastos(desde, hasta);

				for (Gasto gasto : gastos) {
					Object[] cmp = new Object[] {
							m.dateToString(gasto.getFecha(), Misc.DD_MM_YYYY),
							gasto.getNumeroFactura(),
							gasto.getProveedor().getRazonSocial(),
							gasto.getProveedor().getRuc(), gasto.getImporteGs() };
					data.add(cmp);
				}

				ReporteGastosGenerico rep = new ReporteGastosGenerico(desde,
						hasta);
				rep.setDatosReporte(data);
				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * matriz para reposicion de mercaderias..		
		 */
		private void matrizReposicion(String codReporte) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Proveedor proveedor = filtro.getProveedorExterior();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();
				
				if (proveedor == null) {
					Clients.showNotification("Debe seleccionar un proveedor..", 
							Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> ventas = rr.getVentasDetallado_(desde, hasta, 0, 0, proveedor.getId(), 0, 0, "");
				List<Object[]> ncreds = rr.getNotasCreditoDetallado_(desde, hasta, 0, 0, proveedor.getId(), 0, 0, "", Configuracion.SIGLA_TIPO_NC_MOTIVO_DEVOLUCION);
				List<Object[]> data = new ArrayList<Object[]>();
				Map<String, Object[]> values = new HashMap<String, Object[]>();
				Map<String, Object[]> values_ = new HashMap<String, Object[]>();
				
				for (Object[] venta : ventas) {
					int mes = Utiles.getNumeroMes((Date) venta[4]) - 1;
					String cod = (String) venta[1];
					String key = cod + "-" + mes;
					long idArticulo = (long) venta[0];
					Object[] acum = values.get(key);					
					if (acum != null) {
						double cant = (double) acum[0];
						cant += ((double) venta[3]);
						values.put(key, new Object[] { cant, mes, cod, idArticulo, cod });
					} else {
						values.put(key, new Object[] { (double) venta[3], mes, cod, idArticulo, cod });
					}				
				}
				
				for (Object[] ncr : ncreds) {
					int mes = Utiles.getNumeroMes((Date) ncr[4]) - 1;
					String cod = (String) ncr[1];
					String key = cod + "-" + mes;
					long idArticulo = (long) ncr[0];
					Object[] acum = values.get(key);
					if (acum != null) {
						double cant = (double) acum[0];
						cant -= ((double) ncr[3]);
						values.put(key, new Object[] { cant, mes, cod, idArticulo });
					} else {
						values.put(key, new Object[] { (((double) ncr[3]) * -1), mes, cod, idArticulo });
					}				
				}
				
				for (String key : values.keySet()) {
					Object[] value = values.get(key);
					String cod = (String) value[2];
					int mes = (int) value[1];
					double cant = (double) value[0];
					long stock = rr.getStockArticulo((long) value[3]);
					double costo = (double) rr.getCostoPrecio(cod)[1];
					Object[] value_ = values_.get(cod);
					if (value_ != null) {
						value_[mes] = cant;
						values_.put(cod, value_);
					} else {
						Object[] datos = new Object[] { (double) 0, (double) 0,
								(double) 0, (double) 0, (double) 0, (double) 0,
								(double) 0, (double) 0, (double) 0, (double) 0,
								(double) 0, (double) 0, stock, costo };
						datos[mes] = cant;
						values_.put(cod, datos);
					}
				}
				
				for (String key : values_.keySet()) {
					Object[] value_ = values_.get(key);
					List<Double> list = new ArrayList<Double>();
					list.add((Double) value_[0]);
					list.add((Double) value_[1]);
					list.add((Double) value_[2]);
					list.add((Double) value_[3]);
					list.add((Double) value_[4]);
					list.add((Double) value_[5]);
					list.add((Double) value_[6]);
					list.add((Double) value_[7]);
					list.add((Double) value_[8]);
					list.add((Double) value_[9]);
					list.add((Double) value_[10]);
					list.add((Double) value_[11]);
					double max = Collections.max(list);
					double sum = 0;
					int size = (Utiles.getNumeroMes(hasta) + 1) - Utiles.getNumeroMes(desde);
					for (Double value : list) {
						sum += value;
					}
					double prom = sum / size;
					data.add(new Object[] { key, 0.0, value_[13], value_[0],
							value_[1], value_[2], value_[3], value_[4],
							value_[5], value_[6], value_[7], value_[8],
							value_[9], value_[10], value_[11], value_[12],
							max, prom });
				}
				
				Collections.sort(data, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String cod1 = (String) o1[0];
						String cod2 = (String) o2[0];
						int compare = cod1.compareTo(cod2);				
						return compare;
					}
				});	
				
				String proveedor_ = proveedor.getRazonSocial();
				String titulo = codReporte + "-" + ReporteMatrizReposicion.MATRIZ_REPOSICION;
				ReporteMatrizReposicion rep = new ReporteMatrizReposicion(desde, hasta, proveedor_, getSucursal(), titulo);
				rep.setDatosReporte(data);
				rep.setApaisada();			

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * COM-00004
		 */
		private void ultimoCostoArticulos() {
			try {
				Articulo art = filtro.getArticulo();
				ArticuloListaPrecio lprecio = filtro.getListaPrecio();
				long idlprecio = 0;
				int margen = 0;
				
				if (lprecio != null) {
					margen = lprecio.getMargen();
					idlprecio = lprecio.getId();
				}

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				
				List<Articulo> articulos = null;
				
				if (art == null) {
					articulos = rr.getArticulos_();
					for (Articulo articulo : articulos) {
						Object[] ac = rr.getUltimoCosto(articulo.getId());
						if (ac != null) {
							String codigo = articulo.getCodigoInterno();
							double costo = (double) ac[2];
							double precio = 0.0;
							double precioSinIva = 0.0;
							double ganancia = 0.0;
							double rentabilidad = 0.0;
							ArticuloListaPrecioDetalle det = rr.getListaPrecioDetalle(idlprecio, codigo);
							if (det != null) {
								precio = det.getPrecioGs_contado();
								precioSinIva = precio - Utiles.getIVA(precio, Configuracion.VALOR_IVA_10);
							} else {
								precio = ControlArticuloCosto.getPrecioVenta(costo, margen);
								precioSinIva = precio - Utiles.getIVA(precio, Configuracion.VALOR_IVA_10);
							}	
							ganancia = (precioSinIva - costo);
							rentabilidad = Utiles.obtenerPorcentajeDelValor(ganancia, costo);
							data.add(new Object[] {
									codigo,
									Utiles.getDateToString((Date) ac[0], Utiles.DD_MM_YYYY),
									ac[4], ac[1],
									Utiles.getRedondeo(costo), 
									Utiles.getRedondeo(precioSinIva),
									precio, rentabilidad });
						}
					}
				} else {
					Object[] ac = rr.getUltimoCosto(art.getId());
					if (ac != null) {
						String codigo = art.getCodigoInterno();
						double costo = (double) ac[2];
						double precio = 0.0;
						double precioSinIva = 0.0;
						double ganancia = 0.0;
						double rentabilidad = 0.0;
						ArticuloListaPrecioDetalle det = rr.getListaPrecioDetalle(idlprecio, codigo);
						if (det != null) {
							precio = det.getPrecioGs_contado();
							precioSinIva = precio - Utiles.getIVA(precio, Configuracion.VALOR_IVA_10);
						} else {
							precio = ControlArticuloCosto.getPrecioVenta(costo, margen);
							precioSinIva = precio - Utiles.getIVA(precio, Configuracion.VALOR_IVA_10);
						}
						ganancia = (precioSinIva - costo);
						rentabilidad = Utiles.obtenerPorcentajeDelValor(ganancia, costo);
						data.add(new Object[] {
								codigo,
								Utiles.getDateToString((Date) ac[0], Utiles.DD_MM_YYYY),
								ac[4], ac[1],
								Utiles.getRedondeo(costo), 
								Utiles.getRedondeo(precioSinIva),
								precio, rentabilidad });
					}
				}						
				
				String sucursal = getAcceso().getSucursalOperativa().getText();
				String articulo = art == null ? "TODOS.." : art.getDescripcion();
				String listaPrecio = lprecio == null ? "- - -" : lprecio.getDescripcion();

				ReporteCostoFinalPorArticulo rep = new ReporteCostoFinalPorArticulo(sucursal, articulo, listaPrecio);
				rep.setDatosReporte(data);
				
				rep.setApaisada();

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * matriz de importaciones..
		 */
		private void matrizImportaciones() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Proveedor proveedor = filtro.getProveedorExterior();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();
				
				if (!Utiles.getDateToString(desde, "yyyy").equals(Utiles.getDateToString(hasta, "yyyy"))) {
					Clients.showNotification("Rango permitido 1(un) año..", 
							Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				if (proveedor == null) {
					Clients.showNotification("Debe seleccionar un proveedor..", 
							Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<ImportacionPedidoCompra> imps = rr.getImportaciones_(desde, hasta, proveedor.getId());
				List<Object[]> data = new ArrayList<Object[]>();
				Map<String, Object[]> values = new HashMap<String, Object[]>();
				Map<String, Object[]> values_ = new HashMap<String, Object[]>();
				
				for (ImportacionPedidoCompra imp : imps) {
					int mes = Utiles.getNumeroMes(imp.getFechaCreacion());
					
					for (ImportacionFacturaDetalle item : imp.getImportacionFactura_().get(0).getDetalles()) {
						String cod = item.getArticulo().getCodigoInterno();
						String key = cod + "-" + mes;
						Object[] acum = values.get(key);
						Object[] datos = rr.getStockCosto(item.getArticulo().getId());
						long stock = (long) datos[0];
						double costo = (double) datos[1];
						if (acum != null) {
							long cant = (long) acum[0];
							cant += item.getCantidad_();
							values.put(key, new Object[] { cant, mes, cod, stock, costo });
						} else {
							values.put(key,	new Object[] { item.getCantidad_(), mes, cod, stock, costo });
						}																		
					}				
				}
				
				for (String key : values.keySet()) {
					Object[] value = values.get(key);
					String cod = (String) value[2];
					int mes = (int) value[1] - 1;
					long cant = (long) value[0];
					long stock = (long) value[3];
					double costo = (double) value[4];
					Object[] value_ = values_.get(cod);
					if (value_ != null) {
						value_[mes] = cant;
						values_.put(cod, value_);
					} else {
						Object[] datos = new Object[] { (long) 0, (long) 0,
								(long) 0, (long) 0, (long) 0, (long) 0,
								(long) 0, (long) 0, (long) 0, (long) 0,
								(long) 0, (long) 0, stock, costo };
						datos[mes] = cant;
						values_.put(cod, datos);
					}
				}
				
				for (String key : values_.keySet()) {
					Object[] value_ = values_.get(key);
					List<Long> list = new ArrayList<Long>();
					list.add((Long) value_[0]);
					list.add((Long) value_[1]);
					list.add((Long) value_[2]);
					list.add((Long) value_[3]);
					list.add((Long) value_[4]);
					list.add((Long) value_[5]);
					list.add((Long) value_[6]);
					list.add((Long) value_[7]);
					list.add((Long) value_[8]);
					list.add((Long) value_[9]);
					list.add((Long) value_[10]);
					list.add((Long) value_[11]);
					long max = Collections.max(list);
					long sum = 0;
					int size = Utiles.getNumeroMeses(desde, hasta) + 1;
					for (Long value : list) {
						sum += value;
					}
					long prom = sum / size;
					data.add(new Object[] { key, 0.0, value_[13], value_[0],
							value_[1], value_[2], value_[3], value_[4],
							value_[5], value_[6], value_[7], value_[8],
							value_[9], value_[10], value_[11], value_[12],
							max, prom });
				}
				
				String proveedor_ = proveedor.getRazonSocial();
				String titulo = ReporteMatrizReposicion.MATRIZ_IMPORTACIONES;
				ReporteMatrizReposicion rep = new ReporteMatrizReposicion(desde, hasta, proveedor_, getSucursal(), titulo);
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * listado de facturas de compras..
		 */
		private void listadoImportaciones(String codReporte) {
			try {
				Object[] formato = filtro.getFormato();
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Proveedor prov = filtro.getProveedorExterior();
				long idProv = prov == null? 0 : prov.getId();
				boolean fechaDespacho = filtro.isFraccionado();
				boolean fechaFactura = filtro.isTodos();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<ImportacionPedidoCompra> compras = new ArrayList<>();
				if (fechaDespacho) {
					compras = rr.getImportacionesByFechaDespacho(desde, hasta, idProv);					
				} else if (fechaFactura) {
					compras = rr.getImportacionesByFechaFactura(desde, hasta, idProv);
				}

				String proveedor = prov == null? "TODOS.." : prov.getRazonSocial();
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LISTADO_IMPORTACIONES;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new ListadoImportacionesDataSource(compras, desde, hasta, proveedor, fechaFactura);
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", codReporte + " - LISTADO DE IMPORTACIONES");
				imprimirJasper(source, params, dataSource, formato);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * movimientos por articulo..
		 */
		private void movimientosArticulos() {
			try {					
				Proveedor proveedor_ = filtro.getProveedorExterior() != null ? filtro.getProveedorExterior() : filtro.getProveedorLocal();
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				ArticuloFamilia familia_ = filtro.getFamilia_();
				boolean todos = filtro.isTodos();
				long idProveedor = proveedor_ != null ? proveedor_.getId() : 0;
				long idFamilia = familia_ != null ? familia_.getId() : 0;
				
				Date desdeCli = Utiles.getFechaInicioMes((int) filtro.getSelectedMes().getPos1());
				Date hastaCli = Utiles.getFechaFinMes((int) filtro.getSelectedMes().getPos1());
				
				if (proveedor_ == null) {
					Clients.showNotification("Debe seleccionar un Proveedor..", 
							Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				RegisterDomain rr = RegisterDomain.getInstance();				
				List<Object[]> ventas = rr.getVentasDetallado(desde, hasta, idProveedor, idFamilia);
				List<Object[]> ventasCantCli = rr.getVentasDetallado(desdeCli, hastaCli, idProveedor, idFamilia);
				List<Object[]> notasCredito = rr.getNotasCreditoDetallado(desde, hasta, idProveedor, idFamilia);
				List<Object[]> articulos = new ArrayList<Object[]>();
				
				List<Object[]> values = new ArrayList<Object[]>();
				List<HistoricoMovimientoArticulo> list = new ArrayList<HistoricoMovimientoArticulo>();
				
				Map<Long, Long> arts = new HashMap<Long, Long>();
				Map<Long, Long> arts_ = new HashMap<Long, Long>();
				Map<String, Integer> cants = new HashMap<String, Integer>();
				Map<Long, Object[]> stock1 = new HashMap<Long, Object[]>();
				Map<Long, Object[]> stock2 = new HashMap<Long, Object[]>();
				Map<Long, Object[]> stock3 = new HashMap<Long, Object[]>();
				Map<Long, Object[]> stock4 = new HashMap<Long, Object[]>();
				Map<Long, Object[]> stock5 = new HashMap<Long, Object[]>();
				Map<Long, Object[]> stock6 = new HashMap<Long, Object[]>();
				Map<Long, Object[]> stock7 = new HashMap<Long, Object[]>();
				Map<Long, Object[]> stock8 = new HashMap<Long, Object[]>();
				Map<Long, Object[]> stock9 = new HashMap<Long, Object[]>();
				Map<Long, Object[]> stock10 = new HashMap<Long, Object[]>();
				Map<Integer, String> deps = new HashMap<Integer, String>();
				
				Map<String, Integer> cantClientes = new HashMap<String, Integer>();
				Map<String, Integer> cantClientes_ = new HashMap<String, Integer>();
				
				Map<String, Integer> cantClientesVig = new HashMap<String, Integer>();
				Map<String, Integer> cantClientesVig_ = new HashMap<String, Integer>();
				
				for (Object[] venta : ventas) {
					int mes = Utiles.getNumeroMes((Date) venta[19]);
					String cod = (String) venta[1];
					String key = cod + "-" + mes;
					String keyCli = cod + "-" + venta[24];
					
					Integer acum = cants.get(key);
					if (acum != null) {
						acum += ((Long) venta[18]).intValue();
						cants.put(key, acum);
					} else {
						cants.put(key, ((Long) venta[18]).intValue());
					}
					
					Integer acumCliente = cantClientes.get(keyCli);
					if (acumCliente == null) {
						cantClientes.put(keyCli, 1);
						Integer acumCliente_ = cantClientes_.get(cod);
						if (acumCliente_ != null) {
							acumCliente_ ++;
							cantClientes_.put(cod, acumCliente_);
						} else {
							cantClientes_.put(cod, 1);
						}
					}
					
					arts.put((long) venta[0], (long) venta[0]);
					arts_.put((long) venta[0], (long) venta[0]);
				}
				
				for (Object[] venta : ventasCantCli) {
					String cod = (String) venta[1];
					String keyCli = cod + "-" + venta[24];
					
					Integer acumCliente = cantClientesVig.get(keyCli);
					if (acumCliente == null) {
						cantClientesVig.put(keyCli, 1);
						Integer acumCliente_ = cantClientesVig_.get(cod);
						if (acumCliente_ != null) {
							acumCliente_ ++;
							cantClientesVig_.put(cod, acumCliente_);
						} else {
							cantClientesVig_.put(cod, 1);
						}
					}
				}
				
				for (Object[] ncred : notasCredito) {
					int mes = Utiles.getNumeroMes((Date) ncred[19]);
					String cod = (String) ncred[1];
					String key = cod + "-" + mes;
					Integer acum = cants.get(key);
					if (acum != null) {
						acum -= ((Long) ncred[18]).intValue();
						cants.put(key, acum);
					} else {
						cants.put(key, (((Long) ncred[18]).intValue() * -1));
					}
				}
				
				if (todos) {	
					articulos = rr.getArticulos(idProveedor, idFamilia);
					for (Object[] art : articulos) {
						arts.put((long) art[0], (long) art[0]);
					}
				}
				
				for (Long idArticulo : arts.keySet()) {
					Object[] empty = new Object[] { 0, (long) 0, "" };
					long id1 = isEmpresaBaterias() ? Deposito.ID_DEPOSITO_PRINCIPAL : Deposito.ID_MINORISTA;
					long id2 = isEmpresaBaterias() ? Deposito.ID_DEPOSITO_AUXILIO : Deposito.ID_CENTRAL_TEMPORAL;
					long id3 = isEmpresaBaterias() ? Deposito.ID_DEPOSITO_AVERIADOS : Deposito.ID_CENTRAL_RECLAMOS;
					long id4 = isEmpresaBaterias() ? Deposito.ID_DEPOSITO_CONTROL : Deposito.ID_CENTRAL_REPOSICION;
					long id5 = isEmpresaBaterias() ? Deposito.ID_DEPOSITO_FALLADOS : Deposito.ID_MCAL_LOPEZ;
					long id6 = isEmpresaBaterias() ? Deposito.ID_DEPOSITO_PRODUCCION : Deposito.ID_MCAL_TEMPORAL;
					long id7 = isEmpresaBaterias() ? Deposito.ID_DEPOSITO_RECLAMOS : Deposito.ID_MAYORISTA;
					long id8 = isEmpresaBaterias() ? Deposito.ID_DEPOSITO_SECAS : Deposito.ID_MAYORISTA_TEMPORAL;
					long id9 = isEmpresaBaterias() ? Deposito.ID_DEPOSITO_TRANSITORIO : Deposito.ID_MAYORISTA_CENTRAL;
					long id10 = isEmpresaBaterias() ? Deposito.ID_DEPOSITO_SC1 : Deposito.ID_MRA;
					Deposito d1 = (Deposito) rr.getObject(Deposito.class.getName(), id1);
					Deposito d2 = (Deposito) rr.getObject(Deposito.class.getName(), id2);
					Deposito d3 = (Deposito) rr.getObject(Deposito.class.getName(), id3);
					Deposito d4 = (Deposito) rr.getObject(Deposito.class.getName(), id4);
					Deposito d5 = (Deposito) rr.getObject(Deposito.class.getName(), id5);
					Deposito d6 = (Deposito) rr.getObject(Deposito.class.getName(), id6);
					Deposito d7 = (Deposito) rr.getObject(Deposito.class.getName(), id7);
					Deposito d8 = (Deposito) rr.getObject(Deposito.class.getName(), id8);
					Deposito d9 = (Deposito) rr.getObject(Deposito.class.getName(), id9);
					Deposito d10 = (Deposito) rr.getObject(Deposito.class.getName(), id10);
					Object[] st1 = d1 != null ? rr.getStockArticulo(idArticulo, d1.getId().longValue()) : empty;
					Object[] st2 = d2 != null ? rr.getStockArticulo(idArticulo, d2.getId().longValue()) : empty;
					Object[] st3 = d3 != null ? rr.getStockArticulo(idArticulo, d3.getId().longValue()) : empty;
					Object[] st4 = d4 != null ? rr.getStockArticulo(idArticulo, d4.getId().longValue()) : empty;
					Object[] st5 = d5 != null ? rr.getStockArticulo(idArticulo, d5.getId().longValue()) : empty;
					Object[] st6 = d6 != null ? rr.getStockArticulo(idArticulo, d6.getId().longValue()) : empty;
					Object[] st7 = d7 != null ? rr.getStockArticulo(idArticulo, d7.getId().longValue()) : empty;
					Object[] st8 = d8 != null ? rr.getStockArticulo(idArticulo, d8.getId().longValue()) : empty;
					Object[] st9 = d9 != null ? rr.getStockArticulo(idArticulo, d9.getId().longValue()) : empty;
					Object[] st10 = d10 != null ? rr.getStockArticulo(idArticulo, d10.getId().longValue()) : empty;
					stock1.put(idArticulo, st1); deps.put(1, d1 != null ? d1.getObservacion() : "NO DEF.");
					stock2.put(idArticulo, st2); deps.put(2, d2 != null ? d2.getObservacion() : "NO DEF.");
					stock3.put(idArticulo, st3); deps.put(3, d3 != null ? d3.getObservacion() : "NO DEF.");
					stock4.put(idArticulo, st4); deps.put(4, d4 != null ? d4.getObservacion() : "NO DEF.");
					stock5.put(idArticulo, st5); deps.put(5, d5 != null ? d5.getObservacion() : "NO DEF.");
					stock6.put(idArticulo, st6); deps.put(6, d6 != null ? d6.getObservacion() : "NO DEF.");
					stock7.put(idArticulo, st7); deps.put(7, d7 != null ? d7.getObservacion() : "NO DEF.");
					stock8.put(idArticulo, st8); deps.put(8, d8 != null ? d8.getObservacion() : "NO DEF.");
					stock9.put(idArticulo, st9); deps.put(9, d9 != null ? d9.getObservacion() : "NO DEF.");
					stock10.put(idArticulo, st10); deps.put(10, d10 != null ? d10.getObservacion() : "NO DEF.");
				}
				
				for (Object[] venta : ventas) {
					Object[] compraLocal = rr.getUltimaCompraLocal((long) venta[0]);
					Object[] compraImpor = rr.getUltimaCompraImportacion((long) venta[0]);
					
					if (compraLocal == null && compraImpor == null) {
						compraLocal = rr.getUltimaCompraLocalMovimientosArticulos((String) venta[1]);
					}
					
					if (compraLocal == null) compraLocal = new Object[] { 0, null, null, (double) 0.0, (double) 0.0 };
					if (compraImpor == null) compraImpor = new Object[] { 0, null, null, (double) 0.0, (double) 0.0 };
					
					venta = Arrays.copyOf(venta, venta.length + 5);

					Date fcl = null;
					if (compraLocal[1] instanceof Date) {
						fcl = (Date) compraLocal[1];
					}
					Date fcI = (Date) compraImpor[1];
					if (fcI == null || (fcl != null && fcl.compareTo(fcI) >= 0)) {
						venta[30] = compraLocal[0];
						venta[31] = compraLocal[1];
						venta[32] = compraLocal[2];
						venta[33] = compraLocal[3];
						venta[34] = compraLocal[4];
					} else {
						venta[30] = compraImpor[0];
						venta[31] = compraImpor[1];
						venta[32] = compraImpor[2];
						venta[33] = compraImpor[3];
						venta[34] = compraImpor[4];
					}
								
					values.add(venta);
				}
				
				if (todos) {
					for (Object[] art : articulos) {
						long idArt = (long) art[0];
						if (arts_.get(idArt) == null) {
							Object[] ultCompra = rr.getUltimaCompraLocalMovimientosArticulos((String) art[1]);
							values.add(new Object[] { art[0], art[1], art[2], art[3], art[4], art[5], art[6], art[7],
									art[8], art[9], art[10], art[11], art[12], art[13], art[14], art[15], art[16], art[17],
									null, null, null, art[18], art[19], art[20], (long) 0, art[21], art[22], art[23], art[24], 
									art[25], ultCompra[0], ultCompra[1], ultCompra[2], ultCompra[3], ultCompra[4] });
						}
					}
				}
				
				for (Object[] det : values) {				
					String cod = (String) det[1];
					String codProveedor = (String) det[2];
					String referencia = (String) det[3];
					String codigoOriginal = (String) det[4];
					String descripcion = (String) det[6];
					String ochentaVeinte = (String) det[7];
					String abc = (String) det[8];
					String familia = (String) det[9];
					String marca = (String) det[10];
					String linea = (String) det[11];
					String grupo = (String) det[12];
					String aplicacion = (String) det[13];
					String modelo = (String) det[14];
					String peso = Utiles.getNumberFormat((double) det[15]);
					String volumen = Utiles.getNumberFormat((double) det[16]);
					String proveedor = (String) det[17];
					int maximo = (int) det[21];
					int minimo = (int) det[22];
					String subGrupo = (String) det[25];
					String parte = (String) det[26];
					String subMarca = (String) det[27];
					int unidadesPorCaja = (int) det[28];
					String procedencia = (String) det[29];
					Integer cantCliente = cantClientes_.get(cod);
					if (cantCliente == null) cantCliente = 0;
					Integer cantClienteVig = cantClientesVig_.get(cod);
					if (cantClienteVig == null) cantClienteVig = 0;
					String cantidad = det[30] + "";
					String fechaUltimaCompra = det[31] + "";
					if (det[31] instanceof Date) Utiles.getDateToString((Date) det[31], Utiles.DD_MM_YYYY);
					String proveedoUltimaCompra = (String) det[32];
					double costoFobGs = (double) det[33];
					double costoFobDs = (double) det[34];
					
					Object[] st = stock1.get(det[0]);
					String dep_1 = st != null ? st[1] + "" : "0";
					
					Object[] st2 = stock2.get(det[0]);
					String dep_2 = st2 != null ? st2[1] + "" : "0";
					
					Object[] st3 = stock3.get(det[0]);
					String dep_3 = st3 != null ? st3[1] + "" : "0";
					
					Object[] st4 = stock4.get(det[0]);
					String dep_4 = st4 != null ? st4[1] + "" : "0";	
					
					Object[] st5 = stock5.get(det[0]);
					String dep_5 = st5 != null ? st5[1] + "" : "0";	
					
					Object[] st6 = stock6.get(det[0]);
					String dep_6 = st6 != null ? st6[1] + "" : "0";
					
					Object[] st7 = stock7.get(det[0]);
					String dep_7 = st7 != null ? st7[1] + "" : "0";	

					Object[] st8 = stock8.get(det[0]);
					String dep_8 = st8 != null ? st8[1] + "" : "0";
					
					Object[] st9 = stock9.get(det[0]);
					String dep_9 = st9 != null ? st9[1] + "" : "0";
					
					Object[] st10 = stock10.get(det[0]);
					String dep_10 = st10 != null ? st10[1] + "" : "0";
					
					Integer cantEnero = cants.get(cod + "-1");
					String enero = cantEnero != null ? cantEnero + "" : "0";
					
					Integer cantFebrero = cants.get(cod + "-2");
					String febrero = cantFebrero != null ? cantFebrero + "" : "0";
					
					Integer cantMarzo = cants.get(cod + "-3");
					String marzo = cantMarzo != null ? cantMarzo + "" : "0";
					
					Integer cantAbril = cants.get(cod + "-4");
					String abril = cantAbril != null ? cantAbril + "" : "0";
					
					Integer cantMayo = cants.get(cod + "-5");
					String mayo = cantMayo != null ? cantMayo + "" : "0";	
					
					Integer cantJunio = cants.get(cod + "-6");
					String junio = cantJunio != null ? cantJunio + "" : "0";
					
					Integer cantJulio = cants.get(cod + "-7");
					String julio = cantJulio != null ? cantJulio + "" : "0";
					
					Integer cantAgosto = cants.get(cod + "-8");
					String agosto = cantAgosto != null ? cantAgosto + "" : "0";
					
					Integer cantSetiembre = cants.get(cod + "-9");
					String setiembre = cantSetiembre != null ? cantSetiembre + "" : "0";
					
					Integer cantOctubre = cants.get(cod + "-10");
					String octubre = cantOctubre != null ? cantOctubre + "" : "0";
					
					Integer cantNoviembre = cants.get(cod + "-11");
					String noviembre = cantNoviembre != null ? cantNoviembre + "" : "0";
					
					Integer cantDiciembre = cants.get(cod + "-12");
					String diciembre = cantDiciembre != null ? cantDiciembre + "" : "0";
					
					HistoricoMovimientoArticulo hist = new HistoricoMovimientoArticulo();
					hist.setCodigo(cod);
					hist.setCodigoProveedor(codProveedor);
					hist.setDescripcion(descripcion);
					hist.setReferencia(referencia);
					hist.setCodigoOriginal(codigoOriginal);
					hist.setEstado((boolean) det[5] ? "ACTIVO" : "INACTIVO");
					hist.setArticulo("");
					hist.setFamilia(familia);
					hist.setMarca(marca);
					hist.setLinea(linea);
					hist.setGrupo(grupo);
					hist.setOchentaVeinte(ochentaVeinte);
					hist.setAbc(abc);
					hist.setAplicacion(aplicacion);
					hist.setModelo(modelo);
					hist.setPeso(peso);
					hist.setVolumen(volumen);
					hist.setProveedor(proveedor);
					hist.setSubGrupo(subGrupo);
					hist.setParte(parte);
					hist.setSubMarca(subMarca);
					hist.setUnidadesCaja(unidadesPorCaja);
					hist.setProcedencia(procedencia);
					hist.setCantidad(Long.parseLong(cantidad));
					hist.setCantCliente(cantCliente);
					hist.setCantClienteVigente(cantClienteVig);
					hist.setMaximo(maximo);
					hist.setMinimo(minimo);
					hist.setCostoFobGs(costoFobGs);
					hist.setCostoFobDs(costoFobDs);
					hist.setStock1(Long.parseLong(dep_1));
					hist.setStock2(Long.parseLong(dep_2));
					hist.setStock3(Long.parseLong(dep_3));
					hist.setStock4(Long.parseLong(dep_4));
					hist.setStock5(Long.parseLong(dep_5));
					hist.setStock6(Long.parseLong(dep_6));
					hist.setStock7(Long.parseLong(dep_7));
					hist.setStock8(Long.parseLong(dep_8));
					hist.setStock9(Long.parseLong(dep_9));
					hist.setStock10(Long.parseLong(dep_10));
					hist.setStockGral(hist.getStock1() + hist.getStock2() + hist.getStock3() + hist.getStock4() + hist.getStock5() + hist.getStock6() + hist.getStock7() + hist.getStock8());
					hist.setStockMinimo(0);
					hist.setStockMaximo(0);
					hist.setFechaUltimaCompra(fechaUltimaCompra);
					hist.setProveedorUltimaCompra(proveedoUltimaCompra);
					hist.setFechaUltimaVenta("");
					hist.setCostoFob(0);
					hist.setCoeficiente(0);
					hist.setTipoCambio(0);
					hist.setCostoGs(0);
					hist.setMayoristaGs(0);
					hist.setClienteGral(0);
					hist.setClienteMesVigente(0);
					hist.setEnero(Long.parseLong(enero));
					hist.setFebrero(Long.parseLong(febrero));
					hist.setMarzo(Long.parseLong(marzo));
					hist.setAbril(Long.parseLong(abril));
					hist.setMayo(Long.parseLong(mayo));
					hist.setJunio(Long.parseLong(junio));
					hist.setJulio(Long.parseLong(julio));
					hist.setAgosto(Long.parseLong(agosto));
					hist.setSetiembre(Long.parseLong(setiembre));
					hist.setOctubre(Long.parseLong(octubre));
					hist.setNoviembre(Long.parseLong(noviembre));
					hist.setDiciembre(Long.parseLong(diciembre));
					hist.setTotal(hist.getEnero() + hist.getFebrero() + hist.getMarzo() + hist.getAbril() + hist.getMayo() + hist.getJunio()
							+ hist.getJulio() + hist.getAgosto() + hist.getSetiembre() + hist.getOctubre() + hist.getNoviembre() + hist.getDiciembre());
					
					list.add(hist);			
				}				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_ABASTECIMIENTO_MOVIM_ARTICULOS;				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("MES_CLI_VIG", filtro.getSelectedMes().getPos2());
				params.put("dep1", deps.get(1));
				params.put("dep2", deps.get(2));
				params.put("dep3", deps.get(3));
				params.put("dep4", deps.get(4));
				params.put("dep5", deps.get(5));
				params.put("dep6", deps.get(6));
				params.put("dep7", deps.get(7));
				params.put("dep8", deps.get(8));
				params.put("dep9", deps.get(9));
				params.put("dep10", deps.get(10));
				JRDataSource dataSource = new MovimientoArticulos(list);
				imprimirJasper(source, params, dataSource, com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_CSV);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * VEN-00048
		 */
		private void comprasArticuloMes(boolean mobile, String codReporte) {
			try {					
				Object[] formato = filtro.getFormato();
				ArticuloFamilia familia = filtro.getFamilia_();
				Proveedor proveedor = filtro.getProveedor();
				List<Deposito> depositos = filtro.getSelectedDepositos();
				SucursalApp sucursal = filtro.getSelectedSucursal();
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				long idFamilia = familia != null ? familia.getId().longValue() : 0;
				long idProveedor = proveedor != null ? proveedor.getId().longValue() : 0;
				long idSucursal = sucursal != null ? sucursal.getId().longValue() : 0;
				int mes1 = Utiles.getNumeroMes(desde) - 1;
				int mes2 = Utiles.getNumeroMes(hasta);
				int rango = mes2 - mes1;
				
				if (depositos.size() > 3) {
					Clients.showNotification("MÁXIMO HASTA 3 DEPÓSITOS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				Deposito dep1 = depositos.size() > 0 ? depositos.get(0) : null;
				Deposito dep2 = depositos.size() > 1 ? depositos.get(1) : null;
				Deposito dep3 = depositos.size() > 2 ? depositos.get(2) : null;
				
				List<Deposito> deps1 = new ArrayList<Deposito>();
				List<Deposito> deps2 = new ArrayList<Deposito>();
				List<Deposito> deps3 = new ArrayList<Deposito>();
				
				if (dep1 != null) deps1.add(dep1);
				if (dep2 != null) deps2.add(dep2);
				if (dep3 != null) deps3.add(dep3);
				
				RegisterDomain rr = RegisterDomain.getInstance();				
				List<Object[]> locales = rr.getComprasLocalesDetallado(desde, hasta, idFamilia, idProveedor, idSucursal);
				
				List<HistoricoMovimientoArticulo> list = new ArrayList<HistoricoMovimientoArticulo>();
				Map<String, Double> cants = new HashMap<String, Double>();
				Map<String, Double> importes = new HashMap<String, Double>();
				Map<String, Double> volumens = new HashMap<String, Double>();
				Map<String, Object[]> datos = new HashMap<String, Object[]>();
				
				for (Object[] compra : locales) {
					int mes = Utiles.getNumeroMes((Date) compra[4]);
					String cod = (String) compra[1];
					String des = (String) compra[8];
					String prove = (String) compra[15];
					String marca = (String) compra[16];
					String key = cod + ";" + des + ";" + prove + ";" + marca + ";" + mes;
					Double acum = cants.get(key);
					if (acum != null) {
						acum += ((Double) compra[3]);
						cants.put(key, acum);
						volumens.put(cod, (Double) compra[2]);
						datos.put(cod, new Object[] { (Double) compra[9], (Double) compra[10] });
					} else {
						cants.put(key, ((Double) (compra[3])));
						volumens.put(cod, (Double) compra[2]);
					}
					Double acum_ = importes.get(key);
					if (acum_ != null) {
						acum_ += ((Double) compra[6]);
						importes.put(key, acum_);
					} else {
						importes.put(key, ((Double) (compra[6])));
					}
				}
				
				Map<String, String> keys = new HashMap<String, String>();
				
				for (String key : cants.keySet()) {					
					String codigo = key.split(";")[0];
					String descripcion = key.split(";")[1];
					String proveedor_ = key.split(";")[2];
					String marca = key.split(";")[3];
					String key_ = codigo + ";" + descripcion + ";" + proveedor_ + ";" + marca;
					
					if (keys.get(key_) == null) {
						Double cantidad = cants.get(key);
						
						Double cantEnero = cants.get(key_ + ";1");
						if (cantEnero == null) cantEnero = 0.0; 
						
						Double cantFebrero = cants.get(key_ + ";2");
						if (cantFebrero == null) cantFebrero = 0.0;
						
						Double cantMarzo = cants.get(key_ + ";3");
						if (cantMarzo == null) cantMarzo = 0.0;
						
						Double cantAbril = cants.get(key_ + ";4");
						if (cantAbril == null) cantAbril = 0.0;
						
						Double cantMayo = cants.get(key_ + ";5");
						if (cantMayo == null) cantMayo = 0.0;
						
						Double cantJunio = cants.get(key_ + ";6");
						if (cantJunio == null) cantJunio = 0.0;
						
						Double cantJulio = cants.get(key_ + ";7");
						if (cantJulio == null) cantJulio = 0.0;
						
						Double cantAgosto = cants.get(key_ + ";8");
						if (cantAgosto == null) cantAgosto = 0.0;
						
						Double cantSetiembre = cants.get(key_ + ";9");
						if (cantSetiembre == null) cantSetiembre = 0.0;
						
						Double cantOctubre = cants.get(key_ + ";10");
						if (cantOctubre == null) cantOctubre = 0.0;
						
						Double cantNoviembre = cants.get(key_ + ";11");
						if (cantNoviembre == null) cantNoviembre = 0.0;
						
						Double cantDiciembre = cants.get(key_ + ";12");
						if (cantDiciembre == null) cantDiciembre = 0.0;
						
						Double impEnero = importes.get(key_ + ";1");
						if (impEnero == null) impEnero = 0.0;
						
						Double impFebrero = importes.get(key_ + ";2");
						if (impFebrero == null) impFebrero = 0.0;
						
						Double impMarzo = importes.get(key_ + ";3");
						if (impMarzo == null) impMarzo = 0.0;
						
						Double impAbril = importes.get(key_ + ";4");
						if (impAbril == null) impAbril = 0.0;
						
						Double impMayo = importes.get(key_ + ";5");
						if (impMayo == null) impMayo = 0.0;
						
						Double impJunio = importes.get(key_ + ";6");
						if (impJunio == null) impJunio = 0.0;
						
						Double impJulio = importes.get(key_ + ";7");
						if (impJulio == null) impJulio = 0.0;
						
						Double impAgosto = importes.get(key_ + ";8");
						if (impAgosto == null) impAgosto = 0.0;
						
						Double impSetiembre = importes.get(key_ + ";9");
						if (impSetiembre == null) impSetiembre = 0.0;
						
						Double impOctubre = importes.get(key_ + ";10");
						if (impOctubre == null) impOctubre = 0.0;
						
						Double impNoviembre = importes.get(key_ + ";11");
						if (impNoviembre == null) impNoviembre = 0.0;
						
						Double impDiciembre = importes.get(key_ + ";12");
						if (impDiciembre == null) impDiciembre = 0.0;
						
						Object[] costoPrecio = rr.getCostoPrecio(codigo);
						long stock1 = rr.getStock(codigo, deps1);
						long stock2 = rr.getStock(codigo, deps2);
						long stock3 = rr.getStock(codigo, deps3);
						Double volumen = volumens.get(codigo);
						HistoricoMovimientoArticulo hist = new HistoricoMovimientoArticulo();
						hist.setDescripcion(codigo);
						hist.setReferencia(descripcion);
						hist.setProveedor(proveedor_);
						hist.setMarca(marca);
						hist.setCodigoOriginal("");
						hist.setLitraje(cantidad);
						hist.setCoeficiente(volumen != null ? (volumen * (stock1 + stock2 + stock3)) : 0.0);
						hist.setEnero_(cantEnero);
						hist.setFebrero_(cantFebrero);
						hist.setMarzo_(cantMarzo);
						hist.setAbril_(cantAbril);
						hist.setMayo_(cantMayo);
						hist.setJunio_(cantJunio);
						hist.setJulio_(cantJulio);
						hist.setAgosto_(cantAgosto);
						hist.setSetiembre_(cantSetiembre);
						hist.setOctubre_(cantOctubre);
						hist.setNoviembre_(cantNoviembre);
						hist.setDiciembre_(cantDiciembre);
						hist.set_enero(impEnero);
						hist.set_febrero(impFebrero);
						hist.set_marzo(impMarzo);
						hist.set_abril(impAbril);
						hist.set_mayo(impMayo);
						hist.set_junio(impJunio);
						hist.set_julio(impJulio);
						hist.set_agosto(impAgosto);
						hist.set_setiembre(impSetiembre);
						hist.set_octubre(impOctubre);
						hist.set_noviembre(impNoviembre);
						hist.set_diciembre(impDiciembre);
						hist.setCostoGs((double) costoPrecio[1]);
						hist.setCostoFobGs((double) costoPrecio[2]);
						hist.setPrecioMinorista((double) costoPrecio[3]);
						hist.setPrecioLista((double) costoPrecio[4]);
						hist.setStock1(stock1);
						hist.setStock2(stock2);
						hist.setStock3(stock3);
						hist.setTotal_(hist.getEnero_() + hist.getFebrero_() + hist.getMarzo_() + hist.getAbril_() + hist.getMayo_() + hist.getJunio_()
								+ hist.getJulio_() + hist.getAgosto_() + hist.getSetiembre_() + hist.getOctubre_() + hist.getNoviembre_() + hist.getDiciembre_());
						list.add(hist);			
						keys.put(key_, key_);
					}					
				}				
				String format = (String) formato[0];
				String csv = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_CSV[0];
				String xls = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS[0];
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_MATRIZ_COMPRAS_LOCALES;
				if (format.equals(csv) || format.equals(xls)) {
					source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_MATRIZ_COMPRAS_LOCALES_;
				}
				
				String flia = familia != null ? familia.getDescripcion() : "TODOS..";
				String suc = sucursal != null ? sucursal.getDescripcion() : "TODOS..";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("Usuario", getUs().getNombre());
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("Familia", flia);
				params.put("Cliente", "");
				params.put("stock1", dep1 != null ? dep1.getDescripcion() : "- - -");
				params.put("stock2", dep2 != null ? dep2.getDescripcion() : "- - -");
				params.put("stock3", dep3 != null ? dep3.getDescripcion() : "- - -");
				params.put("Titulo", codReporte + " - MATRIZ DE COMPRAS LOCALES POR ARTICULO POR MES");
				params.put("Proveedor", proveedor != null ? proveedor.getRazonSocial() : "TODOS..");
				params.put("Sucursal", suc);
				JRDataSource dataSource = new VentasClienteArticulo(list, rango);
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * compras locales por articulo..
		 */
		private void comprasLocalesPorArticulo(String codigo) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				ArticuloFamilia flia = filtro.getFamilia_();
				List<Deposito> depositos = filtro.getSelectedDepositos();
				
				long idFamilia = flia != null ? flia.getId() : 0; 

				if (depositos.size() > 2) {
					Clients.showNotification("MÁXIMO HASTA 2 DEPÓSITOS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();
				
				Deposito dep1 = depositos.size() > 0 ? depositos.get(0) : null;
				Deposito dep2 = depositos.size() > 1 ? depositos.get(1) : null;
				
				long idDeposito1 = dep1 != null? dep1.getId() : 0;
				long idDeposito2 = dep2 != null? dep2.getId() : 0;

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> compras = rr.getComprasLocalesArticuloDetallado(desde, hasta, idFamilia, idDeposito1, idDeposito2);
				long cero = 0;
				for (Object[] cmp : compras) {
					Object[] obj = new Object[] { cmp[1], cmp[2], cmp[3], cmp[4], cmp[5],
							(cmp[6] != null ? Long.parseLong(cmp[6] + "") : cero),
							(cmp[7] != null ? Long.parseLong(cmp[7] + "") : cero) };
					data.add(obj);
				}
				
				String stock1 = dep1 != null? dep1.getDescripcion() : "- - -";
				String stock2 = dep2 != null? dep2.getDescripcion() : "- - -";
				
				ReporteComprasPorArticulo rep = new ReporteComprasPorArticulo(desde, hasta, stock1, stock2);
				rep.setDatosReporte(data);
				rep.setTitulo(codigo + " - Compras locales por Artículo");
				rep.setApaisada();

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * detalle de compras locales..
		 */
		private void detalleComprasLocales(String codigo) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				ArticuloFamilia flia = filtro.getFamilia_();
				Proveedor prov = filtro.getProveedor();
				
				long idFamilia = flia != null ? flia.getId().longValue() : 0; 
				long idProveedor = prov != null ? prov.getId().longValue() : 0; 
				
				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<CompraLocalFactura> compras = rr.getComprasLocales(desde, hasta, 0, idProveedor, 0);
				List<NotaCredito> ncs = rr.getNotasCreditoCompra(desde, hasta, 0);
				
				for (CompraLocalFactura factura : compras) {
					for (CompraLocalFacturaDetalle item : factura.getDetalles()) {
						boolean add = true;
						if (idFamilia > 0) {
							long idFlia = item.getArticulo().getFamilia().getId().longValue();
							if (idFlia != idFamilia) {
								add = false;
							}
						}						
						if (add) {
							data.add(new Object[] {
									Utiles.getDateToString(factura.getFechaOriginal(), Utiles.DD_MM_YYYY),
									factura.getNumero(),
									item.getArticulo().getCodigoInterno(),
									item.getArticulo().getFamilia().getDescripcion(), item.getCantidad(),
									Utiles.getRedondeo(item.getCostoGs()), Utiles.getRedondeo(item.getDescuentoGs()),
									Utiles.getRedondeo(item.getImporteGs()) });
						}						
					}
				}
				
				for (NotaCredito nc : ncs) {
					for (NotaCreditoDetalle item : nc.getDetallesArticulos()) {
						boolean add = true;
						if (idFamilia > 0) {
							long idFlia = item.getArticulo().getFamilia().getId().longValue();
							if (idFlia != idFamilia) {
								add = false;
							}
						}						
						if (add) {
							data.add(new Object[] {
									Utiles.getDateToString(nc.getFechaEmision(), Utiles.DD_MM_YYYY),
									nc.getNumero(),
									item.getArticulo().getCodigoInterno(),
									item.getArticulo().getFamilia().getDescripcion(), 
									item.getCantidad() * -1,
									Utiles.getRedondeo(item.getMontoGs()), 
									Utiles.getRedondeo(0),
									Utiles.getRedondeo(item.getImporteGs() * -1)});
						}						
					}
				}
				
				String proveedor = prov != null ? prov.getRazonSocial() : "TODOS..";
				String familia = flia != null ? flia.getDescripcion() : "TODOS..";
				ReporteDetalleComprasLocales rep = new ReporteDetalleComprasLocales(desde, hasta, proveedor, familia);
				rep.setDatosReporte(data);
				rep.setTitulo(codigo + " - Detalle de Compras locales");
				rep.setApaisada();

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reportes de logistica..
	 */
	class ReportesDeLogistica {

		static final String LISTADO_REPARTOS = "LOG-00001";
		static final String LISTADO_REPARTOS_DETALLADO = "LOG-00002";

		/**
		 * procesamiento del reporte..
		 */
		public ReportesDeLogistica(String codigoReporte, boolean mobile) throws Exception {
			switch (codigoReporte) {

			case LISTADO_REPARTOS:
				this.listadoRepartos();
				break;
				
			case LISTADO_REPARTOS_DETALLADO:
				this.listadoRepartosDetallado(mobile, LISTADO_REPARTOS_DETALLADO);
				break;
			}
		}

		/**
		 * listado de repartos..
		 */
		private void listadoRepartos() {
			Clients.showNotification(LISTADO_REPARTOS);
		}
		
		/**
		 * listado de repartos detallado..
		 */
		private void listadoRepartosDetallado(boolean mobile, String codReporte) throws Exception {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = filtro.getFechaDesde();			
			Date hasta = filtro.getFechaHasta();
			boolean pendientes = filtro.isFraccionado();
			Funcionario vendedor = filtro.getVendedor();
			String vendedor_ = vendedor != null ? vendedor.getRazonSocial() : null;
			Object[] formato = filtro.getFormato();	
			
			List<Reparto> repartos = rr.getRepartos(desde, hasta);		
			
			String format = (String) formato[0];
			String csv = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_CSV[0];
			String xls = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS[0];
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_REPARTOS_DETALLADO;
			if (format.equals(csv) || format.equals(xls)) {
				source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_REPARTOS_DETALLADO_;
			}
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new RepartosDetalladoDataSource(repartos, desde, hasta, pendientes, vendedor_);
			params.put("Titulo", codReporte + " - REPARTOS DETALLADO");
			params.put("Usuario", getUs().getNombre());
			params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
			params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
			imprimirJasper(source, params, dataSource, formato);
		}

	}
	
	/**
	 * Reportes de servicio tecnico..
	 */
	class ReportesServicioTecnico {

		static final String LISTADO_SERVICIOS_TECNICOS = "STE-00001";

		/**
		 * procesamiento del reporte..
		 */
		public ReportesServicioTecnico(String codigoReporte, boolean mobile) throws Exception {
			switch (codigoReporte) {

			case LISTADO_SERVICIOS_TECNICOS:
				this.listadoServiciosTecnicos(mobile, LISTADO_SERVICIOS_TECNICOS);
				break;
			}
		}
		
		/**
		 * listado de servicios tecnicos..
		 */
		private void listadoServiciosTecnicos(boolean mobile, String codReporte) throws Exception {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = filtro.getFechaDesde();			
			Date hasta = filtro.getFechaHasta();
			Object[] formato = filtro.getFormato();	
			List<ServicioTecnico> stecnicos = rr.getServiciosTecnicos(desde, hasta);
			
			String format = (String) formato[0];
			String csv = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_CSV[0];
			String xls = (String) com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS[0];
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SERVICIOS_TECNICOS;
			if (format.equals(csv) || format.equals(xls)) {
				source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SERVICIOS_TECNICOS_;
			}
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new ServicioTecnicoDataSource(stecnicos, desde, hasta);
			params.put("Titulo", codReporte + " - SERVICIOS TECNICOS");
			params.put("Usuario", getUs().getNombre());
			params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
			params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
			imprimirJasper(source, params, dataSource, formato);
		}

	}

	/**
	 * Reportes de Contabilidad..
	 */
	class ReportesDeContabilidad {

		static final String LIBRO_VENTAS = "CON-00002";
		static final String VENTAS_HECHAUKA = "CON-00003";
		static final String NOTAS_CREDITO_HECHAUKA = "CON-00004";
		static final String VENTAS_GENERICO = "CON-00005";
		static final String CONCILIACION_BANCARIA = "CON-00007";
		static final String CHEQUES_ADELANTADOS_CARTERA = "CON-00008";
		static final String CHEQUES_DESCONTADOS_A_VENCER = "CON-00009";
		static final String STOCK_MERCADERIA_COSTO_PROMEDIO = "CON-00011";
		static final String ANTICIPOS_PROVEEDORES_EXTERIOR = "CON-00012";
		static final String DOCS_A_COBRAR_EMP_RELACIONADAS = "CON-00013";
		static final String DOCS_GESTION_JUDICIAL = "CON-00014";
		static final String DEUDAS_PROVEEDORES_EXTERIOR = "CON-00015";
		static final String DEUDAS_CON_ACREEDORES = "CON-00017";
		static final String DEUDAS_CON_ASEGURADORAS = "CON-00018";
		static final String DEUDAS_CON_FINANCIERAS = "CON-00019";
		static final String LIBRO_MAYOR = "CON-0000X";
		static final String DOCUMENTOS_ANULADOS = "CON-00020";
		static final String GASTOS_POR_CUENTA_CONTABLE_DETALLADO = "CON-00021";
		static final String GASTOS_POR_CUENTA_CONTABLE = "CON-00022";
		static final String RESUMEN_PLANILLAS_CAJA = "CON-00023";
		static final String HISTORIAL_SALDOS_CLIENTES = "CON-00025";
		static final String HISTORIAL_SALDOS_CLIENTES_RESUMIDO = "CON-00026";
		static final String HISTORIAL_SALDOS_PROVEEDORES = "CON-00027";
		static final String HISTORIAL_SALDOS_PROVEEDORES_RESUMIDO = "CON-00028";
		static final String CHEQUES_RECHAZADOS = "CON-00029";
		static final String PRESTAMOS_CASA_CENTRAL = "CON-00030";
		static final String REEMBOLSOS_PRESTAMOS_CASA_CENTRAL = "CON-00031";
		static final String COSTO_VENTAS = "CON-00032";
		static final String HISTORIAL_SALDOS_PROVEEDORES_EXT = "CON-00033";
		static final String HISTORIAL_SALDOS_PROVEEDORES_EXT_RESUMIDO = "CON-00034";
		static final String LIBRO_COMPRAS_INDISTINTO = "CON-00035";
		static final String LIBRO_COMPRAS_DESPACHO = "CON-00036";
		static final String LIBRO_COMPRAS_MERCADERIA = "CON-00037";
		static final String VENTAS_GENERICO_COSTOS = "CON-00038";
		static final String LIBRO_COMPRAS_MATRICIAL = "CON-00040";
		static final String LIBRO_VENTAS_MATRICIAL = "CON-00041";
		
		/**
		 * procesamiento del reporte..
		 */
		public ReportesDeContabilidad(String codigoReporte, boolean mobile) throws Exception {
			switch (codigoReporte) {

			case LIBRO_VENTAS:
				this.libroVentas();
				break;

			case VENTAS_HECHAUKA:
				this.ventasHechauka();
				break;

			case NOTAS_CREDITO_HECHAUKA:
				this.notasCreditoHechauka();
				break;
				
			case VENTAS_GENERICO:
				this.ventasGenerico(mobile);
				break;
				
			case CONCILIACION_BANCARIA:
				this.conciliacionBancaria();
				break;
				
			case CHEQUES_ADELANTADOS_CARTERA:
				this.chequesAdelantadosCartera();
				break;
				
			case CHEQUES_DESCONTADOS_A_VENCER:
				this.chequesDescontadosAvencer();
				break;
				
			case STOCK_MERCADERIA_COSTO_PROMEDIO:
				this.stockMercaderiaAunaFecha(STOCK_MERCADERIA_COSTO_PROMEDIO);
				break;	
				
			case ANTICIPOS_PROVEEDORES_EXTERIOR:
				Clients.showNotification("SIN DATOS PARA MOSTRAR..");
				break;
				
			case DOCS_A_COBRAR_EMP_RELACIONADAS:
				Clients.showNotification("SIN DATOS PARA MOSTRAR..");
				break;
				
			case DOCS_GESTION_JUDICIAL:
				Clients.showNotification("SIN DATOS PARA MOSTRAR..");
				break;
				
			case DEUDAS_PROVEEDORES_EXTERIOR:
				Clients.showNotification("SIN DATOS PARA MOSTRAR..");
				break;
				
			case DEUDAS_CON_ACREEDORES:
				Clients.showNotification("SIN DATOS PARA MOSTRAR..");
				break;
				
			case DEUDAS_CON_ASEGURADORAS:
				Clients.showNotification("SIN DATOS PARA MOSTRAR..");
				break;	
			
			case DEUDAS_CON_FINANCIERAS:
				Clients.showNotification("SIN DATOS PARA MOSTRAR..");
				break;
			
			case DOCUMENTOS_ANULADOS:
				this.documentosAnulados(mobile);
				break;
				
			case GASTOS_POR_CUENTA_CONTABLE_DETALLADO:
				this.gastosPorCuentaContable(mobile);
				break;
				
			case GASTOS_POR_CUENTA_CONTABLE:
				this.gastosPorCuentaContable_(mobile);
				break;
				
			case RESUMEN_PLANILLAS_CAJA:
				this.resumenPlanillasCaja();
				break;	
				
			case HISTORIAL_SALDOS_CLIENTES:
				this.historialMovimientosClientes(mobile, false, HISTORIAL_SALDOS_CLIENTES);
				break;
				
			case HISTORIAL_SALDOS_CLIENTES_RESUMIDO:	
				this.historialMovimientosClientes(mobile, true, HISTORIAL_SALDOS_CLIENTES_RESUMIDO);
				break;
				
			case HISTORIAL_SALDOS_PROVEEDORES:
				this.historialMovimientosProveedores(mobile, false);
				break;
				
			case HISTORIAL_SALDOS_PROVEEDORES_RESUMIDO:
				this.historialMovimientosProveedores(mobile, true);
				break;
				
			case CHEQUES_RECHAZADOS:
				this.chequesRechazados();
				break;
				
			case PRESTAMOS_CASA_CENTRAL:
				this.prestamosCasaCentral(mobile);
				break;
				
			case REEMBOLSOS_PRESTAMOS_CASA_CENTRAL:
				this.reembolsoPrestamosCasaCentral(mobile);
				break;
				
			case COSTO_VENTAS:
				this.costoDeVentas(mobile);
				break;
				
			case HISTORIAL_SALDOS_PROVEEDORES_EXT:
				this.historialMovimientosProveedoresExterior(mobile, false);
				break;
				
			case HISTORIAL_SALDOS_PROVEEDORES_EXT_RESUMIDO:
				this.historialMovimientosProveedoresExterior(mobile, true);
				break;
				
			case LIBRO_COMPRAS_INDISTINTO:
				this.libroComprasIndistinto();
				break;
				
			case LIBRO_COMPRAS_DESPACHO:
				this.libroComprasDespacho();
				break;
				
			case LIBRO_COMPRAS_MERCADERIA:
				this.libroComprasLocales();
				break;
				
			case VENTAS_GENERICO_COSTOS:
				this.ventasGenericoCosto(mobile);
				break;
				
			case LIBRO_COMPRAS_MATRICIAL:
				this.libroComprasMatricial();
				break;	
				
			case LIBRO_VENTAS_MATRICIAL:
				this.libroVentasMatricial();
				break;
				
			case LIBRO_MAYOR:
				this.libroMayor();
				break;
			}
		}

		/**
		 * Hechauka de ventas..
		 */
		private void ventasHechauka() throws Exception {
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			boolean incluirVTA = filtro.isIncluirVCT();
			boolean incluirNCR = filtro.isIncluirNCR();

			List<Venta> ventas = new ArrayList<>();
			if (incluirVTA) ventas = rr.getVentas(desde, hasta, 0, 0);
			List<NotaCredito> notasCredito = new ArrayList<NotaCredito>();
			if (incluirNCR) notasCredito = rr.getNotasCreditoCompra(desde, hasta, 0);
			List<NotaDebito> notasDebito = new ArrayList<NotaDebito>();
			notasDebito = rr.getNotasDebito(desde, hasta, 0, 0);
			InformeHechauka.generarInformeHechauka(ventas, notasCredito, notasDebito);
			Clients.showNotification("Informe Hechauka generado..");
		}

		/**
		 * Hechauka de notas de credito..
		 */
		private void notasCreditoHechauka() throws Exception {
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Date inicio = Utiles.getFechaInicioOperaciones();
			boolean incluirNC = filtro.isIncluirNCR();
			boolean incluirCO = filtro.isIncluirCOM();
			boolean incluirGA = filtro.isIncluirGastos();
			boolean incluirBI = filtro.isIncluirBaseImponible();

			List<NotaCredito> ncs = new ArrayList<NotaCredito>();
			if (incluirNC) ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
			List<CompraLocalFactura> compras = new ArrayList<>();
			if (incluirCO) compras = rr.getLibroComprasLocales(desde, hasta, 0);
			List<ImportacionFactura> importaciones = rr.getLibroComprasImportacion(desde, hasta, inicio, new Date());
			List<Gasto> gastos = new ArrayList<Gasto>();
			if (incluirGA) {
				List<Gasto> gastosIndistinto = rr.getLibroComprasIndistinto(desde, hasta, inicio, new Date(), 0);
				List<Gasto> gastosDespacho = rr.getLibroComprasDespacho_(desde, hasta, inicio, new Date(), 0);
				gastos.addAll(gastosIndistinto);
				gastos.addAll(gastosDespacho);	
			}
			if (incluirBI && !incluirGA) {
				List<Gasto> gastosDespacho = rr.getLibroComprasDespacho_(desde, hasta, inicio, new Date(), 0);
				gastos.addAll(gastosDespacho);
			}
			InformeHechauka.generarInformeHechaukaCompras(ncs, compras, gastos, importaciones, incluirBI, incluirGA);
			Clients.showNotification("Informe Hechauka generado..");
		}

		/**
		 * Libro de Ventas..
		 */
		private void libroVentas() throws Exception {
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			SucursalApp suc = filtro.getSelectedSucursal();
			Cliente cli = filtro.getCliente();
			long idSuc = suc != null ? suc.getId() : 0;
			long idCli = cli != null ? cli.getId() : 0;
			String suc_ = suc != null ? suc.getDescripcion() : "TODOS..";
			Object[] formato = filtro.getFormato();
			boolean formularioContinuo = filtro.isFormularioContinuo();
			List<Venta> ventas = rr.getVentas(desde, hasta, idCli, idSuc);
			List<NotaCredito> notasCredito = rr.getNotasCreditoVenta(desde,	hasta, idCli, idSuc, "");
			List<NotaDebito> notasDebito = rr.getNotasDebito(desde,	hasta, idCli, idSuc);
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_VENTAS;
			if (formularioContinuo)
				source = formato.equals(com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS)? 
						com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_VENTAS_FC_XLS : 
						com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_VENTAS_FC;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new LibroVentasDataSource(ventas, notasCredito, notasDebito, desde, hasta);
			params.put("Usuario", getUs().getNombre());
			params.put("Sucursal", suc_);
			imprimirJasper(source, params, dataSource, formato);
		}
		
		/**
		 * reporte CON-00005
		 */
		private void ventasGenerico(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Set<ArticuloFamilia> _familias = filtro.getSelectedFamilias();
				Cliente cliente = filtro.getCliente();
				SucursalApp suc = filtro.getSelectedSucursal();
				String expedicion = filtro.getExpedicion();
				Deposito dep = filtro.getDeposito();
				String cliente_ = cliente == null ? "TODOS.." : cliente.getRazonSocial();
				long idCliente = cliente == null ? 0 : cliente.getId();
				long idSucursal = suc == null ? 0 : suc.getId(); 
				long idDeposito = dep == null ? 0 : dep.getId();
				
				List<ArticuloFamilia> familias = new ArrayList<ArticuloFamilia>();
				familias.addAll(_familias);

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();
				
				if (!expedicion.isEmpty()) {
					expedicion = "-" + expedicion;
					expedicion += "-";
				}

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				double totalImporte = 0;

				if (filtro.isIncluirNCR() || filtro.isIncluirNCR_CRED()) {
					List<NotaCredito> ncs = rr.getNotasCreditoVenta_(desde, hasta, idCliente, idSucursal, expedicion, idDeposito);
					for (NotaCredito notacred : ncs) {
						int length = notacred.getCliente().getRazonSocial()
								.length();
						int maxlength = length > 25 ? 25 : length;
						String motivo = notacred.getMotivo().getDescripcion()
								.substring(0, 3).toUpperCase()
								+ ".";
						Object[] nc = new Object[] {
								m.dateToString(notacred.getFechaEmision(),
										"dd-MM-yy"),
								notacred.getNumero(),
								notacred.getCliente().getRazonSocial()
										.substring(0, maxlength)
										+ "..",
								notacred.getCliente().getRuc(),
								notacred.isNotaCreditoVentaContado() ? "NC-CO "
										+ motivo : "NC-CR " + motivo,
								notacred.isAnulado() ? 0.0 : notacred
										.getImporteGs(familias) * -1 };

						if (filtro.isIncluirNCR()
								&& notacred.isNotaCreditoVentaContado()) {
							data.add(nc);
							totalImporte += (notacred.isAnulado() ? 0.0
									: notacred.getImporteGs(familias) * -1);
						} else if (filtro.isIncluirNCR_CRED()
								&& !notacred.isNotaCreditoVentaContado()) {
							data.add(nc);
							totalImporte += (notacred.isAnulado() ? 0.0
									: notacred.getImporteGs(familias) * -1);
						}
					}
				}

				if (filtro.isIncluirVCR() && filtro.isIncluirVCT()) {
					List<Venta> ventas = rr.getVentas_(desde, hasta, idCliente, idSucursal, expedicion, idDeposito);
					for (Venta venta : ventas) {
						Object[] vta = new Object[] {
								m.dateToString(venta.getFecha(), "dd-MM-yy"),
								venta.getNumero(),
								getMaxLength(
										venta.getDenominacion() == null ? venta
												.getCliente().getRazonSocial()
												: venta.getDenominacion(), 25),
								venta.getCliente().getRuc(),
								"FAC. "
										+ venta.getCondicionPago()
												.getDescripcion()
												.substring(0, 3).toUpperCase(),
								venta.isAnulado() ? 0.0 : venta
										.getTotalImporteGs(familias) };
						data.add(vta);
						totalImporte += (venta.isAnulado() ? 0.0 : venta
								.getTotalImporteGs(familias));
					}

				} else if (filtro.isIncluirVCR()) {
					List<Venta> ventas = rr.getVentasCredito_(desde, hasta, idCliente, idSucursal, expedicion);
					for (Venta venta : ventas) {
						Object[] vta = new Object[] {
								m.dateToString(venta.getFecha(), "dd-MM-yy"),
								venta.getNumero(),
								getMaxLength(
										venta.getDenominacion() == null ? venta
												.getCliente().getRazonSocial()
												: venta.getDenominacion(), 25),
								venta.getCliente().getRuc(),
								"FAC. "
										+ venta.getCondicionPago()
												.getDescripcion()
												.substring(0, 3).toUpperCase(),
								venta.isAnulado() ? 0.0 : venta
										.getTotalImporteGs(familias) };
						data.add(vta);
						totalImporte += (venta.isAnulado() ? 0.0 : venta
								.getTotalImporteGs(familias));
					}

				} else if (filtro.isIncluirVCT()) {
					List<Venta> ventas = rr.getVentasContado_(desde, hasta, idCliente, idSucursal, expedicion);
					for (Venta venta : ventas) {
						Object[] vta = new Object[] {
								m.dateToString(venta.getFecha(), "dd-MM-yy"),
								venta.getNumero(),
								getMaxLength(
										venta.getDenominacion() == null ? venta
												.getCliente().getRazonSocial()
												: venta.getDenominacion(), 25),
								venta.getCliente().getRuc(),
								"FAC. "
										+ venta.getCondicionPago()
												.getDescripcion()
												.substring(0, 3).toUpperCase(),
								venta.isAnulado() ? 0.0 : venta
										.getTotalImporteGs(familias) };
						data.add(vta);
						totalImporte += (venta.isAnulado() ? 0.0 : venta
								.getTotalImporteGs(familias));
					}
				}

				double totalSinIva = totalImporte - m.calcularIVA(totalImporte, 10);
				String sucursal = suc == null ? "TODOS.." : suc.getDescripcion().toUpperCase();
				String familias_ = "TODOS..";
				if (familias.size() > 0) {
					familias_ = "";
					for (ArticuloFamilia flia : familias) {
						familias_ += "/" + flia.getDescripcion() + " ";
					}
				}

				ReporteVentasGenerico rep = new ReporteVentasGenerico(totalSinIva, desde, hasta, "TODOS..", cliente_, sucursal, "TODOS..", familias_);
				rep.setDatosReporte(data);
				
				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * CON-00007
		 */
		private void conciliacionBancaria() {
			
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Object[] formato = filtro.getFormato();
			
			if (desde == null)
				desde = new Date();

			if (hasta == null)
				hasta = new Date();
			
			try {
				RegisterDomain rr = RegisterDomain.getInstance();
				List<BancoExtracto> concs = rr.getConciliacionesBanco(desde, hasta);		
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_CONCILIACIONES_BANCARIAS;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new ConciliacionesBancariasDataSource(concs, getSucursal());
				params.put("Titulo", "CONCILIACIÓN BANCARIA CON DETALLE DE PARTIDAS");
				params.put("Usuario", getUs().getNombre());
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				Clients.showNotification("Hubo un error al procesar..favor contacte con Sistemas", 
						Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				e.printStackTrace();
			}
			
		}
		
		/**
		 * reporte CON-00008
		 */
		private void chequesAdelantadosCartera() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				
				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<BancoChequeTercero> cheques = rr.getChequesTercero("", "",
						"", "", "", "", "", "", "", "", "", "", null, null, null, null,
						null, null, desde, hasta, "", "", false);
				
				for (BancoChequeTercero cheque : cheques) {
					System.out.println(cheque.getNumero() + " - " + cheque.isEnCartera(hasta) + " - " + cheque.getFecha());
				}
				
				for (BancoChequeTercero cheque : cheques) {
					if (cheque.isDiferido() && cheque.isEnCartera(hasta)) {
						data.add(new Object[] { 
								Utiles.getDateToString(cheque.getEmision(), Utiles.DD_MM_YYYY), 
								Utiles.getDateToString(cheque.getFecha(), Utiles.DD_MM_YYYY), 
								Utiles.getDateToString(cheque.getFechaDeposito(), Utiles.DD_MM_YYYY),
								Utiles.getDateToString(cheque.getFechaDescuento(), Utiles.DD_MM_YYYY),
								cheque.getNumero(),
								cheque.getBanco().getDescripcion().toUpperCase(),
								cheque.getMonto() });
					}
				}
				
				ReporteChequesAdelantadosCartera rep = new ReporteChequesAdelantadosCartera(desde, hasta);
				rep.setApaisada();
				rep.setDatosReporte(data);
				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte CON-00009
		 */
		private void chequesDescontadosAvencer() throws Exception {
			try {
				String emision = filtro.getAnhoDesde();
				String vencimiento = filtro.getAnhoHasta();
				
				if (emision == null || vencimiento == null) {
					Clients.showNotification("Debe especificar los periodos..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				Date emisionDesde = Utiles.getFecha("01-01-" + emision + " 00:00:00");
				Date emisionHasta = Utiles.getFecha("31-12-" + emision + " 23:00:00");
				Date vtoDesde = Utiles.getFecha("01-01-" + vencimiento + " 00:00:00");
				Date vtoHasta = Utiles.getFecha("31-12-" + vencimiento + " 23:00:00");
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<BancoChequeTercero> cheques = rr.getChequesTercero("", "",
						"", "", "", "", "", "", "", "", "", "", null, "TRUE", null, null,
						vtoDesde, vtoHasta, emisionDesde, emisionHasta, "", "", false);
				
				for (BancoChequeTercero cheque : cheques) {
					if (cheque.isDiferido()) {
						Date fechaDesc = cheque.getFechaDescuento();
						if (fechaDesc != null) {
							int cmp = emisionHasta.compareTo(fechaDesc);
							if (cmp >= 0) {
								data.add(new Object[] { 
										Utiles.getDateToString(cheque.getEmision(), Utiles.DD_MM_YYYY), 
										Utiles.getDateToString(cheque.getFecha(), Utiles.DD_MM_YYYY), 
										Utiles.getDateToString(cheque.getFechaDescuento(), Utiles.DD_MM_YYYY),
										cheque.getNumeroDescuento(),
										cheque.getNumero(),
										cheque.getBanco().getDescripcion().toUpperCase(),
										cheque.getMonto() });
							}
						}						
					}
				}
				
				ReporteChequesDescontadosAvencer rep = new ReporteChequesDescontadosAvencer(emision, vencimiento);
				rep.setApaisada();
				rep.setDatosReporte(data);
				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte CON-00011
		 */
		public void stockMercaderiaAunaFecha(String codReporte) throws Exception {
			try {
				Proveedor proveedor = filtro.getProveedorExterior() != null ? filtro.getProveedorExterior() : filtro.getProveedorLocal();
				Date hasta = filtro.getFechaHasta();
				Articulo articulo = filtro.getArticulo();
				String tipoCosto = filtro.getTipoCosto();
				ArticuloFamilia familia = filtro.getFamilia_();
				SucursalApp sucursal = filtro.getSelectedSucursal();
				Deposito deposito = filtro.getDeposito();
				long idProveedor = proveedor != null ? proveedor.getId() : (long) 0;
				long idSucursal = sucursal != null ? sucursal.getId() : (long) 0;
				long idArticulo = articulo != null ? articulo.getId() : (long) 0;
				long idDeposito = deposito != null ? deposito.getId() : (long) 0;
				
				if (familia == null) {
					Clients.showNotification("DEBE SELECCIONAR UNA FAMILIA..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				if (hasta == null) hasta = new Date();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> arts = new ArrayList<Object[]>();
				
				arts = rr.getArticulos(idArticulo, idProveedor, familia.getId(), true);

				for (Object[] art : arts) {
					
					List<Object[]> historial = ControlArticuloStock.getHistorialMovimientos((long) art[0], idDeposito, idSucursal, false, hasta, true);
					Object[] historial_ = historial.size() > 0 ? historial.get(historial.size() - 1) : null;
					
					String codigoInterno = (String) art[1];
					String descripcion = (String) art[2];
					
					String saldo = historial_ != null ? (String) historial_[7] : "0";
					long stock = historial_ != null ? Long.parseLong(saldo) : (long) 0;
					double costo  = (double) art[3];
					
					if (stock != 0 && costo > 0) {
						data.add(new Object[] { codigoInterno, descripcion, stock, costo, (stock * costo) });
					}				
				}
				
				String desc = articulo != null ? articulo.getCodigoInterno() : "TODOS..";
				String familia_ = familia.getDescripcion();
				String proveedor_ = proveedor != null ? proveedor.getRazonSocial() : "TODOS..";
				String deposito_ = deposito != null ? deposito.getDescripcion() : "TODOS..";
				ReporteStockValorizadoAunaFecha rep = new ReporteStockValorizadoAunaFecha(hasta, desc, tipoCosto, familia_, proveedor_, deposito_);
				rep.setTitulo(codReporte + "STOCK VALORIZADO (A UNA FECHA)");
				rep.setApaisada();
				rep.setDatosReporte(data);
				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
				
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		/**
		 * reporte CON-00020
		 */
		private void documentosAnulados(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<NotaCredito> notasCredito = rr.getNotasCreditoVentaAnuladas(desde, hasta);
				List<Venta> ventasContado = rr.getVentasContadoAnuladas(desde, hasta);
				List<Venta> ventasCredito = rr.getVentasCreditoAnuladas(desde, hasta);
				
				for (NotaCredito nc : notasCredito) {
					if (nc.isAnulado()) {
						data.add(new Object[] {
								Utiles.getDateToString(nc.getFechaEmision(), "dd/MM/yyyy"),
								nc.getTipoMovimiento().getDescripcion().toUpperCase(),
								nc.getNumero(), nc.getTimbrado_(),
								"ANULADO" });
					}
				}
					
				for (Venta venta : ventasContado) {
					if (venta.isAnulado()) {
						data.add(new Object[] {
								Utiles.getDateToString(venta.getFecha(), "dd/MM/yyyy"),
								venta.getTipoMovimiento().getDescripcion().toUpperCase(),
								venta.getNumero(), venta.getTimbrado(),
								"ANULADO" });
					}
				}
				
				for (Venta venta : ventasCredito) {
					if (venta.isAnulado()) {
						data.add(new Object[] {
								Utiles.getDateToString(venta.getFecha(), "dd/MM/yyyy"),
								venta.getTipoMovimiento().getDescripcion().toUpperCase(),
								venta.getNumero(), venta.getTimbrado(),
								"ANULADO" });
					}
				}

				ReporteDocumentosAnulados rep = new ReporteDocumentosAnulados(desde, hasta, getSucursal());
				rep.setDatosReporte(data);
				rep.setApaisada();
				

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		
		/**
		 * Gastos por Cuenta Contable detallado..
		 */
		private void gastosPorCuentaContable(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Date desde_ = filtro.getFechaDesde2();
			Date hasta_ = filtro.getFechaHasta2();
			ArticuloGasto cuenta = filtro.getArticuloGasto();
			boolean otrosComprobantes = filtro.isFraccionado();
			SucursalApp suc = filtro.getSelectedSucursal();
			String sucursal = suc != null ? suc.getDescripcion() : "TODOS..";
			long idSucursal = suc != null ? suc.getId() : 0;
			Object[] formato = filtro.getFormato();
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();
			
			if (desde_ == null) desde_ = new Date();
			if (hasta_ == null) hasta_ = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();			
			List<Gasto> gastos = new ArrayList<Gasto>();
			List<Object[]> dets = new ArrayList<Object[]>();
			List<NotaCredito> notascredito = new ArrayList<NotaCredito>();
			if (otrosComprobantes) {
				gastos = rr.getLibroComprasIndistinto_(desde, hasta, desde_, hasta_, idSucursal);
			} else {
				gastos = rr.getLibroComprasIndistinto(desde, hasta, desde_, hasta_, idSucursal);
				notascredito = rr.getNotasCreditoCompra(desde, hasta, idSucursal);
			}

			for (Gasto gasto : gastos) {
				for (GastoDetalle det : gasto.getDetalles()) {
					if (cuenta != null) {
						if (cuenta.getId().longValue() == det.getArticuloGasto().getId().longValue()) {
							dets.add(new Object[]{ det, gasto });
						}
					} else {
						dets.add(new Object[]{ det, gasto });
					}
				}
			}			
			
			Collections.sort(dets, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					GastoDetalle d1 = (GastoDetalle) o1[0];
					GastoDetalle d2 = (GastoDetalle) o2[0];
					String val1 = d1.getArticuloGasto().getDescripcion();
					String val2 = d2.getArticuloGasto().getDescripcion();
					int compare = val1.compareTo(val2);				
					return compare;
				}
			});			
			
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_GASTOS_POR_CUENTA_CONTABLE;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new GastosPorCuentaContableDataSource(dets, notascredito);
			params.put("Usuario", getUs().getNombre());
			params.put("periodo", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY) 
					+ " a " + Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
			params.put("Sucursal", sucursal);
			imprimirJasper(source, params, dataSource, formato);
		}
		
		/**
		 * Gastos por Cuenta Contable..
		 */
		private void gastosPorCuentaContable_(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Date desde_ = filtro.getFechaDesde2();
			Date hasta_ = filtro.getFechaHasta2();
			ArticuloGasto cuenta = filtro.getArticuloGasto();
			boolean otrosComprobantes = filtro.isFraccionado();
			SucursalApp suc = filtro.getSelectedSucursal();
			String sucursal = suc != null ? suc.getDescripcion() : "TODOS..";
			long idSucursal = suc != null ? suc.getId() : 0;
			String keyNC = "DESCUENTOS OBTENIDOS";
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();
			
			if (desde_ == null) desde_ = new Date();
			if (hasta_ == null) hasta_ = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();			
			List<Gasto> gastos = new ArrayList<Gasto>();
			List<NotaCredito> notascredito = new ArrayList<NotaCredito>();
			List<Object[]> dets = new ArrayList<Object[]>();
			List<Object[]> data = new ArrayList<Object[]>();
			Map<String, Object[]> values = new HashMap<String, Object[]>();
			if (otrosComprobantes) {
				gastos = rr.getLibroComprasIndistinto_(desde, hasta, desde_, hasta_, idSucursal);
			} else {
				gastos = rr.getLibroComprasIndistinto(desde, hasta, desde_, hasta_, idSucursal);
				if (cuenta == null) {
					notascredito = rr.getNotasCreditoCompra(desde, hasta, idSucursal);
				}
			}

			for (Gasto gasto : gastos) {
				for (GastoDetalle det : gasto.getDetalles()) {
					if (cuenta != null) {
						if (cuenta.getId().longValue() == det.getArticuloGasto().getId().longValue()) {
							dets.add(new Object[]{ det, gasto });
						}
					} else {
						dets.add(new Object[]{ det, gasto });
					}
				}
			}	
			
			for (Object[] value : dets) {
				GastoDetalle det = (GastoDetalle) value[0];
				Object[] val = values.get(det.getArticuloGasto().getDescripcion());
				if (val != null) {
					double totalGrav10 = (double) val[1];
					double totalGrav5 = (double) val[2];
					double totalIva10 = (double) val[3];
					double totalIva5 = (double) val[4];
					double totalExenta = (double) val[5];
					double totalImporte = (double) val[6]; 
					totalGrav10 += det.getGravada10();
					totalGrav5 += det.getGravada5();
					totalIva10 += det.getIva10();
					totalIva5 += det.getIva5();
					totalExenta += det.getExenta();
					totalImporte += (det.getGravada10() + det.getGravada5() + det.getIva10() + det.getIva5() + det.getExenta());
					values.put(det.getArticuloGasto().getDescripcion(),
							new Object[] { det, Utiles.getRedondeo(totalGrav10), Utiles.getRedondeo(totalGrav5),
									Utiles.getRedondeo(totalIva10), Utiles.getRedondeo(totalIva5),
									Utiles.getRedondeo(totalExenta), Utiles.getRedondeo(totalImporte) });
				} else {
					double totalGrav10 = det.getGravada10();
					double totalGrav5 = det.getGravada5();
					double totalIva10 = det.getIva10();
					double totalIva5 = det.getIva5();
					double totalExenta = det.getExenta();
					double totalImporte = (det.getGravada10() + det.getGravada5() + det.getIva10() + det.getIva5() + det.getExenta());
					values.put(det.getArticuloGasto().getDescripcion(),
							new Object[] { det, Utiles.getRedondeo(totalGrav10), Utiles.getRedondeo(totalGrav5),
									Utiles.getRedondeo(totalIva10), Utiles.getRedondeo(totalIva5),
									Utiles.getRedondeo(totalExenta), Utiles.getRedondeo(totalImporte) });
				}
			}
			
			for (NotaCredito nc : notascredito) {
				if (nc.isNotaCreditoCompraAcreedor()) {
					String key = keyNC;
					Object[] val = values.get(key);
					double gravada10 = nc.getTotalGravado10() * -1;
					double gravada5 = 0.0;
					double exenta = nc.getTotalExenta() * -1;
					double iva10 = nc.getTotalIva10() * -1;
					double iva5 = 0.0;
					double importe = gravada10 + gravada5 + exenta + iva10 + iva5;
					if (val != null) {					
						double totalGrav10 = (double) val[1];
						double totalGrav5 = (double) val[2];
						double totalIva10 = (double) val[3];
						double totalIva5 = (double) val[4];
						double totalExenta = (double) val[5];
						double totalImporte = (double) val[6]; 
						totalGrav10 += gravada10;
						totalGrav5 += gravada5;
						totalIva10 += iva10;
						totalIva5 += iva5;
						totalExenta += exenta;
						totalImporte += importe;
						values.put(key,
								new Object[] { null, Utiles.getRedondeo(totalGrav10), Utiles.getRedondeo(totalGrav5),
										Utiles.getRedondeo(totalIva10), Utiles.getRedondeo(totalIva5),
										Utiles.getRedondeo(totalExenta), Utiles.getRedondeo(totalImporte) });
					} else {
						double totalGrav10 = gravada10;
						double totalGrav5 = gravada5;
						double totalIva10 = iva10;
						double totalIva5 = iva5;
						double totalExenta = exenta;
						double totalImporte = importe;
						values.put(key,
								new Object[] { null, Utiles.getRedondeo(totalGrav10), Utiles.getRedondeo(totalGrav5),
										Utiles.getRedondeo(totalIva10), Utiles.getRedondeo(totalIva5),
										Utiles.getRedondeo(totalExenta), Utiles.getRedondeo(totalImporte) });
					}
				}
			}
			
			for (String key : values.keySet()) {
				if (!key.equals(keyNC)) {
					Object[] value = values.get(key);
					GastoDetalle det = (GastoDetalle) value[0];
					double totalGrav10 = (double) value[1];
					double totalGrav5 = (double) value[2];
					double totalIva10 = (double) value[3];
					double totalIva5 = (double) value[4];
					double totalExenta = (double) value[5];
					double totalImporte = (double) value[6];
					String cod = det.getArticuloGasto().getCuentaContable() != null ? det.getArticuloGasto().getCuentaContable().getCodigo() : "";
					String desc = det.getArticuloGasto().getCuentaContable() != null ? det.getArticuloGasto().getDescripcion() : "";
					data.add(new Object[] { cod, desc, Utiles.getRedondeo(totalGrav10), Utiles.getRedondeo(totalGrav5),
							Utiles.getRedondeo(totalIva10), Utiles.getRedondeo(totalIva5), Utiles.getRedondeo(totalExenta),
							Utiles.getRedondeo(totalImporte) });
				} else {
					Object[] value = values.get(key);
					double totalGrav10 = (double) value[1];
					double totalGrav5 = (double) value[2];
					double totalIva10 = (double) value[3];
					double totalIva5 = (double) value[4];
					double totalExenta = (double) value[5];
					double totalImporte = (double) value[6];
					String cod = "";
					String desc = keyNC;
					data.add(new Object[] { cod, desc, Utiles.getRedondeo(totalGrav10), Utiles.getRedondeo(totalGrav5),
							Utiles.getRedondeo(totalIva10), Utiles.getRedondeo(totalIva5), Utiles.getRedondeo(totalExenta),
							Utiles.getRedondeo(totalImporte) });
				}				
			}
			
			Collections.sort(data, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					String val1 = (String) o1[1];
					String val2 = (String) o2[1];
					int compare = val1.compareTo(val2);				
					return compare;
				}
			});	
			
			ReporteGastosPorCuentas rep = new ReporteGastosPorCuentas(desde, hasta, sucursal);
			rep.setDatosReporte(data);
			rep.setApaisada();			

			if (!mobile) {
				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
			} else {
				rep.ejecutar();
				Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
			}
		}
		
		/**
		 * Resumen planillas caja..
		 */
		private void resumenPlanillasCaja() {
			CajaPlanillaResumen resumen = filtro.getSelectedResumen();
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_RESUMEN_;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new ResumenDataSource(resumen);
			params.put("Fecha", Utiles.getDateToString(resumen.getFecha(), Utiles.DD_MM_YYYY));
			params.put("NroResumen", resumen.getNumero());
			params.put("Usuario", getUs().getNombre());
			imprimirJasper(source, params, dataSource, com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_PDF);
		}
		
		/**
		 * CON-00025
		 */
		private void historialMovimientosClientes(boolean mobile, boolean consolidado, String codReporte) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Date desde = Utiles.getFechaInicioOperaciones();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				Cliente cliente = filtro.getCliente();
				boolean incluirChequesRechazados = filtro.isIncluirCHQ_RECH();
				boolean incluirPrestamos = filtro.isIncluirPRE();

				if (desde == null) desde = filtro.getFechaInicioOperaciones();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> historico;
				List<Object[]> historicoDEBE;
				List<Object[]> historicoHABER;
				Map<String, String> totalSaldo = new HashMap<String, String>();
				double totalVentas = 0;
				double totalChequesRechazados = 0;
				double totalNotasCredito = 0;
				double totalRecibos = 0;
				double totalNotasDebito = 0;
				double totalReembolsosCheques = 0;

				long idCliente = cliente != null ? cliente.getId() : 0;

				List<Object[]> ventas = rr.getVentasPorCliente_(idCliente, desde, hasta);
				List<Object[]> cheques_rechazados = rr.getChequesRechazadosPorCliente(idCliente, desde, hasta);
				List<Object[]> prestamos_cc = rr.getPrestamosCC(idCliente, desde, hasta);
				List<Object[]> ntcsv = rr.getNotasCreditoPorCliente(idCliente, desde, hasta);
				List<Object[]> recibos = rr.getRecibosPorCliente(idCliente, desde, hasta);
				List<Object[]> reembolsos_cheques_rechazados = rr.getReembolsosChequesRechazadosPorCliente(idCliente, desde, hasta);
				List<Object[]> reembolsos_prestamos_cc = rr.getReembolsosPrestamosCC(idCliente, desde, hasta);
				
				for (Object[] venta : ventas) {
					totalVentas += ((double) venta[3]);
				}
				
				for (Object[] chequeRech : cheques_rechazados) {
					totalChequesRechazados += ((double) chequeRech[3]);
				}
				
				for (Object[] ncred : ntcsv) {
					totalNotasCredito -= ((double) ncred[3]);
				}
				
				for (Object[] rec : recibos) {
					totalRecibos -= ((double) rec[3]);
				}
				
				for (Object[] reemb : reembolsos_cheques_rechazados) {
					totalReembolsosCheques -= ((double) reemb[3]);
				}

				historicoDEBE = new ArrayList<Object[]>();
				historicoHABER = new ArrayList<Object[]>();
				
				historicoDEBE.addAll(ventas);				
				if (incluirChequesRechazados) historicoDEBE.addAll(cheques_rechazados);
				if (incluirPrestamos) historicoDEBE.addAll(prestamos_cc);				
				
				historicoHABER.addAll(ntcsv);
				historicoHABER.addAll(recibos);
				if (incluirChequesRechazados) historicoHABER.addAll(reembolsos_cheques_rechazados);
				if (incluirPrestamos) historicoHABER.addAll(reembolsos_prestamos_cc);				

				for (Object[] movim : historicoDEBE) {
					movim[0] = "(+)" + movim[0];
				}

				historico = new ArrayList<Object[]>();
				historico.addAll(historicoDEBE);
				historico.addAll(historicoHABER);

				// ordena la lista segun fecha..
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						Date fecha1 = (Date) o1[1];
						Date fecha2 = (Date) o2[1];
						return fecha1.compareTo(fecha2);
					}
				});
				
				double saldo = 0;
				
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String val1 = (String) o1[4];
						String val2 = (String) o2[4];			
						int compare = val1.compareTo(val2);
						if (compare == 0) {
							Date emision1 = (Date) o1[1];
							Date emision2 = (Date) o2[1];
				            return emision1.compareTo(emision2);
				        }
				        else {
				            return compare;
				        }
					}
				});
				
				String key = "";
				for (Object[] hist : historico) {
					String razonsocial = (String) hist[4];
					if(!key.equals(razonsocial)) saldo = 0;					
					boolean ent = ((String) hist[0]).startsWith("(+)");
					String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
					String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
					String numero = (String) hist[2];
					String concepto = ((String) hist[0]).replace("(+)", "");
					String entrada = ent ? Utiles.getNumberFormat(Double.parseDouble(hist[3] + "")) : "";
					String salida = ent ? "" : Utiles.getNumberFormat(Double.parseDouble(hist[3] + ""));
					saldo += ent ? Double.parseDouble(hist[3] + "") :  Double.parseDouble(hist[3] + "") * -1;
					String saldo_ = Utiles.getNumberFormat(saldo);
					data.add(new Object[] { fecha, hora, numero, concepto, entrada, salida, saldo_, razonsocial, (Date) hist[1] });
					totalSaldo.put(razonsocial, saldo_);
					key = razonsocial;
				}
				
				String cli = cliente != null ? cliente.getRazonSocial() : "TODOS..";
				String sourceDetallado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET_DHS;
				String sourceConsolidado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_CONSOLIDADO_DHS;
				String source = consolidado? sourceConsolidado : sourceDetallado;
				String titulo = codReporte + " - " + (consolidado ? "SALDOS DE CLIENTES RESUMIDO (A UNA FECHA)" 
						: "SALDOS DE CLIENTES DETALLADO (HISTORIAL A UNA FECHA)");
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo, totalVentas,
						totalChequesRechazados, totalNotasCredito, totalRecibos, totalNotasDebito,
						totalReembolsosCheques);
				params.put("Titulo", titulo);
				params.put("Usuario", getUs().getNombre());
				params.put("Moneda", filtro.getMonedaGs());
				params.put("desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * CON-00027
		 */
		private void historialMovimientosProveedores(boolean mobile, boolean consolidado) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Date desde = filtro.getFechaInicioOperaciones();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				Proveedor proveedor = filtro.getProveedor();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> historico;
				List<Object[]> historicoDEBE;
				List<Object[]> historicoHABER;
				Map<String, String> totalSaldo = new HashMap<String, String>();

				long idProveedor = proveedor != null ? proveedor.getId() : 0;

				List<Object[]> compras = rr.getComprasPorProveedor_(idProveedor, desde, hasta);
				List<Object[]> gastos = rr.getGastosPorProveedor_(idProveedor, desde, hasta);
				List<Object[]> pagos = rr.getPagosPorProveedor(idProveedor, desde, hasta, true);
				List<Object[]> notascredito = rr.getNotasCreditoPorProveedor(idProveedor, desde, hasta, true);

				historicoDEBE = new ArrayList<Object[]>();
				historicoHABER = new ArrayList<Object[]>();
				
				historicoDEBE.addAll(pagos);
				historicoDEBE.addAll(notascredito);
				
				historicoHABER.addAll(compras);
				historicoHABER.addAll(gastos);

				for (Object[] movim : historicoDEBE) {
					movim[0] = "(+)" + movim[0];
				}

				historico = new ArrayList<Object[]>();
				historico.addAll(historicoHABER);
				historico.addAll(historicoDEBE);

				// ordena la lista segun fecha..
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						Date fecha1 = (Date) o1[1];
						Date fecha2 = (Date) o2[1];
						return fecha1.compareTo(fecha2);
					}
				});
				
				double saldo = 0;
				
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String val1 = (String) o1[4];
						String val2 = (String) o2[4];			
						int compare = val1.compareTo(val2);
						if (compare == 0) {
							Date emision1 = (Date) o1[1];
							Date emision2 = (Date) o2[1];
				            return emision1.compareTo(emision2);
				        }
				        else {
				            return compare;
				        }
					}
				});
				
				String key = "";
				for (Object[] hist : historico) {
					String razonsocial = (String) hist[4];
					if(!key.equals(razonsocial)) saldo = 0;					
					boolean ent = ((String) hist[0]).startsWith("(+)");
					String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
					String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
					String numero = (String) hist[2];
					String concepto = ((String) hist[0]).replace("(+)", "");
					String entrada = ent ? Utiles.getNumberFormat(Double.parseDouble(hist[3] + "")) : "";
					String salida = ent ? "" : Utiles.getNumberFormat(Double.parseDouble(hist[3] + ""));
					saldo += ent ? Double.parseDouble(hist[3] + "") * -1 : Double.parseDouble(hist[3] + "");
					String saldo_ = Utiles.getNumberFormat(saldo);
					data.add(new Object[] { fecha, hora, numero, concepto, entrada, salida, saldo_, razonsocial, (Date) hist[1] });
					totalSaldo.put(razonsocial, saldo_);
					key = razonsocial;
				}
				
				String cli = proveedor != null ? proveedor.getRazonSocial() : "TODOS..";
				String sourceDetallado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET_DHS;
				String sourceConsolidado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_CONSOLIDADO_DHS;
				String source = consolidado? sourceConsolidado : sourceDetallado;
				String titulo = consolidado ? "SALDOS DE PROVEEDORES RESUMIDO (A UNA FECHA)" : "SALDOS DE PROVEEDORES DETALLADO (A UNA FECHA)";
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
				params.put("Titulo", titulo);
				params.put("Usuario", getUs().getNombre());
				params.put("Moneda", filtro.getMonedaGs());
				params.put("desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte CON-00029
		 */
		private void chequesRechazados() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Cliente cliente = filtro.getCliente();
				String reembolso = filtro.getReembolso();
				
				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<BancoChequeTercero> cheques = rr.getChequesTercero("", "",
						"", "", "", "", "", "", "", "", "", "", null, null, "TRUE", "TRUE",
						null, null, desde, hasta, "", "", false);
				
				for (BancoChequeTercero cheque : cheques) {
					if (cliente == null || cliente.getId().longValue() == cheque.getCliente().getId().longValue()) {
						if (reembolso.isEmpty() || (reembolso.equals(ReportesFiltros.REEMBOLSADO) && cheque.isReembolsado()) 
								|| (reembolso.equals(ReportesFiltros.SIN_REEMBOLSO) && !cheque.isReembolsado())) {
							data.add(new Object[] { Utiles.getDateToString(cheque.getEmision(), Utiles.DD_MM_YY),
									Utiles.getDateToString(cheque.getFecha(), Utiles.DD_MM_YY), cheque.getNumero(),
									cheque.getBanco().getDescripcion().toUpperCase(),
									cheque.getObservacion().isEmpty() ? "- - -" : Utiles.getMaxLength(cheque.getObservacion(), 20),
									Utiles.getMaxLength(cheque.getCliente().getRazonSocial(), 30),
									cheque.isReembolsado() ? "SI" : "NO",
									Utiles.getRedondeo(cheque.getMonto()) });
						}						
					}
				}
				
				String cli = cliente != null ? cliente.getRazonSocial() : "TODOS..";
				String ree = reembolso.isEmpty() ? "TODOS.." : reembolso;
				ReporteChequesRechazados rep = new ReporteChequesRechazados(desde, hasta, cli, ree);
				rep.setApaisada();
				rep.setDatosReporte(data);				

				ViewPdf vp = new ViewPdf();
				vp.setBotonImprimir(false);
				vp.setBotonCancelar(false);
				vp.showReporte(rep, ReportesViewModel.this);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * CON-00030
		 */
		private void prestamosCasaCentral(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<BancoDescuentoCheque> cheques = rr.getPrestamosCasaCentral(desde, hasta);

				for (BancoDescuentoCheque dto : cheques) {
					data.add(new Object[] { Utiles.getDateToString(dto.getFecha(), Utiles.DD_MM_YYYY),
							dto.getObservacion(), Utiles.getRedondeo(dto.getTotalImporteGs()) });
				}

				ReportePrestamosCasaCentral rep = new ReportePrestamosCasaCentral(desde, hasta, "Préstamos Casa Central");
				rep.setDatosReporte(data);
				rep.setA4();
				rep.setApaisada();

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * CON-00031
		 */
		private void reembolsoPrestamosCasaCentral(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<Recibo> reembolsos = rr.getReembolsosPrestamosCasaCentral(desde, hasta);

				for (Recibo reemb : reembolsos) {
					data.add(new Object[] { Utiles.getDateToString(reemb.getFechaEmision(), Utiles.DD_MM_YYYY),
							reemb.getTipoMovimiento().getDescripcion(), Utiles.getRedondeo(reemb.getTotalImporteGs()) });
				}

				ReportePrestamosCasaCentral rep = new ReportePrestamosCasaCentral(desde, hasta, "Reembolsos Préstamos Casa Central");
				rep.setDatosReporte(data);
				rep.setA4();
				rep.setApaisada();

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte CON-00032
		 */
		private void costoDeVentas(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				String tipoCosto = filtro.getTipoCosto();
				
				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<Object[]> ncs = rr.getNotasCreditoVenta_(desde, hasta, 0);
				for (Object[] notacred : ncs) {
					String numero = (String) notacred[1];
					Date fecha = (Date) notacred[2];
					String descrMotivo = (String) notacred[3];
					String siglaMotivo = (String) notacred[4];
					double costoPromedio = (double) notacred[5];
					double costoUltimo = (double) notacred[6];
					if (siglaMotivo.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_DEVOLUCION)) {
						String motivo = descrMotivo.substring(0, 3).toUpperCase() + ".";
						double costo = 0;
						if (tipoCosto.equals(ReportesFiltros.COSTO_ULTIMO)) {
							costo = costoUltimo;
						}
						if (tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO)) {
							costo = costoPromedio;
						}
						Object[] nc = new Object[] {
								Utiles.getDateToString(fecha, "dd-MM-yy"),
								numero,
								"NCR " + motivo,
								Utiles.getRedondeo(costo * -1) };					
						data.add(nc);
					}
				}

				List<Object[]> ventas = rr.getVentas_(desde, hasta, 0);
				for (Object[] venta : ventas) {
					String numero = (String) venta[1];
					Date fecha = (Date) venta[2];
					String sigla = (String) venta[4];
					double costoPromedio = (double) venta[5];
					double costoUltimo = (double) venta[6];
					double costo = 0;					
					if (tipoCosto.equals(ReportesFiltros.COSTO_ULTIMO)) {
						costo = costoUltimo;
					}
					if (tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO)) {
						costo = costoPromedio;
					}
					Object[] vta = new Object[] { 
							Utiles.getDateToString(fecha, "dd-MM-yy"), 
							numero, 
							"FAC. " + TipoMovimiento.getAbreviatura(sigla),
							Utiles.getRedondeo(costo) };
					data.add(vta);
				}
				String sucursal = getAcceso().getSucursalOperativa().getText();

				ReporteCostoVentas rep = new ReporteCostoVentas(desde, hasta, sucursal, tipoCosto);
				rep.setDatosReporte(data);
				rep.setApaisada();

				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * CON-00033
		 */
		private void historialMovimientosProveedoresExterior(boolean mobile, boolean consolidado) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Date desde = filtro.getFechaInicioOperaciones();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				Proveedor proveedor = filtro.getProveedor();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> historico;
				List<Object[]> historicoDEBE;
				List<Object[]> historicoHABER;
				Map<String, String> totalSaldo = new HashMap<String, String>();

				long idProveedor = proveedor != null ? proveedor.getId() : 0;

				List<Object[]> importaciones = rr.getImportacionesPorProveedor_(idProveedor, desde, hasta);
				List<Object[]> pagos = rr.getPagosPorProveedorExterior(idProveedor, desde, hasta, false);
				List<Object[]> anticipos = rr.getPagosAnticipadosPorProveedorExterior(idProveedor, desde, hasta, false);
				List<Object[]> notascredito = rr.getNotasCreditoPorProveedor(idProveedor, desde, hasta, false);
				
				historicoDEBE = new ArrayList<Object[]>();
				historicoHABER = new ArrayList<Object[]>();
				
				historicoDEBE.addAll(pagos);
				historicoDEBE.addAll(anticipos);
				historicoDEBE.addAll(notascredito);
				historicoHABER.addAll(importaciones);

				for (Object[] movim : historicoDEBE) {
					movim[0] = "(+)" + movim[0];
				}

				historico = new ArrayList<Object[]>();
				historico.addAll(historicoHABER);
				historico.addAll(historicoDEBE);

				// ordena la lista segun fecha..
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						Date fecha1 = (Date) o1[1];
						Date fecha2 = (Date) o2[1];
						return fecha1.compareTo(fecha2);
					}
				});
				
				double saldo = 0;
				
				Collections.sort(historico, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] o1, Object[] o2) {
						String val1 = (String) o1[4];
						String val2 = (String) o2[4];			
						int compare = val1.compareTo(val2);
						if (compare == 0) {
							Date emision1 = (Date) o1[1];
							Date emision2 = (Date) o2[1];
				            return emision1.compareTo(emision2);
				        }
				        else {
				            return compare;
				        }
					}
				});
				
				String key = "";
				for (Object[] hist : historico) {
					String razonsocial = (String) hist[4];
					if(!key.equals(razonsocial)) saldo = 0;					
					boolean ent = ((String) hist[0]).startsWith("(+)");
					String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
					String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
					String numero = (String) hist[2];
					String concepto = ((String) hist[0]).replace("(+)", "");
					String entrada = ent ? Utiles.getNumberFormat(Double.parseDouble(hist[3] + "")) : "";
					String salida = ent ? "" : Utiles.getNumberFormat(Double.parseDouble(hist[3] + ""));
					saldo += ent ? Double.parseDouble(hist[3] + "") * -1 : Double.parseDouble(hist[3] + "");
					String saldo_ = Utiles.getNumberFormat(saldo);
					data.add(new Object[] { fecha, hora, numero, concepto, entrada, salida, saldo_, razonsocial, (Date) hist[1] });
					totalSaldo.put(razonsocial, saldo_);
					key = razonsocial;
				}
				
				String cli = proveedor != null ? proveedor.getRazonSocial() : "TODOS..";
				String sourceDetallado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET_DHS;
				String sourceConsolidado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_CONSOLIDADO_DHS;
				String source = consolidado? sourceConsolidado : sourceDetallado;
				String titulo = consolidado ? "SALDOS DE PROVEEDORES EXTERIOR RESUMIDO (A UNA FECHA)" : "SALDOS DE PROVEEDORES EXTERIOR DETALLADO (A UNA FECHA)";
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
				params.put("Titulo", titulo);
				params.put("Usuario", getUs().getNombre());
				params.put("Moneda", filtro.getMonedaDs());
				params.put("desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Libro compras indistinto.
		 */
		private void libroComprasIndistinto() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Date desde_ = filtro.getFechaDesde2();
				Date hasta_ = filtro.getFechaHasta2();
				boolean otrosComprobantes = filtro.isFraccionado();
				SucursalApp suc = filtro.getSelectedSucursal();
				Object[] formato = filtro.getFormato();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				String sucursal = suc != null ? suc.getDescripcion() : "TODOS..";
				long idSucursal = suc != null ? suc.getId() : 0;
				List<Gasto> gastos = new ArrayList<Gasto>();
				List<NotaCredito> notascredito = new ArrayList<NotaCredito>();
				if (otrosComprobantes) {
					gastos = rr.getLibroComprasIndistinto_(desde, hasta, desde_, hasta_, idSucursal);
				} else {
					gastos = rr.getLibroComprasIndistinto(desde, hasta, desde_, hasta_, idSucursal);
					notascredito = rr.getNotasCreditoCompra(desde, hasta, idSucursal);
				}
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_COMPRAS_INDISTINTO;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new LibroComprasIndistintoDataSource(gastos, new ArrayList<ImportacionFactura>(), notascredito, false);
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", "LIBRO DE COMPRAS INDISTINTO - LEY 125/91 MODIF. POR LEY 2421/04");
				params.put("Sucursal", sucursal);
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("periodo", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY) + " a " + Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Libro compras despacho.
		 */
		private void libroComprasDespacho() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Date desde_ = filtro.getFechaDesde2();
				Date hasta_ = filtro.getFechaHasta2();
				SucursalApp suc = filtro.getSelectedSucursal();
				Object[] formato = filtro.getFormato();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				String sucursal = suc != null ? suc.getDescripcion() : "TODOS..";
				long idSucursal = suc != null ? suc.getId() : 0;
				List<Gasto> gastos = rr.getLibroComprasDespacho(desde, hasta, desde_, hasta_, idSucursal);
				List<ImportacionFactura> importaciones = new ArrayList<ImportacionFactura>();
				List<NotaCredito> notasCreditos = rr.getNotasCreditoCompra(desde, hasta, idSucursal);
				
				if (suc.getId().longValue() == SucursalApp.ID_CENTRAL) {
					importaciones = rr.getLibroComprasImportacion(desde, hasta, desde_, hasta_);
				}
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_COMPRAS_INDISTINTO_;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new LibroComprasIndistintoDataSource(gastos, importaciones, notasCreditos, true);
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", "LIBRO DE COMPRAS S/DESPACHO - LEY 125/91 MODIF. POR LEY 2421/04");
				params.put("Sucursal", sucursal);
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("periodo", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY) + " a " + Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Libro compras locales.
		 */
		private void libroComprasLocales() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				SucursalApp suc = filtro.getSelectedSucursal();
				Object[] formato = filtro.getFormato();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				String sucursal = suc != null ? suc.getDescripcion() : "TODOS..";
				long idSucursal = suc != null ? suc.getId() : 0;
				List<CompraLocalFactura> compras = rr.getLibroComprasLocales(desde, hasta, idSucursal);
				List<NotaCredito> ncreditos = rr.getNotasCreditoCompra(desde, hasta, idSucursal);
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_COMPRAS_IVA_DIRECTO;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new LibroComprasLocalesDataSource(compras, ncreditos);
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", "LIBRO DE COMPRAS IVA DIRECTO LEY 125/91 MODIF. POR LEY 2421/04");
				params.put("Sucursal", sucursal);
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("periodo", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY) + " a " + Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * reporte CON-00038
		 */
		private void ventasGenericoCosto(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Set<ArticuloFamilia> _familias = filtro.getSelectedFamilias();
				Cliente cliente = filtro.getCliente();
				SucursalApp suc = filtro.getSelectedSucursal();
				String cliente_ = cliente == null ? "TODOS.." : cliente.getRazonSocial();
				long idCliente = cliente == null ? 0 : cliente.getId();
				long idSucursal = suc == null ? 0 : suc.getId(); 
				
				List<ArticuloFamilia> familias = new ArrayList<ArticuloFamilia>();
				familias.addAll(_familias);

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				double totalImporte = 0;
				double totalCosto = 0;

				if (filtro.isIncluirNCR() || filtro.isIncluirNCR_CRED()) {
					List<NotaCredito> ncs = rr.getNotasCreditoVenta_(desde, hasta, idCliente, idSucursal, "", 0);
					for (NotaCredito notacred : ncs) {
						int length = notacred.getCliente().getRazonSocial().length();
						int maxlength = length > 25 ? 25 : length;
						String motivo = notacred.getMotivo().getDescripcion().substring(0, 3).toUpperCase() + ".";
						Object[] nc = new Object[] {
								m.dateToString(notacred.getFechaEmision(), "dd-MM-yy"),
								notacred.getNumero(),
								notacred.getCliente().getRazonSocial().substring(0, maxlength) + "..",
								notacred.getCliente().getRuc(),
								notacred.isNotaCreditoVentaContado() ? "NC-CO " + motivo : "NC-CR " + motivo,
								notacred.isAnulado() ? 0.0 : notacred.getImporteGs(familias) * -1,
								notacred.isAnulado() ? 0.0 : notacred.getCostoGs(familias) * -1 };

						if (filtro.isIncluirNCR() && notacred.isNotaCreditoVentaContado()) {
							data.add(nc);
							totalImporte += (notacred.isAnulado() ? 0.0 : notacred.getImporteGs(familias) * -1);
							totalCosto += (notacred.isAnulado() ? 0.0 : notacred.getCostoGs(familias) * -1);
						} 
						
						if (filtro.isIncluirNCR_CRED() && !notacred.isNotaCreditoVentaContado()) {
							data.add(nc);
							totalImporte += (notacred.isAnulado() ? 0.0 : notacred.getImporteGs(familias) * -1);
							totalCosto += (notacred.isAnulado() ? 0.0 : notacred.getCostoGs(familias) * -1);
						}
						System.out.println(nc[1] + " - " + nc[6]);
					}
				}

				if (filtro.isIncluirVCR() && filtro.isIncluirVCT()) {
					List<Venta> ventas = rr.getVentas_(desde, hasta, idCliente, idSucursal, "", 0);
					for (Venta venta : ventas) {
						Object[] vta = new Object[] {
								m.dateToString(venta.getFecha(), "dd-MM-yy"), venta.getNumero(),
								getMaxLength(venta.getDenominacion() == null ? venta.getCliente().getRazonSocial() : venta.getDenominacion(), 25),
								venta.getCliente().getRuc(),
								"FAC. " + venta.getCondicionPago().getDescripcion().substring(0, 3).toUpperCase(),
								venta.isAnulado() ? 0.0 : venta.getTotalImporteGs(familias),
								venta.isAnulado() ? 0.0 : venta.getTotalCostoGs(familias) };
						data.add(vta);
						totalImporte += (venta.isAnulado() ? 0.0 : venta.getTotalImporteGs(familias));
						totalCosto += (venta.isAnulado() ? 0.0 : venta.getTotalCostoGs(familias));
						System.out.println(vta[1] + " - " + vta[6]);
					}

				} else if (filtro.isIncluirVCR()) {
					List<Venta> ventas = rr.getVentasCredito_(desde, hasta, idCliente, idSucursal, "");
					for (Venta venta : ventas) {
						Object[] vta = new Object[] {
								m.dateToString(venta.getFecha(), Utiles.DD_MM_YY), 
								venta.getNumero(),
								getMaxLength(venta.getDenominacion() == null ? venta.getCliente().getRazonSocial() : venta.getDenominacion(), 25),
								venta.getCliente().getRuc(),
								"FAC. " + venta.getCondicionPago().getDescripcion().substring(0, 3).toUpperCase(),
								venta.isAnulado() ? 0.0 : venta.getTotalImporteGs(familias),
								venta.isAnulado() ? 0.0 : venta.getTotalCostoGs(familias) };
						data.add(vta);
						totalImporte += (venta.isAnulado() ? 0.0 : venta.getTotalImporteGs(familias));
						totalCosto += (venta.isAnulado() ? 0.0 : venta.getTotalCostoGs(familias));
					}

				} else if (filtro.isIncluirVCT()) {
					List<Venta> ventas = rr.getVentasContado_(desde, hasta, idCliente, idSucursal, "");
					for (Venta venta : ventas) {
						Object[] vta = new Object[] {
								m.dateToString(venta.getFecha(), "dd-MM-yy"),
								venta.getNumero(),
								getMaxLength(venta.getDenominacion() == null ? venta.getCliente().getRazonSocial() : venta.getDenominacion(), 25),
								venta.getCliente().getRuc(),
								"FAC. " + venta.getCondicionPago().getDescripcion().substring(0, 3).toUpperCase(),
								venta.isAnulado() ? 0.0 : venta.getTotalImporteGs(familias),
								venta.isAnulado() ? 0.0 : venta.getTotalCostoGs(familias) };
						data.add(vta);
						totalImporte += (venta.isAnulado() ? 0.0 : venta.getTotalImporteGs(familias));
						totalCosto += (venta.isAnulado() ? 0.0 : venta.getTotalCostoGs(familias));
					}
				}

				double totalSinIva = totalImporte - m.calcularIVA(totalImporte, 10);
				String sucursal = suc == null ? "TODOS.." : suc.getDescripcion().toUpperCase();
				String familias_ = "TODOS..";
				if (familias.size() > 0) {
					familias_ = "";
					for (ArticuloFamilia flia : familias) {
						familias_ += "/" + flia.getDescripcion() + " ";
					}
				}
				for (Object[] item : data) {
					item[5] = Utiles.getRedondeo((double) item[5]);
					item[6] = Utiles.getRedondeo((double) item[6]);
				}
				VentaGenericoCosto rep = new VentaGenericoCosto(totalSinIva, desde, hasta, "TODOS..", cliente_, sucursal, "TODOS..", familias_, totalCosto);
				rep.setDatosReporte(data);
				rep.setApaisada();
				
				if (!mobile) {
					ViewPdf vp = new ViewPdf();
					vp.setBotonImprimir(false);
					vp.setBotonCancelar(false);
					vp.showReporte(rep, ReportesViewModel.this);
				} else {
					rep.ejecutar();
					Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Libro compras matricial.
		 */
		private void libroComprasMatricial() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Date desde_ = Utiles.getFechaInicioOperaciones();
				Date hasta_ = new Date();
				SucursalApp suc = filtro.getSelectedSucursal();
				Object[] formato = filtro.getFormato();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				String sucursal = suc != null ? suc.getDescripcion() : "TODOS..";
				long idSucursal = suc != null ? suc.getId() : 0;
				List<Gasto> gastos = new ArrayList<Gasto>();
				List<NotaCredito> notascredito = new ArrayList<NotaCredito>();
				List<CompraLocalFactura> compras = new ArrayList<CompraLocalFactura>();
				List<Gasto> despacho = new ArrayList<Gasto>();
				List<ImportacionFactura> importaciones = new ArrayList<ImportacionFactura>();
				
				gastos = rr.getLibroComprasIndistinto(desde, hasta, desde_, hasta_, idSucursal);
				despacho = rr.getLibroComprasDespacho(desde, hasta, desde_, hasta_, idSucursal);
				notascredito = rr.getNotasCreditoCompra(desde, hasta, idSucursal);
				compras = rr.getLibroComprasLocales(desde, hasta, idSucursal);
				gastos.addAll(despacho);
			
				if (suc != null && suc.getId().longValue() == SucursalApp.ID_CENTRAL) {
					importaciones = rr.getLibroComprasImportacion(desde, hasta, desde_, hasta_);
				}
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_COMPRAS_MATRICIAL;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new LibroComprasMatricial(gastos, importaciones, notascredito, compras);
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", "LIBRO I.V.A. COMPRAS");
				params.put("Sucursal", sucursal);
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("periodo", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY) + " a " + Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Libro ventas matricial.
		 */
		private void libroVentasMatricial() {
			try {				
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				SucursalApp suc = filtro.getSelectedSucursal();
				Object[] formato = filtro.getFormato();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				String sucursal = suc != null ? suc.getDescripcion() : "TODOS..";
				long idSucursal = suc != null ? suc.getId() : 0;
				
				List<Venta> ventas = rr.getVentas(desde, hasta, 0, idSucursal);
				List<NotaCredito> notasCredito = rr.getNotasCreditoVenta(desde,	hasta, 0, idSucursal, "");
				List<NotaDebito> notasDebito = rr.getNotasDebito(desde,	hasta, 0, idSucursal);
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_VENTAS_MATRICIAL;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new LibroVentasMatricial(ventas, notasCredito, notasDebito, desde, hasta);
				params.put("Usuario", getUs().getNombre());
				params.put("Titulo", "LIBRO I.V.A. VENTAS");
				params.put("Sucursal", sucursal);
				params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
				params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				params.put("periodo", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY) + " a " + Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
				imprimirJasper(source, params, dataSource, formato);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Libro Mayor..
		 */
		private void libroMayor() throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Object[] formato = filtro.getFormato();
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_MAYOR;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new LibroMayorDataSource(desde, hasta);
			params.put("Usuario", getUs().getNombre());
			params.put("periodo", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY) + " a " + Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
			imprimirJasper(source, params, dataSource, formato);
		}
	}

	/**
	 * Reportes de Sistema..
	 */
	class ReportesDeSistema {

		static final String LISTADO_USUARIO = "SIS-00001";
		static final String LISTADO_PERFILES_USUARIO = "SIS-00002";

		/**
		 * procesamiento del reporte..
		 */
		public ReportesDeSistema(String codigoReporte) throws Exception {
			switch (codigoReporte) {

			case LISTADO_USUARIO:
				this.listadoUsuarios();
				break;

			case LISTADO_PERFILES_USUARIO:
				this.listadoPerfilesUsuario();
				break;
			}
		}

		/**
		 * listado de usuarios..
		 * 
		 */
		private void listadoUsuarios() throws Exception {
			boolean activos = filtro.isUsuariosActivos();
			boolean inactivos = filtro.isUsuariosInactivos();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Usuario> usuarios = rr.getUsuarios(activos, inactivos);
			ReporteUsuarios rep = new ReporteUsuarios(activos, inactivos);
			rep.setDatosUsuarios(usuarios);

			ViewPdf vp = new ViewPdf();
			vp.setBotonImprimir(false);
			vp.setBotonCancelar(false);
			vp.showReporte(rep, ReportesViewModel.this);
		}

		/**
		 * listado de perfiles de usuario..
		 */
		public void listadoPerfilesUsuario() throws Exception {
			Usuario user = filtro.getUsuario();
			
			ReportePerfilesUsuario rep = new ReportePerfilesUsuario(user == null);
			if (user == null) {
				RegisterDomain rr = RegisterDomain.getInstance();
				rep.setDatosTodosLosPerfilesUsuarios(rr.getUsuarios(true, true));
			} else {
				rep.setNombre(user.getNombre());
				rep.setLogin(user.getLogin());
				rep.setActivo(user.isActivo());
				rep.setDatosPerfilesUsuario(user.getPerfiles());
			}
			ViewPdf vp = new ViewPdf();
			vp.setBotonImprimir(false);
			vp.setBotonCancelar(false);
			vp.showReporte(rep, ReportesViewModel.this);
		}
	}

	public ReportesFiltros getFiltro() {
		return filtro;
	}

	public void setFiltro(ReportesFiltros filtro) {
		this.filtro = filtro;
	}

	/**
	 * @return longitud maxima de un string..
	 */
	private String getMaxLength(String string, int max) {
		if (string.length() > max)
			return string.substring(0, max) + "..";
		return string;
	}

	public String getSelectedGrupo() {
		return selectedGrupo;
	}

	public void setSelectedGrupo(String selectedGrupo) {
		this.selectedGrupo = selectedGrupo;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getFilterDescripcion() {
		return filterDescripcion;
	}

	public void setFilterDescripcion(String filterDescripcion) {
		this.filterDescripcion = filterDescripcion;
	}

	public String getFilterCodigo() {
		return filterCodigo;
	}

	public void setFilterCodigo(String filterCodigo) {
		this.filterCodigo = filterCodigo;
	}
}

/**
 * Reporte codigo STK-00001
 */
class ReporteArticulosGenerico extends ReporteYhaguy {

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Stock", TIPO_LONG, 20, true);

	private String sucursal;
	private String familia;
	private String deposito;

	public ReporteArticulosGenerico(String sucursal, String deposito,
			String familia) {
		this.sucursal = sucursal;
		this.deposito = deposito;
		this.familia = familia;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Artículos Genérico");
		this.setDirectorio("Articulos");
		this.setNombreArchivo("ArtGenerico-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings({ "rawtypes" })
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Sucursal", this.sucursal))
				.add(this.textoParValor("Depósito", this.deposito))
				.add(this.textoParValor("Familia", this.familia)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte codigo STK-00012
 */
class ReporteChequesAvencer extends ReporteYhaguy {

	ReportesViewModel vm2;

	public ReporteChequesAvencer(ReportesViewModel vm2) {
		this.vm2 = vm2;

	}

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Vencimiento", TIPO_STRING, 20);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 20);
	static DatosColumnas col3 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Banco", TIPO_STRING, 30);
	static DatosColumnas col5 = new DatosColumnas("Monto", TIPO_DOUBLE, 20, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Cheques de Clientes a Vencer");
		this.setDirectorio("banco");
		this.setNombreArchivo("Cheques de Clientes -");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String fechaVacio = "---";
		String fechaDesdeTexto = "";
		String fechaHastaTexto = "";

		if (vm2.getFiltro().getFechaDesde() != null) {
			fechaDesdeTexto = m.dateToString(vm2.getFiltro().getFechaDesde(),
					"dd-MM-yyyy");
		} else {
			fechaDesdeTexto = fechaVacio;
		}

		if (vm2.getFiltro().getFechaHasta() != null) {
			fechaHastaTexto = m.dateToString(vm2.getFiltro().getFechaHasta(),
					"dd-MM-yyyy");
		} else {
			fechaHastaTexto = fechaVacio;
		}

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Fecha Desde", fechaDesdeTexto))
				.add(this.textoParValor("Fecha Hasta", fechaHastaTexto)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte codigo TES-00015
 */
class ReporteChequesDescontados extends ReporteYhaguy {

	private Date desde;
	private Date hasta;
	
	private Date descuentoDesde;
	private Date descuentoHasta;
	
	public ReporteChequesDescontados(Date desde, Date hasta, Date descuentoDesde, Date descuentoHasta) {
		this.desde = desde;
		this.hasta = hasta;
		this.descuentoDesde = descuentoDesde;
		this.descuentoHasta = descuentoHasta;
	}

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha Dto.", TIPO_STRING, 25);
	static DatosColumnas col1 = new DatosColumnas("Vto. Cheq", TIPO_STRING, 25);
	static DatosColumnas col2 = new DatosColumnas("Banco Dto.", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Nro. Cheque", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Banco Cheque", TIPO_STRING, 40);
	static DatosColumnas col5 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col6 = new DatosColumnas("Importe", TIPO_DOUBLE_GS, 25, true);

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);		
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Cheques Descontados");
		this.setDirectorio("banco");
		this.setNombreArchivo("ChequesDescontados-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Descuento Desde", Utiles.getDateToString(this.descuentoDesde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Descuento Hasta", Utiles.getDateToString(this.descuentoHasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Vencimiento Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Vencimiento Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Saldos de Clientes / Proveedores
 */
class ReporteSaldosClientes extends ReporteYhaguy {
	private Date desde;
	private Date hasta;
	String titulo;
	String vendedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Ruc", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("A Vencer", TIPO_DOUBLE, 30, true);
	static DatosColumnas col4 = new DatosColumnas("Vencidos", TIPO_DOUBLE, 30, true);
	static DatosColumnas col5 = new DatosColumnas("Saldo", TIPO_DOUBLE, 30, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	public ReporteSaldosClientes(Date desde, Date hasta, String titulo,
			String vendedor) {
		this.desde = desde;
		this.hasta = hasta;
		this.titulo = titulo;
		this.vendedor = vendedor;
	}

	@Override
	public void informacionReporte() {
		this.setTitulo(this.titulo);
		this.setDirectorio("Clientes");
		this.setNombreArchivo("Saldos-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Vendedor", this.vendedor.toUpperCase())));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

class ReporteSaldosConProveedores extends ReporteYhaguy {

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Id Empresa", TIPO_LONG);
	static DatosColumnas col2 = new DatosColumnas("RUC", TIPO_STRING, 100);
	static DatosColumnas col3 = new DatosColumnas("Razón Social", TIPO_STRING,
			100);
	static DatosColumnas col4 = new DatosColumnas("Fecha Apertura Cuenta",
			TIPO_DATE);
	static DatosColumnas col5 = new DatosColumnas("Estado Cuenta", TIPO_STRING);
	static DatosColumnas col6 = new DatosColumnas("Pendiente Total Gs",
			TIPO_DOUBLE_GS, true);

	static {
		col1.setAlineacionColuman(DatosReporte.COLUMNA_ALINEADA_IZQUIERDA);
		col4.setAlineacionColuman(DatosReporte.COLUMNA_ALINEADA_CENTRADA);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Saldos con Proveedores");
		this.setDirectorio("Proveedores");
		this.setNombreArchivo("Saldos con Proveedores -");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList().add(
				this.texto("Listado general de saldos con proveedores")));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

class ReporteSaldosClientesDetallado2 extends ReporteYhaguy {

	List<MyArray> saldosDetallados = new ArrayList<MyArray>();

	public ReporteSaldosClientesDetallado2(List<MyArray> saldosDetallados) {
		this.saldosDetallados = saldosDetallados;
	}

	@Override
	public void informacionReporte() {

		this.setTitulo("Saldos Clientes Detallado");
		this.setDirectorio("Clientes");
		this.setNombreArchivo("Saldos de Clientes Detallado -");
		this.setBody(this.getCuerpo());

	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();

		List<Object[]> datos1 = new ArrayList<Object[]>();
		List<Object[]> datos2 = new ArrayList<Object[]>();
		List<CtaCteEmpresaMovimientoDTO> movimientosPendientes = new ArrayList<CtaCteEmpresaMovimientoDTO>();

		String[][] cols1 = { { "Id Empresa", WIDTH + "50" },
				{ "Razón Social", WIDTH + "70" },
				{ "Fecha Apertura Cuenta", WIDTH + "70" },
				{ "Estado Cuenta", WIDTH + "60" },
				{ "Linea Credito Gs", DERECHA + WIDTH + "60" },
				{ "Pendiente Total Gs", DERECHA + WIDTH + "70" },
				{ "Credito Disponible Gs", DERECHA + WIDTH + "70" }, };

		String[][] cols2 = { { "Fecha Emisión", WIDTH + "50" },
				{ "Fecha Vencimiento", WIDTH + "50" },
				{ "Sucursal", WIDTH + "70" },
				{ "Tipo Movimiento", WIDTH + "60" },
				{ "Nro. de Comprobante", WIDTH + "60" },
				{ "Moneda", WIDTH + "70" },
				{ "Importe Original", DERECHA + WIDTH + "70" },
				{ "Saldo", DERECHA + WIDTH + "70" }, };

		NumberFormat f = NumberFormat.getInstance();
		f.setGroupingUsed(false);
		String refinedNumber1;
		String refinedNumber2;
		String refinedNumber3;
		String refinedNumber4;

		for (MyArray m : this.saldosDetallados) {

			datos1 = new ArrayList<Object[]>();

			MyArray estadoCuenta = (MyArray) m.getPos1();

			refinedNumber1 = f.format((double) estadoCuenta.getPos6());
			refinedNumber2 = f.format((double) estadoCuenta.getPos7());

			Object[] o = { estadoCuenta.getPos1(), estadoCuenta.getPos2(),
					estadoCuenta.getPos3(),
					((Tipo) estadoCuenta.getPos4()).getDescripcion(),
					((CtaCteLineaCredito) estadoCuenta.getPos5()).getLinea(),
					refinedNumber1, refinedNumber2 };
			datos1.add(o);

			String prop = TABLA_TITULO + "Estado de cuentas de "
					+ estadoCuenta.getPos2();
			out.add(this.getTabla(cols1, datos1, prop));

			movimientosPendientes = (List<CtaCteEmpresaMovimientoDTO>) m
					.getPos2();

			datos2 = new ArrayList<Object[]>();
			String prop2 = TABLA_TITULO + "Detalles de Movimientos "
					+ estadoCuenta.getPos2();

			for (CtaCteEmpresaMovimientoDTO mov : movimientosPendientes) {

				refinedNumber3 = f.format(mov.getImporteOriginal());
				refinedNumber4 = f.format(mov.getSaldo());

				Object[] movimiento = { mov.getFechaEmision(),
						mov.getFechaVencimiento(), mov.getSucursal().getPos1(),
						mov.getTipoMovimiento().getPos1(),
						mov.getNroComprobante(), mov.getMoneda().getText(),
						refinedNumber3, refinedNumber4 };
				datos2.add(movimiento);
			}

			out.add(this.getTabla2(cols2, datos2, prop2));
			out.add(cmp.horizontalFlowList().add(this.texto("")));
			out.add(cmp.horizontalFlowList().add(this.texto("")));
			out.add(cmp.horizontalFlowList().add(this.texto("")));

		}

		return out;

	}

}

/**
 * Reporte TES-00004
 */
class ReporteSaldosClientesDetallado extends ReporteYhaguy {

	RegisterDomain rr = RegisterDomain.getInstance();

	List<MyArray> saldosDetallados = new ArrayList<MyArray>();

	public ReporteSaldosClientesDetallado(List<MyArray> saldosDetallados) {
		this.saldosDetallados = saldosDetallados;
	}

	@Override
	public void informacionReporte() {

		this.setTitulo("Saldos de Clientes Detallado");
		this.setDirectorio("Clientes");
		this.setNombreArchivo("Saldos de Clientes Detallado -");
		try {
			this.setBody(this.getCuerpo());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * cabecera del reporte..
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ComponentBuilder getCuerpo() throws Exception {

		VerticalListBuilder out = cmp.verticalList();

		List<Object[]> datos2 = new ArrayList<Object[]>();
		List<CtaCteEmpresaMovimientoDTO> movimientosPendientes = new ArrayList<CtaCteEmpresaMovimientoDTO>();

		NumberFormat f = NumberFormat.getInstance();
		f.setGroupingUsed(false);
		String refinedNumber1;
		String refinedNumber2;
		String refinedNumber3;
		String refinedNumber4;

		for (MyArray m : this.saldosDetallados) {

			MyArray estadoCuenta = (MyArray) m.getPos1();

			refinedNumber1 = f.format((double) estadoCuenta.getPos6());
			refinedNumber2 = f.format((double) estadoCuenta.getPos7());

			Empresa e = (Empresa) rr.getObject(Empresa.class.getName(),
					(long) estadoCuenta.getPos1());

			out.add(cmp
					.horizontalFlowList()
					.add(this.textoParValor("Nombre", e.getNombre()))
					.add(this.textoParValor("Razón Social", e.getRazonSocial())));
			out.add(cmp
					.horizontalFlowList()
					.add(this.textoParValor("RUC", e.getRuc()))
					.add(this.textoParValor("Apertura Cuenta", this.m
							.dateToString((Date) estadoCuenta.getPos3(),
									"dd-MM-yyyy")))
					.add(this.textoParValor("Estado Cuenta",
							((Tipo) estadoCuenta.getPos4()).getDescripcion())));
			out.add(cmp
					.horizontalFlowList()
					.add(this.textoParValor("Linea Crédito",
							((CtaCteLineaCredito) estadoCuenta.getPos5())
									.getLinea()))
					.add(this.textoParValor("Pendiente Total Gs.",
							refinedNumber1))
					.add(this.textoParValor("Crédito Disponible",
							refinedNumber2)));

			// Movimientos
			movimientosPendientes = (List<CtaCteEmpresaMovimientoDTO>) m
					.getPos2();
			datos2 = new ArrayList<Object[]>();

			for (CtaCteEmpresaMovimientoDTO mov : movimientosPendientes) {

				refinedNumber3 = f.format(mov.getImporteOriginal());
				refinedNumber4 = f.format(mov.getSaldo());
				String concepto = TipoMovimiento.getAbreviatura((String) mov
						.getTipoMovimiento().getPos2());

				Object[] movimiento = {
						this.m.dateToString(mov.getFechaEmision(), "dd-MM-yyyy"),
						this.m.dateToString(mov.getFechaVencimiento(),
								"dd-MM-yyyy"), concepto,
						mov.getNroComprobante().replace("(1/1)", ""),
						refinedNumber3, refinedNumber4 };
				datos2.add(movimiento);
			}
			out.add(this.getTablaMovimientos(datos2));
			out.add(cmp.horizontalFlowList().add(this.texto("")));
			out.add(cmp.horizontalFlowList().add(this.texto("")));
			out.add(cmp.horizontalFlowList().add(this.texto("")));

		}

		return out;

	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder getTablaMovimientos(List<Object[]> datos) {

		ComponentBuilder out = null;

		String[][] cols = { { "Emisión", WIDTH + "50" },
				{ "Vencimiento", WIDTH + "50" }, { "Concepto", WIDTH + "40" },
				{ "Número", WIDTH + "60" },
				{ "Importe Original", DERECHA + WIDTH + "70" },
				{ "Saldo", DERECHA + WIDTH + "70" }, };

		String prop = TABLA_TITULO + "";
		out = this.getTabla(cols, datos, prop);
		return out;
	}

}

class ReporteSaldosProveedoresDetallado extends ReporteYhaguy {

	RegisterDomain rr = RegisterDomain.getInstance();

	List<MyArray> saldosDetallados = new ArrayList<MyArray>();

	public ReporteSaldosProveedoresDetallado(List<MyArray> saldosDetallados) {
		this.saldosDetallados = saldosDetallados;
	}

	@Override
	public void informacionReporte() {

		this.setTitulo("Saldos Proveedores Detallado");
		this.setDirectorio("Proveedores");
		this.setNombreArchivo("Saldos con Proveedores Detallado -");
		try {
			this.setBody(this.getCuerpo());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * cabecera del reporte..
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ComponentBuilder getCuerpo() throws Exception {

		VerticalListBuilder out = cmp.verticalList();

		List<Object[]> datos2 = new ArrayList<Object[]>();
		List<CtaCteEmpresaMovimientoDTO> movimientosPendientes = new ArrayList<CtaCteEmpresaMovimientoDTO>();

		NumberFormat f = NumberFormat.getInstance();
		f.setGroupingUsed(false);
		String refinedNumber1;
		String refinedNumber3;
		String refinedNumber4;

		for (MyArray m : this.saldosDetallados) {

			MyArray estadoCuenta = (MyArray) m.getPos1();

			refinedNumber1 = f.format((double) estadoCuenta.getPos5());

			Empresa e = (Empresa) rr.getObject(Empresa.class.getName(),
					(long) estadoCuenta.getPos1());

			out.add(cmp
					.horizontalFlowList()
					.add(this.textoParValor("Nombre", e.getNombre()))
					.add(this.textoParValor("Razón Social", e.getRazonSocial()))
					.add(this.textoParValor("RUC", e.getRuc())));
			out.add(cmp
					.horizontalFlowList()
					.add(this.textoParValor("Apertura Cuenta", this.m
							.dateToString((Date) estadoCuenta.getPos3(),
									"dd-MM-yyyy")))
					.add(this.textoParValor("Estado Cuenta",
							((Tipo) estadoCuenta.getPos4()).getDescripcion()))
					.add(this.textoParValor("Pendiente Total Gs.",
							refinedNumber1)));

			// Movimientos
			movimientosPendientes = (List<CtaCteEmpresaMovimientoDTO>) m
					.getPos2();
			datos2 = new ArrayList<Object[]>();

			for (CtaCteEmpresaMovimientoDTO mov : movimientosPendientes) {

				refinedNumber3 = f.format(mov.getImporteOriginal());
				refinedNumber4 = f.format(mov.getSaldo());
				Object[] movimiento = {
						this.m.dateToString(mov.getFechaEmision(), "dd-MM-yyyy"),
						this.m.dateToString(mov.getFechaVencimiento(),
								"dd-MM-yyyy"), mov.getSucursal().getPos1(),
						mov.getTipoMovimiento().getPos1(),
						mov.getNroComprobante(), mov.getMoneda().getText(),
						refinedNumber3, refinedNumber4 };
				datos2.add(movimiento);
			}
			out.add(this.getTablaMovimientos(datos2));
			out.add(cmp.horizontalFlowList().add(this.texto("")));
			out.add(cmp.horizontalFlowList().add(this.texto("")));
			out.add(cmp.horizontalFlowList().add(this.texto("")));

		}

		return out;

	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder getTablaMovimientos(List<Object[]> datos) {

		ComponentBuilder out = null;

		String[][] cols = { { "Emisión", WIDTH + "50" },
				{ "Vencimiento", WIDTH + "60" }, { "Sucursal", WIDTH + "70" },
				{ "Tipo Movimiento", WIDTH + "60" },
				{ "Nro. de Comprobante", WIDTH + "60" },
				{ "Moneda", WIDTH + "50" },
				{ "Importe Original", DERECHA + WIDTH + "70" },
				{ "Saldo", DERECHA + WIDTH + "70" }, };

		String prop = TABLA_TITULO + "";
		out = this.getTabla(cols, datos, prop);
		return out;
	}

}

/**
 * Reporte de Ventas y Utilidad por Factura VEN-00001..
 */
class ReporteVentasUtilidadPorFactura extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	
	private Date desde;
	private Date hasta;
	private String sucursal;
	private String excluidos;
	private double promedio;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Concepto", TIPO_STRING, 50);
	static DatosColumnas col4 = new DatosColumnas("Costo S/iva", TIPO_DOUBLE, 35, true);
	static DatosColumnas col5 = new DatosColumnas("Importe S/iva", TIPO_DOUBLE, 35, true);
	static DatosColumnas col6 = new DatosColumnas("Rentabilidad", TIPO_DOUBLE_DS, 35);

	public ReporteVentasUtilidadPorFactura(Date desde, Date hasta, String sucursal, double promedio, String excluidos) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.promedio = promedio;
		this.excluidos = excluidos;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ventas y Utilidad por Factura");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Promedio Rentabilidad", Utiles.getNumberFormatDs(this.promedio)))
				.add(this.textoParValor("Excluídos", this.excluidos))
				.add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ventas y Utilidad por Articulo VEN-00002..
 */
class ReporteVentasUtilidadPorArticulo extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	
	private Date desde;
	private Date hasta;
	private String sucursal;
	private double promedio;
	private String articulo;
	private String cliente;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Concepto", TIPO_STRING, 50);
	static DatosColumnas col3_ = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col4_0 = new DatosColumnas("Costo Unit.", TIPO_DOUBLE, 35);
	static DatosColumnas col4_1 = new DatosColumnas("Cant.", TIPO_LONG, 15);
	static DatosColumnas col4 = new DatosColumnas("Costo S/iva", TIPO_DOUBLE, 35, true);
	static DatosColumnas col5 = new DatosColumnas("Importe S/iva", TIPO_DOUBLE, 35, true);
	static DatosColumnas col6 = new DatosColumnas("Rentabilidad", TIPO_DOUBLE_DS, 35);

	public ReporteVentasUtilidadPorArticulo(Date desde, Date hasta, 
			String sucursal, double promedio, String articulo, String cliente) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.promedio = promedio;
		this.articulo = articulo;
		this.cliente = cliente.toUpperCase();
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col3_);
		cols.add(col4_0);
		cols.add(col4_1);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ventas y Utilidad por Artículo");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Promedio Rentabilidad", Utiles.getNumberFormatDs(this.promedio)))
				.add(this.textoParValor("Artículo", this.articulo))
				.add(this.textoParValor("Cliente", this.cliente)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}


/**
 * Reporte de Compras Generico COM-00001..
 */
class ReporteComprasGenerico extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 25);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 45);
	static DatosColumnas col2 = new DatosColumnas("Cond.", TIPO_STRING, 25);
	static DatosColumnas col3 = new DatosColumnas("Suc.", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col6 = new DatosColumnas("Importe", TIPO_DOUBLE, 30, true);

	public ReporteComprasGenerico(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Compras");
		this.setDirectorio("compras");
		this.setNombreArchivo("Compra-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Gastos Generico COM-00002..
 */
class ReporteGastosGenerico extends ReporteYhaguy {

	Date desde;
	Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 25);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 35);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Ruc", TIPO_STRING, 25);
	static DatosColumnas col4 = new DatosColumnas("Importe", TIPO_DOUBLE, 25,
			true);

	public ReporteGastosGenerico(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Gastos");
		this.setDirectorio("compras");
		this.setNombreArchivo("Compra-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))).add(
				this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Compras Importacion COM-00006..
 */
class ReporteComprasImportacion extends ReporteYhaguy {

	private Date desde;
	private Date hasta;
	private String proveedor;
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col1 = new DatosColumnas("Nro.Importacion", TIPO_STRING, 45);
	static DatosColumnas col2 = new DatosColumnas("Nro.Factura", TIPO_STRING, 45);
	static DatosColumnas col3 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("T.Cambio Gs.", TIPO_DOUBLE, 40);
	static DatosColumnas col6 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE, 50, true);
	static DatosColumnas col7 = new DatosColumnas("Importe Ds.", TIPO_DOUBLE, 40, true);

	public ReporteComprasImportacion(Date desde, Date hasta, String proveedor) {
		this.desde = desde;
		this.hasta = hasta;
		this.proveedor = proveedor;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Compras Importacion");
		this.setDirectorio("compras");
		this.setNombreArchivo("Compra-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Proveedor", this.proveedor))
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Compras por Articulo COM-00009..
 */
class ReporteComprasPorArticulo extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;
	private String stock1 = "";
	private String stock2 = "";

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Familia", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cantidad", TIPO_LONG, 25);
	static DatosColumnas col4 = new DatosColumnas("Cant. Ult.Compra", TIPO_INTEGER, 25);
	static DatosColumnas col5 = new DatosColumnas("Fecha Ult.Compra", TIPO_STRING, 25);
	static DatosColumnas col6 = new DatosColumnas("Stock 1", TIPO_LONG, 25);
	static DatosColumnas col7 = new DatosColumnas("Stock 2", TIPO_LONG, 25);

	public ReporteComprasPorArticulo(Date desde, Date hasta, String stock1, String stock2) {
		this.desde = desde;
		this.hasta = hasta;
		this.stock1 = stock1;
		this.stock2 = stock2;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("compras");
		this.setNombreArchivo("CompraArticulo-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList()
				.add(this.texto(""))
				.add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Stock 1", this.stock1.toUpperCase()))
				.add(this.textoParValor("Stock 2", this.stock2.toUpperCase())));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Compras por Articulo COM-00010..
 */
class ReporteDetalleComprasLocales extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;
	private String proveedor;
	private String familia;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Factura", TIPO_STRING, 45);
	static DatosColumnas col3 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Familia", TIPO_STRING, 40);
	static DatosColumnas col5 = new DatosColumnas("Cant.", TIPO_INTEGER, 20);
	static DatosColumnas col6 = new DatosColumnas("Precio", TIPO_DOUBLE, 35);
	static DatosColumnas col7 = new DatosColumnas("Dscto.", TIPO_DOUBLE, 25);
	static DatosColumnas col8 = new DatosColumnas("Importe", TIPO_DOUBLE, 40, true);

	public ReporteDetalleComprasLocales(Date desde, Date hasta, String proveedor, String familia) {
		this.desde = desde;
		this.hasta = hasta;
		this.proveedor = proveedor;
		this.familia = familia;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("compras");
		this.setNombreArchivo("CompraDetalle-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Proveedor", this.proveedor))
				.add(this.textoParValor("Familia", this.familia)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Cobranzas TES-00016..
 */
class ReporteCobranzas extends ReporteYhaguy {
	
	private String sucursal;
	private String desde;
	private String hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 40);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Ruc", TIPO_STRING, 30);
	static DatosColumnas col5 = new DatosColumnas("Importe", TIPO_DOUBLE, 40,
			true);

	public ReporteCobranzas(String desde, String hasta, String sucursal) {
		this.sucursal = sucursal;
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Cobranzas");
		this.setDirectorio("recibos");
		this.setNombreArchivo("Cobro-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", this.desde))
				.add(this.textoParValor("Hasta", this.hasta))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ventas y Utilidad VEN-00002..
 */
class ReporteVentasUtilidad extends ReporteYhaguy {

	Date desde;
	Date hasta;
	double promedioRentabilidad = 0;
	String vendedor;
	String cliente;
	String proveedor;
	String articulo;
	String sucursal;
	String excluirPyAutopartes;
	String excluirIcaturbo;
	String promediarCosto;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Fac Nro.", TIPO_STRING, 50);
	static DatosColumnas col1_0 = new DatosColumnas("Tipo", TIPO_STRING, 35);
	static DatosColumnas col1_1 = new DatosColumnas("Costo Gs.", TIPO_DOUBLE_GS, 40, true);
	static DatosColumnas col1_1_ = new DatosColumnas("Costo Tot. Gs.", TIPO_DOUBLE_GS, 40, true);
	static DatosColumnas col1_2 = new DatosColumnas("Precio Gs.", TIPO_DOUBLE_GS, 40, true);
	static DatosColumnas col1_3 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 40, true);
	static DatosColumnas col2 = new DatosColumnas("Cant.", TIPO_LONG, 20, true);
	static DatosColumnas col3 = new DatosColumnas("Rent.", TIPO_DOUBLE_DS, 25, true);

	public ReporteVentasUtilidad(double rentabilidad, Date desde, Date hasta,
			String vendedor, String cliente, String proveedor, String articulo, 
			String sucursal, String excluirPyAutopartes,
			String excluirIcaturbo, String promediarCosto) {
		this.promedioRentabilidad = rentabilidad;
		this.desde = desde;
		this.hasta = hasta;
		this.vendedor = vendedor;
		this.cliente = cliente;
		this.proveedor = proveedor;
		this.articulo = articulo;
		this.sucursal = sucursal;
		this.excluirPyAutopartes = excluirPyAutopartes;
		this.excluirIcaturbo = excluirIcaturbo;
		this.promediarCosto = promediarCosto;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col1_0);
		cols.add(col2);
		cols.add(col1_1);
		cols.add(col1_1_);
		cols.add(col1_2);
		cols.add(col1_3);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Informe de Rentabilidad por Artículo");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Vendedor", this.vendedor.toUpperCase()))
				.add(this.textoParValor("Proveedor",
						this.proveedor.toUpperCase()))
				.add(this.textoParValor("Cliente", this.cliente.toUpperCase())));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Artículo", this.articulo.toUpperCase()))
				.add(this.textoParValor("Promedio Rentabilidad",
						String.valueOf(this.promedioRentabilidad)))
				.add(this.textoParValor("Promediar Costo", this.promediarCosto)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Excluye Py Autopartes / Acc. Sur", this.excluirPyAutopartes))
				.add(this.textoParValor("Excluye Icaturbo / NF. Autorrepuestos", this.excluirIcaturbo))
				.add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Listado de Transferencias Externas STK-00002..
 */
class ReporteTransferenciasExternas extends ReporteYhaguy {

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 50);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Origen", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Destino", TIPO_STRING, 40);
	static DatosColumnas col3_ = new DatosColumnas("Nro. Remisión", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE,
			true);

	public ReporteTransferenciasExternas() {
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col3_);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Transferencias Externas");
		this.setDirectorio("transferencias");
		this.setNombreArchivo("Transf-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Listado de Stock Valorizado STK-00003..
 */
class ReporteStockValorizado extends ReporteYhaguy {

	double total = 0;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col1 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Costo Gs", TIPO_DOUBLE, 30);
	static DatosColumnas col3 = new DatosColumnas("Stock", TIPO_LONG, 30);
	static DatosColumnas col4 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE,
			35, true);

	public ReporteStockValorizado(double total) {
		this.total = total;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Stock Valorizado");
		this.setDirectorio("Articulos");
		this.setNombreArchivo("Stock-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Cobranzas por Vendedor VEN-00005..
 */
class ReporteCobranzasPorVendedor extends ReporteYhaguy {

	private Date desde;
	private Date hasta;
	private String vendedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col1 = new DatosColumnas("Recibo", TIPO_STRING, 45);
	static DatosColumnas col2 = new DatosColumnas("Factura", TIPO_STRING, 45);
	static DatosColumnas col3 = new DatosColumnas("Monto Gs.", TIPO_DOUBLE_GS, 40, true);
	static DatosColumnas col4 = new DatosColumnas("Monto Gs. S/iva", TIPO_DOUBLE_GS, 40, true);

	public ReporteCobranzasPorVendedor(Date desde, Date hasta, String vendedor) {
		this.desde = desde;
		this.hasta = hasta;
		this.vendedor = vendedor;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("recibos");
		this.setNombreArchivo("Cobro-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Vendedor", this.vendedor)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ventas por Proveedor VEN-00006..
 */
class ReporteVentasPorProveedor extends ReporteYhaguy {

	private Date desde;
	private Date hasta;
	private String sucursal;
	private String proveedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Código", TIPO_STRING, 40);
	static DatosColumnas col1 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Cant. Vtas", TIPO_LONG, 25, true);
	static DatosColumnas col3 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 40, true);

	public ReporteVentasPorProveedor(Date desde, Date hasta, String proveedor, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.proveedor = proveedor;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ventas por Proveedor");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Vta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Proveedor", this.proveedor))
				.add(this.texto(""))
				.add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Notas de Credito VEN-00007..
 */
class ReporteNotasDeCredito extends ReporteYhaguy {

	private Date desde;
	private Date hasta;
	private String motivo;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Tipo", TIPO_STRING, 15);
	static DatosColumnas col3 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Ruc", TIPO_STRING, 25);
	static DatosColumnas col5 = new DatosColumnas("Factura", TIPO_STRING, 40);
	static DatosColumnas col6 = new DatosColumnas("Vend.", TIPO_STRING, 20);
	static DatosColumnas col7 = new DatosColumnas("Mot.", TIPO_STRING, 20);
	static DatosColumnas col8 = new DatosColumnas("Importe Gs.",
			TIPO_DOUBLE_GS, 30, true);

	public ReporteNotasDeCredito(Date desde, Date hasta, String motivo) {
		this.desde = desde;
		this.hasta = hasta;
		this.motivo = motivo;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Notas de Crédito");
		this.setDirectorio("notascredito");
		this.setNombreArchivo("NCR-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Motivo", this.motivo)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ventas Por Hora VEN-00003..
 */
class ReporteVentasPorHora extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String vendedor;
	private double promedio;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Horario", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Cantidad", TIPO_INTEGER, 50);
	static DatosColumnas col2 = new DatosColumnas("Importe Gs.",
			TIPO_DOUBLE_GS, true);

	public ReporteVentasPorHora(Date desde, Date hasta, String vendedor,
			double promedio, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.vendedor = vendedor;
		this.promedio = promedio;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Ventas Por Hora");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Promedio p/ Hora",
						FORMATTER.format(this.promedio)))
				.add(this.textoParValor("Vendedor", this.vendedor)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Costo de Ventas VEN-00008..
 */
class ReporteCostoVentas extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	
	private Date desde;
	private Date hasta;
	private String sucursal;
	private String tipoCosto;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Concepto", TIPO_STRING, 50);
	static DatosColumnas col4 = new DatosColumnas("Costo S/iva", TIPO_DOUBLE, 35, true);

	public ReporteCostoVentas(Date desde, Date hasta, String sucursal, String tipoCosto) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.tipoCosto = tipoCosto;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Costo de Ventas por Factura");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Tipo Costo", this.tipoCosto))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Costo de Ventas VEN-..
 */
class ReporteCostoVentasDetallado extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	
	private Date desde;
	private Date hasta;
	private String sucursal;
	private String tipoCosto;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Concepto", TIPO_STRING, 50);
	static DatosColumnas col4 = new DatosColumnas("Codigo", TIPO_STRING, 50);
	static DatosColumnas col5 = new DatosColumnas("Costo unit. S/iva", TIPO_DOUBLE, 35);
	static DatosColumnas col6 = new DatosColumnas("Total S/iva", TIPO_DOUBLE, 35, true);
	
	public ReporteCostoVentasDetallado(Date desde, Date hasta, String sucursal, String tipoCosto) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.tipoCosto = tipoCosto;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Costo de Ventas por Factura Detallado");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Tipo Costo", this.tipoCosto))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * DataSource de Saldos de Clientes detallado..
 */
class CtaCteSaldosDataSource_ implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	HashMap<String, Double> totalSaldo = new HashMap<String, Double>();
	HashMap<String, Double> totalVencido = new HashMap<String, Double>();
	HashMap<String, Double> totalAvencer = new HashMap<String, Double>();
	Misc misc = new Misc();

	private double totalVencidoClientes = 0;
	private double totalAvencerClientes = 0;
	
	/**
	 * [0]:idMovimientoOriginal [1]:tipoMovimiento.id
	 * [2]:nrocomprobante [3]:tipoMovimiento.descripcion
	 * [4]:telefono [5]:direccion
	 * [6]:emision [7]:vencimiento
	 * [8]:importe [9]:saldo acum
	 * [10]:razonSocial [11]:ruc
	 * [12]:saldo [13]:tipoMovimiento.sigla
	 */
	public CtaCteSaldosDataSource_(List<Object[]> movims, List<Object[]> aux) {
		
		this.values = aux;
		
		for (Object[] det : movims) {
			double saldo = (double) det[12];
			Date vencimiento = (Date) det[7];
			String siglaTm = (String) det[13];
			String razonSocial = (String) det[10];
			Double saldo_acum = this.totalSaldo.get(razonSocial);
			Double venc = this.totalVencido.get(razonSocial);
			Double aven = this.totalAvencer.get(razonSocial);
			
			if (saldo_acum != null) {
				saldo_acum += this.getSaldoFinal(saldo, siglaTm);
				this.totalSaldo.put(razonSocial, saldo_acum);
			} else {
				this.totalSaldo.put(razonSocial, this.getSaldoFinal(saldo, siglaTm));
			}
			
			if (venc == null && this.isVencido(vencimiento, saldo, siglaTm)) {
				this.totalVencido.put(razonSocial, this.getSaldoFinal(saldo, siglaTm));
			} else if (venc != null && this.isVencido(vencimiento, saldo, siglaTm)) {
				this.totalVencido.put(razonSocial, venc + this.getSaldoFinal(saldo, siglaTm));
			}

			if (aven == null && !this.isVencido(vencimiento, saldo, siglaTm)) {
				this.totalAvencer.put(razonSocial, this.getSaldoFinal(saldo, siglaTm));
			} else if (aven != null && !this.isVencido(vencimiento, saldo, siglaTm)) {
				this.totalAvencer.put(razonSocial, aven + this.getSaldoFinal(saldo, siglaTm));
			}
			
			if (this.isVencido(vencimiento, saldo, siglaTm)) {
				this.totalVencidoClientes += this.getSaldoFinal(saldo, siglaTm);
			} else {
				this.totalAvencerClientes += this.getSaldoFinal(saldo, siglaTm);
			}
		}
		
		Collections.sort(this.values, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String val1 = (String) o1[10];
				String val2 = (String) o2[10];			
				int compare = val1.compareTo(val2);
				if (compare == 0) {
					Date emision1 = (Date) o1[6];
					Date emision2 = (Date) o2[6];
		            return emision1.compareTo(emision2);
		        }
		        else {
		            return compare;
		        }
			}
		});
	}
	
	/**
	 * @return true si es vencido..
	 */
	private boolean isVencido(Date fechaVencimiento, double saldo, String siglaTm) {
		if (fechaVencimiento == null || (!this.isMovimientoCredito(siglaTm)) || saldo <= 1) {
			return false;
		}
		Date hoy = new Date();
		int cmp = hoy.compareTo(fechaVencimiento);
		return cmp >= 0;
	}
	
	/**
	 * @return true si es movimiento credito..
	 */
	private boolean isMovimientoCredito(String siglaTm) {
		if (siglaTm == null) return false;
		return siglaTm.equals(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO)
				|| siglaTm.equals(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO)
				|| siglaTm.equals(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO);
	}
	
	/**
	 * @return el saldo..
	 */
	private double getSaldoFinal(double saldo, String siglaTm) {
		if (this.isNotaCreditoVenta(siglaTm)) {
			return saldo * -1;
		}
		return saldo;
	}
	
	/**
	 * @return true si es nota de credito..
	 */
	private boolean isNotaCreditoVenta(String siglaTm) {
		if (siglaTm == null) return false;
		return siglaTm.equals(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA);
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);
		String nrocomprobante = (String) det[2];
		String tipomovimiento = (String) det[3];
		String telefono = (String) det[4];
		String direccion = (String) det[5];
		Date emision = (Date) det[6];
		Date vencimiento = (Date) det[7];
		double importe = (double) det[8];
		double saldo = (double) det[9];
		String razonSocial = (String) det[10];
		String ruc = (String) det[11];
		String siglaTm = (String) det[13];
		
		if ("Numero".equals(fieldName)) {
			if (nrocomprobante != null) {
				int lenght = nrocomprobante.length();
				value = nrocomprobante.substring(0, lenght < 15? lenght : 15);
			}			
		} else if ("Concepto".equals(fieldName)) {
			value = tipomovimiento;
		} else if ("Telefono".equals(fieldName)) {
			value = telefono;
		} else if ("Direccion".equals(fieldName)) {
			value = direccion;
		} else if ("Emision".equals(fieldName)) {
			value = Utiles.getDateToString(emision, Utiles.DD_MM_YY);
		} else if ("Vencimiento".equals(fieldName)) {
			value = Utiles.getDateToString(vencimiento, Utiles.DD_MM_YY);;
		} else if ("Importe".equals(fieldName)) {
			value = FORMATTER.format(importe);
		} else if ("SaldoGs".equals(fieldName)) {
			value = FORMATTER.format(this.getSaldoFinal(saldo, siglaTm));
		} else if ("TituloDetalle".equals(fieldName)) {
			value = razonSocial.toUpperCase() + " - " + ruc;
		} else if ("TotalImporte".equals(fieldName)) {
			double total = this.totalSaldo.get(razonSocial);
			value = FORMATTER.format(total);
		} else if ("TotalVencido".equals(fieldName)) {
			Double total = this.totalVencido.get(razonSocial);
			value = total == null ? "0" : FORMATTER.format(total);
		} else if ("TotalAvencer".equals(fieldName)) {
			Double total = this.totalAvencer.get(razonSocial);
			value = total == null ? "0" : FORMATTER.format(total);
		} else if ("TotalAvencerClientes".equals(fieldName)) {
			value = FORMATTER.format(this.totalAvencerClientes);
		} else if ("TotalVencidoClientes".equals(fieldName)) {
			value = FORMATTER.format(this.totalVencidoClientes);
		} else if ("TotalSaldo".equals(fieldName)) {
			value = FORMATTER.format(this.totalVencidoClientes + this.totalAvencerClientes);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Saldos de Clientes detallado DHS..
 */
class CtaCteSaldosDHSDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, String> totalSaldo;
	double totalSaldo_ = 0;
	
	double totalVentas = 0;
	double totalChequesRechazados = 0;
	double totalNotasCredito = 0;
	double totalRecibos = 0;
	double totalNotasDebito = 0;
	double totalReembolsosCheques = 0;
	
	/**
	 * [0]:emision
	 * [1]:hora
	 * [2]:numero
	 * [3]:concepto
	 * [4]:debe
	 * [5]:haber
	 * [6]:saldo
	 * [7]:razonsocial
	 * [8]:emision
	 */
	public CtaCteSaldosDHSDataSource(List<Object[]> values, String cliente, Map<String, String> totalSaldo,
			double totalVentas, double totalChequesRechazados, double totalNotasCredito, double totalRecibos,
			double totalNotasDebito, double totalReembolsosCheques) {
		this.values = values;
		this.totalSaldo = totalSaldo;
		for (String key : this.totalSaldo.keySet()) {
			String saldo = this.totalSaldo.get(key);
			totalSaldo_ += Double.parseDouble(saldo.replace(",", "").replace(".", ""));
		}
		this.totalVentas = totalVentas;
		this.totalChequesRechazados = totalChequesRechazados;
		this.totalNotasCredito = totalNotasCredito;
		this.totalRecibos = totalRecibos;
		this.totalNotasDebito = totalNotasDebito;
		this.totalReembolsosCheques = totalReembolsosCheques;
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);
		String fecha = (String) det[0];
		String hora = (String) det[1];
		String numero = (String) det[2];
		String concepto = (String) det[3];
		String debe = (String) det[4];
		String haber = (String) det[5];
		String saldo = (String) det[6];
		String razonsocial = (String) det[7];
		
		if ("fecha".equals(fieldName)) {
			value = fecha;
		} else if ("hora".equals(fieldName)) {
			value = hora;
		} else if ("numero".equals(fieldName)) {
			value = numero;
		} else if ("concepto".equals(fieldName)) {
			value = concepto;
		} else if ("debe".equals(fieldName)) {
			value = debe;
		} else if ("haber".equals(fieldName)) {
			value = haber;
		} else if ("saldo".equals(fieldName)) {
			value = saldo;
		} else if ("TituloDetalle".equals(fieldName)) {
			value = razonsocial;
		} else if ("totalimporte".equals(fieldName)) {
			value = this.totalSaldo.get(razonsocial);
		} else if ("TotalSaldo".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalSaldo_);
		}  else if ("TotalVentas".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalVentas);
		} else if ("TotalChequesRech".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalChequesRechazados);
		} else if ("TotalNotasCredito".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalNotasCredito);
		} else if ("TotalRecibos".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalRecibos);
		} else if ("TotalNotasDebito".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalNotasDebito);
		} else if ("TotalReembolsosCheques".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalReembolsosCheques);
		} else if ("TotalMigracion".equals(fieldName)) {
			value = Utiles.getNumberFormat(0.0);
		} else if ("TotalMigracionCh".equals(fieldName)) {
			value = Utiles.getNumberFormat(0.0);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Saldos de Clientes detallado..
 */
class CtaCteSaldosDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<MyArray> values = new ArrayList<MyArray>();
	HashMap<String, Double> totalSaldo = new HashMap<String, Double>();
	HashMap<String, Double> totalVencido = new HashMap<String, Double>();
	HashMap<String, Double> totalAvencer = new HashMap<String, Double>();
	Misc misc = new Misc();

	private double totalVencidoClientes = 0;
	private double totalAvencerClientes = 0;
	
	public CtaCteSaldosDataSource(List<CtaCteEmpresaMovimiento> movims, List<CtaCteEmpresaMovimiento> aux) {
		
		for (CtaCteEmpresaMovimiento movim : aux) {
			Empresa empresa = null;
			try {
				empresa = movim.getEmpresa();
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.values.add(new MyArray(empresa, movim));
		}
		
		for (CtaCteEmpresaMovimiento movim : movims) {
			Empresa empresa = null;
			try {
				empresa = movim.getEmpresa();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Double total = this.totalSaldo.get(empresa.getRazonSocial());
			Double venc = this.totalVencido.get(empresa.getRazonSocial());
			Double aven = this.totalAvencer.get(empresa.getRazonSocial());

			if (total == null) {
				this.totalSaldo.put(empresa.getRazonSocial(),
						movim.getSaldoFinal());
			} else {
				this.totalSaldo.put(empresa.getRazonSocial(), total
						+ movim.getSaldoFinal());
			}

			if (venc == null && movim.isVencido()) {
				this.totalVencido.put(empresa.getRazonSocial(),
						movim.getSaldoFinal());
			} else if (venc != null && movim.isVencido()) {
				this.totalVencido.put(empresa.getRazonSocial(), venc
						+ movim.getSaldoFinal());
			}

			if (aven == null && !movim.isVencido()) {
				this.totalAvencer.put(empresa.getRazonSocial(),
						movim.getSaldoFinal());
			} else if (aven != null && !movim.isVencido()) {
				this.totalAvencer.put(empresa.getRazonSocial(), aven
						+ movim.getSaldoFinal());
			}

			if (movim.isVencido()) {
				this.totalVencidoClientes += movim.getSaldoFinal();
			} else {
				this.totalAvencerClientes += movim.getSaldoFinal();
			}
		}
		Collections.sort(this.values, new Comparator<MyArray>() {
			@Override
			public int compare(MyArray o1, MyArray o2) {
				Empresa val1 = (Empresa) o1.getPos1();
				Empresa val2 = (Empresa) o2.getPos1();
				int dateCompare = val1.getRazonSocial().compareTo(val2.getRazonSocial());				
				return dateCompare;
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		MyArray det = this.values.get(index);
		Empresa empresa = (Empresa) det.getPos1();
		CtaCteEmpresaMovimiento movim = (CtaCteEmpresaMovimiento) det.getPos2();

		if ("Concepto".equals(fieldName)) {
			value = movim.getNroComprobante().replace("(1/1)", "") + " - " + ""
					+ movim.getTipoMovimiento().getDescripcion();
		} else if ("Telefono".equals(fieldName)) {
			value = empresa.getTelefono();
		} else if ("Direccion".equals(fieldName)) {
			value = empresa.getDireccion();
		} else if ("Emision".equals(fieldName)) {
			value = misc.dateToString(movim.getFechaEmision(), Misc.DD_MM_YYYY);
		} else if ("Vencimiento".equals(fieldName)) {
			value = misc.dateToString(movim.getFechaVencimiento(),
					Misc.DD_MM_YYYY);
		} else if ("Importe".equals(fieldName)) {
			value = FORMATTER.format(movim.getImporteOriginalFinal());
		} else if ("SaldoGs".equals(fieldName)) {
			value = FORMATTER.format(movim.getSaldoFinal());
		} else if ("TituloDetalle".equals(fieldName)) {
			value = empresa.getRazonSocial() + " - " + empresa.getRuc();
		} else if ("TotalImporte".equals(fieldName)) {
			double total = this.totalSaldo.get(empresa.getRazonSocial());
			value = FORMATTER.format(total);
		} else if ("TotalVencido".equals(fieldName)) {
			Double total = this.totalVencido.get(empresa.getRazonSocial());
			value = total == null ? "0" : FORMATTER.format(total);
		} else if ("TotalAvencer".equals(fieldName)) {
			Double total = this.totalAvencer.get(empresa.getRazonSocial());
			value = total == null ? "0" : FORMATTER.format(total);
		} else if ("TotalAvencerClientes".equals(fieldName)) {
			value = FORMATTER.format(this.totalAvencerClientes);
		} else if ("TotalVencidoClientes".equals(fieldName)) {
			value = FORMATTER.format(this.totalVencidoClientes);
		} else if ("TotalSaldo".equals(fieldName)) {
			value = FORMATTER.format(this.totalVencidoClientes
					+ this.totalAvencerClientes);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Saldos de Clientes detallado periodo 2016..
 */
class CtaCteSaldosDataSource_2016 implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<MyArray> values = new ArrayList<MyArray>();
	HashMap<String, Double> totalSaldo = new HashMap<String, Double>();
	HashMap<String, Double> totalVencido = new HashMap<String, Double>();
	HashMap<String, Double> totalAvencer = new HashMap<String, Double>();
	Misc misc = new Misc();

	private double totalVencidoClientes = 0;
	private double totalAvencerClientes = 0;
	
	public CtaCteSaldosDataSource_2016(List<CtaCteEmpresaMovimiento_2016> movims, 
			List<CtaCteEmpresaMovimiento_2016> aux, boolean aseguradora) {
		
		for (CtaCteEmpresaMovimiento_2016 movim : aux) {
			Empresa empresa = null;
			try {
				empresa = movim.getEmpresa();
				if ((!(movim.isCaracterProveedor() && empresa.isAseguradora() && !aseguradora))
						|| (movim.isCaracterProveedor() && empresa.isAseguradora() && aseguradora)) {
					this.values.add(new MyArray(empresa, movim));
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (CtaCteEmpresaMovimiento_2016 movim : movims) {
			Empresa empresa = null;
			try {
				empresa = movim.getEmpresa();
				
				if ((!(movim.isCaracterProveedor() && empresa.isAseguradora()))
						|| (movim.isCaracterProveedor() && empresa.isAseguradora() && aseguradora)) {
					Double total = this.totalSaldo.get(empresa.getRazonSocial());
					Double venc = this.totalVencido.get(empresa.getRazonSocial());
					Double aven = this.totalAvencer.get(empresa.getRazonSocial());

					if (total == null) {
						this.totalSaldo.put(empresa.getRazonSocial(),
								movim.getSaldoFinal());
					} else {
						this.totalSaldo.put(empresa.getRazonSocial(), total
								+ movim.getSaldoFinal());
					}

					if (venc == null && movim.isVencido()) {
						this.totalVencido.put(empresa.getRazonSocial(),
								movim.getSaldoFinal());
					} else if (venc != null && movim.isVencido()) {
						this.totalVencido.put(empresa.getRazonSocial(), venc
								+ movim.getSaldoFinal());
					}

					if (aven == null && !movim.isVencido()) {
						this.totalAvencer.put(empresa.getRazonSocial(),
								movim.getSaldoFinal());
					} else if (aven != null && !movim.isVencido()) {
						this.totalAvencer.put(empresa.getRazonSocial(), aven
								+ movim.getSaldoFinal());
					}

					if (movim.isVencido()) {
						this.totalVencidoClientes += movim.getSaldoFinal();
					} else {
						this.totalAvencerClientes += movim.getSaldoFinal();
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		Collections.sort(this.values, new Comparator<MyArray>() {
			@Override
			public int compare(MyArray o1, MyArray o2) {
				Empresa val1 = (Empresa) o1.getPos1();
				Empresa val2 = (Empresa) o2.getPos1();
				int dateCompare = val1.getRazonSocial().compareTo(val2.getRazonSocial());				
				return dateCompare;
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		MyArray det = this.values.get(index);
		Empresa empresa = (Empresa) det.getPos1();
		CtaCteEmpresaMovimiento_2016 movim = (CtaCteEmpresaMovimiento_2016) det.getPos2();

		if ("Concepto".equals(fieldName)) {
			value = movim.getNroComprobante().replace("(1/1)", "") + " - " + ""
					+ movim.getTipoMovimiento().getDescripcion();
		} else if ("Telefono".equals(fieldName)) {
			value = empresa.getTelefono();
		} else if ("Direccion".equals(fieldName)) {
			value = empresa.getDireccion();
		} else if ("Emision".equals(fieldName)) {
			value = misc.dateToString(movim.getFechaEmision(), Misc.DD_MM_YYYY);
		} else if ("Vencimiento".equals(fieldName)) {
			value = misc.dateToString(movim.getFechaVencimiento(),
					Misc.DD_MM_YYYY);
		} else if ("Importe".equals(fieldName)) {
			value = FORMATTER.format(movim.getImporteOriginalFinal());
		} else if ("SaldoGs".equals(fieldName)) {
			value = FORMATTER.format(movim.getSaldoFinal());
		} else if ("TituloDetalle".equals(fieldName)) {
			value = empresa.getRazonSocial() + " - " + empresa.getRuc();
		} else if ("TotalImporte".equals(fieldName)) {
			double total = this.totalSaldo.get(empresa.getRazonSocial());
			value = FORMATTER.format(total);
		} else if ("TotalVencido".equals(fieldName)) {
			Double total = this.totalVencido.get(empresa.getRazonSocial());
			value = total == null ? "0" : FORMATTER.format(total);
		} else if ("TotalAvencer".equals(fieldName)) {
			Double total = this.totalAvencer.get(empresa.getRazonSocial());
			value = total == null ? "0" : FORMATTER.format(total);
		} else if ("TotalAvencerClientes".equals(fieldName)) {
			value = FORMATTER.format(this.totalAvencerClientes);
		} else if ("TotalVencidoClientes".equals(fieldName)) {
			value = FORMATTER.format(this.totalVencidoClientes);
		} else if ("TotalSaldo".equals(fieldName)) {
			value = FORMATTER.format(this.totalVencidoClientes
					+ this.totalAvencerClientes);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Cobros a Clientes detallado..
 */
class CobrosDetalladoDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Recibo> values = new ArrayList<Recibo>();
	Map<String, Double> totales = new HashMap<String, Double>();
	double totalCobrado = 0;
	Misc misc = new Misc();

	public CobrosDetalladoDataSource(List<Recibo> values) {
		this.values = values;
		for (Recibo recibo : values) {
			Double total = this.totales.get(recibo.getCliente().getRazonSocial());
			if (total == null) {
				this.totales.put(recibo.getCliente().getRazonSocial(),
						recibo.getTotalImporteGs());
			} else {
				this.totales.put(recibo.getCliente().getRazonSocial(), total
						+ recibo.getTotalImporteGs());
			}
		}		
		for (String key : this.totales.keySet()) {
			this.totalCobrado += this.totales.get(key);
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Recibo det = this.values.get(index);
		List<BeanFormaPago> fps = new ArrayList<BeanFormaPago>();
		for (ReciboFormaPago fp : det.getFormasPago()) {
			fps.add(new BeanFormaPago(misc.dateToString(det.getFechaEmision(),
					Misc.DD_MM_YYYY), det.getNumero(), fp.getTipo()
					.getDescripcion(), fp.getMontoGs_()));
		}

		if ("TituloDetalle".equals(fieldName)) {
			value = det.getCliente().getRazonSocial();
		} else if ("FormasPago".equals(fieldName)) {
			value = fps;
		} else if ("Facturas".equals(fieldName)) {
			value = det.getDetalles();
		} else if ("TotalImporte".equals(fieldName)) {
			Double total = this.totales.get(det.getCliente().getRazonSocial());
			value = total == null ? "0" : FORMATTER.format(total);
		} else if ("TotalCobro".equals(fieldName)) {
			value = FORMATTER.format(totalCobrado);
		}

		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Saldos de Clientes detallado por familia..
 */
class CtaCteSaldosFamiliaDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	HashMap<String, Double> totalSaldo = new HashMap<String, Double>();	
	
	/**
	 * [0]:idMovimientoOriginal [1]:tipoMovimiento.id
	 * [2]:nrocomprobante [3]:tipoMovimiento.descripcion
	 * [4]:telefono [5]:direccion
	 * [6]:emision [7]:vencimiento
	 * [8]:importe [9]:saldo acum
	 * [10]:razonSocial [11]:ruc
	 * [12]:saldo [13]:tipoMovimiento.sigla
	 */
	public CtaCteSaldosFamiliaDataSource(List<Object[]> movims) {
		
		this.values = movims;
		for (Object[] movim : movims) {
			String tipomovimiento = (String) movim[15];
			double saldo = (double) movim[9];
			Double total = this.totalSaldo.get(tipomovimiento);
			if (total != null) {
				total += saldo;
			} else {
				total = saldo;
			}
			this.totalSaldo.put(tipomovimiento, total);
		}
		
		Collections.sort(this.values, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String flia1 = (String) o1[15];
	        	String flia2 = (String) o2[15];
							
				int compare = flia1.compareTo(flia2);
				if (compare != 0) {
					return compare;
		        } else {
		        	Date emision1 = (Date) o1[6];
					Date emision2 = (Date) o2[6];
					return emision1.compareTo(emision2);
		        }
			}		
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);
		String nrocomprobante = (String) det[2];
		String tipomovimiento = (String) det[15]; //(String) det[3];
		String telefono = (String) det[4];
		String direccion = (String) det[5];
		Date emision = (Date) det[6];
		Date vencimiento = (Date) det[7];
		double importe = (double) det[8];
		double saldo = (double) det[9];
		String razonSocial = (String) det[10];
		
		if ("Numero".equals(fieldName)) {
			if (nrocomprobante != null) {
				int lenght = nrocomprobante.length();
				value = nrocomprobante.substring(0, lenght < 15? lenght : 15);
			}			
		} else if ("Concepto".equals(fieldName)) {
			value = razonSocial;
		} else if ("Telefono".equals(fieldName)) {
			value = telefono;
		} else if ("Direccion".equals(fieldName)) {
			value = direccion;
		} else if ("Emision".equals(fieldName)) {
			value = Utiles.getDateToString(emision, Utiles.DD_MM_YY);
		} else if ("Vencimiento".equals(fieldName)) {
			value = Utiles.getDateToString(vencimiento, Utiles.DD_MM_YY);;
		} else if ("Importe".equals(fieldName)) {
			value = FORMATTER.format(importe);
		} else if ("SaldoGs".equals(fieldName)) {
			value = FORMATTER.format(saldo);
		} else if ("TituloDetalle".equals(fieldName)) {
			value = tipomovimiento;
		} else if ("TotalImporte".equals(fieldName)) {
			double total = this.totalSaldo.get(tipomovimiento);
			value = FORMATTER.format(total);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Libro Ventas ..
 */
class LibroVentasDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	Misc misc = new Misc();

	List<Venta> ventas;
	List<NotaCredito> notasCredito;
	List<NotaDebito> notasDebito;
	List<BeanLibroVenta> values = new ArrayList<BeanLibroVenta>();
	Date desde;
	Date hasta;

	double totalContado = 0;
	double totalCredito = 0;
	double totalNCContado = 0;
	double totalNCCredito = 0;
	double totalNDebito = 0;

	double totalGravada = 0;
	double totalGravada5 = 0;
	double totalImpuesto = 0;
	double totalImpuesto5 = 0;
	double totalImporte = 0;
	double totalExenta = 0;

	public LibroVentasDataSource(List<Venta> ventas,
			List<NotaCredito> notasCredito, List<NotaDebito> notasDebito, Date desde, Date hasta) {
		this.notasCredito = notasCredito;
		this.ventas = ventas;
		this.notasDebito = notasDebito;
		this.desde = desde;
		this.hasta = hasta;
		this.loadDatos();
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();

		if ("Ventas".equals(fieldName)) {
			value = values;
		} else if ("Desde".equals(fieldName)) {
			value = misc.dateToString(this.desde, Misc.DD_MM_YYYY);
		} else if ("Hasta".equals(fieldName)) {
			value = misc.dateToString(this.hasta, Misc.DD_MM_YYYY);
		} else if ("RucEmpresa".equals(fieldName)) {
			value = "Ruc: " + com.yhaguy.gestion.reportes.formularios.ReportesViewModel.getRucEmpresa();
		} else if ("Periodo".equals(fieldName)) {
			value = "Correspondiente al " + misc.dateToString(this.desde, "MM/yyyy");
		} else if ("DireccionEmpresa".equals(fieldName)) {
			value = "Direccion: " + com.yhaguy.gestion.reportes.formularios.ReportesViewModel.getDireccionEmpresa();
		} else if ("TotalContado".equals(fieldName)) {
			value = FORMATTER.format(this.totalContado);
		} else if ("TotalCredito".equals(fieldName)) {
			value = FORMATTER.format(this.totalCredito);
		} else if ("TotalNCContado".equals(fieldName)) {
			value = FORMATTER.format(this.totalNCContado);
		} else if ("TotalNCCredito".equals(fieldName)) {
			value = FORMATTER.format(this.totalNCCredito);
		} else if ("TotalGravada".equals(fieldName)) {
			value = FORMATTER.format(this.totalGravada);
		} else if ("TotalGravada5".equals(fieldName)) {
			value = FORMATTER.format(this.totalGravada5);
		} else if ("TotalImpuesto".equals(fieldName)) {
			value = FORMATTER.format(this.totalImpuesto);
		} else if ("TotalImpuesto5".equals(fieldName)) {
			value = FORMATTER.format(this.totalImpuesto5);
		} else if ("TotalImporte".equals(fieldName)) {
			value = FORMATTER.format(this.totalImporte);
		} else if ("TotalExenta".equals(fieldName)) {
			value = FORMATTER.format(this.totalExenta);
		} else if ("TotalExenta".equals(fieldName)) {
			value = FORMATTER.format(this.totalExenta);
		} else if ("TotalNDebito".equals(fieldName)) {
			value = FORMATTER.format(this.totalNDebito);
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

	/**
	 * carga los datos para el reporte..
	 */
	private void loadDatos() {
		for (NotaDebito nd : this.notasDebito) {
			String fecha = misc.dateToString(nd.getFecha(), "dd-MM-yy");
			String concepto = "NOT-DEB";
			String numero = nd.getNumero();
			String razonSocial = nd.isAnulado() ? "ANULADO" : nd.getCliente().getRazonSocial();
			String ruc = nd.getCliente().getRuc();
			if (ruc.isEmpty()) ruc = Configuracion.RUC_EMPRESA_LOCAL;
			double grav5 = 0.0;
			double grav10 = nd.getTotalGravado10();
			double iva5 = 0.0;
			double iva10 = nd.getTotalIva10();
			double total = nd.getTotalImporteGs();
			values.add(new BeanLibroVenta(fecha, concepto, numero, razonSocial, ruc, grav10, iva10, grav5, iva5, total, 0.0, nd.getFecha()));
			this.totalGravada += (nd.getTotalGravado10());
			this.totalImpuesto += (nd.getTotalIva10());
			this.totalImporte += (nd.getTotalImporteGs());
			this.totalNDebito += (nd.getTotalImporteGs());
		}
		for (NotaCredito ncred : this.notasCredito) {
			String fecha = misc.dateToString(ncred.getFechaEmision(), "dd-MM-yy");
			String concepto = TipoMovimiento.getAbreviatura(ncred.getTipoMovimiento().getSigla());
			String numero = ncred.getNumero();
			String razonSocial = ncred.isAnulado() ? "ANULADO" : ncred.getCliente().getRazonSocial();
			String ruc = ncred.getCliente().getRuc();
			if (ruc.isEmpty()) ruc = Configuracion.RUC_EMPRESA_LOCAL;
			double iva10 = ncred.isAnulado() ? 0.0 : redondear(ncred.getTotalIva10()) * -1;
			double gravada = ncred.isAnulado() ? 0.0 : redondear(ncred.getTotalGravado10()) * -1;
			double exenta = ncred.isAnulado() ? 0.0 : redondear(ncred.getTotalExenta()) * -1;
			double importe = (iva10 + gravada + exenta);
			values.add(new BeanLibroVenta(fecha, concepto, numero, razonSocial,
					ncred.isAnulado() ? "" : ruc, gravada, iva10, 0.0, 0.0, importe, exenta, ncred.getFechaEmision()));
			if (ncred.isAnulado() == false) {
				this.totalGravada -= (ncred.getTotalGravado10());
				this.totalImpuesto -= (ncred.getTotalIva10());
				this.totalImporte -= (ncred.getTotalIva10() + ncred.getTotalGravado10() + ncred.getTotalExenta());
				this.totalExenta -= ncred.getTotalExenta();
				if (ncred.isNotaCreditoVentaContado()) {
					this.totalNCContado -= (ncred.getTotalIva10() + ncred.getTotalGravado10() + ncred.getTotalExenta());
				} else {
					this.totalNCCredito -= (ncred.getTotalIva10() + ncred.getTotalGravado10() + ncred.getTotalExenta());
				}
			}
		}

		for (Venta vta : this.ventas) {
			String fecha = misc.dateToString(vta.getFecha(), "dd-MM-yy");
			String concepto = TipoMovimiento.getAbreviatura(vta.getTipoMovimiento().getSigla());
			String numero = vta.getNumero();
			String razonSocial = vta.isAnulado() ? "ANULADO" : vta.getDenominacion();
			if (razonSocial == null) razonSocial = vta.getCliente().getRazonSocial();
			String ruc = vta.getCliente().getRuc();
			if (ruc.isEmpty()) ruc = Configuracion.RUC_EMPRESA_LOCAL;
			double iva10 = vta.isAnulado() ? 0.0 : redondear(vta.getTotalIva10());
			double gravada10 = vta.isAnulado() ? 0.0 : redondear(vta.getTotalGravado10());
			double iva5 = vta.isAnulado() ? 0.0 : redondear(vta.getTotalIva5());
			double gravada5 = vta.isAnulado() ? 0.0 : redondear(vta.getTotalGravado5());
			double exenta = vta.isAnulado() ? 0.0 : redondear(vta.getTotalExenta());
			double importe = vta.isAnulado() ? 0.0 : (iva10 + gravada10 + iva5 + gravada5 + exenta);
			values.add(new BeanLibroVenta(fecha, concepto, numero, razonSocial,
					vta.isAnulado() ? "" : ruc, gravada10, iva10, gravada5, iva5, importe, exenta, vta.getFecha()));
			if (vta.isAnulado() == false) {
				this.totalGravada += (vta.getTotalGravado10());
				this.totalGravada5 += (vta.getTotalGravado5());
				this.totalImpuesto += (vta.getTotalIva10());
				this.totalImpuesto5 += (vta.getTotalIva5());
				this.totalImporte += importe;
				this.totalExenta += vta.getTotalExenta();
				if (vta.isVentaContado()) {
					this.totalContado += (importe);
				} else {
					this.totalCredito += (importe);
				}
			}
		}
	}
	
	/**
	 * @return el monto redondeado..
	 */
	private static double redondear(double monto) {
		return Math.rint(monto * 1) / 1;
	}
}

/**
 * DataSource del Libro diario
 */
class LibroDiarioDataSource implements JRDataSource {
	
	Date fechaDesde;
	Date fechaHasta;
	
	public LibroDiarioDataSource(Date desde, Date hasta) {
		this.fechaDesde = desde;
		this.fechaHasta = hasta;
		try {
			this.obtenerValores();
			this.cargarValores();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	List<ContableAsiento> asientos = new ArrayList<ContableAsiento>();
	List<Object[]> asientos_ = new ArrayList<Object[]>();
	
	/**
	 * obtiene los datos de la bd..
	 */
	@SuppressWarnings("unchecked")
	private void obtenerValores() throws Exception {
		
		String desde = Utiles.getDateToString(fechaDesde, Misc.YYYY_MM_DD) + " 00:00:00";
		String hasta = Utiles.getDateToString(fechaHasta, Misc.YYYY_MM_DD) + " 23:00:00";
		
		RegisterDomain rr = RegisterDomain.getInstance();
		String query = "select a from ContableAsiento a where a.fecha >= '" + desde + "'"
				+ " and a.fecha <= '" + hasta + "' ";	
		query += "order by a.fecha, a.numero";
		this.asientos = rr.hql(query);
	}
	
	/**
	 * carga los datos al datasource..
	 */
	private void cargarValores() {
		for (ContableAsiento asiento : this.asientos) {
			for (ContableAsientoDetalle item : asiento.getDetallesOrdenadoPorDebe()) {

				String numero = asiento.getNumero() + "";
				String descripcion = asiento.getDescripcion();
				String fecha = Utiles.getDateToString(asiento.getFecha(), Utiles.DD_MM_YYYY);
				String ctaDesc = item.getCuenta().getDescripcion();
				String detDesc = item.getDescripcion();
				String debe = Utiles.getNumberFormat(item.getDebe());
				String haber = Utiles.getNumberFormat(item.getHaber());
				String totDebe = Utiles.getNumberFormat(item.getDebe());
				String totHaber = Utiles.getNumberFormat(item.getHaber());

				Object[] array = { numero, descripcion, fecha, ctaDesc,
						detDesc, debe, haber, totDebe, totHaber };
				this.asientos_.add(array);
			}
		}
	}
	
	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
        Object value = null;
        String fieldName = field.getName();
         
        if ("Asiento".equals(fieldName)) {
        	value = this.asientos_.get(index)[0];
        } else if ("Descripcion".equals(fieldName)) {
        	value = this.asientos_.get(index)[1];
		} else if ("Fecha".equals(fieldName)) {
			value = this.asientos_.get(index)[2];
		} else if ("Cuenta".equals(fieldName)) {
        	value = this.asientos_.get(index)[3];
        } else if ("Desc Cuenta".equals(fieldName)) {
        	value = this.asientos_.get(index)[4];
        } else if ("Debe".equals(fieldName)) {
        	value = this.asientos_.get(index)[5];
        } else if ("Haber".equals(fieldName)) {
        	value = this.asientos_.get(index)[6];
        } else if ("TotalDebe".equals(fieldName)) {
        	value = this.asientos_.get(index)[7];
		} else if ("TotalHaber".equals(fieldName)) {
			value = this.asientos_.get(index)[8];
		}           
        return value;
    }

	@Override
	public boolean next() throws JRException {
		if (index < asientos_.size() - 1) {
			index ++;
			return true;
		}
		return false;
	}		
}

/**
 * DataSource del Libro mayor..
 */
class LibroMayorDataSource implements JRDataSource {
	Date fechaDesde;
	Date fechaHasta;
	
	List<ContableAsiento> asientos = new ArrayList<ContableAsiento>();
	List<Object[]> asientos_ = new ArrayList<Object[]>();
	Map<String, List<Object[]>> mayor = new HashMap<String, List<Object[]>>();
	Map<String, Double> saldos = new HashMap<String, Double>();
	
	public LibroMayorDataSource(Date desde, Date hasta) {
		this.fechaDesde = desde;
		this.fechaHasta = hasta;
		try {
			this.obtenerValores();
			this.agruparValores();
			this.cargarValores();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * obtiene los datos de la bd..
	 */		
	@SuppressWarnings("unchecked")
	private void obtenerValores() throws Exception {

		String desde = Utiles.getDateToString(fechaDesde, Misc.YYYY_MM_DD) + " 00:00:00";
		String hasta = Utiles.getDateToString(fechaHasta, Misc.YYYY_MM_DD) + " 23:00:00";

		RegisterDomain rr = RegisterDomain.getInstance();
		String query = "select a from ContableAsiento a where a.fecha >= '"
				+ desde + "' and a.fecha <= '" + hasta + "' order by a.fecha, a.numero";
		this.asientos = rr.hql(query);
	}
	
	/**
	 * Agrupa los asientos segun la cuenta..
	 */
	private void agruparValores() {

		for (ContableAsiento asiento : this.asientos) {
			for (ContableAsientoDetalle item : asiento.getDetalles()) {
				double saldo_ = item.getDebe() - item.getHaber();

				String codigo = item.getCuenta().getCodigo();
				String descripcion = item.getCuenta().getDescripcion();
				String nroAsiento = asiento.getNumero() + "";
				String fecha = Utiles.getDateToString(asiento.getFecha(), Utiles.DD_MM_YYYY);
				String descAsiento = asiento.getDescripcion();
				String debe = Utiles.getNumberFormat(item.getDebe());
				String haber = Utiles.getNumberFormat(item.getHaber());
				String saldo = Utiles.getNumberFormat(saldo_);

				Object[] array = { codigo, descripcion, nroAsiento, fecha,
						descAsiento, debe, haber, saldo };

				List<Object[]> objects = this.mayor.get(codigo);					
				if (objects == null) {
					
					List<Object[]> arrays = new ArrayList<Object[]>();
					arrays.add(array);
					this.mayor.put(codigo, arrays);
					this.saldos.put(codigo, saldo_);
					
				} else {
					double saldoAcum = this.saldos.get(codigo);
					this.saldos.put(codigo, (saldoAcum + saldo_));
					double nuevoSaldo = this.saldos.get(codigo);
					array[7] = Utiles.getNumberFormat(nuevoSaldo);
					objects.add(array);
				}
			}
		}
	}
	
	/**
	 * carga los datos al datasource..
	 */
	private void cargarValores() {
		
		for (String codigo : this.mayor.keySet()) {
			List<Object[]> data = this.mayor.get(codigo);
			this.asientos_.addAll(data);
		}
		
		Collections.sort(this.asientos_, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[3];
				Date fecha2 = (Date) o2[3];
				int dateCompare = fecha1.compareTo(fecha2);
				
				if (dateCompare != 0) {
					return dateCompare;
				} else {
					int nro1 = (int) o1[2];
					int nro2 = (int) o2[2];
					return nro1 - nro2;
				}
			}
		});
	}
	
	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
        Object value = null;
        String fieldName = field.getName();
         
        if ("Cuenta".equals(fieldName)) {
        	value = this.asientos_.get(index)[0];
        } else if ("Desc Cuenta".equals(fieldName)) {
        	value = this.asientos_.get(index)[1];
        } else if ("Asiento".equals(fieldName)) {
        	value = this.asientos_.get(index)[2];
        } else if ("Fecha".equals(fieldName)) {
        	value = this.asientos_.get(index)[3];
        } else if ("Descripcion".equals(fieldName)) {
        	value = this.asientos_.get(index)[4];
        } else if ("Debe".equals(fieldName)) {
        	value = this.asientos_.get(index)[5];
		} else if ("Haber".equals(fieldName)) {
			value = this.asientos_.get(index)[6];
		} else if ("Saldo".equals(fieldName)) {
			value = this.asientos_.get(index)[7];
		} else if ("TotalSaldo".equals(fieldName)) {
			value = this.asientos_.get(index)[7];
		}  
        return value;
    }

	@Override
	public boolean next() throws JRException {
		if (index < asientos_.size() - 1) {
			index ++;
			return true;
		}
		return false;
	}		
}

/**
 * DataSource del Libro Compras
 */
class LibroComprasIndistintoDataSource implements JRDataSource {
	
	List<BeanLibroCompra> values = new ArrayList<BeanLibroCompra>();
	List<Gasto> gastos = new ArrayList<Gasto>();
	List<ImportacionFactura> importaciones = new ArrayList<ImportacionFactura>();
	List<Gasto> autofacturas = new ArrayList<Gasto>();
	List<Gasto> boletasVenta = new ArrayList<Gasto>();
	List<Gasto> gastosContado = new ArrayList<Gasto>();
	List<Gasto> gastosCredito = new ArrayList<Gasto>();
	List<Gasto> otrosComprobantes = new ArrayList<Gasto>();
	
	double autoFact_grav10 = 0;
	double autoFact_grav5 = 0;
	double autoFact_exenta = 0;
	double autoFact_iva10 = 0;
	double autoFact_iva5 = 0;
	
	double boleta_grav10 = 0;
	double boleta_grav5 = 0;
	double boleta_exenta = 0;
	double boleta_iva10 = 0;
	double boleta_iva5 = 0;
	
	double contado_grav10 = 0;
	double contado_grav5 = 0;
	double contado_exenta = 0;
	double contado_iva10 = 0;
	double contado_iva5 = 0;
	
	double credito_grav10 = 0;
	double credito_grav5 = 0;
	double credito_exenta = 0;
	double credito_iva10 = 0;
	double credito_iva5 = 0;
	
	double otros_grav10 = 0;
	double otros_grav5 = 0;
	double otros_exenta = 0;
	double otros_iva10 = 0;
	double otros_iva5 = 0;
	
	double nc_gravada10 = 0;
	double nc_gravada5 = 0;
	double nc_exenta = 0;
	double nc_iva10 = 0;
	double nc_iva5 = 0;
	
	double total_gravada10 = 0;
	double total_gravada5 = 0;
	double total_exenta = 0;
	double total_iva10 = 0;
	double total_iva5 = 0;
	double total_baseimponible = 0;
	
	public LibroComprasIndistintoDataSource(List<Gasto> gastos, List<ImportacionFactura> importaciones, List<NotaCredito> notasCredito, boolean despacho) {
		this.gastos = gastos;
		this.importaciones = importaciones;
		for (Gasto gasto : gastos) {
			String timbrado = gasto.getTimbrado();
			BeanLibroCompra value = new BeanLibroCompra(Utiles.getDateToString(gasto.getFecha(), Utiles.DD_MM_YYYY),
					Utiles.getDateToString(gasto.getModificado(), Utiles.DD_MM_YYYY), gasto.getNumeroFactura(),
					gasto.getTipoMovimiento().getDescripcion(), timbrado,
					gasto.getProveedor().getRazonSocial(), gasto.getProveedor().getRuc(), gasto.getGravada10(),
					gasto.getGravada5(), gasto.getIva10(), gasto.getIva5(), gasto.getExenta(),
					gasto.getIva5() + gasto.getIva10() + gasto.getExenta() + gasto.getGravada10() + gasto.getGravada5(),
					gasto.getBaseImponible(), gasto.getDescripcionCuenta1(), gasto.getFecha());
			values.add(value);
			if (gasto.isAutoFactura()) autofacturas.add(gasto);
			if (gasto.isBoletaVenta()) boletasVenta.add(gasto);
			if (gasto.isGastoContado()) gastosContado.add(gasto);
			if (gasto.isGastoCredito()) gastosCredito.add(gasto);
			if (gasto.isOtrosComprobantes()) otrosComprobantes.add(gasto);
			total_gravada10 += gasto.getGravada10();
			total_gravada5 += gasto.getGravada5();
			total_exenta += gasto.getExenta();
			total_iva10 += gasto.getIva10();
			total_iva5 += gasto.getIva5();
			total_baseimponible += gasto.getBaseImponible();
		}		
		for (NotaCredito nc : notasCredito) {
			if ((nc.isNotaCreditoCompraAcreedor() && !despacho) || (despacho && nc.isNotaCreditoGastoDespacho())) {
				Date fecha_ = nc.getFechaEmision();
				String fechaCarga = Utiles.getDateToString(nc.getModificado(), Utiles.DD_MM_YYYY);
				String fecha = Utiles.getDateToString(nc.getFechaEmision(), Utiles.DD_MM_YYYY);
				String numero = nc.getNumero();
				String concepto = "NOTA CRÉDITO-GASTO";
				String timbrado = nc.getTimbrado().getNumero();
				String proveedor = nc.getProveedor().getRazonSocial();
				String ruc = nc.getProveedor().getRuc();
				double gravada10 = nc.getTotalGravado10() * -1;
				double gravada5 = 0.0;
				double exenta = nc.getTotalExenta() * -1;
				double iva10 = nc.getTotalIva10() * -1;
				double iva5 = 0.0;
				double total = gravada10 + gravada5 + iva10 + iva5 + exenta;
				double baseImponible = 0.0;
				String cuenta1 = (nc.getObservacion().contains("COMISIONES") || nc.getObservacion().contains("TELEFONIA") || nc.getObservacion().contains("CT:")) ? 
						nc.getObservacion().replace("CT:", "") : "DESCUENTOS OBTENIDOS";
				
				BeanLibroCompra value = new BeanLibroCompra(fecha, fechaCarga, numero, concepto, timbrado, proveedor, ruc,
						gravada10, gravada5, iva10, iva5, exenta, total, baseImponible, cuenta1, fecha_);
				this.values.add(value);

				total_gravada10 += gravada10;
				total_gravada5 += gravada5;
				total_exenta += exenta;
				total_iva10 += iva10;
				total_iva5 += iva5;
				
				nc_gravada10 += gravada10;
				nc_gravada5 += gravada5;
				nc_exenta += exenta;
				nc_iva10 += iva10;
				nc_iva5 += iva5;
			}
		}
		for (Gasto gasto : autofacturas) {
			autoFact_grav10 += gasto.getGravada10();
			autoFact_grav5 += gasto.getGravada5();
			autoFact_exenta += gasto.getExenta();
			autoFact_iva10 += gasto.getIva10();
			autoFact_iva5 += gasto.getIva5();
		}
		for (Gasto gasto : boletasVenta) {
			boleta_grav10 += gasto.getGravada10();
			boleta_grav5 += gasto.getGravada5();
			boleta_exenta += gasto.getExenta();
			boleta_iva10 += gasto.getIva10();
			boleta_iva5 += gasto.getIva5();
		}
		for (Gasto gasto : gastosContado) {
			contado_grav10 += gasto.getGravada10();
			contado_grav5 += gasto.getGravada5();
			contado_exenta += gasto.getExenta();
			contado_iva10 += gasto.getIva10();
			contado_iva5 += gasto.getIva5();
		}
		for (Gasto gasto : gastosCredito) {
			credito_grav10 += gasto.getGravada10();
			credito_grav5 += gasto.getGravada5();
			credito_exenta += gasto.getExenta();
			credito_iva10 += gasto.getIva10();
			credito_iva5 += gasto.getIva5();
		}
		for (Gasto gasto : otrosComprobantes) {
			otros_grav10 += gasto.getGravada10();
			otros_grav5 += gasto.getGravada5();
			otros_exenta += gasto.getExenta();
			otros_iva10 += gasto.getIva10();
			otros_iva5 += gasto.getIva5();
		}
		for (ImportacionFactura fac : importaciones) {
			BeanLibroCompra value = new BeanLibroCompra(
					Utiles.getDateToString(fac.getFechaOriginal(), Utiles.DD_MM_YYYY),
					Utiles.getDateToString(fac.getModificado(), Utiles.DD_MM_YYYY), fac.getNumero(),
					fac.getTipoMovimiento().getDescripcion(), "", "PROVEEDOR DEL EXTERIOR",
					Configuracion.RUC_EMPRESA_EXTERIOR, 0.0, 0.0, 0.0, 0.0, (fac.getTotalImporteDs() * fac.getPorcProrrateo()),
					(fac.getTotalImporteDs() * fac.getPorcProrrateo()), 0.0, "IMPORTACIONES EN CURSO", fac.getFechaOriginal());
			values.add(value);
		}
		
		// ordena la lista segun fecha..
		Collections.sort(this.values, new Comparator<BeanLibroCompra>() {
			@Override
			public int compare(BeanLibroCompra o1, BeanLibroCompra o2) {
				Date fecha1 = o1.fecha_;
				Date fecha2 = o2.fecha_;
				return fecha1.compareTo(fecha2);
			}
		});
    }
	
	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
        Object value = null;
        String fieldName = field.getName();
        BeanLibroCompra compra = this.values.get(index);
         
        if ("Fecha".equals(fieldName)) {
        	value = compra.fecha;
        } else if ("FechaCarga".equals(fieldName)) {
        	value = compra.fechaCarga;
        } else if ("Numero".equals(fieldName)) {
        	value = compra.numero;
        } else if ("Concepto".equals(fieldName)) {
        	value = compra.concepto;
        } else if ("Timbrado".equals(fieldName)) {
        	value = compra.timbrado;
        } else if ("Proveedor".equals(fieldName)) {
        	value = compra.proveedor;
        } else if ("Ruc".equals(fieldName)) {
        	value = compra.ruc;
        } else if ("Gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.gravada10);
        } else if ("Gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.gravada5);
        } else if ("Exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.exenta);
        } else if ("Iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.iva10);
        } else if ("Iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.iva5);
        } else if ("Total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.total);
        } else if ("Base_imponible".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.baseImponible);
        } else if ("Cuenta1".equals(fieldName)) {
        	value = compra.cuenta1;
        } else if ("Cuenta2".equals(fieldName)) {
        	value = "";
        } else if ("autofact_gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.autoFact_grav10);
        } else if ("autofact_gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.autoFact_grav5);
        } else if ("autofact_exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.autoFact_exenta);
        } else if ("autofact_iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.autoFact_iva10);
        } else if ("autofact_iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.autoFact_iva5);
        } else if ("autofact_total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.autoFact_grav10 + this.autoFact_grav5 + this.autoFact_exenta + this.autoFact_iva10 + this.autoFact_iva5);
        } else if ("boleta_gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.boleta_grav10);
        } else if ("boleta_gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.boleta_grav5);
        } else if ("boleta_exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.boleta_exenta);
        } else if ("boleta_iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.boleta_iva10);
        } else if ("boleta_iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.boleta_iva5);
        } else if ("boleta_total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.boleta_grav10 + this.boleta_grav5 + this.boleta_exenta + this.boleta_iva10 + this.boleta_iva5);
        } else if ("cont_gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.contado_grav10);
        } else if ("cont_gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.contado_grav5);
        } else if ("cont_exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.contado_exenta);
        } else if ("cont_iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.contado_iva10);
        } else if ("cont_iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.contado_iva5);
        } else if ("cont_total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.contado_grav10 + this.contado_grav5 + this.contado_exenta + this.contado_iva10 + this.contado_iva5);
        } else if ("cred_gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.credito_grav10);
        } else if ("cred_gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.credito_grav5);
        } else if ("cred_exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.credito_exenta);
        } else if ("cred_iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.credito_iva10);
        } else if ("cred_iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.credito_iva5);
        } else if ("cred_total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.credito_grav10 + this.credito_grav5 + this.credito_exenta + this.credito_iva10 + this.credito_iva5);
        } else if ("otros_gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.otros_grav10);
        } else if ("otros_gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.otros_grav5);
        } else if ("otros_exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.otros_exenta);
        } else if ("otros_iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.otros_iva10);
        } else if ("otros_iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.otros_iva5);
        } else if ("otros_total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.otros_grav10 + this.otros_grav5 + this.otros_exenta + this.otros_iva10 + this.otros_iva5);
        } else if ("nc_gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_gravada10);
        } else if ("nc_gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_gravada5);
        } else if ("nc_exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_exenta);
        } else if ("nc_iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_iva10);
        } else if ("nc_iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_iva5);
        } else if ("nc_total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_gravada10 + this.nc_gravada5 + this.nc_exenta + this.nc_iva10 + this.nc_iva5);
        } else if ("Total_Gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_gravada10);
        } else if ("Total_Gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_gravada5);
        } else if ("Total_Iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_iva5);
        } else if ("Total_Iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_iva10);
        } else if ("Total_Exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_exenta);
        } else if ("Total_".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_gravada10 + this.total_gravada5 + this.total_iva10 + this.total_iva5 + this.total_exenta);
        } else if ("Total_base_imponible".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_baseimponible);
        }
        return value;
    }

	@Override
	public boolean next() throws JRException {
		if (index < values.size() - 1) {
			index ++;
			return true;
		}
		return false;
	}
	
	class BeanLibroCompra {
		Date fecha_;
		String fecha;
		String fechaCarga;
		String numero;
		String concepto;
		String timbrado;
		String proveedor;
		String ruc;
		double gravada10;
		double gravada5;
		double exenta;
		double iva10;
		double iva5;
		double total;
		double baseImponible;
		String cuenta1;

		public BeanLibroCompra(String fecha, String fechaCarga, String numero, String concepto, String timbrado,
				String proveedor, String ruc, double gravada10, double gravada5, double iva10, double iva5, double exenta,
				double total, double baseImponible, String cuenta, Date fecha_) {
			this.fecha = fecha;
			this.fechaCarga = fechaCarga;
			this.numero = numero;
			this.concepto = concepto;
			this.timbrado = timbrado;
			this.proveedor = proveedor;
			this.ruc = ruc;
			this.gravada10 = gravada10;
			this.gravada5 = gravada5;
			this.iva10 = iva10;
			this.iva5 = iva5;
			this.exenta = exenta;
			this.total = total;
			this.cuenta1 = cuenta;
			this.baseImponible = baseImponible;
			this.fecha_ = fecha_;
		}
	}
}

/**
 * DataSource del Libro compras locales..
 */
class LibroComprasLocalesDataSource implements JRDataSource {
	
	List<BeanLibroCompra> values = new ArrayList<BeanLibroCompra>();
	List<CompraLocalFactura> creditos = new ArrayList<CompraLocalFactura>();
	List<CompraLocalFactura> contado = new ArrayList<CompraLocalFactura>();
	Misc misc = new Misc();
	
	double cred_gravada10 = 0;
	double cred_gravada5 = 0;
	double cred_exenta = 0;
	double cred_iva10 = 0;
	double cred_iva5 = 0;
	
	double cont_gravada10 = 0;
	double cont_gravada5 = 0;
	double cont_exenta = 0;
	double cont_iva10 = 0;
	double cont_iva5 = 0;
	
	double nc_gravada10 = 0;
	double nc_gravada5 = 0;
	double nc_exenta = 0;
	double nc_iva10 = 0;
	double nc_iva5 = 0;
	
	double total_gravada10 = 0;
	double total_gravada5 = 0;
	double total_exenta = 0;
	double total_iva10 = 0;
	double total_iva5 = 0;
	
	public LibroComprasLocalesDataSource(List<CompraLocalFactura> compras, List<NotaCredito> notascredito) {
				
		for (CompraLocalFactura compra : compras) {
			Date fechaCarga = compra.getFechaCreacion();
			Date fecha = compra.getFechaOriginal();
			String numero = compra.getNumero();
			String concepto = compra.getTipoMovimiento().getDescripcion();
			String timbrado = compra.getTimbrado().getNumero();
			String proveedor = compra.getProveedor().getRazonSocial();
			String ruc = compra.getProveedor().getRuc();
			double iva10 = compra.getIva10();
			double gravada10 = compra.getGravada10();
			double gravada5 = compra.getGravada5();
			double exenta = compra.getExenta();
			double iva5 = compra.getIva5();
			double total = gravada10 + gravada5 + iva10 + iva5 + exenta;
			
			BeanLibroCompra value = new BeanLibroCompra(fechaCarga, fecha, numero, concepto, timbrado, proveedor, ruc,
					gravada10, gravada5, exenta, iva10, iva5, total);
			this.values.add(value);

			total_gravada10 += gravada10;
			total_gravada5 += gravada5;
			total_exenta += exenta;
			total_iva10 += iva10;
			total_iva5 += iva5;

			if (compra.isCredito()) creditos.add(compra);
			if (compra.isContado()) contado.add(compra);
		}
		
		for (NotaCredito nc : notascredito) {
			if (nc.isNotaCreditoCompraProveedor()) {
				Date fechaCarga = nc.getModificado();
				Date fecha = nc.getFechaEmision();
				String numero = nc.getNumero();
				String concepto = nc.getTipoMovimiento().getDescripcion();
				String timbrado = nc.getTimbrado().getNumero();
				String proveedor = nc.getProveedor().getRazonSocial();
				String ruc = nc.getProveedor().getRuc();
				double gravada10 = nc.getTotalGravado10() * -1;
				double gravada5 = 0.0;
				double exenta = 0.0;
				double iva10 = nc.getTotalIva10() * -1;
				double iva5 = 0.0;
				double total = gravada10 + gravada5 + iva10 + iva5 + exenta;
				
				BeanLibroCompra value = new BeanLibroCompra(fechaCarga, fecha, numero, concepto, timbrado, proveedor, ruc,
						gravada10, gravada5, exenta, iva10, iva5, total);
				this.values.add(value);

				total_gravada10 += gravada10;
				total_gravada5 += gravada5;
				total_exenta += exenta;
				total_iva10 += iva10;
				total_iva5 += iva5;
				
				nc_gravada10 += gravada10;
				nc_gravada5 += gravada5;
				nc_exenta += exenta;
				nc_iva10 += iva10;
				nc_iva5 += iva5;
			}
		}
		
		for (CompraLocalFactura compra : creditos) {
			cred_gravada10 += compra.getGravada10();
			cred_gravada5 += compra.getGravada5();
			cred_iva10 += compra.getIva10();
			cred_iva5 += compra.getIva5();
			cred_exenta += compra.getExenta();
		}
		
		for (CompraLocalFactura compra : contado) {
			cont_gravada10 += compra.getGravada10();
			cont_gravada5 += compra.getGravada5();
			cont_iva10 += compra.getIva10();
			cont_iva5 += compra.getIva5();
			cont_exenta += compra.getExenta();
		}
		
		// ordena la lista segun fecha..
		Collections.sort(this.values, new Comparator<BeanLibroCompra>() {
			@Override
			public int compare(BeanLibroCompra o1, BeanLibroCompra o2) {
				Date fecha1 = o1.fecha;
				Date fecha2 = o2.fecha;
				return fecha1.compareTo(fecha2);
			}
		});
    }
	
	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
        Object value = null;
        String fieldName = field.getName();
        BeanLibroCompra compra = this.values.get(index);
         
        if ("Fecha".equals(fieldName)) {
        	value = Utiles.getDateToString(compra.fecha, Utiles.DD_MM_YYYY);
        } else if ("FechaCarga".equals(fieldName)) {
        	value = Utiles.getDateToString(compra.fechaCarga, Utiles.DD_MM_YYYY);
        } else if ("Numero".equals(fieldName)) {
        	value = compra.numero;
        } else if ("Concepto".equals(fieldName)) {
        	value = compra.concepto;
        } else if ("Timbrado".equals(fieldName)) {
        	value = compra.timbrado;
        } else if ("Proveedor".equals(fieldName)) {
        	value = compra.proveedor;
        } else if ("Ruc".equals(fieldName)) {
        	value = compra.ruc;
        } else if ("Gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.gravada10);
        } else if ("Gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.gravada5);
        } else if ("Exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.exenta);
        } else if ("Iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.iva10);
        } else if ("Iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.iva5);
        } else if ("Total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.iva5 + compra.iva10 + compra.exenta + compra.gravada10 + compra.gravada5);
        } else if ("Cuenta1".equals(fieldName)) {
        	value = "";
        } else if ("Cuenta2".equals(fieldName)) {
        	value = "";
        } else if ("fac_cre_gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cred_gravada10);
        } else if ("fac_cre_gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cred_gravada5);
        } else if ("fac_cre_exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cred_exenta);
        } else if ("fac_cre_iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cred_iva10);
        } else if ("fac_cre_iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cred_iva5);
        } else if ("fac_cre_total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cred_gravada10 + this.cred_gravada5 + this.cred_iva10 + this.cred_iva5 + this.cred_exenta);
        } else if ("fac_con_gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cont_gravada10);
        } else if ("fac_con_gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cont_gravada5);
        } else if ("fac_con_exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cont_exenta);
        } else if ("fac_con_iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cont_iva10);
        } else if ("fac_con_iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cont_iva5);
        } else if ("fac_con_total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.cont_gravada10 + this.cont_gravada5 + this.cont_iva10 + this.cont_iva5 + this.cont_exenta);
        } else if ("nc_gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_gravada10);
        } else if ("nc_gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_gravada5);
        } else if ("nc_exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_exenta);
        } else if ("nc_iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_iva10);
        } else if ("nc_iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_iva5);
        } else if ("nc_total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.nc_gravada10 + this.nc_gravada5 + this.nc_iva10 + this.nc_iva5 + this.nc_exenta);
        } else if ("Total_Gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_gravada10);
        } else if ("Total_Gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_gravada5);
        } else if ("Total_Iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_iva5);
        } else if ("Total_Iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_iva10);
        } else if ("Total_Exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_exenta);
        } else if ("Total_".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_gravada10 + this.total_gravada5 + this.total_iva10 + this.total_iva5 + this.total_exenta);
        }
        return value;
    }

	@Override
	public boolean next() throws JRException {
		if (index < values.size() - 1) {
			index ++;
			return true;
		}
		return false;
	}	
	
	class BeanLibroCompra {
		
		Date fechaCarga;
		Date fecha;
		String numero;
		String concepto;
		String timbrado;
		String proveedor;
		String ruc;
		double gravada10;
		double gravada5;
		double exenta;
		double iva10;
		double iva5;
		double total;
		
		public BeanLibroCompra(Date fechaCarga, Date fecha, String numero, String concepto, String timbrado,
				String proveedor, String ruc, double gravada10, double gravada5, double exenta, double iva10,
				double iva5, double total) {
			this.fechaCarga = fechaCarga;
			this.fecha = fecha;
			this.numero = numero;
			this.concepto = concepto;
			this.timbrado = timbrado;
			this.proveedor = proveedor;
			this.ruc = ruc;
			this.gravada10 = gravada10;
			this.gravada5 = gravada5;
			this.exenta = exenta;
			this.iva10 = iva10;
			this.iva5 = iva5;
			this.total = total;
		}
	}
}


/**
 * DataSource del listado de Cobranzas..
 */
class ListadoCobranzasDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	Misc misc = new Misc();

	List<BeanRecibo> values = new ArrayList<BeanRecibo>();
	List<Recibo> recibos;
	Date desde;
	Date hasta;
	String sucursal;

	double totalImporte = 0;
	double totalSaldo = 0;

	public ListadoCobranzasDataSource(List<Recibo> recibos, Date desde, Date hasta, String sucursal, boolean anticipos, long idSucursal) {
		this.recibos = recibos;
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.loadDatos(idSucursal, anticipos);	
		if (!anticipos) {
			this.loadAplicacionesAnticipos(idSucursal);
		}
		Collections.sort(this.values, new Comparator<BeanRecibo>() {
			@Override
			public int compare(BeanRecibo o1, BeanRecibo o2) {
				String val1 = o1.getFecha();
				String val2 = o2.getFecha();
				int compare = val1.compareTo(val2);				
				return compare;
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();

		if ("Recibos".equals(fieldName)) {
			value = this.values;
		} else if ("Desde".equals(fieldName)) {
			value = misc.dateToString(this.desde, Misc.DD_MM_YYYY);
		} else if ("Hasta".equals(fieldName)) {
			value = misc.dateToString(this.hasta, Misc.DD_MM_YYYY);
		} else if ("Sucursal".equals(fieldName)) {
			value = this.sucursal;
		} else if ("TotalImporte".equals(fieldName)) {
			value = FORMATTER.format(this.totalImporte);
		} else if ("TotalSaldo".equals(fieldName)) {
			value = FORMATTER.format(this.totalSaldo);
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

	/**
	 * carga los datos para el reporte..
	 */
	private void loadDatos(long idSucursal, boolean anticipo) {
		try {
			for (Recibo recibo : this.recibos) {
				if (!recibo.isReciboContraCuenta()) {					
					double saldo_ = 0;
					if (anticipo) {
						saldo_ = recibo.getSaldoCtaCte();
					}
					String fecha = misc.dateToString(recibo.getFechaEmision(), Misc.DD_MM_YYYY);
					String numero = recibo.getNumero();
					String razonSocial = recibo.isAnulado() ? "ANULADO.." : recibo.getCliente().getRazonSocial();
					String ruc = recibo.getCliente().getRuc();
					double importeGs = recibo.getTotalImporteGsBySucursal(idSucursal);
					String importe = FORMATTER.format(recibo.isCobroExterno() ? 0.0 : importeGs);
					String saldo = FORMATTER.format(recibo.isCobroExterno() ? 0.0 : saldo_);
					if (importeGs > 0) {
						values.add(new BeanRecibo(fecha, numero, razonSocial, ruc, importe, saldo));
						this.totalImporte += (recibo.isCobroExterno() ? 0.0 : importeGs);
						this.totalSaldo += (recibo.isCobroExterno() ? 0.0 : saldo_);
					}					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * busca las aplicaciones de anticipos..
	 */
	private void loadAplicacionesAnticipos(long idSucursal) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<AjusteCtaCte> apls = rr.getAplicacionesAnticipos(this.desde, this.hasta);
			for (AjusteCtaCte apl : apls) {
				if (idSucursal == 0 || (idSucursal == apl.getIdSucursalCredito())) {
					String fecha = misc.dateToString(apl.getFecha(), Misc.DD_MM_YYYY);
					String numero = apl.getOrden();
					String razonSocial = apl.getDescripcion();
					String ruc = apl.getIp_pc();
					String importe = FORMATTER.format(apl.getImporte());
					values.add(new BeanRecibo(fecha, numero, razonSocial, ruc, importe, importe));
					this.totalImporte += (apl.getImporte());
					this.totalSaldo += (apl.getImporte());
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}

/**
 * DataSource del listado de Transferencias..
 */
class ListadoTransferenciasDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	static final int MIN_SIZE = 38;
	Misc misc = new Misc();

	List<Transferencia> transferencias;
	Date desde;
	Date hasta;

	double totalImporte = 0;

	public ListadoTransferenciasDataSource(List<Transferencia> transferencias,
			Date desde, Date hasta) {
		this.transferencias = transferencias;
		this.desde = desde;
		this.hasta = hasta;
		for (Transferencia transf : transferencias) {
			this.totalImporte += (transf.isAnulado() ? 0.0 : transf.getImporteGs());
		}
	}

	private int currentSize = MIN_SIZE;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();

		if ("Transferencias".equals(fieldName)) {
			if ((this.transferencias.size()) < this.currentSize) {
				this.currentSize = this.transferencias.size();
			}

			List<BeanTransferencia> values = new ArrayList<BeanTransferencia>();
			List<Transferencia> temp = new ArrayList<Transferencia>();
			for (int i = 0; i < this.currentSize; i++) {
				Transferencia transf = this.transferencias.get(i);
				String fecha = misc.dateToString(transf.getFechaCreacion(),
						Misc.DD_MM_YYYY);
				String numero = transf.getNumero();
				String origen = transf.getSucursal().getDescripcion();
				String destino = transf.getSucursalDestino().getDescripcion();
				String nroremision = transf.getNumeroRemision();
				String importe = FORMATTER.format(transf.isAnulado() ? 0.0 : transf.getImporteGs());
				temp.add(transf);
				values.add(new BeanTransferencia(fecha, numero, origen,
						destino, nroremision, importe));
			}
			value = values;
			this.transferencias.removeAll(temp);

		} else if ("Desde".equals(fieldName)) {
			value = misc.dateToString(this.desde, Misc.DD_MM_YYYY);
		} else if ("Hasta".equals(fieldName)) {
			value = misc.dateToString(this.hasta, Misc.DD_MM_YYYY);
		} else if ("TotalImporte".equals(fieldName)) {
			value = FORMATTER.format(this.totalImporte);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (this.transferencias.size() > 0) {
			return true;
		}
		return false;
	}
}

/**
 * Reporte de Historial Movimientos por Articulo STK-00007..
 */
class ReporteHistorialMovimientosArticulo extends ReporteYhaguy {
	
	private String articulo;
	private String deposito;
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 35);
	static DatosColumnas col2 = new DatosColumnas("Hora", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Número", TIPO_STRING, 60);
	static DatosColumnas col4 = new DatosColumnas("Concepto", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Depósito", TIPO_STRING);
	static DatosColumnas col6 = new DatosColumnas("Entrada", TIPO_STRING, 30);
	static DatosColumnas col7 = new DatosColumnas("Salida", TIPO_STRING, 30);
	static DatosColumnas col8 = new DatosColumnas("Saldo", TIPO_STRING, 30);
	static DatosColumnas col9 = new DatosColumnas("Precio Gs.", TIPO_STRING, 40);

	public ReporteHistorialMovimientosArticulo(Date desde, Date hasta, String articulo, String deposito) {
		this.desde = desde;
		this.hasta = hasta;
		this.articulo = articulo;
		this.deposito = deposito;
	}

	static {
		col6.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		col7.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		col8.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		col9.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
		cols.add(col9);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Historial de movimientos por artículo");
		this.setDirectorio("Articulos");
		this.setNombreArchivo("HistorialMovimientos-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Artículo", this.articulo))
				.add(this.textoParValor("Depósito", this.deposito)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Ajustes de Stock STK-00004..
 */
class ReporteAjusteStock extends ReporteYhaguy {

	private String sucursal;
	private String deposito;
	private String tipo;
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 25);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 25);
	static DatosColumnas col2 = new DatosColumnas("Observación", TIPO_STRING,
			60);
	static DatosColumnas col3 = new DatosColumnas("Código", TIPO_STRING, 40);
	static DatosColumnas col4 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Can.", TIPO_INTEGER, 10);

	public ReporteAjusteStock(String sucursal, String deposito, String tipo,
			Date desde, Date hasta) {
		this.sucursal = sucursal;
		this.deposito = deposito;
		this.tipo = tipo;
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Ajustes de Stock");
		this.setDirectorio("ajustes");
		this.setNombreArchivo("Ajuste-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Tipo", this.tipo))
				.add(this.textoParValor("Sucursal", this.sucursal))
				.add(this.textoParValor("Depósito", this.deposito)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * DataSource de Ventas por Vendedor..
 */
class VentasPorVendedorDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, Double> totales = new HashMap<String, Double>();
	double totalConIva = 0;
	double totalSinIva = 0;
	
	public VentasPorVendedorDataSource(List<Object[]> values, double totalConIva, double totalSinIva) {
		this.values = values;
		this.totalConIva = totalConIva;
		this.totalSinIva = totalSinIva;
		Collections.sort(this.values, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String val1 = (String) o1[0];
				String val2 = (String) o2[0];
				int compare = val1.compareTo(val2);				
				return compare;
			}
		});
		totales.put("Ene", 0.0);
		totales.put("Feb", 0.0);
		totales.put("Mar", 0.0);
		totales.put("Abr", 0.0);
		totales.put("May", 0.0);
		totales.put("Jun", 0.0);
		totales.put("Jul", 0.0);
		totales.put("Ago", 0.0);
		totales.put("Set", 0.0);
		totales.put("Oct", 0.0);
		totales.put("Nov", 0.0);
		totales.put("Dic", 0.0);
		totales.put("Total", 0.0);
		for (Object[] value : values) {
			Double ene = totales.get("Ene");
			Double feb = totales.get("Feb");
			Double mar = totales.get("Mar");
			Double abr = totales.get("Abr");
			Double may = totales.get("May");
			Double jun = totales.get("Jun");
			Double jul = totales.get("Jul");
			Double ago = totales.get("Ago");
			Double set = totales.get("Set");
			Double oct = totales.get("Oct");
			Double nov = totales.get("Nov");
			Double dic = totales.get("Dic");
			Double tot = totales.get("Total");
			ene += (double) value[1];
			feb += (double) value[2];
			mar += (double) value[3];
			abr += (double) value[4];
			may += (double) value[5];
			jun += (double) value[6];
			jul += (double) value[7];
			ago += (double) value[8];
			set += (double) value[9];
			oct += (double) value[10];
			nov += (double) value[11];
			dic += (double) value[12];
			tot += (double) value[13];
			totales.put("Ene", ene); totales.put("Feb", feb);
			totales.put("Mar", mar); totales.put("Abr", abr);
			totales.put("May", may); totales.put("Jun", jun);
			totales.put("Jul", jul); totales.put("Ago", ago);
			totales.put("Set", set); totales.put("Oct", oct);
			totales.put("Nov", nov); totales.put("Dic", dic);
			totales.put("Total", tot);
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);

		if ("Vendedor".equals(fieldName)) {
			value = det[0];
		} else if ("Ene".equals(fieldName)) {
			value = FORMATTER.format(det[1]);
		} else if ("Feb".equals(fieldName)) {
			value = FORMATTER.format(det[2]);
		} else if ("Mar".equals(fieldName)) {
			value = FORMATTER.format(det[3]);
		} else if ("Abr".equals(fieldName)) {
			value = FORMATTER.format(det[4]);			
		} else if ("May".equals(fieldName)) {
			value = FORMATTER.format(det[5]);
		} else if ("Jun".equals(fieldName)) {
			value = FORMATTER.format(det[6]);
		} else if ("Jul".equals(fieldName)) {
			value = FORMATTER.format(det[7]);
		} else if ("Ago".equals(fieldName)) {
			value = FORMATTER.format(det[8]);
		} else if ("Set".equals(fieldName)) {
			value = FORMATTER.format(det[9]);
		} else if ("Oct".equals(fieldName)) {
			value = FORMATTER.format(det[10]);
		} else if ("Nov".equals(fieldName)) {
			value = FORMATTER.format(det[11]);
		} else if ("Dic".equals(fieldName)) {
			value = FORMATTER.format(det[12]);
		} else if ("Total".equals(fieldName)) {
			value = FORMATTER.format(det[13]);
		} else if ("Tot_1".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Ene"));
		} else if ("Tot_2".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Feb"));
		} else if ("Tot_3".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Mar"));
		} else if ("Tot_4".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Abr"));
		} else if ("Tot_5".equals(fieldName)) {
			value = FORMATTER.format(totales.get("May"));
		} else if ("Tot_6".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Jun"));
		} else if ("Tot_7".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Jul"));
		} else if ("Tot_8".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Ago"));
		} else if ("Tot_9".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Set"));
		} else if ("Tot_10".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Oct"));
		} else if ("Tot_11".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Nov"));
		} else if ("Tot_12".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Dic"));
		} else if ("Tot".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Total"));
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Ventas por Cliente..
 */
class VentasPorClienteDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, Double> totales = new HashMap<String, Double>();
	
	public VentasPorClienteDataSource(List<Object[]> values) {
		this.values = values;
		Collections.sort(this.values, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String val1 = (String) o1[0];
				String val2 = (String) o2[0];
				int compare = val1.compareTo(val2);				
				return compare;
			}
		});
		totales.put("Ene", 0.0);
		totales.put("Feb", 0.0);
		totales.put("Mar", 0.0);
		totales.put("Abr", 0.0);
		totales.put("May", 0.0);
		totales.put("Jun", 0.0);
		totales.put("Jul", 0.0);
		totales.put("Ago", 0.0);
		totales.put("Set", 0.0);
		totales.put("Oct", 0.0);
		totales.put("Nov", 0.0);
		totales.put("Dic", 0.0);
		totales.put("Total", 0.0);
		for (Object[] value : values) {
			Double ene = totales.get("Ene");
			Double feb = totales.get("Feb");
			Double mar = totales.get("Mar");
			Double abr = totales.get("Abr");
			Double may = totales.get("May");
			Double jun = totales.get("Jun");
			Double jul = totales.get("Jul");
			Double ago = totales.get("Ago");
			Double set = totales.get("Set");
			Double oct = totales.get("Oct");
			Double nov = totales.get("Nov");
			Double dic = totales.get("Dic");
			Double tot = totales.get("Total");
			ene += (double) value[1];
			feb += (double) value[2];
			mar += (double) value[3];
			abr += (double) value[4];
			may += (double) value[5];
			jun += (double) value[6];
			jul += (double) value[7];
			ago += (double) value[8];
			set += (double) value[9];
			oct += (double) value[10];
			nov += (double) value[11];
			dic += (double) value[12];
			tot += (double) value[13];
			totales.put("Ene", ene); totales.put("Feb", feb);
			totales.put("Mar", mar); totales.put("Abr", abr);
			totales.put("May", may); totales.put("Jun", jun);
			totales.put("Jul", jul); totales.put("Ago", ago);
			totales.put("Set", set); totales.put("Oct", oct);
			totales.put("Nov", nov); totales.put("Dic", dic);
			totales.put("Total", tot);
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);

		if ("Cliente".equals(fieldName)) {
			value = det[0];
		} else if ("Vendedor".equals(fieldName)) {
			value = det[14];
		} else if ("Rubro".equals(fieldName)) {
			value = det[15];
		} else if ("Ult_Vta".equals(fieldName)) {
			value = det[16];
		} else if ("Saldo".equals(fieldName)) {
			value = FORMATTER.format(det[16]);
		} else if ("Ene".equals(fieldName)) {
			value = FORMATTER.format(det[1]);
		} else if ("Feb".equals(fieldName)) {
			value = FORMATTER.format(det[2]);
		} else if ("Mar".equals(fieldName)) {
			value = FORMATTER.format(det[3]);
		} else if ("Abr".equals(fieldName)) {
			value = FORMATTER.format(det[4]);			
		} else if ("May".equals(fieldName)) {
			value = FORMATTER.format(det[5]);
		} else if ("Jun".equals(fieldName)) {
			value = FORMATTER.format(det[6]);
		} else if ("Jul".equals(fieldName)) {
			value = FORMATTER.format(det[7]);
		} else if ("Ago".equals(fieldName)) {
			value = FORMATTER.format(det[8]);
		} else if ("Set".equals(fieldName)) {
			value = FORMATTER.format(det[9]);
		} else if ("Oct".equals(fieldName)) {
			value = FORMATTER.format(det[10]);
		} else if ("Nov".equals(fieldName)) {
			value = FORMATTER.format(det[11]);
		} else if ("Dic".equals(fieldName)) {
			value = FORMATTER.format(det[12]);
		} else if ("Total".equals(fieldName)) {
			value = FORMATTER.format(det[13]);
		} else if ("Tot_1".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Ene"));
		} else if ("Tot_2".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Feb"));
		} else if ("Tot_3".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Mar"));
		} else if ("Tot_4".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Abr"));
		} else if ("Tot_5".equals(fieldName)) {
			value = FORMATTER.format(totales.get("May"));
		} else if ("Tot_6".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Jun"));
		} else if ("Tot_7".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Jul"));
		} else if ("Tot_8".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Ago"));
		} else if ("Tot_9".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Set"));
		} else if ("Tot_10".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Oct"));
		} else if ("Tot_11".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Nov"));
		} else if ("Tot_12".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Dic"));
		} else if ("Tot".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Total"));
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Ventas Utilidad Detallado..
 */
class VentasUtilidadDetallado implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	
	public VentasUtilidadDetallado(List<Object[]> values) {
		this.values = values;
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);

		if ("Fecha".equals(fieldName)) {
			value = det[0];
		} else if ("Numero".equals(fieldName)) {
			value = det[1];
		} else if ("Concepto".equals(fieldName)) {
			value = det[2];
		} else if ("Cliente".equals(fieldName)) {
			value = det[3];
		} else if ("TipoCliente".equals(fieldName)) {
			value = det[4];
		} else if ("Vendedor".equals(fieldName)) {
			value = det[5];
		} else if ("Codigo".equals(fieldName)) {
			value = det[6];
		} else if ("Marca".equals(fieldName)) {
			value = det[7];
		} else if ("Familia".equals(fieldName)) {
			value = det[8];
		} else if ("CostoUnit".equals(fieldName)) {
			value = FORMATTER.format(det[9]);
		} else if ("Cant".equals(fieldName)) {
			value = FORMATTER.format(det[10]);
		} else if ("CostoTotal".equals(fieldName)) {
			value = FORMATTER.format(det[11]);
		} else if ("Importe".equals(fieldName)) {
			value = FORMATTER.format(det[12]);
		} else if ("Rent".equals(fieldName)) {
			value = FORMATTER.format(Utiles.getRedondeo((double) det[13]));
		} else if ("Descripcion".equals(fieldName)) {
			value = det[14];
		} else if ("Rent_".equals(fieldName)) {
			value = FORMATTER.format(Utiles.getRedondeo((double) det[15]));
		} else if ("Ganancia".equals(fieldName)) {
			value = FORMATTER.format(det[16]);
		}  else if ("Tecnico".equals(fieldName)) {
			value = det[17];
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Ventas Detalle..
 */
class VentasDetalle implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	
	public VentasDetalle(List<Object[]> values) {
		this.values = values;
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);

		if ("Fecha".equals(fieldName)) {
			value = det[0];
		} else if ("Numero".equals(fieldName)) {
			value = det[1];
		} else if ("Concepto".equals(fieldName)) {
			value = det[2];
		} else if ("Cliente".equals(fieldName)) {
			value = det[3];
		} else if ("TipoCliente".equals(fieldName)) {
			value = det[4];
		} else if ("Vendedor".equals(fieldName)) {
			value = det[5];
		} else if ("Codigo".equals(fieldName)) {
			value = det[6];
		} else if ("Marca".equals(fieldName)) {
			value = det[7];
		} else if ("Familia".equals(fieldName)) {
			value = det[8];
		} else if ("CostoUnit".equals(fieldName)) {
			value = FORMATTER.format(det[9]);
		} else if ("Cant".equals(fieldName)) {
			value = FORMATTER.format(det[10]);
		} else if ("CostoTotal".equals(fieldName)) {
			value = FORMATTER.format(det[11]);
		} else if ("Importe".equals(fieldName)) {
			value = FORMATTER.format(det[12]);
		} else if ("SubTotal".equals(fieldName)) {
			value = FORMATTER.format(det[13]);
		} else if ("Descripcion".equals(fieldName)) {
			value = det[14];
		} else if ("PorcDescuento".equals(fieldName)) {
			value = FORMATTER.format(det[15]);
		} else if ("Ganancia".equals(fieldName)) {
			value = FORMATTER.format(det[16]);
		}  else if ("Usuario".equals(fieldName)) {
			value = det[17];
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Ventas Lista Precio Familia..
 */
class VentasListaPrecioFamilia implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	static final NumberFormat FORMATTER_ = new DecimalFormat("###,###,##0.00");

	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, Object[]> totales;
	
	public VentasListaPrecioFamilia(List<Object[]> values, Map<String, Object[]> totales) {
		this.values = values;
		this.totales = totales;
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);
		Object[] tot = this.totales.get(det[0]);

		if ("TituloDetalle".equals(fieldName)) {
			value = det[0];
		} else if ("Descripcion".equals(fieldName)) {
			value = det[1];
		} else if ("Importe".equals(fieldName)) {
			double imp = (double) det[2];
			value = FORMATTER.format(imp);
		} else if ("Costo".equals(fieldName)) {
			double imp = (double) det[3];
			value = FORMATTER.format(imp);
		} else if ("UtilidadBruta".equals(fieldName)) {
			double imp = (double) det[4];
			value = FORMATTER.format(imp);
		} else if ("rentcosto".equals(fieldName)) {
			double imp = (double) det[5];
			value = FORMATTER_.format(imp);
		} else if ("rentventa".equals(fieldName)) {
			double imp = (double) det[6];
			value = FORMATTER_.format(imp);
		} else if ("TotalImporte".equals(fieldName)) {
			value = FORMATTER.format(0.0);
		} else if ("TotalVenta".equals(fieldName)) { 
			value = FORMATTER.format(tot != null ? tot[0] : 0.0);
		} else if ("TotalCosto".equals(fieldName)) { 
			value = FORMATTER.format(tot != null ? tot[1] : 0.0);
		} else if ("TotalUtilidad".equals(fieldName)) { 
			value = FORMATTER.format(tot != null ? tot[2] : 0.0);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * Reporte de Ventas Por Marca VEN-00009..
 */
class ReporteVentasPorMarca extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String sucursal;
	private String deposito;
	private String marca;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 40);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 55);
	static DatosColumnas col3 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col6 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col7 = new DatosColumnas("Can.", TIPO_LONG, 20);
	static DatosColumnas col8 = new DatosColumnas("Precio Gs.", TIPO_DOUBLE_GS,
			35);
	static DatosColumnas col9 = new DatosColumnas("Stock", TIPO_LONG, 20);

	public ReporteVentasPorMarca(Date desde, Date hasta, String sucursal,
			String deposito, String marca) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.deposito = deposito;
		this.marca = marca;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col3);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
		cols.add(col9);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de (Ventas - Devoluciones - Stock) Por Marca");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Deposito", this.deposito))
				.add(this.textoParValor("Marca", this.marca))
				.add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ventas Por Cliente Detallado VEN-00010..
 */
class ReporteVentasPorClienteDetallado extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String sucursal;
	private String cliente;
	private String marca;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 30);
	static DatosColumnas col1_ = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Código", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Marca", TIPO_STRING, 20);
	static DatosColumnas col4 = new DatosColumnas("Cant.", TIPO_LONG, 15);
	static DatosColumnas col5 = new DatosColumnas("Precio Gs.", TIPO_DOUBLE_GS, 30, true);

	public ReporteVentasPorClienteDetallado(Date desde, Date hasta,
			String sucursal, String cliente, String marca) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.cliente = cliente;
		this.marca = marca;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col1_);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Ventas Por Cliente Detallado");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Cliente", this.cliente))
				.add(this.textoParValor("Marca", this.marca)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ventas Anuladas VEN-00011..
 */
class ReporteVentasAnuladas extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 25);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 35);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Ruc", TIPO_STRING, 25);
	static DatosColumnas col4 = new DatosColumnas("Motivo", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Total Gs.", TIPO_DOUBLE_GS,
			25, true);

	public ReporteVentasAnuladas(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Ventas Anuladas");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * REPORTE CON-00020
 */
class ReporteDocumentosAnulados extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Concepto", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Timbrado", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Operación", TIPO_STRING);

	public ReporteDocumentosAnulados(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Documentos Anulados");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ajustes de Stock STK-00005..
 */
class ReporteMigracionArticulo extends ReporteYhaguy {

	private String sucursal;
	private String articulo;
	private String tipoSaldo;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Código", TIPO_STRING, 70);
	static DatosColumnas col1 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Cantidad", TIPO_LONG, 20);

	public ReporteMigracionArticulo(String sucursal, String articulo,
			String tipoSaldo) {
		this.sucursal = sucursal;
		this.articulo = articulo;
		this.tipoSaldo = tipoSaldo;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Migración de Artículos");
		this.setDirectorio("Articulos");
		this.setNombreArchivo("Migracion-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Sucursal", this.sucursal))
				.add(this.textoParValor("Artículo", this.articulo))
				.add(this.textoParValor("Saldo", this.tipoSaldo)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Autorizaciones de Precios VEN-00012..
 */
class ReporteAutorizacionesPrecios extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 25);
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 35);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Precio Gs.", TIPO_DOUBLE_GS,
			25, true);

	public ReporteAutorizacionesPrecios(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Autorizaciones de Precios");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * DataSource de Cartera de Clientes..
 */
class MorosidadClientesDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<CtaCteEmpresaMovimiento> values;
	Map<String, Double> totalSaldo = new HashMap<String, Double>();
	Map<String, Double> totalVencido = new HashMap<String, Double>();
	Map<String, Double> totalAvencer = new HashMap<String, Double>();

	Map<String, Empresa> empresa = new HashMap<String, Empresa>();

	Misc misc = new Misc();

	private String morosidad;
	private String sucursal;

	private double totalVencidoClientes = 0;
	private double totalAvencerClientes = 0;
	
	private boolean avencer;

	public MorosidadClientesDataSource(List<CtaCteEmpresaMovimiento> values,
			String morosidad, String sucursal, boolean avencer) {
		this.values = values;
		this.morosidad = morosidad;
		this.sucursal = sucursal;
		this.avencer = avencer;
		RegisterDomain rr = RegisterDomain.getInstance();

		try {
			for (CtaCteEmpresaMovimiento movim : values) {
				Empresa emp = rr.getEmpresaById(movim.getIdEmpresa());
				empresa.put(String.valueOf(emp.getId()), emp);
				Double total = this.totalSaldo.get(String.valueOf(emp.getId()));
				Double venc = this.totalVencido.get(String.valueOf(emp.getId()));
				Double aven = this.totalAvencer.get(String.valueOf(emp.getId()));

				if (total == null) {
					this.totalSaldo.put(String.valueOf(emp.getId()),
							movim.getSaldoFinal());
				} else {
					this.totalSaldo.put(String.valueOf(emp.getId()), total
							+ movim.getSaldoFinal());
				}

				if (venc == null && movim.isVencido()) {
					this.totalVencido.put(String.valueOf(emp.getId()),
							movim.getSaldoFinal());
				} else if (venc != null && movim.isVencido()) {
					this.totalVencido.put(String.valueOf(emp.getId()), venc
							+ movim.getSaldoFinal());
				}

				if (aven == null && !movim.isVencido()) {
					this.totalAvencer.put(String.valueOf(emp.getId()),
							movim.getSaldoFinal());
				} else if (aven != null && !movim.isVencido()) {
					this.totalAvencer.put(String.valueOf(emp.getId()), aven
							+ movim.getSaldoFinal());
				}

				if (movim.isVencido()) {
					this.totalVencidoClientes += movim.getSaldoFinal();
				} else {
					this.totalAvencerClientes += movim.getSaldoFinal();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		CtaCteEmpresaMovimiento movim = this.values.get(index);
		Empresa empresa = this.empresa
				.get(String.valueOf(movim.getIdEmpresa()));

		if ("Concepto".equals(fieldName)) {
			value = movim.getNroComprobante().replace("(1/1)", "")
					+ " - "
					+ ""
					+ TipoMovimiento.getAbreviatura(movim.getTipoMovimiento()
							.getSigla());
		} else if ("Telefono".equals(fieldName)) {
			value = empresa.getTelefono();
		} else if ("Direccion".equals(fieldName)) {
			value = empresa.getDireccion();
		} else if ("Emision".equals(fieldName)) {
			value = misc.dateToString(movim.getFechaEmision(), Misc.DD_MM_YYYY);
		} else if ("Vencimiento".equals(fieldName)) {
			value = misc.dateToString(movim.getFechaVencimiento(),
					Misc.DD_MM_YYYY);
		} else if ("DiasVto".equals(fieldName)) {
			value = String.valueOf(movim.getDiasVencidos(avencer));
		} else if ("Importe".equals(fieldName)) {
			value = FORMATTER.format(movim.getImporteOriginalFinal());
		} else if ("SaldoGs".equals(fieldName)) {
			value = FORMATTER.format(movim.getSaldoFinal());
		} else if ("TituloDetalle".equals(fieldName)) {
			value = empresa.getRazonSocial() + " - " + empresa.getRuc();
		} else if ("TotalImporte".equals(fieldName)) {
			double total = this.totalSaldo.get(String.valueOf(movim
					.getIdEmpresa()));
			value = FORMATTER.format(total);
		} else if ("TotalVencido".equals(fieldName)) {
			Double total = this.totalVencido.get(String.valueOf(movim
					.getIdEmpresa()));
			value = total == null ? "0" : FORMATTER.format(total);
		} else if ("TotalAvencer".equals(fieldName)) {
			Double total = this.totalAvencer.get(String.valueOf(movim
					.getIdEmpresa()));
			value = total == null ? "0" : FORMATTER.format(total);
		} else if ("TotalAvencerClientes".equals(fieldName)) {
			value = FORMATTER.format(this.totalAvencerClientes);
		} else if ("TotalVencidoClientes".equals(fieldName)) {
			value = FORMATTER.format(this.totalVencidoClientes);
		} else if ("TotalSaldo".equals(fieldName)) {
			value = FORMATTER.format(this.totalVencidoClientes
					+ this.totalAvencerClientes);
		} else if ("Morosidad".equals(fieldName)) {
			value = this.morosidad;
		} else if ("Sucursal".equals(fieldName)) {
			value = this.sucursal;
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Ranking de Ventas por Familia Detallado..
 */
class RankingVentasPorFamiliaDetalladoDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values;
	Map<String, Double> totalImporteGs = new HashMap<String, Double>();
	Map<String, Long> totalCantidad = new HashMap<String, Long>();
	Map<String, Integer> ranking = new HashMap<String, Integer>();

	Misc misc = new Misc();

	private String familia;
	private String sucursal;
	private Date desde;
	private Date hasta;
	private String tipoRanking_;

	public RankingVentasPorFamiliaDetalladoDataSource(List<Object[]> values,
			String familia, String sucursal, Date desde, Date hasta,
			String tipoRanking) {
		this.values = values;
		this.familia = familia;
		this.sucursal = sucursal;
		this.desde = desde;
		this.hasta = hasta;
		this.tipoRanking_ = tipoRanking;

		try {
			for (Object[] data : values) {
				Double total = this.totalImporteGs.get(data[5]);
				Long totalCant = this.totalCantidad.get(data[5]);

				if (data[3] instanceof Integer) {
					data[3] = ((Integer) data[3]).longValue();
				}

				if (total == null) {
					this.totalImporteGs.put((String) data[5],
							((double) data[4] * (long) data[3]));
				} else {
					this.totalImporteGs.put((String) data[5], total
							+ ((double) data[4] * (long) data[3]));
				}

				if (totalCant == null) {
					this.totalCantidad.put((String) data[5], (long) data[3]);
				} else {
					this.totalCantidad.put((String) data[5], totalCant
							+ ((long) data[3]));
				}
			}

			// ordena la lista segun el tipo de ranking..
			Collections.sort(this.values, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					boolean cant = tipoRanking_
							.equals(ReportesFiltros.RANKING_CANTIDAD);
					double id1 = cant ? totalCantidad.get(o1[5])
							: totalImporteGs.get(o1[5]);
					double id2 = cant ? totalCantidad.get(o2[5])
							: totalImporteGs.get(o2[5]);
					return (int) (id2 - id1);
				}
			});

			for (Object[] data : this.values) {
				int index = 1;
				Integer indice = this.ranking.get(data[5]);
				if (indice == null) {
					this.ranking.put((String) data[5], index);
					index++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int index = -1;

	/**
	 * Object[] det = [0]:concepto [1]:fecha [2]:numero [3]:cantidad [4]:precio
	 * [5]:cod art [6]:desc art
	 */
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);

		if ("TituloDetalle".equals(fieldName)) {
			value = det[5] + " - " + det[6];
		} else if ("Emision".equals(fieldName)) {
			value = misc.dateToString((Date) det[1], Misc.DD_MM_YYYY);
		} else if ("Concepto".equals(fieldName)) {
			value = det[2] + " - " + det[0];
		} else if ("Cantidad".equals(fieldName)) {
			value = FORMATTER.format(det[3]);
		} else if ("PrecioGs".equals(fieldName)) {
			value = FORMATTER.format(det[4]);
		} else if ("Importe".equals(fieldName)) {
			value = FORMATTER.format((double) det[4] * (long) det[3]);
		} else if ("TotalImporte".equals(fieldName)) {
			value = FORMATTER.format(this.totalImporteGs.get(det[5]));
		} else if ("TotalCantidad".equals(fieldName)) {
			value = FORMATTER.format(this.totalCantidad.get(det[5]));
		} else if ("FechaDesde".equals(fieldName)) {
			value = this.misc.dateToString(this.desde, Misc.DD_MM_YYYY);
		} else if ("FechaHasta".equals(fieldName)) {
			value = this.misc.dateToString(this.hasta, Misc.DD_MM_YYYY);
		} else if ("Familia".equals(fieldName)) {
			value = this.familia.toUpperCase();
		} else if ("Sucursal".equals(fieldName)) {
			value = this.sucursal;
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * Reporte de Ranking de Clientes VEN-00014..
 */
class ReporteRankingDeClientes extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private String sucursal;
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Nro.", TIPO_INTEGER, 10);
	static DatosColumnas col1 = new DatosColumnas("Ruc", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Importe Gs. S/iva", TIPO_DOUBLE, 40, true);
	static DatosColumnas col4 = new DatosColumnas("Costo Gs. S/iva", TIPO_DOUBLE, 40, true);

	public ReporteRankingDeClientes(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ranking de Clientes");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Ranking de Ventas Por Cliente / Articulo VEN-00015..
 */
class ReporteRankingVentasClienteArticulo extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String sucursal;
	private String cliente;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Nro.", TIPO_INTEGER, 20);
	static DatosColumnas col2 = new DatosColumnas("Código", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Cant.", TIPO_LONG, 15);
	static DatosColumnas col5 = new DatosColumnas("Importe Gs.",
			TIPO_DOUBLE_GS, 30, true);

	public ReporteRankingVentasClienteArticulo(Date desde, Date hasta,
			String sucursal, String cliente) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.cliente = cliente;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ranking de Ventas de Articulos por Cliente");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Cliente", this.cliente)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Retenciones emitidas / recibidas
 */
class ReporteRetenciones extends ReporteYhaguy {
	private Date desde;
	private Date hasta;
	String tipoRetencion;
	String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha Cbte.", TIPO_STRING,
			35);
	static DatosColumnas col2 = new DatosColumnas("Nro. Comprobante",
			TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Nro. Retención", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Importe Gs.",
			TIPO_DOUBLE_GS, 35, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	public ReporteRetenciones(Date desde, Date hasta, String tipoRetencion,
			String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.tipoRetencion = tipoRetencion;
		this.sucursal = sucursal;
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Retenciones");
		this.setDirectorio("recibos");
		this.setNombreArchivo("Ret-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Sucursal", this.sucursal))
				.add(this.textoParValor("Tipos de Retenciones",
						this.tipoRetencion)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * TES-00020 Cobranzas segun Forma Pago
 */
class ReporteCobranzasFormaPago extends ReporteYhaguy {
	private Date desde;
	private Date hasta;
	String formaPago;
	String sucursal;
	String recibos;
	String ventas;
	String anticipos;
	String reembolsos;
	String cobroExterno;
	String notasCredito;
	String ivaIncluido;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 30);
	static DatosColumnas col2_ = new DatosColumnas("Concepto", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Forma de Pago", TIPO_STRING, 40);
	static DatosColumnas col5 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 35, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col2_);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	public ReporteCobranzasFormaPago(Date desde, Date hasta, String formaPago, String recibos, String ventas,
			String anticipos, String reembolsos, String cobroExterno, String notasCredito, String ivaIncluido,
			String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.formaPago = formaPago;
		this.recibos = recibos;
		this.ventas = ventas;
		this.anticipos = anticipos;
		this.reembolsos = reembolsos;
		this.cobroExterno = cobroExterno;
		this.notasCredito = notasCredito;
		this.ivaIncluido = ivaIncluido;
		this.sucursal = sucursal;
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Recaudación Según Forma de Pago");
		this.setDirectorio("recibos");
		this.setNombreArchivo("Cob-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Forma de Pago", this.formaPago))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Recibos", this.recibos))
				.add(this.textoParValor("Ventas Contado", this.ventas))
				.add(this.textoParValor("Anticipos", this.anticipos))
				.add(this.textoParValor("Reembolsos Cheques", this.reembolsos)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Cobranzas (otra Suc)", this.cobroExterno))
				.add(this.textoParValor("Notas Cred. Contado", this.notasCredito))
				.add(this.textoParValor("Iva Incluído", this.ivaIncluido))
				.add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Cheques de Terceros TES-00013..
 */
class ReporteChequesDeTerceros extends ReporteYhaguy {

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col = new DatosColumnas("Emision", TIPO_STRING, 20);
	static DatosColumnas col0 = new DatosColumnas("Vto.", TIPO_STRING, 20);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Banco", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Librador", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Depositado", TIPO_STRING, 30);
	static DatosColumnas col5 = new DatosColumnas("Descontado", TIPO_STRING, 30);
	static DatosColumnas col6 = new DatosColumnas("Importe", TIPO_DOUBLE, 30, true);

	private Date desde;
	private Date hasta;
	private String sucursal;
	private String numeroCheque;
	private String banco;
	private String cliente;

	public ReporteChequesDeTerceros(Date desde, Date hasta, String sucursal,
			String numeroCheque, String banco, String cliente) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.numeroCheque = numeroCheque;
		this.banco = banco;
		this.cliente = cliente;
	}

	static {
		cols.add(col);
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("banco");
		this.setNombreArchivo("cheque-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Nro. Cheque", this.numeroCheque))
				.add(this.textoParValor("Banco", this.banco))
				.add(this.textoParValor("Cliente", this.cliente)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Usuarios SIS-00001..
 */
class ReporteUsuarios extends ReporteYhaguy {
	Misc m = new Misc();

	private boolean activos = true;
	private boolean inactivos = true;

	static DatosColumnas col1 = new DatosColumnas("Nombre", TIPO_STRING, 80);
	static DatosColumnas col2 = new DatosColumnas("Login", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Ingreso", TIPO_DATE, 45);
	static DatosColumnas col4 = new DatosColumnas("Inactivación", TIPO_STRING,
			45);
	static DatosColumnas col5 = new DatosColumnas("Motivo", TIPO_STRING);

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();

	public ReporteUsuarios() {
	}

	public ReporteUsuarios(boolean activos, boolean inactivos) {
		this.activos = activos;
		this.inactivos = inactivos;
	}

	private void setColumnas() {
		cols = new ArrayList<DatosColumnas>();
		col1.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col2.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col3.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		col4.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		col5.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		if (this.inactivos) {
			cols.add(col4);
			cols.add(col5);
		}
	}

	@Override
	public void informacionReporte() {
		this.setColumnas();
		this.setTitulosColumnas(cols);
		if (this.activos && !this.inactivos)
			this.setTitulo("Usuarios Activos del Sistema.");
		else if (!this.activos && this.inactivos)
			this.setTitulo("Usuarios Inactivos del Sistema.");
		else
			this.setTitulo("Usuarios del Sistema.");
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = null;
		out = cmp.verticalList();
		out.add(cmp.verticalGap(20));
		return out;
	}

	public void setDatosUsuarios(List<Usuario> usuarios) {
		List<Object[]> data = new ArrayList<>();
		Object[] obj;
		if (usuarios.size() == 0) {
			Object[] objCero;
			if (this.activos && !this.inactivos) {
				objCero = new Object[3];
				objCero[0] = "-";
				objCero[1] = "-";
				objCero[2] = null;
			} else if (!this.activos && !this.inactivos) {
				objCero = new Object[3];
				objCero[0] = "-";
				objCero[1] = "-";
				objCero[2] = null;
			} else {
				objCero = new Object[5];
				objCero[0] = "-";
				objCero[1] = "-";
				objCero[2] = null;
				objCero[3] = null;
				objCero[4] = "-";
			}

			data.add(objCero);
		} else {
			for (Usuario u : usuarios) {
				if (this.activos && !this.inactivos) {
					obj = new Object[3];
					obj[0] = u.getNombre();
					obj[1] = u.getLogin();
					obj[2] = u.getFechaDeIngreso();
				} else {
					obj = new Object[5];
					obj[0] = u.getNombre();
					obj[1] = u.getLogin();
					obj[2] = u.getFechaDeIngreso();
					obj[3] = u.getFechaDeInactivacion();
					obj[4] = u.getMotivoDeInactivacion();
				}

				data.add(obj);
			}
		}
		this.setDatosReporte(data);
	}

}

/**
 * Reporte de Perfiles de usuario SIS-00002..
 */
class ReportePerfilesUsuario extends ReporteYhaguy {

	private String nombre = "";
	private String login = "";
	private boolean activo = true;
	private boolean todo = true;

	static DatosColumnas col1 = new DatosColumnas("Nro.", TIPO_INTEGER, 20);
	static DatosColumnas col2 = new DatosColumnas("Usuario", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Perfil", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Activo", TIPO_STRING, 30);

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();

	public ReportePerfilesUsuario() {
	}

	public ReportePerfilesUsuario(boolean todo) {
		this.todo = todo;
	}

	private void setColumnas() {
		cols = new ArrayList<DatosColumnas>();
		col1.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		col2.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col3.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col4.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		cols.add(col1);
		if (todo)
			cols.add(col2);
		cols.add(col3);
		if (todo)
			cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setColumnas();
		this.setTitulosColumnas(cols);
		if (todo)
			this.setTitulo("Perfiles de Usuarios");
		else
			this.setTitulo("Perfiles del Usuario");
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		HorizontalListBuilder f1 = cmp.horizontalList();
		if (todo) {
			f1.add(this.textoParValor("Nombre", "Todos"));
			f1.add(this.textoParValor("Login", "Todos"));
		} else {
			f1.add(this.textoParValor("Nombre", this.nombre));
			f1.add(this.textoParValor("Login", this.login));
			f1.add(this.textoParValor("Activo", this.activo ? "SI":"NO"));
		}
		out.add(f1);
		out.add(cmp.verticalGap(20));
		return out;
	}

	public void setDatosPerfilesUsuario(Set<Perfil> perfiles) {
		List<Object[]> data = new ArrayList<>();
		Object[] obj;
		if (perfiles.size() == 0) {
			Object[] objCero;
			objCero = new Object[2];
			objCero[0] = "-";
			objCero[1] = "-";
			data.add(objCero);
		} else {
			int con = 0;
			for (Perfil p : perfiles) {
				con++;
				obj = new Object[2];
				obj[0] = con;
				obj[1] = p.getNombre();
				data.add(obj);
			}
		}
		this.setDatosReporte(data);
	}

	public void setDatosTodosLosPerfilesUsuarios(List<Usuario> usuarios) {
		List<Object[]> data = new ArrayList<>();
		Object[] obj;
		if (usuarios.size() == 0) {
			Object[] objCero;
			objCero = new Object[4];
			objCero[0] = "-";
			objCero[1] = "-";
			objCero[2] = "-";
			objCero[3] = "-";
			data.add(objCero);
		} else {
			int con = 0;
			for (Usuario u : usuarios) {
				for (Perfil p : u.getPerfiles()) {
					con++;
					obj = new Object[4];
					obj[0] = con;
					obj[1] = u.getNombre();
					obj[2] = p.getNombre();
					obj[3] = u.isActivo()? "SI" : "NO";
					data.add(obj);
				}
			}
		}
		this.setDatosReporte(data);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

}

/**
 * Reporte de Articulos sin movimiento STK-00008..
 */
class ReporteArticulosSinMovimiento extends ReporteYhaguy {

	private String sucursal;
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Código", TIPO_STRING, 30);
	static DatosColumnas col1 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Stock", TIPO_LONG, 20, true);

	public ReporteArticulosSinMovimiento(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Artículos sin Movimiento");
		this.setDirectorio("Articulos");
		this.setNombreArchivo("SinMovimiento-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * TES-00021 Cheques pendientes de descuento..
 */
class ReporteChequesPendientesDescuento extends ReporteYhaguy {
	Date desde;
	Date hasta;
	Date vtoDesde;
	Date vtoHasta;
	String sucursal;
	String banco;
	String descuento;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Vto.Cheque", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Nro.Cheque", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Bco.Cheque", TIPO_STRING, 40);
	static DatosColumnas col4 = new DatosColumnas("Fecha Dto.", TIPO_STRING, 30);
	static DatosColumnas col5 = new DatosColumnas("Nro.Dto.", TIPO_STRING, 25);
	static DatosColumnas col6 = new DatosColumnas("Bco.Dto.", TIPO_STRING, 40);	
	static DatosColumnas col7 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col8 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 35, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
	}

	public ReporteChequesPendientesDescuento(Date desde, Date hasta, String sucursal, String banco, Date vtoDesde, Date vtoHasta) {
		this.desde = desde;
		this.hasta = hasta;
		this.vtoDesde = vtoDesde;
		this.vtoHasta = vtoHasta;
		this.sucursal = sucursal;
		this.banco = banco;
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Descuento de Cheques");
		this.setDirectorio("banco");
		this.setNombreArchivo("DtoCheque-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp
				.horizontalFlowList().add(this.textoParValor("Descuento Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Descuento Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Vto.Cheque Desde", m.dateToString(this.vtoDesde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Vto.Cheque Hasta", m.dateToString(this.vtoHasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Banco", this.banco)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * TES-00022 Cheques depositados y no depositados..
 */
class ReporteChequesDepositados extends ReporteYhaguy {
	private Date desde;
	private Date hasta;
	String sucursal;
	String banco;
	String deposito;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Nro. Cheque", TIPO_LONG, 40);
	static DatosColumnas col2 = new DatosColumnas("Fecha", TIPO_DATE, 30);
	static DatosColumnas col3 = new DatosColumnas("Banco", TIPO_STRING, 40);
	static DatosColumnas col4 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Deposito", TIPO_STRING, 50);
	static DatosColumnas col6 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 35, true);

	static {
		cols.add(col2);
		cols.add(col1);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
	}

	public ReporteChequesDepositados(Date desde, Date hasta, String sucursal, String banco, String deposito) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.banco = banco;
		this.deposito = deposito;
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Cheques depositados / no depositados");
		this.setDirectorio("banco");
		this.setNombreArchivo("Cheque-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Banco", this.banco))
				.add(this.textoParValor("Deposito", this.deposito))
				.add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de existencia de Articulos STK-00009..
 */
class ReporteExistenciaArticulos extends ReporteYhaguy {

	private String familia;
	private String marca;
	private String articulo;
	private String deposito;
	private String proveedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Depósito", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Stock", TIPO_LONG, 20, true);
	

	public ReporteExistenciaArticulos(String familia, String articulo, String deposito, String marca, String proveedor) {
		this.familia = familia;
		this.articulo = articulo;
		this.deposito = deposito;
		this.marca = marca;
		this.proveedor = proveedor;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Existencia de Artículos");
		this.setDirectorio("Articulos");
		this.setNombreArchivo("Stock-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Familia", this.familia)).add(this.textoParValor("Articulo", this.articulo)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Depósito", this.deposito)).add(this.textoParValor("Marca", this.marca)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Proveedor", this.proveedor)).add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de existencia de Articulos STK-00010..
 */
class ReporteExistenciaArticulosDeposito extends ReporteYhaguy {

	private String familia;
	private String marca;
	private String articulo;
	private String proveedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 40);
	
	public ReporteExistenciaArticulosDeposito(String familia, String articulo, String marca, String proveedor, List<Deposito> deps) {
		this.familia = familia;
		this.articulo = articulo;
		this.marca = marca;
		this.proveedor = proveedor;
		cols.clear();
		cols.add(col1);
		for (Deposito deposito : deps) {
			cols.add(new DatosColumnas(deposito.getObservacion().toLowerCase(), TIPO_LONG, 20, true));
		}
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Existencia de Artículos por Depósito");
		this.setDirectorio("Articulos");
		this.setNombreArchivo("Stock-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Familia", this.familia)).add(this.textoParValor("Articulo", this.articulo)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Proveedor", this.proveedor)).add(this.textoParValor("Marca", this.marca)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Comision por ventas cobradas VEN-00016..
 */
class ReporteComisionCobro extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;
	private String vendedor;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Proveedor", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Contado", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col2 = new DatosColumnas("Cobrado", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col3 = new DatosColumnas("N.C. Dscto", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col4 = new DatosColumnas("Total", TIPO_DOUBLE_GS, 30, true);

	public ReporteComisionCobro(Date desde, Date hasta, String vendedor, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.vendedor = vendedor;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Comision por Ventas Cobradas (S/iva)");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Vendedor", this.vendedor))
				.add(this.texto(""))
				.add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Clientes Credito TES-00023..
 */
class ReporteClientesCredito extends ReporteYhaguy {
	
	private String estado;
	private String sucursal;
	private String cartera;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Ruc", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Estado", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Límite Créd.", TIPO_DOUBLE_GS, 30, true);

	public ReporteClientesCredito(String estado, String sucursal, String cartera) {
		this.estado = estado;
		this.sucursal = sucursal;
		this.cartera = cartera;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Clientes Crédito");
		this.setDirectorio("Clientes");
		this.setNombreArchivo("Cli-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Estado", this.estado))
				.add(this.textoParValor("Cartera", this.cartera))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Boletas de Deposito TES-00024..
 */
class ReporteBoletasDeposito extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;
	private String banco;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING,30);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Banco", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Cajas", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Importe", TIPO_DOUBLE_GS, 30, true);

	public ReporteBoletasDeposito(Date desde, Date hasta, String banco, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.banco = banco;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Boletas de Deposito");
		this.setDirectorio("banco");
		this.setNombreArchivo("Deposito-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Banco", this.banco.toUpperCase()))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Cheques Propios TES-00025..
 */
class ReporteChequesPropios extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;
	private String banco;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Vencimiento", TIPO_STRING,30);
	static DatosColumnas col2 = new DatosColumnas("Emision", TIPO_STRING,30);
	static DatosColumnas col3 = new DatosColumnas("Número", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Banco", TIPO_STRING, 40);
	static DatosColumnas col5 = new DatosColumnas("Beneficiario", TIPO_STRING);
	static DatosColumnas col7 = new DatosColumnas("Importe USD", TIPO_DOUBLE_DS, 30, true);
	static DatosColumnas col8 = new DatosColumnas("Importe Gs", TIPO_DOUBLE_GS, 30, true);

	public ReporteChequesPropios(Date desde, Date hasta, String banco, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.banco = banco;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col7);
		cols.add(col8);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Emision de Cheques");
		this.setDirectorio("banco");
		this.setNombreArchivo("Cheque-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Banco", this.banco.toUpperCase()))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Recaudacion Casa Central TES-00027..
 */
class ReporteRecaudacionCentral extends ReporteYhaguy {
	
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1_ = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Recibo Nro.", TIPO_STRING, 35);
	static DatosColumnas col1_1 = new DatosColumnas("Cheque Nro.", TIPO_STRING, 35);
	static DatosColumnas col1_2 = new DatosColumnas("Depósito Nro.", TIPO_STRING, 35);
	static DatosColumnas col2 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col3 = new DatosColumnas("Saldo Gs.", TIPO_DOUBLE_GS, 30, true);

	public ReporteRecaudacionCentral(String sucursal) {
		this.sucursal = sucursal;
	}

	static {
		cols.add(col1_);
		cols.add(col1);
		cols.add(col1_1);
		cols.add(col1_2);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Recaudaciones Casa Central");
		this.setDirectorio("recibos");
		this.setNombreArchivo("rcc-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Cheques Propios TES-00028..
 */
class ReporteChequesPropiosAvencer extends ReporteYhaguy {
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Emisión", TIPO_STRING,30);
	static DatosColumnas col0_ = new DatosColumnas("Vencimiento", TIPO_STRING,30);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Banco", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Beneficiario", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Importe USD", TIPO_DOUBLE_DS, 30, true);
	static DatosColumnas col5 = new DatosColumnas("Importe Gs", TIPO_DOUBLE_GS, 30, true);

	public ReporteChequesPropiosAvencer(String sucursal) {
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col0_);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Cheques emitidos a vencer");
		this.setDirectorio("banco");
		this.setNombreArchivo("Cheque-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Anticipo de Utilidad TES-00029..
 */
class ReporteAnticipoUtilidad extends ReporteYhaguy {
	
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 25);
	static DatosColumnas col3 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 30, true);

	public ReporteAnticipoUtilidad(String sucursal) {
		this.sucursal = sucursal;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Anticipos de Utilidad");
		this.setDirectorio("banco");
		this.setNombreArchivo("au-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Anticipo de Utilidad TES-00033..
 */
class ReportePrestamoCasaCentral extends ReporteYhaguy {
	
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 25);
	static DatosColumnas col3 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 30, true);

	public ReportePrestamoCasaCentral(String sucursal) {
		this.sucursal = sucursal;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Prestamos Casa Central");
		this.setDirectorio("banco");
		this.setNombreArchivo("au-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Anticipo de Utilidad TES-00034..
 */
class ReporteReembolsosPrestamoCasaCentral extends ReporteYhaguy {
	
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE, 30, true);

	public ReporteReembolsosPrestamoCasaCentral(String sucursal) {
		this.sucursal = sucursal;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Reembolsos de Prestamos Casa Central");
		this.setDirectorio("banco");
		this.setNombreArchivo("au-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de cantidad vendida por articulo VEN-00017..
 */
class ReporteCantidadVendida extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;
	private String articulo;
	private String proveedor;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col1 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Cant. Venta", TIPO_LONG, 30, true);
	static DatosColumnas col3 = new DatosColumnas("Devolución", TIPO_LONG, 30, true);
	static DatosColumnas col4 = new DatosColumnas("Total", TIPO_LONG, 30, true);
	static DatosColumnas col5 = new DatosColumnas("Stock", TIPO_LONG, 30, true);
	static DatosColumnas col6 = new DatosColumnas("Ult. Costo", TIPO_DOUBLE_GS, 30, true);

	public ReporteCantidadVendida(Date desde, Date hasta, String articulo, String proveedor, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.articulo = articulo;
		this.proveedor = proveedor;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Cantidad de Ventas por Artículo");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Artículo", this.articulo))
				.add(this.textoParValor("Proveedor", this.proveedor))
				.add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * DataSource de Comision por cobros..
 */
class ComisionCobroDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<MyArray> values = new ArrayList<MyArray>();
	HashMap<String, Double> totalSaldo = new HashMap<String, Double>();
	Misc misc = new Misc();
	
	public ComisionCobroDataSource(List<MyArray> values) {
		this.values = values;
		for (MyArray value : this.values) {
			Double saldo = totalSaldo.get(value.getPos1());
			if (saldo == null) {
				this.totalSaldo.put((String) value.getPos1(),
						(Double) value.getPos6());
			} else {
				this.totalSaldo.put((String) value.getPos1(), saldo
						+ (Double) value.getPos6());
			}
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		MyArray det = this.values.get(index);

		if ("TituloDetalle".equals(fieldName)) {
			value = det.getPos1();
		} else if ("Proveedor".equals(fieldName)) {
			value = det.getPos2();
		} else if ("ImporteVenta".equals(fieldName)) {
			double venta = (double) det.getPos3();
			value = FORMATTER.format(venta);
		} else if ("ImporteCobro".equals(fieldName)) {
			double cobro = (double) det.getPos4();
			value = FORMATTER.format(cobro);
		} else if ("ImporteNC".equals(fieldName)) {
			double nc = (double) det.getPos5();
			value = FORMATTER.format(nc);
		} else if ("SaldoGs".equals(fieldName)) {
			double saldo = (double) det.getPos6();
			value = FORMATTER.format(saldo);
		} else if ("TotalImporte".equals(fieldName)) {
			double total = this.totalSaldo.get(det.getPos1());
			value = FORMATTER.format(total);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * Reporte de total cobranzas por vendedor VEN-00018..
 */
class ReporteTotalCobranzas extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Vendedor", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Total Cobrado Gs.", TIPO_DOUBLE_GS, 35, true);
	static DatosColumnas col3 = new DatosColumnas("Total Cobrado S/iva", TIPO_DOUBLE_GS, 35, true);

	public ReporteTotalCobranzas(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {		
		this.setDirectorio("ventas");
		this.setNombreArchivo("Cobro-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de total cobranzas ventas por vendedor VEN-00018..
 */
class ReporteTotalCobranzasVentas extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String vendedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Vendedor", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Total Cobrado S/iva", TIPO_DOUBLE_GS, 35, true);
	static DatosColumnas col3 = new DatosColumnas("Total Contado S/iva", TIPO_DOUBLE_GS, 35, true);

	public ReporteTotalCobranzasVentas(Date desde, Date hasta, String vendedor) {
		this.desde = desde;
		this.hasta = hasta;
		this.vendedor = vendedor;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {		
		this.setDirectorio("ventas");
		this.setNombreArchivo("Cobro-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Vendedor", this.vendedor)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de total cobranzas ventas por vendedor VEN-00039..
 */
class ReporteTotalCobranzasVentasProveedor extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String vendedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Proveedor", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Total Cobrado S/iva", TIPO_DOUBLE_GS, 35, true);
	static DatosColumnas col3 = new DatosColumnas("Total Contado S/iva", TIPO_DOUBLE_GS, 35, true);

	public ReporteTotalCobranzasVentasProveedor(Date desde, Date hasta, String vendedor) {
		this.desde = desde;
		this.hasta = hasta;
		this.vendedor = vendedor;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("ventas");
		this.setNombreArchivo("Cobro-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Vendedor", this.vendedor)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de total cobranzas ventas por vendedor VEN-00045..
 */
class ReporteTotalCobranzasVentasCliente extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String vendedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Total Cobrado S/iva", TIPO_DOUBLE_GS, 35, true);
	static DatosColumnas col3 = new DatosColumnas("Total Contado S/iva", TIPO_DOUBLE_GS, 35, true);
	static DatosColumnas col4 = new DatosColumnas("N.Cred.Promoción S/iva", TIPO_DOUBLE_GS, 35, true);

	public ReporteTotalCobranzasVentasCliente(Date desde, Date hasta, String vendedor) {
		this.desde = desde;
		this.hasta = hasta;
		this.vendedor = vendedor;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("ventas");
		this.setNombreArchivo("Cobro-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Vendedor", this.vendedor)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de total ventas por vendedor..
 */
class ReporteTotalVentas extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String vendedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Vendedor", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Contado S/iva", TIPO_DOUBLE_GS, 35, true);
	static DatosColumnas col3 = new DatosColumnas("Crédito S/iva", TIPO_DOUBLE_GS, 35, true);
	static DatosColumnas col4 = new DatosColumnas("Anulados S/iva", TIPO_DOUBLE_GS, 35, true);
	static DatosColumnas col5 = new DatosColumnas("N.Credito S/iva", TIPO_DOUBLE_GS, 35, true);
	static DatosColumnas col6 = new DatosColumnas("Total S/iva", TIPO_DOUBLE_GS, 35, true);

	public ReporteTotalVentas(Date desde, Date hasta, String vendedor) {
		this.desde = desde;
		this.hasta = hasta;
		this.vendedor = vendedor;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Total ventas contado / credito por vendedor");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Ventas-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Vendedor", this.vendedor)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de ventas por vendedor agrupado VEN-00021..
 */
class ReporteVentasVendedorAgrupado extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;
	private String sucursal;
	private String vendedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Vendedor", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Ventas S/iva", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col3 = new DatosColumnas("Notas de Cred. S/iva", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col4 = new DatosColumnas("Saldo Gs.", TIPO_DOUBLE_GS, 30, true);

	public ReporteVentasVendedorAgrupado(Date desde, Date hasta, String sucursal, String vendedor) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.vendedor = vendedor;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ventas por Vendedor Agrupado S/iva");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Vendedor", this.vendedor))
				.add(this.texto(""))
				.add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de total cobranzas ventas por cobrador TES-00052..
 */
class ReporteTotalCobranzasVentasCobrador extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String cobrador;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Cobrador", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Total Cobrado S/iva", TIPO_DOUBLE_GS, 35, true);
	static DatosColumnas col3 = new DatosColumnas("Total Contado S/iva", TIPO_DOUBLE_GS, 35, true);

	public ReporteTotalCobranzasVentasCobrador(Date desde, Date hasta, String cobrador) {
		this.desde = desde;
		this.hasta = hasta;
		this.cobrador = cobrador;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Total cobranzas / ventas contado por cobrador");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Cobro-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Cobrador", this.cobrador)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Clientes por Cobrador TES-00026..
 */
class ReporteClientesPorCobrador extends ReporteYhaguy {
	
	private String cobrador;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Ruc", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Dirección", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Teléfono", TIPO_STRING, 50);

	public ReporteClientesPorCobrador(String cobrador, String sucursal) {
		this.cobrador = cobrador;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Clientes por Cobrador");
		this.setDirectorio("Clientes");
		this.setNombreArchivo("Cli-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Cobrador", this.cobrador.toUpperCase()))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de matriz para reposicion de mercaderias COM-00003..
 */
class ReporteMatrizReposicion extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	static final String MATRIZ_REPOSICION = "Matriz para reposición de mercaderías";
	static final String MATRIZ_IMPORTACIONES = "Matriz de Importaciones de mercaderías";
	
	private Date desde;
	private Date hasta;
	private String proveedor;
	private String sucursal;
	private String titulo;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Costo Ds", TIPO_DOUBLE_DS, 80);
	static DatosColumnas col2 = new DatosColumnas("Costo Gs", TIPO_DOUBLE_GS, 80);
	static DatosColumnas col3 = new DatosColumnas("Ene", TIPO_DOUBLE, 50, true);
	static DatosColumnas col4 = new DatosColumnas("Feb", TIPO_DOUBLE, 50, true);
	static DatosColumnas col5 = new DatosColumnas("Mar", TIPO_DOUBLE, 50, true);
	static DatosColumnas col6 = new DatosColumnas("Abr", TIPO_DOUBLE, 50, true);
	static DatosColumnas col7 = new DatosColumnas("May", TIPO_DOUBLE, 50, true);
	static DatosColumnas col8 = new DatosColumnas("Jun", TIPO_DOUBLE, 50, true);
	static DatosColumnas col9 = new DatosColumnas("Jul", TIPO_DOUBLE, 50, true);
	static DatosColumnas col10 = new DatosColumnas("Ago", TIPO_DOUBLE, 50, true);
	static DatosColumnas col11 = new DatosColumnas("Set", TIPO_DOUBLE, 50, true);
	static DatosColumnas col12 = new DatosColumnas("Oct", TIPO_DOUBLE, 50, true);
	static DatosColumnas col13 = new DatosColumnas("Nov", TIPO_DOUBLE, 50, true);
	static DatosColumnas col14 = new DatosColumnas("Dic", TIPO_DOUBLE, 50, true);
	static DatosColumnas col15 = new DatosColumnas("Stock", TIPO_LONG, 50);
	static DatosColumnas col16 = new DatosColumnas("Max", TIPO_DOUBLE, 50);
	static DatosColumnas col17 = new DatosColumnas("Prom", TIPO_DOUBLE, 50);
	
	public ReporteMatrizReposicion(Date desde, Date hasta, String proveedor, String sucursal, String titulo) {
		this.desde = desde;
		this.hasta = hasta;
		this.proveedor = proveedor;
		this.sucursal = sucursal;
		this.titulo = titulo;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
		cols.add(col9);
		cols.add(col10);
		cols.add(col11);
		cols.add(col12);
		cols.add(col13);
		cols.add(col14);
		cols.add(col15);
		cols.add(col16);
		cols.add(col17);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo(this.titulo);
		this.setDirectorio("compras");
		this.setNombreArchivo("matriz-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto(""))); //
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Proveedor", this.proveedor.toUpperCase())));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de ventas de clientes por mes VEN-00019..
 */
class ReporteVentaClientesPorMes extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Ene", TIPO_DOUBLE_GS, true);
	static DatosColumnas col2 = new DatosColumnas("Feb", TIPO_DOUBLE_GS, true);
	static DatosColumnas col3 = new DatosColumnas("Mar", TIPO_DOUBLE_GS, true);
	static DatosColumnas col4 = new DatosColumnas("Abr", TIPO_DOUBLE_GS, true);
	static DatosColumnas col5 = new DatosColumnas("May", TIPO_DOUBLE_GS, true);
	static DatosColumnas col6 = new DatosColumnas("Jun", TIPO_DOUBLE_GS, true);
	static DatosColumnas col7 = new DatosColumnas("Jul", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col8 = new DatosColumnas("Ago", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col9 = new DatosColumnas("Set", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col10 = new DatosColumnas("Oct", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col11 = new DatosColumnas("Nov", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col12 = new DatosColumnas("Dic", TIPO_DOUBLE_GS, 30, true);
	
	public ReporteVentaClientesPorMes(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col7);
		cols.add(col8);
		cols.add(col9);
		cols.add(col10);
		cols.add(col11);
		cols.add(col12);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ventas por Clientes por Mes");
		this.setDirectorio("ventas");
		this.setNombreArchivo("venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto(""))); 
		return out;
	}
}

/**
 * Reporte de Ventas por Vendedor VEN-00004..
 */
class ReporteVentasPorVendedor extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String proveedor;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Vendedor", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Ene", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col2 = new DatosColumnas("Feb", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col3 = new DatosColumnas("Mar", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col4 = new DatosColumnas("Abr", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col5 = new DatosColumnas("May", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col6 = new DatosColumnas("Jun", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col7 = new DatosColumnas("Jul", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col8 = new DatosColumnas("Ago", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col9 = new DatosColumnas("Set", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col10 = new DatosColumnas("Oct", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col11 = new DatosColumnas("Nov", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col12 = new DatosColumnas("Dic", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col13 = new DatosColumnas("Total", TIPO_DOUBLE_GS, 50);
	
	public ReporteVentasPorVendedor(Date desde, Date hasta, String proveedor, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.proveedor = proveedor;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
		cols.add(col9);
		cols.add(col10);
		cols.add(col11);
		cols.add(col12);
		cols.add(col13);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ventas por Vendedor / Proveedor");
		this.setDirectorio("ventas");
		this.setNombreArchivo("vta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto(""))); //
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Proveedor", this.proveedor.toUpperCase())));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Pedidos Pendientes VEN-00020..
 */
class ReportePedidosPendientes extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String sucursal;
	private String titulo;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 25);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 35);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Ruc", TIPO_STRING, 25);
	static DatosColumnas col4 = new DatosColumnas("Observación", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Total Gs.", TIPO_DOUBLE_GS,
			25, true);

	public ReportePedidosPendientes(Date desde, Date hasta, String sucursal, String titulo) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.titulo = titulo;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo(titulo);
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ventas y sus recibos VEN-00021..
 */
class ReporteVentasRecibos extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String sucursal;
	private String cliente;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha Vta.", TIPO_STRING, 30);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Fecha Rec.", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Nro. Recibo", TIPO_STRING, 40);
	static DatosColumnas col6 = new DatosColumnas("Nro. N/C", TIPO_STRING, 40);
	static DatosColumnas col7 = new DatosColumnas("Concepto N/C", TIPO_STRING, 40);
	static DatosColumnas col8 = new DatosColumnas("Saldo Gs.", TIPO_DOUBLE_GS, 30);

	public ReporteVentasRecibos(Date desde, Date hasta, String cliente, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.cliente = cliente;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ventas / Recibos / Notas de Crédito");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Cliente", this.cliente))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte Costo Final por Articulo COM-00004..
 */
class ReporteCostoFinalPorArticulo extends ReporteYhaguy {
	
	private String sucursal;
	private String articulo;
	private String listaPrecio;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Nro. Compra", TIPO_STRING, 40);
	static DatosColumnas col4 = new DatosColumnas("Proveedor", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Costo Gs. S/iva", TIPO_DOUBLE, 40);
	static DatosColumnas col6 = new DatosColumnas("Precio Gs. S/iva", TIPO_DOUBLE, 40);
	static DatosColumnas col7 = new DatosColumnas("Precio Gs.", TIPO_DOUBLE, 35);
	static DatosColumnas col8 = new DatosColumnas("Rent.", TIPO_DOUBLE, 20);

	public ReporteCostoFinalPorArticulo(String sucursal, String articulo, String listaPrecio) {
		this.sucursal = sucursal;
		this.articulo = articulo;
		this.listaPrecio = listaPrecio;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Último costo por Artículo");
		this.setDirectorio("compras");
		this.setNombreArchivo("CostoFinal-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Artículo", articulo))
				.add(this.textoParValor("Lista de Precio", listaPrecio))
				.add(this.textoParValor("Sucursal", sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Clientes por Rubro TES-00030..
 */
class ReporteClientesPorRubro extends ReporteYhaguy {
	
	private String rubro;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Ruc", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Dirección", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Teléfono", TIPO_STRING, 40);

	public ReporteClientesPorRubro(String rubro, String sucursal) {
		this.rubro = rubro;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("Clientes");
		this.setNombreArchivo("Cli-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Rubro", this.rubro.toUpperCase()))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte codigo CON-00008
 */
class ReporteChequesAdelantadosCartera extends ReporteYhaguy {

	private Date desde;
	private Date hasta;
	
	public ReporteChequesAdelantadosCartera(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Emisión", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Vencimiento", TIPO_STRING, 30);
	static DatosColumnas col2_ = new DatosColumnas("Fecha Depósito", TIPO_STRING, 30);
	static DatosColumnas col2_2 = new DatosColumnas("Fecha Descuento", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Nro. Cheque", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Banco", TIPO_STRING, 45);
	static DatosColumnas col6 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 40, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col2_);
		cols.add(col2_2);
		cols.add(col3);
		cols.add(col4);	
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Cheques Adelantados en Cartera (a una fecha dada)");
		this.setDirectorio("banco");
		this.setNombreArchivo("ChequesDiferidos-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Fecha Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Fecha Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte codigo CON-00009
 */
class ReporteChequesDescontadosAvencer extends ReporteYhaguy {

	private String desde;
	private String hasta;
	
	public ReporteChequesDescontadosAvencer(String desde, String hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Emisión", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Vencimiento", TIPO_STRING, 30);
	static DatosColumnas col2_ = new DatosColumnas("Fecha Descuento", TIPO_STRING, 30);
	static DatosColumnas col2_2 = new DatosColumnas("Nro. Descuento", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Nro. Cheque", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Banco", TIPO_STRING, 45);
	static DatosColumnas col6 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 40, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col2_);
		cols.add(col2_2);
		cols.add(col3);
		cols.add(col4);	
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Cheques diferidos descontados con vencimiento en " + this.hasta);
		this.setDirectorio("banco");
		this.setNombreArchivo("ChequesDiferidos-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Periodo emisión cheque", this.desde))
				.add(this.textoParValor("Periodo vencimiento cheque", this.hasta)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte CON-00006
 */
class PlanillasCajaDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	List<MyArray> values = new ArrayList<MyArray>();

	double totalIngresos = 0;
	double totalEgresos = 0;

	double totalEfectivo = 0;
	double totalChequeTerceroAlDia = 0;
	double totalChequeTerceroAdelantado = 0;
	double totalTarjetaCredito = 0;
	double totalTarjetaDebito = 0;
	double totalRetencionCliente = 0;
	double totalRetencionClienteDiferido = 0;
	double totalRetencionProveedor = 0;
	double totalDepositoBancario = 0;
	double totalRecaudacionCentral = 0;
	double totalTransferenciaCentral = 0;

	double totalVentaContado = 0;
	double totalVentaContadoCheque = 0;
	double totalVentaContadoEfectivo = 0;
	double totalVentaCredito = 0;
	double totalCobranzaAlDia = 0;
	double totalCobranzaChequeAlDia = 0;
	double totalCobranzaEfectivo = 0;
	double totalCobranzaTarjetaCredito = 0;
	double totalCobranzaTarjetaDebito = 0;
	double totalCobranzaDepositoBancario = 0;
	double totalCobranzaAlDiaOtraSuc = 0;
	double totalCobranzaAdelantado = 0;
	double totalCobranzaAdelantadoOtraSuc = 0;
	double totalCobroEfectivoCheque = 0;
	double totalReposiciones = 0;
	double totalPagos = 0;
	double totalGastos = 0;
	double totalRepEgresos = 0;
	double totalNotaCreditoContado = 0;
	double totalNotaCreditoCredito = 0;
	double totalSaldoFavorCliente = 0;
	double totalCancelacionCheque = 0;
	double totalCancelacionChequeEfectivo = 0;
	double totalCancelacionChequeAldia = 0;

	@SuppressWarnings("unchecked")
	public PlanillasCajaDataSource(List<CajaPeriodo> planillas) {
		try {
			for (CajaPeriodo planilla : planillas) {
				Date fechaPlanilla = planilla.getApertura();
				
				// ventas contado..
				for (Venta venta : planilla.getVentasOrdenado()) {
					if (venta.isVentaContado()) {
						double importe = venta.isAnulado() ? 0.0 : venta.getTotalImporteGs();
						this.totalIngresos += importe;
						this.totalVentaContado += importe;
						MyArray my = new MyArray(
								venta.getDescripcionTipoMovimiento(),
								venta.getNumero() + " - " + venta.getCliente().getRazonSocial().toUpperCase(), importe, "VENTAS CONTADO",
								this.totalVentaContado);
						this.values.add(my);
						for (ReciboFormaPago fp : venta.getFormasPago()) {
							if (fp.isEfectivo()) {
								double montoEf = venta.isAnulado() ? 0.0 : fp.getMontoGs();
								this.totalVentaContadoEfectivo += montoEf;
							} else if (fp.isChequeTercero()) {
								double montoCh = venta.isAnulado() ? 0.0 : fp.getMontoChequeGs();
								this.totalVentaContadoCheque += montoCh;
							}
						}
					}
				}
				
				// ventas contado efectivo..
				for (Venta venta : planilla.getVentasOrdenado()) {
					if (venta.isVentaContado() && !venta.isAnulado()) {
						for (ReciboFormaPago fp : venta.getFormasPago()) {
							if (fp.isEfectivo()) {
								MyArray my = new MyArray(
										venta.getDescripcionTipoMovimiento(),
										venta.getNumero() + " - " + venta.getCliente().getRazonSocial().toUpperCase(), fp.getMontoGs(), "VENTAS CONTADO CON EFECTIVO",
										this.totalVentaContadoEfectivo);
								this.values.add(my);
							}
						}
					}
				}
				
				// ventas contado cheque..
				for (Venta venta : planilla.getVentasOrdenado()) {
					if (venta.isVentaContado() && !venta.isAnulado()) {
						for (ReciboFormaPago fp : venta.getFormasPago()) {
							if (fp.isChequeTercero()) {
								MyArray my = new MyArray(
										venta.getDescripcionTipoMovimiento(),
										venta.getNumero() + " - " + 
										fp.getChequeBanco().getDescripcion().toUpperCase() + " - " + fp.getChequeNumero(), 
										fp.getMontoGs(), "VENTAS CONTADO CON CHEQUE",
										this.totalVentaContadoCheque);
								this.values.add(my);
							}
						}
					}
				}

				// ventas credito..
				for (Venta venta : planilla.getVentasOrdenado()) {
					if (!venta.isVentaContado()) {
						double importe = venta.isAnulado() ? 0.0 : venta.getTotalImporteGs();
						this.totalVentaCredito += importe;
						MyArray my = new MyArray(
								venta.getDescripcionTipoMovimiento(),
								venta.getNumero() + " - " + venta.getCliente().getRazonSocial().toUpperCase(), 
								importe, "VENTAS CREDITO",
								this.totalVentaCredito);
						this.values.add(my);
					}
				}

				// cobranzas (al dia)..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCobro() && !cobro.isAnulado()
							&& !cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
						Object[] aldia = cobro.getCobranzaAlDia(fechaPlanilla);
						if (aldia != null) {
							//List<Object[]> items = (List<Object[]>) aldia[0];
							this.totalCobranzaAlDia += (double) aldia[1];
							/*for (Object[] item : items) {
								MyArray my = new MyArray(
										cobro.getTipoMovimiento().getDescripcion(),
										item[0].toString().toUpperCase(),
										item[1],
										"COBRANZAS CON EFECTIVO / CHEQUE AL DÍA",
										this.totalCobranzaAlDia);
								//this.values.add(my);
							}*/
						}
					}
				}
				
				// cobranzas con efectivo..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCobro() && !cobro.isAnulado()
							&& !cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
						Object[] aldia = cobro.getCobranzasConEfectivo();
						if (aldia != null) {
							List<Object[]> items = (List<Object[]>) aldia[0];
							this.totalCobranzaEfectivo += (double) aldia[1];
							for (Object[] item : items) {
								MyArray my = new MyArray(cobro.getTipoMovimiento()
										.getDescripcion(), item[0].toString()
										.toUpperCase() + " - " + cobro.getCliente().getRazonSocial().toUpperCase(), item[1],
										"COBRANZAS CON EFECTIVO",
										this.totalCobranzaEfectivo);
								this.values.add(my);
							}
						}
					}
				}
				
				// cobranzas con tarjeta de credito..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCobro() && !cobro.isAnulado()
							&& !cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
						Object[] aldia = cobro.getCobranzasConTarjetaCredito();
						if (aldia != null) {
							List<Object[]> items = (List<Object[]>) aldia[0];
							this.totalCobranzaTarjetaCredito += (double) aldia[1];
							for (Object[] item : items) {
								MyArray my = new MyArray(cobro.getTipoMovimiento()
										.getDescripcion(), item[0].toString()
										.toUpperCase() + " - " + cobro.getCliente().getRazonSocial().toUpperCase(), item[1],
										"COBRANZAS CON TARJETA DE CREDITO",
										this.totalCobranzaTarjetaCredito);
								this.values.add(my);
							}
						}
					}
				}
				
				// cobranzas con tarjeta de debito..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCobro() && !cobro.isAnulado()
							&& !cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
						Object[] aldia = cobro.getCobranzasConTarjetaDebito();
						if (aldia != null) {
							List<Object[]> items = (List<Object[]>) aldia[0];
							this.totalCobranzaTarjetaDebito += (double) aldia[1];
							for (Object[] item : items) {
								MyArray my = new MyArray(cobro.getTipoMovimiento()
										.getDescripcion(), item[0].toString()
										.toUpperCase() + " - " + cobro.getCliente().getRazonSocial().toUpperCase(), item[1],
										"COBRANZAS CON TARJETA DE DEBITO",
										this.totalCobranzaTarjetaDebito);
								this.values.add(my);
							}
						}
					}
				}
				
				// cobranzas con cheque (al dia)..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCobro() && !cobro.isAnulado()
							&& !cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
						Object[] aldia = cobro.getCobranzasChequeAlDia(fechaPlanilla);
						if (aldia != null) {
							List<Object[]> items = (List<Object[]>) aldia[0];
							this.totalCobranzaChequeAlDia += (double) aldia[1];
							for (Object[] item : items) {
								MyArray my = new MyArray(cobro.getTipoMovimiento()
										.getDescripcion(), item[0].toString().toUpperCase() + " - " 
										+ Utiles.getMaxLength(cobro.getCliente().getRazonSocial().toUpperCase(), 30), item[1],
										"COBRANZAS CON CHEQUE AL DÍA",
										this.totalCobranzaChequeAlDia);
								this.values.add(my);
							}
						}
					}
				}

				
				// cobranzas de otra suc. (al dia)..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCobro() && !cobro.isAnulado()
							&& cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
						Object[] aldia = cobro.getCobranzaAlDia(fechaPlanilla);
						if (aldia != null) {
							List<Object[]> items = (List<Object[]>) aldia[0];
							this.totalCobranzaAlDiaOtraSuc += (double) aldia[1];
							for (Object[] item : items) {
								MyArray my = new MyArray(
										cobro.getTipoMovimiento()
												.getDescripcion(),
										item[0].toString().toUpperCase() + " - " + cobro.getCliente().getRazonSocial().toUpperCase(),
										item[1],
										"COBRANZAS CON EFECTIVO / CHEQUE AL DÍA (OTRAS SUC.)",
										this.totalCobranzaAlDiaOtraSuc);
								this.values.add(my);
							}
						}
					}
				}
				
				// cobranzas (adelantado)..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCobro() && !cobro.isAnulado() && !cobro.isCobroExterno()
							&& !cobro.isCancelacionCheque()) {
						Object[] adel = cobro.getCobranzaChequeAdelantado(fechaPlanilla);
						if (adel != null) {
							List<Object[]> items = (List<Object[]>) adel[0];
							this.totalCobranzaAdelantado += (double) adel[1];
							for (Object[] item : items) {
								MyArray my = new MyArray(cobro
										.getTipoMovimiento().getDescripcion(),
										item[0].toString().toUpperCase() + " - " 
										+ Utiles.getMaxLength(cobro.getCliente().getRazonSocial().toUpperCase(), 30), item[1],
										"COBRANZAS CON CHEQUE ADELANTADO",
										this.totalCobranzaAdelantado);
								this.values.add(my);
							}
						}
					}		
				}
				
				// cobranzas de otra suc. (adelantado)..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCobro() && !cobro.isAnulado() && cobro.isCobroExterno()) {
						Object[] adel = cobro.getCobranzaChequeAdelantado(fechaPlanilla);
						if (adel != null) {
							List<Object[]> items = (List<Object[]>) adel[0];
							this.totalCobranzaAdelantadoOtraSuc += (double) adel[1];
							for (Object[] item : items) {
								MyArray my = new MyArray(cobro
										.getTipoMovimiento().getDescripcion(),
										item[0].toString().toUpperCase() + " - " + cobro.getCliente().getRazonSocial().toUpperCase(), item[1],
										"COBRANZAS CON CHEQUE ADELANTADO (OTRAS SUC.)",
										this.totalCobranzaAdelantadoOtraSuc);
								this.values.add(my);
							}
						}
					}		
				}
				
				// cobranzas con deposito bancario..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCobro() && !cobro.isAnulado()
							&& !cobro.isCobroExterno()
							&& !cobro.isCancelacionCheque()) {
						Object[] deps = cobro.getCobranzasConDepositoBancario();
						if (deps != null) {
							List<Object[]> items = (List<Object[]>) deps[0];
							this.totalCobranzaDepositoBancario += (double) deps[1];
							for (Object[] item : items) {
								MyArray my = new MyArray(cobro.getTipoMovimiento()
										.getDescripcion(), item[0].toString()
										.toUpperCase()
										+ " - "
										+ cobro.getCliente().getRazonSocial()
												.toUpperCase(), item[1],
										"COBRANZAS CON DEPÓSITO BANCARIO",
										this.totalCobranzaDepositoBancario);
								this.values.add(my);
							}
						}
					}
				}
				
				// aplicaciones de cta cte saldo a favor generado..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCobro() && !cobro.isAnulado() && !cobro.isCobroExterno()) {
						Object[] adel = cobro.getCobranzasConCtaCteSaldoFavorGenerado();
						if (adel != null) {
							List<Object[]> items = (List<Object[]>) adel[0];
							this.totalSaldoFavorCliente += (double) adel[1];
							for (Object[] item : items) {
								MyArray my = new MyArray(cobro
										.getTipoMovimiento().getDescripcion(),
										item[0].toString().toUpperCase(), item[1],
										"SALDO A FAVOR DEL CLIENTE (GENERADO)",
										this.totalSaldoFavorCliente);
								this.values.add(my);
							}
						}
					}		
				}
				
				// aplicaciones de cta cte saldo a favor cobrado..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCobro() && !cobro.isAnulado() && !cobro.isCobroExterno()) {
						Object[] adel = cobro.getCobranzasConCtaCteSaldoFavorCobrado();
						if (adel != null) {
							List<Object[]> items = (List<Object[]>) adel[0];
							this.totalSaldoFavorCliente += (double) adel[1];
							for (Object[] item : items) {
								MyArray my = new MyArray(cobro.getTipoMovimiento()
										.getDescripcion(), item[0].toString().toUpperCase(), item[1],
										"SALDO A FAVOR DEL CLIENTE (COBRADO)",
										this.totalSaldoFavorCliente);
								this.values.add(my);
							}
						}
					}
				}
				
				// reembolso de cheques rechazados..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCancelacionCheque() && !cobro.isAnulado()
							&& !cobro.isCobroExterno()) {
						for (ReciboFormaPago rfp : cobro.getFormasPago()) {
							if (rfp.isEfectivo()) {
								this.totalCancelacionChequeEfectivo += rfp.getMontoGs();
								MyArray my = new MyArray(cobro.getTipoMovimiento()
										.getDescripcion(), rfp.getDescripcion().toUpperCase(),
										rfp.getMontoGs(),
										"REEMBOLSO CHEQUES RECHAZADOS CON EFECTIVO", this.totalCancelacionChequeEfectivo);
								this.values.add(my);
							}						
						}
					}
				}
				
				// reembolso de cheques rechazados..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCancelacionCheque() && !cobro.isAnulado()
							&& !cobro.isCobroExterno()) {
						for (ReciboFormaPago rfp : cobro.getFormasPago()) {
							if (rfp.isChequeTercero() && rfp.isChequeAlDia(fechaPlanilla)) {
								this.totalCancelacionChequeAldia += rfp.getMontoGs();
								MyArray my = new MyArray(cobro.getTipoMovimiento()
										.getDescripcion(), rfp.getDescripcion()
										.toUpperCase(), rfp.getMontoGs(),
										"REEMBOLSO CHEQUES RECHAZADOS CON CHEQUE AL DÍA", this.totalCancelacionChequeAldia);
								this.values.add(my);
							}						
						}
					}
				}
				
				// reembolso de cheques rechazados..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isCancelacionCheque() && !cobro.isAnulado()
							&& !cobro.isCobroExterno()) {
						for (ReciboFormaPago rfp : cobro.getFormasPago()) {
							if ((!(rfp.isChequeTercero() && rfp.isChequeAlDia(fechaPlanilla)))
									&& (!rfp.isEfectivo())) {
								this.totalCancelacionCheque += rfp.getMontoGs();
								MyArray my = new MyArray(cobro.getTipoMovimiento()
										.getDescripcion(), rfp.getDescripcion()
										.toUpperCase(), rfp.getMontoGs(),
										"REEMBOLSO DE CHEQUES RECHAZADOS", this.totalCancelacionCheque);
								this.values.add(my);
							}
						}
					}
				}
				
				// reembolso de prestamos..
				for (Recibo cobro : planilla.getRecibosOrdenado()) {
					if (cobro.isReembolsoPrestamo() && !cobro.isAnulado()
							&& !cobro.isCobroExterno()) {
						for (ReciboFormaPago rfp : cobro.getFormasPago()) {
							this.totalCancelacionCheque += rfp.getMontoGs();
							MyArray my = new MyArray(cobro.getTipoMovimiento()
									.getDescripcion(), rfp.getDescripcion().toUpperCase(),
									rfp.getMontoGs(),
									"REEMBOLSO DE PRESTAMOS",
									this.totalCancelacionCheque);
							this.values.add(my);
						}
					}
				}

				// reposiciones..
				for (CajaReposicion rep : planilla.getReposiciones()) {
					if (rep.isIngreso()) {
						double importe = rep.isAnulado() ? 0.0 : rep.getMontoGs();
						this.totalIngresos += importe;
						this.totalReposiciones += importe;
						MyArray my = new MyArray(
								"REPOSICION DE CAJA",
								(rep.getNumero() + " - " + rep.getFormaPago().getDescripcion().toUpperCase()),
								importe, "REPOSICIONES DE CAJA",
								this.totalReposiciones);
						this.values.add(my);
					}
				}

				// pagos..
				for (Recibo pago : planilla.getRecibosOrdenado()) {
					if (pago.isPago()) {
						double importe = pago.isAnulado() ? 0.0 : pago
								.getTotalImporteGs();
						this.totalEgresos += importe;
						this.totalPagos += importe;
						MyArray my = new MyArray(pago.getTipoMovimiento().getDescripcion(),
								pago.getNumero() + " - " + pago.getProveedor().getRazonSocial().toUpperCase(), importe,
								"PAGOS A PROVEEDORES", this.totalPagos);
						this.values.add(my);
					}
				}

				// gastos..
				for (Gasto gasto : planilla.getGastosOrdenado()) {
					double importe = gasto.isAnulado() ? 0.0 : gasto.getImporteGs();
					this.totalEgresos += importe;
					this.totalGastos += importe;
					MyArray my = new MyArray(gasto.getTipoMovimiento()
							.getDescripcion(), gasto.getNumeroFactura() + " - " + gasto.getDescripcionCuenta(), importe,
							"GASTOS DE CAJA CHICA", this.totalGastos);
					this.values.add(my);
				}

				// egresos..
				for (CajaReposicion rep : planilla.getReposiciones()) {
					if (!rep.isIngreso()) {
						double importe = rep.isAnulado() ? 0.0 : rep.getMontoGs();
						this.totalEgresos += rep.getMontoGs();
						this.totalRepEgresos += rep.getMontoGs();
						MyArray my = new MyArray(rep.getTipoEgreso()
								.getDescripcion(),
								(rep.getNumero() + " - " + rep.getResponsable()	+ " - " + rep.getObservacion()),
								importe, "EGRESOS VARIOS", this.totalRepEgresos);
						this.values.add(my);
					}
				}
				
				// egresos por vueltos (diferencia con cheque)..
				for (ReciboFormaPago fp : planilla.getVueltosPorVenta()) {
					double importe = (fp.getMontoGs() * -1);
					this.totalEgresos += (fp.getMontoGs() * -1);
					this.totalRepEgresos += (fp.getMontoGs() * -1);
					MyArray my = new MyArray("EGRESO EN EFECTIVO",
							fp.getDescripcion(), importe, "EGRESOS VARIOS",
							this.totalRepEgresos);
					this.values.add(my);
				}

				// notas de credito contra contado..
				for (NotaCredito nc : planilla.getNotasCredito()) {
					double importe = nc.isAnulado() ? 0.0 : nc.getImporteGs();
					this.totalEgresos += importe;
					if (nc.isNotaCreditoVentaContado()) {
						this.totalNotaCreditoContado += importe;
						MyArray my = new MyArray(nc.getTipoMovimiento()
								.getDescripcion(), nc.getNumero() + " - " 
							  + nc.getCliente().getRazonSocial().toUpperCase(), importe,
								"NOTAS DE CRÉDITO - (CONTADO)",
								this.totalNotaCreditoContado);
						this.values.add(my);
					}
				}

				// notas de credito contra credito..
				for (NotaCredito nc : planilla.getNotasCredito()) {
					double importe = nc.isAnulado() ? 0.0 : nc.getImporteGs();
					this.totalEgresos += importe;
					if (nc.isNotaCreditoVentaContado() == false) {
						this.totalNotaCreditoCredito += importe;
						MyArray my = new MyArray(nc.getTipoMovimiento()
								.getDescripcion(), nc.getNumero() + " - ("
								+ nc.getNumeroFacturaAplicada() + ")", importe,
								"NOTAS DE CRÉDITO - (CRÉDITO)",
								this.totalNotaCreditoCredito);
						this.values.add(my);
					}
				}

				// cheques de tercero al dia..
				// unifica los montos de un mismo cheque..
				Map<String, Double> montoCheque_ = new HashMap<String, Double>();
				for (ReciboFormaPago fp : planilla.getChequesTercero()) {
					if (fp.isChequeAlDia(fechaPlanilla)) {
						this.totalChequeTerceroAlDia += fp.getMontoChequeGs();

						if (montoCheque_.get(fp.getChequeNumero()) == null) {
							montoCheque_.put(fp.getChequeNumero(), fp.getMontoChequeGs());
						} else {
							montoCheque_.put(fp.getChequeNumero(), 
									(fp.getMontoChequeGs() + montoCheque_.get(fp.getChequeNumero())));
						}
					}
				}

				// cheques de tercero adelantados..
				// unifica los montos de un mismo cheque..
				Map<String, Double> montoCheque = new HashMap<String, Double>();
				for (ReciboFormaPago fp : planilla.getChequesTercero()) {
					if (fp.isChequeAlDia(fechaPlanilla) == false) {
						this.totalChequeTerceroAdelantado += fp.getMontoChequeGs();						
						
						if (montoCheque.get(fp.getChequeNumero()) == null) {
							montoCheque.put(fp.getChequeNumero(),
									fp.getMontoChequeGs());
						} else {
							montoCheque.put(fp.getChequeNumero(), (fp
									.getMontoChequeGs() + montoCheque.get(fp
									.getChequeNumero())));
						}
					}
				}

				// tarjetas de credito..
				for (ReciboFormaPago fp : planilla.getTarjetasCredito()) {
					this.totalTarjetaCredito += fp.getMontoGs();
					MyArray my = new MyArray(fp.getTipo().getDescripcion(),
							fp.getDescripcion(), fp.getMontoGs(),
							"TARJETAS DE CREDITO", this.totalTarjetaCredito);
					this.values.add(my);
				}

				// tarjetas de debito..
				for (ReciboFormaPago fp : planilla.getTarjetasDebito()) {
					this.totalTarjetaDebito += fp.getMontoGs();
					MyArray my = new MyArray(fp.getTipo().getDescripcion(),
							fp.getDescripcion(), fp.getMontoGs(),
							"TARJETAS DE DEBITO", this.totalTarjetaDebito);
					this.values.add(my);
				}

				// retenciones de cliente al dia..
				for (ReciboFormaPago fp : planilla.getRetencionesCliente(fechaPlanilla)) {
					this.totalRetencionCliente += fp.getMontoGs();
					MyArray my = new MyArray(fp.getTipo().getDescripcion(),
							fp.getDescripcion(), fp.getMontoGs(),
							"RETENCIONES DE CLIENTE - AL DÍA",
							this.totalRetencionCliente);
					this.values.add(my);
				}
				
				// retenciones de cliente diferidos..
				for (ReciboFormaPago fp : planilla.getRetencionesClienteDiferidos(fechaPlanilla)) {
					this.totalRetencionClienteDiferido += fp.getMontoGs();
					MyArray my = new MyArray(fp.getTipo().getDescripcion(),
							fp.getDescripcion(), fp.getMontoGs(),
							"RETENCIONES DE CLIENTE - DIFERIDOS",
							this.totalRetencionClienteDiferido);
					this.values.add(my);
				}

				// retenciones de proveedor..
				for (ReciboFormaPago fp : planilla.getRetencionesProveedor()) {
					this.totalRetencionProveedor += fp.getMontoGs();
					MyArray my = new MyArray(fp.getTipo().getDescripcion(),
							fp.getDescripcion(), fp.getMontoGs(),
							"RETENCIONES DE PROVEEDOR",
							this.totalRetencionProveedor);
					this.values.add(my);
				}
				
				// depositos bancarios..
				for (ReciboFormaPago fp : planilla.getDepositosBancarios()) {
					this.totalDepositoBancario += fp.getMontoGs();
					MyArray my = new MyArray(fp.getTipo().getDescripcion(),
							fp.getDescripcion(), fp.getMontoGs(),
							"DEPÓSITOS BANCARIOS", this.totalDepositoBancario);
					this.values.add(my);
				}
				
				// recaudacion central..
				for (ReciboFormaPago fp : planilla.getRecaudacionCentral()) {
					this.totalRecaudacionCentral += fp.getMontoGs();
					MyArray my = new MyArray(fp.getTipo().getDescripcion(),
							fp.getDescripcion(), fp.getMontoGs(),
							"RECAUDACION CASA CENTRAL", this.totalRecaudacionCentral);
					this.values.add(my);
				}
				
				// transferencia central..
				for (ReciboFormaPago fp : planilla.getTransferenciasCentral()) {
					this.totalTransferenciaCentral += fp.getMontoGs();
					MyArray my = new MyArray(fp.getTipo().getDescripcion(),
							fp.getDescripcion(), fp.getMontoGs(),
							"TRANSFERENCIA CASA CENTRAL",
							this.totalTransferenciaCentral);
					this.values.add(my);
				}

				// total efectivo..
				this.totalEfectivo = planilla.getTotalEfectivoIngreso()
						- planilla.getTotalEfectivoEgreso();

			}		

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		MyArray det = this.values.get(index);

		if ("Concepto".equals(fieldName)) {
			value = det.getPos1();
		} else if ("Descripcion".equals(fieldName)) {
			value = det.getPos2();
		} else if ("Importe".equals(fieldName)) {
			value = FORMATTER.format(det.getPos3());
		} else if ("TituloDetalle".equals(fieldName)) {
			value = det.getPos4();
		} else if ("ResumenIngresos".equals(fieldName)) {
			value = FORMATTER.format(this.totalIngresos);
		} else if ("ResumenEgresos".equals(fieldName)) {
			value = FORMATTER.format(this.totalEgresos);
		} else if ("TotalEfectivo".equals(fieldName)) {
			value = FORMATTER.format(this.totalEfectivo);
		} else if ("TotalChequeTercero".equals(fieldName)) {
			value = FORMATTER.format(this.totalChequeTerceroAlDia
					+ this.totalChequeTerceroAdelantado);
		} else if ("TotalTarjCredito".equals(fieldName)) {
			value = FORMATTER.format(this.totalTarjetaCredito);
		} else if ("TotalTarjDebito".equals(fieldName)) {
			value = FORMATTER.format(this.totalTarjetaDebito);
		} else if ("TotalImporte".equals(fieldName)) {
			value = FORMATTER.format(det.getPos5());
		} else if ("TotalVtaContado".equals(fieldName)) {
			value = FORMATTER.format(this.totalVentaContado);
		}else if ("TotalNotaCredCont".equals(fieldName)) {
			value = FORMATTER.format(this.totalNotaCreditoContado * -1);
		} else if ("TotalTarjDebito_".equals(fieldName)) {
			value = FORMATTER.format(this.totalTarjetaDebito * -1);
		} else if ("TotalTarjCredito_".equals(fieldName)) {
			value = FORMATTER.format(this.totalTarjetaCredito * -1);
		} else if ("TotalCobranzasEfe".equals(fieldName)) {
			value = FORMATTER.format(this.totalCobranzaEfectivo);
		} else if ("TotalCobranzasCheq".equals(fieldName)) {
			value = FORMATTER.format(this.totalCobranzaChequeAlDia);
		} else if ("TotalReembolsoEfe".equals(fieldName)) {
			value = FORMATTER.format(this.totalCancelacionChequeEfectivo);
		} else if ("TotalReembolsoCheq".equals(fieldName)) {
			value = FORMATTER.format(this.totalCancelacionChequeAldia);
		} else if ("TotalCobranzasOtraSuc".equals(fieldName)) {
			value = FORMATTER.format(this.totalCobranzaAlDiaOtraSuc);
		} else if ("TotalRetencionCli".equals(fieldName)) {
			value = FORMATTER.format(this.totalRetencionCliente * -1);
		} else if ("TotalResumen2".equals(fieldName)) {
			value = FORMATTER
					.format((this.totalVentaContado
							+ this.totalCobranzaAlDia
							+ this.totalCancelacionChequeEfectivo
							+ this.totalCancelacionChequeAldia
							+ this.totalCobranzaAlDiaOtraSuc
							+ this.totalReposiciones 
							+ this.totalRetencionProveedor)
							- (this.totalNotaCreditoContado
									+ this.totalTarjetaDebito
									+ this.totalRetencionCliente
									+ this.totalDepositoBancario
									+ this.totalTarjetaCredito
									+ this.totalRepEgresos 
									+ this.totalPagos));
		} else if ("TotalPagos".equals(fieldName)) {
			value = FORMATTER.format(this.totalPagos * -1);
		} else if ("TotalReposicion".equals(fieldName)) {
			value = FORMATTER.format(this.totalReposiciones);
		} else if ("TotalGastos".equals(fieldName)) {
			value = FORMATTER.format(this.totalGastos * -1);
		} else if ("TotalEgresos".equals(fieldName)) {
			value = FORMATTER.format(this.totalRepEgresos * -1);
		} else if ("TotalRetProv".equals(fieldName)) {
			value = FORMATTER.format(this.totalRetencionProveedor);
		} else if ("TotalDepBancarios".equals(fieldName)) {
			value = FORMATTER.format(this.totalDepositoBancario * -1);
		} else if ("SaldoCajaChica".equals(fieldName)) {
			value = FORMATTER
					.format((this.totalReposiciones + this.totalRetencionProveedor)
							- (this.totalPagos + this.totalGastos + this.totalRepEgresos));
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Conciliaciones Bancarias..
 */
class ConciliacionesBancariasDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<BancoExtracto> values = new ArrayList<BancoExtracto>();
	String sucursal;
	
	double totalGs_1 = 0;
	double totalGs_2 = 0;

	public ConciliacionesBancariasDataSource(List<BancoExtracto> values, String sucursal) {
		this.values = values;
		this.sucursal = sucursal.toUpperCase();
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		BancoExtracto det = this.values.get(index);
		
		List<BeanConciliacion> dets1 = new ArrayList<BeanConciliacion>();
		List<BeanConciliacion> dets2 = new ArrayList<BeanConciliacion>();
		
		List<BancoExtractoDetalle> items1 = new ArrayList<BancoExtractoDetalle>();
		List<BancoExtractoDetalle> items2 = new ArrayList<BancoExtractoDetalle>();
		
		items2.addAll(det.getDetalles2());
		
		Collections.sort(items1, new Comparator<BancoExtractoDetalle>() {
			@Override
			public int compare(BancoExtractoDetalle o1, BancoExtractoDetalle o2) {
				int compare = o1.getNumero().compareTo(o2.getNumero());				
				return compare;
			}
		});
		
		Collections.sort(items2, new Comparator<BancoExtractoDetalle>() {
			@Override
			public int compare(BancoExtractoDetalle o1, BancoExtractoDetalle o2) {
				int compare = o1.getNumero().compareTo(o2.getNumero());				
				return compare;
			}
		});
		
		for (BancoExtractoDetalle conc : items1) {
			dets1.add(new BeanConciliacion(conc.getNumero(), 
					conc.getDescripcion(), Utiles.getNumberFormat(conc.getDebe())));
			this.totalGs_1 += conc.getDebe();
		}		
		for (BancoExtractoDetalle conc : items2) {
			dets2.add(new BeanConciliacion(conc.getNumero(), 
					conc.getDescripcion(), Utiles.getNumberFormat(conc.getDebe())));
			this.totalGs_2 += conc.getDebe();
		}

		if ("TituloDetalle".equals(fieldName)) {
			String banco = det.getBanco().getBanco().getDescripcion().toUpperCase();
			value = "MOVIMIENTOS " + this.sucursal + "  |  BANCO " + banco;
		} else if ("Detalles1".equals(fieldName)) {
			value = dets1;
		} else if ("Detalles2".equals(fieldName)) {
			value = dets2;
		} else if ("TotalGs1".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalGs_1);
		} else if ("TotalGs2".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalGs_2);
		}

		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * Reporte codigo CON-00011
 */
class ReporteStockValorizadoAunaFecha extends ReporteYhaguy {
	
	private Date hasta;
	private String articulo;
	private String tipoCosto;
	private String familia;
	private String proveedor;
	private String deposito;
	
	public ReporteStockValorizadoAunaFecha(Date hasta, String articulo, String tipoCosto, String familia,
			String proveedor, String deposito) {
		this.hasta = hasta;
		this.articulo = articulo;
		this.tipoCosto = tipoCosto;
		this.familia = familia;
		this.proveedor = proveedor;
		this.deposito = deposito;
	}

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Stock", TIPO_LONG, 20, true);
	static DatosColumnas col4 = new DatosColumnas("Costo Gs.", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col5 = new DatosColumnas("Total Gs.", TIPO_DOUBLE_GS, 30, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("STOCK VALORIZADO (A UNA FECHA)");
		this.setDirectorio("Articulos");
		this.setNombreArchivo("stock-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Articulo", this.articulo))
				.add(this.textoParValor("Tipo Costo", this.tipoCosto))
				.add(this.textoParValor("A la fecha", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Familia", this.familia))
				.add(this.textoParValor("Proveedor", this.proveedor))
				.add(this.textoParValor("Depósito", this.deposito)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte codigo STK-00010
 */
class ReporteStockValorizadoAunaFechaResumido extends ReporteYhaguy {
	
	private Date hasta;
	private String articulo;
	private String tipoCosto;
	private String familia;
	private String proveedor;
	private String sucursal;
	
	public ReporteStockValorizadoAunaFechaResumido(Date hasta, String articulo, String tipoCosto, String familia, String proveedor, String sucursal) {
		this.hasta = hasta;
		this.articulo = articulo;
		this.tipoCosto = tipoCosto;
		this.familia = familia;
		this.proveedor = proveedor;
		this.sucursal = sucursal;
	}

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Familia", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Stock", TIPO_LONG, 20, true);
	static DatosColumnas col3 = new DatosColumnas("Costo Gs. S/iva", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col4 = new DatosColumnas("Total Gs. S/iva", TIPO_DOUBLE_GS, 30, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("STOCK VALORIZADO (A UNA FECHA) - RESUMIDO");
		this.setDirectorio("Articulos");
		this.setNombreArchivo("stock-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Articulo", this.articulo))
				.add(this.textoParValor("Tipo Costo", this.tipoCosto))
				.add(this.textoParValor("A la fecha", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Familia", this.familia))
				.add(this.textoParValor("Proveedor", this.proveedor))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte codigo CON-00029
 */
class ReporteChequesRechazados extends ReporteYhaguy {

	private Date desde;
	private Date hasta;
	private String cliente;
	private String reembolso;
	
	public ReporteChequesRechazados(Date desde, Date hasta, String cliente, String reembolso) {
		this.desde = desde;
		this.hasta = hasta;
		this.cliente = cliente;
		this.reembolso = reembolso;
	}

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Emisión", TIPO_STRING, 25);
	static DatosColumnas col2 = new DatosColumnas("Vto.", TIPO_STRING, 25);
	static DatosColumnas col3 = new DatosColumnas("Nro.", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Banco", TIPO_STRING, 40);
	static DatosColumnas col5 = new DatosColumnas("Motivo", TIPO_STRING, 70);
	static DatosColumnas col6 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col7 = new DatosColumnas("Reembolsado", TIPO_STRING, 35);
	static DatosColumnas col8 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE, 35, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);	
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Cheques Rechazados");
		this.setDirectorio("banco");
		this.setNombreArchivo("ChequesRechazados-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()				
				.add(this.textoParValor("Fecha Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Fecha Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()				
				.add(this.textoParValor("Cliente", this.cliente))
				.add(this.textoParValor("Reembolso", this.reembolso)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ventas preparador-repartidor VEN-00024..
 */
class ReportePreparadorRepartidor extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 25);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 35);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Preparador", TIPO_STRING, 35);
	static DatosColumnas col4 = new DatosColumnas("Repartidor", TIPO_STRING, 35);

	public ReportePreparadorRepartidor(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ventas Preparador / Repartidor");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ventas perdidas registradas VEN-00025..
 */
class ReporteVentasPerdidas extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 35);
	static DatosColumnas col1 = new DatosColumnas("Artículo", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Motivo", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Usuario", TIPO_STRING, 45);

	public ReporteVentasPerdidas(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ventas Perdidas Registradas");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Clientes con movimiento TES-00035..
 */
class ReporteClientesConMovimiento extends ReporteYhaguy {
	
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Vtas.Cont.Gs.", TIPO_DOUBLE_GS, 30, false);
	static DatosColumnas col3 = new DatosColumnas("Vtas.Créd.Gs.", TIPO_DOUBLE_GS, 30, false);
	static DatosColumnas col4 = new DatosColumnas("Saldo Gs.", TIPO_DOUBLE_GS, 30, false);
	static DatosColumnas col5 = new DatosColumnas("Cheq.Pend.Gs.", TIPO_DOUBLE_GS, 32, false);
	static DatosColumnas col6 = new DatosColumnas("Lím.Créd.Gs.", TIPO_DOUBLE_GS, 30, false);
	static DatosColumnas col7 = new DatosColumnas("Riesgo Gs.", TIPO_DOUBLE_GS, 30, false);
	static DatosColumnas col8 = new DatosColumnas("Créd.", TIPO_STRING, 15);

	public ReporteClientesConMovimiento(String sucursal) {
		this.sucursal = sucursal;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col7);
		cols.add(col6);
		cols.add(col8);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Clientes con Movimientos / Estado de Crédito");
		this.setDirectorio("Clientes");
		this.setNombreArchivo("CliMovims-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Historial de Bloqueos de Clientes TES-00036..
 */
class ReporteHistorialBloqueos extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fac Nro.", TIPO_STRING, 25);
	static DatosColumnas col1 = new DatosColumnas("Vto", TIPO_STRING, 25);
	static DatosColumnas col2 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Motivo", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Usuario", TIPO_STRING, 30);
	static DatosColumnas col5 = new DatosColumnas("Fecha", TIPO_STRING, 25);

	public ReporteHistorialBloqueos(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Historial de Bloqueos de Clientes");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Bloqueo-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * TES-00038 Egresos Ingresos por Diferencia Tipo Cambio
 */
class ReporteDiferenciaTipoCambio extends ReporteYhaguy {
	private Date desde;
	private Date hasta;
	String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Concepto", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 35, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	public ReporteDiferenciaTipoCambio(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Egresos / Ingresos por Diferencia Tipo Cambio");
		this.setDirectorio("recibos");
		this.setNombreArchivo("Cob-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * DataSource de Gastos por Cuenta Contable..
 */
class GastosPorCuentaContableDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");		
	static final String KEY_NC = "DESCUENTOS OBTENIDOS";
	
	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, Double> totales = new HashMap<String, Double>();
	Map<String, Double> totalesGravada10 = new HashMap<String, Double>();
	Map<String, Double> totalesIva10 = new HashMap<String, Double>();
	Map<String, Double> totalesGravada5 = new HashMap<String, Double>();
	Map<String, Double> totalesIva5 = new HashMap<String, Double>();
	Map<String, Double> totalesExenta = new HashMap<String, Double>();

	public GastosPorCuentaContableDataSource(List<Object[]> values, List<NotaCredito> notasCredito) {
		this.values = values;
		for (Object[] value : values) {
			GastoDetalle det = (GastoDetalle) value[0];
			Double total = totales.get(det.getArticuloGasto().getDescripcion());
			Double totalGravada10 = totalesGravada10.get(det.getArticuloGasto().getDescripcion());
			Double totalIva10 = totalesIva10.get(det.getArticuloGasto().getDescripcion());
			Double totalGravada5 = totalesGravada5.get(det.getArticuloGasto().getDescripcion());
			Double totalIva5 = totalesIva5.get(det.getArticuloGasto().getDescripcion());
			Double totalExenta = totalesExenta.get(det.getArticuloGasto().getDescripcion());
			if (total != null) {
				total += det.getMontoGs();
				totalGravada10 += det.getGravada10();
				totalIva10 += det.getIva10();
				totalGravada5 += det.getGravada5();
				totalIva5 += det.getIva5();
				totalExenta += det.getExenta();
				totales.put(det.getArticuloGasto().getDescripcion(), total);
				totalesGravada10.put(det.getArticuloGasto().getDescripcion(), totalGravada10);
				totalesIva10.put(det.getArticuloGasto().getDescripcion(), totalIva10);
				totalesGravada5.put(det.getArticuloGasto().getDescripcion(), totalGravada5);
				totalesIva5.put(det.getArticuloGasto().getDescripcion(), totalIva5);
				totalesExenta.put(det.getArticuloGasto().getDescripcion(), totalExenta);
			} else {
				totales.put(det.getArticuloGasto().getDescripcion(), det.getMontoGs());
				totalesGravada10.put(det.getArticuloGasto().getDescripcion(), det.getGravada10());
				totalesIva10.put(det.getArticuloGasto().getDescripcion(), det.getIva10());
				totalesGravada5.put(det.getArticuloGasto().getDescripcion(), det.getGravada5());
				totalesIva5.put(det.getArticuloGasto().getDescripcion(), det.getIva5());
				totalesExenta.put(det.getArticuloGasto().getDescripcion(), det.getExenta());
			}
		}
		for (NotaCredito nc : notasCredito) {
			if (nc.isNotaCreditoCompraAcreedor()) {
				String key = KEY_NC;
				double gravada10 = nc.getTotalGravado10() * -1;
				double gravada5 = 0.0;
				double exenta = nc.getTotalExenta() * -1;
				double iva10 = nc.getTotalIva10() * -1;
				double iva5 = 0.0;
				double importe = gravada10 + gravada5 + exenta + iva10 + iva5;
				Double total = totales.get(key);
				Double totalGravada10 = totalesGravada10.get(key);
				Double totalIva10 = totalesIva10.get(key);
				Double totalGravada5 = totalesGravada5.get(key);
				Double totalIva5 = totalesIva5.get(key);
				Double totalExenta = totalesExenta.get(key);
				if (total != null) {
					total += importe;
					totalGravada10 += gravada10;
					totalIva10 += iva10;
					totalGravada5 += gravada5;
					totalIva5 += iva5;
					totalExenta += exenta;
					totales.put(key, total);
					totalesGravada10.put(key, totalGravada10);
					totalesIva10.put(key, totalIva10);
					totalesGravada5.put(key, totalGravada5);
					totalesIva5.put(key, totalIva5);
					totalesExenta.put(key, totalExenta);
				} else {
					totales.put(key, importe);
					totalesGravada10.put(key, gravada10);
					totalesIva10.put(key, iva10);
					totalesGravada5.put(key, gravada5);
					totalesIva5.put(key, iva5);
					totalesExenta.put(key, exenta);
				}
				this.values.add(new Object[] { null, nc });
			}
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] obj = this.values.get(index);
		
		if (obj[0] != null) {
			GastoDetalle det = (GastoDetalle) obj[0];
			Gasto gasto = (Gasto) obj[1];			
			if ("TituloDetalle".equals(fieldName)) {
				value = det.getArticuloGasto().getDescripcion();
			} else if ("Emision".equals(fieldName)) {
				value = Utiles.getDateToString(gasto.getFecha(), Utiles.DD_MM_YY);
			} else if ("Concepto".equals(fieldName)) {
				value = gasto.getTipoMovimiento().getDescripcion();
			} else if ("Numero".equals(fieldName)) {
				value = gasto.getNumeroFactura();
			} else if ("RazonSocial".equals(fieldName)) {
				value = gasto.getProveedor().getRazonSocial();
			} else if ("Ruc".equals(fieldName)) {
				value = gasto.getProveedor().getRuc();
			} else if ("Iva10".equals(fieldName)) {
				value = Utiles.getNumberFormat(det.getIva10());
			} else if ("Gravada10".equals(fieldName)) {
				value = Utiles.getNumberFormat(det.getGravada10());
			} else if ("Iva5".equals(fieldName)) {
				value = Utiles.getNumberFormat(det.getIva5());
			} else if ("Gravada5".equals(fieldName)) {
				value = Utiles.getNumberFormat(det.getGravada5());
			} else if ("Exenta".equals(fieldName)) {
				value = Utiles.getNumberFormat(det.getExenta());
			} else if ("Importe".equals(fieldName)) {
				value = Utiles.getNumberFormat(det.getMontoGs());
			} else if ("TotalGravada10".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalesGravada10.get(det.getArticuloGasto().getDescripcion()));
			} else if ("TotalIva10".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalesIva10.get(det.getArticuloGasto().getDescripcion()));
			} else if ("TotalGravada5".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalesGravada5.get(det.getArticuloGasto().getDescripcion()));
			} else if ("TotalIva5".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalesIva5.get(det.getArticuloGasto().getDescripcion()));
			} else if ("TotalExenta".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalesExenta.get(det.getArticuloGasto().getDescripcion()));
			} else if ("TotalImporte".equals(fieldName)) {
				value = Utiles.getNumberFormat(totales.get(det.getArticuloGasto().getDescripcion()));
			}
		} else {
			NotaCredito nc = (NotaCredito) obj[1];	
			double gravada10 = nc.getTotalGravado10() * -1;
			double gravada5 = 0.0;
			double exenta = nc.getTotalExenta() * -1;
			double iva10 = nc.getTotalIva10() * -1;
			double iva5 = 0.0;
			double importe = gravada10 + gravada5 + exenta + iva10 + iva5;
			if ("TituloDetalle".equals(fieldName)) {
				value = KEY_NC;
			} else if ("Emision".equals(fieldName)) {
				value = Utiles.getDateToString(nc.getFechaEmision(), Utiles.DD_MM_YY);
			} else if ("Concepto".equals(fieldName)) {
				value = nc.getTipoMovimiento().getDescripcion();
			} else if ("Numero".equals(fieldName)) {
				value = nc.getNumero();
			} else if ("RazonSocial".equals(fieldName)) {
				value = nc.getProveedor().getRazonSocial();
			} else if ("Ruc".equals(fieldName)) {
				value = nc.getProveedor().getRuc();
			} else if ("Iva10".equals(fieldName)) {
				value = Utiles.getNumberFormat(iva10);
			} else if ("Gravada10".equals(fieldName)) {
				value = Utiles.getNumberFormat(gravada10);
			} else if ("Iva5".equals(fieldName)) {
				value = Utiles.getNumberFormat(iva5);
			} else if ("Gravada5".equals(fieldName)) {
				value = Utiles.getNumberFormat(gravada5);
			} else if ("Exenta".equals(fieldName)) {
				value = Utiles.getNumberFormat(exenta);
			} else if ("Importe".equals(fieldName)) {
				value = Utiles.getNumberFormat(importe);
			} else if ("TotalGravada10".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalesGravada10.get(KEY_NC));
			} else if ("TotalIva10".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalesIva10.get(KEY_NC));
			} else if ("TotalGravada5".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalesGravada5.get(KEY_NC));
			} else if ("TotalIva5".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalesIva5.get(KEY_NC));
			} else if ("TotalExenta".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalesExenta.get(KEY_NC));
			} else if ("TotalImporte".equals(fieldName)) {
				value = Utiles.getNumberFormat(totales.get(KEY_NC));
			}
		}		
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * REPORTE CON-00022
 */
class ReporteGastosPorCuentas extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Cód.", TIPO_STRING, 18);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Grav. 10%", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col4 = new DatosColumnas("Grav. 5%", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col5 = new DatosColumnas("Iva 10%", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col6 = new DatosColumnas("Iva 5%", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col7 = new DatosColumnas("Exenta", TIPO_DOUBLE_GS, 30, true);
	static DatosColumnas col8 = new DatosColumnas("Importe", TIPO_DOUBLE_GS, 30, true);

	public ReporteGastosPorCuentas(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Gastos por Cuentas Contables");
		this.setDirectorio("gastos");
		this.setNombreArchivo("Gasto-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * TES-00040 Recaudaciones no depositadas..
 */
class ReporteRecaudacionesNoDepositadas extends ReporteYhaguy {
	private Date desde;
	private Date hasta;
	String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Resumen", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Obs. efectivo no depositado", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Obs. cheque no depositado", TIPO_STRING);
	static DatosColumnas col6 = new DatosColumnas("Efectivo no dep.", TIPO_DOUBLE_GS, 40, true);
	static DatosColumnas col7 = new DatosColumnas("Cheque no dep.", TIPO_DOUBLE_GS, 40, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	public ReporteRecaudacionesNoDepositadas(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Recaudaciones No Depositadas");
		this.setDirectorio("recibos");
		this.setNombreArchivo("Rec-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de Clientes por Vendedor VEN-00027..
 */
class ReporteClientesVendedor extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private String vendedor;
	private String cliente;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Ruc", TIPO_STRING, 20);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Dirección", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Teléfono", TIPO_STRING, 30);

	public ReporteClientesVendedor(String vendedor, String cliente) {
		this.vendedor = vendedor;
		this.cliente = cliente;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Clientes por Vendedor");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Vendedor", this.vendedor))
				.add(this.textoParValor("Cliente", this.cliente)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte TES-00044..
 */
class SaldosCtaCteDesglosado implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, Object[]> data = new HashMap<String, Object[]>();
	Map<String, Double> saldo = new HashMap<String, Double>();
	double total = 0;
	
	public SaldosCtaCteDesglosado(List<Object[]> values, String periodo) {
		
		for (Object[] value : values) {
			long idEmpresa = (long) value[14];
			double saldo = (double) value[9];
			long idCartera = (long) value[15];
			Double acum = this.saldo.get(idCartera + "-" + idEmpresa);
			if (acum != null) {
				acum += saldo;
			} else {
				acum = saldo;
			}
			this.saldo.put(idCartera + "-" + idEmpresa, acum);
		}
		
		for (Object[] value : values) {
			long idEmpresa = (long) value[14];
			String razonSocial = (String) value[10];
			String cartera = (String) value[16];
			String vendedor = (String) value[19];
			boolean cuentaBloqueada = (boolean) value[18];
			String bloqueado = cuentaBloqueada ? "BLOQUEADA" : "HABILITADA";
			Date emision = (Date) value[6];
			double saldo = (double) value[9];
			double linea = (double) value[17];
			long idCartera = (long) value[15];
			int index = Utiles.getNumeroMes(emision);
			
			if (Utiles.getDateToString(emision, "yyyy").equals(periodo)) {
				Object[] acum = data.get(idCartera + "-" + idEmpresa);
				if (acum != null) {
					acum[index] = ((double) acum[index]) + saldo;
				} else {
					acum = new Object[] { razonSocial, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, this.saldo.get(idCartera + "-" + idEmpresa), cartera, linea, vendedor, bloqueado };
					acum[index] = saldo;
				} 
				data.put(idCartera + "-" + idEmpresa, acum);
			} else {
				Object[] acum = data.get(idCartera + "-" + idEmpresa);
				if (acum == null) {
					acum = new Object[] { razonSocial, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, this.saldo.get(idCartera + "-" + idEmpresa), cartera, linea, vendedor, bloqueado };
				}
				data.put(idCartera + "-" + idEmpresa, acum);			
			}			
		}
		
		for (String key : data.keySet()) {
			Object[] value = data.get(key);
			this.values.add(value);
		}
		
		// ordena la lista segun fecha..
		Collections.sort(this.values, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String rs1 = (String) o1[0];
				String rs2 = (String) o2[0];
				return rs1.compareTo(rs2);
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);
		double total = (double) det[1] + (double) det[2] + (double) det[3] + (double) det[4] +
				(double) det[5] + (double) det[6] + (double) det[7] + (double) det[8] +
				(double) det[9] + (double) det[10] + (double) det[11] + (double) det[12];
		this.total += total;

		if ("Cliente".equals(fieldName)) {
			value = det[0];
		} else if ("cartera".equals(fieldName)) {
			value = det[14];
		} else if ("enero".equals(fieldName)) {
			value = FORMATTER.format(det[1]);
		} else if ("febrero".equals(fieldName)) {
			value = FORMATTER.format(det[2]);
		} else if ("marzo".equals(fieldName)) {
			value = FORMATTER.format(det[3]);
		} else if ("abril".equals(fieldName)) {
			value = FORMATTER.format(det[4]);
		} else if ("mayo".equals(fieldName)) {
			value = FORMATTER.format(det[5]);
		} else if ("junio".equals(fieldName)) {
			value = FORMATTER.format(det[6]);
		} else if ("julio".equals(fieldName)) {
			value = FORMATTER.format(det[7]);
		} else if ("agosto".equals(fieldName)) {
			value = FORMATTER.format(det[8]);
		} else if ("setiembre".equals(fieldName)) {
			value = FORMATTER.format(det[9]);
		} else if ("octubre".equals(fieldName)) {
			value = FORMATTER.format(det[10]);
		} else if ("noviembre".equals(fieldName)) {
			value = FORMATTER.format(det[11]);
		} else if ("diciembre".equals(fieldName)) {
			value = FORMATTER.format(det[12]);
		} else if ("total".equals(fieldName)) {
			value = FORMATTER.format(total);
		} else if ("total_".equals(fieldName)) {
			value = FORMATTER.format(this.total);
		} else if ("totalsaldo".equals(fieldName)) {
			value = FORMATTER.format(det[13]);
		} else if ("lineaCredito".equals(fieldName)) {
			value = Utiles.getNumberFormat((double) det[15]);
		} else if ("Vendedor".equals(fieldName)) {
			value = det[16];
		} else if ("Cuenta".equals(fieldName)) {
			value = det[17];
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * Reporte de Historial de atrasos de clientes..
 */
class ReporteHistorialAtrasos extends ReporteYhaguy {
	
	private String cliente;
	private Date desde;
	private Date hasta;
	private String limiteCredito;
	private String chequesPendientes;
	private String saldoActual;
	private long max;
	private long min;
	private long promedio;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Vencimiento", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Recibo", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Fecha Recibo", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Días Atraso", TIPO_LONG, 40);

	public ReporteHistorialAtrasos(Date desde, Date hasta, String cliente, String limiteCredito,
			String chequesPendientes, String saldoActual, long max, long min, long promedio) {
		this.desde = desde;
		this.hasta = hasta;
		this.cliente = cliente;
		this.limiteCredito = limiteCredito;
		this.chequesPendientes = chequesPendientes;
		this.saldoActual = saldoActual;
		this.max = max;
		this.min = min;
		this.promedio = promedio;
	}

	static {
		col5.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Historial de atrasos por cliente");
		this.setDirectorio("Clientes");
		this.setNombreArchivo("atrasos-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Cliente", this.cliente))
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Límite Crédito Gs.", this.limiteCredito))
				.add(this.textoParValor("Cheques Pendientes Gs.", this.chequesPendientes))
				.add(this.textoParValor("Saldo actual Gs.", this.saldoActual)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Máximo días atraso", this.max))
				.add(this.textoParValor("Mínimo días atraso", this.min))
				.add(this.textoParValor("Promedio días atraso", this.promedio)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte codigo CON-00030
 */
class ReportePrestamosCasaCentral extends ReporteYhaguy {

	private Date desde;
	private Date hasta;
	private String titulo;
	
	public ReportePrestamosCasaCentral(Date desde, Date hasta, String titulo) {
		this.desde = desde;
		this.hasta = hasta;
		this.titulo = titulo;
	}

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col2 = new DatosColumnas("Observación", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 40, true);

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo(this.titulo);
		this.setDirectorio("banco");
		this.setNombreArchivo("PrestamosCC-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Fecha Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Fecha Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}


/**
 * Reporte VEN-00030..
 */
class VentasDesglosado implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, Object[]> data = new HashMap<String, Object[]>();
	Map<String, Long> cantidadVendida = new HashMap<String, Long>();
	
	/**
	 * - ventas:
	 * [1]:fecha 
	 * [2]:numero 
	 * [3]:cantidad 
	 * [4]:precio 
	 * [5]:cliente 
	 * [6]:deposito
	 * [7]:articulo.codigoInterno
	 * [8]:articulo.descripcion
	 * [9]:articulo.marca
	 * [10]:articulo.proveedor
	 * [11]:articulo.id
	 */
	public VentasDesglosado(List<Object[]> ventas, String periodo) {
		
		for (Object[] venta : ventas) {
			String codigo = (String) venta[7];
			long cantidad = (long) venta[3];
			Long acum = this.cantidadVendida.get(codigo);
			if (acum != null) {
				acum += cantidad;
			} else {
				acum = cantidad;
			}
			this.cantidadVendida.put(codigo, acum);
		}
		
		for (Object[] venta : ventas) {
			String codigo = (String) venta[7];
			String desc = (String) venta[8];
			String marca = (String) venta[9];
			String prov = (String) venta[10];
			long cantidad = (long) venta[3];
			long idArticulo = (long) venta[11];
			Date fecha = (Date) venta[1];
			int index = Utiles.getNumeroMes(fecha);
			
			if (Utiles.getDateToString(fecha, "yyyy").equals(periodo)) {
				Object[] acum = data.get(codigo);
				if (acum != null) {
					acum[index] = ((long) acum[index]) + cantidad;
				} else {
					acum = new Object[] { codigo, (long) 0, (long) 0, (long) 0, (long) 0, (long) 0, (long) 0, 
							(long) 0, (long) 0, (long) 0, (long) 0, (long) 0, (long) 0, this.cantidadVendida.get(codigo), desc, marca, prov, idArticulo, 0 };
					acum[index] = cantidad;
				} 
				data.put(codigo, acum);
			}		
			
		}
		
		for (String key : data.keySet()) {
			Object[] value = data.get(key);
			this.values.add(value);
		}
		
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			for (Object[] value : this.values) {
				long idarticulo = (long) value[17];
				value[18] = rr.getStock(idarticulo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		// ordena la lista segun codigo..
		Collections.sort(this.values, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String cod1 = (String) o1[0];
				String cod2 = (String) o2[0];
				return cod1.compareTo(cod2);
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);

		if ("codigo".equals(fieldName)) {
			value = det[0];
		} else if ("enero".equals(fieldName)) {
			value = FORMATTER.format(det[1]);
		} else if ("febrero".equals(fieldName)) {
			value = FORMATTER.format(det[2]);
		} else if ("marzo".equals(fieldName)) {
			value = FORMATTER.format(det[3]);
		} else if ("abril".equals(fieldName)) {
			value = FORMATTER.format(det[4]);
		} else if ("mayo".equals(fieldName)) {
			value = FORMATTER.format(det[5]);
		} else if ("junio".equals(fieldName)) {
			value = FORMATTER.format(det[6]);
		} else if ("julio".equals(fieldName)) {
			value = FORMATTER.format(det[7]);
		} else if ("agosto".equals(fieldName)) {
			value = FORMATTER.format(det[8]);
		} else if ("setiembre".equals(fieldName)) {
			value = FORMATTER.format(det[9]);
		} else if ("octubre".equals(fieldName)) {
			value = FORMATTER.format(det[10]);
		} else if ("noviembre".equals(fieldName)) {
			value = FORMATTER.format(det[11]);
		} else if ("diciembre".equals(fieldName)) {
			value = FORMATTER.format(det[12]);
		} else if ("totalsaldo".equals(fieldName)) {
			value = FORMATTER.format(det[13]);
		} else if ("descripcion".equals(fieldName)) {
			value = det[14];
		} else if ("marca".equals(fieldName)) {
			value = det[15].toString().toUpperCase();
		} else if ("proveedor".equals(fieldName)) {
			value = det[16] == null ? "SIN PROVEEDOR" : det[16];
		} else if ("stock".equals(fieldName)) {
			value = FORMATTER.format(det[18]);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * Reporte de Ventas Promo 1 VEN-00032..
 */
class ReporteVentasPromo1 extends ReporteYhaguy {
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Nombre y Apellido", TIPO_STRING);	
	static DatosColumnas col2 = new DatosColumnas("Direccion", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Correo", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Teléfono", TIPO_STRING, 50);
	static DatosColumnas col5 = new DatosColumnas("Fecha Nac.", TIPO_STRING, 30);
	

	public ReporteVentasPromo1() {
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Registro de clientes / Promocion 2000 Gs. por bateria");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Promo1-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ventas Por Familia VEN-00033..
 */
class ReporteVentasPorFamilia extends ReporteYhaguy {
	
	Date desde;
	Date hasta;
	String sucursal = "";
	String cliente = "";
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Familia", TIPO_STRING);	
	static DatosColumnas col2 = new DatosColumnas("Importe S/iva", TIPO_DOUBLE, true);
	static DatosColumnas col3 = new DatosColumnas("Costo", TIPO_DOUBLE, true);
	static DatosColumnas col4 = new DatosColumnas("Utilidad", TIPO_DOUBLE, true);
	static DatosColumnas col5 = new DatosColumnas("% S/Venta", TIPO_DOUBLE_DS);
	static DatosColumnas col6 = new DatosColumnas("% S/Costo", TIPO_DOUBLE_DS);
	static DatosColumnas col7 = new DatosColumnas("Cant.", TIPO_LONG, 35);
	static DatosColumnas col8 = new DatosColumnas("Litros", TIPO_DOUBLE_DS, 50);

	public ReporteVentasPorFamilia(Date desde, Date hasta, String sucursal, String cliente) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.cliente = cliente;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ventas por Familia");
		this.setDirectorio("ventas");
		this.setNombreArchivo("VtaFlia-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal))
				.add(this.textoParValor("Cliente", this.cliente)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Ventas - utilidad resumido VEN-00034..
 */
class ReporteVentasUtilidadResumido extends ReporteYhaguy {
	
	Date desde;
	Date hasta;
	String sucursal = "";
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Ventas Netas", TIPO_DOUBLE);	
	static DatosColumnas col2 = new DatosColumnas("Costo", TIPO_DOUBLE);
	static DatosColumnas col3 = new DatosColumnas("Utilidad Bruta", TIPO_DOUBLE);
	static DatosColumnas col4 = new DatosColumnas("Margen s/Ventas", TIPO_DOUBLE);
	static DatosColumnas col5 = new DatosColumnas("Margen s/Costo", TIPO_DOUBLE);
	

	public ReporteVentasUtilidadResumido(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ventas y utilidad resumido");
		this.setDirectorio("ventas");
		this.setNombreArchivo("VtaUtilidad-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}


/**
 * DataSource de Movimientos de articulos..
 */
class MovimientoArticulos implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<HistoricoMovimientoArticulo> values = new ArrayList<HistoricoMovimientoArticulo>();
	Map<String, HistoricoMovimientoArticulo> map = new HashMap<String, HistoricoMovimientoArticulo>();
	
	public MovimientoArticulos(List<HistoricoMovimientoArticulo> values) {
		for (HistoricoMovimientoArticulo hist : values) {
			map.put(hist.getCodigo(), hist);
		}
		for (String key : map.keySet()) {
			this.values.add(map.get(key));
		}
		Collections.sort(this.values, new Comparator<HistoricoMovimientoArticulo>() {
			@Override
			public int compare(HistoricoMovimientoArticulo o1, HistoricoMovimientoArticulo o2) {
				int compare = o1.getCodigo().compareTo(o2.getCodigo());				
				return compare;
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		HistoricoMovimientoArticulo det = this.values.get(index);
		String cod = det.getCodigo();

		if ("Codigo".equals(fieldName)) {
			value = cod;
		} else if ("CodigoProveedor".equals(fieldName)) {
			value = det.getCodigoProveedor();
		}  else if ("Referencia".equals(fieldName)) {
			value = det.getReferencia();
		} else if ("NroParte".equals(fieldName)) {
			value = det.getCodigoOriginal();
		} else if ("Estado".equals(fieldName)) {
			value = det.getEstado();
		} else if ("Descripcion".equals(fieldName)) {
			value = det.getDescripcion();
		} else if ("OchentaVeinte".equals(fieldName)) {
			value = det.getOchentaVeinte();
		} else if ("Abc".equals(fieldName)) {
			value = det.getAbc();
		} else if ("Familia".equals(fieldName)) {
			value = det.getFamilia();
		} else if ("Marca".equals(fieldName)) {
			value = det.getMarca();
		} else if ("Linea".equals(fieldName)) {
			value = det.getLinea();
		} else if ("Grupo".equals(fieldName)) {
			value = det.getGrupo();
		} else if ("Aplicacion".equals(fieldName)) {
			value = det.getAplicacion();
		} else if ("Modelo".equals(fieldName)) {
			value = det.getModelo();
		} else if ("Peso".equals(fieldName)) {
			value = det.getPeso();
		} else if ("Volumen".equals(fieldName)) {
			value = det.getVolumen();
		} else if ("SubGrupo".equals(fieldName)) {
			value = det.getSubGrupo();
		} else if ("Parte".equals(fieldName)) {
			value = det.getParte();
		} else if ("SubMarca".equals(fieldName)) {
			value = det.getSubMarca();
		} else if ("UnidadesCaja".equals(fieldName)) {
			value = det.getUnidadesCaja() + "";
		} else if ("Procedencia".equals(fieldName)) {
			value = det.getProcedencia();
		} else if ("Proveedor".equals(fieldName)) {
			value = det.getProveedor();
		} else if ("CantLocal".equals(fieldName)) {
			value = det.getCantidad() + "";
		} else if ("FechaLocal".equals(fieldName)) {
			value = det.getFechaUltimaCompra() + "";
		} else if ("ProvLocal".equals(fieldName)) {
			value = det.getProveedorUltimaCompra();			
		} else if ("Maximo".equals(fieldName)) {
			value = det.getMaximo() + "";
		} else if ("Minimo".equals(fieldName)) {
			value = det.getMinimo() + "";
		} else if ("FobGs".equals(fieldName)) {
			value = Utiles.getRedondeo(det.getCostoFobGs()) + "";
		} else if ("FobDs".equals(fieldName)) {
			value = det.getCostoFobDs() + "";
		} else if ("Dep_1".equals(fieldName)) {
			value = det.getStock1() + "";			
		} else if ("Dep_2".equals(fieldName)) {
			value = det.getStock2() + "";			
		} else if ("Dep_3".equals(fieldName)) {
			value = det.getStock3() + "";			
		} else if ("Dep_4".equals(fieldName)) {
			value = det.getStock4() + "";		
		} else if ("Dep_5".equals(fieldName)) {
			value = det.getStock5() + "";			
		} else if ("Dep_6".equals(fieldName)) {
			value = det.getStock6() + "";			
		} else if ("Dep_7".equals(fieldName)) {
			value = det.getStock7() + "";			
		} else if ("Dep_8".equals(fieldName)) {
			value = det.getStock8() + "";			
		} else if ("Dep_9".equals(fieldName)) {
			value = det.getStock9() + "";			
		} else if ("Dep_10".equals(fieldName)) {
			value = det.getStock10() + "";			
		} else if ("Dep_gral".equals(fieldName)) {
			value = det.getTotal() + "";			
		} else if ("Enero".equals(fieldName)) {
			value = det.getEnero() + "";
		} else if ("Febrero".equals(fieldName)) {
			value = det.getFebrero() + "";
		} else if ("Marzo".equals(fieldName)) {
			value = det.getMarzo() + "";
		} else if ("Abril".equals(fieldName)) {
			value = det.getAbril() + "";
		} else if ("Mayo".equals(fieldName)) {
			value = det.getMayo() + "";
		} else if ("Junio".equals(fieldName)) {
			value = det.getJunio() + "";
		} else if ("Julio".equals(fieldName)) {
			value = det.getJulio() + "";
		} else if ("Agosto".equals(fieldName)) {
			value = det.getAgosto() + "";
		} else if ("Setiembre".equals(fieldName)) {
			value = det.getSetiembre() + "";
		} else if ("Octubre".equals(fieldName)) {
			value = det.getOctubre() + "";
		} else if ("Noviembre".equals(fieldName)) {
			value = det.getNoviembre() + "";
		} else if ("Diciembre".equals(fieldName)) {
			value = det.getDiciembre() + "";
		} else if ("CantCliente".equals(fieldName)) {
			value = det.getCantCliente() + "";
		} else if ("CantClienteVig".equals(fieldName)) {
			value = det.getCantClienteVigente() + "";
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Movimientos de articulos..
 */
class LitrajeArticulos implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<HistoricoMovimientoArticulo> values = new ArrayList<HistoricoMovimientoArticulo>();
	
	double totalEnero = 0;
	double totalFebrero = 0;
	double totalMarzo = 0;
	double totalAbril = 0;
	double totalMayo = 0;
	double totalJunio = 0;
	double totalJulio = 0;
	double totalAgosto = 0;
	double totalSetiembre = 0;
	double totalOctubre = 0;
	double totalNoviembre = 0;
	double totalDiciembre = 0;
	
	double totalEnero_ = 0;
	double totalFebrero_ = 0;
	double totalMarzo_ = 0;
	double totalAbril_ = 0;
	double totalMayo_ = 0;
	double totalJunio_ = 0;
	double totalJulio_ = 0;
	double totalAgosto_ = 0;
	double totalSetiembre_ = 0;
	double totalOctubre_ = 0;
	double totalNoviembre_ = 0;
	double totalDiciembre_ = 0;
	
	public LitrajeArticulos(List<HistoricoMovimientoArticulo> values) {
		this.values = values;
		for (HistoricoMovimientoArticulo item : values) {
			totalEnero += item.getEnero_();
			totalEnero_ += item.get_enero();
			totalFebrero += item.getFebrero_();
			totalFebrero_ += item.get_febrero();
			totalMarzo += item.getMarzo_();
			totalMarzo_ += item.get_marzo();
			totalAbril += item.getAbril_();
			totalAbril_ += item.get_abril();
			totalMayo += item.getMayo_();
			totalMayo_ += item.get_mayo();
			totalJunio += item.getJunio_();
			totalJunio_ += item.get_junio();
			totalJulio += item.getJulio_();
			totalJulio_ += item.get_julio();
			totalAgosto += item.getAgosto_();
			totalAgosto_ += item.get_agosto();
			totalSetiembre += item.getSetiembre_();
			totalSetiembre_ += item.get_setiembre();
			totalOctubre += item.getOctubre_();
			totalOctubre_ += item.get_octubre();
			totalNoviembre += item.getNoviembre_();
			totalNoviembre_ += item.get_noviembre();
			totalDiciembre += item.getDiciembre_();
			totalDiciembre_ += item.get_diciembre();
		}
		Collections.sort(this.values, new Comparator<HistoricoMovimientoArticulo>() {
			@Override
			public int compare(HistoricoMovimientoArticulo o1, HistoricoMovimientoArticulo o2) {
				String val1 = o1.getReferencia();
				String val2 = o2.getReferencia();
				int compare = val1.compareTo(val2);				
				return compare;
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		HistoricoMovimientoArticulo det = this.values.get(index);
		String cod = det.getCodigo();

		if ("Codigo".equals(fieldName)) {
			value = cod;
		} else if ("CodigoProveedor".equals(fieldName)) {
			value = det.getCodigoProveedor();
		}  else if ("Referencia".equals(fieldName)) {
			value = det.getReferencia();
		} else if ("NroParte".equals(fieldName)) {
			value = det.getCodigoOriginal();
		} else if ("Estado".equals(fieldName)) {
			value = det.getEstado();
		} else if ("Descripcion".equals(fieldName)) {
			value = det.getDescripcion();
		} else if ("OchentaVeinte".equals(fieldName)) {
			value = det.getOchentaVeinte();
		} else if ("Abc".equals(fieldName)) {
			value = det.getAbc();
		} else if ("Familia".equals(fieldName)) {
			value = det.getFamilia();
		} else if ("Marca".equals(fieldName)) {
			value = det.getMarca();
		} else if ("Linea".equals(fieldName)) {
			value = det.getLinea();
		} else if ("Grupo".equals(fieldName)) {
			value = det.getGrupo();
		} else if ("Aplicacion".equals(fieldName)) {
			value = det.getAplicacion();
		} else if ("Modelo".equals(fieldName)) {
			value = det.getModelo();
		} else if ("Peso".equals(fieldName)) {
			value = det.getPeso();
		} else if ("Volumen".equals(fieldName)) {
			value = det.getVolumen();
		} else if ("Proveedor".equals(fieldName)) {
			value = det.getProveedor();
		} else if ("CantLocal".equals(fieldName)) {
			value = det.getCantidad() + "";
		} else if ("FechaLocal".equals(fieldName)) {
			value = det.getFechaUltimaCompra() + "";
		} else if ("ProvLocal".equals(fieldName)) {
			value = det.getProveedorUltimaCompra();			
		} else if ("Dep_1".equals(fieldName)) {
			value = det.getStock1() + "";			
		} else if ("Dep_2".equals(fieldName)) {
			value = det.getStock2() + "";			
		} else if ("Dep_3".equals(fieldName)) {
			value = det.getStock3() + "";			
		} else if ("Dep_4".equals(fieldName)) {
			value = det.getStock4() + "";		
		} else if ("Dep_5".equals(fieldName)) {
			value = det.getStock5() + "";			
		} else if ("Dep_6".equals(fieldName)) {
			value = det.getStock6() + "";			
		} else if ("Dep_7".equals(fieldName)) {
			value = det.getStock7() + "";			
		} else if ("Dep_8".equals(fieldName)) {
			value = det.getStock8() + "";			
		} else if ("Dep_gral".equals(fieldName)) {
			value = det.getTotal() + "";			
		} else if ("Enero".equals(fieldName)) {
			value = det.getEnero_();
		} else if ("Febrero".equals(fieldName)) {
			value = det.getFebrero_();
		} else if ("Marzo".equals(fieldName)) {
			value = det.getMarzo_();
		} else if ("Abril".equals(fieldName)) {
			value = det.getAbril_();
		} else if ("Mayo".equals(fieldName)) {
			value = det.getMayo_();
		} else if ("Junio".equals(fieldName)) {
			value = det.getJunio_();
		} else if ("Julio".equals(fieldName)) {
			value = det.getJulio_();
		} else if ("Agosto".equals(fieldName)) {
			value = det.getAgosto_();
		} else if ("Setiembre".equals(fieldName)) {
			value = det.getSetiembre_();
		} else if ("Octubre".equals(fieldName)) {
			value = det.getOctubre_();
		} else if ("Noviembre".equals(fieldName)) {
			value = det.getNoviembre_();
		} else if ("Diciembre".equals(fieldName)) {
			value = det.getDiciembre_();
		}  else if ("_Enero".equals(fieldName)) {
			value =  Utiles.getRedondeo(det.get_enero());
		} else if ("_Febrero".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_febrero());
		} else if ("_Marzo".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_marzo());
		} else if ("_Abril".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_abril());
		} else if ("_Mayo".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_mayo());
		} else if ("_Junio".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_junio());
		} else if ("_Julio".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_julio());
		} else if ("_Agosto".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_agosto());
		} else if ("_Setiembre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_setiembre());
		} else if ("_Octubre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_octubre());
		} else if ("_Noviembre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_noviembre());
		} else if ("_Diciembre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_diciembre());
		} else if ("tot_oct".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalOctubre);
		} else if ("tot_oct_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalOctubre_);
		} else if ("tot_nov".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalNoviembre);
		} else if ("tot_nov_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalNoviembre_);
		} else if ("tot_dic".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalDiciembre);
		} else if ("tot_dic_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalDiciembre_);
		} else if ("tot_ene".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalEnero);
		} else if ("tot_ene_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalEnero_);
		} else if ("tot_feb".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalFebrero);
		} else if ("tot_feb_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalFebrero_);
		} else if ("tot_mar".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalMarzo);
		} else if ("tot_mar_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalMarzo_);
		} else if ("tot_abr".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalAbril);
		} else if ("tot_abr_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalAbril_);
		} else if ("tot_may".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalMayo);
		} else if ("tot_may_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalMayo_);
		} else if ("tot_jun".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalJunio);
		} else if ("tot_jun_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalJunio_);
		} else if ("tot_jul".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalJulio);
		} else if ("tot_jul_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalJulio_);
		} else if ("tot_ago".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalAgosto);
		} else if ("tot_ago_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalAgosto_);
		} else if ("tot_set".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalSetiembre);
		} else if ("tot_set_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalSetiembre_);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 *	CON-00038
 */
class VentaGenericoCosto extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private double totalSinIva;
	private double totalCostoSinIva;
	private Date desde;
	private Date hasta;
	private String vendedor;
	private String cliente;
	private String proveedor;
	private String sucursal;
	private String familias;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Ruc", TIPO_STRING, 35);
	static DatosColumnas col5 = new DatosColumnas("Tipo", TIPO_STRING, 35);
	static DatosColumnas col6 = new DatosColumnas("Importe", TIPO_DOUBLE, 35, true);
	static DatosColumnas col7 = new DatosColumnas("Costo", TIPO_DOUBLE, 35, true);

	public VentaGenericoCosto(double totalSinIva, Date desde, Date hasta,
			String vendedor, String cliente, String sucursal, String proveedor, String familias, double totalCostoSinIva) {
		this.totalSinIva = totalSinIva;
		this.desde = desde;
		this.hasta = hasta;
		this.vendedor = vendedor;
		this.cliente = cliente;
		this.sucursal = sucursal;
		this.proveedor = proveedor;
		this.familias = familias;
		this.totalCostoSinIva = totalCostoSinIva;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Ventas / Notas de Credito");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Total Sin IVA", FORMATTER.format(this.totalSinIva)))
				.add(this.textoParValor("Vendedor", this.vendedor)).add(this.textoParValor("Cliente", this.cliente)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Total Costo S/IVA", FORMATTER.format(this.totalCostoSinIva)))
				.add(this.textoParValor("Proveedor", this.proveedor))
				.add(this.textoParValor("Familia", this.familias)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Pedidos Pendientes VEN-00041..
 */
class ReportePromoValvoline extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Vendedor", TIPO_STRING, 60);
	static DatosColumnas col2 = new DatosColumnas("Codigo", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Cantidad", TIPO_LONG, 25, true);
	static DatosColumnas col3_ = new DatosColumnas("Importe Iva inc.", TIPO_DOUBLE, 35, true);
	static DatosColumnas col4 = new DatosColumnas("Dueño", TIPO_LONG, 25, true);
	static DatosColumnas col5 = new DatosColumnas("Empleado", TIPO_LONG, 25, true);

	public ReportePromoValvoline(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col3_);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("PROMO COMPRA VALVOLINE");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * VEN-00042..
 */
class ReporteListaPrecioPorDeposito extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private String proveedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 35);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Min.", TIPO_LONG, 15);
	static DatosColumnas col4 = new DatosColumnas("May.", TIPO_LONG, 15);
	static DatosColumnas col5 = new DatosColumnas("Mcl.", TIPO_LONG, 15);
	static DatosColumnas col6 = new DatosColumnas("May.Gs.", TIPO_DOUBLE_GS, 20);
	static DatosColumnas col7 = new DatosColumnas("Min.Gs.", TIPO_DOUBLE_GS, 20);
	static DatosColumnas col8 = new DatosColumnas("Lis.Gs.", TIPO_DOUBLE_GS, 20);

	public ReporteListaPrecioPorDeposito(String proveedor) {
		this.proveedor = proveedor;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Lista de precios por depósito");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Precios-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Proveedor", this.proveedor)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * VEN-00046..
 */
class ReporteListaPrecioPorDeposito_ extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private String proveedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 35);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Min.", TIPO_LONG, 13);
	static DatosColumnas col4 = new DatosColumnas("May.", TIPO_LONG, 13);
	static DatosColumnas col5 = new DatosColumnas("May.Cen.", TIPO_LONG, 15);
	static DatosColumnas col6 = new DatosColumnas("May.Gs.", TIPO_DOUBLE_GS, 20);

	public ReporteListaPrecioPorDeposito_(String proveedor) {
		this.proveedor = proveedor;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("ventas");
		this.setNombreArchivo("Precios-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Proveedor", this.proveedor)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * DataSource de Saldos de Clientes detallado DHS..
 */
class CtaCteSaldosResumidoDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	double totalVentas = 0.0;
	double totalChequesRechazados = 0.0;
	double totalNotasCredito = 0.0;
	double totalRecibos = 0.0;
	double totalNotasDebito = 0.0;
	double totalChequesReembolsos = 0.0;
	double totalMigracion = 0.0;
	double totalMigracionChequesRechazados = 0.0;
	double totalPrestamos = 0.0;
	double totalAnticipos = 0.0;
	
	/**
	 * [0]:cliente
	 * [1]:importe ventas
	 */
	public CtaCteSaldosResumidoDataSource(List<Object[]> values, String cliente, double totalVentas,
			double totalChequesRechazados, double totalNotasCredito, double totalRecibos, double totalNotasDebito,
			double totalChequesReembolsos, double totalMigracion, double totalMigracionChequesRechazados, 
			double totalPrestamos, double totalAnticipos) {
		this.values = values;
		this.totalVentas = totalVentas;
		this.totalChequesRechazados = totalChequesRechazados;
		this.totalNotasCredito = totalNotasCredito;
		this.totalRecibos = totalRecibos;
		this.totalNotasDebito = totalNotasDebito;
		this.totalChequesReembolsos = totalChequesReembolsos;
		this.totalMigracion = totalMigracion;
		this.totalMigracionChequesRechazados = totalMigracionChequesRechazados;
		this.totalPrestamos = totalPrestamos;
		this.totalAnticipos = totalAnticipos;
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);
		String cliente = (String) det[1];
		String cartera = (String) det[11];
		String cartera_ = (String) det[12];
		double ventas = (double) det[2];
		double chequesRechazados = (double) det[3];
		double ncreditos = (double) det[4];
		double recibos = (double) det[5];
		double ndebitos = (double) det[6];
		double reembolsos = (double) det[7];
		double migracion = (double) det[8];
		double migracionChequesRechazados = (double) det[9];
		double anticipos = (double) det[10];
		double saldo = ventas + chequesRechazados + ncreditos + recibos + ndebitos + reembolsos + migracion + migracionChequesRechazados + anticipos;
		
		if ("Cliente".equals(fieldName)) {
			value = cliente.split(";")[0];
		} else if ("Cartera".equals(fieldName)) {
			value = cartera;
		}  else if ("Cartera_".equals(fieldName)) {
			value = cartera_;
		} else if ("Ventas".equals(fieldName)) {
			value = Utiles.getNumberFormat(ventas);
		} else if ("ChequesRechazados".equals(fieldName)) {
			value = Utiles.getNumberFormat(chequesRechazados);
		} else if ("NotasCredito".equals(fieldName)) {
			value = Utiles.getNumberFormat(ncreditos);
		} else if ("Recibos".equals(fieldName)) {
			value = Utiles.getNumberFormat(recibos);
		} else if ("Anticipos".equals(fieldName)) {
			value = Utiles.getNumberFormat(anticipos);
		} else if ("NotasDebito".equals(fieldName)) {
			value = Utiles.getNumberFormat(ndebitos);
		} else if ("Reembolsos".equals(fieldName)) {
			value = Utiles.getNumberFormat(reembolsos);
		} else if ("Prestamos".equals(fieldName)) {
			value = Utiles.getNumberFormat(0.0);
		} else if ("ReembPrestamos".equals(fieldName)) {
			value = Utiles.getNumberFormat(0.0);
		} else if ("Migracion".equals(fieldName)) {
			value = Utiles.getNumberFormat(migracion);
		} else if ("MigracionChequesRechazados".equals(fieldName)) {
			value = Utiles.getNumberFormat(migracionChequesRechazados);
		} else if ("Saldo".equals(fieldName)) {
			value = Utiles.getNumberFormat(saldo);
		} else if ("TotalVentas".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalVentas);
		} else if ("TotalChequesRechazados".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalChequesRechazados);
		} else if ("TotalNotasCredito".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalNotasCredito);
		} else if ("TotalRecibos".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalRecibos);
		} else if ("TotalAnticipos".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalAnticipos);
		} else if ("TotalNotasDebito".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalNotasDebito);
		} else if ("TotalChequesReembolso".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalChequesReembolsos);
		} else if ("TotalMigracion".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalMigracion);
		} else if ("TotalMigracionChequesRechazados".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalMigracionChequesRechazados);
		} else if ("TotalPrestamos".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalPrestamos);
		} else if ("TotalReembPrestamos".equals(fieldName)) {
			value = Utiles.getNumberFormat(0.0);
		} else if ("TotalSaldo".equals(fieldName)) {
			double totalSaldo = totalVentas + totalChequesRechazados + totalNotasCredito + totalRecibos
					+ totalNotasDebito + totalChequesReembolsos + totalMigracion + totalMigracionChequesRechazados
					+ totalPrestamos + totalAnticipos;
			value = Utiles.getNumberFormat(totalSaldo);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * Reporte de remisiones STK-00011..
 */
class ReporteRemisiones extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col1 = new DatosColumnas("Remisión Nro.", TIPO_STRING, 35);
	static DatosColumnas col2 = new DatosColumnas("Factura Nro.", TIPO_STRING, 35);
	static DatosColumnas col3 = new DatosColumnas("Cliente", TIPO_STRING);

	public ReporteRemisiones(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de remisiones a clientes");
		this.setDirectorio("Articulos");
		this.setNombreArchivo("Remisiones-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte de control articulo carga..
 */
class ReporteArticuloControlCarga extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cantidad", TIPO_INTEGER, 20, true);

	public ReporteArticuloControlCarga(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de control de consumo de carga");
		this.setDirectorio("Articulos");
		this.setNombreArchivo("Articulo-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * Reporte VEN-00047..
 */
class CobranzasVendedorDetallado implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, Object[]> data = new HashMap<String, Object[]>();
	Map<String, Double> saldo = new HashMap<String, Double>();
	
	double totalSinIva = 0;
	double totalIvaInc = 0;
	
	public CobranzasVendedorDetallado(List<Object[]> values, double totalIvaInc, double totalSinIva) {
		this.values = values;
		this.totalIvaInc = totalIvaInc;
		this.totalSinIva = totalSinIva;
		// ordena la lista segun fecha..
		Collections.sort(this.values, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String rs1 = (String) o1[0];
				String rs2 = (String) o2[0];
				return rs1.compareTo(rs2);
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);
		Object[] fac = (Object[]) det[2];

		if ("TituloDetalle".equals(fieldName)) {
			value = det[0];
		} else if ("Recibo".equals(fieldName)) {
			value = fac[0];
		} else if ("Factura".equals(fieldName)) {
			value = fac[1];
		} else if ("Codigo".equals(fieldName)) {
			value = fac[2];
		} else if ("Cliente".equals(fieldName)) {
			value = fac[5];
		} else if ("Monto".equals(fieldName)) {
			value = FORMATTER.format(fac[3]);
		} else if ("MontoSinIva".equals(fieldName)) {
			value = FORMATTER.format(fac[4]);
		} else if ("Importe".equals(fieldName)) {
			value = FORMATTER.format(det[1]);
		} else if ("ImporteSinIva".equals(fieldName)) {
			value = FORMATTER.format(det[3]);
		} else if ("TotalSinIva".equals(fieldName)) {
			value = FORMATTER.format(this.totalSinIva);
		} else if ("TotalIvaInc".equals(fieldName)) {
			value = FORMATTER.format(this.totalIvaInc);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource del Libro Compras
 */
class LibroComprasMatricial implements JRDataSource {
	
	List<BeanLibroCompra> values = new ArrayList<BeanLibroCompra>();
	
	double total_gravada10_ = 0;
	double total_gravada5_ = 0;
	double total_exenta_ = 0;
	double total_iva10_ = 0;
	double total_iva5_ = 0;
	double total_baseimponible_ = 0;
	double total_ = 0;
	
	public LibroComprasMatricial(List<Gasto> gastos, List<ImportacionFactura> importaciones, List<NotaCredito> notasCredito, List<CompraLocalFactura> compras) {
		for (Gasto gasto : gastos) {
			String timbrado = gasto.getTimbrado();
			BeanLibroCompra value = new BeanLibroCompra(Utiles.getDateToString(gasto.getFecha(), "dd"),
					Utiles.getDateToString(gasto.getModificado(), Utiles.DD_MM_YYYY), gasto.getNumeroFactura(),
					gasto.getTipoMovimiento().getDescripcion(), timbrado,
					gasto.getProveedor().getRazonSocial(), gasto.getProveedor().getRuc(), gasto.getGravada10(),
					gasto.getGravada5(), gasto.getIva10(), gasto.getIva5(), gasto.getExenta(),
					gasto.getIva5() + gasto.getIva10() + gasto.getExenta() + gasto.getGravada10() + gasto.getGravada5(),
					gasto.getBaseImponible(), gasto.getDescripcionCuenta1(), gasto.getFecha());
			values.add(value);
		}		
		for (NotaCredito nc : notasCredito) {
				Date fecha_ = nc.getFechaEmision();
				String fechaCarga = Utiles.getDateToString(nc.getModificado(), Utiles.DD_MM_YYYY);
				String fecha = Utiles.getDateToString(nc.getFechaEmision(), "dd");
				String numero = nc.getNumero();
				String concepto = "NOTA CRÉDITO-GASTO";
				String timbrado = nc.getTimbrado().getNumero();
				String proveedor = nc.getProveedor().getRazonSocial();
				String ruc = nc.getProveedor().getRuc();
				double gravada10 = nc.getTotalGravado10() * -1;
				double gravada5 = 0.0;
				double exenta = nc.getTotalExenta() * -1;
				double iva10 = nc.getTotalIva10() * -1;
				double iva5 = 0.0;
				double total = gravada10 + gravada5 + iva10 + iva5 + exenta;
				double baseImponible = 0.0;
				String cuenta1 = (nc.getObservacion().contains("COMISIONES") || nc.getObservacion().contains("TELEFONIA") || nc.getObservacion().contains("CT:")) ? 
						nc.getObservacion().replace("CT:", "") : "DESCUENTOS OBTENIDOS";
				
				BeanLibroCompra value = new BeanLibroCompra(fecha, fechaCarga, numero, concepto, timbrado, proveedor, ruc,
						gravada10, gravada5, iva10, iva5, exenta, total, baseImponible, cuenta1, fecha_);
				this.values.add(value);
		}
		for (ImportacionFactura fac : importaciones) {
			BeanLibroCompra value = new BeanLibroCompra(
					Utiles.getDateToString(fac.getFechaOriginal(), "dd"),
					Utiles.getDateToString(fac.getModificado(), Utiles.DD_MM_YYYY), fac.getNumero(),
					fac.getTipoMovimiento().getDescripcion(), "", "PROVEEDOR DEL EXTERIOR",
					Configuracion.RUC_EMPRESA_EXTERIOR, 0.0, 0.0, 0.0, 0.0, (fac.getTotalImporteDs() * fac.getPorcProrrateo()),
					(fac.getTotalImporteDs() * fac.getPorcProrrateo()), 0.0, "IMPORTACIONES EN CURSO", fac.getFechaOriginal());
			values.add(value);
		}
		for (CompraLocalFactura fac : compras) {
			BeanLibroCompra value = new BeanLibroCompra(Utiles.getDateToString(fac.getFechaOriginal(), "dd"),
					Utiles.getDateToString(fac.getModificado(), Utiles.DD_MM_YYYY), fac.getNumero(),
					fac.getTipoMovimiento().getDescripcion(), "",
					fac.getProveedor().getRazonSocial(), fac.getProveedor().getRuc(), fac.getGravada10(),
					fac.getGravada5(), fac.getIva10(), fac.getIva5(), fac.getExenta(),
					fac.getIva5() + fac.getIva10() + fac.getExenta() + fac.getGravada10() + fac.getGravada5(),
					0.0, "", fac.getFechaOriginal());
			values.add(value);
		}
		
		// ordena la lista segun fecha..
		Collections.sort(this.values, new Comparator<BeanLibroCompra>() {
			@Override
			public int compare(BeanLibroCompra o1, BeanLibroCompra o2) {
				Date fecha1 = o1.fecha_;
				Date fecha2 = o2.fecha_;
				return fecha1.compareTo(fecha2);
			}
		});
    }
	
	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
        Object value = null;
        String fieldName = field.getName();
        BeanLibroCompra compra = this.values.get(index);
         
        if ("dia".equals(fieldName)) {
        	value = compra.fecha;
        } else if ("FechaCarga".equals(fieldName)) {
        	value = compra.fechaCarga;
        } else if ("Numero".equals(fieldName)) {
        	value = compra.numero;
        } else if ("Concepto".equals(fieldName)) {
        	value = compra.concepto;
        } else if ("Timbrado".equals(fieldName)) {
        	value = compra.timbrado;
        } else if ("Proveedor".equals(fieldName)) {
        	value = compra.proveedor;
        } else if ("Ruc".equals(fieldName)) {
        	value = compra.ruc;
        } else if ("Gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.gravada10);
        } else if ("Gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.gravada5);
        } else if ("Gravadas".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.gravada5 + compra.gravada10);
        } else if ("Exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.exenta);
        } else if ("Iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.iva10);
        } else if ("Iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.iva5);
        } else if ("Total".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.total);
        } else if ("Base_imponible".equals(fieldName)) {
        	value = Utiles.getNumberFormat(compra.baseImponible);
        } else if ("Total_Gravada10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_gravada10_);
        } else if ("Total_Gravada5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_gravada5_);
        } else if ("Total_Gravada".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_gravada5_ + this.total_gravada10_);
        } else if ("Total_Iva5".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_iva5_);
        } else if ("Total_Iva10".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_iva10_);
        } else if ("Total_Exenta".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_exenta_);
        } else if ("Total_".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_);
        } else if ("Total_base_imponible".equals(fieldName)) {
        	value = Utiles.getNumberFormat(this.total_baseimponible_);
        } else if ("Footer".equals(fieldName)) {
			value = index < values.size() - 1? "Transporte" : "Totales";
		}
        return value;
    }

	@Override
	public boolean next() throws JRException {
		if (index < values.size() - 1) {
			BeanLibroCompra compra = this.values.get(index + 1);
	        this.total_gravada10_ += compra.gravada10;
	        this.total_gravada5_ += compra.gravada5;
	        this.total_iva10_ += compra.iva10;
	        this.total_iva5_ += compra.iva5;
	        this.total_exenta_ += compra.exenta;
	        this.total_baseimponible_ += compra.baseImponible;
	        this.total_ = (this.total_gravada10_ + this.total_gravada5_ + this.total_iva10_ + this.total_iva5_ + this.total_exenta_);
			index ++;
			return true;
		}
		return false;
	}
	
	class BeanLibroCompra {
		Date fecha_;
		String fecha;
		String fechaCarga;
		String numero;
		String concepto;
		String timbrado;
		String proveedor;
		String ruc;
		double gravada10;
		double gravada5;
		double exenta;
		double iva10;
		double iva5;
		double total;
		double baseImponible;
		String cuenta1;

		public BeanLibroCompra(String fecha, String fechaCarga, String numero, String concepto, String timbrado,
				String proveedor, String ruc, double gravada10, double gravada5, double iva10, double iva5, double exenta,
				double total, double baseImponible, String cuenta, Date fecha_) {
			this.fecha = fecha;
			this.fechaCarga = fechaCarga;
			this.numero = numero;
			this.concepto = concepto;
			this.timbrado = timbrado;
			this.proveedor = proveedor;
			this.ruc = ruc;
			this.gravada10 = gravada10;
			this.gravada5 = gravada5;
			this.iva10 = iva10;
			this.iva5 = iva5;
			this.exenta = exenta;
			this.total = total;
			this.cuenta1 = cuenta;
			this.baseImponible = baseImponible;
			this.fecha_ = fecha_;
		}
		
		public double getGravada() {
			return this.gravada10 + this.gravada5;
		}
	}
}

/**
 * DataSource de Libro Ventas ..
 */
class LibroVentasMatricial implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	Misc misc = new Misc();

	List<Venta> ventas;
	List<NotaCredito> notasCredito;
	List<NotaDebito> notasDebito;
	List<BeanLibroVenta> values = new ArrayList<BeanLibroVenta>();
	Date desde;
	Date hasta;

	double totalContado = 0;
	double totalCredito = 0;
	double totalNCContado = 0;
	double totalNCCredito = 0;
	double totalNDebito = 0;

	double totalGravada = 0;
	double totalImpuesto = 0;
	double totalImporte = 0;
	double totalExenta = 0;
	
	double total_gravada10_ = 0;
	double total_gravada5_ = 0;
	double total_exenta_ = 0;
	double total_iva10_ = 0;
	double total_iva5_ = 0;
	double total_baseimponible_ = 0;
	double total_ = 0;

	public LibroVentasMatricial(List<Venta> ventas,
			List<NotaCredito> notasCredito, List<NotaDebito> notasDebito, Date desde, Date hasta) {
		this.notasCredito = notasCredito;
		this.ventas = ventas;
		this.notasDebito = notasDebito;
		this.desde = desde;
		this.hasta = hasta;
		this.loadDatos();
		// ordena la lista segun fecha..
		Collections.sort(this.values, new Comparator<BeanLibroVenta>() {
			@Override
			public int compare(BeanLibroVenta o1, BeanLibroVenta o2) {
				Date fecha1 = o1.getFecha_();
				Date fecha2 = o2.getFecha_();
				return fecha1.compareTo(fecha2);
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		BeanLibroVenta venta = this.values.get(index);

		if ("Fecha".equals(fieldName)) {
			value = venta.getFecha();
		} else if ("Numero".equals(fieldName)) {
			value = venta.getNumero();
		} else if ("Cliente".equals(fieldName)) {
			value = venta.getRazonSocial();
		} else if ("Ruc".equals(fieldName)) {
			value = venta.getRuc();
		} else if ("Gravada10".equals(fieldName)) {
			value = FORMATTER.format(venta.getGravado10());
		} else if ("Gravada5".equals(fieldName)) {
			value = FORMATTER.format(venta.getGravado5());
		} else if ("Iva10".equals(fieldName)) {
			value = FORMATTER.format(venta.getIva10());
		} else if ("Iva5".equals(fieldName)) {
			value = FORMATTER.format(venta.getIva5());
		} else if ("Exenta".equals(fieldName)) {
			value = FORMATTER.format(venta.getExenta());
		} else if ("Total".equals(fieldName)) {
			value = FORMATTER.format(venta.getTotal());
		} else if ("Total_Exenta".equals(fieldName)) {
			value = FORMATTER.format(this.total_exenta_);
		} else if ("Total_Iva5".equals(fieldName)) {
			value = FORMATTER.format(this.total_iva5_);
		} else if ("Total_Iva10".equals(fieldName)) {
			value = FORMATTER.format(this.total_iva10_);
		} else if ("Total_Gravada10".equals(fieldName)) {
			value = FORMATTER.format(this.total_gravada10_);
		} else if ("Total_Gravada5".equals(fieldName)) {
			value = FORMATTER.format(this.total_gravada5_);
		} else if ("Total_".equals(fieldName)) {
			value = FORMATTER.format(this.total_);
		} else if ("Footer".equals(fieldName)) {
			value = index < values.size() - 1? "Transporte" : "Totales";
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < values.size() - 1) {
			BeanLibroVenta venta = this.values.get(index + 1);
	        this.total_gravada10_ += venta.getGravado10();
	        this.total_gravada5_ += venta.getGravado5();
	        this.total_iva10_ += venta.getIva10();
	        this.total_iva5_ += venta.getIva5();
	        this.total_exenta_ += venta.getExenta();
	        this.total_ = (this.total_gravada10_ + this.total_gravada5_ + this.total_iva10_ + this.total_iva5_ + this.total_exenta_);
			index++;
			return true;
		}
		return false;
	}

	/**
	 * carga los datos para el reporte..
	 */
	private void loadDatos() {
		for (NotaDebito nd : this.notasDebito) {
			String fecha = misc.dateToString(nd.getFecha(), "dd/MM/yyyy");
			String concepto = "NOT-DEB";
			String numero = nd.getNumero();
			String razonSocial = nd.getCliente().getRazonSocial();
			String ruc = nd.getCliente().getRuc();
			if (ruc.isEmpty()) ruc = Configuracion.RUC_EMPRESA_LOCAL;
			double grav5 = 0.0;
			double grav10 = nd.getTotalGravado10();
			double iva5 = 0.0;
			double iva10 = nd.getTotalIva10();
			double total = nd.getTotalImporteGs();
			values.add(new BeanLibroVenta(fecha, concepto, numero, razonSocial, ruc, grav10, iva10, grav5, iva5, total, 0.0, nd.getFecha()));
			this.totalGravada += (nd.getTotalGravado10());
			this.totalImpuesto += (nd.getTotalIva10());
			this.totalImporte += (nd.getTotalImporteGs());
			this.totalNDebito += (nd.getTotalImporteGs());
		}
		for (NotaCredito ncred : this.notasCredito) {
			String fecha = misc.dateToString(ncred.getFechaEmision(), "dd/MM/yyyy");
			String concepto = TipoMovimiento.getAbreviatura(ncred.getTipoMovimiento().getSigla());
			String numero = ncred.getNumero();
			String razonSocial = ncred.isAnulado() ? "ANULADO" : ncred.getCliente().getRazonSocial();
			String ruc = ncred.getCliente().getRuc();
			if (ruc.isEmpty()) ruc = Configuracion.RUC_EMPRESA_LOCAL;
			double iva10 = ncred.isAnulado() ? 0.0 : redondear(ncred.getTotalIva10()) * -1;
			double gravada = ncred.isAnulado() ? 0.0 : redondear(ncred.getTotalGravado10()) * -1;
			double exenta = ncred.isAnulado() ? 0.0 : redondear(ncred.getTotalExenta()) * -1;
			double importe = (iva10 + gravada + exenta);
			values.add(new BeanLibroVenta(fecha, concepto, numero, razonSocial,
					ncred.isAnulado() ? "" : ruc, gravada, iva10, 0.0, 0.0, importe, exenta, ncred.getFechaEmision()));
			if (ncred.isAnulado() == false) {
				this.totalGravada -= (ncred.getTotalGravado10());
				this.totalImpuesto -= (ncred.getTotalIva10());
				this.totalImporte -= (ncred.getTotalIva10() + ncred.getTotalGravado10() + ncred.getTotalExenta());
				this.totalExenta -= ncred.getTotalExenta();
				if (ncred.isNotaCreditoVentaContado()) {
					this.totalNCContado -= (ncred.getTotalIva10() + ncred.getTotalGravado10() + ncred.getTotalExenta());
				} else {
					this.totalNCCredito -= (ncred.getTotalIva10() + ncred.getTotalGravado10() + ncred.getTotalExenta());
				}
			}
		}

		for (Venta vta : this.ventas) {
			String fecha = misc.dateToString(vta.getFecha(), "dd/MM/yyyy");
			String concepto = TipoMovimiento.getAbreviatura(vta.getTipoMovimiento().getSigla());
			String numero = vta.getNumero();
			String razonSocial = vta.isAnulado() ? "ANULADO" : vta.getDenominacion();
			if (razonSocial == null) razonSocial = vta.getCliente().getRazonSocial();
			String ruc = vta.getCliente().getRuc();
			if (ruc.isEmpty()) ruc = Configuracion.RUC_EMPRESA_LOCAL;
			double iva10 = vta.isAnulado() ? 0.0 : redondear(vta.getTotalIva10());
			double gravada10 = vta.isAnulado() ? 0.0 : redondear(vta.getTotalGravado10());
			double iva5 = vta.isAnulado() ? 0.0 : redondear(vta.getTotalIva5());
			double gravada5 = vta.isAnulado() ? 0.0 : redondear(vta.getTotalGravado5());
			double exenta = vta.isAnulado() ? 0.0 : redondear(vta.getTotalExenta());
			double importe = vta.isAnulado() ? 0.0 : (iva10 + gravada10 + iva5 + gravada5 + exenta);
			values.add(new BeanLibroVenta(fecha, concepto, numero, razonSocial,
					vta.isAnulado() ? "" : ruc, gravada10, iva10, gravada5, iva5, importe, exenta, vta.getFecha()));
			if (vta.isAnulado() == false) {
				this.totalGravada += (vta.getTotalGravado10());
				this.totalImpuesto += (vta.getTotalIva10());
				this.totalImporte += importe;
				this.totalExenta += vta.getTotalExenta();
				if (vta.isVentaContado()) {
					this.totalContado += (importe);
				} else {
					this.totalCredito += (importe);
				}
			}
		}
	}
	
	/**
	 * @return el monto redondeado..
	 */
	private static double redondear(double monto) {
		return Math.rint(monto * 1) / 1;
	}
}

/**
 * DataSource de Movimientos de articulos..
 */
class VentasClienteArticulo implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<HistoricoMovimientoArticulo> values = new ArrayList<HistoricoMovimientoArticulo>();
	
	double totalEnero = 0;
	double totalFebrero = 0;
	double totalMarzo = 0;
	double totalAbril = 0;
	double totalMayo = 0;
	double totalJunio = 0;
	double totalJulio = 0;
	double totalAgosto = 0;
	double totalSetiembre = 0;
	double totalOctubre = 0;
	double totalNoviembre = 0;
	double totalDiciembre = 0;
	
	double totalEnero_ = 0;
	double totalFebrero_ = 0;
	double totalMarzo_ = 0;
	double totalAbril_ = 0;
	double totalMayo_ = 0;
	double totalJunio_ = 0;
	double totalJulio_ = 0;
	double totalAgosto_ = 0;
	double totalSetiembre_ = 0;
	double totalOctubre_ = 0;
	double totalNoviembre_ = 0;
	double totalDiciembre_ = 0;
	int rango = 0;
	
	public VentasClienteArticulo(List<HistoricoMovimientoArticulo> values, int rango) {
		this.values = values;
		this.rango = rango;
		for (HistoricoMovimientoArticulo item : values) {
			totalEnero += item.getEnero_();
			totalEnero_ += item.get_enero();
			totalFebrero += item.getFebrero_();
			totalFebrero_ += item.get_febrero();
			totalMarzo += item.getMarzo_();
			totalMarzo_ += item.get_marzo();
			totalAbril += item.getAbril_();
			totalAbril_ += item.get_abril();
			totalMayo += item.getMayo_();
			totalMayo_ += item.get_mayo();
			totalJunio += item.getJunio_();
			totalJunio_ += item.get_junio();
			totalJulio += item.getJulio_();
			totalJulio_ += item.get_julio();
			totalAgosto += item.getAgosto_();
			totalAgosto_ += item.get_agosto();
			totalSetiembre += item.getSetiembre_();
			totalSetiembre_ += item.get_setiembre();
			totalOctubre += item.getOctubre_();
			totalOctubre_ += item.get_octubre();
			totalNoviembre += item.getNoviembre_();
			totalNoviembre_ += item.get_noviembre();
			totalDiciembre += item.getDiciembre_();
			totalDiciembre_ += item.get_diciembre();
		}
		Collections.sort(this.values, new Comparator<HistoricoMovimientoArticulo>() {
			@Override
			public int compare(HistoricoMovimientoArticulo o1, HistoricoMovimientoArticulo o2) {
				String val1 = o1.getReferencia();
				String val2 = o2.getReferencia();
				int compare = val1.compareTo(val2);				
				return compare;
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		HistoricoMovimientoArticulo det = this.values.get(index);
		String cod = det.getCodigo();

		if ("Codigo".equals(fieldName)) {
			value = cod;
		} else if ("CodigoProveedor".equals(fieldName)) {
			value = det.getCodigoProveedor();
		} else if ("Referencia".equals(fieldName)) {
			value = det.getReferencia();
		} else if ("Medida".equals(fieldName)) {
			value = det.getCodigoOriginal();
		} else if ("NroParte".equals(fieldName)) {
			value = det.getCodigoOriginal();
		} else if ("Estado".equals(fieldName)) {
			value = det.getEstado();
		} else if ("Descripcion".equals(fieldName)) {
			value = det.getDescripcion();
		} else if ("OchentaVeinte".equals(fieldName)) {
			value = det.getOchentaVeinte();
		} else if ("Abc".equals(fieldName)) {
			value = det.getAbc();
		} else if ("Familia".equals(fieldName)) {
			value = det.getFamilia();
		} else if ("Marca".equals(fieldName)) {
			value = det.getMarca();
		} else if ("Linea".equals(fieldName)) {
			value = det.getLinea();
		} else if ("Grupo".equals(fieldName)) {
			value = det.getGrupo();
		} else if ("Aplicacion".equals(fieldName)) {
			value = det.getAplicacion();
		} else if ("Modelo".equals(fieldName)) {
			value = det.getModelo();
		} else if ("Peso".equals(fieldName)) {
			value = det.getPeso();
		} else if ("Volumen".equals(fieldName)) {
			value = det.getVolumen();
		} else if ("Proveedor".equals(fieldName)) {
			value = det.getProveedor();
		} else if ("CantLocal".equals(fieldName)) {
			value = det.getCantidad() + "";
		} else if ("FechaLocal".equals(fieldName)) {
			value = det.getFechaUltimaCompra() + "";
		} else if ("ProvLocal".equals(fieldName)) {
			value = det.getProveedorUltimaCompra();			
		} else if ("Dep_1".equals(fieldName)) {
			value = det.getStock1() + "";			
		} else if ("Dep_2".equals(fieldName)) {
			value = det.getStock2() + "";			
		} else if ("Dep_3".equals(fieldName)) {
			value = det.getStock3() + "";			
		} else if ("Dep_4".equals(fieldName)) {
			value = det.getStock4() + "";		
		} else if ("Dep_5".equals(fieldName)) {
			value = det.getStock5() + "";			
		} else if ("Dep_6".equals(fieldName)) {
			value = det.getStock6() + "";			
		} else if ("Dep_7".equals(fieldName)) {
			value = det.getStock7() + "";			
		} else if ("Dep_8".equals(fieldName)) {
			value = det.getStock8() + "";			
		} else if ("Dep_gral".equals(fieldName)) {
			value = det.getTotal() + "";			
		} else if ("Stock".equals(fieldName)) {
			value = det.getStock1() + "";			
		} else if ("Stock2".equals(fieldName)) {
			value = det.getStock2() + "";			
		} else if ("Stock3".equals(fieldName)) {
			value = det.getStock3() + "";			
		} else if ("Enero".equals(fieldName)) {
			value = det.getEnero_();
		} else if ("Febrero".equals(fieldName)) {
			value = det.getFebrero_();
		} else if ("Marzo".equals(fieldName)) {
			value = det.getMarzo_();
		} else if ("Abril".equals(fieldName)) {
			value = det.getAbril_();
		} else if ("Mayo".equals(fieldName)) {
			value = det.getMayo_();
		} else if ("Junio".equals(fieldName)) {
			value = det.getJunio_();
		} else if ("Julio".equals(fieldName)) {
			value = det.getJulio_();
		} else if ("Agosto".equals(fieldName)) {
			value = det.getAgosto_();
		} else if ("Setiembre".equals(fieldName)) {
			value = det.getSetiembre_();
		} else if ("Octubre".equals(fieldName)) {
			value = det.getOctubre_();
		} else if ("Noviembre".equals(fieldName)) {
			value = det.getNoviembre_();
		} else if ("Diciembre".equals(fieldName)) {
			value = det.getDiciembre_();
		}  else if ("_Enero".equals(fieldName)) {
			value =  Utiles.getRedondeo(det.get_enero());
		} else if ("_Febrero".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_febrero());
		} else if ("_Marzo".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_marzo());
		} else if ("_Abril".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_abril());
		} else if ("_Mayo".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_mayo());
		} else if ("_Junio".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_junio());
		} else if ("_Julio".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_julio());
		} else if ("_Agosto".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_agosto());
		} else if ("_Setiembre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_setiembre());
		} else if ("_Octubre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_octubre());
		} else if ("_Noviembre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_noviembre());
		} else if ("_Diciembre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_diciembre());
		} else if ("tot_oct".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalOctubre);
		} else if ("tot_oct_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalOctubre_);
		} else if ("tot_nov".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalNoviembre);
		} else if ("tot_nov_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalNoviembre_);
		} else if ("tot_dic".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalDiciembre);
		} else if ("tot_dic_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalDiciembre_);
		} else if ("tot_ene".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalEnero);
		} else if ("tot_ene_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalEnero_);
		} else if ("tot_feb".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalFebrero);
		} else if ("tot_feb_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalFebrero_);
		} else if ("tot_mar".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalMarzo);
		} else if ("tot_mar_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalMarzo_);
		} else if ("tot_abr".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalAbril);
		} else if ("tot_abr_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalAbril_);
		} else if ("tot_may".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalMayo);
		} else if ("tot_may_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalMayo_);
		} else if ("tot_jun".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalJunio);
		} else if ("tot_jun_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalJunio_);
		} else if ("tot_jul".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalJulio);
		} else if ("tot_jul_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalJulio_);
		} else if ("tot_ago".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalAgosto);
		} else if ("tot_ago_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalAgosto_);
		} else if ("tot_set".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalSetiembre);
		} else if ("tot_set_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalSetiembre_);
		} else if ("Costo".equals(fieldName)) {
			value = Utiles.getNumberFormat(det.getCostoGs());
		} else if ("Mayorista".equals(fieldName)) {
			value = Utiles.getNumberFormat(det.getCostoFobGs());
		} else if ("Minorista".equals(fieldName)) {
			value = Utiles.getNumberFormat(det.getPrecioMinorista());
		} else if ("Lista".equals(fieldName)) {
			value = Utiles.getNumberFormat(det.getPrecioLista());
		} else if ("Total".equals(fieldName)) {
			value = Utiles.getNumberFormat(det.getEnero_() + det.getFebrero_() + det.getMarzo_() + det.getAbril_()
					+ det.getMayo_() + det.getJunio_() + det.getJulio_() + det.getAgosto_() + det.getSetiembre_()
					+ det.getOctubre_() + det.getNoviembre_() + det.getDiciembre_());
		} else if ("tot_tot".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalEnero + this.totalFebrero + this.totalMarzo + this.totalAbril
			+ this.totalMayo + this.totalJunio + this.totalJulio + this.totalAgosto + this.totalSetiembre
			+ this.totalOctubre + this.totalNoviembre + this.totalDiciembre);
		} else if ("Promedio".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(det.getTotal_() / this.rango);
		} else if ("Pmeses".equals(fieldName)) {
			if ((det.getTotal_() / this.rango) > 0) {
				value = Utiles.getNumberFormatDs(det.getStock1() / (det.getTotal_() / this.rango));
			} else {
				value = Utiles.getNumberFormatDs(0.0);
			}
		} else if ("Litraje".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(det.getCoeficiente());
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de ventas por proveedor / cliente..
 */
class VentasProveedorCliente implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<HistoricoMovimientoArticulo> values = new ArrayList<HistoricoMovimientoArticulo>();
	
	double totalEnero = 0;
	double totalFebrero = 0;
	double totalMarzo = 0;
	double totalAbril = 0;
	double totalMayo = 0;
	double totalJunio = 0;
	double totalJulio = 0;
	double totalAgosto = 0;
	double totalSetiembre = 0;
	double totalOctubre = 0;
	double totalNoviembre = 0;
	double totalDiciembre = 0;
	
	double totalEnero_ = 0;
	double totalFebrero_ = 0;
	double totalMarzo_ = 0;
	double totalAbril_ = 0;
	double totalMayo_ = 0;
	double totalJunio_ = 0;
	double totalJulio_ = 0;
	double totalAgosto_ = 0;
	double totalSetiembre_ = 0;
	double totalOctubre_ = 0;
	double totalNoviembre_ = 0;
	double totalDiciembre_ = 0;
	int rango = 0;
	
	public VentasProveedorCliente(List<HistoricoMovimientoArticulo> values, int rango) {
		this.values = values;
		this.rango = rango;
		for (HistoricoMovimientoArticulo item : values) {
			totalEnero += item.getEnero();
			totalEnero_ += item.get_enero();
			totalFebrero += item.getFebrero_();
			totalFebrero_ += item.get_febrero();
			totalMarzo += item.getMarzo_();
			totalMarzo_ += item.get_marzo();
			totalAbril += item.getAbril_();
			totalAbril_ += item.get_abril();
			totalMayo += item.getMayo_();
			totalMayo_ += item.get_mayo();
			totalJunio += item.getJunio_();
			totalJunio_ += item.get_junio();
			totalJulio += item.getJulio_();
			totalJulio_ += item.get_julio();
			totalAgosto += item.getAgosto_();
			totalAgosto_ += item.get_agosto();
			totalSetiembre += item.getSetiembre_();
			totalSetiembre_ += item.get_setiembre();
			totalOctubre += item.getOctubre_();
			totalOctubre_ += item.get_octubre();
			totalNoviembre += item.getNoviembre_();
			totalNoviembre_ += item.get_noviembre();
			totalDiciembre += item.getDiciembre_();
			totalDiciembre_ += item.get_diciembre();
		}
		Collections.sort(this.values, new Comparator<HistoricoMovimientoArticulo>() {
			@Override
			public int compare(HistoricoMovimientoArticulo o1, HistoricoMovimientoArticulo o2) {
				String val1 = o1.getDescripcion();
				String val2 = o2.getDescripcion();
				int compare = val1.compareTo(val2);				
				return compare;
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		HistoricoMovimientoArticulo det = this.values.get(index);
		String cod = det.getCodigo();

		if ("Codigo".equals(fieldName)) {
			value = cod;
		} else if ("Ruc".equals(fieldName)) {
			value = det.getRuc();
		} else if ("Vendedor".equals(fieldName)) {
			value = det.getVendedor();
		} else if ("Rubro".equals(fieldName)) {
			value = det.getRubro();
		} else if ("Zona".equals(fieldName)) {
			value = det.getZona();
		} else if ("CodigoProveedor".equals(fieldName)) {
			value = det.getCodigoProveedor();
		}  else if ("Referencia".equals(fieldName)) {
			value = det.getReferencia();
		} else if ("NroParte".equals(fieldName)) {
			value = det.getCodigoOriginal();
		} else if ("Estado".equals(fieldName)) {
			value = det.getEstado();
		} else if ("Descripcion".equals(fieldName)) {
			value = det.getDescripcion();
		} else if ("OchentaVeinte".equals(fieldName)) {
			value = det.getOchentaVeinte();
		} else if ("Abc".equals(fieldName)) {
			value = det.getAbc();
		} else if ("Familia".equals(fieldName)) {
			value = det.getFamilia();
		} else if ("Marca".equals(fieldName)) {
			value = det.getMarca();
		} else if ("Linea".equals(fieldName)) {
			value = det.getLinea();
		} else if ("Grupo".equals(fieldName)) {
			value = det.getGrupo();
		} else if ("Aplicacion".equals(fieldName)) {
			value = det.getAplicacion();
		} else if ("Modelo".equals(fieldName)) {
			value = det.getModelo();
		} else if ("Peso".equals(fieldName)) {
			value = det.getPeso();
		} else if ("Volumen".equals(fieldName)) {
			value = det.getVolumen();
		} else if ("Proveedor".equals(fieldName)) {
			value = det.getProveedor();
		} else if ("CantLocal".equals(fieldName)) {
			value = det.getCantidad() + "";
		} else if ("FechaLocal".equals(fieldName)) {
			value = det.getFechaUltimaCompra() + "";
		} else if ("ProvLocal".equals(fieldName)) {
			value = det.getProveedorUltimaCompra();			
		} else if ("Dep_1".equals(fieldName)) {
			value = det.getStock1() + "";			
		} else if ("Dep_2".equals(fieldName)) {
			value = det.getStock2() + "";			
		} else if ("Dep_3".equals(fieldName)) {
			value = det.getStock3() + "";			
		} else if ("Dep_4".equals(fieldName)) {
			value = det.getStock4() + "";		
		} else if ("Dep_5".equals(fieldName)) {
			value = det.getStock5() + "";			
		} else if ("Dep_6".equals(fieldName)) {
			value = det.getStock6() + "";			
		} else if ("Dep_7".equals(fieldName)) {
			value = det.getStock7() + "";			
		} else if ("Dep_8".equals(fieldName)) {
			value = det.getStock8() + "";			
		} else if ("Dep_gral".equals(fieldName)) {
			value = det.getTotal() + "";			
		} else if ("Stock".equals(fieldName)) {
			value = det.getStock1() + "";			
		} else if ("Enero".equals(fieldName)) {
			value = det.getEnero_();
		} else if ("Febrero".equals(fieldName)) {
			value = det.getFebrero_();
		} else if ("Marzo".equals(fieldName)) {
			value = det.getMarzo_();
		} else if ("Abril".equals(fieldName)) {
			value = det.getAbril_();
		} else if ("Mayo".equals(fieldName)) {
			value = det.getMayo_();
		} else if ("Junio".equals(fieldName)) {
			value = det.getJunio_();
		} else if ("Julio".equals(fieldName)) {
			value = det.getJulio_();
		} else if ("Agosto".equals(fieldName)) {
			value = det.getAgosto_();
		} else if ("Setiembre".equals(fieldName)) {
			value = det.getSetiembre_();
		} else if ("Octubre".equals(fieldName)) {
			value = det.getOctubre_();
		} else if ("Noviembre".equals(fieldName)) {
			value = det.getNoviembre_();
		} else if ("Diciembre".equals(fieldName)) {
			value = det.getDiciembre_();
		}  else if ("_Enero".equals(fieldName)) {
			value =  Utiles.getRedondeo(det.get_enero());
		} else if ("_Febrero".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_febrero());
		} else if ("_Marzo".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_marzo());
		} else if ("_Abril".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_abril());
		} else if ("_Mayo".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_mayo());
		} else if ("_Junio".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_junio());
		} else if ("_Julio".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_julio());
		} else if ("_Agosto".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_agosto());
		} else if ("_Setiembre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_setiembre());
		} else if ("_Octubre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_octubre());
		} else if ("_Noviembre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_noviembre());
		} else if ("_Diciembre".equals(fieldName)) {
			value = Utiles.getRedondeo(det.get_diciembre());
		} else if ("tot_oct".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalOctubre);
		} else if ("tot_oct_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalOctubre_);
		} else if ("tot_nov".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalNoviembre);
		} else if ("tot_nov_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalNoviembre_);
		} else if ("tot_dic".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalDiciembre);
		} else if ("tot_dic_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalDiciembre_);
		} else if ("tot_ene".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalEnero);
		} else if ("tot_ene_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalEnero_);
		} else if ("tot_feb".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalFebrero);
		} else if ("tot_feb_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalFebrero_);
		} else if ("tot_mar".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalMarzo);
		} else if ("tot_mar_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalMarzo_);
		} else if ("tot_abr".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalAbril);
		} else if ("tot_abr_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalAbril_);
		} else if ("tot_may".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalMayo);
		} else if ("tot_may_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalMayo_);
		} else if ("tot_jun".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalJunio);
		} else if ("tot_jun_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalJunio_);
		} else if ("tot_jul".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalJulio);
		} else if ("tot_jul_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalJulio_);
		} else if ("tot_ago".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalAgosto);
		} else if ("tot_ago_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalAgosto_);
		} else if ("tot_set".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalSetiembre);
		} else if ("tot_set_".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalSetiembre_);
		}  else if ("Costo".equals(fieldName)) {
			value = Utiles.getNumberFormat(det.getCostoGs());
		}  else if ("Mayorista".equals(fieldName)) {
			value = Utiles.getNumberFormat(det.getCostoFobGs());
		} else if ("Total".equals(fieldName)) {
			value = Utiles.getNumberFormat(det.getTotal_());
		} else if ("tot_tot".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalEnero_ + this.totalFebrero_ + this.totalMarzo_ + this.totalAbril_
			+ this.totalMayo_ + this.totalJunio_ + this.totalJulio_ + this.totalAgosto_ + this.totalSetiembre_
			+ this.totalOctubre_ + this.totalNoviembre_ + this.totalDiciembre_);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de cobranzas detallado..
 */
class CobranzasDetalladoDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, Double> totales = new HashMap<String, Double>();
	Date desde;
	Date hasta;
	
	public CobranzasDetalladoDataSource(List<Recibo> recibos, Date desde, Date hasta, boolean cobranzas) {
		this.desde = desde;
		this.hasta = hasta;
		for (Recibo recibo : recibos) {
			if (!recibo.isReciboContraCuenta()) {
				for (ReciboDetalle det : recibo.getDetalles()) {
					if (det.getMovimiento() != null) {
						String cartera = det.getMovimiento().getCarteraCliente() != null
								? det.getMovimiento().getCarteraCliente().getDescripcion()
								: "SIN CARTERA";
						this.values.add(new Object[] { recibo, det, cartera, recibo.getFechaEmision(), recibo.getNumero(),
								recibo.getCliente().getRazonSocial(), det.getMovimiento().getNroComprobante(),
								Utiles.getDateToString(det.getMovimiento().getFechaVencimiento(), Utiles.DD_MM_YYYY),
								Utiles.getNumberFormat(det.getMontoGs()) });						
						Double total = totales.get(cartera);
						if (total != null) {
							total += det.getMontoGs();
						} else {
							total = det.getMontoGs();
						}
						totales.put(cartera, total);
					}				
				}
			}
		}
		
		if (cobranzas) {
			this.loadAplicacionesAnticipos();
		}
		
		Collections.sort(this.values, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String val1 = (String) o1[2];
				String val2 = (String) o2[2];			
				int compare = val1.compareTo(val2);
				if (compare == 0) {
					Date emision1 = (Date) o1[3];
					Date emision2 = (Date) o2[3];
		            return emision1.compareTo(emision2);
		        }
		        else {
		            return compare;
		        }
			}
		});
	}
	
	/**
	 * busca las aplicaciones de anticipos..
	 */
	private void loadAplicacionesAnticipos() {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<AjusteCtaCte> apls = rr.getAplicacionesAnticipos(this.desde, this.hasta);
			for (AjusteCtaCte apl : apls) {
				String cartera = apl.getCredito().getCarteraCliente() != null
						? apl.getCredito().getCarteraCliente().getDescripcion()
						: "SIN CARTERA";
				String cliente = apl.getDescripcion();
				String numero = apl.getOrden();
				String factura = apl.getCredito().getNroComprobante();
				String vencimiento = Utiles.getDateToString(apl.getCredito().getFechaVencimiento(), Utiles.DD_MM_YYYY);
				String importe = Utiles.getNumberFormat(apl.getImporte());
				this.values.add(new Object[] { null, null, cartera, apl.getFecha(), numero, cliente, factura, vencimiento, importe });
				Double total = totales.get(cartera);
				if (total != null) {
					total += apl.getImporte();
				} else {
					total = apl.getImporte();
				}
				totales.put(cartera, total);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);
		
		String tituloDet = (String) det[2];
		String emision = Utiles.getDateToString((Date) det[3], Utiles.DD_MM_YYYY);
		String cliente = (String) det[5];
		String numero = (String) det[4];
		String factura = (String) det[6];
		String vencimiento = (String) det[7];
		String importe = (String) det[8];
		
		if ("TituloDetalle".equals(fieldName)) {
			value = tituloDet.toUpperCase();
		} else if ("Emision".equals(fieldName)) {
			value = emision;
		} else if ("NroRecibo".equals(fieldName)) {
			value = numero;
		} else if ("Cliente".equals(fieldName)) {
			value = cliente;
		} else if ("NroFactura".equals(fieldName)) {
			value = factura;
		} else if ("Vencimiento".equals(fieldName)) {
			value = vencimiento;
		} else if ("Importe".equals(fieldName)) {
			value = importe;
		} else if ("TotalImporte".equals(fieldName)) {
			value = Utiles.getNumberFormat(totales.get(tituloDet));
		} else if ("TotalCorriente".equals(fieldName)) {
			Double tot = totales.get(EmpresaCartera.CORRIENTE);
			if (tot == null) tot = 0.0;
			value = Utiles.getNumberFormat(tot);
		} else if ("TotalCorrienteInterior".equals(fieldName)) {
			Double tot = totales.get(EmpresaCartera.CORRIENTE_INTERIOR);
			if (tot == null) tot = 0.0;
			value = Utiles.getNumberFormat(tot);
		} else if ("TotalDudosoCobro".equals(fieldName)) {
			Double tot = totales.get(EmpresaCartera.DUDOSO_COBRO);
			if (tot == null) tot = 0.0;
			value = Utiles.getNumberFormat(tot);
		} else if ("TotalJudiciales".equals(fieldName)) {
			Double tot = totales.get(EmpresaCartera.JUDICIAL);
			if (tot == null) tot = 0.0;
			value = Utiles.getNumberFormat(tot);
		} else if ("TotalOtros".equals(fieldName)) {
			Double tot = totales.get(EmpresaCartera.OTROS);
			if (tot == null) tot = 0.0;
			value = Utiles.getNumberFormat(tot);
		} else if ("TotalSinCartera".equals(fieldName)) {
			Double tot = totales.get(EmpresaCartera.SIN_CARTERA);
			if (tot == null) tot = 0.0;
			value = Utiles.getNumberFormat(tot);
		} else if ("TotalCobrado".equals(fieldName)) {
			Double tot1 = totales.get(EmpresaCartera.CORRIENTE) != null ? totales.get(EmpresaCartera.CORRIENTE) : 0.0;
			Double tot2 = totales.get(EmpresaCartera.CORRIENTE_INTERIOR) != null ? totales.get(EmpresaCartera.CORRIENTE_INTERIOR) : 0.0;
			Double tot3 = totales.get(EmpresaCartera.DUDOSO_COBRO) != null ? totales.get(EmpresaCartera.DUDOSO_COBRO) : 0.0;
			Double tot4 = totales.get(EmpresaCartera.JUDICIAL) != null ? totales.get(EmpresaCartera.JUDICIAL) : 0.0;
			Double tot5 = totales.get(EmpresaCartera.OTROS) != null ? totales.get(EmpresaCartera.OTROS) : 0.0;
			Double tot6 = totales.get(EmpresaCartera.SIN_CARTERA) != null ? totales.get(EmpresaCartera.SIN_CARTERA) : 0.0;
			double total = tot1 + tot2 + tot3 + tot4 + tot5 + tot6;
			value = Utiles.getNumberFormat(total);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource del listado de importaciones..
 */
class ListadoImportacionesDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<BeanImportacion> values = new ArrayList<BeanImportacion>();
	List<ImportacionPedidoCompra> importaciones;
	Date desde;
	Date hasta;
	String proveedor;

	double totalfobgs = 0;
	double totalfobds = 0;
	double totalcifgs = 0;
	double totalcifds = 0;

	public ListadoImportacionesDataSource(List<ImportacionPedidoCompra> importaciones, Date desde, Date hasta, String proveedor, boolean fechaFactura) {
		this.importaciones = importaciones;
		this.desde = desde;
		this.hasta = hasta;
		this.proveedor = proveedor;
		if (fechaFactura) {
			this.loadDatosFechaFactura();
		} else {
			this.loadDatos();
		}
		Collections.sort(this.values, new Comparator<BeanImportacion>() {
			@Override
			public int compare(BeanImportacion o1, BeanImportacion o2) {
				Date val1 = o1.getFechaDespacho_();
				Date val2 = o2.getFechaDespacho_();
				int compare = val1.compareTo(val2);				
				return compare;
			}
		});
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();

		if ("Importaciones".equals(fieldName)) {
			value = this.values;
		} else if ("Desde".equals(fieldName)) {
			value = Utiles.getDateToString(this.desde, Misc.DD_MM_YYYY);
		} else if ("Hasta".equals(fieldName)) {
			value = Utiles.getDateToString(this.hasta, Misc.DD_MM_YYYY);
		} else if ("Proveedor".equals(fieldName)) {
			value = this.proveedor;
		} else if ("totfobgs".equals(fieldName)) {
			value = FORMATTER.format(this.totalfobgs);
		} else if ("totfobds".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalfobds);
		} else if ("totcifgs".equals(fieldName)) {
			value = FORMATTER.format(this.totalcifgs);
		} else if ("totcifds".equals(fieldName)) {
			value = Utiles.getNumberFormatDs(this.totalcifds);
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

	/**
	 * carga los datos para el reporte..
	 */
	private void loadDatos() {
		for (ImportacionPedidoCompra imp : this.importaciones) {
			if (imp.getResumenGastosDespacho().getFechaDespacho() != null) {
				String fecha = Utiles.getDateToString(imp.getResumenGastosDespacho().getFechaDespacho(), Misc.DD_MM_YYYY);
				String numero = imp.getNumeroPedidoCompra();
				String numeroFactura = imp.getNumeroFactura();
				String proveedor = imp.getProveedor().getRazonSocial();
				String cantidad = imp.getCantidadTotal() + "";
				String fobgs = Utiles.getNumberFormat(imp.getResumenGastosDespacho().getValorFOBgs());
				String fobds = Utiles.getNumberFormatDs(imp.getResumenGastosDespacho().getValorFOBds());
				String cifgs = Utiles.getNumberFormat(imp.getResumenGastosDespacho().getValorCIFgs());
				String cifds = Utiles.getNumberFormatDs(imp.getResumenGastosDespacho().getValorCIFds());
				String tcamb = Utiles.getNumberFormatDs(imp.getResumenGastosDespacho().getTipoCambio());
				String obser = imp.getObservacion();
				values.add(new BeanImportacion(fecha, numero, numeroFactura, cantidad, obser, proveedor, fobgs, fobds, cifgs, cifds,
						imp.getResumenGastosDespacho().getFechaDespacho(), tcamb));
				this.totalfobgs += imp.getResumenGastosDespacho().getValorFOBgs();
				this.totalfobds += imp.getResumenGastosDespacho().getValorFOBds();
				this.totalcifgs += imp.getResumenGastosDespacho().getValorCIFgs();
				this.totalcifds += imp.getResumenGastosDespacho().getValorCIFds();
			}			
		}
	}
	
	/**
	 * carga los datos para el reporte..
	 */
	private void loadDatosFechaFactura() {
		for (ImportacionPedidoCompra imp : this.importaciones) {
			String fecha = Utiles.getDateToString(imp.getFechaFactura(), Misc.DD_MM_YYYY);
			String numero = imp.getNumeroPedidoCompra();
			String numeroFactura = imp.getNumeroFactura();
			String proveedor = imp.getProveedor().getRazonSocial();
			String cantidad = imp.getCantidadTotal() + "";
			String fobgs = Utiles.getNumberFormat(imp.getResumenGastosDespacho().getValorFOBgs());
			String fobds = Utiles.getNumberFormatDs(imp.getResumenGastosDespacho().getValorFOBds());
			String cifgs = Utiles.getNumberFormat(imp.getResumenGastosDespacho().getValorCIFgs());
			String cifds = Utiles.getNumberFormatDs(imp.getResumenGastosDespacho().getValorCIFds());
			String tcamb = Utiles.getNumberFormatDs(imp.getResumenGastosDespacho().getTipoCambio());
			String obser = imp.getObservacion();
			values.add(new BeanImportacion(fecha, numero, numeroFactura, cantidad, obser, proveedor, fobgs, fobds,
					cifgs, cifds, imp.getFechaFactura(), tcamb));
			this.totalfobgs += imp.getResumenGastosDespacho().getValorFOBgs();
			this.totalfobds += imp.getResumenGastosDespacho().getValorFOBds();
			this.totalcifgs += imp.getResumenGastosDespacho().getValorCIFgs();
			this.totalcifds += imp.getResumenGastosDespacho().getValorCIFds();

		}
	}
}

/**
 * DataSource del listado de repartos detallado..
 */
class RepartosDetalladoDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	List<Reparto> repartos;
	Date desde;
	Date hasta;

	public RepartosDetalladoDataSource(List<Reparto> repartos, Date desde, Date hasta, boolean pendientes, String vendedor) {
		this.repartos = repartos;
		this.desde = desde;
		this.hasta = hasta;
		Collections.sort(this.repartos, new Comparator<Reparto>() {
			@Override
			public int compare(Reparto o1, Reparto o2) {
				Date fecha1 = (Date) o1.getFechaCreacion();
				Date fecha2 = (Date) o2.getFechaCreacion();
				return fecha1.compareTo(fecha2);
			}
		});
		try {
			this.loadDatos(pendientes, vendedor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);

		if ("TituloDetalle".equals(fieldName)) {
			value = det[0];
		} if ("NroFactura".equals(fieldName)) {
			value = det[1];
		} if ("Cliente".equals(fieldName)) {
			value = det[2];
		} if ("Codigo".equals(fieldName)) {
			value = det[3];
		} if ("Descripcion".equals(fieldName)) {
			value = det[4];
		}  if ("Cantidad".equals(fieldName)) {
			value = det[5];
		} if ("Entrega".equals(fieldName)) {
			value = det[6];
		} if ("Saldo".equals(fieldName)) {
			value = det[7];
		} if ("Vendedor".equals(fieldName)) {
			value = det[8];
		} if ("NroReparto".equals(fieldName)) {
			value = det[9];
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
	
	/**
	 * load datos..
	 */
	private void loadDatos(boolean pendientes, String vendedor) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Reparto reparto : this.repartos) {
			for (RepartoDetalle det : reparto.getDetalles()) {
				if (det.isVenta()) {
					Venta vta = (Venta) rr.getObject(Venta.class.getName(), det.getIdMovimiento());
					if (vta != null) {
						for (RepartoEntrega item : det.getEntregas()) {
							long saldo_ = item.getSaldo();
							String fecha = Utiles.getDateToString(reparto.getFechaCreacion(), "dd/MM/yyyy");
							String repartidor = reparto.getRepartidor().getRazonSocial();
							String nroRep = reparto.getNumero() + " - " + fecha + " - " + repartidor;
							String nroVta = vta.getNumero();
							String cliente = vta.getCliente().getRazonSocial();
							String vendedor_ = vta.getVendedor().getRazonSocial();
							String codigo = item.getDetalle().getArticulo().getCodigoInterno();
							String descripcion = item.getDetalle().getArticulo().getDescripcion();
							String cantidad = item.getDetalle().getCantidad() + "";
							String entrega = item.getCantidad() + "";
							String saldo = saldo_ + "";
							String nroReparto = reparto.getNumero();
							if (!pendientes) {
								if ((vendedor == null) || (vendedor != null && vendedor.equals(vendedor_))) {
									this.values.add(new Object[] { nroRep, nroVta, cliente, codigo, descripcion, cantidad, entrega, saldo, vendedor_, nroReparto });
								}								
							} else if (pendientes && saldo_ > 0) {
								if ((vendedor == null) || (vendedor != null && vendedor.equals(vendedor_))) {
									this.values.add(new Object[] { nroRep, nroVta, cliente, codigo, descripcion, cantidad, entrega, saldo, vendedor_, nroReparto });
								}								
							}							
						}
					}
				}
			}
		}
	}
}

/**
 * Reporte de pagos..
 */
class ReportePagos extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 25);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Recibo", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Fecha Rec.", TIPO_STRING, 25);
	static DatosColumnas col4 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Importe", TIPO_DOUBLE, 30, true);

	public ReportePagos(Date desde, Date hasta, String proveedor) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("Proveedores");
		this.setNombreArchivo("pago-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * DataSource del listado de clientes por vendedor..
 */
class ClientesVendedorDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	Misc misc = new Misc();

	List<ClienteBean> values = new ArrayList<ClienteBean>();
	List<Object[]> datos;

	public ClientesVendedorDataSource(List<Object[]> datos) {
		this.datos = datos;
		this.loadDatos();
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();

		if ("Clientes".equals(fieldName)) {
			value = this.values;
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

	/**
	 * carga los datos para el reporte..
	 */
	private void loadDatos() {
		for (Object[] dato : datos) {
			String ruc = (String) dato[0];
			String razonSocial = (String) dato[1];
			String direccion = (String) dato[2];
			String telefono = (String) dato[3];
			String rubro = (String) dato[5];
			Double limiteCredito = (Double) dato[6];
			if (limiteCredito == null) {
				limiteCredito = (double) 0;				
			}
			String ciudad = (String) dato[7];
			this.values.add(new ClienteBean(ruc, razonSocial, direccion, telefono, rubro,
					Utiles.getNumberFormat(limiteCredito), ciudad));
		}
	}
}

/**
 * DataSource del servicio tecnico..
 */
class ServicioTecnicoDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	List<Reparto> repartos;
	List<ServicioTecnico> serviciosTecnicos;
	Date desde;
	Date hasta;

	public ServicioTecnicoDataSource(List<ServicioTecnico> serviciosTecnicos, Date desde, Date hasta) {
		this.serviciosTecnicos = serviciosTecnicos;
		this.desde = desde;
		this.hasta = hasta;
		Collections.sort(this.serviciosTecnicos, new Comparator<ServicioTecnico>() {
			@Override
			public int compare(ServicioTecnico o1, ServicioTecnico o2) {
				Date fecha1 = (Date) o1.getFecha();
				Date fecha2 = (Date) o2.getFecha();
				return fecha1.compareTo(fecha2);
			}
		});
		try {
			this.loadDatos();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);

		if ("TituloDetalle".equals(fieldName)) {
			value = det[0];
		} if ("NroFactura".equals(fieldName)) {
			value = det[1];
		} if ("Codigo".equals(fieldName)) {
			value = det[2];
		} if ("Diagnostico".equals(fieldName)) {
			value = det[3];
		} if ("Rep".equals(fieldName)) {
			value = det[4];
		} if ("NroServicio".equals(fieldName)) {
			value = det[5];
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
	
	/**
	 * load datos..
	 */
	private void loadDatos() throws Exception {
		for (ServicioTecnico stec : this.serviciosTecnicos) {
			for (ServicioTecnicoDetalle stdet : stec.getDetalles()) {
				String nroSte = stec.getNumero() + " - " + Utiles.getDateToString(stec.getFecha(), Utiles.DD_MM_YYYY);
				String nroVta = stdet.getNumeroFactura();
				String codigo = stdet.getArticulo().getCodigoInterno();
				String reposicion = stdet.isVerifica_reposicion() ? "SI" : "NO";
				String diagnostico = stdet.getDiagnostico().toUpperCase();
				String nroServicio = stec.getNumero();
				this.values.add(new Object[] { nroSte, nroVta, codigo, diagnostico, reposicion, nroServicio  });
			}
		}
	}
}

/**
 * Reporte de marcaciones..
 */
class ReporteMarcaciones extends ReporteYhaguy {
	
	private Date desde;
	private Date hasta;
	String codigoReporte;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col2 = new DatosColumnas("Día", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Tipo", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Marcación", TIPO_STRING, 20);
	static DatosColumnas col5 = new DatosColumnas("Retraso", TIPO_STRING, 20);
	static DatosColumnas col6 = new DatosColumnas("Adelanto", TIPO_STRING, 20);
	static DatosColumnas col7 = new DatosColumnas("Funcionario", TIPO_STRING);	

	public ReporteMarcaciones(Date desde, Date hasta, String codigoReporte) {
		this.desde = desde;
		this.hasta = hasta;
		this.codigoReporte = codigoReporte;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);		
		cols.add(col4);	
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo(this.codigoReporte + " - Listado de Marcaciones");
		this.setDirectorio("funcionarios");
		this.setNombreArchivo("rrhh-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Horario Entrada", RRHHMarcaciones.ENTRADA))
				.add(this.textoParValor("Horario Salida", RRHHMarcaciones.SALIDA))
				.add(this.textoParValor("Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

