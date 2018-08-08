package com.yhaguy.gestion.empresa.ctacte;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.CtaCteEmpresa;

public class AssemblerCtaCteEmpresa  extends Assembler{	
	
	private String[] attCtaCteEmpresa = {"fechaAperturaCuentaCliente", "fechaAperturaCuentaProveedor"};
	private String[] attLineaCredito = {"descripcion", "linea"};
	private String[] attCondicionPago = {"descripcion", "plazo" };

	
	@Override
	public Domain dtoToDomain(DTO dtoG) throws Exception {
		CtaCteEmpresaDTO dto = (CtaCteEmpresaDTO) dtoG;
		CtaCteEmpresa domain = (CtaCteEmpresa) this.getDomain(dto, CtaCteEmpresa.class);
		this.copiarValoresAtributos(dto, domain, attCtaCteEmpresa);
		this.myArrayToDomain(dto, domain, "lineaCredito");
		this.myArrayToDomain(dto, domain, "condicionPagoCliente");
		this.myPairToDomain(dto, domain, "estadoComoCliente");
		this.myPairToDomain(dto, domain, "estadoComoProveedor");	

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		CtaCteEmpresaDTO dto = (CtaCteEmpresaDTO) getDTO(domain, CtaCteEmpresaDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attCtaCteEmpresa);
		this.domainToMyArray(domain, dto, "lineaCredito", attLineaCredito);
		this.domainToMyArray(domain, dto, "condicionPagoCliente", attCondicionPago);
		this.domainToMyPair(domain, dto, "estadoComoCliente");
		this.domainToMyPair(domain, dto, "estadoComoProveedor");

		return dto;
	}
}
