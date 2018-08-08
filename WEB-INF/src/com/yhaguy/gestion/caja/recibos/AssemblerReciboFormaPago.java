package com.yhaguy.gestion.caja.recibos;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.ReciboFormaPago;


public class AssemblerReciboFormaPago extends Assembler {
	
	String numeroComprobante;
	
	public AssemblerReciboFormaPago(String numeroComprobante) {
		this.numeroComprobante = numeroComprobante;
	}

	private static String[] attIgualesFormaPago = { "montoGs", "montoDs",
			"montoChequeGs", "descripcion", "tarjetaNumero",
			"tarjetaNumeroComprobante", "tarjetaCuotas", "chequeFecha",
			"chequeNumero", "chequeLibrador", "depositoNroReferencia",
			"retencionNumero", "retencionTimbrado", "retencionVencimiento",
			"chequeBancoDescripcion", "reciboDebitoNro" };
	
	private static String[] attDepositoBancoCta = { "nroCuenta", "banco", "tipo","moneda" };
	
	@Override
	public Domain dtoToDomain(DTO dto2) throws Exception {
		ReciboFormaPagoDTO dto = (ReciboFormaPagoDTO) dto2;
		ReciboFormaPago domain = (ReciboFormaPago) getDomain(dto, ReciboFormaPago.class);
		
		domain.setNroComprobanteAsociado(this.numeroComprobante);
		
		this.copiarValoresAtributos(dto, domain, attIgualesFormaPago);
		this.myPairToDomain(dto, domain, "tipo");
		this.myPairToDomain(dto, domain, "tarjetaTipo");
		this.myPairToDomain(dto, domain, "chequeBanco");
		this.myPairToDomain(dto, domain, "moneda");
		this.myArrayToDomain(dto, domain, "tarjetaProcesadora");
		this.myArrayToDomain(dto, domain, "depositoBancoCta");
		
		if (dto.isChequePropio() || dto.isChequeTercero()) {
			domain.setMontoChequeGs(dto.getMontoGs());
		}
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ReciboFormaPagoDTO dto = (ReciboFormaPagoDTO) getDTO(domain, ReciboFormaPagoDTO.class);
		
		this.copiarValoresAtributos(domain, dto, attIgualesFormaPago);
		this.domainToMyPair(domain, dto, "tipo");
		this.domainToMyPair(domain, dto, "tarjetaTipo");
		this.domainToMyPair(domain, dto, "chequeBanco");
		this.domainToMyPair(domain, dto, "moneda");
		this.setProcesadora((ReciboFormaPago) domain, dto);

		this.domainToMyArray(domain, dto, "depositoBancoCta", attDepositoBancoCta);
		
		return dto;
	}	
	
	/**
	 * Setea la Procesadora..
	 * @param domain
	 * @param dto
	 */
	private void setProcesadora(ReciboFormaPago domain, ReciboFormaPagoDTO dto) {
		
		if (domain.getTarjetaProcesadora() == null)
			return;
		
		MyPair suc = new MyPair();
		suc.setId(domain.getTarjetaProcesadora().getSucursal().getId());
		suc.setText(domain.getTarjetaProcesadora().getSucursal().getNombre());
		
		MyPair bco = new MyPair();
		bco.setId(domain.getTarjetaProcesadora().getBanco().getId());
		bco.setText(domain.getTarjetaProcesadora().getBanco().getBancoDescripcion());
				
		MyArray pr = new MyArray();
		pr.setId(domain.getTarjetaProcesadora().getId());
		pr.setPos1(domain.getTarjetaProcesadora().getNombre());
		pr.setPos2(suc);
		pr.setPos3(bco);
		
		dto.setTarjetaProcesadora(pr);
	}	
}