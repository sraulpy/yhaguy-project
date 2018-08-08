package com.yhaguy.gestion.bancos.depositos;

import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.BancoBoletaDeposito;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.RecaudacionCentral;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoCtaCte;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoMovimiento;

public class AssemblerBancoBoletaDeposito extends Assembler {

	private static String[] attIguales = { "fecha", "numeroBoleta", "monto",
			"totalEfectivo", "observacion", "planillaCaja", "cerrado" };
	
	private static String[] attCheques = { "fecha", "banco", "numero",
			"librado", "moneda", "monto", "depositado", "sucursalApp", "cliente", "recaudacionesCentral" };

	@Override
	public Domain dtoToDomain(DTO dtoG) throws Exception {

		BancoBoletaDepositoDTO dto = (BancoBoletaDepositoDTO) dtoG;
		BancoBoletaDeposito domain = (BancoBoletaDeposito) this.getDomain(dto,
				BancoBoletaDeposito.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myPairToDomain(dto, domain, "sucursalApp");
		this.addRecaudacionCentral(dto);
		this.listaMyArrayToListaDomain(dto, domain, "cheques");
		this.hijoDtoToHijoDomain(dto, domain, "bancoMovimiento", new AssemblerBancoMovimiento(), true);
		this.hijoDtoToHijoDomain(dto, domain, "nroCuenta", new AssemblerBancoCtaCte(), false);
		
		domain.setTotalImporte_gs(domain.getTotalImporteGs());

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {

		BancoBoletaDepositoDTO dto = (BancoBoletaDepositoDTO) getDTO(domain,
				BancoBoletaDepositoDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyPair(domain, dto, "sucursalApp");
		this.listaDomainToListaMyArray(domain, dto, "cheques", attCheques);
		this.hijoDomainToHijoDTO(domain, dto, "bancoMovimiento", new AssemblerBancoMovimiento());
		this.hijoDomainToHijoDTO(domain, dto, "nroCuenta", new AssemblerBancoCtaCte());

		return dto;
	}
	
	/**
	 * agrega las recaudaciones central y descuenta los saldos..
	 */
	@SuppressWarnings("unchecked")
	private void addRecaudacionCentral(BancoBoletaDepositoDTO dto) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (MyArray my : dto.getCheques()) {
			if (my.getPos11().equals("RCC")) {				
				BancoChequeTercero cheque = (BancoChequeTercero) rr.getObject(BancoChequeTercero.class.getName(), my.getId());				
				List<MyPair> rccs = (List<MyPair>) my.getPos10();
				for (MyPair myrcc : rccs) {
					RecaudacionCentral rcc = (RecaudacionCentral) rr.getObject(RecaudacionCentral.class.getName(), myrcc.getId());
					rcc.setNumeroDeposito(dto.getNumeroBoleta());
					rcc.setNumeroCheque(cheque.getNumero());
					Double importe = Double.parseDouble(myrcc.getSigla());
					rcc.setSaldoGs(rcc.getSaldoGs() - importe);
					rr.saveObject(rcc, this.getLogin());
					cheque.getRecaudacionesCentral().add(rcc);
				}
				rr.saveObject(cheque, this.getLogin());
				my.setId(cheque.getId());
			}
		}
	}
}
