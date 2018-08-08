package com.yhaguy.gestion.bancos.libro;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.domain.IiD;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.BancoMovimiento;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.cheques.BancoChequeDTO;

public class AssemblerBancoMovimiento extends Assembler {

	// /??? private BancoCta nroCuenta;

	private static String[] attIguales = { "fecha", "monto", "nroReferencia",
			"descripcion", "anulado" };
	private static String[] attTipoMovimiento = { "descripcion", "sigla" };
	private static String[] attBancoCuenta = { "nroCuenta" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		BancoMovimiento domain = (BancoMovimiento) getDomain(dto,
				BancoMovimiento.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.myPairToDomain(dto, domain, "nroCuenta");

		// El cheque no se graba acá, porque el AsseblerBancoCheque ya lo hizo

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		BancoMovimientoDTO dto = (BancoMovimientoDTO) getDTO(domain,
				BancoMovimientoDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "tipoMovimiento", attTipoMovimiento);
		// setear el cheque propios si corresponde
		this.setCheque(dto);
		this.domainToMyPair(domain, dto, "nroCuenta", attBancoCuenta);

		return dto;
	}

	private void setCheque(BancoMovimientoDTO bm) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoCheque b = rr.getChequeDtoByMovimiento(bm.getId());

		if (b != null) {
			MyArray out = new MyArray();
			out.setId(b.getId());
			out.setPos1(b.getNumero());
			out.setPos2(b.getFechaEmision());
			out.setPos3(b.getFechaVencimiento());
			out.setPos4(b.getBeneficiario());
			out.setPos5(b.getConcepto());
			out.setPos6(this.tipoToMyPair(b.getEstadoComprobante()));
			out.setPos7(this.tipoToMyPair(b.getModoDeCreacion()));

				
			bm.setCheque(out);

			// AssemblerBancoCheque asCh = new AssemblerBancoCheque();
			// BancoChequeDTO chDto = (BancoChequeDTO) asCh.domainToDto(b);

			// BancoChequeDTO chDto = new BancoChequeDTO();

			// this.setCheque(bm, chDto);
		}
	}

	// Asigna el Cheque propio correspondiente al Movimiento del Banco
	// ver también el siguiente metodo
	// pos1:numero - pos2:emision - pos3:vencimiento - pos4:beneficiario -
	// pos5:concepto
	public void setCheque(BancoMovimientoDTO mov, BancoChequeDTO ch) {
		MyArray out = new MyArray();
		out.setId(ch.getId());
		out.setPos1(ch.getNumero());
		out.setPos2(ch.getFechaEmision());
		out.setPos3(ch.getFechaVencimiento());
		out.setPos4(ch.getBeneficiario());
		out.setPos5(ch.getConcepto());
		out.setPos6(ch.getEstadoComprobante());
		out.setPos7(ch.getModoDeCreacion());

		mov.setCheque(out);
		//mov.setChequeDto(ch);
		// System.out.println("Assembler banco ChequeDTO: "+
		// mov.getChequeDto());
	}

	public List<BancoMovimientoDTO> getMovimientos(IiD bancoCta)
			throws Exception {
		List<BancoMovimientoDTO> lOut = new ArrayList<>();

		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoMovimiento> l = rr.getBancoMovimientosTodos(bancoCta);
		for (int i = 0; i < l.size(); i++) {
			BancoMovimiento bm = l.get(i);
			lOut.add((BancoMovimientoDTO) this.domainToDto(bm));
		}

		return lOut;
	}

}