package com.yhaguy.gestion.mobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.ServicioTecnico;
import com.yhaguy.domain.ServicioTecnicoDetalle;
import com.yhaguy.domain.Venta;

public class ServicioTecnicoMobileVM extends SimpleViewModel {
	
	private ServicioTecnico nvoServicio;
	private ServicioTecnicoDetalle nvoDetalle;
	private Object[] selectedVenta;
	
	private String razonSocial = "";
	private String filterNumero = "";

	@Init(superclass = true)
	public void init() {
		try {
			this.inicializarServicio();
			this.inicializarDetalle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange({ "nvoServicio", "filterNumero" })
	public void addFactura() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta venta = (Venta) rr.getObject(Venta.class.getName(), (long) this.selectedVenta[0]);
		this.nvoServicio.getFacturas().add(venta);
		this.selectedVenta = null;
		this.filterNumero = "";
	}
	
	@Command
	@NotifyChange("*")
	public void addDetalle() {
		this.nvoServicio.getDetalles().add(this.nvoDetalle);
		this.inicializarDetalle();
	}
	
	@Command
	@NotifyChange("*")
	public void addServicioTecnico(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2,
			@BindingParam("comp3") Component comp3, @BindingParam("comp4") Component comp4) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvoServicio.setNumero("SER-TEC-" + AutoNumeroControl.getAutoNumero("SER-TEC-", 7));
		rr.saveObject(this.nvoServicio, "mobile");
		this.inicializarServicio();
		this.inicializarDetalle();
		comp1.setVisible(false);
		comp2.setVisible(true);
		comp3.setVisible(false);
		comp4.setVisible(false);
	}
	
	/**
	 * inicializacion del servicio..
	 */
	private void inicializarServicio() throws Exception {
		this.nvoServicio = new ServicioTecnico();
		this.nvoServicio.setNumero("SER-TEC-" + AutoNumeroControl.getAutoNumero("SER-TEC-", 7, true));
		this.nvoServicio.setFecha(new Date());
	}
	
	/**
	 * inicializa el detalle del servicio..
	 */
	private void inicializarDetalle() {
		this.nvoDetalle = new ServicioTecnicoDetalle();
		this.nvoDetalle.setEstado("");
		this.nvoDetalle.setVerifica_carga("");
		this.nvoDetalle.setVerifica_borne("");
		this.nvoDetalle.setVerifica_celda("");
		this.nvoDetalle.setVerifica_conexion("");
		this.nvoDetalle.setObservacion("");
	}

	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn("razonSocial")
	public List<Cliente> getClientes() throws Exception {
		if (this.razonSocial.isEmpty()) {
			return new ArrayList<Cliente>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();		
		return rr.getClientes(razonSocial);
	}
	
	@DependsOn({ "nvoServicio.cliente", "filterNumero" })
	public List<Object[]> getFacturas() throws Exception {
		if (this.nvoServicio.getCliente() == null || this.filterNumero.isEmpty()) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getVentas_(this.filterNumero, this.nvoServicio.getCliente().getId());
	}
	
	@DependsOn({ "nvoDetalle.articulo", "nvoDetalle.numeroFactura" })
	public boolean isInsertarItemDisabled() {
		return this.nvoDetalle.getArticulo() == null || this.nvoDetalle.getNumeroFactura() == null
				|| this.nvoDetalle.getNumeroFactura().isEmpty();
	}
	
	/**
	 * @return los tecnicos..
	 */
	public List<String> getTecnicos() throws Exception {
		List<String> out = new ArrayList<String>();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Funcionario func : rr.getFuncionariosTecnicos()) {
			out.add(func.getRazonSocial().toUpperCase());
		}
		return out;
	}
	
	/**
	 * @return funcionarios..
	 */
	public List<String> getReceptores() throws Exception {
		List<String> out = new ArrayList<String>();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Funcionario func : rr.getFuncionariosDeposito()) {
			out.add(func.getRazonSocial().toUpperCase());
		}
		for (Funcionario func : rr.getFuncionariosCobradores()) {
			if (!out.contains(func.getRazonSocial().toUpperCase())) {
				out.add(func.getRazonSocial().toUpperCase());
			}
		}		
		Collections.sort(out, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int compare = o1.compareTo(o2);				
				return compare;
			}
		});		
		return out;
	}
	
	public ServicioTecnico getNvoServicio() {
		return nvoServicio;
	}

	public void setNvoServicio(ServicioTecnico nvoServicio) {
		this.nvoServicio = nvoServicio;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
	}

	public Object[] getSelectedVenta() {
		return selectedVenta;
	}

	public void setSelectedVenta(Object[] selectedVenta) {
		this.selectedVenta = selectedVenta;
	}

	public ServicioTecnicoDetalle getNvoDetalle() {
		return nvoDetalle;
	}

	public void setNvoDetalle(ServicioTecnicoDetalle nvoDetalle) {
		this.nvoDetalle = nvoDetalle;
	}	
}
