package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Execution;

import com.coreweb.Config;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.PlanDeCuenta;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlLogica;

public class LibroMayorSimpleControl extends SimpleViewModel {

	BrowserLibroMayor blm = null;
	private ControlLogica ctr = new ControlLogica(null);
	private boolean mostrarCC = false;
	private boolean mostrarPC = false;
	private Date fechaDesde = new Date();
	private Date fechaHasta = new Date();

	@Init(superclass = true)
	public void initLibroMayorSimpleControl(
			@ContextParam(ContextType.EXECUTION) Execution execution)
			throws Exception {

		this.blm = (BrowserLibroMayor) execution
				.getAttribute(Config.BROWSER2_VM);
		// this.cargarPlanDeCuentas();
		// System.out.println("\n\n\n\n this.br2.getCabeceraZulUrl():"+this.br2.getCabeceraZulUrl()+"\n\n\n\n");

	}

	@AfterCompose(superclass = true)
	public void afterComposeLibroMayorSimpleControl() {
	}

	@NotifyChange("*")
	@Command
	public void buscar() throws Exception {
		
		Hashtable<String, Object> params = new Hashtable<>();
		params.put(":fdesde", this.getFechaDesde());
		params.put(":fhasta", this.getFechaHasta());
		
		
		if (this.isMostrarPC() == true) {
			
			System.out.println("\n\n   this.isMostrarPC() == true");
			this.blm.setWhere(" c.id = " + this.getCuentaContable().getId()+" and sd.fecha between :fdesde and :fhasta ", params);
			this.blm.refreshBrowser();
			
		} else if (this.isMostrarCC() == true) {
			
			System.out.println("\n\n   this.isMostrarCC() == true");
			this.blm.setWhere(" pc.id = " + this.getPlanCuenta().getId()+" and sd.fecha between :fdesde and :fhasta ",params);
			this.blm.refreshBrowser();
			
		} else if((this.isMostrarCC() == false) && (this.isMostrarPC() == false)){
			this.mensajeInfo("\nDebe seleccionar una Cta Contable o un Plan de Cuentas.");
		}
		

		
//		this.setMostrarCC(false);
//		this.setMostrarPC(false);
	}

	/************************** Cuenta Contable ****************************/

	MyArray cuentaContable = new MyArray();

	@Command
	@NotifyChange("*")
	public void buscarCtaContable() throws Exception {
		String nombre = (String) this.getCuentaContable().getPos1();
		MyArray ctaContableEncontrada = this.ctr.buscarCuentaContable("",
				nombre, 1, null);
		if (ctaContableEncontrada != null) {
			this.setCuentaContable(ctaContableEncontrada);
		} else {
			this.setCuentaContable(this.getCuentaContable());
		}
		this.readOnlyCuentas(true, false);
		
	}

	/************************** Plan de Cuenta ****************************/

	MyArray planCuenta = new MyArray();

	@Command
	@NotifyChange("*")
	public void buscarPlanDeCuenta() throws Exception {
		String nombre = (String) this.getPlanCuenta().getPos1();
		MyArray planCuentaEncontrada = this.ctr.buscarPlanCuenta("", nombre, 1,
				null);
		if (planCuentaEncontrada != null) {
			this.setPlanCuenta(planCuentaEncontrada);
		} else {
			this.setPlanCuenta(this.getPlanCuenta());
		}
		this.readOnlyCuentas(false, true);
	}

	@Command
	@NotifyChange("*")
	public void readOnlyCuentas(@BindingParam("mostrarPC") boolean mostrarPc,
			@BindingParam("mostrarCC ") boolean mostrarCc) {
		
		System.out.println("this.readOnlyCuentas(false, true);  "+ mostrarPc +" ; "+ mostrarCc);
		
		this.setMostrarCC(mostrarCc);
		this.setMostrarPC(mostrarPc);

	}

	public BrowserLibroMayor getBlm() {
		return blm;
	}

	public void setBlm(BrowserLibroMayor blm) {
		this.blm = blm;
	}

	public MyArray getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(MyArray cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public MyArray getPlanCuenta() {
		return planCuenta;
	}

	public void setPlanCuenta(MyArray planCuenta) {
		this.planCuenta = planCuenta;
	}

	public boolean isMostrarCC() {
		return mostrarCC;
	}

	public void setMostrarCC(boolean mostrarCC) {
		this.mostrarCC = mostrarCC;
	}

	public boolean isMostrarPC() {
		return mostrarPC;
	}

	public void setMostrarPC(boolean mostrarPC) {
		this.mostrarPC = mostrarPC;
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

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

}
