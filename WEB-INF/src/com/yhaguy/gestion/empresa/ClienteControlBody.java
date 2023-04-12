package com.yhaguy.gestion.empresa;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Popup;

import com.coreweb.componente.BodyPopupAceptarCancelar;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.EmpresaCartera;
import com.yhaguy.domain.EmpresaRubro;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Identificaciones;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.RucSet;
import com.yhaguy.util.Utiles;

public class ClienteControlBody extends EmpresaControlBody {

	private ClienteDTO dto;
	
	private String filterCedula;
	private String filterNombres;
	private String filterApellidos;
	
	private String filterRuc;
	private String filterRazonSocial;
	
	private Identificaciones selectedIdentificaciones;
	private RucSet selectedRucSet;

	@Init(superclass = true)
	public void init() {
		this.filterCedula = "";
		this.filterNombres = "";
		this.filterApellidos = "";
		this.filterRuc = "";
		this.filterRazonSocial = "";
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
		Clients.evalJavaScript("setImage('" + this.dto.getUrlImagen() + "')");
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
		aux.getEmpresa().setRubro(this.getRubroConsumidorFinal());
		
		//Valores por default de CtaCte para un nuevo cliente.
		aux.getEmpresa().getCtaCteEmpresa().setEstadoComoCliente(this.getDtoUtil().getCtaCteEmpresaEstadoSinCuenta());
		aux.getEmpresa().getCtaCteEmpresa().setLineaCredito(this.getDtoUtil().getCtaCteLineaCreditoDefault());
		aux.getEmpresa().getCtaCteEmpresa().setCondicionPagoCliente(this.getDtoUtil().getCondicionPagoContado());
		
		this.setSelectedSucursal(null);
		this.setSelectedContacto(null);
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			aux.getEmpresa().setCartera((EmpresaCartera) rr.getObject(EmpresaCartera.class.getName(), 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	@Command
	@NotifyChange("*")
	public void selectIdentificaciones(@BindingParam("comp") Popup comp) throws Exception {
		String ci = this.selectedIdentificaciones.getPer_nrodocumento();
		String ruc = "";
		
		this.dto.getEmpresa().setRazonSocial("");
		this.dto.getEmpresa().setCi("");
		this.dto.getEmpresa().setRuc("");
		this.dto.getEmpresa().setNombre("");
		this.dto.getEmpresa().setFechaAniversario(null);
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<RucSet> rucs = rr.getRucSet(ci, "");
		if (rucs.size() > 0) {
			String ruc_ = rucs.get(0).getRuc();
			if (ruc_.split("-")[0].length() == ci.length()) {
				ruc = ruc_;
			} else {
				ruc = Configuracion.RUC_EMPRESA_LOCAL;
			}
		} else {
			ruc = Configuracion.RUC_EMPRESA_LOCAL;
		}
		
		if (!this.validarNumeroDocumento(ruc, ci)) {
			comp.close();
			return;
		}
		
		this.dto.getEmpresa().setRazonSocial(this.selectedIdentificaciones.getPer_nombres() + " " + this.selectedIdentificaciones.getPer_apellidos());
		this.dto.getEmpresa().setCi(ci);
		this.dto.getEmpresa().setRuc(ruc);
		this.dto.getEmpresa().setNombre(this.selectedIdentificaciones.getPer_nombres() + " " + this.selectedIdentificaciones.getPer_apellidos());
		this.dto.getEmpresa().setFechaAniversario(Utiles.getFecha(this.selectedIdentificaciones.getPer_fecha_nac(), "yyyy-MM-dd hh:mm:ss"));
		
		comp.close();
	}
	
	@Command
	@NotifyChange("*")
	public void selectRucSet(@BindingParam("comp") Popup comp) {
		String ruc = this.selectedRucSet.getRuc();
		
		this.dto.getEmpresa().setRazonSocial("");
		this.dto.getEmpresa().setCi("");
		this.dto.getEmpresa().setRuc("");
		this.dto.getEmpresa().setNombre("");
		this.dto.getEmpresa().setFechaAniversario(null);
		
		if (!this.validarNumeroDocumento(ruc, "")) {
			comp.close();
			return;
		}
		
		this.dto.getEmpresa().setRazonSocial(this.selectedRucSet.getRazonSocial());
		this.dto.getEmpresa().setCi("");
		this.dto.getEmpresa().setRuc(ruc);
		this.dto.getEmpresa().setNombre(this.selectedRucSet.getRazonSocial());
		
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			String ruc_ = ruc.split("-")[0];
			List<Identificaciones> ident = rr.getIdentificaciones(ruc_, "", "");
			if (ident.size() > 0) {
				String ci = ident.get(0).getPer_nrodocumento();
				if (ci.equals(ruc_)) {
					this.dto.getEmpresa().setCi(ci);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		comp.close();
	}
	
	/**
	 * @return validacion del nro documento..
	 */
	private boolean validarNumeroDocumento(String ruc, String ci) {
		try {		
			RegisterDomain rr = RegisterDomain.getInstance();
			
			if (!ruc.equals(Configuracion.RUC_EMPRESA_LOCAL)) {
				Cliente cli = rr.getClienteByRuc(ruc);
				if (cli != null) {
					Clients.showNotification("YA EXISTE UN CLIENTE CON EL RUC: " + ruc, Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return false;
				}
			} else {
				Cliente cli = rr.getClienteByCedula(ci);
				if (cli != null) {
					Clients.showNotification("YA EXISTE UN CLIENTE CON EL NRO. DE CÉDULA: " + ci, Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return false;
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
		return true;
	}
	
	@DependsOn({ "filterCedula", "filterNombres", "filterApellidos" })
	public List<Identificaciones> getIdentificaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();		
		return rr.getIdentificaciones(this.filterCedula, this.filterNombres, this.filterApellidos);
	}
	
	@DependsOn({ "filterRuc", "filterRazonSocial" })
	public List<RucSet> getRucSet() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();		
		return rr.getRucSet(this.filterRuc, this.filterRazonSocial);
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
		this.mensajeError = "";
		if (this.getDtoEmp().getRazonSocial().trim().isEmpty()) {
			this.mensajeError += " Razon Social no puede quedar en blanco..";
			return false;
		}
		if (this.getDtoEmp().getCiudad() == null) {
			this.mensajeError += " Debe asignar la ciudad..";
			return false;
		}
		if (this.getDtoEmp().getVendedor() == null) {
			this.mensajeError += " Debe asignar un vendedor..";
			return false;
		}
		if (this.getDtoEmp().getRuc() == null || this.getDtoEmp().getRuc().trim().isEmpty()) {
			this.mensajeError += " Debe ingresar el ruc..";
			return false;
		}
		if (this.getDtoEmp().getRubro() == null || this.getDtoEmp().getRubro().esNuevo()) {
			this.mensajeError += " Debe asignar un rubro..";
			return false;
		}
		if (this.getDtoEmp().getDireccion_() == null || this.getDtoEmp().getDireccion_().trim().isEmpty()) {
			this.mensajeError += " Debe ingresar la dirección..";
			return false;
		}
		return true;
	}
	
	/**
	 * GETS / SETS
	 */
	
	private MyArray getRubroConsumidorFinal() {
		MyArray out = new MyArray();		
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			EmpresaRubro rubro = rr.getRubro(EmpresaRubro.CONSUMIDOR_FINAL);
			out.setId(rubro.getId());
			out.setPos1(rubro.getDescripcion());
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;
	}
	
	public boolean isConsultaCtaCteDisabled() throws Exception{
		if (this.operacionHabilitada("ConsultarCtaCteClientesABM", ID.F_CLIENTE_ABM_BODY))
			return false;
		return true;
	}
	
	/**
	 * @return los estados
	 */
	public List<String> getEstados() {
		List<String> out = new ArrayList<String>();
		out.add(Cliente.ACTIVO);
		out.add(Cliente.INACTIVO);
		return out;
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
		for (Funcionario func : rr.getTeleCobradores()) {
			MyArray my = new MyArray(func.getRazonSocial());
			my.setId(func.getId());
			out.add(my);
		}
		return out;
	}

	public String getFilterCedula() {
		return filterCedula;
	}

	public void setFilterCedula(String filterCedula) {
		this.filterCedula = filterCedula;
	}

	public String getFilterNombres() {
		return filterNombres;
	}

	public void setFilterNombres(String filterNombres) {
		this.filterNombres = filterNombres;
	}

	public String getFilterApellidos() {
		return filterApellidos;
	}

	public void setFilterApellidos(String filterApellidos) {
		this.filterApellidos = filterApellidos;
	}

	public Identificaciones getSelectedIdentificaciones() {
		return selectedIdentificaciones;
	}

	public void setSelectedIdentificaciones(Identificaciones selectedIdentificaciones) {
		this.selectedIdentificaciones = selectedIdentificaciones;
	}

	public String getFilterRuc() {
		return filterRuc;
	}

	public void setFilterRuc(String filterRuc) {
		this.filterRuc = filterRuc;
	}

	public String getFilterRazonSocial() {
		return filterRazonSocial;
	}

	public void setFilterRazonSocial(String filterRazonSocial) {
		this.filterRazonSocial = filterRazonSocial;
	}

	public RucSet getSelectedRucSet() {
		return selectedRucSet;
	}

	public void setSelectedRucSet(RucSet selectedRucSet) {
		this.selectedRucSet = selectedRucSet;
	}
}
