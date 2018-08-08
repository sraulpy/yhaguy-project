package com.yhaguy.gestion.empresa;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Register;

import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.AccesoApp;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.inicio.AssemblerAcceso;

public class AssemblerFuncionario extends Assembler {

	public static String[] attIguales = { "correoFuncionario" };

	// para recuperar los contactos internos
	public static String query = " select ci.id, tipo.descripcion, cli.empresa.nombre"
			+ " from  Cliente cli join cli.contactosInternos ci join ci.tipoContactoInterno tipo "
			+ " where ci.funcionario.id = ? ";

	public static String queryAccesos = " select ac from Funcionario fu join fu.accesos ac "
			+ " where ac in elements(fu.accesos) and fu.id = ? ";

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		Funcionario domain = (Funcionario) getDomain(dto, Funcionario.class);
		FuncionarioDTO dtoFun = (FuncionarioDTO) dto;
		dtoFun.pasaDtoToSucursal();

		this.copiarValoresAtributos(dto, domain, attIguales);
		this.myPairToDomain(dto, domain, "funcionarioEstado");
		this.myPairToDomain(dto, domain, "funcionarioCargo");
		this.hijoDtoToHijoDomain(dto, domain, "empresa", new AssemblerEmpresa(), true);
		this.listaDTOToListaDomain(dtoFun, domain, "accesos", true, true, new AssemblerAcceso());

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		FuncionarioDTO dto = (FuncionarioDTO) getDTO(domain, FuncionarioDTO.class);

		this.copiarValoresAtributos(domain, dto, attIguales);
		this.domainToMyPair(domain, dto, "funcionarioEstado");
		this.domainToMyPair(domain, dto, "funcionarioCargo");

		this.hijoDomainToHijoDTO(domain, dto, "empresa", new AssemblerEmpresa());

		if (dto.getEmpresa().getSucursales().size() == 0) {
			dto.getEmpresa().getSucursales().add(new MyArray());
		}
		dto.pasaSucursalToDto();

		// levantar los contactos internos
		Register rr = Register.getInstance();
		List<Object[]> li = (List<Object[]>) rr.hql(query, domain.getId());
		for (int i = 0; i < li.size(); i++) {
			Object[] ao = li.get(i);
			long id = (long) ao[0];
			String texto = ao[1] + " - " + ao[2];
			MyPair mp = new MyPair();
			mp.setId(id);
			mp.setText(texto);
			dto.getContactosInternos().add(mp);
		}

		/*-------------------- Manejo de accesos del funcionario --------------------*/

		List<AccesoApp> listAccesos = (List<AccesoApp>) rr.hql(queryAccesos, domain.getId());
		List<AccesoDTO> accesosDto = new ArrayList<AccesoDTO>();

		AssemblerAcceso as = new AssemblerAcceso();

		for (Iterator iterator = listAccesos.iterator(); iterator.hasNext();) {
			AccesoApp accesoApp = (AccesoApp) iterator.next();
			accesosDto.add((AccesoDTO) as.domainToDto(accesoApp));
		}
		dto.setAccesos(accesosDto);
		return dto;
	}

}
