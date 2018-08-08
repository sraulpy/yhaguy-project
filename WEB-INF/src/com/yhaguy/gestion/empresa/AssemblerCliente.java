package com.yhaguy.gestion.empresa;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.ContactoInterno;
import com.yhaguy.util.Utiles;

public class AssemblerCliente extends Assembler {

	protected static String[] camposCtaCtb = AssemblerProveedor.camposCtaCtb;	
	private static String[] attFuncionario = { "razonSocial" };
	private static String[] attListaPrecio = { "descripcion" };

	private static String[] attIgualesCliente = { "prioridad", "completo",
			"idPersonaJedi", "observaciones", "limiteCredito", "ventaCredito" };

	@Override
	public Cliente dtoToDomain(DTO dtoG) throws Exception {

		ClienteDTO dto = (ClienteDTO) dtoG;
		Cliente domain = (Cliente) getDomain(dto, Cliente.class);

		this.copiarValoresAtributos(dto, domain, attIgualesCliente);

		this.myPairToDomain(dto, domain, "estadoCliente");
		this.myPairToDomain(dto, domain, "categoriaCliente");
		this.myPairToDomain(dto, domain, "tipoCliente");
		this.myArrayToDomain(dto, domain, "cuentaContable");
		this.myArrayToDomain(dto, domain, "cobrador");
		this.myArrayToDomain(dto, domain, "listaPrecio");
		this.hijoDtoToHijoDomain(dto, domain, "empresa", new AssemblerEmpresa(), true);
		this.listaDTOToListaDomain(dto, domain, "contactosInternos", true,
				true, new AssemblerContactoInterno());

		return domain;
	}

	@Override
	public ClienteDTO domainToDto(Domain domain) throws Exception {
		ClienteDTO dto = (ClienteDTO) getDTO(domain, ClienteDTO.class);

		this.copiarValoresAtributos(domain, dto, attIgualesCliente);

		this.domainToMyPair(domain, dto, "estadoCliente");
		this.domainToMyPair(domain, dto, "categoriaCliente");
		this.domainToMyPair(domain, dto, "tipoCliente");
		this.domainToMyArray(domain, dto, "cuentaContable", camposCtaCtb);
		this.domainToMyArray(domain, dto, "cobrador", attFuncionario);
		this.domainToMyArray(domain, dto, "listaPrecio", attListaPrecio);
		this.hijoDomainToHijoDTO(domain, dto, "empresa", new AssemblerEmpresa());
		this.listaDomainToListaDTO(domain, dto, "contactosInternos",
				new AssemblerContactoInterno());
		
		dto.setChequesRechazados(this.getChequesRechazados((Cliente) domain));

		return dto;
	}
	
	/**
	 * @return cheques rechazados en myarray..
	 */
	private List<MyArray> getChequesRechazados(Cliente cliente) throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		for (BancoChequeTercero cheque : cliente.getChequesRechazados()) {
			MyArray my = new MyArray();
			my.setId(cheque.getId());
			my.setPos1(cheque.getBanco().getDescripcion().toUpperCase());
			my.setPos2(cheque.getNumero());
			my.setPos3(Utiles.getDateToString(cheque.getFecha(), Utiles.DD_MM_YYYY));
			my.setPos4(Utiles.getNumberFormat(cheque.getMonto()));
			out.add(my);
		}
		return out;
	}
}

class AssemblerContactoInterno extends Assembler {

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {

		ContactoInterno domain = (ContactoInterno) getDomain(dto,
				ContactoInterno.class);
		this.myPairToDomain(dto, domain, "funcionario");
		this.myPairToDomain(dto, domain, "tipoContactoInterno");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		ContactoInternoDTO dto = (ContactoInternoDTO) getDTO(domain,
				ContactoInternoDTO.class);
		this.domainToMyPair(domain, dto, "funcionario", "empresa");
		this.domainToMyPair(domain, dto, "tipoContactoInterno", "descripcion");

		return dto;
	}
}