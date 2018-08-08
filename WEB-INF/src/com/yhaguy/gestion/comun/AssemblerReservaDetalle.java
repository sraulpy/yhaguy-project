package com.yhaguy.gestion.comun;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.ReservaDetalle;

public class AssemblerReservaDetalle extends Assembler {

	private static String[] attIguales = {"cantidadReservada", "idDetalleOrigen"};
	
	@Override
	public Domain dtoToDomain(DTO dtoD) throws Exception {
		ReservaDetalleDTO dto = (ReservaDetalleDTO) dtoD;
		ReservaDetalle domain = (ReservaDetalle) getDomain(dto,
				ReservaDetalle.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "articulo");
		this.myArrayToDomain(dto, domain, "articuloDeposito");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ReservaDetalleDTO dto = (ReservaDetalleDTO) getDTO(domain,
				ReservaDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "articulo", new String[] {
				"codigoInterno", "descripcion" });
		this.domainToMyArray(domain, dto, "articuloDeposito", new String[] {"ubicacion" });
		

		return dto;
	}

}
