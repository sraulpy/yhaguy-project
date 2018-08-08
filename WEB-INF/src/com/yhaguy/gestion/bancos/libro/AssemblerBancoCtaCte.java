package com.yhaguy.gestion.bancos.libro;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Register;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.BancoCta;

public class AssemblerBancoCtaCte extends Assembler {

	private static String[] attIguales = { "nroCuenta", "fechaApertura" };
	private static String[] attBanco = { "descripcion", "direccion",
			"telefono", "correo", "contacto", "sucursales", "bancoTipo" };
	private static String[] attMoneda = { "descripcion", "sigla" };
	private static String[] attCuentaContable = { "codigo", "descripcion",
			"alias" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		BancoCta domain = (BancoCta) getDomain(dto, BancoCta.class);

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myArrayToDomain(dto, domain, "banco");
		this.myPairToDomain(dto, domain, "tipo");
		this.myArrayToDomain(dto, domain, "moneda");
		this.myArrayToDomain(dto, domain, "cuentaContable");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		BancoCtaDTO dto = (BancoCtaDTO) getDTO(domain, BancoCtaDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyArray(domain, dto, "banco", attBanco);
		this.domainToMyPair(domain, dto, "tipo");
		this.domainToMyArray(domain, dto, "moneda", attMoneda);
		this.domainToMyArray(domain, dto, "cuentaContable", attCuentaContable);

		return dto;
	}

	/**
	 * Verifica el nro de la cuenta bancaria..
	 */
	public boolean verificarNroBancoCta(BancoCtaDTO bancoCta) throws Exception {
		Register rr = Register.getInstance();
		boolean existe = false;
		String query = "select bc from BancoCta bc where bc.nroCuenta='"
				+ bancoCta.getNroCuenta() + "' and bc.banco.descripcion='"
				+ bancoCta.getBancoDescripcion() + "' ";
		int cant = rr.hql(query).size();
		if (cant > 0) {
			existe = true;
		}
		return existe;
	}
}
