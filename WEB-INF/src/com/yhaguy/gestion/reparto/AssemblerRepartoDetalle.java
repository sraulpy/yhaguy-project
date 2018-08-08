package com.yhaguy.gestion.reparto;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.RepartoDetalle;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.domain.TransferenciaDetalle;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;

public class AssemblerRepartoDetalle extends Assembler {

	private static String[] attIgualesRepartoDetalle = { "idMovimiento",
			"observacion", "entregado", "peso" };

	@Override
	public Domain dtoToDomain(DTO dtoR) throws Exception {
		RepartoDetalleDTO dto = (RepartoDetalleDTO) dtoR;
		RepartoDetalle domain = (RepartoDetalle) getDomain(dto,
				RepartoDetalle.class);

		this.copiarValoresAtributos(dto, domain, attIgualesRepartoDetalle);
		this.myArrayToDomain(dto, domain, "tipoMovimiento");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		RepartoDetalleDTO dto = (RepartoDetalleDTO) getDTO(domain,
				RepartoDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, attIgualesRepartoDetalle);
		this.domainToMyArray(domain, dto, "tipoMovimiento", new String[] {
				"descripcion", "sigla" });

		this.setDetalle((RepartoDetalle) domain, dto);

		return dto;
	}

	/**
	 * setea el detalle segun el tipo de movimiento..
	 */
	private void setDetalle(RepartoDetalle det, RepartoDetalleDTO dto) throws Exception {		
		
		MyArray detalle = new MyArray();
		String sigla = det.getTipoMovimiento().getSigla();
		long idMovimiento = det.getIdMovimiento();
		
		if (this.isVenta(sigla)) {
			Venta venta = this.getVenta(idMovimiento);
			detalle = this.getVentaMyArray(venta);
			
		} else {
			Transferencia transf = this.getTransferencia(idMovimiento);
			detalle = this.getTransferenciaMyArray(transf);
		}		
		dto.setDetalle(detalle);
	}
	
	/**
	 * @return si es venta o no segun la sigla..
	 */
	private boolean isVenta(String sigla) {
		return sigla.equals(Configuracion.SIGLA_TM_PEDIDO_VENTA)
				|| sigla.equals(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO)
				|| sigla.equals(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
	}	
	
	/**
	 * @return la transferencia..
	 */
	private Transferencia getTransferencia(long id) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return (Transferencia) rr.getObject(Transferencia.class.getName(), id);
	}
	
	/**
	 * @return la venta..
	 */
	private Venta getVenta(long id) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return (Venta) rr.getObject(Venta.class.getName(), id);
	}
	
	/**
	 * @return la venta convertida a MyArray..
	 */
	public MyArray getVentaMyArray(Venta venta) {		
		MyArray out = new MyArray();
		
		Object[] detalles = this.getDetallesVenta(venta);
		MyArray tipoMovimiento = new MyArray();
		tipoMovimiento.setId(venta.getTipoMovimiento().getId());
		tipoMovimiento.setPos1(venta.getTipoMovimiento().getDescripcion());	
		tipoMovimiento.setPos2(venta.getTipoMovimiento().getSigla());	

		out.setId(venta.getId());
		out.setPos1(venta.getId());
		out.setPos2(venta.getNumero());
		out.setPos3(venta.getFecha());
		out.setPos4(venta.getCliente().getRazonSocial());
		out.setPos5(venta.getTipoMovimiento().getDescripcion());
		out.setPos6(detalles[0]);
		out.setPos7(detalles[1]);
		out.setPos8(detalles[2]);
		out.setPos9(tipoMovimiento);
		out.setPos10(venta.getCliente().getDireccion());
		out.setPos11(venta.getPesoTotal());
		
		return out;
	}
	
	/**
	 * @return el detalle de ventas procesado..
	 */
	private Object[] getDetallesVenta(Venta venta) {
		List<MyArray> detalles = new ArrayList<MyArray>();

		long cantArt = 0;
		double montoTotal = 0;
		double totalUnitario = 0;

		for (VentaDetalle item : venta.getDetalles()) {
			MyArray det = new MyArray();
			det.setId(item.getId());
			det.setPos1(item.getArticulo().getCodigoInterno());
			det.setPos2(item.getArticulo().getDescripcion());
			det.setPos3(item.getCantidad());
			det.setPos4(item.getArticulo().getArticuloFamilia().getDescripcion());
			det.setPos5(item.getCantidadEntregada());

			cantArt = cantArt + item.getCantidad();

			totalUnitario = cantArt * (item.getPrecioGs() * item.getCantidad());
			montoTotal = montoTotal + totalUnitario;

			detalles.add(det);
		}

		return new Object[] { detalles, cantArt, montoTotal };
	}
	
	/**
	 * @return la transferencia convertida a MyArray..
	 */
	public MyArray getTransferenciaMyArray(Transferencia transf) {
		MyArray out = new MyArray();
		
		Object[] detalles = this.getDetallesTransferencia(transf);
		MyArray tipoMovimiento = new MyArray();

		out.setPos1(transf.getId());
		out.setPos2(transf.getNumero());
		out.setPos3(transf.getFechaCreacion());
		out.setPos4(transf.getSucursalDestino().getDescripcion());
		out.setPos5(transf.getTransferenciaTipo().getDescripcion());
		out.setPos6(detalles[0]);
		out.setPos7(detalles[1]);
		out.setPos8(detalles[2]);
		out.setPos9(tipoMovimiento);
		out.setPos11(0.0);
		return out;
	}
	
	/**
	 * @return el detalle de transferencias procesadas..
	 */
	private Object[] getDetallesTransferencia(Transferencia transf) {
		List<MyArray> detalles = new ArrayList<MyArray>();
		
		long cantArt = 0;
		double costoTotal = 0;

		for (TransferenciaDetalle item : transf.getDetalles()) {
			MyArray det = new MyArray();
			det.setPos1(item.getArticulo().getCodigoInterno());
			det.setPos2(item.getArticulo().getDescripcion());
			det.setPos3(item.getCantidad());
			det.setPos4(item.getArticulo().getArticuloFamilia()
					.getDescripcion());
			
			cantArt = cantArt + item.getCantidadEnviada();
			costoTotal = costoTotal + item.getCosto();
			detalles.add(det);
		}

		return new Object[] { detalles, cantArt, costoTotal };
	}
}
