package com.yhaguy.gestion.bancos.prestamos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.BancoPrestamo;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.util.Utiles;

public class BancoPrestamosViewModel extends SimpleViewModel {
	
	private BancoPrestamo nvo_prestamo;
	private BancoPrestamo selected_prestamo;
	
	private String filter_banco = "";
	private String filter_numero = "";
	private String filter_ruc = "";
	private String filter_razonSocial = "";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	
	private List<Object[]> cuotas = new ArrayList<Object[]>();

	@Init(superclass = true)
	public void init() {
		try {
			this.inicializarDatos();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void addPrestamo(@BindingParam("comp") Popup comp) throws Exception {
		if(!this.isDatosValidos()) {
			Clients.showNotification("NO SE PUDO GUARDAR, VERIFIQUE LOS DATOS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.nvo_prestamo, this.getLoginNombre());
		ControlCuentaCorriente.addPrestamoBancario(this.nvo_prestamo, this.getLoginNombre());
		comp.close();
		Clients.showNotification("REGISTRO GUARDADO..");
		this.inicializarDatos();
	}
	
	@Command
	@NotifyChange("filter_razonSocial")
	public void addFilter() {
		this.filter_razonSocial = "BANCO " + this.nvo_prestamo.getBanco().getBanco().getDescripcion().toUpperCase();
	}
	
	/**
	 * @return true si los datos son validos..
	 */
	private boolean isDatosValidos() {
		boolean out = true;
		if (this.nvo_prestamo.getBanco() == null) {
			out = false;
		}
		if (this.nvo_prestamo.getCtacte() == null) {
			out = false;
		}
		if (this.nvo_prestamo.getMoneda() == null) {
			out = false;
		}
		if (this.nvo_prestamo.getCapital() <= 500) {
			out = false;
		}
		return out;
	}
	
	/**
	 * inicializar datos..
	 */
	private void inicializarDatos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvo_prestamo = new BancoPrestamo();
		this.nvo_prestamo.setFecha(new Date());
		this.nvo_prestamo.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		this.nvo_prestamo.setCuotas(12);
		this.nvo_prestamo.setNumero("");
		this.nvo_prestamo.setTipoVencimiento(BancoPrestamo.VTO_MENSUAL);
		this.nvo_prestamo.setTipoCuotas(BancoPrestamo.TIPO_CUOTAS_FIJAS);
	}
	
	

	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filter_banco", "filter_numero", "filterFechaDD", "filterFechaMM", "filterFechaAA" })
	public List<BancoPrestamo> getPrestamos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancoPrestamos(this.getFilterFecha(), this.filter_banco, this.filter_numero);
	}
	
	@DependsOn({ "filter_ruc", "filter_razonSocial" })
	public List<Empresa> getEmpresas() throws Exception {
		if(this.filter_ruc.trim().isEmpty() && this.filter_razonSocial.trim().isEmpty()) return new ArrayList<Empresa>();
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getEmpresas(this.filter_ruc, "", this.filter_razonSocial, "");
	}
	
	@Command
	@NotifyChange("cuotas")
	public void calcularCuotas() throws Exception {
		this.cuotas = new ArrayList<Object[]>();
		
		int cuotas = this.nvo_prestamo.getCuotas();
		String dd = Utiles.getDateToString(this.nvo_prestamo.getFecha(), "dd");
		String mm = Utiles.getDateToString(this.nvo_prestamo.getFecha(), "MM");
		String aa = Utiles.getDateToString(this.nvo_prestamo.getFecha(), "yyyy");
		int acum = Integer.parseInt(mm);
		
		for (int i = 1; i <= cuotas; i++) {
			acum += this.nvo_prestamo.getMesesTipoVencimiento();
			if(acum >= 12) {
				acum = acum - 12;
				aa = ((Integer.parseInt(aa) + 1) + "");
			}
			int mes_ = acum + 0;
			Object[] cuota = new Object[] {
					i,
					Utiles.getFecha(dd + "-" + (mes_ > 9 ? ("" + mes_) : ("0" + mes_)) + "-" + aa + " 00:00:00"),
					this.nvo_prestamo.getDeudaTotal() / cuotas };
			this.cuotas.add(cuota);
		}		
	}
	
	/**
	 * @return los bancos..
	 */
	public List<BancoCta> getBancos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancosCta();
	}
	
	/**
	 * @return las monedas..
	 */
	public List<Tipo> getMonedas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipos(Configuracion.ID_TIPO_MONEDA);
	}
	
	/**
	 * @return los tipos de vencimiento..
	 */
	public List<String> getTiposVencimiento() {
		List<String> out = new ArrayList<String>();
		out.add(BancoPrestamo.VTO_MENSUAL);
		out.add(BancoPrestamo.VTO_BIMESTRAL);
		out.add(BancoPrestamo.VTO_TRIMESTRAL);
		out.add(BancoPrestamo.VTO_SEMESTRAL);
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
	
	public BancoPrestamo getNvo_prestamo() {
		return nvo_prestamo;
	}

	public void setNvo_prestamo(BancoPrestamo nvo_prestamo) {
		this.nvo_prestamo = nvo_prestamo;
	}

	public String getFilter_banco() {
		return filter_banco;
	}

	public void setFilter_banco(String filter_banco) {
		this.filter_banco = filter_banco;
	}

	public String getFilter_numero() {
		return filter_numero;
	}

	public void setFilter_numero(String filter_numero) {
		this.filter_numero = filter_numero;
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

	public BancoPrestamo getSelected_prestamo() {
		return selected_prestamo;
	}

	public void setSelected_prestamo(BancoPrestamo selected_prestamo) {
		this.selected_prestamo = selected_prestamo;
	}

	public String getFilter_ruc() {
		return filter_ruc;
	}

	public void setFilter_ruc(String filter_ruc) {
		this.filter_ruc = filter_ruc;
	}

	public String getFilter_razonSocial() {
		return filter_razonSocial;
	}

	public void setFilter_razonSocial(String filter_razonSocial) {
		this.filter_razonSocial = filter_razonSocial;
	}

	public List<Object[]> getCuotas() {
		return cuotas;
	}

	public void setCuotas(List<Object[]> cuotas) {
		this.cuotas = cuotas;
	}
}
