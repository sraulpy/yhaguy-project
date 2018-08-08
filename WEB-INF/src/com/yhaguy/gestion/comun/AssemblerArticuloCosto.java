package com.yhaguy.gestion.comun;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.ArticuloCosto;

public class AssemblerArticuloCosto extends Assembler {
	
	private static String[] attIgualesArticuloCosto = { "idMovimiento",
		"fechaCompra", "costoFinalGs", "costoFinalDs" };

	@Override
	public Domain dtoToDomain(DTO dtoP) throws Exception {
		ArticuloCostoDTO dto = (ArticuloCostoDTO) dtoP;
		ArticuloCosto domain = (ArticuloCosto) getDomain(dto,
				ArticuloCosto.class);
		this.copiarValoresAtributos(dto, domain, attIgualesArticuloCosto);
		
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ArticuloCostoDTO dto = (ArticuloCostoDTO) getDTO(domain,
				ArticuloCostoDTO.class);

		this.copiarValoresAtributos(domain, dto, attIgualesArticuloCosto);
	
		this.domainToMyArray(domain, dto, "tipoMovimiento",
				new String[] { "descripcion" });

		return dto;
	}

}
