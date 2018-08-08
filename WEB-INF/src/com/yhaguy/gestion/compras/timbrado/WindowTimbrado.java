package com.yhaguy.gestion.compras.timbrado;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Textbox;

import com.coreweb.Config;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Timbrado;

public class WindowTimbrado extends SoloViewModel implements VerificaAceptarCancelar{
	
	public void show(String modo, WindowTimbrado dato){
		
		try {
			wp = new WindowPopup();
			wp.setDato(dato);
			wp.setCheckAC(dato);
			wp.setModo(modo);
			wp.setTitulo(Configuracion.TITULO_POPUP_TIMBRADO);
			wp.setWidth("500px");
			wp.setHigth("410px");			
			wp.show(Configuracion.TIMBRADO_ZUL);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
	
	public boolean isClickAceptar(){
		return this.wp.isClickAceptar();
	}
	
	/**********************************************************************************************/
	
	@Init(superclass=true)
	public void init(@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) WindowTimbrado dato){
		this.dato = dato;
		this.buscarTimbrados();
	}
	
	@AfterCompose(superclass=true)
	public void afterCompose(){
	}
	
	@Override
	public String getAliasFormularioCorriente(){
		return ID.F_TIMBRADO;
	}
	
	private WindowTimbrado dato;
	private WindowPopup wp;
	private long idProveedor;
	private String timbrado = "";
	private MyArray selectedTimbrado;
	private List<MyArray> timbrados = new ArrayList<MyArray>();

	public WindowTimbrado getDato() {
		return dato;
	}

	public void setDato(WindowTimbrado dato) {
		this.dato = dato;
	}

	public long getIdProveedor() {
		return idProveedor;
	}

	public void setIdProveedor(long idProveedor) {
		this.idProveedor = idProveedor;
	}

	public String getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(String timbrado) {
		this.timbrado = timbrado;
	}

	public MyArray getSelectedTimbrado() {
		return selectedTimbrado;
	}

	public void setSelectedTimbrado(MyArray selectedTimbrado) {
		this.selectedTimbrado = selectedTimbrado;
	}

	public List<MyArray> getTimbrados() {
		return timbrados;
	}

	public void setTimbrados(List<MyArray> timbrados) {
		this.timbrados = timbrados;
	}	
	
	private void buscarTimbrados(){
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			this.dato.setTimbrados(rr.getTimbrados(this.dato.getIdProveedor(), this.dato.getTimbrado()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Command
	@NotifyChange("*")
	public void nuevoTimbrado(){
		MyArray nuevo = new MyArray("", null);
		this.dato.getTimbrados().add(nuevo);
		this.dato.setSelectedTimbrado(nuevo);
		BindUtils.postNotifyChange(null, null, this.dato, "*");
	}
	
	@Command
	@NotifyChange("*")
	public void validarNumero(@BindingParam("nro") String nro, @BindingParam("cmp") Textbox cmp){
		boolean encontro = false;
		
		for (MyArray m : this.dato.getTimbrados()) {
			String nr = (String) m.getPos1();
			if (m.esNuevo() == false) {
				if (nro.compareTo(nr) == 0) {				
					encontro = true;
				}
			}			
		}
		
		if (encontro == true) {
			Clients.showNotification("El número de timbrado: " + nro + "\n Ya existe..", "error", cmp, null, 2000);
			this.dato.getSelectedTimbrado().setPos1("");
		}
	}
	
	@Wire
	private Textbox txNum;
	
	@Command
	public void deshabilitarNvoTimbrado(){
		if (this.dato.selectedTimbrado.esNuevo() == false) {
			txNum.setDisabled(true);
		} else {
			txNum.setDisabled(false);
		}
	}
	
	//Guarda en la BD. el nuevo Timbrado
	public void agregarTimbrado(MyArray timbrado, long idProveedor){
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			Timbrado t = new Timbrado();
			t.setNumero((String) timbrado.getPos1());
			t.setVencimiento((Date) timbrado.getPos2());
			rr.saveObject(t, this.getLoginNombre());
			Proveedor p = rr.getProveedorById(idProveedor);
			p.getTimbrados().add(t);
			rr.saveObject(p, this.getLoginNombre());
			timbrado.setId(t.getId());
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}			
	}	
	
	/******************************************************************************************/
	
	private String mensajeVerificarAceptar = "";

	@Override
	public boolean verificarAceptar() {
		boolean out = false;
		try {
			out = this.validarTimbrado();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return mensajeVerificarAceptar;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al cancelar";
	}
	
	private boolean validarTimbrado() throws Exception {
		boolean out = true;
		this.mensajeVerificarAceptar = "No se puede completar la operación debido a: ";

		RegisterDomain rr = RegisterDomain.getInstance();

		if (this.selectedTimbrado == null) {
			this.mensajeVerificarAceptar += "\n - Debe seleccionar un Timbrado..";
			return false;
		}

		String nro = (String) this.selectedTimbrado.getPos1();

		if (nro.length() == 0) {
			out = false;
			this.mensajeVerificarAceptar += "\n - El número del Timbrado no puede quedar vacío..";
		}

		if (this.selectedTimbrado.getPos2() == null) {
			out = false;
			this.mensajeVerificarAceptar += "\n - La fecha de Vencimiento del Timbrado no puede quedar vacío..";
		}

		if (rr.existe(Timbrado.class, "numero", Config.TIPO_STRING, nro,
				this.selectedTimbrado) == true) {
			out = false;
			this.mensajeVerificarAceptar += "\n - Ya existe este número de Timbrado en la Base de Datos..";
		}

		return out;
	}
}
