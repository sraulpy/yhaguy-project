package com.yhaguy.util.macros;

import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.annotation.ComponentAnnotation;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.gestion.articulos.ArticuloDTO;
import com.yhaguy.gestion.comun.ControlLogica;

@ComponentAnnotation("value:@ZKBIND(ACCESS=both,SAVE_EVENT=onEdited)")
public class BuscadorArticulo extends HtmlMacroComponent {

	private static String CAMPO_CODIGO_INTERNO = "codigoInterno";
	private static String CAMPO_DESCRIPCION = "descripcion";
	
	private Object dato = null;
	private String campo = CAMPO_DESCRIPCION;
	private String where = "";
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
		this.setDato(this.buscarArticulo());
		if (this.getDato() != null) {
			misc.ejecutarMetoto(this.parent.getClass().getName(),
					"setArticulo", this.parent, this.getDato());
		}
		BindUtils.postGlobalCommand(null, null, "refreshComponentes", null);
	}	

	private Object buscarArticulo() throws Exception {
		Object out = new Object();

		int posFiltro = this.getPosFiltro();
		String codigoInterno = this.getCodigoInterno();
		String descripcion = this.getDescripcion(); 
		String where = this.where;

		if (this.getArticulo() instanceof ArticuloDTO) {
			out = ctr.buscarArticuloDTO(codigoInterno, descripcion, posFiltro,
					where);

		} else if (this.getArticulo() instanceof MyArray) {
			out = ctr.buscarArticuloMyArray(codigoInterno, descripcion, posFiltro,
					where);
		}
		return out;
	}	

	private void setTextValue(Object articulo) {
		String value = "";
		try {

			if (articulo instanceof ArticuloDTO) {
				if (campo.compareTo(CAMPO_CODIGO_INTERNO) == 0) {
					value = ((ArticuloDTO) articulo).getCodigoInterno();
				}
				if (campo.compareTo(CAMPO_DESCRIPCION) == 0) {
					value = ((ArticuloDTO) articulo).getDescripcion();
				} 
			}

			if (articulo instanceof MyArray) {
				if (campo.compareTo(CAMPO_CODIGO_INTERNO) == 0) {
					value = (String) ((MyArray) articulo).getPos1();
				}
				if (campo.compareTo(CAMPO_DESCRIPCION) == 0) {
					value = (String) ((MyArray) articulo).getPos2();
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		searchText.setValue(value);
	}
	
	private void setPlaceholder(){
		if (this.campo.compareTo(CAMPO_CODIGO_INTERNO) == 0) {
			searchText.setPlaceholder("Código Interno..");
		}
		if (this.campo.compareTo(CAMPO_DESCRIPCION) == 0) {
			searchText.setPlaceholder("Descripción..");
		} 
	}
	
	private void setSize(){
		dv.setWidth(this.getWidth());
		int widthRoot = Integer.parseInt(dv.getWidth().replace("px", ""));
		int widthText = widthRoot - 28;
		searchText.setWidth(widthText + "px");
	}
	
	/************************ GETTER/SETTER ***********************/
	
	private Object getArticulo() throws Exception{
		Object out = null;
		out = this.misc.ejecutarMetoto(this.parent, "getArticulo");
		return out;
	}
	
	public Object getValue() {
		return parent;
	}

	public void setValue(Object value) {
		this.parent = value;	
		try {
			this.setTextValue(this.getArticulo());
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

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}
	
	public String getWidth(){
		return width;
	}
	
	public void setWidth(String width){
		this.width = width;
	}
	
	// retorna la posicion del filtro segun el valor [campoFiltro]
	private int getPosFiltro() {
		int out = 1; // descripcion por defecto
		if (this.campo.compareTo(CAMPO_CODIGO_INTERNO) == 0) {
			out = 0;
		}
		if (this.campo.compareTo(CAMPO_DESCRIPCION) == 0) {
			out = 1;
		}
		return out;
	}

	// retorna el valor del filtro codigo segun el [campoFiltro]
	private String getCodigoInterno() {
		if (this.campo.compareTo(CAMPO_CODIGO_INTERNO) == 0) {
			return searchText.getValue();
		} else {
			return "";
		}
	}

	// retorna el valor del filtro descripcion segun el [campoFiltro]
	private String getDescripcion() {
		if (this.campo.compareTo(CAMPO_DESCRIPCION) == 0) {
			return searchText.getValue();
		} else {
			return "";
		}
	}

	 
}
