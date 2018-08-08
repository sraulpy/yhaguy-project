package com.yhaguy.gestion.caja.recibos;

import java.util.Date;
import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.gestion.contabilidad.retencion.RetencionIvaAssembler;
import com.yhaguy.gestion.empresa.ctacte.AssemblerCtaCteEmpresaMovimiento;

public class AssemblerRecibo extends Assembler {

	private static String[] attIguales = { "numero", "idUsuarioCarga",
			"nombreUsuarioCarga", "fechaEmision", "totalImporteGs",
			"totalImporteDs", "tipoCambio", "movimientoBancoActualizado",
			"motivoAnulacion", "cobroExterno", "tesaka", "numeroPlanilla", 
			"entregado", "numeroRecibo", "fechaRecibo", "nro", "cobrador" };
	
	private static String[] attEmpresa = { "codigoEmpresa", "razonSocial", "ruc", "idEmpresa" };	
	
	private static String[] attMoneda = { "descripcion", "sigla" };
	private static String[] attTipoMovimiento = { "descripcion", "sigla", };
	
	/**
	 * registra el recibo de pago..
	 */
	public static void registrarReciboPago(String numeroRecibo, Date fechaRecibo, long idOrdenPago, String user, boolean contado) 
		throws Exception {
		
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo pago = rr.getOrdenPagoById(idOrdenPago);
		pago.setNumeroRecibo(numeroRecibo);
		pago.setFechaRecibo(fechaRecibo);
		pago.setEntregado(true);
		rr.saveObject(pago, user);
		
		if (contado) {
			ControlCuentaCorriente.addReciboDePagoGastosContado(idOrdenPago, user);
		} else {
			ControlCuentaCorriente.addReciboDePago(idOrdenPago, user);
		}		
	}
	
	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		Recibo domain = (Recibo) getDomain(dto, Recibo.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myPairToDomain(dto, domain, "estadoComprobante");
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.myArrayToDomain(dto, domain, "proveedor");
		this.myArrayToDomain(dto, domain, "cliente");
		this.myArrayToDomain(dto, domain, "moneda");
		this.myPairToDomain(dto, domain, "sucursal");
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true, new AssemblerReciboDetalle());
		this.listaDTOToListaDomain(dto, domain, "formasPago", true, true, new AssemblerReciboFormaPago(domain.getNumero()));
		this.hijoDtoToHijoDomain(dto, domain, "retencion", new RetencionIvaAssembler(), true);
		
		this.updateGastos(domain);
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ReciboDTO dto = (ReciboDTO) getDTO(domain, ReciboDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyPair(domain, dto, "estadoComprobante");
		this.domainToMyArray(domain, dto, "tipoMovimiento", attTipoMovimiento);
		this.domainToMyArray(domain, dto, "proveedor", attEmpresa);
		this.domainToMyArray(domain, dto, "cliente", attEmpresa);
		this.domainToMyArray(domain, dto, "moneda", attMoneda);
		this.domainToMyPair(domain, dto, "sucursal");
		this.listaDomainToListaDTO(domain, dto, "detalles", new AssemblerReciboDetalle());
		this.listaDomainToListaDTO(domain, dto, "formasPago", new AssemblerReciboFormaPago(dto.getNumero()));	
		this.hijoDomainToHijoDTO(domain, dto, "retencion", new RetencionIvaAssembler());
		
		this.asignarEstadoComprobante(dto);
		
		return dto;
	}
	
	/**
	 * asigna el estado del comprobante..
	 */
	private void asignarEstadoComprobante(ReciboDTO recibo) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();		
		List<Tipo> list = rr.getTipos(Configuracion.ID_TIPO_ESTADO_COMPROBANTE);
		recibo.setEstadosComprobantes(this.listaTiposToListaMyPair(list));
	}
	
	/**
	 * actualiza los gastos..
	 */
	private void updateGastos(Recibo pago) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (ReciboDetalle det : pago.getDetalles()) {
			Gasto gasto = det.getGasto();
			if (gasto != null) {
				gasto.setNumeroOrdenPago(pago.getNumero());
				gasto.setCajaPagoNumero(pago.getNumeroPlanilla());
				rr.saveObject(gasto, this.getLogin());
			}
		}
	}
}


class AssemblerReciboDetalle extends Assembler {

	private static String[] attIgualesPagoDetalle = {"montoGs", "montoDs", "concepto"};
	
	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		ReciboDetalle domain = (ReciboDetalle) getDomain(dto, ReciboDetalle.class);
		ReciboDetalleDTO dto_ = (ReciboDetalleDTO) dto;
		
		this.copiarValoresAtributos(dto, domain, attIgualesPagoDetalle);
		if (dto_.getMovimiento() != null) {
			this.hijoDtoToHijoDomain(dto, domain, "movimiento", new AssemblerCtaCteEmpresaMovimiento(), false);
		}		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ReciboDetalleDTO dto = (ReciboDetalleDTO) getDTO(domain, ReciboDetalleDTO.class);
		ReciboDetalle domain_ = (ReciboDetalle) domain;
		
		this.copiarValoresAtributos(domain, dto, attIgualesPagoDetalle);
		if (domain_.getMovimiento() != null) {
			this.hijoDomainToHijoDTO(domain, dto, "movimiento", new AssemblerCtaCteEmpresaMovimiento());
		}		
		return dto;
	}	
}
