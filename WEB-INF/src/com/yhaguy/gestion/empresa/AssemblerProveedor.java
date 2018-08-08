package com.yhaguy.gestion.empresa;


import java.util.*;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

import com.yhaguy.Configuracion;
import com.yhaguy.domain.Proveedor;

public class AssemblerProveedor extends Assembler {

	protected static String[] camposCtaCtb = {"codigo", "descripcion", "alias"};
	
	private static String[] attIgualesProveedor = { "prioridad", "completo" };
	
	@Override
	public Domain dtoToDomain(DTO dtog) throws Exception {

		ProveedorDTO dto = (ProveedorDTO)dtog;
		Proveedor domain = (Proveedor) getDomain(dto, Proveedor.class);

		this.copiarValoresAtributos(dto, domain, attIgualesProveedor);
		
		this.myPairToDomain(dto, domain, "estadoProveedor");
		this.myPairToDomain(dto, domain, "tipoProveedor");
		this.myArrayToDomain(dto, domain, "cuentaContable");
		this.hijoDtoToHijoDomain(dto, domain, "empresa", new AssemblerEmpresa(), true);
		
		// lista de los emails
		String emailLista = "";
		
		List<MyArray> ht = dto.getEmails();
		for (Iterator em = ht.iterator(); em.hasNext();) {
			MyArray ma = (MyArray) em.next();
			MyPair mp = (MyPair)ma.getPos1();
			String eml = (String)ma.getPos2();
			emailLista += mp.getId()+":"+eml+"|";
		}
		domain.setEmailsLista(emailLista);
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain dom) throws Exception {
		
		Proveedor domain = (Proveedor) dom;
		ProveedorDTO dto = (ProveedorDTO) getDTO(domain, ProveedorDTO.class);

		this.copiarValoresAtributos(domain, dto, attIgualesProveedor);
		
		this.domainToMyPair(domain, dto, "estadoProveedor");
		this.domainToMyPair(domain, dto, "tipoProveedor");
		this.domainToMyArray(domain, dto, "cuentaContable", camposCtaCtb);
		this.hijoDomainToHijoDTO(domain, dto, "empresa", new AssemblerEmpresa());		
		
		List<MyArray> ht = new ArrayList<MyArray>();
		String emails = domain.getEmailsLista();
		if ((emails != null) && (emails.trim().length() != 0)){
			String[] ems = emails.split("|");
			for (int i = 0; i < ems.length; i++) {
				String es = ems[i];
				String[] e = es.split(":");
				Long id = new Long(e[0]);
				String email = e[1];
				MyPair t = this.getTipo(Configuracion.ID_TIPO_EMPRESA_EMAILS, id);
				MyArray ma = new MyArray(t, email);
				ht.add(ma);
			}
		}
		dto.setEmails(ht);
		
		return dto;
	}	
}
