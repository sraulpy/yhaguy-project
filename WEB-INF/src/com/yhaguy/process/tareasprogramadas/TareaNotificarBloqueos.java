package com.yhaguy.process.tareasprogramadas;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import com.coreweb.Config;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.HistoricoBloqueoClientes;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Tarea_Programada;
import com.yhaguy.util.EnviarCorreo;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class TareaNotificarBloqueos {
	
	static final String TAREA_NOTIFICAR_BLOQUEOS = "CORREO BLOQUEO CLIENTES ENVIADO";
	static final String USER_NOTIF = "sys";
	static final String DIRECTORIO_BASE_MRA = "/home/mraserver/project/yhaguy-project/";
	static final String DIRECTORIO_BASE_BAT = "/home/server/project/yhaguy/";
	static final String DIRECTORIO_REPORTES_MRA = "/home/mraserver/project/yhaguy-project/reportes/";
	static final String DIRECTORIO_REPORTES_BAT = "/home/server/project/yhaguy/reportes/";
	static final String EMPRESA_MRA = Configuracion.EMPRESA_MRA;
	static final String EMPRESA_BAT = Configuracion.EMPRESA_BATERIAS;
	
	static final String[] DESTINATARIOS_MRA = new String[] {
			"davida@yhaguyrepuestos.com.py", "soniat@yhaguyrepuestos.com.py" };
	
	static final String[] DESTINATARIOS_BAT = new String[] {
			"nataliac@yhaguyrepuestos.com.py", "dianaa@yhaguyrepuestos.com.py",
			"soniat@yhaguyrepuestos.com.py", "yaninaf@yhaguyrepuestos.com.py" };
	
	static final String[] COPIA_OCULTA = new String[] { "sergioa@yhaguyrepuestos.com.py" };
	static final String ASUNTO_MRA = "Bloqueos de Cuentas Automatico - Yhaguy M.R.A.";
	static final String ASUNTO_BAT = "Bloqueos de Cuentas Automatico - Yhaguy Baterias";
	
	/**
	 * notificacion por email de bloqueos automaticos..
	 */
	private static void enviarCorreoBloqueos(String empresa, String directorioReportes, 
			String directorioBase, String[] destinatarios, String asunto) {
		Configuracion.empresa = empresa;
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_BLOQUEOS, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_BLOQUEOS);
				return;
			}
			
			System.out.println("Generando el Reporte...");
			String hoy = Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY);
			Date desde = Utiles.getFecha(hoy + " 00:00:00");
			Date hasta = Utiles.getFecha(hoy + " 23:00:00");
			
			List<Object[]> data = new ArrayList<Object[]>();
			List<HistoricoBloqueoClientes> bloqueos = rr.getHistoricoBloqueoClientes(desde, hasta);
			List<HistoricoBloqueoClientes> _bloqueos = new ArrayList<HistoricoBloqueoClientes>();
			Map<String, HistoricoBloqueoClientes> bloqueos_ = new HashMap<String, HistoricoBloqueoClientes>();
			
			if (bloqueos.size() == 0) {
				return;
			}
			
			for (HistoricoBloqueoClientes bloqueo : bloqueos) {
				if (bloqueos_.get(bloqueo.getCliente()) == null) {
					bloqueos_.put(bloqueo.getCliente(), bloqueo);
				}				
			}
			
			for (String key : bloqueos_.keySet()) {
				HistoricoBloqueoClientes bloqueo = bloqueos_.get(key);
				_bloqueos.add(bloqueo);
			}
			
			Collections.sort(_bloqueos, new Comparator<HistoricoBloqueoClientes>() {
				@Override
				public int compare(HistoricoBloqueoClientes o1, HistoricoBloqueoClientes o2) {
					int compare = o1.getCliente().compareTo(o2.getCliente());				
					return compare;
				}
			});
			
			for (HistoricoBloqueoClientes bloqueo : _bloqueos) {
				data.add(new Object[] {
						bloqueo.getNumeroFactura(),
						Utiles.getDateToString(bloqueo.getVencimiento(), Utiles.DD_MM_YYYY),
						bloqueo.getCliente(), bloqueo.getMotivo(), 
						bloqueo.getUsuarioMod().toUpperCase(), bloqueo.getDiasVencimiento()});
			}

			Config.DIRECTORIO_REAL_REPORTES = directorioReportes;
			Config.DIRECTORIO_BASE_REAL = directorioBase;
			
			ReporteHistorialBloqueos rep = new ReporteHistorialBloqueos(desde, hasta);
			rep.setDatosReporte(data);
			rep.setApaisada();
			rep.ejecutar(false);
			
			System.out.println("Reporte: " + directorioReportes + rep.getArchivoSalida());			
			
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_BLOQUEOS, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_BLOQUEOS);
				return;
			}
			
			System.out.println("enviando email...");
			Tarea_Programada tarea = new Tarea_Programada();
			tarea.setFecha(new Date());
			tarea.setDescripcion(TAREA_NOTIFICAR_BLOQUEOS);
			
			EnviarCorreo correo = new EnviarCorreo(tarea);
			correo.sendMessage(destinatarios, COPIA_OCULTA, asunto,
					"Bloqueos de Cuentas con atraso mayor a 90 dias", "Bloqueos.pdf", directorioReportes + rep.getArchivoSalida());

		} catch (Exception e) {
			e.printStackTrace();
		}	
	}	
	
	public static void main(String[] args) {
		TareaNotificarBloqueos.enviarCorreoBloqueos(EMPRESA_MRA, DIRECTORIO_REPORTES_MRA, DIRECTORIO_BASE_MRA, DESTINATARIOS_MRA, ASUNTO_MRA);
		//TareaNotificarBloqueos.enviarCorreoBloqueos(EMPRESA_BAT, DIRECTORIO_REPORTES_BAT, DIRECTORIO_BASE_BAT, DESTINATARIOS_BAT, ASUNTO_BAT);
	}
}

/**
 * Reporte de Historial de Bloqueos de Clientes..
 */
class ReporteHistorialBloqueos extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fac. Nro.", TIPO_STRING, 30);
	static DatosColumnas col1 = new DatosColumnas("Vto.", TIPO_STRING, 25);
	static DatosColumnas col2 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Motivo", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Usuario", TIPO_STRING, 30);
	static DatosColumnas col5 = new DatosColumnas("Dias Vto.", TIPO_LONG, 25);

	public ReporteHistorialBloqueos(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Historial de Bloqueos de Clientes");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Bloqueo-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
