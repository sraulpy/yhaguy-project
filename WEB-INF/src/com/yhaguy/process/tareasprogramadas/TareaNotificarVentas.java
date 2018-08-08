package com.yhaguy.process.tareasprogramadas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.Config;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Tarea_Programada;
import com.yhaguy.domain.Venta;
import com.yhaguy.gestion.venta.ReporteVentasGenerico;
import com.yhaguy.util.EnviarCorreo;
import com.yhaguy.util.Utiles;

public class TareaNotificarVentas {
	
	static final String TAREA_NOTIFICAR_VENTAS = "CORREO VENTAS ENVIADO";
	static final String USER_NOTIF = "sys";	
	static final String DIRECTORIO_BASE_MRA = "/home/mraserver/project/yhaguy-project/";
	static final String DIRECTORIO_BASE_BAT = "/home/server/project/yhaguy/";
	static final String DIRECTORIO_REPORTES_MRA = "/home/mraserver/project/yhaguy-project/reportes/";
	static final String DIRECTORIO_REPORTES_BAT = "/home/server/project/yhaguy/reportes/";
	static final String EMPRESA_MRA = Configuracion.EMPRESA_MRA;
	static final String EMPRESA_BAT = Configuracion.EMPRESA_BATERIAS;
	
	static final String[] DESTINATARIOS_MRA = new String[] { "davida@yhaguyrepuestos.com.py" };
	static final String[] DESTINATARIOS_BAT = new String[] { "nataliac@yhaguyrepuestos.com.py", "dianaa@yhaguyrepuestos.com.py", "yaninaf@yhaguyrepuestos.com.py" };
	static final String[] COPIA_OCULTA = new String[] { "sergioa@yhaguyrepuestos.com.py" };
	
	/**
	 * notificacion por email de ventas..
	 */
	private static void enviarCorreoVentas(String empresa, String directorioReportes, String directorioBase, String[] destinatarios) {
		Configuracion.empresa = empresa;
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_VENTAS, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_VENTAS);
				return;
			}
			
			System.out.println("Generando el Reporte...");
			String hoy = Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY);
			Date desde = Utiles.getFecha(hoy + " 00:00:00");
			Date hasta = Utiles.getFecha(hoy + " 23:00:00");
			
			List<Object[]> data = new ArrayList<Object[]>();
			double totalImporte = 0;

			List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
			for (NotaCredito notacred : ncs) {
				int length = notacred.getCliente().getRazonSocial().length();
				int maxlength = length > 25 ? 25 : length;
				String motivo = notacred.getMotivo().getDescripcion().substring(0, 3).toUpperCase() + ".";
				Object[] nc = new Object[] {
						Utiles.getDateToString(notacred.getFechaEmision(), Utiles.DD_MM_YY),
						notacred.getNumero(), notacred.getCliente().getRazonSocial().substring(0, maxlength) + "..",
						notacred.getCliente().getRuc(),
						notacred.isNotaCreditoVentaContado() ? "NC-CO " + motivo : "NC-CR " + motivo,
						notacred.isAnulado() ? 0.0 : notacred.getImporteGs() * -1 };
				data.add(nc);
				totalImporte += (notacred.isAnulado() ? 0.0 : notacred.getImporteGs() * -1);
			}
		
			List<Venta> ventas = rr.getVentas(desde, hasta, 0);
			for (Venta venta : ventas) {
				Object[] vta = new Object[] {
						Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YY),
						venta.getNumero(),
						Utiles.getMaxLength(venta.getDenominacion() == null ? 
								venta.getCliente().getRazonSocial() : venta.getDenominacion(), 25),
						venta.getCliente().getRuc(),
						"FAC. "	+ venta.getCondicionPago().getDescripcion().substring(0, 3).toUpperCase(),
						venta.isAnulado() ? 0.0 : venta.getTotalImporteGs() };
				data.add(vta);
				totalImporte += (venta.isAnulado() ? 0.0 : venta.getTotalImporteGs());
			}

			double totalSinIva = totalImporte - Utiles.getIVA(totalImporte, 10);

			Config.DIRECTORIO_REAL_REPORTES = directorioReportes;
			Config.DIRECTORIO_BASE_REAL = directorioBase;
			
			ReporteVentasGenerico rep = new ReporteVentasGenerico(totalSinIva, desde, hasta, "TODOS..", "TODOS..", empresa, "TODOS..");
			rep.setDatosReporte(data);
			rep.setApaisada();
			rep.ejecutar(false);
			
			System.out.println("Reporte: " + directorioReportes + rep.getArchivoSalida());
			
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_VENTAS, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_VENTAS);
				return;
			}
			
			System.out.println("enviando email...");
			Tarea_Programada tarea = new Tarea_Programada();
			tarea.setFecha(new Date());
			tarea.setDescripcion(TAREA_NOTIFICAR_VENTAS);
			EnviarCorreo correo = new EnviarCorreo(tarea);
			correo.sendMessage(destinatarios, COPIA_OCULTA, "Ventas Genérico",
					"Ventas del día..", "Ventas.pdf", directorioReportes + rep.getArchivoSalida());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args) {
		TareaNotificarVentas.enviarCorreoVentas(EMPRESA_MRA, DIRECTORIO_REPORTES_MRA, DIRECTORIO_BASE_MRA, DESTINATARIOS_MRA);
		//TareaNotificarVentas.enviarCorreoVentas(EMPRESA_BAT, DIRECTORIO_REPORTES_BAT, DIRECTORIO_BASE_BAT, DESTINATARIOS_BAT);
	}
}
