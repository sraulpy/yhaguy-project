package com.yhaguy.gestion.comun;

import org.zkoss.bind.BindUtils;

import com.coreweb.Config;
import com.coreweb.control.Control;
import com.coreweb.domain.IiD;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.Check;
import com.coreweb.util.MyPair;
import com.coreweb.util.Ruc;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.empresa.AssemblerEmpresa;
import com.yhaguy.gestion.empresa.ClienteDTO;
import com.yhaguy.gestion.empresa.EmpresaDTO;
import com.yhaguy.gestion.empresa.ProveedorDTO;

public class ControlLogicaEmpresa extends Control {

	private UtilDTO utilDto = (UtilDTO) this.getDtoUtil();
	private Ruc utilRuc = new Ruc();
	
	private static final String CLIENTE = "CLIENTE";
	private static final String PROVEEDOR = "PROVEEDOR";

	public ControlLogicaEmpresa(Assembler ass) {
		super(ass);
	}
	
	

	/**
	 * Recibe una empresaDto y verifica el pais si es del extranjero setea el
	 * ruc:'99999901-0'
	 * @param empresa
	 */
	public void verificarPais(EmpresaDTO empresa) {

		boolean exterior = this.isPaisExterior(empresa.getPais());

		if (exterior == true) {
			empresa.setRuc(Configuracion.RUC_EMPRESA_EXTERIOR);
		}

		if ((exterior == false) && (empresa.esNuevo() == true)) {
			empresa.setRuc("");
		}
	}
	
	

	/**
	 * @param pais
	 * @return booleano que indica si es del exterior. true:es extranjero
	 */
	public boolean isPaisExterior(IiD pais) {
		return (pais.getId().compareTo(utilDto.getPaisParaguay().getId()) != 0);
	}

	
	
	/**
	 * Recibe como parametro un DTO que puede ser Cliente o Proveedor
	 * y de acuerdo al valor del ruc y la cedula invoca a los metodos
	 * verificarRuc() y verificarCedula() para validar las mismas..	
	 * @param dtoCorriente que puede ser un ClienteDTO o un ProveedorDTO
	 * @return booleano que indica si fue correcta la verificacion del ruc..
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public boolean buscarRUC(DTO dtoCorriente) throws Exception {		
		boolean out = false;
		
		ClienteDTO cli = null; 
		ProveedorDTO prov = null;
		EmpresaDTO empresa = null;
		
		Class clase = null;
		String claseDto = null;
		String tipo = null;
		
		if (dtoCorriente instanceof ClienteDTO) {
			cli = (ClienteDTO) dtoCorriente;
			empresa = cli.getEmpresa();
			clase = Cliente.class;
			claseDto = ClienteDTO.class.getName();
			tipo = CLIENTE;
		}
		
		if (dtoCorriente instanceof ProveedorDTO) {
			prov = (ProveedorDTO) dtoCorriente;
			empresa = prov.getEmpresa();
			clase = Proveedor.class;
			claseDto = ProveedorDTO.class.getName();
			tipo = PROVEEDOR;
		}
		

		// Si el ruc esta vacio asigna el ruc por defecto..
		if (empresa.getRuc().trim().length() == 0) {
			empresa.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		}

		String ruc = empresa.getRuc();
		String ci = empresa.getCi();
		

		if ((ruc.compareTo(Configuracion.RUC_EMPRESA_EXTERIOR) == 0)
				|| (ruc.compareTo(Configuracion.RUC_EMPRESA_LOCAL) == 0)) {
			
			out = this.verificarCedula(clase, tipo, ci, claseDto, empresa, dtoCorriente);
			
		} else {
			
			out = this.verificarRuc(clase, tipo, ruc, claseDto, empresa, dtoCorriente);
		}
		
		return out;
	}
	
	

	/**
	 * Realiza las siguientes verificaciones sobre el ruc:
	 *  -el formato y el digito verificador del ruc.
	 * 	-si ya existe un cliente/proveedor con el mismo ruc.
	 * 	-si ya existe una empresa con el mismo.
	 * 	-busca el ruc en la BD del SET.
	 */
	@SuppressWarnings("rawtypes")
	private boolean verificarRuc(Class clase, String tipo, String ruc,
			String claseDto, EmpresaDTO empresa, DTO dtoCorriente) throws Exception {

		boolean out = false;
		RegisterDomain rr = RegisterDomain.getInstance();
		
		// verifica el formato y el digito verificador del ruc.
		if (this.utilRuc.validarRuc(ruc) == false) {
			this.mensajeError(Check.MENSAJE_RUC);
			return false;
		}
		
		// ver si hay un cliente (proveedor) con este RUC
		if (rr.existe(clase, "empresa.ruc", Config.TIPO_STRING, ruc, empresa) == true) {
			this.mensajeError("Ya existe un " + tipo + " con este RUC \n" + ruc);
			out = false;
			
		} else {
			
			// ver si hay una empresa con este RUC, si si, asociarla
			Empresa emp = (Empresa) rr.getObject(Empresa.class.getName(), "ruc", ruc);
			if (emp != null) {
				
				AssemblerEmpresa assEmp = new AssemblerEmpresa();
				empresa = (EmpresaDTO) assEmp.domainToDto(emp);
				this.m.ejecutarMetoto(claseDto, "setEmpresa", dtoCorriente, empresa);
				
			} else {
				
				// buscar en la tabla del SET la razon social
				String rs = rr.getRazonSocialSET(ruc);
				this.m.ejecutarMetoto(claseDto, "setRazonSocial", dtoCorriente, rs);
				empresa.setNombre(rs);
				this.asignarTipoPersona(empresa);
				
				if (rs.trim().length() > 0) {
					empresa.setRazonSocialSet(true);
				}
			}
			out = true;
		}
		return out;
	}
	
	
	

	/**
	 * Realiza las sgtes verificaciones sobre la cedula:
	 *  -si ya existe un cliente/proveedor con la misma cedula.
	 *  -si ya existe una empresa con la misma cedula.
	 * @param clase
	 * @param tipo
	 * @param ci
	 * @param claseDto
	 * @param empresa
	 * @param dtoCorriente
	 * @return booleano que indica si la validacion fue correcta
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private boolean verificarCedula(Class clase, String tipo, String ci,
			String claseDto, EmpresaDTO empresa, DTO dtoCorriente) throws Exception {	
		
		boolean out = false;		
		RegisterDomain rr = RegisterDomain.getInstance();
		
		//verifica que la cedula no este vacia..
		if (ci.trim().length() == 0) {
			this.mensajeError("El valor de la Cédula no debe estar vacía..");
			return false;
		}
		
		// ver si hay un cliente (proveedor) con esta Cedula
		if (rr.existe(clase, "empresa.ci", Config.TIPO_STRING, ci, dtoCorriente) == true) {
			
			this.mensajeError("Ya existe un " + tipo + " con esta Cedula \n" + ci);
			out = false;
			
		} else {
			
			// ver si hay una empresa con este RUC, si si, asociarla
			Empresa emp = (Empresa) rr.getObject(Empresa.class.getName(), "ci", ci);
			if (emp != null) {
				
				AssemblerEmpresa assEmp = new AssemblerEmpresa();
				empresa = (EmpresaDTO) assEmp.domainToDto(emp);
				this.m.ejecutarMetoto(claseDto, "setEmpresa", dtoCorriente, empresa);
				
			} else {
				
				this.asignarTipoPersona(empresa);
				
			}
			out = true;
		}
		return out;
	}
	
	
	
	
	/**
	 * Asigna el tipo de persona de acuerdo al valor del ruc de la empresa.
	 * @param empresa
	 */
	public void asignarTipoPersona(EmpresaDTO empresa){	
		
		String ruc = empresa.getRuc();
		boolean isJuridica = m.esPersonaJuridica(ruc);
		
		MyPair juridica = utilDto.getTipoPersonaJuridica();
		MyPair fisica = utilDto.getTipoPersonaFisica();
		
		if (isJuridica == true) {
			empresa.setTipoPersona(juridica);
		} else {
			empresa.setTipoPersona(fisica);
		}
		
		BindUtils.postNotifyChange(null, null, empresa, "tipoPersona");
	}

}
