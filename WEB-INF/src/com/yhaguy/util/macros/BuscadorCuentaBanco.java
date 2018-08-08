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
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;
import com.yhaguy.gestion.comun.ControlLogica;
import com.yhaguy.gestion.empresa.ClienteDTO;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@ComponentAnnotation("value:@ZKBIND(ACCESS=both,SAVE_EVENT=onEdited)")
public class BuscadorCuentaBanco extends HtmlMacroComponent {

	private static String CAMPO_NRO_CUENTA = "nroCuenta";
	private static String CAMPO_BANCO = "banco.descripcion";
	private static String CAMPO_TIPO = "tipo.descripcion";
	private static String CAMPO_MONEDA = "moneda.descripcion";

	private Object dato = null;
	private String campo = CAMPO_NRO_CUENTA;
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
		this.setDato(this.buscarCuentaBanco());
		if (this.getDato() != null) {
			System.out.println("------> instancia de " + this.getDato().toString());
			misc.ejecutarMetoto(this.parent.getClass().getName(), "setNroCuenta", this.parent, this.getDato());
		}
		BindUtils.postGlobalCommand(null, null, "refreshComponentes", null);
	}

	private Object buscarCuentaBanco() throws Exception {
		Object out = new Object();

		int posFiltro = this.getPosFiltro();
		String nroCuenta = this.getNroCuenta();
		String banco = this.getBanco();
		String tipo = this.getTipo();
		String moneda = this.getMoneda();
		String where = this.where;

		if (this.getCuenta() instanceof BancoCtaDTO) {
			out = ctr.buscarCuentaBancoDTO(nroCuenta, banco, tipo, moneda, posFiltro, where);

		} else if (this.getCuenta() instanceof MyArray) {
			out = ctr.buscarCuentaBanco(nroCuenta, banco, tipo, moneda, posFiltro, where);
		}
		return out;
	}

	private void setTextValue(Object cuentaBanco) {
		String value = "";
		try {

			if (cuentaBanco instanceof BancoCtaDTO) {
				if (campo.compareTo(CAMPO_NRO_CUENTA) == 0) {
					value = ((BancoCtaDTO) cuentaBanco).getNroCuenta();
				}
				if (campo.compareTo(CAMPO_BANCO) == 0) {
					value = ((BancoCtaDTO) cuentaBanco).getBancoDescripcion();
				}
				if (campo.compareTo(CAMPO_TIPO) == 0) {
					value = ((BancoCtaDTO) cuentaBanco).getTipo().getText();
				}
				if (campo.compareTo(CAMPO_MONEDA) == 0) {
					value = (String) ((BancoCtaDTO) cuentaBanco).getMoneda().getPos1();
				}
			}

			if (cuentaBanco instanceof MyArray) {
				if (campo.compareTo(CAMPO_NRO_CUENTA) == 0) {
					value = (String) ((MyArray) cuentaBanco).getPos1();
				}
				if (campo.compareTo(CAMPO_BANCO) == 0) {
					value = (String) ((MyArray) cuentaBanco).getPos2();
				}
				if (campo.compareTo(CAMPO_TIPO) == 0) {
					value = (String) ((MyArray) cuentaBanco).getPos3();
				}
				if (campo.compareTo(CAMPO_MONEDA) == 0) {
					value = (String) ((MyArray) cuentaBanco).getPos3();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		searchText.setValue(value);
	}

	private void setPlaceholder() {

		if (this.campo.compareTo(CAMPO_NRO_CUENTA) == 0) {
			searchText.setPlaceholder("Nro Cuenta..");
		}
		if (this.campo.compareTo(CAMPO_BANCO) == 0) {
			searchText.setPlaceholder("Banco..");
		}
		if (this.campo.compareTo(CAMPO_TIPO) == 0) {
			searchText.setPlaceholder("Tipo..");
		}
		if (this.campo.compareTo(CAMPO_MONEDA) == 0) {
			searchText.setPlaceholder("Moneda..");
		}
	}

	/************************ GETTER/SETTER ***********************/

	private Object getCuenta() throws Exception {
		Object out = null;
		out = this.misc.ejecutarMetoto(this.parent, "getNroCuenta");
		return out;
	}

	public Object getValue() {
		return parent;
	}

	public void setValue(Object value) {
		this.parent = value;
		try {
			this.setTextValue(this.getCuenta());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getDato() {
		return dato;
	}

	public void setDato(Object dato) {
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

	private int getPosFiltro() {
		int out = 0; 
		if (this.campo.compareTo(CAMPO_NRO_CUENTA) == 0) {
			out = 0;
		}
		if (this.campo.compareTo(CAMPO_BANCO) == 0) {
			out = 1;
		}
		return out;
	}

	private String getNroCuenta() {
		if (this.campo.compareTo(CAMPO_NRO_CUENTA) == 0) {
			return searchText.getValue();
		} else {
			return "";
		}
	}

	private String getBanco() {
		if (this.campo.compareTo(CAMPO_BANCO) == 0) {
			return searchText.getValue();
		} else {
			return "";
		}
	}

	private String getTipo() {
		if (this.campo.compareTo(CAMPO_TIPO) == 0) {
			return searchText.getValue();
		} else {
			return "";
		}
	}

	private String getMoneda() {
		if (this.campo.compareTo(CAMPO_MONEDA) == 0) {
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
