package com.yhaguy.gestion.compras.gastos.subdiario;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.GastoDetalle;
import com.yhaguy.gestion.caja.recibos.AssemblerReciboFormaPago;
import com.yhaguy.gestion.compras.timbrado.WindowTimbrado;

public class AssemblerGasto extends Assembler {

	final static String[] ATT_IGUALES = { "fecha", "vencimiento",
			"numeroFactura", "numeroTimbrado", "tipoCambio", "totalAsignado",
			"totalIvaAsignado", "existeComprobanteFisico",
			"motivoComprobanteFisico", "cajaPagoNumero", "observacion",
			"beneficiario", "idImportacion", "importeGs", "importeDs",
			"importeIva10", "importeIva5", "numeroImportacion", "despachante",
			"debitoBancario", "no_generar_saldo" };

	final static String[] ATT_PROVEEDOR = { "codigoEmpresa", "razonSocial", "ruc" };
	final static String[] ATT_MONEDA = { "descripcion", "sigla" };
	final static String[] ATT_TIMBRADO = { "numero", "vencimiento" };
	final static String[] ATT_CONDICION = { "descripcion", "plazo" };
	final static String[] ATT_TIPO_MOVIMIENTO = { "descripcion", "sigla", "clase", "tipoIva" };
	final static String[] ATT_BANCO = { "bancoDescripcion" };

	@Override
	public Domain dtoToDomain(DTO dtoG) throws Exception {
		GastoDTO dto = (GastoDTO) dtoG;
		Gasto domain = (Gasto) getDomain(dto, Gasto.class);

		if (dto.getTimbrado().esNuevo())
			this.saveTimbrado(dto);

		this.copiarValoresAtributos(dto, domain, ATT_IGUALES);
		this.myArrayToDomain(dto, domain, "proveedor");
		this.myArrayToDomain(dto, domain, "moneda");
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.myArrayToDomain(dto, domain, "timbrado");
		this.myArrayToDomain(dto, domain, "condicionPago");
		this.myArrayToDomain(dto, domain, "banco");
		this.myPairToDomain(dto, domain, "estadoComprobante");
		this.myPairToDomain(dto, domain, "sucursal");
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true, new AssemblerGastoDetalle());		
		this.listaDTOToListaDomain(dto, domain, "formasPago", true, true, new AssemblerReciboFormaPago(dto.getNumeroFactura()));
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		GastoDTO dto = (GastoDTO) getDTO(domain, GastoDTO.class);

		this.copiarValoresAtributos(domain, dto, ATT_IGUALES);
		this.domainToMyArray(domain, dto, "proveedor", ATT_PROVEEDOR);
		this.domainToMyArray(domain, dto, "moneda", ATT_MONEDA);
		this.domainToMyArray(domain, dto, "tipoMovimiento", ATT_TIPO_MOVIMIENTO);
		this.domainToMyArray(domain, dto, "timbrado", ATT_TIMBRADO);
		this.domainToMyArray(domain, dto, "condicionPago", ATT_CONDICION);
		this.domainToMyArray(domain, dto, "banco", ATT_BANCO);
		this.domainToMyPair(domain, dto, "estadoComprobante");
		this.domainToMyPair(domain, dto, "sucursal");
		this.listaDomainToListaDTO(domain, dto, "detalles", new AssemblerGastoDetalle());
		this.listaDomainToListaDTO(domain, dto, "formasPago", new AssemblerReciboFormaPago(dto.getNumeroFactura()));

		return dto;
	}

	/**
	 * graba el timbrado en la bd..
	 */
	private void saveTimbrado(GastoDTO dto) {
		WindowTimbrado w = new WindowTimbrado();
		w.agregarTimbrado(dto.getTimbrado(), dto.getProveedor().getId());
	}
}

class AssemblerGastoDetalle extends Assembler {

	private static String[] attIguales = {"observacion", "cantidad", "montoGs", "montoDs", "montoIva"};
	private static String[] attCentroCosto = {"numero", "descripcion", "montoAsignado"};
	private static String[] attTipoIva = {"descripcion", "sigla"};
	
	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		GastoDetalle domain = (GastoDetalle) getDomain(dto, GastoDetalle.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.hijoDtoToHijoDomain(dto, domain, "articuloGasto", new AssemblerArticuloGasto(), false);
		this.myArrayToDomain(dto, domain, "centroCosto");
		this.myArrayToDomain(dto, domain, "tipoIva");
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		GastoDetalleDTO dto = (GastoDetalleDTO) getDTO(domain, GastoDetalleDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.hijoDomainToHijoDTO(domain, dto, "articuloGasto", new AssemblerArticuloGasto());
		this.domainToMyArray(domain, dto, "centroCosto", attCentroCosto);
		this.domainToMyArray(domain, dto, "tipoIva", attTipoIva);
		
		return dto;
	}	
}
