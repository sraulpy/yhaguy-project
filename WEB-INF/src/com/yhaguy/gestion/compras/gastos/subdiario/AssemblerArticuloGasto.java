package com.yhaguy.gestion.compras.gastos.subdiario;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.ArticuloGasto;

public class AssemblerArticuloGasto extends Assembler {

	private static String[] attIguales = { "descripcion", "creadoPor",
			"verificadoPor" };
	private static String[] attCuentaContable = { "codigo", "descripcion",
			"alias" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		ArticuloGasto domain = (ArticuloGasto) getDomain(dto,
				ArticuloGasto.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "cuentaContable");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ArticuloGastoDTO dto = (ArticuloGastoDTO) getDTO(domain,
				ArticuloGastoDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "cuentaContable", attCuentaContable);

		return dto;
	}

}



