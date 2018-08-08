package com.yhaguy.util.macros;

import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.annotation.ComponentAnnotation;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.gestion.comun.ControlLogica;
import com.yhaguy.gestion.empresa.ClienteDTO;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@ComponentAnnotation("value:@ZKBIND(ACCESS=both,SAVE_EVENT=onEdited)")
public class BuscadorCliente extends HtmlMacroComponent {

	private static String CAMPO_CODIGO = "codigo";
	private static String CAMPO_RAZON_SOCIAL = "razonSocial";
	private static String CAMPO_RUC = "ruc";
	private static String CAMPO_NOMBRE_FANTASIA = "nombre";
	
	private Object dato = null;
	private String campo = CAMPO_RAZON_SOCIAL;
	private String where = "";
	private Misc misc = new Misc();
	private ControlLogica ctr = new ControlLogica(null);
	private Object parent = null;
	private boolean buttonVisible = true;

	@Wire
	Button searchBtn;

	@Wire
	Textbox searchText;
	
	
	public void afterCompose() {
		super.afterCompose();
		this.setPlaceholder();	
		
		searchBtn.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				executeEvent();
			}
		});
		
		searchBtn.setVisible(this.buttonVisible);

		searchText.addEventListener(Events.ON_OK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				executeEvent();
			}
		});
	}

	// Ejecuta las funciones del componente
	private void executeEvent() throws Exception {
		this.setDato(this.buscarCliente());
		if (this.getDato() != null) {
			misc.ejecutarMetoto(this.parent.getClass().getName(),
					"setCliente", this.parent, this.getDato());
		}
		BindUtils.postGlobalCommand(null, null, "refreshComponentes", null);
	}	

	private Object buscarCliente() throws Exception {
		Object out = new Object();

		int posFiltro = this.getPosFiltro();
		String codigo = this.getCodigo();
		String razonSocial = this.getRazonSocial();
		String ruc = this.getRuc();
		String nombreFantasia = this.getNombreFantasia();
		String where = this.where;

		if (this.getCliente() instanceof ClienteDTO) {
			out = ctr.buscarClienteDTO(codigo, razonSocial, ruc, nombreFantasia, posFiltro, where);

		} else if (this.getCliente() instanceof MyArray) {
			out = ctr.buscarCliente(codigo, razonSocial, ruc, nombreFantasia, posFiltro, where);
		}
		return out;
	}	

	private void setTextValue(Object cliente) {
		String value = "";
		try {

			if (cliente instanceof ClienteDTO) {
				if (campo.compareTo(CAMPO_CODIGO) == 0) {
					value = ((ClienteDTO) cliente).getCodigoEmpresa();
				}
				if (campo.compareTo(CAMPO_RAZON_SOCIAL) == 0) {
					value = ((ClienteDTO) cliente).getRazonSocial();
				}
				if (campo.compareTo(CAMPO_RUC) == 0) {
					value = ((ClienteDTO) cliente).getRuc();
				}
				if (campo.compareTo(CAMPO_NOMBRE_FANTASIA) == 0) {
					value = ((ClienteDTO) cliente).getNombre();
				}
			}

			if (cliente instanceof MyArray) {
				if (campo.compareTo(CAMPO_CODIGO) == 0) {
					value = (String) ((MyArray) cliente).getPos1();
				}
				if (campo.compareTo(CAMPO_RAZON_SOCIAL) == 0) {
					value = (String) ((MyArray) cliente).getPos2();
				}
				if (campo.compareTo(CAMPO_RUC) == 0) {
					value = (String) ((MyArray) cliente).getPos3();
				}
				if (campo.compareTo(CAMPO_NOMBRE_FANTASIA) == 0) {
					value = (String) ((MyArray) cliente).getPos10();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		searchText.setValue(value);
	}
	
	private void setPlaceholder(){
		if (this.campo.compareTo(CAMPO_CODIGO) == 0) {
			searchText.setPlaceholder("Código..");
		}
		if (this.campo.compareTo(CAMPO_RAZON_SOCIAL) == 0) {
			searchText.setPlaceholder("Razón Social..");
		}
		if (this.campo.compareTo(CAMPO_RUC) == 0) {
			searchText.setPlaceholder("Ruc..");
		}
		if (this.campo.compareTo(CAMPO_NOMBRE_FANTASIA) == 0) {
			searchText.setPlaceholder("Nombre Fantasía..");
		}
	}
	
	/************************ GETTER/SETTER ***********************/
	
	private Object getCliente() throws Exception{
		Object out = null;
		out = this.misc.ejecutarMetoto(this.parent, "getCliente");
		return out;
	}
	
	public Object getValue() {
		return parent;
	}

	public void setValue(Object value) {
		this.parent = value;	
		try {
			this.setTextValue(this.getCliente());
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
	
	// retorna la posicion del filtro segun el valor [campoFiltro]
	private int getPosFiltro() {
		int out = 1; // razonSocial por defecto
		if (this.campo.compareTo(CAMPO_CODIGO) == 0) {
			out = 0;
		}
		if (this.campo.compareTo(CAMPO_RUC) == 0) {
			out = 2;
		}
		if (this.campo.compareTo(CAMPO_NOMBRE_FANTASIA) == 0) {
			out = 9;
		}
		return out;
	}

	// retorna el valor del filtro codigo segun el [campoFiltro]
	private String getCodigo() {
		if (this.campo.compareTo(CAMPO_CODIGO) == 0) {
			return searchText.getValue();
		} else {
			return "";
		}
	}

	// retorna el valor del filtro razonSocial segun el [campoFiltro]
	private String getRazonSocial() {
		if (this.campo.compareTo(CAMPO_RAZON_SOCIAL) == 0) {
			return searchText.getValue();
		} else {
			return "";
		}
	}

	// retorna el valor del filtro razonSocial segun el [campoFiltro]
	private String getRuc() {
		if (this.campo.compareTo(CAMPO_RUC) == 0) {
			return searchText.getValue();
		} else {
			return "";
		}
	}
	
	// retorna el valor del filtro nombre fantasia segun el [campoFiltro]
	private String getNombreFantasia() {
		if (this.campo.compareTo(CAMPO_NOMBRE_FANTASIA) == 0) {
			return searchText.getValue();
		} else {
			return "";
		}
	}

	public boolean isButtonVisible() {
		return buttonVisible;
	}

	public void setButtonVisible(boolean buttonVisible) {
		this.buttonVisible = buttonVisible;
	}			
}
