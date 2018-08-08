package com.yhaguy.util.macros;

import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.annotation.ComponentAnnotation;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.yhaguy.gestion.empresa.FuncionarioDTO;
import com.yhaguy.gestion.comun.ControlLogica;

import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;

@ComponentAnnotation("value:@ZKBIND(ACCESS=both,SAVE_EVENT=onEdited)")
public class BuscadorFuncionario extends HtmlMacroComponent {

	private static String CAMPO_NOMBRE = "nombre";
	
	private Object dato = null;
	private String campo = CAMPO_NOMBRE;
	private String width = "150px";
	private Misc misc = new Misc();
	private ControlLogica ctr = new ControlLogica(null);
	private Object parent = null;

	@Wire
	Div dv;

	@Wire
	Toolbarbutton searchBtn;

	@Wire
	Textbox searchText;

	
	public void afterCompose() {
		super.afterCompose();
		this.setPlaceholder();	
		this.setSize();
		
		searchBtn.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				executeEvent();
			}
		});

		searchText.addEventListener(Events.ON_OK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				executeEvent();
			}
		});
	}

	// Ejecuta las funciones del componente
	private void executeEvent() throws Exception {
		this.setDato(this.buscarFuncionario());
		if (this.getDato() != null) {
			misc.ejecutarMetoto(this.parent.getClass().getName(),
					"setFuncionario", this.parent, this.getDato());
		}
		BindUtils.postGlobalCommand(null, null, "refreshComponentes", null);
	}	

	private Object buscarFuncionario() throws Exception {
		Object out = new Object();		
		String nombre = this.getNombre();

		if (this.getFuncionario() instanceof FuncionarioDTO) {
			out = ctr.buscarFuncionarioDTO(nombre);

		} else if (this.getFuncionario() instanceof MyArray) {
			out = ctr.buscarFuncionario(nombre);
		}
		return out;
	}	

	private void setTextValue(Object funcionario) {
		String value = "";
		try {
			if (funcionario instanceof FuncionarioDTO) {
				if (campo.compareTo(CAMPO_NOMBRE) == 0) {
					value = ((FuncionarioDTO) funcionario).getNombre();
				}
			}

			if (funcionario instanceof MyArray) {
				if (campo.compareTo(CAMPO_NOMBRE) == 0) {
					value = (String) ((MyArray) funcionario).getPos1();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		searchText.setValue(value);
	}
	
	private void setPlaceholder(){
		if (this.campo.compareTo(CAMPO_NOMBRE) == 0) {
			searchText.setPlaceholder("Nombre..");
		}
	}
	
	private void setSize(){
		dv.setWidth(this.getWidth());
		int widthRoot = Integer.parseInt(dv.getWidth().replace("px", ""));
		int widthText = widthRoot - 28;
		searchText.setWidth(widthText + "px");
	}
	
	
	/************************ GETTER/SETTER ***********************/
	
	private Object getFuncionario() throws Exception{
		Object out = null;
		out = this.misc.ejecutarMetoto(this.parent, "getFuncionario");
		return out;
	}
	
	public Object getValue() {
		return parent;
	}

	public void setValue(Object value) {
		this.parent = value;	
		try {
			this.setTextValue(this.getFuncionario());
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public Object getDato(){
		return dato;
	}
	
	public void setDato(Object dato){
		this.dato = dato;
		this.setTextValue(dato);
	}

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}
	
	public String getWidth(){
		return width;
	}
	
	public void setWidth(String width){
		this.width = width;
	}	

	// retorna el valor del filtro razonSocial segun el [campoFiltro]
	private String getNombre() {
		if (this.campo.compareTo(CAMPO_NOMBRE) == 0) {
			return searchText.getValue();
		} else {
			return "";
		}
	}
}
