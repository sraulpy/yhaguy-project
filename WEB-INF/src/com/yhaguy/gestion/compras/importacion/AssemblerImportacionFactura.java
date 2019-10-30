package com.yhaguy.gestion.compras.importacion;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.ImportacionFactura;
import com.yhaguy.domain.ImportacionFacturaDetalle;
import com.yhaguy.gestion.empresa.AssemblerProveedor;

public class AssemblerImportacionFactura extends Assembler {
	

	private static String[] attIguales = {"numero", "fechaOriginal", "fechaCreacion", "observacion", "confirmadoImportacion",
					"confirmadoAuditoria", "confirmadoVentas", "confirmadoAdministracion", "propietarioActual", "descuentoGs",
					"descuentoDs", "totalAsignadoGs", "totalAsignadoDs", "porcProrrateo", "facturaVerificada", 
					"recepcionConfirmada"};
	private static String[] attTipoMovimiento = { "descripcion" };
	private static String[] attCondicion = { "descripcion", "plazo" };
	private static String[] attMoneda = { "descripcion", "sigla" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		ImportacionFactura domain = (ImportacionFactura) getDomain(dto, ImportacionFactura.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.hijoDtoToHijoDomain(dto, domain, "proveedor", new AssemblerProveedor(), false);
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.myArrayToDomain(dto, domain, "condicionPago");
		this.myArrayToDomain(dto, domain, "moneda");
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true, new AssemblerImportacionFacturaDetalle());

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ImportacionFacturaDTO dto = (ImportacionFacturaDTO) getDTO(domain, ImportacionFacturaDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.hijoDomainToHijoDTO(domain, dto, "proveedor", new AssemblerProveedor());
		this.domainToMyArray(domain, dto, "tipoMovimiento", attTipoMovimiento);
		this.domainToMyArray(domain, dto, "condicionPago", attCondicion);
		this.domainToMyArray(domain, dto, "moneda", attMoneda);
		this.listaDomainToListaDTO(domain, dto, "detalles", new AssemblerImportacionFacturaDetalle());

		return dto;
	}
}

// **************************************************************************************

class AssemblerImportacionFacturaDetalle extends Assembler {

	private static String[] attIgualesImportacionFacturaDetalle = { 
			"costoGs", "costoDs", "costoSinProrrateoGs", "costoSinProrrateoDs",
			"textoDescuento", "descuentoGs", "descuentoDs", 
			 "importeGastoDescuentoGs", "importeGastoDescuentoDs", "gastoDescuento", 
			 "valorProrrateo", "cantidad", "cantidadRecibida", "precioFinalGs", "minoristaGs", "listaGs",
			 "verificado", "conteo1", "conteo2", "conteo3", "cantidad_acum" };
	
	static final String[] ATT_ARTICULO = { "codigoInterno", "codigoProveedor",
			"codigoOriginal", "descripcion", "servicio" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {

		ImportacionFacturaDetalle domain = (ImportacionFacturaDetalle) getDomain(dto, ImportacionFacturaDetalle.class);

		this.copiarValoresAtributos(dto, domain,attIgualesImportacionFacturaDetalle);	
		this.myPairToDomain(dto, domain, "tipoGastoDescuento");
		this.myArrayToDomain(dto, domain, "articulo");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ImportacionFacturaDetalle domain_ = (ImportacionFacturaDetalle) domain;
		ImportacionFacturaDetalleDTO dto = (ImportacionFacturaDetalleDTO) getDTO(domain, ImportacionFacturaDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, attIgualesImportacionFacturaDetalle);
		this.domainToMyPair(domain, dto, "tipoGastoDescuento");
		this.domainToMyArray(domain, dto, "articulo", ATT_ARTICULO);
		String desc = domain_.getArticulo() != null? domain_.getArticulo().getFamilia().getDescripcion() : ArticuloFamilia.CONTABILIDAD;
		dto.getArticulo().setPos6(desc);

		return dto;
	}

}
