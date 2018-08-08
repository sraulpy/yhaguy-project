package com.yhaguy.gestion.bancos.cheques;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoCtaCte;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoMovimiento;

public class AssemblerBancoCheque extends Assembler{

	private static String[] attIguales = { "numero", "fechaEmision",
			"fechaVencimiento", "beneficiario", "concepto", "monto",
			"numeroCaja", "numeroOrdenPago" };
	
	private static String[] attMoneda = { "descripcion", "sigla" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		BancoCheque domain = (BancoCheque) getDomain(dto, BancoCheque.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "moneda");
		this.hijoDtoToHijoDomain(dto, domain, "movimiento", new AssemblerBancoMovimiento(), true);
		this.hijoDtoToHijoDomain(dto, domain, "banco", new AssemblerBancoCtaCte(), false);
		this.myPairToDomain(dto, domain, "estadoComprobante");
		this.myPairToDomain(dto, domain, "modoDeCreacion");
		this.myPairToDomain(dto, domain, "reciboFormaPago");
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		BancoChequeDTO dto = (BancoChequeDTO) getDTO(domain, BancoChequeDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "moneda", attMoneda);
		this.hijoDomainToHijoDTO(domain, dto, "movimiento", new AssemblerBancoMovimiento());
		this.hijoDomainToHijoDTO(domain, dto, "banco", new AssemblerBancoCtaCte());
		this.domainToMyPair(domain, dto, "estadoComprobante");
		this.domainToMyPair(domain, dto, "modoDeCreacion");
		this.domainToMyPair(domain, dto, "reciboFormaPago");
		
		return dto;
	}
}
