package com.yhaguy.gestion.notadebito;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.NotaDebito;
import com.yhaguy.domain.NotaDebitoDetalle;
import com.yhaguy.gestion.empresa.AssemblerCliente;

public class NotaDebitoAssembler extends Assembler {
	
	static String[] attIguales = { "numero", "timbrado", "fecha", "numeroFactura" };	
	static String[] attTipoMovimiento = { "descripcion", "sigla", "clase", "tipoIva" };	
	static String[] attTipos = { "descripcion", "sigla" };	

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		NotaDebito domain = (NotaDebito) this.getDomain(dto, NotaDebito.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myPairToDomain(dto, domain, "sucursal");
		this.myPairToDomain(dto, domain, "estadoComprobante");
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		
		this.hijoDtoToHijoDomain(dto, domain, "cliente", new AssemblerCliente(), false);
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true,
				new NotaDebitoDetalleAssembler());
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		NotaDebitoDTO dto = (NotaDebitoDTO) this.getDTO(domain, NotaDebitoDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyPair(domain, dto, "sucursal");
		this.domainToMyPair(domain, dto, "estadoComprobante");
		this.domainToMyArray(domain, dto, "tipoMovimiento", attTipoMovimiento);
		
		this.hijoDomainToHijoDTO(domain, dto, "cliente", new AssemblerCliente());
		this.listaDomainToListaDTO(domain, dto, "detalles", new NotaDebitoDetalleAssembler());
		
		return dto;
	}

}

// Assembler del detalle..
class NotaDebitoDetalleAssembler extends Assembler {
	
	static String[] attIguales = { "descripcion", "importeGs" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		NotaDebitoDetalle domain = (NotaDebitoDetalle) this.getDomain(dto, NotaDebitoDetalle.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myPairToDomain(dto, domain, "tipoIva");
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		NotaDebitoDetalleDTO dto = (NotaDebitoDetalleDTO) this.getDTO(domain, NotaDebitoDetalleDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyPair(domain, dto, "tipoIva");
		
		return dto;
	}	
}
