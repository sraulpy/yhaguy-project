package com.yhaguy.gestion.transferencia;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.TransferenciaDetalle;

public class AssemblerTransferenciaDetalle extends Assembler {
	
	static String[] attIguales = { "cantidad", "cantidadPedida",
			"cantidadEnviada", "cantidadRecibida", "costo", "estado" };

	@Override
	public Domain dtoToDomain(DTO dtoD) throws Exception {
		TransferenciaDetalleDTO dto = (TransferenciaDetalleDTO) dtoD;
		TransferenciaDetalle domain = (TransferenciaDetalle) getDomain(dto,
				TransferenciaDetalle.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "articulo");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		TransferenciaDetalleDTO dto = (TransferenciaDetalleDTO) getDTO(domain,
				TransferenciaDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "articulo", new String[] {
				"codigoInterno", "descripcion" });

		return dto;
	}
}
