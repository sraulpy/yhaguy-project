package com.yhaguy.gestion.compras.gastos.generales.pedidos;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.OrdenPedidoGastoDetalle;
import com.yhaguy.gestion.compras.gastos.subdiario.AssemblerArticuloGasto;

public class AssemblerOrdenPedidoGastoDetalle extends Assembler {

	private static String[] attIguales = { "importe", "descripcion" };
	private static String[] attDepartamento = { "nombre" };
	private static String[] attCentroCosto = { "numero", "descripcion",
			"montoAsignado" };

	@Override
	public Domain dtoToDomain(DTO dtoD) throws Exception {
		OrdenPedidoGastoDetalleDTO dto = (OrdenPedidoGastoDetalleDTO) dtoD;
		OrdenPedidoGastoDetalle domain = (OrdenPedidoGastoDetalle) getDomain(
				dto, OrdenPedidoGastoDetalle.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "departamento");
		this.myArrayToDomain(dto, domain, "centroCosto");
		this.myPairToDomain(dto, domain, "iva");

		this.hijoDtoToHijoDomain(dto, domain, "articuloGasto",
				new AssemblerArticuloGasto(), false);

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		OrdenPedidoGastoDetalleDTO dto = (OrdenPedidoGastoDetalleDTO) getDTO(
				domain, OrdenPedidoGastoDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "departamento", attDepartamento);
		this.domainToMyArray(domain, dto, "centroCosto", attCentroCosto);
		this.domainToMyPair(domain, dto, "iva");

		this.hijoDomainToHijoDTO(domain, dto, "articuloGasto",
				new AssemblerArticuloGasto());

		return dto;
	}
}