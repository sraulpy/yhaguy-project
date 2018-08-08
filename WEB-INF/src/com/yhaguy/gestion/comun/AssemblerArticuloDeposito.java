package com.yhaguy.gestion.comun;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.ArticuloDeposito;

public class AssemblerArticuloDeposito extends Assembler{

	private static String[] attIgualesArticulo = { "ubicacion",
		"stock", "stockMinimo", "stockMaximo" };
	@Override
	public Domain dtoToDomain(DTO dtoP) throws Exception {
		ArticuloDepositoDTO dto = (ArticuloDepositoDTO) dtoP;
		ArticuloDeposito domain = (ArticuloDeposito) getDomain(dto,
				ArticuloDeposito.class);
		this.copiarValoresAtributos(dto, domain, attIgualesArticulo);
		this.myArrayToDomain(dto, domain, "articulo");
		this.myPairToDomain(dto, domain, "deposito");
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ArticuloDepositoDTO dto = (ArticuloDepositoDTO) getDTO(domain,
				ArticuloDepositoDTO.class);
		ArticuloDeposito dom = (ArticuloDeposito) domain;
		
		this.copiarValoresAtributos(dom, dto, attIgualesArticulo);
		this.domainToMyArray(dom, dto, "articulo", new String[] {
				 "descripcion" });	
		this.domainToMyPair(dom, dto, "deposito");
		return dto;
	}
}
