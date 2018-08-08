package com.yhaguy.gestion.compras.definiciones;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.CentroCosto;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.DepartamentoApp;
import com.yhaguy.domain.PlanDeCuenta;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.inicio.AccesoDTO;

public class CuentasContablesDefinicionesViewModel extends SimpleViewModel {

	private AccesoDTO acceso = (AccesoDTO) this.getAtributoSession(Configuracion.ACCESO);
	
	@Init(superclass = true)
	public void init() throws Exception {		
		this.cargarDepartamentos();
		this.cargarCuentasContables();
		this.cargarPlanDeCuentas();
		this.cargarCentrosDeCosto();
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	/************************* CONSTANTES **************************/
	
	static final int DATOS_CUENTA_CONTABLE = 1;
	static final int DATOS_PLAN_DE_CUENTA = 2;
	static final int DATOS_CENTRO_COSTO = 3;
	static final int DATOS_DEPARTAMENTO = 4;
	static final int DATOS_LIST_CENTRO_COSTO = 5;
	static final int DATOS_LIST_CUENTA_CONTABLE = 6;
	
	static final int AGREGAR = 1;
	static final int MODIFICAR = 2;
	static final int ASIGNAR = 3;
	
	/***************************************************************/
	
	
	/************************ DEPARTAMENTOS ************************/
	
	private List<MyArray> departamentos = new ArrayList<MyArray>();
	private MyArray selectedDepartamento = new MyArray();
	
	/**
	 * Levanta los departamentos de la bd..
	 * @throws Exception
	 */
	private void cargarDepartamentos() throws Exception {
		
		List<MyArray> departamentos = new ArrayList<MyArray>();
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<DepartamentoApp> list = rr.getDepartamentosApp();
		
		for (DepartamentoApp dep : list) {
			MyArray my = new MyArray();
			my.setId(dep.getId());
			my.setPos1(dep.getNombre());
			my.setPos2(dep.getDescripcion());
			my.setPos3(new MyPair(dep.getSucursal().getId(), dep.getSucursal().getNombre()));	
			my.setPos4(this.centroCostosToMyArray(dep.getCentroCostos()));
			my.setPos5(this.cuentasContablesToMyArray(dep.getCuentas()));
			departamentos.add(my);
		}
		this.departamentos = departamentos;
		this.selectedDepartamento = departamentos.get(0);
	}
	
	/**
	 * Graba un departamento
	 * @throws Exception
	 */
	private void saveDepartamento() throws Exception {
		this.ass.saveDepartamento(this.selectedDepartamento, this.acceso
				.getSucursalOperativa().getId());
		this.mensajePopupTemporal("Correctamente Agregado");
		this.cargarDepartamentos();
	}
	
	/**
	 * Inicializa un departamento
	 * @return
	 */
	private MyArray inicializarDepartamento() {
		MyArray out = new MyArray();
		out.setPos1("");
		out.setPos2("");
		out.setPos3(new MyPair());	
		out.setPos4(new ArrayList<MyArray>());
		out.setPos5(new ArrayList<MyArray>());		
		return out;
	}
	
	/**
	 * Asigna un centro de costo al departamento..
	 * @throws Exception
	 */	
	private void asignarCentroCosto() throws Exception {
		
		if (this.newCentroCosto.esNuevo() == true) 
			return;
		
		this.ass.saveDepartamento(this.selectedDepartamento, this.acceso
				.getSucursalOperativa().getId(), this.newCentroCosto, new MyArray());
		this.mensajePopupTemporal("Correctamente Agregado");
		this.cargarDepartamentos();
	}
	
	/**
	 * Asigna una cuenta contable al departamento..
	 * @throws Exception
	 */	
	private void asignarCuentaContable() throws Exception {
		
		if (this.newCuentaContable.esNuevo() == true) 
			return;
		
		this.ass.saveDepartamento(this.selectedDepartamento, this.acceso
				.getSucursalOperativa().getId(), new MyArray(), this.newCuentaContable);
		this.mensajePopupTemporal("Correctamente Agregado");
		this.cargarDepartamentos();
	}
	
	/***************************************************************/
	
	
	/********************** CUENTAS CONTABLES **********************/
	
	private List<MyArray> cuentasContables = new ArrayList<MyArray>();
	private MyArray newCuentaContable = new MyArray();
	
	/**
	 * Levanta las cuentas contables de la bd..
	 * @throws Exception
	 */
	private void cargarCuentasContables() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CuentaContable> cuentas = rr.getCuentasContables();
		this.cuentasContables = this.cuentasContablesToMyArray(cuentas);
	}
	
	/**
	 * @param cuenta
	 * @return las cuentas contables convertidas a MyArray..
	 */
	private List<MyArray> cuentasContablesToMyArray(List<CuentaContable> cuentas) {
		List<MyArray> out = new ArrayList<MyArray>();
		
		for (CuentaContable ct : cuentas) {
			MyArray pc = new MyArray();
			
			if (ct.getPlanCuenta() != null) {
				pc.setId(ct.getPlanCuenta().getId());
				pc.setPos1(ct.getPlanCuenta().getCodigo());
				pc.setPos2(ct.getPlanCuenta().getDescripcion());
			}			
			
			MyArray my = new MyArray();
			my.setId(ct.getId());
			my.setPos1(ct.getCodigo());
			my.setPos2(ct.getDescripcion());
			my.setPos3(ct.getAlias());
			my.setPos4(pc);
			out.add(my);
		}		
		return out;
	}
	
	/**
	 * @param cuentas
	 * @return las cuentas contables convertidas a MyArray..
	 */
	private List<MyArray> cuentasContablesToMyArray(Set<CuentaContable> cuentas) {
		return this.cuentasContablesToMyArray(new ArrayList<CuentaContable>(cuentas));
	}	
	
	/**
	 * Graba una cuenta contable..
	 */
	private void saveCuentaContable() throws Exception {
		String key = Configuracion.NRO_CUENTA_CONTABLE;
		
		if (this.newCuentaContable.esNuevo() == true) {
			this.newCuentaContable.setPos1(key + "-" + AutoNumeroControl.getAutoNumero(key, 7));
			this.newCuentaContable.setPos3(this.newCuentaContable.getPos1());
		}

		this.ass.saveCuentaContable(this.newCuentaContable);
		this.mensajePopupTemporal("Correctamente Agregado");
		this.cargarCuentasContables();
	}
	
	/**
	 * @return las cuentas contables sin asignar..
	 */
	public List<MyArray> getCuentasSinAsignar() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CuentaContable> list = rr
				.getCuentasContablesNotIn(this.selectedDepartamento.getId());
		return this.cuentasContablesToMyArray(list);
	}
	
	/***************************************************************/	
	
	
	/************************ PLAN DE CUENTAS **********************/
	
	private List<MyArray> planDeCuentas = new ArrayList<MyArray>();
	private MyArray newPlanCuenta = new MyArray();
	
	/**
	 * Carga el Plan de Cuentas desde la bd..
	 * @throws Exception
	 */
	private void cargarPlanDeCuentas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<PlanDeCuenta> pctas = rr.getPlanDeCuentas();
		this.planDeCuentas = this.planDeCuentasToMyArray(pctas);
	}
	
	/**
	 * @param planDeCuenta
	 * @return el Plan De Cuentas convertido a MyArray..
	 */
	private List<MyArray> planDeCuentasToMyArray(List<PlanDeCuenta> planDeCuenta) {
		List<MyArray> out = new ArrayList<MyArray>();
		
		for (PlanDeCuenta pcta : planDeCuenta) {
			MyPair tipo = new MyPair();
			
			if (pcta.getTipoCuenta() != null) {
				tipo.setId(pcta.getTipoCuenta().getId());
				tipo.setSigla(pcta.getTipoCuenta().getSigla());
				tipo.setText(pcta.getTipoCuenta().getDescripcion());
			}
			
			MyArray my = new MyArray();
			my.setId(pcta.getId());
			my.setPos1(pcta.getCodigo());
			my.setPos2(pcta.getDescripcion());
			my.setPos3(tipo);
			out.add(my);
		}		
		return out;
	}
	
	/**
	 * Graba un Plan de Cuenta..
	 * @throws Exception
	 */
	private void savePlanDeCuenta() throws Exception {
		this.ass.savePlanDeCuenta(this.newPlanCuenta);
		this.mensajePopupTemporal("Correctamente Agregado");
		this.cargarPlanDeCuentas();
	}
	
	/***************************************************************/
	
	
	/*********************** CENTROS DE COSTO **********************/
	
	private List<MyArray> centrosDeCosto = new ArrayList<MyArray>();
	private MyArray newCentroCosto = this.inicializarCentroCosto();
	
	/**
	 * Levanta los Centros de Costos de la bd..
	 * @throws Exception
	 */
	private void cargarCentrosDeCosto() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CentroCosto> centros = rr.getCentrosDeCosto();
		this.centrosDeCosto = this.centroCostosToMyArray(centros);
	}
	
	/**
	 * @param centroCostos
	 * @return los centros de costo convertidos a MyArray..
	 */
	private List<MyArray> centroCostosToMyArray(List<CentroCosto> centroCostos) {
		List<MyArray> out = new ArrayList<MyArray>();
		
		for (CentroCosto cc : centroCostos) {			
			MyArray my = new MyArray();
			my.setId(cc.getId());
			my.setPos1(cc.getNumero());
			my.setPos2(cc.getDescripcion());
			my.setPos3(cc.getMontoAsignado());
			out.add(my);
		}		
		return out;
	}	
	
	/**
	 * @param centroCostos
	 * @return los centros de costo convertidos a MyArray..
	 */
	private List<MyArray> centroCostosToMyArray(Set<CentroCosto> centroCostos) {
		return this.centroCostosToMyArray(new ArrayList<CentroCosto>(centroCostos));
	}
	
	/**
	 * Graba un Centro de Costo..
	 * @throws Exception
	 */
	private void saveCentroCosto() throws Exception {
		this.ass.saveCentroCosto(this.newCentroCosto);
		this.mensajePopupTemporal("Correctamente agregado");
		this.cargarCentrosDeCosto();
	}
	
	/**
	 * Inicializa un Centro de Costo
	 */
	private MyArray inicializarCentroCosto() {
		MyArray out = new MyArray();
		out.setPos1("");
		out.setPos2("");
		out.setPos3((Double) 0.0);		
		return out;
	}
	
	/**
	 * @return los centros de costo sin asignar del dpto seleccionado..
	 */
	public List<MyArray> getCentrosDeCostoSinAsignar() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CentroCosto> list = rr
				.getCentrosDeCostoNotIn(this.selectedDepartamento.getId());
		return this.centroCostosToMyArray(list);
	}
	
	/***************************************************************/
	
	
	/***************************** UTILES **************************/
	
	private AssemblerDefinicionesCuentaContable ass = new AssemblerDefinicionesCuentaContable();
	private int tipoSeleccionado = -1;
	
	/**
	 * Abre el popup para agregar o editar los datos..
	 */
	@Command @NotifyChange("*")
	public void abrirPopupDatos(@BindingParam("tipo") int tipo,
			@BindingParam("operacion") int operacion) throws Exception {
		this.tipoSeleccionado = tipo;
		
		if ((operacion == MODIFICAR) 
				&& (this.isItemSeleccionado() == false)) {
			this.mensajeError("Debe seleccionar un ítem..");
			return;
		
		} else if (operacion == AGREGAR) {
			this.newCuentaContable = new MyArray();
			this.newPlanCuenta = new MyArray();
			this.newCentroCosto = this.inicializarCentroCosto();
		}
		
		if ((operacion == AGREGAR) && (tipo == DATOS_DEPARTAMENTO)) {
			this.selectedDepartamento = this.inicializarDepartamento();
		}

		WindowPopup wp = new WindowPopup();
		wp.setTitulo("Definiciones");
		wp.setModo(WindowPopup.NUEVO);
		wp.setDato(this);
		wp.setCheckAC(this.getCheckAC(tipo));
		wp.setWidth("400px");
		wp.setHigth("300px");
		wp.show(Configuracion.DEFINICIONES_CUENTAS_CONTABLES_ZUL);

		if (wp.isClickAceptar() == true) {

			switch (tipo) {

			case DATOS_CUENTA_CONTABLE:
				this.saveCuentaContable();
				this.newCuentaContable = new MyArray();
				break;

			case DATOS_PLAN_DE_CUENTA:
				this.savePlanDeCuenta();
				this.newPlanCuenta = new MyArray();
				break;

			case DATOS_DEPARTAMENTO:
				this.saveDepartamento();
				break;

			case DATOS_CENTRO_COSTO:
				this.saveCentroCosto();
				this.newCentroCosto = this.inicializarCentroCosto();
				break;
				
			case DATOS_LIST_CENTRO_COSTO:
				this.asignarCentroCosto();
				this.newCentroCosto = this.inicializarCentroCosto();
				break;
				
			case DATOS_LIST_CUENTA_CONTABLE:
				this.asignarCuentaContable();
				this.newCuentaContable = new MyArray();
				break;
			}
		}
		
		if (tipo == DATOS_DEPARTAMENTO) {
			this.cargarDepartamentos();
		}
	}
	
	/**
	 * Concatena dos Strings..
	 */
	public String concat(String uno, String dos) {
		return uno + " - " + dos;
	}
	
	/**
	 * @return el validador del popup segun el tipo..
	 */
	private VerificaAceptarCancelar getCheckAC(int tipo) {
		
		switch (tipo) {
		
		case DATOS_CUENTA_CONTABLE:
			return new ValidadorCuentaContable(this.newCuentaContable);

		case DATOS_PLAN_DE_CUENTA:
			return new ValidadorPlanCuenta(this.newPlanCuenta);
			
		case DATOS_CENTRO_COSTO:
			return new ValidadorCentroCosto(this.newCentroCosto);
			
		case DATOS_DEPARTAMENTO:
			return new ValidadorDepartamento(this.selectedDepartamento);
			
		default:
			return null;
		}
	}
	
	/**
	 * @return true si fue seleccionado un ítem..
	 */
	private boolean isItemSeleccionado() {
		
		switch (this.tipoSeleccionado) {
		
		case DATOS_CUENTA_CONTABLE:	
			return this.newCuentaContable.esNuevo() == false;

		case DATOS_PLAN_DE_CUENTA:			
			return this.newPlanCuenta.esNuevo() == false;
			
		case DATOS_CENTRO_COSTO:
			return this.newCentroCosto.esNuevo() == false;
			
		case DATOS_DEPARTAMENTO:
			return this.selectedDepartamento.esNuevo() == false;
		}
		
		return false;
	}	
	
	/***************************************************************/
	
	
	/*************************** GET/SET ***************************/
	
	public UtilDTO getUtil() {
		return (UtilDTO) this.getDtoUtil();
	}
	
	public List<MyArray> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<MyArray> departamentos) {
		this.departamentos = departamentos;
	}

	public MyArray getSelectedDepartamento() {
		return selectedDepartamento;
	}

	public void setSelectedDepartamento(MyArray selectedDepartamento) {
		this.selectedDepartamento = selectedDepartamento;
	}

	public List<MyArray> getCuentasContables() {
		return cuentasContables;
	}

	public void setCuentasContables(List<MyArray> cuentasContables) {
		this.cuentasContables = cuentasContables;
	}

	public List<MyArray> getPlanDeCuentas() {
		return planDeCuentas;
	}

	public void setPlanDeCuentas(List<MyArray> planDeCuentas) {
		this.planDeCuentas = planDeCuentas;
	}

	public List<MyArray> getCentrosDeCosto() {
		return centrosDeCosto;
	}

	public void setCentrosDeCosto(List<MyArray> centrosDeCosto) {
		this.centrosDeCosto = centrosDeCosto;
	}

	public int getTipoSeleccionado() {
		return tipoSeleccionado;
	}

	public void setTipoSeleccionado(int tipoSeleccionado) {
		this.tipoSeleccionado = tipoSeleccionado;
	}

	public MyArray getNewCuentaContable() {
		return newCuentaContable;
	}

	public void setNewCuentaContable(MyArray newCuentaContable) {
		this.newCuentaContable = newCuentaContable;
	}

	public MyArray getNewPlanCuenta() {
		return newPlanCuenta;
	}

	public void setNewPlanCuenta(MyArray newPlanCuenta) {
		this.newPlanCuenta = newPlanCuenta;
	}

	public MyArray getNewCentroCosto() {
		return newCentroCosto;
	}

	public void setNewCentroCosto(MyArray newCentroCosto) {
		this.newCentroCosto = newCentroCosto;
	}
}

// Assembler para las Cuentas Contables
class AssemblerDefinicionesCuentaContable extends Assembler {

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		return null;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		return null;
	}
	
	/**
	 * Graba una cuenta contable..
	 * @param cuentaContable
	 * @throws Exception
	 */
	public void saveCuentaContable(MyArray cuentaContable) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		MyArray planCuenta = (MyArray) cuentaContable.getPos4();
		
		CuentaContable ct = null;
		
		if (cuentaContable.esNuevo() == true) {
			ct = new CuentaContable();
		} else {
			ct = rr.getCuentaContableById(cuentaContable.getId());
		}
		
		ct.setCodigo((String) cuentaContable.getPos1());
		ct.setDescripcion(((String) cuentaContable.getPos2()).toUpperCase());
		ct.setAlias((String) cuentaContable.getPos3());
		ct.setPlanCuenta((PlanDeCuenta) rr.getObject(
				PlanDeCuenta.class.getName(), planCuenta.getId()));

		rr.saveObject(ct, this.getLogin());
	}
	
	/**
	 * Graba un plan de cuenta..
	 * @param planDeCuenta
	 */
	public void savePlanDeCuenta(MyArray planDeCuenta) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		MyPair tipo = (MyPair) planDeCuenta.getPos3();

		PlanDeCuenta pct = null;

		if (planDeCuenta.esNuevo() == true) {
			pct = new PlanDeCuenta();
		} else {
			pct = (PlanDeCuenta) rr.getObject(PlanDeCuenta.class.getName(),
					planDeCuenta.getId());
		}

		pct.setCodigo((String) planDeCuenta.getPos1());
		pct.setDescripcion(((String) planDeCuenta.getPos2()).toUpperCase());
		pct.setTipoCuenta(rr.getTipoById(tipo.getId()));
		rr.saveObject(pct, this.getLogin());
	}
	
	/**
	 * Graba un Centro de Costo..
	 * @param centroCosto
	 */
	public void saveCentroCosto(MyArray centroCosto) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String key = Configuracion.NRO_CENTRO_COSTO;
		
		CentroCosto cc = null;
		
		if (centroCosto.esNuevo() == true) {
			cc = new CentroCosto();
			cc.setNumero(key + "-" + AutoNumeroControl.getAutoNumero(key, 5));
		} else {
			cc = (CentroCosto) rr.getObject(CentroCosto.class.getName(), centroCosto.getId());
		}
		cc.setDescripcion(((String) centroCosto.getPos2()).toUpperCase());		
		cc.setMontoAsignado((Double) centroCosto.getPos3());
		rr.saveObject(cc, getLogin());
	}
	
	/**
	 * Graba un departamento..
	 * @param departamento
	 */
	public void saveDepartamento(MyArray departamento, long idSucursal,
			MyArray centroCosto, MyArray cuentaContable) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		DepartamentoApp dep = new DepartamentoApp();

		if (departamento.esNuevo() == true) {
			dep = new DepartamentoApp();

		} else {
			dep = (DepartamentoApp) rr.getObject(
					DepartamentoApp.class.getName(), departamento.getId());
		}

		dep.setNombre(((String) departamento.getPos1()).toUpperCase());
		dep.setDescripcion((String) departamento.getPos2());
		dep.setSucursal((SucursalApp) rr.getObject(SucursalApp.class.getName(),
				idSucursal));

		if (centroCosto.esNuevo() == false) {
			CentroCosto cc = (CentroCosto) rr.getObject(
					CentroCosto.class.getName(), centroCosto.getId());
			dep.getCentroCostos().add(cc);
		}

		if (cuentaContable.esNuevo() == false) {
			CuentaContable ct = (CuentaContable) rr.getObject(
					CuentaContable.class.getName(), cuentaContable.getId());
			dep.getCuentas().add(ct);
		}

		rr.saveObject(dep, getLogin());
	}
	
	/**
	 * Graba un departamento..
	 */
	public void saveDepartamento(MyArray departamento, long idSucursal)
			throws Exception {
		this.saveDepartamento(departamento, idSucursal, new MyArray(), new MyArray());
	}	
}

// Validador de las cuentas contables
class ValidadorCuentaContable implements VerificaAceptarCancelar {
	
	private MyArray cuentaContable;
	private String mensaje = "";
	
	public ValidadorCuentaContable(MyArray cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensaje = "No se puede completar la operación debido a: \n";
		
		String descripcion = (String) this.cuentaContable.getPos2();
		
		if (descripcion.trim().length() == 0) {
			out = false;
			this.mensaje += "- Debe ingresar una descripcion. \n";
		}
		
		if (this.cuentaContable.getPos4() instanceof String == true) {
			out = false;
			this.mensaje += "- Debe asignar un Plan de Cuenta. \n";
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
		return "Error al cancelar";
	}
}

// Validador del Plan de Cuenta..
class ValidadorPlanCuenta implements VerificaAceptarCancelar {
	
	private MyArray planDeCuenta;
	private String mensaje = "";
	
	public ValidadorPlanCuenta(MyArray planDeCuenta) {
		this.planDeCuenta = planDeCuenta;
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensaje = "No se puede completar la operación debido a: \n";
		
		String codigo = (String) this.planDeCuenta.getPos1();
		String descripcion = (String) this.planDeCuenta.getPos2();
		
		if (codigo.trim().length() == 0) {
			out = false;
			this.mensaje += "- Debe ingresar el código. \n";
		}
		
		if (descripcion.trim().length() == 0) {
			out = false;
			this.mensaje += "- Debe ingresar la descripción. \n";
		}
		
		if (this.planDeCuenta.getPos3() instanceof String == true) {
			out = false;
			this.mensaje += "- Debe asignar el tipo. \n";
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

// Validador del Centro de Costo..
class ValidadorCentroCosto implements VerificaAceptarCancelar {

	private MyArray centroCosto;
	private String mensaje;
	
	public ValidadorCentroCosto(MyArray centroCosto) {
		this.centroCosto = centroCosto;
	}
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensaje = "No se puede completar la operación debido a: \n";
		String descripcion = (String) this.centroCosto.getPos2();
		
		if (descripcion.trim().length() == 0) {
			out = false;
			this.mensaje += "- Debe ingresar una descripción \n";
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
		return "Error al cancelar";
	}
}

// Validador del Departamento..
class ValidadorDepartamento implements VerificaAceptarCancelar {

	private MyArray departamento;
	private String mensaje;
	
	public ValidadorDepartamento(MyArray departamento) {
		this.departamento = departamento;
	}
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensaje = "No se puede completar la operación debido a: \n";
		
		String nombre = (String) this.departamento.getPos1();
		String descripcion  = (String) this.departamento.getPos2();
		
		if (nombre.trim().length() == 0) {
			out = false;
			this.mensaje += "- Debe ingresar el nombre \n";
		}
		
		if (descripcion.trim().length() == 0) {
			out = false;
			this.mensaje += "- Debe ingresar la descripción \n";
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
		return "Error al cancelar";
	}
}
