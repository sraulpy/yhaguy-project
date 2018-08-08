package com.yhaguy.gestion.definiciones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.AssemblerUtil;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Banco;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.CtaCteLineaCredito;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.Talonario;
import com.yhaguy.domain.Timbrado;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.VendedorComision;
import com.yhaguy.inicio.AccesoDTO;

@SuppressWarnings("unchecked")
public class DefinicionesViewModel extends SimpleViewModel {

	static final String FOLDER = "/yhaguy/gestion/definiciones/";

	static final String ZUL_TESORERIA = FOLDER + "definicionesTesoreria.zul";
	static final String ZUL_COMPRAS = FOLDER + "definicionesCompras.zul";
	static final String ZUL_VENTAS = FOLDER + "definicionesVentas.zul";
	static final String ZUL_STOCK = FOLDER + "definicionesStock.zul";
	static final String ZUL_LOGISTICA = FOLDER + "definicionesLogistica.zul";
	static final String ZUL_CONTABILIDAD = FOLDER + "definicionesContabilidad.zul";
	static final String ZUL_RRHH = FOLDER + "definicionesRRHH.zul";
	static final String ZUL_SISTEMA = FOLDER + "definicionesSistema.zul";

	static final String ZUL_TALONARIO = FOLDER + "editTalonarios.zul";
	static final String ZUL_TIPO = FOLDER + "definicionesTipo.zul";
	static final String ZUL_BANCO = FOLDER + "definicionesBancoPopup.zul";
	static final String ZUL_LINEA_CREDITO = FOLDER + "definicionesCtaCteLineaCredito.zul";
	static final String ZUL_CHEQUERAS = FOLDER + "editChequeras.zul";
	static final MyArray TESORERIA = new MyArray("Tesorería", 1, ZUL_TESORERIA);
	static final MyArray COMPRAS = new MyArray("Compras", 2, ZUL_COMPRAS);
	static final MyArray VENTAS = new MyArray("Ventas", 3, ZUL_VENTAS);
	static final MyArray STOCK = new MyArray("Stock", 4, ZUL_STOCK);
	static final MyArray LOGISTICA = new MyArray("Logística", 5, ZUL_LOGISTICA);
	static final MyArray CONTABILIDAD = new MyArray("Contabilidad", 6, ZUL_CONTABILIDAD);
	static final MyArray RRHH = new MyArray("Recursos Humanos", 7, ZUL_RRHH);
	static final MyArray SISTEMA = new MyArray("Sistema", 8, ZUL_SISTEMA);

	private MyArray selectedItem = SISTEMA;
	private MyArray selectedTalonario;
	private MyArray selectedChequera;
	private MyPair currentTipeGeneric;

	/**
	 * Atributos Funcionario
	 */
	private MyPair selectedCargoFuncionario;
	private MyPair selectedEstadoFuncionario;

	/**
	 * Atributos tesoreria
	 */
	private MyArray selectedBanco;
	private MyPair selectedBancoTercero;
	private MyPair selectedTipoCuentaBanco;
	private MyArray selectedLineaDeCredito;
	private AssemblerUtil ass = new AssemblerUtil();
	
	/**
	 * Atributos de Ventas
	 */
	private Tipo selectedZona;
	private Tipo nuevaZona = new Tipo();
	
	private Proveedor selectedProveedor;
	private Tipo selectedFamilia;

	@Init(superclass = true)
	public void init() {
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
	public void addTalonario() throws Exception {
		this.inicializar();
		this.showTalonario(true);
	}

	@Command
	@NotifyChange("*")
	public void deleteTalonario() throws Exception {
		if (this.mensajeSiNo("Esta seguro de eliminar " + "el talonario seleccionado..") == false)
			return;
		this.deleteTalonario_();
	}

	@Command
	public void editTalonario() throws Exception {
		this.showTalonario(false);
	}
	
	@Command
	@NotifyChange({ "zonas", "nuevaZona", "selectedZona" })
	public void addZona(@BindingParam("comp") Popup comp) throws Exception {
		if (this.nuevaZona.getDescripcion() == null || this.nuevaZona.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nuevaZona.setDescripcion(this.nuevaZona.getDescripcion().toUpperCase());
		this.nuevaZona.setTipoTipo(rr.getTipoTipoPorDescripcion(Configuracion.ID_TIPO_ZONAS));
		rr.saveObject(this.nuevaZona, this.getLoginNombre());
		this.nuevaZona = new Tipo();
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO..");
	}
	
	@Command
	@NotifyChange({ "zonas", "nuevaZona", "selectedZona" })
	public void saveZona(@BindingParam("comp") Popup comp) throws Exception {
		if (this.selectedZona.getDescripcion() == null || this.selectedZona.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedZona.setDescripcion(this.selectedZona.getDescripcion().toUpperCase());
		rr.saveObject(this.selectedZona, this.getLoginNombre());
		this.nuevaZona = new Tipo();
		this.selectedZona = null;
		comp.close();
		Clients.showNotification("REGISTRO MODIFICADO..");
	}
	
	@Command
	@NotifyChange({ "zonas", "nuevaZona", "selectedZona" })
	public void deleteZona() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		if (rr.getEmpresasGeolocalizadasPorZona(this.selectedZona.getDescripcion()).size() > 0) {
			Clients.showNotification("No se puede eliminar, existen clientes asignados a esta zona..", 
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		rr.deleteObject(this.selectedZona);
		this.selectedZona = null;
		Clients.showNotification("REGISTRO ELIMINADO..");
	}

	/**
	 * Para agregar cargos nuevos
	 * 
	 * @throws Exception
	 */
	@Command
	@NotifyChange("*")
	public void addCargoFuncionario() throws Exception {
		this.inicializarCargoFuncionario();
		this.showCargoFuncionario(true);
		this.getUtilDto().setFuncionarioCargo(this.getCargosFuncionario());
	}

	/**
	 * Para editar cargos existentes
	 * 
	 * @throws Exception
	 */
	@Command
	@NotifyChange("*")
	public void editCargoFuncionario() throws Exception {
		if (this.selectedCargoFuncionario != null && this.selectedCargoFuncionario.esNuevo() == false) {
			this.showCargoFuncionario(false);
			this.getUtilDto().setFuncionarioCargo(this.getCargosFuncionario());
		} else {
			this.mensajeError("- Debe seleccionar un elemento.");
		}
	}

	/**
	 * Para eliminar cargos existentes. Si ya esta asociado a algun objeto no le
	 * permite la eliminacion
	 * 
	 * @throws Exception
	 */
	@Command
	@NotifyChange("*")
	public void deleteCargoFuncionario() throws Exception {

		boolean deleteOk = false;
		if (this.selectedCargoFuncionario != null && this.selectedCargoFuncionario.esNuevo() == false) {
			if (this.mensajeSiNo("Esta seguro de eliminar el cargo seleccionado?") == false) {
				return;
			} else {
				deleteOk = this.deleteTipo(this.selectedCargoFuncionario.getId());
				if (deleteOk == true) {
					this.selectedCargoFuncionario = null;
					this.mensajePopupTemporal("Cargo eliminado.");
					this.getUtilDto().setFuncionarioCargo(this.getCargosFuncionario());
				}
			}
		} else {
			this.mensajeError("- Debe seleccionar un elemento.");
		}

	}

	/**
	 * Sirve para agregar nuevos estados de funcionario
	 * 
	 * @throws Exception
	 */
	@Command
	@NotifyChange("*")
	public void addEstadoFuncionario() throws Exception {
		this.inicializarEstadoFuncionario();
		this.showEstadoFuncionario(true);
		this.getUtilDto().setFuncionarioEstado(this.getEstadosFuncionario());
	}

	/**
	 * Sirve para editar estados de funcionario
	 * 
	 * @throws Exception
	 */
	@Command
	@NotifyChange("*")
	public void editEstadoFuncionario() throws Exception {
		if (this.selectedEstadoFuncionario != null && this.selectedEstadoFuncionario.esNuevo() == false) {
			this.showEstadoFuncionario(false);
			this.getUtilDto().setFuncionarioEstado(this.getEstadosFuncionario());
		} else {
			this.mensajeError("- Debe seleccionar un elemento.");
		}
	}

	/**
	 * Sirve para eliminar estados de funcionario
	 * 
	 * @throws Exception
	 */
	@Command
	@NotifyChange("*")
	public void deleteEstadoFuncionaro() throws Exception {
		boolean deleteOk = false;
		if (this.selectedEstadoFuncionario != null && this.selectedEstadoFuncionario.esNuevo() == false) {

			if (this.mensajeSiNo("Esta seguro de eliminar el estado seleccionado?") == false) {
				return;
			} else {
				deleteOk = this.deleteTipo(this.selectedEstadoFuncionario.getId());
				if (deleteOk == true) {
					this.selectedEstadoFuncionario = null;
					this.mensajePopupTemporal("Cargo eliminado.");
					this.getUtilDto().setFuncionarioEstado(this.getEstadosFuncionario());
				}
			}

		} else {
			this.mensajeError("- Debe seleccionar un elemento.");
		}

	}

	/**
	 * Sirve para agregar bancos terceros OJO: los bancos estan asociados a los
	 * bancos terceros
	 * 
	 * @throws Exception
	 */
	@Command
	@NotifyChange("*")
	public void addBancoTercero() throws Exception {
		this.inicializarBancoTercero();
		this.showBancosTipo(true);
		this.getUtilDto().setBancosTerceros(this.getBancosTipo());
	}

	/**
	 * Sirve para editar bancos terceros OJO: los bancos estan asociados a los
	 * bancos terceros
	 * 
	 * @throws Exception
	 */
	@Command
	@NotifyChange("*")
	public void editBancoTercero() throws Exception {
		if (this.selectedBancoTercero != null && this.selectedBancoTercero.esNuevo() == false) {
			this.showBancosTipo(false);
			this.getUtilDto().setBancosTerceros(this.getBancosTipo());
		} else {
			this.mensajeError("- Debe seleccionar un elemento.");
		}
	}

	/**
	 * Sirve para eliminar bancos terceros OJO: los bancos estan asociados a los
	 * bancos terceros
	 * 
	 * @throws Exception
	 */
	@Command
	@NotifyChange("*")
	public void deleteBancoTerceros() throws Exception {
		boolean deleteOk = false;
		if (this.selectedBancoTercero != null && this.selectedBancoTercero.esNuevo() == false) {

			if (this.mensajeSiNo("Esta seguro de eliminar el banco seleccionado?") == false) {
				return;
			} else {
				deleteOk = this.deleteTipo(this.selectedBancoTercero.getId());
				if (deleteOk == true) {
					this.selectedBancoTercero = null;
					this.mensajePopupTemporal("Banco tercero eliminado.");
					this.getUtilDto().setBancosTerceros(this.getBancosTipo());
				}
			}

		} else {
			this.mensajeError("Debe seleccionar un elemento.");
		}

	}

	@Command
	@NotifyChange("*")
	public void addTipoCuentaBanco() throws Exception {
		this.inicializarTipoCuentaBanco();
		this.showBancoTiposCuenta(true);
		this.getUtilDto().setBancoCtaTipos(this.getBancoTiposCuenta());
	}

	@Command
	@NotifyChange("*")
	public void editTipoCuentaBanco() throws Exception {
		if (this.selectedTipoCuentaBanco != null && this.selectedTipoCuentaBanco.esNuevo() == false) {
			this.showBancoTiposCuenta(false);
			this.getUtilDto().setBancoCtaTipos(this.getBancoTiposCuenta());
		} else {
			this.mensajeError("- Debe seleccionar un elemento.");
		}
	}

	@Command
	@NotifyChange("*")
	public void deleteTipoCuentaBanco() throws Exception {
		boolean deleteOk = false;
		if (this.selectedTipoCuentaBanco != null && this.selectedTipoCuentaBanco.esNuevo() == false) {

			if (this.mensajeSiNo("Esta seguro de eliminar el tipo de cuenta seleccionado?") == false) {
				return;
			} else {
				deleteOk = this.deleteTipo(this.selectedTipoCuentaBanco.getId());
				if (deleteOk == true) {
					this.selectedTipoCuentaBanco = null;
					this.mensajePopupTemporal("Tipo de cuenta eliminado.");
					this.getUtilDto().setBancoCtaTipos(this.getBancoTiposCuenta());
				}
			}

		} else {
			this.mensajeError("Debe seleccionar un elemento.");
		}
	}

	@Command
	@NotifyChange("*")
	public void addBanco() throws Exception {
		this.inicializarBanco();
		this.showBancos(true);
		this.updateBancosUtilDto();
	}

	@Command
	@NotifyChange("*")
	public void editBanco() throws Exception {
		if (this.selectedBanco != null && this.selectedBanco.esNuevo() == false) {
			this.showBancos(false);
			this.updateBancosUtilDto();
		} else {
			this.mensajeError("- Debe seleccionar un elemento.");
		}
	}

	@Command
	@NotifyChange("*")
	public void deleteBanco() throws Exception {
		boolean deleteOk = true;

		if (this.selectedBanco != null && this.selectedBanco.esNuevo() == false) {

			if (this.mensajeSiNo("Esta seguro de eliminar el banco seleccionado?") == false) {
				return;
			} else {

				long id = this.selectedBanco.getId();
				RegisterDomain rr = RegisterDomain.getInstance();

				try {
					rr.deleteObject(Banco.class.getName(), id);
				} catch (ConstraintViolationException cV) {
					this.mensajeError("- No se puede eliminar elemento ya que esta asociado a otro elemento.");
					deleteOk = false;
				}

				if (deleteOk == true) {
					this.selectedBanco = null;
					this.mensajeInfo("Banco eliminado..");
					this.updateBancosUtilDto();
				} else {
					return;
				}
			}

		} else {
			this.mensajeError("Debe seleccionar un elemento.");
		}
	}

	@Command
	@NotifyChange("*")
	public void addCtaCteLineaCredito() throws Exception {
		this.inicializarLineaCredito();
		this.showLineaCredito(true);
		this.getUtilDto().setCtaCteLineaCredito(this.getCtaCteLineasCredito());
	}

	@Command
	@NotifyChange("*")
	public void editCtaCteLineaCredito() throws Exception {
		if (this.selectedLineaDeCredito != null && this.selectedLineaDeCredito.esNuevo() == false) {
			this.showLineaCredito(false);
			this.getUtilDto().setCtaCteLineaCredito(this.getCtaCteLineasCredito());
		} else {
			this.mensajeError("- Debe seleccionar un elemento.");
		}
	}

	@Command
	@NotifyChange("*")
	public void deleteCtaCteLineaCredito() throws Exception {
		boolean deleteOk = true;

		if (this.selectedLineaDeCredito != null && this.selectedLineaDeCredito.esNuevo() == false) {

			if (this.mensajeSiNo("Esta seguro de eliminar el elemento seleccionado?") == false) {
				return;
			} else {

				long id = this.selectedLineaDeCredito.getId();
				RegisterDomain rr = RegisterDomain.getInstance();

				try {
					rr.deleteObject(CtaCteLineaCredito.class.getName(), id);
				} catch (ConstraintViolationException cV) {
					this.mensajeError("- No se puede eliminar elemento ya que esta asociado a otro elemento.");
					cV.printStackTrace();
					deleteOk = false;
				}
				
				if (deleteOk == true) {
					this.selectedLineaDeCredito = null;
					this.mensajeInfo("Elemento eliminado..");
					this.updateCtaCteLineaCredito();
				} else {
					return;
				}
			}

		} else {
			this.mensajeError("Debe seleccionar un elemento.");
		}
	}
	
	@Command
	public void updateComision(@BindingParam("vendedor") Funcionario vendedor) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		VendedorComision com = rr.getVendedorComision(this.selectedProveedor.getId(), vendedor.getId());
		if (com == null) {
			com = new VendedorComision();
			com.setId_proveedor(this.selectedProveedor.getId());
			com.setId_funcionario(vendedor.getId());
		}
		com.setPorc_comision(vendedor.getPorc_comision());
		com.setPorc_comision_cobros(vendedor.getPorc_comision_cobros());
		rr.saveObject(com, this.getLoginNombre());
	}

	/**
	 * FUNCIONES..
	 */

	/**
	 * inicializa los datos del talonario..
	 */
	private void inicializar() {
		this.selectedTalonario = new MyArray();
		this.selectedTalonario.setPos1("...");
		this.selectedTalonario.setPos2(this.getEstablecimiento());
		this.selectedTalonario.setPos3((int) 0);
		this.selectedTalonario.setPos4((long) 0);
		this.selectedTalonario.setPos5((long) 0);
		this.selectedTalonario.setPos6(new MyArray());
		this.selectedTalonario.setPos7(new Date());
		this.selectedTalonario.setPos9(this.getSucursal());
		this.selectedTalonario.setPos10(new ArrayList<MyPair>());
	}

	private void inicializarCargoFuncionario() {
		this.selectedCargoFuncionario = new MyPair();
		this.selectedCargoFuncionario.setText("");
		this.selectedCargoFuncionario.setSigla("FUN-CRG-");
	}

	private void inicializarEstadoFuncionario() {
		this.selectedEstadoFuncionario = new MyPair();
		this.selectedEstadoFuncionario.setText("");
		this.selectedEstadoFuncionario.setSigla("FUN-EST-");
	}

	private void inicializarBancoTercero() {
		this.selectedBancoTercero = new MyPair();
		this.selectedBancoTercero.setText("");
		this.selectedBancoTercero.setSigla("BAN-TER-");
	}

	private void inicializarTipoCuentaBanco() {
		this.selectedTipoCuentaBanco = new MyPair();
		this.selectedTipoCuentaBanco.setText("");
		this.selectedTipoCuentaBanco.setSigla("BCO-");
	}

	private void inicializarBanco() {
		this.selectedBanco = new MyArray();
		this.selectedBanco.setPos1("");
		this.selectedBanco.setPos2("");
		this.selectedBanco.setPos3("");
		this.selectedBanco.setPos4("");
		this.selectedBanco.setPos5("");
		this.selectedBanco.setPos6(new ArrayList<MyArray>());
		this.selectedBanco.setPos7(new MyPair());
	}

	private void inicializarLineaCredito() {
		this.selectedLineaDeCredito = new MyArray();
		this.selectedLineaDeCredito.setPos2((double) 0.0);
		this.selectedLineaDeCredito.setPos1("");
	}

	/**
	 * despliega la ventana de datos del talonario..
	 */
	private void showTalonario(boolean add) throws Exception {
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setCheckAC(new ValidadorAddTalonario());
		wp.setHigth("400px");
		wp.setWidth("600px");
		wp.setTitulo((add ? "Agregar" : "Modificar") + " Talonarios..");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(ZUL_TALONARIO);

		if (wp.isClickAceptar()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			MyArray timb = (MyArray) this.selectedTalonario.getPos6();
			Timbrado t;
			if (timb.esNuevo()) {
				t = new Timbrado();
				t.setNumero((String) timb.getPos1());
				t.setVencimiento((Date) timb.getPos2());
				rr.saveObject(t, this.getLoginNombre());
				Proveedor p = rr.getProveedorById(Configuracion.ID_PROVEEDOR_YHAGUY_MRA);
				p.getTimbrados().add(t);
				rr.saveObject(p, this.getLoginNombre());
			} else {
				t = (Timbrado) rr.getObject(Timbrado.class.getName(), timb.getId());
			}

			SucursalApp suc = rr.getSucursalAppById(this.getSucursal().getId());
			Set<TipoMovimiento> tms = new HashSet<TipoMovimiento>();
			List<MyPair> _tms = (List<MyPair>) this.selectedTalonario.getPos10();
			for (MyPair tm : _tms) {
				tms.add(rr.getTipoMovimientoById(tm.getId()));
			}

			Talonario tal = new Talonario();

			if (add == false)
				tal = (Talonario) rr.getObject(Talonario.class.getName(), this.selectedTalonario.getId());

			tal.setNumero(this.getNumeroTalonario());
			tal.setFecha(new Date());
			tal.setBocaExpedicion((int) this.selectedTalonario.getPos2());
			tal.setPuntoExpedicion((int) this.selectedTalonario.getPos3());
			tal.setDesde((long) this.selectedTalonario.getPos4());
			tal.setHasta((long) this.selectedTalonario.getPos5());
			tal.setSaldo(tal.getHasta() - tal.getDesde());
			tal.setVencimiento((Date) this.selectedTalonario.getPos7());
			tal.setTimbrado(t);
			tal.setSucursal(suc);
			tal.setMovimientos(tms);
			rr.saveObject(tal, this.getLoginNombre());

			AutoNumeroControl.inicializarAutonumero(tal.getNumero(), tal.getDesde(), tal.getHasta(), "");

			Clients.showNotification("Registro " + (add ? "agregado.." : "modificado.."));
		}
	}

	/**
	 * elimina un talonario del sistema..
	 */
	private void deleteTalonario_() throws Exception {
		long id = this.selectedTalonario.getId();
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.deleteObject(Talonario.class.getName(), id);
		this.selectedTalonario = null;
		Clients.showNotification("Talonario eliminado..");
	}

	private boolean deleteTipo(long id) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		boolean out = true;
		try {
			rr.deleteObject(Tipo.class.getName(), id);
		} catch (ConstraintViolationException cV) {
			this.mensajeError("- No se puede eliminar elemento ya que esta asociado a otro elemento.");
			out = false;
		}
		return out;
	}

	private void showCargoFuncionario(boolean add) throws Exception {

		this.setCurrentTipeGeneric(this.getSelectedCargoFuncionario());
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setCheckAC(new ValidadorTipo(this));
		wp.setHigth("200px");
		wp.setWidth("400px");
		wp.setTitulo((add ? "Agregar" : "Modificar") + " cargo de funcionario");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(ZUL_TIPO);

		if (wp.isClickAceptar()) {

			RegisterDomain rr = RegisterDomain.getInstance();
			this.setSelectedCargoFuncionario(this.getCurrentTipeGeneric());
			MyPair cargoMP = this.selectedCargoFuncionario;
			Tipo cargoT;
			if (add == true) {
				cargoT = new Tipo();
				cargoT.setTipoTipo(rr.getTipoTipoPorDescripcion(Configuracion.ID_TIPO_CARGO_FUNCIONARIO));
			} else {
				cargoT = (Tipo) rr.getObject(Tipo.class.getName(), cargoMP.getId());
			}

			cargoT.setSigla(this.getSelectedCargoFuncionario().getSigla());
			cargoT.setDescripcion(this.getSelectedCargoFuncionario().getText());

			rr.saveObject(cargoT, this.getLoginNombre());

			Clients.showNotification("Registro " + (add ? "agregado.." : "modificado.."));
		}
	}

	private void showEstadoFuncionario(boolean add) throws Exception {

		this.setCurrentTipeGeneric(this.getSelectedEstadoFuncionario());
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setCheckAC(new ValidadorTipo(this));
		wp.setHigth("200px");
		wp.setWidth("400px");
		wp.setTitulo((add ? "Agregar" : "Modificar") + " estado de funcionario");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(ZUL_TIPO);

		if (wp.isClickAceptar()) {

			RegisterDomain rr = RegisterDomain.getInstance();
			this.setSelectedEstadoFuncionario(this.getCurrentTipeGeneric());
			MyPair estadoMP = this.selectedEstadoFuncionario;
			Tipo estadoT;
			if (add == true) {
				estadoT = new Tipo();
				estadoT.setTipoTipo(rr.getTipoTipoPorDescripcion(Configuracion.ID_TIPO_ESTADO_FUNCIONARIO));
			} else {
				estadoT = (Tipo) rr.getObject(Tipo.class.getName(), estadoMP.getId());
			}

			estadoT.setSigla(this.getSelectedEstadoFuncionario().getSigla());
			estadoT.setDescripcion(this.getSelectedEstadoFuncionario().getText());

			rr.saveObject(estadoT, this.getLoginNombre());

			Clients.showNotification("Registro " + (add ? "agregado.." : "modificado.."));
		}
	}

	private void showBancosTipo(boolean add) throws Exception {

		this.setCurrentTipeGeneric(this.getSelectedBancoTercero());
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setCheckAC(new ValidadorTipo(this));
		wp.setHigth("200px");
		wp.setWidth("400px");
		wp.setTitulo((add ? "Agregar" : "Modificar") + " banco tercero");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(ZUL_TIPO);

		if (wp.isClickAceptar()) {

			RegisterDomain rr = RegisterDomain.getInstance();
			this.setSelectedBancoTercero(this.getCurrentTipeGeneric());
			MyPair bancoTercero = this.selectedBancoTercero;
			Tipo bt;
			if (add == true) {
				bt = new Tipo();
				bt.setTipoTipo(rr.getTipoTipoPorDescripcion(Configuracion.ID_TIPO_BANCOS_TERCEROS));
			} else {
				bt = (Tipo) rr.getObject(Tipo.class.getName(), bancoTercero.getId());
			}

			bt.setSigla(this.getSelectedBancoTercero().getSigla());
			bt.setDescripcion(this.getSelectedBancoTercero().getText());

			rr.saveObject(bt, this.getLoginNombre());

			Clients.showNotification("Registro " + (add ? "agregado.." : "modificado.."));
		}
	}

	private void showBancoTiposCuenta(boolean add) throws Exception {

		this.setCurrentTipeGeneric(this.getSelectedTipoCuentaBanco());
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setCheckAC(new ValidadorTipo(this));
		wp.setHigth("200px");
		wp.setWidth("400px");
		wp.setTitulo((add ? "Agregar" : "Modificar") + " tipos de cuenta bancaria");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(ZUL_TIPO);

		if (wp.isClickAceptar()) {

			RegisterDomain rr = RegisterDomain.getInstance();
			this.setSelectedTipoCuentaBanco(this.getCurrentTipeGeneric());
			MyPair tipoCuenta = this.selectedTipoCuentaBanco;
			Tipo t;
			if (add == true) {
				t = new Tipo();
				t.setTipoTipo(rr.getTipoTipoPorDescripcion(Configuracion.ID_TIPO_BANCO_CUENTA));
			} else {
				t = (Tipo) rr.getObject(Tipo.class.getName(), tipoCuenta.getId());
			}

			t.setSigla(this.getSelectedTipoCuentaBanco().getSigla());
			t.setDescripcion(this.getSelectedTipoCuentaBanco().getText());

			rr.saveObject(t, this.getLoginNombre());

			Clients.showNotification("Registro " + (add ? "agregado.." : "modificado.."));
		}
	}

	private void showBancos(boolean add) throws Exception {

		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setCheckAC(new ValidadorBanco(this));
		wp.setDato(this);
		wp.setHigth("300px");
		wp.setWidth("400px");
		wp.setTitulo((add ? "Agregar" : "Modificar") + " banco");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(ZUL_BANCO);

		if (wp.isClickAceptar()) {

			RegisterDomain rr = RegisterDomain.getInstance();
			MyArray bancoMA = this.selectedBanco;
			Banco banco;
			if (add == true) {
				banco = new Banco();
			} else {
				banco = (Banco) rr.getObject(Banco.class.getName(), bancoMA.getId());
			}

			banco.setId(bancoMA.getId());
			banco.setDescripcion((String) bancoMA.getPos1());
			banco.setDireccion((String) bancoMA.getPos2());
			banco.setTelefono((String) bancoMA.getPos3());
			banco.setCorreo((String) bancoMA.getPos4());
			banco.setContacto((String) bancoMA.getPos5());

			Tipo bancoTipo = new Tipo();
			bancoTipo.setId(((MyPair) bancoMA.getPos7()).getId());
			bancoTipo.setSigla(((MyPair) bancoMA.getPos7()).getSigla());
			bancoTipo.setDescripcion(((MyPair) bancoMA.getPos7()).getText());
			banco.setBancoTipo(bancoTipo);

			rr.saveObject(banco, this.getLoginNombre());

			Clients.showNotification("Registro " + (add ? "agregado.." : "modificado.."));
		}
	}

	private void showLineaCredito(boolean add) throws Exception {

		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setCheckAC(new ValidadorLineaCredito(this));
		wp.setDato(this);
		wp.setHigth("200px");
		wp.setWidth("400px");
		wp.setTitulo((add ? "Agregar" : "Modificar") + " linea de crédito");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(ZUL_LINEA_CREDITO);

		if (wp.isClickAceptar()) {

			RegisterDomain rr = RegisterDomain.getInstance();
			MyArray lineaCreditoMA = this.selectedLineaDeCredito;
			CtaCteLineaCredito lineaCred;
			if (add == true) {
				lineaCred = new CtaCteLineaCredito();
			} else {
				lineaCred = (CtaCteLineaCredito) rr.getObject(CtaCteLineaCredito.class.getName(),
						lineaCreditoMA.getId());
			}

			lineaCred.setId(lineaCreditoMA.getId());
			lineaCred.setDescripcion((String) lineaCreditoMA.getPos1());
			lineaCred.setLinea((double) lineaCreditoMA.getPos2());

			rr.saveObject(lineaCred, this.getLoginNombre());

			Clients.showNotification("Registro " + (add ? "agregado.." : "modificado.."));
		}
	}
	

	/**
	 * VALIDACIONES..
	 */

	/**
	 * Validador agregar Talonario..
	 */
	class ValidadorAddTalonario implements VerificaAceptarCancelar {

		private String mensaje;

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";

			int punto = (int) selectedTalonario.getPos3();
			long desde = (long) selectedTalonario.getPos4();
			long hasta = (long) selectedTalonario.getPos5();
			MyArray timb_ = (MyArray) selectedTalonario.getPos6();
			String timb = (String) timb_.getPos1();
			List<MyPair> movs = (List<MyPair>) selectedTalonario.getPos10();

			if (punto <= 0) {
				out = false;
				this.mensaje += "\n -Debe ingresar el Punto de expedición..";
			}

			if (desde <= 0 || hasta <= 0) {
				out = false;
				this.mensaje += "\n -Debe ingresar el desde / hasta del talonario..";
			}

			if (timb.isEmpty()) {
				out = false;
				this.mensaje += "\n -Debe asignar un timbrado..";
			}

			if (movs.size() == 0) {
				out = false;
				this.mensaje += "\n -Debe especificar los tipos de movimientos..";
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
	 * Validador agregar Chequera..
	 */
	class ValidadorAddChequera implements VerificaAceptarCancelar {

		private String mensaje;

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";

			MyPair banco = (MyPair) selectedChequera.getPos2();
			String descripcion = (String) selectedChequera.getPos3();
			long desde = (long) selectedChequera.getPos4();
			long hasta = (long) selectedChequera.getPos5();
			MyArray tipo = (MyArray) selectedChequera.getPos6();
			
			if (banco.esNuevo()) {
				out = false;
				this.mensaje += "\n - Debe seleccionar el Banco..";
			}
			
			if (descripcion.isEmpty()) {
				out = false;
				this.mensaje += "\n - Debe ingresar una descripción..";
			}

			if (desde <= 0 || hasta <= 0) {
				out = false;
				this.mensaje += "\n - Debe ingresar el desde / hasta del talonario..";
			}

			if (((String)tipo.getPos1()).isEmpty()) {
				out = false;
				this.mensaje += "\n - Debe asignar un tipo de chequera..";
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

	class ValidadorBanco implements VerificaAceptarCancelar {

		private String mensaje;
		DefinicionesViewModel defVM;
		RegisterDomain rr = RegisterDomain.getInstance();

		public ValidadorBanco(DefinicionesViewModel defVM) {
			this.defVM = defVM;
		}

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";

			if (((MyPair) this.defVM.getSelectedBanco().getPos7()) == null
					|| ((MyPair) this.defVM.getSelectedBanco().getPos7()).esNuevo() == true) {
				out = false;
				this.mensaje += "\n - Debe seleccionar un banco";
			}

			if (((String) this.defVM.getSelectedBanco().getPos2()) == null
					|| ((String) this.defVM.getSelectedBanco().getPos2()).trim().length() == 0) {
				out = false;
				this.mensaje += "\n - Campo dirección no puede ser vacio.";
			}

			if (((String) this.defVM.getSelectedBanco().getPos3()) == null
					|| ((String) this.defVM.getSelectedBanco().getPos3()).trim().length() == 0) {
				out = false;
				this.mensaje += "\n - Campo teléfono no puede ser vacio.";
			}

			if (((String) this.defVM.getSelectedBanco().getPos4()) == null
					|| ((String) this.defVM.getSelectedBanco().getPos4()).trim().length() == 0) {
				out = false;
				this.mensaje += "\n - Campo correo no puede ser vacio.";
			}

			if (((String) this.defVM.getSelectedBanco().getPos5()) == null
					|| ((String) this.defVM.getSelectedBanco().getPos5()).trim().length() == 0) {
				out = false;
				this.mensaje += "\n - Campo contacto no puede ser vacio.";
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

	class ValidadorTipo implements VerificaAceptarCancelar {

		private String mensaje;
		DefinicionesViewModel defVM;
		RegisterDomain rr = RegisterDomain.getInstance();

		public ValidadorTipo(DefinicionesViewModel defVM) {
			this.defVM = defVM;
		}

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";

			if (this.defVM.getCurrentTipeGeneric().getText().trim().length() == 0) {
				out = false;
				this.mensaje += "\n - Debe ingresar la descripción";
			}

			try {
				if (this.defVM.getCurrentTipeGeneric().getSigla().trim().length() == 0) {

					out = false;
					this.mensaje += "\n - Debe ingresar la sigla";

				} else if (rr.existeTipoBySigla(this.defVM.getCurrentTipeGeneric().getSigla()) == true) {
					out = false;
					this.mensaje += "\n - La sigla ya esta asociada a otro elemento";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
			return "Error al cancelar..";
		}
	}

	class ValidadorLineaCredito implements VerificaAceptarCancelar {

		private String mensaje;
		DefinicionesViewModel defVM;
		RegisterDomain rr = RegisterDomain.getInstance();

		public ValidadorLineaCredito(DefinicionesViewModel defVM) {
			this.defVM = defVM;
		}

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";

			if (((Double) this.defVM.getSelectedLineaDeCredito().getPos2()) == null
					|| ((Double) this.defVM.getSelectedLineaDeCredito().getPos2()) <= 0.001) {
				out = false;
				this.mensaje += "\n - Debe asignar un monto superior a 0";
			}

			if (((String) this.defVM.getSelectedLineaDeCredito().getPos1()) == null
					|| ((String) this.defVM.getSelectedLineaDeCredito().getPos1()).trim().length() == 0) {
				out = false;
				this.mensaje += "\n - Campo descripción no puede ser vacio.";
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
	 * GETS / SETS
	 */

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
		return out;
	}

	/**
	 * @return los talonarios..
	 */
	public List<MyArray> getTalonarios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<MyArray> out = new ArrayList<MyArray>();

		List<Talonario> talonarios = rr.getTalonarios();
		for (Talonario item : talonarios) {

			MyArray timb = new MyArray();
			timb.setId(item.getTimbrado().getId());
			timb.setPos1(item.getTimbrado().getNumero());

			MyArray m = new MyArray();
			m.setId(item.getId());
			m.setPos1(item.getNumero());
			m.setPos2(item.getBocaExpedicion());
			m.setPos3(item.getPuntoExpedicion());
			m.setPos4(item.getDesde());
			m.setPos5(item.getHasta());
			m.setPos6(timb);
			m.setPos7(item.getTimbrado().getVencimiento());
			m.setPos8(item.getSaldo());
			m.setPos9(new MyPair(item.getSucursal().getId(), item.getSucursal().getDescripcion()));
			List<MyPair> tms = new ArrayList<MyPair>();
			for (TipoMovimiento tm : item.getMovimientos()) {
				MyPair mp = new MyPair(tm.getId(), tm.getDescripcion());
				tms.add(mp);
			}
			m.setPos10(tms);
			out.add(m);
		}
		Collections.sort(out, new Comparator<MyArray>() {
			@Override
			public int compare(MyArray o1, MyArray o2) {
				long id1 = o1.getId().longValue();
				long id2 = o2.getId().longValue();
				if (id1 < 0) {
					return 1;
				}
				return (int) (id1 - id2);
			}
		});
		return out;
	}
	
	/**
	 * @return los bancos cta..
	 */
	public List<MyPair> getBancosCta() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoCta> bancos = rr.getBancosCta();
		List<MyPair> out = new ArrayList<MyPair>();
		for (BancoCta banco : bancos) {
			MyPair my = new MyPair(banco.getId(), banco.getBanco()
					.getBancoTipo().getDescripcion());
			out.add(my);
		}
		return out;
	}
	
	/**
	 * @return las zonas..
	 */
	public List<Tipo> getZonas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipos(Configuracion.ID_TIPO_ZONAS);
	}
	
	/**
	 * @return los proveedores internacionales..
	 */
	public List<Proveedor> getProveedoresInternacionales() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getProveedoresExterior("");
	}
	
	/**
	 * @return las familias..
	 */
	public List<Tipo> getFamiliasArticulos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipos(Configuracion.ID_TIPO_ARTICULO_FAMILIA);
	}
	
	/**
	 * @return los vendedores y definicion de comision..
	 */
	@DependsOn({ "selectedProveedor", "selectedFamilia" })
	public List<Funcionario> getVendedoresComision() throws Exception {
		List<Funcionario> out = new ArrayList<Funcionario>();
		if (this.selectedProveedor == null) {
			return out;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		out.addAll(rr.getVendedores());
		for (Funcionario vend : out) {
			Object[] porc_com = vend.getPorcentajeComision(this.selectedProveedor.getId()); 
			double porc_com_vta = (double) porc_com[0];
			double porc_com_cob = (double) porc_com[1];
			vend.setPorc_comision(porc_com_vta);
			vend.setPorc_comision_cobros(porc_com_cob);
		}
		return out;
	}

	@DependsOn("selectedTalonario")
	public boolean isEditTalonarioDisabled() {
		return this.selectedTalonario == null || this.selectedTalonario.esNuevo();
	}

	/**
	 * @return el acceso del usuario corriente..
	 */
	private AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}

	/**
	 * @return la sucursal operativa..
	 */
	private MyPair getSucursal() {
		return this.getAcceso().getSucursalOperativa();
	}

	/**
	 * @return la boca de expedicion..
	 */
	private int getEstablecimiento() {
		String out = this.getSucursal().getSigla();
		return Integer.valueOf(out);
	}

	/**
	 * @return la numeracion correlativa de talonarios..
	 */
	private String getNumeroTalonario() throws Exception {
		String key = Configuracion.NRO_TALONARIOS;
		String out = AutoNumeroControl.getAutoNumeroKey(key, 5);
		return out;
	}

	public MyArray getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MyArray selectedItem) {
		this.selectedItem = selectedItem;
	}

	public MyArray getSelectedTalonario() {
		return selectedTalonario;
	}

	public void setSelectedTalonario(MyArray selectedTalonario) {
		this.selectedTalonario = selectedTalonario;
	}

	/**
	 * 
	 * @return cargos de funcionario definidos
	 * @throws Exception
	 */
	public List<MyPair> getCargosFuncionario() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<MyPair> out = new ArrayList<MyPair>();

		List<Tipo> cargos = rr.getTipos(Configuracion.ID_TIPO_CARGO_FUNCIONARIO);
		for (Tipo item : cargos) {

			MyPair cargo = new MyPair();
			cargo.setId(item.getId());
			cargo.setText(item.getDescripcion());
			cargo.setSigla(item.getSigla());
			out.add(cargo);
		}

		Collections.sort(out, new Comparator<MyPair>() {
			@Override
			public int compare(MyPair o1, MyPair o2) {
				long id1 = o1.getId().longValue();
				long id2 = o2.getId().longValue();
				if (id1 < 0) {
					return 1;
				}
				return (int) (id1 - id2);
			}
		});
		return out;
	}

	/**
	 * 
	 * @return cargos de funcionario definidos
	 * @throws Exception
	 */
	public List<MyPair> getEstadosFuncionario() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<MyPair> out = new ArrayList<MyPair>();

		List<Tipo> cargos = rr.getTipos(Configuracion.ID_TIPO_ESTADO_FUNCIONARIO);
		for (Tipo item : cargos) {

			MyPair cargo = new MyPair();
			cargo.setId(item.getId());
			cargo.setText(item.getDescripcion());
			cargo.setSigla(item.getSigla());
			out.add(cargo);
		}

		Collections.sort(out, new Comparator<MyPair>() {
			@Override
			public int compare(MyPair o1, MyPair o2) {
				long id1 = o1.getId().longValue();
				long id2 = o2.getId().longValue();
				if (id1 < 0) {
					return 1;
				}
				return (int) (id1 - id2);
			}
		});
		return out;
	}

	public List<MyArray> getBancos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<MyArray> out = new ArrayList<MyArray>();

		List<Banco> bancos = rr.getObjects(Banco.class.getName());
		for (Banco b : bancos) {

			MyArray bancoMA = new MyArray();
			bancoMA.setId(b.getId());
			bancoMA.setPos1(b.getDescripcion());
			bancoMA.setPos2(b.getDireccion());
			bancoMA.setPos3(b.getTelefono());
			bancoMA.setPos4(b.getCorreo());
			bancoMA.setPos5(b.getContacto());
			bancoMA.setPos6(b.getSucursales());

			MyPair bancoTipo = new MyPair();
			bancoTipo.setId(b.getBancoTipo().getId());
			bancoTipo.setSigla(b.getBancoTipo().getSigla());
			bancoTipo.setText(b.getBancoTipo().getDescripcion());
			bancoMA.setPos7(bancoTipo);
			out.add(bancoMA);
		}

		Collections.sort(out, new Comparator<MyArray>() {
			@Override
			public int compare(MyArray o1, MyArray o2) {
				long id1 = o1.getId().longValue();
				long id2 = o2.getId().longValue();
				if (id1 < 0) {
					return 1;
				}
				return (int) (id1 - id2);
			}
		});
		return out;
	}

	public List<MyArray> updateBancosUtilDto() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();

		this.ass.utilDomainToListaMyArray(this.getUtilDto(), "bancos", com.yhaguy.domain.Banco.class.getName(),
				new String[] { "descripcion", "direccion", "telefono", "correo", "contacto", "sucursales",
						"bancoTipo" });

		return out;
	}

	public List<MyArray> updateCtaCteLineaCredito() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();

		this.ass.utilDomainToListaMyArray(this.getUtilDto(), "ctaCteLineaCredito",
				com.yhaguy.domain.CtaCteLineaCredito.class.getName(), new String[] { "descripcion", "linea" });

		return out;
	}

	public List<MyPair> getBancosTipo() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<MyPair> out = new ArrayList<MyPair>();

		List<Tipo> bancos = rr.getTipos(Configuracion.ID_TIPO_BANCOS_TERCEROS);
		for (Tipo bancoTipo : bancos) {

			MyPair cargo = new MyPair();
			cargo.setId(bancoTipo.getId());
			cargo.setText(bancoTipo.getDescripcion());
			cargo.setSigla(bancoTipo.getSigla());
			out.add(cargo);
		}

		Collections.sort(out, new Comparator<MyPair>() {
			@Override
			public int compare(MyPair o1, MyPair o2) {
				long id1 = o1.getId().longValue();
				long id2 = o2.getId().longValue();
				if (id1 < 0) {
					return 1;
				}
				return (int) (id1 - id2);
			}
		});
		return out;
	}

	public List<MyPair> getBancoTiposCuenta() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<MyPair> out = new ArrayList<MyPair>();

		List<Tipo> tiposCuenta = rr.getTipos(Configuracion.ID_TIPO_BANCO_CUENTA);
		for (Tipo tipoCuenta : tiposCuenta) {

			MyPair cargo = new MyPair();
			cargo.setId(tipoCuenta.getId());
			cargo.setText(tipoCuenta.getDescripcion());
			cargo.setSigla(tipoCuenta.getSigla());
			out.add(cargo);
		}

		Collections.sort(out, new Comparator<MyPair>() {
			@Override
			public int compare(MyPair o1, MyPair o2) {
				long id1 = o1.getId().longValue();
				long id2 = o2.getId().longValue();
				if (id1 < 0) {
					return 1;
				}
				return (int) (id1 - id2);
			}
		});
		return out;
	}

	public List<MyArray> getCtaCteLineasCredito() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<MyArray> out = new ArrayList<MyArray>();

		List<CtaCteLineaCredito> lineasCred = rr.getObjects(CtaCteLineaCredito.class.getName());
		for (CtaCteLineaCredito lc : lineasCred) {

			MyArray lcMA = new MyArray();
			lcMA.setId(lc.getId());
			lcMA.setPos1(lc.getDescripcion());
			lcMA.setPos2(lc.getLinea());
			out.add(lcMA);
		}

		Collections.sort(out, new Comparator<MyArray>() {
			@Override
			public int compare(MyArray o1, MyArray o2) {
				long id1 = o1.getId().longValue();
				long id2 = o2.getId().longValue();
				if (id1 < 0) {
					return 1;
				}
				return (int) (id1 - id2);
			}
		});
		return out;
	}

	public MyPair getSelectedCargoFuncionario() {
		return selectedCargoFuncionario;
	}

	public void setSelectedCargoFuncionario(MyPair selectedCargoFuncionario) {
		this.selectedCargoFuncionario = selectedCargoFuncionario;
	}

	public MyPair getSelectedEstadoFuncionario() {
		return selectedEstadoFuncionario;
	}

	public void setSelectedEstadoFuncionario(MyPair selectedEstadoFuncionario) {
		this.selectedEstadoFuncionario = selectedEstadoFuncionario;
	}

	public MyPair getCurrentTipeGeneric() {
		return currentTipeGeneric;
	}

	public void setCurrentTipeGeneric(MyPair currentTipeGeneric) {
		this.currentTipeGeneric = currentTipeGeneric;
	}

	public MyArray getSelectedBanco() {
		return selectedBanco;
	}

	public void setSelectedBanco(MyArray selectedBanco) {
		this.selectedBanco = selectedBanco;
	}

	public MyPair getSelectedBancoTercero() {
		return selectedBancoTercero;
	}

	public void setSelectedBancoTercero(MyPair selectedBancoTercero) {
		this.selectedBancoTercero = selectedBancoTercero;
	}

	public MyPair getSelectedTipoCuentaBanco() {
		return selectedTipoCuentaBanco;
	}

	public void setSelectedTipoCuentaBanco(MyPair selectedTipoCuentaBanco) {
		this.selectedTipoCuentaBanco = selectedTipoCuentaBanco;
	}

	public MyArray getSelectedLineaDeCredito() {
		return selectedLineaDeCredito;
	}

	public void setSelectedLineaDeCredito(MyArray selectedLineaDeCredito) {
		this.selectedLineaDeCredito = selectedLineaDeCredito;
	}

	public MyArray getSelectedChequera() {
		return selectedChequera;
	}

	public void setSelectedChequera(MyArray selectedChequera) {
		this.selectedChequera = selectedChequera;
	}

	public Tipo getSelectedZona() {
		return selectedZona;
	}

	public void setSelectedZona(Tipo selectedZona) {
		this.selectedZona = selectedZona;
	}

	public Tipo getNuevaZona() {
		return nuevaZona;
	}

	public void setNuevaZona(Tipo nuevaZona) {
		this.nuevaZona = nuevaZona;
	}

	public Proveedor getSelectedProveedor() {
		return selectedProveedor;
	}

	public void setSelectedProveedor(Proveedor selectedProveedor) {
		this.selectedProveedor = selectedProveedor;
	}

	public Tipo getSelectedFamilia() {
		return selectedFamilia;
	}

	public void setSelectedFamilia(Tipo selectedFamilia) {
		this.selectedFamilia = selectedFamilia;
	}
}
