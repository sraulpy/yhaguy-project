package com.yhaguy.gestion.stock.ajustes;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.AjusteStockDetalle;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ArticuloDepositoDTO;
import com.yhaguy.gestion.comun.AssemblerArticuloDeposito;

public class AjusteStockAssembler extends Assembler {

	static final String[] attIguales = { "fecha", "numero", "descripcion",
			"autorizadoPor" };
	static final String[] attTipoMovimiento = { "descripcion", "sigla",
			"clase", "tipoIva" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		AjusteStock domain = (AjusteStock) this.getDomain(dto,
				AjusteStock.class);
		AjusteStockDTO dto_ = (AjusteStockDTO) dto;

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myPairToDomain(dto, domain, "sucursal");
		this.myPairToDomain(dto, domain, "estadoComprobante");
		this.myPairToDomain(dto, domain, "deposito");
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true,
				new AjusteStockDetalleAssembler());
		
		if(dto_.isActualizarStock())
			this.actualizarStock(dto_);

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		AjusteStockDTO dto = (AjusteStockDTO) this.getDTO(domain,
				AjusteStockDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyPair(domain, dto, "sucursal");
		this.domainToMyPair(domain, dto, "estadoComprobante");
		this.domainToMyPair(domain, dto, "deposito");
		this.domainToMyArray(domain, dto, "tipoMovimiento", attTipoMovimiento);
		this.listaDomainToListaDTO(domain, dto, "detalles",
				new AjusteStockDetalleAssembler());

		return dto;
	}
	
	/**
	 * crea o modifica el articuloDeposito..
	 */
	private void actualizarStock(AjusteStockDTO dto) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (AjusteStockDetalleDTO item : dto.getDetalles()) {

			ArticuloDeposito adep = rr.getArticuloDeposito(item.getArticulo()
					.getId(), dto.getDeposito().getId());
			if (adep == null) {
				ArticuloDepositoDTO ad = new ArticuloDepositoDTO();
				ad.setArticulo(item.getArticulo());
				ad.setDeposito(dto.getDeposito());
				ad.setStock(item.getCantidad());
				ad.setStockMaximo(0);
				ad.setStockMinimo(0);
				adep = (ArticuloDeposito) new AssemblerArticuloDeposito()
						.dtoToDomain(ad);
			} else {
				adep.setStock(adep.getStock() + item.getCantidad());
			}
			rr.saveObject(adep, this.getLogin());
		}
	}

}

/**
 * Assembler del detalle..
 */
class AjusteStockDetalleAssembler extends Assembler {

	static String[] attIguales = { "cantidad", "costoGs" };
	static String[] attArticulo = { "codigoInterno", "codigoProveedor",
			"codigoOriginal", "descripcion" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		AjusteStockDetalle domain = (AjusteStockDetalle) this.getDomain(dto,
				AjusteStockDetalle.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "articulo");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		AjusteStockDetalleDTO dto = (AjusteStockDetalleDTO) this.getDTO(domain,
				AjusteStockDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "articulo", attArticulo);

		return dto;
	}

}
