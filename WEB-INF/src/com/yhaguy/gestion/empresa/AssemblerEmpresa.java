package com.yhaguy.gestion.empresa;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Register;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Contacto;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Sucursal;
import com.yhaguy.gestion.empresa.ctacte.AssemblerCtaCteEmpresa;

public class AssemblerEmpresa extends Assembler {

	private static String[] attIgualesEmpresa = { "nombre", "codigoEmpresa",
			"razonSocial", "ruc", "ci", "observacion", "fechaIngreso",
			"fechaAniversario", "web", "sigla", "cuentaBloqueada", "latitud",
			"longitud", "userlocation", "direccion_", "telefono_", "correo_" };

	public static String[] attSucursal = { "nombre", "direccion", "telefono",
			"correo", "zona", "localidad", "auxi" };
	
	static String[] attRubro = { "descripcion" };

	@Override
	public Domain dtoToDomain(DTO dtoG) throws Exception {

		EmpresaDTO dto = (EmpresaDTO) dtoG;
		Empresa domain = (Empresa) getDomain(dto, Empresa.class);

		this.copiarValoresAtributos(dto, domain, attIgualesEmpresa);
		this.myPairToDomain(dto, domain, "empresaGrupoSociedad");
		this.myPairToDomain(dto, domain, "pais");
		this.myPairToDomain(dto, domain, "tipoPersona");
		this.myPairToDomain(dto, domain, "regimenTributario");
		this.myPairToDomain(dto, domain, "moneda");
		this.myArrayToDomain(dto, domain, "rubro");

		this.listaMyPairToListaDomain(dto, domain, "monedas");
		this.listaMyPairToListaDomain(dto, domain, "rubroEmpresas");

		this.listaMyArrayToListaDomain(dto, domain, "sucursales", attSucursal,
				true, true);
		this.listaDTOToListaDomain(dto, domain, "contactos", true, true,
				new AssemblerContacto());

		// Contacto - Sucursal (ver una mejor forma de hacer esto)
		this.dtoToDomainContactosSucursal(dto.getContactos(),
				domain.getContactos(), domain.getSucursales());
		
		verificarFechaAperturaCtaCte(dto);
		this.hijoDtoToHijoDomain(dto, domain, "ctaCteEmpresa", new AssemblerCtaCteEmpresa(), true);
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain dom) throws Exception {

		Empresa domain = (Empresa) dom;

		EmpresaDTO dto = (EmpresaDTO) getDTO(domain, EmpresaDTO.class);

		this.copiarValoresAtributos(domain, dto, attIgualesEmpresa);

		this.domainToMyPair(domain, dto, "empresaGrupoSociedad");
		this.domainToMyPair(domain, dto, "regimenTributario");
		this.domainToMyArray(domain, dto, "rubro", attRubro);

		dto.setTipoPersona(this.tipoToMyPair(domain.getTipoPersona()));
		dto.setMonedaConSimbolo(this.tipoToMyArray(domain.getMoneda()));
		if (dto.getMonedaConSimbolo() != null) {
			dto.setMoneda(new MyPair(dto.getMonedaConSimbolo().getId(),
					(String) dto.getMonedaConSimbolo().getPos1()));
		}
		dto.setPais(this.tipoToMyPair(domain.getPais()));

		this.listaDomainToListaMyPair(domain, dto, "monedas");
		this.listaDomainToListaMyPair(domain, dto, "rubroEmpresas");
		this.listaDomainToListaMyArray(domain, dto, "sucursales", attSucursal);
		this.listaDomainToListaDTO(domain, dto, "contactos", new AssemblerContacto());

		
		this.hijoDomainToHijoDTO(domain, dto, "ctaCteEmpresa", new AssemblerCtaCteEmpresa());

		return dto;
	}

	/*
	 * Hace la relacion contacto sucursal, pero hay que hacerlo mucho mejor :(
	 * Hay que hacerlo así porque la primera vez es posible que no tenga el ID
	 * para buscarlo en la BD y asociarlo. Sólo es necesario de DtoToDomain,
	 * porque al revés si hay Id.
	 */

	private void dtoToDomainContactosSucursal(List<ContactoDTO> lConDTO,
			Set<Contacto> lConDom, Set<Sucursal> lSucDom) throws Exception {

		Register rr = Register.getInstance();

		// recorre los contactos DTO
		for (int i = 0; i < lConDTO.size(); i++) {
			ContactoDTO conDTO = lConDTO.get(i);
			String cedula = conDTO.getCedula();
			String cargo = conDTO.getCargo();
			String nombre = conDTO.getNombre();
			// buscar cual es el domain que le corresponde
			// No se puede hacer por ID porque la primera ves es 0
			Iterator<Contacto> iteCon = lConDom.iterator();
			while (iteCon.hasNext()) {
				Contacto con = iteCon.next();
					if ((cedula.compareTo(con.getCedula()) == 0)
							&& (nombre.compareTo(con.getNombre()) == 0)
							&& (cargo.compareTo(con.getCargo()) == 0)) {
						// ahora buscar la sucursal
						String nombreSuc = (String) conDTO.getSucursal()
								.getPos1();
						Iterator<Sucursal> iteSuc = lSucDom.iterator();
						while (iteSuc.hasNext()) {
							Sucursal suc = iteSuc.next();
							if (nombreSuc.compareTo(suc.getNombre()) == 0) {
								con.setSucursal(suc);
								rr.saveObject(con, this.getLogin());
								System.out
										.println("*** Contacto:"
												+ con.getNombre() + " - "
												// +
												// con.getProfesion().getDescripcion()
												+ " - "
												+ con.getSucursal().getNombre());
							}
						}

					}
			}

		}

	}

	/**
	 * Si los estados de la cuenta de la empresa pasan de "Sin Cta. Cte."
	 * a cualquier otro estado referente a la Cta Cte por primera vez
	 * se que la fecha de esa operacion es la fecha de apertura de la cuenta.
	 * @param dto
	 */
	private void verificarFechaAperturaCtaCte(DTO dto){
		
		EmpresaDTO emp = (EmpresaDTO) dto;
		MyPair estadoComoCliente   = emp.getCtaCteEmpresa().getEstadoComoCliente();
		MyPair estadoComoProveedor = emp.getCtaCteEmpresa().getEstadoComoProveedor();
		
		if (estadoComoCliente != null) {
			if (estadoComoCliente.getSigla().compareTo(
					Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA) != 0
					&& emp.getCtaCteEmpresa().getFechaAperturaCuentaCliente() == null) {
				emp.getCtaCteEmpresa()
						.setFechaAperturaCuentaCliente(new Date());
			}
		}
		
		if (estadoComoProveedor != null) {
			if (estadoComoProveedor.getSigla().compareTo(
					Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA) != 0
					&& emp.getCtaCteEmpresa().getFechaAperturaCuentaProveedor() == null) {
				emp.getCtaCteEmpresa()
						.setFechaAperturaCuentaProveedor(new Date());
			}
		}
	}

}

// ***************************************************************************
// ***************************************************************************
// ***************************************************************************

class AssemblerContacto extends Assembler {

	private static String[] attIgualesContacto = { "cargo", "nombre",
			"telefono", "correo", "fechaCumpleanhos", "cedula" };

	@Override
	public Domain dtoToDomain(DTO dtoG) throws Exception {

		ContactoDTO dto = (ContactoDTO) dtoG;
		Contacto domain = (Contacto) getDomain(dto, Contacto.class);

		this.copiarValoresAtributos(dto, domain, attIgualesContacto);
		this.myPairToDomain(dto, domain, "profesion");
		this.myPairToDomain(dto, domain, "contactoSexo");
		this.myPairToDomain(dto, domain, "estadoCivil");
		// NOTA: La relación con Sucursal se hace en AsemblerEmpresa

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {

		ContactoDTO dto = (ContactoDTO) getDTO(domain, ContactoDTO.class);

		this.copiarValoresAtributos(domain, dto, attIgualesContacto);

		this.domainToMyPair(domain, dto, "profesion", "descripcion");
		this.domainToMyPair(domain, dto, "contactoSexo", "descripcion");
		this.domainToMyPair(domain, dto, "estadoCivil", "descripcion");

		// NOTA: acá si se puede porque la relación ya existe
		this.domainToMyArray(domain, dto, "sucursal",
				AssemblerEmpresa.attSucursal);
		return dto;
	}

}
