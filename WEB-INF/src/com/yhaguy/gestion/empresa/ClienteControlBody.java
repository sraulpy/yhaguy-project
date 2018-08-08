package com.yhaguy.gestion.empresa;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.coreweb.componente.BodyPopupAceptarCancelar;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;

public class ClienteControlBody extends EmpresaControlBody {

	private ClienteDTO dto = new ClienteDTO();

	@Init(superclass = true)
	public void init() {
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	public ClienteDTO getDto() {
		return dto;
	}

	public void setDto(ClienteDTO dto) {
		this.dto = dto;
		this.setDtoEmp(this.dto.getEmpresa());
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (ClienteDTO) dto;
		this.setDtoEmp(this.dto.getEmpresa());
	}

	@Override
	public DTO nuevoDTO() {
		ClienteDTO aux = new ClienteDTO();
		UtilDTO auxUtil = (UtilDTO)this.getDtoUtil();
		aux.setCuentaContable(auxUtil.getCuentaClientesOcasionales());
		aux.setTipoCliente(auxUtil.getTipoClienteMinorista());
		aux.setEstadoCliente(auxUtil.getEstadoClienteActivo());
		aux.setCategoriaCliente(auxUtil.getCategoriaClienteAldia());
		aux.getEmpresa().setPais(auxUtil.getPaisParaguay());
		aux.getEmpresa().setEmpresaGrupoSociedad(auxUtil.getGrupoEmpresaNoDefinido());
		aux.getEmpresa().setRegimenTributario(auxUtil.getRegimenTributarioNoExenta());
		
		//Valores por default de CtaCte para un nuevo cliente.
		aux.getEmpresa().getCtaCteEmpresa().setEstadoComoCliente(this.getDtoUtil().getCtaCteEmpresaEstadoSinCuenta());
		aux.getEmpresa().getCtaCteEmpresa().setLineaCredito(this.getDtoUtil().getCtaCteLineaCreditoDefault());
		aux.getEmpresa().getCtaCteEmpresa().setCondicionPagoCliente(this.getDtoUtil().getCondicionPagoContado());
		
		this.setSelectedSucursal(null);
		this.setSelectedContacto(null);
		return aux;
	}

	@Override
	public String getEntidadPrincipal() {
		return Cliente.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}

	@Override
	public Assembler getAss() {
		return new AssemblerCliente();
	}
	
	@Override
	public Browser getBrowser(){
		return new ClienteBrowser();
	}
	
	@Override
	public void afterSave() {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			Empresa emp = rr.getEmpresaById(this.dto.getIdEmpresa());
			emp.setDireccion_(emp.getDireccion());
			emp.setTelefono_(emp.getTelefono());
			emp.setCorreo_(emp.getCorreo());
			rr.saveObject(emp, this.getLoginNombre());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	//=============== Contacto interno ================================

	ContactoInternoDTO selectedContactoInterno = null;

	public ContactoInternoDTO getSelectedContactoInterno() {
		return selectedContactoInterno;
	}

	public void setSelectedContactoInterno(
			ContactoInternoDTO selectedContactoInterno) {
		this.selectedContactoInterno = selectedContactoInterno;
	}
	
	@Command()
	@NotifyChange("*")
	public void eliminarContactoInterno() {	
		
		if (this.selectedContactoInterno != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el contacto interno?")) {
				this.dto.getContactosInternos().remove(this.selectedContactoInterno);
			}
			this.setSelectedContactoInterno(null);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command()
	@NotifyChange("*")
	public void agregarContactoInterno() throws Exception{

		if (mensajeAgregar("Agregar un contacto interno?")) {
			
			// buscar los funcionarios
			List<DTO> lFun = this.getAllDTOs(Funcionario.class.getName(), new AssemblerFuncionario());
			Listbox lbFun = new Listbox();
			lbFun.setModel(new ListModelList(lFun));
			lbFun.setRows(10);
			
			// buscar los tipos de contacto interno
			UtilDTO uDto = (UtilDTO)this.getDtoUtil();
			Combobox lbTipo = new Combobox();
			lbTipo.setModel(new ListModelList(uDto.getTipoContactoInterno()));
			
			// armar la ventana			
			BodyPopupAceptarCancelar bAC = new BodyPopupAceptarCancelar();
			
			bAC.addComponente("Funcionarios:",lbFun);
			bAC.addComponente("Tipo de contacto:",lbTipo);
			bAC.setHighWindows("470px");
			
			bAC.showPopup("Agregar contacto interno");
			
			if (bAC.isClickAceptar() == true){
				// hizo click en aceptar
				
				// recuperar los objetos seleccionados y  verifica que no sean nulos
				Listitem seleFun = lbFun.getSelectedItem();
				Comboitem seleTipo = lbTipo.getSelectedItem();
				if ((seleFun != null)&&(seleTipo!=null)){
					
					FuncionarioDTO fun = (FuncionarioDTO)seleFun.getValue();
					MyPair tipo = (MyPair)seleTipo.getValue();

					
					MyPair f = new MyPair();
					f.setId(fun.getId());
					f.setText(fun.getNombre());
					
					ContactoInternoDTO ci = new ContactoInternoDTO();
					ci.setFuncionario(f);
					ci.setTipoContactoInterno(tipo);
					
					this.dto.getContactosInternos().add(ci);
				}else{
					this.mensajeError("Error al seleccionar el funcionario o el tipo de contacto.");
				}
				
			}
			
			
		}
	}	

	@Command
	public void ventana() throws Exception{
		System.out.println("Ventana");
		com.coreweb.componente.WindowPopup w = new com.coreweb.componente.WindowPopup();
		w.setDato("siiii");
		w.show("/yhaguy/prueba/Prueba.zul");
		
	}
	
	private String mensajeError = "";
	
	@Override
	public boolean verificarAlGrabar() {		
		return this.validarFormulario();
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensajeError;
	}
	
	private boolean validarFormulario() {
		return true;
	}
	
	/**
	 * GETS / SETS
	 */
	
	public boolean isConsultaCtaCteDisabled() throws Exception{
		if (this.operacionHabilitada("ConsultarCtaCteClientesABM", ID.F_CLIENTE_ABM_BODY))
			return false;
		return true;
	}
	
	public boolean isCtaCteVisible() throws Exception{
		return !this.isConsultaCtaCteDisabled();
		
	}
	
	public boolean isCtaCteDisabled() throws Exception{
		if (this.operacionHabilitada("EditarCtaCteClientesABM", ID.F_CLIENTE_ABM_BODY))
			return false;
		return true;
		
	}
	
	/**
	 * @return las listas de precio..
	 * pos1:descripcion
	 * pos2:margen
	 */
	public List<MyArray> getListasDePrecio() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloListaPrecio> precios = rr.getListasDePrecio();
		List<MyArray> out = new ArrayList<MyArray>();
		for (ArticuloListaPrecio precio : precios) {
			MyArray mprecio = new MyArray(precio.getDescripcion(), precio.getMargen(), precio.getFormula());
			mprecio.setId(precio.getId());
			out.add(mprecio);
		}
		return out;
	}
	
	/**
	 * @return los cobradores..
	 */
	public List<MyArray> getCobradores() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Funcionario func : rr.getFuncionarios()) {
			MyArray my = new MyArray(func.getRazonSocial());
			my.setId(func.getId());
			out.add(my);
		}
		return out;
	}
}
