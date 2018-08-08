package com.yhaguy.gestion.empresa;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Init;

import com.coreweb.Config;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;


public class ProveedorControlBody extends EmpresaControlBody {

	ProveedorDTO dto = new ProveedorDTO();
	

	@Init(superclass = true)
	public void initProveeddorControlBody() {
	}

	@AfterCompose(superclass = true)
	public void afterComposeProveedorControlBody() {
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
		
		//Valores por defecto para CtaCteProveedor
		aux.getEmpresa().getCtaCteEmpresa().setEstadoComoProveedor(this.utilDto.getCtaCteEmpresaEstadoSinCuenta());
		
		this.setSelectedSucursal(null);
		this.setSelectedContacto(null);
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
	
	
	/************************************* UTILES ****************************************/
	
	private UtilDTO utilDto = this.getDtoUtil();
	
	
	//============================= VALIDAR FORMULARIO ===================================
	
		private String mensajeError = "";
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

}
