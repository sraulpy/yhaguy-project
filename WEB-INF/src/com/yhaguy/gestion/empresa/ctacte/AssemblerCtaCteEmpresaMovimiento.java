package com.yhaguy.gestion.empresa.ctacte;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.RegisterDomain;

public class AssemblerCtaCteEmpresaMovimiento extends Assembler {

	private String[] attIgualesMovimiento = { "idEmpresa", "fechaEmision", "idMovimientoOriginal", "nroComprobante",
			"fechaVencimiento", "importeOriginal", "saldo", "tipoCambio", "cerrado", "anulado", "tesaka", "idVendedor", "numeroImportacion" };

	private String[] attSucursal = { "nombre", "descripcion" };

	private String[] attTipoMovimiento = { "descripcion", "sigla", "clase", "TipoIva", "TipoEmpresa", "TipoComprobante",
			"tipoDocumento" };

	@Override
	public Domain dtoToDomain(DTO dtog) throws Exception {

		CtaCteEmpresaMovimientoDTO dto = (CtaCteEmpresaMovimientoDTO) dtog;
		CtaCteEmpresaMovimiento domain = (CtaCteEmpresaMovimiento) getDomain(dto, CtaCteEmpresaMovimiento.class);
		this.copiarValoresAtributos(dto, domain, attIgualesMovimiento);
		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.myPairToDomain(dto, domain, "tipoCaracterMovimiento");
		this.myPairToDomain(dto, domain, "moneda");
		this.myArrayToDomain(dto, domain, "sucursal");
		this.listaDTOToListaDomain(dto, domain, "imputaciones", false, false, new AssemblerCtaCteImputacion());

		return domain;

	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {

		CtaCteEmpresaMovimientoDTO dto = (CtaCteEmpresaMovimientoDTO) getDTO(domain, CtaCteEmpresaMovimientoDTO.class);
		this.copiarValoresAtributos(domain, dto, attIgualesMovimiento);
		this.domainToMyArray(domain, dto, "tipoMovimiento", attTipoMovimiento);
		this.domainToMyPair(domain, dto, "tipoCaracterMovimiento");
		this.domainToMyPair(domain, dto, "moneda");
		this.domainToMyArray(domain, dto, "sucursal", attSucursal);
		this.listaDomainToListaDTO(domain, dto, "imputaciones", new AssemblerCtaCteImputacion());
		
		RegisterDomain rr = RegisterDomain.getInstance();
		Empresa empresa = rr.getEmpresaById(dto.getIdEmpresa());
		if (empresa != null) {
			dto.setTelefono(empresa.getTelefono());
			dto.setDireccion(empresa.getDireccion());
			dto.setRuc(empresa.getRuc());
		}

		return dto;

	}

	public Tipo MyPairToTipo(MyPair m) {
		if (m == null) {
			return null;
		}
		Tipo t = new Tipo();
		t.setId(m.getId());
		t.setSigla(m.getSigla());
		t.setDescripcion(m.getText());
		return t;
	}

}
