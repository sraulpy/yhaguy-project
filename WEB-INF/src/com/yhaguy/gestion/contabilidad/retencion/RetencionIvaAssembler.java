package com.yhaguy.gestion.contabilidad.retencion;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.RetencionIva;
import com.yhaguy.domain.RetencionIvaDetalle;

public class RetencionIvaAssembler extends Assembler {

	static String[] attIguales = { "fecha", "numero", "montoIvaIncluido",
			"montoIva", "montoRetencion", "porcentaje" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		RetencionIva domain = (RetencionIva) this.getDomain(dto,
				RetencionIva.class);
		RetencionIvaDTO dto_ = (RetencionIvaDTO) dto;

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myPairToDomain(dto, domain, "estadoComprobante");
		this.myArrayToDomain(dto, domain, "empresa");
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true,
				new RetencionIvaDetalleAssembler(dto_.getPorcentaje()));

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		RetencionIvaDTO dto = (RetencionIvaDTO) this.getDTO(domain,
				RetencionIvaDTO.class);
		RetencionIva dom = (RetencionIva) domain;

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyPair(domain, dto, "estadoComprobante");
		this.domainToMyArray(domain, dto, "empresa", new String[]{});
		this.listaDomainToListaDTO(domain, dto, "detalles",
				new RetencionIvaDetalleAssembler(dom.getPorcentaje()));
		
		dto.setProveedor(this.getProveedor(dom.getEmpresa()));

		return dto;
	}
	
	/**
	 * @return myarray del proveedor..
	 */
	private MyArray getProveedor(Empresa empresa) {
		
		MyArray out = new MyArray();
		out.setId(empresa.getId());
		out.setPos1(empresa.getCodigoEmpresa() == null? "" : empresa.getCodigoEmpresa());
		out.setPos2(empresa.getRazonSocial());
		out.setPos3(empresa.getRuc());
		out.setPos4(empresa.getId());
		
		return out;
	}

}

/**
 * Assembler del detalle..
 */
class RetencionIvaDetalleAssembler extends Assembler {

	static String[] attIguales = { "importeIvaGs" };
	
	static String[] attGasto = { "tipoMovimiento", "numeroFactura",
		"fecha", "importeGs", "importeIva5", "importeIva10" };
	
	static String[] attCompra = { "tipoMovimiento", "numero",
		"fechaOriginal", "importeGs", "importeIva5", "importeIva10" };
	
	private int porcentaje = Configuracion.PORCENTAJE_RETENCION;
	
	public RetencionIvaDetalleAssembler(int porcentaje) {
		this.porcentaje = porcentaje;
	}

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		RetencionIvaDetalle domain = (RetencionIvaDetalle) this.getDomain(dto,
				RetencionIvaDetalle.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "gasto");
		this.myArrayToDomain(dto, domain, "compra");
		
		this.actualizarGasto(domain.getGasto());
		this.actualizarCompra(domain.getCompra());

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		RetencionIvaDetalleDTO dto = (RetencionIvaDetalleDTO) this.getDTO(
				domain, RetencionIvaDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "gasto", attGasto);
		this.domainToMyArray(domain, dto, "compra", attCompra);
		
		dto.setPorcentaje(this.porcentaje);

		return dto;
	}
	
	/**
	 * actualiza el gasto..marca como retenido..
	 */
	private void actualizarGasto(Gasto gasto) throws Exception {
		if(gasto == null)
			return;
		RegisterDomain rr = RegisterDomain.getInstance();
		gasto.setIvaRetenido(true);
		rr.saveObject(gasto, this.getLogin());
	}
	
	/**
	 * actualiza la compra..marca como retenido..
	 */
	private void actualizarCompra(CompraLocalFactura compra) throws Exception {
		if(compra == null)
			return;
		RegisterDomain rr = RegisterDomain.getInstance();
		compra.setIvaRetenido(true);
		rr.saveObject(compra, this.getLogin());
	}
}
