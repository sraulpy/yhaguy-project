package com.yhaguy.gestion.empresa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.domain.IiD;
import com.coreweb.domain.Tipo;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.EmpresaGrupoSociedad;
import com.yhaguy.domain.Localidad;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlLogicaEmpresa;
import com.yhaguy.gestion.empresa.ctacte.AssemblerCtaCteEmpresaMovimiento;
import com.yhaguy.gestion.empresa.ctacte.ControlCtaCteEmpresa;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaDTO;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaMovimientoDTO;
import com.yhaguy.gestion.empresa.ctacte.CuentasCobrarControlBody;
import com.yhaguy.gestion.empresa.ctacte.CuentasPagarControlBody;
import com.yhaguy.gestion.empresa.ctacte.FiltroCtaCteMovimientos;
import com.yhaguy.gestion.empresa.ctacte.ReporteMovimientosCtaCte;
import com.yhaguy.util.reporte.ReporteYhaguy;

public abstract class EmpresaControlBody extends BodyApp {

	private static int TIPO_CLIENTE = 1;
	private static int TIPO_PROVEEDOR = 2;

	@Init(superclass = true)
	public void initEmpresaControlBody() {
	}

	@AfterCompose(superclass = true)
	public void afterComposeEmpresaControlBody() {
	}

	private EmpresaDTO dtoEmp = new EmpresaDTO();
	private ControlLogicaEmpresa ctrlog = new ControlLogicaEmpresa(null);

	public EmpresaDTO getDtoEmp() {
		return dtoEmp;
	}

	public void setDtoEmp(EmpresaDTO dtoEmp) {

		this.dtoEmp = dtoEmp;
		if (dtoEmp.getSucursales().size() > 0) {
			this.setSelectedSucursal(dtoEmp.getSucursales().get(0));
		}

		if (dtoEmp.getContactos().size() > 0) {
			this.setSelectedContacto(dtoEmp.getContactos().get(0));
		}

		this.tabDatosVisible = true;
		if (this.dtoEmp.esNuevo() == true) {
			this.tabDatosVisible = false;
		}

		/*
		 * Para que limpie los datos utilizados en la CtaCte al cambiar de
		 * Empresa
		 */
		this.restaurarValoresDefectoCtaCte();
	}

	// ========== Tipo de Control ========================

	public int getTipoControl() throws Exception {
		int out = 0;
		if (this instanceof ClienteControlBody || this instanceof CuentasCobrarControlBody) {
			out = TIPO_CLIENTE;
		} else if (this instanceof ProveedorControlBody || this instanceof CuentasPagarControlBody) {
			out = TIPO_PROVEEDOR;
		} else {
			throw new Exception("Error tipo de control " + this.getClass().getName());
		}

		return out;
	}
	
	public boolean isCliente() throws Exception {
		return this.getTipoControl() == TIPO_CLIENTE;
	}

	// =========== monedas ================================
	private MyPair selectedMoneda = null;

	public MyPair getSelectedMoneda() {
		return selectedMoneda;
	}

	public void setSelectedMoneda(MyPair selectedMoneda) {
		this.selectedMoneda = selectedMoneda;
	}

	@Command()
	@NotifyChange("*")
	public void defaultMoneda() {

		if (this.getSelectedMoneda() != null) {
			this.dtoEmp.setMoneda(this.selectedMoneda);
		}
	}

	@Command()
	@NotifyChange("*")
	public void eliminarMoneda() {
		if (this.getSelectedMoneda() != null) {
			if (this.getSelectedMoneda() == this.dtoEmp.getMoneda()) {
				this.mensajeError("No puede eliminar la moneda por default");
				return;
			}

			if (mensajeEliminar("Está seguro que quiere eliminar la moneda " + this.getSelectedMoneda() + "?")) {
				this.getDtoEmp().getMonedas().remove(this.selectedMoneda);
			}
			this.setSelectedMoneda(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarMoneda() {
		// si no hay nada seleccionado, sale
		if (this.getSelectedMoneda() == null) {
			return;
		}
		// si la moneda ya está, sale
		if (this.dtoEmp.getMonedas().contains(this.getSelectedMoneda()) == true) {
			return;
		}

		if (mensajeAgregar("Agregar la moneda " + this.getSelectedMoneda() + "?")) {
			this.dtoEmp.getMonedas().add(this.getSelectedMoneda());
			this.setSelectedMoneda(null);
		}
	}

	// =========== sucursal ================================

	private MyArray selectedSucursal = null;

	public MyArray getSelectedSucursal() {
		return selectedSucursal;
	}

	public void setSelectedSucursal(MyArray suc) {
		this.selectedSucursal = suc;
	}

	public boolean getSinSucursal() {
		return this.getDtoEmp().getSucursales().size() == 0;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarSucursal() {
		if (this.selectedSucursal != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la sucursal?")) {
				this.getDtoEmp().getSucursales().remove(this.selectedSucursal);
			}
			this.setSelectedSucursal(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarSucursal() {

		if (mensajeAgregar("Agregar una nueva sucursal?")) {
			MyArray nSuc = new MyArray();
			MyPair zona = new MyPair();
			MyPair localidad = new MyPair();
			nSuc.setPos1("nueva sucursal");
			nSuc.setPos2("");
			nSuc.setPos3("");
			nSuc.setPos4("");
			nSuc.setPos5(zona);
			nSuc.setPos6(localidad);
			nSuc.setPos7("");
			this.getDtoEmp().getSucursales().add(nSuc);
			this.setSelectedSucursal(nSuc);
		}
	}

	// =====================================================

	// =========== contacto ================================

	private ContactoDTO selectedContacto = null;

	public ContactoDTO getSelectedContacto() {
		return selectedContacto;
	}

	public void setSelectedContacto(ContactoDTO selectedContacto) {
		this.selectedContacto = selectedContacto;
	}

	public boolean getSinContacto() {
		return ((this.getDtoEmp().getContactos().size() == 0) || (this.getSinSucursal()));
	}

	@Command
	@NotifyChange("*")
	public void eliminarContacto() {
		if (this.selectedSucursal != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar este contacto?")) {
				this.getDtoEmp().getContactos().remove(this.selectedContacto);
			}
			this.setSelectedContacto(null);
		}
	}

	@Command
	@NotifyChange("*")
	public void agregarContacto() {
		if (mensajeAgregar("Agregar a nuevo contacto?")) {
			ContactoDTO nCon = new ContactoDTO();
			nCon.setNombre("nuevo contacto");
			this.getDtoEmp().getContactos().add(nCon);
			this.setSelectedContacto(nCon);
		}
	}

	// =====================================================

	private boolean rucReadonly = true;
	private boolean ciReadonly = true;

	public boolean isRucReadonly() {
		this.rucReadonly = true;

		// este IF es sólo para la migración
		if (this.rucReadonly == true) {
			return false;
		}

		if ((this.isDeshabilitado() == false) && ((this.dtoEmp.esNuevo() == true)
				&& (this.dtoEmp.getPais().getId() == this.getDtoUtil().getPaisParaguay().getId()))) {
			this.rucReadonly = false;
		}
		return rucReadonly;
	}

	public void setRucReadonly(boolean rucReadonly) {
		this.rucReadonly = rucReadonly;
	}

	public boolean isCiReadonly() {
		this.ciReadonly = true;

		if ((this.isDeshabilitado() == false) && (this.dtoEmp.esNuevo() == true)) {
			this.ciReadonly = false;
		}

		return ciReadonly;
	}

	public void setCiReadonly(boolean ciReadonly) {
		this.ciReadonly = ciReadonly;
	}

	@SuppressWarnings("rawtypes")
	public Class getRucEmpresa() {
		return Empresa.class;
	}

	public IiD getIdEmpresa() {
		return this.dtoEmp;
	}

	public boolean isEmpresaSinRuc() {
		boolean out = false;
		String ruc = this.dtoEmp.getRuc();

		if ((ruc.compareTo(Configuracion.RUC_EMPRESA_EXTERIOR) == 0)
				|| (ruc.compareTo(Configuracion.RUC_EMPRESA_LOCAL) == 0)) {
			out = true;
		}

		return out;
	}

	public boolean isBotonRucDisabled() {
		return this.rucReadonly && this.ciReadonly;
	}

	@Command
	@NotifyChange("*")
	public void buscarRUC() throws Exception {
		this.tabDatosVisible = this.ctrlog.buscarRUC(this.getDTOCorriente());
	}

	private boolean tabDatosVisible = true;

	public boolean isTabDatosVisible() {
		return this.tabDatosVisible;
	}

	public void setTabDatosVisible(boolean tabDatosVisible) throws Exception {
		throw new Exception("No se debe setear 'setTabDatosVisible' ");
	}

	// =====================================================

	@Command
	@NotifyChange("*")
	public void buscarGruposEmpresas() throws Exception {

		BuscarElemento b = new BuscarElemento();
		b.setAtributos(new String[] { "descripcion" });
		b.setNombresColumnas(new String[] { "Descripción" });
		b.setClase(EmpresaGrupoSociedad.class);
		b.show(this.dtoEmp.getEmpresaGrupoSociedad().getText());

		if (b.isClickAceptar()) {
			MyArray m = b.getSelectedItem();
			MyPair mp = new MyPair();
			mp.setId(m.getId());
			mp.setText(m.getPos1() + "");
			this.dtoEmp.setEmpresaGrupoSociedad(mp);
		}
	}

	// ============================= RUBROS DE EMPRESA
	// =============================

	private MyPair selectedRubroEmpresa;

	public MyPair getSelectedRubroEmpresa() {
		return selectedRubroEmpresa;
	}

	public void setSelectedRubroEmpresa(MyPair selectedRubroEmpresa) {
		this.selectedRubroEmpresa = selectedRubroEmpresa;
	}

	@Command
	@NotifyChange("*")
	public void agregarRubro() throws Exception {

		BuscarElemento b = new BuscarElemento();
		b.setAtributos(new String[] { "descripcion" });
		b.setNombresColumnas(new String[] { "Descripción" });
		b.setClase(Tipo.class);
		b.addWhere(" tipoTipo.descripcion like 'Rubros Empresas'");
		b.show("%");
		if (b.isClickAceptar()) {

			MyArray m = b.getSelectedItem();
			MyPair mp = new MyPair();
			mp.setId(m.getId());
			mp.setText(m.getPos1() + "");

			for (MyPair rubro : this.dtoEmp.getRubroEmpresas()) {
				if (rubro.getId() == mp.getId()) {
					this.mensajeError("El rubro: " + mp.getText() + "\n Ya esta asignado a esta Empresa..");
					return;
				}
			}

			this.dtoEmp.getRubroEmpresas().add(mp);
		}
	}

	@Command
	@NotifyChange("*")
	public void eliminarRubro() {

		if (this.selectedRubroEmpresa == null) {
			this.mensajeError("Debe seleccionar un Rubro..");
			return;
		}

		if (this.mensajeSiNo("Desea eliminar de la lista el rubro: \n" + this.selectedRubroEmpresa.getText()) == true) {
			this.dtoEmp.getRubroEmpresas().remove(this.selectedRubroEmpresa);
			this.selectedRubroEmpresa = null;
		}
	}

	// ============================= BUSCAR LOCALIDAD
	// =============================

	@Command
	@NotifyChange("*")
	public void buscarLocalidad() throws Exception {

		BuscarElemento b = new BuscarElemento();
		b.setAtributos(new String[] { "localidad", "departamento", "barrio", "pais" });
		b.setNombresColumnas(new String[] { "Localidad", "Departamento", "Barrio", "País" });
		b.setClase(Localidad.class);
		b.setWidth("900px");
		b.show(this.selectedSucursal.getPos6() + "");
		if (b.isClickAceptar()) {
			MyArray m = b.getSelectedItem();
			MyPair mp = new MyPair();
			mp.setId(m.getId());
			mp.setText(m.getPos3() + " - " + m.getPos1() + " - " + m.getPos2());
			this.selectedSucursal.setPos6(mp);
		}
	}

	// ============================= COPIAR RAZON SOCIAL
	// =============================

	@Command
	public void copiarRazonSocial() {
		this.dtoEmp.setNombre(this.dtoEmp.getRazonSocial());
		BindUtils.postNotifyChange(null, null, this.dtoEmp, "nombre");
	}

	// ================================= VERIFICAR PAIS
	// ==============================

	@Command
	@NotifyChange("*")
	public void verificarPais(@BindingParam("comp1") Textbox comp1, @BindingParam("comp2") Textbox comp2) {

		this.ctrlog.verificarPais(this.dtoEmp);

		if (this.ctrlog.isPaisExterior(this.dtoEmp.getPais()) == true) {
			comp2.focus();
		} else {
			comp1.focus();
		}
	}

	// ============================== TAB SUCURSALES / CONTACTOS
	// ==============================

	@DependsOn("dtoEmp.sucursales")
	public String getTabSucursalLabel() {
		String cantSuc = "";
		int cant = this.dtoEmp.getSucursales().size();
		if (cant > 0) {
			cantSuc = "#" + cant;
		}
		return "Sucursales " + cantSuc;
	}

	@DependsOn("dtoEmp.contactos")
	public String getTabContactoLabel() {
		String cantCont = "";
		int cant = this.dtoEmp.getContactos().size();
		if (cant > 0) {
			cantCont = "#" + cant;
		}
		return "Contactos " + cantCont;
	}

	// ====================== Empresa sin Ruc ================
	private static String CLIENTE_SIN_RUC = "CLIENTE SIN RUC";
	private static String PROVEEDOR_SIN_RUC = "PROVEEDOR SIN RUC";
	private static String EMPTY = "";

	public String getLabelEmpresaSinRuc() {
		if (this instanceof ClienteControlBody) {
			return CLIENTE_SIN_RUC;
		} else if (this instanceof ProveedorControlBody) {
			return PROVEEDOR_SIN_RUC;
		} else {
			return EMPTY;
		}
	}

	@Override
	public void showImprimir() throws Exception {
		this.imprimir();
	}

	@Override
	public boolean getImprimirDeshabilitado() {
		return this.dtoEmp.esNuevo();
	}

	private Window selectPrint;

	private void imprimir() {
		this.selectPrint = (Window) Executions.createComponents(Configuracion.EMPRESA_IMPRIMIR_ZUL, this.mainComponent,
				null);
		this.selectPrint.doModal();
	}

	// ***************************** CtaCte
	// ***********************************//

	@SuppressWarnings("static-access")
	@Command
	public void imprimirMovimientosCtaCte() throws Exception {

		if (this.movimientos.size() <= 0) {
			this.mensajeError("- Primero debe seleccionar movimientos");
			return;
		}

		boolean isCliente = false;

		if (this.getTipoControl() == this.TIPO_PROVEEDOR) {
			isCliente = false;
		} else if (this.getTipoControl() == this.TIPO_CLIENTE) {
			isCliente = true;
		}

		ReporteYhaguy rep = new ReporteMovimientosCtaCte(this.movimientos, this.dtoEmp, isCliente);
		rep.setApaisada();
		rep.setBorrarDespuesDeVer(true);
		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}

	ControlCtaCteEmpresa controlCtaCte = new ControlCtaCteEmpresa(null);
	List<MyArray> movimientos = new ArrayList<MyArray>();
	private FiltroCtaCteMovimientos filtroMovimiento = new FiltroCtaCteMovimientos();
	Date fechaDesde = this.m.agregarDias(new Date(), -30);
	Date fechaHasta = new Date();
	MyPair selectedMovimientos = new MyPair();// Todos, Pendientes, Vencidos
	MyPair sucursalSeleccionada = this.getDtoUtil().getSucursalAppTodas();// SucrusaleApp
	boolean buscarPorFecha = true;
	double saldoSuma = 0;
	String infoBusqueda = " Ninguno.";
	private CtaCteEmpresaMovimientoDTO selectedMov;

	public FiltroCtaCteMovimientos getFiltroMovimiento() {
		return filtroMovimiento;
	}

	public void setFiltroMovimiento(FiltroCtaCteMovimientos filtroMovimiento) {
		this.filtroMovimiento = filtroMovimiento;
	}

	@Command
	@NotifyChange({ "movimientos" })
	public void changeFilter() throws Exception {
		this.movimientos = this.getFilterMovs(this.filtroMovimiento);

	}

	public List<MyArray> getFilterMovs(FiltroCtaCteMovimientos filtroMov) throws Exception {
		List<MyArray> movs = new ArrayList<MyArray>();
		String nroComprobante = this.filtroMovimiento.getComprobanteNro().toLowerCase();// .getCategory().toLowerCase();
		String tipoMovimiento = this.filtroMovimiento.getTipoMovimiento().toLowerCase();
		String emision = this.filtroMovimiento.getEmision().toLowerCase();
		String moneda = this.filtroMovimiento.getMoneda().toLowerCase();
		String vencimiento = this.filtroMovimiento.getVencimiento().toLowerCase();
		String sucursal = this.filtroMovimiento.getSucursal().toLowerCase();

		this.buscarMovimientos();

		for (MyArray m : this.movimientos) {

			String emi = this.m.dateToString((Date) m.getPos3(), "dd-MM-yyyy");
			String venci = this.m.dateToString((Date) m.getPos4(), "dd-MM-yyyy");

			if (((String) m.getPos2()).toLowerCase().contains(nroComprobante)
					&& ((String) m.getPos8()).toLowerCase().contains(tipoMovimiento)
					&& (((Date) m.getPos3()).toString().contains(emision))
					&& ((MyPair) m.getPos7()).getText().toLowerCase().contains(moneda) && emi.contains(emision)
					&& venci.contains(vencimiento) && ((String) m.getPos11()).toLowerCase().contains(sucursal)) {
				movs.add(m);
			}
		}
		return movs;
	}

	public CtaCteEmpresaMovimientoDTO getSelectedMov() {
		return selectedMov;
	}

	public void setSelectedMov(CtaCteEmpresaMovimientoDTO selectedMov) {
		this.selectedMov = selectedMov;
	}

	public ControlCtaCteEmpresa getControlCtaCte() {
		return controlCtaCte;
	}

	public void setControlCtaCte(ControlCtaCteEmpresa controlCtaCte) {
		this.controlCtaCte = controlCtaCte;
	}

	public void setCtaCteEmpresa(CtaCteEmpresaDTO ctaCteEmpresa) {
		this.dtoEmp.setCtaCteEmpresa(ctaCteEmpresa);
	}

	public CtaCteEmpresaDTO getCtaCteEmpresa() {
		return this.dtoEmp.getCtaCteEmpresa();
	}

	public List<MyArray> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(List<MyArray> movimientos) {
		this.movimientos = movimientos;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	/**
	 * Setea una fecha y una hora para la busqueda de los movimientos Por
	 * defecto el Zk genera esta fecha:"dd/mm/aaaa 00:00:00" Y con estoy lo que
	 * se quiere lograr es tener:"dd/mm/aaaa 23:59:59" Para que se pueda
	 * realizar la busqueda incluyendo el dia de "fechaHasta"
	 * 
	 * @param fechaHasta
	 */
	@SuppressWarnings("deprecation")
	public void setFechaHasta(Date fechaHasta) {

		this.fechaHasta = fechaHasta;
		/*
		
		 */
		if (this.fechaHasta != null) {
			this.fechaHasta.setHours(23);
			this.fechaHasta.setMinutes(59);
			this.fechaHasta.setSeconds(59);
		}
	}

	public void setSelectedMovimientos(MyPair selectedMovimientos) {
		this.selectedMovimientos = selectedMovimientos;
	}

	public MyPair getSelectedMovimientos() {
		return selectedMovimientos;
	}

	public MyPair getSucursalSeleccionada() {
		return sucursalSeleccionada;
	}

	public void setSucursalSeleccionada(MyPair sucursalSeleccionada) {
		this.sucursalSeleccionada = sucursalSeleccionada;
	}

	public boolean isBuscarPorFecha() {
		return buscarPorFecha;
	}

	public void setBuscarPorFecha(boolean buscarPorFecha) {
		this.buscarPorFecha = buscarPorFecha;
	}

	public double getSaldoSuma() {
		return saldoSuma;
	}

	public void setSaldoSuma(double saldoSuma) {
		this.saldoSuma = saldoSuma;
	}

	public String getInfoBusqueda() {
		return infoBusqueda;
	}

	public void setInfoBusqueda(String infoBusqueda) {
		this.infoBusqueda = infoBusqueda;
	}

	/**
	 * Para desactivar opciones de la vista de CtaCte Retorna False si la
	 * empresa posee CtaCte
	 * 
	 * @return
	 */
	public boolean getInfoCtaCteDisable() {
		if (this.getDtoEmp().getCtaCteEmpresa().esNuevo()) {
			return true;
		}
		return false;
	}

	public boolean ctasCobrar = false;

	/**
	 * Para desactivar opciones de la vista de CtaCte
	 * 
	 * @return
	 */
	public boolean getConsultaCtaCteDisabled() {
		if (ctasCobrar == false) {
			if (this.getInfoCtaCteDisable() && !this.isSiempreHabilitado()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Deshabilita la opcion de busqueda por fecha si el DTO de la CtaCte es
	 * nuevo o si la opcion de buscar por fecha esta deshabilitada.
	 * 
	 * @return
	 */
	public boolean getBuscarPorFechaDisable() {

		if (this.getDtoEmp().getCtaCteEmpresa().esNuevo() || this.buscarPorFecha == false) {
			return true;
		}
		return false;
	}

	/**
	 * Manejador del evento check para habilitar o no la busqueda por fecha no
	 * usa checked porque se tiene que notificar a los cuadros de fecha si estan
	 * habilitados o no.
	 */
	@Command()
	@NotifyChange("buscarPorFechaDisable")
	public void checkBuscarPorFecha() {

		if (this.buscarPorFecha == false) {
			this.setBuscarPorFecha(true);
		} else if (this.buscarPorFecha == true) {
			this.setBuscarPorFecha(false);
		}
	}

	@NotifyChange({ "movimientos", "fechaDesde", "fechaHasta", "selectedMovimientos", "buscarPorFecha", "saldoSuma",
			"infoBusqueda", "sucursalSeleccionada", "movimientosSeleccionados" })
	private void restaurarValoresDefectoCtaCte() {
		this.restoreDefaultValuesOfCtaCte();
	}

	/**
	 * Metodo para limpiar los valores de los atributos detallados mas abajo. Se
	 * ejecuta cada vez que se cambia de empresa.
	 */
	protected void restoreDefaultValuesOfCtaCte() {
		this.setMovimientos(new ArrayList<MyArray>());
		this.setFechaDesde(this.m.agregarDias(new Date(), -30));
		this.setFechaHasta(new Date());
		this.setSelectedMovimientos(new MyPair());
		this.setBuscarPorFecha(true);
		this.setSaldoSuma(0);
		this.setInfoBusqueda(" Ninguno.");
		this.setSucursalSeleccionada(this.getDtoUtil().getSucursalAppTodas());
	}

	/**
	 * Sumo la columna de monto de los movimientos originales y del saldo de los
	 * movimientos obtenidos de la BD.
	 */
	private void sumaSubtotales() {
		double sumaSaldo = 0;
		if (this.movimientos.size() != 0) {
			for (MyArray m : movimientos) {
				sumaSaldo += (Double) m.getPos12();
			}
		}
		this.setSaldoSuma(sumaSaldo);
	}

	/**
	 * Para saber si traer sus movimientos como Cliente o Como proveedor de
	 * acuerdo al tipo de control instanciado. (ControlBodyCliente o
	 * ControlBodyProveedor)
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public MyPair getCaracterMovimientoPorTipoControlInstanciado() throws Exception {

		MyPair caracterMovimiento = new MyPair();
		if (this.getTipoControl() == this.TIPO_PROVEEDOR) {
			caracterMovimiento = this.getDtoUtil().getCtaCteEmpresaCaracterMovProveedor();
		} else if (this.getTipoControl() == this.TIPO_CLIENTE) {
			caracterMovimiento = this.getDtoUtil().getCtaCteEmpresaCaracterMovCliente();
		}
		return caracterMovimiento;

	}

	// ******************************* Buscar Movimientos CtaCte
	// **************************************//
	/**
	 * Realiza la busqueda de los movimientos de la CtaCte de una empresa de
	 * acuerdo a los filtros seleccionados en la vista.
	 * 
	 * @throws Exception
	 */
	@Command()
	@NotifyChange({ "movimientos", "infoBusqueda", "saldoSuma" })
	public void buscarMovimientos() throws Exception {

		MyPair caracterMovimiento = this.getCaracterMovimientoPorTipoControlInstanciado();
		MyPair movimientosSeleccionados = this.getSelectedMovimientos();
		MyPair sucursalSeleccionada = this.getSucursalSeleccionada();
		boolean pendientes = false;
		boolean vencidos = false;
		Date fechaDesde = this.fechaDesde;
		Date fechaHasta = this.fechaHasta;

		this.infoBusqueda = " " + movimientosSeleccionados.getText();

		/*
		 * Para ver si taer los movimientos de acuerdo a las opciones Todos,
		 * Pendientes o Vencidos
		 */
		if (movimientosSeleccionados.getText()
				.compareTo(Configuracion.TIPO_CTA_CTE_EMPRESA_SELECCION_MOV_PENDIENTES) == 0) {
			pendientes = true;
		} else if (movimientosSeleccionados.getText()
				.compareTo(Configuracion.TIPO_CTA_CTE_EMPRESA_SELECCION_MOV_VENCIDOS) == 0) {
			vencidos = true;
		} else if (movimientosSeleccionados.getText()
				.compareTo(Configuracion.TIPO_CTA_CTE_EMPRESA_SELECCION_MOV_TODOS) == 0) {
			vencidos = false;
			pendientes = false;
		} else {
			// Ver para lanzar un popup aca que diga que tiene que elegir o
			// todos o pendientes o vencidos
			mensajeInfo("No se han seleccinado movimientos");
			this.infoBusqueda = " Ninguno.";
			return;
		}

		if (fechaHasta.compareTo(fechaDesde) < 0) {
			mensajeInfo("La fecha desde debe ser menor que la fecha hasta");
			this.infoBusqueda = " Ninguno.";
			return;
		}

		/*
		 * Setea las fechas de busqueda a null si esta deshabilitada la busqueda
		 * por fecha. De lo contrario se agrega al String que mostara la
		 * informacion de los filtros seleccionados, el rango de fechas
		 * seleccionado
		 */
		if (this.isBuscarPorFecha() == false) {
			fechaDesde = null;
			fechaHasta = null;
		} else {
			this.infoBusqueda += " entre el " + m.dateToString(fechaDesde, "dd/MM/yyyy") + " hasta el "
					+ m.dateToString(fechaHasta, "dd/MM/yyyy");
		}

		/* Obtiene los movimientos de acuerdo a los parametros */
		this.movimientos = this.controlCtaCte.getCtaCteEmpresaMovimientosMyArray(this.getDtoEmp(), caracterMovimiento,
				sucursalSeleccionada, fechaDesde, fechaHasta, pendientes, vencidos);

		this.infoBusqueda += " (" + this.movimientos.size() + ") ";

		this.sumaSubtotales();
	}

	// ************************************** PopPups de CtaCte
	// **************************************//
	public boolean isVerDetallesMovimientoDisabled() throws Exception {
		if (this.operacionHabilitada("AbrirDetalleMovimientoCtaCte", ID.F_CTA_CTE_EMPRESA_DETALLE_MOVIMIENTO))
			return false;
		return true;
	}

	/**
	 * Mostrar los detalles del movimiento seleccionado
	 * 
	 * @param movimiento
	 * @throws Exception
	 */
	@Command
	@NotifyChange("*")
	public void mostrarMovimientoCtaCte(@BindingParam("movimiento") MyArray movimiento) throws Exception {
		if (isVerDetallesMovimientoDisabled()) {
			this.mensajeError(
					"No posee los permisos necesarios para acceder a esta operacion: [Abrir Detalle Movimiento Cta. Cte.]");
			return;
		}
		CtaCteEmpresaMovimientoDTO movimientoDto = new CtaCteEmpresaMovimientoDTO();
		try {

			RegisterDomain rr = RegisterDomain.getInstance();
			CtaCteEmpresaMovimiento movimientoDom = rr.getCtaCteEmpresaMovimientoById(movimiento.getId());
			movimientoDto = (CtaCteEmpresaMovimientoDTO) new AssemblerCtaCteEmpresaMovimiento()
					.domainToDto(movimientoDom);
			this.setSelectedMov(movimientoDto);

			WindowPopup win = new WindowPopup();
			win.setDato(this);
			win.setHigth("580px");
			win.setWidth("850px");
			win.setTitulo("Detalles del Movimiento");
			win.setModo(WindowPopup.NUEVO);
			win.show(Configuracion.CTACTE_EMPRESA_DETALLES_MOVIMIENTO_ZUL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean getInformacionDeshabilitado() throws Exception {
		return this.getInfoCtaCteDisable();
	}

	/**
	 * Mostrar la informacion general de la CtaCte
	 */
	@Override
	public void showInformacion() throws Exception {
		try {

			WindowPopup win = new WindowPopup();
			win.setDato(this);
			win.setHigth("300px");
			win.setWidth("400px");
			win.setSoloBotonCerrar();
			win.setTitulo("Informacion de la Cuenta Corriente");
			win.setModo(WindowPopup.SOLO_LECTURA);
			win.show(Configuracion.CTACTE_EMPRESA_MAS_INFORMACION_ZUL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// **********************************AgendaCtaCte**************************************//

	private List<Object[]> eventosAgenda = new ArrayList<Object[]>();

	public List<Object[]> getEventosAgenda() {
		return eventosAgenda;
	}

	public void setEventosAgenda(List<Object[]> eventosAgenda) {
		this.eventosAgenda = eventosAgenda;
	}

	@Override
	public int getCtrAgendaTipo() {
		return ControlAgendaEvento.NORMAL;
	}

	@Override
	public String getCtrAgendaKey() {
		String key = "";
		try {
			key = this.dtoEmp.getId().toString();
		} catch (Exception e) {
			key = "ERROR-AVISAR A INFORMATICA-" + e.getMessage();
		}
		return key;
	}

	@Override
	public String getCtrAgendaTitulo() {
		return "Agenda";
	}

	@Override
	public boolean getAgendaDeshabilitado() throws Exception {
		return this.getInfoCtaCteDisable();
	}

	public void addEventoAgenda(int tipoAgenda, String claveAgenda, int tipoDetalle, String texto, String link) {
		Object[] evento = new Object[5];
		evento[0] = tipoAgenda;
		evento[1] = claveAgenda;
		evento[2] = tipoDetalle;
		evento[3] = texto;
		evento[4] = link;
		this.eventosAgenda.add(evento);
	}
}
