package com.yhaguy.gestion.reportes;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.AjusteStockDetalle;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloGasto;
import com.yhaguy.domain.ArticuloHistorialMigracion;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.ArticuloListaPrecioDetalle;
import com.yhaguy.domain.ArticuloPrecioMinimo;
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
import com.yhaguy.domain.ContableAsiento;
import com.yhaguy.domain.ContableAsientoDetalle;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.CtaCteEmpresaMovimiento_2016;
import com.yhaguy.domain.CtaCteLineaCredito;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.GastoDetalle;
import com.yhaguy.domain.HistoricoBloqueoClientes;
import com.yhaguy.domain.HistoricoComisiones;
import com.yhaguy.domain.HistoricoMovimientos;
import com.yhaguy.domain.ImportacionFacturaDetalle;
import com.yhaguy.domain.ImportacionPedidoCompra;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.ProveedorArticulo;
import com.yhaguy.domain.RecaudacionCentral;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Reporte;
import com.yhaguy.domain.ReporteFavoritos;
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
import com.yhaguy.gestion.comun.ControlArticuloCosto;
import com.yhaguy.gestion.contabilidad.BeanLibroVenta;
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

	static final MyArray TESORERIA = new MyArray("Tesorería", ID_TESORERIA, "z-icon-briefcase");
	static final MyArray COMPRAS = new MyArray("Compras", ID_COMPRAS, "z-icon-shopping-cart");
	static final MyArray VENTAS = new MyArray("Ventas", ID_VENTAS, "z-icon-tags");
	static final MyArray STOCK = new MyArray("Stock", ID_STOCK, "z-icon-archive");
	static final MyArray LOGISTICA = new MyArray("Logística", ID_LOGISTICA, "z-icon-truck");
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
	
	static final String GRUPO_COMPRAS_COMPRAS = " COMPRAS";
	
	static final String GRUPO_VENTAS_UTILIDAD = " UTILIDAD";
	static final String GRUPO_VENTAS_VENDEDOR = " VENDEDORES";
	static final String GRUPO_VENTAS_PROVEEDOR = " PROVEEDORES";
	static final String GRUPO_VENTAS_CLIENTES = " CLIENTES";
	static final String GRUPO_VENTAS_ARTICULOS = " ARTICULOS";
	static final String GRUPO_VENTAS_AUDITORIA = " AUDITORIA";
	static final String GRUPO_VENTAS_NOTACREDITO = " NOTAS DE CREDITO";
	
	static final String GRUPO_STOCK_ARTICULOS = " ARTICULOS";	
	static final String GRUPO_LOGISTICA_LOGISTICA = " LOGISTICA";
	static final String GRUPO_CONTABILIDAD_CONTABILIDAD = " CONTABILIDAD";
	static final String GRUPO_CONTABILIDAD_CIERRE_PERIODO = " CIERRE-PERIODO";
	static final String GRUPO_RRHH_RRHH = " RR.HH";
	static final String GRUPO_SISTEMA_SISTEMA = " SISTEMA";
	static final String GRUPO_FAVORITOS_FAVORITOS = " FAVORITOS";

	private MyArray selectedItem = TESORERIA;
	private MyArray selectedReporte;
	private String selectedGrupo = GRUPO_TESORERIA_CAJAS;

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
		this.pass = Utiles.encriptar(this.pass, true);
		if (rr.getUsuario(this.user, this.pass) != null) {
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
			new ReportesDeLogistica(this.getCodigoReporte());
			break;

		case ID_CONTABILIDAD:
			new ReportesDeContabilidad(this.getCodigoReporte(), false);
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
				new ReportesDeLogistica(cod);
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
				this.listadoStockValorizado();
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
				this.stockValorizadoAunaFecha();
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

				List<Transferencia> transferencias = rr
						.getTransferenciasExternas(desde, hasta,
								origen.getId(), destino.getId());

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
		private void listadoStockValorizado() {
			try {
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<ArticuloDeposito> articulos = rr.getArticulosPorDeposito((long) 2);
				double total = 0;

				for (ArticuloDeposito art : articulos) {
					double costo = art.getArticulo().getCostoGs();
					Object[] cmp = new Object[] {
							art.getArticulo().getCodigoInterno(),
							art.getArticulo().getDescripcion(), costo,
							art.getStock(), (costo * art.getStock()) };
					if (art.getStock() != 0 && costo != 0
							&& !art.getArticulo().isServicio()
							&& !art.getArticulo().getCodigoInterno().startsWith("@")) {
						data.add(cmp);
						total += (costo * art.getStock());
					}
				}

				ReporteStockValorizado rep = new ReporteStockValorizado(total);
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

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> historico;
				List<Object[]> historicoEntrada;
				List<Object[]> historicoSalida;

				long idArticulo = articulo.getId();
				long idSucursal = getAcceso().getSucursalOperativa().getId();

				List<Object[]> ventas = rr.getVentasPorArticulo(idArticulo, idDeposito, desde, hasta);
				List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(idArticulo, idDeposito, desde, hasta);
				List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(idArticulo, idDeposito, desde, hasta);
				List<Object[]> compras = rr.getComprasLocalesPorArticulo_(idArticulo, idDeposito, desde, hasta);
				List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(idArticulo, idDeposito, desde, hasta);
				List<Object[]> transfs = rr.getTransferenciasPorArticulo(idArticulo, idDeposito, desde, hasta, true);
				List<Object[]> transfs_ = rr.getTransferenciasPorArticulo(idArticulo, idDeposito, desde, hasta, false);
				List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(idArticulo, idDeposito, desde, hasta, idSucursal, Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
				List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(idArticulo, idDeposito, desde, hasta, idSucursal, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
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
				Tipo familia = filtro.getFamilia();
				Tipo marca = filtro.getMarca();
				Proveedor proveedor = filtro.getProveedorExterior();
				Articulo articulo = filtro.getArticulo();
				List<Deposito> depositos = filtro.getDepositos();
				
				if (depositos.isEmpty()) {
					Clients.showNotification("Debe seleccionar al menos un depósito..", 
							Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				long idFamilia = familia == null? 0 : familia.getId();
				long idMarca = marca == null? 0 : marca.getId();
				long idArticulo = articulo == null? 0 : articulo.getId();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				Map<String, Object[]> values = new HashMap<String, Object[]>();
				
				List<Articulo> articulos = rr.getArticulos(idFamilia, idMarca, idArticulo);
				
				for (Articulo art : articulos) {

					if (proveedor != null) {
						for (ProveedorArticulo provArt : art.getProveedorArticulos()) {
							if (provArt.getProveedor().getId().longValue() == proveedor.getId().longValue()) {
								for (Deposito deposito : depositos) {
									ArticuloDeposito adp = rr.getArticuloDeposito(art.getId(), deposito.getId());
									long stock = adp == null ? 0 : adp.getStock();
									Object[] value = values.get(art.getCodigoInterno());
									if (value != null) {
										long cant = (long) value[0];
										cant += stock;
										values.put(art.getCodigoInterno(), 
												new Object[] { cant, art.getDescripcion(), art.getCostoGs(), 
											art.getArticuloFamilia().getDescripcion().toUpperCase() });
									} else {
										values.put(art.getCodigoInterno(),
												new Object[] { stock, art.getDescripcion(), art.getCostoGs(),
											art.getArticuloFamilia().getDescripcion().toUpperCase()});
									}
								}
							}
						}
					} else {
						for (Deposito deposito : depositos) {
							ArticuloDeposito adp = rr.getArticuloDeposito(art.getId(), deposito.getId());
							long stock = adp == null ? 0 : adp.getStock();
							Object[] value = values.get(art.getCodigoInterno());
							if (value != null) {
								long cant = (long) value[0];
								cant += stock;
								values.put(art.getCodigoInterno(), new Object[] { cant, art.getDescripcion(), art.getCostoGs(), 
									art.getArticuloFamilia().getDescripcion().toUpperCase() });
							} else {
								values.put(art.getCodigoInterno(),
										new Object[] { stock, art.getDescripcion(), art.getCostoGs(), 
									art.getArticuloFamilia().getDescripcion().toUpperCase() });
							}
						}
					}
				}	
				
				for (String key : values.keySet()) {
					Object[] value = values.get(key);
					long stock = (long) value[0];
					double costo = (double) value[2];
					data.add(new Object[] { key, value[1] , value[3], stock, Utiles.getRedondeo(costo), Utiles.getRedondeo(costo * stock) });
				}

				String suc = getAcceso().getSucursalOperativa().getText();		
				String familia_ = familia == null ? "TODOS.." : familia.getDescripcion();
				String marca_ = marca == null ? "TODOS.." : marca.getDescripcion();
				String proveedor_ = proveedor == null ? "TODOS.." : proveedor.getRazonSocial(); 
				String articulo_ = articulo == null ? "TODOS.." : articulo.getDescripcion();
				String deposito_ = "";
				
				for (Deposito deposito : depositos) {
					deposito_ += deposito.getDescripcion() + " / ";
				}
				
				ReporteExistenciaArticulos rep = new ReporteExistenciaArticulos(
						suc, familia_, marca_, proveedor_, articulo_, deposito_);
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
		@SuppressWarnings("unchecked")
		private void stockValorizadoAunaFecha() throws Exception {
			try {
				Date desde = filtro.getFechaInicioOperaciones();
				Date hasta = filtro.getFechaHasta();
				Articulo articulo = filtro.getArticulo();
				String tipoCosto = filtro.getTipoCosto();
				
				if (hasta == null) hasta = new Date();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Articulo> arts = new ArrayList<Articulo>();
				
				if (articulo != null) {
					arts.add(articulo);
				} else {
					arts = rr.getArticulos();
				}

				for (Articulo art : arts) {
					if (!art.getCodigoInterno().startsWith("@")) {
						List<Object[]> historicoEntrada = new ArrayList<Object[]>();
						List<Object[]> historicoSalida = new ArrayList<Object[]>();
						long stockEntrada = 0;
						long stockSalida = 0;
						
						List<Object[]> ventas = rr.getVentasPorArticulo(art.getId(), desde, hasta);
						List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(art.getId(), desde, hasta);
						List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(art.getId(), desde, hasta);
						List<Object[]> compras = rr.getComprasLocalesPorArticulo(art.getId(), desde, hasta);
						List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(art.getId(), desde, hasta);
						List<Object[]> transfs = rr.getTransferenciasPorArticulo(art.getId(), desde, hasta);
						List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(art.getId(), desde, hasta, 2, Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
						List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(art.getId(), desde, hasta, 2, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
						List<Object[]> migracion = rr.getMigracionPorArticulo(art.getCodigoInterno(), desde, hasta, 2);
						
						historicoEntrada = new ArrayList<Object[]>();
						historicoEntrada.addAll(migracion);
						historicoEntrada.addAll(ajustStockPost);		
						historicoEntrada.addAll(ntcsv);
						historicoEntrada.addAll(compras);
						historicoEntrada.addAll(importaciones);
						
						historicoSalida = new ArrayList<Object[]>();
						historicoSalida.addAll(ajustStockNeg);
						historicoSalida.addAll(ventas);
						historicoSalida.addAll(ntcsc);
						
						for (Object[] transf : transfs) {
							long idsuc = (long) transf[6];
							if(idsuc == 2) {
								historicoSalida.add(transf);
							} else {				
								historicoEntrada.add(transf);
							}
						}
						
						for(Object[] obj : historicoEntrada){
							stockEntrada += Long.parseLong(String.valueOf(obj[3]));
						}
						
						for(Object[] obj : historicoSalida){
							long cantidad = Long.parseLong(String.valueOf(obj[3]));
							if(cantidad < 0)
								cantidad = cantidad * -1;
							stockSalida += cantidad;
						}
						
						long stock = stockEntrada - stockSalida;
						double costo  = tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO) ? 
								ControlArticuloCosto.getCostoPromedio(art.getId(), hasta) : ControlArticuloCosto.getCostoUltimo(art.getId(), hasta);
						
						data.add(new Object[] { art.getCodigoInterno(), art.getDescripcion(), stock, costo, (stock * costo) });
					}
				}
				
				String desc = articulo == null ? "TODOS.." : articulo.getCodigoInterno();
				ReporteStockValorizadoAunaFecha rep = new ReporteStockValorizadoAunaFecha(hasta, desc, tipoCosto);
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
				this.cobranzasPorVendedor(mobile);
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
				this.totalCobranzasPorVendedor(mobile);
				break;
				
			case VENTAS_CLIENTES_MES:
				this.ventasClientesPorMes(mobile);
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
				this.ventasUtilidadDetallado(mobile, false);
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
				this.clientesPorVendedor(mobile);
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
				this.ventasUtilidadDetallado(mobile, true);
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

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				double totalImporte = 0;
				
				Map<String, Object[]> values = new HashMap<String, Object[]>();
				Map<String, Object[]> values_ = new HashMap<String, Object[]>();

				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
				Map<String, List<NotaCredito>> ncs_vendedor = new HashMap<String, List<NotaCredito>>();
				
				//Notas de Credito..
				if (filtro.isIncluirNCR() || filtro.isIncluirNCR_CRED()) {
					if (vendedor == null) {
						for (Funcionario vend : filtro.getVendedores()) {
							for (NotaCredito nc : ncs) {
								long idVen = vend.getId().longValue();
								long idVenNc = nc.getVendedor().getId().longValue();
								if (idVen == idVenNc) {
									List<NotaCredito> ncs_ = ncs_vendedor.get(vend.getRazonSocial());
									if (ncs_ == null) {
										ncs_ = new ArrayList<NotaCredito>();
									}
									if (filtro.isIncluirNCR()
											&& nc.isNotaCreditoVentaContado()) {
										ncs_.add(nc);
									} else if (filtro.isIncluirNCR_CRED()
											&& !nc.isNotaCreditoVentaContado()) {
										ncs_.add(nc);
									}									
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
							if (ncs_ == null) {
								ncs_ = new ArrayList<NotaCredito>();
							}
							if (filtro.isIncluirNCR()
									&& nc.isNotaCreditoVentaContado()) {
								ncs_.add(nc);
							} else if (filtro.isIncluirNCR_CRED()
									&& !nc.isNotaCreditoVentaContado()) {
								ncs_.add(nc);
							}
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
				
				// Ventas Contado y Credito..
				if (filtro.isIncluirVCR() && filtro.isIncluirVCT()) {
					List<Venta> ventas = new ArrayList<Venta>();
					if (vendedor == null) {
						for (Funcionario vend : filtro.getVendedores()) {
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
					
				} else if (filtro.isIncluirVCR()) {
					List<Venta> ventas = new ArrayList<Venta>();
					if (vendedor == null) {
						for (Funcionario vend : filtro.getVendedores()) {
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
					
				} else if (filtro.isIncluirVCT()) {
					List<Venta> ventas = new ArrayList<Venta>();
					if (vendedor == null) {
						for (Funcionario vend : filtro.getVendedores()) {
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
				params.put("Titulo", "Ventas por Vendedor / Proveedor");
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
		public void cobranzasPorVendedor(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				Proveedor proveedor = filtro.getProveedorExterior();
				long idVendedor = vendedor != null ? vendedor.getId() : 0;

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<Object[]> cobranzas = rr.getCobranzasPorVendedor(desde, hasta, idVendedor, getIdSucursal());

				for (Object[] cobro : cobranzas) {
					Recibo rec = (Recibo) cobro[0];
					String fac = (String) cobro[1];
					ReciboDetalle det = (ReciboDetalle) cobro[3];
					double montoGs = (double) cobro[2];
					if (proveedor != null) {
						montoGs = det.getImporteByProveedor(proveedor.getId());
						NotaCredito nc = rr.getNotaCreditoVenta(det.getVenta().getId());
						if (nc != null && nc.isMotivoDescuento()) {
							double importeNcr = 0;
							int cantidad = det.getVenta().getCantidadItemsByProveedor(proveedor.getId());
							double monto = (nc.getImporteGs() / det.getVenta().getDetalles().size()) * cantidad;
							importeNcr += (monto);
							montoGs = montoGs - importeNcr;
						}						
					} 
					Object[] cob = new Object[] {
							m.dateToString(rec.getFechaEmision(),
									Misc.DD_MM_YYYY), rec.getNumero(), fac,
							montoGs };
					data.add(cob);
				}
				String proveedor_ = proveedor == null ? "TODOS.." : proveedor.getRazonSocial(); 
				String vendedor_ = vendedor == null ? "TODOS.." : vendedor.getRazonSocial();
				
				ReporteCobranzasPorVendedor rep = new ReporteCobranzasPorVendedor(
						desde, hasta, vendedor_, proveedor_);
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

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<NotaCredito> notasCredito = rr.getNotasCreditoVenta(desde,
						hasta, 0);

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
				
				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<Object[]> ncs = rr.getNotasCreditoVenta_(desde, hasta, 0);
				for (Object[] notacred : ncs) {
					long idNcred = (long) notacred[0];
					String numero = (String) notacred[1];
					Date fecha = (Date) notacred[2];
					String descrMotivo = (String) notacred[3];
					String siglaMotivo = (String) notacred[4];
					if (siglaMotivo.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_DEVOLUCION)) {
						String motivo = descrMotivo.substring(0, 3).toUpperCase() + ".";
						List<Object[]> dets = rr.getNotaCreditoDetalles(idNcred);
						double costo = 0;
						if (tipoCosto.equals(ReportesFiltros.COSTO_ULTIMO)) {
							for (Object[] det : dets) {
								double cost = (double) det[0];
								int cant = (int) det[1];
								costo += (cost * cant);
							}
						}
						if (tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO)) {
							for (Object[] det : dets) {
								long idArt = (long) det[2];
								double cost = ControlArticuloCosto.getCostoPromedio(idArt, hasta);
								if(cost == 0) cost = (double) det[0];
								int cant = (int) det[1];
								costo += (cost * cant);
							}
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
					long idvta = (long) venta[0];
					String numero = (String) venta[1];
					Date fecha = (Date) venta[2];
					String sigla = (String) venta[4];
					List<Object[]> dets = rr.getVentaDetalles(idvta);
					double costo = 0;
					if (tipoCosto.equals(ReportesFiltros.COSTO_ULTIMO)) {
						for (Object[] det : dets) {
							double cost = (double) det[0];
							long cant = (long) det[1];
							costo += (cost * cant);
						}
					}
					if (tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO)) {
						for (Object[] det : dets) {
							long idArt = (long) det[2];
							double cost = ControlArticuloCosto.getCostoPromedio(idArt, hasta);
							if(cost == 0) cost = (double) det[0];
							long cant = (long) det[1];
							costo += (cost * cant);
						}
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
											item.getArticulo().getArticuloMarca().getDescripcion().toUpperCase(),
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
				Tipo familia = filtro.getFamilia();
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

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

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
									long cantn = (long) art.getPos2();
									art.setPos2(cantn + item.getCantidad());
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
					long cantn = (long) value.getPos2();
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
		private void totalCobranzasPorVendedor(boolean mobile) {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> cobros = rr.getCobranzasPorVendedor(desde, hasta, 0, getIdSucursal());
				Map<Long, Double> values = new HashMap<Long, Double>();

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
				
				for (Funcionario vendedor : filtro.getVendedores()) {
					Double cobrado = values.get(vendedor.getId());
					if(cobrado != null) 
						data.add(new Object[]{ vendedor.getRazonSocial().toUpperCase(), cobrado });
				}

				ReporteTotalCobranzas rep = new ReporteTotalCobranzas(desde,
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
		 * VEN-00019 ventas por clientes por mes..
		 */
		private void ventasClientesPorMes(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE PARA VERSION MOVIL..");
				return;
			}
			
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Funcionario vendedor = filtro.getVendedor();
				Tipo rubro = filtro.getRubro();
				boolean ivaInc = filtro.isIvaIncluido();
				boolean ncrInc = filtro.isIncluirNCR();
				Object[] formato = filtro.getFormato();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Venta> ventas = rr.getVentas(desde, hasta, 0);
				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
				List<Object[]> data = new ArrayList<Object[]>();
				Map<String, Object[]> values = new HashMap<String, Object[]>();
				Map<String, Object[]> values_ = new HashMap<String, Object[]>();
				
				for (Venta venta : ventas) {
					int mes = Utiles.getNumeroMes(venta.getFecha()) - 1;
					if (vendedor == null || vendedor.getId().longValue() == venta.getVendedor().getId().longValue()) {
						if (rubro == null || rubro.getDescripcion().equals(venta.getCliente().getRubro())) {
							if (!venta.isAnulado()) {
								String id = venta.getCliente().getId() + "";
								String key = id + "-" + mes;
								String cliente = venta.getCliente().getRazonSocial();
								String vendedor_ = venta.getVendedor().getRazonSocial();
								String rubro_ = venta.getCliente().getRubro();
								Object[] acum = values.get(key);
								if (acum != null) {
									double importe = (double) acum[0];
									double importe_ = ivaInc ? venta.getTotalImporteGs() : venta.getTotalImporteGsSinIva();
									importe += importe_;
									values.put(key, new Object[] { importe, mes, cliente, vendedor_, rubro_ });
								} else {
									double importe_ = ivaInc ? venta.getTotalImporteGs() : venta.getTotalImporteGsSinIva();
									values.put(key,
											new Object[] { importe_, mes, cliente, vendedor_, rubro_ });
								}
							}
						}						
					}					
				}
				
				if (ncrInc) {
					for (NotaCredito ncred : ncs) {
						int mes = Utiles.getNumeroMes(ncred.getFechaEmision()) - 1;
						if (vendedor == null || vendedor.getId().longValue() == ncred.getVendedor().getId().longValue()) {
							if (rubro == null || rubro.getDescripcion().equals(ncred.getCliente().getRubro())) {
								if (!ncred.isAnulado()) {
									String id = ncred.getCliente().getId() + "";
									String key = id + "-" + mes;
									String cliente = ncred.getCliente().getRazonSocial();
									String vendedor_ = ncred.getVendedor().getRazonSocial();
									String rubro_ = ncred.getCliente().getRubro();
									Object[] acum = values.get(key);
									if (acum != null) {
										double importe = (double) acum[0];
										double importe_ = ivaInc ? ncred.getImporteGs() : ncred.getTotalImporteGsSinIva();
										importe -= importe_;
										values.put(key, new Object[] { importe, mes, cliente, vendedor_, rubro_ });
									} else {
										double importe_ = ivaInc ? ncred.getImporteGs() : ncred.getTotalImporteGsSinIva();
										values.put(key,
												new Object[] { importe_ * -1, mes, cliente, vendedor_, rubro_ });
									}
								}
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
					
					Object[] value_ = values_.get(id);
					if (value_ != null) {
						value_[mes] = importe;
						values_.put(id, value_);
					} else {
						Object[] datos = new Object[] { (double) 0, (double) 0,
								(double) 0, (double) 0, (double) 0, (double) 0,
								(double) 0, (double) 0, (double) 0, (double) 0,
								(double) 0, (double) 0, vend, rubro_ };
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
							value_[9], value_[10], value_[11], total, value_[12], value_[13] });
				}
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_POR_CLIENTES;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new VentasPorClienteDataSource(data);
				params.put("Titulo", "Ventas por Clientes por Mes");
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
		 * reporte VEN-00023
		 */
		private void ventasUtilidadDetallado(boolean mobile, boolean resumido) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}			
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Articulo art = filtro.getArticulo();
				Cliente cliente = filtro.getCliente();
				Object[] formato = filtro.getFormato();
				
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
								Utiles.getDateToString(notacred.getFechaEmision(), "dd-MM-yyyy"),
								notacred.getNumero(),
								notacred.isNotaCreditoVentaContado() ? "NC-CO " + motivo : "NC-CR " + motivo,
								notacred.getCliente().getRazonSocial().toUpperCase(),
								notacred.getCliente().getRubro().toUpperCase(),
								notacred.getVendedor().getRazonSocial().toUpperCase(),
								item.getArticulo().getCodigoInterno(),
								item.getArticulo().getArticuloMarca().getDescripcion().toUpperCase(),
								item.getArticulo().getArticuloFamilia().getDescripcion().toUpperCase(),
								notacred.isAnulado() || !notacred.isMotivoDevolucion() ? 0.0 : item.getCostoGs() * -1,
								notacred.isAnulado() || !notacred.isMotivoDevolucion() ? (long) 0 : Long.parseLong(item.getCantidad() + ""),
								notacred.isAnulado() || !notacred.isMotivoDevolucion() ? 0.0 : item.getCostoTotalGsSinIva() * -1,
								notacred.isAnulado() ? 0.0 : item.getImporteGsSinIva() * -1,
								notacred.isAnulado() ? 0.0 : item.getRentabilidad() * -1,
								item.getArticulo().getDescripcion()};
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
								"DESCUENTO CONCEDIDO" };
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
								Utiles.getDateToString(venta.getFecha(), "dd-MM-yyyy"),
								venta.getNumero(),
								"FAC. " + venta.getCondicionPago().getDescripcion().substring(0, 3).toUpperCase(),
								venta.getCliente().getRazonSocial().toUpperCase(),
								venta.getCliente().getRubro().toUpperCase(),
								venta.getVendedor().getRazonSocial().toUpperCase(),
								item.getArticulo().getCodigoInterno(),
								item.getArticulo().getArticuloMarca().getDescripcion().toUpperCase(),
								item.getArticulo().getArticuloFamilia().getDescripcion().toUpperCase(),
								venta.isAnulado() ? 0.0 : item.getCostoUnitarioGs(),
								venta.isAnulado() ? (long) 0 : item.getCantidad(),
								venta.isAnulado() ? 0.0 : item.getCostoTotalGsSinIva(),
								venta.isAnulado() ? 0.0 : item.getImporteGsSinIva() - item.getDescuentoUnitarioGsSinIva(),
								venta.isAnulado() ? 0.0 : item.getRentabilidad(),
								item.getArticulo().getDescripcion()};
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
				double utilidad = totalImporte - totalCosto;
				double promedioSobreCosto = Utiles.obtenerPorcentajeDelValor(utilidad, totalCosto);
				double promedioSobreVenta = Utiles.obtenerPorcentajeDelValor(utilidad, totalImporte);
				
				if (!resumido) {
					String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_UTILIDAD_DETALLADO;
					if (!formato[0].equals("PDF")) {
						source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_VENTAS_UTILIDAD_DETALLADO_SIN_CAB;
					}
					Map<String, Object> params = new HashMap<String, Object>();
					JRDataSource dataSource = new VentasUtilidadDetallado(data);
					params.put("Titulo", "Ventas y Utilidad por Articulo Detallado");
					params.put("Usuario", getUs().getNombre());
					params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
					params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
					params.put("Promedio", Utiles.getNumberFormat(promedioSobreCosto));
					params.put("TOT_VTA_NETA", Utiles.getNumberFormat(totalImporte));
					params.put("TOT_COSTO", Utiles.getNumberFormat(totalCosto));
					params.put("TOT_UTILIDAD", Utiles.getNumberFormat((totalImporte - totalCosto)));
					params.put("TOT_MARGEN_VTA", Utiles.getNumberFormat(promedioSobreVenta));
					params.put("TOT_MARGEN_COSTO", Utiles.getNumberFormat(promedioSobreCosto));
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
				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0, idVendedor);
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

				ReporteVentasGenerico rep = new ReporteVentasGenerico(
						totalSinIva, desde, hasta, vendedor_, "TODOS..", sucursal, "TODOS..");
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
		private void clientesPorVendedor(boolean mobile) {
			try {
				Funcionario vendedor = filtro.getVendedor();
				Cliente cliente = filtro.getCliente();
				
				String vendedor_ = vendedor == null ? "TODOS.." : vendedor.getRazonSocial();
				long idVendedor = vendedor == null ? 0 : vendedor.getId();
				
				String cliente_ = cliente == null ? "TODOS.." : cliente.getRazonSocial();
				//long idCliente = cliente == null ? 0 : cliente.getId();
				
				if (vendedor == null) {
					Clients.showNotification("Debe seleccionar un vendedor..", 
							Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}

				RegisterDomain rr = RegisterDomain.getInstance();				
				List<Object[]> emps = rr.getClientesPorVendedor(idVendedor);

				ReporteClientesVendedor rep = new ReporteClientesVendedor(vendedor_, cliente_);
				rep.setApaisada();
				rep.setDatosReporte(emps);
				
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
				}

				double totalSinIva = totalImporte
						- m.calcularIVA(totalImporte, 10);
				String sucursal = getAcceso().getSucursalOperativa().getText();

				ReporteVentasGenerico rep = new ReporteVentasGenerico(
						totalSinIva, desde, hasta, "TODOS..", cliente_, sucursal, "TODOS..");
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
		private void ventasPorFamilia(boolean mobile) {
			try {
				
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				
				Map<String, Double> acum = new HashMap<String, Double>();
				List<Object[]> data = new ArrayList<Object[]>();
				
				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				
				List<Venta> ventas = rr.getVentas(desde, hasta, 0);
				for (Venta venta : ventas) {
					if (!venta.isAnulado()) {
						for (VentaDetalle item : venta.getDetalles()) {
							Tipo flia = item.getArticulo().getArticuloFamilia();
							if (flia != null) {
								String desc = flia.getDescripcion();
								Double total = acum.get(desc);
								if (total != null) {
									total += Utiles.getRedondeo(item.getImporteGs());
									acum.put(desc, total);
								} else {
									acum.put(desc, Utiles.getRedondeo(item.getImporteGs()));
								}
							}
						}
					}
				}
				
				List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
				for (NotaCredito nc : ncs) {
					if (!nc.isAnulado()) {
						for (NotaCreditoDetalle item : nc.getDetallesArticulos()) {
							Tipo flia = item.getArticulo().getArticuloFamilia();
							if (flia != null) {
								String desc = flia.getDescripcion();
								Double total = acum.get(desc);
								if (total != null) {
									total -= Utiles.getRedondeo(item.getImporteGs());
									acum.put(desc, total);
								} else {
									acum.put(desc, Utiles.getRedondeo(item.getImporteGs()) * -1);
								}
							}
						}
					}				
				}
				
				for (String key : acum.keySet()) {
					double total = acum.get(key);
					data.add(new Object[] { key, total });
				}

				ReporteVentasPorFamilia rep = new ReporteVentasPorFamilia(desde, hasta, getSucursal());
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
	}
	
	/**
	 * Limpia los atributos e inicializa los valores por defecto..
	 */
	public void inicializarFiltros() {
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
		this.filtro.setTodos(false);
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
		this.filtro.setDepositos(new ArrayList<Deposito>());
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
				this.saldosCtaCteDetallado(true, mobile);
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
				this.saldosCtaCteDetallado(false, mobile);
				break;

			case PAGOS_POR_PROVEEDOR:
				this.pagosPorProveedor();
				break;

			case CHEQUES_A_VENCER:
				this.cheques_a_vencer(vm, mobile);
				break;

			case CHEQUES_POR_CLIENTE:
				this.chequesPorCliente(mobile);
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
				this.cobranzas(mobile);
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
				this.clientesPorRubro(mobile);
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
				this.historialMovimientosClientes(mobile, false);
				break;
				
			case HISTORIAL_SALDOS_CLIENTES_RESUMIDO:
				this.historialMovimientosClientes(mobile, true);
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
				recibos = rr.getCobranzas(desde, hasta, 0);
				ventas = rr.getVentasContado(desde, hasta, 0);
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
		private void saldosCtaCteDetallado(boolean cliente, boolean mobile) throws Exception {
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
			Tipo rubro = filtro.getRubro();
			Tipo moneda = filtro.getMoneda();
			
			if (moneda.esNuevo()) {
				Clients.showNotification("DEBE SELECCIONAR UNA MONEDA..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
			
			boolean fraccionado = filtro.isFraccionado();
			Object[] formato = filtro.getFormato();
			long idVendedor = vendedor == null ? 0 : vendedor.getId();
			long idEmpresa = cliente_ == null ? 0 : cliente_.getEmpresa().getId();
			String caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE;
			if (!cliente) {
				caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR;
			}
			List<CtaCteEmpresaMovimiento> movims_ = new ArrayList<CtaCteEmpresaMovimiento>();	
			List<Object[]> movims = new ArrayList<Object[]>();
			List<Object[]> movimientos = new ArrayList<Object[]>();
			List<Object[]> aux = new ArrayList<Object[]>();
			Map<String, Object[]> data = new HashMap<String, Object[]>();
			
			if (!fraccionado) {
				movims = rr.getSaldos(desde_, hasta_, caracter, idVendedor, idEmpresa, moneda.getId(), incluirChequesRechazados, incluirPrestamos);	
				for (Object[] movim : movims) {
					List<Tipo> rubros = rr.getRubroEmpresas((long) movim[14]);
					if ((rubro != null && this.isRubro(rubro.getId(), rubros)) || rubro == null) {
						long id_mov = (long) movim[0];
						long id_tmv = (long) movim[1];
						data.put(id_mov + "-" + id_tmv, movim);
						movimientos.add(movim);
					}					
				}
				for (String key : data.keySet()) {
					aux.add(data.get(key));
				}
				// ordena la lista segun fecha..
				/*Collections.sort(aux, new Comparator<CtaCteEmpresaMovimiento>() {
					@Override
					public int compare(CtaCteEmpresaMovimiento o1, CtaCteEmpresaMovimiento o2) {
						Date fecha1 = o1.getFechaEmision();
						Date fecha2 = o2.getFechaEmision();
						return fecha1.compareTo(fecha2);
					}
				});*/
			} else {
				movims_ = rr.getMovimientosConSaldo(desde_, hasta_, caracter, idVendedor, idEmpresa, moneda.getId());
				// ordena la lista segun fecha..
				Collections.sort(movims_, new Comparator<CtaCteEmpresaMovimiento>() {
					@Override
					public int compare(CtaCteEmpresaMovimiento o1, CtaCteEmpresaMovimiento o2) {
						Date fecha1 = o1.getFechaEmision();
						Date fecha2 = o2.getFechaEmision();
						return fecha1.compareTo(fecha2);
					}
				});
			}
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new CtaCteSaldosDataSource_(movimientos, aux);
			params.put("Titulo", cliente ? "SALDOS DE CLIENTES DETALLADO" : "SALDOS DE PROVEEDORES DETALLADO");
			params.put("Usuario", getUs().getNombre());
			params.put("Vendedor", vendedor == null ? "TODOS.." : vendedor.getRazonSocial().toUpperCase());
			params.put("Rubro", rubro == null ? "TODOS.." : rubro.getDescripcion().toUpperCase());
			params.put("Moneda", moneda.getDescripcion().toUpperCase());
			imprimirJasper(source, params, dataSource, formato);
		}
		
		/**
		 * @return true si es del rubro segun sigla..
		 */
		private boolean isRubro(long idRubro, List<Tipo> rubroEmpresas) {
			for (Tipo rubro : rubroEmpresas) {
				if (rubro.getId().longValue() == idRubro) {
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
		private void cobranzas(boolean mobile) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				SucursalApp sucursal = filtro.getSelectedSucursal();
				long idSucursal = sucursal == null ? 0 : sucursal.getId();
				String sucursal_ = sucursal == null ? "TODOS.." : sucursal.getDescripcion();

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Recibo> cobranzas = rr.getCobranzas(desde, hasta, idSucursal);
				
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LISTADO_COBRANZAS;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new ListadoCobranzasDataSource(cobranzas, desde, hasta, sucursal_);
				params.put("Usuario", getUs().getNombre());
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
		private void chequesPorCliente(boolean mobile) throws Exception {
			try {
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
				List<BancoChequeTercero> cheques = rr.getChequesTercero(desde,
						hasta, banco, idCliente);

				if (selectedCheque != null) {
					int length = selectedCheque.getLibrado() == null ? 0
							: selectedCheque.getLibrado().length();
					int maxlength = length > 40 ? 40 : length;
					String librador = selectedCheque.getLibrado() == null ? "---"
							: selectedCheque.getLibrado().substring(0,
									maxlength);
					Object[] obj = new Object[] {
							m.dateToString(selectedCheque.getFecha(),
									Misc.DD_MM_YYYY),
							selectedCheque.getNumero(),
							selectedCheque.getBanco().getDescripcion()
									.toUpperCase(), librador.toUpperCase(),
							selectedCheque.isDepositado() ? "SI" : "NO",
							selectedCheque.isDescontado() ? "SI" : "NO",
							selectedCheque.getMonto() };
					data.add(obj);
				} else {
					for (BancoChequeTercero cheque : cheques) {
						int length = cheque.getLibrado().length();
						int maxlength = length > 40 ? 40 : length;
						String librador = cheque.getLibrado() == null ? "---"
								: cheque.getLibrado().substring(0, maxlength);
						Object[] obj = new Object[] {
								m.dateToString(cheque.getFecha(),
										Misc.DD_MM_YYYY),
								cheque.getNumero(),
								cheque.getBanco().getDescripcion()
										.toUpperCase(), librador.toUpperCase(),
								cheque.isDepositado() ? "SI" : "NO",
								cheque.isDescontado() ? "SI" : "NO",
								cheque.getMonto() };
						data.add(obj);
					}
				}

				String sucursal = getAcceso().getSucursalOperativa().getText();
				String nroCheque = selectedCheque == null ? "TODOS.."
						: selectedCheque.getNumero();
				String banco_ = banco == null || selectedCheque != null ? "TODOS.."
						: banco.getDescripcion();
				String cliente_ = cliente == null ? "TODOS.." : cliente
						.getRazonSocial();
				ReporteChequesDeTerceros rep = new ReporteChequesDeTerceros(
						desde, hasta, sucursal, nroCheque, banco_, cliente_);
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

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();

			List<BancoChequeTercero> cheques = rr.getChequesTercero("", "", "",
					"", "", "", "", "", "", "", "", "", null, "TRUE", null, null, 
					desde, hasta, null, null, "", "", false);

			for (BancoChequeTercero cheque : cheques) {
				if (cheque.isDescontado() 
						&& !cheque.getNumeroDescuento().trim().isEmpty() 
						&& !cheque.isAnulado()) {
					Object[] desc = rr.getBancoDescuento(cheque.getId());
					data.add(new Object[] {
							Utiles.getDateToString((Date) desc[0], Utiles.DD_MM_YYYY),
							Utiles.getDateToString(cheque.getFecha(), Utiles.DD_MM_YYYY),
							cheque.getNumero(),
							cheque.getBanco().getDescripcion().toUpperCase(),
							cheque.getCliente().getRazonSocial(),
							cheque.getMonto()});
				}				
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

			ReporteChequesDescontados rep = new ReporteChequesDescontados(desde, hasta);
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
			String formaPago = filtro.getFormaPago_();
			boolean ivaInc = filtro.isIvaIncluido();
			boolean recInc = filtro.isIncluirREC();
			boolean vtaInc = filtro.isIncluirVCT();
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<Recibo> cobranzas = new ArrayList<Recibo>();
			List<Venta> ventas = new ArrayList<Venta>();

			if (recInc) {
				cobranzas = rr.getCobranzas(desde, hasta, 0);
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
				}
			}
			
			// Ventas Contado..
			if (vtaInc) {
				List<Venta> ventas_ = rr.getVentasContado(desde, hasta, 0);
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

			ReporteCobranzasFormaPago rep = new ReporteCobranzasFormaPago(
					desde, hasta, formaPago.isEmpty() ? "TODOS.." : formaPago,
					recInc ? "SI" : "NO", vtaInc ? "SI" : "NO", ivaInc ? "SI" : "NO",
					getAcceso().getSucursalOperativa().getText());
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
			Date vtoHasta_ = Utiles.getFecha(Utiles.getDateToString(vtoHasta, Utiles.DD_MM_YYYY + " 23:00:00"));

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			
			List<BancoDescuentoCheque> descuentos = rr.getBancoDescuentos(desde, hasta, idBanco);
			
			for (BancoDescuentoCheque dto : descuentos) {
				for (BancoChequeTercero cheque : dto.getCheques()) {
					if (cheque.getFecha().compareTo(vtoDesde_) >= 0
							&& cheque.getFecha().compareTo(vtoHasta_) <=0) {
						data.add(new Object[] { 
								Utiles.getDateToString(cheque.getFecha(), Utiles.DD_MM_YYYY),
								cheque.getNumero(),
								cheque.getBanco().getDescripcion().toUpperCase(),
								Utiles.getDateToString(dto.getFecha(), Utiles.DD_MM_YYYY),
								dto.getId() + "",
								dto.getBanco().getBanco().getDescripcion().toUpperCase(),
								Utiles.getMaxLength(cheque.getCliente().getRazonSocial(), 30),
								cheque.getMonto()});
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
			List<BancoChequeTercero> cheques = rr.getChequesTercero(desde,
					hasta, banco, 0);

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

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Cliente> clientes = rr.getClientesCredito();
			List<Object[]> data = new ArrayList<Object[]>();

			for (Cliente cliente : clientes) {
				Object[] cli = new Object[] {
						cliente.getRazonSocial(),
						cliente.getRuc(),
						cliente.isCuentaBloqueada() ? "BLOQUEADO"
								: "HABILITADO", cliente.getLimiteCredito() };
				if (estado == null) {
					data.add(cli);
				} else if (estado
						.equals(ReportesFiltros.ESTADO_CTA_CLIENTE_BLOQUEADO)
						&& cliente.isCuentaBloqueada()) {
					data.add(cli);
				} else if (estado
						.equals(ReportesFiltros.ESTADO_CTA_CLIENTE_HABILITADO)
						&& !cliente.isCuentaBloqueada()) {
					data.add(cli);
				}
			}
			String estado_ = estado == null? "TODOS.." : estado;
			ReporteClientesCredito rep = new ReporteClientesCredito(estado_, getSucursal());
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
							Utiles.getDateToString(cheque.getFechaEmision(), Utiles.DD_MM_YYYY),
							cheque.getNumero() + "", cheque.getBanco().getBanco().getDescripcion().toUpperCase(),
							cheque.getBeneficiario(),
							cheque.getMonto()});
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
			List<BancoCheque> cheques = rr.getCheques("", "", "", "", "", "", true, false, desde, hasta, "");			
			for (BancoCheque cheque : cheques) {
				if (!cheque.isAnulado()) {
					data.add(new Object[]{ 
							Utiles.getDateToString(cheque.getFechaEmision(), Utiles.DD_MM_YYYY),
							Utiles.getDateToString(cheque.getFechaVencimiento(), Utiles.DD_MM_YYYY),
							cheque.getNumero() + "", cheque.getBanco().getBanco().getDescripcion().toUpperCase(),
							cheque.getBeneficiario(),
							cheque.getMonto()});
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
		private void clientesPorRubro(boolean mobile) throws Exception {
			
			RegisterDomain rr = RegisterDomain.getInstance();
			Tipo rubro = filtro.getRubro();
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
							cobro.getTotalImporteGs()
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
				String sucursal_ = sucursal == null ? "TODOS.." : sucursal.getDescripcion();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Recibo> cobranzas = rr.getCobranzas(desde, hasta, idSucursal);
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
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();
			
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<BancoCheque> cheques = rr.getCheques_("", "", "", "", "", "", true, false, desde, hasta);			
			for (BancoCheque cheque : cheques) {
				if (!cheque.isAnulado()) {
					data.add(new Object[]{ 
							Utiles.getDateToString(cheque.getFechaEmision(), Utiles.DD_MM_YYYY),
							Utiles.getDateToString(cheque.getFechaVencimiento(), Utiles.DD_MM_YYYY),
							cheque.getNumero() + "", cheque.getBanco().getBanco().getDescripcion().toUpperCase(),
							cheque.getBeneficiario(),
							cheque.getMonto()});
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
			Tipo rubro = filtro.getRubro();
			Tipo moneda = filtro.getMoneda();
			
			if (moneda.esNuevo()) {
				Clients.showNotification("DEBE SELECCIONAR UN TIPO DE CUENTA..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
			
			boolean fraccionado = filtro.isFraccionado();
			Object[] formato = filtro.getFormato();
			long idVendedor = vendedor == null ? 0 : vendedor.getId();
			long idEmpresa = cliente_ == null ? 0 : cliente_.getEmpresa().getId();
			String caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE;
			if (!cliente) {
				caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR;
			}
			List<CtaCteEmpresaMovimiento> movims_ = new ArrayList<CtaCteEmpresaMovimiento>();	
			List<Object[]> movims = new ArrayList<Object[]>();
			List<Object[]> movimientos = new ArrayList<Object[]>();
			List<Object[]> aux = new ArrayList<Object[]>();
			Map<String, Object[]> data = new HashMap<String, Object[]>();
			
			if (!fraccionado) {
				movims = rr.getSaldos(desde_, hasta_, caracter, idVendedor, idEmpresa, moneda.getId());	
				for (Object[] movim : movims) {
					List<Tipo> rubros = rr.getRubroEmpresas((long) movim[14]);
					if ((rubro != null && this.isRubro(rubro.getId(), rubros)) || rubro == null) {
						long id_mov = (long) movim[0];
						long id_tmv = (long) movim[1];
						data.put(id_mov + "-" + id_tmv, movim);
						movimientos.add(movim);
					}					
				}
				for (String key : data.keySet()) {
					aux.add(data.get(key));
				}
			} else {
				movims_ = rr.getMovimientosConSaldo(desde_, hasta_, caracter, idVendedor, idEmpresa, moneda.getId());
				// ordena la lista segun fecha..
				Collections.sort(movims_, new Comparator<CtaCteEmpresaMovimiento>() {
					@Override
					public int compare(CtaCteEmpresaMovimiento o1, CtaCteEmpresaMovimiento o2) {
						Date fecha1 = o1.getFechaEmision();
						Date fecha2 = o2.getFechaEmision();
						return fecha1.compareTo(fecha2);
					}
				});
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
				Date desde = filtro.getFechaInicioOperaciones();		
				Date hasta = new Date();
				Tipo moneda = filtro.getMoneda();
				
				if (moneda.esNuevo()) {
					Clients.showNotification("DEBE SELECCIONAR UNA MONEDA..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return;
				}
				
				Object[] formato = filtro.getFormato();
				String caracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE;					
				List<Object[]> movims = new ArrayList<Object[]>();	

				movims = rr.getSaldos(desde, hasta, caracter, 0, 0, moneda.getId());
				String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_CTA_CTE_SALDOS_DESGLOSADO;
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new SaldosCtaCteDesglosado(movims, periodo);
				params.put("Titulo", "Saldos de Clientes desglosado por mes (saldo a la fecha actual)");
				params.put("Usuario", getUs().getNombre());
				params.put("Periodo", filtro.getAnhoDesde());
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
				
				List<Object[]> movims = rr.getSaldos(desde, hasta, Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE, 0, cliente.getIdEmpresa(), gs.getId());
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
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo);
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
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo);
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
		private void historialMovimientosClientes(boolean mobile, boolean consolidado) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Date desde = filtro.getFechaDesde();
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

				long idCliente = cliente != null ? cliente.getId() : 0;

				List<Object[]> ventas = rr.getVentasPorCliente_(idCliente, desde, hasta);
				List<Object[]> cheques_rechazados = rr.getChequesRechazadosPorCliente(idCliente, desde, hasta);
				List<Object[]> prestamos_cc = rr.getPrestamosCC(idCliente, desde, hasta);
				List<Object[]> ntcsv = rr.getNotasCreditoPorCliente(idCliente, desde, hasta);
				List<Object[]> recibos = rr.getRecibosPorCliente(idCliente, desde, hasta);
				List<Object[]> reembolsos_cheques_rechazados = rr.getReembolsosChequesRechazadosPorCliente(idCliente, desde, hasta);
				List<Object[]> reembolsos_prestamos_cc = rr.getReembolsosPrestamosCC(idCliente, desde, hasta);

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
				String titulo = consolidado ? "SALDOS DE CLIENTES RESUMIDO (A UNA FECHA)" : "SALDOS DE CLIENTES DETALLADO (HISTORIAL A UNA FECHA)";
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo);
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
				this.matrizReposicion();
				break;
				
			case ULTIMO_COSTO_ARTICULOS:
				this.ultimoCostoArticulos();
				break;	
			
			case MATRIZ_IMPORTACIONES:
				this.matrizImportaciones();
				break;
				
			case LISTADO_IMPORTACIONES:
				this.listadoImportaciones();
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

				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<CompraLocalFactura> compras = rr.getComprasLocales(desde,
						hasta);

				for (CompraLocalFactura compra : compras) {
					Object[] cmp = new Object[] {
							m.dateToString(compra.getFechaOriginal(),
									Misc.DD_MM_YYYY), compra.getNumero(),
							compra.getProveedor().getRazonSocial(),
							compra.getProveedor().getRuc(),
							compra.getImporteGs() };
					data.add(cmp);
				}

				ReporteComprasGenerico rep = new ReporteComprasGenerico();
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
		private void matrizReposicion() {
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
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Venta> ventas = rr.getVentas(desde, hasta, 0);
				List<Object[]> data = new ArrayList<Object[]>();
				Map<String, Object[]> values = new HashMap<String, Object[]>();
				Map<String, Object[]> values_ = new HashMap<String, Object[]>();
				
				for (Venta venta : ventas) {
					int mes = Utiles.getNumeroMes(venta.getFecha()) - 1;
					if (!venta.isAnulado()) {
						for (VentaDetalle item : venta.getDetalles()) {
							if (item.getArticulo().isProveedor(proveedor.getId())) {
								String cod = item.getArticulo().getCodigoInterno();
								String key = cod + "-" + mes;
								Object[] acum = values.get(key);
								Object[] datos = rr.getStockCosto(item.getArticulo().getId());
								long stock = (long) datos[0];
								double costo = (double) datos[1];
								if (acum != null) {
									long cant = (long) acum[0];
									cant += item.getCantidad();
									values.put(key, new Object[] { cant, mes, cod, stock, costo });
								} else {
									values.put(key,
											new Object[] { item.getCantidad(), mes, cod, stock, costo });
								}
							}														
						}
					}
				}
				
				for (String key : values.keySet()) {
					Object[] value = values.get(key);
					String cod = (String) value[2];
					int mes = (int) value[1];
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
					int size = (Utiles.getNumeroMes(hasta) + 1) - Utiles.getNumeroMes(desde);
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
				String titulo = ReporteMatrizReposicion.MATRIZ_REPOSICION;
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
		private void listadoImportaciones() {
			try {
				Date desde = filtro.getFechaDesde();
				Date hasta = filtro.getFechaHasta();
				Proveedor prov = filtro.getProveedorExterior();
				long idProv = prov == null? 0 : prov.getId();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<ImportacionPedidoCompra> compras = rr.getImportaciones(desde, hasta, idProv);

				for (ImportacionPedidoCompra compra : compras) {
					Object[] cmp = new Object[] {
							Utiles.getDateToString(compra.getFechaCreacion(), Utiles.DD_MM_YYYY), 
							compra.getNumeroPedidoCompra(),
							compra.getImportacionFactura_().get(0).getNumero(),
							compra.getProveedor().getRazonSocial(),
							compra.getCambio(),
							compra.getTotalImporteGs(),
							compra.getTotalImporteDs()};
					data.add(cmp);
				}

				String proveedor = prov == null? "TODOS.." : prov.getRazonSocial();
				ReporteComprasImportacion rep = new ReporteComprasImportacion(desde, hasta, proveedor);
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
	}

	/**
	 * Reportes de logistica..
	 */
	class ReportesDeLogistica {

		static final String LISTADO_REPARTOS = "LOG-00001";

		/**
		 * procesamiento del reporte..
		 */
		public ReportesDeLogistica(String codigoReporte) throws Exception {
			switch (codigoReporte) {

			case LISTADO_REPARTOS:
				this.listadoRepartos();
				break;
			}
		}

		/**
		 * listado de repartos..
		 */
		public void listadoRepartos() {
			Clients.showNotification(LISTADO_REPARTOS);
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
				this.stockMercaderiaAunaFecha();
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
				this.historialMovimientosClientes(mobile, false);
				break;
				
			case HISTORIAL_SALDOS_CLIENTES_RESUMIDO:	
				this.historialMovimientosClientes(mobile, true);
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

			List<Venta> ventas = rr.getVentas(desde, hasta, 0);
			InformeHechauka.generarInformeHechauka(ventas);
			Clients.showNotification("Informe Hechauka generado..");
		}

		/**
		 * Hechauka de notas de credito..
		 */
		private void notasCreditoHechauka() throws Exception {
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();

			List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
			InformeHechauka.generarInformeHechaukaNotaCred(ncs);
			Clients.showNotification("Informe Hechauka generado..");
		}

		/**
		 * Libro de Ventas..
		 */
		private void libroVentas() throws Exception {
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			Object[] formato = filtro.getFormato();
			boolean formularioContinuo = filtro.isFormularioContinuo();
			List<Venta> ventas = rr.getVentas(desde, hasta, 0);
			List<NotaCredito> notasCredito = rr.getNotasCreditoVenta(desde,	hasta, 0);
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_VENTAS;
			if (formularioContinuo)
				source = formato.equals(com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_XLS)? 
						com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_VENTAS_FC_XLS : 
							com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_LIBRO_VENTAS_FC;
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new LibroVentasDataSource(ventas, notasCredito, desde, hasta);
			params.put("Usuario", getUs().getNombre());
			imprimirJasper(source, params, dataSource, formato);
		}
		
		/**
		 * reporte CON-00005
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
				}

				double totalSinIva = totalImporte
						- m.calcularIVA(totalImporte, 10);
				String sucursal = getAcceso().getSucursalOperativa().getText();

				ReporteVentasGenerico rep = new ReporteVentasGenerico(
						totalSinIva, desde, hasta, "TODOS..", cliente_, sucursal, "TODOS..");
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
		@SuppressWarnings("unchecked")
		private void stockMercaderiaAunaFecha() throws Exception {
			try {
				Date desde = filtro.getFechaInicioOperaciones();
				Date hasta = filtro.getFechaHasta();
				Articulo articulo = filtro.getArticulo();
				String tipoCosto = filtro.getTipoCosto();
				
				if (hasta == null) hasta = new Date();
				
				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Articulo> arts = new ArrayList<Articulo>();
				
				if (articulo != null) {
					arts.add(articulo);
				} else {
					arts = rr.getArticulos();
				}

				for (Articulo art : arts) {
					if (!art.getCodigoInterno().startsWith("@")) {
						List<Object[]> historicoEntrada = new ArrayList<Object[]>();
						List<Object[]> historicoSalida = new ArrayList<Object[]>();
						long stockEntrada = 0;
						long stockSalida = 0;
						
						List<Object[]> ventas = rr.getVentasPorArticulo(art.getId(), desde, hasta);
						List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(art.getId(), desde, hasta);
						List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(art.getId(), desde, hasta);
						List<Object[]> compras = rr.getComprasLocalesPorArticulo(art.getId(), desde, hasta);
						List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(art.getId(), desde, hasta);
						List<Object[]> transfs = rr.getTransferenciasPorArticulo(art.getId(), desde, hasta);
						List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(art.getId(), desde, hasta, 2, Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
						List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(art.getId(), desde, hasta, 2, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
						List<Object[]> migracion = rr.getMigracionPorArticulo(art.getCodigoInterno(), desde, hasta, 2);
						
						historicoEntrada = new ArrayList<Object[]>();
						historicoEntrada.addAll(migracion);
						historicoEntrada.addAll(ajustStockPost);		
						historicoEntrada.addAll(ntcsv);
						historicoEntrada.addAll(compras);
						historicoEntrada.addAll(importaciones);
						
						historicoSalida = new ArrayList<Object[]>();
						historicoSalida.addAll(ajustStockNeg);
						historicoSalida.addAll(ventas);
						historicoSalida.addAll(ntcsc);
						
						for (Object[] transf : transfs) {
							long idsuc = (long) transf[6];
							if(idsuc == 2) {
								historicoSalida.add(transf);
							} else {				
								historicoEntrada.add(transf);
							}
						}
						
						for(Object[] obj : historicoEntrada){
							stockEntrada += Long.parseLong(String.valueOf(obj[3]));
						}
						
						for(Object[] obj : historicoSalida){
							long cantidad = Long.parseLong(String.valueOf(obj[3]));
							if(cantidad < 0)
								cantidad = cantidad * -1;
							stockSalida += cantidad;
						}
						
						long stock = stockEntrada - stockSalida;
						double costo  = tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO) ? 
								ControlArticuloCosto.getCostoPromedio(art.getId(), hasta) : ControlArticuloCosto.getCostoUltimo(art.getId(), hasta);
						
						data.add(new Object[] { art.getCodigoInterno(), art.getDescripcion(), stock, costo, (stock * costo) });
					}
				}
				
				String desc = articulo == null ? "TODOS.." : articulo.getCodigoInterno();
				ReporteStockValorizadoAunaFecha rep = new ReporteStockValorizadoAunaFecha(hasta, desc, tipoCosto);
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
			ArticuloGasto cuenta = filtro.getArticuloGasto();
			Object[] formato = filtro.getFormato();
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Gasto> gastos = rr.getGastos(desde, hasta);
			List<Object[]> dets = new ArrayList<Object[]>();

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
			JRDataSource dataSource = new GastosPorCuentaContableDataSource(dets);
			params.put("Usuario", getUs().getNombre());
			params.put("periodo", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY) 
					+ " a " + Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
			imprimirJasper(source, params, dataSource, formato);
		}
		
		/**
		 * Gastos por Cuenta Contable..
		 */
		private void gastosPorCuentaContable_(boolean mobile) throws Exception {
			Date desde = filtro.getFechaDesde();
			Date hasta = filtro.getFechaHasta();
			ArticuloGasto cuenta = filtro.getArticuloGasto();
			
			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Gasto> gastos = rr.getGastos(desde, hasta);
			List<Object[]> dets = new ArrayList<Object[]>();
			List<Object[]> data = new ArrayList<Object[]>();
			Map<String, Object[]> values = new HashMap<String, Object[]>();

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
					double total = (double) val[1];
					total += det.getMontoGs();
					values.put(det.getArticuloGasto().getDescripcion(), new Object[]{ det, total });
				} else {
					double monto = det.getMontoGs();
					values.put(det.getArticuloGasto().getDescripcion(), new Object[]{ det, monto});
				}
			}
			
			for (String key : values.keySet()) {
				Object[] value = values.get(key);
				GastoDetalle det = (GastoDetalle) value[0];
				double importe = (double) value[1];
				String cod = det.getArticuloGasto().getCuentaContable().getCodigo();
				String desc = det.getArticuloGasto().getDescripcion();
				data.add(new Object[]{ cod, desc, importe });
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
			
			ReporteGastosPorCuentas rep = new ReporteGastosPorCuentas(desde, hasta, getSucursal());
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
		private void historialMovimientosClientes(boolean mobile, boolean consolidado) {
			if (mobile) {
				Clients.showNotification("AUN NO DISPONIBLE EN VERSION MOVIL..");
				return;
			}
			try {
				Date desde = filtro.getFechaInicioOperaciones();
				Date hasta = filtro.getFechaHasta();
				Object[] formato = filtro.getFormato();
				Cliente cliente = filtro.getCliente();
				boolean incluirChequesRechazados = filtro.isIncluirCHQ_RECH();
				boolean incluirPrestamos = filtro.isIncluirPRE();

				if (desde == null) desde = new Date();
				if (hasta == null) hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();
				List<Object[]> historico;
				List<Object[]> historicoDEBE;
				List<Object[]> historicoHABER;
				Map<String, String> totalSaldo = new HashMap<String, String>();

				long idCliente = cliente != null ? cliente.getId() : 0;

				List<Object[]> ventas = rr.getVentasPorCliente_(idCliente, desde, hasta);
				List<Object[]> cheques_rechazados = rr.getChequesRechazadosPorCliente(idCliente, desde, hasta);
				List<Object[]> prestamos_cc = rr.getPrestamosCC(idCliente, desde, hasta);
				List<Object[]> ntcsv = rr.getNotasCreditoPorCliente(idCliente, desde, hasta);
				List<Object[]> recibos = rr.getRecibosPorCliente(idCliente, desde, hasta);
				List<Object[]> reembolsos_cheques_rechazados = rr.getReembolsosChequesRechazadosPorCliente(idCliente, desde, hasta);
				List<Object[]> reembolsos_prestamos_cc = rr.getReembolsosPrestamosCC(idCliente, desde, hasta);

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
				String titulo = consolidado ? "SALDOS DE CLIENTES RESUMIDO (A UNA FECHA)" : "SALDOS DE CLIENTES DETALLADO (HISTORIAL A UNA FECHA)";
				Map<String, Object> params = new HashMap<String, Object>();
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo);
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
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo);
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
				
				if (desde == null)
					desde = new Date();

				if (hasta == null)
					hasta = new Date();

				RegisterDomain rr = RegisterDomain.getInstance();
				List<Object[]> data = new ArrayList<Object[]>();

				List<Object[]> ncs = rr.getNotasCreditoVenta_(desde, hasta, 0);
				for (Object[] notacred : ncs) {
					long idNcred = (long) notacred[0];
					String numero = (String) notacred[1];
					Date fecha = (Date) notacred[2];
					String descrMotivo = (String) notacred[3];
					String siglaMotivo = (String) notacred[4];
					if (siglaMotivo.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_DEVOLUCION)) {
						String motivo = descrMotivo.substring(0, 3).toUpperCase() + ".";
						List<Object[]> dets = rr.getNotaCreditoDetalles(idNcred);
						double costo = 0;
						if (tipoCosto.equals(ReportesFiltros.COSTO_ULTIMO)) {
							for (Object[] det : dets) {
								double cost = (double) det[0];
								int cant = (int) det[1];
								costo += (cost * cant);
							}
						}
						if (tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO)) {
							for (Object[] det : dets) {
								long idArt = (long) det[2];								
								double cost = ControlArticuloCosto.getCostoPromedio(idArt, hasta);
								if(cost == 0) cost = (double) det[0];
								int cant = (int) det[1];
								costo += (cost * cant);
							}
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
					long idvta = (long) venta[0];
					String numero = (String) venta[1];
					Date fecha = (Date) venta[2];
					String sigla = (String) venta[4];
					List<Object[]> dets = rr.getVentaDetalles(idvta);
					double costo = 0;
					if (tipoCosto.equals(ReportesFiltros.COSTO_ULTIMO)) {
						for (Object[] det : dets) {
							double cost = (double) det[0];
							long cant = (long) det[1];
							costo += (cost * cant);
						}
					}
					if (tipoCosto.equals(ReportesFiltros.COSTO_PROMEDIO)) {
						for (Object[] det : dets) {
							long idArt = (long) det[2];
							double cost = ControlArticuloCosto.getCostoPromedio(idArt, hasta);
							if(cost == 0) cost = (double) det[0];
							long cant = (long) det[1];
							costo += (cost * cant);
						}
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
				JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo);
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

	public static void main(String[] args) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Usuario> usuarios = rr.getUsuarios("", "");

		ReporteUsuarios rp = new ReporteUsuarios(true, true);
		rp.setDatosUsuarios(usuarios);
		rp.ejecutar(true);
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
	
	public ReporteChequesDescontados(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha Desc.", TIPO_STRING, 30);
	static DatosColumnas col1 = new DatosColumnas("Vto. Cheque", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Nro. Cheque", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Banco", TIPO_STRING, 45);
	static DatosColumnas col4 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 40, true);

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
				.add(this.textoParValor("Fecha Desde", Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY)))
				.add(this.textoParValor("Fecha Hasta", Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY))));
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

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 45);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Ruc", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Importe", TIPO_DOUBLE, 40,
			true);

	public ReporteComprasGenerico() {
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
		out.add(cmp.horizontalFlowList().add(this.texto("")));
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
	private String proveedor;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 40);
	static DatosColumnas col1 = new DatosColumnas("Recibo", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Factura", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Monto Gs.", TIPO_DOUBLE_GS,
			40, true);

	public ReporteCobranzasPorVendedor(Date desde, Date hasta, String vendedor, String proveedor) {
		this.desde = desde;
		this.hasta = hasta;
		this.vendedor = vendedor;
		this.proveedor = proveedor;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Cobranzas por Vendedor");
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
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Vendedor", this.vendedor))
				.add(this.textoParValor("Proveedor", this.proveedor)));
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
			int lenght = nrocomprobante.length();
			value = nrocomprobante.substring(0, lenght < 15? lenght : 15);
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
	public CtaCteSaldosDHSDataSource(List<Object[]> values, String cliente, Map<String, String> totalSaldo) {
		this.values = values;
		this.totalSaldo = totalSaldo;
		for (String key : this.totalSaldo.keySet()) {
			String saldo = this.totalSaldo.get(key);
			totalSaldo_ += Double.parseDouble(saldo.replace(",", "").replace(".", ""));
		}
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
 * DataSource de Libro Ventas ..
 */
class LibroVentasDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	Misc misc = new Misc();

	List<Venta> ventas;
	List<NotaCredito> notasCredito;
	List<BeanLibroVenta> values = new ArrayList<BeanLibroVenta>();
	Date desde;
	Date hasta;

	double totalContado = 0;
	double totalCredito = 0;
	double totalNCContado = 0;
	double totalNCCredito = 0;

	double totalGravada = 0;
	double totalImpuesto = 0;
	double totalImporte = 0;

	public LibroVentasDataSource(List<Venta> ventas,
			List<NotaCredito> notasCredito, Date desde, Date hasta) {
		this.notasCredito = notasCredito;
		this.ventas = ventas;
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
			value = "Ruc: "
					+ com.yhaguy.gestion.reportes.formularios.ReportesViewModel.getRucEmpresa();
		} else if ("Periodo".equals(fieldName)) {
			value = "Correspondiente al "
					+ misc.dateToString(this.desde, "MM/yyyy");
		} else if ("DireccionEmpresa".equals(fieldName)) {
			value = "Direccion: "
					+ com.yhaguy.gestion.reportes.formularios.ReportesViewModel.getDireccionEmpresa();
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
		} else if ("TotalImpuesto".equals(fieldName)) {
			value = FORMATTER.format(this.totalImpuesto);
		} else if ("TotalImporte".equals(fieldName)) {
			value = FORMATTER.format(this.totalImporte);
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
		for (NotaCredito ncred : this.notasCredito) {
			String fecha = misc.dateToString(ncred.getFechaEmision(),
					"dd-MM-yy");
			String concepto = TipoMovimiento.getAbreviatura(ncred
					.getTipoMovimiento().getSigla());
			String numero = ncred.getNumero();
			String razonSocial = ncred.isAnulado() ? "ANULADO" : ncred
					.getCliente().getRazonSocial();
			String ruc = ncred.getCliente().getRuc();
			if (ruc.isEmpty())
				ruc = Configuracion.RUC_EMPRESA_LOCAL;
			double grav5 = 0.0;
			double grav10 = ncred.isAnulado() ? 0.0 : (ncred.getTotalGravado10() * -1);
			double iva5 = 0.0;
			double iva10 = ncred.isAnulado() ? 0.0 : (ncred.getTotalIva10() * -1);
			double total = ncred.isAnulado() ? 0.0 : (ncred.getImporteGs() * -1);
			values.add(new BeanLibroVenta(fecha, concepto, numero, razonSocial,
					ncred.isAnulado() ? "" : ruc, grav10, iva10, grav5, iva5,
					total));
			if (ncred.isAnulado() == false) {
				this.totalGravada -= (ncred.getTotalGravado10());
				this.totalImpuesto -= (ncred.getTotalIva10());
				this.totalImporte -= (ncred.getImporteGs());

				if (ncred.isNotaCreditoVentaContado()) {
					this.totalNCContado -= (ncred.getImporteGs());
				} else {
					this.totalNCCredito -= (ncred.getImporteGs());
				}
				System.out.println(ncred.getImporteGs() * -1);
			}
		}

		for (Venta vta : this.ventas) {
			String fecha = misc.dateToString(vta.getFecha(), "dd-MM-yy");
			String concepto = TipoMovimiento.getAbreviatura(vta.getTipoMovimiento().getSigla());
			String numero = vta.getNumero();
			String razonSocial = vta.isAnulado() ? "ANULADO" : vta.getDenominacion();
			if (razonSocial == null)
				razonSocial = vta.getCliente().getRazonSocial();
			String ruc = vta.getCliente().getRuc();
			if (ruc.isEmpty())
				ruc = Configuracion.RUC_EMPRESA_LOCAL;
			double grav5 = 0.0;
			double grav10 = vta.isAnulado() ? 0.0 : vta.getTotalGravado10();
			double iva5 = 0.0;
			double iva10 = vta.isAnulado() ? 0.0 : vta.getTotalIva10();
			double total = vta.isAnulado() ? 0.0 : vta.getTotalImporteGs();
			values.add(new BeanLibroVenta(fecha, concepto, numero, razonSocial,
					vta.isAnulado() ? "" : ruc, grav10, iva10, grav5, iva5, total));
			if (vta.isAnulado() == false) {
				this.totalGravada += (vta.getTotalGravado10());
				this.totalImpuesto += (vta.getTotalIva10());
				this.totalImporte += (vta.getTotalImporteGs());
				System.out.println(vta.getTotalImporteGs());
				if (vta.isVentaContado()) {
					this.totalContado += (vta.getTotalImporteGs());
				} else {
					this.totalCredito += (vta.getTotalImporteGs());
				}
			}
		}
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

	public ListadoCobranzasDataSource(List<Recibo> recibos, Date desde,
			Date hasta, String sucursal) {
		this.recibos = recibos;
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
		this.loadDatos();
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
		for (Recibo recibo : this.recibos) {
			String fecha = misc.dateToString(recibo.getFechaEmision(), Misc.DD_MM_YYYY);
			String numero = recibo.getNumero();
			String razonSocial = recibo.isAnulado() ? "ANULADO.." : recibo.getCliente().getRazonSocial();
			String ruc = recibo.getCliente().getRuc();
			String importe = FORMATTER.format(recibo.getTotalImporteGs());
			values.add(new BeanRecibo(fecha, numero, razonSocial, ruc, importe));
			this.totalImporte += recibo.getTotalImporteGs();
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
			this.totalImporte += transf.getImporteGs();
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
				String nroremision = transf.getObservacion();
				String importe = FORMATTER.format(transf.getImporteGs());
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
			value = FORMATTER.format(det[13]);
		} else if ("Descripcion".equals(fieldName)) {
			value = det[14];
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

	public ReporteCobranzasFormaPago(Date desde, Date hasta, String formaPago,
			String recibos, String ventas, String ivaIncluido, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.formaPago = formaPago;
		this.recibos = recibos;
		this.ventas = ventas;
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
	static DatosColumnas col0 = new DatosColumnas("Vencimiento", TIPO_STRING,
			40);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Banco", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Librador", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Depositado", TIPO_STRING, 30);
	static DatosColumnas col5 = new DatosColumnas("Descontado", TIPO_STRING, 30);
	static DatosColumnas col6 = new DatosColumnas("Importe", TIPO_DOUBLE, 40,
			true);

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
		this.setTitulo("Listado de Cheques de Clientes");
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
				.add(this.textoParValor("Vto.Cheque Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Vto.Cheque Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
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

	private String sucursal;
	private String familia;
	private String marca;
	private String proveedor;
	private String articulo;
	private String deposito;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Código", TIPO_STRING, 40);
	static DatosColumnas col1 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Familia", TIPO_STRING, 25);
	static DatosColumnas col3 = new DatosColumnas("Stock", TIPO_LONG, 20, true);
	static DatosColumnas col4 = new DatosColumnas("Costo Gs.", TIPO_DOUBLE_GS, 40, true);
	static DatosColumnas col5 = new DatosColumnas("Total Gs.", TIPO_DOUBLE_GS, 40, true);

	public ReporteExistenciaArticulos(String sucursal, String familia,
			String marca, String proveedor, String articulo, String deposito) {
		this.sucursal = sucursal;
		this.familia = familia;
		this.marca = marca;
		this.proveedor = proveedor;
		this.articulo = articulo;
		this.deposito = deposito;
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
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Familia", this.familia))
				.add(this.textoParValor("Marca", this.marca)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Proveedor", this.proveedor))
				.add(this.textoParValor("Articulo", this.articulo)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()	
				.add(this.textoParValor("Depósito", this.deposito))
				.add(this.textoParValor("Sucursal", this.sucursal)));
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

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Ruc", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Estado", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Límite Créd.", TIPO_DOUBLE_GS, 30, true);

	public ReporteClientesCredito(String estado, String sucursal) {
		this.estado = estado;
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
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING,30);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Banco", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Beneficiario", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Importe", TIPO_DOUBLE_GS, 30, true);

	public ReporteChequesPropios(Date desde, Date hasta, String banco, String sucursal) {
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
	static DatosColumnas col4 = new DatosColumnas("Importe", TIPO_DOUBLE_GS, 30, true);

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
	static DatosColumnas col4 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 30, true);

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
	static DatosColumnas col2 = new DatosColumnas("Vendedor", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Total Cobrado Gs.", TIPO_DOUBLE_GS, 40, true);

	public ReporteTotalCobranzas(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col2);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Total Cobranzas por Vendedor");
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
	static DatosColumnas col3 = new DatosColumnas("Ene", TIPO_LONG, 50, true);
	static DatosColumnas col4 = new DatosColumnas("Feb", TIPO_LONG, 50, true);
	static DatosColumnas col5 = new DatosColumnas("Mar", TIPO_LONG, 50, true);
	static DatosColumnas col6 = new DatosColumnas("Abr", TIPO_LONG, 50, true);
	static DatosColumnas col7 = new DatosColumnas("May", TIPO_LONG, 50, true);
	static DatosColumnas col8 = new DatosColumnas("Jun", TIPO_LONG, 50, true);
	static DatosColumnas col9 = new DatosColumnas("Jul", TIPO_LONG, 50, true);
	static DatosColumnas col10 = new DatosColumnas("Ago", TIPO_LONG, 50, true);
	static DatosColumnas col11 = new DatosColumnas("Set", TIPO_LONG, 50, true);
	static DatosColumnas col12 = new DatosColumnas("Oct", TIPO_LONG, 50, true);
	static DatosColumnas col13 = new DatosColumnas("Nov", TIPO_LONG, 50, true);
	static DatosColumnas col14 = new DatosColumnas("Dic", TIPO_LONG, 50, true);
	static DatosColumnas col15 = new DatosColumnas("Stock", TIPO_LONG, 50);
	static DatosColumnas col16 = new DatosColumnas("Max", TIPO_LONG, 50);
	static DatosColumnas col17 = new DatosColumnas("Prom", TIPO_LONG, 50);
	
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
		this.setTitulo("Clientes por Rubro");
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
	
	public ReporteStockValorizadoAunaFecha(Date hasta, String articulo, String tipoCosto) {
		this.hasta = hasta;
		this.articulo = articulo;
		this.tipoCosto = tipoCosto;
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
	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, Double> totales = new HashMap<String, Double>();

	public GastosPorCuentaContableDataSource(List<Object[]> values) {
		this.values = values;
		for (Object[] value : values) {
			GastoDetalle det = (GastoDetalle) value[0];
			Double total = totales.get(det.getArticuloGasto().getDescripcion());
			if (total != null) {
				total += det.getMontoGs();
				totales.put(det.getArticuloGasto().getDescripcion(), total);
			} else {
				totales.put(det.getArticuloGasto().getDescripcion(), det.getMontoGs());
			}
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] obj = this.values.get(index);
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
		} else if ("Importe".equals(fieldName)) {
			value = Utiles.getNumberFormat(det.getMontoGs());
		} else if ("TotalImporte".equals(fieldName)) {
			value = Utiles.getNumberFormat(totales.get(det.getArticuloGasto().getDescripcion()));
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
	static DatosColumnas col0 = new DatosColumnas("Código", TIPO_STRING, 45);
	static DatosColumnas col1 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 40, true);

	public ReporteGastosPorCuentas(Date desde, Date hasta, String sucursal) {
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
	
	public SaldosCtaCteDesglosado(List<Object[]> values, String periodo) {
		
		for (Object[] value : values) {
			long idEmpresa = (long) value[14];
			double saldo = (double) value[9];
			Double acum = this.saldo.get(idEmpresa + "");
			if (acum != null) {
				acum += saldo;
			} else {
				acum = saldo;
			}
			this.saldo.put(idEmpresa + "", acum);
		}
		
		for (Object[] value : values) {
			long idEmpresa = (long) value[14];
			String razonSocial = (String) value[10];
			Date emision = (Date) value[6];
			double saldo = (double) value[9];
			int index = Utiles.getNumeroMes(emision);
			
			if (Utiles.getDateToString(emision, "yyyy").equals(periodo)) {
				Object[] acum = data.get(idEmpresa + "");
				if (acum != null) {
					acum[index] = ((double) acum[index]) + saldo;
				} else {
					acum = new Object[] { razonSocial, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, this.saldo.get(idEmpresa + "") };
					acum[index] = saldo;
				} 
				data.put(idEmpresa + "", acum);
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

		if ("Cliente".equals(fieldName)) {
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
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Familia", TIPO_STRING);	
	static DatosColumnas col2 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE, true);
	

	public ReporteVentasPorFamilia(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col1);
		cols.add(col2);
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
				.add(this.textoParValor("Sucursal", this.sucursal)));
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
