package com.yhaguy.gestion.stock.definiciones;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyPair;
import com.yhaguy.UtilDTO;
public class StockDefinicionesViewModel extends SimpleViewModel{
	
	private MyPair selectedItem = new MyPair();
	private MyPair nuevoItem = new MyPair();
	private String[] tipos = {"Linea", "Familia", "Parte", "Marca"};
	private String[] operaciones = {"Agregar", "Editar"};
	private AssemblerDefiniciones ass = new AssemblerDefiniciones();
	
	static final int AGREGAR = 0;
	static final int EDITAR = 1;
	static final String PATHPOPUP = "/yhaguy/gestion/stock/definicionesPopup.zul";
	
	@Init(superclass = true)
	public void init(){
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose(){
		
	}
	
	/*************** COMANDOS  **************/
	
	@Command
	@NotifyChange("*")
	public void abrirPopupDatos(@BindingParam("tipo") int tipo,
			@BindingParam("operacion") int operacion) throws Exception{
		this.agregarEditar(tipo, operacion);		
	}
	
	/*************** FUNCIONES  **************/
	
	public void agregarEditar(int tipo, int operacion) throws Exception{
		
		String titulo ="";
		this.nuevoItem = new MyPair();
		if ((operacion == EDITAR) 
				&& (this.selectedItem.esNuevo() == true)) {
			this.mensajeError("Debe seleccionar un ítem..");
			return;
		
		} else if (operacion == AGREGAR) {
			titulo = this.operaciones[operacion] +" nueva "+ this.tipos[tipo];
		} else if (operacion == EDITAR) {
			titulo = this.operaciones[operacion] +" item "+ this.tipos[tipo];
			this.nuevoItem = this.selectedItem;
		}
		
		WindowPopup wp = new WindowPopup();
		wp.setTitulo(titulo);
		wp.setModo(WindowPopup.NUEVO);
		wp.setDato(this);
		wp.setCheckAC(this.getCheckAC(operacion));
		wp.setWidth("400px");
		wp.setHigth("300px");
		wp.show(PATHPOPUP);

		if (wp.isClickAceptar() == true) {
				this.ass.gravarItemDefinicines(this.nuevoItem, tipo, operacion);
		}
		
	}
	
	public VerificaAceptarCancelar getCheckAC(int operacion) {
		switch (operacion) {

		case AGREGAR:
			return new ValidadorArticuloDefinicion(this.nuevoItem);

		case EDITAR:
			return new ValidadorArticuloDefinicion(this.nuevoItem);

		default:
			return null;
		}
	}
	
	
	
	/*************** GET Y SET **************/
	
	public UtilDTO getUtil(){
		return (UtilDTO) this.getDtoUtil();
	}

	public MyPair getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MyPair selectedItem) {
		this.selectedItem = selectedItem;
	}

	public MyPair getNuevoItem() {
		return nuevoItem;
	}

	public void setNuevoItem(MyPair nuevoItem) {
		this.nuevoItem = nuevoItem;
	}
}

/**
 * Validador Artículo Definiciones
 *
 */
class ValidadorArticuloDefinicion implements VerificaAceptarCancelar {

	private MyPair item;
	private String mensaje = "";
	
	public ValidadorArticuloDefinicion(MyPair item) {
		this.item = item;
	}
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensaje = "No se puede completar la operación debido a: \n";
		String descripcion = (String) this.item.getText();
		String sigla = (String) this.item.getSigla();
		
		if (descripcion.trim().length() == 0) {
			out = false;
			this.mensaje += "- Debe ingresar una descripción \n";
		}
		if (sigla.trim().length() == 0) {
			out = false;
			this.mensaje += "- Debe ingresar una sigla \n";
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