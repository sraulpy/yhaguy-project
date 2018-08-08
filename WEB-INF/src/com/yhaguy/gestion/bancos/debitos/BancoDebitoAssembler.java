package com.yhaguy.gestion.bancos.debitos;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.BancoDebito;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoCtaCte;

public class BancoDebitoAssembler extends Assembler {

	static final String[] attIguales = { "fecha", "numero", "descripcion",
			"importe", "confirmado" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		BancoDebito domain = (BancoDebito) this.getDomain(dto, BancoDebito.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.hijoDtoToHijoDomain(dto, domain, "cuenta", new AssemblerBancoCtaCte(), false);
		this.myPairToDomain(dto, domain, "sucursal");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		BancoDebitoDTO dto = (BancoDebitoDTO) this.getDTO(domain, BancoDebitoDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.hijoDomainToHijoDTO(domain, dto, "cuenta", new AssemblerBancoCtaCte());
		this.domainToMyPair(domain, dto, "sucursal");

		return dto;
	}

}
