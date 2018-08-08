package com.yhaguy.gestion.compras.gastos.generales;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.OrdenPedidoGasto;
import com.yhaguy.gestion.compras.gastos.generales.pedidos.AssemblerOrdenPedidoGastoDetalle;
import com.yhaguy.gestion.compras.gastos.generales.pedidos.OrdenPedidoGastoDTO;
import com.yhaguy.gestion.compras.gastos.subdiario.AssemblerGasto;
import com.yhaguy.gestion.contabilidad.subdiario.AssemblerSubDiario;

public class AssemblerGastoGeneral extends Assembler{
	
	private static String[] attIguales = {"numero", "fechaCarga", "descripcion"};	
	private static String[] attCondicionPago = { "descripcion", "plazo"};
	private static String[] attProveedor = { "codigoEmpresa", "razonSocial",
			"ruc", "cuentaContable", "moneda", "idEmpresa" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		OrdenPedidoGasto domain = (OrdenPedidoGasto) getDomain(dto, OrdenPedidoGasto.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		
		this.hijoDtoToHijoDomain(dto, domain, "subDiario", new AssemblerSubDiario(), true);
		this.listaDTOToListaDomain(dto, domain, "gastos", true, false, new AssemblerGasto());
		this.myArrayToDomain(dto, domain, "proveedor");
		this.myArrayToDomain(dto, domain, "condicionPago");
		this.myPairToDomain(dto, domain, "sucursal");
		this.listaDTOToListaDomain(dto, domain, "ordenPedidoGastoDetalle", true, true, 
				new AssemblerOrdenPedidoGastoDetalle());
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		OrdenPedidoGastoDTO dto = (OrdenPedidoGastoDTO) getDTO(domain, OrdenPedidoGastoDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.hijoDomainToHijoDTO(domain, dto, "subDiario", new AssemblerSubDiario());
		this.listaDomainToListaDTO(domain, dto, "gastos", new AssemblerGasto());
		this.domainToMyArray(domain, dto, "proveedor", attProveedor);
		this.domainToMyArray(domain, dto, "condicionPago", attCondicionPago);
		this.domainToMyPair(domain, dto, "sucursal");
		this.listaDomainToListaDTO(domain, dto, "ordenPedidoGastoDetalle", new AssemblerOrdenPedidoGastoDetalle());		
		
		return dto;
	}

}
