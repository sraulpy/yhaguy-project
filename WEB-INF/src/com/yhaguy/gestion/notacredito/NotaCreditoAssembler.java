package com.yhaguy.gestion.notacredito;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.compras.timbrado.WindowTimbrado;
import com.yhaguy.gestion.comun.ControlArticuloStock;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;

public class NotaCreditoAssembler extends Assembler {

	static String[] attIguales = { "numero", "timbrado_", "observacion", "fechaEmision",
			"importeGs", "importeDs", "importeIva", "tipoCambio", "enlace",
			"cajaNro", "planillaCajaNro", "cajero" };
	static String[] attTipoMovimiento = { "descripcion", "sigla", "clase", "tipoIva" };
	static String[] attEmpresa = { "codigoEmpresa", "razonSocial", "ruc", "idEmpresa", "direccion", "telefono" };
	static String[] attTipos = { "descripcion", "sigla" };	
	static String[] attTimbrado = { "numero", "vencimiento" };
	static String[] attServicioTecnico = { "numero" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		NotaCredito domain = (NotaCredito) getDomain(dto, NotaCredito.class);
		NotaCreditoDTO _dto = (NotaCreditoDTO) dto;

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myPairToDomain(dto, domain, "motivo");
		this.myPairToDomain(dto, domain, "sucursal");
		this.myPairToDomain(dto, domain, "estadoComprobante");
		this.myPairToDomain(dto, domain, "moneda");
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.myArrayToDomain(dto, domain, "cliente");
		this.myArrayToDomain(dto, domain, "proveedor");	
		this.listaMyArrayToListaDomain(dto, domain, "serviciosTecnicos");
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true,
				new NotaCreditoDetalleAssembler());
		
		// asignacion del timbrado..
		if((_dto.getTimbrado().esNuevo()) && (!_dto.isNotaCreditoVenta()))
			this.saveTimbrado(_dto);
		this.myArrayToDomain(dto, domain, "timbrado");		
		
		// Actualizacion de datos de cta cte, stock, costos
		if(_dto.isActualizarDatos() == true && !domain.isNotaCreditoGastoCajaChica()) {
			this.actualizarCtaCte(_dto);
			this.actualizarStock(_dto);
			this.actualizarCostos(_dto);
		}
		
		// Si es nc de gasto caja chica enlaza con la caja correspondiente
		if (_dto.isActualizarDatos() == true && domain.isNotaCreditoGastoCajaChica()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			Gasto gasto = domain.getDetallesFacturas().get(0).getGasto();
			CajaPeriodo caja = rr.getCajaPlanillas(gasto.getCajaPagoNumero()).get(0);
			if (caja != null) {
				caja.getNotasCredito().add(domain);
				rr.saveObject(caja, this.getLogin());
			}
		}
		
		domain.setVendedor(domain.getVendedor_());
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		NotaCreditoDTO dto = (NotaCreditoDTO) getDTO(domain, NotaCreditoDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyPair(domain, dto, "motivo");
		this.domainToMyPair(domain, dto, "sucursal");
		this.domainToMyPair(domain, dto, "estadoComprobante");
		this.domainToMyPair(domain, dto, "moneda");
		this.domainToMyArray(domain, dto, "tipoMovimiento", attTipoMovimiento);
		this.domainToMyArray(domain, dto, "cliente", attEmpresa);
		this.domainToMyArray(domain, dto, "proveedor", attEmpresa);
		this.domainToMyArray(domain, dto, "vendedor", attEmpresa);
		this.domainToMyArray(domain, dto, "timbrado", attTimbrado);
		this.listaDomainToListaMyArray(domain, dto, "serviciosTecnicos", attServicioTecnico);
		this.listaDomainToListaDTO(domain, dto, "detalles", new NotaCreditoDetalleAssembler());

		return dto;
	}
	
	/**
	 * actualiza los datos de cta cte..
	 */
	private void actualizarCtaCte(NotaCreditoDTO nc) throws Exception {
		if (nc.isNotaCreditoCompra()) {
			ControlCuentaCorriente.addNotaCreditoCompra(nc, this.getLogin());
		}
	}

	/**
	 * actualiza los datos del stock..
	 */
	private void actualizarStock(NotaCreditoDTO nc) throws Exception {
		if (nc.isMotivoDevolucion() == false) {
			return;
		}
		for (NotaCreditoDetalleDTO item : nc.getDetallesArticulos()) {
			MyArray articulo = item.getArticulo();
			long cantidad = item.getCantidad();
			ControlArticuloStock.actualizarStock(articulo.getId(), item.getDeposito().getId(), cantidad * -1, this.getLogin());
		}		
	}
	
	/**
	 * actualiza los datos de cta cte..
	 */
	private void actualizarCostos(NotaCreditoDTO nc) {
	}
	
	/**
	 * Graba el timbrado asignado..
	 */
	private void saveTimbrado(NotaCreditoDTO nc) throws Exception {
		WindowTimbrado wt = new WindowTimbrado();
		wt.agregarTimbrado(nc.getTimbrado(), nc.getProveedor().getId());
	}
}

/**
 * Assembler del detalle..
 */
class NotaCreditoDetalleAssembler extends Assembler {

	static final String[] ATT_IGUALES = { "cantidad", "costoGs", "montoGs", "montoDs", "importeGs", "importeDs" };
	static final String[] ATT_VENTA = { "descripcionTipoMovimiento", "numero", "fecha", "tipoMovimiento", "siglaTipoMovimiento", "descripcionCondicion" };	
	static final String[] ATT_COMPRA = { "descripcionTipoMovimiento", "numero", "fechaCreacion", "tipoMovimiento" };
	static final String[] ATT_ARTICULO = { "descripcion", "codigoInterno" };
	static final String[] ATT_GASTO = { "descripcionTipoMovimiento", "numeroFactura", "fecha", "tipoMovimiento" };
	static final String[] ATT_IMPORTACION = { "descripcionTipoMovimiento", "numero", "fechaOriginal", "tipoMovimiento" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		NotaCreditoDetalle domain = (NotaCreditoDetalle) getDomain(dto,
				NotaCreditoDetalle.class);

		this.copiarValoresAtributos(dto, domain, ATT_IGUALES);
		this.myPairToDomain(dto, domain, "tipoIva");
		this.myPairToDomain(dto, domain, "tipoDetalle");
		this.myPairToDomain(dto, domain, "deposito");
		this.myArrayToDomain(dto, domain, "articulo");
		this.myArrayToDomain(dto, domain, "venta");
		this.myArrayToDomain(dto, domain, "gasto");
		this.myArrayToDomain(dto, domain, "compra");
		this.myArrayToDomain(dto, domain, "importacion");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		NotaCreditoDetalleDTO dto = (NotaCreditoDetalleDTO) getDTO(domain,
				NotaCreditoDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, ATT_IGUALES);
		this.domainToMyPair(domain, dto, "tipoIva");
		this.domainToMyPair(domain, dto, "tipoDetalle");
		this.domainToMyPair(domain, dto, "deposito");
		this.domainToMyArray(domain, dto, "articulo", ATT_ARTICULO);
		this.domainToMyArray(domain, dto, "venta", ATT_VENTA);
		this.domainToMyArray(domain, dto, "gasto", ATT_GASTO);
		this.domainToMyArray(domain, dto, "compra", ATT_COMPRA);
		this.domainToMyArray(domain, dto, "importacion", ATT_IMPORTACION);

		return dto;
	}
}