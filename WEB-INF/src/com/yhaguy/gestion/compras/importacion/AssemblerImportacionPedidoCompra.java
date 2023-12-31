package com.yhaguy.gestion.compras.importacion;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.ImportacionAplicacionAnticipo;
import com.yhaguy.domain.ImportacionGastoImprevisto;
import com.yhaguy.domain.ImportacionPedidoCompra;
import com.yhaguy.domain.ImportacionPedidoCompraDetalle;
import com.yhaguy.gestion.empresa.AssemblerProveedor;
import com.yhaguy.gestion.empresa.ctacte.AssemblerCtaCteEmpresaMovimiento;

public class AssemblerImportacionPedidoCompra extends Assembler {

	private static String[] attIgualesImportacionPedidoCompra = { "numeroPedidoCompra", "fechaCreacion", "fechaCierre",
			"observacion", "cambio", "confirmadoImportacion", "confirmadoVentas", "confirmadoAdministracion",
			"propietarioActual", "pedidoConfirmado", "importacionConfirmada", "cifProrrateado", "totalImporteGs",
			"totalImporteDs", "via", "recepcionHabilitada", "conteo1", "conteo2", "conteo3" };

	private static final String[] ATT_MONEDA = { "descripcion", "sigla" };	
	private static final String[] ATT_CONDICION = { "descripcion", "plazo" };	
	private static final String[] ATT_TIPOMOVIMIENTO = { "descripcion" };
	private static final String[] ATT_SUBDIARIO = { "numero", "fecha", "descripcion", "confirmado"};
	private static final String[] ATT_TRAZABILIDAD = { "fecha", "descripcion", "urlDocumento" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		ImportacionPedidoCompra domain = (ImportacionPedidoCompra) getDomain(dto, ImportacionPedidoCompra.class);
		ImportacionPedidoCompraDTO dto_ = (ImportacionPedidoCompraDTO) dto;

		this.copiarValoresAtributos(dto, domain, attIgualesImportacionPedidoCompra);
		this.myArrayToDomain(dto, domain, "proveedorCondicionPago");
		this.myPairToDomain(dto, domain, "estado");
		this.myArrayToDomain(dto, domain, "moneda");
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.myPairToDomain(dto, domain, "tipo");
		this.myPairToDomain(dto, domain, "deposito");
		
		this.listaDTOToListaDomain(dto, domain, "solicitudCotizaciones", true, true, new AssemblerImportacionPedidoCompraDetalle());
		this.listaDTOToListaDomain(dto, domain, "importacionPedidoCompraDetalle", true, true, new AssemblerImportacionPedidoCompraDetalle());
		this.listaDTOToListaDomain(dto, domain, "importacionFactura", true, true, new AssemblerImportacionFactura());
		this.listaDTOToListaDomain(dto, domain, "gastosImprevistos", true, true, new AssemblerImportacionGastoImprevisto());
		this.listaDTOToListaDomain(dto, domain, "aplicacionAnticipos", true, true, new AssemblerImportacionAplicacionAnticipo());
		this.hijoDtoToHijoDomain(dto, domain, "proveedor", new AssemblerProveedor(), false);
		this.hijoDtoToHijoDomain(dto, domain, "resumenGastosDespacho", new AssemblerGastosDespacho(), true);
		this.listaMyArrayToListaDomain(dto_, domain, "trazabilidad", ATT_TRAZABILIDAD, true, true);
		
		if (dto_.getImportacionFactura().size() > 0) {
			domain.setFechaFactura(dto_.getImportacionFactura().get(0).getFechaOriginal());
			domain.setNumeroFactura(dto_.getImportacionFactura().get(0).getNumero());
		}

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ImportacionPedidoCompraDTO dto = (ImportacionPedidoCompraDTO) getDTO(domain, ImportacionPedidoCompraDTO.class);

		this.copiarValoresAtributos(domain, dto, attIgualesImportacionPedidoCompra);
		this.domainToMyArray(domain, dto, "proveedorCondicionPago", ATT_CONDICION);
		this.domainToMyPair(domain, dto, "estado", "descripcion");
		this.domainToMyArray(domain, dto, "moneda", ATT_MONEDA);
		this.domainToMyArray(domain, dto, "tipoMovimiento", ATT_TIPOMOVIMIENTO);
		this.domainToMyPair(domain, dto, "tipo", "descripcion");
		this.domainToMyPair(domain, dto, "deposito");
		this.domainToMyArray(domain, dto, "subDiario", ATT_SUBDIARIO);
		
		this.listaDomainToListaDTO(domain, dto, "solicitudCotizaciones", new AssemblerImportacionPedidoCompraDetalle());
		this.listaDomainToListaDTO(domain, dto, "importacionPedidoCompraDetalle", new AssemblerImportacionPedidoCompraDetalle());
		this.listaDomainToListaDTO(domain, dto, "importacionFactura", new AssemblerImportacionFactura());		
		this.listaDomainToListaDTO(domain, dto, "gastosImprevistos", new AssemblerImportacionGastoImprevisto());
		this.listaDomainToListaDTO(domain, dto, "aplicacionAnticipos", new AssemblerImportacionAplicacionAnticipo());
		this.hijoDomainToHijoDTO(domain, dto, "proveedor", new AssemblerProveedor());
		this.hijoDomainToHijoDTO(domain, dto, "resumenGastosDespacho", new AssemblerGastosDespacho());
		this.listaDomainToListaMyArray(domain, dto, "trazabilidad", ATT_TRAZABILIDAD);

		return dto;
	}
}

// Assembler importacion detalle..
class AssemblerImportacionPedidoCompraDetalle extends Assembler {

	private static String[] attIgualesImportacionPedidoCompraDetalle = { "cantidad", "ultimoCostoDs",
			"fechaUltimoCosto", "costoProformaGs", "costoProformaDs", "observacion" };
	
	static final String[] ATT_ARTICULO = { "codigoInterno", "codigoProveedor",
			"codigoOriginal", "descripcion", "servicio" };

	@Override
	public Domain dtoToDomain(DTO dtoPD) throws Exception {

		ImportacionPedidoCompraDetalleDTO dto = (ImportacionPedidoCompraDetalleDTO) dtoPD;
		ImportacionPedidoCompraDetalle domain = (ImportacionPedidoCompraDetalle) getDomain(
				dto, ImportacionPedidoCompraDetalle.class);

		this.copiarValoresAtributos(dto, domain, attIgualesImportacionPedidoCompraDetalle);	
		this.myArrayToDomain(dto, domain, "articulo");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {

		ImportacionPedidoCompraDetalleDTO dto = (ImportacionPedidoCompraDetalleDTO) getDTO(
				domain, ImportacionPedidoCompraDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, attIgualesImportacionPedidoCompraDetalle);		
		this.domainToMyArray(domain, dto, "articulo", ATT_ARTICULO);

		return dto;
	}

}

// Assembler gasto imprevisto..
class AssemblerImportacionGastoImprevisto extends Assembler {

	private static String[] attIguales = {"importeGs", "importeDs"};
	private static String[] attProveedor = {"codigoEmpresa", "razonSocial", "ruc"};
	
	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		ImportacionGastoImprevisto domain = (ImportacionGastoImprevisto) getDomain(dto, ImportacionGastoImprevisto.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "proveedor");
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ImportacionGastoImprevistoDTO dto = (ImportacionGastoImprevistoDTO) getDTO(domain, ImportacionGastoImprevistoDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "proveedor", attProveedor);
		
		return dto;
	}	
}

// Assembler aplicacion anticipo..
class AssemblerImportacionAplicacionAnticipo extends Assembler {

	private static String[] attIguales = { "importeGs", "importeDs" };
	
	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		ImportacionAplicacionAnticipo domain = (ImportacionAplicacionAnticipo) getDomain(dto, ImportacionAplicacionAnticipo.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.hijoDtoToHijoDomain(dto, domain, "movimiento", new AssemblerCtaCteEmpresaMovimiento(), false);
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ImportacionAplicacionAnticipoDTO dto = (ImportacionAplicacionAnticipoDTO) getDTO(domain, ImportacionAplicacionAnticipoDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.hijoDomainToHijoDTO(domain, dto, "movimiento", new AssemblerCtaCteEmpresaMovimiento());
		
		return dto;
	}	
}




