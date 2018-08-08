package com.yhaguy.gestion.compras.importacion;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.ImportacionResumenGastosDespacho;
import com.yhaguy.gestion.empresa.AssemblerProveedor;

public class AssemblerGastosDespacho extends Assembler{

	private static String[] attIgualesGastosDespacho = {
		"nroDespacho", "nroLiquidacion", "fechaFacturaDespacho", "tipoCambio", 
		"valorCIFds", "valorCIFgs", "valorFOBds", "valorFOBgs", "valorFleteDs",
		"valorFleteGs", "valorSeguroDs", "valorSeguroGs", "totalIVAds", "totalIVAgs", 
		"totalGastosDs", "totalGastosGs", "coeficiente", "coeficienteAsignado"};		
	
	@Override
	public Domain dtoToDomain(DTO dtoGD) throws Exception {
		ResumenGastosDespachoDTO dto = (ResumenGastosDespachoDTO) dtoGD;
		ImportacionResumenGastosDespacho domain = (ImportacionResumenGastosDespacho) getDomain(dto, ImportacionResumenGastosDespacho.class);
		
		this.copiarValoresAtributos(dto, domain, attIgualesGastosDespacho);
		
		if (dto.getDespachante().esNuevo() == false) {
			this.hijoDtoToHijoDomain(dto, domain, "despachante", new AssemblerProveedor(), false);
		}		
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ResumenGastosDespachoDTO dto = (ResumenGastosDespachoDTO) getDTO(domain, ResumenGastosDespachoDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIgualesGastosDespacho);
		this.hijoDomainToHijoDTO(domain, dto, "despachante", new AssemblerProveedor());
		
		return dto;
	}
}