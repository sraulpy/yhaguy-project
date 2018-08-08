package com.yhaguy.gestion.reparto;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Reparto;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.domain.Vehiculo;
import com.yhaguy.domain.Venta;

public class AssemblerReparto extends Assembler {

	static String[] attIgualesReparto = { "numero", "fechaCreacion", "fechaRecepcion", "observaciones", "costo" };
	
	static String[] attServicioTecnico = { "numero" };

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		Reparto domain = (Reparto) getDomain(dto, Reparto.class);

		this.copiarValoresAtributos(dto, domain, attIgualesReparto);
		this.myPairToDomain(dto, domain, "estadoReparto");
		this.myPairToDomain(dto, domain, "tipoReparto");
		this.myPairToDomain(dto, domain, "repartidor");
		this.myArrayToDomain(dto, domain, "creador");
		this.myArrayToDomain(dto, domain, "receptor");
		this.myArrayToDomain(dto, domain, "vehiculo");
		this.myPairToDomain(dto, domain, "sucursal", true);
		this.myArrayToDomain(dto, domain, "proveedor");
		this.listaMyArrayToListaDomain(dto, domain, "serviciosTecnicos");

		this.listaDTOToListaDomain(dto, domain, "detalles", true, true, new AssemblerRepartoDetalle());		
		this.setEstadoMovimientos((RepartoDTO) dto);
		
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		RepartoDTO dto = (RepartoDTO) getDTO(domain, RepartoDTO.class);
		Reparto dom = (Reparto) domain;

		this.copiarValoresAtributos(domain, dto, attIgualesReparto);
		this.domainToMyPair(dom, dto, "estadoReparto");
		this.domainToMyPair(dom, dto, "tipoReparto");
		this.domainToMyPair(dom, dto, "repartidor");
		this.domainToMyArray(dom, dto, "creador", new String[] { "descripcion" });
		this.domainToMyArray(domain, dto, "vehiculo", new String[] { "marca",
				"modelo", "color", "chapa", "peso" });

		this.domainToMyPair(domain, dto, "sucursal");
		
		dto.getRepartidor().setSigla(dom.getRepartidor().getCedula());
		
		if (dom.getReceptor() != null) {
			this.domainToMyArray(dom, dto, "receptor",
					new String[] { "descripcion" });
		}
		if (dom.getProveedor() != null) {
			MyArray proveedor = new MyArray();
			proveedor.setId(dom.getProveedor().getId());
			proveedor.setPos1(dom.getProveedor().getEmpresa()
					.getCodigoEmpresa());
			proveedor.setPos2(dom.getProveedor().getEmpresa().getRazonSocial());
			proveedor.setPos3(dom.getProveedor().getEmpresa().getRuc());
			dto.setProveedor(proveedor);
		}

		this.listaDomainToListaDTO(domain, dto, "detalles",
				new AssemblerRepartoDetalle());
		this.listaDomainToListaMyArray(domain, dto, "serviciosTecnicos", attServicioTecnico);
		
		return dto;
	}
	
	/**
	 * @return la lista de vehiculos convertidas a MyArray..
	 */
	public List<MyArray> getVehiculosSucursalMyArray(long sucursal)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Vehiculo> vehiculosDomain = rr.getVehiculosSucursal(sucursal);

		List<MyArray> vehiculosMyArray = new ArrayList<MyArray>();
		MyArray item;
		for (Vehiculo vehiculo : vehiculosDomain) {
			item = new MyArray();
			item.setId(vehiculo.getId());
			item.setPos1(vehiculo.getMarca());
			item.setPos2(vehiculo.getModelo());
			item.setPos3(vehiculo.getChapa());
			item.setPos5(vehiculo.getPeso());
			item.setPos6(vehiculo.getMarcaModelo());
			vehiculosMyArray.add(item);
		}

		return vehiculosMyArray;
	}
	
	/**
	 * actualiza el estado de los movimientos del reparto..
	 */
	private void setEstadoMovimientos(RepartoDTO dto) throws Exception {
		MyPair estado = dto.getEstadoReparto();

		for (RepartoDetalleDTO item : dto.getDetalles()) {
			long id = item.getIdMovimiento();

			if (item.isVenta()) {
				//this.setEstadoVenta(id, estado); verificar..!
			} else {
				this.setEstadoTransferencia(id, estado);
			}
		}

		// items eliminados..
		this.resetEstados(dto.getItemsEliminados());

		// items sin entregar..
		if (dto.isEntregado())
			this.resetEstados(dto.getEntregas(false));
	}
	
	/**
	 * reset de los estados..
	 */
	private void resetEstados(List<RepartoDetalleDTO> detalles)
			throws Exception {
		for (RepartoDetalleDTO item : detalles) {
			long id = item.getIdMovimiento();

			if (item.isVenta()) {
				this.setEstadoVenta(id, this.getEstadoVtaFacturada());
			} else {
				this.setEstadoTransferencia(id,
						this.getEstadoTransfConfirmada());
			}
		}
	}
	
	/**
	 * actualiza el estado de la venta..
	 */
	private void setEstadoVenta(long id, MyPair estado) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta venta = (Venta) rr.getObject(Venta.class.getName(), id);
		Tipo estado_ = (Tipo) rr.getObject(Tipo.class.getName(), estado.getId());
		venta.setEstado(estado_);
		rr.saveObject(venta, "sys");
	}
	
	/**
	 * actualiza el estado de la transferencia..
	 */
	private void setEstadoTransferencia(long id, MyPair estado)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Transferencia transf = (Transferencia) rr.getObject(Transferencia.class.getName(), id);
		Tipo estado_ = (Tipo) rr.getObject(Tipo.class.getName(), estado.getId());
		transf.setTransferenciaEstado(estado_);
		rr.saveObject(transf, "sys");
	}
	
	/**
	 * @return estado de la venta facturada..
	 */
	private MyPair getEstadoVtaFacturada() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String sigla = Configuracion.SIGLA_VENTA_ESTADO_FACTURADO;
		Tipo estado = rr.getTipoPorSigla(sigla);
		return new MyPair(estado.getId());
	}
	
	/**
	 * @return estado de la transferencia confirmada..
	 */
	private MyPair getEstadoTransfConfirmada() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String sigla = Configuracion.SIGLA_ESTADO_TRANSF_CONFIRMADA;
		Tipo estado = rr.getTipoPorSigla(sigla);
		return new MyPair(estado.getId());
	}
}
