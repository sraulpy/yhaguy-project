package com.yhaguy.gestion.transferencia;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.gestion.comun.AssemblerReserva;

public class AssemblerTransferencia extends Assembler {

	private static String[] attIgualesTransferencia = { "numero",
			"fechaCreacion", "fechaEnvio", "fechaRecepcion", "observacion" };

	@Override
	public Domain dtoToDomain(DTO dtoP) throws Exception {
		TransferenciaDTO dto = (TransferenciaDTO) dtoP;
		Transferencia domain = (Transferencia) getDomain(dto, Transferencia.class);
		
		this.copiarValoresAtributos(dto, domain, attIgualesTransferencia);
		this.myPairToDomain(dto, domain, "transferenciaEstado");
		this.myPairToDomain(dto, domain, "transferenciaTipo");
		this.myArrayToDomain(dto, domain, "funcionarioCreador");
		
		this.myArrayToDomain(dto, domain, "funcionarioEnvio");
		this.myArrayToDomain(dto, domain, "funcionarioReceptor");
		
		this.myPairToDomain(dto, domain, "depositoSalida");
		this.myPairToDomain(dto, domain, "depositoEntrada");
		
		this.myPairToDomain(dto, domain, "transporte",true);
		
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true,
				new AssemblerTransferenciaDetalle());
		this.hijoDtoToHijoDomain(dto, domain, "reserva", new AssemblerReserva(), true);
		
		this.myPairToDomain(dto, domain, "sucursal");
		this.myPairToDomain(dto, domain, "sucursalDestino");
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		TransferenciaDTO dto = (TransferenciaDTO) getDTO(domain,
				TransferenciaDTO.class);
		Transferencia dom = (Transferencia) domain;

		this.copiarValoresAtributos(domain, dto, attIgualesTransferencia);
		this.domainToMyPair(dom, dto, "transferenciaEstado");
		this.domainToMyPair(dom, dto, "transferenciaTipo");

		this.domainToMyArray(domain, dto, "funcionarioCreador",
				new String[] { "descripcion" });
		this.domainToMyArray(domain, dto, "funcionarioEnvio",
				new String[] { "descripcion" });
		this.domainToMyArray(domain, dto, "funcionarioReceptor",
				new String[] { "descripcion" });		
		
		this.domainToMyPair(domain, dto, "depositoSalida");
		this.domainToMyPair(domain, dto, "depositoEntrada");

		this.listaDomainToListaDTO(domain, dto, "detalles",
				new AssemblerTransferenciaDetalle());
		this.domainToMyPair(dom, dto, "transporte");
		
		this.hijoDomainToHijoDTO(dom, dto, "reserva", new AssemblerReserva());
		
		this.domainToMyPair(dom, dto, "sucursal");
		this.domainToMyPair(dom, dto, "sucursalDestino");

		return dto;
	}
}
