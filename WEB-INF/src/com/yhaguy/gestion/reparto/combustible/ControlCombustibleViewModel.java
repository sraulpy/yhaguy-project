package com.yhaguy.gestion.reparto.combustible;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.ControlCombustible;
import com.yhaguy.domain.OrdenCompra;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlLogistica;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;

public class ControlCombustibleViewModel extends SimpleViewModel {

	static final String ZUL_INSERT_ITEM = "/yhaguy/gestion/reparto/addcombustible.zul";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	private String filterFactura = "";
	private String filterChofer = "";
	private String filterVehiculo = "";
	private String filterTipo = "";
	private String filterNroOrdenCompra = "";
	private double totalImporteGs = 0;
	
	private String filterNroOrden = "";
	
	private MyArray nvoCombustible;
	private MyArray selectedCombustible;
	
	@Init(superclass = true)
	public void init() {
		try {
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void addCombustible() throws Exception {
		this.addCombustible_();
	}
	
	@Command
	@NotifyChange("*")
	public void deleteCombustible() throws Exception {
		this.deleteCombustible_();
	}
	
	/**
	 * add combustible..
	 */
	private void addCombustible_() throws Exception {
		this.nvoCombustible = new MyArray(new Date(), "", 0.0, 0.0, 0.0, "", "", new MyPair(), new OrdenCompra());
		WindowPopup wp = new WindowPopup();
		wp.setCheckAC(new ValidadorInsertarCombustible());
		wp.setDato(this);
		wp.setHigth("450px");
		wp.setWidth("430px");
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Agregar Control de Gasto");
		wp.show(ZUL_INSERT_ITEM);
		if (wp.isClickAceptar()) {
			Date fecha = (Date) this.nvoCombustible.getPos1();
			MyPair combustible = (MyPair) this.nvoCombustible.getPos8();
			ControlLogistica.addControlCombustible(fecha, (String) this.nvoCombustible.getPos2(),
					(double) this.nvoCombustible.getPos3(), (double) this.nvoCombustible.getPos4(), 
					(double) this.nvoCombustible.getPos5(), 
					((String) this.nvoCombustible.getPos6()).toUpperCase(), 
					((String) this.nvoCombustible.getPos7()).toUpperCase(),
					combustible.getId(), (OrdenCompra) this.nvoCombustible.getPos9(), this.getLoginNombre());
			this.mensajePopupTemporal("Registro agregado..");
		}
	}
	
	/**
	 * elimina el control combustible seleccionado..
	 */
	private void deleteCombustible_() throws Exception {
		if (!this.mensajeSiNo("Desea eliminar el registro seleccionado..")) {
			return;
		}
		ControlLogistica.deleteCombustible(this.selectedCombustible.getId());
		this.selectedCombustible = null;
		this.mensajePopupTemporalWarning("REGISTRO ELIMINADO..");
	}
	
	/**
	 * validador insertar combustible..
	 */
	class ValidadorInsertarCombustible implements VerificaAceptarCancelar {
		
		String mensaje = "";

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a:";
			String nroFac = (String) nvoCombustible.getPos2();
			double kilometraje = (double) nvoCombustible.getPos3();
			double litros = (double) nvoCombustible.getPos4();
			double costoGs = (double) nvoCombustible.getPos5();
			String nroChapa = (String) nvoCombustible.getPos6();
			String chofer = (String) nvoCombustible.getPos7();
			MyPair combustible = (MyPair) nvoCombustible.getPos8();
			OrdenCompra orden = (OrdenCompra) nvoCombustible.getPos9();
			
			if (nroFac.isEmpty()) {
				out = false;
				this.mensaje += "\n - Debe ingresar el nro de factura..";
			}
			
			if (kilometraje <= 0) {
				out = false;
				this.mensaje += "\n - Debe ingresar el kilometraje..";
			}
			
			if (litros <= 0) {
				out = false;
				this.mensaje += "\n - Debe ingresar el litraje..";
			}
			
			if (costoGs <= 0) {
				out = false;
				this.mensaje += "\n - Debe ingresar el costo..";
			}
			
			if (nroChapa.isEmpty()) {
				out = false;
				this.mensaje += "\n - Debe ingresar el número de chapa..";
			}
			
			if (chofer.isEmpty()) {
				out = false;
				this.mensaje += "\n - Debe ingresar el chofer..";
			}
			
			if (combustible.esNuevo()) {
				out = false;
				this.mensaje += "\n - Debe asignar el tipo de combustible..";
			}
			
			if (orden.esNuevo()) {
				out = false;
				this.mensaje += "\n - Debe asignar la orden de compra..";
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
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaAA",
			"filterFactura", "filterChofer", "filterVehiculo", "filterTipo", "filterNroOrdenCompra" })
	public List<MyArray> getControlCombustibles() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		this.totalImporteGs = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ControlCombustible> ccs = rr.getControlCombustibles(this.getFilterFecha(), this.filterFactura, 
				this.filterChofer, this.filterVehiculo, this.filterTipo, this.filterNroOrdenCompra);
		for (ControlCombustible cc : ccs) {
			out.add(this.getCombustibleToMyArray(cc));
			this.totalImporteGs += (cc.getLitros() * cc.getCostoGs());
		}
		BindUtils.postNotifyChange(null, null, this, "totalImporteGs");
		return out;
	}
	
	/**
	 * @return el combustible convertido a MyArray
	 */
	private MyArray getCombustibleToMyArray(ControlCombustible cc) {
		MyArray out = new MyArray();
		out.setId(cc.getId());
		out.setPos1(cc.getFecha());
		out.setPos2(cc.getNumeroFactura());
		out.setPos3(cc.getKilometraje());
		out.setPos4(cc.getLitros());
		out.setPos5(cc.getCostoGs());
		out.setPos6(cc.getNumeroChapa());
		out.setPos7(cc.getChofer());
		out.setPos8(new MyPair(cc.getCombustible().getId(), cc.getCombustible().getDescripcion()));
		out.setPos9(cc.getLitros() * cc.getCostoGs());
		out.setPos10(cc.getNumeroOrdenCompra());
		return out;
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
	
	@DependsOn("filterNroOrden")
	public List<OrdenCompra> getOrdenesCompra() throws Exception {
		if (this.filterNroOrden.isEmpty()) {
			return new ArrayList<OrdenCompra>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getOrdenesCompra("", this.filterNroOrden, "", "", "");
	}
	
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}

	public MyArray getNvoCombustible() {
		return nvoCombustible;
	}

	public void setNvoCombustible(MyArray nvoCombustible) {
		this.nvoCombustible = nvoCombustible;
	}

	public String getFilterFactura() {
		return filterFactura;
	}

	public void setFilterFactura(String filterFactura) {
		this.filterFactura = filterFactura;
	}

	public String getFilterChofer() {
		return filterChofer;
	}

	public void setFilterChofer(String filterChofer) {
		this.filterChofer = filterChofer;
	}

	public String getFilterVehiculo() {
		return filterVehiculo;
	}

	public void setFilterVehiculo(String filterVehiculo) {
		this.filterVehiculo = filterVehiculo;
	}

	public String getFilterTipo() {
		return filterTipo;
	}

	public void setFilterTipo(String filterTipo) {
		this.filterTipo = filterTipo;
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

	public double getTotalImporteGs() {
		return totalImporteGs;
	}

	public void setTotalImporteGs(double totalImporteGs) {
		this.totalImporteGs = totalImporteGs;
	}

	public MyArray getSelectedCombustible() {
		return selectedCombustible;
	}

	public void setSelectedCombustible(MyArray selectedCombustible) {
		this.selectedCombustible = selectedCombustible;
	}

	public String getFilterNroOrden() {
		return filterNroOrden;
	}

	public void setFilterNroOrden(String filterNroOrden) {
		this.filterNroOrden = filterNroOrden;
	}

	public String getFilterNroOrdenCompra() {
		return filterNroOrdenCompra;
	}

	public void setFilterNroOrdenCompra(String filterNroOrdenCompra) {
		this.filterNroOrdenCompra = filterNroOrdenCompra;
	}
}
