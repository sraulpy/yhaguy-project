package com.yhaguy.gestion.bancos.conciliacion;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.BancoExtracto;
import com.yhaguy.domain.BancoExtractoDetalle;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoCtaCte;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoMovimiento;

public class BancoExtractoAssembler extends Assembler {

	static String[] attIguales = new String[] { "numero", "desde", "hasta", "cerrado" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		BancoExtracto domain = (BancoExtracto) this.getDomain(dto,
				BancoExtracto.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.hijoDtoToHijoDomain(dto, domain, "banco", new AssemblerBancoCtaCte(), false);
		this.myPairToDomain(dto, domain, "sucursal");
		this.listaDTOToListaDomain(dto, domain, "detalles2", true, true, new BancoExtractoDetalleAssembler());
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		BancoExtractoDTO dto = (BancoExtractoDTO) this.getDTO(domain,
				BancoExtractoDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.hijoDomainToHijoDTO(domain, dto, "banco", new AssemblerBancoCtaCte());
		this.domainToMyPair(domain, dto, "sucursal");
		this.listaDomainToListaDTO(domain, dto, "detalles2", new BancoExtractoDetalleAssembler());

		return dto;
	}

}

/**
 * assembler del detalle..
 */
class BancoExtractoDetalleAssembler extends Assembler {
	
	static  String[] attIguales = { "fecha", "numero", "descripcion", "debe", "haber", "conciliado" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		BancoExtractoDetalle domain = (BancoExtractoDetalle) this.getDomain(dto, BancoExtractoDetalle.class);		
		this.copiarValoresAtributos(dto, domain, attIguales);	
		this.hijoDtoToHijoDomain(dto, domain, "bancoMovimiento", new AssemblerBancoMovimiento(), false);
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		BancoExtractoDetalleDTO dto = (BancoExtractoDetalleDTO) this.getDTO(domain, BancoExtractoDetalleDTO.class);
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.hijoDomainToHijoDTO(domain, dto, "bancoMovimiento", new AssemblerBancoMovimiento());
		return dto;
	}	
}
