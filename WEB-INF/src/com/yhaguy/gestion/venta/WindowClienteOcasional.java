package com.yhaguy.gestion.venta;

import java.util.Date;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.gestion.comun.ControlLogicaEmpresa;
import com.yhaguy.gestion.empresa.ClienteDTO;
import com.yhaguy.gestion.empresa.EmpresaDTO;

public class WindowClienteOcasional extends SoloViewModel implements VerificaAceptarCancelar{
	
	private WindowClienteOcasional dato;
	private WindowPopup wp;
	private ClienteDTO clienteDto = new ClienteDTO();
	private ControlLogicaEmpresa ctrlog = new ControlLogicaEmpresa(null);
	
	private MyArray clienteMyArray = new MyArray();
	
	private static String TITULO = "Clientes Ocasionales";
	
	@Init(superclass = true)
	public void init(@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) 
						WindowClienteOcasional dato) {
		this.dato = dato;
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose(){
	}
	
	@Override
	public String getAliasFormularioCorriente(){
		return ID.F_CLIENTE_OCASIONAL;
	}
	
	//Metodo que despliega la ventana..
	public void show(String modo, WindowClienteOcasional dato, ClienteDTO cli){
		
		try {
			
			if (cli != null) {
				this.clienteDto = cli;
			} else {
				this.sugerirValores(dato.getClienteDto());
			}			
			wp = new WindowPopup();
			wp.setDato(dato);
			wp.setCheckAC(dato);
			wp.setModo(modo);
			wp.setTitulo(TITULO);
			wp.setWidth("400px");
			wp.setHigth("370px");			
			wp.show(Configuracion.CLIENTES_OCASIONALES_ZUL);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void show(String modo, WindowClienteOcasional dato){
		this.show(modo, dato, null);
	}
	
	public boolean isClickAceptar(){
		return this.wp.isClickAceptar();
	}
	
	
	
	/***************************** VALIDAR RUC *************************/
	
	public boolean rucValido = false;
	public String rucValidado = "";
	public String ciValidado = "";
	
	@Command @NotifyChange("*")
	public void validarRuc() throws Exception {

		this.dato.getClienteDto().setRazonSocial("");
		this.dato.getClienteDto().setNombre("");
		this.dato.getClienteDto().getEmpresa().setRazonSocialSet(false);
		
		this.rucValido = this.ctrlog.buscarRUC(this.dato.getClienteDto());
		this.rucValidado = this.dato.getClienteDto().getRuc();
		this.ciValidado = this.dato.getClienteDto().getEmpresa().getCi();
		
		this.dato.rucValido = this.rucValido;
		this.dato.rucValidado = this.rucValidado;
		this.dato.ciValidado = this.ciValidado;
	}
	
	@Command @NotifyChange("*")
	public void validarPais(){
		this.ctrlog.verificarPais(this.dato.getClienteDto().getEmpresa());
	}
	
	//retorna true si es pais del exterior.
	public boolean isPaisExterior(){
		MyPair pais = this.dato.getClienteDto().getEmpresa().getPais();
		return this.ctrlog.isPaisExterior(pais);
	}
	
	/*******************************************************************/	
	
	
	/******************************* UTILES ****************************/
	
	private UtilDTO utilDto = (UtilDTO) this.getDtoUtil();
	
	private void sugerirValores(ClienteDTO dto){
		
		EmpresaDTO emp = dto.getEmpresa();
		emp.setPais(utilDto.getPaisParaguay());
		emp.setRegimenTributario(utilDto.getRegimenTributarioNoExenta());
		emp.setFechaIngreso(new Date());		
		emp.setEmpresaGrupoSociedad(utilDto.getGrupoEmpresaNoDefinido());
		
		dto.setTipoCliente(this.utilDto.getTipoClienteOcasional());
		dto.setEstadoCliente(utilDto.getEstadoClienteActivo());
		dto.setCategoriaCliente(utilDto.getCategoriaClienteAldia());
		dto.setCuentaContable(utilDto.getCuentaClientesOcasionales());
		
	}
	
	@Command
	public void copiarRazonSocial() {
		ClienteDTO clienteDto = this.dato.getClienteDto();
		clienteDto.setNombre(clienteDto.getRazonSocial());
		BindUtils.postNotifyChange(null, null, clienteDto, "nombre");
	}
	
	/*******************************************************************/
	

	/**************************** VALIDACIONES *************************/
	
	private String msgError = "";
	private static String txtError = "No se puede realizar la " +
			"operación debido a \n";
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;		
		
		String ruc = this.clienteDto.getRuc();
		String ci = this.clienteDto.getEmpresa().getCi();
		String rs = this.clienteDto.getRazonSocial();
		String nmb = this.clienteDto.getNombre();
		
		this.msgError = txtError;
		
		//verifica que sea valido el ruc
		if (this.rucValido == false) {
			this.msgError += "\n - RUC ó CI no válido";
			out = false;
		}
		
		//verifica que se haya procesado la validacion del ruc.
		if (this.rucValidado.compareTo(ruc) != 0) {
			this.msgError += "\n - Debe verificar el RUC.";
			out = false;
		}
		
		//verifica que se haya procesado la validacion del ci.
		if (this.ciValidado.compareTo(ci) != 0) {
			this.msgError += "\n - Debe verificar la cédula.";
			out = false;
		}
		
		//verifica que la razonSocial no este vacia
		if (rs.trim().length() == 0) {
			this.msgError += "\n - La Razón Social no puede quedar vacía.";
			out = false;
		}
		
		//verifica que el nombre no este vacio
		if (nmb.trim().length() == 0) {
			this.msgError += "\n - El nombre no puede quedar vacío.";
			out = false;
		}
		
		//Si la validacion es correcta setea los valores del clienteMyArray
		if (out == true) {
			MyArray cl = new MyArray();
			cl.setId(this.clienteDto.getId());
			cl.setPos1(this.clienteDto.getCodigoEmpresa());
			cl.setPos2(this.clienteDto.getRazonSocial());
			cl.setPos3(this.clienteDto.getRuc());
			cl.setPos4(this.clienteDto.getIdEmpresa());
			cl.setPos5(this.clienteDto.getTipoCliente());
			cl.setPos6(this.clienteDto.getEmpresa().isRazonSocialSet());
			cl.setPos9(false);
			this.clienteMyArray = cl;
		}
		
		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.msgError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error Al Cancelar";
	}

	/*******************************************************************/	
	
	
	/***************************** GETTER/SETTER ***********************/
	
	public ClienteDTO getClienteDto() {
		return clienteDto;
	}

	public void setClienteDto(ClienteDTO dto) {
		this.clienteDto = dto;
	}
	
	public boolean isRucValido() {
		return rucValido;
	}

	public void setRucValido(boolean rucValidado) {
		this.rucValido = rucValidado;
	}

	public WindowClienteOcasional getDato() {
		return dato;
	}

	public void setDato(WindowClienteOcasional dato) {
		this.dato = dato;
	}

	/**
	 * pos1:codigo - pos2:razonSocial - pos3:ruc 
	 * pos4:idEmpresa - pos5:tipoCliente  
	 * pos6:razonSocialSet
	 */
	public MyArray getClienteMyArray() {
		return clienteMyArray;
	}

	public void setClienteMyArray(MyArray clienteMyArray) {
		this.clienteMyArray = clienteMyArray;
	}	
}
