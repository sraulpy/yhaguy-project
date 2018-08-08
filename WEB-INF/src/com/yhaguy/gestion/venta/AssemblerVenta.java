package com.yhaguy.gestion.venta;

import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.ArticuloUbicacion;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.domain.VentaFiscal;
import com.yhaguy.gestion.caja.recibos.AssemblerReciboFormaPago;
import com.yhaguy.gestion.empresa.AssemblerCliente;
import com.yhaguy.gestion.empresa.ClienteDTO;

public class AssemblerVenta extends Assembler {

	private static String[] attIgualesVenta = { "idEnlaceSiguiente", "fecha",
			"vencimiento", "cuotas", "numero", "timbrado", "tipoCambio", "observacion", "preparadoPor",
			"numeroPresupuesto", "numeroPedido", "numeroFactura", "numeroPlanillaCaja",
			"totalImporteGs", "totalImporteDs", "reparto",
			"puntoPartida", "fechaTraslado", "fechaFinTraslado", "repartidor",
			"cedulaRepartidor", "marcaVehiculo", "chapaVehiculo", "denominacion", "validez" };

	private static String[] attCliente = { "codigoEmpresa", "razonSocial",
			"ruc", "idEmpresa", "tipoCliente", "direccion", "telefono",
			"nombreFantasia", "cuentaBloqueada", "nombre", "ventaCredito", "limiteCredito" };

	private static String[] attTipo = { "descripcion", "sigla" };

	private static String[] attCondicionPago = { "descripcion", "plazo", "cuotas", "diasEntreCuotas" };

	private static String[] attTipoMovimiento = { "descripcion", "sigla",
			"clase", "tipoIva" };

	@Override
	public Domain dtoToDomain(DTO dtoP) throws Exception {

		VentaDTO dto = (VentaDTO) dtoP;
		Venta domain = (Venta) getDomain(dto, Venta.class);
		this.copiarValoresAtributos(dto, domain, attIgualesVenta);

		// verifica si se dio de alta un cliente ocasional desde el pedido.
		if (dto.getClienteOcasional() != null) {
			dto.setCliente(this.saveClienteOcasional(dto.getClienteOcasional()));
		}

		this.myArrayToDomain(dto, domain, "tipoMovimiento");
		this.myPairToDomain(dto, domain, "sucursal");
		this.myArrayToDomain(dto, domain, "cliente");
		this.myArrayToDomain(dto, domain, "estado");
		this.myArrayToDomain(dto, domain, "atendido");
		this.myArrayToDomain(dto, domain, "vendedor");
		this.myArrayToDomain(dto, domain, "condicionPago");
		this.myPairToDomain(dto, domain, "modoVenta");
		this.myPairToDomain(dto, domain, "estadoComprobante");

		this.myPairToDomain(dto, domain, "deposito");
		this.myArrayToDomain(dto, domain, "moneda");
		
		this.listaDTOToListaDomain(dto, domain, "formasPago", true, true,
				new AssemblerReciboFormaPago(dto.getNumero()));

		this.listaDTOToListaDomain(dto, domain, "detalles", true, true,
				new AssemblerVentaPedidoDetalle());

		// genera la venta fiscal..
		if ((dto.esNuevo() == true)
				&& ((dto.isFacturaContado() == true) || (dto.isFacturaCredito() == true))) {
			this.saveVentaFiscal(dto);
		}
		
		if (dto.isPedido()) {
			EventQueues.lookup(Configuracion.EVENTO_NUEVO_PEDIDO,
					EventQueues.APPLICATION, true).publish(
					new Event(Configuracion.ON_NUEVO_PEDIDO, null, null));
		}
		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {

		VentaDTO dto = (VentaDTO) getDTO(domain, VentaDTO.class);
		Venta dom = (Venta) domain;

		this.copiarValoresAtributos(domain, dto, attIgualesVenta);

		this.domainToMyArray(domain, dto, "tipoMovimiento", attTipoMovimiento);
		this.domainToMyPair(domain, dto, "sucursal");
		this.domainToMyArray(dom, dto, "estado", attTipo);
		this.domainToMyArray(domain, dto, "atendido", new String[] { "descripcion" });
		this.domainToMyArray(domain, dto, "vendedor", new String[] { "descripcion" });
		this.domainToMyArray(domain, dto, "cliente", attCliente);
		this.domainToMyArray(domain, dto, "condicionPago", attCondicionPago);
		this.domainToMyPair(dom, dto, "deposito");
		this.domainToMyArray(dom, dto, "moneda", attTipo);
		this.domainToMyPair(domain, dto, "modoVenta");
		this.domainToMyPair(domain, dto, "estadoComprobante");
		this.listaDomainToListaDTO(domain, dto, "formasPago",
				new AssemblerReciboFormaPago(dto.getNumero()));
		this.listaDomainToListaDTO(dom, dto, "detalles",
				new AssemblerVentaPedidoDetalle());

		return dto;
	}

	/**
	 * Graba en la BD el cliente.. Para los casos en que se da de alta un
	 * cliente ocasional..
	 * @return el cliente grabado en forma de MyArray pos1:codigoEmpresa
	 *         pos2:razonSocial pos3:ruc pos4:tipoCliente
	 */
	private MyArray saveClienteOcasional(ClienteDTO cliente) throws Exception {
		MyArray out = new MyArray();

		Cliente clienteDomain = new AssemblerCliente().dtoToDomain(cliente);
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(clienteDomain, getLogin());

		out.setId(clienteDomain.getId());
		out.setPos1(clienteDomain.getCodigoEmpresa());
		out.setPos2(clienteDomain.getRazonSocial());
		out.setPos3(clienteDomain.getRuc());
		out.setPos4(new MyPair(clienteDomain.getTipoCliente().getId(), null));

		return out;
	}

	/**
	 * Copia los datos de la Factura de Venta en la Clase VentaFiscal y lo graba
	 * en la BD.. Esta informacion es con el fin de generar el libro ventas
	 * desde una sola tabla..
	 */
	private void saveVentaFiscal(VentaDTO venta) throws Exception {

		VentaFiscal vf = new VentaFiscal();
		vf.setCondicion("");
		vf.setEmision(venta.getFecha());
		vf.setIdCliente(venta.getCliente().getId());
		vf.setIdDeposito(venta.getDeposito().getId());
		vf.setIdModoVenta(venta.getModoVenta().getId());
		vf.setIdSucursal(venta.getSucursal().getId());
		vf.setIdTipoMovimiento(venta.getTipoMovimiento().getId());
		vf.setIdVenta(venta.getId());
		vf.setImporteDs(venta.getTotalImporteDs());
		vf.setImporteGs(venta.getTotalImporteGs());
		vf.setMoneda((String) venta.getMoneda().getPos1());
		vf.setNumero(venta.getNumero());
		vf.setRazonSocial((String) venta.getCliente().getPos2());
		vf.setRuc((String) venta.getCliente().getPos3());
		vf.setSucursal(venta.getSucursal().getText());
		vf.setTipoCambio(venta.getTipoCambio());
		vf.setVencimiento(venta.getVencimiento());

		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(vf, getLogin());
	}
}

/**
 * Assembler del detalle de venta..
 */
class AssemblerVentaPedidoDetalle extends Assembler {

	static final String[] ATT_IGUALES = { "descripcion",
			"cantidad", "costoUnitarioGs",
			"precioVentaUnitarioGs", "precioVentaUnitarioDs",
			"impuestoUnitario", "precioVentaFinalGs", "precioVentaFinalDs",
			"descuentoUnitarioGs", "descuentoUnitarioDs",
			"precioVentaFinalUnitarioGs", "precioVentaFinalUnitarioDs",
			"impuestoFinal", "reservado", "nombreRegla",
			"precioGs"};
	
	static final String[] ATT_ARTICULO = { "codigoInterno", "codigoProveedor",
			"codigoOriginal", "descripcion", "servicio" };
	
	static final String[] ATT_LISTAPRECIO = { "descripcion", "margen" };

	@Override
	public Domain dtoToDomain(DTO dtoP) throws Exception {
		VentaDetalleDTO dto = (VentaDetalleDTO) dtoP;
		VentaDetalle domain = (VentaDetalle) getDomain(dto, VentaDetalle.class);
		
		this.copiarValoresAtributos(dto, domain, ATT_IGUALES);
		this.myArrayToDomain(dto, domain, "articulo");
		this.myArrayToDomain(dto, domain, "listaPrecio");
		this.myPairToDomain(dto, domain, "tipoIVA");

		return domain;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		VentaDetalleDTO dto = (VentaDetalleDTO) getDTO(domain,
				VentaDetalleDTO.class);

		this.copiarValoresAtributos(domain, dto, ATT_IGUALES);
		this.domainToMyArray(domain, dto, "articulo", ATT_ARTICULO);
		this.domainToMyArray(domain, dto, "listaPrecio", ATT_LISTAPRECIO);
		this.domainToMyPair(domain, dto, "tipoIVA");
		
		dto.setUbicacion(this.getUbicacion(dto.getArticulo().getId()));
		
		return dto;
	}
	
	/**
	 * @return la ubicacion del articulo..
	 */
	private String getUbicacion(long idArticulo) throws Exception {
		String out = "";
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloUbicacion> ubics = rr.getUbicacion(idArticulo);
		for (ArticuloUbicacion ubic : ubics) {
			out += ubic.getEstante() + "." + ubic.getFila() + "." + ubic.getColumna() + " - ";
		}		
		return out.isEmpty() ? "SIN UBIC.." : out.substring(0, out.length() - 2);
	}
}
