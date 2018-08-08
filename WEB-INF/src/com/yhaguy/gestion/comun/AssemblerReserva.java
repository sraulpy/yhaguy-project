package com.yhaguy.gestion.comun;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.Reserva;

public class AssemblerReserva extends Assembler{

	private static String[] attIgualesReserva = { "fecha", "descripcion" };
	
	@Override
	public Domain dtoToDomain(DTO dtoR) throws Exception {
		ReservaDTO dto = (ReservaDTO) dtoR;
		Reserva domain = (Reserva) getDomain(dto,
				Reserva.class);
		
		dto.setEstadoReserva(dto.getEstadoReserva());
		
		this.copiarValoresAtributos(dto, domain, attIgualesReserva);
		this.myArrayToDomain(dto, domain, "funcionarioEmisor");
		this.myPairToDomain(dto, domain, "depositoSalida");
		this.myPairToDomain(dto, domain, "tipoReserva");
		this.myPairToDomain(dto, domain, "estadoReserva");
		
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true,
				new AssemblerReservaDetalle());
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ReservaDTO dto = (ReservaDTO) getDTO(domain,
				ReservaDTO.class);
		Reserva dom = (Reserva) domain;
		
		this.copiarValoresAtributos(dom, dto, attIgualesReserva);
		this.domainToMyArray(dom, dto, "funcionarioEmisor", new String[] { "descripcion" });
		this.domainToMyPair(dom, dto, "depositoSalida");
		this.domainToMyPair(domain, dto, "tipoReserva");
		this.domainToMyPair(domain, dto, "estadoReserva");
		
		this.listaDomainToListaDTO(domain, dto, "detalles", new AssemblerReservaDetalle());
		
		dto.setEstadoReserva(dto.getEstadoReserva());
		
		return dto;
	}

	
}
