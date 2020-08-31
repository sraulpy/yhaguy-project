package com.yhaguy.gestion.bancos.cheques;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SoloViewModel;
import com.coreweb.domain.AutoNumero;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoCtaCte;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;

public class WindowCheque extends SoloViewModel {	
	
	private AutoNumero selectedChequera;
	
	public void show(String modo, boolean chequeManual) {
		try {
			this.chequeManual = chequeManual;
			wp = new WindowPopup();
			wp.setDato(this);
			wp.setCheckAC(new ValidadorWindowCheque(this));
			wp.setModo(modo);
			wp.setTitulo("Agregar Cheque");
			wp.setWidth("750px");
			wp.setHigth("530px");
			wp.show(Configuracion.CHEQUE_ZUL);			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void show(String modo) {
		this.show(modo, false);
	}
	
	public boolean isClickAceptar(){
		return this.wp.isClickAceptar();
	}	
	
	private WindowPopup wp; 
	private WindowCheque dato;
	private BancoChequeDTO chequeDTO = new BancoChequeDTO();	
	private BancoCtaDTO cuentaDTO = new BancoCtaDTO();
	private double montoRecibo = 0;
	private boolean chequeManual;

	@Init(superclass=true)
	public void init(@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) WindowCheque dato){
		this.chequeDTO = dato.chequeDTO;
		this.cuentaDTO = dato.cuentaDTO;
		this.dato = dato;
		this.montoRecibo = dato.chequeDTO.getMonto();
	}
	
	@AfterCompose(superclass=true)
	public void afterCompose(){
	}
	
	@Override
	public String getAliasFormularioCorriente(){
		return ID.F_BANCO_CHEQUE;
	}
	
	@Command
	@NotifyChange("*")
	public void setNumero() {
		this.chequeDTO.setNumero(this.selectedChequera.getNumero() + 1);
		Map<String, Object> mp = new HashMap<String, Object>();
		mp.put("key", this.selectedChequera.getKey());
		BindUtils.postGlobalCommand(null, null, "updateAuxi", mp);
	}
	
	private static String[] attBanco = { "banco.descripcion", "nroCuenta",
			"tipo.descripcion", "moneda.descripcion" };
	private static String[] columnas = { "Banco", "Número", "Tipo", "Moneda" };
	
	@Command @NotifyChange("*")
	public void buscarBanco() throws Exception{
		
		long moneda = this.getMonedaCorriente().getId();
		
		String descripcion = (String) this.cuentaDTO.getBanco().getPos1();
		
		BuscarElemento b = new BuscarElemento();
		b.setClase(BancoCta.class);
		b.setAssembler(new AssemblerBancoCtaCte());
		b.setAtributos(attBanco);
		b.setTitulo("Buscar Cuentas Bancarias");
		b.setNombresColumnas(columnas);
		b.setWidth("800px");
		b.addOrden("banco.descripcion");
		b.addWhere("moneda.id = " + moneda);
		b.show(descripcion);
		
		if (b.isClickAceptar()) {				
			this.cuentaDTO = (BancoCtaDTO) b.getSelectedItemDTO();
			this.dato.setCuentaDTO(this.cuentaDTO);
		}	
		
		Map<String, Object> mp = new HashMap<String, Object>();
		mp.put("cuenta", this.cuentaDTO);
		// se usa en ReciboSimpleControl.java
		BindUtils.postGlobalCommand(null, null, "updateCuentaBanco", mp);
		BindUtils.postNotifyChange(null, null, this.cuentaDTO, "*");
	}
	
	/****************************************************/
	
	
	/******************  GETTER/SETTER ******************/
	
	@DependsOn("cuentaDTO")
	public List<AutoNumero> getChequeras() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<AutoNumero> out = rr.getBancoChequeras(this.cuentaDTO.getDescripcion());
		List<AutoNumero> out_ = new ArrayList<AutoNumero>();
		for (AutoNumero item : out) {
			item.setDescripcion("(" + item.getNumeroDesde() + " - " + item.getNumeroHasta() + ") - " + (item.getNumero() + 1));
		}
		out_.addAll(out);
		for (AutoNumero item : out) {
			if (item.getNumero() >= item.getNumeroHasta()) {
				out_.remove(item);
			}
		}
		return out_;
	}
	
	public WindowPopup getWp() {
		return wp;
	}

	public void setWp(WindowPopup wp) {
		this.wp = wp;
	}

	public BancoChequeDTO getChequeDTO() {
		return chequeDTO;
	}

	public void setChequeDTO(BancoChequeDTO chequeDTO) {
		this.chequeDTO = chequeDTO;
	}

	public BancoCtaDTO getCuentaDTO() {
		return cuentaDTO;
	}

	public void setCuentaDTO(BancoCtaDTO cuentaDTO) {
		this.cuentaDTO = cuentaDTO;
	}	
	
	public String getFormat(){
		String sigla = (String) this.getMonedaCorriente().getPos2();
		
		if (sigla.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0) {
			return Configuracion.FORMAT_MONEDA_LOCAL;
		} else {
			return Configuracion.FORMAT_MONEDA_EXTRANJERA;
		}
	}
	
	/**
	 * @return las monedas..
	 */
	public List<MyArray> getMonedas() {
		return this.getDtoUtil().getMonedasConSimbolo();
	}
	
	public UtilDTO getDtoUtil() {
		UtilDTO u = (UtilDTO)super.getDtoUtil();
		return u;
	}
	
	// para saber el tipo de momenda a usar
	private MyArray getMonedaCorriente(){
		MyArray out = null;
		if (this.chequeDTO.getMoneda() != null){
			out = this.chequeDTO.getMoneda();
		}else{
			out = this.cuentaDTO.getMoneda();
		}
		return out;		
	}

	public double getMontoRecibo() {
		return montoRecibo;
	}

	public void setMontoRecibo(double montoCheque) {
		this.montoRecibo = montoCheque;
	}

	public boolean isChequeManual() {
		return chequeManual;
	}

	public void setChequeManual(boolean chequeManual) {
		this.chequeManual = chequeManual;
	}

	public AutoNumero getSelectedChequera() {
		return selectedChequera;
	}

	public void setSelectedChequera(AutoNumero selectedChequera) {
		this.selectedChequera = selectedChequera;
	}
}

// Validador del WindowCheque
class ValidadorWindowCheque implements VerificaAceptarCancelar {

	private WindowCheque windowCheque;
	private String mensaje = "";
	
	public ValidadorWindowCheque(WindowCheque windowCheque) {
		this.windowCheque = (WindowCheque) windowCheque.getWp().getDato();
	}
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensaje = "No se puede completar la operación debido a: ";	
		
		BancoCtaDTO cuentaBanco = this.windowCheque.getCuentaDTO();
		BancoChequeDTO cheque = this.windowCheque.getChequeDTO();
		
		if (cuentaBanco.esNuevo() == true) {
			this.mensaje += "\n - Debe seleccionar una cuenta de banco..";
			out = false;
		}
		
		if (cheque.getNumero() == 0) {
			this.mensaje += "\n - Debe ingresar un numero de cheque..";
			out = false;
		}
		
		if (cheque.getBeneficiario().trim().length() == 0) {
			this.mensaje += "\n - Debe ingresar el beneficiario..";
			out = false;
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
