package com.yhaguy.gestion.compras.gastos.subdiario;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.control.SoloViewModel;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.ArticuloGasto;

public class GastoSoloViewModel extends SoloViewModel implements VerificaAceptarCancelar{
	
	static final String[] ATRIBUTOS = {"cuentaContable.codigo", "cuentaContable.descripcion"};
	static final String[] ANCHOS = {"120px", ""};
	static final String[] COLUMNAS = {"Código", "Descripción"};

	private GastoSimpleControl dato = new GastoSimpleControl();	
	
	private UtilDTO utilDto = (UtilDTO) this.getDtoUtil();
	
	@Wire
	private Combobox cmbTipoIva;
	
	@Wire
	private Textbox txtObs;
	
	@Init(superclass=true)
	public void init(@ExecutionArgParam("dato") GastoSimpleControl dato){
		this.dato = dato;
	}
	
	@AfterCompose(superclass=true)
	public void afterCompose(){
	}
	
	@Override
	public String getAliasFormularioCorriente(){
		return ID.F_FACTURA_GASTO;
	}
	
	
	/******************* COMANDOS *******************/
	
	
	/**
	 * Seleccion del item de gasto..
	 */
	@Command
	public void selectItemDeGasto(@BindingParam("filtro") int filtro)
			throws Exception {

		String filtro_ = (String) this.dato.getNvoItem().getArticuloGasto()
				.getCuentaContable().getPos1();

		if (filtro > 0)
			filtro_ = (String) this.dato.getNvoItem().getArticuloGasto()
					.getCuentaContable().getPos2();

		BuscarElemento b = new BuscarElemento();
		b.setClase(ArticuloGasto.class);
		b.setAssembler(new AssemblerArticuloGasto());
		b.setAtributos(ATRIBUTOS);
		b.setAnchoColumnas(ANCHOS);
		b.setNombresColumnas(COLUMNAS);
		b.setTitulo("Ítems de Gastos");
		b.setWidth("800px");
		b.show(filtro_, filtro);
		
		if (b.isClickAceptar()) {
			ArticuloGastoDTO art = (ArticuloGastoDTO) b.getSelectedItemDTO();
			this.dato.getNvoItem().setArticuloGasto(art);
		}
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"articuloGasto");
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"tipoIva");
	}
	
	
	@Command
	public void dolarizar(){
		double montoGs = this.dato.getNvoItem().getMontoGs();
		double cambio = this.dato.getDto().getTipoCambio();
		this.dato.getNvoItem().setMontoDs(montoGs / cambio);
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(), "montoDs");
	}
	
	@Command
	public void guaranizar(){
		double montoDs = this.dato.getNvoItem().getMontoDs();
		double cambio = this.dato.getDto().getTipoCambio();
		this.dato.getNvoItem().setMontoGs(montoDs * cambio);
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(), "montoGs");
	}
	
	@DependsOn("dato.dto.moneda")
	public boolean isMonedaExtranjera(){
		boolean out = true;
		if (this.utilDto.getMonedaGuaraniConSimbolo().compareTo(this.dato.getDto().getMoneda()) == 0) {
			out = false;
		}
		return out;
	}
	
	/************************************************/	
	
	
	/***************** VALIDACIONES *****************/
	
	private String mensajeError = "";
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		mensajeError = "No se puede realizar la operación debido a: \n";
		
		if (this.dato.getNvoItemGasto().getDescripcion().length() == 0) {
			mensajeError = mensajeError + "\n - Debe ingresar la descripción..";
			out = false;
		}
		
		if (this.dato.getNvoItemGasto().getCuentaContable().esNuevo() == true) {
			mensajeError = mensajeError + "\n - Debe asignar una Cuenta Contable..";
			out = false;
		}
		
		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensajeError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return null;
	}
	
	
	/**
	 * GET / SET
	 */
	
	public GastoSimpleControl getDato() {
		return dato;
	}

	public void setDato(GastoSimpleControl dato) {
		this.dato = dato;
	}
}
