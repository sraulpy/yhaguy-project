package com.yhaguy.process.tareasprogramadas;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.Config;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Tarea_Programada;
import com.yhaguy.util.EnviarCorreo;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class TareaNotificarDesbloqueos {
	
	static final String TAREA_NOTIFICAR_DESBLOQUEOS = "CORREO DESBLOQUEO CLIENTES ENVIADO";
	static final String USER_NOTIF = "sys";
	static final String DIRECTORIO_BASE_MRA = "/home/mraserver/project/yhaguy-project/";
	static final String DIRECTORIO_BASE_BAT = "/home/server/project/yhaguy/";
	static final String DIRECTORIO_BASE_CEN = "/home/server/project/yhaguy-central/";
	static final String DIRECTORIO_REPORTES_MRA = "/home/mraserver/project/yhaguy-project/reportes/";
	static final String DIRECTORIO_REPORTES_BAT = "/home/server/project/yhaguy/reportes/";
	static final String DIRECTORIO_REPORTES_CEN = "/home/server/project/yhaguy-central/reportes/";
	static final String EMPRESA = Configuracion.EMPRESA_YRSA;
	static final String EMPRESA_BAT = Configuracion.EMPRESA_GTSA;
	
	static final String[] DESTINATARIOS_MRA = new String[] {
			"davida@yhaguyrepuestos.com.py", "soniat@yhaguyrepuestos.com.py" };
	
	static final String[] DESTINATARIOS_BAT = new String[] {
			"nataliac@gtsa.com.py", "dianaa@gtsa.com.py",
			"silviap@gtsa.com.py" };
	
	static final String[] DESTINATARIOS_CEN = new String[] { "cobranzas@yhaguyrepuestos.com.py" };
	
	static final String[] COPIA_OCULTA = new String[] { "sergioa@yhaguyrepuestos.com.py" };
	static final String ASUNTO = "Desbloqueos de Cuentas Automatico - Yhaguy Repuestos S.A.";
	static final String ASUNTO_BAT = "Desbloqueos de Cuentas Automatico - Yhaguy Baterias";
	
	/**
	 * notificacion por email de desbloqueos automaticos..
	 */
	private static void enviarCorreoDesbloqueos(String empresa, String directorioReportes, 
			String directorioBase, String[] destinatarios, String asunto) {
		Configuracion.empresa = empresa;
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_DESBLOQUEOS, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_DESBLOQUEOS);
				return;
			}
			
			System.out.println("Generando el Reporte...");
			String hoy = Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY);
			Date desde = Utiles.getFecha(hoy + " 00:00:00");
			Date hasta = Utiles.getFecha(hoy + " 23:00:00");
			
			List<Object[]> data = new ArrayList<Object[]>();
			List<Empresa> desbloqueos = rr.getDesBloqueoClientes(desde, hasta);
			
			for (Empresa emp : desbloqueos) {
				data.add(new Object[] { emp.getRazonSocial(), emp.getMotivoBloqueo(), emp.getUsuarioMod() });
			}

			Config.DIRECTORIO_REAL_REPORTES = directorioReportes;
			Config.DIRECTORIO_BASE_REAL = directorioBase;
			
			ReporteHistorialDesbloqueos rep = new ReporteHistorialDesbloqueos(desde, hasta);
			rep.setDatosReporte(data);
			rep.ejecutar(false);
			
			System.out.println("Reporte: " + directorioReportes + rep.getArchivoSalida());			
			
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_DESBLOQUEOS, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_DESBLOQUEOS);
				return;
			}
			
			System.out.println("enviando email...");
			Tarea_Programada tarea = new Tarea_Programada();
			tarea.setFecha(new Date());
			tarea.setDescripcion(TAREA_NOTIFICAR_DESBLOQUEOS);
			
			EnviarCorreo correo = new EnviarCorreo(tarea);
			correo.sendMessage(destinatarios, COPIA_OCULTA, asunto,
					"Desbloqueos automaticos por cobro de facturas..", "Desbloqueos.pdf", directorioReportes + rep.getArchivoSalida());

		} catch (Exception e) {
			e.printStackTrace();
		}	
	}	
	
	public static void main(String[] args) {
		//TareaNotificarDesbloqueos.enviarCorreoDesbloqueos(EMPRESA, DIRECTORIO_REPORTES_CEN, DIRECTORIO_BASE_CEN, DESTINATARIOS_CEN, ASUNTO);
		TareaNotificarDesbloqueos.enviarCorreoDesbloqueos(EMPRESA_BAT, DIRECTORIO_REPORTES_BAT, DIRECTORIO_BASE_BAT, DESTINATARIOS_BAT, ASUNTO_BAT);
	}
}

/**
 * Reporte de Historial de Desbloqueos de Clientes..
 */
class ReporteHistorialDesbloqueos extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Motivo", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Usuario", TIPO_STRING, 30);

	public ReporteHistorialDesbloqueos(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Desbloqueos de Clientes");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Desbloqueo-");
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
