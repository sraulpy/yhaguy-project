package com.yhaguy.gestion.compras.locales;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.CompraFiscal;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.CompraLocalFacturaDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.empresa.AssemblerProveedor;

public class AssemblerCompraLocalFactura extends Assembler {

	private static String[] attIguales = { "numero", "fechaCreacion",
			"fechaOriginal", "fechaVencimiento", "tipoCambio", "observacion",
			"descuentoGs", "descuentoDs", "totalAsignadoGs", "totalAsignadoDs",
			"recepcionConfirmada", "importeGs", "importeDs", "importeIva10",
			"importeIva5" };
	private static String[] attMoneda = { "descripcion", "sigla" };
	private static String[] attTipoMovimiento = { "descripcion" };
	private static String[] attCondicion = { "descripcion", "plazo" };
	private static String[] attTimbrado = { "numero", "vencimiento" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		CompraLocalFactura domain = (CompraLocalFactura) getDomain(dto,
				CompraLocalFactura.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.hijoDtoToHijoDomain(dto, domain, "proveedor", new AssemblerProveedor(), false);
		this.myArrayToDomain(dto, domain, "condicionPago");
		this.myArrayToDomain(dto, domain, "moneda");
		this.myPairToDomain(dto, domain, "sucursal");
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.myArrayToDomain(dto, domain, "timbrado");
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true,
				new AssemblerCompraLocalFacturaDetalle());

		// Genera la CompraFiscal..
		if (dto.esNuevo() == true) {
			this.saveCompraFiscal((CompraLocalFacturaDTO) dto);
		}

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		CompraLocalFacturaDTO dto = (CompraLocalFacturaDTO) getDTO(domain,
				CompraLocalFacturaDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.hijoDomainToHijoDTO(domain, dto, "proveedor",
				new AssemblerProveedor());
		this.domainToMyArray(domain, dto, "condicionPago", attCondicion);
		this.domainToMyArray(domain, dto, "moneda", attMoneda);
		this.domainToMyPair(domain, dto, "sucursal");
		this.domainToMyArray(domain, dto, "tipoMovimiento", attTipoMovimiento);
		this.domainToMyArray(domain, dto, "timbrado", attTimbrado);
		this.listaDomainToListaDTO(domain, dto, "detalles",
				new AssemblerCompraLocalFacturaDetalle());

		return dto;
	}

	/**
	 * Genera la Compra Fiscal a partir de los datos de la factura.. Esta
	 * información sera usada para generar el libro compras desde una sola
	 * tabla..
	 */
	private void saveCompraFiscal(CompraLocalFacturaDTO compra)
			throws Exception {

		CompraFiscal cf = new CompraFiscal();
		cf.setCondicion((String) compra.getCondicionPago().getPos1());
		cf.setDescuentoDs(compra.getDescuentoDs());
		cf.setDescuentoGs(compra.getDescuentoGs());
		cf.setEmision(compra.getFechaCreacion());
		cf.setIdCompra(compra.getId());
		cf.setIdCondicion(compra.getCondicionPago().getId());
		cf.setIdMoneda(compra.getMoneda().getId());
		cf.setIdProveedor(compra.getProveedor().getId());
		cf.setIdSucursal(compra.getSucursal().getId());
		cf.setIdTipoMovimiento(compra.getTipoMovimiento().getId());
		cf.setImporteDs(compra.getTotalAsignadoDs());
		cf.setImporteGs(compra.getTotalAsignadoGs());
		cf.setMoneda((String) compra.getMoneda().getPos2());
		cf.setNumero(compra.getNumero());
		cf.setRazonSocial(compra.getProveedor().getRazonSocial());
		cf.setRuc(compra.getProveedor().getRuc());
		cf.setSucursal(compra.getSucursal().getText());
		cf.setTimbrado((String) compra.getTimbrado().getPos1());
		cf.setTipoCambio(compra.getTipoCambio());
		cf.setTipoMovimiento((String) compra.getTipoMovimiento().getPos1());
		cf.setVencimiento(compra.getFechaVencimiento());

		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(cf, getLogin());
	}

}

/**
 * Assembler del detalle..
 */
class AssemblerCompraLocalFacturaDetalle extends Assembler{

	private static String[] attIguales = {"costoGs", "costoDs", "importeExentaGs", "importeExentaDs", "importeGravadaGs", 
											"importeGravadaDs", "descuentoGs", "descuentoDs", "cantidad", "cantidadRecibida",
											"textoDescuento", "importeDescuentoGs", "importeDescuentoDs", "descuento"};
	private static String[] attArticulo = { "codigoInterno", "codigoProveedor", "codigoOriginal", "descripcion" };
	
	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		CompraLocalFacturaDetalle domain = (CompraLocalFacturaDetalle) getDomain(dto, CompraLocalFacturaDetalle.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "articulo");
		this.myPairToDomain(dto, domain, "tipoDescuento");
		this.myPairToDomain(dto, domain, "iva");
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		CompraLocalFacturaDetalleDTO dto = (CompraLocalFacturaDetalleDTO) getDTO(domain, CompraLocalFacturaDetalleDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "articulo", attArticulo);
		this.domainToMyPair(domain, dto, "tipoDescuento");
		this.domainToMyPair(domain, dto, "iva");
		
		return dto;
	}
	
}
