package com.yhaguy.gestion.compras.locales;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.CompraLocalGasto;

public class AssemblerCompraLocalGasto extends Assembler{
	
	private static String[] attIguales = {"descripcion", "montoGs", "montoDs"};
	private static String[] attGasto = {"numeroFactura", "proveedor", "importeGs"};

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		CompraLocalGasto domain = (CompraLocalGasto) getDomain(dto, CompraLocalGasto.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "gasto");
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		CompraLocalGastoDTO dto = (CompraLocalGastoDTO) getDTO(domain, CompraLocalGastoDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "gasto", attGasto);
		
		MyPair prov = (MyPair) dto.getGasto().getPos2();
		dto.getGasto().setPos2(prov.getText());
		
		return dto;
	}	
}