package com.yhaguy.gestion.compras.locales;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.CompraLocalOrden;
import com.yhaguy.domain.CompraLocalOrdenDetalle;
import com.yhaguy.gestion.empresa.AssemblerProveedor;

public class AssemblerCompraLocalOrden extends Assembler {

	private static String[] attIguales = { "numero", "fechaCreacion",
			"tipoCambio", "observacion", "autorizado", "cerrado",
			"recepcionado", "autorizadoPor", "numeroFactura" };
	private static String[] attMoneda = { "descripcion", "sigla" };
	private static String[] attCondicion = { "descripcion", "plazo" };
	private static String[] attTipoMovimiento = { "descripcion" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		CompraLocalOrden domain = (CompraLocalOrden) getDomain(dto,
				CompraLocalOrden.class);
		CompraLocalOrdenDTO dto_ = (CompraLocalOrdenDTO) dto;

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.hijoDtoToHijoDomain(dto, domain, "proveedor", new AssemblerProveedor(), false);
		this.myArrayToDomain(dto, domain, "condicionPago");
		this.myArrayToDomain(dto, domain, "moneda");
		this.myPairToDomain(dto, domain, "sucursal");
		this.myPairToDomain(dto, domain, "deposito");
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.hijoDtoToHijoDomain(dto, domain, "factura", new AssemblerCompraLocalFactura(), true);
		
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true,
				new AssemblerCompraLocalOrdenDetalle());
		this.listaDTOToListaDomain(dto, domain, "facturas", true, true,
				new AssemblerCompraLocalFactura());
		this.listaDTOToListaDomain(dto, domain, "resumenGastos", true, true,
				new AssemblerCompraLocalGasto());
		
		if (dto_.getFactura() != null) {
			domain.setNumeroFactura(dto_.getFactura().getNumero());
		}

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		CompraLocalOrdenDTO dto = (CompraLocalOrdenDTO) getDTO(domain,
				CompraLocalOrdenDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.hijoDomainToHijoDTO(domain, dto, "proveedor", new AssemblerProveedor());
		this.domainToMyArray(domain, dto, "condicionPago", attCondicion);
		this.domainToMyArray(domain, dto, "moneda", attMoneda);
		this.domainToMyPair(domain, dto, "sucursal");
		this.domainToMyPair(domain, dto, "deposito");
		this.domainToMyArray(domain, dto, "tipoMovimiento", attTipoMovimiento);
		this.hijoDomainToHijoDTO(domain, dto, "factura", new AssemblerCompraLocalFactura());
		
		this.listaDomainToListaDTO(domain, dto, "detalles",
				new AssemblerCompraLocalOrdenDetalle());
		this.listaDomainToListaDTO(domain, dto, "facturas",
				new AssemblerCompraLocalFactura());
		this.listaDomainToListaDTO(domain, dto, "resumenGastos",
				new AssemblerCompraLocalGasto());
		
		return dto;
	}

}

class AssemblerCompraLocalOrdenDetalle extends Assembler {

	private static String[] attIguales = { "costoGs", "costoDs", "ultCostoGs",
			"cantidad", "cantidadRecibida", "presupuesto", "ordenCompra" };
	private static String[] attArticulo = { "codigoInterno", "codigoProveedor", "codigoOriginal", "descripcion" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		CompraLocalOrdenDetalle domain = (CompraLocalOrdenDetalle) getDomain(
				dto, CompraLocalOrdenDetalle.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "articulo");
		this.myPairToDomain(dto, domain, "iva");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		CompraLocalOrdenDetalleDTO dto = (CompraLocalOrdenDetalleDTO) getDTO(
				domain, CompraLocalOrdenDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "articulo", attArticulo);
		this.domainToMyPair(domain, dto, "iva");

		return dto;
	}

}
