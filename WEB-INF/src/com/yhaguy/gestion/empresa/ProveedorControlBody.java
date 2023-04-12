package com.yhaguy.gestion.empresa;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.Config;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.EmpresaCartera;
import com.yhaguy.domain.EmpresaRubro;
import com.yhaguy.domain.Identificaciones;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.RucSet;
import com.yhaguy.util.Utiles;


public class ProveedorControlBody extends EmpresaControlBody {

	ProveedorDTO dto;
	
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

	public String textoCabecera() {
		String nombre = this.dto.getNombre();
		if (nombre.trim().length() < 1){
			nombre = "-- seleccione una empresa --";
		}
		String out = "";
		out += "" + nombre.toUpperCase();
		return out;
	}

	public ProveedorDTO getDto() {
		return dto;
	}

	public void setDto(ProveedorDTO dto) {
		this.dto = dto;
		this.setDtoEmp(this.dto.getEmpresa());
	}


	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (ProveedorDTO) dto;
		this.setDtoEmp(this.dto.getEmpresa());
	}

	@Override
	public DTO nuevoDTO() {
		ProveedorDTO aux = new ProveedorDTO();
		aux.setTipoProveedor(this.utilDto.getProveedorTipoLocal());
		aux.setEstadoProveedor(this.utilDto.getProveedorEstadoActivo());
		aux.setCuentaContable(this.utilDto.getCuentaClientesOcasionales());
		aux.getEmpresa().setPais(this.utilDto.getPaisParaguay());
		aux.getEmpresa().setEmpresaGrupoSociedad(this.utilDto.getGrupoEmpresaNoDefinido());
		aux.getEmpresa().setRegimenTributario(this.utilDto.getRegimenTributarioNoExenta());	
		aux.getEmpresa().setRubro(this.getRubroConsumidorFinal());
		
		//Valores por defecto para CtaCteProveedor
		aux.getEmpresa().getCtaCteEmpresa().setEstadoComoProveedor(this.utilDto.getCtaCteEmpresaEstadoSinCuenta());
		
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
		return Proveedor.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}

	@Override
	public Assembler getAss() {
		// TODO Auto-generated method stub
		return new AssemblerProveedor();
	}
	
	public Browser getBrowser(){
		return new ProveedorBrowser();
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
				Proveedor prov = rr.getProveedorByRuc(ruc);
				if (prov != null) {
					Clients.showNotification("YA EXISTE UN PROVEEDOR CON EL RUC: " + ruc, Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
					return false;
				}
			} else {
				Proveedor prov = rr.getProveedorByCI(ci);
				if (prov != null) {
					Clients.showNotification("YA EXISTE UN PROVEEDOR CON EL NRO. DE CÉDULA: " + ci, Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
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
	
	/**
	 * @return el rubro consumidor final
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
	
	/************************************* UTILES ****************************************/
	
	private UtilDTO utilDto = this.getDtoUtil();
	
	
	//============================= VALIDAR FORMULARIO ===================================
	
		private String mensajeError = "";
		@SuppressWarnings("unused")
		private boolean controlRuc = true;
		
		@Override
		public boolean verificarAlGrabar() {
			return this.validarFormulario();
		}

		@Override
		public String textoErrorVerificarGrabar() {
			return this.mensajeError;
		}


		
		private boolean validarFormulario(){
			boolean out = true;
			
			out = this.validarFormularioCompleto();
			
			// control de RUC
			
			
			if (out == false){
				
				this.mensajeError(this.mensajeError);
				
				if (this.mensajeSiNo("La informacion esta incompleta/incorrecta, \n Desea grabar lo mismo? ")==true){
					this.dto.setCompleto(false);
					out = true;
				}else{
					out = false;
				}
				
			}
			return out;
		}

		
		private boolean validarFormularioCompleto(){
			boolean out = true;
			
			this.mensajeError = "No se puede realizar la operación debido a: \n";
			EmpresaDTO emp = this.getDtoEmp();
			String ruc = emp.getRuc();
			MyPair perFis = this.getDtoUtil().getTipoPersonaFisica();
			RegisterDomain rr = RegisterDomain.getInstance();
			
			try {
				
				if (emp.getPais() == null) {
					out = false;
					this.mensajeError += "\n - Debe asignar un País..";
				}
				
				if (emp.getRazonSocial().trim().length() == 0) {
					out = false;
					this.mensajeError += "\n - La Razón Social no puede quedar vacía..";
				}
				
				if (emp.getNombre().trim().length() == 0) {
					out = false;
					this.mensajeError += "\n - El Nombre Fantasía no puede quedar vacío..";
				}
				
				if (emp.getTipoPersona() == null) {
					out = false;
					this.mensajeError += "\n - Debe asignar el Tipo de Persona..";
				}			
				
				if (emp.getRegimenTributario() == null) {
					out = false;
					this.mensajeError += "\n - Debe asignar el Régimen Tributario..";
				}
				
				if (emp.getFechaIngreso() == null) {
					out = false;
					this.mensajeError += "\n - La fecha de Ingreso no puede quedar vacía..";
				}
				
				if ((emp.getFechaIngreso()!= null) && (emp.getFechaIngreso().compareTo(new Date()) == 1)) {
					out = false;
					this.mensajeError += "\n - La fecha de Ingreso no puede ser mayor a la actual..";
				}
				
				if (emp.getFechaAniversario() == null) {
					out = false;
					this.mensajeError += "\n - La fecha de Aniversario no puede quedar vacía..";
				}
				
				if (emp.getRubroEmpresas().size() == 0) {
					out = false;
					this.mensajeError += "\n - Debe asignar al menos un rubro..";
				}
				
				if (emp.getMonedas().size() == 0) {
					out = false;
					this.mensajeError += "\n - Debe asignar al menos una moneda..";
				}
				
				if (emp.getMoneda() == null) {
					out = false;
					this.mensajeError += "\n - Debe asignar la moneda por defecto..";
				}
				
				if ((emp.getSucursales().size() > 0) && (this.validarSucursal(emp.getSucursales()) == false)) {
					out = false;
					this.mensajeError += "\n - Verifique los campos obligatorios de las Sucursales..";
				}
				
				if ((emp.getContactos().size() > 0) && (this.validarContacto(emp.getContactos()) == false)) {
					out = false;
					this.mensajeError += "\n - Verifique los campos obligatorios de los Contactos..";
				}
				
				if (this.dto.getTipoProveedor() == null) {
					out = false;
					this.mensajeError += "\n - Debe asignar el Tipo de Proveedor..";
				}
				
				if (this.dto.getEstadoProveedor() == null) {
					out = false;
					this.mensajeError += "\n - Debe asignar el Estado del Proveedor..";
				}
				
				if (this.dto.getCuentaContable() == null) {
					out = false;
					this.mensajeError += "\n - Debe asignar la Cuenta Contable..";
				}
				
				if ((emp.getTipoPersona().compareTo(perFis) == 0)
						&& (emp.getCi().length() == 0)) {
					out = false;
					this.mensajeError += "\n - Debe ingresar la Cédula del Proveedor..";
				} 	
				
				if (emp.getSigla().trim().length() > 3) {
					out = false;
					this.mensajeError += "\n - La longitud de la Sigla no debe ser mayor a 3..";
				}
				
				//Valida el ruc..
				if (rr.existe(Proveedor.class, "empresa.ruc", Config.TIPO_STRING , ruc, this.getDTOCorriente()) == true){
					out = false;
					this.mensajeError = this.mensajeError + "\n - Ya existe un cliente con este RUC: " + ruc;
					this.controlRuc = false;
				}
				
				if(emp.getCtaCteEmpresa().getEstadoComoProveedor() == null ||  emp.getCtaCteEmpresa().getEstadoComoProveedor().esNuevo()){
					out = false;
					this.mensajeError += "\n - Debe seleccionar un estado para la Cta. Cte.";
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				out = false;
			}
			
			return out;
		}
		
		
		// Valida los campos obligatorios de cada Sucursal y el formato del correo..	
		private boolean validarSucursal(List<MyArray> sucursales){
			boolean out = true;
			
			for (MyArray suc : sucursales) {
				
				String nombre = suc.getPos1() + "";
				String direccion = suc.getPos2() + "";
				String correo = suc.getPos4() + "";
				MyPair zona = (MyPair) suc.getPos5();
				MyPair localidad = (MyPair) suc.getPos6();
				
				if (zona == null) {
					zona = new MyPair();
				}
				
				if (localidad == null) {
					localidad = new MyPair();
				}
				
				if ((nombre.length() == 0)
						|| (direccion.length() == 0)
							|| ((zona.esNuevo() == true))
								|| (localidad.esNuevo() == true)
									|| (m.checkEmail(correo) == false)) {
					out = false;
				}
			}
			
			return out;
		}
		
		
		// Valida los campos obligatorios de cada Contacto..
		private boolean validarContacto(List<ContactoDTO> contactos){
			boolean out = true;
			
			for (ContactoDTO cont : contactos) {
				if ((cont.getNombre().trim().length() == 0)
						|| (cont.getSucursal().esNuevo() == true)
							|| (cont.getCargo().trim().length() == 0)) {
					out = false;
				}
			}
			
			return out;
		}
		public boolean isConsultaCtaCteDisabled() throws Exception{
			if (this.operacionHabilitada("ConsultarCtaCteProveedoresABM", ID.F_PROVEEDOR_ABM_BODY))
				return false;
			return true;
		}
		
		public boolean isCtaCteVisible() throws Exception{
			return !this.isConsultaCtaCteDisabled();
			
		}
		
		public boolean isCtaCteDisabled() throws Exception{
			if (this.operacionHabilitada("EditarCtaCteProveedoresABM", ID.F_PROVEEDOR_ABM_BODY))
				return false;
			return true;
			
		}

		public Identificaciones getSelectedIdentificaciones() {
			return selectedIdentificaciones;
		}

		public void setSelectedIdentificaciones(Identificaciones selectedIdentificaciones) {
			this.selectedIdentificaciones = selectedIdentificaciones;
		}

		public RucSet getSelectedRucSet() {
			return selectedRucSet;
		}

		public void setSelectedRucSet(RucSet selectedRucSet) {
			this.selectedRucSet = selectedRucSet;
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

}
