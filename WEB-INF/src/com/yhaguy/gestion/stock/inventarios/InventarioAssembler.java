package com.yhaguy.gestion.stock.inventarios;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.AjusteStockDetalle;

public class InventarioAssembler extends Assembler {

	static final String[] attIguales = { "fecha", "numero", "descripcion", "autorizadoPor", "orden" };
	static final String[] attTipoMovimiento = { "descripcion", "sigla", "clase", "tipoIva" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		AjusteStock domain = (AjusteStock) this.getDomain(dto, AjusteStock.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myPairToDomain(dto, domain, "sucursal");
		this.myPairToDomain(dto, domain, "estadoComprobante");
		this.myPairToDomain(dto, domain, "deposito");
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true, new AjusteStockDetalleAssembler());

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		InventarioDTO dto = (InventarioDTO) this.getDTO(domain,
				InventarioDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyPair(domain, dto, "sucursal");
		this.domainToMyPair(domain, dto, "estadoComprobante");
		this.domainToMyPair(domain, dto, "deposito");
		this.domainToMyArray(domain, dto, "tipoMovimiento", attTipoMovimiento);
		this.listaDomainToListaDTO(domain, dto, "detalles", new AjusteStockDetalleAssembler());

		return dto;
	}
}

/**
 * Assembler del detalle..
 */
class AjusteStockDetalleAssembler extends Assembler {

	static String[] attIguales = { "cantidad", "cantidadSistema", "costoGs" };
	static String[] attArticulo = { "codigoInterno", "codigoProveedor", "codigoOriginal", "descripcion" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		AjusteStockDetalle domain = (AjusteStockDetalle) this.getDomain(dto, AjusteStockDetalle.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "articulo");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		InventarioDetalleDTO dto = (InventarioDetalleDTO) this.getDTO(domain, InventarioDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "articulo", attArticulo);

		return dto;
	}

}
