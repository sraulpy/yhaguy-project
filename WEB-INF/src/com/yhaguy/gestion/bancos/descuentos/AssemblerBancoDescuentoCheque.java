package com.yhaguy.gestion.bancos.descuentos;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.BancoDescuentoCheque;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.cheques.AssemblerBancoCheque;
import com.yhaguy.gestion.caja.recibos.AssemblerReciboFormaPago;

public class AssemblerBancoDescuentoCheque extends Assembler {

	static final String[] ATT_IGUALES = { "fecha", "totalChequesDescontado",
			"observacion", "liq_impuestos", "liq_gastos_adm", "liq_intereses",
			"liq_neto_aldia", "liq_neto_diferidos", "liq_registrado", "confirmado" };
	
	static final String[] ATT_CHEQUES = { "fecha", "banco", "numero",
			"librado", "monto", "montoAcreditado", "depositado", "sucursalApp", "descontado" };
	
	static final String[] ATT_BANCO = { "banco" };
	
	static final String[] ATT_ACREEDOR = { "ruc", "razonSocial" };

	@Override
	public Domain dtoToDomain(DTO dtoG) throws Exception {

		BancoDescuentoChequeDTO dto = (BancoDescuentoChequeDTO) dtoG;
		BancoDescuentoCheque domain = (BancoDescuentoCheque) this.getDomain(dto, BancoDescuentoCheque.class);

		this.copiarValoresAtributos(dto, domain, ATT_IGUALES);
		this.myPairToDomain(dto, domain, "sucursalApp");
		this.myPairToDomain(dto, domain, "moneda");
		this.myArrayToDomain(dto, domain, "banco");
		this.myArrayToDomain(dto, domain, "acreedor");
		this.listaDTOToListaDomain(dto, domain, "chequesPropios", false, false, new AssemblerBancoCheque());
		this.listaDTOToListaDomain(dto, domain, "formasPago", true, true, new AssemblerReciboFormaPago(""));
		this.listaMyArrayToListaDomain(dto, domain, "cheques", ATT_CHEQUES, true, true);
		this.listaMyArrayToListaDomain(dto, domain, "cheques_", ATT_CHEQUES, true, true);
		
		domain.setTotalImporte_gs(dto.getTotalImporteAcreditado());

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {

		BancoDescuentoChequeDTO dto = (BancoDescuentoChequeDTO) getDTO(domain, BancoDescuentoChequeDTO.class);

		this.copiarValoresAtributos(domain, dto, ATT_IGUALES);
		this.domainToMyPair(domain, dto, "sucursalApp");
		this.domainToMyPair(domain, dto, "moneda");
		this.domainToMyArray(domain, dto, "banco", ATT_BANCO);
		this.domainToMyArray(domain, dto, "acreedor", ATT_ACREEDOR);
		this.listaDomainToListaMyArray(domain, dto, "cheques", ATT_CHEQUES);
		this.listaDomainToListaMyArray(domain, dto, "cheques_", ATT_CHEQUES);
		this.listaDomainToListaDTO(domain, dto, "chequesPropios", new AssemblerBancoCheque());
		this.listaDomainToListaDTO(domain, dto, "formasPago", new AssemblerReciboFormaPago(""));
		
		RegisterDomain rr = RegisterDomain.getInstance();
		for (MyArray cheque : dto.getCheques()) {
			BancoChequeTercero ch = (BancoChequeTercero) rr.getObject(BancoChequeTercero.class.getName(), cheque.getId());
			if (ch != null) {
				cheque.setPos10(ch.getCliente().getRazonSocial());
			}
		}
		for (MyArray cheque : dto.getCheques_()) {
			BancoChequeTercero ch = (BancoChequeTercero) rr.getObject(BancoChequeTercero.class.getName(), cheque.getId());
			if (ch != null) {
				cheque.setPos10(ch.getCliente().getRazonSocial());
			}
		}
		return dto;
	}

}
