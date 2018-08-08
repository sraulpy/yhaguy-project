package com.yhaguy.gestion.empresa.ctacte;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.CtaCteImputacion;

public class AssemblerCtaCteImputacion extends Assembler {
	
	private String[] attIgualesImputacion = { "quienImputa", "dondeImputa",
			"montoImputado", "tipoCambio"};

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		CtaCteImputacion domain = new CtaCteImputacion();
		this.copiarValoresAtributos(dto, domain, attIgualesImputacion);
		this.myPairToDomain(dto, domain, "tipoImputacion");
		this.myPairToDomain(dto, domain, "moneda");
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		CtaCteImputacionDTO dto = (CtaCteImputacionDTO) getDTO(
				domain, CtaCteImputacionDTO.class);
		this.copiarValoresAtributos(domain, dto, attIgualesImputacion);
		this.domainToMyPair(domain, dto, "tipoImputacion");
		this.domainToMyPair(domain, dto, "moneda");
		return dto;
	}

}
