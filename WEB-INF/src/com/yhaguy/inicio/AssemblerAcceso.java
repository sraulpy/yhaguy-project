package com.yhaguy.inicio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.AccesoApp;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;

public class AssemblerAcceso extends Assembler {

	@SuppressWarnings("rawtypes")
	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		AccesoApp dom = (AccesoApp) getDomain(dto, AccesoApp.class);
		AccesoDTO dtoA = (AccesoDTO) dto;
		this.myArrayToDomain(dto, dom, "departamento");
		
		this.myArrayToDomain(dto, dom, "usuario");
		
		RegisterDomain rr = RegisterDomain.getInstance();
		
		List<MyArray> sucursales = dtoA.getSucursales();
		Set<SucursalApp> sucursalesApp = new HashSet<SucursalApp>();
		for (Iterator iterator = sucursales.iterator(); iterator.hasNext();) {
			MyArray sucursalArr = (MyArray) iterator.next();
			SucursalApp sucursalApp = new SucursalApp();
			sucursalApp = (SucursalApp)rr.getObject(com.yhaguy.domain.SucursalApp.class.getName(),sucursalArr.getId());
			sucursalesApp.add(sucursalApp);
		}
		dom.setSucursales(sucursalesApp);
		
		return dom;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		//AccesoDTO dto = new AccesoDTO();
		AccesoDTO dto = (AccesoDTO) getDTO(domain,
				AccesoDTO.class);
		AccesoApp dom = (AccesoApp) domain;
		
		this.domainToMyArray(dom, dto, "usuario", new String [] {"nombre", "login"});
		
		this.domainToMyArray(dom, dto, "departamento", new String [] {"nombre", "sucursal"});
		
		MyPair sucursalOperativa = new MyPair();
		sucursalOperativa.setId(dom.getDepartamento().getSucursal().getId());
		sucursalOperativa.setText(dom.getDepartamento().getSucursal().getNombre());
		sucursalOperativa.setSigla(dom.getDepartamento().getSucursal().getEstablecimiento());
		dto.setSucursalOperativa(sucursalOperativa);
		
		/*SucursalApp sucuAux = (SucursalApp) rr.getObject(com.yhaguy.domain.SucursalApp.class.getName(), sucursalOperativa.getId());
		MyArray sucuArrAux = new MyArray();
		sucuArrAux.setId(sucuAux.getId());
		sucuArrAux.setPos1(sucuAux.getNombre());*/

		Set<SucursalApp> listaSucursales = (Set<SucursalApp>) dom
				.getSucursales();
		List<MyArray> sucursalesHabilitadas = new ArrayList<MyArray>();

		for (Iterator iterator = listaSucursales.iterator(); iterator.hasNext();) {
			SucursalApp sucursalApp = (SucursalApp) iterator.next();
			MyArray sucursalHab = new MyArray();
			sucursalHab.setId(sucursalApp.getId());
			sucursalHab.setPos1(sucursalApp.getNombre());
			sucursalesHabilitadas.add(sucursalHab);
		}
		dto.setSucursales(sucursalesHabilitadas);
		/*if(!sucursalesHabilitadas.contains(sucuArrAux))
			sucursalesHabilitadas.add(sucuArrAux);*/
		//this.listaDomainToListaMyArray(dom, dto, "sucursales", new String [] {"nombre"});
		//this.domainToListaMyArray(dto, "sucursales", com.yhaguy.domain.SucursalApp.class.getName(), new String [] {"nombre"});
		
		return dto;
	}

	public DTO obtenerAccesoDTO(String login) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		AccesoApp acceso = (AccesoApp) rr.getAcceso(login);
		
		AssemblerAcceso as = new AssemblerAcceso();
		AccesoDTO aDto = (AccesoDTO) as.domainToDto(acceso);

		Funcionario funcionario = (Funcionario) rr.getFuncionario(acceso.getId());
		MyArray funArr = new MyArray();
		funArr.setId(funcionario.getId());
		funArr.setPos1(funcionario.getEmpresa().getNombre());
		aDto.setFuncionario(funArr);
		
		return aDto;
	}

	
	public static void main(String[] args) {
		try {
			AssemblerAcceso as = new AssemblerAcceso();
			String login = "juan";
			AccesoDTO aDto = (AccesoDTO) as.obtenerAccesoDTO(login);
			System.out.println("Login: " + login + " - Datos: "
					+ aDto.getFuncionario() + " "
					+ aDto.getSucursalOperativa() + " " + aDto.getDepartamento()
					+ " " + aDto.getSucursales());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
