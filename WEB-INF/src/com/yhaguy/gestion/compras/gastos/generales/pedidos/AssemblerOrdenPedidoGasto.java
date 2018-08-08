package com.yhaguy.gestion.compras.gastos.generales.pedidos;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.OrdenPedidoGasto;
import com.yhaguy.gestion.compras.gastos.subdiario.AssemblerGasto;
import com.yhaguy.gestion.compras.gastos.subdiario.GastoDTO;

public class AssemblerOrdenPedidoGasto extends Assembler {

	private static String[] attIgualesOrdenPedidoGasto = { "numero",
			"idUsuarioCarga", "nombreUsuarioCarga", "fechaCarga",
			"descripcion", "estado", "autorizado", "confirmado", "presupuesto",
			"idUsuarioAutoriza", "nombreUsuarioAutoriza", "fechaAutorizacion" };

	private static String[] attProveedor = { "codigoEmpresa", "razonSocial",
			"ruc", "cuentaContable", "moneda", "idEmpresa" };

	private static String[] attCondicionPago = { "descripcion", "plazo" };
	private static String[] attDepartamento = { "nombre", "descripcion" };

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		OrdenPedidoGastoDTO dto = (OrdenPedidoGastoDTO) getDTO(domain,
				OrdenPedidoGastoDTO.class);

		this.copiarValoresAtributos(domain, dto, attIgualesOrdenPedidoGasto);

		this.domainToMyArray(domain, dto, "proveedor", attProveedor);
		this.domainToMyArray(domain, dto, "condicionPago", attCondicionPago);
		this.listaDomainToListaDTO(domain, dto, "ordenPedidoGastoDetalle",
				new AssemblerOrdenPedidoGastoDetalle());
		this.listaDomainToListaDTO(domain, dto, "gastos", new AssemblerGasto());
		this.domainToMyArray(domain, dto, "departamento", attDepartamento);
		this.domainToMyPair(domain, dto, "sucursal");

		return dto;
	}

	@Override
	public Domain dtoToDomain(DTO dtoP) throws Exception {
		OrdenPedidoGastoDTO dto = (OrdenPedidoGastoDTO) dtoP;
		OrdenPedidoGasto domain = (OrdenPedidoGasto) getDomain(dto, OrdenPedidoGasto.class);

		this.copiarValoresAtributos(dto, domain, attIgualesOrdenPedidoGasto);

		this.myArrayToDomain(dto, domain, "proveedor");
		this.myArrayToDomain(dto, domain, "condicionPago");
		this.myArrayToDomain(dto, domain, "departamento");
		this.myPairToDomain(dto, domain, "sucursal");
		this.listaDTOToListaDomain(dto, domain, "ordenPedidoGastoDetalle",
				true, true, new AssemblerOrdenPedidoGastoDetalle());
		this.listaDTOToListaDomain(dto, domain, "gastos", false, false,
				new AssemblerGasto());
		
		for (GastoDTO gasto : dto.getGastos()) {
			domain.setNumeroFactura(gasto.getNumeroFactura());
			domain.setNumeroImportacion(gasto.getNumeroImportacion());
		}
		
		for (OrdenPedidoGastoDetalleDTO det : dto.getOrdenPedidoGastoDetalle()) {
			domain.setAuxi(det.getArticuloGasto().getDescripcion());
		}

		return domain;
	}

}

