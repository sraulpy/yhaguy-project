package com.yhaguy.gestion.caja.principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.Caja;
import com.yhaguy.domain.Funcionario;

public class AssemblerCaja extends Assembler{

	static String[] attIguales = { "numero", "descripcion", "fecha", "fondo",
			"cobro", "reposicion", "facturacion", "pago", "gasto", "egreso",
			"notaCredito" };
	static String[] attTalonario = { "numero", "bocaExpedicion",
			"puntoExpedicion", "desde", "hasta", "timbrado" };
 	
	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		Caja domain = (Caja) getDomain(dto, Caja.class);
		
		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myPairToDomain(dto, domain, "clasificacion");
		this.myPairToDomain(dto, domain, "tipo");
		this.myPairToDomain(dto, domain, "estado");
		this.myPairToDomain(dto, domain, "duracion");
		this.myPairToDomain(dto, domain, "sucursal");
		this.myArrayToDomain(dto, domain, "creador");
		this.myArrayToDomain(dto, domain, "responsable");
		this.myArrayToDomain(dto, domain, "talonarioVentas");
		this.myArrayToDomain(dto, domain, "talonarioNotasCredito");
		this.myArrayToDomain(dto, domain, "talonarioAutoFacturas");
		this.myArrayToDomain(dto, domain, "talonarioRecibos");
		this.myArrayToDomain(dto, domain, "talonarioRetenciones");
		this.listaMyArrayToListaDomain(dto, domain, "supervisores");
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		CajaDTO dto = (CajaDTO) getDTO(domain, CajaDTO.class);
		Caja dom = (Caja) domain;
		
		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyPair(domain, dto, "clasificacion");
		this.domainToMyPair(domain, dto, "tipo");
		this.domainToMyPair(domain, dto, "estado");
		this.domainToMyPair(domain, dto, "duracion");
		this.domainToMyPair(domain, dto, "sucursal");
		this.domainToMyArray(domain, dto, "talonarioVentas", attTalonario);
		this.domainToMyArray(domain, dto, "talonarioNotasCredito", attTalonario);
		this.domainToMyArray(domain, dto, "talonarioAutoFacturas", attTalonario);
		this.domainToMyArray(domain, dto, "talonarioRecibos", attTalonario);
		this.domainToMyArray(domain, dto, "talonarioRetenciones", attTalonario);
		dto.setCreador(this.getFuncionario(dom.getCreador()));
		dto.setResponsable(this.getFuncionario(dom.getResponsable()));
		dto.setSupervisores(this.getSupervisores(dom.getSupervisores()));
		
		return dto;
	}

	private List<MyArray> getSupervisores(Set<Funcionario> supervisores){
		List<MyArray> out = new ArrayList<MyArray>();
		for (Funcionario f : supervisores) {
			MyArray s = new MyArray();
			s.setId(f.getId());
			s.setPos1(f.getEmpresa().getNombre());
			out.add(s);
		}
		return out;
	}
	
	private MyArray getFuncionario(Funcionario fun){
		MyArray out = new MyArray();
		out.setId(fun.getId()); out.setPos1(fun.getEmpresa().getNombre());
		return out;
	}
}
