package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SubDiario;
import com.yhaguy.domain.SubDiarioDetalle;

public class AssemblerSubDiario extends Assembler {

	private static String[] attIgualesGastoSubDiario = { "numero", "fecha",
			"descripcion", "confirmado" };

	@Override
	public Domain dtoToDomain(DTO dtoS) throws Exception {
		SubDiarioDTO dto = (SubDiarioDTO) dtoS;
		SubDiario domain = (SubDiario) getDomain(dto, SubDiario.class);

		this.copiarValoresAtributos(dto, domain, attIgualesGastoSubDiario);
		this.myPairToDomain(dto, domain, "sucursal");
		this.listaDTOToListaDomain(dto, domain, "detalles", true, true,
				new AssemblerGastoSubDiarioDetalle());

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		SubDiarioDTO dto = (SubDiarioDTO) getDTO(domain, SubDiarioDTO.class);

		this.copiarValoresAtributos(domain, dto, attIgualesGastoSubDiario);
		this.domainToMyPair(domain, dto, "sucursal");
		this.listaDomainToListaDTO(domain, dto, "detalles",
				new AssemblerGastoSubDiarioDetalle());

		return dto;
	}
	
	
	public static void main(String[] args) throws Exception{
		RegisterDomain rr = RegisterDomain.getInstance();
		SubDiario sd = (SubDiario) rr.getObject(SubDiario.class.getName(), 1);
		
		AssemblerSubDiario ass = new AssemblerSubDiario();
		SubDiarioDTO sdDto = (SubDiarioDTO) ass.domainToDto(sd);
		
	 	List<SubDiarioDetalleDTO> lsd = sdDto.getDetalles();
	 	for (int i = 0; i < lsd.size(); i++) {
	 		SubDiarioDetalleDTO sdd = lsd.get(i);
	 		System.out.println("1"+sdd.getCuenta().getPos1());
	 		System.out.println("2"+sdd.getCuenta().getPos2());
	 		System.out.println("4"+sdd.getCuenta().getPos4());
	 		System.out.println("5"+sdd.getCuenta().getPos5());
		}
		
	}
}

class AssemblerGastoSubDiarioDetalle extends Assembler {

	private static String[] attIgualesGastoSubDiarioDetalle = { "tipo",
			"descripcion", "importe", "editable" };
	private static String[] attCuentaContable = { "codigo", "descripcion",
			"alias" };

	@Override
	public Domain dtoToDomain(DTO dtoD) throws Exception {
		SubDiarioDetalleDTO dto = (SubDiarioDetalleDTO) dtoD;
		SubDiarioDetalle domain = (SubDiarioDetalle) getDomain(dto,
				SubDiarioDetalle.class);

		this.copiarValoresAtributos(dto, domain,
				attIgualesGastoSubDiarioDetalle);
		this.myArrayToDomain(dto, domain, "cuenta");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain dom) throws Exception {
		SubDiarioDetalle domain = (SubDiarioDetalle)dom;
		SubDiarioDetalleDTO dto = (SubDiarioDetalleDTO) getDTO(domain,
				SubDiarioDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto,
				attIgualesGastoSubDiarioDetalle);
		this.domainToMyArray(domain, dto, "cuenta", attCuentaContable);
		dto.getCuenta().setPos4(domain.getCuenta().getPlanCuenta().getCodigo());
		dto.getCuenta().setPos5(domain.getCuenta().getPlanCuenta().getDescripcion());

		return dto;
	}
}
