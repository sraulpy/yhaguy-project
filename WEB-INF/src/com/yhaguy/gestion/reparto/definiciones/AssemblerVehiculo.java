package com.yhaguy.gestion.reparto.definiciones;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Vehiculo;

public class AssemblerVehiculo extends Assembler {

	private static String[] attIgualesVehiculo = { "codigo", "descripcion",
			"marca", "modelo", "color", "chapa", "consumo", "peso", "volumen",
			"observaciones" };

	@Override
	public Domain dtoToDomain(DTO dtoV) throws Exception {
		VehiculoDTO dto = (VehiculoDTO) dtoV;
		Vehiculo domain = (Vehiculo) getDomain(dto, Vehiculo.class);
		this.copiarValoresAtributos(dto, domain, attIgualesVehiculo);
		this.myArrayToDomain(dto, domain, "conductor");
		this.myPairToDomain(dto, domain, "sucursal");
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		VehiculoDTO dto = (VehiculoDTO) getDTO(domain, VehiculoDTO.class);
		Vehiculo dom = (Vehiculo) domain;
		this.copiarValoresAtributos(dom, dto, attIgualesVehiculo);
		this.domainToMyArray(dom, dto, "conductor", new String [] {"nombre", "funcionarioEstado", "funcionarioCargo"});
		this.domainToMyPair(dom, dto, "sucursal");
		return dto;
	}
	
	public void saveVehiculo(VehiculoDTO dto) throws Exception{
		RegisterDomain rr = RegisterDomain.getInstance();
		Vehiculo domain= new Vehiculo();
		domain = (Vehiculo) this.dtoToDomain(dto);
		rr.saveObject(domain, getLogin());
	}
	
	public List<VehiculoDTO> getVehiculosAss(MyPair sucursal) throws Exception{
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Vehiculo> listaVehiculos = new ArrayList<Vehiculo>();
		List<VehiculoDTO> listaVehiculosDTO = new ArrayList<VehiculoDTO>();	
		listaVehiculos = rr.getVehiculosSucursal(sucursal.getId().longValue());
		for (Vehiculo vehiculo : listaVehiculos) {
			listaVehiculosDTO.add((VehiculoDTO) this.domainToDto(vehiculo));
		}
		return listaVehiculosDTO;
	}
}
